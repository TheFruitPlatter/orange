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
import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import com.langwuyue.orange.redis.annotation.Multiple;
import com.langwuyue.orange.redis.annotation.hash.GetHashValues;
import com.langwuyue.orange.redis.context.OrangeRedisContext;
import com.langwuyue.orange.redis.executor.OrangeRedisGetAbstractExecutor;
import com.langwuyue.orange.redis.executor.hash.context.OrangeHashKeysContext;
import com.langwuyue.orange.redis.mapping.OrangeRedisExecutorIdGenerator;
import com.langwuyue.orange.redis.operations.OrangeRedisHashOperations;
import com.langwuyue.orange.redis.utils.OrangeCollectionUtils;
import com.langwuyue.orange.redis.utils.OrangeReflectionUtils;

/**
 * Executor for retrieving multiple values from a Redis hash.
 * 
 * <p>This executor handles operations annotated with {@link GetHashValues} and {@link Multiple},
 * supporting both collection and map return types. When returning a map, the keys will be the
 * hash field names and the values will be the corresponding hash values.
 *
 * @author Liang.Zhong
 * @since 1.0.0
 * @see GetHashValues
 * @see Multiple
 * @see OrangeRedisHashOperations#multiGet
 */
public class OrangeGetValuesExecutor extends OrangeRedisGetAbstractExecutor {
	
	private OrangeRedisHashOperations operations;

	/**
	 * Constructs a new OrangeGetValuesExecutor.
	 *
	 * <p>This executor requires Redis hash operations for retrieving multiple values
	 * and an ID generator for tracking execution contexts.
	 *
	 * @param operations the Redis hash operations implementation to use for executing commands
	 * @param idGenerator the generator for creating unique executor identifiers
	 */
	public OrangeGetValuesExecutor(OrangeRedisHashOperations operations,OrangeRedisExecutorIdGenerator idGenerator) {
		super(idGenerator);
		this.operations = operations;
	}

	/**
	 * Returns the list of annotation classes supported by this executor.
	 * 
	 * <p>This executor supports methods annotated with both {@link GetHashValues} and {@link Multiple} annotations.
	 * The {@link GetHashValues} annotation marks the method as a hash values retrieval operation,
	 * while the {@link Multiple} annotation indicates the parameter containing multiple hash field names.
	 *
	 * @return a list containing both {@link GetHashValues} and {@link Multiple} annotation classes
	 */
	@Override
	protected List<Class<? extends Annotation>> getSupportedAnnotationClasses() {
		return OrangeCollectionUtils.asList(GetHashValues.class,Multiple.class);
	}

	/**
	 * Returns the context class used by this executor.
	 * 
	 * <p>This executor uses {@link OrangeHashKeysContext} to store and manage the Redis key
	 * and the hash field names to be retrieved.
	 *
	 * @return the {@link OrangeHashKeysContext} class
	 */
	@Override
	public Class<? extends OrangeRedisContext> getContextClass() {
		return OrangeHashKeysContext.class;
	}

	/**
	 * Performs the actual Redis hash values retrieval operation.
	 * 
	 * <p>This method is called by the parent class to execute the Redis operation.
	 * It retrieves multiple values from a Redis hash based on the provided context.
	 *
	 * @param context the execution context containing the Redis key and field names
	 * @param valueField the field representing the value in the return type, may be null
	 * @param returnArgumentType the type of the return value
	 * @return a collection of values corresponding to the requested fields
	 * @throws Exception if there is an error executing the Redis command
	 */
	@Override
	protected Collection doGet(OrangeRedisContext context, Field valueField, Type returnArgumentType) throws Exception {
		OrangeHashKeysContext ctx = (OrangeHashKeysContext) context;
		return this.operations.multiGet(
				ctx.getRedisKey().getValue(),
				ctx.getHashKeys(),
				ctx.getKeyType(),
				ctx.getValueType(),
				valueField == null ? returnArgumentType : valueField.getGenericType()
		);
	}
	
	/**
	 * Determines the actual return argument type for the operation.
	 * 
	 * <p>This method handles both Map and Collection return types:
	 * <ul>
	 *   <li>For Map return types, it extracts the value type from the map's generic type parameters</li>
	 *   <li>For Collection return types, it delegates to the parent class implementation</li>
	 * </ul>
	 *
	 * @param context the execution context containing method return type information
	 * @return the actual type argument for the return value
	 */
	@Override
	protected Type getReturnArgumentType(OrangeRedisContext context) {
		Class<?> returnType = context.getOperationMethod().getReturnType();
		if(Map.class.isAssignableFrom(returnType)) {
			Type genericType = context.getOperationMethod().getGenericReturnType();
			return OrangeReflectionUtils.getMapActaulTypeArguments(genericType)[1];
		} else {
			return super.getReturnArgumentType(context);	
		}
	}

	/**
	 * Converts the Redis operation result to the appropriate return type.
	 * 
	 * <p>This method handles both Map and Collection return types:
	 * <ul>
	 *   <li>For Map return types, it creates a map with hash field names as keys and hash values as values</li>
	 *   <li>For Collection return types, it delegates to the parent class implementation</li>
	 * </ul>
	 *
	 * @param context the execution context containing method return type information
	 * @param result the collection of values retrieved from Redis
	 * @param returnArgumentType the type argument for the return value
	 * @param field the field representing the value in the return type, may be null
	 * @return the result converted to the appropriate return type (Map or Collection)
	 * @throws Exception if there is an error during type conversion
	 */
	@Override
	protected Object toReturnValue(
		OrangeRedisContext context, 
		Collection result, 
		Type returnArgumentType, 
		Field field
	)throws Exception {
		Class<?> returnClass = context.getOperationMethod().getReturnType();
		if(Map.class.isAssignableFrom(returnClass)) {
			OrangeHashKeysContext ctx = (OrangeHashKeysContext) context;
			Map map = OrangeReflectionUtils.newMap(returnClass);
			List keys = ctx.getCachedKeys();
			List values = (List)result;
			int size = values.size();
			for(int i = 0; i < size; i++) {
				map.put(keys.get(i), values.get(i));
			}
			return map;
		}else{
			return super.toReturnValue(context, result, returnArgumentType, field);
		}
	}

	/**
	 * Returns the Redis hash operations implementation used by this executor.
	 * 
	 * <p>This method is primarily used for testing purposes to access the underlying
	 * operations object.
	 *
	 * @return the Redis hash operations implementation
	 */
	OrangeRedisHashOperations getOperations() {
		return operations;
	}
}