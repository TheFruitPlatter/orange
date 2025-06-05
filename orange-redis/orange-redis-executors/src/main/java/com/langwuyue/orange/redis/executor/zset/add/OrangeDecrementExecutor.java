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

import com.langwuyue.orange.redis.annotation.Decrement;
import com.langwuyue.orange.redis.annotation.RedisValue;
import com.langwuyue.orange.redis.context.OrangeRedisContext;
import com.langwuyue.orange.redis.context.OrangeRedisValueContext;
import com.langwuyue.orange.redis.executor.OrangeRedisAbstractExecutor;
import com.langwuyue.orange.redis.mapping.OrangeRedisExecutorIdGenerator;
import com.langwuyue.orange.redis.operations.OrangeRedisZSetOperations;
import com.langwuyue.orange.redis.utils.OrangeCollectionUtils;

/**
 * Redis executor for decrementing scores in Redis Sorted Sets (ZSet).
 * 
 * <p>This executor handles the {@link Decrement} annotation and decrements
 * the score of a specified member in a Redis ZSet by 1. If the member doesn't
 * exist in the ZSet, it will be added with a score of -1.
 * 
 * 
 * <p>The return type can be Double, Long, Integer, or Float. The score will be
 * automatically converted to the appropriate return type.
 *
 * @author Liang.Zhong
 * @since 1.0.0
 */
public class OrangeDecrementExecutor extends OrangeRedisAbstractExecutor {

	/**
	 * Redis ZSet operations template for executing sorted set commands.
	 * Provides operations for manipulating scores in Redis sorted sets.
	 */
	private OrangeRedisZSetOperations operations;
	
	/**
	 * Constructs a new decrement executor for Redis ZSet operations.
	 *
	 * @param operations The Redis ZSet operations template to use for executing
	 *                  sorted set commands, particularly for score manipulation
	 * @param idGenerator Generator for creating unique executor IDs, used for
	 *                    tracking and debugging executor instances
	 */
	public OrangeDecrementExecutor(OrangeRedisZSetOperations operations,OrangeRedisExecutorIdGenerator idGenerator) {
		super(idGenerator);
		this.operations = operations;
	}

	/**
	 * Executes the decrement operation on a Redis ZSet member.
	 * 
	 * <p>This method decrements the score of the specified member in the ZSet by 1.
	 * If the member doesn't exist in the ZSet, it will be added with a score of -1.
	 * The result is automatically converted to match the method's return type.
	 *
	 * @param context The Redis operation context containing the key and member value
	 * @return Object The new score after decrementing, converted to the appropriate
	 *         return type (Double, Long, Integer, or Float)
	 * @throws Exception if the operation fails
	 */
	@Override
	public Object execute(OrangeRedisContext context) throws Exception {
		OrangeRedisValueContext ctx = (OrangeRedisValueContext)context;
		Double result = this.operations.incrementScore(ctx.getRedisKey().getValue(), ctx.getValue(), -1, context.getValueType());
		if(result == null) {
			return null;
		}
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
	 *           <li>{@link Decrement} - Marks methods that perform score decrement operations</li>
	 *           <li>{@link RedisValue} - Marks parameters that provide the member value</li>
	 *         </ul>
	 */
	@Override
	protected List<Class<? extends Annotation>> getSupportedAnnotationClasses() {
		return OrangeCollectionUtils.asList(Decrement.class,RedisValue.class);
	}

	/**
	 * Returns the context class used by this executor.
	 * 
	 * @return {@link OrangeRedisValueContext} class which contains the Redis key
	 *         and member value information needed for the decrement operation
	 */
	@Override
	public Class<? extends OrangeRedisContext> getContextClass() {
		return OrangeRedisValueContext.class;
	}

}