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
package com.langwuyue.orange.redis.executor.zset.add;

import java.lang.annotation.Annotation;
import java.util.List;

import com.langwuyue.orange.redis.annotation.Increment;
import com.langwuyue.orange.redis.annotation.RedisValue;
import com.langwuyue.orange.redis.annotation.zset.Score;
import com.langwuyue.orange.redis.context.OrangeRedisContext;
import com.langwuyue.orange.redis.executor.OrangeRedisAbstractExecutor;
import com.langwuyue.orange.redis.executor.zset.context.OrangeScoreContext;
import com.langwuyue.orange.redis.mapping.OrangeRedisExecutorIdGenerator;
import com.langwuyue.orange.redis.operations.OrangeRedisZSetOperations;
import com.langwuyue.orange.redis.utils.OrangeCollectionUtils;

/**
 * Redis executor for incrementing scores by a specified amount in Redis Sorted Sets (ZSet).
 * 
 * <p>This executor handles the {@link Increment} annotation combined with {@link Score} annotation
 * to increment the score of a specified member in a Redis ZSet by a given amount. If the member
 * doesn't exist in the ZSet, it will be added with the specified score.
 * 
 * <p>The return type can be Double, Long, Integer, or Float. The score will be
 * automatically converted to the appropriate return type.
 *
 * @author Liang.Zhong
 * @since 1.0.0
 */
public class OrangeIncrementWithScoresExecutor extends OrangeRedisAbstractExecutor {

	/**
	 * Redis ZSet operations template for executing sorted set commands.
	 * Provides operations for manipulating scores in Redis sorted sets.
	 */
	private OrangeRedisZSetOperations operations;
	
	/**
	 * Constructs a new increment with scores executor for Redis ZSet operations.
	 *
	 * @param operations The Redis ZSet operations template to use for executing
	 *                  sorted set commands, particularly for score manipulation
	 * @param idGenerator Generator for creating unique executor IDs, used for
	 *                    tracking and debugging executor instances
	 */
	public OrangeIncrementWithScoresExecutor(OrangeRedisZSetOperations operations,OrangeRedisExecutorIdGenerator idGenerator) {
		super(idGenerator);
		this.operations = operations;
	}

	/**
	 * Executes the increment operation on a Redis ZSet member with a specified score.
	 * 
	 * <p>This method increments the score of the specified member in the ZSet by the
	 * amount specified in the Score annotation. If the member doesn't exist in the ZSet,
	 * it will be added with the specified score.
	 * The result is automatically converted to match the method's return type.
	 *
	 * @param context The Redis operation context containing the key, member value and score
	 * @return Object The new score after incrementing, converted to the appropriate
	 *         return type (Double, Long, Integer, or Float)
	 * @throws Exception if the operation fails
	 */
	@Override
	public Object execute(OrangeRedisContext context) throws Exception {
		OrangeScoreContext ctx = (OrangeScoreContext) context;
		Double result = this.operations.incrementScore(ctx.getRedisKey().getValue(), ctx.getValue(), ctx.getScore(), context.getValueType());
		Class returnClass = context.getOperationMethod().getReturnType();
		if(returnClass == Long.class || returnClass == long.class) {
			return result.longValue();
		}
		if(returnClass == Integer.class || returnClass == int.class) {
			return result.intValue();
		}
		if(returnClass == Float.class || returnClass == float.class) {
			return result.floatValue();
		}
		return result;
	}
	
	/**
	 * Returns the list of annotations supported by this executor.
	 * 
	 * @return List containing:
	 *         <ul>
	 *           <li>{@link Increment} - Marks methods that perform score increment operations</li>
	 *           <li>{@link RedisValue} - Marks parameters that provide the member value</li>
	 *           <li>{@link Score} - Marks parameters that specify the increment amount</li>
	 *         </ul>
	 */
	@Override
	protected List<Class<? extends Annotation>> getSupportedAnnotationClasses() {
		return OrangeCollectionUtils.asList(Increment.class,RedisValue.class,Score.class);
	}

	/**
	 * Returns the context class used by this executor.
	 * 
	 * @return {@link OrangeScoreContext} class which contains the Redis key,
	 *         member value and score needed for the increment operation
	 */
	@Override
	public Class<? extends OrangeRedisContext> getContextClass() {
		return OrangeScoreContext.class;
	}

}