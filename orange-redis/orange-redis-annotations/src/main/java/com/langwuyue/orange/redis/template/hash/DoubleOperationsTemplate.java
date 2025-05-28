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
 * Interface template for Redis Hash operations with Double values.
 * This template extends {@link JSONOperationsTemplate} and provides optimized
 * operations for Double values, including precise floating-point arithmetic operations.
 * 
 * <p>Key features:
 * <ul>
 *   <li>Direct Double value storage without serialization</li>
 *   <li>Precise floating-point arithmetic</li>
 *   <li>String-based hash keys with Double values</li>
 *   <li>High performance for decimal operations</li>
 *   <li>Thread-safe operations</li>
 *   <li>Automatic type conversion</li>
 * </ul>
 * 
 * <p>Usage example:
 * <pre>{@code
 * // 1. Define your Redis Hash interface
 * @OrangeRedisKey(
 *     expirationTime = @Timeout(value = 1, unit = TimeUnit.DAYS),
 *     key = "orange:hash:prices"
 * )
 * public interface PriceHash extends DoubleOperationsTemplate {
 *     // Inherit all operations from template
 * }
 * 
 * // 2. Use in your service
 * {@code @Service}
 * public class PriceService {
 *     {@code @Autowired}
 *     private PriceHash prices;
 *     
 *     public void applyDiscount(String product, double discountPercent) {
 *         Double currentPrice = prices.get(product);
 *         if (currentPrice != null) {
 *             double discount = currentPrice * (discountPercent / 100.0);
 *             prices.decrement(product, discount);
 *         }
 *     }
 *     
 *     public void increasePrice(String product, double amount) {
 *         Double newPrice = prices.increment(product, amount);
 *         log.info("New price for {}: ${}", product, newPrice);
 *     }
 *     
 *     public Map<String, Double> getAllPrices() {
 *         return prices.getAllMembers();
 *     }
 * }
 * }</pre>
 * 
 * <p>Precision considerations:
 * <ul>
 *   <li>Uses double-precision floating-point format (IEEE 754)</li>
 *   <li>Maintains approximately 15-17 decimal digits of precision</li>
 *   <li>Suitable for most business calculations</li>
 *   <li>For high-precision financial calculations, consider using {@code BigDecimal}</li>
 *   <li>Be aware of floating-point arithmetic limitations</li>
 * </ul>
 * 
 * <p>Performance considerations:
 * <ul>
 *   <li>Uses Redis NUMBER data type directly</li>
 *   <li>No serialization/deserialization overhead</li>
 *   <li>Atomic operations for increment/decrement</li>
 *   <li>Optimized for floating-point operations</li>
 *   <li>Better memory usage than JSON storage</li>
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
    hashValueType = RedisValueTypeEnum.DOUBLE // Specifies that hash values are of Double type
)
public interface DoubleOperationsTemplate extends JSONOperationsTemplate<Double> {
    
    /**
     * Atomically increments the value associated with the given key by the specified delta.
     * This operation is equivalent to the Redis HINCRBYFLOAT command.
     * If the key does not exist, it is initialized with 0 before performing the increment.
     * 
     * <p>Example:
     * <pre>{@code
     * // Increase product price
     * Double newPrice = priceHash.increment("product:123", 10.50);
     * log.info("New price: ${}", newPrice);
     * 
     * // Add interest rate
     * Double newRate = rateHash.increment("USD:interest", 0.25);
     * log.info("New interest rate: {}%", newRate);
     * }</pre>
     * 
     * <p>Note on precision:
     * <ul>
     *   <li>Uses double-precision floating-point arithmetic</li>
     *   <li>May have small rounding errors due to binary floating-point representation</li>
     *   <li>For exact decimal arithmetic, consider storing cents as Long values</li>
     * </ul>
     * 
     * @param key The hash key to increment
     * @param delta The amount to increment by (can be negative for decrement)
     * @return The new value after increment
     */
    @Increment
    Double increment(@HashKey String key, @RedisValue Double delta);
    
    /**
     * Atomically decrements the value associated with the given key by the specified delta.
     * This operation is equivalent to the Redis HINCRBYFLOAT command with a negative increment.
     * If the key does not exist, it is initialized with 0 before performing the decrement.
     * 
     * <p>Example:
     * <pre>{@code
     * // Apply discount to price
     * Double discountedPrice = priceHash.decrement("product:123", 5.99);
     * log.info("Price after discount: ${}", discountedPrice);
     * 
     * // Decrease temperature
     * Double newTemp = tempHash.decrement("sensor:1", 0.5);
     * log.info("New temperature: {}°C", newTemp);
     * }</pre>
     * 
     * <p>Note on precision:
     * <ul>
     *   <li>Uses double-precision floating-point arithmetic</li>
     *   <li>May have small rounding errors due to binary floating-point representation</li>
     *   <li>For exact decimal arithmetic, consider storing cents as Long values</li>
     * </ul>
     * 
     * @param key The hash key to decrement
     * @param delta The amount to decrement by (can be negative for increment)
     * @return The new value after decrement
     */
    @Decrement
    Double decrement(@HashKey String key, @RedisValue Double delta);
}