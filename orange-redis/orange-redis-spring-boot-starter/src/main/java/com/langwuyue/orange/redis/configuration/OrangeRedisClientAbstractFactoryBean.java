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
import java.lang.reflect.Proxy;
import java.util.Collection;
import java.util.Map;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultScriptExecutor;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import com.langwuyue.orange.redis.OrangeRedisCircuitBreaker;
import com.langwuyue.orange.redis.OrangeRedisException;
import com.langwuyue.orange.redis.RedisValueTypeEnum;
import com.langwuyue.orange.redis.annotation.OrangeRedisKey;
import com.langwuyue.orange.redis.context.builder.OrangeOperationArgHandler;
import com.langwuyue.orange.redis.context.builder.OrangeOperationArgHandlerMapping;
import com.langwuyue.orange.redis.listener.OrangeRedisMultipleSetIfAbsentListener;
import com.langwuyue.orange.redis.listener.OrangeRedisSetIfAbsentListener;
import com.langwuyue.orange.redis.logger.OrangeRedisLogger;
import com.langwuyue.orange.redis.mapping.OrangeRedisExecutorsMapping;
import com.langwuyue.orange.redis.registry.OrangeRedisKeyRegistry;
import com.langwuyue.orange.redis.util.OrangeStringTemlateUtils;

/**
 * @author Liang.Zhong
 * @since 1.0.0
 */
public abstract class OrangeRedisClientAbstractFactoryBean implements FactoryBean<Object>, ApplicationContextAware {
	
	private static RedisTemplate<String, byte[]> redisTemplate;
	
	private static OrangeRedisSerializer redisSerializer;
	
	private Class<?> operationOwner;
	
	private OrangeRedisKey redisKey;
	
	private OrangeRedisConfiguration configuration;
	
	private ApplicationContext applicationContext;
	
	private OrangeRedisExecutorsMapping mapping;
	
	private OrangeOperationArgHandlerMapping operationArgHandlerMapping;
	
	private Class<?> clientDefinitionClass;
	
	private OrangeRedisProperties properties;
	
	private OrangeRedisLogger logger;
	
	private OrangeRedisCircuitBreaker circuitBreaker;
	
	private InvocationHandler invocationHandler;
	
	protected OrangeRedisClientAbstractFactoryBean(
			Class<?> operationOwner,
			OrangeRedisConfiguration configuration,
			Class<?> clientDefinitionClass
	) {
		this.operationOwner = operationOwner;
		this.configuration = configuration;
		this.clientDefinitionClass = clientDefinitionClass;
	}
	
	public void init() {
		this.redisKey = getOrangeRedisKey();
		this.mapping = getExecutorsMapping();
		final Map<Class<? extends OrangeOperationArgHandler>, OrangeOperationArgHandler> valueHandlerMap = this.configuration.getValueHandlerMap();
		this.operationArgHandlerMapping = getOrangeOperationArgHandlerMapping(valueHandlerMap);
		this.operationArgHandlerMapping.buildMapping();
		this.invocationHandler = getInvocationHandler();
	}
	
	protected OrangeOperationArgHandlerMapping getOrangeOperationArgHandlerMapping(Map<Class<? extends OrangeOperationArgHandler>, OrangeOperationArgHandler> valueHandlerMap) {
		return new OrangeOperationArgHandlerMapping(this.mapping,this.operationOwner,valueHandlerMap);
	}
	
	protected OrangeRedisKey getOrangeRedisKey() {
		OrangeRedisKey redisKey = this.operationOwner.getAnnotation(OrangeRedisKey.class);
		if(redisKey == null) {
			throw new OrangeRedisException(String.format("@%s not found on the class %s", OrangeRedisKey.class,this.operationOwner));
		}
		String originKey = getOriginKey(redisKey.key());
		OrangeRedisKeyRegistry.register(
			originKey, 
			OrangeStringTemlateUtils.replaceVariable(originKey,OrangeRedisKeyRegistry.VARIABLE_MARK_CHAR) ,
			this.operationOwner
		);
		return redisKey;
	}
	
	protected String getOriginKey(String key) {
		return this.properties.getKeyPrefix() + key;
	}
	
	protected abstract OrangeRedisExecutorsMapping getExecutorsMapping();

	protected abstract Collection<OrangeRedisMultipleSetIfAbsentListener> getMultipleListener();

	protected abstract Collection<OrangeRedisSetIfAbsentListener> getListeners();

	@Override
	public Object getObject() throws Exception {
		return Proxy.newProxyInstance(
			this.operationOwner.getClassLoader(), 
			new Class[] {this.operationOwner}, 
			this.invocationHandler
			
		);
	}

	protected InvocationHandler getInvocationHandler() {
		return new OrangeRedisClientInvocationHandler(
			this.operationOwner,
			this.mapping,
			this.redisKey,
			this.operationArgHandlerMapping,
			getValueType(),
			this.circuitBreaker,
			this.properties
		);
	}

	protected abstract RedisValueTypeEnum getValueType();
	
	protected abstract Class<? extends OrangeRedisCircuitBreaker> getCircuitBreakerClass();

	@Override
	public Class<?> getObjectType() {
		return this.operationOwner;
	}

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.applicationContext = applicationContext;
		OrangeRedisConnectionConfiguration config = applicationContext.getBean(OrangeRedisConnectionConfiguration.class);
		this.properties = config.getProperties();
		String serviceName = this.properties.getTransaction().getServiceName();
		if(serviceName == null || serviceName.trim().isEmpty()) {
			serviceName = applicationContext.getEnvironment().getProperty("spring.application.name", "replaceAppName");
			this.properties.getTransaction().setServiceName(serviceName);
		}
		if(OrangeRedisClientAbstractFactoryBean.redisTemplate == null) {
			RedisTemplate<String, byte[]> template = new RedisTemplate<>();
			template.setConnectionFactory(config.redisConnectionFactory());
			template.setKeySerializer(RedisSerializer.string());
			template.setValueSerializer(OrangeByteArrayRedisSerializer.INSTANCE);
			template.setHashKeySerializer(OrangeByteArrayRedisSerializer.INSTANCE);
			template.setHashValueSerializer(OrangeByteArrayRedisSerializer.INSTANCE);
			template.setScriptExecutor(new DefaultScriptExecutor<>(template));
			template.afterPropertiesSet();
			OrangeRedisClientAbstractFactoryBean.redisTemplate = template;
		}
		if(OrangeRedisClientAbstractFactoryBean.redisSerializer == null) {
			OrangeRedisClientAbstractFactoryBean.redisSerializer = new OrangeRedisSerializer(StringRedisSerializer.UTF_8,this.configuration.getObjectMapper());
		}
		this.logger = applicationContext.getBean(OrangeRedisLogger.class);
		this.circuitBreaker = applicationContext.getBean(getCircuitBreakerClass());
	}

	ApplicationContext getApplicationContext() {
		return applicationContext;
	}

	OrangeRedisConfiguration getConfiguration() {
		return configuration;
	}

	RedisTemplate<String, byte[]> getRedisTemplate() {
		return OrangeRedisClientAbstractFactoryBean.redisTemplate;
	}

	Class<?> getOperationOwner() {
		return operationOwner;
	}

	OrangeRedisKey getRedisKey() {
		return redisKey;
	}

	OrangeRedisExecutorsMapping getMapping() {
		return mapping;
	}

	OrangeOperationArgHandlerMapping getOperationArgHandlerMapping() {
		return operationArgHandlerMapping;
	}

	Class<?> getClientDefinitionClass() {
		return clientDefinitionClass;
	}

	OrangeRedisProperties getProperties() {
		return properties;
	}

	OrangeRedisLogger getLogger() {
		return logger;
	}

	OrangeRedisCircuitBreaker getCircuitBreaker() {
		return circuitBreaker;
	}

	OrangeRedisSerializer getRedisSerializer() {
		return redisSerializer;
	}
	
	public int getActiveRequestCount() {
		if(!(this.invocationHandler instanceof OrangeRedisClientInvocationHandler)) {
			return -1;
		}
		OrangeRedisClientInvocationHandler handler = (OrangeRedisClientInvocationHandler)this.invocationHandler;
		return handler.getActiveRequestCount();
	}
}
