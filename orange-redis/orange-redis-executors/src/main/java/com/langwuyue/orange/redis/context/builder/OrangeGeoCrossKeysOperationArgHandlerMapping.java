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
import java.lang.reflect.Method;
import java.util.Map;
import java.util.Set;

import com.langwuyue.orange.redis.OrangeRedisException;
import com.langwuyue.orange.redis.RedisValueTypeEnum;
import com.langwuyue.orange.redis.annotation.cross.StoreTo;
import com.langwuyue.orange.redis.annotation.geo.OrangeRedisGeoClient;
import com.langwuyue.orange.redis.annotation.zset.OrangeRedisZSetClient;
import com.langwuyue.orange.redis.client.OrangeClientFactoryProvider;
import com.langwuyue.orange.redis.mapping.OrangeRedisExecutorsMapping;

/**
 * @author Liang.Zhong
 * @since 1.0.0
 */
public class OrangeGeoCrossKeysOperationArgHandlerMapping extends OrangeCrossKeysOperationArgHandlerMapping {

	public OrangeGeoCrossKeysOperationArgHandlerMapping(
			OrangeRedisExecutorsMapping executorsMapping,
			Class operationOwner,
			Map<Class<? extends OrangeOperationArgHandler>, OrangeOperationArgHandler> valueHandlerMap,
			OrangeClientFactoryProvider provider, Class<? extends Annotation> clientAnnotationClass) {
		super(executorsMapping, operationOwner, valueHandlerMap, provider, clientAnnotationClass);
	}

	@Override
	protected RedisValueTypeEnum getValueType(Annotation annotation) {
		if(annotation instanceof OrangeRedisZSetClient) {
			OrangeRedisZSetClient zsetClient = (OrangeRedisZSetClient)annotation;
			return zsetClient.valueType();
		}
		OrangeRedisGeoClient geoClient = (OrangeRedisGeoClient)annotation;
		return geoClient.valueType();
	}
	
	@Override
	protected StoreTo checkStoreToAndGet(Method actualMethod,Set<RedisValueTypeEnum> valueTypes) {
		StoreTo storeTo = actualMethod.getAnnotation(StoreTo.class);
		if(storeTo == null) {
			return null;
		}
		Class<?> keyClass = storeTo.value();
		if(keyClass == null) {
			throw new OrangeRedisException(String.format("The value of @%s cannot be null", StoreTo.class));
		}
		checkClientType(keyClass,valueTypes,OrangeRedisZSetClient.class);
		return storeTo;
	}

	@Override
	protected void checkClientType(Class<?> keyClass,Set<RedisValueTypeEnum> valueTypes,Class<? extends Annotation> clientAnnotationClass) {
		if(clientAnnotationClass != OrangeRedisZSetClient.class) {
			super.checkClientType(keyClass, valueTypes, clientAnnotationClass);
		}else{
			try {
				super.checkClientType(keyClass, valueTypes, clientAnnotationClass);	
			}catch (Exception e) {
				super.checkClientType(keyClass, valueTypes, OrangeRedisGeoClient.class);	
			}
		}
	}
}
