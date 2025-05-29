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
 * Marks a class as a listener for Redis transaction timeout events.
 * This annotation is used to define listeners that will be notified
 * when Redis transactions associated with specific keys timeout.
 *
 * <p>Classes annotated with {@code @OrangeRedisTxTimeoutListener} must implement
 * appropriate handler methods to process timeout events. The listener will be
 * triggered when a Redis transaction exceeds its configured timeout period.</p>
 *
 * <p>This annotation is particularly useful for:</p>
 * <ul>
 *   <li>Implementing fallback mechanisms for timed-out transactions</li>
 *   <li>Logging and monitoring transaction timeouts</li>
 * </ul>
 *
 * @see OrangeRedisKey
 * @see OrangeRedisOriginalKey
 * @author Liang.Zhong
 * @since 1.0.0
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface OrangeRedisTxTimeoutListener {
	
	/**
	 * Specifies the class that defines the Redis key pattern for transaction monitoring.
	 * The specified class must be annotated with {@code @OrangeRedisKey} and defines
	 * the key pattern for which this listener will receive transaction timeout events.
	 *
	 * <p>When a Redis transaction involving keys matching the pattern defined in the
	 * specified class times out, this listener will be notified. The key pattern
	 * helps scope the listener to specific types of transactions.</p>
	 *
	 * <p>For example, if monitoring user-related transactions:</p>
	 * <pre>
	 * {@code @OrangeRedisKey("user:transaction:${userId}")}
	 * public class UserTransactionCache {
	 *     // Cache implementation
	 * }
	 * </pre>
	 *
	 * @return the class annotated with {@code @OrangeRedisKey} that defines the
	 *         key pattern for transaction monitoring
	 */
	Class<?> key();
	
}