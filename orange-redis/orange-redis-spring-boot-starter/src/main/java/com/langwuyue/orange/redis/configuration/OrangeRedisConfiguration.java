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

import java.lang.annotation.Annotation;
import java.text.SimpleDateFormat;
import java.time.ZoneId;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TimeZone;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import org.springframework.beans.factory.FactoryBean;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.langwuyue.orange.redis.client.OrangeClientFactoryProvider;
import com.langwuyue.orange.redis.context.builder.OrangeOperationArgHandler;

/**
 * Configuration class for Orange Redis client that manages Redis client annotations, factory mappings,
 * operation argument handlers, and JSON serialization settings.
 * 
 * <p>This class serves as the central configuration point for the Orange Redis framework, providing:
 * <ul>
 *   <li>Registration and mapping between Redis client annotations and their corresponding factory beans</li>
 *   <li>Management of operation argument handlers that process method parameters</li>
 *   <li>Configuration of JSON serialization/deserialization through Jackson's ObjectMapper</li>
 * </ul>
 * 
 * <p>The configuration maintains two primary mappings:
 * <ol>
 *   <li>A mapping between annotation classes and factory bean classes that create Redis client implementations</li>
 *   <li>A registry of operation argument handlers that process method parameters during Redis operations</li>
 * </ol>
 * 
 * <p>By default, it initializes an ObjectMapper with appropriate settings for handling dates, time zones,
 * and JSON serialization preferences. This ObjectMapper can be customized or replaced through the
 * {@link #config(ObjectMapper)} method.
 *
 * @author Liang.Zhong
 * @since 1.0.0
 * @see OrangeClientFactoryProvider
 * @see OrangeOperationArgHandler
 */
public class OrangeRedisConfiguration implements OrangeClientFactoryProvider {
	
	/**
	 * Thread-safe map storing operation argument handlers.
	 * Keys are handler classes and values are their corresponding instances.
	 * These handlers are responsible for processing method parameters during Redis operations.
	 */
	private static final Map<Class<? extends OrangeOperationArgHandler>, OrangeOperationArgHandler> VALUE_HANDLERS = new ConcurrentHashMap<>();

	/**
	 * Thread-safe map storing the mapping between Redis client annotations and their factory classes.
	 * Keys are annotation classes that mark Redis client interfaces, and values are the factory classes
	 * responsible for creating the corresponding Redis client implementations.
	 */
	private static final Map<Class<? extends Annotation>, Class<? extends FactoryBean>> EXECUTOR_FACTORY_MAPPING = new ConcurrentHashMap<>();
	
	/**
	 * ObjectMapper instance used for JSON serialization and deserialization.
	 * This mapper is configured with specific settings for handling dates, time zones,
	 * and other JSON formatting preferences.
	 */
	private ObjectMapper objectMapper;
	
	/**
	 * Constructs a new OrangeRedisConfiguration with default ObjectMapper settings.
	 * 
	 * <p>Initializes the configuration with a default ObjectMapper that has appropriate
	 * settings for handling dates, time zones, and JSON serialization preferences.
	 * 
	 * @see #getDefaultObjectMapper()
	 */
	public OrangeRedisConfiguration() {
		this.objectMapper = getDefaultObjectMapper();
	}
	
	/**
	 * Registers a mapping between a Redis client annotation class and its corresponding factory class.
	 * 
	 * <p>This method establishes the relationship between Redis client interface annotations
	 * and the factory beans responsible for creating their implementations. When the framework
	 * encounters an interface annotated with {@code clientClass}, it will use the specified
	 * {@code factoryClass} to create an implementation.
	 *
	 * @param clientClass the annotation class that marks Redis client interfaces
	 * @param factoryClass the factory bean class that creates implementations for the annotated interfaces
	 */
	public void config(Class<? extends Annotation> clientClass,Class<? extends FactoryBean> factoryClass) {
		EXECUTOR_FACTORY_MAPPING.put(clientClass, factoryClass);
	}
	
	/**
	 * Retrieves all registered Redis client annotation classes.
	 * 
	 * <p>Returns a set of all annotation classes that have been registered with
	 * corresponding factory beans through the {@link #config(Class, Class)} method.
	 * These annotations are used to mark interfaces as Redis clients.
	 *
	 * @return a set of registered Redis client annotation classes
	 */
	public Set<Class<? extends Annotation>> getRedisClientAnnotationClasses() {
		return EXECUTOR_FACTORY_MAPPING.keySet().stream().collect(Collectors.toSet());
	}
	
	/**
	 * Retrieves the factory bean class associated with a given Redis client annotation class.
	 * 
	 * <p>For a given Redis client annotation class, returns the factory bean class that was
	 * registered to create implementations of interfaces marked with that annotation.
	 *
	 * @param clientClass the Redis client annotation class
	 * @return the factory bean class registered for the given annotation class, or null if none is registered
	 */
	@Override
	public Class<?> getFactoryClass(Class<? extends Annotation> clientClass) {
		return EXECUTOR_FACTORY_MAPPING.get(clientClass);
	}
	
	/**
	 * Registers an operation argument handler.
	 * 
	 * <p>This method adds a handler that processes method parameters during Redis operations.
	 * The handler is registered with its class as the key in the handler registry.
	 *
	 * @param handler the operation argument handler to register
	 */
	public void config(OrangeOperationArgHandler handler) {
		VALUE_HANDLERS.put(handler.getClass(), handler);
	}
	
	/**
	 * Retrieves a copy of all registered operation argument handlers.
	 * 
	 * <p>Returns a new map containing all registered operation argument handlers.
	 * The returned map is a copy of the internal registry, so modifications to it
	 * will not affect the registered handlers.
	 *
	 * @return a map of handler classes to handler instances
	 */
	public Map<Class<? extends OrangeOperationArgHandler>, OrangeOperationArgHandler> getValueHandlerMap(){
		Map<Class<? extends OrangeOperationArgHandler>, OrangeOperationArgHandler> map = new HashMap<>();
		for(Entry<Class<? extends OrangeOperationArgHandler>, OrangeOperationArgHandler> entry : VALUE_HANDLERS.entrySet()) {
			map.put(entry.getKey(), entry.getValue());
		}
		return map;
	}
	
	/**
	 * Configures the ObjectMapper instance used for JSON serialization/deserialization.
	 * 
	 * <p>This method allows customization of the ObjectMapper used throughout the Redis client
	 * operations. The provided ObjectMapper will be used for all JSON conversions in Redis
	 * operations.
	 *
	 * @param objectMapper the custom ObjectMapper to use for JSON operations
	 */
	public void config(ObjectMapper objectMapper) {
		this.objectMapper = objectMapper;
	}
	
	/**
	 * Retrieves the configured ObjectMapper instance.
	 * 
	 * <p>Returns the ObjectMapper that is currently configured for JSON serialization
	 * and deserialization operations.
	 *
	 * @return the configured ObjectMapper instance
	 */
	public ObjectMapper getObjectMapper() {
		return this.objectMapper;
	}
	
	/**
	 * Creates and returns a default ObjectMapper with appropriate settings for Redis operations.
	 * 
	 * <p>The default ObjectMapper is configured with the following settings:
	 * <ul>
	 *   <li>Date format pattern: "yyyy-MM-dd HH:mm:ss"</li>
	 *   <li>System default time zone</li>
	 *   <li>Support for Java 8 date/time types via JavaTimeModule</li>
	 *   <li>Dates represented as strings rather than timestamps</li>
	 *   <li>Null values excluded from serialization</li>
	 *   <li>Unknown properties ignored during deserialization</li>
	 *   <li>No indentation in output JSON</li>
	 *   <li>Map entries ordered by keys</li>
	 * </ul>
	 *
	 * @return a newly configured ObjectMapper instance
	 */
	private ObjectMapper getDefaultObjectMapper() {
		ObjectMapper objectMapper = new ObjectMapper();
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        objectMapper.setDateFormat(dateFormat);
        objectMapper.setTimeZone(TimeZone.getTimeZone(ZoneId.systemDefault()));
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        objectMapper.disable(SerializationFeature.INDENT_OUTPUT);
        objectMapper.configure(SerializationFeature.ORDER_MAP_ENTRIES_BY_KEYS,true);
        return objectMapper;
	}
}