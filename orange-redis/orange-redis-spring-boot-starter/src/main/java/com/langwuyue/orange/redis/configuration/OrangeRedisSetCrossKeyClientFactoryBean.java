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
import com.langwuyue.orange.redis.annotation.cross.OrangeRedisSetCrossKeyClient;
import com.langwuyue.orange.redis.annotation.set.OrangeRedisSetClient;
import com.langwuyue.orange.redis.context.builder.OrangeOperationArgHandler;
import com.langwuyue.orange.redis.context.builder.OrangeOperationArgHandlerMapping;
import com.langwuyue.orange.redis.context.builder.OrangeSetCrossKeysOperationArgHandlerMapping;
import com.langwuyue.orange.redis.listener.OrangeRedisMultipleSetIfAbsentListener;
import com.langwuyue.orange.redis.listener.OrangeRedisSetIfAbsentListener;
import com.langwuyue.orange.redis.mapping.OrangeRedisExecutorsMapping;
import com.langwuyue.orange.redis.mapping.OrangeRedisSetCrossKeyExecutorIdGenerator;
import com.langwuyue.orange.redis.mapping.OrangeRedisSetCrossKeyExecutorsMapping;
import com.langwuyue.orange.redis.operations.OrangeRedisDefaultScriptOperations;
import com.langwuyue.orange.redis.operations.OrangeRedisDefaultSetOperations;
import com.langwuyue.orange.redis.operations.OrangeRedisScriptOperations;
import com.langwuyue.orange.redis.operations.OrangeRedisSetOperations;

/**
 * Factory bean for creating Redis cross-key Set operations client proxy.
 * This factory handles the creation and configuration of proxies for Redis Set operations
 * that involve multiple keys, providing features like circuit breaking and operation mapping.
 *
 * <p>The factory manages:
 * <ul>
 *   <li>Executor mappings for cross-key Set operations</li>
 *   <li>Circuit breaker configuration</li>
 *   <li>Operation argument handling</li>
 *   <li>Proxy invocation processing</li>
 * </ul>
 *
 * @author Liang.Zhong
 * @since 1.0.0
 */
public class OrangeRedisSetCrossKeyClientFactoryBean extends OrangeRedisClientAbstractFactoryBean {
	
	/**
	 * Static cache for cross-key Set operations executors mapping.
	 * This mapping is shared across all instances of the factory bean to avoid
	 * redundant creation of executors for the same operations. It is lazily
	 * initialized when first accessed through {@link #getExecutorsMapping()}.
	 */
	private static OrangeRedisSetCrossKeyExecutorsMapping EXECUTORS_MAPPING;
	
	/**
	 * The cross-key Set client annotation instance.
	 * This field stores the annotation metadata from {@link OrangeRedisSetCrossKeyClient}
	 * that configures this client's behavior, including circuit breaker settings
	 * and operation configuration.
	 */
	private OrangeRedisSetCrossKeyClient client;
	
	/**
	 * Constructs a new OrangeRedisSetCrossKeyClientFactoryBean with the specified parameters.
	 * 
	 * <p>This constructor initializes the factory bean with the necessary components to create
	 * and configure Redis Set operation proxies that support cross-key operations.
	 * It delegates to the parent class constructor for common initialization tasks such as
	 * setting up the operation owner, Redis configuration, and client definition class.
	 * 
	 * <p>Cross-key Set operations allow for operations that span multiple Redis keys,
	 * such as unions, intersections, and differences between sets stored under different keys.
	 * This factory bean creates proxies that handle these operations efficiently and safely.
	 *
	 * @param operationOwner The class that owns the Redis Set operations to be proxied.
	 *                       This is typically an interface defining the Set operations.
	 * @param clientDefinitionClass The class that contains the {@link OrangeRedisSetCrossKeyClient} annotation,
	 *                             which provides configuration details for the client.
	 * @param configuration The Orange Redis configuration that contains global settings
	 *                     such as connection details, serializers, and other Redis-related properties.
	 */
	public OrangeRedisSetCrossKeyClientFactoryBean(
			Class<?> operationOwner,
			Class<?> clientDefinitionClass,
			OrangeRedisConfiguration configuration
	) {
		super(operationOwner, configuration, clientDefinitionClass);
	}
	
	/**
	 * Gets the Redis key annotation.
	 * For cross-key operations, this method returns null as keys are handled differently.
	 *
	 * @return null for cross-key operations
	 */
	@Override
	protected OrangeRedisKey getOrangeRedisKey() {
		return null;
	}

	/**
	 * Creates and returns an operation argument handler mapping for Set cross-key operations.
	 * This mapping is responsible for handling and processing operation arguments for Redis Set cross-key operations.
	 *
	 * @param valueHandlerMap map of value handler classes to their instances
	 * @return the operation argument handler mapping for Set cross-key operations
	 */
	@Override
	protected OrangeOperationArgHandlerMapping getOrangeOperationArgHandlerMapping(
			Map<Class<? extends OrangeOperationArgHandler>, OrangeOperationArgHandler> valueHandlerMap) {
		return new OrangeSetCrossKeysOperationArgHandlerMapping(
				this.getExecutorsMapping(),
				this.getOperationOwner(),
				valueHandlerMap,
				this.getConfiguration(),
				OrangeRedisSetClient.class
		);
	}
	
	/**
	 * Creates and returns an invocation handler for Set cross-key operations.
	 * This handler processes method invocations on the Redis Set cross-key client proxy.
	 *
	 * @return the invocation handler for Set cross-key operations
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
	 * Creates and returns the executors mapping for Set cross-key operations.
	 * This method initializes Redis Set operations and script operations,
	 * and creates a mapping between methods and their corresponding Redis executors.
	 *
	 * @return the executors mapping for Set cross-key operations
	 */
	@Override
	protected OrangeRedisExecutorsMapping getExecutorsMapping() {
		if(EXECUTORS_MAPPING != null) {
			return EXECUTORS_MAPPING;
		}
		OrangeRedisSetOperations operations = new OrangeRedisDefaultSetOperations(
			this.getRedisTemplate(), 
			getRedisSerializer(),
			getLogger()
		);
		OrangeRedisScriptOperations scriptOperations = new OrangeRedisDefaultScriptOperations(
			this.getRedisTemplate(), 
			getRedisSerializer(),
			getLogger()
		);
		EXECUTORS_MAPPING = new OrangeRedisSetCrossKeyExecutorsMapping(
			operations, 
			new OrangeRedisSetCrossKeyExecutorIdGenerator(),
			getListeners(),
			scriptOperations,
			getMultipleListener(),
			getLogger()
		);
		return EXECUTORS_MAPPING;
	}
	
	/**
	 * Gets the listeners for multiple if-absent operations in Set cross-key operations.
	 * This method returns an empty collection as cross-key operations handle multiple listeners differently.
	 *
	 * @return an empty collection of multiple if-absent listeners
	 */
	@Override
	protected Collection<OrangeRedisMultipleSetIfAbsentListener> getMultipleListener() {
		return new ArrayList<>();
	}

	/**
	 * Gets the listeners for if-absent operations in Set cross-key operations.
	 * This method returns an empty collection as cross-key operations handle listeners differently.
	 *
	 * @return an empty collection of if-absent listeners
	 */
	@Override
	protected Collection<OrangeRedisSetIfAbsentListener> getListeners() {
		return new ArrayList<>();
	}

	/**
	 * Gets the value type enum for Set cross-key operations.
	 * This method returns null as the value type is handled differently in cross-key operations.
	 *
	 * @return null for cross-key operations
	 */
	@Override
	protected RedisValueTypeEnum getValueType() {
		return null;
	}
	
	/**
	 * Gets the circuit breaker class for Set cross-key operations.
	 * This method retrieves the circuit breaker class from the client annotation,
	 * or attempts to load it by class name if specified.
	 *
	 * @return the circuit breaker class to be used for Set cross-key operations
	 */
	@Override
	protected Class<? extends OrangeRedisCircuitBreaker> getCircuitBreakerClass(){
		this.client = this.getClientDefinitionClass().getAnnotation(OrangeRedisSetCrossKeyClient.class);
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