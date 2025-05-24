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
package com.langwuyue.orange.example.redis.api.list;

import java.util.concurrent.TimeUnit;

import com.langwuyue.orange.redis.annotation.OrangeRedisKey;
import com.langwuyue.orange.redis.annotation.Timeout;
import com.langwuyue.orange.redis.template.list.StringOperationsTemplate;

/**
 * Redis List operations interface with String value support.
 * 
 * <p>This interface provides Redis List operations for storing and manipulating
 * String values. Unlike {@link OrangeRedisListExample1Api} which works with JSON objects,
 * this interface works directly with String values, offering better performance
 * for simple text storage and manipulation.
 * 
 * <p>Supported Redis commands include:
 * <ul>
 *   <li>LPUSH/RPUSH - Add strings to the left/right of the list</li>
 *   <li>LPOP/RPOP - Remove and return strings from the left/right</li>
 *   <li>LINDEX - Get string by index</li>
 *   <li>LRANGE - Get strings within an index range</li>
 *   <li>LLEN - Get list length</li>
 *   <li>LINSERT - Insert string before/after another string</li>
 *   <li>LSET - Set string value at specific index</li>
 *   <li>LTRIM - Trim list to specified range</li>
 * </ul>
 * 
 * <p>Key configuration:
 * <ul>
 *   <li>Fixed key: "orange:list:example2"</li>
 *   <li>Default expiration: 1 hour</li>
 *   <li>Value type: String (no serialization needed)</li>
 * </ul>
 * 
 * <p>Usage example:
 * <pre>{@code
 * // Add strings to list
 * api.leftPush("first", "second", "third");
 * api.rightPush("fourth", "fifth");
 * 
 * // Get strings by index
 * String element = api.get(0L);
 * List<String> range = api.getByIndexRange(0L, 2L);
 * 
 * // Modify list
 * api.set(1L, "new second");
 * api.insertBefore("third", "before third");
 * api.trim(0L, 4L);  // Keep only first 5 elements
 * 
 * // Pop elements
 * String left = api.leftPop();
 * List<String> rights = api.rightPop(2L);
 * }</pre>
 * 
 * <p>Use cases and advantages:
 * <ul>
 *   <li>Simple text queue or stack implementations</li>
 *   <li>Activity logs or message streams</li>
 *   <li>More efficient than JSON for simple string values</li>
 *   <li>No serialization/deserialization overhead</li>
 * </ul>
 * 
 * @author Liang.Zhong
 * @since 1.0.0
 * @see StringOperationsTemplate Base template for String List operations
 * @see OrangeRedisListExample1Api JSON-based alternative for complex objects
 * @see com.langwuyue.orange.redis.annotation.OrangeRedisKey Key configuration
 */
@OrangeRedisKey(expirationTime = @Timeout(value = 1, unit = TimeUnit.HOURS), key = "orange:list:example2")
public interface OrangeRedisListExample2Api extends StringOperationsTemplate {

	
	
}