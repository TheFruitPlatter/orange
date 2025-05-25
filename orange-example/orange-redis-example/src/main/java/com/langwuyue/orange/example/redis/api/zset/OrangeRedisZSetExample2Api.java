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
package com.langwuyue.orange.example.redis.api.zset;

import java.util.concurrent.TimeUnit;

import com.langwuyue.orange.redis.annotation.OrangeRedisKey;
import com.langwuyue.orange.redis.annotation.Timeout;
import com.langwuyue.orange.redis.template.zset.StringOperationsTemplate;

/**
 * Redis Sorted Set (ZSet) advanced operations interface
 * 
 * <p>Provides extended operations for Redis Sorted Set data structure including:
 * <ul>
 *   <li>Score range operations with limit and offset</li>
 *   <li>Reverse score range queries</li>
 *   <li>Lexicographical range operations</li>
 *   <li>Composite range queries</li>
 * </ul>
 * 
 * <p>Default expiration time: 1 hour
 * 
 * @author Liang.Zhong
 * @since 1.0.0
 * @see <a href="https://redis.io/commands#sorted_set">Redis Sorted Set Commands</a>
 */
@OrangeRedisKey(expirationTime = @Timeout(value = 1, unit = TimeUnit.HOURS), key = "orange:zset:example2")
public interface OrangeRedisZSetExample2Api extends StringOperationsTemplate {

	
}