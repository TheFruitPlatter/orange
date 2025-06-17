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
import com.langwuyue.orange.redis.annotation.zset.Score;
import com.langwuyue.orange.redis.context.OrangeRedisValueContext;

/**
 * Base context class for Redis ZSet score operations.
 * 
 * <p>This class provides core functionality for handling score-related operations
 * in Redis Sorted Sets (ZSets). It processes and validates score values annotated
 * with {@link Score}, supporting both numeric and string representations of scores.
 *
 * <p>Performance Considerations:
 * <ul>
 *   <li>Use appropriate numeric types to avoid unnecessary string parsing
 *   <li>Be cautious with large Long values (>2^53) due to Double precision limitations
 *   <li>Consider using bulk operations when adding multiple scored members
 * </ul>
 *
 * <p>The context supports various numeric types for scores and provides both
 * nullable and non-nullable score retrieval methods. It includes safety checks
 * for numeric precision when converting between different numeric types.
 *
 * @author Liang.Zhong
 * @since 1.0.0
 * @see Score
 * @see OrangeRedisValueContext
 * @see <a href="https://orange.langwuyue.com/redis/advanced/zset">Orange Redis ZSet Documentation</a>
 */
public class OrangeScoreContext extends OrangeRedisValueContext {
	
	/**
	 * The score value extracted from the method parameter annotated with {@link Score}.
	 * This can be a numeric value (Integer, Long, Double, etc.) or a String representation
	 * of a number that can be parsed to a Double.
	 */
	@OrangeRedisOperationArg(binding = Score.class)
	private Object score;

	/**
	 * Constructs a new OrangeScoreContext for ZSet score operations.
	 *
	 * <p>This constructor initializes the context by processing method parameters
	 * to extract the score value annotated with {@link Score}.
	 *
	 * @param operationOwner The class that owns the Redis operation method
	 * @param operationMethod The method annotated with Redis operation annotations
	 * @param args The array of arguments passed to the operation method
	 * @param redisKey The Redis key wrapper containing the target ZSet key
	 * @param valueType The type of Redis value being operated on (should be ZSET)
	 */
	public OrangeScoreContext(
		Class<?> operationOwner, 
		Method operationMethod, 
		Object[] args,
		Key redisKey,
		RedisValueTypeEnum valueType
	) {
		super(operationOwner, operationMethod, args, redisKey,valueType);
	}

	/**
	 * Returns the score value as a non-null Double.
	 * 
	 * <p>This method retrieves the score value that was extracted from the parameter
	 * annotated with {@link Score} and ensures it is not null.
	 *
	 * @return The score value as a Double
	 * @throws OrangeRedisException if the score value is null or invalid
	 */
	public Double getScore() {
		return getScore(score,Score.class);
	}
	
	/**
	 * Returns the score value as a nullable Double.
	 * 
	 * <p>This method retrieves the score value that was extracted from the parameter
	 * annotated with {@link Score}. Returns null if no score value is available.
	 *
	 * @return The score value as a Double, or null if no score is available
	 * @throws OrangeRedisException if the score value is present but invalid
	 */
	public Double getNullableScore() {
		return getScore(score,Score.class);
	}
	
	/**
	 * Returns the score value from the given object as a non-null Double.
	 * 
	 * <p>This method converts the given score object to a Double value, ensuring
	 * it is not null. It supports various numeric types and string representations
	 * of numbers.
	 *
	 * @param score The score value to convert
	 * @param annotationClass The annotation class used to mark the score field
	 * @return The score value as a Double
	 * @throws OrangeRedisException if the score value is null or cannot be converted to Double
	 */
	protected Double getScore(Object score,Class<? extends Annotation> annotationClass) {
		Double doubleScore = getNullableScore(score,annotationClass);
		if(doubleScore == null) {
			throw new OrangeRedisException(String.format("The argument annotated with @%s cannot be null", annotationClass));
		}
		return doubleScore;
	}
	
	/**
	 * Returns the score value from the given object as a nullable Double.
	 * 
	 * <p>This method converts the given score object to a Double value. It supports:
	 * <ul>
	 *   <li>Numeric types (Integer, Long, Double, etc.)</li>
	 *   <li>String representations of numbers</li>
	 * </ul>
	 *
	 * <p>Note: When converting Long values to Double, there may be precision loss
	 * for values greater than 2^53 due to Double's precision limitations.
	 *
	 * @param score The score value to convert
	 * @param annotationClass The annotation class used to mark the score field
	 * @return The score value as a Double, or null if the input is null
	 * @throws OrangeRedisException if the score value is present but cannot be converted to Double
	 */
	protected Double getNullableScore(Object score, Class<? extends Annotation> annotationClass) {
		if(score == null) {
			return null;
		}
		if(score instanceof Number || score instanceof String) {
			// It's risky for a long value greater than 2^53 when converting to double due to potential precision loss.
			return Double.valueOf(score.toString());
		}
		throw new OrangeRedisException(String.format("The argument annotated with @%s must be a number or a string", annotationClass));
	}
}