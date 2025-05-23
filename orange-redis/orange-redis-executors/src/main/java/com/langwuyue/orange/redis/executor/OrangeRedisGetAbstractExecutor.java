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
package com.langwuyue.orange.redis.executor;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.Collection;

import com.langwuyue.orange.redis.annotation.RedisValue;
import com.langwuyue.orange.redis.context.OrangeRedisContext;
import com.langwuyue.orange.redis.mapping.OrangeRedisExecutorIdGenerator;
import com.langwuyue.orange.redis.utils.OrangeReflectionUtils;

/**
 * @author Liang.Zhong
 * @since 1.0.0
 */
public abstract class OrangeRedisGetAbstractExecutor extends OrangeRedisAbstractExecutor {
	
	public OrangeRedisGetAbstractExecutor(OrangeRedisExecutorIdGenerator idGenerator) {
		super(idGenerator);
	}

	@Override
	public Object execute(OrangeRedisContext context) throws Exception {
		Type returnArgumentType = getReturnArgumentType(context);
		Field field = getValueField(returnArgumentType);
		Collection result = doGet(context,field,returnArgumentType);
		return toReturnValue(context, result, returnArgumentType, field);
	}
	
	protected abstract Collection doGet(OrangeRedisContext context, Field valueField, Type returnArgumentType) throws Exception ;

	protected Object toReturnValue(OrangeRedisContext context, Collection result, Type returnArgumentType, Field field) throws Exception {
		boolean fieldValue = field != null;
		Class<?> returnClass = context.getOperationMethod().getReturnType();
		if (result == null 
				|| (
					!fieldValue && returnClass.isAssignableFrom(result.getClass())
				   )
		) {
			return result;
		}
		Class<?> argumentClass = getRawType(returnArgumentType);
		Object instance = getArrayOrCollectionInstance(returnClass, result.size());
		if(instance instanceof Collection) {
			Collection collection = (Collection) instance;
			for(Object value : result) {
				if(fieldValue) {
					Object obj = argumentClass.getConstructor().newInstance();
					OrangeReflectionUtils.setFieldValue(field, obj, value);
					collection.add(obj);
				}else{
					collection.add(value);
				}
			}
			return collection;
		}else{
			int i = 0;
			for(Object value : result) {
				if(fieldValue) {
					Object obj = argumentClass.getConstructor().newInstance();
					OrangeReflectionUtils.setFieldValue(field, obj, value);
					Array.set(instance, i, obj);
				}else{
					Array.set(instance, i, value);
				}
				i++;
			}
			return instance;
		}
	}
	
	protected Object getArrayOrCollectionInstance(Class<?> returnType,int size) {
		return OrangeReflectionUtils.getArrayOrCollectionInstance(returnType, size);
	}
	
	protected Field getValueField(Type type){
		Class<?> returnType = getRawType(type);
		Field[] fields = returnType.getDeclaredFields();
		for(Field field : fields) {
			if(field.isAnnotationPresent(RedisValue.class)) {
				return field;
			}
		}
		return null;
	}
	
	protected Class<?> getRawType(Type type){
		return OrangeReflectionUtils.getRawType(type);
	}
	
	protected Type getReturnArgumentType(OrangeRedisContext context){
		return OrangeReflectionUtils.getCollectionOrArrayArgumentType(context.getOperationMethod().getGenericReturnType());
	}
}
