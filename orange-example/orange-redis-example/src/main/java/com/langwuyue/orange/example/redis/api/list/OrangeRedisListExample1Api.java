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
package com.langwuyue.orange.example.redis.api.list;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import com.langwuyue.orange.example.redis.entity.OrangeValueExampleEntity;
import com.langwuyue.orange.redis.annotation.OrangeRedisKey;
import com.langwuyue.orange.redis.annotation.Timeout;
import com.langwuyue.orange.redis.template.list.JSONOperationsTemplate;


/**
 * Basic Redis List operations interface with JSON value support.
 * 
 * <p>This interface provides fundamental Redis List operations for storing and manipulating
 * JSON objects ({@link OrangeValueExampleEntity}). It extends {@link JSONOperationsTemplate}
 * to inherit common List operations with JSON serialization support.
 * 
 * <p>Supported Redis commands include:
 * <ul>
 *   <li>LPUSH/RPUSH - Add elements to the left/right of the list</li>
 *   <li>LPOP/RPOP - Remove and return elements from the left/right</li>
 *   <li>LINDEX - Get element by index</li>
 *   <li>LRANGE - Get elements within an index range</li>
 *   <li>LPOS - Find index of elements</li>
 *   <li>LRANDMEMBER - Get random elements from the list</li>
 * </ul>
 * 
 * <p>Key configuration:
 * <ul>
 *   <li>Fixed key: "orange:list:example1"</li>
 *   <li>Default expiration: 1 hour</li>
 *   <li>Value type: JSON (automatically serialized/deserialized)</li>
 * </ul>
 * 
 * <p>Usage example:
 * <pre>{@code
 * // Add elements to list
 * OrangeValueExampleEntity entity = new OrangeValueExampleEntity();
 * api.leftPush(Collections.singleton(entity));
 * 
 * // Get elements by index
 * OrangeValueExampleEntity first = api.get(0L);
 * List<OrangeValueExampleEntity> range = api.getByIndexRange(0L, 9L);
 * 
 * // Pop elements
 * OrangeValueExampleEntity left = api.leftPop();
 * List<OrangeValueExampleEntity> rights = api.rightPop(5L);
 * 
 * // Random access
 * OrangeValueExampleEntity random = api.randomOne();
 * List<OrangeValueExampleEntity> distinct = api.randomAndDistinct(3L);
 * }</pre>
 * 
 * <p>Performance considerations:
 * <ul>
 *   <li>List operations are generally O(1), except for random access O(N)</li>
 *   <li>Batch operations (like bulk push) reduce network round-trips</li>
 *   <li>Index-based access near list ends is faster than middle access</li>
 *   <li>Consider using SCAN for iterating large lists</li>
 * </ul>
 * 
 * @author Liang.Zhong
 * @since 1.0.0
 * @see JSONOperationsTemplate Base template for JSON List operations
 * @see OrangeValueExampleEntity Entity type for list elements
 * @see com.langwuyue.orange.redis.annotation.OrangeRedisKey Key configuration
 */
@OrangeRedisKey(expirationTime = @Timeout(value = 1, unit = TimeUnit.HOURS), key = "orange:list:example1")
public interface OrangeRedisListExample1Api extends JSONOperationsTemplate<OrangeValueExampleEntity> {

	/**
	 * Adds one or more elements to the left (head) of the list.
	 * <p>
	 * This operation corresponds to the Redis LPUSH command. It inserts all the specified
	 * values at the head of the list stored at the key. Elements are inserted one after
	 * the other from left to right. If the key does not exist, it is created as an empty list
	 * before performing the push operation.
	 *
	 * @param members The collection of {@link OrangeValueExampleEntity} objects to add to the list
	 * @return A map with each entity as key and a boolean indicating success (true) or failure (false)
	 *         for each entity's insertion
	 * @see <a href="https://redis.io/commands/lpush">Redis LPUSH command</a>
	 */
	@Override
	default Map<OrangeValueExampleEntity, Boolean> leftPush(Collection<OrangeValueExampleEntity> members) {
		
		return null;
	}

	/**
	 * Adds one or more elements to the right (tail) of the list.
	 * <p>
	 * This operation corresponds to the Redis RPUSH command. It inserts all the specified
	 * values at the tail of the list stored at the key. Elements are inserted one after
	 * the other from left to right. If the key does not exist, it is created as an empty list
	 * before performing the push operation.
	 *
	 * @param members The collection of {@link OrangeValueExampleEntity} objects to add to the list
	 * @return A map with each entity as key and a boolean indicating success (true) or failure (false)
	 *         for each entity's insertion
	 * @see <a href="https://redis.io/commands/rpush">Redis RPUSH command</a>
	 */
	@Override
	default Map<OrangeValueExampleEntity, Boolean> rightPush(Collection<OrangeValueExampleEntity> members) {
		
		return null;
	}

	/**
	 * Returns the index of the first occurrence of each specified member in the list.
	 * <p>
	 * This operation corresponds to the Redis LPOS command with COUNT=1. It returns the index
	 * (zero-based) of the first occurrence of each element in the list. If an element does not
	 * exist in the list, its corresponding value in the result map will be null.
	 *
	 * @param members The collection of {@link OrangeValueExampleEntity} objects to find in the list
	 * @return A map with each entity as key and its index (position) in the list as value,
	 *         or null if the entity is not found
	 * @see <a href="https://redis.io/commands/lpos">Redis LPOS command</a>
	 */
	@Override
	default Map<OrangeValueExampleEntity, Long> getIndex(Collection<OrangeValueExampleEntity> members) {
		
		return null;
	}

	/**
	 * Returns the index of the last occurrence of each specified member in the list.
	 * <p>
	 * This operation corresponds to the Redis LPOS command with the RANK option set to -1.
	 * It returns the index (zero-based) of the last occurrence of each element in the list.
	 * If an element does not exist in the list, its corresponding value in the result map will be null.
	 *
	 * @param member The collection of {@link OrangeValueExampleEntity} objects to find in the list
	 * @return A map with each entity as key and the index of its last occurrence in the list as value,
	 *         or null if the entity is not found
	 * @see <a href="https://redis.io/commands/lpos">Redis LPOS command</a>
	 */
	@Override
	default Map<OrangeValueExampleEntity, Long> getLastIndex(Collection<OrangeValueExampleEntity> member) {
		
		return null;
	}

	/**
	 * Returns the element at the specified index in the list.
	 * <p>
	 * This operation corresponds to the Redis LINDEX command. It returns the element at index
	 * in the list. The index is zero-based, so 0 means the first element, 1 the second element
	 * and so on. Negative indices can be used to designate elements starting at the tail of the list.
	 * Here, -1 means the last element, -2 means the penultimate and so forth.
	 *
	 * @param index The index of the element to return (zero-based, can be negative)
	 * @return The {@link OrangeValueExampleEntity} at the specified position, or null if the index is out of bounds
	 * @see <a href="https://redis.io/commands/lindex">Redis LINDEX command</a>
	 */
	@Override
	default OrangeValueExampleEntity get(Long index) {
		
		return null;
	}

	/**
	 * Returns the elements within the specified index range in the list.
	 * <p>
	 * This operation corresponds to the Redis LRANGE command. It returns the specified elements
	 * of the list stored at the key. The offsets start and end are zero-based indexes, with 0 being
	 * the first element of the list, 1 being the next element and so on. These offsets can also be
	 * negative numbers indicating offsets starting at the end of the list, with -1 being the last element.
	 *
	 * @param start The starting index (inclusive, zero-based, can be negative)
	 * @param end The ending index (inclusive, zero-based, can be negative)
	 * @return A list of {@link OrangeValueExampleEntity} objects within the specified range,
	 *         or an empty list if the range is invalid or the list is empty
	 * @see <a href="https://redis.io/commands/lrange">Redis LRANGE command</a>
	 */
	@Override
	default List<OrangeValueExampleEntity> getByIndexRange(Long start, Long end) {
		
		return null;
	}

	/**
	 * Removes and returns the first element of the list.
	 * <p>
	 * This operation corresponds to the Redis LPOP command. It atomically returns and removes
	 * the first element of the list. If the list does not exist or is empty, null is returned.
	 *
	 * @return The first {@link OrangeValueExampleEntity} in the list, or null if the list is empty
	 * @see <a href="https://redis.io/commands/lpop">Redis LPOP command</a>
	 */
	@Override
	default OrangeValueExampleEntity leftPop() {
		
		return null;
	}

	/**
	 * Removes and returns the first count elements of the list.
	 * <p>
	 * This operation corresponds to the Redis LPOP command with the COUNT option.
	 * It atomically returns and removes the first count elements of the list.
	 * If the list contains less than count elements, all elements are returned.
	 *
	 * @param count The number of elements to pop from the left side of the list
	 * @return A list of {@link OrangeValueExampleEntity} objects that were popped,
	 *         or an empty list if the list is empty
	 * @see <a href="https://redis.io/commands/lpop">Redis LPOP command</a>
	 */
	@Override
	default List<OrangeValueExampleEntity> leftPop(Long count) {
		
		return null;
	}

	/**
	 * Blocks and waits for an element to be available, then removes and returns elements from the left side of the list.
	 * <p>
	 * This operation corresponds to the Redis BLPOP command. It is a blocking list pop primitive.
	 * It blocks the connection when there are no elements to pop from the list, until another client
	 * pushes to it or until the timeout is reached. If the timeout is reached, null is returned.
	 *
	 * @param value The timeout value
	 * @param unit The time unit of the timeout value
	 * @return A list of {@link OrangeValueExampleEntity} objects that were popped,
	 *         or null if the timeout is reached
	 * @see <a href="https://redis.io/commands/blpop">Redis BLPOP command</a>
	 */
	@Override
	default List<OrangeValueExampleEntity> leftPop(Long value, TimeUnit unit) {
		
		return null;
	}

	/**
	 * Removes and returns the last element of the list.
	 * <p>
	 * This operation corresponds to the Redis RPOP command. It atomically returns and removes
	 * the last element of the list. If the list does not exist or is empty, null is returned.
	 *
	 * @return The last {@link OrangeValueExampleEntity} in the list, or null if the list is empty
	 * @see <a href="https://redis.io/commands/rpop">Redis RPOP command</a>
	 */
	@Override
	default OrangeValueExampleEntity rightPop() {
		
		return null;
	}

	/**
	 * Removes and returns the last count elements of the list.
	 * <p>
	 * This operation corresponds to the Redis RPOP command with the COUNT option.
	 * It atomically returns and removes the last count elements of the list.
	 * If the list contains less than count elements, all elements are returned.
	 *
	 * @param count The number of elements to pop from the right side of the list
	 * @return A list of {@link OrangeValueExampleEntity} objects that were popped,
	 *         or an empty list if the list is empty
	 * @see <a href="https://redis.io/commands/rpop">Redis RPOP command</a>
	 */
	@Override
	default List<OrangeValueExampleEntity> rightPop(Long count) {
		
		return null;
	}

	/**
	 * Blocks and waits for an element to be available, then removes and returns elements from the right side of the list.
	 * <p>
	 * This operation corresponds to the Redis BRPOP command. It is a blocking list pop primitive.
	 * It blocks the connection when there are no elements to pop from the list, until another client
	 * pushes to it or until the timeout is reached. If the timeout is reached, null is returned.
	 *
	 * @param value The timeout value
	 * @param unit The time unit of the timeout value
	 * @return A list of {@link OrangeValueExampleEntity} objects that were popped,
	 *         or null if the timeout is reached
	 * @see <a href="https://redis.io/commands/brpop">Redis BRPOP command</a>
	 */
	@Override
	default List<OrangeValueExampleEntity> rightPop(Long value, TimeUnit unit) {
		
		return null;
	}

	/**
	 * Returns a random element from the list.
	 * <p>
	 * This operation corresponds to the Redis LRANDMEMBER command with COUNT=1.
	 * It returns a random element from the list without removing it.
	 * If the list is empty, null is returned.
	 *
	 * @return A random {@link OrangeValueExampleEntity} from the list, or null if the list is empty
	 * @see <a href="https://redis.io/commands/lrandmember">Redis LRANDMEMBER command</a>
	 */
	@Override
	default OrangeValueExampleEntity randomOne() {
		return null;
	}

	/**
	 * Returns multiple random elements from the list, with possible duplicates.
	 * <p>
	 * This operation corresponds to the Redis LRANDMEMBER command with a positive COUNT.
	 * It returns up to count random elements from the list without removing them.
	 * If count is greater than the list size, the entire list is returned.
	 * The same element may be returned multiple times.
	 *
	 * @param count The number of random elements to return
	 * @return A list of random {@link OrangeValueExampleEntity} objects from the list,
	 *         or an empty list if the list is empty
	 * @see <a href="https://redis.io/commands/lrandmember">Redis LRANDMEMBER command</a>
	 */
	@Override
	default List<OrangeValueExampleEntity> random(Long count) {
		return null;
	}

	/**
	 * Returns multiple distinct random elements from the list.
	 * <p>
	 * This operation corresponds to the Redis LRANDMEMBER command with a negative COUNT.
	 * It returns up to count distinct random elements from the list without removing them.
	 * If count is greater than the list size, the entire list is returned.
	 * Each element will appear at most once in the result.
	 *
	 * @param count The number of distinct random elements to return
	 * @return A list of distinct random {@link OrangeValueExampleEntity} objects from the list,
	 *         or an empty list if the list is empty
	 * @see <a href="https://redis.io/commands/lrandmember">Redis LRANDMEMBER command</a>
	 */
	@Override
	default List<OrangeValueExampleEntity> randomAndDistinct(Long count) {
		return null;
	}
	
	
	
}