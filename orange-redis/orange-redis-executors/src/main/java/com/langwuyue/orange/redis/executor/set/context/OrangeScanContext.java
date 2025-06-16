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
package com.langwuyue.orange.redis.executor.set.context;

import java.lang.reflect.Method;

import com.langwuyue.orange.redis.OrangeRedisException;
import com.langwuyue.orange.redis.RedisValueTypeEnum;
import com.langwuyue.orange.redis.annotation.OrangeRedisOperationArg;
import com.langwuyue.orange.redis.annotation.ScanPattern;
import com.langwuyue.orange.redis.annotation.zset.PageNo;
import com.langwuyue.orange.redis.context.OrangeRedisCountContext;
import com.langwuyue.orange.redis.utils.OrangeReflectionUtils;

/**
 * Context class for handling Redis scan operations.
 * 
 * <p>This class extends {@link OrangeRedisCountContext} to provide specific functionality
 * for Redis scan operations, which are used to incrementally iterate over a collection
 * of elements. It manages scan patterns and pagination parameters required for efficient
 * scanning of Redis data structures.
 * 
 * <p>The class processes {@link ScanPattern} and {@link PageNo} annotations to extract
 * the necessary parameters for scan operations, providing validation and type conversion
 * for these parameters.
 *
 * @author Liang.Zhong
 * @since 1.0.0
 */
public class OrangeScanContext extends OrangeRedisCountContext {

	/**
	 * The pattern used for scanning Redis keys or members.
	 * 
	 * <p>This field holds the pattern value bound from the {@link ScanPattern} annotation.
	 * The pattern follows Redis' glob-style pattern matching syntax (e.g., "user:*").
	 * It cannot be null when performing scan operations.
	 */
	@OrangeRedisOperationArg(binding = ScanPattern.class)
	private Object pattern;
	
	/**
	 * The page number for scan pagination.
	 * 
	 * <p>This field holds the page number value bound from the {@link PageNo} annotation.
	 * It represents the cursor position for Redis scan operations. The value must be
	 * a non-negative integer or a string that can be parsed to a non-negative integer.
	 */
	@OrangeRedisOperationArg(binding = PageNo.class)
	private Object pageNo;
	
	/**
	 * Constructs a new OrangeScanContext with the specified parameters.
	 * 
	 * <p>This constructor initializes the context with essential information needed for
	 * Redis scan operations. It passes all parameters to the parent class
	 * {@link OrangeRedisCountContext} constructor to establish the base context.
	 *
	 * @param operationOwner The class that owns the Redis operation method
	 * @param operationMethod The method that represents the Redis operation
	 * @param args The arguments passed to the operation method
	 * @param redisKey The Redis key to operate on
	 * @param valueType The type of values stored in the Redis data structure
	 */
	public OrangeScanContext(
		Class<?> operationOwner, 
		Method operationMethod, 
		Object[] args,
		Key redisKey,
		RedisValueTypeEnum valueType
	) {
		super(operationOwner, operationMethod, args, redisKey, valueType);
	}
	

	/**
	 * Gets the scan pattern as a string.
	 * 
	 * <p>Returns the pattern value that was bound from the {@link ScanPattern} annotation
	 * as a string. This pattern is used to filter Redis keys or members during scan operations.
	 * 
	 * <p>This method performs validation to ensure the pattern is not null, throwing an
	 * exception if the validation fails.
	 *
	 * @return the scan pattern as a string
	 * @throws OrangeRedisException if the pattern is null
	 */
	public String getPattern() {
		if(pattern == null) {
			throw new OrangeRedisException(String.format("The argument annotated with @%s cannot be null", ScanPattern.class));
		}
		return pattern.toString();
	}
	
	/**
	 * Gets the page number as a Long value.
	 * 
	 * <p>Returns the page number value that was bound from the {@link PageNo} annotation
	 * as a Long. This value represents the cursor position for Redis scan operations and
	 * is used for pagination.
	 * 
	 * <p>This method performs several validations:
	 * <ul>
	 *   <li>Ensures the page number is not null</li>
	 *   <li>Verifies the page number is either an integer type or a string that can be parsed to a Long</li>
	 *   <li>Confirms the page number is non-negative</li>
	 * </ul>
	 *
	 * @return the page number as a Long value
	 * @throws OrangeRedisException if the page number is null, not an integer or string, or negative
	 */
	public Long getPageNo() {
		if(this.pageNo == null) {
			throw new OrangeRedisException(String.format("The argument annotated with @%s cannot be null", PageNo.class));
		}
		if(!(OrangeReflectionUtils.isInteger(this.pageNo.getClass())) && !(this.pageNo instanceof String)) {
			throw new OrangeRedisException(String.format("The argument annotated with @%s must be a integer or a string", PageNo.class));
		}
		Long value = Long.valueOf(this.pageNo.toString());
		if(value < 0) {
			throw new OrangeRedisException(String.format("The argument annotated with @%s must be greater than zero", PageNo.class));
		}
		return value;
	}

}