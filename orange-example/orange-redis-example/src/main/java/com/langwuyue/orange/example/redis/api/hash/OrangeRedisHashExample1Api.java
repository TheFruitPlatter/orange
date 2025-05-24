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

import com.langwuyue.orange.example.redis.entity.OrangeValueExampleEntity;
import com.langwuyue.orange.redis.annotation.OrangeRedisKey;
import com.langwuyue.orange.redis.annotation.Timeout;
import com.langwuyue.orange.redis.template.hash.JSONOperationsTemplate;

/**
 * Example interface demonstrating Redis hash operations for JSON values.
 * 
 * <p>Redis hashes map string fields to values, making them ideal for:
 * <ul>
 *   <li>Storing objects (field=property, value=property value)</li>
 *   <li>Implementing simple key-value stores within a key</li>
 *   <li>Grouping related fields together</li>
 * </ul>
 * 
 * <p>Extends {@link JSONOperationsTemplate} which provides common Redis hash commands:
 * <ul>
 *   <li>{@code HSET} - Set field value</li>
 *   <li>{@code HGET} - Get field value</li>
 *   <li>{@code HMGET} - Get multiple field values</li>
 *   <li>{@code HGETALL} - Get all fields and values</li>
 *   <li>{@code HDEL} - Delete fields</li>
 *   <li>{@code HEXISTS} - Check if field exists</li>
 * </ul>
 * 
 * <p>Key configuration:
 * <ul>
 *   <li>Fixed hash key: "orange:hash:example1"</li>
 *   <li>Default expiration: 1 hour</li>
 * </ul>
 * 
 * <p>Example usage:
 * <pre>{@code
 * // Store entity in hash
 * hashTemplate.put("user1", new OrangeValueExampleEntity(...));
 * 
 * // Get single entity
 * OrangeValueExampleEntity user = hashTemplate.get("user1");
 * 
 * // Get all entities
 * Map<String, OrangeValueExampleEntity> allUsers = hashTemplate.getAllMembers();
 * }</pre>
 * 
 * @author Liang.Zhong
 * @since 1.0.0
 * @see JSONOperationsTemplate Base template providing JSON hash operations
 * @see OrangeValueExampleEntity Example entity class
 * @see com.langwuyue.orange.redis.annotation.OrangeRedisKey Key configuration
 * @see com.langwuyue.orange.redis.annotation.Timeout Expiration configuration
 */
@OrangeRedisKey(expirationTime = @Timeout(value = 1, unit = TimeUnit.HOURS), key = "orange:hash:example1")
public interface OrangeRedisHashExample1Api extends JSONOperationsTemplate<OrangeValueExampleEntity> {

	@Override
	default List<OrangeValueExampleEntity> getValues() {
		
		return null;
	}

	@Override
	default OrangeValueExampleEntity get(String key) {
		
		return null;
	}

	@Override
	default Map<String, OrangeValueExampleEntity> get(Collection<String> key) {
		
		return null;
	}

	@Override
	default Map<String, OrangeValueExampleEntity> getAllMembers() {
		
		return null;
	}
}