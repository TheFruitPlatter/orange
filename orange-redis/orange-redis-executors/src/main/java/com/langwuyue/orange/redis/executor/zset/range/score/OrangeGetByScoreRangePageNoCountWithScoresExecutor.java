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
import com.langwuyue.orange.redis.annotation.zset.ScoreRange;
import com.langwuyue.orange.redis.annotation.zset.WithScores;
import com.langwuyue.orange.redis.context.OrangeRedisContext;
import com.langwuyue.orange.redis.executor.zset.OrangeGetWithScoresAbstractExecutor;
import com.langwuyue.orange.redis.executor.zset.context.OrangeScoreRangePagNoCountContext;
import com.langwuyue.orange.redis.mapping.OrangeRedisExecutorIdGenerator;
import com.langwuyue.orange.redis.operations.OrangeRedisZSetOperations;
import com.langwuyue.orange.redis.operations.OrangeRedisZSetOperations.Pager;
import com.langwuyue.orange.redis.utils.OrangeCollectionUtils;

/**
 * Executor implementation for retrieving members with their scores from a Redis ZSet within a specified score range.
 * This executor supports pagination through traditional page number and count parameters, and includes score information
 * in the results. It's particularly useful when both the members and their scores are needed for the application logic.
 *
 * @author Liang.Zhong
 * @since 1.0.0
 */
public class OrangeGetByScoreRangePageNoCountWithScoresExecutor extends OrangeGetWithScoresAbstractExecutor {
	
	/**
	 * Redis ZSet operations instance used to perform ZSet-specific operations with score information.
	 */
	private OrangeRedisZSetOperations operations;

	/**
	 * Constructs a new executor for retrieving members with their scores from a Redis ZSet within a specified score range.
	 *
	 * @param operations the Redis ZSet operations instance to use for executing ZSet commands
	 * @param idGenerator the ID generator for creating unique executor identifiers
	 */
	public OrangeGetByScoreRangePageNoCountWithScoresExecutor(OrangeRedisZSetOperations operations,OrangeRedisExecutorIdGenerator idGenerator) {
		super(idGenerator);
		this.operations = operations;
	}

	/**
	 * Executes the Redis ZSet range query operation to retrieve members with their scores within the specified score range.
	 * The results are paginated based on the page number and count parameters provided in the context.
	 *
	 * @param context the Redis operation context containing key, score range, page number and count parameters
	 * @param valueField the field representing the value type in the target object, may be null
	 * @param returnArgumentType the expected return type for the operation
	 * @return a Collection of members with their scores from the ZSet that fall within the specified score range and pagination parameters
	 * @throws Exception if an error occurs during the Redis operation
	 */
	@Override
	public Collection doGet(OrangeRedisContext context, Field valueField, Type returnArgumentType) throws Exception {
		OrangeScoreRangePagNoCountContext ctx = (OrangeScoreRangePagNoCountContext)context;
		return this.operations.rangeByScoreWithScores(
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
	 * @return a list containing the annotation classes that this executor can process:
	 *         GetMembersWithScores, MinScore, MaxScore, PageNo, and Count
	 */
	@Override
	protected List<Class<? extends Annotation>> getSupportedAnnotationClasses() {
		return OrangeCollectionUtils.asList(GetMembers.class,ScoreRange.class,WithScores.class,PageNo.class,Count.class);
	}

	/**
	 * Returns the context class used by this executor.
	 * 
	 * @return the OrangeScoreRangePageNoCountContext class, which contains the necessary
	 *         parameters for executing ZSet range queries with score boundaries, page number and count
	 */
	@Override
	public Class<? extends OrangeRedisContext> getContextClass() {
		return OrangeScoreRangePagNoCountContext.class;
	}
	
	
}