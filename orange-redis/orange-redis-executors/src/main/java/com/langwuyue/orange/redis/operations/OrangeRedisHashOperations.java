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
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.langwuyue.orange.redis.RedisValueTypeEnum;

/**
 * Interface for Redis Hash operations, providing various operations on Redis hash data structure.
 * 
 * <p>This interface extends {@link OrangeRedisOperations} and specializes in handling hash type data in Redis.
 * It provides a complete hash operation API including adding, getting, and deleting hash members.
 * 
 * <p>All methods support specifying serialization types for keys and values via {@link RedisValueTypeEnum},
 * and specifying concrete return types via {@link Type} parameter.
 * 
 * @author Liang.Zhong
 * @since 1.0.0
 */
public interface OrangeRedisHashOperations extends OrangeRedisOperations {

	/**
	 * Adds a single member to a hash.
	 * 
	 * <p>If the hash doesn't exist, it will be created automatically. If the field already exists, 
	 * the old value will be overwritten.
	 * 
	 * @param key Redis key
	 * @param hashKey Hash field key
	 * @param hashValue Hash field value
	 * @param hashKeyType Serialization type of hash field key
	 * @param hashValueType Serialization type of hash field value
	 * @throws Exception If Redis operation fails or serialization/deserialization error occurs
	 */
	void putMember(
		String key, 
		Object hashKey, 
		Object hashValue, 
		RedisValueTypeEnum hashKeyType,
		RedisValueTypeEnum hashValueType
	) throws Exception;

	/**
	 * Adds multiple members to a hash in batch.
	 * 
	 * <p>If the hash doesn't exist, it will be created automatically. If fields already exist, 
	 * their old values will be overwritten.
	 * 
	 * @param key Redis key
	 * @param members Map of hash members to add (field names as keys, field values as values)
	 * @param hashKeyType Serialization type of hash field keys
	 * @param hashValueType Serialization type of hash field values
	 * @throws Exception If Redis operation fails or serialization/deserialization error occurs
	 */
	void putMembers(
		String key, 
		Map members, 
		RedisValueTypeEnum hashKeyType,
		RedisValueTypeEnum hashValueType
	) throws Exception;

	/**
	 * Increments the floating-point value of a hash field by the given amount.
	 * 
	 * <p>If the hash or field doesn't exist, it will first set the field value to 0 
	 * before performing the increment operation.
	 * 
	 * @param key Redis key
	 * @param hashKey Hash field key
	 * @param delta Increment value (can be negative)
	 * @param hashKeyType Serialization type of hash field key
	 * @return The value after increment
	 * @throws Exception If Redis operation fails or serialization/deserialization error occurs
	 */
	Double increment(
		String key, 
		Object hashKey, 
		double delta, 
		RedisValueTypeEnum hashKeyType
	) throws Exception;

	        /**
	         * Increments the integer value of a hash field by the given amount.
	         * 
	         * <p>If the hash or field doesn't exist, it will first set the field value to 0 
	         * before performing the increment operation.
	         * 
	         * @param key Redis key
	         * @param hashKey Hash field key
	         * @param delta Increment value (can be negative)
	         * @param hashKeyType Serialization type of hash field key
	         * @return The value after increment
	         * @throws Exception If Redis operation fails or serialization/deserialization error occurs
	         */
	        Long increment(
	                String key, 
	                Object hashKey, 
	                long delta, 
	                RedisValueTypeEnum hashKeyType
	        ) throws Exception;

	        /**
	         * Gets all field keys in a hash.
	         * 
	         * @param key Redis key
	         * @param hashKeyType Serialization type of hash field keys
	         * @param returnType Type of return values
	         * @return Set containing all field keys, empty set if hash doesn't exist
	         * @throws Exception If Redis operation fails or serialization/deserialization error occurs
	         */
	        Set<Object> keys(
	                String key, 
	                RedisValueTypeEnum hashKeyType, 
	                Type returnType
	        ) throws Exception;

	        /**
	         * Gets values of multiple hash fields in batch.
	         * 
	         * @param key Redis key
	         * @param hashKeys List of hash field keys to get
	         * @param hashKeyType Serialization type of hash field keys
	         * @param hashValueType Serialization type of hash field values
	         * @param returnType Type of return values
	         * @return List containing all field values in same order as input hashKeys, null for non-existent fields
	         * @throws Exception If Redis operation fails or serialization/deserialization error occurs
	         */
	        List<Object> multiGet(
	                String key, 
	                List<Object> hashKeys, 
	                RedisValueTypeEnum hashKeyType,
	                RedisValueTypeEnum hashValueType, 
	                Type returnType
	        ) throws Exception;

	        /**
	         * Gets the value of a single hash field.
	         * 
	         * @param key Redis key
	         * @param hashKey Hash field key
	         * @param hashKeyType Serialization type of hash field key
	         * @param hashValueType Serialization type of hash field value
	         * @param returnType Type of return value
	         * @return Field value, or null if field doesn't exist
	         * @throws Exception If Redis operation fails or serialization/deserialization error occurs
	         */
	        Object get(
	                String key, 
	                Object hashKey, 
	                RedisValueTypeEnum hashKeyType,
	                RedisValueTypeEnum hashValueType,
	                Type returnType
	        ) throws Exception;

	        /**
	         * Gets all field key-value pairs in a hash.
	         * 
	         * @param key Redis key
	         * @param hashKeyType Serialization type of hash field keys
	         * @param hashValueType Serialization type of hash field values
	         * @param keyType Type of returned keys
	         * @param valueType Type of returned values
	         * @return Map containing all field key-value pairs, empty Map if hash doesn't exist
	         * @throws Exception If Redis operation fails or serialization/deserialization error occurs
	         */
	        Map<Object, Object> entries(
	                String key, 
	                RedisValueTypeEnum hashKeyType,
	                RedisValueTypeEnum hashValueType, 
	                Type keyType, 
	                Type valueType
	        ) throws Exception;

	        /**
	         * Checks if a specified field exists in a hash.
	         * 
	         * @param key Redis key
	         * @param hashKey Hash field key to check
	         * @param hashKeyType Serialization type of hash field key
	         * @return true if field exists, false otherwise
	         * @throws Exception If Redis operation fails or serialization/deserialization error occurs
	         */
	        Boolean hasKey(
	                String key, 
	                Object hashKey, 
	                RedisValueTypeEnum hashKeyType
	        ) throws Exception;

	        /**
	         * Gets multiple random field key-value pairs from a hash.
	         * 
	         * @param key Redis key
	         * @param count Number of key-value pairs to get
	         * @param hashKeyType Serialization type of hash field keys
	         * @param hashValueType Serialization type of hash field values
	         * @param keyType Type of returned keys
	         * @param valueType Type of returned values
	         * @return Map containing random field key-value pairs, empty Map if hash doesn't exist
	         * @throws Exception If Redis operation fails or serialization/deserialization error occurs
	         */
	        Map<Object, Object> randomEntries(
	                String key, 
	                long count, 
	                RedisValueTypeEnum hashKeyType,
	                RedisValueTypeEnum hashValueType, 
	                Type keyType, 
	                Type valueType
	        ) throws Exception;

	        /**
	         * Gets multiple random field keys from a hash.
	         * 
	         * @param key Redis key
	         * @param count Number of field keys to get
	         * @param hashValueType Serialization type of hash field values
	         * @param valueType Type of return values
	         * @return List containing random field keys, empty list if hash doesn't exist
	         * @throws Exception If Redis operation fails or serialization/deserialization error occurs
	         */
	        List<Object> randomKeys(
	                String key, 
	                long count, 
	                RedisValueTypeEnum hashValueType, 
	                Type valueType
	        )throws Exception;

	        /**
	         * Gets the number of fields in a hash.
	         * 
	         * @param key Redis key
	         * @return Number of fields in the hash, 0 if hash doesn't exist
	         */
	        Long size(String key);

	        /**
	         * Removes multiple fields from a hash.
	         * 
	         * @param key Redis key
	         * @param hashKeyType Serialization type of hash field keys
	         * @param hashKeys Hash field keys to remove
	         * @return Number of fields actually removed
	         * @throws Exception If Redis operation fails or serialization/deserialization error occurs
	         */
	        Long removeMembers(
	                String key, 
	                RedisValueTypeEnum hashKeyType, 
	                Object... hashKeys
	        ) throws Exception;

	        /**
	         * Adds a field to a hash only if the field does not already exist.
	         * 
	         * @param key Redis key
	         * @param hashKey Hash field key
	         * @param hashValue Hash field value
	         * @param hashKeyType Serialization type of hash field key
	         * @param hashValueType Serialization type of hash field value
	         * @return true if field didn't exist and was successfully added, false if field already existed
	         * @throws Exception If Redis operation fails or serialization/deserialization error occurs
	         */
	        Boolean putIfAbsent(
	                String key, 
	                Object hashKey, 
	                Object hashValue, 
	                RedisValueTypeEnum hashKeyType,
	                RedisValueTypeEnum hashValueType
	        ) throws Exception;

	/**
	 * Gets the length of a hash field value.
	 * 
	 * @param key Redis key
	 * @param hashKey Hash field key
	 * @param hashKeyType Serialization type of the hash field key
	 * @return Length of the field value, or 0 if the field does not exist
	 * @throws Exception If Redis operation fails or serialization/deserialization error occurs
	 */
	Long getLengthByHashKey(
		String key, 
		Object hashKey, 
		RedisValueTypeEnum hashKeyType
	) throws Exception;

	/**
	 * Gets all field values in a hash.
	 * 
	 * @param key Redis key
	 * @param valueType Serialization type of hash field values
	 * @param returnType Type of return values
	 * @return List containing all field values, empty list if hash doesn't exist
	 * @throws Exception If Redis operation fails or serialization/deserialization error occurs
	 */
	List<Object> getAllValues(String key, RedisValueTypeEnum valueType, Type returnType) throws Exception;

}