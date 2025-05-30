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

import java.lang.reflect.Method;

import com.langwuyue.orange.redis.OrangeRedisException;
import com.langwuyue.orange.redis.RedisValueTypeEnum;
import com.langwuyue.orange.redis.annotation.EndIndex;
import com.langwuyue.orange.redis.annotation.OrangeRedisOperationArg;
import com.langwuyue.orange.redis.annotation.StartIndex;
import com.langwuyue.orange.redis.utils.OrangeReflectionUtils;

/**
 * Context class for handling Redis operations that require start and end indices.
 * 
 * <p>This class provides support for operations that need to specify a range using
 * start and end indices, such as retrieving a subset of elements from a Redis list
 * or getting a range of characters from a string. It handles the parsing and validation
 * of parameters marked with {@link StartIndex} and {@link EndIndex} annotations.
 * 
 * <p>Both start and end indices can be specified as either integers or strings that
 * can be parsed as integers. The class provides validation to ensure that:
 * <ul>
 *   <li>Neither index is null</li>
 *   <li>Both indices are either integers or strings that can be parsed as integers</li>
 * </ul>
 * 
 *
 * @author Liang.Zhong
 * @since 1.0.0
 * @see StartIndex
 * @see EndIndex
 */
public class OrangeRedisStartIndexEndIndexContext extends OrangeRedisContext {
	
	/**
	 * The start index for range operations.
	 * 
	 * <p>This field is automatically populated with the value of the method parameter
	 * annotated with {@link StartIndex}. The value can be either an integer or a
	 * string that can be parsed as an integer.
	 */
	@OrangeRedisOperationArg(binding = StartIndex.class)
	private Object startIndex;
	
	/**
	 * The end index for range operations.
	 * 
	 * <p>This field is automatically populated with the value of the method parameter
	 * annotated with {@link EndIndex}. The value can be either an integer or a
	 * string that can be parsed as an integer.
	 */
	@OrangeRedisOperationArg(binding = EndIndex.class)
	private Object endIndex;

	/**
	 * Constructs a new Redis start-end index context with the specified parameters.
	 * 
	 * <p>This constructor initializes the context with the necessary information
	 * for handling range operations that require start and end indices.
	 *
	 * @param operationOwner the class that owns the Redis operation method
	 * @param operationMethod the method representing the Redis operation
	 * @param args the arguments to be passed to the operation method
	 * @param redisKey the Redis key information
	 * @param valueType the type of Redis value being operated on
	 */
	public OrangeRedisStartIndexEndIndexContext(
		Class<?> operationOwner, 
		Method operationMethod, 
		Object[] args, 
		Key redisKey,
		RedisValueTypeEnum valueType
	) {
		super(operationOwner, operationMethod, args, redisKey, valueType);
	}
	
	/**
	 * Gets the validated start index for the range operation.
	 * 
	 * <p>This method validates and converts the start index value to a Long.
	 * The start index must be either an integer or a string that can be parsed
	 * as an integer.
	 *
	 * @return the start index as a Long value
	 * @throws OrangeRedisException if the start index is null or not a valid integer value
	 */
	public Long getStartIndex() {
		if(startIndex == null) {
			throw new OrangeRedisException(String.format("The argument annotated with @%s cannot be null", StartIndex.class));
		}
		if(!(OrangeReflectionUtils.isInteger(startIndex.getClass())) && !(startIndex instanceof String)) {
			throw new OrangeRedisException(String.format("The argument annotated with @%s must be a integer or a string", StartIndex.class));
		}
		return Long.valueOf(startIndex.toString());
	}
	
	/**
	 * Gets the validated end index for the range operation.
	 * 
	 * <p>This method validates and converts the end index value to a Long.
	 * The end index must be either an integer or a string that can be parsed
	 * as an integer.
	 *
	 * @return the end index as a Long value
	 * @throws OrangeRedisException if the end index is null or not a valid integer value
	 */
	public Long getEndIndex() {
		if(endIndex == null) {
			throw new OrangeRedisException(String.format("The argument annotated with @%s cannot be null", EndIndex.class));
		}
		if(!(OrangeReflectionUtils.isInteger(endIndex.getClass())) && !(endIndex instanceof String)) {
			throw new OrangeRedisException(String.format("The argument annotated with @%s must be a integer or a string", EndIndex.class));
		}
		return Long.valueOf(endIndex.toString());
	}

}