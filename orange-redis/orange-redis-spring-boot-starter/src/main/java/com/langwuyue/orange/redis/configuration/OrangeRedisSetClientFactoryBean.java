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
import com.langwuyue.orange.redis.annotation.set.OrangeRedisSetClient;
import com.langwuyue.orange.redis.listener.OrangeIfAbsentListenerProxy;
import com.langwuyue.orange.redis.listener.OrangeRedisMultipleSetIfAbsentListener;
import com.langwuyue.orange.redis.listener.OrangeRedisSetIfAbsentListener;
import com.langwuyue.orange.redis.listener.set.OrangeRedisSetAddMemberIfAbsentListener;
import com.langwuyue.orange.redis.listener.set.OrangeRedisSetAddMembersIfAbsentListener;
import com.langwuyue.orange.redis.mapping.OrangeRedisExecutorsMapping;
import com.langwuyue.orange.redis.mapping.OrangeRedisSetExecutorIdGenerator;
import com.langwuyue.orange.redis.mapping.OrangeRedisSetExecutorsMapping;
import com.langwuyue.orange.redis.operations.OrangeRedisDefaultScriptOperations;
import com.langwuyue.orange.redis.operations.OrangeRedisDefaultSetOperations;
import com.langwuyue.orange.redis.operations.OrangeRedisScriptOperations;
import com.langwuyue.orange.redis.operations.OrangeRedisSetOperations;

/**
 * Factory bean for creating Redis Set client instances.
 * This class handles the creation and configuration of Redis Set operations,
 * including listeners, executors, and circuit breakers.
 *
 * @author Liang.Zhong
 * @since 1.0.0
 */
public class OrangeRedisSetClientFactoryBean extends OrangeRedisClientAbstractFactoryBean {
	
	private OrangeRedisSetClient client;
	
	private static OrangeRedisSetExecutorsMapping EXECUTORS_MAPPING;
	
	/**
	 * Constructs a new OrangeRedisSetClientFactoryBean.
	 *
	 * @param operationOwner the class that owns the Redis operations
	 * @param clientDefinitionClass the class that defines the client interface
	 * @param configuration the Orange Redis configuration
	 */
	public OrangeRedisSetClientFactoryBean(
			Class<?> operationOwner,
			Class<?> clientDefinitionClass,
			OrangeRedisConfiguration configuration
	) {
		super(operationOwner, configuration, clientDefinitionClass);
	}
	
	/**
	 * Gets the executors mapping for Redis Set operations.
	 * Creates and initializes the mapping if it doesn't exist yet.
	 *
	 * @return the Redis Set executors mapping
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
		EXECUTORS_MAPPING = new OrangeRedisSetExecutorsMapping(
			operations, 
			new OrangeRedisSetExecutorIdGenerator(),
			getListeners(),
			scriptOperations,
			getMultipleListener(),
			getLogger()
		);
		return EXECUTORS_MAPPING;
	}
	
	/**
	 * Gets the collection of multiple Set if-absent listeners.
	 * These listeners handle operations for adding multiple members to a Set if they are absent.
	 *
	 * @return collection of multiple Set if-absent listeners
	 */
	@Override
	protected Collection<OrangeRedisMultipleSetIfAbsentListener> getMultipleListener() {
		Map<String, OrangeRedisSetAddMembersIfAbsentListener> beanMap = this.getApplicationContext().getBeansOfType(OrangeRedisSetAddMembersIfAbsentListener.class);
		if(beanMap == null || beanMap.isEmpty()) {
			return new ArrayList<>();
		}
		List<OrangeRedisMultipleSetIfAbsentListener> listeners = OrangeIfAbsentListenerProxy.proxy(beanMap.values(), OrangeRedisMultipleSetIfAbsentListener.class,this.getProperties().getKeyPrefix());
		AnnotationAwareOrderComparator.sort(listeners);
		return listeners;
	}

	/**
	 * Gets the collection of Redis Set if-absent listeners.
	 * These listeners handle operations for adding a single member to a Set if it is absent.
	 *
	 * @return collection of Redis Set if-absent listeners
	 */
	@Override
	protected Collection<OrangeRedisSetIfAbsentListener> getListeners() {
		Map<String, OrangeRedisSetAddMemberIfAbsentListener> beanMap = this.getApplicationContext().getBeansOfType(OrangeRedisSetAddMemberIfAbsentListener.class);
		if(beanMap == null || beanMap.isEmpty()) {
			return new ArrayList<>();
		}
		List<OrangeRedisSetIfAbsentListener> listeners = OrangeIfAbsentListenerProxy.proxy(beanMap.values(), OrangeRedisSetIfAbsentListener.class,this.getProperties().getKeyPrefix());
		AnnotationAwareOrderComparator.sort(listeners);
		return listeners;
	}

	/**
	 * Gets the Redis value type for this client.
	 * The value type is determined from the client annotation.
	 *
	 * @return the Redis value type enum
	 */
	@Override
	protected RedisValueTypeEnum getValueType() {
		return client.valueType();
	}
	
	/**
	 * Gets the circuit breaker class for Redis Set operations.
	 * The circuit breaker class is determined from the client annotation.
	 * If a custom breaker class name is specified, it will attempt to load that class.
	 *
	 * @return the circuit breaker class for Redis Set operations
	 */
	@Override
	protected Class<? extends OrangeRedisCircuitBreaker> getCircuitBreakerClass(){
		this.client = this.getClientDefinitionClass().getAnnotation(OrangeRedisSetClient.class);
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