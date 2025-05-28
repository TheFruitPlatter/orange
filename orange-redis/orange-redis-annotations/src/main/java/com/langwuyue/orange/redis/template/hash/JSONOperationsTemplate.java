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
 * This template provides a comprehensive API for managing Redis Hashes where values
 * are automatically serialized to and deserialized from JSON format.
 * 
 * <p>Key features:
 * <ul>
 *   <li>Type-safe Redis Hash operations</li>
 *   <li>Automatic JSON serialization/deserialization of values</li>
 *   <li>String-based hash keys with JSON-serialized values</li>
 *   <li>Atomic operations support (CAS)</li>
 *   <li>Conditional operations (if absent)</li>
 *   <li>Batch operations with failure handling</li>
 *   <li>Random key selection</li>
 *   <li>Complete hash management capabilities</li>
 * </ul>
 * 
 * <p>Usage example:
 * <pre>{@code
 * // 1. Define your value type
 * public class UserProfile {
 *     private String userId;
 *     private String name;
 *     private int age;
 *     private Map<String, String> preferences;
 *     // getters, setters, etc.
 * }
 * 
 * // 2. Define your Redis Hash interface
 * {@code @OrangeRedisKey(
 *     expirationTime = @Timeout(value = 1, unit = TimeUnit.DAYS),
 *     key = "orange:hash:user:profiles"
 * )}
 * public interface UserProfileHash extends JSONOperationsTemplate{@code <UserProfile>} {
 *     // Inherit all operations from template
 * }
 * 
 * // 3. Use in your service
 * {@code @Service}
 * public class UserProfileService {
 *     {@code @Autowired}
 *     private UserProfileHash profileHash;
 *     
 *     public void saveProfile(UserProfile profile) {
 *         // Use userId as hash key
 *         profileHash.add(profile.getUserId(), profile);
 *     }
 *     
 *     public UserProfile getProfile(String userId) {
 *         return profileHash.get(userId);
 *     }
 *     
 *     public void updateIfExists(UserProfile oldProfile, UserProfile newProfile) {
 *         boolean updated = profileHash.compareAndSwap(
 *             oldProfile.getUserId(), oldProfile, newProfile);
 *         if (!updated) {
 *             throw new OptimisticLockException("Profile was modified by another process");
 *         }
 *     }
 * }
 * }</pre>
 * 
 * <p>Implementation notes:
 * <ul>
 *   <li>Uses Redis HASH data type for storage</li>
 *   <li>Hash keys are stored as strings</li>
 *   <li>Values are stored as JSON strings in Redis</li>
 *   <li>Supports all Redis Hash operations (HSET, HGET, HDEL, etc.)</li>
 *   <li>Thread-safe operations</li>
 *   <li>Automatic connection and error handling</li>
 * </ul>
 * 
 * @param <T> The type of values stored in the Redis Hash (will be serialized as JSON)
 * @author Liang.Zhong
 * @since 1.0.0
 * @see com.langwuyue.orange.redis.annotation.hash.OrangeRedisHashClient
 * @see com.langwuyue.orange.redis.annotation.OrangeRedisKey
 * @see com.langwuyue.orange.redis.template.global.GlobalOperationsTemplate
 */
@OrangeRedisHashClient(
    hashKeyType = RedisValueTypeEnum.STRING,  // Hash keys are always strings
    hashValueType = RedisValueTypeEnum.JSON   // Values are stored as JSON
)
public interface JSONOperationsTemplate<T> extends GlobalOperationsTemplate {
    
    /**
     * Adds a single key-value pair to the hash.
     * This operation is equivalent to the Redis HSET command.
     * 
     * <p>Example:
     * <pre>{@code
     * UserProfile profile = new UserProfile("user123", "John Doe", 30);
     * profileHash.add("user123", profile);
     * }</pre>
     * 
     * @param key The hash key (field name)
     * @param value The value to store (will be JSON serialized)
     */
    @AddMembers
    void add(@HashKey String key, @RedisValue T value);
    
    /**
     * Batch operation to add multiple key-value pairs to the hash.
     * This operation is equivalent to the Redis HMSET command.
     * 
     * <p>The {@code @ContinueOnFailure(true)} annotation indicates that the operation
     * will continue attempting to add remaining key-value pairs even if some additions fail.
     * 
     * <p>Example:
     * <pre>{@code
     * Map<String, UserProfile> profiles = new HashMap<>();
     * profiles.put("user123", new UserProfile("user123", "John Doe", 30));
     * profiles.put("user456", new UserProfile("user456", "Jane Smith", 28));
     * 
     * profileHash.add(profiles);
     * }</pre>
     * 
     * @param members Map of key-value pairs to add
     */
    @AddMembers
    @ContinueOnFailure(true)
    void add(@Multiple Map<String, T> members);
    
    /**
     * Adds a key-value pair only if the key doesn't exist in the hash.
     * This operation is equivalent to the Redis HSETNX command.
     * 
     * <p>Once the addition process is completed, the {@code OrangeRedisHashAddMemberIfAbsentListener} 
     * component will be triggered. Developers should configure {@code OrangeRedisHashAddMemberIfAbsentListener} 
     * to manage post-addition business logic. Note that the {@code OrangeRedisHashAddMemberIfAbsentListener} 
     * implementation class must be annotated with Spring's {@code @Component}.
     * 
     * <p>The property {@code deleteInTheEnd=false} of {@code @IfAbsent} indicates that the value 
     * will be kept after operation completion.
     * 
     * <p>Example:
     * <pre>{@code
     * UserProfile newProfile = new UserProfile("user123", "John Doe", 30);
     * // Will only add if "user123" doesn't exist in the hash
     * profileHash.addIfAbsent("user123", newProfile);
     * }</pre>
     * 
     * @param key The hash key to check
     * @param value The value to store if key is absent
     */
    @AddMembers
    @IfAbsent(deleteInTheEnd = false)
    void addIfAbsent(@HashKey String key, @RedisValue T value);
    
    /**
     * Batch version of addIfAbsent to conditionally add multiple key-value pairs.
     * This operation is equivalent to multiple Redis HSETNX commands.
     * 
     * <p>Once the addition process is completed, the {@code OrangeRedisHashAddMembersIfAbsentListener} 
     * component will be triggered. Developers should configure {@code OrangeRedisHashAddMembersIfAbsentListener} 
     * to manage post-addition business logic. Note that the {@code OrangeRedisHashAddMembersIfAbsentListener} 
     * implementation class must be annotated with Spring's {@code @Component}.
     * 
     * <p>The property {@code deleteInTheEnd=false} of {@code @IfAbsent} indicates that the values 
     * will be kept after operation completion.
     * 
     * <p>The {@code @ContinueOnFailure(true)} annotation indicates that the operation
     * will continue attempting to add remaining key-value pairs even if some additions fail.
     * 
     * <p>Example:
     * <pre>{@code
     * Map<String, UserProfile> newProfiles = new HashMap<>();
     * newProfiles.put("user123", new UserProfile("user123", "John Doe", 30));
     * newProfiles.put("user456", new UserProfile("user456", "Jane Smith", 28));
     * 
     * // Will only add entries where the key doesn't exist
     * profileHash.addIfAbsent(newProfiles);
     * }</pre>
     * 
     * @param members Map of key-value pairs to conditionally add
     */
    @AddMembers
    @IfAbsent(deleteInTheEnd = false)
    @ContinueOnFailure(true)
    void addIfAbsent(@Multiple Map<String, T> members);
    
    /**
     * Compare-And-Swap operation for atomic conditional updates.
     * This operation atomically updates a hash field only if its current value matches the expected value.
     * 
     * <p>Example:
     * <pre>{@code
     * UserProfile currentProfile = profileHash.get("user123");
     * UserProfile updatedProfile = new UserProfile(currentProfile);
     * updatedProfile.setAge(31);
     * 
     * boolean success = profileHash.compareAndSwap("user123", currentProfile, updatedProfile);
     * if (success) {
     *     log.info("Profile updated successfully");
     * } else {
     *     log.warn("Profile was modified by another process");
     * }
     * }</pre>
     * 
     * @param key The hash key to modify
     * @param oldValue Expected current value (must match)
     * @param newValue New value to set
     * @return true if swap was successful (current value matched oldValue), false otherwise
     */
    @CAS
    Boolean compareAndSwap(@HashKey String key, @RedisOldValue T oldValue, @RedisValue T newValue);
    
    /**
     * Gets the number of fields in the hash.
     * This operation is equivalent to the Redis HLEN command.
     * 
     * <p>Example:
     * <pre>{@code
     * Long size = profileHash.getSize();
     * log.info("Number of profiles: {}", size);
     * }</pre>
     * 
     * @return The size (field count) of the hash
     */
    @GetSize
    Long getSize();
    
    /**
     * Gets the serialized byte length of a hash value.
     * This operation is useful for monitoring memory usage.
     * 
     * <p>Example:
     * <pre>{@code
     * Long bytes = profileHash.getHashValueBytesLength("user123");
     * log.info("Profile size: {} bytes", bytes);
     * }</pre>
     * 
     * @param key The field name to check
     * @return Length in bytes of the serialized value, or null if the field doesn't exist
     */
    @GetHashValueLength
    Long getHashValueBytesLength(@HashKey String key);
    
    /**
     * Retrieves all field names (keys) in the hash.
     * This operation is equivalent to the Redis HKEYS command.
     * 
     * <p>Example:
     * <pre>{@code
     * Set<String> userIds = profileHash.getAllKeys();
     * log.info("User IDs: {}", userIds);
     * }</pre>
     * 
     * @return Set of all field names
     */
    @GetHashKeys
    Set<String> getAllKeys();
    
    /**
     * Gets a random field name from the hash.
     * This operation is equivalent to the Redis HRANDFIELD command with count=1.
     * 
     * <p>Example:
     * <pre>{@code
     * String randomUserId = profileHash.randomKey();
     * if (randomUserId != null) {
     *     UserProfile randomProfile = profileHash.get(randomUserId);
     *     log.info("Random profile: {}", randomProfile);
     * }
     * }</pre>
     * 
     * @return A randomly selected field name, or null if the hash is empty
     */
    @GetHashKeys
    @Random
    String randomKey();
    
    /**
     * Gets multiple random field names from the hash (may contain duplicates).
     * This operation is equivalent to the Redis HRANDFIELD command.
     * 
     * <p>Example:
     * <pre>{@code
     * // Get 5 random user IDs (may include duplicates)
     * List<String> randomUserIds = profileHash.randomKeys(5L);
     * log.info("Random user IDs: {}", randomUserIds);
     * }</pre>
     * 
     * @param count Number of random keys to return
     * @return List of randomly selected field names
     */
    @GetHashKeys
    @Random
    List<String> randomKeys(@Count Long count);
    
    /**
     * Gets multiple distinct random field names from the hash.
     * This operation is equivalent to the Redis HRANDFIELD command with DISTINCT option.
     * 
     * <p>Example:
     * <pre>{@code
     * // Get 5 unique random user IDs
     * Set<String> uniqueUserIds = profileHash.randomAndDistinctKeys(5L);
     * log.info("Unique random user IDs: {}", uniqueUserIds);
     * }</pre>
     * 
     * @param count Number of distinct random keys to return
     * @return Set of distinct randomly selected field names
     */
    @GetHashKeys
    @Random
    @Distinct
    Set<String> randomAndDistinctKeys(@Count Long count);
    
    /**
     * Checks if a field exists in the hash.
     * This operation is equivalent to the Redis HEXISTS command.
     * 
     * <p>Example:
     * <pre>{@code
     * boolean exists = profileHash.hasKey("user123");
     * if (exists) {
     *     log.info("User profile exists");
     * } else {
     *     log.info("User profile not found");
     * }
     * }</pre>
     * 
     * @param key Field name to check
     * @return true if the field exists, false otherwise
     */
    @HasKeys
    Boolean hasKey(@HashKey String key);
    
    /**
     * Batch check for multiple field existences.
     * This operation is equivalent to multiple Redis HEXISTS commands.
     * 
     * <p>The {@code @ContinueOnFailure(true)} annotation indicates that the operation
     * will continue checking remaining keys even if some checks fail.
     * 
     * <p>Example:
     * <pre>{@code
     * List<String> userIds = Arrays.asList("user123", "user456", "user789");
     * Map<String, Boolean> existsMap = profileHash.hasKeys(userIds);
     * 
     * existsMap.forEach((userId, exists) -> {
     *     if (exists) {
     *         log.info("User {} exists", userId);
     *     } else {
     *         log.info("User {} not found", userId);
     *     }
     * });
     * }</pre>
     * 
     * @param keys Collection of field names to check
     * @return Map of field names to existence booleans
     */
    @HasKeys
    @ContinueOnFailure(true)
    Map<String, Boolean> hasKeys(@Multiple Collection<String> keys);
    
    /**
     * Gets all values from the hash (without keys).
     * This operation is equivalent to the Redis HVALS command.
     * 
     * <p>Developers must override this method when another interface extends this template; 
     * otherwise, an exception will occur, because the method's return type involves a generic argument T.
     * 
     * <p>Example:
     * <pre>{@code
     * List<UserProfile> allProfiles = profileHash.getValues();
     * log.info("Retrieved {} profiles", allProfiles.size());
     * }</pre>
     * 
     * @return List of all values (deserialized from JSON)
     */
    @GetHashValues
    List<T> getValues();
    
    /**
     * Gets a single value by field name.
     * This operation is equivalent to the Redis HGET command.
     * 
     * <p>Developers must override this method when another interface extends this template; 
     * otherwise, an exception will occur, because the method's return type involves a generic argument T.
     * 
     * <p>Example:
     * <pre>{@code
     * UserProfile profile = profileHash.get("user123");
     * if (profile != null) {
     *     log.info("Found profile: {}", profile);
     * } else {
     *     log.info("Profile not found");
     * }
     * }</pre>
     * 
     * @param key Field name to retrieve
     * @return The deserialized value, or null if not found
     */
    @GetHashValues
    T get(@HashKey String key);
    
    /**
     * Batch retrieval of multiple values.
     * This operation is equivalent to the Redis HMGET command.
     * 
     * <p>Developers must override this method when another interface extends this template; 
     * otherwise, an exception will occur, because the method's return type involves a generic argument T.
     * 
     * <p>Example:
     * <pre>{@code
     * List<String> userIds = Arrays.asList("user123", "user456", "user789");
     * Map<String, UserProfile> profiles = profileHash.get(userIds);
     * 
     * profiles.forEach((userId, profile) -> {
     *     if (profile != null) {
     *         log.info("User {}: {}", userId, profile.getName());
     *     } else {
     *         log.info("User {} not found", userId);
     *     }
     * });
     * }</pre>
     * 
     * @param keys Collection of field names to retrieve
     * @return Map of field names to deserialized values (null for non-existent fields)
     */
    @GetHashValues
    Map<String,T> get(@Multiple Collection<String> keys);
    
    /**
     * Gets all key-value pairs from the hash.
     * This operation is equivalent to the Redis HGETALL command.
     * 
     * <p>Developers must override this method when another interface extends this template; 
     * otherwise, an exception will occur, because the method's return type involves a generic argument T.
     * 
     * <p>Example:
     * <pre>{@code
     * Map<String, UserProfile> allProfiles = profileHash.getAllMembers();
     * log.info("Retrieved {} profiles", allProfiles.size());
     * 
     * allProfiles.forEach((userId, profile) -> {
     *     log.info("User {}: {}", userId, profile.getName());
     * });
     * }</pre>
     * 
     * @return Map of all field names to deserialized values
     */
    @GetMembers
    Map<String, T> getAllMembers();
    
    /**
     * Removes a single field from the hash.
     * This operation is equivalent to the Redis HDEL command.
     * 
     * <p>Example:
     * <pre>{@code
     * boolean removed = profileHash.remove("user123");
     * if (removed) {
     *     log.info("Profile removed successfully");
     * } else {
     *     log.info("Profile did not exist");
     * }
     * }</pre>
     * 
     * @param key Field name to remove
     * @return true if field existed and was removed, false otherwise
     */
    @RemoveMembers
    Boolean remove(@HashKey String key);
    
    /**
     * Batch removal of multiple fields.
     * This operation is equivalent to the Redis HDEL command with multiple fields.
     * 
     * <p>The {@code @ContinueOnFailure(true)} annotation indicates that the operation
     * will continue removing remaining fields even if some removals fail.
     * 
     * <p>Example:
     * <pre>{@code
     * List<String> userIds = Arrays.asList("user123", "user456", "user789");
     * Map<String, Boolean> results = profileHash.remove(userIds);
     * 
     * results.forEach((userId, removed) -> {
     *     if (removed) {
     *         log.info("User {} removed successfully", userId);
     *     } else {
     *         log.info("User {} did not exist", userId);
     *     }
     * });
     * }</pre>
     * 
     * @param keys Collection of field names to remove
     * @return Map of field names to removal status booleans
     */
    @RemoveMembers
    @ContinueOnFailure(true)
    Map<String,Boolean> remove(@Multiple Collection<String> keys);
}