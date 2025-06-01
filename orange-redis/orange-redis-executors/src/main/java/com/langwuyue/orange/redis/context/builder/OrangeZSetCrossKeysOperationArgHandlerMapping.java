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
package com.langwuyue.orange.redis.context.builder;

import java.lang.annotation.Annotation;
import java.util.Map;

import com.langwuyue.orange.redis.RedisValueTypeEnum;
import com.langwuyue.orange.redis.annotation.zset.OrangeRedisZSetClient;
import com.langwuyue.orange.redis.client.OrangeClientFactoryProvider;
import com.langwuyue.orange.redis.mapping.OrangeRedisExecutorsMapping;

/**
 * Handler mapping for Redis Sorted Set (ZSet) operations involving multiple keys.
 * 
 * <p>This class extends {@link OrangeCrossKeysOperationArgHandlerMapping} to provide
 * specialized handling for Redis Sorted Set operations across multiple keys. It manages
 * the mapping of method arguments to Redis ZSet operations and ensures proper handling
 * of both member values and their associated scores.
 * 
 * <p>Key features include:
 * <ul>
 *   <li>Support for {@link OrangeRedisZSetClient} annotation processing</li>
 *   <li>Score-based operations handling</li>
 *   <li>Cross-key sorted set operations (e.g., union, intersection with score aggregation)</li>
 *   <li>Range-based operations support (by score, rank, or lexicographical order)</li>
 * </ul>
 * 
 * <p>This handler is particularly useful for operations that involve:
 * <ul>
 *   <li>Combining multiple sorted sets with score aggregation</li>
 *   <li>Cross-set range queries and operations</li>
 *   <li>Score-based member management across multiple keys</li>
 * </ul>
 * 
 *
 * @author Liang.Zhong
 * @since 1.0.0
 * @see OrangeRedisZSetClient
 * @see OrangeCrossKeysOperationArgHandlerMapping
 */
public class OrangeZSetCrossKeysOperationArgHandlerMapping extends OrangeCrossKeysOperationArgHandlerMapping {

	/**
	 * Constructs a new OrangeZSetCrossKeysOperationArgHandlerMapping instance.
	 * 
	 * <p>Initializes a handler mapping for Redis Sorted Set operations that work with
	 * multiple keys. This constructor sets up the necessary components for handling
	 * ZSet operations and their arguments, including score-based operations and
	 * member management.
	 *
	 * @param executorsMapping the mapping of executors for Redis operations
	 * @param operationOwner the class that owns the Redis operations
	 * @param valueHandlerMap map of value handlers for different argument types
	 * @param provider provider for Redis client factories
	 * @param clientAnnotationClass the annotation class used to mark Redis ZSet clients
	 */
	public OrangeZSetCrossKeysOperationArgHandlerMapping(
			OrangeRedisExecutorsMapping executorsMapping,
			Class operationOwner,
			Map<Class<? extends OrangeOperationArgHandler>, OrangeOperationArgHandler> valueHandlerMap,
			OrangeClientFactoryProvider provider, Class<? extends Annotation> clientAnnotationClass) {
		super(executorsMapping, operationOwner, valueHandlerMap, provider, clientAnnotationClass);
	}

	/**
	 * Extracts the Redis value type from a ZSet client annotation.
	 * 
	 * <p>This method retrieves the value type configuration from an {@link OrangeRedisZSetClient}
	 * annotation. The value type determines how sorted set members are serialized and
	 * deserialized during Redis operations, while maintaining their associated scores.
	 * 
	 * <p>The extracted value type is used to:
	 * <ul>
	 *   <li>Select appropriate serialization/deserialization strategies</li>
	 *   <li>Validate type compatibility across sorted set operations</li>
	 *   <li>Ensure consistent data handling across multiple sorted set keys</li>
	 *   <li>Support proper score-based operations and comparisons</li>
	 * </ul>
	 *
	 * @param annotation the client annotation to extract value type from (must be {@link OrangeRedisZSetClient})
	 * @return the {@link RedisValueTypeEnum} specified in the annotation
	 */
	@Override
	protected RedisValueTypeEnum getValueType(Annotation annotation) {
		OrangeRedisZSetClient zSetClient = (OrangeRedisZSetClient)annotation;
		return zSetClient.valueType();
	}
}