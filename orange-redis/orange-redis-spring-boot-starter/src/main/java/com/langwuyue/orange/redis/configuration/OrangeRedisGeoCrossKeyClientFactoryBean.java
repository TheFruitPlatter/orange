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
import com.langwuyue.orange.redis.annotation.cross.OrangeRedisGeoCrossKeyClient;
import com.langwuyue.orange.redis.annotation.geo.OrangeRedisGeoClient;
import com.langwuyue.orange.redis.context.builder.OrangeGeoCrossKeysOperationArgHandlerMapping;
import com.langwuyue.orange.redis.context.builder.OrangeOperationArgHandler;
import com.langwuyue.orange.redis.context.builder.OrangeOperationArgHandlerMapping;
import com.langwuyue.orange.redis.listener.OrangeRedisMultipleSetIfAbsentListener;
import com.langwuyue.orange.redis.listener.OrangeRedisSetIfAbsentListener;
import com.langwuyue.orange.redis.mapping.OrangeRedisExecutorsMapping;
import com.langwuyue.orange.redis.mapping.OrangeRedisGeoCrossKeyExecutorIdGenerator;
import com.langwuyue.orange.redis.mapping.OrangeRedisGeoCrossKeyExecutorsMapping;
import com.langwuyue.orange.redis.operations.OrangeRedisDefaultGeoOperations;
import com.langwuyue.orange.redis.operations.OrangeRedisDefaultScriptOperations;
import com.langwuyue.orange.redis.operations.OrangeRedisGeoOperations;
import com.langwuyue.orange.redis.operations.OrangeRedisScriptOperations;

/**
 * Factory bean for creating Redis Geo clients that support cross-key operations.
 * 
 * <p>This factory bean extends {@link OrangeRedisClientAbstractFactoryBean} to create
 * Redis clients that can perform geospatial operations across multiple keys. It is
 * specifically designed to work with interfaces annotated with {@link OrangeRedisGeoCrossKeyClient}.
 * 
 * <p>Key features include:
 * <ul>
 *   <li>Support for geospatial operations across multiple Redis keys</li>
 *   <li>Integration with circuit breaker pattern for fault tolerance</li>
 *   <li>Custom executor mapping for cross-key operations</li>
 *   <li>Support for script operations that span multiple keys</li>
 *   <li>Listener support for monitoring operations</li>
 * </ul>
 * 
 * <p>Implementation details:
 * <ul>
 *   <li>Uses {@link OrangeRedisGeoCrossKeyExecutorsMapping} for operation execution</li>
 *   <li>Creates specialized {@link OrangeRedisCrossKeysClientInvocationHandler} for method interception</li>
 *   <li>Provides null value type as cross-key operations don't require a specific value type</li>
 *   <li>Supports custom circuit breakers through annotation configuration</li>
 * </ul>
 * 
 * @author Liang.Zhong
 * @since 1.0.0
 * @see OrangeRedisClientAbstractFactoryBean
 * @see OrangeRedisGeoCrossKeyClient
 * @see OrangeRedisGeoCrossKeyExecutorsMapping
 * @see OrangeRedisCrossKeysClientInvocationHandler
 */
public class OrangeRedisGeoCrossKeyClientFactoryBean extends OrangeRedisClientAbstractFactoryBean {
	
	/**
	 * Static cache for cross-key Geo operations executors mapping.
	 * This mapping is shared across all instances of the factory bean to avoid
	 * redundant creation of executors for the same operations. It is lazily
	 * initialized when first accessed through {@link #getExecutorsMapping()}.
	 */
	private static OrangeRedisGeoCrossKeyExecutorsMapping EXECUTORS_MAPPING;
	
	/**
	 * The cross-key Geo client annotation instance.
	 * This field stores the annotation metadata from {@link OrangeRedisGeoCrossKeyClient}
	 * that configures this client's behavior, including circuit breaker settings
	 * and operation configuration for geospatial operations.
	 */
	private OrangeRedisGeoCrossKeyClient client;
	
	/**
	 * Constructs a new OrangeRedisGeoCrossKeyClientFactoryBean with the specified parameters.
	 * This constructor initializes the factory bean with the necessary components to create
	 * and configure Redis Geo operation proxies that support cross-key operations.
	 * It delegates to the parent class constructor for common initialization tasks.
	 *
	 * @param operationOwner The class that owns the Redis Geo operations to be proxied.
	 *                       This is typically an interface defining the geospatial operations.
	 * @param clientDefinitionClass The class that contains the {@link OrangeRedisGeoCrossKeyClient} annotation,
	 *                             which provides configuration details for the client.
	 * @param configuration The Orange Redis configuration that contains global settings
	 *                     such as connection details, serializers, and other Redis-related properties.
	 */
	public OrangeRedisGeoCrossKeyClientFactoryBean(
			Class<?> operationOwner,
			Class<?> clientDefinitionClass,
			OrangeRedisConfiguration configuration
	) {
		super(operationOwner, configuration, clientDefinitionClass);
	}
	
	/**
	 * Returns the Redis key configuration for cross-key Geo operations.
	 * 
	 * <p>This implementation always returns null because cross-key Geo operations
	 * don't require a specific key configuration. This differs from single-key operations
	 * which typically need key prefix/suffix configuration.
	 * 
	 * <p>Key characteristics:
	 * <ul>
	 *   <li>Cross-key operations work with multiple keys simultaneously</li>
	 *   <li>Keys are typically provided as method parameters</li>
	 *   <li>No need for global key prefix/suffix configuration</li>
	 *   <li>Key generation is handled by the operation implementation</li>
	 * </ul>
	 * 
	 * @return null indicating no specific key configuration is required
	 * @see OrangeRedisKey
	 */
	@Override
	protected OrangeRedisKey getOrangeRedisKey() {
		return null;
	}

	/**
	 * Creates and returns the argument handler mapping for cross-key Geo operations.
	 * 
	 * <p>This method creates a specialized {@link OrangeGeoCrossKeysOperationArgHandlerMapping}
	 * that can handle arguments for cross-key operations. The mapping is responsible for:
	 * <ul>
	 *   <li>Converting method arguments to Redis operation parameters</li>
	 *   <li>Handling multiple keys in a single operation</li>
	 *   <li>Managing geospatial coordinate conversions</li>
	 *   <li>Supporting various parameter types and annotations</li>
	 * </ul>
	 * 
	 * <p>The mapping is configured with:
	 * <ul>
	 *   <li>The executors mapping for operation execution</li>
	 *   <li>The operation owner class for context</li>
	 *   <li>Value handlers for type conversion</li>
	 *   <li>Redis configuration for global settings</li>
	 *   <li>The annotation type to identify Geo operations</li>
	 * </ul>
	 * 
	 * @param valueHandlerMap map of value handler types to their instances
	 * @return the configured argument handler mapping
	 * @see OrangeGeoCrossKeysOperationArgHandlerMapping
	 * @see OrangeOperationArgHandler
	 */
	@Override
	protected OrangeOperationArgHandlerMapping getOrangeOperationArgHandlerMapping(
			Map<Class<? extends OrangeOperationArgHandler>, OrangeOperationArgHandler> valueHandlerMap) {
		return new OrangeGeoCrossKeysOperationArgHandlerMapping(
				this.getExecutorsMapping(),
				this.getOperationOwner(),
				valueHandlerMap,
				this.getConfiguration(),
				OrangeRedisGeoClient.class
		);
	}
	
	/**
	 * Creates and returns the invocation handler for cross-key Geo operations.
	 * 
	 * <p>This method creates a specialized {@link OrangeRedisCrossKeysClientInvocationHandler}
	 * that intercepts method calls on the client interface. The handler is responsible for:
	 * <ul>
	 *   <li>Intercepting method invocations on the client interface</li>
	 *   <li>Extracting operation metadata from annotations</li>
	 *   <li>Processing method arguments using the handler mapping</li>
	 *   <li>Delegating to appropriate Redis operations</li>
	 *   <li>Converting and returning results</li>
	 *   <li>Handling exceptions and error conditions</li>
	 * </ul>
	 * 
	 * <p>The handler is configured with:
	 * <ul>
	 *   <li>The operation owner class for context</li>
	 *   <li>The executors mapping for operation execution</li>
	 *   <li>The argument handler mapping for parameter processing</li>
	 *   <li>The circuit breaker for fault tolerance</li>
	 *   <li>Redis properties for configuration</li>
	 * </ul>
	 * 
	 * @return the configured invocation handler
	 * @see OrangeRedisCrossKeysClientInvocationHandler
	 * @see InvocationHandler
	 */
	@Override
	protected InvocationHandler getInvocationHandler() {
		return new OrangeRedisCrossKeysClientInvocationHandler(
			this.getOperationOwner(),
			this.getExecutorsMapping(),
			this.getOperationArgHandlerMapping(),
			this.getCircuitBreaker(),
			this.getProperties()
		);
	}

	/**
	 * Creates and returns the executors mapping for cross-key Geo operations with caching.
	 * 
	 * <p>This method implements a singleton pattern to cache and reuse the executors mapping.
	 * On first call, it initializes all required components:
	 * <ul>
	 *   <li>{@link OrangeRedisDefaultGeoOperations} - For standard Geo operations</li>
	 *   <li>{@link OrangeRedisDefaultScriptOperations} - For script execution</li>
	 *   <li>{@link OrangeRedisGeoCrossKeyExecutorIdGenerator} - For operation ID generation</li>
	 * </ul>
	 * 
	 * <p>The mapping combines these components with listeners to create a comprehensive
	 * executor that can handle:
	 * <ul>
	 *   <li>Standard Redis Geo commands</li>
	 *   <li>Custom Lua scripts for complex operations</li>
	 *   <li>Operation monitoring through listeners</li>
	 *   <li>Cross-key operation coordination</li>
	 * </ul>
	 * 
	 * <p>Subsequent calls return the cached instance for better performance.
	 * 
	 * @return the singleton executors mapping instance, never null
	 * @see OrangeRedisGeoCrossKeyExecutorsMapping
	 * @see OrangeRedisDefaultGeoOperations
	 * @see OrangeRedisDefaultScriptOperations
	 */
	@Override
	protected OrangeRedisExecutorsMapping getExecutorsMapping() {
		if(EXECUTORS_MAPPING != null) {
			return EXECUTORS_MAPPING;
		}
		OrangeRedisGeoOperations operations = new OrangeRedisDefaultGeoOperations(this.getRedisTemplate(), getRedisSerializer(),getLogger());
		OrangeRedisScriptOperations scriptOperations = new OrangeRedisDefaultScriptOperations(
			this.getRedisTemplate(), 
			getRedisSerializer(),
			getLogger()
		);
		EXECUTORS_MAPPING = new OrangeRedisGeoCrossKeyExecutorsMapping(
			operations, 
			new OrangeRedisGeoCrossKeyExecutorIdGenerator(),
			getListeners(),
			scriptOperations,
			getMultipleListener(),
			getLogger()
		);
		return EXECUTORS_MAPPING;
	}
	
	/**
	 * Provides a collection of multiple set-if-absent listeners for cross-key operations.
	 * 
	 * <p>This implementation returns an empty list by default. Subclasses can override
	 * to provide custom listeners that will be notified about:
	 * <ul>
	 *   <li>Before/after multiple set-if-absent operations</li>
	 *   <li>Operation success/failure events</li>
	 *   <li>Performance metrics collection</li>
	 * </ul>
	 * 
	 * <p>Listeners are particularly useful for:
	 * <ul>
	 *   <li>Monitoring and logging</li>
	 *   <li>Performance optimization</li>
	 *   <li>Implementing custom retry logic</li>
	 *   <li>Gathering statistics</li>
	 * </ul>
	 * 
	 * @return empty list of listeners by default
	 * @see OrangeRedisMultipleSetIfAbsentListener
	 */
	@Override
	protected Collection<OrangeRedisMultipleSetIfAbsentListener> getMultipleListener() {
		return new ArrayList<>();
	}

	/**
	 * Provides a collection of set-if-absent listeners for single-key operations.
	 * 
	 * <p>This implementation returns an empty list by default. Subclasses can override
	 * to provide custom listeners that will be notified about:
	 * <ul>
	 *   <li>Before/after set-if-absent operations</li>
	 *   <li>Operation success/failure events</li>
	 *   <li>Cache synchronization events</li>
	 * </ul>
	 * 
	 * <p>Listeners enable various cross-cutting concerns:
	 * <ul>
	 *   <li>Audit logging of Redis operations</li>
	 *   <li>Cache invalidation triggers</li>
	 *   <li>Real-time monitoring</li>
	 *   <li>Custom error handling</li>
	 * </ul>
	 * 
	 * @return empty list of listeners by default
	 * @see OrangeRedisSetIfAbsentListener
	 */
	@Override
	protected Collection<OrangeRedisSetIfAbsentListener> getListeners() {
		return new ArrayList<>();
	}

	/**
	 * Returns the value type for cross-key Geo operations.
	 * 
	 * <p>This implementation always returns null because cross-key Geo operations
	 * don't require a specific value type. This differs from single-key operations
	 * which typically need to know the value type for proper serialization.
	 * 
	 * <p>Key characteristics:
	 * <ul>
	 *   <li>Cross-key operations work with geospatial coordinates rather than typed values</li>
	 *   <li>Coordinates are handled uniformly as (longitude, latitude) pairs</li>
	 *   <li>Member names are treated as strings regardless of actual type</li>
	 *   <li>Return values are typically distances or position lists</li>
	 * </ul>
	 * 
	 * @return null indicating no specific value type is required
	 * @see RedisValueTypeEnum
	 */
	@Override
	protected RedisValueTypeEnum getValueType() {
		return null;
	}
	
	/**
	 * Determines the circuit breaker class to use for cross-key Geo operations.
	 * 
	 * <p>This method follows a specific resolution order:
	 * <ol>
	 *   <li>Gets the {@link OrangeRedisGeoCrossKeyClient} annotation from the client interface</li>
	 *   <li>Checks if a custom breaker class is specified (non-default)</li>
	 *   <li>If no custom class is specified, checks for a breaker class name</li>
	 *   <li>Attempts to load the class by name if specified</li>
	 *   <li>Falls back to the default circuit breaker if any errors occur</li>
	 * </ol>
	 * 
	 * <p>Note: The circuit breaker provides fault tolerance for Redis operations,
	 * preventing cascading failures when Redis is unavailable or experiencing issues.
	 * 
	 * @return the circuit breaker class to use, never null
	 * @throws OrangeRedisException if there's an error loading the specified class
	 *         (but falls back to default breaker)
	 * @see OrangeRedisGeoCrossKeyClient#breaker()
	 * @see OrangeRedisGeoCrossKeyClient#breakerClassName()
	 * @see OrangeRedisDefaultCircuitBreaker
	 */
	@Override
	protected Class<? extends OrangeRedisCircuitBreaker> getCircuitBreakerClass(){
		this.client = this.getClientDefinitionClass().getAnnotation(OrangeRedisGeoCrossKeyClient.class);
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