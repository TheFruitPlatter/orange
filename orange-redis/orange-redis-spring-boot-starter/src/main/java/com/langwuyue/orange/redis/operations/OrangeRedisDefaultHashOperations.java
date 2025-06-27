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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;

import com.langwuyue.orange.redis.RedisValueTypeEnum;
import com.langwuyue.orange.redis.configuration.OrangeRedisSerializer;
import com.langwuyue.orange.redis.logger.OrangeRedisLogger;

/**
 * Default implementation of {@link OrangeRedisHashOperations} for Redis hash operations.
 * 
 * <p>This class provides thread-safe implementations of all Redis hash operations,
 * including CRUD operations, atomic increments, and bulk operations. It handles
 * serialization/deserialization of keys and values transparently using the configured
 * {@link OrangeRedisSerializer}.
 * 
 * <p>All operations are logged at debug level when debug logging is enabled.
 * 
 * @author Liang.Zhong
 * @since 1.0.0
 * @see OrangeRedisHashOperations
 * @see HashOperations
 * @see OrangeRedisSerializer
 */
public class OrangeRedisDefaultHashOperations extends OrangeRedisAbstractOperations implements OrangeRedisHashOperations{

	/**
	 * The Spring Data Redis HashOperations instance used to perform the actual Redis operations.
	 * This is the core component that executes the Redis hash commands.
	 */
	private HashOperations<String,byte[],byte[]> operations;
	
	/**
	 * The serializer used to convert Java objects to byte arrays and vice versa.
	 * This enables storing complex Java objects in Redis hashes.
	 */
	private OrangeRedisSerializer redisSerializer;
	
	/**
	 * Logger for recording operation details and debugging information.
	 * Used to log method calls, parameters, and results when debug logging is enabled.
	 */
	private OrangeRedisLogger logger;
	
	/**
	 * Constructs a new OrangeRedisDefaultHashOperations instance with the required dependencies.
	 * 
	 * <p>This constructor initializes the hash operations by obtaining a HashOperations instance
	 * from the provided RedisTemplate. It also initializes the serializer and logger for use
	 * throughout the class.
	 *
	 * @param template The Spring Data Redis template that provides Redis operations
	 * @param redisSerializer The serializer to convert between Java objects and byte arrays
	 * @param logger The logger for recording operation details and debugging information
	 */
	public OrangeRedisDefaultHashOperations(RedisTemplate<String,byte[]> template,OrangeRedisSerializer redisSerializer,OrangeRedisLogger logger) {
		super(template,logger);
		this.operations = template.opsForHash();
		this.redisSerializer = redisSerializer;
		this.logger = logger;
	}

	/**
	 * Puts a single hash entry into a Redis hash stored at the specified key.
	 * 
	 * <p>This method serializes both the hash key and value according to their specified types
	 * before storing them in the hash. If the hash does not exist, it is created first.
	 * If the hash key already exists, its value is overwritten.
	 * 
	 * <p>The method supports different value types through the RedisValueTypeEnum parameter,
	 * allowing for flexible serialization of both keys and values.
	 *
	 * @param key The key of the Redis hash
	 * @param hashKey The key of the hash entry to be added
	 * @param hashValue The value to be associated with the hash key
	 * @param hashKeyType The type of the hash key, used for serialization
	 * @param hashValueType The type of the hash value, used for serialization
	 * @throws Exception If serialization fails or Redis operation encounters an error
	 * @see HashOperations#put(Object, Object, Object)
	 */
	@Override
	public void putMember(
		String key, 
		Object hashKey, 
		Object hashValue, 
		RedisValueTypeEnum hashKeyType,
		RedisValueTypeEnum hashValueType
	) throws Exception {
		byte[] hashKeyBytes = redisSerializer.serialize(hashKey, hashKeyType);
		byte[] hashValueBytes = redisSerializer.serialize(hashValue, hashValueType);
		if(this.logger.isDebugEnabled()) {
			this.logger.debug("Rdis hash 'putMember' operation executing: putMember(key:{},hashKey:{},hashValue:{})", key, new String(hashKeyBytes), new String(hashValueBytes));
		}
		this.operations.put(key, hashKeyBytes, hashValueBytes);
		if(logger.isDebugEnabled()) {
			logger.debug("Redis hash 'putMember' operation execute successfully.");
		}
	}

	/**
	 * Puts multiple hash entries into a Redis hash stored at the specified key.
	 * 
	 * <p>This method serializes all hash keys and values according to their specified types
	 * before storing them in the hash. If the hash does not exist, it is created first.
	 * If any hash key already exists, its value is overwritten.
	 * 
	 * <p>This is a batch operation that allows for efficient addition of multiple entries
	 * in a single Redis command, which is more efficient than adding entries one by one.
	 *
	 * @param key The key of the Redis hash
	 * @param members A map containing the hash keys and values to be added
	 * @param hashKeyType The type of the hash keys, used for serialization
	 * @param hashValueType The type of the hash values, used for serialization
	 * @throws Exception If serialization fails or Redis operation encounters an error
	 * @see HashOperations#putAll(Object, Map)
	 */
	@Override
	public void putMembers(
		String key, 
		Map members,
		RedisValueTypeEnum hashKeyType,
		RedisValueTypeEnum hashValueType
	) throws Exception {
		Map<byte[], byte[]> byteMemebers = new HashMap<>();
		Set<Entry> entries = members.entrySet();
		for(Entry entry : entries) {
			Object hashKey = entry.getKey();
			Object hashValue = entry.getValue();
			byte[] hashKeyBytes = redisSerializer.serialize(hashKey, hashKeyType);
			byte[] hashValueBytes = redisSerializer.serialize(hashValue, hashValueType);
			byteMemebers.put(hashKeyBytes, hashValueBytes);
		}
		if(this.logger.isDebugEnabled()) {
			this.logger.debug("Rdis hash 'putMembers' operation executing: putMembers(key:{},members:{})", key, redisSerializer.serializeToJSONString(members));
		}
		this.operations.putAll(key, byteMemebers);
		if(logger.isDebugEnabled()) {
			logger.debug("Redis hash 'putMembers' operation execute successfully.");
		}
	}

	/**
	 * Increments the value of a hash field by the given amount.
	 * 
	 * <p>This method serializes the hash key according to the specified type
	 * before performing the increment operation. If the field does not exist,
	 * it is set to 0 before performing the operation. If the key does not exist,
	 * a new hash is created.
	 * 
	 * <p>The field value must be a number that can be represented as a double precision
	 * floating point number. The increment can be negative to perform a decrement operation.
	 *
	 * @param key The key of the hash
	 * @param hashKey The field whose value should be incremented
	 * @param delta The increment value (can be negative for decrement)
	 * @param hashKeyType The type of the hash key, used for serialization
	 * @return The new value of the field after the increment operation
	 * @throws Exception If serialization fails, the field contains a non-numeric value,
	 *                   or Redis operation encounters an error
	 * @see HashOperations#increment(Object, Object, double)
	 */
	@Override
	public Double increment(String key, Object hashKey, double delta, RedisValueTypeEnum hashKeyType) throws Exception {
		byte[] hashKeyBytes = redisSerializer.serialize(hashKey, hashKeyType);
		if(this.logger.isDebugEnabled()) {
			this.logger.debug("Rdis hash 'increment' operation executing: increment(key:{},hashKey:{},delta:{})", key, new String(hashKeyBytes), delta);
		}
		Double results = this.operations.increment(key, hashKeyBytes, delta);
		if(logger.isDebugEnabled()) {
			logger.debug("Redis hash 'increment' operation returned {}", results);
		}
		return results;
	}

	/**
	 * Increments the value of a hash field by the given long value.
	 * 
	 * <p>This method serializes the hash key according to the specified type
	 * before performing the increment operation. If the field does not exist,
	 * it is set to 0 before performing the operation. If the key does not exist,
	 * a new hash is created.
	 * 
	 * <p>The field value must be a number that can be represented as a long integer.
	 * The increment can be negative to perform a decrement operation. This method is
	 * more efficient than the double version when working with integer values.
	 *
	 * @param key The key of the hash
	 * @param hashKey The field whose value should be incremented
	 * @param delta The increment value (can be negative for decrement)
	 * @param hashKeyType The type of the hash key, used for serialization
	 * @return The new value of the field after the increment operation
	 * @throws Exception If serialization fails, the field contains a non-numeric value,
	 *                   or Redis operation encounters an error
	 * @see HashOperations#increment(Object, Object, long)
	 */
	@Override
	public Long increment(String key, Object hashKey, long delta, RedisValueTypeEnum hashKeyType) throws Exception {
		byte[] hashKeyBytes = redisSerializer.serialize(hashKey, hashKeyType);
		if(this.logger.isDebugEnabled()) {
			this.logger.debug("Rdis hash 'increment' operation executing: increment(key:{},hashKey:{},delta:{})", key, new String(hashKeyBytes), delta);
		}
		Long results = this.operations.increment(key, hashKeyBytes, delta);
		if(logger.isDebugEnabled()) {
			logger.debug("Redis hash 'increment' operation returned {}", results);
		}
		return results;
	}

	/**
	 * Retrieves all field names (keys) from a Redis hash stored at the specified key.
	 * 
	 * <p>This method retrieves all field names from the hash and deserializes them
	 * according to the specified hash key type. The deserialized objects are then
	 * returned as a Set of the specified return type.
	 * 
	 * <p>If the key does not exist, an empty set is returned. The method handles
	 * serialization and type conversion automatically based on the provided parameters.
	 *
	 * @param key The key of the Redis hash
	 * @param hashKeyType The type of the hash keys, used for deserialization
	 * @param returnType The Java type to which the hash keys should be deserialized
	 * @return A Set containing all field names in the hash, deserialized to the specified type
	 * @throws Exception If deserialization fails or Redis operation encounters an error
	 * @see HashOperations#keys(Object)
	 */
	@Override
	public Set<Object> keys(String key,RedisValueTypeEnum hashKeyType, Type returnType) throws Exception {
		if(this.logger.isDebugEnabled()) {
			this.logger.debug("Rdis hash 'keys' operation executing: keys(key:{})", key);
		}
		Set<byte[]> keys = this.operations.keys(key);
		Set<Object> results = redisSerializer.deserialize(keys, hashKeyType, returnType);
		if(logger.isDebugEnabled()) {
			logger.debug("Redis hash 'keys' operation returned {}", redisSerializer.serializeToJSONString(results));
		}
		return results;
	}

	/**
	 * Retrieves multiple values from a Redis hash in a single operation.
	 * 
	 * <p>This method serializes the provided hash keys according to the specified type,
	 * retrieves their corresponding values from the Redis hash, and then deserializes
	 * those values according to the specified value type and return type.
	 * 
	 * <p>This batch operation is more efficient than retrieving values one by one,
	 * especially when dealing with a large number of fields.
	 *
	 * @param key The key of the Redis hash
	 * @param hashKeys A list of hash keys whose values should be retrieved
	 * @param hashKeyType The type of the hash keys, used for serialization
	 * @param hashValueType The type of the hash values, used for deserialization
	 * @param returnType The Java type to which the hash values should be deserialized
	 * @return A List containing the values associated with the specified hash keys
	 * @throws Exception If serialization/deserialization fails or Redis operation encounters an error
	 * @see HashOperations#multiGet(Object, Collection)
	 */
	@Override
	public List<Object> multiGet(
		String key, 
		List<Object> hashKeys, 
		RedisValueTypeEnum hashKeyType, 
		RedisValueTypeEnum hashValueType, 
		Type returnType
	) throws Exception {
		if(this.logger.isDebugEnabled()) {
			this.logger.debug("Rdis hash 'multiGet' operation executing: multiGet(key:{},hashKeys:{})", key, redisSerializer.serializeToJSONString(hashKeys));
		}
		List<byte[]> bytes = redisSerializer.serialize(hashKeys, hashKeyType);
		List<byte[]> result = this.operations.multiGet(key, bytes);
		List<Object> results = redisSerializer.deserialize(result, hashValueType, returnType);
		if(logger.isDebugEnabled()) {
			logger.debug("Redis hash 'multiGet' operation returned {}", redisSerializer.serializeToJSONString(results));
		}
		return results;
	}

	/**
	 * Retrieves a single value from a Redis hash.
	 * 
	 * <p>This method serializes the provided hash key according to the specified type,
	 * retrieves its corresponding value from the Redis hash, and then deserializes
	 * that value according to the specified value type and return type.
	 * 
	 * <p>If the hash key does not exist in the hash, null is returned.
	 *
	 * @param key The key of the Redis hash
	 * @param hashKey The hash key whose value should be retrieved
	 * @param hashKeyType The type of the hash key, used for serialization
	 * @param hashValueType The type of the hash value, used for deserialization
	 * @param returnType The Java type to which the hash value should be deserialized
	 * @return The value associated with the specified hash key, or null if the hash key does not exist
	 * @throws Exception If serialization/deserialization fails or Redis operation encounters an error
	 * @see HashOperations#get(Object, Object)
	 */
	@Override
	public Object get(
		String key, 
		Object hashKey, 
		RedisValueTypeEnum hashKeyType, 
		RedisValueTypeEnum hashValueType, 
		Type returnType
	) throws Exception {
		byte[] hashKeyBytes = redisSerializer.serialize(hashKey, hashKeyType);
		if(this.logger.isDebugEnabled()) {
			this.logger.debug("Rdis hash 'get' operation executing: get(key:{},hashKey:{})", key, new String(hashKeyBytes));
		}
		byte[] hashValueBytes = this.operations.get(key, hashKeyBytes);
		Object results = redisSerializer.deserialize(hashValueBytes, hashValueType, returnType);
		if(logger.isDebugEnabled()) {
			logger.debug("Redis hash 'get' operation returned {}", new String(hashKeyBytes));
		}
		return results;
	}

	/**
	 * Retrieves all entries (key-value pairs) from a Redis hash.
	 * 
	 * <p>This method retrieves all entries from the Redis hash and deserializes both
	 * the keys and values according to their specified types. The deserialized objects
	 * are then returned as a Map with keys and values of the specified return types.
	 * 
	 * <p>If the key does not exist, an empty map is returned. This method is useful
	 * when you need to get a complete view of all data stored in the hash.
	 *
	 * @param key The key of the Redis hash
	 * @param hashKeyType The type of the hash keys, used for deserialization
	 * @param hashValueType The type of the hash values, used for deserialization
	 * @param keyType The Java type to which the hash keys should be deserialized
	 * @param valueType The Java type to which the hash values should be deserialized
	 * @return A Map containing all entries in the hash, with keys and values deserialized to the specified types
	 * @throws Exception If deserialization fails or Redis operation encounters an error
	 * @see HashOperations#entries(Object)
	 */
	@Override
	public Map<Object, Object> entries(
		String key,
		RedisValueTypeEnum hashKeyType,
		RedisValueTypeEnum hashValueType,
		Type keyType,
		Type valueType
	) throws Exception {
		if(this.logger.isDebugEnabled()) {
			this.logger.debug("Rdis hash 'entries' operation executing: entries(key:{})", key);
		}
		Map<byte[],byte[]> resultMap = this.operations.entries(key);
		Map<Object, Object> results = redisSerializer.deserialize(resultMap, hashKeyType, hashValueType, keyType, valueType);
		if(logger.isDebugEnabled()) {
			logger.debug("Redis hash 'entries' operation returned {}", redisSerializer.serializeToJSONString(results));
		}
		return results;
	}

	/**
	 * Returns the number of fields (entries) in the hash stored at the specified key.
	 * 
	 * <p>This method provides a way to determine the size of a Redis hash without
	 * retrieving all of its contents. It's an efficient operation that runs in O(1) time.
	 * 
	 * <p>If the key does not exist, 0 is returned.
	 *
	 * @param key The key of the Redis hash
	 * @return The number of fields in the hash, or 0 if the key does not exist
	 * @see HashOperations#size(Object)
	 */
	@Override
	public Long size(String key) {
		if(this.logger.isDebugEnabled()) {
			this.logger.debug("Rdis hash 'size' operation executing: size(key:{})", key);
		}
		Long results = this.operations.size(key);
		if(logger.isDebugEnabled()) {
			logger.debug("Redis hash 'size' operation returned {}", results);
		}
		return results;
	}

	/**
	 * Determines if the specified field exists in the hash stored at the given key.
	 * 
	 * <p>This method serializes the provided hash key according to the specified type
	 * before checking for its existence in the Redis hash.
	 * 
	 * <p>If the key does not exist, false is returned.
	 *
	 * @param key The key of the Redis hash
	 * @param hashKey The field to check for existence
	 * @param hashKeyType The type of the hash key, used for serialization
	 * @return true if the field exists in the hash, false otherwise
	 * @throws Exception If serialization fails or Redis operation encounters an error
	 * @see HashOperations#hasKey(Object, Object)
	 */
	@Override
	public Boolean hasKey(String key, Object hashKey, RedisValueTypeEnum hashKeyType) throws Exception {
		byte[] hashKeyBytes = redisSerializer.serialize(hashKey, hashKeyType);
		if(this.logger.isDebugEnabled()) {
			this.logger.debug("Rdis hash 'hasKey' operation executing: hasKey(key:{},hashKey:{})", key, new String(hashKeyBytes));
		}
		Boolean results = this.operations.hasKey(key, hashKeyBytes);
		if(logger.isDebugEnabled()) {
			logger.debug("Redis hash 'hasKey' operation returned {}", results);
		}
		return results;
	}

	/**
	 * Retrieves a random sample of keys from a Redis hash.
	 * 
	 * <p>This method returns a specified number of random field names (keys) from
	 * the hash stored at the given key. The returned keys are deserialized according
	 * to the specified hash value type.
	 * 
	 * <p>If count is positive, the command returns an array of distinct fields.
	 * If count is negative, the command may return the same field multiple times.
	 *
	 * @param key The key of the Redis hash
	 * @param count The number of random keys to return
	 * @param hashValueType The type of the hash values, used for deserialization
	 * @param valueType The Java type to which the hash values should be deserialized
	 * @return A List containing the randomly selected keys, deserialized to the specified type
	 * @throws Exception If deserialization fails or Redis operation encounters an error
	 * @see HashOperations#randomKeys(Object, long)
	 */
	@Override
	public List<Object> randomKeys(String key, long count, RedisValueTypeEnum hashValueType, Type valueType) throws Exception {
		if(this.logger.isDebugEnabled()) {
			this.logger.debug("Rdis hash 'randomKeys' operation executing: randomKeys(key:{},count:{})", key, count);
		}
		List<byte[]> result = this.operations.randomKeys(key, count);
		List<Object> results = redisSerializer.deserialize(result, hashValueType, valueType);
		if(logger.isDebugEnabled()) {
			logger.debug("Redis hash 'randomKeys' operation returned {}", redisSerializer.serializeToJSONString(results));
		}
		return results;
	}

	/**
	 * Retrieves a random sample of entries (key-value pairs) from a Redis hash.
	 * 
	 * <p>This method returns a specified number of random entries from the hash
	 * stored at the given key. Both keys and values are deserialized according
	 * to their specified types.
	 * 
	 * <p>If count is positive, the command returns an array of distinct field-value pairs.
	 * If count is negative, the command may return the same field multiple times.
	 *
	 * @param key The key of the Redis hash
	 * @param count The number of random entries to return
	 * @param hashKeyType The type of the hash keys, used for deserialization
	 * @param hashValueType The type of the hash values, used for deserialization
	 * @param keyType The Java type to which the hash keys should be deserialized
	 * @param valueType The Java type to which the hash values should be deserialized
	 * @return A Map containing the randomly selected entries, with keys and values deserialized to the specified types
	 * @throws Exception If deserialization fails or Redis operation encounters an error
	 * @see HashOperations#randomEntries(Object, long)
	 */
	@Override
	public Map<Object, Object> randomEntries(
			String key, 
			long count, 
			RedisValueTypeEnum hashKeyType,
			RedisValueTypeEnum hashValueType,
			Type keyType,
			Type valueType
	) throws Exception {
		if(this.logger.isDebugEnabled()) {
			this.logger.debug("Rdis hash 'randomEntries' operation executing: randomEntries(key:{},count:{})", key, count);
		}
		Map<byte[],byte[]> resultMap = this.operations.randomEntries(key, count);
		Map<Object, Object> results = redisSerializer.deserialize(resultMap, hashKeyType, hashValueType, keyType, valueType);
		if(logger.isDebugEnabled()) {
			logger.debug("Redis hash 'randomEntries' operation returned {}", redisSerializer.serializeToJSONString(results));
		}
		return results;
	}

	/**
	 * Removes one or more fields from the hash stored at the specified key.
	 * 
	 * <p>This method serializes the provided hash keys according to the specified type
	 * before removing them from the Redis hash.
	 * 
	 * <p>If the key does not exist, it is treated as an empty hash and this command returns 0.
	 *
	 * @param key The key of the Redis hash
	 * @param hashKeyType The type of the hash keys, used for serialization
	 * @param hashKeys The fields to be removed from the hash
	 * @return The number of fields that were removed from the hash, not including non-existing fields
	 * @throws Exception If serialization fails or Redis operation encounters an error
	 * @see HashOperations#delete(Object, Object...)
	 */
	@Override
	public Long removeMembers(String key, RedisValueTypeEnum hashKeyType, Object... hashKeys) throws Exception {
		if(this.logger.isDebugEnabled()) {
			this.logger.debug("Rdis hash 'removeMembers' operation executing: removeMembers(key:{},hashKeys:{})", key, redisSerializer.serializeToJSONString(hashKeys));
		}
		Object[] byteHashKeys = new Object[hashKeys.length];
		for(int i = 0; i < hashKeys.length; i++) {
			byteHashKeys[i] = redisSerializer.serialize(hashKeys[i], hashKeyType);
		}
		Long results = this.operations.delete(key, byteHashKeys);
		if(logger.isDebugEnabled()) {
			logger.debug("Redis hash 'removeMembers' operation returned {}", results);
		}
		return results;
	}

	/**
	 * Sets the hash field to the specified value, only if the field does not exist.
	 * 
	 * <p>This method serializes both the hash key and value according to their specified types
	 * before attempting to store them in the hash. If the hash does not exist, it is created first.
	 * If the hash field already exists, the operation does nothing and returns false.
	 * 
	 * <p>This method is useful for implementing atomic operations where you want to ensure
	 * that a field is only set if it doesn't already have a value.
	 *
	 * @param key The key of the Redis hash
	 * @param hashKey The key of the hash entry to be added
	 * @param hashValue The value to be associated with the hash key
	 * @param hashKeyType The type of the hash key, used for serialization
	 * @param hashValueType The type of the hash value, used for serialization
	 * @return true if the field was set, false if the field already exists
	 * @throws Exception If serialization fails or Redis operation encounters an error
	 * @see HashOperations#putIfAbsent(Object, Object, Object)
	 */
	@Override
	public Boolean putIfAbsent(
		String key, 
		Object hashKey, 
		Object hashValue,
		RedisValueTypeEnum hashKeyType,
		RedisValueTypeEnum hashValueType
	) throws Exception {
		byte[] hashKeyBytes = redisSerializer.serialize(hashKey, hashKeyType);
		byte[] hashValueBytes = redisSerializer.serialize(hashValue, hashValueType);
		if(this.logger.isDebugEnabled()) {
			this.logger.debug("Rdis hash 'putIfAbsent' operation executing: putIfAbsent(key:{},hashKey:{},hashValue:{})", key, new String(hashKeyBytes), new String(hashValueBytes));
		}
		Boolean results = this.operations.putIfAbsent(key, hashKeyBytes, hashValueBytes);
		if(logger.isDebugEnabled()) {
			logger.debug("Redis hash 'putIfAbsent' operation returned {}", results);
		}
		return results;
	}

	/**
	 * Returns the length of the value associated with a hash field.
	 * 
	 * <p>This method serializes the hash key according to the specified type
	 * before retrieving the length of its associated value in the Redis hash.
	 * 
	 * <p>If the key or field does not exist, null is returned. The length is
	 * measured in bytes for string values.
	 *
	 * @param key The key of the Redis hash
	 * @param hashKey The field whose value length should be retrieved
	 * @param hashKeyType The type of the hash key, used for serialization
	 * @return The length of the value associated with the field, or null if the key or field does not exist
	 * @throws Exception If serialization fails or Redis operation encounters an error
	 * @see HashOperations#lengthOfValue(Object, Object)
	 */
	@Override
	public Long getLengthByHashKey(String key, Object hashKey, RedisValueTypeEnum hashKeyType) throws Exception {
		byte[] hashKeyBytes = redisSerializer.serialize(hashKey, hashKeyType);
		if(this.logger.isDebugEnabled()) {
			this.logger.debug("Rdis hash 'putMember' operation executing: putMember(key:{},hashKey:{})", key, new String(hashKeyBytes));
		}
		Long results = this.operations.lengthOfValue(key, hashKeyBytes);
		if(logger.isDebugEnabled()) {
			logger.debug("Redis hash 'putMember' operation returned {}", results);
		}
		return results;
	}

	/**
	 * Returns all values in the hash stored at the specified key.
	 * 
	 * <p>This method retrieves all values from the Redis hash and deserializes them
	 * according to the specified value type and return type. The values are returned
	 * in no particular order.
	 * 
	 * <p>If the key does not exist, an empty list is returned. This method is useful
	 * when you need to process all values in a hash without knowing their keys.
	 *
	 * @param key The key of the Redis hash
	 * @param valueType The type of the values stored in the hash
	 * @param returnType The Java type to deserialize the values into
	 * @return A list containing all values in the hash, or an empty list if the key does not exist
	 * @throws Exception If deserialization fails or Redis operation encounters an error
	 * @see HashOperations#values(Object)
	 */
	@Override
	public List<Object> getAllValues(String key, RedisValueTypeEnum valueType, Type returnType) throws Exception {
		if(this.logger.isDebugEnabled()) {
			this.logger.debug("Rdis hash 'getAllValues' operation executing: getAllValues(key:{})", key);
		}
		List<byte[]> values = this.operations.values(key);
		List<Object> results = this.redisSerializer.deserialize(values, valueType, returnType);
		if(logger.isDebugEnabled()) {
			logger.debug("Redis hash 'getAllValues' operation returned {}", redisSerializer.serializeToJSONString(results));
		}
		return results;
	}
	
}