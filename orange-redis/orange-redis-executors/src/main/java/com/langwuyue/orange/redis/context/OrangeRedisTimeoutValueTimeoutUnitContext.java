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
import com.langwuyue.orange.redis.annotation.OrangeRedisOperationArg;
import com.langwuyue.orange.redis.annotation.TimeoutValue;
import com.langwuyue.orange.redis.utils.OrangeReflectionUtils;

/**
 * Context class that combines timeout value and unit handling for Redis operations.
 * 
 * <p>This class extends {@link OrangeRedisTimeoutUnitContext} to provide a comprehensive
 * context for Redis operations that require both a timeout value and its corresponding
 * time unit. It's particularly useful for operations such as:
 * <ul>
 *   <li>SET with explicit timeout value and unit</li>
 *   <li>EXPIRE with custom duration</li>
 *   <li>Any Redis command that needs both duration and time unit specification</li>
 * </ul>
 * 
 * <p>The class handles two distinct annotations:
 * <ul>
 *   <li>{@link TimeoutValue} - for the timeout duration value</li>
 *   <li>{@link com.langwuyue.orange.redis.annotation.TimeoutUnit} - for the time unit (inherited from parent class)</li>
 * </ul>
 * 
 *
 * @author Liang.Zhong
 * @since 1.0.0
 * @see OrangeRedisTimeoutUnitContext
 * @see TimeoutValue
 * @see com.langwuyue.orange.redis.annotation.TimeoutUnit
 */
public class OrangeRedisTimeoutValueTimeoutUnitContext extends OrangeRedisTimeoutUnitContext {
	
	/**
	 * The timeout value for Redis operations.
	 * 
	 * <p>This field is automatically populated with the value of the method parameter
	 * annotated with {@link TimeoutValue}. The value can be either:
	 * <ul>
	 *   <li>An integer type (int, long, etc.)</li>
	 *   <li>A string that can be parsed to a long value</li>
	 * </ul>
	 * 
	 * <p>The field is marked with {@link OrangeRedisOperationArg} to indicate that
	 * it should be bound to parameters annotated with {@link TimeoutValue}.
	 */
	@OrangeRedisOperationArg(binding = TimeoutValue.class)
	private Object value;
	
	/**
	 * Constructs a new Redis timeout value and unit context with the specified parameters.
	 * 
	 * <p>This constructor extends the functionality of {@link OrangeRedisTimeoutUnitContext}
	 * by adding support for timeout value handling.
	 *
	 * @param operationOwner the class that owns the Redis operation method
	 * @param operationMethod the method representing the Redis operation
	 * @param args the arguments to be passed to the operation method
	 * @param redisKey the Redis key information
	 * @param valueType the type of Redis value being operated on
	 */
	public OrangeRedisTimeoutValueTimeoutUnitContext(
		Class<?> operationOwner, 
		Method operationMethod, 
		Object[] args, 
		Key redisKey,
		RedisValueTypeEnum valueType
	) {
		super(operationOwner, operationMethod, args, redisKey, valueType);
	}
	
	/**
	 * Returns the validated timeout value as a Long.
	 * 
	 * <p>This method performs several validations on the timeout value:
	 * <ul>
	 *   <li>Checks that the value is not null</li>
	 *   <li>Verifies that the value is either an integer type or a string</li>
	 *   <li>Converts the value to a Long, regardless of its original type</li>
	 * </ul>
	 *
	 * @return the timeout value as a Long
	 * @throws OrangeRedisException if:
	 *         <ul>
	 *           <li>The timeout value is null</li>
	 *           <li>The timeout value is neither an integer type nor a string</li>
	 *           <li>The timeout value cannot be parsed to a Long (if it's a string)</li>
	 *         </ul>
	 */
	public Long getValue() {
		if(value == null) {
			throw new OrangeRedisException(String.format("The argument annotated with @%s cannot be null", TimeoutValue.class));
		}
		if(!(OrangeReflectionUtils.isInteger(value.getClass())) && !(value instanceof String)) {
			throw new OrangeRedisException(String.format("The argument annotated with @%s must be a integer or a string", TimeoutValue.class));
		}
		return Long.valueOf(value.toString());
	}
}