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
package com.langwuyue.orange.redis.context;

import java.util.function.BiConsumer;

/**
 * Interface for Redis contexts that support iteration over multiple elements.
 * 
 * <p>This interface defines the contract for Redis operation contexts that need to
 * process multiple elements in batch or sequence. It's particularly useful for
 * operations that work with collections of values, such as:
 * <ul>
 *   <li>Multi-key operations (MGET, MSET)</li>
 *   <li>Collection operations (SADD, LPUSH, ZADD)</li>
 *   <li>Batch processing of multiple Redis commands</li>
 * </ul>
 * 
 * <p>Implementations of this interface provide methods to iterate through elements,
 * convert them to arrays, and define failure handling behavior.
 *
 * @author Liang.Zhong
 * @since 1.0.0
 */
public interface OrangeRedisIterableContext {
	
	/**
	 * Performs the given action for each element in this context.
	 * 
	 * <p>The action is performed on each element in sequence. The BiConsumer receives
	 * two parameters for each element, typically the key and value or index and value,
	 * depending on the specific Redis operation context.
	 * 
	 * <p>If {@link #continueOnFailure()} returns true, the iteration will continue
	 * even if the action throws an exception for some elements. Otherwise, the first
	 * exception will halt the iteration.
	 *
	 * @param action the action to be performed for each element
	 * @throws NullPointerException if the specified action is null
	 * @throws RuntimeException if an error occurs during iteration and 
	 *         {@link #continueOnFailure()} returns false
	 */
	void forEach(BiConsumer action);
	
	/**
	 * Returns an array containing all elements in this context.
	 * 
	 * <p>The returned array contains all elements in their iteration order. The array's
	 * runtime component type is Object. This method acts as a bridge between array-based
	 * and collection-based APIs.
	 *
	 * @return an array containing all elements in this context
	 */
	Object[] toArray();
	
	/**
	 * Indicates whether operations should continue when an error occurs.
	 * 
	 * <p>This method determines the behavior when an error occurs during iteration:
	 * <ul>
	 *   <li>If true: continue processing remaining elements even if some operations fail</li>
	 *   <li>If false: stop processing immediately when the first error occurs</li>
	 * </ul>
	 * 
	 * <p>This is particularly useful for batch operations where you might want to:
	 * <ul>
	 *   <li>Continue processing other elements even if some fail (e.g., bulk inserts)</li>
	 *   <li>Stop immediately on any error to maintain consistency (e.g., atomic operations)</li>
	 * </ul>
	 *
	 * @return true if operations should continue after errors, false if they should stop
	 */
	boolean continueOnFailure();
}