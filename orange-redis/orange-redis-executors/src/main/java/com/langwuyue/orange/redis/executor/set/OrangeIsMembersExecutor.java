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

import com.langwuyue.orange.redis.annotation.Multiple;
import com.langwuyue.orange.redis.annotation.set.IsMembers;
import com.langwuyue.orange.redis.context.OrangeRedisContext;
import com.langwuyue.orange.redis.context.OrangeRedisMultipleValueContext;
import com.langwuyue.orange.redis.executor.OrangeRedisAbstractExecutor;
import com.langwuyue.orange.redis.mapping.OrangeRedisExecutorIdGenerator;
import com.langwuyue.orange.redis.operations.OrangeRedisSetOperations;
import com.langwuyue.orange.redis.utils.OrangeCollectionUtils;

/**
 * Executor implementation for checking if multiple values are members of a Redis Set.
 * 
 * <p>This executor is responsible for determining whether multiple specified values exist
 * as members in a Redis Set identified by the specified key. It supports the following annotations:
 * <ul>
 *   <li>{@link IsMembers} - Indicates this is a membership check operation</li>
 *   <li>{@link Multiple} - Indicates that multiple values should be checked</li>
 * </ul>
 * 
 * <p>The executor extends {@link OrangeRedisAbstractExecutor} and uses 
 * {@link OrangeRedisSetOperations} to perform the actual Redis operations.
 * 
 * <p>Unlike {@link OrangeIsMemberExecutor} which checks a single value, this executor
 * processes multiple values and returns a list of Boolean results corresponding to each value:
 * <ul>
 *   <li>true - if the value is a member of the set</li>
 *   <li>false - if the value is not a member of the set</li>
 * </ul>
 * 
 * <p>This implementation uses the Redis SMISMEMBER command (if available) or multiple SISMEMBER
 * commands to efficiently check multiple members.
 *
 * @author Liang.Zhong
 * @since 1.0.0
 * @see <a href="https://orange.langwuyue.com/redis/advanced/set">Orange Redis Set Documentation</a>
 */
public class OrangeIsMembersExecutor extends OrangeRedisAbstractExecutor {
	
	private OrangeRedisSetOperations operations;

	/**
	 * Constructs a new OrangeIsMembersExecutor with the specified Redis operations and ID generator.
	 *
	 * @param operations the Redis Set operations implementation to use for multiple membership checking
	 * @param idGenerator the generator for creating unique executor identifiers
	 */
	public OrangeIsMembersExecutor(OrangeRedisSetOperations operations, OrangeRedisExecutorIdGenerator idGenerator) {
		super(idGenerator);
		this.operations = operations;
	}

	/**
	 * Executes the multiple membership check operation on the Redis Set.
	 * 
	 * <p>This method checks if multiple values in the context are members of the Redis Set
	 * identified by the key in the provided context. It uses the Redis SMISMEMBER command
	 * (if available) or multiple SISMEMBER commands for efficient batch checking.
	 *
	 * @param context the Redis operation context containing the key, values, and other metadata
	 * @return a Map where keys are the values being checked and values are Boolean results
	 *         indicating whether each value is a member of the set (true) or not (false)
	 * @throws Exception if an error occurs during the Redis operation
	 */
	@Override
	public Object execute(OrangeRedisContext context) throws Exception {
		OrangeRedisMultipleValueContext ctx = (OrangeRedisMultipleValueContext)context;
		return this.operations.isMember(ctx.getRedisKey().getValue(), context.getValueType(), ctx.toArray());
	}

	/**
	 * Returns the list of annotation classes that this executor supports.
	 * 
	 * <p>This executor supports the following annotations:
	 * <ul>
	 *   <li>{@link IsMembers} - Indicates this is a membership check operation</li>
	 *   <li>{@link Multiple} - Indicates that multiple values should be checked</li>
	 * </ul>
	 *
	 * @return a list containing the supported annotation classes
	 */
	@Override
	protected List<Class<? extends Annotation>> getSupportedAnnotationClasses() {
		return OrangeCollectionUtils.asList(IsMembers.class, Multiple.class);
	}
	
	/**
	 * Returns the context class required by this executor.
	 * 
	 * <p>This executor requires an {@link OrangeRedisMultipleValueContext} which contains
	 * both the Redis key and the multiple values to check for membership in the set.
	 *
	 * @return the OrangeRedisMultipleValueContext class
	 */
	@Override
	public Class<? extends OrangeRedisContext> getContextClass() {
		return OrangeRedisMultipleValueContext.class;
	}
}