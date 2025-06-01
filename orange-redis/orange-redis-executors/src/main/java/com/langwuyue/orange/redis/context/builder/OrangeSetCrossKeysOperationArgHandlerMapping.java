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
import com.langwuyue.orange.redis.annotation.set.OrangeRedisSetClient;
import com.langwuyue.orange.redis.client.OrangeClientFactoryProvider;
import com.langwuyue.orange.redis.mapping.OrangeRedisExecutorsMapping;

/**
 * Handler mapping for Redis Set operations involving multiple keys.
 * 
 * <p>This class extends {@link OrangeCrossKeysOperationArgHandlerMapping} to provide
 * specialized handling for Redis Set operations across multiple keys. It manages the
 * mapping of method arguments to Redis Set operations and ensures proper type handling
 * for set members.
 * 
 * <p>Key features include:
 * <ul>
 *   <li>Support for {@link OrangeRedisSetClient} annotation processing</li>
 *   <li>Set-specific value type extraction and validation</li>
 *   <li>Cross-key set operations handling (e.g., union, intersection, difference)</li>
 * </ul>
 * 
 * <p>This handler is particularly useful for operations that involve:
 * <ul>
 *   <li>Set operations between multiple keys (SUNION, SINTER, SDIFF)</li>
 *   <li>Moving members between sets</li>
 *   <li>Comparing members across multiple sets</li>
 * </ul>
 * 
 *
 * @author Liang.Zhong
 * @since 1.0.0
 * @see OrangeRedisSetClient
 * @see OrangeCrossKeysOperationArgHandlerMapping
 */
public class OrangeSetCrossKeysOperationArgHandlerMapping extends OrangeCrossKeysOperationArgHandlerMapping {

	/**
	 * Constructs a new OrangeSetCrossKeysOperationArgHandlerMapping instance.
	 * 
	 * <p>Initializes a handler mapping for Redis Set operations that work with multiple keys.
	 * This constructor sets up the necessary components for handling Set operations and
	 * their arguments.
	 *
	 * @param executorsMapping the mapping of executors for Redis operations
	 * @param operationOwner the class that owns the Redis operations
	 * @param valueHandlerMap map of value handlers for different argument types
	 * @param provider provider for Redis client factories
	 * @param clientAnnotationClass the annotation class used to mark Redis Set clients
	 */
	public OrangeSetCrossKeysOperationArgHandlerMapping(
			OrangeRedisExecutorsMapping executorsMapping,
			Class operationOwner,
			Map<Class<? extends OrangeOperationArgHandler>, OrangeOperationArgHandler> valueHandlerMap,
			OrangeClientFactoryProvider provider, Class<? extends Annotation> clientAnnotationClass) {
		super(executorsMapping, operationOwner, valueHandlerMap, provider, clientAnnotationClass);
	}

	/**
	 * Extracts the Redis value type from a Set client annotation.
	 * 
	 * <p>This method retrieves the value type configuration from an {@link OrangeRedisSetClient}
	 * annotation. The value type determines how set members are serialized and
	 * deserialized during Redis operations.
	 * 
	 * <p>The extracted value type is used to:
	 * <ul>
	 *   <li>Select appropriate serialization/deserialization strategies</li>
	 *   <li>Validate type compatibility across set operations</li>
	 *   <li>Ensure consistent data handling across multiple set keys</li>
	 * </ul>
	 *
	 * @param annotation the client annotation to extract value type from (must be {@link OrangeRedisSetClient})
	 * @return the {@link RedisValueTypeEnum} specified in the annotation
	 * @throws ClassCastException if the annotation is not a Set client annotation
	 */
	@Override
	protected RedisValueTypeEnum getValueType(Annotation annotation) {
		OrangeRedisSetClient setClient = (OrangeRedisSetClient)annotation;
		return setClient.valueType();
	}
}