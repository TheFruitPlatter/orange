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
package com.langwuyue.orange.redis.executor.zset.range.score;

import java.lang.annotation.Annotation;
import java.util.List;

import com.langwuyue.orange.redis.annotation.RemoveMembers;
import com.langwuyue.orange.redis.annotation.zset.MaxScore;
import com.langwuyue.orange.redis.annotation.zset.MinScore;
import com.langwuyue.orange.redis.context.OrangeRedisContext;
import com.langwuyue.orange.redis.executor.OrangeRedisAbstractExecutor;
import com.langwuyue.orange.redis.executor.zset.context.OrangeMaxScoreMinScoreContext;
import com.langwuyue.orange.redis.mapping.OrangeRedisExecutorIdGenerator;
import com.langwuyue.orange.redis.operations.OrangeRedisZSetOperations;
import com.langwuyue.orange.redis.operations.OrangeRedisZSetOperations.ScoreRange;
import com.langwuyue.orange.redis.utils.OrangeCollectionUtils;

/**
 * Executor implementation for removing members from a Redis ZSet based on a score range.
 * This executor removes all members whose scores fall within the specified minimum and maximum score range.
 * 
 * This is particularly useful for cleaning up or pruning sorted sets based on score criteria,
 * such as removing expired items, deleting records within a specific date range, or
 * filtering out elements that fall within certain numerical boundaries.
 *
 * @author Liang.Zhong
 * @since 1.0.0
 */
public class OrangeRemoveByMaxScoreMinScoreExecutor extends OrangeRedisAbstractExecutor {
	
	/**
	 * Redis ZSet operations instance used to perform ZSet-specific operations.
	 */
	private OrangeRedisZSetOperations operations;

	/**
	 * Constructs a new executor for removing members from a Redis ZSet based on a score range.
	 *
	 * @param operations the Redis ZSet operations instance to use for executing ZSet commands
	 * @param idGenerator the ID generator for creating unique executor identifiers
	 */
	public OrangeRemoveByMaxScoreMinScoreExecutor(OrangeRedisZSetOperations operations,OrangeRedisExecutorIdGenerator idGenerator) {
		super(idGenerator);
		this.operations = operations;
	}

	/**
	 * Returns a list of annotation classes that this executor supports.
	 * 
	 * @return a list containing RemoveMembers, MaxScore, and MinScore annotation classes
	 *         that this executor can process for removing members within a score range
	 */
	@Override
	protected List<Class<? extends Annotation>> getSupportedAnnotationClasses() {
		return OrangeCollectionUtils.asList(RemoveMembers.class,MaxScore.class,MinScore.class);
	}

	/**
	 * Returns the context class used by this executor.
	 * 
	 * @return the OrangeRedisMaxScoreMinScoreContext class, which contains the necessary
	 *         parameters for executing ZSet range operations (key, maxScore, and minScore parameters)
	 */
	@Override
	public Class<? extends OrangeRedisContext> getContextClass() {
		return OrangeMaxScoreMinScoreContext.class;
	}

	/**
	 * Executes the Redis ZSet remove operation to remove members within the specified score range.
	 * 
	 * This method casts the context to OrangeMaxScoreMinScoreContext to access the maxScore and minScore parameters,
	 * then calls the operations.removeRangeByScore method to remove members whose scores fall within the specified range.
	 *
	 * @param context the Redis operation context containing the key, maxScore, and minScore parameters
	 * @return the number of members removed from the sorted set
	 * @throws Exception if an error occurs during the Redis operation
	 */
	@Override
	public Object execute(OrangeRedisContext context) throws Exception {
		OrangeMaxScoreMinScoreContext ctx = (OrangeMaxScoreMinScoreContext)context;
		return this.operations.removeRangeByScore(ctx.getRedisKey().getValue(), new ScoreRange(ctx.getMaxScore(),ctx.getMinScore()));
	}
	
	
}