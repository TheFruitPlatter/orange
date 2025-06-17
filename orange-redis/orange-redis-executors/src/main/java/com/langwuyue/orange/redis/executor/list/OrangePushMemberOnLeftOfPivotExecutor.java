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
import com.langwuyue.orange.redis.annotation.RedisValue;
import com.langwuyue.orange.redis.annotation.list.Left;
import com.langwuyue.orange.redis.annotation.list.Pivot;
import com.langwuyue.orange.redis.context.OrangeRedisContext;
import com.langwuyue.orange.redis.executor.OrangeRedisAbstractExecutor;
import com.langwuyue.orange.redis.executor.list.context.OrangePivotValueContext;
import com.langwuyue.orange.redis.mapping.OrangeRedisExecutorIdGenerator;
import com.langwuyue.orange.redis.operations.OrangeRedisListOperations;
import com.langwuyue.orange.redis.utils.OrangeCollectionUtils;

/**
 * Executor implementation for inserting a value to the left of a pivot element in a Redis list.
 * 
 * <p>This executor provides functionality to insert a new element to the left (before) 
 * a specified pivot element in a Redis list. If the pivot element occurs multiple times,
 * the insertion happens before the first occurrence only.
 * 
 * <p>The operation returns different values based on the method's return type:
 * <ul>
 *   <li>For boolean return types: returns true if insertion was successful</li>
 *   <li>For integer return types: returns the new length of the list after insertion</li>
 *   <li>For Long return types: returns the new length of the list as a Long</li>
 * </ul>
 *
 * @author Liang.Zhong
 * @since 1.0.0
 * @see <a href="https://orange.langwuyue.com/redis/advanced/list">Orange Redis List Documentation</a>
 */
public class OrangePushMemberOnLeftOfPivotExecutor extends OrangeRedisAbstractExecutor {
	
	/**
	 * Redis list operations handler that provides the core functionality for interacting with Redis lists.
	 * This field is used to execute the actual LINSERT command on the Redis server.
	 */
	private OrangeRedisListOperations operations;

	/**
	 * Constructs a new OrangePushMemberOnLeftOfPivotExecutor with the specified operations and ID generator.
	 *
	 * @param operations the Redis list operations handler that will execute the LINSERT command
	 * @param idGenerator the generator used to create unique identifiers for this executor
	 */
	public OrangePushMemberOnLeftOfPivotExecutor(OrangeRedisListOperations operations,OrangeRedisExecutorIdGenerator idGenerator) {
		super(idGenerator);
		this.operations = operations;
	}

	/**
	 * Executes the LINSERT operation to insert a value to the left of a pivot element in a Redis list.
	 * 
	 * <p>This method inserts the value specified in the context to the left (before) of the
	 * pivot element in the Redis list identified by the key provided in the context.
	 * 
	 * <p>The return value is adapted based on the expected return type:
	 * <ul>
	 *   <li>For boolean types: returns true if insertion was successful (result > 0)</li>
	 *   <li>For integer types: returns the new length of the list as an int</li>
	 *   <li>For other types: returns the raw Long result from Redis</li>
	 * </ul>
	 *
	 * @param context the execution context containing the key, pivot, and value information
	 * @return the result of the operation, adapted to the expected return type
	 * @throws Exception if an error occurs during execution
	 * @throws ClassCastException if the provided context is not an instance of {@link OrangePivotValueContext}
	 */
	@Override
	public Object execute(OrangeRedisContext context) throws Exception {
		OrangePivotValueContext ctx = (OrangePivotValueContext) context;
		Long result = this.operations.leftPush(context.getRedisKey().getValue(), ctx.getPivot(), ctx.getValue(),ctx.getValueType());
		Class<?> returnClass = context.getOperationMethod().getReturnType();
		if(returnClass == Boolean.class || returnClass == boolean.class) {
			return result != null && result > 0;	
		}
		if(returnClass == Integer.class || returnClass == int.class) {
			return result == null ? 0 : Integer.valueOf(result.toString());	
		}
		return result;
	}

	/**
	 * Returns the list of annotation classes that this executor supports.
	 * 
	 * <p>This executor supports the following annotations:
	 * <ul>
	 *   <li>{@link Left} - Indicates that the insertion should be to the left of the pivot</li>
	 *   <li>{@link AddMembers} - Indicates that this operation adds members to a collection</li>
	 *   <li>{@link RedisValue} - Marks the parameter that provides the value to be inserted</li>
	 *   <li>{@link Pivot} - Marks the parameter that provides the pivot element</li>
	 * </ul>
	 *
	 * @return a list containing the supported annotation classes
	 */
	@Override
	protected List<Class<? extends Annotation>> getSupportedAnnotationClasses() {
		return OrangeCollectionUtils.asList(Left.class,AddMembers.class,RedisValue.class,Pivot.class);
	}
	
	/**
	 * Returns the context class required for this executor.
	 * 
	 * <p>This executor requires {@link OrangePivotValueContext} which provides
	 * both the pivot element and the value to be inserted to the left of the pivot.
	 *
	 * @return the {@link OrangePivotValueContext} class
	 */
	@Override
	public Class<? extends OrangeRedisContext> getContextClass() {
		return OrangePivotValueContext.class;
	}

}