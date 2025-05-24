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

import com.langwuyue.orange.example.redis.entity.OrangeValueExampleEntity;
import com.langwuyue.orange.redis.annotation.OrangeRedisKey;
import com.langwuyue.orange.redis.annotation.Timeout;
import com.langwuyue.orange.redis.template.value.JSONOperationsTemplate;

/**
 * Example interface demonstrating basic Redis value operations using JSON serialization.
 * 
 * <p>This interface extends {@link JSONOperationsTemplate} to provide Redis value operations
 * for {@link OrangeValueExampleEntity} objects with JSON serialization. The Redis key is
 * configured with:
 * <ul>
 *   <li>Fixed key prefix: "orange:value:example1"</li>
 *   <li>Default expiration: 1 hour</li>
 * </ul>
 * 
 * <p>Typical Redis commands used:
 * <ul>
 *   <li>{@code GET} - For retrieving values</li>
 *   <li>{@code SET} - For storing values (handled by superclass)</li>
 *   <li>{@code EXPIRE} - For setting expiration (configured via annotation)</li>
 * </ul>
 * 
 * @author Liang.Zhong
 * @since 1.0.0
 * @see JSONOperationsTemplate Base template providing common Redis value operations
 * @see OrangeValueExampleEntity The entity type being stored/retrieved
 * @see com.langwuyue.orange.redis.annotation.OrangeRedisKey Key configuration annotation
 */
@OrangeRedisKey(expirationTime = @Timeout(value = 1, unit = TimeUnit.HOURS), key = "orange:value:example1")
public interface OrangeRedisValueExample1Api extends JSONOperationsTemplate<OrangeValueExampleEntity> {

	/**
	 * Retrieves the {@link OrangeValueExampleEntity} stored at the configured Redis key.
	 * 
	 * <p>This operation uses Redis {@code GET} command under the hood and deserializes
	 * the JSON value back to the entity object. Returns {@code null} if no value exists
	 * at the key.</p>
	 * 
	 * @return The deserialized entity, or null if key doesn't exist
	 * @see JSONOperationsTemplate#getValue() Base implementation details
	 */
	@Override
	OrangeValueExampleEntity getValue();
}