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

import com.langwuyue.orange.redis.annotation.GetSize;
import com.langwuyue.orange.redis.context.OrangeRedisContext;
import com.langwuyue.orange.redis.executor.OrangeRedisAbstractExecutor;
import com.langwuyue.orange.redis.mapping.OrangeRedisExecutorIdGenerator;
import com.langwuyue.orange.redis.operations.OrangeRedisSetOperations;
import com.langwuyue.orange.redis.utils.OrangeCollectionUtils;

/**
 * Executor implementation for retrieving the size (number of members) of a Redis Set.
 * 
 * <p>This executor is responsible for determining the cardinality of a Redis Set
 * identified by the specified key. It supports the {@link GetSize} annotation
 * which indicates that this operation should return the count of members in the set.
 * 
 * <p>The executor extends {@link OrangeRedisAbstractExecutor} and uses 
 * {@link OrangeRedisSetOperations} to perform the actual Redis operations.
 * 
 * <p>The size is returned as a numeric value (typically Long) representing the
 * total number of members in the set. This operation has O(1) time complexity
 * in Redis as it doesn't need to retrieve all members to determine the size.
 *
 * @author Liang.Zhong
 * @since 1.0.0
 */
public class OrangeGetSizeExecutor extends OrangeRedisAbstractExecutor {
	
	private OrangeRedisSetOperations operations;

	/**
	 * Constructs a new OrangeGetSizeExecutor with the specified Redis operations and ID generator.
	 *
	 * @param operations the Redis Set operations implementation to use for size retrieval
	 * @param idGenerator the generator for creating unique executor identifiers
	 */
	public OrangeGetSizeExecutor(OrangeRedisSetOperations operations, OrangeRedisExecutorIdGenerator idGenerator) {
		super(idGenerator);
		this.operations = operations;
	}

	/**
	 * Executes the size retrieval operation on the Redis Set.
	 * 
	 * <p>This method retrieves the number of members in the Redis Set identified by
	 * the key in the provided context. It uses the Redis SCARD command which has
	 * O(1) time complexity.
	 *
	 * @param context the Redis operation context containing the key and other metadata
	 * @return a Long value representing the number of members in the set
	 * @throws Exception if an error occurs during the Redis operation
	 */
	@Override
	public Object execute(OrangeRedisContext context) throws Exception {
		return this.operations.size(context.getRedisKey().getValue());
	}

	/**
	 * Returns the list of annotation classes that this executor supports.
	 * 
	 * <p>This executor specifically supports the {@link GetSize} annotation,
	 * which is used to mark methods or fields that should retrieve the size
	 * of a Redis Set.
	 *
	 * @return a list containing the GetSize.class annotation class
	 */
	@Override
	protected List<Class<? extends Annotation>> getSupportedAnnotationClasses() {
		return OrangeCollectionUtils.asList(GetSize.class);
	}
	
}