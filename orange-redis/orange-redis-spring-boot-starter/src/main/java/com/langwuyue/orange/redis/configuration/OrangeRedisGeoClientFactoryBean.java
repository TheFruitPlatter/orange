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

import java.util.ArrayList;
import java.util.Collection;

import com.langwuyue.orange.redis.OrangeRedisCircuitBreaker;
import com.langwuyue.orange.redis.OrangeRedisDefaultCircuitBreaker;
import com.langwuyue.orange.redis.RedisValueTypeEnum;
import com.langwuyue.orange.redis.annotation.geo.OrangeRedisGeoClient;
import com.langwuyue.orange.redis.listener.OrangeRedisMultipleSetIfAbsentListener;
import com.langwuyue.orange.redis.listener.OrangeRedisSetIfAbsentListener;
import com.langwuyue.orange.redis.mapping.OrangeRedisExecutorsMapping;
import com.langwuyue.orange.redis.mapping.OrangeRedisGeoExecutorIdGenerator;
import com.langwuyue.orange.redis.mapping.OrangeRedisGeoExecutorsMapping;
import com.langwuyue.orange.redis.operations.OrangeRedisDefaultGeoOperations;
import com.langwuyue.orange.redis.operations.OrangeRedisDefaultScriptOperations;
import com.langwuyue.orange.redis.operations.OrangeRedisGeoOperations;
import com.langwuyue.orange.redis.operations.OrangeRedisScriptOperations;

/**
 * Factory bean for creating Redis Geo client instances.
 * 
 * <p>This factory bean is responsible for creating and configuring Redis clients
 * that handle geospatial operations. It extends the abstract Redis client factory bean
 * and provides specific implementations for Geo operations.
 * 
 * <p>The factory creates clients based on interfaces annotated with {@link OrangeRedisGeoClient}
 * and configures them with appropriate executors, operations, and circuit breakers.
 * 
 * <p>Key features include:
 * <ul>
 *   <li>Support for Redis geospatial commands (GEOADD, GEODIST, GEOHASH, etc.)</li>
 *   <li>Integration with circuit breaker pattern for fault tolerance</li>
 *   <li>Custom serialization of geospatial data</li>
 *   <li>Script execution capabilities for complex geospatial operations</li>
 *   <li>Support for multiple Redis instances and sharding</li>
 *   <li>Configurable value types for geospatial data</li>
 *   <li>Listener support for monitoring operations</li>
 * </ul>
 * 
 * <p>This implementation provides concrete implementations for:
 * <ul>
 *   <li>{@link #getExecutorsMapping()} - Creates the executor mapping for Geo operations</li>
 *   <li>{@link #getValueType()} - Returns the configured value type for Geo data</li>
 *   <li>{@link #getCircuitBreakerClass()} - Determines the appropriate circuit breaker</li>
 *   <li>{@link #getListeners()} - Provides operation listeners</li>
 * </ul>
 *
 * @author Liang.Zhong
 * @since 1.0.0
 * @see OrangeRedisClientAbstractFactoryBean
 * @see OrangeRedisGeoClient
 * @see OrangeRedisGeoOperations
 */
public class OrangeRedisGeoClientFactoryBean extends OrangeRedisClientAbstractFactoryBean {
	
	private OrangeRedisGeoClient client;
	
	private static OrangeRedisGeoExecutorsMapping EXECUTORS_MAPPING;
	
	/**
	 * Creates a new Redis Geo client factory bean.
	 * 
	 * <p>This constructor initializes the factory with the necessary components to create
	 * a Redis client for geospatial operations. It sets up the operation owner class,
	 * client definition class, and Redis configuration.
	 *
	 * @param operationOwner the class that owns the Redis operations (typically the client interface)
	 * @param clientDefinitionClass the class that defines the client configuration through annotations
	 * @param configuration the Redis configuration properties
	 */
	public OrangeRedisGeoClientFactoryBean(
			Class<?> operationOwner,
			Class<?> clientDefinitionClass,
			OrangeRedisConfiguration configuration
	) {
		super(operationOwner, configuration,clientDefinitionClass);
	}
	
	/**
	 * Creates and retrieves the Redis Geo executors mapping.
	 * 
	 * <p>This method initializes the executors mapping if it hasn't been created yet.
	 * The mapping includes:
	 * <ul>
	 *   <li>Geo operations for handling Redis geospatial commands</li>
	 *   <li>Script operations for executing complex Redis scripts</li>
	 *   <li>Executor ID generator for unique operation identification</li>
	 *   <li>Listeners for monitoring set operations</li>
	 * </ul>
	 *
	 * <p>The mapping is cached statically to avoid recreating it for each factory instance.
	 *
	 * @return the Redis Geo executors mapping
	 * @see OrangeRedisGeoExecutorsMapping
	 * @see OrangeRedisDefaultGeoOperations
	 * @see OrangeRedisDefaultScriptOperations
	 */
	@Override
	protected OrangeRedisExecutorsMapping getExecutorsMapping() {
		if(EXECUTORS_MAPPING != null) {
			return EXECUTORS_MAPPING;
		}
		OrangeRedisGeoOperations operations = new OrangeRedisDefaultGeoOperations(
			this.getRedisTemplate(), 
			getRedisSerializer(),
			getLogger()
		);
		OrangeRedisScriptOperations scriptOperations = new OrangeRedisDefaultScriptOperations(
			this.getRedisTemplate(), 
			getRedisSerializer(),
			getLogger()
		);
		EXECUTORS_MAPPING = new OrangeRedisGeoExecutorsMapping(
			operations, 
			new OrangeRedisGeoExecutorIdGenerator(),
			getListeners(),
			scriptOperations,
			getMultipleListener(),
			getLogger()
		);
		return EXECUTORS_MAPPING;
	}
	
	/**
	 * Gets the collection of multiple set-if-absent listeners.
	 * 
	 * <p>This method returns an empty list as the Geo client currently does not
	 * require multiple set-if-absent listeners. This method can be overridden
	 * by subclasses if multiple set-if-absent functionality is needed.
	 *
	 * @return an empty collection of multiple set-if-absent listeners
	 * @see OrangeRedisMultipleSetIfAbsentListener
	 */
	@Override
	protected Collection<OrangeRedisMultipleSetIfAbsentListener> getMultipleListener() {
		return new ArrayList<>();
	}

	/**
	 * Gets the collection of set-if-absent listeners.
	 * 
	 * <p>This method returns an empty list as the Geo client currently does not
	 * require set-if-absent listeners. This method can be overridden by subclasses
	 * if set-if-absent functionality is needed.
	 *
	 * @return an empty collection of set-if-absent listeners
	 * @see OrangeRedisSetIfAbsentListener
	 */
	@Override
	protected Collection<OrangeRedisSetIfAbsentListener> getListeners() {
		return new ArrayList<>();
	}

	/**
	 * Gets the Redis value type for Geo operations.
	 * 
	 * <p>This method returns the value type specified in the {@link OrangeRedisGeoClient}
	 * annotation. The value type determines how geospatial data is serialized and
	 * deserialized when interacting with Redis.
	 *
	 * @return the value type specified in the client annotation
	 * @see RedisValueTypeEnum
	 * @see OrangeRedisGeoClient#valueType()
	 */
	@Override
	protected RedisValueTypeEnum getValueType() {
		return client.valueType();
	}
	
	/**
	 * Gets the circuit breaker class for Geo operations.
	 * 
	 * <p>This method determines the appropriate circuit breaker class to use for
	 * Redis Geo operations. It follows these steps:
	 * <ol>
	 *   <li>Retrieves the {@link OrangeRedisGeoClient} annotation from the client definition class</li>
	 *   <li>Gets the circuit breaker class specified in the annotation's {@code breaker()} attribute</li>
	 *   <li>If a non-default breaker class is specified, returns that class</li>
	 *   <li>If a breaker class name is specified in the annotation's {@code breakerClassName()} attribute,
	 *       attempts to load that class</li>
	 *   <li>Falls back to the default circuit breaker if any errors occur</li>
	 * </ol>
	 *
	 * <p>The circuit breaker provides fault tolerance for Redis operations by preventing
	 * cascading failures when Redis is experiencing issues.
	 *
	 * @return the circuit breaker class to use for Redis Geo operations
	 * @see OrangeRedisCircuitBreaker
	 * @see OrangeRedisDefaultCircuitBreaker
	 * @see OrangeRedisGeoClient#breaker()
	 * @see OrangeRedisGeoClient#breakerClassName()
	 */
	@Override
	protected Class<? extends OrangeRedisCircuitBreaker> getCircuitBreakerClass(){
		this.client = this.getClientDefinitionClass().getAnnotation(OrangeRedisGeoClient.class);
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