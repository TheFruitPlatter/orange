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
import com.langwuyue.orange.redis.annotation.zset.ScoreRange;
import com.langwuyue.orange.redis.context.OrangeRedisContext;
import com.langwuyue.orange.redis.executor.OrangeRedisGetAbstractExecutor;
import com.langwuyue.orange.redis.executor.zset.context.OrangeScoreRangeContext;
import com.langwuyue.orange.redis.mapping.OrangeRedisExecutorIdGenerator;
import com.langwuyue.orange.redis.operations.OrangeRedisZSetOperations;
import com.langwuyue.orange.redis.utils.OrangeCollectionUtils;

/**
 * Executor implementation for retrieving members from a Redis Sorted Set (ZSet) using a score range.
 * 
 * <p>This executor handles Redis ZSet operations that retrieve members based on a score range specified
 * through the {@link ScoreRange} annotation. It provides a more concise alternative to using separate
 * {@link com.langwuyue.orange.redis.annotation.zset.MaxScore} and {@link com.langwuyue.orange.redis.annotation.zset.MinScore} annotations.
 * 
 * <p>The executor uses {@link OrangeRedisZSetOperations#rangeByScore} to perform the actual Redis operation.
 * It retrieves all members whose scores are within the specified range.
 * 
 * @author Liang.Zhong
 * @since 1.0.0
 * @see OrangeRedisGetAbstractExecutor
 * @see OrangeScoreRangeContext
 * @see OrangeRedisZSetOperations
 * @see ScoreRange
 * @see <a href="https://orange.langwuyue.com/redis/advanced/zset">Orange Redis ZSet Documentation</a>
 */
public class OrangeGetByScoreRangeExecutor extends OrangeRedisGetAbstractExecutor {
	
	/**
	 * Redis ZSet operations instance used to execute the actual Redis commands.
	 */
	private OrangeRedisZSetOperations operations;

	/**
	 * Constructs a new executor for retrieving ZSet members by score range.
	 * 
	 * @param operations The Redis ZSet operations implementation to use for executing commands
	 * @param idGenerator The ID generator for creating unique executor identifiers
	 */
	public OrangeGetByScoreRangeExecutor(OrangeRedisZSetOperations operations,OrangeRedisExecutorIdGenerator idGenerator) {
		super(idGenerator);
		this.operations = operations;
	}

	/**
	 * Executes the Redis ZSet range query operation to retrieve members within the specified score range.
	 * 
	 * @param context The operation context containing key and score range parameters
	 * @param valueField The field annotated with value type information, may be null
	 * @param returnArgumentType The expected return type for the operation
	 * @return A Collection of members whose scores fall within the specified range, ordered by score
	 * @throws Exception if the Redis operation fails or type conversion fails
	 */
	@Override
	public Collection doGet(OrangeRedisContext context, Field valueField, Type returnArgumentType) throws Exception {
		OrangeScoreRangeContext ctx = (OrangeScoreRangeContext)context;
		return this.operations.rangeByScore(
				ctx.getRedisKey().getValue(), 
				ctx.getScoreRange(), 
				ctx.getValueType(), 
				valueField == null ? returnArgumentType : valueField.getGenericType()
		);
	}

	/**
	 * Specifies which annotations this executor supports.
	 * 
	 * <p>This executor supports two annotations:
	 * <ul>
	 *   <li>{@link GetMembers} - Indicates this is a member retrieval operation
	 *   <li>{@link ScoreRange} - Specifies the score range for the query, including minimum and maximum scores
	 * </ul>
	 * 
	 * <p>The ScoreRange annotation provides a more concise way to specify both minimum and maximum scores
	 * compared to using separate MaxScore and MinScore annotations.
	 * 
	 * @return A List containing the supported annotation classes
	 */
	@Override
	protected List<Class<? extends Annotation>> getSupportedAnnotationClasses() {
		return OrangeCollectionUtils.asList(GetMembers.class,ScoreRange.class);
	}

	/**
	 * Specifies the context class used by this executor.
	 * 
	 */
	@Override
	public Class<? extends OrangeRedisContext> getContextClass() {
		return OrangeScoreRangeContext.class;
	}
	
	
}