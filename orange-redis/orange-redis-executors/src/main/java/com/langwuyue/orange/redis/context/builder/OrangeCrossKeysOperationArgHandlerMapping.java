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
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import com.langwuyue.orange.redis.OrangeRedisException;
import com.langwuyue.orange.redis.RedisValueTypeEnum;
import com.langwuyue.orange.redis.annotation.cross.Aggregate;
import com.langwuyue.orange.redis.annotation.cross.CrossOperationKeys;
import com.langwuyue.orange.redis.annotation.cross.StoreTo;
import com.langwuyue.orange.redis.client.OrangeClientFactoryProvider;
import com.langwuyue.orange.redis.client.OrangeRedisClientFactoryMetaData;
import com.langwuyue.orange.redis.client.OrangeRedisClientFactoryMetaDataBuilder;
import com.langwuyue.orange.redis.executor.OrangeRedisExecutor;
import com.langwuyue.orange.redis.mapping.OrangeRedisExecutorsMapping;

/**
 * @author Liang.Zhong
 * @since 1.0.0
 */
public class OrangeCrossKeysOperationArgHandlerMapping extends OrangeOperationArgHandlerMapping {
	
	private OrangeClientFactoryProvider provider;
	
	private Class<? extends Annotation> clientAnnotationClass;
	
	private static final Map<Method, RedisValueTypeEnum> OPEATION_VALUE_TYPE_MAPPING = new ConcurrentHashMap<>();
	
	private static final Map<Method, List<Class<?>>> OPEATION_KEYS_MAPPING = new ConcurrentHashMap<>();
	
	private static final Map<Method, Class<?>> OPEATION_STORE_TO_MAPPING = new ConcurrentHashMap<>();
	
	public OrangeCrossKeysOperationArgHandlerMapping(
		OrangeRedisExecutorsMapping executorsMapping, 
		Class<?> operationOwner,
		Map<Class<? extends OrangeOperationArgHandler>, OrangeOperationArgHandler> valueHandlerMap,
		OrangeClientFactoryProvider provider,
		Class<? extends Annotation> clientAnnotationClass
	) {
		super(executorsMapping,operationOwner,valueHandlerMap);
		this.provider = provider;
		this.clientAnnotationClass = clientAnnotationClass;
	}
	
	@Override
	protected OrangeRedisExecutor getOrangeRedisExecutor(Method method) {
		OrangeRedisExecutor executor = super.getOrangeRedisExecutor(method);
		Method actualMethod = getExecutorsMapping().getActualMethod(method);
		Set<RedisValueTypeEnum> valueTypes = new LinkedHashSet<>();
		List<Class<?>> keyClasses = new ArrayList<>();
		int keyClassesLength = checkCrossOperationKeysAndGetKeysLength(actualMethod,valueTypes,keyClasses);
		checkWeightsLength(actualMethod,keyClassesLength);
		StoreTo storeTo = checkStoreToAndGet(actualMethod,valueTypes);
		if(storeTo != null) {
			OPEATION_STORE_TO_MAPPING.put(method, storeTo.value());
		}
		checkValueType(valueTypes);
		OPEATION_VALUE_TYPE_MAPPING.put(method, valueTypes.iterator().next());
		OPEATION_KEYS_MAPPING.put(method, keyClasses);
		return executor;
	}
	
	protected void checkValueType(Set<RedisValueTypeEnum> valueTypes) {
		if(valueTypes.isEmpty()) {
			throw new OrangeRedisException("The value types of keys cannot be null");
		}
		if(valueTypes.size() != 1) {
			throw new OrangeRedisException("The value types of keys are not the same");
		}
	}
	
	protected int checkCrossOperationKeysAndGetKeysLength(Method actualMethod,Set<RedisValueTypeEnum> valueTypes,List<Class<?>> keyClassList) {
		CrossOperationKeys crossOperationKeys = actualMethod.getAnnotation(CrossOperationKeys.class);
		Class<?>[] keyClasses = crossOperationKeys.value();
		if(keyClasses == null || keyClasses.length == 0) {
			throw new OrangeRedisException("The value of @%s cannot be empty");
		}
		for(Class<?> keyClass : keyClasses) {
			if(keyClassList.contains(keyClass)) {
				throw new OrangeRedisException(String.format("Depucated key found, key class: %s", keyClass));
			}
			checkClientType(keyClass,valueTypes,this.clientAnnotationClass);
			keyClassList.add(keyClass);
		}
		return keyClasses.length;
	}
	
	protected StoreTo checkStoreToAndGet(Method actualMethod,Set<RedisValueTypeEnum> valueTypes) {
		StoreTo storeTo = actualMethod.getAnnotation(StoreTo.class);
		if(storeTo == null) {
			return null;
		}
		Class<?> keyClass = storeTo.value();
		if(keyClass == null) {
			throw new OrangeRedisException(String.format("The value of @%s cannot be null", StoreTo.class));
		}
		checkClientType(keyClass,valueTypes,this.clientAnnotationClass);
		return storeTo;
	}
	
	protected void checkWeightsLength(Method actualMethod,int keyClassesLen) {
		Aggregate aggregate = actualMethod.getAnnotation(Aggregate.class);
		if(aggregate == null) {
			return;
		}
		int len = aggregate.weights().length;
		if(len != keyClassesLen) {
			throw new OrangeRedisException(String.format("The length of @%s's weights must equal the length of @%s's value", Aggregate.class,CrossOperationKeys.class));
		}
	}
	
	protected void checkClientType(Class<?> keyClass,Set<RedisValueTypeEnum> valueTypes,Class<? extends Annotation> clientAnnotationClass) {
		OrangeRedisClientFactoryMetaDataBuilder builder = new OrangeRedisClientFactoryMetaDataBuilder(keyClass,this.provider);
		OrangeRedisClientFactoryMetaData metaData = builder.build();
		Annotation annotation = metaData.getClientClass().getAnnotation(clientAnnotationClass);
		if(annotation == null) {
			throw new OrangeRedisException(String.format("Key not support, %s must be annotated with @%s", keyClass, clientAnnotationClass));
		}
		RedisValueTypeEnum valueType = getValueType(annotation);
		if(valueType != null) {
			valueTypes.add(valueType);
		}
	}
	
	protected RedisValueTypeEnum getValueType(Annotation annotation) {
		return null;
	}
	
	public RedisValueTypeEnum getValueType(Method method) {
		return OPEATION_VALUE_TYPE_MAPPING.get(method);
	}
	
	public List<Class<?>> getKeyClasses(Method method){
		return OPEATION_KEYS_MAPPING.get(method);
	}

	public Class<?> getStoreTo(Method method) {
		return OPEATION_STORE_TO_MAPPING.get(method);
	}

	protected OrangeClientFactoryProvider getProvider() {
		return provider;
	}
	
}
