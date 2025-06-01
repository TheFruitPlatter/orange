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
 * Abstract base implementation for Redis GET operations that retrieves data from Redis
 * and converts it to appropriate Java types.
 * 
 * <p>This class extends {@link OrangeRedisAbstractExecutor} and provides a template
 * for implementing various Redis retrieval operations (GET, HGET, SMEMBERS, etc.).
 * It handles the common aspects of data retrieval and conversion, allowing concrete
 * implementations to focus on the specific Redis command execution.
 * 
 * <p>Key features:
 * <ul>
 *   <li>Automatic conversion between Redis data and Java objects</li>
 *   <li>Support for collection and array return types</li>
 *   <li>Support for objects with {@link RedisValue} annotated fields</li>
 *   <li>Type inference for generic return types</li>
 * </ul>
 * 
 * <p>The execution flow is:
 * <ol>
 *   <li>Determine the return type from the method signature</li>
 *   <li>Find any fields annotated with {@link RedisValue} in the return type</li>
 *   <li>Call the abstract {@link #doGet} method to retrieve data from Redis</li>
 *   <li>Convert the retrieved data to the appropriate return type</li>
 * </ol>
 * 
 * @author Liang.Zhong
 * @since 1.0.0
 * @see OrangeRedisAbstractExecutor
 * @see RedisValue
 */
public abstract class OrangeRedisGetAbstractExecutor extends OrangeRedisAbstractExecutor {
	
	/**
	 * Creates a new instance of OrangeRedisGetAbstractExecutor.
	 *
	 * @param idGenerator the generator for creating unique executor IDs
	 */
	protected OrangeRedisGetAbstractExecutor(OrangeRedisExecutorIdGenerator idGenerator) {
		super(idGenerator);
	}

	/**
	 * {@inheritDoc}
	 * 
	 * <p>This implementation provides a template for executing Redis GET operations:
	 * <ol>
	 *   <li>Determines the return type from the method signature</li>
	 *   <li>Finds any {@link RedisValue} annotated fields in the return type</li>
	 *   <li>Retrieves data from Redis using the {@link #doGet} method</li>
	 *   <li>Converts the retrieved data to the appropriate return type</li>
	 * </ol>
	 *
	 * @param context the Redis operation context containing connection and parameters
	 * @return the retrieved and converted data, or null if not found
	 * @throws Exception if any error occurs during execution
	 */
	@Override
	public Object execute(OrangeRedisContext context) throws Exception {
		Type returnArgumentType = getReturnArgumentType(context);
		Field field = getValueField(returnArgumentType);
		Collection result = doGet(context,field,returnArgumentType);
		return toReturnValue(context, result, returnArgumentType, field);
	}
	
	/**
	 * Executes the actual Redis GET operation to retrieve data.
	 * 
	 * <p>This method must be implemented by concrete classes to perform the specific
	 * Redis retrieval operation. The implementation should:
	 * <ul>
	 *   <li>Use the connection from the context to execute Redis commands</li>
	 *   <li>Convert the retrieved data to appropriate Java types</li>
	 *   <li>Return the results as a Collection</li>
	 * </ul>
	 *
	 * @param context the Redis operation context
	 * @param valueField the field annotated with {@link RedisValue}, or null if none
	 * @param returnArgumentType the generic type argument of the return type
	 * @return a Collection containing the retrieved data, or null if not found
	 * @throws Exception if any error occurs during the operation
	 */
	protected abstract Collection doGet(OrangeRedisContext context, Field valueField, Type returnArgumentType) throws Exception ;

	/**
	 * Converts the retrieved data to the appropriate return type.
	 * 
	 * <p>This method handles the conversion of Redis data to Java objects:
	 * <ul>
	 *   <li>Supports direct return of Collection types</li>
	 *   <li>Converts to arrays when needed</li>
	 *   <li>Handles objects with {@link RedisValue} annotated fields</li>
	 *   <li>Maintains type safety during conversion</li>
	 * </ul>
	 *
	 * @param context the Redis operation context
	 * @param result the Collection of retrieved data
	 * @param returnArgumentType the generic type argument of the return type
	 * @param field the field annotated with {@link RedisValue}, or null if none
	 * @return the converted data in the appropriate return type
	 * @throws Exception if any error occurs during conversion
	 */
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
	
	/**
	 * Creates a new instance of either an array or a collection based on the return type.
	 * 
	 * <p>This method delegates to {@link OrangeReflectionUtils} to create:
	 * <ul>
	 *   <li>A new array of the specified size if returnType is an array type</li>
	 *   <li>A new Collection instance if returnType is a Collection type</li>
	 * </ul>
	 *
	 * @param returnType the type of array or collection to create
	 * @param size the size of the array or initial capacity of the collection
	 * @return a new array or collection instance
	 */
	protected Object getArrayOrCollectionInstance(Class<?> returnType,int size) {
		return OrangeReflectionUtils.getArrayOrCollectionInstance(returnType, size);
	}
	
	/**
	 * Finds a field annotated with {@link RedisValue} in the given type.
	 * 
	 * <p>This method:
	 * <ul>
	 *   <li>Gets the raw type from the generic type</li>
	 *   <li>Searches through declared fields</li>
	 *   <li>Returns the first field with {@link RedisValue} annotation</li>
	 * </ul>
	 *
	 * @param type the type to search for annotated fields
	 * @return the first field annotated with {@link RedisValue}, or null if none found
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
	 * Gets the raw class type from a generic Type.
	 * 
	 * <p>This method delegates to {@link OrangeReflectionUtils} to handle:
	 * <ul>
	 *   <li>Parameterized types (e.g., List&lt;String&gt;)</li>
	 *   <li>Type variables</li>
	 *   <li>Wild card types</li>
	 * </ul>
	 *
	 * @param type the Type to get the raw class from
	 * @return the raw Class object
	 */
	protected Class<?> getRawType(Type type){
		return OrangeReflectionUtils.getRawType(type);
	}
	
	/**
	 * Gets the generic type argument from the method's return type.
	 * 
	 * <p>This method delegates to {@link OrangeReflectionUtils} to:
	 * <ul>
	 *   <li>Extract the generic type from collections (e.g., List&lt;String&gt; → String)</li>
	 *   <li>Extract the component type from arrays (e.g., String[] → String)</li>
	 *   <li>Handle nested generic types</li>
	 * </ul>
	 *
	 * @param context the Redis operation context containing method information
	 * @return the generic type argument of the return type
	 */
	protected Type getReturnArgumentType(OrangeRedisContext context){
		return OrangeReflectionUtils.getCollectionOrArrayArgumentType(context.getOperationMethod().getGenericReturnType());
	}
}