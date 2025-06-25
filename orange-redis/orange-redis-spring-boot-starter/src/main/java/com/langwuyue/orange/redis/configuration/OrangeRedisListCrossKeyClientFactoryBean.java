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
import com.langwuyue.orange.redis.annotation.cross.OrangeRedisListCrossKeyClient;
import com.langwuyue.orange.redis.annotation.list.OrangeRedisListClient;
import com.langwuyue.orange.redis.context.builder.OrangeListCrossKeysOperationArgHandlerMapping;
import com.langwuyue.orange.redis.context.builder.OrangeOperationArgHandler;
import com.langwuyue.orange.redis.context.builder.OrangeOperationArgHandlerMapping;
import com.langwuyue.orange.redis.listener.OrangeRedisMultipleSetIfAbsentListener;
import com.langwuyue.orange.redis.listener.OrangeRedisSetIfAbsentListener;
import com.langwuyue.orange.redis.mapping.OrangeRedisExecutorsMapping;
import com.langwuyue.orange.redis.mapping.OrangeRedisListCrossKeyExecutorIdGenerator;
import com.langwuyue.orange.redis.mapping.OrangeRedisListCrossKeyExecutorsMapping;
import com.langwuyue.orange.redis.operations.OrangeRedisDefaultListOperations;
import com.langwuyue.orange.redis.operations.OrangeRedisDefaultScriptOperations;
import com.langwuyue.orange.redis.operations.OrangeRedisListOperations;
import com.langwuyue.orange.redis.operations.OrangeRedisScriptOperations;

/**
 * Factory bean for creating Redis List clients that operate across multiple keys.
 *
 * <p>This factory bean creates and configures Redis List client instances that can perform
 * operations involving multiple Redis keys. It handles the initialization of:
 * <ul>
 *   <li>Redis List operations implementation</li>
 *   <li>Cross-key executor mappings</li>
 *   <li>Circuit breakers configuration</li>
 *   <li>Operation argument handlers</li>
 * </ul>
 *
 * <p>The factory creates a proxy implementation that delegates Redis operations to the
 * appropriate executors based on method annotations. It manages the lifecycle of these
 * components and ensures they are properly configured with the necessary dependencies.
 *
 * <p>This implementation is thread-safe and caches the executors mapping at the class level
 * for performance optimization.
 *
 * @author Liang.Zhong
 * @since 1.0.0
 * @see OrangeRedisClientAbstractFactoryBean
 * @see OrangeRedisListCrossKeyClient
 * @see OrangeRedisListCrossKeyExecutorsMapping
 */
public class OrangeRedisListCrossKeyClientFactoryBean extends OrangeRedisClientAbstractFactoryBean {
	
	/**
	 * Shared static mapping of Redis List cross-key executors.
	 * 
	 * <p>This static field caches the executors mapping to improve performance by reusing
	 * the same mapping across multiple instances of this factory bean. The mapping contains
	 * all the necessary executors for handling cross-key Redis List operations.
	 * 
	 * <p>The mapping is lazily initialized when first needed and then reused for subsequent requests.
	 */
	private static OrangeRedisListCrossKeyExecutorsMapping EXECUTORS_MAPPING;
	
	/**
	 * The Redis List cross-key client annotation instance extracted from the client definition class.
	 * 
	 * <p>This field stores the annotation metadata that configures how the cross-key List client
	 * should behave, including circuit breaker settings and other operational parameters.
	 */
	private OrangeRedisListCrossKeyClient client;
	
	/**
	 * Constructs a new OrangeRedisListCrossKeyClientFactoryBean with the specified parameters.
	 * 
	 * <p>This constructor initializes the factory bean with:
	 * <ul>
	 *   <li>The operation owner class that will use the cross-key Redis List operations</li>
	 *   <li>The client definition class annotated with {@link OrangeRedisListCrossKeyClient}</li>
	 *   <li>The Redis connection configuration containing connection details</li>
	 * </ul>
	 * 
	 * @param operationOwner the class that owns the cross-key Redis List operations
	 * @param clientDefinitionClass the interface class annotated with {@link OrangeRedisListCrossKeyClient}
	 *                             that defines the cross-key Redis List operations
	 * @param configuration the Redis connection configuration containing connection details
	 */
	public OrangeRedisListCrossKeyClientFactoryBean(
			Class<?> operationOwner,
			Class<?> clientDefinitionClass,
			OrangeRedisConfiguration configuration
	) {
		super(operationOwner, configuration, clientDefinitionClass);
	}
	
	@Override
	protected OrangeRedisKey getOrangeRedisKey() {
		return null;
	}

	@Override
	protected OrangeOperationArgHandlerMapping getOrangeOperationArgHandlerMapping(
			Map<Class<? extends OrangeOperationArgHandler>, OrangeOperationArgHandler> valueHandlerMap) {
		return new OrangeListCrossKeysOperationArgHandlerMapping(
				this.getExecutorsMapping(),
				this.getOperationOwner(),
				valueHandlerMap,
				this.getConfiguration(),
				OrangeRedisListClient.class
		);
	}
	
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
	 * Gets or creates the executors mapping for cross-key Redis List operations.
	 * 
	 * <p>This method implements a lazy initialization pattern with caching at the class level.
	 * On first call, it creates a new {@link OrangeRedisListCrossKeyExecutorsMapping} instance with:
	 * <ul>
	 *   <li>Cross-key List operations implementation</li>
	 *   <li>Executor ID generator</li>
	 *   <li>Listeners collection</li>
	 *   <li>Script operations implementation</li>
	 *   <li>Multiple listeners collection</li>
	 *   <li>Logger instance</li>
	 * </ul>
	 * 
	 * <p>Subsequent calls return the cached instance for better performance.
	 * 
	 * @return the executors mapping instance for cross-key Redis List operations
	 */
	@Override
	protected OrangeRedisExecutorsMapping getExecutorsMapping() {
		if(EXECUTORS_MAPPING != null) {
			return EXECUTORS_MAPPING;
		}
		OrangeRedisListOperations operations = new OrangeRedisDefaultListOperations(
			this.getRedisTemplate(), 
			getRedisSerializer(),
			getLogger()
		);
		OrangeRedisScriptOperations scriptOperations = new OrangeRedisDefaultScriptOperations(
			this.getRedisTemplate(), 
			getRedisSerializer(),
			getLogger()
		);
		EXECUTORS_MAPPING = new OrangeRedisListCrossKeyExecutorsMapping(
			operations, 
			new OrangeRedisListCrossKeyExecutorIdGenerator(),
			getListeners(),
			scriptOperations,
			getMultipleListener(),
			getLogger()
		);
		return EXECUTORS_MAPPING;
	}
	
	@Override
	protected Collection<OrangeRedisMultipleSetIfAbsentListener> getMultipleListener() {
		return new ArrayList<>();
	}

	@Override
	protected Collection<OrangeRedisSetIfAbsentListener> getListeners() {
		return new ArrayList<>();
	}

	@Override
	protected RedisValueTypeEnum getValueType() {
		return null;
	}
	
	/**
	 * Gets the circuit breaker class configured for this cross-key List client.
	 * 
	 * <p>This method determines the circuit breaker implementation to use by:
	 * <ol>
	 *   <li>First checking the {@link OrangeRedisListCrossKeyClient#breaker()} value</li>
	 *   <li>If the default breaker is specified, falls back to checking {@link OrangeRedisListCrossKeyClient#breakerClassName()}</li>
	 *   <li>If a custom class name is provided, attempts to load that class dynamically</li>
	 * </ol>
	 * 
	 * <p>If any errors occur during class loading, the method falls back to the default
	 * circuit breaker and logs a warning.
	 * 
	 * @return the circuit breaker class to use for this client, never null
	 */
	@Override
	protected Class<? extends OrangeRedisCircuitBreaker> getCircuitBreakerClass(){
		this.client = this.getClientDefinitionClass().getAnnotation(OrangeRedisListCrossKeyClient.class);
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