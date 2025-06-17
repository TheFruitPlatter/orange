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
 * Abstract base executor for retrieving members with their scores from Redis Sorted Sets.
 * 
 * <p>This class provides core functionality for handling operations that retrieve both
 * members and their associated scores from Redis Sorted Sets. It includes sophisticated
 * type conversion and mapping capabilities to support various return types and data
 * structures.
 *
 * <p>Key features:
 * <ul>
 *   <li>Flexible return type support (Collections, Arrays, Maps)</li>
 *   <li>Automatic type conversion for scores to various numeric types</li>
 *   <li>Support for custom object mapping with {@link RedisValue} and {@link Score} annotations</li>
 *   <li>Complex object construction with nested property mapping</li>
 * </ul>
 *
 * <p>This class handles the complexities of:
 * <ul>
 *   <li>Type inference and conversion</li>
 *   <li>Object instantiation and property mapping</li>
 *   <li>Collection and map creation</li>
 *   <li>Score type conversion to various numeric formats</li>
 * </ul>
 *
 * @author Liang.Zhong
 * @since 1.0.0
 * @see <a href="https://orange.langwuyue.com/redis/advanced/zset">Orange Redis ZSet Documentation</a>
 */
public abstract class OrangeGetWithScoresAbstractExecutor extends OrangeRedisGetAbstractExecutor {

	/**
	 * Constructs a new instance of the executor.
	 *
	 * @param idGenerator the generator for creating unique operation IDs
	 */
	protected OrangeGetWithScoresAbstractExecutor(OrangeRedisExecutorIdGenerator idGenerator) {
		super(idGenerator);
	}
	
	/**
	 * Determines the return argument type based on the method's return type and generic parameters.
	 *
	 * <p>This method handles different return type scenarios:
	 * <ul>
	 *   <li>For Map return types, extracts the key type from generic parameters</li>
	 *   <li>For nested Map types, extracts the appropriate type argument</li>
	 *   <li>For other types, returns the generic type as determined by the superclass</li>
	 * </ul>
	 *
	 * @param context the context containing method information
	 * @return the Type to be used for result conversion
	 */
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

	/**
	 * Converts the result collection to the appropriate return value type.
	 *
	 * <p>This method handles complex type conversion scenarios:
	 * <ul>
	 *   <li>Map return types - converts results to key-value pairs</li>
	 *   <li>Nested Map types - creates maps with complex value types</li>
	 *   <li>Custom object types - maps results to objects with score fields</li>
	 * </ul>
	 *
	 * <p>The conversion process considers:
	 * <ul>
	 *   <li>The method's return type</li>
	 *   <li>Generic type parameters</li>
	 *   <li>Field annotations for value and score mapping</li>
	 * </ul>
	 *
	 * @param context the context containing method information
	 * @param result the collection of results to convert
	 * @param returnArgumentType the target return argument type
	 * @param field the field information for value mapping
	 * @return the converted result in the appropriate return type
	 * @throws Exception if an error occurs during type conversion
	 */
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
	
	/**
	 * Converts the result collection to a collection of maps with member-score pairs.
	 *
	 * <p>This method creates a collection where each element is a map containing
	 * member-score pairs. It handles both Collection and Array return types.
	 *
	 * @param result the collection of results to convert
	 * @param field the field information for value mapping
	 * @param argumentType the type of arguments in the collection
	 * @param returnType the class of the return type
	 * @param returnValueArgumentType the generic type of values in the return type
	 * @return a collection of maps containing member-score pairs
	 * @throws Exception if an error occurs during type conversion
	 */
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
	
	/**
	 * Converts the result collection to a collection of objects with member and score fields.
	 *
	 * <p>This method creates objects with fields mapped to member values and scores.
	 * It requires:
	 * <ul>
	 *   <li>A field marked with {@link RedisValue} for the member value</li>
	 *   <li>A field marked with {@link Score} for the score value</li>
	 * </ul>
	 *
	 * <p>The method handles both Collection and Array return types.
	 *
	 * @param result the collection of results to convert
	 * @param field the field marked with {@link RedisValue} for member values
	 * @param argumentType the type of arguments in the collection
	 * @param returnType the class of the return type
	 * @return a collection of objects with member and score fields populated
	 * @throws Exception if an error occurs during object creation or field mapping
	 */
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
	
	/**
	 * Converts a value to the specified type.
	 *
	 * <p>This method handles conversion of score values to various numeric types:
	 * <ul>
	 *   <li>String - converts to string representation</li>
	 *   <li>Double/double - converts to double value</li>
	 *   <li>Float/float - converts to float value</li>
	 *   <li>BigDecimal - creates a new BigDecimal</li>
	 *   <li>Long/long - converts to long value</li>
	 *   <li>Integer/int - converts to integer value</li>
	 *   <li>Short/short - converts to short value</li>
	 *   <li>Byte/byte - converts to byte value</li>
	 * </ul>
	 *
	 * @param clazz the target class type
	 * @param value the value to convert
	 * @return the converted value in the specified type
	 * @throws OrangeRedisException if the target type is not supported
	 */
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

	/**
	 * Converts the result collection to a map with complex value types.
	 *
	 * <p>This method handles various mapping scenarios:
	 * <ul>
	 *   <li>Simple key-score pairs where score is converted to the specified type</li>
	 *   <li>Complex objects with {@link RedisValue} and {@link Score} annotated fields</li>
	 *   <li>Nested objects where both key and value are custom types</li>
	 * </ul>
	 *
	 * <p>The method supports:
	 * <ul>
	 *   <li>Direct score mapping to numeric types</li>
	 *   <li>Object creation with field mapping</li>
	 *   <li>Complex object hierarchies</li>
	 * </ul>
	 *
	 * @param mapValueType the type of values in the map
	 * @param result the collection of results to convert
	 * @param field the field for value mapping
	 * @param argumentType the type of arguments
	 * @param returnClass the class of the return type
	 * @return a map containing the converted key-value pairs
	 * @throws Exception if an error occurs during object creation or field mapping
	 */
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
	
	/**
	 * Extracts the value type from a Map's generic type information.
	 *
	 * <p>This method handles:
	 * <ul>
	 *   <li>Simple parameterized types (e.g., Map&lt;K,V&gt;)</li>
	 *   <li>Nested parameterized types (e.g., Map&lt;K,Map&lt;K2,V2&gt;&gt;)</li>
	 * </ul>
	 *
	 * @param returnType the generic return type containing Map type parameters
	 * @return the Class representing the map's value type
	 * @throws OrangeRedisException if the Map's generic type arguments are not properly specified
	 */
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
	
	/**
	 * Retrieves and validates the score field from a class.
	 *
	 * <p>This method:
	 * <ul>
	 *   <li>Finds the field annotated with {@link Score}</li>
	 *   <li>Validates that the field type is a supported numeric type or string</li>
	 * </ul>
	 *
	 * <p>Supported field types:
	 * <ul>
	 *   <li>Integer types (byte, short, int, long and their wrapper classes)</li>
	 *   <li>Floating point types (float, double and their wrapper classes)</li>
	 *   <li>BigDecimal</li>
	 *   <li>String</li>
	 * </ul>
	 *
	 * @param memberClass the class to search for the score field
	 * @return the Field object representing the score field
	 * @throws OrangeRedisException if the field type is not supported
	 */
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