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
import com.langwuyue.orange.redis.annotation.RedisOldValue;

/**
 * Context class for Redis Compare-And-Swap (CAS) operations.
 * 
 * <p>This class extends {@link OrangeRedisValueContext} to provide support for atomic
 * Compare-And-Swap operations in Redis. CAS operations are used to update a value
 * only if it matches an expected old value, ensuring atomic updates in concurrent
 * environments.
 *
 * @author Liang.Zhong
 * @since 1.0.0
 */
public class OrangeCompareAndSwapContext extends OrangeRedisValueContext {
	
	/**
	 * The expected old value for the CAS operation.
	 * 
	 * <p>This field is automatically populated with the value of the method parameter
	 * annotated with {@link RedisOldValue}. It represents the expected current value
	 * in Redis that must match for the CAS operation to succeed.
	 */
	@OrangeRedisOperationArg(binding = RedisOldValue.class)
	private Object oldValue;
	
	/**
	 * Constructs a new CAS operation context with the specified parameters.
	 *
	 * @param operationOwner the class that owns the Redis operation method
	 * @param operationMethod the method representing the Redis operation
	 * @param args the arguments to be passed to the operation method
	 * @param redisKey the Redis key information
	 * @param valueType the type of Redis value being operated on
	 */
	public OrangeCompareAndSwapContext(
		Class<?> operationOwner, 
		Method operationMethod, 
		Object[] args,
		Key redisKey,
		RedisValueTypeEnum valueType
	) {
		super(operationOwner, operationMethod, args, redisKey,valueType);
	}

	/**
	 * Returns the expected old value for the CAS operation.
	 * 
	 * <p>This method ensures that the old value is not null before returning it.
	 * If the old value is null, it indicates that the method parameter annotated
	 * with {@link RedisOldValue} was not properly provided.
	 *
	 * @return the non-null old value for the CAS operation
	 * @throws OrangeRedisException if the old value is null
	 */
	public Object getOldValue() {
		if(oldValue == null) {
			throw new OrangeRedisException(String.format("The argument annotated with @%s cannot be null", RedisOldValue.class));
		}
		return oldValue;
	}
	
	/**
	 * Returns the old value for the CAS operation, which may be null.
	 * 
	 * <p>Unlike {@link #getOldValue()}, this method does not throw an exception
	 * if the old value is null. This can be useful in cases where null old values
	 * need to be handled specially.
	 *
	 * @return the old value for the CAS operation, may be null
	 */
	public Object getNullableOldValue() {
		return oldValue;
	}
}