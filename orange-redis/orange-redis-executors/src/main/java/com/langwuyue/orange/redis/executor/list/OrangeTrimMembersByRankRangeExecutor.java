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
import java.util.List;

import com.langwuyue.orange.redis.annotation.RemoveMembers;
import com.langwuyue.orange.redis.annotation.zset.RankRange;
import com.langwuyue.orange.redis.annotation.zset.Reverse;
import com.langwuyue.orange.redis.context.OrangeRedisContext;
import com.langwuyue.orange.redis.context.OrangeRedisRankRangeContext;
import com.langwuyue.orange.redis.executor.OrangeRedisAbstractExecutor;
import com.langwuyue.orange.redis.mapping.OrangeRedisExecutorIdGenerator;
import com.langwuyue.orange.redis.operations.OrangeRedisListOperations;
import com.langwuyue.orange.redis.utils.OrangeCollectionUtils;

/**
 * Executor implementation for trimming Redis lists based on rank range specifications.
 * 
 * <p>This executor provides functionality to trim a Redis list, keeping only elements
 * within the specified rank range (inclusive). All elements outside this range
 * are removed. This operation is similar to the standard trim operation but uses
 * a RankRange annotation to specify the range instead of separate start and end indices.
 * 
 * 
 * <p>The trim operation modifies the list in-place and does not return the removed elements.
 *
 * @author Liang.Zhong
 * @since 1.0.0
 * @see <a href="https://orange.langwuyue.com/redis/advanced/list">Orange Redis List Documentation</a>
 */
public class OrangeTrimMembersByRankRangeExecutor extends OrangeRedisAbstractExecutor {
	
	/**
	 * Redis list operations handler that provides the core functionality for interacting with Redis lists.
	 * This field is used to execute the actual TRIM command on the Redis server.
	 */
	private OrangeRedisListOperations operations;

	/**
	 * Constructs a new OrangeTrimMembersByRankRangeExecutor with the specified operations and ID generator.
	 *
	 * @param operations the Redis list operations handler that will execute the TRIM command
	 * @param idGenerator the generator used to create unique identifiers for this executor
	 */
	public OrangeTrimMembersByRankRangeExecutor(OrangeRedisListOperations operations,OrangeRedisExecutorIdGenerator idGenerator) {
		super(idGenerator);
		this.operations = operations;
	}

	/**
	 * Executes the TRIM operation on a Redis list based on rank range specifications.
	 * 
	 * <p>This method trims the Redis list identified by the key provided in the context,
	 * keeping only elements within the specified rank range (inclusive). All elements
	 * outside this range are removed.
	 * 
	 * <p>The rank range is specified through the {@link OrangeRedisRankRangeContext} which
	 * contains the rank range object with start and end indices for the trim operation.
	 *
	 * @param context the execution context containing the key and rank range information
	 * @return null as this operation does not return any value
	 * @throws Exception if an error occurs during execution
	 * @throws ClassCastException if the provided context is not an instance of {@link OrangeRedisRankRangeContext}
	 */
	@Override
	public Object execute(OrangeRedisContext context) throws Exception {
		OrangeRedisRankRangeContext ctx = (OrangeRedisRankRangeContext)context;
		this.operations.trim(
			context.getRedisKey().getValue(), 
			ctx.getRankRange().getStartIndex(), 
			ctx.getRankRange().getEndIndex()
		);
		return null;
	}

	/**
	 * Returns the list of annotation classes that this executor supports.
	 * 
	 * <p>This executor supports the following annotations:
	 * <ul>
	 *   <li>{@link RemoveMembers} - Indicates that this executor should remove members from a list</li>
	 *   <li>{@link Reverse} - Indicates that the operation should be performed in reverse order</li>
	 *   <li>{@link RankRange} - Specifies the rank range for the trim operation</li>
	 * </ul>
	 *
	 * @return a list containing the supported annotation classes
	 */
	@Override
	protected List<Class<? extends Annotation>> getSupportedAnnotationClasses() {
		return OrangeCollectionUtils.asList(RemoveMembers.class,Reverse.class,RankRange.class);
	}

	/**
	 * Returns the context class required for this executor.
	 * 
	 * <p>This executor requires {@link OrangeRedisRankRangeContext} which provides
	 * the rank range object containing start and end indices for the trim operation.
	 *
	 * @return the {@link OrangeRedisRankRangeContext} class
	 */
	@Override
	public Class<? extends OrangeRedisContext> getContextClass() {
		return OrangeRedisRankRangeContext.class;
	}
}