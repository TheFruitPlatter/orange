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
package com.langwuyue.orange.redis.operations;

import java.lang.reflect.Type;
import java.util.concurrent.TimeUnit;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import com.langwuyue.orange.redis.RedisValueTypeEnum;
import com.langwuyue.orange.redis.configuration.OrangeRedisSerializer;
import com.langwuyue.orange.redis.logger.OrangeRedisLogger;

/**
 * Default implementation of {@link OrangeRedisValueOperations} for Redis value operations.
 * 
 * <p>This class provides the implementation for basic Redis value operations, including:
 * <ul>
 *   <li>Get and set operations with optional expiration</li>
 *   <li>Atomic increment operations for both integer and floating-point values</li>
 *   <li>Conditional set operations (setIfAbsent/setNX)</li>
 * </ul>
 * 
 * <p>All operations in this class support serialization and deserialization of values
 * using the configured {@link OrangeRedisSerializer}. The actual Redis operations are
 * delegated to Spring's {@link ValueOperations}.
 *
 * @author Liang.Zhong
 * @since 1.0.0
 * @see OrangeRedisValueOperations
 * @see OrangeRedisAbstractOperations
 * @see ValueOperations
 */
public class OrangeRedisDefaultValueOperations extends OrangeRedisAbstractOperations implements OrangeRedisValueOperations {

	/**
	 * The underlying Redis value operations template.
	 * Provides direct access to Redis value operations with String keys and byte[] values.
	 * Used for all basic Redis value operations like get, set, increment, etc.
	 */
	private ValueOperations<String, byte[]> operations;
	
	/**
	 * Serializer for converting between Java objects and Redis byte arrays.
	 * Handles serialization of values before storage and deserialization when retrieving.
	 * Supports various value types as defined in {@link RedisValueTypeEnum}.
	 */
	private OrangeRedisSerializer redisSerializer;
	
	/**
	 * Logger for recording operation details and debugging information.
	 * Logs method calls, parameters and results at debug level.
	 * Helps with troubleshooting Redis operations.
	 */
	private OrangeRedisLogger logger;
	
	/**
	 * Constructs a new OrangeRedisDefaultValueOperations instance.
	 * 
	 * <p>Initializes the value operations with the provided Redis template, serializer, and logger.
	 * The template is used for actual Redis operations, the serializer handles conversion of objects
	 * to and from byte arrays, and the logger is used for debug logging of operations.
	 *
	 * @param template The Redis template to use for operations, configured to use String keys and byte[] values
	 * @param redisSerializer The serializer to use for converting objects to/from byte arrays
	 * @param logger The logger instance for debug logging
	 */
	public OrangeRedisDefaultValueOperations(RedisTemplate<String,byte[]> template,OrangeRedisSerializer redisSerializer,OrangeRedisLogger logger) {
		super(template,logger);
		this.operations = template.opsForValue();
		this.redisSerializer = redisSerializer;
		this.logger = logger;
	}

	/**
	 * Retrieves the value associated with the specified key from Redis.
	 * 
	 * <p>This method gets the value stored at the given key and deserializes it according
	 * to the specified value type and return type. If the key does not exist, returns null.
	 * The deserialization process uses the configured {@link OrangeRedisSerializer}.
	 *
	 * @param key The key whose value is to be retrieved
	 * @param valueType The type enum indicating how the value should be deserialized
	 * @param genericReturnType The expected generic return type for deserialization
	 * @return The deserialized value associated with the key, or null if the key doesn't exist
	 * @throws Exception If deserialization fails or Redis operation encounters an error
	 * @see ValueOperations#get(Object)
	 */
	@Override
	public Object get(String key, RedisValueTypeEnum valueType, Type genericReturnType) throws Exception{
		if(logger.isDebugEnabled()) {
			logger.debug("Redis value 'get' operation executing: get(key:{})", key);
		}
		byte[] value = this.operations.get(key);
		if(value == null) {
			if(logger.isDebugEnabled()) {
				logger.debug("Redis value 'get' operation returned null");
			}
			return null;
		}
		Object obj = redisSerializer.deserialize(value, valueType, genericReturnType);
		if(logger.isDebugEnabled()) {
			logger.debug("Redis value 'get' operation returned {}", new String(value));
		}
		return obj;
	}

	/**
	 * Increments the value stored at the specified key by a given amount.
	 * 
	 * <p>This method atomically increments the number stored at the given key by the
	 * specified value. If the key does not exist, it is initialized as 0 before
	 * performing the operation. The value must be a valid long integer.
	 *
	 * @param key The key whose value should be incremented
	 * @param value The value to increment by
	 * @return The value after the increment operation
	 * @see ValueOperations#increment(Object, long)
	 */
	@Override
	public Long increment(String key, long value) {
		if(logger.isDebugEnabled()) {
			logger.debug("Redis value 'increment' operation executing: increment(key:{},value:{})", key,value);
		}
		Long result = this.operations.increment(key,value);
		if(logger.isDebugEnabled()) {
			logger.debug("Redis value 'increment' operation returned {}", result);
		}
		return result;
	}

	/**
	 * Increments the value stored at the specified key by a given double value.
	 * 
	 * <p>This method atomically increments the floating point number stored at the given key
	 * by the specified value. If the key does not exist, it is initialized as 0 before
	 * performing the operation. The value must be a valid double precision floating point number.
	 *
	 * @param key The key whose value should be incremented
	 * @param value The double value to increment by
	 * @return The value after the increment operation
	 * @see ValueOperations#increment(Object, double)
	 */
	@Override
	public Double increment(String key, double value) {
		if(logger.isDebugEnabled()) {
			logger.debug("Redis value 'increment' operation executing: increment(key:{},value:{})", key,value);
		}
		Double result = this.operations.increment(key, value);
		if(logger.isDebugEnabled()) {
			logger.debug("Redis value 'increment' operation returned {}", result);
		}
		return result;
	}

	/**
	 * Increments the value stored at the specified key by 1.
	 * 
	 * <p>This method atomically increments the number stored at the given key by 1.
	 * If the key does not exist, it is initialized as 0 before performing the operation.
	 *
	 * @param key The key whose value should be incremented
	 * @return The value after the increment operation
	 * @see ValueOperations#increment(Object)
	 */
	@Override
	public Long increment(String key) {
		if(logger.isDebugEnabled()) {
			logger.debug("Redis value 'increment' operation executing: increment(key:{})", key);
		}
		Long result =  this.operations.increment(key);
		if(logger.isDebugEnabled()) {
			logger.debug("Redis value 'increment' operation returned {}", result);
		}
		return result;
	}

	/**
	 * Sets the value for a specific key in Redis with an expiration time.
	 * 
	 * <p>This method stores the serialized value at the specified key with a given expiration time.
	 * The value is serialized according to the specified value type using the configured
	 * {@link OrangeRedisSerializer}.
	 *
	 * @param key The key at which to store the value
	 * @param value The value to store
	 * @param expirationTime The amount of time after which the key will expire
	 * @param expirationTimeUnit The time unit for the expiration time
	 * @param valueType The type enum indicating how the value should be serialized
	 * @throws Exception If serialization fails or Redis operation encounters an error
	 * @see ValueOperations#set(Object, Object, long, TimeUnit)
	 */
	@Override
	public void set(
		String key, 
		Object value, 
		long expirationTime, 
		TimeUnit expirationTimeUnit, 
		RedisValueTypeEnum valueType
	) throws Exception{
		byte[] bytes = redisSerializer.serialize(value, valueType);
		if(logger.isDebugEnabled()) {
			logger.debug(
				"Redis value 'set' operation executing: set(key:{},value:{},expire:{}ms)",
				key,
				new String(bytes),
				TimeUnit.MINUTES.convert(expirationTime, expirationTimeUnit)
			);
		}
		this.operations.set(key, bytes, expirationTime, expirationTimeUnit);
		if(logger.isDebugEnabled()) {
			logger.debug("Redis value set successfully");
		}
	}

	/**
	 * Sets the value for a specific key in Redis only if the key does not already exist, with an expiration time.
	 * 
	 * <p>This method is equivalent to the Redis SETNX (SET if Not eXists) command with an expiration time.
	 * It will set the key with the serialized value only if the key does not already exist in Redis.
	 * The value is serialized according to the specified value type using the configured
	 * {@link OrangeRedisSerializer}.
	 *
	 * @param key The key at which to store the value
	 * @param value The value to store
	 * @param expirationTime The amount of time after which the key will expire
	 * @param expirationTimeUnit The time unit for the expiration time
	 * @param valueType The type enum indicating how the value should be serialized
	 * @return Boolean indicating whether the operation was successful (true if the key was set, false if the key already existed)
	 * @throws Exception If serialization fails or Redis operation encounters an error
	 * @see ValueOperations#setIfAbsent(Object, Object, long, TimeUnit)
	 */
	@Override
	public Boolean setIfAbsent(
		String key, 
		Object value, 
		long expirationTime, 
		TimeUnit expirationTimeUnit, 
		RedisValueTypeEnum valueType
	) throws Exception{
		byte[] bytes = redisSerializer.serialize(value, valueType);
		if(logger.isDebugEnabled()) {
			logger.debug(
				"Redis value 'setIfAbsent' operation executing: setIfAbsent(key:{},value:{},expire:{}ms)", 
				key,
				new String(bytes),
				TimeUnit.MINUTES.convert(expirationTime, expirationTimeUnit)
			);
		}
		Boolean result = this.operations.setIfAbsent(key, bytes, expirationTime, expirationTimeUnit);
		if(logger.isDebugEnabled()) {
			logger.debug("Redis value 'setIfAbsent' operation returned {}", result);
		}
		return result;
	}

	/**
	 * Sets the value for a specific key in Redis without an expiration time.
	 * 
	 * <p>This method stores the serialized value at the specified key without setting an expiration time.
	 * The value is serialized according to the specified value type using the configured
	 * {@link OrangeRedisSerializer}. The key will persist until explicitly deleted.
	 *
	 * @param key The key at which to store the value
	 * @param value The value to store
	 * @param valueType The type enum indicating how the value should be serialized
	 * @throws Exception If serialization fails or Redis operation encounters an error
	 * @see ValueOperations#set(Object, Object)
	 */
	@Override
	public void set(String key, Object value, RedisValueTypeEnum valueType) throws Exception {
		byte[] bytes = redisSerializer.serialize(value, valueType);
		if(logger.isDebugEnabled()) {
			logger.debug("Redis value 'set' operation executing: set(key:{},value:{})", key,new String(bytes));
		}
		this.operations.set(key, bytes);
		if(logger.isDebugEnabled()) {
			logger.debug("Redis value set successfully");
		}
	}

	/**
	 * Sets the value for a specific key in Redis only if the key does not already exist, without an expiration time.
	 * 
	 * <p>This method is equivalent to the Redis SETNX (SET if Not eXists) command without an expiration time.
	 * It will set the key with the serialized value only if the key does not already exist in Redis.
	 * The value is serialized according to the specified value type using the configured
	 * {@link OrangeRedisSerializer}.
	 *
	 * @param key The key at which to store the value
	 * @param value The value to store
	 * @param valueType The type enum indicating how the value should be serialized
	 * @return Boolean indicating whether the operation was successful (true if the key was set, false if the key already existed)
	 * @throws Exception If serialization fails or Redis operation encounters an error
	 * @see ValueOperations#setIfAbsent(Object, Object)
	 */
	@Override
	public Boolean setIfAbsent(String key, Object value, RedisValueTypeEnum valueType) throws Exception {
		byte[] bytes = redisSerializer.serialize(value, valueType);
		if(logger.isDebugEnabled()) {
			logger.debug("Redis value 'setIfAbsent' operation executing: setIfAbsent(key:{},value:{})", key,new String(bytes));
		}
		Boolean result = this.operations.setIfAbsent(key, bytes);
		if(logger.isDebugEnabled()) {
			logger.debug("Redis value 'setIfAbsent' operation returned {}", result);
		}
		return result;
	}
}