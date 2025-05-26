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
package com.langwuyue.orange.redis.template.value;

import com.langwuyue.orange.redis.RedisValueTypeEnum;
import com.langwuyue.orange.redis.annotation.CAS;
import com.langwuyue.orange.redis.annotation.Decrement;
import com.langwuyue.orange.redis.annotation.IfAbsent;
import com.langwuyue.orange.redis.annotation.Increment;
import com.langwuyue.orange.redis.annotation.RedisOldValue;
import com.langwuyue.orange.redis.annotation.RedisValue;
import com.langwuyue.orange.redis.annotation.value.GetValue;
import com.langwuyue.orange.redis.annotation.value.OrangeRedisValueClient;
import com.langwuyue.orange.redis.annotation.value.SetValue;
import com.langwuyue.orange.redis.template.global.GlobalOperationsTemplate;

/**
 * Interface template for Redis Long value operations with atomic counters.
 * <p>
 * This template provides thread-safe operations for Redis Long values,
 * supporting atomic increments, decrements, and compare-and-swap operations.
 *
 * <p>Key characteristics:
 * <ul>
 *   <li>Thread-safe: All operations are safe for concurrent use</li>
 *   <li>Non-blocking: Operations do not wait for other threads</li>
 *   <li>Atomic counters: Supports INCR, DECR, INCRBY, DECRBY commands</li>
 *   <li>CAS operations: Atomic compare-and-swap support</li>
 * </ul>
 *
 * <p>Performance considerations:
 * <ul>
 *   <li>All operations are O(1) time complexity</li>
 *   <li>Atomic operations avoid explicit locking</li>
 *   <li>Supports 64-bit signed integers (-2^63 to 2^63-1)</li>
 *   <li>Overflow protection for increment/decrement operations</li>
 * </ul>
 *
 * <p>Implementation requirements:
 * <ul>
 *   <li>Child interfaces must be annotated with {@code @OrangeRedisKey}</li>
 *   <li>Values must be valid 64-bit signed integers</li>
 *   <li>NaN and Infinity values are not supported</li>
 * </ul>
 *
 * <p>Example implementation:
 * <blockquote><pre>
 * {@code @OrangeRedisKey(expirationTime = @Timeout(value = 1, unit = TimeUnit.HOURS), key = "orange:value:example1")} 
 * public interface VisitCounterApi extends LongOperationsTemplate {
 *     // Custom operations can be added here
 * }
 * </pre></blockquote>
 *
 *
 * @author Liang.Zhong
 * @since 1.0.0
 * @see GlobalOperationsTemplate
 * @see <a href="https://redis.io/commands/incr">Redis INCR command</a>
 * @see <a href="https://redis.io/commands/decr">Redis DECR command</a>
 */
@OrangeRedisValueClient(valueType = RedisValueTypeEnum.LONG)
public interface LongOperationsTemplate extends GlobalOperationsTemplate {
	
	
	/**
	 * Set a value only if it does not already exist.
	 * <p>
	 * Triggers the {@code OrangeRedisValueSetIfAbsentListener} upon completion.
	 * The listener implementation must be annotated with Spring's {@code @Component}.
	 *
	 * <p>Configuration options:
	 * <ul>
	 *   <li>{@code deleteInTheEnd=true}: Removes the element after operation</li>
	 *   <li>{@code deleteInTheEnd=false}: Keeps the element after operation</li>
	 * </ul>
	 *
	 * @param value the element value to add (serialized as Long)
	 * @return true if the value was added, false otherwise
	 */
	@SetValue
	@IfAbsent(deleteInTheEnd = false)
	Boolean init(@RedisValue Long value);
	
	/**
	 * Atomically compares and swaps the value (CAS operation).
	 * <p>
	 * This operation provides atomic compare-and-set semantics, similar to
	 * {@link java.util.concurrent.atomic.AtomicLong#compareAndSet}.
	 *
	 * <p>Key characteristics:
	 * <ul>
	 *   <li>O(1) time complexity</li>
	 *   <li>Fully atomic operation - no need for external locking</li>
	 *   <li>Returns true only if the value was changed</li>
	 *   <li>More efficient than WATCH/MULTI for simple CAS scenarios</li>
	 * </ul>
	 *
	 * <p>Operation semantics:
	 * <ul>
	 *   <li>Compares current value with {@code oldValue}</li>
	 *   <li>If equal, sets to {@code newValue} and returns true</li>
	 *   <li>If not equal, leaves unchanged and returns false</li>
	 * </ul>
	 *
	 * <p>Typical use cases:
	 * <ul>
	 *   <li>Optimistic locking strategies</li>
	 *   <li>Atomic state transitions</li>
	 *   <li>Race condition prevention</li>
	 *   <li>Distributed counter updates</li>
	 * </ul>
	 *
	 * <p>Example usage:
	 * <pre>{@code
	 * Long current = getValue();
	 * if (compareAndSwap(current, current + 1)) {
	 *     // Update succeeded
	 * } else {
	 *     // Retry or handle contention
	 * }
	 * }</pre>
	 *
	 * @param oldValue the expected current value
	 * @param newValue the new value to set if verification succeeds
	 * @return true if the value was updated, false otherwise
	 */
	@CAS
	Boolean compareAndSwap(@RedisOldValue Long oldValue, @RedisValue Long newValue);
	
	/**
	 * Retrieves the long value from Redis.
	 * <p>
	 * This is the fundamental GET operation for long values in Redis.
	 *
	 * <p>Key characteristics:
	 * <ul>
	 *   <li>O(1) time complexity</li>
	 *   <li>Returns null if key does not exist</li>
	 *   <li>Supports 64-bit signed integers (-2^63 to 2^63-1)</li>
	 *   <li>Values are stored and returned as Java Long objects</li>
	 * </ul>
	 *
	 * <p>Typical use cases:
	 * <ul>
	 *   <li>Retrieving counters and statistics</li>
	 *   <li>Reading numeric configuration values</li>
	 *   <li>Getting numeric IDs or sequence numbers</li>
	 * </ul>
	 *
	 * <p>Example usage:
	 * <pre>{@code
	 * Long visitCount = getValue();
	 * if (visitCount != null) {
	 *     // Use the numeric value
	 * } else {
	 *     // Initialize counter
	 * }
	 * }</pre>
	 *
	 * @return the long value, or null if key does not exist
	 */
	@GetValue
	Long getValue();
	
	/**
	 * Atomically increments the value by 1 and returns the new value.
	 * <p>
	 * This operation uses Redis's INCR command which is atomic and thread-safe.
	 *
	 * <p>Key characteristics:
	 * <ul>
	 *   <li>O(1) time complexity</li>
	 *   <li>Atomic operation - safe for concurrent access</li>
	 *   <li>Returns the new value after increment</li>
	 *   <li>If key doesn't exist, initializes to 0 before incrementing (returns 1)</li>
	 * </ul>
	 *
	 * <p>Example usage:
	 * <pre>{@code
	 * Long newCount = increment(); // Thread-safe counter increment
	 * }</pre>
	 *
	 * @return the new value after increment
	 */
	@Increment
	Long increment();
	
	/**
	 * Atomically increments the value by given delta and returns the new value.
	 * <p>
	 * This operation uses Redis's INCRBY command which is atomic and thread-safe.
	 *
	 * <p>Key characteristics:
	 * <ul>
	 *   <li>O(1) time complexity</li>
	 *   <li>Atomic operation - safe for concurrent access</li>
	 *   <li>Returns the new value after increment</li>
	 *   <li>If key doesn't exist, initializes to 0 before incrementing</li>
	 * </ul>
	 *
	 * <p>Example usage:
	 * <pre>{@code
	 * // Increase inventory count by 5
	 * Long newStock = increment(5L);
	 * }</pre>
	 *
	 * @param delta the value to increment by (positive or negative)
	 * @return the new value after increment
	 */
	@Increment
	Long increment(@RedisValue Long delta);
	
	/**
	 * Atomically decrements the value by 1 and returns the new value.
	 * <p>
	 * This operation uses Redis's DECR command which is atomic and thread-safe.
	 *
	 * <p>Key characteristics:
	 * <ul>
	 *   <li>O(1) time complexity</li>
	 *   <li>Atomic operation - safe for concurrent access</li>
	 *   <li>Returns the new value after decrement</li>
	 *   <li>If key doesn't exist, initializes to 0 before decrementing (returns -1)</li>
	 * </ul>
	 *
	 * <p>Example usage:
	 * <pre>{@code
	 * Long remaining = decrement(); // Thread-safe counter decrement
	 * }</pre>
	 *
	 * @return the new value after decrement
	 */
	@Decrement
	Long decrement();
	
	/**
	 * Atomically decrements the value by given delta and returns the new value.
	 * <p>
	 * This operation uses Redis's DECRBY command which is atomic and thread-safe.
	 *
	 * <p>Key characteristics:
	 * <ul>
	 *   <li>O(1) time complexity</li>
	 *   <li>Atomic operation - safe for concurrent access</li>
	 *   <li>Returns the new value after decrement</li>
	 *   <li>If key doesn't exist, initializes to 0 before decrementing</li>
	 * </ul>
	 *
	 * <p>Example usage:
	 * <pre>{@code
	 * // Reduce inventory count by 3
	 * Long remainingStock = decrement(3L);
	 * }</pre>
	 *
	 * @param delta the value to decrement by (positive or negative)
	 * @return the new value after decrement
	 */
	@Decrement
	Long decrement(@RedisValue Long delta);
}