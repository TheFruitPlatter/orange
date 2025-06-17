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
import com.langwuyue.orange.redis.annotation.zset.Pager;
import com.langwuyue.orange.redis.annotation.zset.Reverse;
import com.langwuyue.orange.redis.annotation.zset.ScoreRange;
import com.langwuyue.orange.redis.context.OrangeRedisContext;
import com.langwuyue.orange.redis.executor.OrangeRedisGetAbstractExecutor;
import com.langwuyue.orange.redis.executor.zset.context.OrangeScoreRangePagerContext;
import com.langwuyue.orange.redis.mapping.OrangeRedisExecutorIdGenerator;
import com.langwuyue.orange.redis.operations.OrangeRedisZSetOperations;
import com.langwuyue.orange.redis.utils.OrangeCollectionUtils;

/**
 * Executor implementation for retrieving paginated members from a Redis Sorted Set (ZSet) 
 * using a score range, in reverse order (highest to lowest score).
 * 
 * <p>This executor combines the functionality of {@link OrangeGetByScoreRangePagerExecutor} and
 * {@link OrangeReverseByScoreRangeExecutor}, providing both pagination support and reverse ordering.
 * It supports the following annotations:
 * <ul>
 *   <li>{@link GetMembers} - Indicates this is a member retrieval operation
 *   <li>{@link ScoreRange} - Specifies both minimum and maximum scores for the range query
 *   <li>{@link Pager} - Provides pagination parameters (offset and count)
 *   <li>{@link Reverse} - Indicates that members should be returned in reverse order
 * </ul>
 * 
 * <p>The executor uses {@link OrangeRedisZSetOperations#reverseRangeByScore} with pagination parameters
 * to perform the actual Redis operation. It retrieves a subset of members whose scores are within 
 * the specified range, in reverse order, starting from the given offset and returning up to count members.
 * 
 * @author Liang.Zhong
 * @since 1.0.0
 * @see OrangeRedisGetAbstractExecutor
 * @see OrangeScoreRangePagerContext
 * @see OrangeRedisZSetOperations
 * @see <a href="https://orange.langwuyue.com/redis/advanced/zset">Orange Redis ZSet Documentation</a>
 */
public class OrangeReverseByScoreRangePagerExecutor extends OrangeRedisGetAbstractExecutor {
	
	/**
	 * Redis ZSet operations instance used to execute the actual Redis commands.
	 * This provides access to specialized sorted set operations including reverseRangeByScore with pagination.
	 */
	private OrangeRedisZSetOperations operations;

	/**
	 * Constructs a new executor for retrieving paginated ZSet members in reverse order using a score range.
	 * 
	 * @param operations The Redis ZSet operations implementation to use for executing commands
	 * @param idGenerator The ID generator for creating unique executor identifiers
	 */
	public OrangeReverseByScoreRangePagerExecutor(OrangeRedisZSetOperations operations,OrangeRedisExecutorIdGenerator idGenerator) {
		super(idGenerator);
		this.operations = operations;
	}

	/**
	 * Retrieves a paginated subset of members from the Redis ZSet within the specified score range in reverse order.
	 * 
	 * <p>The method handles type conversion and generic type resolution to ensure the returned
	 * collection matches the expected return type of the annotated method.
	 * 
	 * <p>The score range defines the minimum and maximum scores (inclusive or exclusive) for member selection.
	 * The pager defines the offset (how many members to skip) and count (maximum number of members to return).
	 * For example, a score range of [1.0, 5.0] with pager(10, 20) will skip the first 10 members and
	 * return up to 20 members with scores between 1.0 and 5.0 (inclusive) in descending score order
	 * (from highest to lowest score).
	 * 
	 * @param context The operation context containing key, score range, and pagination parameters
	 * @param valueField The field representing the value type, may be null
	 * @param returnArgumentType The expected return type from the annotated method
	 * @return A Collection of members within the specified score range in reverse order, limited by pagination parameters
	 * @throws Exception if the Redis operation fails or type conversion fails
	 */
	@Override
	public Collection doGet(OrangeRedisContext context, Field valueField, Type returnArgumentType) throws Exception {
		OrangeScoreRangePagerContext ctx = (OrangeScoreRangePagerContext)context;
		return this.operations.reverseRangeByScore(
				ctx.getRedisKey().getValue(), 
				ctx.getScoreRange(), 
				ctx.getPager(),
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
	 *   <li>{@link ScoreRange} - Specifies the score range for filtering members
	 *   <li>{@link Pager} - Provides pagination parameters (offset and count)
	 *   <li>{@link Reverse} - Indicates that members should be returned in reverse order
	 * </ul>
	 * 
	 * <p>The combination of these annotations allows the framework to identify methods that should
	 * retrieve a paginated subset of members within a specified score range in reverse order.
	 * The Reverse and Pager annotations together distinguish this executor from other score-based range executors.
	 * 
	 * @return A List containing the supported annotation classes
	 */
	@Override
	protected List<Class<? extends Annotation>> getSupportedAnnotationClasses() {
		return OrangeCollectionUtils.asList(GetMembers.class,ScoreRange.class,Pager.class,Reverse.class);
	}

	/**
	 * Specifies the context class used by this executor.
	 * 
	 * <p>The OrangeScoreRangePagerContext extends OrangeScoreRangeContext to add pagination support,
	 * allowing this executor to retrieve a subset of members within the specified score range in reverse order.
	 * 
	 * @return The OrangeScoreRangePagerContext class
	 */
	@Override
	public Class<? extends OrangeRedisContext> getContextClass() {
		return OrangeScoreRangePagerContext.class;
	}
	
	
}