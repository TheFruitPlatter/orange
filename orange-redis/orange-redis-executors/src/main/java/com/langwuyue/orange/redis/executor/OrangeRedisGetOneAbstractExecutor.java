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

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;

import com.langwuyue.orange.redis.context.OrangeRedisContext;
import com.langwuyue.orange.redis.mapping.OrangeRedisExecutorIdGenerator;

/**
 * Abstract executor for Redis operations that retrieve a single value.
 * 
 * <p>This class extends {@link OrangeRedisGetAbstractExecutor} and specializes it for
 * operations that return a single value rather than a collection. It handles the
 * conversion between Redis' collection-based responses and single-value Java types.
 * 
 * <p>Key features:
 * <ul>
 *   <li>Automatically extracts single values from collection results</li>
 *   <li>Maintains collection/array return types when explicitly requested</li>
 *   <li>Handles null results appropriately</li>
 *   <li>Supports both primitive and object return types</li>
 * </ul>
 * 
 * @author Liang.Zhong
 * @since 1.0.0
 * @see OrangeRedisGetAbstractExecutor
 */
public abstract class OrangeRedisGetOneAbstractExecutor extends OrangeRedisGetAbstractExecutor {
	
	/**
	 * Creates a new instance of OrangeRedisGetOneAbstractExecutor.
	 *
	 * @param idGenerator the generator for creating unique executor IDs
	 */
	public OrangeRedisGetOneAbstractExecutor(OrangeRedisExecutorIdGenerator idGenerator) {
		super(idGenerator);
	}

	/**
	 * {@inheritDoc}
	 * 
	 * <p>This implementation extends the parent's execute method to handle single-value
	 * return types. The process follows these steps:
	 * <ol>
	 *   <li>Calls parent's execute method to get the initial result</li>
	 *   <li>Returns null if the result is null</li>
	 *   <li>If return type is Collection or array, returns the result as-is</li>
	 *   <li>Otherwise, extracts and returns the first element from the result list</li>
	 * </ol>
	 *
	 * @param context the Redis operation context
	 * @return the single value result, or null if not found
	 * @throws Exception if any error occurs during execution
	 */
	@Override
	public Object execute(OrangeRedisContext context) throws Exception {
		Object result = super.execute(context);
		if(result == null) {
			return result;
		}
		Class<?> returnClass = context.getOperationMethod().getReturnType();
		if(Collection.class.isAssignableFrom(returnClass) || returnClass.isArray()) {
			return result;	
		}
		else{
			ArrayList list = ((ArrayList)result);
			return list.isEmpty() ? null : list.get(0);
		}
	}
	
	/**
	 * {@inheritDoc}
	 * 
	 * <p>This implementation modifies the return type resolution for single-value cases:
	 * <ul>
	 *   <li>For Collection/array return types, delegates to parent implementation</li>
	 *   <li>For single-value types, returns the method's direct return type</li>
	 * </ul>
	 *
	 * @param context the Redis operation context
	 * @return the resolved return type for value conversion
	 */
	@Override
	protected Type getReturnArgumentType(OrangeRedisContext context) {
		Class<?> returnClass = context.getOperationMethod().getReturnType();
		if(Collection.class.isAssignableFrom(returnClass) || returnClass.isArray()) {
			return super.getReturnArgumentType(context);	
		}
		else{
			return context.getOperationMethod().getGenericReturnType();
		}
	}

	/**
	 * {@inheritDoc}
	 * 
	 * <p>This implementation customizes collection creation for single-value cases:
	 * <ul>
	 *   <li>For Collection/array return types, delegates to parent implementation</li>
	 *   <li>For single-value types, creates an ArrayList with capacity 1</li>
	 * </ul>
	 *
	 * @param returnClass the return type class
	 * @param size the expected size of the collection
	 * @return a new collection instance
	 */
	@Override
	protected Object getArrayOrCollectionInstance(Class<?> returnClass, int size) {
		if(Collection.class.isAssignableFrom(returnClass) || returnClass.isArray()) {
			return super.getArrayOrCollectionInstance(returnClass, size);
		}
		return new ArrayList<>(1);
	}

}