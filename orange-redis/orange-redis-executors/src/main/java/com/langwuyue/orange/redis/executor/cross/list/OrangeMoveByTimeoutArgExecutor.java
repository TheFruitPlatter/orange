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

import com.langwuyue.orange.redis.annotation.TimeoutUnit;
import com.langwuyue.orange.redis.annotation.TimeoutValue;
import com.langwuyue.orange.redis.annotation.cross.CrossOperationKeys;
import com.langwuyue.orange.redis.annotation.cross.ListMoveDirection;
import com.langwuyue.orange.redis.annotation.cross.Move;
import com.langwuyue.orange.redis.annotation.cross.StoreTo;
import com.langwuyue.orange.redis.context.OrangeRedisContext;
import com.langwuyue.orange.redis.executor.OrangeRedisGetOneAbstractExecutor;
import com.langwuyue.orange.redis.executor.cross.list.context.OrangeMoveTimeoutValueAndUnitContext;
import com.langwuyue.orange.redis.mapping.OrangeRedisExecutorIdGenerator;
import com.langwuyue.orange.redis.operations.OrangeRedisListOperations;
import com.langwuyue.orange.redis.utils.OrangeCollectionUtils;

/**
 * Executor for Redis list move operations with timeout support.
 * 
 * <p>This executor extends the basic move functionality by adding support for:
 * <ul>
 *   <li>Timeout value specification</li>
 *   <li>Timeout unit configuration</li>
 *   <li>All standard move operations between Redis lists</li>
 * </ul>
 *
 * @author Liang.Zhong
 * @since 1.0.0
 */
public class OrangeMoveByTimeoutArgExecutor extends OrangeRedisGetOneAbstractExecutor {
	
	/**
	 * Redis list operations instance used to perform the actual move operation with timeout.
	 */
	private OrangeRedisListOperations operations;

	/**
	 * Creates a new OrangeMoveByTimeoutArgExecutor instance.
	 * 
	 * @param operations the Redis list operations instance
	 * @param idGenerator the executor ID generator
	 */
	public OrangeMoveByTimeoutArgExecutor(OrangeRedisListOperations operations, OrangeRedisExecutorIdGenerator idGenerator) {
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
	 *   <li>{@link TimeoutUnit}</li>
	 * </ul>
	 */
	@Override
	protected List<Class<? extends Annotation>> getSupportedAnnotationClasses() {
		return OrangeCollectionUtils.asList(Move.class,CrossOperationKeys.class,StoreTo.class,ListMoveDirection.class,TimeoutValue.class,TimeoutUnit.class);
	}

	/**
	 * Returns the context class used by this executor.
	 * 
	 * @return the {@link OrangeMoveTimeoutValueAndUnitContext} class which holds:
	 * <ul>
	 *   <li>Source and destination keys</li>
	 *   <li>Move direction</li>
	 *   <li>Timeout value</li>
	 *   <li>Timeout unit</li>
	 * </ul>
	 */
	@Override
	public Class<? extends OrangeRedisContext> getContextClass() {
		return OrangeMoveTimeoutValueAndUnitContext.class;
	}

	/**
	 * Executes the Redis list move operation with timeout support.
	 * 
	 * @param context the execution context containing:
	 * <ul>
	 *   <li>Source list key</li>
	 *   <li>Destination list key</li>
	 *   <li>Move direction (LEFT/RIGHT)</li>
	 *   <li>Value to move</li>
	 *   <li>Timeout value</li>
	 *   <li>Timeout unit</li>
	 * </ul>
	 * @param valueField the field annotated with value to move (may be null)
	 * @param returnArgumentType the expected return type
	 * @return a collection containing the moved value
	 * @throws Exception if the move operation fails or timeout occurs
	 */
	@Override
	protected Collection doGet(OrangeRedisContext context, Field valueField, Type returnArgumentType) throws Exception {
		OrangeMoveTimeoutValueAndUnitContext ctx = (OrangeMoveTimeoutValueAndUnitContext) context;
		Object result = this.operations.move(
				ctx.getReferenceKey(),
				ctx.getStoreTo(),
				ctx.getDirection(),
				ctx.getValueType(),
				valueField == null ? returnArgumentType : valueField.getGenericType(),
				ctx.getValue(),
				ctx.getUnit()
		);
		return OrangeCollectionUtils.asList(result);
	}
}