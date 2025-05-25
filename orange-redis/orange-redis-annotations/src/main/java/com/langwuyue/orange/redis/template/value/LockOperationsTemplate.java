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
package com.langwuyue.orange.redis.template.value;

import com.langwuyue.orange.redis.RedisValueTypeEnum;
import com.langwuyue.orange.redis.annotation.AutoRenew;
import com.langwuyue.orange.redis.annotation.global.SetExpiration;
import com.langwuyue.orange.redis.annotation.value.Lock;
import com.langwuyue.orange.redis.annotation.value.OrangeRedisValueClient;
import com.langwuyue.orange.redis.template.global.GlobalOperationsTemplate;

/**
 * Interface template for Redis Value operations. 
 * Developers should extend this interface and annotate the child interface with {@code OrangeRedisKey}.
 * 
 * <p>A example is:
 * <blockquote><pre>
 *  {@code @OrangeRedisKey(expirationTime = @Timeout(value = 1, unit = TimeUnit.HOURS), key = "orange:value:example1")} 
 *  public interface OrangeRedisValueExample1Api extends LockOperationsTemplate {
 *  	
 *  	// Custom operations can be added here
 *  }
 * </pre></blockquote>
 * 
 * 
 * <p>Please review examples for more information.
 * 
 * @param <T> The type of elements stored in the Redis Value (will be serialized as JSON)
 * @author Liang.Zhong
 * @since 1.0.0
 */
@OrangeRedisValueClient(valueType = RedisValueTypeEnum.STRING)
public interface LockOperationsTemplate extends GlobalOperationsTemplate {
	
	/**
	 * Acquires a distributed Redis lock with automatic renewal and listener notification.
	 * 
	 * <p>Key characteristics:
	 * <ul>
	 *   <li>Atomic lock acquisition using Redis SETNX</li>
	 *   <li>Automatic TTL extension via {@code @AutoRenew}</li>
	 *   <li>Listener notification mechanism</li>
	 *   <li>Deadlock prevention through TTL</li>
	 * </ul>
	 *
	 * <p>Lock lifecycle:
	 * <ol>
	 *   <li>Acquire lock (SETNX with TTL)</li>
	 *   <li>Notify registered listeners</li>
	 *   <li>Listeners execute business logic</li>
	 *   <li>Release lock when done</li>
	 * </ol>
	 *
	 * <p>Automatic renewal conditions:
	 * <ul>
	 *   <li>When remaining TTL ≤ (1/{@link AutoRenew#threshold()}) * key expiration time</li>
	 *   <li>Only extends if lock is still held by current process</li>
	 * </ul>
	 *
	 * <p>Best practices:
	 * <ul>
	 *   <li>Set reasonable TTL based on expected critical section duration</li>
	 *   <li>Implement {@code OrangeRedisValueSetIfAbsentListener} as Spring {@code @Component}</li>
	 *   <li>Ensure listener logic is idempotent</li>
	 * </ul>
	 *
	 * <p>Example usage:
	 * <pre>{@code
	 * // 1. Define lock interface
	 * @OrangeRedisKey("order:lock:${orderId}")
	 * public interface OrderLockApi extends LockOperationsTemplate {
	 *     
	 * }
	 *
	 * // 2. Implement listener to handle lock events
	 * @Component
	 * public class OrderLockListener implements OrangeRedisValueSetIfAbsentListener {
	 *     
	 *     {@code @Override}
	 *     public void onFailure(OrangeSetIfAbsentFailedEvent event) {
	 *         log.warn("Failed to acquire lock for key: {}", event.getKey());
	 *         // Handle lock acquisition failure
	 *     }
	 *     
	 *     {@code @Override}
	 *     public void onSuccess(OrangeSetIfAbsentSuccessEvent event) {
	 *        //  Do business logic
	 *     }
	 * }
	 * }</pre>
	 *
	 * @param args Optional arguments for lock key construction
	 */
	@Lock
	@SetExpiration
	@AutoRenew(autoInitKeyExpirationTime = true)
	void lock(Object... args);
}