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

import com.langwuyue.orange.redis.RedisValueTypeEnum;

/**
 * Interface for Redis string/value operations providing methods to manipulate string values in Redis.
 * Supports operations like get, set, increment, and conditional set operations.
 * 
 * @author Liang.Zhong
 * @since 1.0.0
 */
public interface OrangeRedisValueOperations extends OrangeRedisOperations {

	/**
	 * Increments the number stored at key by the given value.
	 * Equivalent to Redis INCRBY command.
	 *
	 * @param key the key
	 * @param value the increment value
	 * @return the value after the increment
	 */
	Long increment(String key, long value);

	/**
	 * Increments the floating point number stored at key by the given value.
	 * Equivalent to Redis INCRBYFLOAT command.
	 *
	 * @param key the key
	 * @param value the increment value
	 * @return the value after the increment
	 */
	Double increment(String key, double value);

	/**
	 * Increments the number stored at key by one.
	 * Equivalent to Redis INCR command.
	 *
	 * @param key the key
	 * @return the value after the increment
	 */
	Long increment(String key);

	/**
	 * Sets the value and expiration of a key.
	 * Equivalent to Redis SET command with EX/PX option.
	 *
	 * @param key the key
	 * @param value the value
	 * @param expirationTime the expiration time
	 * @param expirationTimeUnit the time unit of the expiration time
	 * @param valueType the type of the value
	 * @throws Exception if serialization fails
	 */
	void set(String key, Object value, long expirationTime, TimeUnit expirationTimeUnit, RedisValueTypeEnum valueType) throws Exception;
	
	/**
	 * Sets the value of a key without expiration.
	 * Equivalent to Redis SET command.
	 *
	 * @param key the key
	 * @param value the value
	 * @param valueType the type of the value
	 * @throws Exception if serialization fails
	 */
	void set(String key, Object value, RedisValueTypeEnum valueType) throws Exception;

	/**
	 * Sets the value and expiration of a key, only if the key does not exist.
	 * Equivalent to Redis SET command with NX and EX/PX options.
	 *
	 * @param key the key
	 * @param value the value
	 * @param expirationTime the expiration time
	 * @param expirationTimeUnit the time unit of the expiration time
	 * @param valueType the type of the value
	 * @return true if the key was set, false if the key already exists
	 * @throws Exception if serialization fails
	 */
	Boolean setIfAbsent(String key, Object value, long expirationTime, TimeUnit expirationTimeUnit, RedisValueTypeEnum valueType) throws Exception;
	
	/**
	 * Sets the value of a key, only if the key does not exist.
	 * Equivalent to Redis SET command with NX option.
	 *
	 * @param key the key
	 * @param value the value
	 * @param valueType the type of the value
	 * @return true if the key was set, false if the key already exists
	 * @throws Exception if serialization fails
	 */
	Boolean setIfAbsent(String key, Object value, RedisValueTypeEnum valueType) throws Exception;

	/**
	 * Gets the value of a key.
	 * Equivalent to Redis GET command.
	 *
	 * @param key the key
	 * @param valueType the type of the value
	 * @param genericReturnType the expected return type
	 * @return the value of the key, or null if the key does not exist
	 * @throws Exception if deserialization fails
	 */
	Object get(String key, RedisValueTypeEnum valueType, Type genericReturnType) throws Exception;

}