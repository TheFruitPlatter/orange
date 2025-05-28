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

import com.langwuyue.orange.redis.RedisValueTypeEnum;
import com.langwuyue.orange.redis.annotation.Decrement;
import com.langwuyue.orange.redis.annotation.Increment;
import com.langwuyue.orange.redis.annotation.RedisValue;
import com.langwuyue.orange.redis.annotation.hash.HashKey;
import com.langwuyue.orange.redis.annotation.hash.OrangeRedisHashClient;

/**
 * Interface template for Redis Hash operations with Long values.
 * This template extends {@link JSONOperationsTemplate} and provides optimized
 * operations for Long values, including atomic increment and decrement operations.
 * 
 * <p>Key features:
 * <ul>
 *   <li>Direct Long value storage without serialization</li>
 *   <li>Atomic increment/decrement operations</li>
 *   <li>String-based hash keys with Long values</li>
 *   <li>High performance for numeric operations</li>
 *   <li>Thread-safe operations</li>
 *   <li>Automatic type conversion</li>
 * </ul>
 * 
 * <p>Usage example:
 * <pre>{@code
 * // 1. Define your Redis Hash interface
 * @OrangeRedisKey(
 *     expirationTime = @Timeout(value = 1, unit = TimeUnit.DAYS),
 *     key = "orange:hash:counters"
 * )
 * public interface CounterHash extends LongOperationsTemplate {
 *     // Inherit all operations from template
 * }
 * 
 * // 2. Use in your service
 * {@code @Service}
 * public class CounterService {
 *     {@code @Autowired}
 *     private CounterHash counters;
 *     
 *     public void incrementPageView(String page) {
 *         Long newCount = counters.increment(page);
 *         log.info("Page {} views: {}", page, newCount);
 *     }
 *     
 *     public void addVisitors(String page, long visitors) {
 *         Long newCount = counters.increment(page, visitors);
 *         log.info("Page {} total visitors: {}", page, newCount);
 *     }
 *     
 *     public void decrementStock(String product, long quantity) {
 *         Long remaining = counters.decrement(product, quantity);
 *         if (remaining < 0) {
 *             // Revert the decrement
 *             counters.increment(product, quantity);
 *             throw new OutOfStockException("Product out of stock");
 *         }
 *     }
 *     
 *     public Map<String, Long> getAllCounters() {
 *         return counters.getAllMembers();
 *     }
 * }
 * }</pre>
 * 
 * <p>Performance considerations:
 * <ul>
 *   <li>Uses Redis NUMBER data type directly</li>
 *   <li>No serialization/deserialization overhead</li>
 *   <li>Atomic operations for increment/decrement</li>
 *   <li>Optimized for counter use cases</li>
 *   <li>Better memory usage than JSON storage</li>
 * </ul>
 * 
 * <p>Common use cases:
 * <ul>
 *   <li>Page view counters</li>
 *   <li>Rate limiting</li>
 *   <li>Inventory tracking</li>
 *   <li>Score/points systems</li>
 *   <li>Statistics collection</li>
 * </ul>
 * 
 * <p>Implementation notes:
 * <ul>
 *   <li>All methods are implemented by the framework at runtime</li>
 *   <li>Atomic operations are guaranteed by Redis</li>
 *   <li>Thread-safe operations</li>
 *   <li>Automatic connection handling</li>
 *   <li>Integrated error handling</li>
 * </ul>
 * 
 * @author Liang.Zhong
 * @since 1.0.0
 * @see JSONOperationsTemplate
 * @see com.langwuyue.orange.redis.annotation.hash.OrangeRedisHashClient
 * @see com.langwuyue.orange.redis.annotation.OrangeRedisKey
 */
@OrangeRedisHashClient(
    hashKeyType = RedisValueTypeEnum.STRING,  // Specifies that hash keys are of String type
    hashValueType = RedisValueTypeEnum.LONG   // Specifies that hash values are of Long type
)
public interface LongOperationsTemplate extends JSONOperationsTemplate<Long> {
    
    /**
     * Atomically increments the value associated with the given key by 1.
     * This operation is equivalent to the Redis HINCRBY command with increment of 1.
     * If the key does not exist, it is initialized with 0 before performing the increment.
     * 
     * <p>Example:
     * <pre>{@code
     * // Increment page view counter
     * Long views = counterHash.increment("page:/home");
     * log.info("Home page views: {}", views);
     * }</pre>
     * 
     * @param key The hash key to increment
     * @return The new value after increment
     */
    @Increment
    Long increment(@HashKey String key);
    
    /**
     * Atomically increments the value associated with the given key by the specified delta.
     * This operation is equivalent to the Redis HINCRBY command.
     * If the key does not exist, it is initialized with 0 before performing the increment.
     * 
     * <p>Example:
     * <pre>{@code
     * // Add multiple visitors
     * Long totalVisitors = counterHash.increment("visitors:/shop", 5L);
     * log.info("Shop total visitors: {}", totalVisitors);
     * 
     * // Track points in a game
     * Long newScore = counterHash.increment("player:123:score", 100L);
     * log.info("Player new score: {}", newScore);
     * }</pre>
     * 
     * @param key The hash key to increment
     * @param delta The amount to increment by (can be negative for decrement)
     * @return The new value after increment
     */
    @Increment
    Long increment(@HashKey String key, @RedisValue Long delta);
    
    /**
     * Atomically decrements the value associated with the given key by 1.
     * This operation is equivalent to the Redis HINCRBY command with increment of -1.
     * If the key does not exist, it is initialized with 0 before performing the decrement.
     * 
     * <p>Example:
     * <pre>{@code
     * // Decrement available slots
     * Long remainingSlots = counterHash.decrement("slots:meeting-room-1");
     * if (remainingSlots < 0) {
     *     // Revert the decrement
     *     counterHash.increment("slots:meeting-room-1");
     *     throw new NoAvailableSlotsException("No slots available");
     * }
     * }</pre>
     * 
     * @param key The hash key to decrement
     * @return The new value after decrement
     */
    @Decrement
    Long decrement(@HashKey String key);
    
    /**
     * Atomically decrements the value associated with the given key by the specified delta.
     * This operation is equivalent to the Redis HINCRBY command with a negative increment.
     * If the key does not exist, it is initialized with 0 before performing the decrement.
     * 
     * <p>Example:
     * <pre>{@code
     * // Decrease inventory
     * Long remainingStock = counterHash.decrement("stock:product-123", 5L);
     * if (remainingStock < 0) {
     *     // Revert the decrement
     *     counterHash.increment("stock:product-123", 5L);
     *     throw new InsufficientStockException("Not enough stock available");
     * }
     * 
     * // Deduct points
     * Long newBalance = counterHash.decrement("user:456:points", 100L);
     * log.info("User points after deduction: {}", newBalance);
     * }</pre>
     * 
     * @param key The hash key to decrement
     * @param delta The amount to decrement by (can be negative for increment)
     * @return The new value after decrement
     */
    @Decrement
    Long decrement(@HashKey String key, @RedisValue Long delta);
}