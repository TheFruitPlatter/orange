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
package com.langwuyue.orange.redis.executor.zset.range.rank;

import java.lang.annotation.Annotation;
import java.util.List;

import com.langwuyue.orange.redis.annotation.RemoveMembers;
import com.langwuyue.orange.redis.annotation.zset.RankRange;
import com.langwuyue.orange.redis.context.OrangeRedisContext;
import com.langwuyue.orange.redis.context.OrangeRedisRankRangeContext;
import com.langwuyue.orange.redis.executor.OrangeRedisAbstractExecutor;
import com.langwuyue.orange.redis.mapping.OrangeRedisExecutorIdGenerator;
import com.langwuyue.orange.redis.operations.OrangeRedisZSetOperations;
import com.langwuyue.orange.redis.utils.OrangeCollectionUtils;

/**
 * Executor implementation for removing members from a Redis ZSet based on their rank range.
 * 
 * This executor removes members from a sorted set within a specified rank range (position range).
 * Ranks are zero-based indexes, with 0 being the element with the lowest score. This is useful
 * for operations like removing the top or bottom N elements from a leaderboard, trimming a sorted
 * collection to a specific size, or removing elements based on their position rather than their score.
 * 
 * The executor supports the following annotations:
 * - RemoveMembers: Indicates this is a member removal operation
 * - RankRange: Specifies the range of ranks (positions) from which to remove members
 *
 * @author Liang.Zhong
 * @since 1.0.0
 */
public class OrangeRemoveByRankRangeExecutor extends OrangeRedisAbstractExecutor {
	
	/**
	 * Redis ZSet operations instance used to perform ZSet-specific operations.
	 */
	private OrangeRedisZSetOperations operations;

	/**
	 * Constructs a new executor for removing members from a Redis ZSet based on their rank range.
	 *
	 * @param operations the Redis ZSet operations instance to use for executing ZSet commands
	 * @param idGenerator the ID generator for creating unique executor identifiers
	 */
	public OrangeRemoveByRankRangeExecutor(OrangeRedisZSetOperations operations,OrangeRedisExecutorIdGenerator idGenerator) {
		super(idGenerator);
		this.operations = operations;
	}

	/**
	 * Returns a list of annotation classes that this executor supports.
	 * 
	 * @return a list containing RemoveMembers and RankRange annotation classes
	 *         that this executor can process for removing members based on rank range
	 */
	@Override
	protected List<Class<? extends Annotation>> getSupportedAnnotationClasses() {
		return OrangeCollectionUtils.asList(RemoveMembers.class,RankRange.class);
	}

	/**
	 * Returns the context class used by this executor.
	 * 
	 * @return the OrangeRankRangeContext class, which contains the necessary
	 *         parameters for executing rank range operations, including the key
	 *         and the start and end rank values
	 */
	@Override
	public Class<? extends OrangeRedisContext> getContextClass() {
		return OrangeRedisRankRangeContext.class;
	}

	/**
	 * Executes the removal of members from a Redis ZSet based on their rank range.
	 * 
	 * This method removes all members whose positions fall within the specified rank range from
	 * the sorted set. The rank range is zero-based, where 0 represents the member with the lowest
	 * score. The method first casts the context to OrangeRedisRankRangeContext to access the key
	 * and rank range parameters, then uses the ZSet operations to perform the removal.
	 *
	 * @param context the Redis operation context containing the key and rank range parameters
	 * @return the number of members that were removed from the sorted set
	 * @throws Exception if an error occurs during the Redis operation
	 */
	@Override
	public Object execute(OrangeRedisContext context) throws Exception {
		OrangeRedisRankRangeContext ctx = (OrangeRedisRankRangeContext)context;
		return this.operations.removeRange(context.getRedisKey().getValue(), ctx.getRankRange());
	}
	
	
}