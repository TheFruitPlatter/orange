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
package com.langwuyue.orange.example.redis.api.set;

import java.util.concurrent.TimeUnit;

import com.langwuyue.orange.redis.annotation.OrangeRedisKey;
import com.langwuyue.orange.redis.annotation.Timeout;
import com.langwuyue.orange.redis.template.set.StringOperationsTemplate;

/**
 * Redis Set operations interface with String value support.
 * 
 * <p>This interface provides Redis Set operations for storing and manipulating
 * String values. Unlike {@link OrangeRedisSetExample1Api} which works with JSON objects,
 * this interface works directly with String values, offering better performance
 * for simple text storage and set operations.
 * 
 * <p>Supported Redis commands include:
 * <ul>
 *   <li>SADD - Add string members to the set</li>
 *   <li>SPOP - Remove and return random string members</li>
 *   <li>SRANDMEMBER - Get random string members without removing</li>
 *   <li>SMEMBERS - Get all string members</li>
 *   <li>SISMEMBER - Check if strings are members</li>
 *   <li>SREM - Remove string members</li>
 *   <li>SSCAN - Incrementally iterate set elements</li>
 *   <li>SCARD - Get the number of members in the set</li>
 * </ul>
 * 
 * <p>Key configuration:
 * <ul>
 *   <li>Fixed key: "orange:set:example2"</li>
 *   <li>Default expiration: 1 hour</li>
 *   <li>Value type: String (no serialization needed)</li>
 * </ul>
 * 
 * <p>Usage example:
 * <pre>{@code
 * // Add strings to set
 * api.add("value1", "value2", "value3");
 * 
 * // Check membership
 * boolean isMember = api.isMember("value1");
 * Map<String, Boolean> results = api.isMembers(Arrays.asList("value1", "value4"));
 * 
 * // Get random members
 * String random = api.randomGetOne();
 * List<String> multiple = api.randomGetMembers(3L);
 * 
 * // Get all members
 * Set<String> all = api.getMembers();
 * 
 * // Remove members
 * api.remove("value1");
 * String popped = api.pop();
 * 
 * // Get set size
 * Long size = api.size();
 * }</pre>
 * 
 * <p>Use cases and advantages:
 * <ul>
 *   <li>Unique collections of strings (tags, categories, keywords)</li>
 *   <li>User roles or permissions</li>
 *   <li>IP address filtering or allowlists</li>
 *   <li>More efficient than JSON for simple string values</li>
 *   <li>No serialization/deserialization overhead</li>
 *   <li>Ideal for high-performance set operations</li>
 * </ul>
 * 
 * <p>Performance considerations:
 * <ul>
 *   <li>Set operations are generally O(1), except for getting all members O(N)</li>
 *   <li>String sets are more memory-efficient than JSON sets</li>
 *   <li>Use SSCAN for iterating large sets to avoid blocking</li>
 *   <li>Batch operations (add/remove multiple members) reduce network round-trips</li>
 * </ul>
 * 
 * @author Liang.Zhong
 * @since 1.0.0
 * @see StringOperationsTemplate Base template for String Set operations
 * @see OrangeRedisSetExample1Api JSON-based alternative for complex objects
 * @see com.langwuyue.orange.redis.annotation.OrangeRedisKey Key configuration
 */
@OrangeRedisKey(expirationTime = @Timeout(value = 1, unit = TimeUnit.HOURS), key = "orange:set:example2")
public interface OrangeRedisSetExample2Api extends StringOperationsTemplate {

 
}