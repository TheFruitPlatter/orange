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
package com.langwuyue.orange.redis.executor.zset;

import java.lang.annotation.Annotation;
import java.util.List;

import com.langwuyue.orange.redis.annotation.RedisValue;
import com.langwuyue.orange.redis.annotation.RemoveMembers;
import com.langwuyue.orange.redis.context.OrangeRedisContext;
import com.langwuyue.orange.redis.context.OrangeRedisValueContext;
import com.langwuyue.orange.redis.executor.OrangeRedisAbstractExecutor;
import com.langwuyue.orange.redis.mapping.OrangeRedisExecutorIdGenerator;
import com.langwuyue.orange.redis.operations.OrangeRedisZSetOperations;
import com.langwuyue.orange.redis.utils.OrangeCollectionUtils;
/**
 * Executor for removing a single member from a Redis Sorted Set.
 * 
 * <p>This executor provides functionality to remove a specified member from
 * a Redis Sorted Set. It can return either the number of removed members
 * or a boolean indicating whether the removal was successful, depending
 * on the method return type.
 *
 * <p>The executor supports the following annotations:
 * <ul>
 *   <li>{@link RemoveMembers} - Indicates this is a member removal operation</li>
 *   <li>{@link RedisValue} - Specifies the member value to be removed</li>
 * </ul>
 *
 * <p>The executor uses {@link OrangeRedisZSetOperations} to perform the actual
 * Redis operation and supports flexible return types (Long or boolean).
 *
 * @author Liang.Zhong
 * @since 1.0.0
 * @see OrangeRedisZSetOperations
 * @see <a href="https://orange.langwuyue.com/redis/advanced/zset">Orange Redis ZSet Documentation</a>
 */
public class OrangeRemoveMemberExecutor extends OrangeRedisAbstractExecutor {
	
	private OrangeRedisZSetOperations operations;

	public OrangeRemoveMemberExecutor(OrangeRedisZSetOperations operations,OrangeRedisExecutorIdGenerator idGenerator) {
		super(idGenerator);
		this.operations = operations;
	}

	/**
	 * Executes the removal of a member from a Redis Sorted Set.
	 *
	 * <p>This method performs the actual Redis operation to remove a specified member
	 * from a sorted set. It supports flexible return types:
	 * <ul>
	 *   <li>Long - returns the number of members actually removed (0 or 1)</li>
	 *   <li>boolean - returns true if the member was removed, false otherwise</li>
	 * </ul>
	 *
	 * @param context the execution context containing:
	 *                - Redis key information
	 *                - Member value to remove
	 *                - Value type information
	 *                - Operation method metadata
	 * @return either:
	 *         - Long: number of removed members (0 or 1)
	 *         - boolean: true if member was removed, false otherwise
	 * @throws Exception if:
	 *                   - context is not of type OrangeRedisValueContext
	 *                   - Redis operation fails
	 *                   - any other execution error occurs
	 */
	@Override
	public Object execute(OrangeRedisContext context) throws Exception {
		OrangeRedisValueContext ctx = (OrangeRedisValueContext)context;
		Long result = this.operations.remove(context.getRedisKey().getValue(), context.getValueType(), ctx.getValue());
		Class<?> returnClass = context.getOperationMethod().getReturnType();
		if(returnClass == Boolean.class || returnClass == boolean.class) {
			return result != null && result > 0;	
		}
		return result;
	}

	/**
	 * Gets the list of annotation classes supported by this executor.
	 *
	 * <p>This executor supports the following annotations:
	 * <ul>
	 *   <li>{@link RemoveMembers} - Marks this as a member removal operation</li>
	 *   <li>{@link RedisValue} - Specifies the member value to be removed</li>
	 * </ul>
	 *
	 * @return immutable list of supported annotation classes
	 */
	@Override
	protected List<Class<? extends Annotation>> getSupportedAnnotationClasses() {
		return OrangeCollectionUtils.asList(RemoveMembers.class,RedisValue.class);
	}

	/**
	 * Gets the expected context class for this executor.
	 *
	 * <p>This executor requires an {@link OrangeRedisValueContext} which provides:
	 * <ul>
	 *   <li>Redis key information</li>
	 *   <li>Member value to remove</li>
	 *   <li>Value type information</li>
	 * </ul>
	 *
	 * @return the OrangeRedisValueContext class
	 */
	@Override
	public Class<? extends OrangeRedisContext> getContextClass() {
		return OrangeRedisValueContext.class;
	}
}