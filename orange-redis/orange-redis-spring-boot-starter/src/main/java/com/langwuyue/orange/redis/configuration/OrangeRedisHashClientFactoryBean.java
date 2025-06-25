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
import java.util.List;
import java.util.Map;

import org.springframework.core.annotation.AnnotationAwareOrderComparator;

import com.langwuyue.orange.redis.OrangeRedisCircuitBreaker;
import com.langwuyue.orange.redis.OrangeRedisDefaultCircuitBreaker;
import com.langwuyue.orange.redis.RedisValueTypeEnum;
import com.langwuyue.orange.redis.annotation.hash.OrangeRedisHashClient;
import com.langwuyue.orange.redis.listener.OrangeIfAbsentListenerProxy;
import com.langwuyue.orange.redis.listener.OrangeRedisMultipleSetIfAbsentListener;
import com.langwuyue.orange.redis.listener.OrangeRedisSetIfAbsentListener;
import com.langwuyue.orange.redis.listener.hash.OrangeRedisHashAddMemberIfAbsentListener;
import com.langwuyue.orange.redis.listener.hash.OrangeRedisHashAddMembersIfAbsentListener;
import com.langwuyue.orange.redis.mapping.OrangeRedisExecutorsMapping;
import com.langwuyue.orange.redis.mapping.OrangeRedisHashExecutorIdGenerator;
import com.langwuyue.orange.redis.mapping.OrangeRedisHashExecutorsMapping;
import com.langwuyue.orange.redis.operations.OrangeRedisDefaultHashOperations;
import com.langwuyue.orange.redis.operations.OrangeRedisDefaultScriptOperations;
import com.langwuyue.orange.redis.operations.OrangeRedisHashOperations;
import com.langwuyue.orange.redis.operations.OrangeRedisScriptOperations;

/**
 * Factory bean for creating Redis Hash operation clients.
 * 
 * <p>This factory bean creates and configures clients for Redis Hash operations.
 * It handles the creation of dynamic proxies that implement the client interface
 * annotated with {@link OrangeRedisHashClient}. The factory manages the lifecycle
 * of Redis Hash clients and provides all necessary components for their operation.
 * 
 * <p>Key responsibilities:
 * <ul>
 *   <li>Creating and configuring Redis Hash operation executors</li>
 *   <li>Setting up invocation handlers for method interception</li>
 *   <li>Managing listeners for Hash operations</li>
 *   <li>Configuring circuit breakers for fault tolerance</li>
 *   <li>Handling serialization and deserialization of Hash values</li>
 * </ul>
 * 
 * <p>This factory supports various Hash operations including:
 * <ul>
 *   <li>Single field operations (HSET, HGET, HDEL)</li>
 *   <li>Multiple field operations (HMSET, HMGET)</li>
 *   <li>Scanning operations (HSCAN)</li>
 *   <li>Atomic operations with Lua scripts</li>
 * </ul>
 * 
 * @author Liang.Zhong
 * @since 1.0.0
 * @see OrangeRedisHashClient
 * @see OrangeRedisClientAbstractFactoryBean
 * @see OrangeRedisHashOperations
 */
public class OrangeRedisHashClientFactoryBean extends OrangeRedisClientAbstractFactoryBean {
	
	/**
	 * The Hash client annotation instance.
	 * 
	 * <p>This field stores the annotation metadata from {@link OrangeRedisHashClient}
	 * that configures this client's behavior, including circuit breaker settings,
	 * hash key type, hash value type, and other operation configurations.
	 * 
	 * <p>The annotation is retrieved from the client definition class and provides
	 * all the necessary configuration parameters for creating and configuring
	 * the Redis Hash client proxy.
	 * 
	 * <!-- Chinese documentation -->
	 * Hash客户端注解实例。
	 * 
	 * <p>此字段存储来自{@link OrangeRedisHashClient}的注解元数据，
	 * 用于配置此客户端的行为，包括断路器设置、哈希键类型、哈希值类型和其他操作配置。
	 * 
	 * <p>该注解从客户端定义类中获取，并提供创建和配置Redis Hash客户端代理所需的所有配置参数。
	 */
	private OrangeRedisHashClient client;
	
	/**
	 * Static cache for Hash operations executors mapping.
	 * 
	 * <p>This mapping is shared across all instances of the factory bean to avoid
	 * redundant creation of executors for the same operations. It is lazily
	 * initialized when first accessed through {@link #getExecutorsMapping()}.
	 * 
	 * <p>The executors mapping contains all the necessary components for executing
	 * Redis Hash operations, including the operations implementation, executor ID generator,
	 * listeners, and script operations.
	 * 
	 * <!-- Chinese documentation -->
	 * Hash操作执行器映射的静态缓存。
	 * 
	 * <p>此映射在工厂Bean的所有实例间共享，以避免为相同操作重复创建执行器。
	 * 它在首次通过{@link #getExecutorsMapping()}访问时被懒加载初始化。
	 * 
	 * <p>执行器映射包含执行Redis Hash操作所需的所有组件，包括操作实现、
	 * 执行器ID生成器、监听器和脚本操作。
	 */
	private static OrangeRedisHashExecutorsMapping EXECUTORS_MAPPING;
	
	/**
	 * Constructs a new OrangeRedisHashClientFactoryBean with the specified parameters.
	 * 
	 * <p>This constructor initializes the factory bean with the necessary components to create
	 * and configure Redis Hash operation proxies. It delegates to the parent class constructor
	 * for common initialization tasks such as setting up the operation owner, Redis configuration,
	 * and client definition class.
	 * 
	 * <p>The factory bean uses these parameters to:
	 * <ul>
	 *   <li>Identify the interface defining Redis Hash operations</li>
	 *   <li>Extract configuration from annotations on the client definition class</li>
	 *   <li>Access Redis connection and serialization settings</li>
	 *   <li>Create appropriate proxies and handlers</li>
	 * </ul>
	 *
	 * @param operationOwner The class that owns the Redis Hash operations to be proxied.
	 *                       This is typically an interface defining the Hash operations.
	 * @param clientDefinitionClass The class that contains the {@link OrangeRedisHashClient} annotation,
	 *                             which provides configuration details for the client.
	 * @param configuration The Orange Redis configuration that contains global settings
	 *                     such as connection details, serializers, and other Redis-related properties.
	 *
	 * <!-- Chinese documentation -->
	 * 使用指定参数构造一个新的OrangeRedisHashClientFactoryBean。
	 * 
	 * <p>此构造函数使用必要的组件初始化工厂Bean，以创建和配置Redis Hash操作代理。
	 * 它将通用初始化任务（如设置操作所有者、Redis配置和客户端定义类）委托给父类构造函数。
	 * 
	 * <p>工厂Bean使用这些参数来：
	 * <ul>
	 *   <li>识别定义Redis Hash操作的接口</li>
	 *   <li>从客户端定义类的注解中提取配置</li>
	 *   <li>访问Redis连接和序列化设置</li>
	 *   <li>创建适当的代理和处理器</li>
	 * </ul>
	 *
	 * @param operationOwner 拥有要代理的Redis Hash操作的类。这通常是定义Hash操作的接口。
	 * @param clientDefinitionClass 包含{@link OrangeRedisHashClient}注解的类，该注解为客户端提供配置详情。
	 * @param configuration Orange Redis配置，包含全局设置，如连接详情、序列化器和其他Redis相关属性。
	 */
	public OrangeRedisHashClientFactoryBean(
			Class<?> operationOwner,
			Class<?> clientDefinitionClass,
			OrangeRedisConfiguration configuration
	) {
		super(operationOwner, configuration,clientDefinitionClass);
	}
	
	/**
	 * Creates and returns the executors mapping for Redis Hash operations.
	 * 
	 * <p>This method implements a singleton pattern for the executors mapping,
	 * ensuring that only one instance is created and reused across all instances
	 * of this factory bean. The mapping contains all the necessary components
	 * for executing Redis Hash operations.
	 * 
	 * <p>The method creates the following components:
	 * <ul>
	 *   <li>Hash operations implementation for basic Hash commands</li>
	 *   <li>Script operations for executing Lua scripts</li>
	 *   <li>Executor ID generator for creating unique operation identifiers</li>
	 *   <li>Listeners for operation events</li>
	 * </ul>
	 * 
	 * <p>The executors mapping acts as a registry of operation executors and
	 * provides the infrastructure for executing Redis Hash commands with proper
	 * serialization, error handling, and event notification.
	 * 
	 * @return the configured executors mapping for Redis Hash operations
	 * @see OrangeRedisHashExecutorsMapping
	 * @see OrangeRedisDefaultHashOperations
	 * @see OrangeRedisDefaultScriptOperations
	 */
	@Override
	protected OrangeRedisExecutorsMapping getExecutorsMapping() {
		if(EXECUTORS_MAPPING != null) {
			return EXECUTORS_MAPPING;
		}
		OrangeRedisHashOperations operations = new OrangeRedisDefaultHashOperations(
			this.getRedisTemplate(), 
			getRedisSerializer(),
			getLogger()
		);
		OrangeRedisScriptOperations scriptOperations = new OrangeRedisDefaultScriptOperations(
			this.getRedisTemplate(), 
			getRedisSerializer(),
			getLogger()
		);
		EXECUTORS_MAPPING = new OrangeRedisHashExecutorsMapping(
			operations, 
			new OrangeRedisHashExecutorIdGenerator(),
			getListeners(),
			scriptOperations,
			getMultipleListener(),
			getLogger()
		);
		return EXECUTORS_MAPPING;
	}
	
	/**
	 * Creates and returns the invocation handler for Redis Hash operations.
	 * 
	 * <p>This method creates a specialized {@link OrangeRedisHashClientInvocationHandler}
	 * that intercepts method calls on the client interface. The handler is responsible for:
	 * <ul>
	 *   <li>Intercepting method invocations on the client interface</li>
	 *   <li>Extracting operation metadata from annotations</li>
	 *   <li>Processing method arguments using the handler mapping</li>
	 *   <li>Delegating to appropriate Redis Hash operations</li>
	 *   <li>Converting and returning results</li>
	 *   <li>Handling exceptions and error conditions</li>
	 * </ul>
	 * 
	 * <p>The handler is configured with:
	 * <ul>
	 *   <li>The operation owner class for context</li>
	 *   <li>The mapping for operation execution</li>
	 *   <li>The Redis key for operations</li>
	 *   <li>The argument handler mapping for parameter processing</li>
	 *   <li>The value type for serialization</li>
	 *   <li>The hash key type for field serialization</li>
	 *   <li>The circuit breaker for fault tolerance</li>
	 *   <li>Redis properties for configuration</li>
	 * </ul>
	 * 
	 * @return the configured invocation handler for Redis Hash operations
	 * @see OrangeRedisHashClientInvocationHandler
	 * @see InvocationHandler
	 */
	@Override
	protected InvocationHandler getInvocationHandler() {
		return new OrangeRedisHashClientInvocationHandler(
			this.getOperationOwner(),
			this.getMapping(),
			this.getRedisKey(),
			this.getOperationArgHandlerMapping(),
			this.getValueType(),
			this.client.hashKeyType(),
			this.getCircuitBreaker(),
			this.getProperties()
		);
	}
	
	/**
	 * Retrieves and configures multiple-value listeners for Hash operations.
	 * 
	 * <p>This method collects all {@link OrangeRedisHashAddMembersIfAbsentListener} beans
	 * from the application context and creates proxies that implement the
	 * {@link OrangeRedisMultipleSetIfAbsentListener} interface. These listeners are
	 * triggered when multiple hash fields are added in a single operation.
	 * 
	 * <p>The method performs the following steps:
	 * <ul>
	 *   <li>Retrieves all beans of type {@link OrangeRedisHashAddMembersIfAbsentListener}</li>
	 *   <li>Creates proxy wrappers that adapt them to the {@link OrangeRedisMultipleSetIfAbsentListener} interface</li>
	 *   <li>Configures them with the Redis key prefix from properties</li>
	 *   <li>Sorts them according to Spring's {@link AnnotationAwareOrderComparator}</li>
	 * </ul>
	 * 
	 * <p>If no listeners are found, an empty collection is returned.
	 * 
	 * @return a collection of configured multiple-value listeners
	 * @see OrangeRedisHashAddMembersIfAbsentListener
	 * @see OrangeRedisMultipleSetIfAbsentListener
	 * @see OrangeIfAbsentListenerProxy
	 */
	@Override
	protected Collection<OrangeRedisMultipleSetIfAbsentListener> getMultipleListener() {
		Map<String, OrangeRedisHashAddMembersIfAbsentListener> beanMap = this.getApplicationContext().getBeansOfType(OrangeRedisHashAddMembersIfAbsentListener.class);
		if(beanMap == null || beanMap.isEmpty()) {
			return new ArrayList<>();
		}
		List<OrangeRedisMultipleSetIfAbsentListener> listeners = OrangeIfAbsentListenerProxy.proxy(beanMap.values(), OrangeRedisMultipleSetIfAbsentListener.class,this.getProperties().getKeyPrefix());
		AnnotationAwareOrderComparator.sort(listeners);
		return listeners;
	}

	/**
	 * Retrieves and configures single-value listeners for Hash operations.
	 * 
	 * <p>This method collects all {@link OrangeRedisHashAddMemberIfAbsentListener} beans
	 * from the application context and creates proxies that implement the
	 * {@link OrangeRedisSetIfAbsentListener} interface. These listeners are
	 * triggered when a single hash field is set with an if-absent condition.
	 * 
	 * <p>The method performs the following steps:
	 * <ul>
	 *   <li>Retrieves all beans of type {@link OrangeRedisHashAddMemberIfAbsentListener}</li>
	 *   <li>Creates proxy wrappers that adapt them to the {@link OrangeRedisSetIfAbsentListener} interface</li>
	 *   <li>Configures them with the Redis key prefix from properties</li>
	 *   <li>Sorts them according to Spring's {@link AnnotationAwareOrderComparator}</li>
	 * </ul>
	 * 
	 * <p>If no listeners are found, an empty collection is returned.
	 * 
	 * @return a collection of configured single-value listeners
	 * @see OrangeRedisHashAddMemberIfAbsentListener
	 * @see OrangeRedisSetIfAbsentListener
	 * @see OrangeIfAbsentListenerProxy
	 */
	@Override
	protected Collection<OrangeRedisSetIfAbsentListener> getListeners() {
		Map<String, OrangeRedisHashAddMemberIfAbsentListener> beanMap = this.getApplicationContext().getBeansOfType(OrangeRedisHashAddMemberIfAbsentListener.class);
		if(beanMap == null || beanMap.isEmpty()) {
			return new ArrayList<>();
		}
		List<OrangeRedisSetIfAbsentListener> listeners = OrangeIfAbsentListenerProxy.proxy(beanMap.values(), OrangeRedisSetIfAbsentListener.class,this.getProperties().getKeyPrefix());
		AnnotationAwareOrderComparator.sort(listeners);
		return listeners;
	}

	/**
	 * Returns the value type enumeration for Redis Hash field values.
	 * 
	 * <p>This method retrieves the value type configuration from the {@link OrangeRedisHashClient}
	 * annotation. The value type determines how hash field values are serialized and
	 * deserialized when interacting with Redis.
	 * 
	 * <p>The value type is used to:
	 * <ul>
	 *   <li>Select appropriate serialization strategy</li>
	 *   <li>Convert Java objects to Redis data types</li>
	 *   <li>Convert Redis data back to Java objects</li>
	 *   <li>Ensure type safety in operations</li>
	 * </ul>
	 * 
	 * @return the configured {@link RedisValueTypeEnum} for hash field values
	 * @see RedisValueTypeEnum
	 * @see OrangeRedisHashClient#hashValueType()
	 */
	@Override
	protected RedisValueTypeEnum getValueType() {
		return client.hashValueType();
	}
	
	/**
	 * Returns the circuit breaker class for Redis Hash operations.
	 * 
	 * <p>This method determines the appropriate circuit breaker implementation to use
	 * based on the {@link OrangeRedisHashClient} annotation configuration. It supports
	 * multiple ways to specify the circuit breaker:
	 * 
	 * <ul>
	 *   <li>Direct class reference through {@link OrangeRedisHashClient#breaker()}</li>
	 *   <li>Class name as a string through {@link OrangeRedisHashClient#breakerClassName()}</li>
	 *   <li>Default implementation when neither is specified</li>
	 * </ul>
	 * 
	 * <p>The method follows this resolution process:
	 * <ol>
	 *   <li>Retrieves the {@link OrangeRedisHashClient} annotation from the client interface</li>
	 *   <li>Checks if a non-default breaker class is directly specified</li>
	 *   <li>If using default and a breaker class name is provided, attempts to load that class</li>
	 *   <li>Falls back to the default implementation if class loading fails</li>
	 * </ol>
	 * 
	 * <p>The circuit breaker provides fault tolerance mechanisms including:
	 * <ul>
	 *   <li>Failure detection and monitoring</li>
	 *   <li>Circuit breaking to prevent cascading failures</li>
	 *   <li>Fallback strategies during outages</li>
	 *   <li>Recovery and reset policies</li>
	 * </ul>
	 * 
	 * @return the resolved circuit breaker class
	 * @see OrangeRedisCircuitBreaker
	 * @see OrangeRedisDefaultCircuitBreaker
	 * @see OrangeRedisHashClient#breaker()
	 * @see OrangeRedisHashClient#breakerClassName()
	 */
	@Override
	protected Class<? extends OrangeRedisCircuitBreaker> getCircuitBreakerClass(){
		this.client = this.getClientDefinitionClass().getAnnotation(OrangeRedisHashClient.class);
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