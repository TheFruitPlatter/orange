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

import com.langwuyue.orange.redis.annotation.GetIndexs;
import com.langwuyue.orange.redis.annotation.RedisValue;
import com.langwuyue.orange.redis.annotation.zset.Reverse;
import com.langwuyue.orange.redis.context.OrangeRedisContext;
import com.langwuyue.orange.redis.context.OrangeRedisValueContext;
import com.langwuyue.orange.redis.executor.OrangeRedisAbstractExecutor;
import com.langwuyue.orange.redis.mapping.OrangeRedisExecutorIdGenerator;
import com.langwuyue.orange.redis.operations.OrangeRedisListOperations;
import com.langwuyue.orange.redis.utils.OrangeCollectionUtils;

/**
 * Executor implementation for finding the last occurrence of a value in a Redis list.
 * 
 * <p>This executor searches for a value in a Redis list starting from the end (right side)
 * of the list and returns the index of its last occurrence. It is particularly useful when
 * you need to find the rightmost position of a value in a list that may contain duplicate values.
 * 
 * <p>This executor supports three annotations:
 * <ul>
 *   <li>{@link GetIndexs} - Marks the method as an index retrieval operation</li>
 *   <li>{@link RedisValue} - Specifies the value to search for in the list</li>
 *   <li>{@link Reverse} - Indicates that the search should be performed from right to left</li>
 * </ul>
 *
 * @author Liang.Zhong
 * @since 1.0.0
 * @see <a href="https://orange.langwuyue.com/redis/advanced/list">Orange Redis List Documentation</a>
 */
public class OrangeReverseGetIndexExecutor extends OrangeRedisAbstractExecutor {
	
	/**
	 * Redis list operations implementation used to perform reverse index lookup operations.
	 * This field provides access to the underlying Redis commands for list manipulation,
	 * specifically the lastIndexOf operation for reverse searching.
	 */
	private OrangeRedisListOperations operations;

	/**
	 * Constructs a new OrangeReverseGetIndexExecutor instance.
	 *
	 * @param operations the Redis list operations implementation to use for executing commands
	 * @param idGenerator the generator for creating unique executor IDs
	 */
	public OrangeReverseGetIndexExecutor(OrangeRedisListOperations operations,OrangeRedisExecutorIdGenerator idGenerator) {
		super(idGenerator);
		this.operations = operations;
	}

	/**
	 * Returns the list of annotation classes that this executor supports.
	 * 
	 * <p>This executor requires three annotations to be present on the method:
	 * <ul>
	 *   <li>{@link GetIndexs} - Identifies this as an index retrieval operation</li>
	 *   <li>{@link RedisValue} - Provides the value to search for in the list</li>
	 *   <li>{@link Reverse} - Indicates that the search should be performed from right to left</li>
	 * </ul>
	 *
	 * @return a list of supported annotation classes
	 */
	@Override
	protected List<Class<? extends Annotation>> getSupportedAnnotationClasses() {
		return OrangeCollectionUtils.asList(GetIndexs.class,RedisValue.class,Reverse.class);
	}
	
	/**
	 * Specifies the context class required for this executor.
	 * 
	 * <p>This executor uses {@link OrangeRedisValueContext} which extends the base context
	 * to provide additional functionality for handling value-based operations. This context
	 * type is necessary because the reverse index lookup operation needs to access both
	 * the Redis key and the specific value to search for.
	 *
	 * @return the {@link OrangeRedisValueContext} class
	 */
	@Override
	public Class<? extends OrangeRedisContext> getContextClass() {
		return OrangeRedisValueContext.class;
	}

	/**
	 * Executes the reverse index lookup operation on a Redis list.
	 * 
	 * @param context the execution context containing the Redis key, value to search for, and other parameters
	 * @return the index of the last occurrence of the value in the list (0-based, from right to left),
	 *         or -1 if the value is not found
	 * @throws Exception if an error occurs during the execution
	 */
	@Override
	public Object execute(OrangeRedisContext context) throws Exception {
		OrangeRedisValueContext ctx = (OrangeRedisValueContext) context;
		return this.operations.lastIndexOf(
				context.getRedisKey().getValue(), 
				ctx.getValue(), 
				context.getValueType()
		);
	}
}