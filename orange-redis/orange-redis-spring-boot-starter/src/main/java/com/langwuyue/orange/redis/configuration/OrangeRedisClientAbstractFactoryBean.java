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
 * Abstract factory bean for creating Redis client proxy instances in the Orange Redis framework.
 * 
 * <p>This class serves as a base factory bean implementation that creates proxy instances
 * for Redis operations. It integrates with Spring's dependency injection system through
 * {@link FactoryBean} and {@link ApplicationContextAware} interfaces.
 * 
 * <p>The factory bean handles:
 * <ul>
 *   <li>Redis connection and template configuration</li>
 *   <li>Proxy creation for Redis operations</li>
 *   <li>Redis key management and validation</li>
 *   <li>Operation argument handling and mapping</li>
 *   <li>Circuit breaker integration</li>
 *   <li>Serialization configuration</li>
 * </ul>
 * 
 * <p>Subclasses must implement specific methods to define:
 * <ul>
 *   <li>Executor mappings for Redis operations</li>
 *   <li>Value type handling</li>
 *   <li>Circuit breaker configuration</li>
 *   <li>Event listeners for Redis operations</li>
 * </ul>
 *
 * @author Liang.Zhong
 * @since 1.0.0
 * @see FactoryBean
 * @see ApplicationContextAware
 * @see OrangeRedisConfiguration
 * @see OrangeRedisKey
 */
public abstract class OrangeRedisClientAbstractFactoryBean implements FactoryBean<Object>, ApplicationContextAware {
	
	/** Shared Redis template for byte array operations */
	private static RedisTemplate<String, byte[]> redisTemplate;
	
	/** Shared Redis serializer for converting objects to/from Redis data format */
	private static OrangeRedisSerializer redisSerializer;
	
	/** The interface class that defines Redis operations */
	private Class<?> operationOwner;
	
	/** Redis key annotation metadata from the operation owner class */
	private OrangeRedisKey redisKey;
	
	/** Configuration for Redis connections and operations */
	private OrangeRedisConfiguration configuration;
	
	/** Spring application context for accessing beans */
	private ApplicationContext applicationContext;
	
	/** Mapping between method signatures and Redis operation executors */
	private OrangeRedisExecutorsMapping mapping;
	
	/** Mapping for handling operation arguments */
	private OrangeOperationArgHandlerMapping operationArgHandlerMapping;
	
	/** The client definition class type */
	private Class<?> clientDefinitionClass;
	
	/** Redis properties from configuration */
	private OrangeRedisProperties properties;
	
	/** Logger for Redis operations */
	private OrangeRedisLogger logger;
	
	/** Circuit breaker for Redis fault tolerance */
	private OrangeRedisCircuitBreaker circuitBreaker;
	
	/** Invocation handler for the proxy instance */
	private InvocationHandler invocationHandler;
	
	/**
	 * Protected constructor for creating a new Redis client factory bean.
	 * 
	 * <p>Initializes the factory with essential components needed for Redis operations.
	 *
	 * @param operationOwner the interface class that defines Redis operations
	 * @param configuration the Redis connection and operation configuration
	 * @param clientDefinitionClass the class type that defines the client implementation
	 */
	protected OrangeRedisClientAbstractFactoryBean(
			Class<?> operationOwner,
			OrangeRedisConfiguration configuration,
			Class<?> clientDefinitionClass
	) {
		this.operationOwner = operationOwner;
		this.configuration = configuration;
		this.clientDefinitionClass = clientDefinitionClass;
	}
	
	/**
	 * Initializes the factory bean by setting up required components.
	 * 
	 * <p>This method performs the following initialization steps:
	 * <ul>
	 *   <li>Retrieves and validates Redis key configuration</li>
	 *   <li>Sets up executor mappings for Redis operations</li>
	 *   <li>Configures argument handlers for operations</li>
	 *   <li>Creates the invocation handler for the proxy</li>
	 * </ul>
	 */
	public void init() {
		this.redisKey = getOrangeRedisKey();
		this.mapping = getExecutorsMapping();
		final Map<Class<? extends OrangeOperationArgHandler>, OrangeOperationArgHandler> valueHandlerMap = this.configuration.getValueHandlerMap();
		this.operationArgHandlerMapping = getOrangeOperationArgHandlerMapping(valueHandlerMap);
		this.operationArgHandlerMapping.buildMapping();
		this.invocationHandler = getInvocationHandler();
	}
	
	/**
	 * Creates an operation argument handler mapping for Redis operations.
	 * 
	 * <p>This method creates a mapping between operation methods and their argument handlers
	 * based on the provided value handler map.
	 *
	 * @param valueHandlerMap a map of argument handler classes to their instances
	 * @return a new operation argument handler mapping
	 */
	protected OrangeOperationArgHandlerMapping getOrangeOperationArgHandlerMapping(Map<Class<? extends OrangeOperationArgHandler>, OrangeOperationArgHandler> valueHandlerMap) {
		return new OrangeOperationArgHandlerMapping(this.mapping,this.operationOwner,valueHandlerMap);
	}
	
	/**
	 * Retrieves and validates the Redis key annotation from the operation owner class.
	 * 
	 * <p>This method:
	 * <ul>
	 *   <li>Extracts the {@link OrangeRedisKey} annotation from the operation owner class</li>
	 *   <li>Validates that the annotation exists</li>
	 *   <li>Processes the key with prefix and registers it with the key registry</li>
	 * </ul>
	 *
	 * @return the Redis key annotation from the operation owner class
	 * @throws OrangeRedisException if the annotation is not found on the class
	 */
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
	
	/**
	 * Applies the configured key prefix to the provided Redis key.
	 *
	 * @param key the original Redis key without prefix
	 * @return the key with the configured prefix applied
	 */
	protected String getOriginKey(String key) {
		return this.properties.getKeyPrefix() + key;
	}
	
	/**
	 * Returns the executor mappings for Redis operations.
	 * 
	 * <p>Subclasses must implement this method to define the mapping between
	 * method signatures and their corresponding Redis operation executors.
	 *
	 * @return the Redis executors mapping
	 */
	protected abstract OrangeRedisExecutorsMapping getExecutorsMapping();

	/**
	 * Returns the collection of multiple set-if-absent listeners.
	 * 
	 * <p>Subclasses must implement this method to provide listeners for
	 * multiple set-if-absent operations.
	 *
	 * @return a collection of multiple set-if-absent listeners
	 */
	protected abstract Collection<OrangeRedisMultipleSetIfAbsentListener> getMultipleListener();

	/**
	 * Returns the collection of set-if-absent listeners.
	 * 
	 * <p>Subclasses must implement this method to provide listeners for
	 * set-if-absent operations.
	 *
	 * @return a collection of set-if-absent listeners
	 */
	protected abstract Collection<OrangeRedisSetIfAbsentListener> getListeners();

	/**
	 * Creates and returns the Redis client proxy instance.
	 * 
	 * <p>This method implements the {@link FactoryBean#getObject()} method to create
	 * a dynamic proxy that implements the operation owner interface. The proxy uses
	 * the configured invocation handler to process Redis operations.
	 *
	 * @return a proxy instance that implements the operation owner interface
	 * @throws Exception if an error occurs during proxy creation
	 */
	@Override
	public Object getObject() throws Exception {
		return Proxy.newProxyInstance(
			this.operationOwner.getClassLoader(), 
			new Class[] {this.operationOwner}, 
			this.invocationHandler
			
		);
	}

	/**
	 * Creates and returns the invocation handler for the Redis client proxy.
	 * 
	 * <p>This method creates a new {@link OrangeRedisClientInvocationHandler} instance
	 * configured with all necessary components for handling Redis operations.
	 *
	 * @return the invocation handler for the Redis client proxy
	 */
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

	/**
	 * Returns the Redis value type enumeration for this client.
	 * 
	 * <p>Subclasses must implement this method to specify how Redis values
	 * should be handled and serialized.
	 *
	 * @return the Redis value type enumeration
	 */
	protected abstract RedisValueTypeEnum getValueType();
	
	/**
	 * Returns the circuit breaker class to be used for this Redis client.
	 * 
	 * <p>Subclasses must implement this method to specify which circuit breaker
	 * implementation should be used for handling Redis operation failures.
	 *
	 * @return the circuit breaker class
	 */
	protected abstract Class<? extends OrangeRedisCircuitBreaker> getCircuitBreakerClass();

	/**
	 * Returns the type of object that this FactoryBean creates.
	 * 
	 * <p>This method implements the {@link FactoryBean#getObjectType()} method
	 * to return the operation owner interface class.
	 *
	 * @return the operation owner interface class
	 */
	@Override
	public Class<?> getObjectType() {
		return this.operationOwner;
	}

	/**
	 * Sets the Spring application context and initializes Redis components.
	 * 
	 * <p>This method implements the {@link ApplicationContextAware#setApplicationContext(ApplicationContext)}
	 * method to receive the Spring application context. It performs several initialization steps:
	 * <ul>
	 *   <li>Retrieves Redis connection configuration</li>
	 *   <li>Sets up service name for transactions</li>
	 *   <li>Initializes shared Redis template if not already created</li>
	 *   <li>Configures Redis serializers</li>
	 *   <li>Retrieves logger and circuit breaker beans</li>
	 * </ul>
	 *
	 * @param applicationContext the Spring application context
	 * @throws BeansException if an error occurs while accessing the application context
	 */
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

	/**
	 * Returns the Spring application context.
	 *
	 * @return the application context
	 */
	ApplicationContext getApplicationContext() {
		return applicationContext;
	}

	/**
	 * Returns the Redis configuration.
	 *
	 * @return the Redis configuration
	 */
	OrangeRedisConfiguration getConfiguration() {
		return configuration;
	}

	/**
	 * Returns the shared Redis template instance.
	 *
	 * @return the Redis template for byte array operations
	 */
	RedisTemplate<String, byte[]> getRedisTemplate() {
		return OrangeRedisClientAbstractFactoryBean.redisTemplate;
	}

	/**
	 * Returns the operation owner interface class.
	 *
	 * @return the interface class that defines Redis operations
	 */
	Class<?> getOperationOwner() {
		return operationOwner;
	}

	/**
	 * Returns the Redis key annotation metadata.
	 *
	 * @return the Redis key annotation from the operation owner class
	 */
	OrangeRedisKey getRedisKey() {
		return redisKey;
	}

	/**
	 * Returns the executor mappings for Redis operations.
	 *
	 * @return the mapping between method signatures and Redis operation executors
	 */
	OrangeRedisExecutorsMapping getMapping() {
		return mapping;
	}

	/**
	 * Returns the operation argument handler mapping.
	 *
	 * @return the mapping for handling operation arguments
	 */
	OrangeOperationArgHandlerMapping getOperationArgHandlerMapping() {
		return operationArgHandlerMapping;
	}

	/**
	 * Returns the client definition class.
	 *
	 * @return the class type that defines the client implementation
	 */
	Class<?> getClientDefinitionClass() {
		return clientDefinitionClass;
	}

	/**
	 * Returns the Redis properties.
	 *
	 * @return the Redis properties from configuration
	 */
	OrangeRedisProperties getProperties() {
		return properties;
	}

	/**
	 * Returns the Redis logger.
	 *
	 * @return the logger for Redis operations
	 */
	OrangeRedisLogger getLogger() {
		return logger;
	}

	/**
	 * Returns the circuit breaker instance.
	 *
	 * @return the circuit breaker for Redis fault tolerance
	 */
	OrangeRedisCircuitBreaker getCircuitBreaker() {
		return circuitBreaker;
	}

	/**
	 * Returns the Redis serializer.
	 *
	 * @return the serializer for converting objects to/from Redis data format
	 */
	OrangeRedisSerializer getRedisSerializer() {
		return redisSerializer;
	}
	
	/**
	 * Returns the count of active Redis requests being processed.
	 * 
	 * <p>This method provides visibility into the current workload of the Redis client.
	 * It can be used for monitoring or throttling purposes.
	 *
	 * @return the number of active requests, or -1 if the invocation handler is not properly initialized
	 */
	public int getActiveRequestCount() {
		if(!(this.invocationHandler instanceof OrangeRedisClientInvocationHandler)) {
			return -1;
		}
		OrangeRedisClientInvocationHandler handler = (OrangeRedisClientInvocationHandler)this.invocationHandler;
		return handler.getActiveRequestCount();
	}
}