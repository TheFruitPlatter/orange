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
import java.util.List;
import java.util.Map;

import org.springframework.core.annotation.AnnotationAwareOrderComparator;

import com.langwuyue.orange.redis.OrangeRedisCircuitBreaker;
import com.langwuyue.orange.redis.OrangeRedisDefaultCircuitBreaker;
import com.langwuyue.orange.redis.RedisValueTypeEnum;
import com.langwuyue.orange.redis.annotation.zset.OrangeRedisZSetClient;
import com.langwuyue.orange.redis.listener.OrangeIfAbsentListenerProxy;
import com.langwuyue.orange.redis.listener.OrangeRedisMultipleSetIfAbsentListener;
import com.langwuyue.orange.redis.listener.OrangeRedisSetIfAbsentListener;
import com.langwuyue.orange.redis.listener.zset.OrangeRedisZSetAddMemberIfAbsentListener;
import com.langwuyue.orange.redis.listener.zset.OrangeRedisZSetAddMembersIfAbsentListener;
import com.langwuyue.orange.redis.mapping.OrangeRedisExecutorsMapping;
import com.langwuyue.orange.redis.mapping.OrangeRedisZSetExecutorIdGenerator;
import com.langwuyue.orange.redis.mapping.OrangeRedisZSetExecutorsMapping;
import com.langwuyue.orange.redis.operations.OrangeRedisDefaultScriptOperations;
import com.langwuyue.orange.redis.operations.OrangeRedisDefaultZSetOperations;
import com.langwuyue.orange.redis.operations.OrangeRedisScriptOperations;
import com.langwuyue.orange.redis.operations.OrangeRedisZSetOperations;

/**
 * Factory bean for creating Redis ZSet client proxies.
 * This class is responsible for creating and configuring proxy instances that handle
 * Redis ZSet (Sorted Set) operations. It extends the abstract factory bean to provide
 * specific implementation for ZSet operations.
 * 
 * <p>The factory bean manages the creation of invocation handlers, executors mappings,
 * and various listeners required for ZSet operations. It also handles circuit breaker
 * configuration for fault tolerance.</p>
 * 
 * @author Liang.Zhong
 * @since 1.0.0
 */
public class OrangeRedisZSetClientFactoryBean extends OrangeRedisClientAbstractFactoryBean {
	
	/**
	 * The ZSet client annotation instance.
	 * This field stores the annotation metadata from {@link OrangeRedisZSetClient}
	 * that configures this client's behavior, including circuit breaker settings
	 * and value type configuration.
	 *
	 */
	private OrangeRedisZSetClient client;
	
	/**
	 * Static cache for the ZSet executors mapping.
	 * This mapping is shared across all instances of the factory bean to avoid
	 * redundant creation of executors for the same operations. It is lazily
	 * initialized when first accessed through {@link #getExecutorsMapping()}.
	 *
	 */
	private static OrangeRedisZSetExecutorsMapping EXECUTORS_MAPPING;
	
	/**
	 * Constructs a new OrangeRedisZSetClientFactoryBean with the specified parameters.
	 * This constructor initializes the factory bean with the necessary components to create
	 * and configure Redis ZSet operation proxies. It delegates to the parent class constructor
	 * for common initialization tasks.
	 *
	 * @param operationOwner The class that owns the Redis ZSet operations to be proxied.
	 *                       This is typically an interface defining the ZSet operations.
	 * @param clientDefinitionClass The class that contains the {@link OrangeRedisZSetClient} annotation,
	 *                             which provides configuration details for the client.
	 * @param configuration The Orange Redis configuration that contains global settings
	 *                     such as connection details, serializers, and other Redis-related properties.
	 */
	public OrangeRedisZSetClientFactoryBean(
			Class<?> operationOwner,
			Class<?> clientDefinitionClass,
			OrangeRedisConfiguration configuration
	) {
		super(operationOwner, configuration, clientDefinitionClass);
	}
	
	/**
	 * Gets the executors mapping for Redis ZSet operations.
	 * This method initializes and returns the mapping between operation IDs and their executors.
	 * If the mapping already exists, it returns the cached instance.
	 * Otherwise, it creates a new mapping with ZSet operations, script operations, listeners, and logger.
	 *
	 * @return the Redis ZSet executors mapping
	 *
	 */
	@Override
	protected OrangeRedisExecutorsMapping getExecutorsMapping() {
		if(EXECUTORS_MAPPING !=  null) {
			return EXECUTORS_MAPPING;
		}
		OrangeRedisZSetOperations operations = new OrangeRedisDefaultZSetOperations(this.getRedisTemplate(), getRedisSerializer(),getLogger());
		OrangeRedisScriptOperations scriptOperations = new OrangeRedisDefaultScriptOperations(
			this.getRedisTemplate(), 
			getRedisSerializer(),
			getLogger()
		);
		EXECUTORS_MAPPING = new OrangeRedisZSetExecutorsMapping(
			operations, 
			new OrangeRedisZSetExecutorIdGenerator(), 
			getListeners(), 
			scriptOperations,
			getMultipleListener(),
			getLogger()
		);
		return EXECUTORS_MAPPING;
	}
	
	/**
	 * Gets the collection of multiple set-if-absent listeners for ZSet operations.
	 * This method retrieves all beans of type OrangeRedisZSetAddMembersIfAbsentListener from the application context,
	 * proxies them to implement the OrangeRedisMultipleSetIfAbsentListener interface, and sorts them according to
	 * their annotation-based order.
	 *
	 * @return a collection of multiple set-if-absent listeners, or an empty list if none are found
	 *
	 */
	@Override
	protected Collection<OrangeRedisMultipleSetIfAbsentListener> getMultipleListener() {
		Map<String, OrangeRedisZSetAddMembersIfAbsentListener> beanMap = this.getApplicationContext().getBeansOfType(OrangeRedisZSetAddMembersIfAbsentListener.class);
		if(beanMap == null || beanMap.isEmpty()) {
			return new ArrayList<>();
		}
		List<OrangeRedisMultipleSetIfAbsentListener> listeners = OrangeIfAbsentListenerProxy.proxy(
			beanMap.values(), 
			OrangeRedisMultipleSetIfAbsentListener.class,
			this.getProperties().getKeyPrefix()
		);
		AnnotationAwareOrderComparator.sort(listeners);
		return listeners;
	}

	/**
	 * Gets the collection of Redis set-if-absent listeners for ZSet operations.
	 * This method retrieves all beans of type OrangeRedisZSetAddIfAbsentListener from the application context,
	 * proxies them to implement the OrangeRedisSetIfAbsentListener interface, and sorts them according to
	 * their annotation-based order.
	 *
	 * @return a collection of Redis set-if-absent listeners, or an empty list if none are found
	 *
	 */
	@Override
	protected Collection<OrangeRedisSetIfAbsentListener> getListeners() {
		Map<String, OrangeRedisZSetAddMemberIfAbsentListener> beanMap = this.getApplicationContext().getBeansOfType(OrangeRedisZSetAddMemberIfAbsentListener.class);
		if(beanMap == null || beanMap.isEmpty()) {
			return new ArrayList<>();
		}
		List<OrangeRedisSetIfAbsentListener> listeners = OrangeIfAbsentListenerProxy.proxy(
			beanMap.values(), 
			OrangeRedisSetIfAbsentListener.class,
			this.getProperties().getKeyPrefix()
		);
		AnnotationAwareOrderComparator.sort(listeners);
		return listeners;
	}

	/**
	 * Gets the Redis value type enum for ZSet operations.
	 * This method returns the ZSET value type, which is used to determine the appropriate
	 * Redis data structure operations to use.
	 *
	 * @return the Redis value type enum (ZSET)
	 *
	 */
	@Override
	protected RedisValueTypeEnum getValueType() {
		return client.valueType();
	}
	
	/**
	 * Gets the circuit breaker class for Redis ZSet operations.
	 * This method retrieves the circuit breaker class from the client annotation.
	 * If a custom breaker class is not specified, it attempts to load the class by name.
	 * If that fails, it falls back to the default circuit breaker class.
	 *
	 * @return the class of the Redis circuit breaker to use
	 *
	 */
	@Override
	protected Class<? extends OrangeRedisCircuitBreaker> getCircuitBreakerClass(){
		this.client = this.getClientDefinitionClass().getAnnotation(OrangeRedisZSetClient.class);
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