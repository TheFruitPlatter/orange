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
import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.List;

import com.langwuyue.orange.redis.annotation.EndIndex;
import com.langwuyue.orange.redis.annotation.GetMembers;
import com.langwuyue.orange.redis.annotation.StartIndex;
import com.langwuyue.orange.redis.annotation.zset.Reverse;
import com.langwuyue.orange.redis.annotation.zset.WithScores;
import com.langwuyue.orange.redis.context.OrangeRedisContext;
import com.langwuyue.orange.redis.context.OrangeRedisStartIndexEndIndexContext;
import com.langwuyue.orange.redis.executor.zset.OrangeGetWithScoresAbstractExecutor;
import com.langwuyue.orange.redis.mapping.OrangeRedisExecutorIdGenerator;
import com.langwuyue.orange.redis.operations.OrangeRedisZSetOperations;
import com.langwuyue.orange.redis.operations.OrangeRedisZSetOperations.RankRange;
import com.langwuyue.orange.redis.utils.OrangeCollectionUtils;

/**
 * Executor implementation for retrieving members with their scores from a Redis Sorted Set (ZSet) 
 * within a specified range using start and end indices, in reverse order (highest to lowest score).
 * 
 * <p>This executor combines the functionality of {@link OrangeReverseByStartIndexEndIndexExecutor} and
 * {@link OrangeGetByStartIndexEndIndexWithScoresExecutor}, providing both reverse ordering and score inclusion.
 * It supports the following annotations:
 * <ul>
 *   <li>{@link GetMembers} - Indicates this is a member retrieval operation
 *   <li>{@link StartIndex} - Specifies the starting position (inclusive) for the range query
 *   <li>{@link EndIndex} - Specifies the ending position (inclusive) for the range query
 *   <li>{@link WithScores} - Indicates that scores should be included with the returned members
 *   <li>{@link Reverse} - Indicates that members should be returned in reverse order (highest to lowest score)
 * </ul>
 * 
 * <p>The executor uses {@link OrangeRedisZSetOperations#reverseRangeWithScores} to perform the actual Redis operation
 * by converting the start and end indices into a {@link RankRange} object.
 * It retrieves all members whose ranks are within the specified range [start, end], along with their scores,
 * in reverse order.
 * 
 * @author Liang.Zhong
 * @since 1.0.0
 * @see OrangeGetWithScoresAbstractExecutor
 * @see OrangeRedisStartIndexEndIndexContext
 * @see OrangeRedisZSetOperations
 * @see OrangeRedisZSetOperations.TypedTuple
 */
public class OrangeReverseByStartIndexEndIndexWithScoresExecutor extends OrangeGetWithScoresAbstractExecutor {
	
	/**
	 * Redis ZSet operations instance used to execute the actual Redis commands.
	 * This provides access to specialized sorted set operations including reverseRangeWithScores.
	 */
	private OrangeRedisZSetOperations operations;

	/**
	 * Constructs a new executor for retrieving ZSet members with scores in reverse order using start and end indices.
	 * 
	 * @param operations The Redis ZSet operations implementation to use for executing commands
	 * @param idGenerator The ID generator for creating unique executor identifiers
	 */
	public OrangeReverseByStartIndexEndIndexWithScoresExecutor(OrangeRedisZSetOperations operations,OrangeRedisExecutorIdGenerator idGenerator) {
		super(idGenerator);
		this.operations = operations;
	}

	/**
	 * Retrieves members with their scores from the Redis ZSet within the specified index range in reverse order.
	 * 
	 * <p>The method handles type conversion and generic type resolution to ensure the returned
	 * collection matches the expected return type of the annotated method. The return type should be
	 * a collection of {@link OrangeRedisZSetOperations.TypedTuple} objects or a compatible type that
	 * can hold both the member value and its score.
	 * 
	 * <p>The start and end indices define an inclusive range of positions in the sorted set.
	 * For example, start=0 and end=4 will return the first 5 elements with the highest scores
	 * (positions 0,1,2,3,4 in reverse order) along with their scores.
	 * 
	 * @param context The operation context containing key and index parameters
	 * @param valueField The field representing the value type, may be null
	 * @param returnArgumentType The expected return type from the annotated method
	 * @return A Collection of TypedTuple objects containing members and their scores within the specified index range in reverse order
	 * @throws Exception if the Redis operation fails or type conversion fails
	 */
	@Override
	public Collection doGet(OrangeRedisContext context, Field valueField, Type returnArgumentType) throws Exception {
		OrangeRedisStartIndexEndIndexContext ctx = (OrangeRedisStartIndexEndIndexContext)context;
		return this.operations.reverseRangeWithScores(
			ctx.getRedisKey().getValue(), 
			new RankRange(ctx.getStartIndex(),ctx.getEndIndex()), 
			ctx.getValueType(), 
			valueField == null ? returnArgumentType : valueField.getGenericType()
		);
	}

	/**
	 * Specifies which annotations this executor supports.
	 * 
	 * <p>The combination of these annotations allows the framework to identify methods that should
	 * retrieve members with their scores from within a specified index range in reverse order.
	 * The WithScores and Reverse annotations together distinguish this executor from the other
	 * rank-based range executors.
	 * 
	 * @return A List containing the supported annotation classes
	 */
	@Override
	protected List<Class<? extends Annotation>> getSupportedAnnotationClasses() {
		return OrangeCollectionUtils.asList(GetMembers.class,StartIndex.class,EndIndex.class,WithScores.class,Reverse.class);
	}

	/**
	 * Specifies the context class used by this executor.
	 * 
	 * <p>Note that even though this executor returns members with scores in reverse order, it uses the same context class
	 * as the other rank-based range executors since the parameters needed to define the range
	 * are the same. The difference is in the operation performed (reverseRangeWithScores vs rangeWithScores vs range)
	 * and the return type (TypedTuple vs member value).
	 * 
	 * @return The OrangeRedisStartIndexEndIndexContext class
	 */
	@Override
	public Class<? extends OrangeRedisContext> getContextClass() {
		return OrangeRedisStartIndexEndIndexContext.class;
	}
	
	
}