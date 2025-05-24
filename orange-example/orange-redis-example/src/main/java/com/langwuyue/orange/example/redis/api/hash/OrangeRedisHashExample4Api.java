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

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import com.langwuyue.orange.redis.annotation.OrangeRedisKey;
import com.langwuyue.orange.redis.annotation.Timeout;
import com.langwuyue.orange.redis.template.hash.LongOperationsTemplate;

/**
 * Example interface demonstrating Redis hash operations for Long values.
 * 
 * <p>Specialized for numeric operations on hash fields, providing:
 * <ul>
 *   <li>Atomic increment/decrement operations</li>
 *   <li>Bulk numeric operations</li>
 *   <li>Efficient storage of numeric counters</li>
 * </ul>
 * 
 * <p>Extends {@link LongOperationsTemplate} which provides numeric-specific commands:
 * <ul>
 *   <li>{@code HINCRBY} - Atomic increment/decrement</li>
 *   <li>{@code HGET} - Get numeric value</li>
 *   <li>{@code HMGET} - Get multiple numeric values</li>
 *   <li>{@code HGETALL} - Get all numeric fields</li>
 * </ul>
 * 
 * <p>Key configuration:
 * <ul>
 *   <li>Fixed hash key: "orange:hash:example4"</li>
 *   <li>Default expiration: 1 hour</li>
 * </ul>
 * 
 * <p>Example usage:
 * <pre>{@code
 * // Initialize counters
 * api.put("page_views", 0L);
 * api.put("downloads", 0L);
 * 
 * // Atomic increments
 * api.increment("page_views", 1); // Increase by 1
 * api.increment("downloads", 5);  // Increase by 5
 * 
 * // Bulk operations
 * Map<String, Long> stats = api.getAllMembers();
 * }</pre>
 * 
 * @author Liang.Zhong
 * @since 1.0.0
 * @see LongOperationsTemplate Base template providing numeric hash operations
 * @see OrangeRedisHashExample1Api JSON hash operations
 * @see OrangeRedisHashExample2Api String hash operations
 * @see com.langwuyue.orange.redis.annotation.OrangeRedisKey Key configuration
 * @see com.langwuyue.orange.redis.annotation.Timeout Expiration configuration
 */
@OrangeRedisKey(expirationTime = @Timeout(value = 1, unit = TimeUnit.HOURS), key = "orange:hash:example4")
public interface OrangeRedisHashExample4Api extends LongOperationsTemplate {

	@Override
	default List<Long> getValues() {
		
		return null;
	}

	@Override
	default Long get(String key) {
		
		return null;
	}

	@Override
	default Map<String, Long> get(Collection<String> key) {
		
		return null;
	}

	@Override
	default Map<String, Long> getAllMembers() {
		
		return null;
	}
	
}