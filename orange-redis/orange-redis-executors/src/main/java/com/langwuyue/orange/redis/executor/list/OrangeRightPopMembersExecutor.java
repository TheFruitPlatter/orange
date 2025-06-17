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
import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.List;

import com.langwuyue.orange.redis.annotation.Count;
import com.langwuyue.orange.redis.annotation.PopMembers;
import com.langwuyue.orange.redis.annotation.list.Right;
import com.langwuyue.orange.redis.context.OrangeRedisContext;
import com.langwuyue.orange.redis.context.OrangeRedisCountContext;
import com.langwuyue.orange.redis.executor.OrangeRedisGetAbstractExecutor;
import com.langwuyue.orange.redis.mapping.OrangeRedisExecutorIdGenerator;
import com.langwuyue.orange.redis.operations.OrangeRedisListOperations;
import com.langwuyue.orange.redis.utils.OrangeCollectionUtils;

/**
 * Executor implementation for popping multiple elements from the right side of a Redis list.
 * 
 * <p>This executor extends {@link OrangeRedisGetAbstractExecutor} to provide
 * functionality for removing and returning multiple elements from the end (right side)
 * of a Redis list. It supports the {@link PopMembers}, {@link Count}, and {@link Right} annotations
 * to indicate that elements should be popped from the right side of the list with a specified count.
 * 
 * <p>The executor removes and returns elements from the list in a LIFO
 * (Last-In-First-Out) manner, where the last elements added to the list will
 * be the first ones removed.
 *
 * @author Liang.Zhong
 * @since 1.0.0
 * @see <a href="https://orange.langwuyue.com/redis/advanced/list">Orange Redis List Documentation</a>
 */
public class OrangeRightPopMembersExecutor extends OrangeRedisGetAbstractExecutor {
	
	/**
	 * Redis list operations handler that provides the core functionality for list manipulation.
	 */
	private OrangeRedisListOperations operations;

	/**
	 * Constructs a new OrangeRightPopMembersExecutor.
	 *
	 * @param operations the Redis list operations handler for performing list-specific operations
	 * @param idGenerator generator for creating unique executor identifiers
	 */
	public OrangeRightPopMembersExecutor(OrangeRedisListOperations operations,OrangeRedisExecutorIdGenerator idGenerator) {
		super(idGenerator);
		this.operations = operations;
	}

	/**
	 * Returns the list of annotation classes supported by this executor.
	 * 
	 * <p>This executor supports three annotations:
	 * <ul>
	 *   <li>{@link PopMembers} - indicates a pop operation for multiple elements</li>
	 *   <li>{@link Count} - specifies the number of elements to pop</li>
	 *   <li>{@link Right} - specifies that the operation should be performed from the right side</li>
	 * </ul>
	 *
	 * @return a list containing the PopMembers, Count, and Right annotation classes
	 */
	@Override
	protected List<Class<? extends Annotation>> getSupportedAnnotationClasses() {
		return OrangeCollectionUtils.asList(PopMembers.class,Count.class,Right.class);
	}
	
	/**
	 * Returns the context class used by this executor.
	 * 
	 * <p>This executor uses {@link OrangeRedisCountContext} class as it requires
	 * access to the count parameter that specifies how many elements to pop from the list.
	 *
	 * @return the OrangeRedisCountContext class
	 */
	@Override
	public Class<? extends OrangeRedisContext> getContextClass() {
		return OrangeRedisCountContext.class;
	}

	/**
	 * Performs the right pop operation on a Redis list for multiple elements.
	 * 
	 * <p>This method removes and returns multiple elements from the right side (end) of the Redis list
	 * identified by the key in the provided context. The number of elements to pop is specified
	 * by the count parameter in the {@link OrangeRedisCountContext}.
	 *
	 * @param context the Redis operation context containing the key and other operation parameters
	 * @param valueField the field to which the popped values will be assigned, may be null
	 * @param returnArgumentType the expected return type for the popped values
	 * @return a collection containing the popped elements (or empty if the list was empty)
	 * @throws Exception if an error occurs during the Redis operation
	 */
	@Override
	protected Collection doGet(OrangeRedisContext context, Field valueField, Type returnArgumentType) throws Exception {
		OrangeRedisCountContext ctx = (OrangeRedisCountContext) context;
		return this.operations.rightPop(
				context.getRedisKey().getValue(), 
				ctx.getCount(), 
				context.getValueType(),
				valueField == null ? returnArgumentType : valueField.getGenericType()
		);
	}

}