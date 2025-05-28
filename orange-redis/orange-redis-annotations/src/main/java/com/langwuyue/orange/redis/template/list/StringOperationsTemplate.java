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
package com.langwuyue.orange.redis.template.list;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import com.langwuyue.orange.redis.RedisValueTypeEnum;
import com.langwuyue.orange.redis.annotation.list.OrangeRedisListClient;

/**
 * Interface template for Redis List operations with String values.
 * This template extends {@link JSONOperationsTemplate} and provides optimized
 * operations for String-based Redis lists. It eliminates the need for JSON
 * serialization/deserialization, offering better performance for String values.
 * 
 * <p>Key features:
 * <ul>
 *   <li>Direct String value storage</li>
 *   <li>No serialization overhead</li>
 *   <li>Bi-directional list operations</li>
 *   <li>Batch operations support</li>
 *   <li>Blocking operations with timeout</li>
 *   <li>Random element selection</li>
 * </ul>
 * 
 * <p>Implementation notes:
 * <ul>
 *   <li>Uses Redis STRING data type for storage</li>
 *   <li>All operations are atomic</li>
 *   <li>Thread-safe operations</li>
 *   <li>Automatic connection management</li>
 *   <li>Default implementations return null</li>
 * </ul>
 * 
 * @author Liang.Zhong
 * @since 1.0.0
 * @see JSONOperationsTemplate
 * @see com.langwuyue.orange.redis.annotation.list.OrangeRedisListClient
 * @see com.langwuyue.orange.redis.annotation.OrangeRedisKey
 */
@OrangeRedisListClient(valueType = RedisValueTypeEnum.STRING)
public interface StringOperationsTemplate extends JSONOperationsTemplate<String> {

	/**
	 * Pushes multiple String elements to the head (left) of the list.
	 * This method provides a default implementation that returns null.
	 * 
	 * @param members collection of String elements to add
	 * @return map indicating success/failure for each element
	 */
	@Override
	default Map<String, Boolean> leftPush(Collection<String> members) {
		return null;
	}

	/**
	 * Pushes multiple String elements to the tail (right) of the list.
	 * This method provides a default implementation that returns null.
	 * 
	 * @param members collection of String elements to add
	 * @return map indicating success/failure for each element
	 */
	@Override
	default Map<String, Boolean> rightPush(Collection<String> members) {
		return null;
	}

	/**
	 * Gets the first index for each specified String element in the list.
	 * This method provides a default implementation that returns null.
	 * 
	 * @param members collection of String elements to search for
	 * @return map of elements to their first indices
	 */
	@Override
	default Map<String, Long> getIndex(Collection<String> members) {
		return null;
	}

	/**
	 * Gets the last index for each specified String element in the list.
	 * This method provides a default implementation that returns null.
	 * 
	 * @param member collection of String elements to search for
	 * @return map of elements to their last indices
	 */
	@Override
	default Map<String, Long> getLastIndex(Collection<String> member) {
		return null;
	}

	/**
	 * Gets the String element at the specified index.
	 * This method provides a default implementation that returns null.
	 * 
	 * @param index the index to retrieve
	 * @return the String element at the index
	 */
	@Override
	default String get(Long index) {
		return null;
	}

	/**
	 * Gets a range of String elements from the list.
	 * This method provides a default implementation that returns null.
	 * 
	 * @param start the starting index (inclusive)
	 * @param end the ending index (inclusive)
	 * @return list of String elements in the range
	 */
	@Override
	default List<String> getByIndexRange(Long start, Long end) {
		return null;
	}

	/**
	 * Removes and returns the first String element from the list.
	 * This method provides a default implementation that returns null.
	 * 
	 * @return the removed String element
	 */
	@Override
	default String leftPop() {
		return null;
	}

	/**
	 * Removes and returns multiple String elements from the head of the list.
	 * This method provides a default implementation that returns null.
	 * 
	 * @param count number of elements to remove
	 * @return list of removed String elements
	 */
	@Override
	default List<String> leftPop(Long count) {
		return null;
	}

	/**
	 * Blocks until a String element is available to pop from the head of the list.
	 * This method provides a default implementation that returns null.
	 * 
	 * @param value timeout duration
	 * @param unit timeout time unit
	 * @return list containing the removed String element
	 */
	@Override
	default List<String> leftPop(Long value, TimeUnit unit) {
		return null;
	}

	/**
	 * Removes and returns the last String element from the list.
	 * This method provides a default implementation that returns null.
	 * 
	 * @return the removed String element
	 */
	@Override
	default String rightPop() {
		return null;
	}

	/**
	 * Removes and returns multiple String elements from the tail of the list.
	 * This method provides a default implementation that returns null.
	 * 
	 * @param count number of elements to remove
	 * @return list of removed String elements
	 */
	@Override
	default List<String> rightPop(Long count) {
		return null;
	}

	/**
	 * Blocks until a String element is available to pop from the tail of the list.
	 * This method provides a default implementation that returns null.
	 * 
	 * @param value timeout duration
	 * @param unit timeout time unit
	 * @return list containing the removed String element
	 */
	@Override
	default List<String> rightPop(Long value, TimeUnit unit) {
		return null;
	}
	
	/**
	 * Gets a random String element from the list.
	 * This method provides a default implementation that returns null.
	 * 
	 * @return a random String element
	 */
	@Override
	default String randomOne() {
		return null;
	}

	/**
	 * Gets multiple random String elements from the list.
	 * This method provides a default implementation that returns null.
	 * 
	 * @param count number of random elements to get
	 * @return list of random String elements
	 */
	@Override
	default List<String> random(Long count) {
		return null;
	}

	/**
	 * Gets multiple distinct random String elements from the list.
	 * This method provides a default implementation that returns null.
	 * 
	 * @param count number of distinct random elements to get
	 * @return list of distinct random String elements
	 */
	@Override
	default List<String> randomAndDistinct(Long count) {
		return null;
	}
}