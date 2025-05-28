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
package com.langwuyue.orange.redis.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Configures Redis key settings for Orange Redis operations.
 * This annotation is used at the interface or class level to define Redis key patterns
 * and expiration settings, providing a type-safe way to manage Redis keys.
 * 
 * <p>Key Features:
 * <ul>
 *   <li>Dynamic key generation with variable placeholders</li>
 *   <li>Support for Spring Expression Language (SpEL)</li>
 *   <li>Configurable expiration policies</li>
 *   <li>Type-safe key management</li>
 * </ul>
 * 
 * <p>Basic Usage Examples:
 * 
 * <p>1. Simple Key Pattern:
 * <pre>{@code
 * @OrangeRedisKey(key = "user:profile:{userId}")
 * public interface UserProfileOperations {
 *     @GetValue
 *     UserProfile getUserProfile(@KeyVariable("userId") String userId);
 *     
 *     @SetValue
 *     void saveUserProfile(@KeyVariable("userId") String userId, @RedisValue UserProfile profile);
 * }
 * }</pre>
 * 
 * <p>2. With Expiration Time:
 * <pre>{@code
 * @OrangeRedisKey(
 *     key = "session:{sessionId}",
 *     expirationTime = @Timeout(value = 30, unit = TimeUnit.MINUTES)
 * )
 * public interface SessionOperations {
 *     @GetValue
 *     SessionData getSession(@KeyVariable("sessionId") String sessionId);
 *     
 *     @SetValue
 *     void saveSession(@KeyVariable("sessionId") String sessionId, @RedisValue SessionData data);
 * }
 * }</pre>
 * 
 * <p>3. Composite Keys:
 * <pre>{@code
 * @OrangeRedisKey(key = "product:{categoryId}:{productId}")
 * public interface ProductOperations {
 *     @GetValue
 *     ProductDetails getProduct(
 *         @KeyVariable("categoryId") String categoryId,
 *         @KeyVariable("productId") String productId
 *     );
 * }
 * }</pre>
 * 
 * <p>4. Namespaced Keys:
 * <pre>{@code
 * @OrangeRedisKey(key = "app:inventory:{itemId}:stock")
 * public interface InventoryOperations {
 *     @GetValue
 *     int getStockLevel(@KeyVariable("itemId") String itemId);
 *     
 *     @SetValue
 *     void updateStock(@KeyVariable("itemId") String itemId, @RedisValue int quantity);
 * }
 * }</pre>
 * 
 * <p>Key Pattern Guidelines:
 * <ul>
 *   <li>Use colon (:) as a separator between key parts</li>
 *   <li>Place variable parts in curly braces: {variableName}</li>
 *   <li>Consider using namespaces to organize keys</li>
 *   <li>Keep keys descriptive but concise</li>
 *   <li>Use consistent naming conventions</li>
 * </ul>
 * 
 * <p>Best Practices:
 * <ul>
 *   <li>Key Structure:
 *     <ul>
 *       <li>Use object-type:id pattern (e.g., user:1000)</li>
 *       <li>Include application name for shared Redis instances</li>
 *       <li>Group related keys under common prefixes</li>
 *       <li>Consider environment separation (dev/test/prod)</li>
 *     </ul>
 *   </li>
 *   <li>Performance:
 *     <ul>
 *       <li>Keep keys short but meaningful</li>
 *       <li>Avoid very long keys (memory overhead)</li>
 *       <li>Consider key scan patterns</li>
 *       <li>Plan for key expiration</li>
 *     </ul>
 *   </li>
 *   <li>Maintenance:
 *     <ul>
 *       <li>Document key patterns</li>
 *       <li>Consider versioning for schema changes</li>
 *       <li>Plan for key migration strategies</li>
 *       <li>Monitor key usage and distribution</li>
 *     </ul>
 *   </li>
 * </ul>
 * 
 * @author Liang.Zhong
 * @since 1.0.0
 * @see KeyVariable
 * @see GetValue
 * @see SetValue
 * @see Timeout
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface OrangeRedisKey {
	
	/**
	 * Specifies the Redis key or key pattern.
	 * Supports dynamic key generation using Spring Expression Language (SpEL)
	 * and variable placeholders.
	 * 
	 * <p>Variable Placeholder Format:
	 * <pre>{variableName}</pre>
	 * 
	 * <p>Each variable in the key pattern must have a corresponding method parameter
	 * annotated with {@link KeyVariable} with a matching name.
	 * 
	 * <p>Examples:
	 * <ul>
	 *   <li>"user:simple" - Simple key</li>
	 *   <li>"user:${userId}" - Simple variable</li>
	 * </ul>
	 * 
	 * @return the Redis key pattern
	 */
	String key();
	
	/**
	 * Configures the Time To Live (TTL) for the Redis key.
	 * The expiration time is not automatically applied and requires explicit
	 * activation through a method annotated with {@code @SetExpiration}.
	 * 
	 * <p>This setting defines the default expiration policy for all keys
	 * generated from this pattern. Individual operations can override this
	 * setting with method-level {@link Timeout} annotations.
	 * 
	 * 
	 * <p>Note: Setting an expiration time does not automatically apply it.
	 * You must either:
	 * <ul>
	 *   <li>Use {@code @SetValue} with {@code @Timeout} annotation</li>
	 *   <li>Create a dedicated method with {@code @SetExpiration} annotation</li>
	 * </ul>
	 * 
	 * @return the timeout configuration for this key
	 * @see Timeout
	 */
	Timeout expirationTime();
}