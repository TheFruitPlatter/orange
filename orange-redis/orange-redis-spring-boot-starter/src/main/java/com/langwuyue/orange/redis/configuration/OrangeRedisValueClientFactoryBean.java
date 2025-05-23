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

import com.langwuyue.orange.redis.OrangeRedisCircuitBreaker;
import com.langwuyue.orange.redis.OrangeRedisDefaultCircuitBreaker;
import com.langwuyue.orange.redis.OrangeRedisException;
import com.langwuyue.orange.redis.RedisValueTypeEnum;
import com.langwuyue.orange.redis.annotation.value.OrangeRedisValueClient;
import com.langwuyue.orange.redis.executor.multiplelocks.OrangeExpirationTimeAutoInitializer;
import com.langwuyue.orange.redis.listener.OrangeIfAbsentListenerProxy;
import com.langwuyue.orange.redis.listener.OrangeRedisMultipleSetIfAbsentListener;
import com.langwuyue.orange.redis.listener.OrangeRedisSetIfAbsentListener;
import com.langwuyue.orange.redis.listener.value.OrangeRedisValueSetIfAbsentListener;
import com.langwuyue.orange.redis.mapping.OrangeRedisExecutorsMapping;
import com.langwuyue.orange.redis.mapping.OrangeRedisValueExecutorIdGenerator;
import com.langwuyue.orange.redis.mapping.OrangeRedisValueExecutorsMapping;
import com.langwuyue.orange.redis.operations.OrangeRedisDefaultScriptOperations;
import com.langwuyue.orange.redis.operations.OrangeRedisDefaultValueOperations;
import com.langwuyue.orange.redis.operations.OrangeRedisScriptOperations;
import com.langwuyue.orange.redis.operations.OrangeRedisValueOperations;
import com.langwuyue.orange.redis.timer.OrangeRenewTimerWheel;

/**
 * @author Liang.Zhong
 * @since 1.0.0
 */
public class OrangeRedisValueClientFactoryBean extends OrangeRedisClientAbstractFactoryBean {
	
	private OrangeRedisValueClient client;
	
	private static OrangeRedisValueExecutorsMapping EXECUTORS_MAPPING;
	
	private OrangeRenewTimerWheel wheel;
	
	private OrangeExpirationTimeAutoInitializer expirationTimeAutoInitializer;
	
	public OrangeRedisValueClientFactoryBean(
			Class<?> operationOwner,
			Class<?> clientDefinitionClass,
			OrangeRedisConfiguration configuration
	) {
		super(operationOwner, configuration, clientDefinitionClass);
	}
	
	@Override
	protected OrangeRedisExecutorsMapping getExecutorsMapping() {
		if(EXECUTORS_MAPPING != null) {
			return EXECUTORS_MAPPING;
		}
		OrangeRedisValueOperations operations = new OrangeRedisDefaultValueOperations(
			this.getRedisTemplate(), 
			getRedisSerializer(),
			getLogger()
		);
		OrangeRedisScriptOperations scriptOperations = new OrangeRedisDefaultScriptOperations(
			this.getRedisTemplate(), 
			getRedisSerializer(),
			getLogger()
		);
		EXECUTORS_MAPPING = new OrangeRedisValueExecutorsMapping(
			operations, 
			new OrangeRedisValueExecutorIdGenerator(), 
			getListeners(), 
			scriptOperations,
			getMultipleListener(),
			wheel,
			expirationTimeAutoInitializer,
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
		Map<String, OrangeRedisValueSetIfAbsentListener> beanMap = this.getApplicationContext().getBeansOfType(OrangeRedisValueSetIfAbsentListener.class);
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

	@Override
	protected RedisValueTypeEnum getValueType() {
		return this.client.valueType();
	}
	
	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		super.setApplicationContext(applicationContext);
		this.wheel = applicationContext.getBean(OrangeRenewTimerWheel.class);
		this.expirationTimeAutoInitializer = applicationContext.getBean(OrangeExpirationTimeAutoInitializer.class);
	}
	
	@Override
	protected Class<? extends OrangeRedisCircuitBreaker> getCircuitBreakerClass(){
		this.client = this.getClientDefinitionClass().getAnnotation(OrangeRedisValueClient.class);
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
