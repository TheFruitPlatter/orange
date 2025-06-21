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
package com.langwuyue.orange.redis.executor.cross.set;

import java.lang.annotation.Annotation;
import java.util.List;

import com.langwuyue.orange.redis.annotation.RedisValue;
import com.langwuyue.orange.redis.annotation.cross.CrossOperationKeys;
import com.langwuyue.orange.redis.annotation.cross.Move;
import com.langwuyue.orange.redis.annotation.cross.StoreTo;
import com.langwuyue.orange.redis.context.OrangeRedisContext;
import com.langwuyue.orange.redis.executor.OrangeRedisAbstractExecutor;
import com.langwuyue.orange.redis.executor.cross.set.context.OrangeMoveContext;
import com.langwuyue.orange.redis.mapping.OrangeRedisExecutorIdGenerator;
import com.langwuyue.orange.redis.operations.OrangeRedisSetOperations;
import com.langwuyue.orange.redis.utils.OrangeCollectionUtils;

/**
 * Executor for moving elements between Redis sets.
 *
 * <p>This executor handles the movement of elements from one Redis set to another.
 * It supports the following annotations:
 * <ul>
 *   <li>{@link Move} - Marks a method as a set move operation</li>
 *   <li>{@link CrossOperationKeys} - Specifies the source and destination keys</li>
 *   <li>{@link StoreTo} - Specifies the destination key (alternative to CrossOperationKeys)</li>
 *   <li>{@link RedisValue} - Specifies the value to be moved</li>
 * </ul>
 *
 * @author Liang.Zhong
 * @since 1.0.0
 */
public class OrangeMoveExecutor extends OrangeRedisAbstractExecutor {
	
	/**
	 * Redis set operations instance used to perform the move operation.
	 */
	private OrangeRedisSetOperations operations;

	/**
	 * Constructs a new move executor.
	 * 
	 * @param operations the Redis set operations instance
	 * @param idGenerator the executor ID generator
	 */
	public OrangeMoveExecutor(OrangeRedisSetOperations operations, OrangeRedisExecutorIdGenerator idGenerator) {
		super(idGenerator);
		this.operations = operations;
	}

	/**
	 * Returns the list of annotation classes supported by this executor.
	 * 
	 * <p>This executor supports the following annotations:
	 * <ul>
	 *   <li>{@link Move} - Marks a method as a set move operation</li>
	 *   <li>{@link CrossOperationKeys} - Specifies the source and destination keys</li>
	 *   <li>{@link StoreTo} - Specifies the destination key</li>
	 *   <li>{@link RedisValue} - Specifies the value to be moved</li>
	 * </ul>
	 * 
	 * @return list of supported annotation classes
	 */
	@Override
	protected List<Class<? extends Annotation>> getSupportedAnnotationClasses() {
		return OrangeCollectionUtils.asList(Move.class,CrossOperationKeys.class,StoreTo.class,RedisValue.class);
	}

	/**
	 * Executes the move operation on Redis sets.
	 * 
	 * <p>Performs the following steps:
	 * <ol>
	 *   <li>Removes the specified element from the source set</li>
	 *   <li>Adds the element to the destination set</li>
	 *   <li>Returns true if the element was moved successfully, false otherwise</li>
	 * </ol>
	 * 
	 * @param context the Redis operation context containing source and destination keys
	 * @return true if the element was moved successfully, false otherwise
	 * @throws Exception if the operation fails
	 */
	@Override
	public Object execute(OrangeRedisContext context) throws Exception {
		OrangeMoveContext ctx = (OrangeMoveContext) context;
		return this.operations.move(ctx.getReferenceKey(),ctx.getValue(),ctx.getValueType(),ctx.getStoreTo());
	}
	
	/**
	 * Returns the context class used by this executor for move operations.
	 * 
	 * @return the OrangeCrossOperationContext class used for this operation
	 */
	@Override
	public Class<? extends OrangeRedisContext> getContextClass() {
		return OrangeMoveContext.class;
	}
}