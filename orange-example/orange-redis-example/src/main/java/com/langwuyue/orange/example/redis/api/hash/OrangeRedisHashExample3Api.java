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
package com.langwuyue.orange.example.redis.api.hash;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import com.langwuyue.orange.redis.RedisValueTypeEnum;
import com.langwuyue.orange.redis.annotation.AddMembers;
import com.langwuyue.orange.redis.annotation.CAS;
import com.langwuyue.orange.redis.annotation.ContinueOnFailure;
import com.langwuyue.orange.redis.annotation.Count;
import com.langwuyue.orange.redis.annotation.GetMembers;
import com.langwuyue.orange.redis.annotation.GetSize;
import com.langwuyue.orange.redis.annotation.IfAbsent;
import com.langwuyue.orange.redis.annotation.KeyVariable;
import com.langwuyue.orange.redis.annotation.Multiple;
import com.langwuyue.orange.redis.annotation.OrangeRedisKey;
import com.langwuyue.orange.redis.annotation.Random;
import com.langwuyue.orange.redis.annotation.RedisOldValue;
import com.langwuyue.orange.redis.annotation.RedisValue;
import com.langwuyue.orange.redis.annotation.RemoveMembers;
import com.langwuyue.orange.redis.annotation.Timeout;
import com.langwuyue.orange.redis.annotation.TimeoutUnit;
import com.langwuyue.orange.redis.annotation.global.Delete;
import com.langwuyue.orange.redis.annotation.global.GetExpiration;
import com.langwuyue.orange.redis.annotation.global.SetExpiration;
import com.langwuyue.orange.redis.annotation.hash.GetHashKeys;
import com.langwuyue.orange.redis.annotation.hash.GetHashValueLength;
import com.langwuyue.orange.redis.annotation.hash.GetHashValues;
import com.langwuyue.orange.redis.annotation.hash.HasKeys;
import com.langwuyue.orange.redis.annotation.hash.HashKey;
import com.langwuyue.orange.redis.annotation.hash.OrangeRedisHashClient;

/**
 * Advanced example demonstrating Redis hash operations with dynamic keys.
 * 
 * <p>Extends basic hash functionality from {@link OrangeRedisHashExample1Api} and
 * {@link OrangeRedisHashExample2Api} with:
 * <ul>
 *   <li>Dynamic key support via {@code ${var}} template and {@link KeyVariable}</li>
 *   <li>Atomic compare-and-swap operations</li>
 *   <li>Bulk operations for better performance</li>
 *   <li>Random key access</li>
 *   <li>Fine-grained expiration control</li>
 * </ul>
 * 
 * <p>Key features:
 * <ul>
 *   <li>Template key: "orange:hash:example3:${var}"</li>
 *   <li>Default expiration: 1 hour (configurable per key)</li>
 *   <li>String field and value types</li>
 * </ul>
 * 
 * <p>Advanced patterns:
 * <pre>{@code
 * // Dynamic key usage
 * String userId = "user123";
 * api.add(userId, "preferences", "{theme:dark}");
 * 
 * // Atomic compare-and-swap
 * boolean swapped = api.compareAndSwap(
 *     "session456", 
 *     "token",
 *     "oldToken123",  // Only swap if current value matches
 *     "newToken456"
 * );
 * 
 * // Bulk operations
 * Map<String,String> userData = Map.of(
 *     "name", "John",
 *     "email", "john@example.com"
 * );
 * api.add("profile789", userData);
 * }</pre>
 * 
 * @author Liang.Zhong
 * @since 1.0.0
 * @see OrangeRedisHashExample1Api Basic JSON hash operations
 * @see OrangeRedisHashExample2Api Basic String hash operations
 * @see com.langwuyue.orange.redis.annotation.KeyVariable Dynamic key configuration
 * @see com.langwuyue.orange.redis.annotation.CAS Compare-and-swap operations
 */
@OrangeRedisHashClient(
		hashKeyType = RedisValueTypeEnum.STRING,
		hashValueType = RedisValueTypeEnum.STRING
	)
@OrangeRedisKey(expirationTime = @Timeout(value = 1, unit = TimeUnit.HOURS), key = "orange:hash:example3:${var}")
public interface OrangeRedisHashExample3Api  {

	/**
	 * Sets expiration time for the hash key (default unit: seconds)
	 * 
	 * <p>Corresponding Redis command: EXPIRE</p>
	 * 
	 * @param keyVar The variable part of the key name
	 * @return true if expiration was set, false if key doesn't exist
	 */
	@SetExpiration
	boolean setExpiration(@KeyVariable(name = "var") String keyVar);
	
	/**
	 * Deletes the entire hash key and all its fields
	 * 
	 * <p>Corresponding Redis command: DEL</p>
	 * 
	 * @param keyVar The variable part of the key name
	 * @return true if key was deleted, false if key didn't exist
	 */
	@Delete
	boolean delete(@KeyVariable(name = "var") String keyVar);
	
	/**
	 * Gets remaining expiration time in seconds
	 * 
	 * <p>Corresponding Redis command: TTL</p>
	 * 
	 * @param keyVar The variable part of the key name
	 * @return Remaining time in seconds, -1 if no expiration, -2 if key doesn't exist
	 */
	@GetExpiration
	Long getExpiration(@KeyVariable(name = "var") String keyVar);
	
	/**
	 * Gets remaining expiration time in specified time unit
	 * 
	 * <p>Corresponding Redis command: TTL (converted to requested unit)</p>
	 * 
	 * @param keyVar The variable part of the key name
	 * @param unit The time unit for the return value
	 * @return Remaining time in requested unit, -1 if no expiration, -2 if key doesn't exist
	 */
	@GetExpiration
	Long getExpiration(@KeyVariable(name = "var") String keyVar,@TimeoutUnit TimeUnit unit);
	
	/**
	 * Adds or updates a single field in the hash
	 * 
	 * <p>Corresponding Redis command: HSET</p>
	 * 
	 * @param keyVar The variable part of the key name
	 * @param key The hash field name
	 * @param value The hash field value
	 */
	@AddMembers
	void add(@KeyVariable(name = "var") String keyVar,@HashKey String key, @RedisValue String value);
	
	/**
	 * Adds or updates multiple fields in the hash (batch operation)
	 * 
	 * <p>Corresponding Redis command: Multiple HSET commands</p>
	 * 
	 * <p>Operation will continue on failure for individual fields due to @ContinueOnFailure(true)</p>
	 * 
	 * @param keyVar The variable part of the key name
	 * @param members Map of field names and values to add/update
	 */
	@AddMembers
	@ContinueOnFailure(true)
	void add(@KeyVariable(name = "var") String keyVar,@Multiple Map<String, String> members);
	
	/**
	 * Adds a field to the hash only if it does not already exist (atomic operation)
	 * 
	 * <p>Corresponding Redis pattern: HSETNX (simulated with atomic check)</p>
	 * 
	 * <p>This is an atomic operation guaranteed by the @IfAbsent annotation.
	 * The field will only be added if the hash doesn't already contain it.</p>
	 * 
	 * @param keyVar The variable part of the key name
	 * @param key The hash field name
	 * @param value The hash field value to add
	 */
	@AddMembers
	@IfAbsent(deleteInTheEnd = false)
	void addIfAbsent(@KeyVariable(name = "var") String keyVar,@HashKey String key, @RedisValue String value);
	
	/**
	 * Adds multiple fields to the hash only if they do not already exist (batch atomic operation)
	 * 
	 * <p>Corresponding Redis pattern: Multiple HSETNX commands (simulated with atomic checks)</p>
	 * 
	 * <p>This batch operation applies the same atomic guarantee as the single field version,
	 * with the addition of @ContinueOnFailure support.</p>
	 * 
	 * @param keyVar The variable part of the key name
	 * @param members Map of field names and values to conditionally add
	 */
	@AddMembers
	@IfAbsent(deleteInTheEnd = false)
	@ContinueOnFailure(true)
	void addIfAbsent(@KeyVariable(name = "var") String keyVar,@Multiple Map<String, String> members);
	
	/**
	 * Atomically compares the current field value with expected value and swaps if they match
	 * 
	 * <p>Corresponding Redis pattern: WATCH/MULTI/EXEC with GET and conditional HSET</p>
	 * 
	 * <p>This operation provides atomic compare-and-swap (CAS) semantics:
	 * <ul>
	 *   <li>If current value matches oldValue, sets to newValue and returns true</li>
	 *   <li>If current value differs, leaves unchanged and returns false</li>
	 * </ul></p>
	 * 
	 * @param keyVar The variable part of the key name
	 * @param hashKey The hash field name
	 * @param oldValue The expected current value for the swap to occur
	 * @param newValue The new value to set if comparison succeeds
	 * @return true if swap occurred, false otherwise
	 */
	@CAS
	Boolean compareAndSwap(@KeyVariable(name = "var") String keyVar,@HashKey String hashKey, @RedisOldValue String oldValue, @RedisValue String newValue);
	
	/**
	 * Gets the number of fields in the hash
	 * 
	 * <p>Corresponding Redis command: HLEN</p>
	 * 
	 * @param keyVar The variable part of the key name
	 * @return Number of fields, 0 if key doesn't exist
	 */
	@GetSize
	Long getSize(@KeyVariable(name = "var") String keyVar);
	
	/**
	 * Gets the byte length of a hash field's value
	 * 
	 * <p>Corresponding Redis command: HSTRLEN</p>
	 * 
	 * @param keyVar The variable part of the key name
	 * @param hashKey The hash field name
	 * @return Byte length of the field value, 0 if field doesn't exist
	 */
	@GetHashValueLength
	Long getHashValueBytesLength(@KeyVariable(name = "var") String keyVar,@HashKey String hashKey);
	
	/**
	 * Gets all field names in the hash
	 * 
	 * <p>Corresponding Redis command: HKEYS</p>
	 * 
	 * @param keyVar The variable part of the key name
	 * @return Set of all field names, empty set if key doesn't exist
	 */
	@GetHashKeys
	Set<String> getAllKeys(@KeyVariable(name = "var") String keyVar);
	
	/**
	 * Gets a random field name from the hash
	 * 
	 * <p>Corresponding Redis command: HRANDFIELD with count=1</p>
	 * 
	 * @param keyVar The variable part of the key name
	 * @return Random field name, or null if hash is empty
	 */
	@GetHashKeys
	@Random
	String randomKey(@KeyVariable(name = "var") String keyVar);
	
	/**
	 * Gets multiple random field names from the hash
	 * 
	 * <p>Corresponding Redis command: HRANDFIELD with count</p>
	 * 
	 * @param keyVar The variable part of the key name
	 * @param count Number of random fields to return
	 * @return Set of random field names (may contain duplicates if withReplacement=true)
	 */
	@GetHashKeys
	@Random
	Set<String> randomKeys(@KeyVariable(name = "var") String keyVar,@Count Long count);
	
	/**
	 * Checks if a field exists in the hash
	 * 
	 * <p>Corresponding Redis command: HEXISTS</p>
	 * 
	 * @param keyVar The variable part of the key name
	 * @param hashKey The hash field name
	 * @return true if field exists, false otherwise
	 */
	@HasKeys
	Boolean hasKey(@KeyVariable(name = "var") String keyVar,@HashKey String hashKey);
	
	/**
	 * Checks if multiple fields exist in the hash (batch operation)
	 * 
	 * <p>Corresponding Redis command: Multiple HEXISTS commands</p>
	 * 
	 * <p>Operation will continue on failure for individual checks due to @ContinueOnFailure(true)</p>
	 * 
	 * @param keyVar The variable part of the key name
	 * @param hashKeys Collection of field names to check
	 * @return Map of field names to existence status (true/false)
	 */
	@HasKeys
	@ContinueOnFailure(true)
	Map<String, Boolean> hasKeys(@KeyVariable(name = "var") String keyVar,@Multiple Collection<String> hashKeys);
	
	/**
	 * Gets all field values in the hash
	 * 
	 * <p>Corresponding Redis command: HVALS</p>
	 * 
	 * @param keyVar The variable part of the key name
	 * @return List of all field values, empty list if key doesn't exist
	 */
	@GetHashValues
	List<String> getValues(@KeyVariable(name = "var") String keyVar);
	
	/**
	 * Gets the value of a single hash field
	 * 
	 * <p>Corresponding Redis command: HGET</p>
	 * 
	 * @param keyVar The variable part of the key name
	 * @param hashKey The hash field name
	 * @return The field value, or null if field doesn't exist
	 */
	@GetHashValues
	String get(@KeyVariable(name = "var") String keyVar,@HashKey String hashKey);
	
	/**
	 * Gets values for multiple hash fields (batch operation)
	 * 
	 * <p>Corresponding Redis command: HMGET</p>
	 * 
	 * @param keyVar The variable part of the key name
	 * @param hashKeys Collection of field names to get
	 * @return Map of field names to values (only contains existing fields)
	 */
	@GetHashValues
	Map<String,String> get(@KeyVariable(name = "var") String keyVar,@Multiple Collection<String> hashKeys);
	
	/**
	 * Gets all field-value pairs in the hash
	 * 
	 * <p>Corresponding Redis command: HGETALL</p>
	 * 
	 * @param keyVar The variable part of the key name
	 * @return Map containing all fields and values, empty map if key doesn't exist
	 */
	@GetMembers
	Map<String, String> getAllMembers(@KeyVariable(name = "var") String keyVar);
	
	/**
	 * Removes a single field from the hash
	 * 
	 * <p>Corresponding Redis command: HDEL</p>
	 * 
	 * @param keyVar The variable part of the key name
	 * @param hashKey The hash field name to remove
	 * @return true if field was removed, false if field didn't exist
	 */
	@RemoveMembers
	Boolean remove(@KeyVariable(name = "var") String keyVar,@HashKey String hashKey);
	
	/**
	 * Removes multiple fields from the hash (batch operation)
	 * 
	 * <p>Corresponding Redis command: HDEL with multiple fields</p>
	 * 
	 * <p>Operation will continue on failure for individual deletions due to @ContinueOnFailure(true)</p>
	 * 
	 * @param keyVar The variable part of the key name
	 * @param hashKeys Collection of field names to remove
	 * @return Map of field names to deletion status (true=deleted, false=didn't exist)
	 */
	@RemoveMembers
	@ContinueOnFailure(true)
	Map<String,Boolean> remove(@KeyVariable(name = "var") String keyVar,@Multiple Collection<String> hashKeys);
	
}