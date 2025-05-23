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
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.data.redis.core.RedisTemplate;

import com.langwuyue.orange.redis.OrangeRedisCircuitBreaker;
import com.langwuyue.orange.redis.OrangeRedisDefaultCircuitBreaker;
import com.langwuyue.orange.redis.OrangeRedisException;
import com.langwuyue.orange.redis.RedisValueTypeEnum;
import com.langwuyue.orange.redis.annotation.OrangeRedisKey;
import com.langwuyue.orange.redis.annotation.OrangeRedisTxTimeoutListener;
import com.langwuyue.orange.redis.annotation.transaction.OrangeRedisTransactionClient;
import com.langwuyue.orange.redis.executor.multiplelocks.OrangeExpirationTimeAutoInitializer;
import com.langwuyue.orange.redis.executor.transaction.OrangeRedisDefaultTransactionManager;
import com.langwuyue.orange.redis.executor.transaction.OrangeRedisTransactionTimeoutListener;
import com.langwuyue.orange.redis.listener.OrangeRedisMultipleSetIfAbsentListener;
import com.langwuyue.orange.redis.listener.OrangeRedisSetIfAbsentListener;
import com.langwuyue.orange.redis.logger.OrangeRedisLogger;
import com.langwuyue.orange.redis.mapping.OrangeRedisExecutorsMapping;
import com.langwuyue.orange.redis.mapping.OrangeRedisTransactionExecutorIdGenerator;
import com.langwuyue.orange.redis.mapping.OrangeRedisTransactionExecutorsMapping;
import com.langwuyue.orange.redis.operations.OrangeRedisDefaultHashOperations;
import com.langwuyue.orange.redis.operations.OrangeRedisDefaultScriptOperations;
import com.langwuyue.orange.redis.operations.OrangeRedisDefaultSetOperations;
import com.langwuyue.orange.redis.operations.OrangeRedisDefaultZSetOperations;
import com.langwuyue.orange.redis.operations.OrangeRedisScriptOperations;
import com.langwuyue.orange.redis.timer.OrangeRenewTimerWheel;

/**
 * @author Liang.Zhong
 * @since 1.0.0
 */
public class OrangeRedisTransactionClientFactoryBean extends OrangeRedisClientAbstractFactoryBean {
	
	private OrangeRedisTransactionClient client;
	
	private static OrangeRedisTransactionExecutorsMapping EXECUTORS_MAPPING;
	
	private OrangeRenewTimerWheel wheel;
	
	private OrangeExpirationTimeAutoInitializer expirationTimeAutoInitializer;
	
	private OrangeRedisLogger logger;
	
	public OrangeRedisTransactionClientFactoryBean(
			Class<?> operationOwner,
			Class<?> clientDefinitionClass,
			OrangeRedisConfiguration configuration
	) {
		super(operationOwner, configuration, clientDefinitionClass);
	}
	
	@Override
	protected OrangeRedisExecutorsMapping getExecutorsMapping() {
		if(EXECUTORS_MAPPING !=  null) {
			return EXECUTORS_MAPPING;
		}
		RedisTemplate<String, byte[]> template = this.getRedisTemplate();
		OrangeRedisScriptOperations scriptOperations = new OrangeRedisDefaultScriptOperations(
			this.getRedisTemplate(), 
			getRedisSerializer(),
			getLogger()
		);
		EXECUTORS_MAPPING = new OrangeRedisTransactionExecutorsMapping(
			new OrangeRedisDefaultHashOperations(template, getRedisSerializer(),getLogger()), 
			new OrangeRedisDefaultSetOperations(template,getRedisSerializer(),getLogger()),
			new OrangeRedisDefaultZSetOperations(template,getRedisSerializer(),getLogger()),
			new OrangeRedisTransactionExecutorIdGenerator(), 
			getListeners(), 
			scriptOperations,
			getMultipleListener(),
			getProperties().getTransaction(),
			this.wheel,
			this.expirationTimeAutoInitializer,
			getTransactionTimeoutCallback(),
			this.logger
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
	
	protected Map<String,OrangeRedisTransactionTimeoutListener> getTransactionTimeoutCallback() {
		Map<String, OrangeRedisTransactionTimeoutListener> beanMap = this.getApplicationContext().getBeansOfType(OrangeRedisTransactionTimeoutListener.class);
		if(beanMap == null || beanMap.isEmpty()) {
			return new HashMap<>();
		}
		Map<String,OrangeRedisTransactionTimeoutListener> callbacks = new HashMap<>();
		beanMap.forEach((k,v) -> {
			OrangeRedisTxTimeoutListener listener = v.getClass().getAnnotation(OrangeRedisTxTimeoutListener.class);
			if(listener == null) {
				throw new OrangeRedisException(
					String.format(
						"The class %s must be annotated with @%s", 
						v.getClass(),
						OrangeRedisTransactionTimeoutListener.class
					)
				);
			}
			Class<?> keyClass = listener.key();
			OrangeRedisKey redisKey = keyClass.getAnnotation(OrangeRedisKey.class);
			if(redisKey == null) {
				throw new OrangeRedisException(
					String.format(
						"The key of @%s must be annotated with @%s", 
						OrangeRedisTransactionTimeoutListener.class,
						OrangeRedisKey.class
					)
				);
			}
			callbacks.put(getOriginKey(redisKey.key()), v);
		});
		return callbacks;
	}

	@Override
	protected RedisValueTypeEnum getValueType() {
		return client.valueType();
	}
	
	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		super.setApplicationContext(applicationContext);
		this.wheel = applicationContext.getBean(OrangeRenewTimerWheel.class);
		this.expirationTimeAutoInitializer = applicationContext.getBean(OrangeExpirationTimeAutoInitializer.class);
		this.logger = applicationContext.getBean(OrangeRedisLogger.class);
	}
	
	@Override
	protected Class<? extends OrangeRedisCircuitBreaker> getCircuitBreakerClass(){
		this.client = this.getClientDefinitionClass().getAnnotation(OrangeRedisTransactionClient.class);
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

	protected void destroy() throws Exception {
		OrangeRedisDefaultTransactionManager transactionManager = getTransactionManger();
		if(transactionManager != null) {
			transactionManager.destory();
		}
		if(this.wheel != null) {
			this.wheel.destory();
		}
	}
	
	public Set<Object> getDeadTransaction() throws Exception {
		return getTransactionManger().getDeadTransaction();
	}
	
	protected OrangeRedisDefaultTransactionManager getTransactionManger() {
		if(EXECUTORS_MAPPING != null) {
			return EXECUTORS_MAPPING.getTransactionManager();
		}
		return null;
	}
}
