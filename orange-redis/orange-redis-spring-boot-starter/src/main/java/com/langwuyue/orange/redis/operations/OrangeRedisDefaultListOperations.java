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
import java.util.concurrent.TimeUnit;

import org.springframework.data.redis.connection.RedisListCommands.Direction;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.RedisTemplate;

import com.langwuyue.orange.redis.ListMoveDirectionEnum;
import com.langwuyue.orange.redis.RedisValueTypeEnum;
import com.langwuyue.orange.redis.annotation.cross.ListMoveDirection;
import com.langwuyue.orange.redis.configuration.OrangeRedisSerializer;
import com.langwuyue.orange.redis.logger.OrangeRedisLogger;

/**
 * Default implementation of {@link OrangeRedisListOperations} interface for Redis List operations.
 * 
 * <p>This class provides a comprehensive implementation of Redis List data structure operations,
 * including push, pop, range, index, and other list-specific functionalities. It handles
 * serialization/deserialization of values and provides detailed logging of operations.
 * 
 * <p>The implementation supports both blocking and non-blocking operations, with configurable
 * timeouts for blocking operations. It also provides bidirectional operations (left/right)
 * for push and pop operations.
 *
 * @author Liang.Zhong
 * @since 1.0.0
 */
public class OrangeRedisDefaultListOperations extends OrangeRedisAbstractOperations implements OrangeRedisListOperations {

	/**
	 * The underlying Redis list operations template.
	 * Provides direct access to Redis list operations with String keys and byte[] values.
	 * Supports all standard Redis list operations including push, pop, range, and index operations.
	 */
	private ListOperations<String, byte[]> operations;
	
	/**
	 * Serializer for converting between Java objects and Redis byte arrays.
	 * Handles serialization of list elements before storage and deserialization when retrieving.
	 * Supports various value types as defined in {@link RedisValueTypeEnum}.
	 */
	private OrangeRedisSerializer redisSerializer;
	
	/**
	 * Logger for recording operation details and debugging information.
	 * Logs method calls, parameters and results at debug level.
	 * Helps with troubleshooting Redis list operations and tracking data flow.
	 */
	private OrangeRedisLogger logger;
	
	/**
	 * Constructs a new OrangeRedisDefaultListOperations instance.
	 * 
	 * <p>Initializes the list operations with the provided Redis template, serializer, and logger.
	 * The template is used for Redis operations, the serializer handles conversion between Java
	 * objects and Redis byte arrays, and the logger records operation details.
	 *
	 * @param template The Redis template to use for list operations
	 * @param redisSerializer The serializer to use for converting between Java objects and Redis byte arrays
	 * @param logger The logger to use for recording operation details
	 */
	public OrangeRedisDefaultListOperations(RedisTemplate<String,byte[]> template,OrangeRedisSerializer redisSerializer,OrangeRedisLogger logger) {
		super(template,logger);
		this.operations = template.opsForList();
		this.redisSerializer = redisSerializer;
		this.logger = logger;
	}
	
	/**
	 * Gets the element at the specified position in the list stored at key.
	 * 
	 * <p>The index is zero-based, so 0 means the first element, 1 the second element and so on.
	 * Negative indices can be used to designate elements starting at the tail of the list.
	 * For example, -1 means the last element, -2 the penultimate and so forth.
	 *
	 * @param key the key of the list
	 * @param index the index of the element to retrieve
	 * @param valueType the type of the value stored in Redis
	 * @param returnType the Java type to deserialize the value into
	 * @return the requested element, or null when index is out of range
	 * @throws Exception if deserialization fails or Redis operation fails
	 */
	@Override
	public Object index(String key, long index, RedisValueTypeEnum valueType, Type returnType) throws Exception {
		if(logger.isDebugEnabled()) {
			logger.debug("Redis list 'index' operation executing: index(key:{},index:{})",key,index);
		}
		byte[] bytes = this.operations.index(key, index);
		Object result = redisSerializer.deserialize(bytes, valueType, returnType);
		if(logger.isDebugEnabled()) {
			logger.debug("Redis list 'index' operation returned", new String(bytes));
		}
		return result;
	}

	/**
	 * Returns the index of the first occurrence of the specified value in the list stored at key.
	 * 
	 * <p>The method performs a linear search from the head of the list to the tail,
	 * comparing each element with the given value. The comparison is done after
	 * serializing the input value according to the specified value type.
	 *
	 * @param key the key of the list
	 * @param value the value to search for
	 * @param valueType the type of the value stored in Redis
	 * @return the index of the first occurrence of the value, or -1 if not found
	 * @throws Exception if serialization fails or Redis operation fails
	 */
	@Override
	public Long indexOf(String key, Object value, RedisValueTypeEnum valueType) throws Exception {
		byte[] bytes = redisSerializer.serialize(value, valueType);
		if(logger.isDebugEnabled()) {
			logger.debug("Redis list 'indexOf' operation executing: indexOf(key:{},member:{})",key,new String(bytes));
		}
		Long results = this.operations.indexOf(key, bytes);
		if(logger.isDebugEnabled()) {
			logger.debug("Redis list 'indexOf' operation returned {}", results);
		}
		return results;
	}

	/**
	 * Returns the index of the last occurrence of the specified value in the list stored at key.
	 * 
	 * <p>The method performs a linear search from the tail of the list to the head,
	 * comparing each element with the given value. The comparison is done after
	 * serializing the input value according to the specified value type.
	 *
	 * @param key the key of the list
	 * @param value the value to search for
	 * @param valueType the type of the value stored in Redis
	 * @return the index of the last occurrence of the value, or -1 if not found
	 * @throws Exception if serialization fails or Redis operation fails
	 */
	@Override
	public Long lastIndexOf(String key, Object value, RedisValueTypeEnum valueType) throws Exception {
		byte[] bytes = redisSerializer.serialize(value, valueType);
		if(logger.isDebugEnabled()) {
			logger.debug("Redis list 'lastIndexOf' operation executing: lastIndexOf(key:{},index:{})",key,new String(bytes));
		}
		Long results = this.operations.lastIndexOf(key, bytes);
		if(logger.isDebugEnabled()) {
			logger.debug("Redis list 'lastIndexOf' operation returned {}", results);
		}
		return results;
	}

	/**
	 * Returns the length of the list stored at key.
	 * 
	 * <p>If the key does not exist, it is interpreted as an empty list and 0 is returned.
	 * If the key exists but does not hold a list value, an exception is thrown.
	 *
	 * @param key the key of the list
	 * @return the length of the list at key
	 */
	@Override
	public Long size(String key) {
		if(logger.isDebugEnabled()) {
			logger.debug("Redis list 'size' operation executing: size(key:{})",key);
		}
		Long results = this.operations.size(key);
		if(logger.isDebugEnabled()) {
			logger.debug("Redis list 'size' operation returned {}", results);
		}
		return results;
	}

	/**
	 * Removes the first count occurrences of value from the list stored at key.
	 * 
	 * <p>The count argument influences the operation in the following ways:
	 * <ul>
	 *   <li>count > 0: Remove elements equal to value moving from head to tail</li>
	 *   <li>count < 0: Remove elements equal to value moving from tail to head</li>
	 *   <li>count = 0: Remove all elements equal to value</li>
	 * </ul>
	 *
	 * @param key the key of the list
	 * @param count the number of occurrences to remove (see method description for details)
	 * @param value the value to remove
	 * @param valueType the type of the value stored in Redis
	 * @return the number of removed elements
	 * @throws Exception if serialization fails or Redis operation fails
	 */
	@Override
	public Long remove(String key, long count, Object value, RedisValueTypeEnum valueType) throws Exception {
		byte[] bytes = redisSerializer.serialize(value, valueType);
		if(logger.isDebugEnabled()) {
			logger.debug("Redis list 'index' operation executing: index(key:{},count:{},member:{})",key,count,new String(bytes));
		}
		Long results = this.operations.remove(key, count, bytes);
		if(logger.isDebugEnabled()) {
			logger.debug("Redis list 'index' operation returned {}", results);
		}
		return results;
	}

	/**
	 * Inserts all the specified values at the head of the list stored at key.
	 * 
	 * <p>Elements are inserted one after the other, so the first element in the arguments
	 * will be the last element in the list after the operation. If the key does not exist,
	 * it is created as an empty list before performing the push operations.
	 *
	 * @param key the key of the list
	 * @param valueType the type of the values to be stored in Redis
	 * @param values the values to push to the list
	 * @return the length of the list after the push operations
	 * @throws Exception if serialization fails or Redis operation fails
	 */
	@Override
	public Long leftPush(String key, RedisValueTypeEnum valueType, Object... values) throws Exception {
		if(logger.isDebugEnabled()) {
			logger.debug("Redis leftPush 'index' operation executing: leftPush(key:{},members:{})",key,redisSerializer.serializeToJSONString(values));
		}
		byte[][] bytes = redisSerializer.serialize(values, valueType);
		Long results = this.operations.leftPushAll(key, bytes);
		if(logger.isDebugEnabled()) {
			logger.debug("Redis list 'index' operation returned {}", results);
		}
		return results;
	}

	/**
	 * Inserts the specified value before the pivot element in the list stored at key.
	 * 
	 * <p>When the pivot is not found, no operation is performed and -1 is returned.
	 * If the key does not exist, it is treated as an empty list and no operation is performed.
	 *
	 * @param key the key of the list
	 * @param pivot the pivot element to insert before
	 * @param value the value to insert
	 * @param valueType the type of the values to be stored in Redis
	 * @return the length of the list after the operation, or -1 if pivot was not found
	 * @throws Exception if serialization fails or Redis operation fails
	 */
	@Override
	public Long leftPush(String key, Object pivot, Object value, RedisValueTypeEnum valueType) throws Exception {
		byte[] pivotBytes = redisSerializer.serialize(pivot, valueType);
		byte[] valueBytes = redisSerializer.serialize(value, valueType);
		if(logger.isDebugEnabled()) {
			logger.debug(
				"Redis list 'leftPush' operation executing: leftPush(key:{},pivot:{},member:{})",
				key,
				new String(pivotBytes),
				new String(valueBytes)
			);
		}
		Long results = this.operations.leftPush(key, pivotBytes, valueBytes);
		if(logger.isDebugEnabled()) {
			logger.debug("Redis list 'leftPush' operation returned {}", results);
		}
		return results;
	}

	/**
	 * Removes and returns the first element of the list stored at key, waiting up to the specified timeout
	 * if the list is empty.
	 * 
	 * <p>This is a blocking version of leftPop where the client will wait for the specified timeout
	 * for an element to become available. If the timeout expires before an element is available,
	 * null is returned.
	 *
	 * @param <T> the type of the returned value
	 * @param key the key of the list
	 * @param timeout the maximum time to wait for an element to become available
	 * @param timeUnit the time unit of the timeout
	 * @param valueType the type of the value stored in Redis
	 * @param returnType the Java type to deserialize the value into
	 * @return the value of the first element, or null when timeout expires or key does not exist
	 * @throws Exception if deserialization fails or Redis operation fails
	 */
	@Override
	public Object leftPop(
		String key, 
		long timeout, 
		TimeUnit timeUnit, 
		RedisValueTypeEnum valueType, 
		Type returnType
	) throws Exception {
		if(logger.isDebugEnabled()) {
			logger.debug("Redis list 'leftPop' operation executing: leftPop(key:{},timeout:{}ms)",
				key,
				TimeUnit.MILLISECONDS.convert(timeout,timeUnit)
			);
		}
		byte[] bytes = this.operations.leftPop(key,timeout,timeUnit);
		Object results = redisSerializer.deserialize(bytes, valueType, returnType);
		if(logger.isDebugEnabled()) {
			logger.debug("Redis list 'leftPop' operation returned {}", new String(bytes));
		}
		return results;
	}

	/**
	 * Inserts all the specified values at the tail of the list stored at key.
	 * 
	 * <p>Elements are inserted one after the other, so the last element in the arguments
	 * will be the last element in the list after the operation. If the key does not exist,
	 * it is created as an empty list before performing the push operations.
	 *
	 * @param key the key of the list
	 * @param valueType the type of the values to be stored in Redis
	 * @param values the values to push to the list
	 * @return the length of the list after the push operations
	 * @throws Exception if serialization fails or Redis operation fails
	 */
	@Override
	public Long rightPush(String key, RedisValueTypeEnum valueType, Object... values) throws Exception {
		if(logger.isDebugEnabled()) {
			logger.debug(
				"Redis list 'rightPush' operation executing: rightPush(key:{},members:{})",
				key,
				redisSerializer.serializeToJSONString(values)
			);
		}
		byte[][] bytes = redisSerializer.serialize(values, valueType);
		Long results = this.operations.rightPushAll(key, bytes);
		if(logger.isDebugEnabled()) {
			logger.debug("Redis list 'rightPush' operation returned {}", results);
		}
		return results;
	}

	/**
	 * Inserts the specified value after the pivot element in the list stored at key.
	 * 
	 * <p>When the pivot is not found, no operation is performed and -1 is returned.
	 * If the key does not exist, it is treated as an empty list and no operation is performed.
	 *
	 * @param key the key of the list
	 * @param pivot the pivot element to insert after
	 * @param value the value to insert
	 * @param valueType the type of the values to be stored in Redis
	 * @return the length of the list after the operation, or -1 if pivot was not found
	 * @throws Exception if serialization fails or Redis operation fails
	 */
	@Override
	public Long rightPush(String key, Object pivot, Object value, RedisValueTypeEnum valueType) throws Exception {
		byte[] pivotBytes = redisSerializer.serialize(pivot, valueType);
		byte[] valueBytes = redisSerializer.serialize(value, valueType);
		if(logger.isDebugEnabled()) {
			logger.debug(
				"Redis list 'rightPush' operation executing: rightPush(key:{},pivot:{},member:{})",
				key,
				new String(pivotBytes),
				new String(valueBytes)
			);
		}
		Long results = this.operations.rightPush(key, pivotBytes, valueBytes);
		if(logger.isDebugEnabled()) {
			logger.debug("Redis list 'rightPush' operation returned {}", results);
		}
		return results;
	}

	/**
	 * Removes and returns the last element of the list stored at key, waiting up to the specified timeout
	 * if the list is empty.
	 * 
	 * <p>This is a blocking version of rightPop where the client will wait for the specified timeout
	 * for an element to become available. If the timeout expires before an element is available,
	 * null is returned.
	 *
	 * @param key the key of the list
	 * @param timeout the maximum time to wait for an element to become available
	 * @param timeUnit the time unit of the timeout
	 * @param valueType the type of the value stored in Redis
	 * @param returnType the Java type to deserialize the value into
	 * @return the value of the last element, or null when timeout expires or key does not exist
	 * @throws Exception if deserialization fails or Redis operation fails
	 */
	@Override
	public Object rightPop(
		String key, 
		long timeout, 
		TimeUnit timeUnit, 
		RedisValueTypeEnum valueType, 
		Type returnType
	) throws Exception {
		if(logger.isDebugEnabled()) {
			logger.debug(
				"Redis list 'rightPop' operation executing: rightPop(key:{},timeout:{}ms)",
				key,
				TimeUnit.MILLISECONDS.convert(timeout, timeUnit)
			);
		}
		byte[] bytes = this.operations.rightPop(key,timeout,timeUnit);
		Object results = redisSerializer.deserialize(bytes, valueType, returnType);
		if(logger.isDebugEnabled()) {
			logger.debug("Redis list 'rightPop' operation returned {}", new String(bytes));
		}
		return results;
	}

	/**
	 * Returns the specified elements of the list stored at key.
	 * 
	 * <p>The offsets startIndex and endIndex are zero-based indexes, with 0 being the first element
	 * of the list (the head of the list), 1 being the next element and so on.
	 * These offsets can also be negative numbers indicating offsets starting at the end of the list.
	 * For example, -1 is the last element of the list, -2 the penultimate, and so on.
	 *
	 * @param key the key of the list
	 * @param startIndex the starting index (inclusive)
	 * @param endIndex the ending index (inclusive)
	 * @param valueType the type of the values stored in Redis
	 * @param returnType the Java type to deserialize the values into
	 * @return list of elements in the specified range
	 * @throws Exception if deserialization fails or Redis operation fails
	 */
	@Override
	public List<Object> range(
		String key, 
		Long startIndex, 
		Long endIndex, 
		RedisValueTypeEnum valueType, 
		Type returnType
	) throws Exception {
		if(logger.isDebugEnabled()) {
			logger.debug("Redis list 'range' operation executing: range(key:{},startIndex:{},endIndex:{})",key,startIndex,endIndex);
		}
		List<byte[]> result = this.operations.range(key, startIndex, endIndex);
		List<Object> results = redisSerializer.deserialize(result, valueType, returnType);
		if(logger.isDebugEnabled()) {
			logger.debug("Redis list 'range' operation returned {}", redisSerializer.serializeToJSONString(results));
		}
		return results;
	}

	/**
	 * Removes and returns up to count elements from the head of the list stored at key.
	 * 
	 * <p>If the list contains fewer than count elements, all available elements will be returned.
	 * If the key does not exist, an empty list is returned.
	 *
	 * @param <T> the type of the returned values
	 * @param key the key of the list
	 * @param count the maximum number of elements to pop
	 * @param valueType the type of the values stored in Redis
	 * @param returnType the Java type to deserialize the values into
	 * @return a list of popped elements, or empty list if key does not exist
	 * @throws Exception if deserialization fails or Redis operation fails
	 */
	@Override
	public List<Object> leftPop(
		String key, 
		long count, 
		RedisValueTypeEnum valueType, 
		Type returnType
	)throws Exception {
		if(logger.isDebugEnabled()) {
			logger.debug("Redis list 'leftPop' operation executing: leftPop(key:{},count:{})",key,count);
		}
		List<byte[]> bytes = this.operations.leftPop(key,count);
		List<Object> results = redisSerializer.deserialize(bytes, valueType, returnType);
		if(logger.isDebugEnabled()) {
			logger.debug("Redis list 'leftPop' operation returned {}", redisSerializer.serializeToJSONString(results));
		}
		return results;
	}

	/**
	 * Removes and returns up to count elements from the tail of the list stored at key.
	 * 
	 * <p>If the list contains fewer than count elements, all available elements will be returned.
	 * If the key does not exist, an empty list is returned.
	 *
	 * @param key the key of the list
	 * @param count the maximum number of elements to pop
	 * @param valueType the type of the values stored in Redis
	 * @param returnType the Java type to deserialize the values into
	 * @return a list of popped elements, or empty list if key does not exist
	 * @throws Exception if deserialization fails or Redis operation fails
	 */
	@Override
	public List<Object> rightPop(
		String key, 
		long count, 
		RedisValueTypeEnum valueType, 
		Type returnType
	)throws Exception {
		if(logger.isDebugEnabled()) {
			logger.debug("Redis list 'rightPop' operation executing: rightPop(key:{},count:{})",key,count);
		}
		List<byte[]> bytes = this.operations.rightPop(key,count);
		List<Object> results = redisSerializer.deserialize(bytes, valueType, returnType);
		if(logger.isDebugEnabled()) {
			logger.debug("Redis list 'rightPop' operation returned {}", redisSerializer.serializeToJSONString(results));
		}
		return results;
	}

	/**
	 * Sets the list element at index to value.
	 * 
	 * <p>An error is returned for out of range indexes (index >= size or index < -size).
	 * Negative indexes are supported, with -1 being the last element, -2 the penultimate, etc.
	 *
	 * @param key the key of the list
	 * @param index the index of the element to set
	 * @param value the new value
	 * @param valueType the type of the value to be stored in Redis
	 * @throws Exception if serialization fails, index is out of range, or Redis operation fails
	 */
	@Override
	public void set(String key, long index, Object value, RedisValueTypeEnum valueType) throws Exception {
		byte[] bytes = redisSerializer.serialize(value, valueType);
		if(logger.isDebugEnabled()) {
			logger.debug("Redis list 'set' operation executing: set(key:{},index:{},member:{})",key,index,new String(bytes));
		}
		this.operations.set(key, index, bytes);
		if(logger.isDebugEnabled()) {
			logger.debug("Redis list 'set' operation execute successfully");
		}
	}

	/**
	 * Trim an existing list so that it will contain only the specified range of elements.
	 * 
	 * <p>Both startIndex and endIndex can be negative, where -1 is the last element of the list,
	 * -2 the penultimate, and so on. Out of range indexes will not produce an error:
	 * if startIndex is larger than the end of the list, or startIndex > endIndex,
	 * the result will be an empty list (which causes key to be removed).
	 *
	 * @param key the key of the list
	 * @param startIndex the starting index (inclusive)
	 * @param endIndex the ending index (inclusive)
	 */
	@Override
	public void trim(String key, Long startIndex, Long endIndex) {
		if(logger.isDebugEnabled()) {
			logger.debug("Redis list 'index' operation executing: index(key:{},startIndex:{},endIndex:{})",key,startIndex,endIndex);
		}
		this.operations.trim(key, startIndex, endIndex);
		if(logger.isDebugEnabled()) {
			logger.debug("Redis list 'index' operation execute successfully");
		}
	}

	/**
	 * Atomically moves an element from one list to another.
	 * 
	 * <p>This operation removes the element from the source list (referenceKey) and
	 * pushes it to the destination list (destKey). The direction parameters specify
	 * from which end of the source list to remove the element and to which end of
	 * the destination list to push it.
	 *
	 * @param referenceKey the key of the source list
	 * @param destKey the key of the destination list
	 * @param direction specifies the source and destination ends (LEFT/RIGHT)
	 * @param valueType the type of the value stored in Redis
	 * @param returnType the Java type to deserialize the value into
	 * @return the moved element, or null if source list is empty
	 * @throws Exception if deserialization fails or Redis operation fails
	 */
	@Override
	public Object move(String referenceKey, String destKey, ListMoveDirection direction, RedisValueTypeEnum valueType, Type returnType) throws Exception {
		if(logger.isDebugEnabled()) {
			logger.debug("Redis list 'move' operation executing: move(referenceKey:{},destKey:{},direction:{})",referenceKey,destKey,direction);
		}
		Direction from = direction.from() == ListMoveDirectionEnum.LEFT ? Direction.LEFT : Direction.RIGHT;
		Direction to = direction.to() == ListMoveDirectionEnum.LEFT ? Direction.LEFT : Direction.RIGHT;
		byte[] bytes = this.operations.move(referenceKey, from, destKey, to);
		if(bytes == null || bytes.length == 0) {
			return null;
		}
		Object results = redisSerializer.deserialize(bytes, valueType, returnType);
		if(logger.isDebugEnabled()) {
			logger.debug("Redis list 'move' operation returned {}", new String(bytes));
		}
		return results;
	}

	/**
	 * Atomically moves an element from one list to another with a blocking timeout.
	 * 
	 * <p>This operation removes the element from the source list (referenceKey) and
	 * pushes it to the destination list (destKey). If the source list is empty,
	 * the operation will block until either an element becomes available or the timeout expires.
	 * The direction parameters specify from which end of the source list to remove
	 * the element and to which end of the destination list to push it.
	 *
	 * @param referenceKey the key of the source list
	 * @param destKey the key of the destination list
	 * @param direction specifies the source and destination ends (LEFT/RIGHT)
	 * @param valueType the type of the value stored in Redis
	 * @param returnType the Java type to deserialize the value into
	 * @param timeoutValue the maximum time to wait for an element
	 * @param timeoutUnit the time unit of the timeout
	 * @return the moved element, or null if timeout expires
	 * @throws Exception if deserialization fails or Redis operation fails
	 */
	@Override
	public Object move(
		String referenceKey, 
		String destKey, 
		ListMoveDirection direction, 
		RedisValueTypeEnum valueType,
		Type returnType, 
		long timeoutValue, 
		TimeUnit timeoutUnit
	) throws Exception {
		if(logger.isDebugEnabled()) {
			logger.debug(
				"Redis list 'move' operation executing: move(referenceKey:{},destKey:{},direction:{},timeout:{}ms)",
				referenceKey,
				destKey,
				direction,
				TimeUnit.MILLISECONDS.convert(timeoutValue, timeoutUnit)
			);
		}
		Direction from = direction.from() == ListMoveDirectionEnum.LEFT ? Direction.LEFT : Direction.RIGHT;
		Direction to = direction.to() == ListMoveDirectionEnum.LEFT ? Direction.LEFT : Direction.RIGHT;
		byte[] bytes = this.operations.move(referenceKey, from, destKey, to, timeoutValue, timeoutUnit);
		Object results = redisSerializer.deserialize(bytes, valueType, returnType);
		if(logger.isDebugEnabled()) {
			logger.debug("Redis list 'move' operation returned {}", new String(bytes));
		}
		return results;
	}
}