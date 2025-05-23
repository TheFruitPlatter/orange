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
 * @author Liang.Zhong
 * @since 1.0.0
 */
public class OrangeZSetCrossKeysOperationArgHandlerMapping extends OrangeCrossKeysOperationArgHandlerMapping {

	public OrangeZSetCrossKeysOperationArgHandlerMapping(
			OrangeRedisExecutorsMapping executorsMapping,
			Class operationOwner,
			Map<Class<? extends OrangeOperationArgHandler>, OrangeOperationArgHandler> valueHandlerMap,
			OrangeClientFactoryProvider provider, Class<? extends Annotation> clientAnnotationClass) {
		super(executorsMapping, operationOwner, valueHandlerMap, provider, clientAnnotationClass);
	}

	@Override
	protected RedisValueTypeEnum getValueType(Annotation annotation) {
		OrangeRedisZSetClient zSetClient = (OrangeRedisZSetClient)annotation;
		return zSetClient.valueType();
	}
}
