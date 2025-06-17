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
import com.langwuyue.orange.redis.annotation.TimeoutUnit;
import com.langwuyue.orange.redis.annotation.TimeoutValue;
import com.langwuyue.orange.redis.annotation.list.Right;
import com.langwuyue.orange.redis.context.OrangeRedisContext;
import com.langwuyue.orange.redis.context.OrangeRedisTimeoutValueTimeoutUnitContext;
import com.langwuyue.orange.redis.executor.OrangeRedisGetOneAbstractExecutor;
import com.langwuyue.orange.redis.mapping.OrangeRedisExecutorIdGenerator;
import com.langwuyue.orange.redis.operations.OrangeRedisListOperations;
import com.langwuyue.orange.redis.utils.OrangeCollectionUtils;

/**
 * Executor implementation for popping a single element from the right side of a Redis list with explicit timeout parameters.
 * 
 * <p>This executor extends {@link OrangeRedisGetOneAbstractExecutor} to provide
 * functionality for removing and returning a single element from the end (right side)
 * of a Redis list with explicitly specified timeout value and timeout unit. It supports 
 * the {@link PopMembers}, {@link TimeoutValue}, {@link TimeoutUnit}, and {@link Right} 
 * annotations to indicate that an element should be popped from the right side of the list 
 * with the specified timeout parameters.
 * 
 * <p>Unlike {@link OrangeRightPopMemberTimeoutExecutor} which uses a combined {@link Timeout} annotation,
 * this executor uses separate annotations for timeout value and unit, allowing for more flexible
 * timeout specification, particularly when the timeout parameters need to be determined dynamically.
 *
 * @author Liang.Zhong
 * @since 1.0.0
 * @see <a href="https://orange.langwuyue.com/redis/advanced/list">Orange Redis List Documentation</a>
 */
public class OrangeTimeLimitedRightPopMemberExecutor extends OrangeRedisGetOneAbstractExecutor {
	
	/**
	 * Redis list operations handler that provides the core functionality for list manipulation.
	 * This field handles the actual Redis list operations with timeout parameters.
	 */
	private OrangeRedisListOperations operations;

	/**
	 * Constructs a new OrangeTimeLimitedRightPopMemberExecutor.
	 *
	 * @param operations the Redis list operations handler for performing list-specific operations
	 * @param idGenerator generator for creating unique executor identifiers
	 */
	public OrangeTimeLimitedRightPopMemberExecutor(OrangeRedisListOperations operations,OrangeRedisExecutorIdGenerator idGenerator) {
		super(idGenerator);
		this.operations = operations;
	}

	/**
	 * Returns the list of annotation classes supported by this executor.
	 * 
	 * <p>This executor supports four annotations:
	 * <ul>
	 *   <li>{@link PopMembers} - indicates a pop operation for multiple elements</li>
	 *   <li>{@link TimeoutValue} - specifies the timeout value for the blocking operation</li>
	 *   <li>{@link TimeoutUnit} - specifies the time unit for the timeout value</li>
	 *   <li>{@link Right} - specifies that the operation should be performed from the right side</li>
	 * </ul>
	 *
	 * @return a list containing the PopMembers, TimeoutValue, TimeoutUnit, and Right annotation classes
	 */
	@Override
	protected List<Class<? extends Annotation>> getSupportedAnnotationClasses() {
		return OrangeCollectionUtils.asList(PopMembers.class,TimeoutValue.class,TimeoutUnit.class,Right.class);
	}
	
	/**
	 * Performs the right pop operation on a Redis list with explicit timeout parameters.
	 * 
	 * <p>This method removes and returns a single element from the right side (end) of the Redis list
	 * identified by the key in the provided context. If the list is empty, the method will block
	 * until either an element becomes available or the specified timeout expires.
	 * 
	 * <p>The timeout parameters are obtained from the {@link OrangeRedisTimeoutValueTimeoutUnitContext},
	 * which provides separate methods for accessing the timeout value and unit. This allows for more
	 * flexible timeout specification compared to using a combined timeout annotation.
	 * 
	 * <p>The returned element is wrapped in a collection to maintain consistency with the collection
	 * return type, even though only a single element is popped.
	 *
	 * @param context the Redis operation context containing the key and timeout parameters
	 * @param valueField the field to which the popped value will be assigned, may be null
	 * @param returnArgumentType the expected return type for the popped value
	 * @return a collection containing the popped element, or an empty collection if the timeout expires
	 * @throws Exception if an error occurs during the Redis operation
	 */
	@Override
	protected Collection doGet(OrangeRedisContext context, Field valueField, Type returnArgumentType) throws Exception {
		OrangeRedisTimeoutValueTimeoutUnitContext ctx = (OrangeRedisTimeoutValueTimeoutUnitContext) context;
		Object value = this.operations.rightPop(
				context.getRedisKey().getValue(), 
				ctx.getValue(),
				ctx.getUnit(),
				context.getValueType(),
				valueField == null ? returnArgumentType : valueField.getGenericType()
		);
		
		return OrangeCollectionUtils.asList(value);
	}

	/**
	 * Returns the context class used by this executor.
	 * 
	 * <p>This executor uses {@link OrangeRedisTimeoutValueTimeoutUnitContext} class as it requires
	 * access to separate timeout value and timeout unit parameters. This context type provides
	 * methods to get both the timeout value ({@code getValue()}) and the timeout time unit 
	 * ({@code getUnit()}), allowing for more flexible timeout specification.
	 *
	 * @return the OrangeRedisTimeoutValueTimeoutUnitContext class
	 */
	@Override
	public Class<? extends OrangeRedisContext> getContextClass() {
		return OrangeRedisTimeoutValueTimeoutUnitContext.class;
	}
}