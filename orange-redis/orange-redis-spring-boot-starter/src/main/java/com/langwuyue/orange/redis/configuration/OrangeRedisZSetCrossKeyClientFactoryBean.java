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
import com.langwuyue.orange.redis.annotation.cross.OrangeRedisZSetCrossKeyClient;
import com.langwuyue.orange.redis.annotation.zset.OrangeRedisZSetClient;
import com.langwuyue.orange.redis.context.builder.OrangeOperationArgHandler;
import com.langwuyue.orange.redis.context.builder.OrangeOperationArgHandlerMapping;
import com.langwuyue.orange.redis.context.builder.OrangeZSetCrossKeysOperationArgHandlerMapping;
import com.langwuyue.orange.redis.listener.OrangeRedisMultipleSetIfAbsentListener;
import com.langwuyue.orange.redis.listener.OrangeRedisSetIfAbsentListener;
import com.langwuyue.orange.redis.mapping.OrangeRedisExecutorsMapping;
import com.langwuyue.orange.redis.mapping.OrangeRedisZSetCrossKeyExecutorIdGenerator;
import com.langwuyue.orange.redis.mapping.OrangeRedisZSetCrossKeyExecutorsMapping;
import com.langwuyue.orange.redis.operations.OrangeRedisDefaultScriptOperations;
import com.langwuyue.orange.redis.operations.OrangeRedisDefaultZSetOperations;
import com.langwuyue.orange.redis.operations.OrangeRedisScriptOperations;
import com.langwuyue.orange.redis.operations.OrangeRedisZSetOperations;

/**
 * Factory bean for creating Redis ZSet cross-key client proxies.
 * This class is responsible for creating and configuring proxy instances that handle
 * Redis ZSet (Sorted Set) operations across multiple keys. It extends the abstract
 * factory bean to provide specific implementation for ZSet cross-key operations.
 * 
 * <p>The factory bean manages the creation of invocation handlers, executors mappings,
 * and various listeners required for ZSet cross-key operations. It also handles
 * circuit breaker configuration for fault tolerance.</p>
 * 
 * <p>ZSet cross-key operations allow for atomic operations across multiple sorted sets
 * in Redis, providing consistency guarantees when working with related data stored
 * in different keys.</p>
 *
 * @author Liang.Zhong
 * @since 1.0.0
 */
public class OrangeRedisZSetCrossKeyClientFactoryBean extends OrangeRedisClientAbstractFactoryBean {
	
	/**
	 * Static cache for the ZSet cross-key executors mapping.
	 * This mapping is shared across all instances of the factory bean to avoid
	 * redundant creation of executors for the same operations. It is lazily
	 * initialized when first accessed through {@link #getExecutorsMapping()}.
	 */
	private static OrangeRedisZSetCrossKeyExecutorsMapping EXECUTORS_MAPPING;
	
	/**
	 * The ZSet cross-key client annotation instance.
	 * This field stores the annotation metadata from {@link OrangeRedisZSetCrossKeyClient}
	 * that configures this client's behavior, including circuit breaker settings.
	 * It is initialized when the circuit breaker class is determined.
	 */
	private OrangeRedisZSetCrossKeyClient client;
	
	public OrangeRedisZSetCrossKeyClientFactoryBean(
			Class<?> operationOwner,
			Class<?> clientDefinitionClass,
			OrangeRedisConfiguration configuration
	) {
		super(operationOwner, configuration, clientDefinitionClass);
	}
	
	/**
	 * Gets the Redis key annotation.
	 * This method returns null because cross-key operations do not use a single key annotation.
	 * Instead, keys are determined dynamically based on the operation context.
	 *
	 * @return null as cross-key operations don't use a single key annotation
	 */
	@Override
	protected OrangeRedisKey getOrangeRedisKey() {
		return null;
	}

	/**
	 * Creates and returns the operation argument handler mapping for ZSet cross-key operations.
	 * This method initializes a specialized mapping for handling arguments in ZSet cross-key operations,
	 * using the provided value handler map, configuration, and other necessary components.
	 *
	 * @param valueHandlerMap a map of argument handler classes to their instances
	 * @return the operation argument handler mapping for ZSet cross-key operations
	 */
	@Override
	protected OrangeOperationArgHandlerMapping getOrangeOperationArgHandlerMapping(
			Map<Class<? extends OrangeOperationArgHandler>, OrangeOperationArgHandler> valueHandlerMap) {
		return new OrangeZSetCrossKeysOperationArgHandlerMapping(
				this.getExecutorsMapping(),
				this.getOperationOwner(),
				valueHandlerMap,
				this.getConfiguration(),
				OrangeRedisZSetClient.class
		);
	}
	
	/**
	 * Creates and returns the invocation handler for ZSet cross-key operations.
	 * This method initializes a specialized invocation handler that processes method calls
	 * for ZSet cross-key operations, using the executors mapping, argument handler mapping,
	 * circuit breaker, and properties.
	 *
	 * @return the invocation handler for ZSet cross-key operations
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
	 * Creates and returns the executors mapping for ZSet cross-key operations.
	 * This method initializes the executors mapping if it doesn't exist yet, creating
	 * the necessary ZSet operations, script operations, and executor ID generator.
	 * The mapping is cached statically for reuse.
	 *
	 * @return the executors mapping for ZSet cross-key operations
	 */
	@Override
	protected OrangeRedisExecutorsMapping getExecutorsMapping() {
		if(EXECUTORS_MAPPING != null) {
			return EXECUTORS_MAPPING;
		}
		OrangeRedisZSetOperations operations = new OrangeRedisDefaultZSetOperations(
			this.getRedisTemplate(), 
			getRedisSerializer(),
			getLogger()
		);
		OrangeRedisScriptOperations scriptOperations = new OrangeRedisDefaultScriptOperations(
			this.getRedisTemplate(), 
			getRedisSerializer(),
			getLogger()
		);
		EXECUTORS_MAPPING = new OrangeRedisZSetCrossKeyExecutorsMapping(
			operations, 
			new OrangeRedisZSetCrossKeyExecutorIdGenerator(),
			getListeners(),
			scriptOperations,
			getMultipleListener(),
			getLogger()
		);
		return EXECUTORS_MAPPING;
	}
	
	/**
	 * Gets the collection of multiple set-if-absent listeners for ZSet cross-key operations.
	 * This method returns an empty list as the default implementation for ZSet cross-key operations.
	 * Subclasses can override this method to provide custom listeners for multiple set-if-absent operations.
	 *
	 * @return an empty collection of multiple set-if-absent listeners
	 */
	@Override
	protected Collection<OrangeRedisMultipleSetIfAbsentListener> getMultipleListener() {
		return new ArrayList<>();
	}

	/**
	 * Gets the collection of set-if-absent listeners for ZSet cross-key operations.
	 * This method returns an empty list as the default implementation for ZSet cross-key operations.
	 * Subclasses can override this method to provide custom listeners for set-if-absent operations.
	 *
	 * @return an empty collection of set-if-absent listeners
	 */
	@Override
	protected Collection<OrangeRedisSetIfAbsentListener> getListeners() {
		return new ArrayList<>();
	}

	/**
	 * Gets the Redis value type for ZSet cross-key operations.
	 * This method returns null because the value type is determined dynamically
	 * based on the specific cross-key operation being executed.
	 *
	 * @return null as the value type is determined dynamically
	 */
	@Override
	protected RedisValueTypeEnum getValueType() {
		return null;
	}
	
	/**
	 * Gets the circuit breaker class for ZSet cross-key operations.
	 * This method determines the appropriate circuit breaker class based on the client's annotation.
	 * It first checks for a custom breaker class specified in the annotation, then falls back to
	 * the breaker class name if provided, and finally uses the default circuit breaker if neither
	 * is specified or valid.
	 *
	 * @return the circuit breaker class to be used for ZSet cross-key operations
	 */
	@Override
	protected Class<? extends OrangeRedisCircuitBreaker> getCircuitBreakerClass(){
		this.client = this.getClientDefinitionClass().getAnnotation(OrangeRedisZSetCrossKeyClient.class);
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