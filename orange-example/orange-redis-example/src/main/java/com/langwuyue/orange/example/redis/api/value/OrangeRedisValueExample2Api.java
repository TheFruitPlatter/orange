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
import com.langwuyue.orange.redis.template.value.LongOperationsTemplate;

/**
 * Example interface demonstrating Redis value operations for Long values.
 * 
 * <p>This interface extends {@link LongOperationsTemplate} to provide Redis value operations
 * for primitive {@code long} values. Compared to {@link OrangeRedisValueExample1Api} which
 * handles JSON objects, this interface works with simple numeric values.</p>
 * 
 * <p>Redis key configuration:
 * <ul>
 *   <li>Fixed key prefix: "orange:value:example2"</li>
 *   <li>Default expiration: 1 hour</li>
 * </ul>
 * 
 * <p>Common operations include:
 * <ul>
 *   <li>{@code INCR} - Increment value</li>
 *   <li>{@code DECR} - Decrement value</li>
 *   <li>{@code GET} - Retrieve value</li>
 *   <li>{@code SET} - Store value</li>
 * </ul>
 * 
 * @author Liang.Zhong
 * @since 1.0.0
 * @see LongOperationsTemplate Base template providing Long-specific Redis operations
 * @see OrangeRedisValueExample1Api Example using JSON object values
 */
@OrangeRedisKey(expirationTime = @Timeout(value = 1, unit = TimeUnit.HOURS), key = "orange:value:example2")
public interface OrangeRedisValueExample2Api extends LongOperationsTemplate {
	
}