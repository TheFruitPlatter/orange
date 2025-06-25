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

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.core.annotation.AnnotationAwareOrderComparator;
import org.springframework.data.redis.core.RedisTemplate;

import com.langwuyue.orange.redis.OrangeRedisCircuitBreaker;
import com.langwuyue.orange.redis.OrangeRedisDefaultCircuitBreaker;
import com.langwuyue.orange.redis.RedisValueTypeEnum;
import com.langwuyue.orange.redis.annotation.multiplelocks.OrangeRedisMultipleLocksClient;
import com.langwuyue.orange.redis.executor.multiplelocks.OrangeExpirationTimeAutoInitializer;
import com.langwuyue.orange.redis.listener.OrangeIfAbsentListenerProxy;
import com.langwuyue.orange.redis.listener.OrangeRedisMultipleSetIfAbsentListener;
import com.langwuyue.orange.redis.listener.OrangeRedisSetIfAbsentListener;
import com.langwuyue.orange.redis.listener.multiplelocks.OrangeRedisMultipleLocksListener;
import com.langwuyue.orange.redis.mapping.OrangeRedisExecutorsMapping;
import com.langwuyue.orange.redis.mapping.OrangeRedisMultipleLocksExecutorIdGenerator;
import com.langwuyue.orange.redis.mapping.OrangeRedisMultipleLocksExecutorsMapping;
import com.langwuyue.orange.redis.operations.OrangeRedisDefaultHashOperations;
import com.langwuyue.orange.redis.operations.OrangeRedisDefaultScriptOperations;
import com.langwuyue.orange.redis.operations.OrangeRedisHashOperations;
import com.langwuyue.orange.redis.operations.OrangeRedisScriptOperations;
import com.langwuyue.orange.redis.timer.OrangeRenewTimerWheel;

/**
 * Factory bean for creating Redis multiple locks client instances.
 * 
 * <p>This factory handles the creation and configuration of Redis multiple locks clients,
 * including executor mappings, listeners, and circuit breakers.</p>
 *
 * @author Liang.Zhong
 * @since 1.0.0
 */
public class OrangeRedisMultipleLocksClientFactoryBean extends OrangeRedisClientAbstractFactoryBean {
	
	private OrangeRedisMultipleLocksClient client;
	
	private static OrangeRedisMultipleLocksExecutorsMapping EXECUTORS_MAPPING;
	
	private OrangeRenewTimerWheel wheel;
	
	private OrangeExpirationTimeAutoInitializer expirationTimeAutoInitializer;
	
	/**
	 * Constructs a new factory bean for Redis multiple locks client.
	 *
	 * @param operationOwner the operation owner class
	 * @param clientDefinitionClass the client definition class
	 * @param configuration the Redis configuration
	 */
	public OrangeRedisMultipleLocksClientFactoryBean(
			Class<?> operationOwner,
			Class<?> clientDefinitionClass,
			OrangeRedisConfiguration configuration
	) {
		super(operationOwner, configuration, clientDefinitionClass);
	}
	
	/**
	 * Gets or creates the executors mapping for Redis multiple locks.
	 * 
	 * <p>This method implements a singleton pattern for the executors mapping,
	 * ensuring thread-safe lazy initialization.</p>
	 * 
	 * @return the Redis multiple locks executors mapping instance
	 */
	@Override
	protected OrangeRedisExecutorsMapping getExecutorsMapping() {
		if(EXECUTORS_MAPPING !=  null) {
			return EXECUTORS_MAPPING;
		}
		RedisTemplate<String, byte[]> template = this.getRedisTemplate();
		OrangeRedisHashOperations operations = new OrangeRedisDefaultHashOperations(template, getRedisSerializer(),getLogger());
		OrangeRedisScriptOperations scriptOperations = new OrangeRedisDefaultScriptOperations(
			this.getRedisTemplate(), 
			getRedisSerializer(),
			getLogger()
		);
		EXECUTORS_MAPPING = new OrangeRedisMultipleLocksExecutorsMapping(
			operations, 
			new OrangeRedisMultipleLocksExecutorIdGenerator(), 
			getListeners(), 
			scriptOperations,
			getMultipleListener(),
			this.wheel,
			this.expirationTimeAutoInitializer,
			getLogger()
		);
		return EXECUTORS_MAPPING;
	}
	
	/**
	 * Gets all configured multiple lock listeners from application context.
	 * 
	 * <p>This method retrieves all beans of type OrangeRedisMultipleLocksListener,
	 * wraps them in proxies, and sorts them according to Spring's ordering rules.</p>
	 * 
	 * @return collection of proxy-wrapped and sorted multiple lock listeners
	 */
	@Override
	protected Collection<OrangeRedisMultipleSetIfAbsentListener> getMultipleListener() {
		Map<String, OrangeRedisMultipleLocksListener> beanMap = this.getApplicationContext().getBeansOfType(OrangeRedisMultipleLocksListener.class);
		if(beanMap == null || beanMap.isEmpty()) {
			return new ArrayList<>();
		}
		List<OrangeRedisMultipleSetIfAbsentListener> listeners = OrangeIfAbsentListenerProxy.proxy(beanMap.values(), OrangeRedisMultipleSetIfAbsentListener.class,this.getProperties().getKeyPrefix());
		AnnotationAwareOrderComparator.sort(listeners);
		return listeners;
	}

	/**
	 * Gets the collection of Redis lock listeners.
	 * 
	 * <p>Currently returns an empty list as this implementation doesn't support
	 * individual lock listeners. Subclasses may override to provide custom listeners.</p>
	 * 
	 * @return empty collection of Redis lock listeners
	 */
	@Override
	protected Collection<OrangeRedisSetIfAbsentListener> getListeners() {
		return new ArrayList<>();
	}

	/**
	 * Gets the value type for Redis multiple locks operations.
	 * 
	 * <p>Returns the value type specified in the {@link OrangeRedisMultipleLocksClient}
	 * annotation on the client interface.</p>
	 * 
	 * @return the Redis value type enum
	 */
	@Override
	protected RedisValueTypeEnum getValueType() {
		return client.valueType();
	}

	/**
	 * Sets the Spring application context and initializes required beans.
	 * 
	 * @param applicationContext the Spring application context
	 * @throws BeansException if bean lookup fails
	 */
	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		super.setApplicationContext(applicationContext);
		this.wheel = applicationContext.getBean(OrangeRenewTimerWheel.class);
		this.expirationTimeAutoInitializer = applicationContext.getBean(OrangeExpirationTimeAutoInitializer.class);
	}
	
	/**
	 * Gets the circuit breaker class for Redis multiple locks operations.
	 * 
	 * <p>Retrieves the circuit breaker configuration from the {@link OrangeRedisMultipleLocksClient}
	 * annotation, supporting both direct class reference and class name specification.</p>
	 * 
	 * <p>If class name specification fails, falls back to the default circuit breaker class.</p>
	 * 
	 * @return the configured circuit breaker class, or default if not specified
	 */
	@Override
	protected Class<? extends OrangeRedisCircuitBreaker> getCircuitBreakerClass(){
		this.client = this.getClientDefinitionClass().getAnnotation(OrangeRedisMultipleLocksClient.class);
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