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
import com.langwuyue.orange.redis.annotation.list.OrangeRedisListClient;
import com.langwuyue.orange.redis.client.OrangeClientFactoryProvider;
import com.langwuyue.orange.redis.mapping.OrangeRedisExecutorsMapping;

/**
 * Handler mapping for Redis List operations involving multiple keys.
 * 
 * <p>This class extends {@link OrangeCrossKeysOperationArgHandlerMapping} to provide
 * specialized handling for Redis List operations across multiple keys. It manages the
 * mapping of method arguments to Redis List operations and ensures proper type handling
 * for list values.
 * 
 * <p>Key features include:
 * <ul>
 *   <li>Support for {@link OrangeRedisListClient} annotation processing</li>
 *   <li>List-specific value type extraction and validation</li>
 *   <li>Cross-key list operations handling (e.g., list merging, copying)</li>
 * </ul>
 * 
 * <p>This handler is particularly useful for operations that involve:
 * <ul>
 *   <li>Moving elements between multiple lists</li>
 *   <li>Combining elements from multiple lists</li>
 *   <li>Cross-list operations like merging or comparing</li>
 * </ul>
 *
 * @author Liang.Zhong
 * @since 1.0.0
 * @see OrangeRedisListClient
 * @see OrangeCrossKeysOperationArgHandlerMapping
 */
public class OrangeListCrossKeysOperationArgHandlerMapping extends OrangeCrossKeysOperationArgHandlerMapping {

	public OrangeListCrossKeysOperationArgHandlerMapping(
			OrangeRedisExecutorsMapping executorsMapping,
			Class operationOwner,
			Map<Class<? extends OrangeOperationArgHandler>, OrangeOperationArgHandler> valueHandlerMap,
			OrangeClientFactoryProvider provider, Class<? extends Annotation> clientAnnotationClass) {
		super(executorsMapping, operationOwner, valueHandlerMap, provider, clientAnnotationClass);
	}

	/**
	 * Extracts the Redis value type from a List client annotation.
	 * 
	 * <p>This method retrieves the value type configuration from an {@link OrangeRedisListClient}
	 * annotation. The value type determines how list elements are serialized and
	 * deserialized during Redis operations.
	 * 
	 * <p>The extracted value type is used to:
	 * <ul>
	 *   <li>Select appropriate serialization/deserialization strategies</li>
	 *   <li>Validate type compatibility across operations</li>
	 *   <li>Ensure consistent data handling across multiple list keys</li>
	 * </ul>
	 *
	 * @param annotation the client annotation to extract value type from (must be {@link OrangeRedisListClient})
	 * @return the {@link RedisValueTypeEnum} specified in the annotation
	 */
	@Override
	protected RedisValueTypeEnum getValueType(Annotation annotation) {
		OrangeRedisListClient setClient = (OrangeRedisListClient)annotation;
		return setClient.valueType();
	}
}