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
package com.langwuyue.orange.redis.context;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import com.langwuyue.orange.redis.OrangeRedisException;
import com.langwuyue.orange.redis.RedisValueTypeEnum;
import com.langwuyue.orange.redis.annotation.EndIndex;
import com.langwuyue.orange.redis.annotation.OrangeRedisOperationArg;
import com.langwuyue.orange.redis.annotation.StartIndex;
import com.langwuyue.orange.redis.annotation.zset.RankRange;
import com.langwuyue.orange.redis.utils.OrangeReflectionUtils;

/**
 * Context class for handling rank range operations in Redis Sorted Sets.
 * 
 * <p>This class provides support for operations that require a range of ranks
 * (positions) within a Redis Sorted Set, such as retrieving elements by their
 * position in the sorted set. It handles the parsing and validation of rank
 * range parameters marked with the {@link RankRange} annotation.
 * 
 * <p>The class supports two ways of specifying rank ranges:
 * <ul>
 *   <li>Using a {@link com.langwuyue.orange.redis.operations.OrangeRedisZSetOperations.RankRange}
 *       object directly</li>
 *   <li>Using a custom object with fields annotated with {@link StartIndex} and
 *       {@link EndIndex}</li>
 * </ul>
 *
 * @author Liang.Zhong
 * @since 1.0.0
 * @see com.langwuyue.orange.redis.operations.OrangeRedisZSetOperations.RankRange
 * @see RankRange
 * @see StartIndex
 * @see EndIndex
 */
public class OrangeRedisRankRangeContext extends OrangeRedisContext {
	
	/**
	 * The rank range object for Redis sorted set operations.
	 * 
	 * <p>This field is automatically populated with the value of the method parameter
	 * annotated with {@link RankRange}. It can be either:
	 * <ul>
	 *   <li>A {@link com.langwuyue.orange.redis.operations.OrangeRedisZSetOperations.RankRange} object</li>
	 *   <li>A custom object with fields annotated with {@link StartIndex} and {@link EndIndex}</li>
	 * </ul>
	 * 
	 * <p>The field is marked with {@link OrangeRedisOperationArg} to indicate that
	 * it should be bound to parameters annotated with {@link RankRange}.
	 */
	@OrangeRedisOperationArg(binding = RankRange.class)
	private Object rankRange;

	/**
	 * Constructs a new Redis rank range context with the specified parameters.
	 * 
	 * <p>This constructor initializes the context with the necessary information
	 * for handling rank range operations on Redis sorted sets.
	 *
	 * @param operationOwner the class that owns the Redis operation method
	 * @param operationMethod the method representing the Redis operation
	 * @param args the arguments to be passed to the operation method
	 * @param redisKey the Redis key information
	 * @param valueType the type of Redis value being operated on
	 */
	public OrangeRedisRankRangeContext(
		Class<?> operationOwner, 
		Method operationMethod, 
		Object[] args,
		Key redisKey,
		RedisValueTypeEnum valueType
	) {
		super(operationOwner, operationMethod, args, redisKey,valueType);
	}

	/**
	 * Extracts and returns the rank range for Redis sorted set operations.
	 * 
	 * <p>This method processes the object annotated with {@link RankRange} and
	 * converts it to a {@link com.langwuyue.orange.redis.operations.OrangeRedisZSetOperations.RankRange}
	 * object that can be used in Redis operations. The method handles two cases:
	 * 
	 * <ol>
	 *   <li>If the object is already a {@code RankRange}, it is returned directly</li>
	 *   <li>If the object is a custom class, it looks for fields annotated with
	 *       {@link StartIndex} and {@link EndIndex}, extracts their values, and
	 *       creates a new {@code RankRange} object</li>
	 * </ol>
	 * 
	 * <p>The method performs extensive validation to ensure that:
	 * <ul>
	 *   <li>The rank range object is not null</li>
	 *   <li>The custom object has both {@code StartIndex} and {@code EndIndex} annotated fields</li>
	 *   <li>The start and end index values are not null</li>
	 *   <li>The start and end index values are either integers or strings that can be parsed as integers</li>
	 * </ul>
	 *
	 * @return a {@link com.langwuyue.orange.redis.operations.OrangeRedisZSetOperations.RankRange} object
	 * @throws OrangeRedisException if the rank range is null, missing required annotations,
	 *         or contains invalid values
	 */
	public com.langwuyue.orange.redis.operations.OrangeRedisZSetOperations.RankRange getRankRange() {
		if(rankRange == null) {
			throw new OrangeRedisException(String.format("The argument annotated @%s cannot be null", RankRange.class));
		}
		if(rankRange instanceof com.langwuyue.orange.redis.operations.OrangeRedisZSetOperations.RankRange) {
			return (com.langwuyue.orange.redis.operations.OrangeRedisZSetOperations.RankRange)rankRange;
		}
		Field startField = null;
		Field endField = null;
		Field[] fields = rankRange.getClass().getDeclaredFields();
		for(Field field : fields) {
			if(field.isAnnotationPresent(StartIndex.class)) {
				startField = field;
			}
			else if(field.isAnnotationPresent(EndIndex.class)) {
				endField = field;
			}
			if(startField != null && endField != null) {
				break;
			}
		}
		if(startField == null || endField == null) {
			throw new OrangeRedisException(String.format("The argument annotated @%s must have two fields annotated with @%s and @%s", RankRange.class,StartIndex.class,EndIndex.class));
		}
		Object startIndex = OrangeReflectionUtils.getFieldValue(startField, rankRange);
		if(startIndex == null) {
			throw new OrangeRedisException(String.format("The field annotated @%s cannot be null", StartIndex.class));
		}
		if(!(OrangeReflectionUtils.isInteger(startIndex.getClass())) && !(startIndex instanceof String)) {
			throw new OrangeRedisException(String.format("The field annotated @%s must be a integer or a string", StartIndex.class));
		}
		Object endIndex = OrangeReflectionUtils.getFieldValue(endField, rankRange);
		if(endIndex == null) {
			throw new OrangeRedisException(String.format("The field annotated @%s cannot be null", EndIndex.class));
		}
		if(!(OrangeReflectionUtils.isInteger(endIndex.getClass())) && !(endIndex instanceof String)) {
			throw new OrangeRedisException(String.format("The field annotated @%s must be a integer or a string", EndIndex.class));
		}
		return new com.langwuyue.orange.redis.operations.OrangeRedisZSetOperations.RankRange(
			Long.valueOf(startIndex.toString()),
			Long.valueOf(endIndex.toString())
		);
	}
}