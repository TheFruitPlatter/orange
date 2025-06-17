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
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.langwuyue.orange.redis.annotation.PopMembers;
import com.langwuyue.orange.redis.annotation.TimeoutUnit;
import com.langwuyue.orange.redis.annotation.TimeoutValue;
import com.langwuyue.orange.redis.annotation.zset.MaxScore;
import com.langwuyue.orange.redis.context.OrangeRedisContext;
import com.langwuyue.orange.redis.context.OrangeRedisTimeoutValueTimeoutUnitContext;
import com.langwuyue.orange.redis.executor.zset.OrangeGetOneWithScoresAbstractExecutor;
import com.langwuyue.orange.redis.mapping.OrangeRedisExecutorIdGenerator;
import com.langwuyue.orange.redis.operations.OrangeRedisZSetOperations;
import com.langwuyue.orange.redis.operations.OrangeRedisZSetOperations.ZSetEntry;
import com.langwuyue.orange.redis.utils.OrangeCollectionUtils;

/**
 * Executor implementation for popping a member with the highest score from a Redis ZSet with a time limit.
 * 
 * This executor removes and returns the member with the highest score from a sorted set, waiting up to
 * a specified timeout if no element is initially available. This is useful for implementing priority queues,
 * scheduled tasks, or any scenario where you need to process items in order of priority (score) with a
 * blocking behavior when no items are available.
 * 
 * The executor supports the following annotations:
 * - PopMembers: Indicates this is a member removal operation
 * - MaxScore: Specifies that the member with the maximum score should be popped
 * - TimeoutValue: Specifies the timeout value for the blocking operation
 * - TimeoutUnit: Specifies the unit of the timeout value
 *
 * @author Liang.Zhong
 * @since 1.0.0
 * @see <a href="https://orange.langwuyue.com/redis/advanced/zset">Orange Redis ZSet Documentation</a>
 */
public class OrangeTimeLimitedPopMemberByMaxScoreExecutor extends OrangeGetOneWithScoresAbstractExecutor {
	
	/**
	 * Redis ZSet operations instance used to perform ZSet-specific operations.
	 */
	private OrangeRedisZSetOperations operations;

	/**
	 * Constructs a new executor for popping a member with the highest score from a Redis ZSet with a time limit.
	 *
	 * @param operations the Redis ZSet operations instance to use for executing ZSet commands
	 * @param idGenerator the ID generator for creating unique executor identifiers
	 */
	public OrangeTimeLimitedPopMemberByMaxScoreExecutor(OrangeRedisZSetOperations operations,OrangeRedisExecutorIdGenerator idGenerator) {
		super(idGenerator);
		this.operations = operations;
	}

	/**
	 * Pops (removes and returns) the member with the highest score from a Redis ZSet with a time limit.
	 * 
	 * This method casts the context to OrangeRedisTimeoutValueTimeoutUnitContext to access the key, timeout value,
	 * and timeout unit parameters, then calls the operations.popMaxScore method to remove and return the member
	 * with the highest score. If no member is available, the method will block until either a member becomes
	 * available or the specified timeout is reached.
	 *
	 * @param context the Redis operation context containing the key and timeout parameters
	 * @param valueField the field representing the value type, used for type conversion
	 * @param returnArgumentType the expected return type for the collection elements
	 * @return a collection containing the popped ZSetEntry (member and score), or an empty collection if no member is available within the timeout
	 * @throws Exception if an error occurs during the Redis operation
	 */
	@Override
	public Collection doGet(OrangeRedisContext context, Field valueField, Type returnArgumentType) throws Exception {
		OrangeRedisTimeoutValueTimeoutUnitContext ctx = (OrangeRedisTimeoutValueTimeoutUnitContext)context;
		ZSetEntry entry = this.operations.popMaxScore(
				ctx.getRedisKey().getValue(), 
				ctx.getValue(),
				ctx.getUnit(),
				ctx.getValueType(), 
				valueField == null ? returnArgumentType : valueField.getGenericType()
		);
		if(entry == null) {
			return new ArrayList<>();
		}
		return OrangeCollectionUtils.asList(entry);
	}

	/**
	 * Returns a list of annotation classes that this executor supports.
	 * 
	 * @return a list containing PopMembers, MaxScore, TimeoutValue, and TimeoutUnit annotation classes
	 *         that this executor can process for popping a member with the highest score with a time limit
	 */
	@Override
	protected List<Class<? extends Annotation>> getSupportedAnnotationClasses() {
		return OrangeCollectionUtils.asList(PopMembers.class,MaxScore.class,TimeoutValue.class,TimeoutUnit.class);
	}

	/**
	 * Returns the context class used by this executor.
	 * 
	 * @return the OrangeRedisTimeoutValueTimeoutUnitContext class, which contains the necessary
	 *         parameters for executing time-limited pop operations, including the key, timeout value,
	 *         and timeout unit
	 */
	@Override
	public Class<? extends OrangeRedisContext> getContextClass() {
		return OrangeRedisTimeoutValueTimeoutUnitContext.class;
	}
	
	
}