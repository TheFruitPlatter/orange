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
import com.langwuyue.orange.redis.annotation.RedisValue;

/**
 * Base context class for Redis operations that involve value manipulation.
 * 
 * <p>This class extends {@link OrangeRedisContext} to provide support for Redis operations
 * that require a value parameter, such as SET, SETNX, or other value-based operations.
 * It manages the value to be stored or compared in Redis operations.
 * 
 *
 * @author Liang.Zhong
 * @since 1.0.0
 */
public class OrangeRedisValueContext extends OrangeRedisContext {
	
	/**
	 * The value to be used in the Redis operation.
	 * 
	 * <p>This field is automatically populated with the value of the method parameter
	 * annotated with {@link RedisValue}. It represents the value to be stored or
	 * compared in Redis operations.
	 */
	@OrangeRedisOperationArg(binding = RedisValue.class)
	private Object value;

	/**
	 * Constructs a new Redis value operation context with the specified parameters.
	 *
	 * @param operationOwner the class that owns the Redis operation method
	 * @param operationMethod the method representing the Redis operation
	 * @param args the arguments to be passed to the operation method
	 * @param redisKey the Redis key information
	 * @param valueType the type of Redis value being operated on
	 */
	public OrangeRedisValueContext(
		Class<?> operationOwner, 
		Method operationMethod, 
		Object[] args,
		Key redisKey,
		RedisValueTypeEnum valueType
	) {
		super(operationOwner, operationMethod, args, redisKey,valueType);
	}

	/**
	 * Returns the value for the Redis operation.
	 * 
	 * <p>This method ensures that the value is not null before returning it.
	 * If the value is null, it indicates that the method parameter annotated
	 * with {@link RedisValue} was not properly provided.
	 *
	 * @return the non-null value for the Redis operation
	 * @throws OrangeRedisException if the value is null
	 */
	public Object getValue() {
		if(value == null) {
			throw new OrangeRedisException(String.format("The argument annotated with @%s cannot be null", RedisValue.class));
		}
		return value;
	}
	
	/**
	 * Returns the value for the Redis operation, which may be null.
	 * 
	 * <p>Unlike {@link #getValue()}, this method does not throw an exception
	 * if the value is null. This can be useful in cases where null values
	 * need to be handled specially.
	 *
	 * @return the value for the Redis operation, may be null
	 */
	public Object getNullableValue() {
		return value;
	}
}