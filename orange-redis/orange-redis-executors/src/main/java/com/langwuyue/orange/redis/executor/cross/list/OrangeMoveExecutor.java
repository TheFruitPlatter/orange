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
package com.langwuyue.orange.redis.executor.cross.list;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.List;

import com.langwuyue.orange.redis.annotation.cross.CrossOperationKeys;
import com.langwuyue.orange.redis.annotation.cross.ListMoveDirection;
import com.langwuyue.orange.redis.annotation.cross.Move;
import com.langwuyue.orange.redis.annotation.cross.StoreTo;
import com.langwuyue.orange.redis.context.OrangeRedisContext;
import com.langwuyue.orange.redis.executor.OrangeRedisGetOneAbstractExecutor;
import com.langwuyue.orange.redis.executor.cross.list.context.OrangeMoveContext;
import com.langwuyue.orange.redis.mapping.OrangeRedisExecutorIdGenerator;
import com.langwuyue.orange.redis.operations.OrangeRedisListOperations;
import com.langwuyue.orange.redis.utils.OrangeCollectionUtils;

/**
 * Executor for Redis list move operations between different keys.
 * 
 * <p>This executor handles the movement of elements between Redis lists with support for:
 * <ul>
 *   <li>Specifying source and destination keys</li>
 *   <li>Controlling move direction (LEFT/RIGHT)</li>
 *   <li>Type conversion of moved values</li>
 * </ul>
 *
 * @author Liang.Zhong
 * @since 1.0.0
 */
public class OrangeMoveExecutor extends OrangeRedisGetOneAbstractExecutor {
	
	/**
	 * Redis list operations instance used to perform the actual move operation.
	 */
	private OrangeRedisListOperations operations;

	/**
	 * Creates a new OrangeMoveExecutor instance.
	 * 
	 * @param operations the Redis list operations instance
	 * @param idGenerator the executor ID generator
	 */
	public OrangeMoveExecutor(OrangeRedisListOperations operations, OrangeRedisExecutorIdGenerator idGenerator) {
		super(idGenerator);
		this.operations = operations;
	}

	/**
	 * Returns the list of annotation classes supported by this executor.
	 * 
	 * @return a list containing the following annotation classes:
	 * <ul>
	 *   <li>{@link Move}</li>
	 *   <li>{@link CrossOperationKeys}</li>
	 *   <li>{@link StoreTo}</li>
	 *   <li>{@link ListMoveDirection}</li>
	 * </ul>
	 */
	@Override
	protected List<Class<? extends Annotation>> getSupportedAnnotationClasses() {
		return OrangeCollectionUtils.asList(Move.class,CrossOperationKeys.class,StoreTo.class,ListMoveDirection.class);
	}

	/**
	 * Returns the context class used by this executor.
	 * 
	 * @return the {@link OrangeMoveContext} class which holds all necessary
	 *         information for list move operations
	 */
	@Override
	public Class<? extends OrangeRedisContext> getContextClass() {
		return OrangeMoveContext.class;
	}

	/**
	 * Executes the Redis list move operation.
	 * 
	 * @param context the execution context containing:
	 * <ul>
	 *   <li>Source list key</li>
	 *   <li>Destination list key</li>
	 *   <li>Move direction (LEFT/RIGHT)</li>
	 *   <li>Value to move</li>
	 * </ul>
	 * @param valueField the field annotated with value to move (may be null)
	 * @param returnArgumentType the expected return type
	 * @return a collection containing the moved value
	 * @throws Exception if the move operation fails
	 */
	@Override
	protected Collection doGet(OrangeRedisContext context, Field valueField, Type returnArgumentType) throws Exception {
		OrangeMoveContext ctx = (OrangeMoveContext) context;
		Object result = this.operations.move(ctx.getReferenceKey(),ctx.getStoreTo(),ctx.getDirection(),ctx.getValueType(),valueField == null ? returnArgumentType : valueField.getGenericType());
		return OrangeCollectionUtils.asList(result);
	}
}