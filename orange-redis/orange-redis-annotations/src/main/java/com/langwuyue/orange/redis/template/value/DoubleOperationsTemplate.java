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
 * Interface template for Redis Value operations. 
 * Developers should extend this interface and annotate the child interface with {@code OrangeRedisKey}.
 * 
 * <p>A example is:
 * <blockquote><pre>
 *  {@code @OrangeRedisKey(expirationTime = @Timeout(value = 1, unit = TimeUnit.HOURS), key = "orange:value:example1")} 
 *  public interface OrangeRedisValueExample1Api extends DoubleOperationsTemplate{
 *  
 *  }
 * </pre></blockquote>
 * 
 * 
 * <p>Please review examples for more information.
 * 
 * @author Liang.Zhong
 * @since 1.0.0
 */
@OrangeRedisValueClient(valueType = RedisValueTypeEnum.DOUBLE)
public interface DoubleOperationsTemplate extends GlobalOperationsTemplate {
	
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
	 * @param value the element value to add (serialized as Double)
	 * @return true if the value was added, false otherwise
	 */
	@SetValue
	@IfAbsent(deleteInTheEnd = false)
	Boolean init(@RedisValue Double value);
	
	/**
	 * Atomically compares and swaps the double value (CAS operation).
	 * <p>
	 * This operation provides compare-and-set semantics for floating-point numbers.
	 *
	 * <p>Key characteristics:
	 * <ul>
	 *   <li>O(1) time complexity</li>
   *   <li>Not truly atomic due to floating-point comparison limitations</li>
   *   <li>Returns true only if the value was changed</li>
   *   <li>More efficient than WATCH/MULTI for simple CAS scenarios</li>
	 * </ul>
	 *
	 * <p>Important considerations:
	 * <ul>
   *   <li>Uses exact equality comparison - may fail due to floating-point precision</li>
   *   <li>Not suitable for values that may have accumulated rounding errors</li>
   *   <li>For monetary values, consider using fixed-point arithmetic instead</li>
	 * </ul>
	 *
	 * <p>Operation semantics:
	 * <ul>
   *   <li>Compares current value with {@code oldValue} using exact equality</li>
   *   <li>If equal, sets to {@code newValue} and returns true</li>
   *   <li>If not equal, leaves unchanged and returns false</li>
	 * </ul>
	 *
	 * <p>Example usage:
	 * <pre>{@code
	 * Double current = getValue();
	 * if (compareAndSwap(current, current * 1.1)) { // 10% increase
	 *     // Update succeeded
	 * } else {
	 *     // Retry or handle contention
	 * }
	 * }</pre>
	 *
	 * @param oldValue the expected current value (exact match required)
	 * @param newValue the new value to set if verification succeeds
	 * @return true if the value was updated, false otherwise
	 */
	@CAS
	Boolean compareAndSwap(@RedisOldValue Double oldValue, @RedisValue Double newValue);
	
	/**
	 * Retrieves the double value from Redis.
	 * <p>
	 * This is the fundamental GET operation for double values in Redis.
	 *
	 * <p>Key characteristics:
	 * <ul>
	 *   <li>O(1) time complexity</li>
	 *   <li>Returns null if key does not exist</li>
	 *   <li>Supports IEEE 754 double-precision floating-point numbers</li>
	 *   <li>Values are stored as strings in Redis and converted to Java Double</li>
	 *   <li>Precision may be affected by string conversion</li>
	 * </ul>
	 *
	 * <p>Typical use cases:
	 * <ul>
	 *   <li>Retrieving floating-point metrics and measurements</li>
	 *   <li>Reading percentage values</li>
	 *   <li>Getting floating-point configuration values</li>
	 * </ul>
	 *
	 * <p>Example usage:
	 * <pre>{@code
	 * Double temperature = getValue();
	 * if (temperature != null) {
	 *     // Use the floating-point value
	 * } else {
	 *     // Initialize value
	 * }
	 * }</pre>
	 *
	 * @return the double value, or null if key does not exist
	 */
	@GetValue
	Double getValue();
	
	/**
	 * Increments the value by given delta and returns the new value.
	 * <p>
	 * This operation performs floating-point addition in Redis.
	 *
	 * <p>Key characteristics:
	 * <ul>
	 *   <li>O(1) time complexity</li>
	 *   <li>Not atomic - may have race conditions in concurrent scenarios</li>
	 *   <li>Returns the new value after increment</li>
	 *   <li>If key doesn't exist, initializes to 0.0 before incrementing</li>
	 *   <li>May have precision loss due to floating-point arithmetic</li>
	 * </ul>
	 *
	 * <p>Precision considerations:
	 * <ul>
	 *   <li>Uses Redis INCRBYFLOAT command internally</li>
	 *   <li>Values are stored as strings with up to 17 significant digits</li>
	 *   <li>Rounding may occur during arithmetic operations</li>
	 * </ul>
	 *
	 * <p>Example usage:
	 * <pre>{@code
	 * // Increase temperature reading by 0.5 degrees
	 * Double newTemp = increment(0.5);
	 * }</pre>
	 *
	 * @param delta the value to increment by (positive or negative)
	 * @return the new value after increment
	 */
	@Increment
	Double increment(@RedisValue Double delta);
	
	/**
	 * Decrements the value by given delta and returns the new value.
	 * <p>
	 * This operation performs floating-point subtraction in Redis.
	 *
	 * <p>Key characteristics:
	 * <ul>
	 *   <li>O(1) time complexity</li>
	 *   <li>Not atomic - may have race conditions in concurrent scenarios</li>
	 *   <li>Returns the new value after decrement</li>
	 *   <li>If key doesn't exist, initializes to 0.0 before decrementing</li>
	 *   <li>May have precision loss due to floating-point arithmetic</li>
	 * </ul>
	 *
	 * <p>Precision considerations:
	 * <ul>
	 *   <li>Uses Redis INCRBYFLOAT command internally (with negative delta)</li>
	 *   <li>Values are stored as strings with up to 17 significant digits</li>
	 *   <li>Rounding may occur during arithmetic operations</li>
	 * </ul>
	 *
	 * <p>Example usage:
	 * <pre>{@code
	 * // Reduce account balance by 100.50
	 * Double newBalance = decrement(100.50);
	 * }</pre>
	 *
	 * @param delta the value to decrement by (positive or negative)
	 * @return the new value after decrement
	 */
	@Decrement
	Double decrement(@RedisValue Double delta);
}