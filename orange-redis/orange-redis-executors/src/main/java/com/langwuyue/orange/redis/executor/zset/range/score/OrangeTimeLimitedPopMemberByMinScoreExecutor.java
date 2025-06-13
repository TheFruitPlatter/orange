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
import com.langwuyue.orange.redis.annotation.zset.MinScore;
import com.langwuyue.orange.redis.context.OrangeRedisContext;
import com.langwuyue.orange.redis.context.OrangeRedisTimeoutValueTimeoutUnitContext;
import com.langwuyue.orange.redis.executor.zset.OrangeGetOneWithScoresAbstractExecutor;
import com.langwuyue.orange.redis.mapping.OrangeRedisExecutorIdGenerator;
import com.langwuyue.orange.redis.operations.OrangeRedisZSetOperations;
import com.langwuyue.orange.redis.operations.OrangeRedisZSetOperations.ZSetEntry;
import com.langwuyue.orange.redis.utils.OrangeCollectionUtils;


/**
 * Executor implementation for popping a member with the minimum score from a Redis ZSet
 * with a specified timeout duration.
 * 
 * This executor atomically removes and returns the member with the lowest score from a sorted set.
 * If no member is available within the specified timeout period, it returns an empty collection.
 * This operation is particularly useful for implementing time-limited priority queues or
 * delayed task processing systems where tasks are ordered by priority (score).
 * 
 * The executor supports the following annotations:
 * - PopMembers: Indicates this is a member removal operation
 * - MinScore: Specifies the minimum score threshold for members to be considered
 * - TimeoutValue: Specifies the timeout duration value
 * - TimeoutUnit: Specifies the unit for the timeout duration (e.g., seconds, milliseconds)
 *
 * @author Liang.Zhong
 * @since 1.0.0
 */
public class OrangeTimeLimitedPopMemberByMinScoreExecutor extends OrangeGetOneWithScoresAbstractExecutor {
	
	/**
	 * Redis ZSet operations instance used to perform ZSet-specific operations,
	 * particularly the atomic pop operation with minimum score and timeout.
	 */
	private OrangeRedisZSetOperations operations;

	/**
	 * Constructs a new executor for popping a member with the minimum score from a Redis ZSet
	 * with a specified timeout duration.
	 *
	 * @param operations the Redis ZSet operations instance to use for executing ZSet commands
	 * @param idGenerator the ID generator for creating unique executor identifiers
	 */
	public OrangeTimeLimitedPopMemberByMinScoreExecutor(OrangeRedisZSetOperations operations,OrangeRedisExecutorIdGenerator idGenerator) {
		super(idGenerator);
		this.operations = operations;
	}

	/**
	 * Pops and returns the member with the minimum score from a Redis ZSet with a timeout limit.
	 * 
	 * The operation is atomic, ensuring that the member is both retrieved and removed from the sorted set
	 * in a single operation, which is critical for implementing reliable distributed queues.
	 *
	 * @param context the context containing the Redis key, minimum score threshold, timeout value, and timeout unit
	 * @param valueField the field representing the value type, may be null
	 * @param returnArgumentType the expected return type for the collection elements
	 * @return Collection containing a single ZSetEntry with the popped member and its score, or an empty collection if no member was available within the timeout period
	 * @throws Exception if an error occurs during the Redis operation or type conversion
	 */
	@Override
	public Collection doGet(OrangeRedisContext context, Field valueField, Type returnArgumentType) throws Exception {
		OrangeRedisTimeoutValueTimeoutUnitContext ctx = (OrangeRedisTimeoutValueTimeoutUnitContext)context;
		ZSetEntry entry = this.operations.popMinScore(
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
	 * Returns the list of annotation classes that this executor supports.
	 * 
	 * This executor supports the following annotations:
	 * - PopMembers: For indicating that this operation removes members from a Redis ZSet
	 * - MinScore: For specifying the minimum score threshold for members to be considered
	 * - TimeoutValue: For specifying the timeout duration value
	 * - TimeoutUnit: For specifying the unit for the timeout duration (e.g., seconds, milliseconds)
	 *
	 * @return List of supported annotation classes including PopMembers, MinScore, TimeoutValue, and TimeoutUnit
	 */
	@Override
	protected List<Class<? extends Annotation>> getSupportedAnnotationClasses() {
		return OrangeCollectionUtils.asList(PopMembers.class,MinScore.class,TimeoutValue.class,TimeoutUnit.class);
	}
	/**
	 * Returns the context class used by this executor.
	 * 
	 * This context is specifically designed for operations that require both a score threshold
	 * and a timeout duration, making it suitable for implementing time-limited priority queues
	 * or delayed task processing systems.
	 *
	 * @return The OrangeRedisTimeoutValueTimeoutUnitContext class
	 */
	@Override
	public Class<? extends OrangeRedisContext> getContextClass() {
		return OrangeRedisTimeoutValueTimeoutUnitContext.class;
	}
}