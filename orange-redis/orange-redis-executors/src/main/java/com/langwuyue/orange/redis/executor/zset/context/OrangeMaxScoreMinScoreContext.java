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
package com.langwuyue.orange.redis.executor.zset.context;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

import com.langwuyue.orange.redis.OrangeRedisException;
import com.langwuyue.orange.redis.RedisValueTypeEnum;
import com.langwuyue.orange.redis.annotation.OrangeRedisOperationArg;
import com.langwuyue.orange.redis.annotation.zset.MaxScore;
import com.langwuyue.orange.redis.annotation.zset.MinScore;
import com.langwuyue.orange.redis.context.OrangeRedisContext;

/**
 * Context class for Redis ZSet operations that involve maximum and minimum score bounds.
 * This class extends {@link OrangeRedisContext} to handle operations that require both
 * upper and lower score boundaries.
 *
 * <p>This context processes parameters annotated with {@link MaxScore} and {@link MinScore}
 * to define the score range for ZSet operations. It supports flexible score specification
 * through both numeric types and strings that can be parsed as numbers.
 *
 * <p>Both maxScore and minScore parameters support:
 * <ul>
 *   <li>Any numeric type (Integer, Long, Double, etc.)
 *   <li>Strings that can be parsed as numbers
 * </ul>
 *
 * @author Liang.Zhong
 * @since 1.0.0
 * @see MaxScore
 * @see MinScore
 */
public class OrangeMaxScoreMinScoreContext extends OrangeRedisContext {
	
	/**
	 * The maximum score bound, bound by {@link MaxScore} annotation.
	 * Can be either a number or a string that can be parsed as a number.
	 */
	@OrangeRedisOperationArg(binding = MaxScore.class)
	private Object maxScore;
	
	/**
	 * The minimum score bound, bound by {@link MinScore} annotation.
	 * Can be either a number or a string that can be parsed as a number.
	 */
	@OrangeRedisOperationArg(binding = MinScore.class)
	private Object minScore;

	/**
	 * Constructs a new max/min score context instance.
	 *
	 * @param operationOwner the class that owns the operation
	 * @param operationMethod the operation method
	 * @param args the method parameter array
	 * @param redisKey Redis key
	 * @param valueType Redis value type
	 */
	public OrangeMaxScoreMinScoreContext(
		Class<?> operationOwner, 
		Method operationMethod, 
		Object[] args, 
		Key redisKey,
		RedisValueTypeEnum valueType
	) {
		super(operationOwner, operationMethod, args, redisKey, valueType);
	}
	
	/**
	 * Gets the maximum score bound.
	 *
	 * @return the maximum score as a Double value
	 * @throws OrangeRedisException if the maxScore parameter is neither a number nor a parseable string
	 */
	public Double getMaxScore() {
		return getScore(maxScore,MaxScore.class);
	}
	
	/**
	 * Gets the minimum score bound.
	 *
	 * @return the minimum score as a Double value
	 * @throws OrangeRedisException if the minScore parameter is neither a number nor a parseable string
	 */
	public Double getMinScore() {
		return getScore(minScore,MinScore.class);
	}
	
	/**
	 * Converts a score parameter to a Double value.
	 *
	 * <p>This method handles the conversion of both numeric types and strings to Double values.
	 * It provides consistent error handling for both maximum and minimum score parameters.
	 *
	 * @param score the score value to convert
	 * @param annotationClass the annotation class ({@link MaxScore} or {@link MinScore}) for error messages
	 * @return the score as a Double value
	 * @throws OrangeRedisException if the score parameter is neither a number nor a parseable string
	 */
	protected Double getScore(Object score,Class<? extends Annotation> annotationClass) {
		if(score instanceof Number || score instanceof String) {
			return Double.valueOf(score.toString());
		}
		throw new OrangeRedisException(String.format("The argument annotated with @%s must be a number or a string", annotationClass));
	}

}