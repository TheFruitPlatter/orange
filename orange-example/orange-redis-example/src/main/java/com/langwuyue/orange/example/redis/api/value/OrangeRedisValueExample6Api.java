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
package com.langwuyue.orange.example.redis.api.value;

import java.util.concurrent.TimeUnit;

import com.langwuyue.orange.redis.annotation.OrangeRedisKey;
import com.langwuyue.orange.redis.annotation.Timeout;
import com.langwuyue.orange.redis.template.value.LockOperationsTemplate;

/**
 * Example interface demonstrating Redis distributed lock operations.
 * 
 * <p>Extends {@link LockOperationsTemplate} which provides Redis-based distributed locking using:
 * <ul>
 *   <li>{@code SETNX} - Atomic lock acquisition (set if not exists)</li>
 *   <li>{@code DEL} - Lock release</li>
 *   <li>{@code PEXPIRE} - Lock timeout management</li>
 * </ul>
 * 
 * <p>Key configuration:
 * <ul>
 *   <li>Fixed lock key: "orange:value:example6"</li>
 *   <li>Default lock timeout: 1 hour</li>
 * </ul>
 * 
 * <p>Common patterns:
 * <pre>{@code
 * // Try to acquire lock
 * boolean acquired = redisTemplate.tryLock();
 * 
 * // Block until lock acquired
 * redisTemplate.lock();
 * 
 * // Release lock
 * redisTemplate.unlock();
 * }</pre>
 * 
 * <p>Note: Redis locks are advisory only - all clients must cooperate by checking the lock.</p>
 * 
 * @author Liang.Zhong
 * @since 1.0.0
 * @see LockOperationsTemplate Base template providing Redis lock operations
 * @see com.langwuyue.orange.redis.annotation.OrangeRedisKey Key configuration
 * @see com.langwuyue.orange.redis.annotation.Timeout Expiration configuration
 */
@OrangeRedisKey(expirationTime = @Timeout(value = 1, unit = TimeUnit.HOURS), key = "orange:value:example6")
public interface OrangeRedisValueExample6Api extends LockOperationsTemplate {

	
}