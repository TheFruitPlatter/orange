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
package com.langwuyue.orange.redis.executor.list;

import java.lang.annotation.Annotation;
import java.util.List;

import com.langwuyue.orange.redis.annotation.AddMembers;
import com.langwuyue.orange.redis.annotation.ContinueOnFailure;
import com.langwuyue.orange.redis.annotation.Multiple;
import com.langwuyue.orange.redis.annotation.list.Right;
import com.langwuyue.orange.redis.context.OrangeRedisContext;
import com.langwuyue.orange.redis.context.OrangeRedisMultipleValueContext;
import com.langwuyue.orange.redis.executor.OrangeAddMembersAbstractExecutor;
import com.langwuyue.orange.redis.logger.OrangeRedisLogger;
import com.langwuyue.orange.redis.mapping.OrangeRedisExecutorIdGenerator;
import com.langwuyue.orange.redis.operations.OrangeRedisListOperations;
import com.langwuyue.orange.redis.utils.OrangeCollectionUtils;

/**
 * Executor implementation for pushing multiple elements to the right side of a Redis list.
 * 
 * <p>This executor extends {@link OrangeAddMembersAbstractExecutor} to provide
 * functionality for adding multiple elements to the end (right side)
 * of a Redis list. It supports the {@link Right}, {@link AddMembers}, {@link Multiple},
 * and {@link ContinueOnFailure} annotations.
 * 
 * <p>The executor adds elements to the list in the order they are provided,
 * so that each new element becomes the rightmost element in the list.
 * The operation returns the new length of the list after the push operation.
 * 
 * <p>When {@link ContinueOnFailure} is used, the executor will continue processing
 * remaining elements even if some operations fail, collecting errors for later reporting.
 *
 * @author Liang.Zhong
 * @since 1.0.0
 * @see <a href="https://orange.langwuyue.com/redis/advanced/list">Orange Redis List Documentation</a>
 */
public class OrangeRightPushMembersExecutor extends OrangeAddMembersAbstractExecutor {
	
	/**
	 * Redis list operations interface for performing list-specific operations.
	 */
	private OrangeRedisListOperations operations;

	/**
	 * Constructs a new OrangeRightPushMembersExecutor with the specified operations, ID generator, and logger.
	 *
	 * @param operations the Redis list operations interface
	 * @param idGenerator the executor ID generator for generating unique identifiers
	 * @param logger the logger for recording operation results and errors
	 */
	public OrangeRightPushMembersExecutor(OrangeRedisListOperations operations,OrangeRedisExecutorIdGenerator idGenerator,OrangeRedisLogger logger) {
		super(idGenerator,logger);
		this.operations = operations;
	}

	/**
	 * Returns the list of annotation classes that this executor supports.
	 * 
	 * <p>This executor supports the following annotations:
	 * <ul>
	 *   <li>{@link Right} - Indicates the operation targets the right side of the list</li>
	 *   <li>{@link AddMembers} - Indicates the operation adds elements to the collection</li>
	 *   <li>{@link Multiple} - Indicates the operation handles multiple values at once</li>
	 *   <li>{@link ContinueOnFailure} - Indicates the operation should continue processing remaining elements even if some fail</li>
	 * </ul>
	 *
	 * @return a list of supported annotation classes
	 */
	@Override
	protected List<Class<? extends Annotation>> getSupportedAnnotationClasses() {
		return OrangeCollectionUtils.asList(Right.class,AddMembers.class,Multiple.class,ContinueOnFailure.class);
	}
	
	/**
	 * Returns the context class that this executor requires for execution.
	 * 
	 * <p>This executor requires {@link OrangeRedisMultipleValueContext} which provides
	 * access to multiple values that will be pushed to the Redis list.
	 *
	 * @return the context class required by this executor
	 */
	@Override
	public Class<? extends OrangeRedisContext> getContextClass() {
		return OrangeRedisMultipleValueContext.class;
	}

	/**
	 * Performs the right push operation for multiple values at once.
	 * 
	 * <p>This method is called when processing multiple elements in a batch operation.
	 * It casts the context to {@link OrangeRedisMultipleValueContext} to access multiple values,
	 * then uses the {@code rightPush} operation with an array of values to push all elements
	 * in a single Redis command, improving performance compared to multiple individual pushes.
	 *
	 * @param ctx the execution context containing the Redis key and multiple values
	 * @return the new length of the list after all values are pushed
	 * @throws Exception if an error occurs during execution
	 */
	@Override
	protected Long doAdd(OrangeRedisContext ctx) throws Exception {
		OrangeRedisMultipleValueContext context = (OrangeRedisMultipleValueContext) ctx;
		return this.operations.rightPush(context.getRedisKey().getValue(), context.getValueType(), context.toArray());
	}

	/**
	 * Performs the right push operation for a single value.
	 * 
	 * <p>This method is called when processing individual elements during a multi-value operation,
	 * especially when using the {@link ContinueOnFailure} annotation to handle each element separately.
	 * It pushes a single value to the right side of the Redis list.
	 *
	 * @param ctx the execution context containing the Redis key
	 * @param value the single value to push to the right of the list
	 * @return the new length of the list after the value is pushed
	 * @throws Exception if an error occurs during execution
	 */
	@Override
	protected Long doAdd(OrangeRedisContext ctx, Object value) throws Exception {
		return this.operations.rightPush(ctx.getRedisKey().getValue(), ctx.getValueType(), value);
	}

}