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

import com.langwuyue.orange.redis.annotation.GetSize;
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
 * Executor implementation for counting members in a Redis Sorted Set (ZSet) within a specified score range.
 * 
 * <p>This executor handles Redis ZSet operations that count the number of members whose scores fall
 * within a range specified by separate {@link MaxScore} and {@link MinScore} annotations. It returns
 * the count as a numeric value rather than retrieving the actual members.
 * 
 * <p>The executor uses {@link OrangeRedisZSetOperations#countByScore} to perform the actual Redis operation.
 * It counts all members whose scores are within the specified range without retrieving the members themselves,
 * making it more efficient than retrieving and counting members when only the count is needed.
 * 
 * @author Liang.Zhong
 * @since 1.0.0
 * @see OrangeRedisAbstractExecutor
 * @see OrangeMaxScoreMinScoreContext
 * @see OrangeRedisZSetOperations
 * @see MaxScore
 * @see MinScore
 * @see GetSize
 */
public class OrangeCountByMaxScoreMinScoreExecutor extends OrangeRedisAbstractExecutor {
	
	/**
	 * Redis ZSet operations instance used to execute the actual Redis commands.
	 */
	private OrangeRedisZSetOperations operations;

	/**
	 * Constructs a new executor for counting ZSet members by score range.
	 * 
	 * @param operations The Redis ZSet operations implementation to use for executing commands
	 * @param idGenerator The ID generator for creating unique executor identifiers
	 */
	public OrangeCountByMaxScoreMinScoreExecutor(OrangeRedisZSetOperations operations,OrangeRedisExecutorIdGenerator idGenerator) {
		super(idGenerator);
		this.operations = operations;
	}

	/**
	 * Specifies which annotations this executor supports.
	 * 
	 * <p>This executor supports three annotations:
	 * <ul>
	 *   <li>{@link GetSize} - Indicates this is a count operation rather than a retrieval operation
	 *   <li>{@link MaxScore} - Specifies the maximum score (inclusive) for range query
	 *   <li>{@link MinScore} - Specifies the minimum score (inclusive) for range query
	 * </ul>
	 * 
	 * <p>The combination of these annotations allows the framework to identify methods that should
	 * count members within a score range rather than retrieving them.
	 * 
	 * @return A List containing the supported annotation classes
	 */
	@Override
	protected List<Class<? extends Annotation>> getSupportedAnnotationClasses() {
		return OrangeCollectionUtils.asList(GetSize.class,MaxScore.class,MinScore.class);
	}

	/**
	 * Specifies the context class used by this executor.
	 * 
	 * @return The OrangeMaxScoreMinScoreContext class
	 */
	@Override
	public Class<? extends OrangeRedisContext> getContextClass() {
		return OrangeMaxScoreMinScoreContext.class;
	}

	/**
	 * Executes the Redis ZSet count operation to determine the number of members within the specified score range.
	 * 
	 * <p>The returned value is a Long representing the count of members whose scores fall within
	 * the specified range.
	 * 
	 * @param context The operation context containing key and score range parameters
	 * @return A Long value representing the count of members in the specified score range
	 * @throws Exception if the Redis operation fails
	 */
	@Override
	public Object execute(OrangeRedisContext context) throws Exception {
		OrangeMaxScoreMinScoreContext ctx = (OrangeMaxScoreMinScoreContext)context;
		return this.operations.countByScore(ctx.getRedisKey().getValue(), new ScoreRange(ctx.getMaxScore(),ctx.getMinScore()));
	}
	
	
}