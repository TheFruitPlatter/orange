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
import com.langwuyue.orange.redis.annotation.list.Left;
import com.langwuyue.orange.redis.context.OrangeRedisContext;
import com.langwuyue.orange.redis.context.OrangeRedisTimeoutContext;
import com.langwuyue.orange.redis.executor.OrangeRedisGetOneAbstractExecutor;
import com.langwuyue.orange.redis.mapping.OrangeRedisExecutorIdGenerator;
import com.langwuyue.orange.redis.operations.OrangeRedisListOperations;
import com.langwuyue.orange.redis.utils.OrangeCollectionUtils;

/**
 * Redis executor that handles left pop operations with timeout parameters.
 * 
 * <p>This executor removes and returns a single element from the left side (beginning) of a Redis list.
 * If the list is empty, the operation will block until either an element becomes available or the
 * specified timeout expires. The timeout is specified using the {@link Timeout} annotation.
 * 
 * <p>This executor supports the following annotations:
 * <ul>
 *   <li>{@link PopMembers} - indicates a pop operation for list elements</li>
 *   <li>{@link Timeout} - specifies the timeout duration and unit for the blocking operation</li>
 *   <li>{@link Left} - specifies that the operation should be performed from the left side</li>
 * </ul>
 *
 * <p>The executor uses {@link OrangeRedisTimeoutContext} to handle the timeout parameters.
 *
 * @author Liang.Zhong
 * @since 1.0.0
 * @see <a href="https://orange.langwuyue.com/redis/advanced/list">Orange Redis List Documentation</a>
 */
public class OrangeLeftPopMemberTimeoutExecutor extends OrangeRedisGetOneAbstractExecutor {
	
	/**
	 * Redis list operations instance used to perform the left pop operation with timeout.
	 * This field provides access to the underlying Redis commands for list manipulation.
	 */
	private OrangeRedisListOperations operations;

	/**
	 * Constructs a new OrangeLeftPopMemberTimeoutExecutor with the specified Redis list operations and ID generator.
	 *
	 * @param operations the Redis list operations instance to use for executing the left pop command
	 * @param idGenerator the generator used to create unique identifiers for this executor
	 */
	public OrangeLeftPopMemberTimeoutExecutor(OrangeRedisListOperations operations,OrangeRedisExecutorIdGenerator idGenerator) {
		super(idGenerator);
		this.operations = operations;
	}

	/**
	 * Returns the list of annotation classes that this executor supports.
	 * 
	 * <p>This executor supports the following annotations:
	 * <ul>
	 *   <li>{@link PopMembers} - indicates a pop operation for list elements</li>
	 *   <li>{@link Timeout} - specifies the timeout duration and unit for the blocking operation</li>
	 *   <li>{@link Left} - specifies that the operation should be performed from the left side</li>
	 * </ul>
	 *
	 * @return a list of supported annotation classes
	 */
	@Override
	protected List<Class<? extends Annotation>> getSupportedAnnotationClasses() {
		return OrangeCollectionUtils.asList(PopMembers.class,Timeout.class,Left.class);
	}
	
	/**
	 * Executes the left pop operation with timeout on the Redis list.
	 * 
	 * <p>This method removes and returns a single element from the left side (beginning) of the list
	 * stored at the specified key. If the list is empty, the operation will block until either an
	 * element becomes available or the specified timeout expires.
	 *
	 * @param context the Redis context containing the key and other operation parameters
	 * @param valueField the field that will receive the popped value, may be null
	 * @param returnArgumentType the expected return type for the operation
	 * @return a collection containing the popped element, or an empty collection if the timeout expires
	 * @throws Exception if an error occurs during the operation
	 */
	@Override
	protected Collection doGet(OrangeRedisContext context, Field valueField, Type returnArgumentType) throws Exception {
		OrangeRedisTimeoutContext ctx = (OrangeRedisTimeoutContext) context;
		Object value = this.operations.leftPop(
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
	 * <p>This executor uses {@link OrangeRedisTimeoutContext} to handle timeout parameters
	 * required for blocking operations. The timeout context extends the standard Redis context
	 * with additional fields for timeout duration and time unit.
	 *
	 * @return the OrangeRedisTimeoutContext class
	 */
	@Override
	public Class<? extends OrangeRedisContext> getContextClass() {
		return OrangeRedisTimeoutContext.class;
	}
	
	

}