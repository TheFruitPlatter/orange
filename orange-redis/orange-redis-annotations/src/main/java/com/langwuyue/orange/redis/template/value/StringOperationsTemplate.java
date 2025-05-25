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
import com.langwuyue.orange.redis.annotation.value.OrangeRedisValueClient;

/**
 * Interface template for Redis Value operations. 
 * Developers should extend this interface and annotate the child interface with {@code OrangeRedisKey}.
 * 
 * <p>A example is:
 * <blockquote><pre>
 *  {@code @OrangeRedisKey(expirationTime = @Timeout(value = 1, unit = TimeUnit.HOURS), key = "orange:value:example1")} 
 *  public interface OrangeRedisValueExample1Api extends StringOperationsTemplate {
 *  
 *  }
 * </pre></blockquote>
 * 
 * 
 * <p>Please review examples for more information.
 * 
 * @author Liang.Zhong
 * @since 1.0.0
 */
@OrangeRedisValueClient(valueType = RedisValueTypeEnum.STRING)
public interface StringOperationsTemplate extends JSONOperationsTemplate<String> {
	
	/**
	 * Retrieves the string value from Redis.
	 * <p>
	 * This is the fundamental GET operation for string values in Redis.
	 *
	 * <p>Key characteristics:
	 * <ul>
	 *   <li>O(1) time complexity</li>
	 *   <li>Returns null if key does not exist</li>
	 *   <li>Supports strings up to 512MB in size</li>
	 * </ul>
	 *
	 * <p>Typical use cases:
	 * <ul>
	 *   <li>Retrieving cached string data</li>
	 *   <li>Getting configuration values</li>
	 *   <li>Reading serialized objects stored as strings</li>
	 * </ul>
	 *
	 * <p>Example usage:
	 * <pre>{@code
	 * String cachedData = getValue();
	 * if (cachedData != null) {
	 *     // Use cached data
	 * } else {
	 *     // Fetch from primary source
	 * }
	 * }</pre>
	 *
	 * @return the string value, or null if key does not exist
	 * @throws RedisConnectionFailureException if unable to communicate with Redis
	 * @throws SerializationException if value cannot be deserialized
	 */
	@Override
	String getValue();
}