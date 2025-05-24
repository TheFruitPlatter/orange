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
package com.langwuyue.orange.example.redis.api.set;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import com.langwuyue.orange.redis.RedisValueTypeEnum;
import com.langwuyue.orange.redis.annotation.AddMembers;
import com.langwuyue.orange.redis.annotation.CAS;
import com.langwuyue.orange.redis.annotation.ContinueOnFailure;
import com.langwuyue.orange.redis.annotation.Count;
import com.langwuyue.orange.redis.annotation.Distinct;
import com.langwuyue.orange.redis.annotation.GetMembers;
import com.langwuyue.orange.redis.annotation.GetSize;
import com.langwuyue.orange.redis.annotation.IfAbsent;
import com.langwuyue.orange.redis.annotation.KeyVariable;
import com.langwuyue.orange.redis.annotation.Multiple;
import com.langwuyue.orange.redis.annotation.OrangeRedisKey;
import com.langwuyue.orange.redis.annotation.PopMembers;
import com.langwuyue.orange.redis.annotation.Random;
import com.langwuyue.orange.redis.annotation.RedisOldValue;
import com.langwuyue.orange.redis.annotation.RedisValue;
import com.langwuyue.orange.redis.annotation.RemoveMembers;
import com.langwuyue.orange.redis.annotation.ScanPattern;
import com.langwuyue.orange.redis.annotation.Timeout;
import com.langwuyue.orange.redis.annotation.TimeoutUnit;
import com.langwuyue.orange.redis.annotation.global.Delete;
import com.langwuyue.orange.redis.annotation.global.GetExpiration;
import com.langwuyue.orange.redis.annotation.global.SetExpiration;
import com.langwuyue.orange.redis.annotation.set.IsMembers;
import com.langwuyue.orange.redis.annotation.set.OrangeRedisSetClient;
import com.langwuyue.orange.redis.annotation.zset.PageNo;

/**
 * Redis Set Operations API Example 3 - Variable Key Operations
 * 
 * <p>This interface provides Redis Set operations with variable key names.
 * It supports all basic Set operations including add, remove, random get, 
 * membership check, and more.
 * 
 * <p>Supported Redis commands: SADD, SCARD, SISMEMBER, SMEMBERS, 
 * SPOP, SRANDMEMBER, SREM, SSCAN
 * 
 * <p>Key configuration:
 * <ul>
 *   <li>Key pattern: "orange:set:example3:${var}"</li>
 *   <li>Default expiration: 1 hour</li>
 *   <li>Value type: String</li>
 * </ul>
 * 
 * 
 * <p>Example usage:
 * <pre>{@code
 * // Add single member
 * api.add("myKey", "member1");
 * 
 * // Check membership
 * boolean exists = api.isMember("myKey", "member1");
 * }</pre>
 * 
 * 
 * @author Liang.Zhong
 * @since 1.0.0
 */
@OrangeRedisSetClient(valueType = RedisValueTypeEnum.STRING)
@OrangeRedisKey(expirationTime = @Timeout(value = 1, unit = TimeUnit.HOURS), key = "orange:set:example3:${var}")
public interface OrangeRedisSetExample3Api {
	
	/**
	 * Sets expiration time for the Set key
	 * 
	 * <p>Corresponding Redis command: EXPIRE
	 * 
	 * @param var The variable part of the key name
	 * @return true if the timeout was set, false if key does not exist
	 */
	@SetExpiration
	boolean setExpiration(@KeyVariable(name = "var") String var);
	
	/**
	 * Deletes the Set key from Redis
	 * 
	 * <p>Corresponding Redis command: DEL
	 * 
	 * @param var The variable part of the key name
	 * @return true if the key was deleted, false if key did not exist
	 */
	@Delete
	boolean delete(@KeyVariable(name = "var") String var);
	
	/**
	 * Gets the remaining expiration time of the Set key in seconds
	 * 
	 * <p>Corresponding Redis command: TTL
	 * 
	 * @param var The variable part of the key name
	 * @return Remaining time in seconds, -1 if no expiration, -2 if key doesn't exist
	 */
	@GetExpiration
	Long getExpiration(@KeyVariable(name = "var") String var);
	
	/**
	 * Gets the remaining expiration time of the Set key in specified time unit
	 * 
	 * <p>Corresponding Redis command: TTL (converted to requested unit)
	 * 
	 * @param var The variable part of the key name
	 * @param unit The time unit for the return value
	 * @return Remaining time in requested unit, -1 if no expiration, -2 if key doesn't exist
	 */
	@GetExpiration
	Long getExpiration(@KeyVariable(name = "var") String var,@TimeoutUnit TimeUnit unit);

	
	/**
	 * Adds a single member to the Set
	 * 
	 * <p>Corresponding Redis command: SADD
	 * 
	 * @param var The variable part of the key name
	 * @param value The member value to add
	 * @return true if the member was added, false if it already existed
	 */
	@AddMembers
	Boolean add(@KeyVariable(name = "var") String var,@RedisValue String value);
	
	/**
	 * Adds multiple members to the Set in batch
	 * 
	 * <p>Corresponding Redis command: SADD (multiple executions)
	 * 
	 * <p>This operation will continue on failure due to the @ContinueOnFailure(true) annotation.
	 * If some members fail to be added, the operation will continue with remaining members.
	 * 
	 * @param var The variable part of the key name
	 * @param members Set of members to add
	 * @return Map containing each member and its add result (true=added, false=already existed)
	 */
	@AddMembers
	@ContinueOnFailure(true)
	Map<String, Boolean> add(@KeyVariable(name = "var") String var,@Multiple Set<String> members);
	
	/**
	 * Adds a member to the Set only if it does not already exist (atomic operation)
	 * 
	 * <p>This is an atomic operation guaranteed by the @IfAbsent annotation.
	 * The member will only be added if the Set doesn't already contain it.
	 * 
	 * <p>Note: This method has no return value. If the member already exists,
	 * the operation will silently fail (no exception thrown).
	 * 
	 * @param var The variable part of the key name
	 * @param value The member value to add
	 */
	@AddMembers
	@IfAbsent(deleteInTheEnd=false)
	void addIfAbsent(@KeyVariable(name = "var") String var,@RedisValue String value);
	
	/**
	 * Adds multiple members to the Set only if they do not already exist (batch atomic operation)
	 * 
	 * <p>This batch operation applies the same atomic guarantee as the single member version,
	 * with the addition of @ContinueOnFailure support.
	 * 
	 * <p>If some members already exist, the operation will continue processing remaining members
	 * rather than failing the entire batch.
	 * 
	 * <p>Note: This method has no return value. Existing members will be silently skipped.
	 * 
	 * @param var The variable part of the key name
	 * @param members Set of members to add
	 */
	@AddMembers
	@IfAbsent(deleteInTheEnd=false)
	@ContinueOnFailure(true)
	void addIfAbsent(@KeyVariable(name = "var") String var,@Multiple Set<String> members);
	
	/**
	 * Atomically compares the current member with expected value and swaps if they match
	 * 
	 * <p>Corresponding Redis pattern: WATCH/MULTI/EXEC with conditional SADD/SREM
	 * 
	 * <p>This operation provides atomic compare-and-swap (CAS) semantics:
	 * <ul>
	 *   <li>If current member matches oldValue, replaces with newValue and returns true</li>
	 *   <li>If current member differs, leaves unchanged and returns false</li>
	 * </ul>
	 * 
	 * @param var The variable part of the key name
	 * @param oldValue The expected current member value
	 * @param newValue The new member value to set if comparison succeeds
	 * @return true if swap occurred, false otherwise
	 */
	@CAS
	Boolean compareAndSwap(@KeyVariable(name = "var") String var,@RedisOldValue String oldValue, @RedisValue String newValue);
	
	/**
	 * Gets a random member from the Set
	 * 
	 * <p>Corresponding Redis command: SRANDMEMBER
	 * 
	 * @param var The variable part of the key name
	 * @return A random member, or null if Set is empty
	 */
	@GetMembers
	@Random
	String randomGetOne(@KeyVariable(name = "var") String var);
	
	/**
	 * Gets multiple random members from the Set (may contain duplicates)
	 * 
	 * <p>Corresponding Redis command: SRANDMEMBER with count
	 * 
	 * <p>When count is positive, returned members may be duplicated if Set size is smaller than count
	 * 
	 * @param var The variable part of the key name
	 * @param count Number of random members to return
	 * @return List of random members (may contain duplicates), empty list if Set is empty
	 */
	@GetMembers
	@Random
	List<String> randomGetMembers(@KeyVariable(name = "var") String var,@Count Long count);
	
	/**
	 * Gets multiple distinct random members from the Set
	 * 
	 * <p>Corresponding Redis pattern: Multiple SRANDMEMBER calls with deduplication
	 * 
	 * <p>When count exceeds Set size, returns all members in random order
	 * 
	 * @param var The variable part of the key name
	 * @param count Number of distinct random members to return
	 * @return Set of distinct random members, empty set if Set is empty
	 */
	@GetMembers
	@Distinct
	@Random
	Set<String> distinctRandomGetMembers(@KeyVariable(name = "var") String var,@Count Long count);
	
	/**
	 * Gets all members of the Set.
	 * Corresponding Redis command: SMEMBERS.
	 *
	 * @param var the variable part of the key name
	 * @return Set containing all members, empty set if key doesn't exist
	 * @throws RedisException if Redis operation fails
	 */
	@GetMembers
	Set<String> getMembers(@KeyVariable(name = "var") String var);
	
	/**
	 * Checks if a value is a member of the Set.
	 * Corresponding Redis command: SISMEMBER.
	 *
	 * @param var the variable part of the key name
	 * @param member the value to check
	 * @return true if value is a member, false otherwise
	 * @throws RedisException if Redis operation fails
	 */
	@IsMembers
	Boolean isMember(@KeyVariable(name = "var") String var,@RedisValue String member);
	
	/**
	 * Checks if multiple values are members of the Set in a batch operation.
	 * Uses multiple SISMEMBER commands internally.
	 *
	 * @param var the variable part of the key name
	 * @param members set of values to check
	 * @return map where key is the member and value indicates membership status (true=is member)
	 * @throws RedisException if Redis operation fails
	 */
	@IsMembers
	Map<String, Boolean> isMembers(@KeyVariable(name = "var") String var,@Multiple Set<String> members);
	
	/**
	 * Removes and returns a random member from the Set.
	 * Corresponding Redis command: SPOP.
	 *
	 * @param var the variable part of the key name
	 * @return the removed member, or null if Set is empty
	 * @throws RedisException if Redis operation fails
	 */
	@PopMembers
	String pop(@KeyVariable(name = "var") String var);
	
	/**
	 * Removes and returns multiple random members from the Set.
	 * Corresponding Redis command: SPOP with count parameter.
	 *
	 * @param var the variable part of the key name
	 * @param count the maximum number of members to remove and return
	 * @return set of removed members (size may be less than count if Set is smaller), empty set if Set is empty
	 * @throws RedisException if Redis operation fails
	 * @throws IllegalArgumentException if count is negative
	 */
	@PopMembers
	Set<String> pop(@KeyVariable(name = "var") String var,@Count Long count);
	
	/**
	 * Gets the number of members in the Set.
	 * Corresponding Redis command: SCARD.
	 *
	 * @param var the variable part of the key name
	 * @return the cardinality (number of elements) of the Set, 0 if key does not exist
	 * @throws RedisException if Redis operation fails
	 */
	@GetSize
	Long getSize(@KeyVariable(name = "var") String var);
	
	/**
	 * Removes the specified member from the Set.
	 * Corresponding Redis command: SREM.
	 *
	 * @param var the variable part of the key name
	 * @param value the member to remove
	 * @return true if the member was removed, false if the member was not present
	 * @throws RedisException if Redis operation fails
	 */
	@RemoveMembers
	Boolean remove(@KeyVariable(name = "var") String var,@RedisValue String value);
	
	/**
	 * Removes multiple members from the Set in a batch operation.
	 * Corresponding Redis command: SREM with multiple members.
	 * <p>
	 * The operation will continue on failure for individual removals
	 * due to {@code @ContinueOnFailure(true)} annotation.
	 *
	 * @param var the variable part of the key name
	 * @param values set of members to remove
	 * @return map where key is the member and value indicates removal status
	 *         (true=member was removed, false=member was not present)
	 * @throws RedisException if Redis operation fails
	 */
	@RemoveMembers
	@ContinueOnFailure(true)
	Map<String,Boolean> remove(@KeyVariable(name = "var") String var,@Multiple Set<String> values);
	
	/**
	 * Incrementally scans the Set for members matching a pattern.
	 * Corresponding Redis command: SSCAN.
	 * <p>
	 * This method allows iterating over large Sets without blocking the server.
	 * Use 0 as preCursor to start a new scan, then use the returned cursor
	 * for subsequent calls until it returns 0 indicating scan completion.
	 *
	 * @param var the variable part of the key name
	 * @param pattern the glob-style pattern to match (use '*' for all members)
	 * @param count the approximate number of members to return per call
	 * @param preCursor the cursor from previous scan (0 for initial scan)
	 * @return the next cursor to use for subsequent scans (0 when scan is complete)
	 * @throws RedisException if Redis operation fails
	 * @throws IllegalArgumentException if count is negative
	 */
	@GetMembers
	String scan(@KeyVariable(name = "var") String var, @ScanPattern String pattern,@Count Long count,@PageNo Long preCursor);
	
}