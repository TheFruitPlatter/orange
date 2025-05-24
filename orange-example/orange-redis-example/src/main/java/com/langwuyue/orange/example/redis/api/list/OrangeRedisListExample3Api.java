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

import com.langwuyue.orange.redis.RedisValueTypeEnum;
import com.langwuyue.orange.redis.annotation.AddMembers;
import com.langwuyue.orange.redis.annotation.CAS;
import com.langwuyue.orange.redis.annotation.ContinueOnFailure;
import com.langwuyue.orange.redis.annotation.Count;
import com.langwuyue.orange.redis.annotation.Distinct;
import com.langwuyue.orange.redis.annotation.EndIndex;
import com.langwuyue.orange.redis.annotation.GetIndexs;
import com.langwuyue.orange.redis.annotation.GetMembers;
import com.langwuyue.orange.redis.annotation.GetSize;
import com.langwuyue.orange.redis.annotation.KeyVariable;
import com.langwuyue.orange.redis.annotation.Multiple;
import com.langwuyue.orange.redis.annotation.OrangeRedisKey;
import com.langwuyue.orange.redis.annotation.PopMembers;
import com.langwuyue.orange.redis.annotation.Random;
import com.langwuyue.orange.redis.annotation.RedisOldValue;
import com.langwuyue.orange.redis.annotation.RedisValue;
import com.langwuyue.orange.redis.annotation.RemoveMembers;
import com.langwuyue.orange.redis.annotation.StartIndex;
import com.langwuyue.orange.redis.annotation.Timeout;
import com.langwuyue.orange.redis.annotation.TimeoutUnit;
import com.langwuyue.orange.redis.annotation.TimeoutValue;
import com.langwuyue.orange.redis.annotation.global.Delete;
import com.langwuyue.orange.redis.annotation.global.GetExpiration;
import com.langwuyue.orange.redis.annotation.global.SetExpiration;
import com.langwuyue.orange.redis.annotation.list.Index;
import com.langwuyue.orange.redis.annotation.list.Left;
import com.langwuyue.orange.redis.annotation.list.OrangeRedisListClient;
import com.langwuyue.orange.redis.annotation.list.Pivot;
import com.langwuyue.orange.redis.annotation.list.Right;
import com.langwuyue.orange.redis.annotation.zset.Reverse;

/**
 * Advanced Redis List operations interface with dynamic keys and enhanced functionality.
 * 
 * <p>This interface extends the capabilities of basic Redis List operations by providing:
 * <ul>
 *   <li>Dynamic key generation using variables (via {@code ${var}} placeholder)</li>
 *   <li>Advanced atomic operations like Compare-And-Swap (CAS)</li>
 *   <li>Comprehensive positional operations (left/right/before/after)</li>
 *   <li>Blocking operations with timeout support</li>
 *   <li>Batch operations with failure handling options</li>
 *   <li>Enhanced index and search capabilities</li>
 * </ul>
 * 
 * <p>Key features:
 * <ul>
 *   <li><b>Dynamic Keys:</b> Create multiple lists with different keys using the {@code ${var}} placeholder</li>
 *   <li><b>Atomic Operations:</b> Support for CAS to ensure thread safety</li>
 *   <li><b>Flexible Positioning:</b> Insert elements at specific positions or relative to other elements</li>
 *   <li><b>Blocking Operations:</b> Wait for elements with configurable timeouts</li>
 *   <li><b>Batch Processing:</b> Process multiple elements with detailed success/failure reporting</li>
 *   <li><b>Expiration Control:</b> Fine-grained control over key expiration</li>
 * </ul>
 * 
 * <p>Key configuration:
 * <ul>
 *   <li>Dynamic key pattern: "orange:list:example3:${var}"</li>
 *   <li>Default expiration: 1 hour</li>
 *   <li>Value type: String</li>
 * </ul>
 * 
 * <p>Usage example:
 * <pre>{@code
 * // Create lists with different keys
 * api.leftPush("user123", "first action");
 * api.leftPush("user456", "another action");
 * 
 * // Atomic operations
 * boolean success = api.compareAndSwap("user123", 0L, "first action", "updated action");
 * 
 * // Batch operations with failure handling
 * Map<String, Boolean> results = api.leftPush("user123", 
 *     Arrays.asList("action1", "action2", "action3"));
 * 
 * // Blocking operations with timeout
 * List<String> items = api.leftPop("user123", 5L, TimeUnit.SECONDS);
 * 
 * // Advanced positional operations
 * api.leftPush("user123", "pivot", "before pivot");
 * api.rightPush("user123", "pivot", "after pivot");
 * 
 * // Index and search
 * Long index = api.getIndex("user123", "specific item");
 * Map<String, Long> indices = api.getIndex("user123", 
 *     Arrays.asList("item1", "item2"));
 * 
 * // Expiration management
 * api.setExpiration("user123", 30L, TimeUnit.MINUTES);
 * }</pre>
 * 
 * <p>Advanced use cases:
 * <ul>
 *   <li>User-specific action logs or activity streams</li>
 *   <li>Multi-tenant data with isolated storage</li>
 *   <li>High-concurrency environments requiring atomic operations</li>
 *   <li>Time-sensitive processing with blocking operations</li>
 *   <li>Complex workflows with position-specific insertions</li>
 * </ul>
 * 
 * @author Liang.Zhong
 * @since 1.0.0
 * @see OrangeRedisListClient List client annotation
 * @see OrangeRedisKey Key configuration with dynamic variables
 * @see OrangeRedisListExample1Api Basic JSON list operations
 * @see OrangeRedisListExample2Api Basic String list operations
 */
@OrangeRedisListClient(valueType = RedisValueTypeEnum.STRING)
@OrangeRedisKey(expirationTime = @Timeout(value = 1, unit = TimeUnit.HOURS), key = "orange:list:example3:${var}")
public interface OrangeRedisListExample3Api {
	
	/**
	 * Sets expiration time for the list
	 * @param keyVar Key variable for dynamic list identification
	 * @return true if expiration was set successfully
	 * @see <a href="https://redis.io/commands/expire">Redis EXPIRE command</a>
	 */
	@SetExpiration
	boolean setExpiration(@KeyVariable(name = "var") String keyVar);
	
	/**
	 * Deletes the entire list
	 * @param keyVar Key variable for dynamic list identification
	 * @return true if deletion was successful
	 * @see <a href="https://redis.io/commands/del">Redis DEL command</a>
	 */
	@Delete
	boolean delete(@KeyVariable(name = "var") String keyVar);
	
	/**
	 * Gets remaining time to live (TTL) in milliseconds
	 * @param keyVar Key variable for dynamic list identification
	 * @return Remaining time in milliseconds, or null if no expiration set
	 * @see <a href="https://redis.io/commands/ttl">Redis TTL command</a>
	 */
	@GetExpiration
	Long getExpiration(@KeyVariable(name = "var") String keyVar);
	
	/**
	 * Gets remaining time to live (TTL) in specified time unit
	 * @param keyVar Key variable for dynamic list identification
	 * @param unit Time unit for the return value
	 * @return Remaining time in specified unit, or null if no expiration set
	 * @see <a href="https://redis.io/commands/ttl">Redis TTL command</a>
	 */
	@GetExpiration
	Long getExpiration(@KeyVariable(name = "var") String keyVar,@TimeoutUnit TimeUnit unit);

	/**
	 * Sets the value of an element at the specified index in the list
	 * @param keyVar Key variable for dynamic list identification
	 * @param index Zero-based index at which to set the value
	 * @param member New value to be set at the specified position
	 * @see <a href="https://redis.io/commands/lset">Redis LSET command</a>
	 */
	@AddMembers
	void set(@KeyVariable(name = "var") String keyVar,@Index Long index, @RedisValue String member);
	
	/**
	 * Atomically replaces an element at the specified index if it matches the expected value
	 * @param keyVar Key variable for dynamic list identification
	 * @param index Zero-based index of the element to replace
	 * @param oldMember Expected current value at the specified index
	 * @param newMember New value to set if the current value matches oldMember
	 * @return true if the operation was successful (current value matched and was replaced)
	 * @see <a href="https://redis.io/commands/lset">Redis LSET command</a>
	 */
	@CAS
	Boolean compareAndSwap(@KeyVariable(name = "var") String keyVar,@Index Long index, @RedisOldValue String oldMember, @RedisValue String newMember);

	/**
	 * Inserts element at the head of the list
	 * @param keyVar Key variable for dynamic list identification
	 * @param member Element to insert
	 * @return Length of the list after the push operation
	 * @see <a href="https://redis.io/commands/lpush">Redis LPUSH command</a>
	 */
	@AddMembers
	@Left
	Long leftPush(@KeyVariable(name = "var") String keyVar,@RedisValue String member);
	
	/**
	 * Inserts an element before the specified pivot element in the list
	 * @param keyVar Key variable for dynamic list identification
	 * @param pivot Element before which the new element will be inserted
	 * @param member Element to insert
	 * @return Length of the list after the operation, or -1 if pivot was not found
	 * @see <a href="https://redis.io/commands/linsert">Redis LINSERT command</a>
	 */
	@AddMembers
	@Left
	Long leftPush(@KeyVariable(name = "var") String keyVar,@Pivot String pivot, @RedisValue String member);
	
	/**
	 * Inserts multiple elements at the head of the list with failure handling
	 * @param keyVar Key variable for dynamic list identification
	 * @param members Collection of elements to insert
	 * @return Map of elements to their insertion status (true for success, false for failure)
	 * @see <a href="https://redis.io/commands/lpush">Redis LPUSH command</a>
	 */
	@AddMembers
	@Left
	@ContinueOnFailure(true)
	Map<String,Boolean> leftPush(@KeyVariable(name = "var") String keyVar,@Multiple Collection<String> members);
	
	/**
	 * Inserts element at the tail of the list
	 * @param keyVar Key variable for dynamic list identification
	 * @param member Element to insert
	 * @return Length of the list after the push operation
	 * @see <a href="https://redis.io/commands/rpush">Redis RPUSH command</a>
	 */
	@AddMembers
	@Right
	Long rightPush(@KeyVariable(name = "var") String keyVar,@RedisValue String member);
	
	/**
	 * Inserts an element after the specified pivot element in the list
	 * @param keyVar Key variable for dynamic list identification
	 * @param pivot Element after which the new element will be inserted
	 * @param member Element to insert
	 * @return Length of the list after the operation, or -1 if pivot was not found
	 * @see <a href="https://redis.io/commands/linsert">Redis LINSERT command</a>
	 */
	@AddMembers
	@Right
	Long rightPush(@KeyVariable(name = "var") String keyVar,@Pivot String pivot, @RedisValue String member);
	
	/**
	 * Inserts multiple elements at the tail of the list with failure handling
	 * @param keyVar Key variable for dynamic list identification
	 * @param members Collection of elements to insert
	 * @return Map of elements to their insertion status (true for success, false for failure)
	 * @see <a href="https://redis.io/commands/rpush">Redis RPUSH command</a>
	 */
	@AddMembers
	@Right
	@ContinueOnFailure(true)
	Map<String,Boolean> rightPush(@KeyVariable(name = "var") String keyVar,@Multiple Collection<String> members);
	
	/**
	 * Gets the length of the list
	 * @param keyVar Key variable for dynamic list identification
	 * @return Number of elements in the list
	 * @see <a href="https://redis.io/commands/llen">Redis LLEN command</a>
	 */
	@GetSize
	Long getSize(@KeyVariable(name = "var") String keyVar);
	
	/**
	 * Gets the index of the first occurrence of the specified element in the list
	 * @param keyVar Key variable for dynamic list identification
	 * @param member Element to find
	 * @return Zero-based index of the first occurrence, or null if not found
	 * @see <a href="https://redis.io/commands/lpos">Redis LPOS command</a>
	 */
	@GetIndexs
	Long getIndex(@KeyVariable(name = "var") String keyVar,@RedisValue String member);
	
	/**
	 * Gets the indices of the first occurrences of multiple elements in the list
	 * @param keyVar Key variable for dynamic list identification
	 * @param members Collection of elements to find
	 * @return Map of elements to their first occurrence indices (null if not found)
	 * @see <a href="https://redis.io/commands/lpos">Redis LPOS command</a>
	 */
	@GetIndexs
	@ContinueOnFailure(true)
	Map<String, Long> getIndex(@KeyVariable(name = "var") String keyVar,@Multiple Collection<String> members);
	
	/**
	 * Gets the index of the last occurrence of the specified element in the list
	 * @param keyVar Key variable for dynamic list identification
	 * @param member Element to find
	 * @return Zero-based index of the last occurrence, or null if not found
	 * @see <a href="https://redis.io/commands/lpos">Redis LPOS command</a>
	 */
	@GetIndexs
	@Reverse
	Long getLastIndex(@KeyVariable(name = "var") String keyVar,@RedisValue String member);
	
	/**
	 * Gets the indices of the last occurrences of multiple elements in the list
	 * @param keyVar Key variable for dynamic list identification
	 * @param member Collection of elements to find
	 * @return Map of elements to their last occurrence indices (null if not found)
	 * @see <a href="https://redis.io/commands/lpos">Redis LPOS command</a>
	 */
	@GetIndexs
	@Reverse
	@ContinueOnFailure(true)
	Map<String, Long> getLastIndex(@KeyVariable(name = "var") String keyVar,@Multiple Collection<String> member);
	
	/**
	 * Gets the element at the specified index in the list
	 * @param keyVar Key variable for dynamic list identification
	 * @param index Zero-based index of the element to retrieve
	 * @return Element at the specified position, or null if index is out of bounds
	 * @see <a href="https://redis.io/commands/lindex">Redis LINDEX command</a>
	 */
	@GetMembers
	String get(@KeyVariable(name = "var") String keyVar,@Index Long index);
	
	/**
	 * Gets elements within the specified index range in the list
	 * @param keyVar Key variable for dynamic list identification
	 * @param start Start index (0-based, inclusive)
	 * @param end End index (0-based, inclusive, -1 for end of list)
	 * @return List of elements within the specified range
	 * @see <a href="https://redis.io/commands/lrange">Redis LRANGE command</a>
	 */
	@GetMembers
	List<String> getByIndexRange(@KeyVariable(name = "var") String keyVar,@StartIndex Long start, @EndIndex Long end);
	
	/**
	 * Removes and returns the first element of the list
	 * @param keyVar Key variable for dynamic list identification
	 * @return The first element of the list, or null if the list is empty
	 * @see <a href="https://redis.io/commands/lpop">Redis LPOP command</a>
	 */
	@PopMembers
	@Left
	String leftPop(@KeyVariable(name = "var") String keyVar);
	
	/**
	 * Removes and returns multiple elements from the head of the list
	 * @param keyVar Key variable for dynamic list identification
	 * @param count Maximum number of elements to pop
	 * @return List of popped elements, or empty list if the list is empty
	 * @see <a href="https://redis.io/commands/lpop">Redis LPOP command</a>
	 */
	@PopMembers
	@Left
	List<String> leftPop(@KeyVariable(name = "var") String keyVar,@Count Long count);

	/**
	 * Removes and returns the first element of the list, blocking until an element is available or timeout occurs
	 * @param keyVar Key variable for dynamic list identification
	 * @param value Maximum time to block
	 * @param unit Time unit for the timeout value
	 * @return List containing the popped element, or empty list if timeout occurs
	 * @see <a href="https://redis.io/commands/blpop">Redis BLPOP command</a>
	 */
	@PopMembers
	@Left
	List<String> leftPop(@KeyVariable(name = "var") String keyVar,@TimeoutValue Long value, @TimeoutUnit TimeUnit unit);
	
	/**
	 * Removes and returns the last element of the list
	 * @param keyVar Key variable for dynamic list identification
	 * @return The last element of the list, or null if the list is empty
	 * @see <a href="https://redis.io/commands/rpop">Redis RPOP command</a>
	 */
	@PopMembers
	@Right
	String rightPop(@KeyVariable(name = "var") String keyVar);
	
	/**
	 * Removes and returns multiple elements from the tail of the list
	 * @param keyVar Key variable for dynamic list identification
	 * @param count Maximum number of elements to pop
	 * @return List of popped elements, or empty list if the list is empty
	 * @see <a href="https://redis.io/commands/rpop">Redis RPOP command</a>
	 */
	@PopMembers
	@Right
	List<String> rightPop(@KeyVariable(name = "var") String keyVar,@Count Long count);

	/**
	 * Removes and returns the last element of the list, blocking until an element is available or timeout occurs
	 * @param keyVar Key variable for dynamic list identification
	 * @param value Maximum time to block
	 * @param unit Time unit for the timeout value
	 * @return List containing the popped element, or empty list if timeout occurs
	 * @see <a href="https://redis.io/commands/brpop">Redis BRPOP command</a>
	 */
	@PopMembers
	@Right
	List<String> rightPop(@KeyVariable(name = "var") String keyVar,@TimeoutValue Long value, @TimeoutUnit TimeUnit unit);
	
	/**
	 * Removes occurrences of the specified element from the list
	 * @param keyVar Key variable for dynamic list identification
	 * @param member Element to remove
	 * @param count Number of occurrences to remove: positive (from head to tail), negative (from tail to head), 0 (all occurrences)
	 * @return Number of removed elements
	 * @see <a href="https://redis.io/commands/lrem">Redis LREM command</a>
	 */
	@RemoveMembers
	Long remove(@KeyVariable(name = "var") String keyVar,@RedisValue String member, @Count Long count);
	
	/**
	 * Trims list to specified range (removes elements outside start-end)
	 * @param keyVar Key variable for dynamic list identification
	 * @param start Start index (0-based)
	 * @param end End index (0-based, -1 for end of list)
	 * @see <a href="https://redis.io/commands/ltrim">Redis LTRIM command</a>
	 */
	@RemoveMembers
	@Reverse
	void trim(@KeyVariable(name = "var") String keyVar,@StartIndex Long start, @EndIndex Long end);
	
	/**
	 * Gets one random element from the list
	 * @param keyVar Key variable for dynamic list identification
	 * @return List containing one random element
	 * @see <a href="https://redis.io/commands/lrandmember">Redis LRANDMEMBER command</a>
	 */
	@GetMembers
	@Random
	List<String> randomOne(@KeyVariable(name = "var") String keyVar);
	
	/**
	 * Gets multiple random elements from the list (may include duplicates)
	 * @param keyVar Key variable for dynamic list identification
	 * @param count Number of elements to return
	 * @return List of random elements (may contain duplicates)
	 * @see <a href="https://redis.io/commands/lrandmember">Redis LRANDMEMBER command</a>
	 */
	@GetMembers
	@Random
	List<String> random(@KeyVariable(name = "var") String keyVar,@Count Long count);
	
	/**
	 * Gets multiple unique random elements from the list
	 * @param keyVar Key variable for dynamic list identification
	 * @param count Number of distinct elements to return
	 * @return List of unique random elements
	 * @see <a href="https://redis.io/commands/lrandmember">Redis LRANDMEMBER command</a>
	 */
	@GetMembers
	@Random
	@Distinct
	List<String> randomAndDistinct(@KeyVariable(name = "var") String keyVar,@Count Long count);
	
}