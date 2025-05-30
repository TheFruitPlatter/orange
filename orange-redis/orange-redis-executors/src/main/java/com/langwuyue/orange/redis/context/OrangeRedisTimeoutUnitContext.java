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
import java.util.concurrent.TimeUnit;

import com.langwuyue.orange.redis.OrangeRedisException;
import com.langwuyue.orange.redis.RedisValueTypeEnum;
import com.langwuyue.orange.redis.annotation.OrangeRedisOperationArg;
import com.langwuyue.orange.redis.annotation.TimeoutUnit;

/**
 * Context class for Redis operations that require a timeout unit parameter.
 * 
 * <p>This class extends {@link OrangeRedisContext} to provide support for Redis operations
 * that need to specify a time unit for timeout values. It works in conjunction with
 * timeout values to determine the exact duration of expiration times.
 * 
 * <p>The timeout unit is specified using the {@link TimeoutUnit} annotation on a
 * {@link TimeUnit} parameter. This allows for flexible time unit specification in
 * Redis operations such as:
 * <ul>
 *   <li>SET with expiration</li>
 *   <li>EXPIRE commands</li>
 *   <li>Any other command requiring time unit specification</li>
 * </ul>
 *
 * @author Liang.Zhong
 * @since 1.0.0
 * @see TimeUnit
 * @see TimeoutUnit
 */
public class OrangeRedisTimeoutUnitContext extends OrangeRedisContext {
	
	/**
	 * The time unit for timeout operations.
	 * 
	 * <p>This field is automatically populated with the value of the method parameter
	 * annotated with {@link TimeoutUnit}. The value must be an instance of {@link TimeUnit}.
	 * 
	 * <p>The field is marked with {@link OrangeRedisOperationArg} to indicate that
	 * it should be bound to parameters annotated with {@link TimeoutUnit}.
	 */
	@OrangeRedisOperationArg(binding = TimeoutUnit.class)
	private Object unit;

	/**
	 * Constructs a new Redis timeout unit context with the specified parameters.
	 *
	 * @param operationOwner the class that owns the Redis operation method
	 * @param operationMethod the method representing the Redis operation
	 * @param args the arguments to be passed to the operation method
	 * @param redisKey the Redis key information
	 * @param valueType the type of Redis value being operated on
	 */
	public OrangeRedisTimeoutUnitContext(
		Class<?> operationOwner, 
		Method operationMethod, 
		Object[] args, 
		Key redisKey,
		RedisValueTypeEnum valueType
	) {
		super(operationOwner, operationMethod, args, redisKey, valueType);
	}
	
	/**
	 * Returns the validated time unit for timeout operations.
	 * 
	 * <p>This method performs several validations on the unit value:
	 * <ul>
	 *   <li>Checks that the value is not null</li>
	 *   <li>Verifies that the value is an instance of {@link TimeUnit}</li>
	 * </ul>
	 *
	 * @return the time unit specified by the {@link TimeoutUnit} annotation
	 * @throws OrangeRedisException if:
	 *         <ul>
	 *           <li>The unit value is null</li>
	 *           <li>The unit value is not an instance of {@link TimeUnit}</li>
	 *         </ul>
	 */
	public TimeUnit getUnit() {
		if(unit == null) {
			throw new OrangeRedisException(String.format("The argument annotated with @%s cannot be null", TimeoutUnit.class));
		}
		if(!(unit instanceof TimeUnit)) {
			throw new OrangeRedisException(String.format("The argument annotated with @%s must be a TimeUnit", TimeoutUnit.class));
		}
		return (TimeUnit) unit;
	}
	
}