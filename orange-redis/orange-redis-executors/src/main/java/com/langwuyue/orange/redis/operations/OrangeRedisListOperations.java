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

import com.langwuyue.orange.redis.RedisValueTypeEnum;
import com.langwuyue.orange.redis.annotation.cross.ListMoveDirection;

/**
 * Redis list operations interface providing methods to manipulate list values in Redis.
 * 
 * @author Liang.Zhong
 * @since 1.0.0
 */
public interface OrangeRedisListOperations extends OrangeRedisOperations {

	/**
	 * Gets the element at the specified index in the list stored at key.
	 * Equivalent to Redis LINDEX command.
	 *
	 * @param key the key of the list
	 * @param index the index of the element to get
	 * @param valueType the type of the value
	 * @param returnType the expected return type
	 * @return the element at the specified index
	 * @throws Exception if any error occurs
	 */
	Object index(String key, long index, RedisValueTypeEnum valueType, Type returnType) throws Exception;

	/**
	 * Returns the index of the first occurrence of the specified value in the list.
	 * Equivalent to Redis LPOS command.
	 *
	 * @param key the key of the list
	 * @param value the value to search for
	 * @param valueType the type of the value
	 * @return the index of the first occurrence of the value, or null if not found
	 * @throws Exception if any error occurs
	 */
	Long indexOf(String key, Object value, RedisValueTypeEnum valueType) throws Exception;

	/**
	 * Returns the index of the last occurrence of the specified value in the list.
	 *
	 * @param key the key of the list
	 * @param value the value to search for
	 * @param valueType the type of the value
	 * @return the index of the last occurrence of the value, or null if not found
	 * @throws Exception if any error occurs
	 */
	Long lastIndexOf(String key, Object value, RedisValueTypeEnum valueType) throws Exception;

	/**
	 * Gets the length of the list stored at key.
	 * Equivalent to Redis LLEN command.
	 *
	 * @param key the key of the list
	 * @return the length of the list, or 0 if key does not exist
	 */
	Long size(String key);
	
	/**
	 * Removes the first count occurrences of value from the list stored at key.
	 * Equivalent to Redis LREM command.
	 *
	 * @param key the key of the list
	 * @param count the number of occurrences to remove
	 * @param value the value to remove
	 * @param valueType the type of the value
	 * @return the number of removed elements
	 * @throws Exception if any error occurs
	 */
	Long remove(String key, long count, Object value, RedisValueTypeEnum valueType) throws Exception;

	/**
	 * Inserts all the specified values at the head of the list stored at key.
	 * Equivalent to Redis LPUSH command.
	 *
	 * @param key the key of the list
	 * @param valueType the type of the values
	 * @param values the values to push
	 * @return the length of the list after the push operation
	 * @throws Exception if any error occurs
	 */
	Long leftPush(String key, RedisValueTypeEnum valueType, Object... values) throws Exception;

	/**
	 * Inserts value in the list stored at key either before or after the pivot value.
	 * Equivalent to Redis LINSERT command.
	 *
	 * @param key the key of the list
	 * @param pivot the pivot value
	 * @param value the value to insert
	 * @param valueType the type of the value
	 * @return the length of the list after the insert operation
	 * @throws Exception if any error occurs
	 */
	Long leftPush(String key, Object pivot, Object value, RedisValueTypeEnum valueType) throws Exception;
	
	/**
	 * Sets the list element at index to value.
	 * Equivalent to Redis LSET command.
	 *
	 * @param key the key of the list
	 * @param index the index to set
	 * @param value the new value
	 * @param valueType the type of the value
	 * @throws Exception if any error occurs
	 */
	void set(String key, long index, Object value, RedisValueTypeEnum valueType) throws Exception;
	
	/**
	 * Removes and returns count elements from the head of the list stored at key.
	 * Equivalent to Redis LPOP command with count.
	 *
	 * @param key the key of the list
	 * @param count the number of elements to pop
	 * @param valueType the type of the values
	 * @param returnType the expected return type
	 * @return the popped elements
	 * @throws Exception if any error occurs
	 */
	List<Object> leftPop(String key, long count, RedisValueTypeEnum valueType, Type returnType) throws Exception;

	/**
	 * Removes and returns an element from the head of the list stored at key, waiting up to timeout if necessary.
	 * Equivalent to Redis BLPOP command.
	 *
	 * @param key the key of the list
	 * @param timeout the timeout duration
	 * @param timeUnit the timeout time unit
	 * @param valueType the type of the value
	 * @param returnType the expected return type
	 * @return the popped element, or null if timeout reached
	 * @throws Exception if any error occurs
	 */
	Object leftPop(String key, long timeout, TimeUnit timeUnit,RedisValueTypeEnum valueType, Type returnType) throws Exception;

	/**
	 * Inserts all the specified values at the tail of the list stored at key.
	 * Equivalent to Redis RPUSH command.
	 *
	 * @param key the key of the list
	 * @param valueType the type of the values
	 * @param values the values to push
	 * @return the length of the list after the push operation
	 * @throws Exception if any error occurs
	 */
	Long rightPush(String key, RedisValueTypeEnum valueType, Object... values) throws Exception;

	/**
	 * Inserts value in the list stored at key either before or after the pivot value.
	 * Equivalent to Redis LINSERT command.
	 *
	 * @param key the key of the list
	 * @param pivot the pivot value
	 * @param value the value to insert
	 * @param valueType the type of the value
	 * @return the length of the list after the insert operation
	 * @throws Exception if any error occurs
	 */
	Long rightPush(String key, Object pivot, Object value, RedisValueTypeEnum valueType) throws Exception;
	
	/**
	 * Removes and returns count elements from the tail of the list stored at key.
	 * Equivalent to Redis RPOP command with count.
	 *
	 * @param key the key of the list
	 * @param count the number of elements to pop
	 * @param valueType the type of the values
	 * @param returnType the expected return type
	 * @return the popped elements
	 * @throws Exception if any error occurs
	 */
	List<Object> rightPop(String key, long count, RedisValueTypeEnum valueType, Type returnType) throws Exception;

	/**
	 * Removes and returns an element from the tail of the list stored at key, waiting up to timeout if necessary.
	 * Equivalent to Redis BRPOP command.
	 *
	 * @param key the key of the list
	 * @param timeout the timeout duration
	 * @param timeUnit the timeout time unit
	 * @param valueType the type of the value
	 * @param returnType the expected return type
	 * @return the popped element, or null if timeout reached
	 * @throws Exception if any error occurs
	 */
	Object rightPop(String key, long timeout, TimeUnit timeUnit,RedisValueTypeEnum valueType, Type returnType) throws Exception;

	/**
	 * Returns the specified elements of the list stored at key.
	 * Equivalent to Redis LRANGE command.
	 *
	 * @param key the key of the list
	 * @param startIndex the start index (0-based)
	 * @param endIndex the end index (0-based, -1 for end of list)
	 * @param valueType the type of the values
	 * @param returnType the expected return type
	 * @return the specified range of elements
	 * @throws Exception if any error occurs
	 */
	List<Object> range(String key, Long startIndex, Long endIndex,RedisValueTypeEnum valueType, Type returnType) throws Exception;

	/**
	 * Trim the list stored at key so that it will contain only the specified range of elements.
	 * Equivalent to Redis LTRIM command.
	 *
	 * @param value the key of the list
	 * @param startIndex the start index (0-based)
	 * @param endIndex the end index (0-based, -1 for end of list)
	 */
	void trim(String value, Long startIndex, Long endIndex);

	/**
	 * Atomically moves an element from one list to another.
	 *
	 * @param referenceKey the source key
	 * @param destKey the destination key
	 * @param direction the direction of move (LEFT or RIGHT)
	 * @param valueType the type of the value
	 * @param returnType the expected return type
	 * @return the moved element
	 * @throws Exception if any error occurs
	 */
	Object move(String referenceKey, String destKey, ListMoveDirection direction, RedisValueTypeEnum valueType, Type returnType) throws Exception;

	/**
	 * Atomically moves an element from one list to another with timeout.
	 *
	 * @param referenceKey the source key
	 * @param destKey the destination key
	 * @param direction the direction of move (LEFT or RIGHT)
	 * @param valueType the type of the value
	 * @param returnType the expected return type
	 * @param timeoutValue the timeout duration
	 * @param timeoutUnit the timeout time unit
	 * @return the moved element, or null if timeout reached
	 * @throws Exception if any error occurs
	 */
	Object move(
		String referenceKey, 
		String destKey, 
		ListMoveDirection direction, 
		RedisValueTypeEnum valueType,
		Type returnType, 
		long timeoutValue, 
		TimeUnit timeoutUnit
	) throws Exception;
}