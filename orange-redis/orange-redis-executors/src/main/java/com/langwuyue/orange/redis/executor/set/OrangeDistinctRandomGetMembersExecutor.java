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
import com.langwuyue.orange.redis.annotation.Distinct;
import com.langwuyue.orange.redis.annotation.GetMembers;
import com.langwuyue.orange.redis.annotation.Random;
import com.langwuyue.orange.redis.context.OrangeRedisContext;
import com.langwuyue.orange.redis.context.OrangeRedisCountContext;
import com.langwuyue.orange.redis.executor.OrangeRedisGetAbstractExecutor;
import com.langwuyue.orange.redis.mapping.OrangeRedisExecutorIdGenerator;
import com.langwuyue.orange.redis.operations.OrangeRedisSetOperations;
import com.langwuyue.orange.redis.utils.OrangeCollectionUtils;

/**
 * Executor implementation for retrieving distinct random members from a Redis Set.
 * 
 * <p>This executor is designed to randomly select a specified number of unique members
 * from a Redis Set. It ensures that all returned members are distinct (no duplicates).
 * 
 * <p>The executor supports the following annotations:
 * <ul>
 *   <li>{@link GetMembers} - Indicates this is a member retrieval operation</li>
 *   <li>{@link Random} - Specifies that members should be selected randomly</li>
 *   <li>{@link Count} - Specifies the number of members to retrieve</li>
 *   <li>{@link Distinct} - Ensures all returned members are unique</li>
 * </ul>
 * 
 * <p>This implementation uses {@link OrangeRedisSetOperations} to perform the actual
 * Redis operations and requires an {@link OrangeRedisCountContext} to specify the
 * number of members to retrieve.
 *
 * @author Liang.Zhong
 * @since 1.0.0
 */
public class OrangeDistinctRandomGetMembersExecutor extends OrangeRedisGetAbstractExecutor {
	
	private OrangeRedisSetOperations operations;

	/**
	 * Constructs a new OrangeDistinctRandomGetMembersExecutor with the specified Redis operations and ID generator.
	 *
	 * @param operations the Redis Set operations implementation to use for distinct random member retrieval
	 * @param idGenerator the generator for creating unique executor identifiers
	 */
	public OrangeDistinctRandomGetMembersExecutor(OrangeRedisSetOperations operations, OrangeRedisExecutorIdGenerator idGenerator) {
		super(idGenerator);
		this.operations = operations;
	}

	/**
	 * Returns the list of annotation classes that this executor supports.
	 * 
	 * <p>This executor supports the following annotations:
	 * <ul>
	 *   <li>{@link GetMembers} - Indicates this is a member retrieval operation</li>
	 *   <li>{@link Random} - Specifies that members should be selected randomly</li>
	 *   <li>{@link Count} - Specifies the number of members to retrieve</li>
	 *   <li>{@link Distinct} - Ensures all returned members are unique</li>
	 * </ul>
	 *
	 * @return a list containing the supported annotation classes
	 */
	@Override
	protected List<Class<? extends Annotation>> getSupportedAnnotationClasses() {
		return OrangeCollectionUtils.asList(GetMembers.class, Random.class, Count.class, Distinct.class);
	}

	/**
	 * Performs the actual retrieval of distinct random members from the Redis Set.
	 * 
	 * <p>This method uses the Redis SPOP command to randomly select distinct members
	 * from the set. The number of members to retrieve is specified in the context's
	 * count parameter. This operation guarantees that all returned members are unique.
	 * If the requested count exceeds the set size, only the available members will be returned.
	 *
	 * @param context the Redis operation context containing the key and count
	 * @param valueField the field annotated with GetMembers (may be null if the annotation is on a method)
	 * @param returnArgumentType the expected return type for the operation
	 * @return a Collection containing the randomly selected distinct members
	 * @throws Exception if an error occurs during the Redis operation
	 */
	@Override
	protected Collection doGet(OrangeRedisContext context, Field valueField, Type returnArgumentType) throws Exception {
		OrangeRedisCountContext ctx = (OrangeRedisCountContext) context;
		return this.operations.distinctRandomMembers(
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
	 * both the Redis key and the count of distinct members to retrieve.
	 *
	 * @return the OrangeRedisCountContext class
	 */
	@Override
	public Class<? extends OrangeRedisContext> getContextClass() {
		return OrangeRedisCountContext.class;
	}
}