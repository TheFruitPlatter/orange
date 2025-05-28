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
package com.langwuyue.orange.redis.template.global;

import java.util.concurrent.TimeUnit;

import com.langwuyue.orange.redis.annotation.TimeoutUnit;
import com.langwuyue.orange.redis.annotation.global.Delete;
import com.langwuyue.orange.redis.annotation.global.GetExpiration;
import com.langwuyue.orange.redis.annotation.global.SetExpiration;

/**
 * A template interface for global Redis key operations.
 * This interface provides essential methods for managing Redis keys, including
 * expiration time management and key deletion operations.
 * 
 * <p>Key Features:
 * <ul>
 *   <li>Key expiration management</li>
 *   <li>TTL (Time To Live) queries</li>
 *   <li>Key deletion</li>
 *   <li>Flexible time unit support</li>
 *   <li>Integration with {@code @OrangeRedisKey} configuration</li>
 * </ul>
 * 
 * <p>Implementation Example:
 * <pre>{@code
 * // 1. Define your Redis operations interface
 * @OrangeRedisKey(
 *     key = "user:profile:{userId}",
 *     expirationTime = {@code @Timeout(value = 24, unit = TimeUnit.HOURS)}
 * )
 * public interface UserProfileOperations extends GlobalOperationsTemplate {
 *     // Your specific operations here
 * }
 * 
 * // 2. Use in your service
 * {@code @Service}
 * public class UserService {
 *     {@code @Autowired}
 *     private UserProfileOperations userProfile;
 *     
 *     public void refreshUserProfile(String userId) {
 *         // Reset expiration time to 24 hours
 *         userProfile.setExpiration();
 *     }
 *     
 *     public boolean isProfileExpiringSoon(String userId) {
 *         // Check if TTL is less than 1 hour
 *         return userProfile.getExpiration(TimeUnit.HOURS) &lt; 1;
 *     }
 *     
 *     public void deleteUserProfile(String userId) {
 *         userProfile.delete();
 *     }
 * }
 * }</pre>
 * 
 * <p>Common Use Cases:
 * <ul>
 *   <li>Session management</li>
 *   <li>Cache expiration control</li>
 *   <li>Temporary data cleanup</li>
 *   <li>TTL monitoring</li>
 *   <li>Resource lifecycle management</li>
 * </ul>
 * 
 * <p>Performance Considerations:
 * <ul>
 *   <li>TTL operations are O(1) complexity</li>
 *   <li>Deletion is immediate for single keys</li>
 *   <li>Expiration times are stored in Redis metadata</li>
 *   <li>No additional memory overhead</li>
 *   <li>Atomic operations guaranteed</li>
 * </ul>
 * 
 * <p>Implementation Notes:
 * <ul>
 *   <li>All methods are thread-safe</li>
 *   <li>Operations are atomic at Redis level</li>
 *   <li>Expiration precision is limited to milliseconds</li>
 *   <li>Expired keys are deleted asynchronously</li>
 *   <li>Zero/negative TTL means key is expired/non-existent</li>
 * </ul>
 * 
 * @author Liang.Zhong
 * @since 1.0.0
 * @see com.langwuyue.orange.redis.annotation.OrangeRedisKey
 * @see com.langwuyue.orange.redis.annotation.Timeout
 */
public interface GlobalOperationsTemplate {
	
	/**
	 * Sets the expiration time for the Redis key as configured in {@code @OrangeRedisKey} annotation.
	 * This method applies the timeout value specified in the {@code @OrangeRedisKey} annotation's
	 * {@code expirationTime} parameter.
	 * 
	 * <p>Example:
	 * <pre>{@code
	 * @OrangeRedisKey(
	 *     key = "session:{sessionId}",
	 *     expirationTime = {@code @Timeout(value = 30, unit = TimeUnit.MINUTES)}
	 * )
	 * public interface SessionOperations extends GlobalOperationsTemplate {
	 *     // ... other methods
	 * }
	 * 
	 * // Usage:
	 * sessionOps.setExpiration(); // Sets 30-minute expiration
	 * }</pre>
	 * 
	 * <p>Note: If the key doesn't exist, this operation will return false and no
	 * expiration will be set.
	 * 
	 * @return true if the expiration was set successfully, false if the key doesn't
	 *         exist or the operation failed
	 */
	@SetExpiration
	Boolean setExpiration();
	
	/**
	 * Deletes the Redis key configured in {@code @OrangeRedisKey} annotation.
	 * This operation is immediate and permanent. Once a key is deleted, all
	 * associated data is removed from Redis.
	 * 
	 * <p>Example:
	 * <pre>{@code
	 * @OrangeRedisKey(key = "cart:{userId}")
	 * public interface CartOperations extends GlobalOperationsTemplate {
	 *     // ... other methods
	 * }
	 * 
	 * // Usage:
	 * cartOps.delete(); // Removes the cart data
	 * }</pre>
	 * 
	 * <p>Note: This operation is idempotent - calling it multiple times or on
	 * a non-existent key will not cause an error.
	 * 
	 * @return true if the key was deleted, false if the key didn't exist
	 */
	@Delete
	Boolean delete();
	
	/**
	 * Gets the remaining Time To Live (TTL) for the Redis key in the time unit
	 * specified in {@code @OrangeRedisKey} annotation's {@code expirationTime}.
	 * 
	 * <p>Example:
	 * <pre>{@code
	 * @OrangeRedisKey(
	 *     key = "token:{tokenId}",
	 *     expirationTime = {@code @Timeout(value = 60, unit = TimeUnit.MINUTES)}
	 * )
	 * public interface TokenOperations extends GlobalOperationsTemplate {
	 *     // ... other methods
	 * }
	 * 
	 * // Usage:
	 * Long ttl = tokenOps.getExpiration();
	 * if (ttl != null && ttl &lt; 300) { // Less than 5 minutes
	 *     // Refresh token
	 * }
	 * }</pre>
	 * 
	 * @return the remaining TTL in the configured time unit, or:
	 *         <ul>
	 *           <li>-2 if the key doesn't exist</li>
	 *           <li>-1 if the key exists but has no expiration set</li>
	 *           <li>null if the operation failed</li>
	 *         </ul>
	 */
	@GetExpiration
	Long getExpiration();
	
	/**
	 * Gets the remaining Time To Live (TTL) for the Redis key in the specified time unit.
	 * This method allows querying the TTL in a different time unit than what's configured
	 * in the {@code @OrangeRedisKey} annotation.
	 * 
	 * <p>Example:
	 * <pre>{@code
	 * @OrangeRedisKey(
	 *     key = "cache:{key}",
	 *     expirationTime = {@code @Timeout(value = 24, unit = TimeUnit.HOURS)}
	 * )
	 * public interface CacheOperations extends GlobalOperationsTemplate {
	 *     // ... other methods
	 * }
	 * 
	 * // Usage:
	 * Long minutesLeft = cacheOps.getExpiration(TimeUnit.MINUTES);
	 * if (minutesLeft != null && minutesLeft &lt; 30) { // Less than 30 minutes
	 *     // Refresh cache
	 * }
	 * }</pre>
	 * 
	 * @param unit the time unit for the returned TTL value
	 * @return the remaining TTL in the specified time unit, or:
	 *         <ul>
	 *           <li>-2 if the key doesn't exist</li>
	 *           <li>-1 if the key exists but has no expiration set</li>
	 *           <li>null if the operation failed</li>
	 *         </ul>
	 */
	@GetExpiration
	Long getExpiration(@TimeoutUnit TimeUnit unit);
}