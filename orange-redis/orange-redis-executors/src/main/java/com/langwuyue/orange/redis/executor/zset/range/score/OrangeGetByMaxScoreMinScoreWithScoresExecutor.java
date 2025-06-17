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
import com.langwuyue.orange.redis.annotation.zset.WithScores;
import com.langwuyue.orange.redis.context.OrangeRedisContext;
import com.langwuyue.orange.redis.executor.zset.OrangeGetWithScoresAbstractExecutor;
import com.langwuyue.orange.redis.executor.zset.context.OrangeMaxScoreMinScoreContext;
import com.langwuyue.orange.redis.mapping.OrangeRedisExecutorIdGenerator;
import com.langwuyue.orange.redis.operations.OrangeRedisZSetOperations;
import com.langwuyue.orange.redis.operations.OrangeRedisZSetOperations.ScoreRange;
import com.langwuyue.orange.redis.utils.OrangeCollectionUtils;

/**
 * Executor implementation for retrieving members with their scores from a Redis Sorted Set (ZSet) 
 * within a specified score range.
 * 
 * <p>This executor extends the functionality of {@link OrangeGetByMaxScoreMinScoreExecutor} by also 
 * retrieving the score associated with each member. It supports the following annotations:
 * <ul>
 *   <li>{@link GetMembers} - Indicates this is a member retrieval operation
 *   <li>{@link MaxScore} - Specifies the maximum score (inclusive) for range query
 *   <li>{@link MinScore} - Specifies the minimum score (inclusive) for range query
 *   <li>{@link WithScores} - Indicates that scores should be included with the returned members
 * </ul>
 * 
 * <p>The executor uses {@link OrangeRedisZSetOperations#rangeByScoreWithScores} to perform the actual Redis operation.
 * It retrieves all members whose scores are within the specified range [minScore, maxScore], along with their scores.
 * 
 * @author Liang.Zhong
 * @since 1.0.0
 * @see OrangeGetWithScoresAbstractExecutor
 * @see OrangeMaxScoreMinScoreContext
 * @see OrangeRedisZSetOperations
 * @see OrangeRedisZSetOperations.TypedTuple
 * @see <a href="https://orange.langwuyue.com/redis/advanced/zset">Orange Redis ZSet Documentation</a>
 */
public class OrangeGetByMaxScoreMinScoreWithScoresExecutor extends OrangeGetWithScoresAbstractExecutor {
	
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
	public OrangeGetByMaxScoreMinScoreWithScoresExecutor(OrangeRedisZSetOperations operations,OrangeRedisExecutorIdGenerator idGenerator) {
		super(idGenerator);
		this.operations = operations;
	}

	/**
	 * Retrieves members and their scores from the Redis ZSet within the specified score range.
	 * 
	 * <p>This method performs the following steps:
	 * <ol>
	 *   <li>Casts the context to {@link OrangeMaxScoreMinScoreContext} to access score range parameters
	 *   <li>Creates a {@link ScoreRange} object with the maximum and minimum scores
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
		OrangeMaxScoreMinScoreContext ctx = (OrangeMaxScoreMinScoreContext)context;
		return this.operations.rangeByScoreWithScores(
				ctx.getRedisKey().getValue(), 
				new ScoreRange(ctx.getMaxScore(),ctx.getMinScore()), 
				ctx.getValueType(), 
				valueField == null ? returnArgumentType : valueField.getGenericType()
		);
	}

	/**
	 * Specifies which annotations this executor supports.
	 * 
	 * <p>This executor supports four annotations:
	 * <ul>
	 *   <li>{@link GetMembers} - Indicates this is a member retrieval operation
	 *   <li>{@link MaxScore} - Specifies the maximum score for range query
	 *   <li>{@link MinScore} - Specifies the minimum score for range query
	 *   <li>{@link WithScores} - Indicates that scores should be included with the returned members
	 * </ul>
	 * 
	 * <p>The combination of these annotations allows the framework to identify methods that should
	 * retrieve members with their scores from within a specified score range.
	 * 
	 * @return A List containing the supported annotation classes
	 */
	@Override
	protected List<Class<? extends Annotation>> getSupportedAnnotationClasses() {
		return OrangeCollectionUtils.asList(GetMembers.class,MaxScore.class,MinScore.class,WithScores.class);
	}

	/**
	 * Specifies the context class used by this executor.
	 * 
	 * <p>This executor uses {@link OrangeMaxScoreMinScoreContext} which provides:
	 * <ul>
	 *   <li>Access to the maximum score parameter
	 *   <li>Access to the minimum score parameter
	 *   <li>Validation of score parameters
	 *   <li>Type conversion for Redis operations
	 * </ul>
	 * 
	 * @return The OrangeMaxScoreMinScoreContext class
	 */
	@Override
	public Class<? extends OrangeRedisContext> getContextClass() {
		return OrangeMaxScoreMinScoreContext.class;
	}
	
	
}