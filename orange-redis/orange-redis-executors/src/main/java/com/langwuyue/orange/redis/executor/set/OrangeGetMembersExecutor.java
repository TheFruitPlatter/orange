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

import com.langwuyue.orange.redis.annotation.GetMembers;
import com.langwuyue.orange.redis.context.OrangeRedisContext;
import com.langwuyue.orange.redis.executor.OrangeRedisGetAbstractExecutor;
import com.langwuyue.orange.redis.mapping.OrangeRedisExecutorIdGenerator;
import com.langwuyue.orange.redis.operations.OrangeRedisSetOperations;
import com.langwuyue.orange.redis.utils.OrangeCollectionUtils;

/**
 * Executor implementation for retrieving all members from a Redis Set.
 * 
 * <p>This executor is responsible for fetching all members stored in a Redis Set
 * identified by the specified key. It supports the {@link GetMembers} annotation
 * which indicates that this operation should retrieve all members from a set.
 * 
 * <p>The executor extends {@link OrangeRedisGetAbstractExecutor} to leverage common
 * get operation functionality and uses {@link OrangeRedisSetOperations} to perform
 * the actual Redis operations.
 * 
 * <p>The retrieved members are returned as a Collection with the appropriate generic
 * type based on either the value field's type or the method's return type.
 *
 * @author Liang.Zhong
 * @since 1.0.0
 */
public class OrangeGetMembersExecutor extends OrangeRedisGetAbstractExecutor {
	
	private OrangeRedisSetOperations operations;

	/**
	 * Constructs a new OrangeGetMembersExecutor with the specified Redis operations and ID generator.
	 *
	 * @param operations the Redis Set operations implementation to use for member retrieval
	 * @param idGenerator the generator for creating unique executor identifiers
	 */
	public OrangeGetMembersExecutor(OrangeRedisSetOperations operations, OrangeRedisExecutorIdGenerator idGenerator) {
		super(idGenerator);
		this.operations = operations;
	}

	/**
	 * Returns the list of annotation classes that this executor supports.
	 * 
	 * <p>This executor specifically supports the {@link GetMembers} annotation,
	 * which is used to mark methods or fields that should retrieve all members
	 * from a Redis Set.
	 *
	 * @return a list containing the GetMembers.class annotation class
	 */
	@Override
	protected List<Class<? extends Annotation>> getSupportedAnnotationClasses() {
		return OrangeCollectionUtils.asList(GetMembers.class);
	}

	/**
	 * Performs the actual retrieval of members from the Redis Set.
	 * 
	 * <p>This method is called by the abstract executor to perform the specific
	 * Redis Set member retrieval operation. It uses the configured Redis operations
	 * to fetch all members of the set identified by the context's Redis key.
	 *
	 * @param context the Redis operation context containing the key and other metadata
	 * @param valueField the field annotated with GetMembers (may be null if the annotation is on a method)
	 * @param returnArgumentType the expected return type for the operation
	 * @return a Collection containing all members of the Redis Set
	 * @throws Exception if an error occurs during the Redis operation
	 */
	@Override
	protected Collection doGet(OrangeRedisContext context, Field valueField, Type returnArgumentType) throws Exception {
		return this.operations.members(
			context.getRedisKey().getValue(), 
			context.getValueType(), 
			valueField == null ? returnArgumentType : valueField.getGenericType()
		);
	}
}