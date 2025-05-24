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
 * Example interface demonstrating Redis distributed lock operations (separate instance).
 * 
 * <p>Extends {@link LockOperationsTemplate} with identical functionality to 
 * {@link OrangeRedisValueExample6Api} but using a different lock key. This demonstrates
 * how to create multiple independent lock instances.
 * 
 * <p>Redis commands used:
 * <ul>
 *   <li>{@code SETNX} - Atomic lock acquisition</li>
 *   <li>{@code DEL} - Lock release</li>
 *   <li>{@code PEXPIRE} - Lock timeout management</li>
 * </ul>
 * 
 * <p>Key configuration:
 * <ul>
 *   <li>Fixed lock key: "orange:value:example7"</li>
 *   <li>Default lock timeout: 1 hour</li>
 * </ul>
 * 
 * @author Liang.Zhong
 * @since 1.0.0
 * @see LockOperationsTemplate Base template providing Redis lock operations
 * @see OrangeRedisValueExample6Api Primary lock operations example
 * @see com.langwuyue.orange.redis.annotation.OrangeRedisKey Key configuration
 * @see com.langwuyue.orange.redis.annotation.Timeout Expiration configuration
 */
@OrangeRedisKey(expirationTime = @Timeout(value = 1, unit = TimeUnit.HOURS), key = "orange:value:example7")
public interface OrangeRedisValueExample7Api extends LockOperationsTemplate {

	
}