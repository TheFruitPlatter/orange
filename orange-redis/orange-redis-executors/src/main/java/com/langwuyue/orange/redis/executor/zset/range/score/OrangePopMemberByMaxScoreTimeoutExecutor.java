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

import com.langwuyue.orange.redis.annotation.PopMembers;
import com.langwuyue.orange.redis.annotation.Timeout;
import com.langwuyue.orange.redis.annotation.zset.MaxScore;
import com.langwuyue.orange.redis.context.OrangeRedisContext;
import com.langwuyue.orange.redis.context.OrangeRedisTimeoutContext;
import com.langwuyue.orange.redis.executor.zset.OrangeGetOneWithScoresAbstractExecutor;
import com.langwuyue.orange.redis.mapping.OrangeRedisExecutorIdGenerator;
import com.langwuyue.orange.redis.operations.OrangeRedisZSetOperations;
import com.langwuyue.orange.redis.operations.OrangeRedisZSetOperations.ZSetEntry;
import com.langwuyue.orange.redis.utils.OrangeCollectionUtils;

/**
 * Executor implementation for popping (removing and returning) the member with the highest score from a Redis ZSet
 * with a timeout parameter. This executor removes and returns a single member with the maximum score from the sorted set,
 * but will wait only for the specified timeout period if no suitable member is immediately available.
 * 
 * This is particularly useful in scenarios where you need to process items in order of priority with a time constraint,
 * such as task queues where higher scores represent higher priorities and processing cannot wait indefinitely.
 *
 * @author Liang.Zhong
 * @since 1.0.0
 */
public class OrangePopMemberByMaxScoreTimeoutExecutor extends OrangeGetOneWithScoresAbstractExecutor {
	
	/**
	 * Redis ZSet operations instance used to perform ZSet-specific operations with timeout support.
	 */
	private OrangeRedisZSetOperations operations;

	/**
	 * Constructs a new executor for popping the highest-scored member from a Redis ZSet with timeout support.
	 *
	 * @param operations the Redis ZSet operations instance to use for executing ZSet commands
	 * @param idGenerator the ID generator for creating unique executor identifiers
	 */
	public OrangePopMemberByMaxScoreTimeoutExecutor(OrangeRedisZSetOperations operations,OrangeRedisExecutorIdGenerator idGenerator) {
		super(idGenerator);
		this.operations = operations;
	}

	/**
	 * Executes the Redis ZSet pop operation to remove and return the member with the highest score,
	 * waiting up to the specified timeout if no suitable member is immediately available.
	 * 
	 * This method casts the context to OrangeRedisTimeoutContext to access timeout parameters,
	 * then calls the operations.popMaxScore method with appropriate timeout settings.
	 *
	 * @param context the Redis operation context containing the key and timeout parameters
	 * @param valueField the field representing the value type in the target object, may be null
	 * @param returnArgumentType the expected return type for the operation
	 * @return a Collection containing the popped member with its score (typically a singleton collection)
	 * @throws Exception if an error occurs during the Redis operation
	 */
	@Override
	public Collection doGet(OrangeRedisContext context, Field valueField, Type returnArgumentType) throws Exception {
		OrangeRedisTimeoutContext ctx = (OrangeRedisTimeoutContext) context;
		ZSetEntry entry = this.operations.popMaxScore(
				context.getRedisKey().getValue(), 
				ctx.getTimeout(),
				ctx.getTimeoutUnit(),
				context.getValueType(), 
				valueField == null ? returnArgumentType : valueField.getGenericType()
		);
		return OrangeCollectionUtils.asList(entry);
	}

	/**
	 * Returns a list of annotation classes that this executor supports.
	 * 
	 * @return a list containing the PopMemberByMaxScoreTimeout annotation class that this executor can process
	 */
	@Override
	protected List<Class<? extends Annotation>> getSupportedAnnotationClasses() {
		return OrangeCollectionUtils.asList(PopMembers.class,MaxScore.class,Timeout.class);
	}

	/**
	 * Returns the context class used by this executor.
	 * 
	 * @return the OrangeRedisTimeoutContext class, which contains the necessary
	 *         parameters for executing ZSet pop operations with timeout (key and timeout parameters)
	 */
	@Override
	public Class<? extends OrangeRedisContext> getContextClass() {
		return OrangeRedisTimeoutContext.class;
	}

	
}