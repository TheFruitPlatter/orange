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
import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.List;

import com.langwuyue.orange.redis.annotation.GetMembers;
import com.langwuyue.orange.redis.annotation.zset.MaxScore;
import com.langwuyue.orange.redis.annotation.zset.MinScore;
import com.langwuyue.orange.redis.annotation.zset.Reverse;
import com.langwuyue.orange.redis.context.OrangeRedisContext;
import com.langwuyue.orange.redis.executor.OrangeRedisGetAbstractExecutor;
import com.langwuyue.orange.redis.executor.zset.context.OrangeMaxScoreMinScoreContext;
import com.langwuyue.orange.redis.mapping.OrangeRedisExecutorIdGenerator;
import com.langwuyue.orange.redis.operations.OrangeRedisZSetOperations;
import com.langwuyue.orange.redis.operations.OrangeRedisZSetOperations.ScoreRange;
import com.langwuyue.orange.redis.utils.OrangeCollectionUtils;

/**
 * Executor implementation for retrieving members from a Redis ZSet in reverse order based on a score range.
 * 
 * This executor retrieves members whose scores fall within the specified range (defined by MaxScore and MinScore),
 * and returns them in reverse order (from highest to lowest score). This is particularly useful for operations
 * such as retrieving the top-N highest scoring elements, accessing the most recent items in time-based sorted sets,
 * or implementing leaderboards where higher scores are better.
 *
 * @author Liang.Zhong
 * @since 1.0.0
 * @see <a href="https://orange.langwuyue.com/redis/advanced/zset">Orange Redis ZSet Documentation</a>
 */
public class OrangeReverseByMaxScoreMinScoreExecutor extends OrangeRedisGetAbstractExecutor {
	
	/**
	 * Redis ZSet operations instance used to perform ZSet-specific operations.
	 */
	private OrangeRedisZSetOperations operations;

	/**
	 * Constructs a new executor for retrieving members from a Redis ZSet in reverse order based on a score range.
	 *
	 * @param operations the Redis ZSet operations instance to use for executing ZSet commands
	 * @param idGenerator the ID generator for creating unique executor identifiers
	 */
	public OrangeReverseByMaxScoreMinScoreExecutor(OrangeRedisZSetOperations operations,OrangeRedisExecutorIdGenerator idGenerator) {
		super(idGenerator);
		this.operations = operations;
	}

	/**
	 * Retrieves members from a Redis ZSet in reverse order (highest to lowest score) within the specified score range.
	 * 
	 * This method casts the context to OrangeMaxScoreMinScoreContext to access the score range parameters,
	 * then calls the operations.reverseRangeByScore method to retrieve members whose scores fall within 
	 * the specified range in reverse order.
	 *
	 * @param context the Redis operation context containing the key and score range parameters
	 * @param valueField the field representing the value type, used for type conversion
	 * @param returnArgumentType the expected return type for the collection elements
	 * @return a collection of members from the sorted set that fall within the specified score range, in reverse order
	 * @throws Exception if an error occurs during the Redis operation
	 */
	@Override
	public Collection doGet(OrangeRedisContext context, Field valueField, Type returnArgumentType) throws Exception {
		OrangeMaxScoreMinScoreContext ctx = (OrangeMaxScoreMinScoreContext)context;
		return this.operations.reverseRangeByScore(
				ctx.getRedisKey().getValue(), 
				new ScoreRange(ctx.getMaxScore(),ctx.getMinScore()), 
				ctx.getValueType(), 
				valueField == null ? returnArgumentType : valueField.getGenericType()
		);
	}

	/**
	 * Returns a list of annotation classes that this executor supports.
	 * 
	 * @return a list containing GetMembers, MaxScore, and MinScore annotation classes
	 *         that this executor can process for retrieving members within a score range
	 *         in reverse order
	 */
	@Override
	protected List<Class<? extends Annotation>> getSupportedAnnotationClasses() {
		return OrangeCollectionUtils.asList(GetMembers.class,MaxScore.class,MinScore.class,Reverse.class);
	}

	/**
	 * Returns the context class used by this executor.
	 * 
	 * @return the OrangeMaxScoreMinScoreContext class, which contains the necessary
	 *         parameters for executing ZSet range operations with max and min score boundaries
	 */
	@Override
	public Class<? extends OrangeRedisContext> getContextClass() {
		return OrangeMaxScoreMinScoreContext.class;
	}
}