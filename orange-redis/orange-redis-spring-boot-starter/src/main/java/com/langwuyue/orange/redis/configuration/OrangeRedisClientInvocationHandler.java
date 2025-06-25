/*
 * Copyright (c) 2025 Liang.Zhong. All rights reserved.
 *
 *
 * SPDX-License-Identifier: Apache-2.0
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.langwuyue.orange.redis.configuration;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.concurrent.atomic.AtomicInteger;

import com.langwuyue.orange.redis.OrangeRedisCircuitBreaker;
import com.langwuyue.orange.redis.OrangeRedisException;
import com.langwuyue.orange.redis.OrangeRedisState;
import com.langwuyue.orange.redis.RedisValueTypeEnum;
import com.langwuyue.orange.redis.annotation.OrangeRedisKey;
import com.langwuyue.orange.redis.annotation.Timeout;
import com.langwuyue.orange.redis.context.OrangeRedisContext;
import com.langwuyue.orange.redis.context.OrangeRedisContext.Key;
import com.langwuyue.orange.redis.context.builder.OrangeOperationArgHandlerMapping;
import com.langwuyue.orange.redis.context.builder.OrangeRedisContextBuilder;
import com.langwuyue.orange.redis.executor.OrangeRedisExecutor;
import com.langwuyue.orange.redis.mapping.OrangeRedisExecutorsMapping;
import com.langwuyue.orange.redis.registry.OrangeSlowOperationRegistry;
import com.langwuyue.orange.redis.util.OrangeStringTemlateUtils;

/**
 * Invocation handler for Redis client interface proxies.
 * 
 * <p>This class implements the {@link InvocationHandler} interface to handle method invocations
 * on Redis client proxy instances. It manages the execution flow of Redis operations including:
 * <ul>
 *   <li>Context creation for each operation</li>
 *   <li>Circuit breaker integration for fault tolerance</li>
 *   <li>Execution of Redis operations through appropriate executors</li>
 *   <li>Metrics collection for active requests</li>
 *   <li>Slow operation detection and registration</li>
 *   <li>Exception handling and propagation</li>
 * </ul>
 *
 * <p>The handler resolves Redis keys based on annotations and templates, manages timeouts,
 * and delegates actual Redis operations to specialized executors.
 *
 * @author Liang.Zhong
 * @since 1.0.0
 * @see InvocationHandler
 * @see OrangeRedisExecutor
 * @see OrangeRedisContext
 * @see OrangeRedisCircuitBreaker
 */
public class OrangeRedisClientInvocationHandler implements InvocationHandler {
	
	/**
	 * Counter for tracking the number of active Redis requests.
	 * 
	 * <p>This atomic counter provides real-time metrics about the number of Redis
	 * operations currently being processed across all instances of this handler.
	 * It is incremented when an operation starts and decremented when it completes,
	 * allowing accurate concurrent request tracking.
	 */
	private static AtomicInteger ACTIVE_REQUEST_COUNTER = new AtomicInteger(0);
	
	private OrangeRedisKey redisKey;
	
	private Class<?> operationOwner;
	
	private OrangeRedisExecutorsMapping mapping;
	
	private OrangeOperationArgHandlerMapping operationArgHandlerMapping;
	
	private RedisValueTypeEnum valueType;
	
	private OrangeRedisCircuitBreaker circuitBreaker;
	
	private OrangeRedisProperties properties;
	
	/**
	 * Creates a new invocation handler for Redis client proxies.
	 *
	 * @param operationOwner the class that owns the Redis operations (typically the client interface)
	 * @param mapping the mapping between methods and their corresponding Redis executors
	 * @param redisKey the Redis key annotation that provides key template and expiration settings
	 * @param operationArgHandlerMapping the mapping for handling method arguments in operations
	 * @param valueType the enum indicating the type of values stored in Redis
	 * @param circuitBreaker the circuit breaker for fault tolerance (may be null if not used)
	 * @param properties the Redis configuration properties
	 */
	public OrangeRedisClientInvocationHandler(
		Class<?> operationOwner,
		OrangeRedisExecutorsMapping mapping,
		OrangeRedisKey redisKey,
		OrangeOperationArgHandlerMapping operationArgHandlerMapping,
		RedisValueTypeEnum valueType,
		OrangeRedisCircuitBreaker circuitBreaker,
		OrangeRedisProperties properties
	) {
		this.operationOwner = operationOwner;
		this.mapping = mapping;
		this.redisKey = redisKey;
		this.operationArgHandlerMapping = operationArgHandlerMapping;
		this.valueType = valueType;
		this.circuitBreaker = circuitBreaker;
		this.properties = properties;
	}
	
	/**
	 * Handles method invocations on the Redis client proxy.
	 * 
	 * <p>This method implements the core logic for executing Redis operations:
	 * <ol>
	 *   <li>Handles Object class methods directly</li>
	 *   <li>Tracks metrics for active requests if enabled</li>
	 *   <li>Resolves the appropriate executor for the method</li>
	 *   <li>Builds the operation context with all necessary information</li>
	 *   <li>Checks circuit breaker status before proceeding</li>
	 *   <li>Executes the Redis operation through the resolved executor</li>
	 *   <li>Detects and registers slow operations that exceed the configured threshold</li>
	 *   <li>Handles exceptions with circuit breaker integration</li>
	 * </ol>
	 *
	 * @param proxy the proxy instance that the method was invoked on
	 * @param method the method being invoked
	 * @param args the arguments to the method
	 * @return the result of the Redis operation, or null if circuit breaker is triggered
	 * @throws Throwable if the Redis operation fails and circuit breaker is not configured
	 * @see OrangeRedisExecutor#execute(OrangeRedisContext)
	 * @see OrangeRedisCircuitBreaker
	 */
	@Override
	public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
		if(Object.class.equals(method.getDeclaringClass())) {
			return method.invoke(this, args);
		}
		OrangeRedisContext context = null;
		try {
			
			long start = System.currentTimeMillis();
			if(properties.getMetrics().getEnabled().booleanValue()) {
				ACTIVE_REQUEST_COUNTER.incrementAndGet();
			}
			
			// Get executor
			OrangeRedisExecutor executor = this.mapping.getExecutor(method);
			this.mapping.getLogger().debug("Got a executor {} for method {}", executor.getClass(), method);
			
			// Build context
			context = createContextBuilder(executor,method,args).build();
			
			// Circuit Breaker​​ 
			if(this.circuitBreaker != null && OrangeRedisState.isOutOfService()) {
				this.circuitBreaker.outOfService(
					context != null ? context.getRedisKey().getValue() : null, 
					this.operationOwner, 
					method, 
					args
				);
				return null;
			}
			
			// Executing
			Object result = executor.execute(context);
			if(!this.properties.getSlowOperation().isEnabled()) {
				return result;
			}
			long cost = System.currentTimeMillis() - start;
			long threshold = this.properties.getSlowOperation().getSlowOperationThreshold().toMillis();
			if(cost >= threshold) {
				this.mapping.getLogger().warn(
					"Slow operation found: execution time {}ms >= threshold {}ms.\n Key: {}.\n Operation: {}.", 
					cost, 
					threshold,
					context.getRedisKey().getValue(),
					method
				);
				// Collect slow operations for management.
				OrangeSlowOperationRegistry.register(this.operationOwner,method, cost);
			}
			return result;
		}catch (Exception e) {
			if(this.circuitBreaker == null) {
				throw new OrangeRedisException(String.format("Execution Error! %n Operation Owner: %s %n Operation : %s", this.operationOwner,method),e);
			}
			String key = null;
			if(context != null && context.getRedisKey() != null) {
				key = context.getRedisKey().getValue();
			}
			this.circuitBreaker.onException(
				key, 
				this.operationOwner, 
				method, 
				args, 
				e
			);
			return null;
		}finally {
			if(properties.getMetrics().getEnabled().booleanValue()) {
				ACTIVE_REQUEST_COUNTER.decrementAndGet();
			}
		}
	}
	
	/**
	 * Creates a new instance of the context builder.
	 * 
	 * <p>This factory method allows subclasses to override and provide custom builder implementations.
	 *
	 * @return a new instance of OrangeRedisContextBuilder
	 */
	protected OrangeRedisContextBuilder newBuilder() {
		return new OrangeRedisContextBuilder();
	}
	
	/**
	 * Creates and configures a context builder for the current Redis operation.
	 * 
	 * <p>This method prepares a builder with all necessary information to create a complete
	 * operation context, including:
	 * <ul>
	 *   <li>The appropriate context class based on the executor</li>
	 *   <li>Argument handling configuration</li>
	 *   <li>Method metadata (both proxy method and actual implementation method)</li>
	 *   <li>Operation owner class</li>
	 *   <li>Redis key information with template resolution</li>
	 *   <li>Value type configuration</li>
	 * </ul>
	 *
	 * @param executor the Redis operation executor that will handle the operation
	 * @param method the method being invoked on the proxy
	 * @param args the arguments passed to the method
	 * @return a configured context builder ready to build the operation context
	 * @throws Exception if any error occurs during context builder creation
	 */
	protected OrangeRedisContextBuilder createContextBuilder(OrangeRedisExecutor executor,Method method, Object[] args) throws Exception {
		Method actualMethod = this.mapping.getActualMethod(method);
		Class<? extends OrangeRedisContext> contextClass = executor.getContextClass();
		OrangeRedisContextBuilder builder = newBuilder();
		builder.contextClass(contextClass);
		builder.operationArgHandlerMapping(this.operationArgHandlerMapping);
		builder.args(args);
		builder.actualMethod(actualMethod);
		builder.operationMethod(method);
		builder.operationOwner(this.operationOwner);
		builder.redisKey(getKey(method,args));
		builder.valueType(this.valueType);
		return builder;
	}
	
	/**
	 * Builds a Redis key object for the given method and arguments.
	 * 
	 * <p>This method creates a complete Redis key by:
	 * <ol>
	 *   <li>Getting the original key from the Redis key annotation</li>
	 *   <li>Applying the configured prefix</li>
	 *   <li>Resolving any templates in the key using method and arguments</li>
	 *   <li>Adding expiration time information from the annotation</li>
	 * </ol>
	 *
	 * @param method the method being invoked
	 * @param args the arguments passed to the method
	 * @return a fully configured Redis key object with value and expiration settings
	 */
	protected Key getKey(Method method,Object[] args) {
		String originKey = getOriginKey(this.redisKey.key());
		String key = OrangeStringTemlateUtils.getString(originKey, method, args);
		Timeout timeout = this.redisKey.expirationTime();
		return new OrangeRedisContext.Key(
			originKey,
			key, 
			timeout.value(), 
			timeout.unit()
		);
	}
	
	/**
	 * Applies the configured global key prefix to the given key.
	 * 
	 * <p>This method ensures that all Redis keys follow the configured naming convention
	 * by prepending the global prefix from properties.
	 *
	 * @param key the original key without prefix
	 * @return the key with the global prefix applied
	 */
	protected String getOriginKey(String key) {
		return this.properties.getKeyPrefix() + key;
	}

	/**
	 * Gets the Redis key annotation configured for this handler.
	 *
	 * @return the Redis key annotation containing key template and expiration settings
	 */
	protected OrangeRedisKey getRedisKey() {
		return redisKey;
	}

	/**
	 * Gets the class that owns the Redis operations.
	 *
	 * @return the class (typically an interface) that defines the Redis operations
	 */
	protected Class<?> getOperationOwner() {
		return operationOwner;
	}

	/**
	 * Gets the mapping between methods and their Redis executors.
	 *
	 * @return the executor mapping used to resolve operation implementations
	 */
	protected OrangeRedisExecutorsMapping getMapping() {
		return mapping;
	}

	/**
	 * Gets the mapping for handling method arguments in operations.
	 *
	 * @return the argument handler mapping for processing method parameters
	 */
	protected OrangeOperationArgHandlerMapping getOperationArgHandlerMapping() {
		return operationArgHandlerMapping;
	}

	/**
	 * Gets the configured value type for Redis operations.
	 *
	 * @return the enum indicating how values should be serialized/deserialized
	 */
	protected RedisValueTypeEnum getValueType() {
		return valueType;
	}
	
	/**
	 * Gets the current count of active Redis requests.
	 * 
	 * <p>This method provides real-time metrics about the number of Redis
	 * operations currently being processed.
	 *
	 * @return the number of active Redis requests
	 */
	public int getActiveRequestCount() {
		return ACTIVE_REQUEST_COUNTER.get();
	}
}