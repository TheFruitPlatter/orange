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
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

import com.langwuyue.orange.redis.OrangeRedisCircuitBreaker;
import com.langwuyue.orange.redis.OrangeRedisDefaultCircuitBreaker;
import com.langwuyue.orange.redis.RedisValueTypeEnum;
import com.langwuyue.orange.redis.annotation.OrangeRedisKey;
import com.langwuyue.orange.redis.annotation.script.OrangeRedisScriptClient;
import com.langwuyue.orange.redis.context.builder.OrangeOperationArgHandler;
import com.langwuyue.orange.redis.context.builder.OrangeOperationArgHandlerMapping;
import com.langwuyue.orange.redis.context.builder.OrangeScriptArgHandlerMapping;
import com.langwuyue.orange.redis.listener.OrangeRedisMultipleSetIfAbsentListener;
import com.langwuyue.orange.redis.listener.OrangeRedisSetIfAbsentListener;
import com.langwuyue.orange.redis.mapping.OrangeRedisExecutorsMapping;
import com.langwuyue.orange.redis.mapping.OrangeRedisScriptExecutorIdGenerator;
import com.langwuyue.orange.redis.mapping.OrangeRedisScriptExecutorsMapping;
import com.langwuyue.orange.redis.operations.OrangeRedisDefaultScriptOperations;
import com.langwuyue.orange.redis.operations.OrangeRedisScriptOperations;

/**
 * Factory bean for creating Redis script client proxies.
 *
 * <p>This factory bean handles the creation of Redis script client instances based on
 * {@link OrangeRedisScriptClient} annotations. It provides the necessary infrastructure
 * for executing Redis scripts with features like circuit breaking and metrics collection.</p>
 *
 * <p>Key features include:
 * <ul>
 *   <li>Script execution management</li>
 *   <li>Circuit breaker integration</li>
 *   <li>Executor mapping for script operations</li>
 *   <li>Support for multiple return types</li>
 * </ul>
 *
 * @author Liang.Zhong
 * @since 1.0.0
 * @see OrangeRedisClientAbstractFactoryBean
 * @see OrangeRedisScriptClient
 * @see OrangeRedisScriptOperations
 */
public class OrangeRedisScriptClientFactoryBean extends OrangeRedisClientAbstractFactoryBean {
	
	/**
	 * The Redis script client annotation instance.
	 * <p>Lazily initialized from the client definition class annotation.</p>
	 */
	private OrangeRedisScriptClient client;
	
	/**
	 * Static mapping of script executors for performance optimization.
	 * <p>Shared across all instances of this factory bean.</p>
	 */
	private static OrangeRedisScriptExecutorsMapping EXECUTORS_MAPPING;
	
	/**
	 * Constructs a new OrangeRedisScriptClientFactoryBean.
	 *
	 * @param operationOwner the owner class that contains the Redis script operations
	 * @param clientDefinitionClass the interface class annotated with {@link OrangeRedisScriptClient}
	 * @param configuration the Redis configuration properties
	 */
	public OrangeRedisScriptClientFactoryBean(
			Class<?> operationOwner,
			Class<?> clientDefinitionClass,
			OrangeRedisConfiguration configuration
	) {
		super(operationOwner, configuration, clientDefinitionClass);
	}
	
	/**
	 * Gets the {@link OrangeRedisKey} annotation for script operations.
	 *
	 * <p>This implementation currently returns null as script operations typically
	 * don't use a fixed key prefix. Subclasses may override to provide specific
	 * key annotation behavior.</p>
	 *
	 * @return the Redis key annotation, or null if not applicable
	 */
	@Override
	protected OrangeRedisKey getOrangeRedisKey() {
		return null;
	}

	/**
	 * Creates a mapping of argument handlers for script operations.
	 *
	 * <p>This implementation creates a specialized {@link OrangeScriptArgHandlerMapping}
	 * that combines the executors mapping with the provided value handlers.</p>
	 *
	 * @param valueHandlerMap map of pre-configured value handlers
	 * @return a new argument handler mapping instance for script operations
	 * @see OrangeScriptArgHandlerMapping
	 */
	@Override
	protected OrangeOperationArgHandlerMapping getOrangeOperationArgHandlerMapping(
			Map<Class<? extends OrangeOperationArgHandler>, OrangeOperationArgHandler> valueHandlerMap
	) {
		return new OrangeScriptArgHandlerMapping(this.getExecutorsMapping(),this.getOperationOwner(),valueHandlerMap);
	}
	
	/**
	 * Creates an invocation handler for script client operations.
	 *
	 * <p>This implementation returns a specialized {@link OrangeRedisScriptClientInvocationHandler}
	 * that handles Redis script execution with features like circuit breaking and metrics collection.</p>
	 *
	 * @return a new invocation handler instance configured for script operations
	 * @see OrangeRedisScriptClientInvocationHandler
	 */
	@Override
	protected InvocationHandler getInvocationHandler() {
		return new OrangeRedisScriptClientInvocationHandler(
			this.getOperationOwner(),
			this.getExecutorsMapping(),
			this.getOperationArgHandlerMapping(),
			this.getCircuitBreaker(),
			this.getProperties()
		);
	}
	
	/**
	 * Gets the executors mapping for script operations.
	 *
	 * <p>This implementation uses a static singleton pattern to share the executors mapping
	 * across all instances of this factory bean. The mapping is lazily initialized on first use.</p>
	 *
	 * <p>The executors mapping includes:
	 * <ul>
	 *   <li>Script executor ID generator</li>
	 *   <li>Listeners for script execution events</li>
	 *   <li>Default script operations implementation</li>
	 * </ul>
	 *
	 * @return the shared executors mapping instance
	 */
	@Override
	protected OrangeRedisExecutorsMapping getExecutorsMapping() {
		if(EXECUTORS_MAPPING != null) {
			return EXECUTORS_MAPPING;
		}
		OrangeRedisScriptOperations scriptOperations = new OrangeRedisDefaultScriptOperations(
			this.getRedisTemplate(), 
			getRedisSerializer(),
			getLogger()
		);
		EXECUTORS_MAPPING = new OrangeRedisScriptExecutorsMapping(
			new OrangeRedisScriptExecutorIdGenerator(),
			getListeners(),
			scriptOperations,
			getMultipleListener(),
			getLogger()
		);
		return EXECUTORS_MAPPING;
	}
	
	/**
	 * Gets the collection of multiple set-if-absent listeners for script operations.
	 *
	 * <p>This implementation returns an empty collection by default. Subclasses should
	 * override this method to provide specific listeners for script execution events.</p>
	 *
	 * @return an empty collection of multiple set-if-absent listeners
	 */
	@Override
	protected Collection<OrangeRedisMultipleSetIfAbsentListener> getMultipleListener() {
		return new ArrayList<>();
	}

	/**
	 * Gets the collection of set-if-absent listeners for script operations.
	 *
	 * <p>This implementation returns an empty collection by default. Subclasses should
	 * override this method to provide specific listeners for script execution events.</p>
	 *
	 * @return an empty collection of set-if-absent listeners
	 */
	@Override
	protected Collection<OrangeRedisSetIfAbsentListener> getListeners() {
		return new ArrayList<>();
	}

	/**
	 * Gets the value type for script operation results.
	 *
	 * <p>This implementation returns the value type specified in the
	 * {@link OrangeRedisScriptClient} annotation's returnType attribute.</p>
	 *
	 * @return the configured Redis value type for script operation results
	 * @see OrangeRedisScriptClient#returnType()
	 */
	@Override
	protected RedisValueTypeEnum getValueType() {
		return client.returnType();
	}
	
	/**
	 * Gets the circuit breaker class for script operations.
	 *
	 * <p>This implementation first checks the {@link OrangeRedisScriptClient} annotation
	 * for a configured circuit breaker class. If not specified, falls back to the
	 * default circuit breaker implementation.</p>
	 *
	 * <p>Handles the following cases:
	 * <ul>
	 *   <li>Explicit circuit breaker class from annotation</li>
	 *   <li>Fallback to default circuit breaker</li>
	 *   <li>Class loading errors with graceful fallback</li>
	 * </ul>
	 *
	 * @return the circuit breaker class to use for script operations
	 * @see OrangeRedisScriptClient#breaker()
	 * @see OrangeRedisScriptClient#breakerClassName()
	 */
	@Override
	protected Class<? extends OrangeRedisCircuitBreaker> getCircuitBreakerClass(){
		this.client = this.getClientDefinitionClass().getAnnotation(OrangeRedisScriptClient.class);
		Class<? extends OrangeRedisCircuitBreaker> clazz = client.breaker();
		if(clazz != OrangeRedisDefaultCircuitBreaker.class) {
			return clazz;
		}
		if(client.breakerClassName() == null || client.breakerClassName().trim().isEmpty()) {
			return clazz;
		}
		try {
			return (Class<? extends OrangeRedisCircuitBreaker>) Class.forName(client.breakerClassName());
		}catch (Exception e) {
			this.getLogger().warn(String.format(
				"Get circuit breaker Class error, operation:%s",
				this.getOperationOwner()
			),e);
			return clazz;
		}
	}
}