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

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import com.langwuyue.orange.redis.OrangeRedisException;
import com.langwuyue.orange.redis.RedisValueTypeEnum;
import com.langwuyue.orange.redis.annotation.OrangeRedisOperationArg;
import com.langwuyue.orange.redis.annotation.zset.MaxScore;
import com.langwuyue.orange.redis.annotation.zset.MinScore;
import com.langwuyue.orange.redis.annotation.zset.ScoreRange;
import com.langwuyue.orange.redis.context.OrangeRedisContext;
import com.langwuyue.orange.redis.utils.OrangeReflectionUtils;

/**
 * Score range query context class, used for handling score range based queries in Redis ZSet.
 * This class supports two ways to specify the score range:
 * <ul>
 *   <li>Using a predefined ScoreRange object
 *   <li>Using a custom object with {@link MaxScore} and {@link MinScore} annotations
 * </ul>
 *
 * @author Liang.Zhong
 * @since 1.0.0
 * @see ScoreRange
 * @see MaxScore
 * @see MinScore
 * @see com.langwuyue.orange.redis.operations.OrangeRedisZSetOperations.ScoreRange
 */
public class OrangeScoreRangeContext extends OrangeRedisContext {
	
	/**
	 * Score range object, bound by {@link ScoreRange} annotation.
	 * Can be either a predefined ScoreRange type or a custom type with {@link MaxScore} and {@link MinScore} annotations.
	 */
	@OrangeRedisOperationArg(binding = ScoreRange.class)
	private Object scoreRange;

	/**
	 * Constructs a new score range query context instance.
	 *
	 * @param operationOwner the class that owns the operation
	 * @param operationMethod the operation method
	 * @param args the method parameter array
	 * @param redisKey Redis key
	 * @param valueType Redis value type
	 */
	public OrangeScoreRangeContext(
		Class<?> operationOwner, 
		Method operationMethod, 
		Object[] args,
		Key redisKey,
		RedisValueTypeEnum valueType
	) {
		super(operationOwner, operationMethod, args, redisKey,valueType);
	}

	/**
	 * Gets the score range object.
	 * This method supports two types of input:
	 * <ul>
	 *   <li>Predefined ScoreRange object
	 *   <li>Custom object with {@link MaxScore} and {@link MinScore} annotations
	 * </ul>
	 *
	 * <p>For custom objects, this method will:
	 * <ul>
	 *   <li>Find fields with {@link MaxScore} and {@link MinScore} annotations
	 *   <li>Validate field value types (must be numeric or string parsable to number)
	 *   <li>Convert field values to Double type
	 *   <li>Create and return a new ScoreRange object
	 * </ul>
	 *
	 * @return the standardized ScoreRange object
	 * @throws OrangeRedisException if:
	 *         <ul>
	 *           <li>Score range object is null
	 *           <li>Custom object is missing required annotated fields
	 *           <li>Max or min score fields are null
	 *           <li>Score field values have incorrect types
	 *         </ul>
	 */
	public com.langwuyue.orange.redis.operations.OrangeRedisZSetOperations.ScoreRange getScoreRange() {
		if(scoreRange == null) {
			throw new OrangeRedisException(String.format("The argument annotated with @%s cannot be null", ScoreRange.class));
		}
		if(scoreRange instanceof com.langwuyue.orange.redis.operations.OrangeRedisZSetOperations.ScoreRange) {
			return (com.langwuyue.orange.redis.operations.OrangeRedisZSetOperations.ScoreRange)scoreRange;
		}
		Field maxScoreField = null;
		Field minScoreField = null;
		Field[] fields = scoreRange.getClass().getDeclaredFields();
		for(Field field : fields) {
			if(field.isAnnotationPresent(MaxScore.class)) {
				maxScoreField = field;
			}
			else if(field.isAnnotationPresent(MinScore.class)) {
				minScoreField = field;
			}
			if(maxScoreField != null && minScoreField != null) {
				break;
			}
		}
		if(maxScoreField == null || minScoreField == null) {
			throw new OrangeRedisException(String.format("The argument annotated with @%s must have two fields annotated with @%s and @%s", ScoreRange.class,MaxScore.class,MinScore.class));
		}
		Object maxScore = OrangeReflectionUtils.getFieldValue(maxScoreField, scoreRange);
		if(maxScore == null) {
			throw new OrangeRedisException(String.format("The field annotated with @%s cannot be null", MaxScore.class));
		}
		if(!(maxScore instanceof Number) && !(maxScore instanceof String)) {
			throw new OrangeRedisException(String.format("The field annotated with @%s must be a number or a string", MaxScore.class));
		}
		Object minScore = OrangeReflectionUtils.getFieldValue(minScoreField, scoreRange);
		if(minScore == null) {
			throw new OrangeRedisException(String.format("The field annotated with @%s cannot be null", MinScore.class));
		}
		if(!(minScore instanceof Number) && !(minScore instanceof String)) {
			throw new OrangeRedisException(String.format("The field annotated with @%s must be a number or a string", MinScore.class));
		}
		return new com.langwuyue.orange.redis.operations.OrangeRedisZSetOperations.ScoreRange(
			Double.valueOf(maxScore.toString()),
			Double.valueOf(minScore.toString())
		);
	}
}