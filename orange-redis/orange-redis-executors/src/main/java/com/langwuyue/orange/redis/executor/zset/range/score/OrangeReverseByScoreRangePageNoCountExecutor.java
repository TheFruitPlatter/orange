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
import com.langwuyue.orange.redis.annotation.zset.PageNo;
import com.langwuyue.orange.redis.annotation.zset.Reverse;
import com.langwuyue.orange.redis.annotation.zset.ScoreRange;
import com.langwuyue.orange.redis.context.OrangeRedisContext;
import com.langwuyue.orange.redis.executor.OrangeRedisGetAbstractExecutor;
import com.langwuyue.orange.redis.executor.zset.context.OrangeScoreRangePagNoCountContext;
import com.langwuyue.orange.redis.mapping.OrangeRedisExecutorIdGenerator;
import com.langwuyue.orange.redis.operations.OrangeRedisZSetOperations;
import com.langwuyue.orange.redis.operations.OrangeRedisZSetOperations.Pager;
import com.langwuyue.orange.redis.utils.OrangeCollectionUtils;

/**
 * Executor implementation for retrieving paginated members from a Redis ZSet in reverse order based on a score range,
 * using page number for pagination without calculating the total count.
 * 
 * This executor retrieves members whose scores fall within the specified range (defined by ScoreRange),
 * and returns them in reverse order (from highest to lowest score) with pagination based on page number.
 * It does not calculate the total count of matching members, which can improve performance for large sets.
 * 
 * This is particularly useful for implementing paginated leaderboards, time-based feeds,
 * or any scenario requiring paginated access to sorted data in reverse order where the total count is not needed.
 * 
 * The executor supports the following annotations:
 * - GetMembers: Indicates this is a member retrieval operation
 * - ScoreRange: Specifies the score range to filter members
 * - PageNo: Specifies the page number for pagination
 * - Count: Specifies the number of items per page
 * - Reverse: Indicates that results should be returned in reverse order
 *
 * @author Liang.Zhong
 * @since 1.0.0
 */
public class OrangeReverseByScoreRangePageNoCountExecutor extends OrangeRedisGetAbstractExecutor {
	
	/**
	 * Redis ZSet operations instance used to perform ZSet-specific operations.
	 */
	private OrangeRedisZSetOperations operations;

	/**
	 * Constructs a new executor for retrieving paginated members from a Redis ZSet in reverse order
	 * based on a score range, using page number for pagination without calculating the total count.
	 *
	 * @param operations the Redis ZSet operations instance to use for executing ZSet commands
	 * @param idGenerator the ID generator for creating unique executor identifiers
	 */
	public OrangeReverseByScoreRangePageNoCountExecutor(OrangeRedisZSetOperations operations,OrangeRedisExecutorIdGenerator idGenerator) {
		super(idGenerator);
		this.operations = operations;
	}

	/**
	 * Retrieves a paginated set of members from a Redis ZSet in reverse order within the specified score range.
	 * 
	 * This method casts the context to OrangeScoreRangePagNoCountContext to access the score range and pagination parameters,
	 * then calls the operations.reverseRangeByScore method to retrieve members whose scores fall within the specified range
	 * in reverse order. The results are paginated based on the page number and count parameters.
	 *
	 * @param context the Redis operation context containing the key, score range, and pagination parameters
	 * @param valueField the field representing the value type, used for type conversion
	 * @param returnArgumentType the expected return type for the collection elements
	 * @return a paginated collection of members from the sorted set that fall within the specified score range, in reverse order
	 * @throws Exception if an error occurs during the Redis operation
	 */
	@Override
	public Collection doGet(OrangeRedisContext context, Field valueField, Type returnArgumentType) throws Exception {
		OrangeScoreRangePagNoCountContext ctx = (OrangeScoreRangePagNoCountContext)context;
		return this.operations.reverseRangeByScore(
				ctx.getRedisKey().getValue(), 
				ctx.getScoreRange(), 
				new Pager(ctx.getPageNo(),ctx.getCount()),
				ctx.getValueType(), 
				valueField == null ? returnArgumentType : valueField.getGenericType()
		);
	}

	/**
	 * Returns a list of annotation classes that this executor supports.
	 * 
	 * @return a list containing GetMembers, ScoreRange, PageNo, and Count annotation classes
	 *         that this executor can process for retrieving paginated members within a score range
	 *         in reverse order without calculating total count
	 */
	@Override
	protected List<Class<? extends Annotation>> getSupportedAnnotationClasses() {
		return OrangeCollectionUtils.asList(GetMembers.class,ScoreRange.class,PageNo.class,Count.class,Reverse.class);
	}

	/**
	 * Returns the context class used by this executor.
	 * 
	 * @return the OrangeScoreRangePagNoCountContext class, which contains the necessary
	 *         parameters for executing ZSet range operations with score range and pagination
	 *         without calculating the total count
	 */
	@Override
	public Class<? extends OrangeRedisContext> getContextClass() {
		return OrangeScoreRangePagNoCountContext.class;
	}
	
	
}