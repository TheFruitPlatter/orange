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
package com.langwuyue.orange.redis.executor.zset;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collection;
import java.util.Map;

import com.langwuyue.orange.redis.OrangeRedisException;
import com.langwuyue.orange.redis.annotation.RedisValue;
import com.langwuyue.orange.redis.annotation.zset.Score;
import com.langwuyue.orange.redis.context.OrangeRedisContext;
import com.langwuyue.orange.redis.executor.OrangeRedisGetAbstractExecutor;
import com.langwuyue.orange.redis.mapping.OrangeRedisExecutorIdGenerator;
import com.langwuyue.orange.redis.operations.OrangeRedisZSetOperations.ZSetEntry;
import com.langwuyue.orange.redis.utils.OrangeReflectionUtils;
/**
 * @author Liang.Zhong
 * @since 1.0.0
 */
public abstract class OrangeGetWithScoresAbstractExecutor extends OrangeRedisGetAbstractExecutor {

	protected OrangeGetWithScoresAbstractExecutor(OrangeRedisExecutorIdGenerator idGenerator) {
		super(idGenerator);
	}
	
	@Override
	protected Type getReturnArgumentType(OrangeRedisContext context) {
		Class<?> returnType = context.getOperationMethod().getReturnType();
		if(Map.class.isAssignableFrom(returnType)) {
			Type genericType = context.getOperationMethod().getGenericReturnType();
			return OrangeReflectionUtils.getMapActaulTypeArguments(genericType)[0];
		} else {
			Type genericType = super.getReturnArgumentType(context);
			Class<?> rawType = getRawType(genericType);
			if(Map.class.isAssignableFrom(rawType)) {
				return OrangeReflectionUtils.getMapActaulTypeArguments(genericType)[0];
			}
			return genericType;
		}
	}

	@Override
	protected Object toReturnValue(OrangeRedisContext context, Collection result, Type returnArgumentType, Field field) throws Exception {
		Class<?> returnClass = context.getOperationMethod().getReturnType();
		if(Map.class.isAssignableFrom(returnClass)) {
			Class<?> valueType = getMapValueType(context.getOperationMethod().getGenericReturnType());
			return toMap(valueType,result,field,returnArgumentType,returnClass);
		}else{
			Type genericType = super.getReturnArgumentType(context);
			Class<?> rawType = getRawType(genericType);
			if(Map.class.isAssignableFrom(rawType)) {
				return toWithScoreReturnMultipleMap(result, field, returnArgumentType, returnClass, genericType);
			}
			return toWithScoreReturnValue(result, field, returnArgumentType, returnClass);
		}
	}
	
	protected Object toWithScoreReturnMultipleMap(Collection result, Field field, Type argumentType, Class<?> returnType,Type returnValueArgumentType) throws Exception{
		Class<?> valueType = getMapValueType(returnValueArgumentType);
		Class<?> rawType = getRawType(returnValueArgumentType);
		Object instance = getArrayOrCollectionInstance(returnType, result.size());
		if(instance instanceof Collection) {
			Collection collection = (Collection) instance;
			for(Object value : result) {
				Object obj = toMap(valueType, Arrays.asList(value), field, argumentType, rawType);
				collection.add(obj);
			}
			return collection;
		}else{
			int i = 0;
			for(Object value : result) {
				Object obj = toMap(valueType, Arrays.asList(value), field, argumentType, rawType);
				Array.set(instance, i, obj);
				i++;
			}
			return instance;
		}
	}
	
	protected Object toWithScoreReturnValue(Collection result, Field field, Type argumentType, Class<?> returnType) throws Exception{
		if(field == null) {
			throw new OrangeRedisException(String.format("The field marked with @%s cannot be null.", RedisValue.class));
		}
		Class<?> argumentClass = getRawType(argumentType);
		Field scoreField = getScoreField(argumentClass);
		Object instance = getArrayOrCollectionInstance(returnType, result.size());
		if(instance instanceof Collection) {
			Collection collection = (Collection) instance;
			for(Object value : result) {
				ZSetEntry entry = (ZSetEntry)value;
				Object obj = argumentClass.getConstructor().newInstance();
				OrangeReflectionUtils.setFieldValue(field, obj, entry.getValue());
				OrangeReflectionUtils.setFieldValue(scoreField, obj, toSpecifiedTypeValue(scoreField.getType(),entry.getScore()));
				collection.add(obj);
			}
			return collection;
		}else{
			int i = 0;
			for(Object value : result) {
				ZSetEntry entry = (ZSetEntry)value;
				Object obj = argumentClass.getConstructor().newInstance();
				OrangeReflectionUtils.setFieldValue(field, obj, entry.getValue());
				OrangeReflectionUtils.setFieldValue(scoreField, obj, toSpecifiedTypeValue(scoreField.getType(),entry.getScore()));
				Array.set(instance, i, obj);
				i++;
			}
			return instance;
		}
	}
	
	private Object toSpecifiedTypeValue(Class<?> clazz,Object value) {
		if(clazz == String.class) {
			return value.toString();
		}
		if(clazz == Double.class || clazz == double.class) {
			return Double.valueOf(value.toString());
		}
		if(clazz == Float.class || clazz == float.class) {
			return Float.valueOf(value.toString());
		}
		if(clazz == BigDecimal.class) {
			return new BigDecimal(value.toString());
		}
		if(clazz == Long.class || clazz == long.class) {
			return Long.valueOf(value.toString());
		}
		if(clazz == Integer.class || clazz == int.class) {
			return Integer.valueOf(value.toString());
		}
		if(clazz == Short.class || clazz == short.class) {
			return Short.valueOf(value.toString());
		}
		if(clazz == Byte.class || clazz == byte.class) {
			return Short.valueOf(value.toString());
		}
		throw new OrangeRedisException("Score must be a number or a string");
		
	}

	protected Object toMap(Class mapValueType, Collection result, Field field, Type argumentType, Class<?> returnClass) throws Exception {
		if (result == null) {
			return result;
		}
		Map map = OrangeReflectionUtils.newMap(returnClass);
		boolean fieldValue = field != null;
		Class<?> argumentClass = getRawType(argumentType);
		Field scoreField = OrangeReflectionUtils.getFieldMarkedWithAnnotationNullable(mapValueType, Score.class);
		boolean fieldScore = scoreField != null;
		for(Object element : result) {
			ZSetEntry entry = (ZSetEntry)element;
			if(fieldValue) {
				Object obj = argumentClass.getConstructor().newInstance();
				OrangeReflectionUtils.setFieldValue(field, obj, entry.getValue());
				if(fieldScore) {
					if(mapValueType == argumentClass) {
						OrangeReflectionUtils.setFieldValue(field, obj, toSpecifiedTypeValue(scoreField.getType(),entry.getScore()));	
						map.put(obj, obj);
					}else{
						Object value = mapValueType.getConstructor().newInstance();
						OrangeReflectionUtils.setFieldValue(field, value, toSpecifiedTypeValue(scoreField.getType(),entry.getScore()));
						map.put(obj, value);
					}
				}else{
					map.put(obj, toSpecifiedTypeValue(mapValueType,entry.getScore()));
				}
			}else{
				if(fieldScore) {
					Object obj = argumentClass.getConstructor().newInstance();
					OrangeReflectionUtils.setFieldValue(field, obj, toSpecifiedTypeValue(scoreField.getType(),entry.getScore()));
					map.put(entry.getValue(), obj);
				}else{
					map.put(entry.getValue(), toSpecifiedTypeValue(mapValueType,entry.getScore()));
				}
			}
		}
		return map;
	}
	
	protected Class<?> getMapValueType(Type returnType){
		ParameterizedType type = (ParameterizedType)returnType;
		Type[] types = type.getActualTypeArguments();
		if(types.length < 2) {
			throw new OrangeRedisException("The generic type arguments for 'Map' must be specified.");
		}
		Type valueType = types[1];
		if(valueType instanceof ParameterizedType) {
			type = (ParameterizedType)valueType;
			return (Class<?>) type.getRawType();
		}
		return (Class<?>) valueType;
	}
	
	protected Field getScoreField(Class<?> memberClass) {
		Field field = OrangeReflectionUtils.getFieldMarkedWithAnnotation(memberClass, Score.class);
		Class fieldType = field.getType();
		if(!OrangeReflectionUtils.isInteger(fieldType) 
				&& !OrangeReflectionUtils.isFloat(fieldType) 
				&& fieldType != String.class
				&& fieldType != BigDecimal.class) {
			
			throw new OrangeRedisException(String.format("The field %s annotated with @%s must be a number or a string.", field,Score.class));
		}
		return field;
	}

}
