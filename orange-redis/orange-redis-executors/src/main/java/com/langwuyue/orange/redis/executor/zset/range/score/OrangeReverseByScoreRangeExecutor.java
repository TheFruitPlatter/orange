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
import com.langwuyue.orange.redis.annotation.zset.Reverse;
import com.langwuyue.orange.redis.annotation.zset.ScoreRange;
import com.langwuyue.orange.redis.context.OrangeRedisContext;
import com.langwuyue.orange.redis.executor.OrangeRedisGetAbstractExecutor;
import com.langwuyue.orange.redis.executor.zset.context.OrangeScoreRangeContext;
import com.langwuyue.orange.redis.mapping.OrangeRedisExecutorIdGenerator;
import com.langwuyue.orange.redis.operations.OrangeRedisZSetOperations;
import com.langwuyue.orange.redis.utils.OrangeCollectionUtils;

/**
 * Executor implementation for retrieving members from a Redis Sorted Set (ZSet) 
 * using a score range, but in reverse order (highest to lowest score).
 * 
 * <p>This executor provides functionality similar to {@link OrangeGetByScoreRangeExecutor} 
 * but returns members in descending score order rather than ascending order. It supports the following annotations:
 * <ul>
 *   <li>{@link GetMembers} - Indicates this is a member retrieval operation
 *   <li>{@link ScoreRange} - Specifies both minimum and maximum scores for the range query in a single annotation
 *   <li>{@link Reverse} - Indicates that members should be returned in reverse order (highest to lowest score)
 * </ul>
 * 
 * <p>The executor uses {@link OrangeRedisZSetOperations#reverseRangeByScore} to perform the actual Redis operation.
 * It retrieves all members whose scores are within the specified range, but in reverse order.
 * 
 * @author Liang.Zhong
 * @since 1.0.0
 * @see OrangeRedisGetAbstractExecutor
 * @see OrangeScoreRangeContext
 * @see OrangeRedisZSetOperations
 */
public class OrangeReverseByScoreRangeExecutor extends OrangeRedisGetAbstractExecutor {
	
	/**
	 * Redis ZSet operations instance used to execute the actual Redis commands.
	 * This provides access to specialized sorted set operations including reverseRangeByScore.
	 */
	private OrangeRedisZSetOperations operations;

	/**
	 * Constructs a new executor for retrieving ZSet members in reverse order using a score range.
	 * 
	 * @param operations The Redis ZSet operations implementation to use for executing commands
	 * @param idGenerator The ID generator for creating unique executor identifiers
	 */
	public OrangeReverseByScoreRangeExecutor(OrangeRedisZSetOperations operations,OrangeRedisExecutorIdGenerator idGenerator) {
		super(idGenerator);
		this.operations = operations;
	}

	/**
	 * Retrieves members from the Redis ZSet within the specified score range in reverse order.
	 * 
	 * <p>The method handles type conversion and generic type resolution to ensure the returned
	 * collection matches the expected return type of the annotated method.
	 * 
	 * <p>The score range defines the minimum and maximum scores (inclusive or exclusive) for member selection.
	 * For example, a score range of [1.0, 5.0] will return all members with scores between 1.0 and 5.0
	 * (inclusive) in reverse order (highest to lowest score).
	 * 
	 * @param context The operation context containing key and score range parameters
	 * @param valueField The field representing the value type, may be null
	 * @param returnArgumentType The expected return type from the annotated method
	 * @return A Collection of members within the specified score range in reverse order
	 * @throws Exception if the Redis operation fails or type conversion fails
	 */
	@Override
	public Collection doGet(OrangeRedisContext context, Field valueField, Type returnArgumentType) throws Exception {
		OrangeScoreRangeContext ctx = (OrangeScoreRangeContext)context;
		return this.operations.reverseRangeByScore(
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
	 *   <li>{@link ScoreRange} - Specifies the score range for filtering members
	 *   <li>{@link Reverse} - Indicates that members should be returned in reverse order
	 * </ul>
	 * 
	 * <p>The combination of these annotations allows the framework to identify methods that should
	 * retrieve members within a specified score range in reverse order. The Reverse annotation
	 * distinguishes this executor from {@link OrangeGetByScoreRangeExecutor}.
	 * 
	 * @return A List containing the supported annotation classes
	 */
	@Override
	protected List<Class<? extends Annotation>> getSupportedAnnotationClasses() {
		return OrangeCollectionUtils.asList(GetMembers.class,ScoreRange.class,Reverse.class);
	}

	/**
	 * Specifies the context class used by this executor.
	 * 
	 * <p>Note that even though this executor returns members in reverse order, it uses the same context class
	 * as {@link OrangeGetByScoreRangeExecutor} since the parameters needed to define the score range
	 * are the same. The difference is in the operation performed (reverseRangeByScore vs rangeByScore).
	 * 
	 * @return The OrangeScoreRangeContext class
	 */
	@Override
	public Class<? extends OrangeRedisContext> getContextClass() {
		return OrangeScoreRangeContext.class;
	}
	
	
}