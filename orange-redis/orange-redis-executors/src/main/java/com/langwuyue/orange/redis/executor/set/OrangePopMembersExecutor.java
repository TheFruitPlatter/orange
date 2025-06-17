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
import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.List;

import com.langwuyue.orange.redis.annotation.Count;
import com.langwuyue.orange.redis.annotation.PopMembers;
import com.langwuyue.orange.redis.context.OrangeRedisContext;
import com.langwuyue.orange.redis.context.OrangeRedisCountContext;
import com.langwuyue.orange.redis.executor.OrangeRedisGetAbstractExecutor;
import com.langwuyue.orange.redis.mapping.OrangeRedisExecutorIdGenerator;
import com.langwuyue.orange.redis.operations.OrangeRedisSetOperations;
import com.langwuyue.orange.redis.utils.OrangeCollectionUtils;

/**
 * Executor implementation for popping (removing and returning) random members from a Redis Set.
 * 
 * <p>This executor implements the Redis SPOP command functionality, which atomically removes
 * and returns one or more random members from a Redis Set. This operation is useful for:
 * <ul>
 *   <li>Implementing random selection with removal (e.g., lottery systems, random assignments)</li>
 *   <li>Processing set elements in a non-deterministic order</li>
 *   <li>Consuming items from a pool without replacement</li>
 *   <li>Implementing work queues where any task can be processed next</li>
 * </ul>
 * 
 * <p>The SPOP operation modifies the set by removing the selected members and returns
 * these members to the caller. If the set is empty, the operation returns an empty collection.
 * 
 * <p>This executor supports specifying the number of members to pop via the {@link Count}
 * annotation. If the requested count exceeds the number of members in the set, all remaining
 * members will be popped.
 * 
 * <p>Time complexity: O(1) for a single member pop, O(N) for popping multiple members where
 * N is the number of members to be popped.
 * 
 * @author Liang.Zhong
 * @since 1.0.0
 * @see com.langwuyue.orange.redis.annotation.PopMembers
 * @see com.langwuyue.orange.redis.annotation.Count
 * @see <a href="https://redis.io/commands/spop">Redis SPOP Command Reference</a>
 */
public class OrangePopMembersExecutor extends OrangeRedisGetAbstractExecutor {
	
	private OrangeRedisSetOperations operations;

	/**
	 * Constructs a new OrangePopMembersExecutor with the specified Redis operations and ID generator.
	 *
	 * @param operations the Redis Set operations implementation to use for popping members
	 * @param idGenerator the generator for creating unique executor identifiers
	 */
	public OrangePopMembersExecutor(OrangeRedisSetOperations operations, OrangeRedisExecutorIdGenerator idGenerator) {
		super(idGenerator);
		this.operations = operations;
	}

	/**
	 * Returns the list of annotation classes that this executor supports.
	 * 
	 * <p>This executor supports the following annotations:
	 * <ul>
	 *   <li>{@link PopMembers} - Indicates this is a member pop operation</li>
	 *   <li>{@link Count} - Specifies the number of members to pop</li>
	 * </ul>
	 *
	 * @return a list containing the supported annotation classes
	 */
	@Override
	protected List<Class<? extends Annotation>> getSupportedAnnotationClasses() {
		return OrangeCollectionUtils.asList(PopMembers.class, Count.class);
	}

	/**
	 * Performs the actual popping of members from the Redis Set.
	 * 
	 * <p>This method uses the Redis SPOP command to remove and return random members
	 * from the set. The number of members to pop is specified in the context's
	 * count parameter. This operation both removes the members from the set and
	 * returns them to the caller.
	 *
	 * @param context the Redis operation context containing the key and count
	 * @param valueField the field annotated with PopMembers (may be null if the annotation is on a method)
	 * @param returnArgumentType the expected return type for the operation
	 * @return a Collection containing the popped members
	 * @throws Exception if an error occurs during the Redis operation
	 */
	@Override
	protected Collection doGet(OrangeRedisContext context, Field valueField, Type returnArgumentType) throws Exception {
		OrangeRedisCountContext ctx = (OrangeRedisCountContext) context;
		return this.operations.popMember(
			context.getRedisKey().getValue(), 
			ctx.getCount(),
			context.getValueType(), 
			valueField == null ? returnArgumentType : valueField.getGenericType()
		);
	}
	
	/**
	 * Returns the context class required by this executor.
	 * 
	 * <p>This executor requires an {@link OrangeRedisCountContext} which contains
	 * both the Redis key and the count of members to pop.
	 *
	 * @return the OrangeRedisCountContext class
	 */
	@Override
	public Class<? extends OrangeRedisContext> getContextClass() {
		return OrangeRedisCountContext.class;
	}
}