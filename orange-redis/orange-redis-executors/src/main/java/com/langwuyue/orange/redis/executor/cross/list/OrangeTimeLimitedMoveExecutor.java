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

import com.langwuyue.orange.redis.annotation.Timeout;
import com.langwuyue.orange.redis.annotation.cross.CrossOperationKeys;
import com.langwuyue.orange.redis.annotation.cross.ListMoveDirection;
import com.langwuyue.orange.redis.annotation.cross.Move;
import com.langwuyue.orange.redis.annotation.cross.StoreTo;
import com.langwuyue.orange.redis.context.OrangeRedisContext;
import com.langwuyue.orange.redis.executor.OrangeRedisGetOneAbstractExecutor;
import com.langwuyue.orange.redis.executor.cross.list.context.OrangeMoveTimeoutContext;
import com.langwuyue.orange.redis.mapping.OrangeRedisExecutorIdGenerator;
import com.langwuyue.orange.redis.operations.OrangeRedisListOperations;
import com.langwuyue.orange.redis.utils.OrangeCollectionUtils;

/**
 * Executor for time-limited move operations between Redis lists.
 *
 * <p>This executor handles the movement of elements between Redis lists with a specified timeout.
 * It supports the following annotations:
 * <ul>
 *   <li>{@link Move} - Marks a method as a list move operation</li>
 *   <li>{@link CrossOperationKeys} - Specifies the source list key</li>
 *   <li>{@link StoreTo} - Specifies the destination list key</li>
 *   <li>{@link ListMoveDirection} - Specifies the direction of movement (LEFT/RIGHT)</li>
 *   <li>{@link Timeout} - Specifies the timeout for the operation</li>
 * </ul>
 *
 * @author Liang.Zhong
 * @since 1.0.0
 */
public class OrangeTimeLimitedMoveExecutor extends OrangeRedisGetOneAbstractExecutor {
	
	/**
	 * Redis list operations instance used to perform the actual move operation.
	 */
	private OrangeRedisListOperations operations;

	/**
	 * Creates a new time-limited move executor.
	 *
	 * @param operations the Redis list operations instance
	 * @param idGenerator the executor ID generator
	 */
	public OrangeTimeLimitedMoveExecutor(OrangeRedisListOperations operations, OrangeRedisExecutorIdGenerator idGenerator) {
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
	 *   <li>{@link Timeout}</li>
	 * </ul>
	 */
	@Override
	protected List<Class<? extends Annotation>> getSupportedAnnotationClasses() {
		return OrangeCollectionUtils.asList(Move.class,CrossOperationKeys.class,StoreTo.class,ListMoveDirection.class,Timeout.class);
	}

	/**
	 * Returns the context class used by this executor.
	 * 
	 * @return the {@link OrangeMoveTimeoutContext} class which holds all necessary
	 *         information for time-limited list move operations
	 */
	@Override
	public Class<? extends OrangeRedisContext> getContextClass() {
		return OrangeMoveTimeoutContext.class;
	}

	/**
	 * Executes the time-limited list move operation.
	 * 
	 * @param context the execution context containing:
	 * <ul>
	 *   <li>Source list key</li>
	 *   <li>Destination list key</li>
	 *   <li>Move direction (LEFT/RIGHT)</li>
	 *   <li>Timeout value and unit</li>
	 *   <li>Value to move</li>
	 * </ul>
	 * @param valueField the field annotated with value to move (may be null)
	 * @param returnArgumentType the expected return type
	 * @return a collection containing the moved value
	 * @throws Exception if the move operation fails
	 */
	@Override
	protected Collection doGet(OrangeRedisContext context, Field valueField, Type returnArgumentType) throws Exception {
		OrangeMoveTimeoutContext ctx = (OrangeMoveTimeoutContext) context;
		Object result = this.operations.move(
				ctx.getReferenceKey(),
				ctx.getStoreTo(),
				ctx.getDirection(),
				ctx.getValueType(),
				valueField == null ? returnArgumentType : valueField.getGenericType(),
				ctx.getTimeout().value(),
				ctx.getTimeout().unit()
		);
		return OrangeCollectionUtils.asList(result);
	}
}