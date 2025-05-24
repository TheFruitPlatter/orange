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
import com.langwuyue.orange.redis.template.hash.DoubleOperationsTemplate;


/**
 * Example interface demonstrating Redis hash operations for Double values.
 * 
 * <p>Specialized for floating-point numeric operations on hash fields, providing:
 * <ul>
 *   <li>Precise storage of decimal values</li>
 *   <li>Atomic increment/decrement operations</li>
 *   <li>Bulk floating-point operations</li>
 * </ul>
 * 
 * <p>Extends {@link DoubleOperationsTemplate} which provides floating-point commands:
 * <ul>
 *   <li>{@code HINCRBYFLOAT} - Atomic floating-point increment</li>
 *   <li>{@code HGET} - Get double value</li>
 *   <li>{@code HMGET} - Get multiple double values</li>
 *   <li>{@code HGETALL} - Get all double fields</li>
 * </ul>
 * 
 * <p>Key configuration:
 * <ul>
 *   <li>Fixed hash key: "orange:hash:example5"</li>
 *   <li>Default expiration: 1 hour</li>
 * </ul>
 * 
 * <p>Example usage:
 * <pre>{@code
 * // Initialize metrics
 * api.put("temperature", 23.5);
 * api.put("humidity", 65.2);
 * 
 * // Atomic increments
 * api.increment("temperature", 0.5); // Increase by 0.5
 * api.increment("humidity", -2.1);    // Decrease by 2.1
 * 
 * // Bulk operations
 * Map<String, Double> metrics = api.getAllMembers();
 * }</pre>
 * 
 * <p>Note: When precision is critical, consider using {@link BigDecimal} via
 * {@link OrangeRedisHashExample1Api} JSON operations instead of native doubles.
 * 
 * @author Liang.Zhong
 * @since 1.0.0
 * @see DoubleOperationsTemplate Base template providing floating-point hash operations
 * @see OrangeRedisHashExample4Api Long (integer) hash operations
 * @see OrangeRedisHashExample1Api JSON hash operations for precise decimals
 * @see com.langwuyue.orange.redis.annotation.OrangeRedisKey Key configuration
 * @see com.langwuyue.orange.redis.annotation.Timeout Expiration configuration
 */
@OrangeRedisKey(expirationTime = @Timeout(value = 1, unit = TimeUnit.HOURS), key = "orange:hash:example5")
public interface OrangeRedisHashExample5Api extends DoubleOperationsTemplate {

	@Override
	default List<Double> getValues() {
		
		return null;
	}

	@Override
	default Double get(String key) {
		
		return null;
	}

	@Override
	default Map<String, Double> get(Collection<String> key) {
		
		return null;
	}

	@Override
	default Map<String, Double> getAllMembers() {
		
		return null;
	}
}