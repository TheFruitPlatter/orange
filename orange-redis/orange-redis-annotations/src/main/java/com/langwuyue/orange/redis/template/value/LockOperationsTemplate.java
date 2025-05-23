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
 * </p>
 * 
 * <p>Please review examples for more information.</p>
 * 
 * @param <T> The type of elements stored in the Redis Value (will be serialized as JSON)
 * @author Liang.Zhong
 * @since 1.0.0
 */
@OrangeRedisValueClient(valueType = RedisValueTypeEnum.STRING)
public interface LockOperationsTemplate extends GlobalOperationsTemplate {
	
	/**
	 * Acquires lock, notifies listeners (who will handle business logic)
	 * 
	 * <p>
	 * [Lock] -> [Notify listeners]  (listeners process business logic)-> [Release Lock]
	 * </p>
	 * 
	 * <p>
	 * Once the set operation process is completed, the {@code OrangeRedisValueSetIfAbsentListener} component ​​will be triggered​​. 
	 * Developers should ​​configure​​ {@code OrangeRedisValueSetIfAbsentListener} to manage post-setting business logic. 
	 * Note that the {@code OrangeRedisValueSetIfAbsentListener} implementation class must be annotated with Spring’s {@code @Component}
	 * </p>
	 * 
	 * <p>
	 * This method automatically extends the TTL if the listeners are still executing when the current TTL approaches expiration.
	 * Automatically extends the TTL when:
	 * - Remaining TTL ≤ {@link AutoRenew#threshold()} * {@link com.langwuyue.orange.redis.annotation.OrangeRedisKey#expirationTime()}  
	 * </p>
	 * 
	 * <p>
	 * Please review examples for more information.
	 * </p>
	 */
	@Lock
	@SetExpiration
	@AutoRenew(autoInitKeyExpirationTime = true)
	void lock(Object... args);
}
