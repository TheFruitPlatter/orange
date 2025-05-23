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
package com.langwuyue.orange.redis.context;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.BiConsumer;

import com.langwuyue.orange.redis.OrangeRedisException;
import com.langwuyue.orange.redis.RedisValueTypeEnum;
import com.langwuyue.orange.redis.annotation.ContinueOnFailure;
import com.langwuyue.orange.redis.annotation.Multiple;
import com.langwuyue.orange.redis.annotation.OrangeRedisOperationArg;
import com.langwuyue.orange.redis.annotation.RedisValue;
import com.langwuyue.orange.redis.context.builder.OrangeMethodAnnotationHandler;
import com.langwuyue.orange.redis.context.builder.OrangeOperationArgMultipleHandler;
import com.langwuyue.orange.redis.utils.OrangeReflectionUtils;

/**
 * @author Liang.Zhong
 * @since 1.0.0
 */
public class OrangeRedisMultipleValueContext extends OrangeRedisContext implements OrangeRedisIterableContext {
	
	@OrangeRedisOperationArg(binding = Multiple.class, valueHandler = OrangeOperationArgMultipleHandler.class)
	private Object multipleValue;
	
	@OrangeRedisOperationArg(binding = ContinueOnFailure.class, valueHandler = OrangeMethodAnnotationHandler.class)
	private ContinueOnFailure continueOnFailure;

	public OrangeRedisMultipleValueContext(
		Class<?> operationOwner, 
		Method operationMethod, 
		Object[] args,
		Key redisKey,
		RedisValueTypeEnum valueType
	) {
		super(operationOwner, operationMethod, args, redisKey,valueType);
	}

	public Object getMultipleValue() {
		return multipleValue;
	}
	
	@Override
	public void forEach(BiConsumer consumer) {
		if(multipleValue == null) {
			return;
		}
		if(multipleValue instanceof Collection) {
			((Collection)multipleValue).forEach((t) -> {
				Object value = getValue(t);
				if(value == null) {
					return;
				}
				consumer.accept(value,t);
			});
			return;
		}
		if(multipleValue.getClass().isArray()) {
			int len = Array.getLength(multipleValue);
			for(int i = 0; i < len; i++) {
				Object t = Array.get(multipleValue, i);
				Object value = getValue(t);
				if(value == null) {
					continue;
				}
				consumer.accept(value,t);
			}
			return;
		}
		
		throw new OrangeRedisException(String.format("The arugment annotated with @%s must be an array or a collection", Multiple.class));
	}

	public Object[] toArray() {
		if(multipleValue == null) {
			return new Object[] {};
		}
		final List newArray = new ArrayList<>();
		forEach((t,o)-> newArray.add(t));
		return newArray.toArray();
	}
	
	private Object getValue(Object value) {
		if(value == null) {
			return null;
		}
		Class valueClass = value.getClass();
		if(valueClass.getPackage().getName().startsWith("java")) {
			return value;
		}
		Field[] fields = valueClass.getDeclaredFields();
		if(fields == null || fields.length == 0) {
			return value;
		}
		
		Field valueField = null;
		for(Field field : fields) {
			if(field.isAnnotationPresent(RedisValue.class)) {
				valueField = field;
				break;
			}
		}
		
		if(valueField == null) {
			return value;
		}
		
		return OrangeReflectionUtils.getFieldValue(valueField, value);
	}

	@Override
	public boolean continueOnFailure() {
		return continueOnFailure.value();
	}
}
