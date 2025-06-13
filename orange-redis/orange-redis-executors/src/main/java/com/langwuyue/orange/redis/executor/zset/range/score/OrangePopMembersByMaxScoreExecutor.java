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
import com.langwuyue.orange.redis.annotation.PopMembers;
import com.langwuyue.orange.redis.annotation.zset.MaxScore;
import com.langwuyue.orange.redis.context.OrangeRedisContext;
import com.langwuyue.orange.redis.context.OrangeRedisCountContext;
import com.langwuyue.orange.redis.executor.zset.OrangeGetWithScoresAbstractExecutor;
import com.langwuyue.orange.redis.mapping.OrangeRedisExecutorIdGenerator;
import com.langwuyue.orange.redis.operations.OrangeRedisZSetOperations;
import com.langwuyue.orange.redis.utils.OrangeCollectionUtils;

/**
 * Executor implementation for popping (removing and returning) multiple members with the highest scores from a Redis ZSet.
 * This executor removes and returns a specified number of members with the maximum scores from the sorted set.
 * 
 * This is particularly useful in scenarios where you need to process items in reverse order of priority,
 * such as task queues where higher scores represent higher priorities or more recent timestamps.
 *
 * @author Liang.Zhong
 * @since 1.0.0
 */
public class OrangePopMembersByMaxScoreExecutor extends OrangeGetWithScoresAbstractExecutor {
	
	/**
	 * Redis ZSet operations instance used to perform ZSet-specific operations.
	 */
	private OrangeRedisZSetOperations operations;

	/**
	 * Constructs a new executor for popping multiple highest-scored members from a Redis ZSet.
	 *
	 * @param operations the Redis ZSet operations instance to use for executing ZSet commands
	 * @param idGenerator the ID generator for creating unique executor identifiers
	 */
	public OrangePopMembersByMaxScoreExecutor(OrangeRedisZSetOperations operations,OrangeRedisExecutorIdGenerator idGenerator) {
		super(idGenerator);
		this.operations = operations;
	}

	/**
	 * Executes the Redis ZSet pop operation to remove and return multiple members with the highest scores.
	 * 
	 * This method casts the context to OrangeRedisCountContext to access the count parameter,
	 * then calls the operations.popMaxScore method with the specified count to retrieve multiple members.
	 *
	 * @param context the Redis operation context containing the key and count parameters
	 * @param valueField the field representing the value type in the target object, may be null
	 * @param returnArgumentType the expected return type for the operation
	 * @return a Collection containing the popped members with their scores
	 * @throws Exception if an error occurs during the Redis operation
	 */
	@Override
	public Collection doGet(OrangeRedisContext context, Field valueField, Type returnArgumentType) throws Exception {
		OrangeRedisCountContext ctx = (OrangeRedisCountContext)context;
		return this.operations.popMaxScore(
				ctx.getRedisKey().getValue(), 
				ctx.getCount(), 
				ctx.getValueType(), 
				valueField == null ? returnArgumentType : valueField.getGenericType()
		);
	}

	/**
	 * Returns a list of annotation classes that this executor supports.
	 * 
	 * @return a list containing the PopMembers and MaxScore annotation classes that this executor can process
	 */
	@Override
	protected List<Class<? extends Annotation>> getSupportedAnnotationClasses() {
		return OrangeCollectionUtils.asList(PopMembers.class,MaxScore.class,Count.class);
	}

	/**
	 * Returns the context class used by this executor.
	 * 
	 * @return the OrangeRedisCountContext class, which contains the necessary
	 *         parameters for executing ZSet pop operations (key and count parameters)
	 */
	@Override
	public Class<? extends OrangeRedisContext> getContextClass() {
		return OrangeRedisCountContext.class;
	}
	
	
}