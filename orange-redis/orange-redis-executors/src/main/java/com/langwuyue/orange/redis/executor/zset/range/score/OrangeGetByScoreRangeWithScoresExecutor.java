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
import com.langwuyue.orange.redis.annotation.zset.WithScores;
import com.langwuyue.orange.redis.context.OrangeRedisContext;
import com.langwuyue.orange.redis.executor.zset.OrangeGetWithScoresAbstractExecutor;
import com.langwuyue.orange.redis.executor.zset.context.OrangeScoreRangeContext;
import com.langwuyue.orange.redis.mapping.OrangeRedisExecutorIdGenerator;
import com.langwuyue.orange.redis.operations.OrangeRedisZSetOperations;
import com.langwuyue.orange.redis.utils.OrangeCollectionUtils;

/**
 * Executor implementation for retrieving members with their scores from a Redis Sorted Set (ZSet) 
 * using a single ScoreRange annotation.
 * 
 * <p>This executor provides functionality similar to {@link OrangeGetByMaxScoreMinScoreWithScoresExecutor} 
 * but uses a single {@link ScoreRange} annotation instead of separate {@link MaxScore} and {@link MinScore} 
 * annotations. It supports the following annotations:
 * <ul>
 *   <li>{@link GetMembers} - Indicates this is a member retrieval operation
 *   <li>{@link ScoreRange} - Specifies both minimum and maximum scores for the range query in a single annotation
 *   <li>{@link WithScores} - Indicates that scores should be included with the returned members
 * </ul>
 * 
 * <p>The executor uses {@link OrangeRedisZSetOperations#rangeByScoreWithScores} to perform the actual Redis operation.
 * It retrieves all members whose scores are within the specified range, along with their scores.
 * 
 * @author Liang.Zhong
 * @since 1.0.0
 * @see OrangeGetWithScoresAbstractExecutor
 * @see OrangeScoreRangeContext
 * @see OrangeRedisZSetOperations
 * @see OrangeRedisZSetOperations.TypedTuple
 * @see <a href="https://orange.langwuyue.com/redis/advanced/zset">Orange Redis ZSet Documentation</a>
 */
public class OrangeGetByScoreRangeWithScoresExecutor extends OrangeGetWithScoresAbstractExecutor {
	
	/**
	 * Redis ZSet operations instance used to execute the actual Redis commands.
	 */
	private OrangeRedisZSetOperations operations;

	/**
	 * Constructs a new executor for retrieving ZSet members with their scores using a score range.
	 * 
	 * @param operations The Redis ZSet operations implementation to use for executing commands
	 * @param idGenerator The ID generator for creating unique executor identifiers
	 */
	public OrangeGetByScoreRangeWithScoresExecutor(OrangeRedisZSetOperations operations,OrangeRedisExecutorIdGenerator idGenerator) {
		super(idGenerator);
		this.operations = operations;
	}

	/**
	 * Retrieves members and their scores from the Redis ZSet within the specified score range.
	 * 
	 * <p>This method performs the following steps:
	 * <ol>
	 *   <li>Casts the context to {@link OrangeScoreRangeContext} to access the score range
	 *   <li>Retrieves the Redis key and score range from the context
	 *   <li>Determines the correct return type based on the valueField or returnArgumentType
	 *   <li>Executes the rangeByScoreWithScores operation to get members and scores
	 * </ol>
	 * 
	 * <p>The method handles type conversion and generic type resolution to ensure the returned
	 * collection matches the expected return type of the annotated method.
	 * 
	 * @param context The operation context containing key and score range parameters
	 * @param valueField The field representing the value type, may be null
	 * @param returnArgumentType The expected return type from the annotated method
	 * @return A Collection of TypedTuple objects containing members and their scores
	 * @throws Exception if the Redis operation fails or type conversion fails
	 */
	@Override
	public Collection doGet(OrangeRedisContext context, Field valueField, Type returnArgumentType) throws Exception {
		OrangeScoreRangeContext ctx = (OrangeScoreRangeContext)context;
		return this.operations.rangeByScoreWithScores(
				ctx.getRedisKey().getValue(), 
				ctx.getScoreRange(), 
				ctx.getValueType(), 
				valueField == null ? returnArgumentType : valueField.getGenericType()
		);
	}

	/**
	 * Specifies which annotations this executor supports.
	 * 
	 * <p>This executor supports three annotations:
	 * <ul>
	 *   <li>{@link GetMembers} - Indicates this is a member retrieval operation
	 *   <li>{@link ScoreRange} - Specifies both minimum and maximum scores for range query in a single annotation
	 *   <li>{@link WithScores} - Indicates that scores should be included with the returned members
	 * </ul>
	 * 
	 * <p>The combination of these annotations allows the framework to identify methods that should
	 * retrieve members with their scores from within a specified score range using a single ScoreRange annotation.
	 * 
	 * @return A List containing the supported annotation classes
	 */
	@Override
	protected List<Class<? extends Annotation>> getSupportedAnnotationClasses() {
		return OrangeCollectionUtils.asList(GetMembers.class,ScoreRange.class,WithScores.class);
	}

	/**
	 * Specifies the context class used by this executor.
	 * 
	 * <p>This executor uses {@link OrangeScoreRangeContext} which provides:
	 * <ul>
	 *   <li>Access to the score range parameters through a single ScoreRange object
	 *   <li>Validation of score range parameters
	 *   <li>Type conversion for Redis operations
	 *   <li>Support for inclusive/exclusive range bounds as specified in the ScoreRange annotation
	 * </ul>
	 * 
	 * @return The OrangeScoreRangeContext class
	 */
	@Override
	public Class<? extends OrangeRedisContext> getContextClass() {
		return OrangeScoreRangeContext.class;
	}
	
	
}