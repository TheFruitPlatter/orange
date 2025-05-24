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
import com.langwuyue.orange.redis.template.value.StringOperationsTemplate;

/**
 * Example interface demonstrating Redis operations for String values.
 * 
 * <p>Extends {@link StringOperationsTemplate} which provides common Redis string commands including:
 * <ul>
 *   <li>{@code SET} - Set string value</li>
 *   <li>{@code GET} - Get string value</li>
 *   <li>{@code GETSET} - Atomically set and return old value</li>
 *   <li>{@code STRLEN} - Get string length</li>
 *   <li>{@code APPEND} - Append to string</li>
 *   <li>{@code SETRANGE} - Overwrite part of string</li>
 *   <li>{@code GETRANGE} - Get substring</li>
 * </ul>
 * 
 * <p>Key configuration:
 * <ul>
 *   <li>Fixed key: "orange:value:example5"</li>
 *   <li>Default expiration: 1 hour</li>
 * </ul>
 * 
 * <p>Example usage:
 * <pre>{@code
 * // Set value
 * redisTemplate.set("Hello World");
 * 
 * // Get value
 * String value = redisTemplate.get();
 * 
 * // Append to value
 * redisTemplate.append("!");
 * }</pre>
 * 
 * @author Liang.Zhong
 * @since 1.0.0
 * @see StringOperationsTemplate Base template providing String Redis operations
 * @see OrangeRedisValueExample1Api Example using JSON object values
 * @see OrangeRedisValueExample2Api Example using Long values
 */
@OrangeRedisKey(expirationTime = @Timeout(value = 1, unit = TimeUnit.HOURS), key = "orange:value:example5")
public interface OrangeRedisValueExample5Api extends StringOperationsTemplate {

	
}