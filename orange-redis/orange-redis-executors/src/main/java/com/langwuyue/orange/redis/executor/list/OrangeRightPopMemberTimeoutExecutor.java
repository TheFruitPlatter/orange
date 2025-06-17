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
import com.langwuyue.orange.redis.annotation.Timeout;
import com.langwuyue.orange.redis.annotation.list.Right;
import com.langwuyue.orange.redis.context.OrangeRedisContext;
import com.langwuyue.orange.redis.context.OrangeRedisTimeoutContext;
import com.langwuyue.orange.redis.executor.OrangeRedisGetOneAbstractExecutor;
import com.langwuyue.orange.redis.mapping.OrangeRedisExecutorIdGenerator;
import com.langwuyue.orange.redis.operations.OrangeRedisListOperations;
import com.langwuyue.orange.redis.utils.OrangeCollectionUtils;

/**
 * Executor implementation for popping a single element from the right side of a Redis list with timeout.
 * 
 * <p>This executor extends {@link OrangeRedisGetOneAbstractExecutor} to provide
 * functionality for removing and returning a single element from the end (right side)
 * of a Redis list with a specified timeout. It supports the {@link PopMembers}, {@link Timeout}, 
 * and {@link Right} annotations to indicate that an element should be popped from the right side 
 * of the list with a timeout period.
 * 
 * <p>The executor will wait up to the specified timeout duration for an element to become
 * available in the list. If the list is empty, it will block until either an element becomes
 * available or the timeout expires. If the timeout expires before an element becomes available,
 * the operation returns null.
 *
 * @author Liang.Zhong
 * @since 1.0.0
 * @see <a href="https://orange.langwuyue.com/redis/advanced/list">Orange Redis List Documentation</a>
 */
public class OrangeRightPopMemberTimeoutExecutor extends OrangeRedisGetOneAbstractExecutor {
	
	/**
	 * Redis list operations handler that provides the core functionality for list manipulation.
	 */
	private OrangeRedisListOperations operations;

	/**
	 * Constructs a new OrangeRightPopMemberTimeoutExecutor.
	 *
	 * @param operations the Redis list operations handler for performing list-specific operations
	 * @param idGenerator generator for creating unique executor identifiers
	 */
	public OrangeRightPopMemberTimeoutExecutor(OrangeRedisListOperations operations,OrangeRedisExecutorIdGenerator idGenerator) {
		super(idGenerator);
		this.operations = operations;
	}

	/**
	 * Returns the list of annotation classes supported by this executor.
	 * 
	 * <p>This executor supports three annotations:
	 * <ul>
	 *   <li>{@link PopMembers} - indicates a pop operation for multiple elements</li>
	 *   <li>{@link Timeout} - specifies the timeout duration for the blocking operation</li>
	 *   <li>{@link Right} - specifies that the operation should be performed from the right side</li>
	 * </ul>
	 *
	 * @return a list containing the PopMembers, Timeout, and Right annotation classes
	 */
	@Override
	protected List<Class<? extends Annotation>> getSupportedAnnotationClasses() {
		return OrangeCollectionUtils.asList(PopMembers.class,Timeout.class,Right.class);
	}
	
	/**
	 * Performs the right pop operation on a Redis list with timeout.
	 * 
	 * <p>This method removes and returns a single element from the right side (end) of the Redis list
	 * identified by the key in the provided context. If the list is empty, the method will block
	 * until either an element becomes available or the specified timeout expires.
	 * 
	 * <p>The returned element is wrapped in a collection to maintain consistency with the collection
	 * return type, even though only a single element is popped.
	 *
	 * @param context the Redis operation context containing the key and other operation parameters
	 * @param valueField the field to which the popped value will be assigned, may be null
	 * @param returnArgumentType the expected return type for the popped value
	 * @return a collection containing the popped element, or an empty collection if the timeout expires
	 * @throws Exception if an error occurs during the Redis operation
	 */
	@Override
	protected Collection doGet(OrangeRedisContext context, Field valueField, Type returnArgumentType) throws Exception {
		OrangeRedisTimeoutContext ctx = (OrangeRedisTimeoutContext) context;
		Object value = this.operations.rightPop(
				context.getRedisKey().getValue(), 
				ctx.getTimeout(),
				ctx.getTimeoutUnit(),
				context.getValueType(),
				valueField == null ? returnArgumentType : valueField.getGenericType()
		);
		
		return OrangeCollectionUtils.asList(value);
	}

	/**
	 * Returns the context class used by this executor.
	 * 
	 * <p>This executor uses {@link OrangeRedisTimeoutContext} class as it requires
	 * access to the timeout parameters that specify how long to block waiting for an element.
	 * The timeout context provides methods to get both the timeout duration and the timeout
	 * time unit.
	 *
	 * @return the OrangeRedisTimeoutContext class
	 */
	@Override
	public Class<? extends OrangeRedisContext> getContextClass() {
		return OrangeRedisTimeoutContext.class;
	}
	
	

}