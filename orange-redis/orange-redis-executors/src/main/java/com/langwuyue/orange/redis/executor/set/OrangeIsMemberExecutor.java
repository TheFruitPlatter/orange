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
package com.langwuyue.orange.redis.executor.set;

import java.lang.annotation.Annotation;
import java.util.List;
import java.util.Map;

import com.langwuyue.orange.redis.annotation.RedisValue;
import com.langwuyue.orange.redis.annotation.set.IsMembers;
import com.langwuyue.orange.redis.context.OrangeRedisContext;
import com.langwuyue.orange.redis.context.OrangeRedisValueContext;
import com.langwuyue.orange.redis.executor.OrangeRedisAbstractExecutor;
import com.langwuyue.orange.redis.mapping.OrangeRedisExecutorIdGenerator;
import com.langwuyue.orange.redis.operations.OrangeRedisSetOperations;
import com.langwuyue.orange.redis.utils.OrangeCollectionUtils;

/**
 * Executor implementation for checking if a value is a member of a Redis Set.
 * 
 * <p>This executor is responsible for determining whether a specific value exists
 * as a member in a Redis Set identified by the specified key. It supports the
 * {@link IsMembers} annotation which indicates that this operation should check
 * for set membership.
 * 
 * <p>The executor extends {@link OrangeRedisAbstractExecutor} and uses 
 * {@link OrangeRedisSetOperations} to perform the actual Redis operations.
 * 
 * <p>The result is returned as a Boolean value:
 * <ul>
 *   <li>true - if the value is a member of the set</li>
 *   <li>false - if the value is not a member of the set</li>
 * </ul>
 * 
 * <p>This implementation uses the Redis SISMEMBER command which has O(1) time complexity.
 *
 * @author Liang.Zhong
 * @since 1.0.0
 */
public class OrangeIsMemberExecutor extends OrangeRedisAbstractExecutor {
	
	private OrangeRedisSetOperations operations;

	/**
	 * Constructs a new OrangeIsMemberExecutor with the specified Redis operations and ID generator.
	 *
	 * @param operations the Redis Set operations implementation to use for membership checking
	 * @param idGenerator the generator for creating unique executor identifiers
	 */
	public OrangeIsMemberExecutor(OrangeRedisSetOperations operations, OrangeRedisExecutorIdGenerator idGenerator) {
		super(idGenerator);
		this.operations = operations;
	}

	/**
	 * Executes the membership check operation on the Redis Set.
	 * 
	 * <p>This method checks if the value in the context is a member of the Redis Set
	 * identified by the key in the provided context. It uses the Redis SISMEMBER command
	 * which has O(1) time complexity.
	 *
	 * @param context the Redis operation context containing the key, value, and other metadata
	 * @return a Boolean value indicating whether the value is a member of the set (true) or not (false)
	 * @throws Exception if an error occurs during the Redis operation
	 */
	@Override
	public Object execute(OrangeRedisContext context) throws Exception {
		OrangeRedisValueContext ctx = (OrangeRedisValueContext)context;
		Map<Object, Boolean> resultMap = this.operations.isMember(ctx.getRedisKey().getValue(), context.getValueType(), ctx.getValue());
		return resultMap.get(ctx.getValue());
	}

	/**
	 * Returns the list of annotation classes that this executor supports.
	 * 
	 * <p>This executor supports the following annotations:
	 * <ul>
	 *   <li>{@link IsMembers} - Indicates this is a membership check operation</li>
	 *   <li>{@link RedisValue} - Indicates the value to check for membership</li>
	 * </ul>
	 *
	 * @return a list containing the supported annotation classes
	 */
	@Override
	protected List<Class<? extends Annotation>> getSupportedAnnotationClasses() {
		return OrangeCollectionUtils.asList(IsMembers.class, RedisValue.class);
	}
	
	/**
	 * Returns the context class required by this executor.
	 * 
	 * <p>This executor requires an {@link OrangeRedisValueContext} which contains
	 * both the Redis key and the value to check for membership in the set.
	 *
	 * @return the OrangeRedisValueContext class
	 */
	@Override
	public Class<? extends OrangeRedisContext> getContextClass() {
		return OrangeRedisValueContext.class;
	}
}