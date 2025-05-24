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
package com.langwuyue.orange.example.redis.api.hash;

import java.util.concurrent.TimeUnit;

import com.langwuyue.orange.redis.annotation.OrangeRedisKey;
import com.langwuyue.orange.redis.annotation.Timeout;
import com.langwuyue.orange.redis.template.hash.StringOperationsTemplate;

/**
 * Example interface demonstrating Redis hash operations for String values.
 * 
 * <p>Similar to {@link OrangeRedisHashExample1Api} but stores String values instead of JSON,
 * making it more suitable for:
 * <ul>
 *   <li>Simple string key-value pairs</li>
 *   <li>Cases where JSON serialization overhead isn't needed</li>
 *   <li>Basic field-value storage with better performance</li>
 * </ul>
 * 
 * <p>Extends {@link StringOperationsTemplate} which provides String-specific hash commands:
 * <ul>
 *   <li>{@code HSET} - Set string field value</li>
 *   <li>{@code HGET} - Get string field value</li>
 *   <li>{@code HINCRBY} - Increment numeric field value</li>
 *   <li>{@code HSTRLEN} - Get string field length</li>
 *   <li>{@code HMSET} - Set multiple string fields</li>
 * </ul>
 * 
 * <p>Key configuration:
 * <ul>
 *   <li>Fixed hash key: "orange:hash:example2"</li>
 *   <li>Default expiration: 1 hour</li>
 * </ul>
 * 
 * <p>Example usage:
 * <pre>{@code
 * // Store string values
 * hashTemplate.put("username", "john_doe");
 * hashTemplate.put("login_count", "42");
 * 
 * // Get single value
 * String username = hashTemplate.get("username");
 * 
 * // Increment numeric value
 * hashTemplate.increment("login_count", 1);
 * }</pre>
 * 
 * @author Liang.Zhong
 * @since 1.0.0
 * @see StringOperationsTemplate Base template providing String hash operations
 * @see OrangeRedisHashExample1Api JSON hash operations example
 * @see com.langwuyue.orange.redis.annotation.OrangeRedisKey Key configuration
 * @see com.langwuyue.orange.redis.annotation.Timeout Expiration configuration
 */
@OrangeRedisKey(expirationTime = @Timeout(value = 1, unit = TimeUnit.HOURS), key = "orange:hash:example2")
public interface OrangeRedisHashExample2Api extends StringOperationsTemplate {

	
}