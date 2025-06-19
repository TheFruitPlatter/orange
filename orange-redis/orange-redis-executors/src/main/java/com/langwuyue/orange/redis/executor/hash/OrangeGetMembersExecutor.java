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
package com.langwuyue.orange.redis.executor.hash;

import java.lang.annotation.Annotation;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.langwuyue.orange.redis.OrangeRedisException;
import com.langwuyue.orange.redis.annotation.GetMembers;
import com.langwuyue.orange.redis.annotation.RedisValue;
import com.langwuyue.orange.redis.annotation.hash.HashKey;
import com.langwuyue.orange.redis.context.OrangeRedisContext;
import com.langwuyue.orange.redis.executor.OrangeRedisAbstractExecutor;
import com.langwuyue.orange.redis.executor.hash.context.OrangeHashContext;
import com.langwuyue.orange.redis.mapping.OrangeRedisExecutorIdGenerator;
import com.langwuyue.orange.redis.operations.OrangeRedisHashOperations;
import com.langwuyue.orange.redis.utils.OrangeCollectionUtils;
import com.langwuyue.orange.redis.utils.OrangeReflectionUtils;

/**
 * Executor implementation for retrieving multiple members from a Redis hash.
 * 
 * <p>This executor handles operations annotated with {@link GetMembers} annotation
 * and retrieves multiple field-value pairs from a Redis hash. It supports returning
 * the results as either a {@link Map} or as a collection/array of objects where
 * fields are mapped to object properties using annotations.
 * 
 * <p>The executor can handle two return types:
 * <ul>
 *   <li>Map: Where keys and values are directly returned as a Map</li>
 *   <li>Collection/Array: Where each entry is mapped to an object with fields
 *       annotated with {@link HashKey} and {@link RedisValue}</li>
 * </ul>
 *
 * @author Liang.Zhong
 * @since 1.0.0
 */
public class OrangeGetMembersExecutor extends OrangeRedisAbstractExecutor {
	
	/** Redis hash operations used by this executor */
	private OrangeRedisHashOperations operations;

	/**
	 * Constructs a new OrangeGetMembersExecutor with the specified operations and ID generator.
	 *
	 * @param operations the Redis hash operations to use for executing commands
	 * @param idGenerator the generator for creating unique executor IDs
	 */
	public OrangeGetMembersExecutor(OrangeRedisHashOperations operations, OrangeRedisExecutorIdGenerator idGenerator) {
		super(idGenerator);
		this.operations = operations;
	}

	/**
	 * Returns the list of annotation classes supported by this executor.
	 * 
	 * @return a list containing the {@link GetMembers} annotation class
	 */
	@Override
	protected List<Class<? extends Annotation>> getSupportedAnnotationClasses() {
		return OrangeCollectionUtils.asList(GetMembers.class);
	}

	/**
	 * Returns the context class used by this executor.
	 * 
	 * @return the {@link OrangeHashContext} class
	 */
	@Override
	public Class<? extends OrangeRedisContext> getContextClass() {
		return OrangeHashContext.class;
	}

	/**
	 * Executes the get members operation on a Redis hash.
	 * 
	 * <p>This method handles two types of return values:
	 * <ul>
	 *   <li>If the return type is a {@link Map}, it directly returns the field-value pairs</li>
	 *   <li>If the return type is a Collection or Array, it creates objects with fields
	 *       annotated with {@link HashKey} and {@link RedisValue} for each entry</li>
	 * </ul>
	 *
	 * @param context the Redis operation context containing method and key information
	 * @return the retrieved hash members, either as a Map or as a Collection/Array of objects
	 * @throws Exception if an error occurs during execution or if required annotations are missing
	 * @throws OrangeRedisException if the return type is not a Map and doesn't have properly
	 *         annotated fields for key and value
	 */
	@Override
	public Object execute(OrangeRedisContext context) throws Exception {
		Class returnClass = context.getOperationMethod().getReturnType();
		if(Map.class.isAssignableFrom(returnClass)) {
			Type[] types = OrangeReflectionUtils.getMapActaulTypeArguments(context.getOperationMethod().getGenericReturnType());
			Type keyType = types[0];
			Type valueType = types[1];
			return doGet(context,keyType,valueType);
		}else{
			Type returnArgumentType = getReturnArgumentType(context);
			Field valueField = getValueField(returnArgumentType);
			Field keyField = getKeyField(returnArgumentType);
			if(valueField == null || keyField == null) {
				throw new OrangeRedisException(String.format("The return value must contain two fields annotated with @%s and @%s", HashKey.class,RedisValue.class));
			}
			Map resultMap = doGet(context,keyField.getGenericType(),valueField.getGenericType());
			return toReturnValue(resultMap,keyField,valueField,returnArgumentType,returnClass);
		}
		
	}
	
	/**
	 * Performs the actual get operation on the Redis hash.
	 * 
	 * <p>This method retrieves all field-value pairs from the hash specified in the context
	 * and processes them according to the return type requirements.
	 *
	 * @param context the Redis operation context containing method and key information
	 * @param keyType the type of the hash field keys
	 * @param valueType the type of the hash field values
	 * @return a map containing all field-value pairs from the Redis hash
	 * @throws Exception if an error occurs during execution
	 */
	protected Map doGet(OrangeRedisContext context, Type keyType, Type valueType) throws Exception {
		OrangeHashContext ctx = (OrangeHashContext) context;
		return this.operations.entries(context.getRedisKey().getValue(), ctx.getKeyType(), ctx.getValueType(), keyType, valueType);
	}
	
	/**
	 * Converts the result map to the appropriate return value type.
	 * 
	 * <p>This method handles the conversion of the retrieved hash entries into either
	 * a Collection or Array of objects, where each object's fields are populated with
	 * the hash entry's key and value.
	 *
	 * @param resultMap the map containing the hash entries
	 * @param keyField the field annotated with {@link HashKey} to store the hash key
	 * @param valueField the field annotated with {@link RedisValue} to store the hash value
	 * @param returnArgumentType the type argument of the return type (for collections/arrays)
	 * @param returnClass the class of the return type
	 * @return a Collection or Array containing objects populated with the hash entries
	 * @throws Exception if an error occurs during object creation or field setting
	 */
	protected Object toReturnValue(Map resultMap, Field keyField, Field valueField, Type returnArgumentType, Class returnClass) throws Exception {
		Class<?> argumentClass = getRawType(returnArgumentType);
		Object instance = getArrayOrCollectionInstance(returnClass, resultMap.size());
		if(instance instanceof Collection) {
			Collection collection = (Collection) instance;
			Set<Entry> entrySet = resultMap.entrySet();
			for(Entry entry : entrySet) {
				Object obj = argumentClass.getConstructor().newInstance();
				OrangeReflectionUtils.setFieldValue(keyField, obj, entry.getKey());
				OrangeReflectionUtils.setFieldValue(valueField, obj, entry.getValue());
				collection.add(obj);
			}
			return collection;
		}else{
			int i = 0;
			Set<Entry> entrySet = resultMap.entrySet();
			for(Entry entry : entrySet) {
				Object obj = argumentClass.getConstructor().newInstance();
				OrangeReflectionUtils.setFieldValue(keyField, obj, entry.getKey());
				OrangeReflectionUtils.setFieldValue(valueField, obj, entry.getValue());
				Array.set(instance, i, obj);
				i++;
			}
			return instance;
		}
	}
	
	/**
	 * Creates an appropriate array or collection instance based on the return type.
	 * 
	 * <p>This method handles the creation of various collection types or arrays
	 * with the specified size to hold the result objects.
	 *
	 * @param returnType the class of the return type (array or collection)
	 * @param size the number of elements that will be stored in the collection/array
	 * @return a new instance of the appropriate collection or array
	 */
	protected Object getArrayOrCollectionInstance(Class<?> returnType,int size) {
		return OrangeReflectionUtils.getArrayOrCollectionInstance(returnType, size);
	}
	
	/**
	 * Finds and returns the field annotated with {@link RedisValue} in the given type.
	 * 
	 * <p>This method scans all declared fields of the type and returns the first one
	 * that is annotated with {@link RedisValue}.
	 *
	 * @param type the type to scan for annotated fields
	 * @return the field annotated with {@link RedisValue}, or null if not found
	 */
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
	
	/**
	 * Finds and returns the field annotated with {@link HashKey} in the given type.
	 * 
	 * <p>This method scans all declared fields of the type and returns the first one
	 * that is annotated with {@link HashKey}.
	 *
	 * @param type the type to scan for annotated fields
	 * @return the field annotated with {@link HashKey}, or null if not found
	 */
	protected Field getKeyField(Type type){
		Class<?> returnType = getRawType(type);
		Field[] fields = returnType.getDeclaredFields();
		for(Field field : fields) {
			if(field.isAnnotationPresent(HashKey.class)) {
				return field;
			}
		}
		return null;
	}
	
	/**
	 * Extracts the raw class type from a potentially parameterized type.
	 * 
	 * <p>This method resolves the raw class from a Type object, which might be
	 * a Class, ParameterizedType, GenericArrayType, etc.
	 *
	 * @param type the type to extract the raw class from
	 * @return the raw class representation of the given type
	 */
	protected Class<?> getRawType(Type type){
		return OrangeReflectionUtils.getRawType(type);
	}
	
	/**
	 * Determines the argument type of the return type for collection or array returns.
	 * 
	 * <p>This method extracts the generic type parameter from the return type of the method
	 * in the context. For example, if the return type is List&lt;User&gt;, this method
	 * returns the User class.
	 *
	 * @param context the Redis operation context containing method information
	 * @return the argument type of the return type, or null if not applicable
	 */
	protected Type getReturnArgumentType(OrangeRedisContext context){
		return OrangeReflectionUtils.getCollectionOrArrayArgumentType(context.getOperationMethod().getGenericReturnType());
	}
}