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
import com.langwuyue.orange.redis.context.OrangeRedisContext;
import com.langwuyue.orange.redis.context.OrangeRedisValueContext;
import com.langwuyue.orange.redis.executor.OrangeRedisAbstractExecutor;
import com.langwuyue.orange.redis.mapping.OrangeRedisExecutorIdGenerator;
import com.langwuyue.orange.redis.operations.OrangeRedisListOperations;
import com.langwuyue.orange.redis.utils.OrangeCollectionUtils;

/**
 * Executor implementation for pushing a single element to the left side of a Redis list.
 * 
 * <p>This executor extends {@link OrangeRedisAbstractExecutor} to provide
 * functionality for adding a single element to the beginning (left side)
 * of a Redis list. It supports the {@link Left}, {@link AddMembers}, and {@link RedisValue}
 * annotations to indicate that an element should be pushed to the left side of the list.
 * 
 * <p>The executor adds elements to the list in a FIFO (First-In-First-Out) manner,
 * where new elements are added to the beginning of the list. The operation returns
 * the new length of the list after the push operation.
 *
 * @author Liang.Zhong
 * @since 1.0.0
 * @see <a href="https://orange.langwuyue.com/redis/advanced/list">Orange Redis List Documentation</a>
 */
public class OrangeLeftPushMemberExecutor extends OrangeRedisAbstractExecutor {
	
	/**
	 * Redis list operations interface for performing list-specific operations.
	 */
	private OrangeRedisListOperations operations;

	/**
	 * Constructs a new OrangeLeftPushMemberExecutor with the specified operations and ID generator.
	 *
	 * @param operations the Redis list operations interface
	 * @param idGenerator the executor ID generator for generating unique identifiers
	 */
	public OrangeLeftPushMemberExecutor(OrangeRedisListOperations operations,OrangeRedisExecutorIdGenerator idGenerator) {
		super(idGenerator);
		this.operations = operations;
	}

	/**
	 * Executes the left push operation by adding a single element to the left side of the Redis list.
	 * 
	 * <p>This method casts the context to {@link OrangeRedisValueContext} to access
	 * the key and value parameters, then delegates to the Redis list operations to perform
	 * the actual push operation.
	 * 
	 * <p>The method handles different return types based on the operation method's declared return type:
	 * <ul>
	 *   <li>For boolean return types, returns true if the operation was successful (result > 0)</li>
	 *   <li>For integer return types, returns the result as an Integer or 0 if null</li>
	 *   <li>For other types, returns the raw Long result</li>
	 * </ul>
	 *
	 * @param context the execution context containing the key and value to be pushed
	 * @return the result of the push operation, converted to the appropriate return type
	 * @throws Exception if an error occurs during execution
	 */
	@Override
	public Object execute(OrangeRedisContext context) throws Exception {
		OrangeRedisValueContext ctx = (OrangeRedisValueContext) context;
		Long result = this.operations.leftPush(context.getRedisKey().getValue(), ctx.getValueType(), ctx.getValue());
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
	 *   <li>{@link Left} - Indicates the operation targets the left side of the list</li>
	 *   <li>{@link AddMembers} - Indicates the operation adds elements to the collection</li>
	 *   <li>{@link RedisValue} - Indicates the parameter contains the value to be added</li>
	 * </ul>
	 *
	 * @return a list of supported annotation classes
	 */
	@Override
	protected List<Class<? extends Annotation>> getSupportedAnnotationClasses() {
		return OrangeCollectionUtils.asList(Left.class,AddMembers.class,RedisValue.class);
	}
	
	/**
	 * Returns the context class that this executor requires for execution.
	 * 
	 * <p>This executor requires {@link OrangeRedisValueContext} which provides
	 * access to the value that will be pushed to the Redis list.
	 *
	 * @return the context class required by this executor
	 */
	@Override
	public Class<? extends OrangeRedisContext> getContextClass() {
		return OrangeRedisValueContext.class;
	}

}