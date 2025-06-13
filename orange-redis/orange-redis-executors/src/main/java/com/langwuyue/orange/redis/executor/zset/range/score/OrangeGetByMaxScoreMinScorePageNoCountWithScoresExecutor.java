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

import com.langwuyue.orange.redis.annotation.Count;
import com.langwuyue.orange.redis.annotation.GetMembers;
import com.langwuyue.orange.redis.annotation.zset.MaxScore;
import com.langwuyue.orange.redis.annotation.zset.MinScore;
import com.langwuyue.orange.redis.annotation.zset.PageNo;
import com.langwuyue.orange.redis.annotation.zset.WithScores;
import com.langwuyue.orange.redis.context.OrangeRedisContext;
import com.langwuyue.orange.redis.executor.zset.OrangeGetWithScoresAbstractExecutor;
import com.langwuyue.orange.redis.executor.zset.context.OrangeMaxScoreMinScorePageNoCountContext;
import com.langwuyue.orange.redis.mapping.OrangeRedisExecutorIdGenerator;
import com.langwuyue.orange.redis.operations.OrangeRedisZSetOperations;
import com.langwuyue.orange.redis.operations.OrangeRedisZSetOperations.Pager;
import com.langwuyue.orange.redis.operations.OrangeRedisZSetOperations.ScoreRange;
import com.langwuyue.orange.redis.utils.OrangeCollectionUtils;

/**
 * Executor for retrieving members with their scores from a Redis ZSet within a specified score range using pagination.
 * 
 * <p>This executor retrieves members and their scores from a Redis ZSet where the score is between a maximum and minimum value,
 * with pagination support using page number and count parameters. It does not perform a count operation before
 * retrieving the members, which can improve performance for large sets when the total count is not needed.
 * 
 * <p>The key difference between this executor and {@link OrangeGetByMaxScoreMinScorePageNoCountExecutor} is that
 * this executor returns both the members and their scores as {@link OrangeRedisZSetOperations.TypedTuple} objects,
 * while the other executor returns only the members.
 * 
 * <p>This executor processes the following annotations:
 * <ul>
 *   <li>{@link GetMembers} - Marks the method as a ZSet member retrieval operation
 *   <li>{@link MaxScore} - Specifies the maximum score bound (inclusive)
 *   <li>{@link MinScore} - Specifies the minimum score bound (inclusive)
 *   <li>{@link PageNo} - Specifies the page number for pagination (1-based)
 *   <li>{@link Count} - Specifies the number of items per page
 *   <li>{@link WithScores} - Indicates that scores should be returned along with members
 * </ul>
 * 
 * @author Liang.Zhong
 * @since 1.0.0
 * @see OrangeMaxScoreMinScorePageNoCountContext
 * @see OrangeRedisZSetOperations#rangeByScoreWithScores(Object, ScoreRange, Pager, RedisValueTypeEnum, Type)
 * @see OrangeGetWithScoresAbstractExecutor
 */
public class OrangeGetByMaxScoreMinScorePageNoCountWithScoresExecutor extends OrangeGetWithScoresAbstractExecutor {
	
	/**
	 * Redis ZSet operations instance used to perform ZSet-specific operations with score information.
	 */
	private OrangeRedisZSetOperations operations;

	/**
	 * Constructs a new executor for retrieving members with their scores from a Redis ZSet within a specified score range using pagination.
	 *
	 * @param operations the Redis ZSet operations instance to use for executing ZSet commands
	 * @param idGenerator the ID generator for creating unique executor identifiers
	 */
	public OrangeGetByMaxScoreMinScorePageNoCountWithScoresExecutor(OrangeRedisZSetOperations operations,OrangeRedisExecutorIdGenerator idGenerator) {
		super(idGenerator);
		this.operations = operations;
	}

	/**
	 * Executes the Redis ZSet range query operation to retrieve members with their scores within the specified score range.
	 *
	 * @param context the Redis operation context containing key, score range, and pagination parameters
	 * @param valueField the field representing the value type in the target object, may be null
	 * @param returnArgumentType the expected return type for the operation
	 * @return a Collection of TypedTuple objects containing both members and their scores from the ZSet
	 * @throws Exception if an error occurs during the Redis operation
	 */
	@Override
	public Collection doGet(OrangeRedisContext context, Field valueField, Type returnArgumentType) throws Exception {
		OrangeMaxScoreMinScorePageNoCountContext ctx = (OrangeMaxScoreMinScorePageNoCountContext)context;
		return this.operations.rangeByScoreWithScores(
				ctx.getRedisKey().getValue(), 
				new ScoreRange(ctx.getMaxScore(),ctx.getMinScore()),
				new Pager(ctx.getPageNo(),ctx.getCount()),
				ctx.getValueType(), 
				valueField == null ? returnArgumentType : valueField.getGenericType()
		);
	}

	/**
	 * Returns a list of annotation classes that this executor supports.
	 * 
	 * @return a list containing the annotation classes that this executor can process:
	 *         GetMembersWithScores, MaxScore, MinScore, PageNo, and Count
	 */
	@Override
	protected List<Class<? extends Annotation>> getSupportedAnnotationClasses() {
		return OrangeCollectionUtils.asList(GetMembers.class,MaxScore.class,MinScore.class,WithScores.class,PageNo.class,Count.class);
	}

	/**
	 * Returns the context class used by this executor.
	 * 
	 * @return the OrangeMaxScoreMinScorePageNoCountContext class, which contains the necessary
	 *         parameters for executing ZSet range queries with score boundaries and pagination
	 */
	@Override
	public Class<? extends OrangeRedisContext> getContextClass() {
		return OrangeMaxScoreMinScorePageNoCountContext.class;
	}
	
	
}