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

import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

import com.langwuyue.orange.redis.context.OrangeRedisContext;
import com.langwuyue.orange.redis.mapping.OrangeRedisExecutorIdGenerator;
/**
 * Abstract base class for executors that retrieve a single member with its score from a Redis Sorted Set.
 * 
 * <p>This class extends {@link OrangeGetWithScoresAbstractExecutor} to provide specialized handling
 * for operations that return a single member-score pair. It includes logic to handle different return
 * types (arrays, collections, maps, or single objects) and automatically converts the result to the
 * appropriate type.
 *
 * <p>Key features:
 * <ul>
 *   <li>Supports returning a single object or collection/array/map of results</li>
 *   <li>Handles null results appropriately</li>
 *   <li>Provides type conversion for member-score pairs</li>
 *   <li>Supports flexible return type mapping</li>
 * </ul>
 *
 * <p>This class serves as a base for concrete executors that need to retrieve single
 * members with scores from Redis Sorted Sets, providing common functionality while
 * allowing specific implementations to define their own retrieval logic.
 *
 * @author Liang.Zhong
 * @since 1.0.0
 * @see OrangeGetWithScoresAbstractExecutor
 * @see <a href="https://orange.langwuyue.com/redis/advanced/zset">Orange Redis ZSet Documentation</a>
 */
public abstract class OrangeGetOneWithScoresAbstractExecutor extends OrangeGetWithScoresAbstractExecutor {
	
	/**
	 * Constructs a new instance of the executor.
	 *
	 * @param idGenerator the generator for creating unique operation IDs
	 */
	protected OrangeGetOneWithScoresAbstractExecutor(OrangeRedisExecutorIdGenerator idGenerator) {
		super(idGenerator);
	}

	/**
	 * Executes the operation to retrieve a member with its score from Redis.
	 *
	 * <p>This method handles the execution result based on the return type:
	 * <ul>
	 *   <li>For array, Collection, or Map return types, returns the result as is</li>
	 *   <li>For single object return types, extracts the first element from the result</li>
	 *   <li>Returns null if the result is null or empty</li>
	 * </ul>
	 *
	 * @param context the context containing operation parameters and return type information
	 * @return the execution result, converted to the appropriate type
	 * @throws Exception if an error occurs during execution or type conversion
	 */
	@Override
	public Object execute(OrangeRedisContext context) throws Exception {
		Object result = super.execute(context);
		if(result == null) {
			return result;
		}
		Class<?> returnClass = context.getOperationMethod().getReturnType();
		if(returnClass.isArray() 
				|| Collection.class.isAssignableFrom(returnClass) 
				|| Map.class.isAssignableFrom(returnClass)) {
			return result;	
		}
		else{
			ArrayList list = ((ArrayList)result);
			return list.isEmpty() ? null : list.get(0);
		}
	}

	/**
	 * Determines the return argument type based on the method's return type.
	 *
	 * <p>This method provides specialized handling for different return types:
	 * <ul>
	 *   <li>For array, Collection, or Map types, delegates to superclass</li>
	 *   <li>For single object types, returns the method's generic return type</li>
	 * </ul>
	 *
	 * @param context the context containing method information
	 * @return the Type to be used for result conversion
	 */
	@Override
	protected Type getReturnArgumentType(OrangeRedisContext context) {
		Class<?> returnClass = context.getOperationMethod().getReturnType();
		if(returnClass.isArray() 
				|| Collection.class.isAssignableFrom(returnClass) 
				|| Map.class.isAssignableFrom(returnClass)) {
			return super.getReturnArgumentType(context);	
		}
		else{
			return context.getOperationMethod().getGenericReturnType();
		}
	}
	
	/**
	 * Converts the result collection to the appropriate return value type.
	 *
	 * <p>This method handles different return type scenarios:
	 * <ul>
	 *   <li>Map return types - converts to appropriate map structure</li>
	 *   <li>Collection return types with scores - converts to score-based collection</li>
	 *   <li>Single object return types - converts to appropriate object type</li>
	 * </ul>
	 *
	 * @param context the context containing method information
	 * @param result the collection of results to convert
	 * @param returnArgumentType the target return argument type
	 * @param field the field information for type conversion
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
			Type genericType = this.getReturnArgumentType(context);
			Class<?> rawType = getRawType(genericType);
			if(Map.class.isAssignableFrom(rawType)) {
				return toWithScoreReturnMultipleMap(result, field, returnArgumentType, returnClass, genericType);
			}
			return toWithScoreReturnValue(result, field, returnArgumentType, returnClass);
		}
	}

	/**
	 * Creates an appropriate collection instance based on the return type.
	 *
	 * <p>This method determines the type of collection to create:
	 * <ul>
	 *   <li>For array, Collection, or Map types, delegates to superclass</li>
	 *   <li>For single object types, creates an ArrayList with initial capacity of 1</li>
	 * </ul>
	 *
	 * @param returnClass the class of the return type
	 * @param size the expected size of the collection
	 * @return a new collection instance of the appropriate type
	 */
	@Override
	protected Object getArrayOrCollectionInstance(Class<?> returnClass, int size) {
		if(returnClass.isArray() 
				|| Collection.class.isAssignableFrom(returnClass) 
				|| Map.class.isAssignableFrom(returnClass)) {
			return super.getArrayOrCollectionInstance(returnClass, size);
		}
		return new ArrayList<>(1);
	}
}