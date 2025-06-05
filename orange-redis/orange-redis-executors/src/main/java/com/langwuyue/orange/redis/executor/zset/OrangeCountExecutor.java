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

import com.langwuyue.orange.redis.annotation.GetSize;
import com.langwuyue.orange.redis.context.OrangeRedisContext;
import com.langwuyue.orange.redis.executor.OrangeRedisAbstractExecutor;
import com.langwuyue.orange.redis.mapping.OrangeRedisExecutorIdGenerator;
import com.langwuyue.orange.redis.operations.OrangeRedisZSetOperations;
import com.langwuyue.orange.redis.utils.OrangeCollectionUtils;

/**
 * Executor for retrieving the size (count of members) of a Redis Sorted Set.
 * 
 * <p>This executor provides functionality to get the total number of members
 * in a Redis Sorted Set. It implements a simple counting operation that returns
 * the cardinality of the set identified by the given key.
 *
 *
 * <p>The executor uses {@link OrangeRedisZSetOperations} to perform the actual
 * Redis operation and supports the {@link GetSize} annotation for method mapping.
 *
 * @author Liang.Zhong
 * @since 1.0.0
 */
public class OrangeCountExecutor extends OrangeRedisAbstractExecutor {

	private OrangeRedisZSetOperations operations;

	/**
	 * Constructs a new count executor for Redis Sorted Sets.
	 *
	 * @param operations the Redis Sorted Set operations implementation to use
	 * @param idGenerator the generator for creating unique operation IDs
	 */
	public OrangeCountExecutor(OrangeRedisZSetOperations operations,OrangeRedisExecutorIdGenerator idGenerator) {
		super(idGenerator);
		this.operations = operations;
	}

	/**
	 * Executes the count operation on the Redis Sorted Set.
	 *
	 * <p>This method retrieves the total number of members in the Sorted Set
	 * identified by the key in the provided context.
	 *
	 * @param context the context containing the Redis key
	 * @return the number of members in the Sorted Set as a Long value
	 * @throws Exception if an error occurs while accessing Redis
	 */
	@Override
	public Object execute(OrangeRedisContext context) throws Exception {
		return this.operations.size(context.getRedisKey().getValue());
	}

	/**
	 * Returns the list of annotations supported by this executor.
	 *
	 * <p>This executor supports the {@link GetSize} annotation, which is used
	 * to mark methods that retrieve the size of a Redis Sorted Set.
	 *
	 * @return a list containing only the GetSize.class annotation
	 */
	@Override
	protected List<Class<? extends Annotation>> getSupportedAnnotationClasses() {
		return OrangeCollectionUtils.asList(GetSize.class);
	}
	

}