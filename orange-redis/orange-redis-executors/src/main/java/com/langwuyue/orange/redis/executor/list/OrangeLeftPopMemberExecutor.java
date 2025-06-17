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

import com.langwuyue.orange.redis.annotation.PopMembers;
import com.langwuyue.orange.redis.annotation.list.Left;
import com.langwuyue.orange.redis.context.OrangeRedisContext;
import com.langwuyue.orange.redis.executor.OrangeRedisGetOneAbstractExecutor;
import com.langwuyue.orange.redis.mapping.OrangeRedisExecutorIdGenerator;
import com.langwuyue.orange.redis.operations.OrangeRedisListOperations;
import com.langwuyue.orange.redis.utils.OrangeCollectionUtils;

/**
 * Executor implementation for popping elements from the left side of a Redis list.
 * 
 * <p>This executor extends {@link OrangeRedisGetOneAbstractExecutor} to provide
 * functionality for removing and returning elements from the beginning (left side)
 * of a Redis list. It supports the {@link PopMembers} and {@link Left} annotations
 * to indicate that elements should be popped from the left side of the list.
 * 
 * <p>The executor removes and returns a single element from the list in a FIFO
 * (First-In-First-Out) manner, where the first element added to the list will
 * be the first one removed.
 *
 * @author Liang.Zhong
 * @since 1.0.0
 * @see <a href="https://orange.langwuyue.com/redis/advanced/list">Orange Redis List Documentation</a>
 */
public class OrangeLeftPopMemberExecutor extends OrangeRedisGetOneAbstractExecutor {
	
	/**
	 * Redis list operations handler that provides the core functionality for list manipulation.
	 */
	private OrangeRedisListOperations operations;

	/**
	 * Constructs a new OrangeLeftPopMemberExecutor.
	 *
	 * @param operations the Redis list operations handler for performing list-specific operations
	 * @param idGenerator generator for creating unique executor identifiers
	 */
	public OrangeLeftPopMemberExecutor(OrangeRedisListOperations operations,OrangeRedisExecutorIdGenerator idGenerator) {
		super(idGenerator);
		this.operations = operations;
	}

	/**
	 * Returns the list of annotation classes supported by this executor.
	 * 
	 * <p>This executor supports the {@link PopMembers} annotation for popping elements
	 * from a list and the {@link Left} annotation for specifying that the operation
	 * should be performed from the left side (beginning) of the list.
	 *
	 * @return a list containing the PopMembers and Left annotation classes
	 */
	@Override
	protected List<Class<? extends Annotation>> getSupportedAnnotationClasses() {
		return OrangeCollectionUtils.asList(PopMembers.class,Left.class);
	}
	
	/**
	 * Performs the left pop operation on a Redis list.
	 * 
	 * <p>This method removes and returns the first element (leftmost) from the Redis list
	 * identified by the key in the provided context. It uses the {@link OrangeRedisListOperations#leftPop}
	 * method to perform the actual Redis operation.
	 *
	 * @param context the Redis operation context containing the key and other operation parameters
	 * @param valueField the field to which the popped value will be assigned, may be null
	 * @param returnArgumentType the expected return type for the popped value
	 * @return a collection containing the popped element (or empty if the list was empty)
	 * @throws Exception if an error occurs during the Redis operation
	 */
	@Override
	protected Collection doGet(OrangeRedisContext context, Field valueField, Type returnArgumentType) throws Exception {
		return this.operations.leftPop(
				context.getRedisKey().getValue(), 
				1, 
				context.getValueType(),
				valueField == null ? returnArgumentType : valueField.getGenericType()
		);
	}

}