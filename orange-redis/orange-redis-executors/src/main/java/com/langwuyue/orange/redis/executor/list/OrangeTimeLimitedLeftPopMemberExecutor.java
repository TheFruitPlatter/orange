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
import com.langwuyue.orange.redis.annotation.list.Left;
import com.langwuyue.orange.redis.context.OrangeRedisContext;
import com.langwuyue.orange.redis.context.OrangeRedisTimeoutValueTimeoutUnitContext;
import com.langwuyue.orange.redis.executor.OrangeRedisGetOneAbstractExecutor;
import com.langwuyue.orange.redis.mapping.OrangeRedisExecutorIdGenerator;
import com.langwuyue.orange.redis.operations.OrangeRedisListOperations;
import com.langwuyue.orange.redis.utils.OrangeCollectionUtils;

/**
 * Executor implementation for popping a single element from the left side of a Redis list with a time limit.
 * 
 * <p>This executor provides functionality to pop (retrieve and remove) a single element from the left side
 * of a Redis list. If the list is empty, the operation will block until either:
 * <ul>
 *   <li>An element becomes available in the list</li>
 *   <li>The specified timeout period elapses</li>
 * </ul>
 * 
 * <p>This blocking behavior makes this executor useful for implementing simple message queues
 * or work distribution systems where consumers need to wait for new items to be added to the list.
 * 
 * <p>The executor requires the following annotations to be present:
 * <ul>
 *   <li>{@link PopMembers} - Indicates a pop operation</li>
 *   <li>{@link TimeoutValue} - Specifies the timeout value</li>
 *   <li>{@link TimeoutUnit} - Specifies the unit for the timeout value</li>
 *   <li>{@link Left} - Indicates that the pop operation should be performed from the left side of the list</li>
 * </ul>
 * 
 * <p>If the timeout expires before an element becomes available, the executor will return null.
 * 
 * @author Liang.Zhong
 * @since 1.0.0
 * @see OrangeTimeLimitedRightPopMemberExecutor for popping from the right side of a list
 * @see <a href="https://orange.langwuyue.com/redis/advanced/list">Orange Redis List Documentation</a>
 */
public class OrangeTimeLimitedLeftPopMemberExecutor extends OrangeRedisGetOneAbstractExecutor {
	
	/**
	 * Redis list operations handler that provides the core functionality for interacting with Redis lists.
	 * This field is used to execute the blocking left pop operation on the Redis server.
	 */
	private OrangeRedisListOperations operations;

	/**
	 * Constructs a new OrangeTimeLimitedLeftPopMemberExecutor with the specified operations and ID generator.
	 *
	 * @param operations the Redis list operations handler that will execute the blocking left pop command
	 * @param idGenerator the generator used to create unique identifiers for this executor
	 * @throws IllegalArgumentException if either operations or idGenerator is null
	 */
	public OrangeTimeLimitedLeftPopMemberExecutor(OrangeRedisListOperations operations,OrangeRedisExecutorIdGenerator idGenerator) {
		super(idGenerator);
		this.operations = operations;
	}

	/**
	 * Returns the list of annotation classes that this executor supports.
	 * 
	 * <p>This executor supports the following annotations:
	 * <ul>
	 *   <li>{@link PopMembers} - Indicates a pop operation from a Redis list</li>
	 *   <li>{@link TimeoutValue} - Specifies the timeout value for the blocking operation</li>
	 *   <li>{@link TimeoutUnit} - Specifies the time unit for the timeout value</li>
	 *   <li>{@link Left} - Indicates that the pop operation should be performed from the left side</li>
	 * </ul>
	 *
	 * @return a list containing the supported annotation classes
	 */
	@Override
	protected List<Class<? extends Annotation>> getSupportedAnnotationClasses() {
		return OrangeCollectionUtils.asList(PopMembers.class,TimeoutValue.class,TimeoutUnit.class,Left.class);
	}
	
	/**
	 * Performs the actual blocking left pop operation on the Redis list.
	 * 
	 *
	 * @param context the Redis operation context containing key and timeout information
	 * @param valueField the field that will store the popped value, may be null
	 * @param returnArgumentType the expected return type for proper type conversion
	 * @return a collection containing the popped value, or an empty collection if the timeout expires
	 * @throws Exception if an error occurs during the operation
	 */
	@Override
	protected Collection doGet(OrangeRedisContext context, Field valueField, Type returnArgumentType) throws Exception {
		OrangeRedisTimeoutValueTimeoutUnitContext ctx = (OrangeRedisTimeoutValueTimeoutUnitContext) context;
		Object value = this.operations.leftPop(
				context.getRedisKey().getValue(), 
				ctx.getValue(),
				ctx.getUnit(),
				context.getValueType(),
				valueField == null ? returnArgumentType : valueField.getGenericType()
		);
		
		return OrangeCollectionUtils.asList(value);
	}

	/**
	 * Returns the context class required for this executor.
	 * 
	 * <p>This executor uses the {@link OrangeRedisTimeoutValueTimeoutUnitContext} class which extends
	 * the base {@link OrangeRedisContext} to include additional information needed for
	 * the blocking pop operation, specifically:
	 * <ul>
	 *   <li>The timeout value</li>
	 *   <li>The time unit for the timeout</li>
	 * </ul>
	 *
	 * @return the {@link OrangeRedisTimeoutValueTimeoutUnitContext} class
	 */
	@Override
	public Class<? extends OrangeRedisContext> getContextClass() {
		return OrangeRedisTimeoutValueTimeoutUnitContext.class;
	}
}