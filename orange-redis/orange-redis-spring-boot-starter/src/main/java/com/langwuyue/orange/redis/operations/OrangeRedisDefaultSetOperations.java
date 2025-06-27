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
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.data.redis.core.SetOperations;

import com.langwuyue.orange.redis.RedisValueTypeEnum;
import com.langwuyue.orange.redis.configuration.OrangeRedisSerializer;
import com.langwuyue.orange.redis.logger.OrangeRedisLogger;

/**
 * Default implementation of {@link OrangeRedisSetOperations} that provides Redis set operations.
 * 
 * <p>This class implements all Redis set operations with support for:
 * <ul>
 *   <li>Custom serialization of set members</li>
 *   <li>Type-safe deserialization of set members</li>
 *   <li>Detailed logging of all operations</li>
 *   <li>Support for all standard Redis set operations (add, remove, union, intersection, etc.)</li>
 * </ul>
 *
 * @author Liang.Zhong
 * @since 1.0.0
 * @see OrangeRedisSetOperations
 */
public class OrangeRedisDefaultSetOperations extends OrangeRedisAbstractOperations implements OrangeRedisSetOperations{

	/**
	 * The underlying Redis set operations template.
	 * Provides direct access to Redis set operations with String keys and byte[] values.
	 */
	private SetOperations<String, byte[]> operations;
	
	/**
	 * Serializer for converting between Java objects and Redis byte arrays.
	 * Handles serialization of set members before storage and deserialization when retrieving.
	 */
	private OrangeRedisSerializer redisSerializer;
	
	/**
	 * Logger for recording operation details and debugging information.
	 * Logs method calls, parameters and results at debug level.
	 */
	private OrangeRedisLogger logger;
	
	/**
	 * Constructs a new OrangeRedisDefaultSetOperations instance.
	 * 
	 * <p>Initializes the set operations with the provided Redis template, serializer, and logger.
	 * The template is used for Redis operations, the serializer handles conversion between Java
	 * objects and Redis byte arrays, and the logger records operation details.
	 *
	 * @param template The Redis template to use for set operations
	 * @param redisSerializer The serializer to use for converting between Java objects and Redis byte arrays
	 * @param logger The logger to use for recording operation details
	 */
	public OrangeRedisDefaultSetOperations(RedisTemplate<String,byte[]> template,OrangeRedisSerializer redisSerializer,OrangeRedisLogger logger) {
		super(template,logger);
		this.operations = template.opsForSet();
		this.redisSerializer = redisSerializer;
		this.logger = logger;
	}

	/**
	 * Adds one or more members to a set stored at the specified key.
	 * 
	 * <p>This method adds the specified values to the set stored at the key. If the key does
	 * not exist, a new set is created before adding the values. If a value already exists
	 * in the set, it is ignored. The values are serialized according to the specified value
	 * type before being added to the set.
	 *
	 * @param key The key of the set
	 * @param valueType The type of the values to be added, used for serialization
	 * @param values One or more values to add to the set
	 * @return The number of elements that were added to the set, not including elements
	 *         already present
	 * @throws Exception If serialization fails or Redis operation encounters an error
	 * @see SetOperations#add(Object, Object...)
	 */
	@Override
	public Long add(String key, RedisValueTypeEnum valueType, Object... values) throws Exception {
		if(logger.isDebugEnabled()) {
			logger.debug("Redis set 'add' operation executing: add(key:{},values:{})", key, redisSerializer.serializeToJSONString(values));
		}
		Long results = this.operations.add(key, redisSerializer.serialize(values,valueType));
		if(logger.isDebugEnabled()) {
			logger.debug("Redis set 'add' operation returned {}", results);
		}
		return results;
	}

	/**
	 * Returns all members of the set stored at the specified key.
	 * 
	 * <p>This method retrieves all members of the set stored at the key. If the key does not
	 * exist, an empty set is returned. The retrieved values are deserialized according to
	 * the specified value type and return type.
	 *
	 * @param key The key of the set
	 * @param valueType The type of the values stored in the set, used for deserialization
	 * @param returnType The expected return type for deserialization
	 * @return A set of deserialized objects representing all members of the set
	 * @throws Exception If deserialization fails or Redis operation encounters an error
	 * @see SetOperations#members(Object)
	 */
	@Override
	public Set<Object> members(String key,RedisValueTypeEnum valueType,Type returnType) throws Exception {
		if(logger.isDebugEnabled()) {
			logger.debug("Redis set 'members' operation executing: members(key:{})", key);
		}
		Set<byte[]> values = this.operations.members(key);
		Set<Object> results = redisSerializer.deserialize(values, valueType, returnType);
		if(logger.isDebugEnabled()) {
			logger.debug("Redis set 'members' operation returned {}", redisSerializer.serializeToJSONString(results));
		}
		return results;
	}
	

	/**
	 * Returns the number of elements in the set stored at the specified key.
	 * 
	 * <p>This method returns the cardinality (number of elements) of the set stored at the key.
	 * If the key does not exist, it is treated as an empty set and 0 is returned.
	 *
	 * @param key The key of the set
	 * @return The number of elements in the set, or 0 if the key does not exist
	 * @see SetOperations#size(Object)
	 */
	@Override
	public Long size(String key) {
		if(logger.isDebugEnabled()) {
			logger.debug("Redis set 'size' operation executing: size(key:{})", key);
		}
		Long results = this.operations.size(key);
		if(logger.isDebugEnabled()) {
			logger.debug("Redis set 'size' operation returned {}", results);
		}
		return results;
	}


	/**
	 * Determines if multiple values are members of the set stored at the specified key.
	 * 
	 * <p>This method checks if each of the specified values is a member of the set stored at the key.
	 * The values are serialized according to the specified value type before checking for
	 * membership. If the key does not exist, it is treated as an empty set and all values
	 * will be mapped to false.
	 *
	 * @param key The key of the set
	 * @param valueType The type of the values to check, used for serialization
	 * @param values The values to check for membership in the set
	 * @return A map with each value as key and a Boolean indicating whether it is a member of the set
	 * @throws Exception If serialization fails or Redis operation encounters an error
	 * @see SetOperations#isMember(Object, Object...)
	 */
	@Override
	public Map<Object, Boolean> isMember(String key, RedisValueTypeEnum valueType, Object... values) throws Exception {
		if(logger.isDebugEnabled()) {
			logger.debug("Redis set 'isMember' operation executing: isMember(key:{},values:{})", key,redisSerializer.serializeToJSONString(values));
		}
		int len = values.length;
		Object[] newArray = new Object[len];
		for(int i = 0; i < len; i++) {
			newArray[i] = redisSerializer.serialize(values[i], valueType);
		}
		Map<Object,Boolean> resultMap = this.operations.isMember(key, newArray);
		if(resultMap == null) {
			return new LinkedHashMap<>();
		}
		for(int i = 0; i < len; i++) {
			Object byteObj = newArray[i];
			Boolean result = resultMap.get(byteObj);
			resultMap.remove(byteObj);
			resultMap.put(values[i], result);
		}
		if(logger.isDebugEnabled()) {
			logger.debug("Redis set 'isMember' operation returned {}", redisSerializer.serializeToJSONString(resultMap));
		}
		return resultMap;
	}

	/**
	 * Returns distinct random members from the set stored at the specified key.
	 * 
	 * <p>This method retrieves the specified number of distinct random members from the set
	 * stored at the key. The returned members are guaranteed to be unique within the returned set.
	 * If count is larger than the set size, the entire set will be returned. The retrieved
	 * values are deserialized according to the specified value type and return type.
	 *
	 * @param key The key of the set
	 * @param count The number of distinct random members to return
	 * @param valueType The type of the values stored in the set, used for deserialization
	 * @param returnType The expected return type for deserialization
	 * @return A set of distinct random members from the set
	 * @throws Exception If deserialization fails or Redis operation encounters an error
	 * @see SetOperations#distinctRandomMembers(Object, long)
	 */
	@Override
	public Set<Object> distinctRandomMembers(String key, long count,RedisValueTypeEnum valueType,Type returnType) throws Exception {
		if(logger.isDebugEnabled()) {
			logger.debug("Redis set 'distinctRandomMembers' operation executing: distinctRandomMembers(key:{},count:{})", key,count);
		}
		Set<byte[]> values = this.operations.distinctRandomMembers(key, count);
		Set<Object> results = redisSerializer.deserialize(values, valueType, returnType);
		if(logger.isDebugEnabled()) {
			logger.debug("Redis set 'distinctRandomMembers' operation returned {}", redisSerializer.serializeToJSONString(results));
		}
		return results;
	}

	/**
	 * Returns random members from the set stored at the specified key.
	 * 
	 * <p>This method retrieves the specified number of random members from the set stored at the key.
	 * Unlike distinctRandomMembers, this method may return the same member multiple times in the result.
	 * The retrieved values are deserialized according to the specified value type and return type.
	 *
	 * @param key The key of the set
	 * @param count The number of random members to return
	 * @param valueType The type of the values stored in the set, used for deserialization
	 * @param returnType The expected return type for deserialization
	 * @return A list of random members from the set, possibly containing duplicates
	 * @throws Exception If deserialization fails or Redis operation encounters an error
	 * @see SetOperations#randomMembers(Object, long)
	 */
	@Override
	public List<Object> randomMembers(String key, long count,RedisValueTypeEnum valueType,Type returnType) throws Exception {
		if(logger.isDebugEnabled()) {
			logger.debug("Redis set 'randomMembers' operation executing: randomMembers(key:{},count:{})", key,count);
		}
		List<byte[]> values =  this.operations.randomMembers(key, count);
		List<Object> results = redisSerializer.deserialize(values, valueType, returnType);
		if(logger.isDebugEnabled()) {
			logger.debug("Redis set 'randomMembers' operation returned {}", redisSerializer.serializeToJSONString(results));
		}
		return results;
	}

	/**
	 * Removes one or more members from the set stored at the specified key.
	 * 
	 * <p>This method removes the specified values from the set stored at the key.
	 * The values are serialized according to the specified value type before removal.
	 * If a value does not exist in the set, it is ignored. If the key does not exist,
	 * it is treated as an empty set and 0 is returned.
	 *
	 * @param key The key of the set
	 * @param valueType The type of the values to remove, used for serialization
	 * @param values The values to remove from the set
	 * @return The number of members that were removed from the set
	 * @throws Exception If serialization fails or Redis operation encounters an error
	 * @see SetOperations#remove(Object, Object...)
	 */
	@Override
	public Long remove(String key, RedisValueTypeEnum valueType, Object... values) throws Exception{
		if(logger.isDebugEnabled()) {
			logger.debug("Redis set 'remove' operation executing: remove(key:{})", key,redisSerializer.serializeToJSONString(values));
		}
		Object[] newArray = new Object[values.length];
		for(int i = 0; i < values.length; i++) {
			newArray[i] = redisSerializer.serialize(values[i], valueType);
		}
		Long results = this.operations.remove(key, newArray);
		if(logger.isDebugEnabled()) {
			logger.debug("Redis set 'remove' operation returned {}", results);
		}
		return results;
	}

	/**
	 * Returns the difference between the set stored at the first key and all other sets.
	 * 
	 * <p>This method computes the difference between the first set and all other sets specified
	 * in the collection of keys. The difference is the set of elements that exist in the first
	 * set but not in any of the other sets. The retrieved values are deserialized according to
	 * the specified value type and return type.
	 *
	 * @param comparisonKeys Collection of keys to compare
	 * @param valueType The type of the values stored in the sets, used for deserialization
	 * @param returnType The expected return type for deserialization
	 * @return A set containing the difference between the first set and all other sets
	 * @throws Exception If deserialization fails or Redis operation encounters an error
	 * @see SetOperations#difference(Collection)
	 */
	@Override
	public Set<Object> difference(Collection<String> comparisonKeys,RedisValueTypeEnum valueType,Type returnType) throws Exception {
		if(logger.isDebugEnabled()) {
			logger.debug("Redis set 'difference' operation executing: difference(comparisonKeys:{})", comparisonKeys);
		}
		Set<byte[]> values = this.operations.difference(comparisonKeys);
		Set<Object> results = redisSerializer.deserialize(values, valueType, returnType);
		if(logger.isDebugEnabled()) {
			logger.debug("Redis set 'difference' operation returned {}", redisSerializer.serializeToJSONString(results));
		}
		return results;
	}

	/**
	 * Computes the difference between sets and stores the result in a new set.
	 * 
	 * <p>This method computes the difference between the first set and all other sets specified
	 * in the collection of keys, and stores the result in the destination key. If the destination
	 * key already exists, it is overwritten. The difference is the set of elements that exist in
	 * the first set but not in any of the other sets.
	 *
	 * @param comparisonKeys Collection of keys to compare
	 * @param storeTo The key where the result will be stored
	 * @return The number of elements in the resulting set
	 * @see SetOperations#differenceAndStore(Collection, Object)
	 */
	@Override
	public Long differenceAndStore(Collection<String> comparisonKeys, String storeTo) {
		if(logger.isDebugEnabled()) {
			logger.debug("Redis set 'differenceAndStore' operation executing: differenceAndStore(comparisonKeys:{},storeTo:{})", comparisonKeys,storeTo);
		}
		Long results = this.operations.differenceAndStore(comparisonKeys, storeTo);
		if(logger.isDebugEnabled()) {
			logger.debug("Redis set 'differenceAndStore' operation returned {}", results);
		}
		return results;
	}

	/**
	 * Computes the union between sets and stores the result in a new set.
	 * 
	 * <p>This method computes the union between all sets specified in the collection of keys,
	 * and stores the result in the destination key. If the destination key already exists,
	 * it is overwritten. The union is the set of elements that exist in at least one of the sets.
	 *
	 * @param comparisonKeys Collection of keys to compute the union
	 * @param storeTo The key where the result will be stored
	 * @return The number of elements in the resulting set
	 * @see SetOperations#unionAndStore(Collection, Object)
	 */
	@Override
	public Long unionAndStore(Collection<String> comparisonKeys, String storeTo) {
		if(logger.isDebugEnabled()) {
			logger.debug("Redis set 'unionAndStore' operation executing: unionAndStore(comparisonKeys:{},storeTo:{})", comparisonKeys,storeTo);
		}
		Long results = this.operations.unionAndStore(comparisonKeys, storeTo);
		if(logger.isDebugEnabled()) {
			logger.debug("Redis set 'unionAndStore' operation returned {}", results);
		}
		return results;
	}

	/**
	 * Returns the union of all sets specified by the collection of keys.
	 * 
	 * <p>This method computes the union of all sets specified in the collection of keys.
	 * The union is the set of elements that exist in at least one of the sets.
	 * The retrieved values are deserialized according to the specified value type and return type.
	 *
	 * @param comparisonKeys Collection of keys to compute the union
	 * @param valueType The type of the values stored in the sets, used for deserialization
	 * @param returnType The expected return type for deserialization
	 * @return A set containing the union of all specified sets
	 * @throws Exception If deserialization fails or Redis operation encounters an error
	 * @see SetOperations#union(Collection)
	 */
	@Override
	public Set<Object> union(Collection<String> comparisonKeys,RedisValueTypeEnum valueType,Type returnType) throws Exception {
		if(logger.isDebugEnabled()) {
			logger.debug("Redis set 'union' operation executing: union(comparisonKeys:{})", comparisonKeys);
		}
		Set<byte[]> values = this.operations.union(comparisonKeys);
		Set<Object> results = redisSerializer.deserialize(values, valueType, returnType);
		if(logger.isDebugEnabled()) {
			logger.debug("Redis set 'union' operation returned {}", redisSerializer.serializeToJSONString(results));
		}
		return results;
	}

	/**
	 * Returns the intersection of all sets specified by the collection of keys.
	 * 
	 * <p>This method computes the intersection of all sets specified in the collection of keys.
	 * The intersection is the set of elements that exist in all of the sets.
	 * The retrieved values are deserialized according to the specified value type and return type.
	 *
	 * @param comparisonKeys Collection of keys to compute the intersection
	 * @param valueType The type of the values stored in the sets, used for deserialization
	 * @param returnType The expected return type for deserialization
	 * @return A set containing the intersection of all specified sets
	 * @throws Exception If deserialization fails or Redis operation encounters an error
	 * @see SetOperations#intersect(Collection)
	 */
	@Override
	public Set<Object> intersect(Collection<String> comparisonKeys,RedisValueTypeEnum valueType,Type returnType) throws Exception {
		if(logger.isDebugEnabled()) {
			logger.debug("Redis set 'intersect' operation executing: intersect(comparisonKeys:{})", comparisonKeys);
		}
		Set<byte[]> values = this.operations.intersect(comparisonKeys);
		Set<Object> results = redisSerializer.deserialize(values, valueType, returnType);
		if(logger.isDebugEnabled()) {
			logger.debug("Redis set 'intersect' operation returned {}", redisSerializer.serializeToJSONString(results));
		}
		return results;
	}

	/**
	 * Computes the intersection between sets and stores the result in a new set.
	 * 
	 * <p>This method computes the intersection of all sets specified in the collection of keys,
	 * and stores the result in the destination key. If the destination key already exists,
	 * it is overwritten. The intersection is the set of elements that exist in all of the sets.
	 *
	 * @param comparisonKeys Collection of keys to compute the intersection
	 * @param storeTo The key where the result will be stored
	 * @return The number of elements in the resulting set
	 * @see SetOperations#intersectAndStore(Collection, Object)
	 */
	@Override
	public Long intersectAndStore(Collection<String> comparisonKeys, String storeTo) {
		if(logger.isDebugEnabled()) {
			logger.debug("Redis set 'intersectAndStore' operation executing: intersectAndStore(comparisonKeys:{},storeTo:{})", comparisonKeys,storeTo);
		}
		Long results = this.operations.intersectAndStore(comparisonKeys, storeTo);
		if(logger.isDebugEnabled()) {
			logger.debug("Redis set 'intersectAndStore' operation returned {}", results);
		}
		return results;
	}

	/**
	 * Removes and returns random members from the set stored at the specified key.
	 * 
	 * <p>This method removes and returns the specified number of random members from the set
	 * stored at the key. If the key does not exist, an empty list is returned. The retrieved
	 * values are deserialized according to the specified value type and return type.
	 *
	 * @param key The key of the set
	 * @param count The number of members to pop
	 * @param valueType The type of the values stored in the set, used for deserialization
	 * @param returnType The expected return type for deserialization
	 * @return A list of popped members from the set
	 * @throws Exception If deserialization fails or Redis operation encounters an error
	 * @see SetOperations#pop(Object, long)
	 */
	@Override
	public List<Object> popMember(String key, long count,RedisValueTypeEnum valueType,Type returnType) throws Exception {
		if(logger.isDebugEnabled()) {
			logger.debug("Redis set 'popMember' operation executing: popMember(key:{},count:{})", key,count);
		}
		List<byte[]> values = this.operations.pop(key, count);
		List<Object> results = redisSerializer.deserialize(values, valueType, returnType);
		if(logger.isDebugEnabled()) {
			logger.debug("Redis set 'popMember' operation returned {}", redisSerializer.serializeToJSONString(results));
		}
		return results;
	}

	/**
	 * Moves a member from one set to another.
	 * 
	 * <p>This method removes the specified member from the source set and adds it to the
	 * destination set. If the source set does not contain the member, no operation is
	 * performed and false is returned. The value is serialized according to the specified
	 * value type before being moved.
	 *
	 * @param key The key of the source set
	 * @param value The member to move
	 * @param valueType The type of the value to move, used for serialization
	 * @param destKey The key of the destination set
	 * @return true if the member was moved, false if the member was not in the source set
	 * @throws Exception If serialization fails or Redis operation encounters an error
	 * @see SetOperations#move(Object, Object, Object)
	 */
	@Override
	public Boolean move(String key, Object value, RedisValueTypeEnum valueType, String destKey) throws Exception {
		if(logger.isDebugEnabled()) {
			logger.debug("Redis set 'move' operation executing: move(key:{},member:{},destKey:{})", key,redisSerializer.serializeToJSONString(value),destKey);
		}
		byte[] valueBytes = this.redisSerializer.serialize(value, valueType);
		Boolean results = this.operations.move(key, valueBytes, destKey);
		if(logger.isDebugEnabled()) {
			logger.debug("Redis set 'move' operation returned {}", results);
		}
		return results;
	}

	/**
	 * Incrementally iterates over elements of a set with pagination support.
	 * 
	 * <p>This method provides a way to scan through elements of a set in a paginated manner.
	 * It allows filtering elements by pattern and controlling the number of elements returned per page.
	 * The retrieved values are deserialized according to the specified value type and return type.
	 *
	 * @param key The key of the set to scan
	 * @param pattern The pattern to match elements (supports glob-style patterns)
	 * @param count The maximum number of elements to return per page
	 * @param pageNo The page number (1-based)
	 * @param valueType The type of the values stored in the set, used for deserialization
	 * @param returnType The expected return type for deserialization
	 * @return A ScanResults object containing the matching elements and the next page number
	 * @throws Exception If deserialization fails or Redis operation encounters an error
	 * @see SetOperations#scan(Object, ScanOptions)
	 */
	@Override
	public ScanResults scan(String key, String pattern, Integer count, Long pageNo, RedisValueTypeEnum valueType, Type returnType) throws Exception {
		if(logger.isDebugEnabled()) {
			logger.debug("Redis set 'scan' operation executing: scan(key:{},pattern:{},count:{},pageNo:{})", key,pattern,count,pageNo);
		}
		try(Cursor<byte[]> cursor = this.operations.scan(key, ScanOptions.scanOptions().match(pattern).count(count).build())){
			Set<byte[]> results = new HashSet<>();
			long offset = count * (pageNo - 1);
			for(int i = 0, c = 0; cursor.hasNext() && c < count; i++) {
				if(i >= offset) {
					c++;
					results.add(cursor.next());
					continue;
				}
				cursor.next();
			}
			Set<Object> members = redisSerializer.deserialize(results, valueType, returnType);
			
			if(logger.isDebugEnabled()) {
				logger.debug("Redis set 'scan' operation returned {}", redisSerializer.serializeToJSONString(members));
			}
			return new ScanResults(members,pageNo+1);
		}catch (Exception e) {
			throw e;
		}
		
	}
}