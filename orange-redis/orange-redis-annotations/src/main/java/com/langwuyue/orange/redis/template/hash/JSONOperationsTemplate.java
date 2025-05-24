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
package com.langwuyue.orange.redis.template.hash;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.langwuyue.orange.redis.RedisValueTypeEnum;
import com.langwuyue.orange.redis.annotation.AddMembers;
import com.langwuyue.orange.redis.annotation.CAS;
import com.langwuyue.orange.redis.annotation.ContinueOnFailure;
import com.langwuyue.orange.redis.annotation.Count;
import com.langwuyue.orange.redis.annotation.Distinct;
import com.langwuyue.orange.redis.annotation.GetMembers;
import com.langwuyue.orange.redis.annotation.GetSize;
import com.langwuyue.orange.redis.annotation.IfAbsent;
import com.langwuyue.orange.redis.annotation.Multiple;
import com.langwuyue.orange.redis.annotation.Random;
import com.langwuyue.orange.redis.annotation.RedisOldValue;
import com.langwuyue.orange.redis.annotation.RedisValue;
import com.langwuyue.orange.redis.annotation.RemoveMembers;
import com.langwuyue.orange.redis.annotation.hash.GetHashKeys;
import com.langwuyue.orange.redis.annotation.hash.GetHashValueLength;
import com.langwuyue.orange.redis.annotation.hash.GetHashValues;
import com.langwuyue.orange.redis.annotation.hash.HasKeys;
import com.langwuyue.orange.redis.annotation.hash.HashKey;
import com.langwuyue.orange.redis.annotation.hash.OrangeRedisHashClient;
import com.langwuyue.orange.redis.template.global.GlobalOperationsTemplate;

/**
 * Interface template for Redis Hash operations with JSON value serialization.
 * 
 * <p>Key characteristics:
 * <ul>
 *   <li>Provides atomic operations for Redis Hash data structure</li>
 *   <li>Supports JSON serialization/deserialization of values</li>
 *   <li>Includes single-key and batch operations</li>
 *   <li>Supports conditional operations and Compare-And-Swap pattern</li>
 * </ul>
 * 
 * 
 * <p>Usage example:
 * <blockquote><pre>
 * {@code @OrangeRedisKey(expirationTime = @Timeout(value = 1, unit = TimeUnit.HOURS), key = "orange:hash:example1")}
 * public interface OrangeRedisHashExample1Api extends JSONOperationsTemplate{@code <Value>}{
 * }
 * </pre></blockquote>
 * 
 * 
 * @param <T> The type of value stored in the Redis Hash (will be JSON serialized)
 * @author Liang.Zhong
 * @since 1.0.0
 */
@OrangeRedisHashClient(
    hashKeyType = RedisValueTypeEnum.STRING,  // Hash keys are always strings
    hashValueType = RedisValueTypeEnum.JSON   // Values are stored as JSON
)
public interface JSONOperationsTemplate<T> extends GlobalOperationsTemplate {
    
    /**
     * Adds a single key-value pair to the hash
     * 
     * @param key The hash key (field name)
     * @param value The value to store (will be JSON serialized)
     */
    @AddMembers
    void add(@HashKey String key, @RedisValue T value);
    
    /**
     * Batch operation to add multiple key-value pairs
     * 
     * <p>
	 * The {@code @ContinueOnFailure} annotation's value determines 
	 * whether the method continues to add subsequent members into Redis hash
	 * even if an exception occurs or some members fail.
	 * True: continue | False: break
	 * 
     * 
     * @param members Map of key-value pairs to add
     * @see ContinueOnFailure If true, continues processing remaining items on failure
     */
    @AddMembers
    @ContinueOnFailure(true)
    void add(@Multiple Map<String, T> members);
    
    /**
     * Adds a key-value pair only if the key doesn't exist
     * 
     * <p>
	 * Once the addition process is completed, the {@code OrangeRedisHashAddMemberIfAbsentListener} component ​​will be triggered​​. 
	 * Developers should ​​configure​​ {@code OrangeRedisHashAddMemberIfAbsentListener} to manage post-addition business logic. 
	 * Note that the {@code OrangeRedisHashAddMemberIfAbsentListener} implementation class must be annotated with Spring’s {@code @Component}
	 * 
	 * 
	 * <p>
	 * The property {@code deleteInTheEnd} of {@code @IfAbsent} determines if the value is deleted after operation completion.
	 * True​​: Delete | ​​False​​: Keep
	 * 
     * 
     * @param key The hash key to check
     * @param value The value to store if key is absent
     */
    @AddMembers
    @IfAbsent(deleteInTheEnd = false)
    void addIfAbsent(@HashKey String key, @RedisValue T value);
    
    /**
     * Batch version of addIfAbsent
     * 
     * <p>
	 * Once the addition process is completed, the {@code OrangeRedisHashAddMembersIfAbsentListener} component ​​will be triggered​​. 
	 * Developers should ​​configure​​ {@code OrangeRedisHashAddMembersIfAbsentListener} to manage post-addition business logic. 
	 * Note that the {@code OrangeRedisHashAddMembersIfAbsentListener} implementation class must be annotated with Spring’s {@code @Component}
	 * 
	 * 
	 * <p>
	 * The property {@code deleteInTheEnd} of {@code @IfAbsent} determines if the value is deleted after operation completion.
	 * True​​: Delete | ​​False​​: Keep
	 * 
     * 
     * <p>
	 * The {@code @ContinueOnFailure} annotation's value determines 
	 * whether the method continues to add subsequent members into Redis hash
	 * even if an exception occurs or some members fail.
	 * True: continue | False: break
	 * 
	 * 
     * 
     * @param members Map of key-value pairs to conditionally add
     */
    @AddMembers
    @IfAbsent(deleteInTheEnd = false)
    @ContinueOnFailure(true)
    void addIfAbsent(@Multiple Map<String, T> members);
    
    /**
     * Compare-And-Swap operation (atomic check-and-set)
     * 
     * @param key The hash key to modify
     * @param oldValue Expected current value (must match)
     * @param newValue New value to set
     * @return true if swap was successful, false otherwise
     */
    @CAS
    Boolean compareAndSwap(@HashKey String key, @RedisOldValue T oldValue, @RedisValue T newValue);
    
    /**
     * Gets the number of fields in the hash
     * 
     * @return The size (field count) of the hash
     */
    @GetSize
    Long getSize();
    
    /**
     * Gets the serialized byte length of a hash value
     * 
     * @param key The field name to check
     * @return Length in bytes of the serialized value
     */
    @GetHashValueLength
    Long getHashValueBytesLength(@HashKey String key);
    
    /**
     * Retrieves all field names (keys) in the hash
     * 
     * @return Set of all field names
     */
    @GetHashKeys
    Set<String> getAllKeys();
    
    /**
     * Gets a random field name from the hash
     * 
     * @return A randomly selected field name
     */
    @GetHashKeys
    @Random
    String randomKey();
    
    /**
     * Gets multiple random field names
     * 
     * @param count Number of random keys to return
     * @return Set of randomly selected field names
     */
    @GetHashKeys
    @Random
    List<String> randomKeys(@Count Long count);
    
    /**
     * Gets multiple random field names
     * 
     * @param count Number of random keys to return
     * @return Set of randomly selected field names
     */
    @GetHashKeys
    @Random
    @Distinct
    Set<String> randomAndDistinctKeys(@Count Long count);
    
    /**
     * Checks if a field exists in the hash
     * 
     * @param key Field name to check
     * @return true if the field exists, false otherwise
     */
    @HasKeys
    Boolean hasKey(@HashKey String key);
    
    /**
     * Batch check for multiple field existences
     * 
     * <p>
	 * The {@code @ContinueOnFailure} annotation's value determines 
	 * whether the method continues to check the subsequent members in the Redis hash
	 * even if an exception occurs or some members fail.
	 * True: continue | False: break
	 * 
     * 
     * @param key Collection of field names to check
     * @return Map of field names to existence booleans
     */
    @HasKeys
    @ContinueOnFailure(true)
    Map<String, Boolean> hasKeys(@Multiple Collection<String> key);
    
    /**
     * Gets all values from the hash (without keys)
     * 
     * <p>
	 * Developers must override this method when another interface extends this template; 
	 * otherwise, an exception will occur, 
	 * because the method's return type involves a generic argument T.
	 * 
     * 
     * @return List of all values (deserialized from JSON)
     */
    @GetHashValues
    List<T> getValues();
    
    /**
     * Gets a single value by field name
     * 
     * <p>
	 * Developers must override this method when another interface extends this template; 
	 * otherwise, an exception will occur, 
	 * because the method's return type involves a generic argument T.
	 * 
     * 
     * @param key Field name to retrieve
     * @return The deserialized value, or null if not found
     */
    @GetHashValues
    T get(@HashKey String key);
    
    /**
     * Batch retrieval of multiple values
     * 
     * <p>
	 * Developers must override this method when another interface extends this template; 
	 * otherwise, an exception will occur, 
	 * because the method's return type involves a generic argument T.
	 * 
     * 
     * @param key Collection of field names to retrieve
     * @return Map of field names to deserialized values
     */
    @GetHashValues
    Map<String,T> get(@Multiple Collection<String> key);
    
    /**
     * Gets all key-value pairs from the hash
     * 
     * <p>
	 * Developers must override this method when another interface extends this template; 
	 * otherwise, an exception will occur, 
	 * because the method's return type involves a generic argument T.
	 * 
     * 
     * @return Map of all field names to deserialized values
     */
    @GetMembers
    Map<String, T> getAllMembers();
    
    /**
     * Removes a single field from the hash
     * 
     * @param key Field name to remove
     * @return true if field existed and was removed, false otherwise
     */
    @RemoveMembers
    Boolean remove(@HashKey String key);
    
    /**
     * Batch removal of multiple fields
     * 
     * <p>
	 * The {@code @ContinueOnFailure} annotation's value determines 
	 * whether the method continues to remove subsequent members from Redis hash
	 * even if an exception occurs or some members fail.
	 * True: continue | False: break
	 * 
     * 
     * @param keys Collection of field names to remove
     * @return Map of field names to removal status booleans
     */
    @RemoveMembers
    @ContinueOnFailure(true)
    Map<String,Boolean> remove(@Multiple Collection<String> keys);
}