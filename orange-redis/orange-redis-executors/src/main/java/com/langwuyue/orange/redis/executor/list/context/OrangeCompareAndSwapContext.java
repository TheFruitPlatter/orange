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
package com.langwuyue.orange.redis.executor.list.context;

import java.lang.reflect.Method;

import com.langwuyue.orange.redis.OrangeRedisException;
import com.langwuyue.orange.redis.RedisValueTypeEnum;
import com.langwuyue.orange.redis.annotation.OrangeRedisOperationArg;
import com.langwuyue.orange.redis.annotation.RedisOldValue;

/**
 * Context class for Redis list Compare-And-Swap (CAS) operations.
 * 
 * <p>This context extends the basic set context to include an additional field
 * for the expected old value in a CAS operation. Compare-And-Swap is an atomic
 * operation that compares the contents of a memory location with a given value
 * and, only if they are the same, modifies the contents of that memory location
 * to a new given value.
 * 
 * <p>In the context of Redis lists, this allows for conditional updates where
 * an element is only modified if it currently matches an expected value, helping
 * to prevent race conditions in concurrent environments.
 * 
 * <p>This context class captures the old (expected) value from method parameters
 * annotated with {@link RedisOldValue}.
 *
 * @author Liang.Zhong
 * @since 1.0.0
 */
public class OrangeCompareAndSwapContext extends OrangeSetContext {
	
	/**
	 * The expected old value for the Compare-And-Swap operation.
	 * 
	 * <p>This field is bound to method parameters annotated with {@link RedisOldValue}
	 * and represents the value that must be present in the Redis list for the operation
	 * to succeed.
	 */
	@OrangeRedisOperationArg(binding = RedisOldValue.class)
	private Object oldValue;
	
	/**
	 * Constructs a new OrangeCompareAndSwapContext with the specified parameters.
	 *
	 * @param operationOwner the class that owns the Redis operation method
	 * @param operationMethod the method representing the Redis operation
	 * @param args the arguments passed to the operation method
	 * @param redisKey the Redis key to operate on
	 * @param valueType the type of values stored in the Redis list
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
	 * Retrieves the expected old value for the Compare-And-Swap operation.
	 *
	 * <p>This method returns the value that must be present in the Redis list
	 * for the CAS operation to proceed with the update. If the current value
	 * in Redis doesn't match this expected value, the operation will fail.
	 *
	 * @return the expected old value for comparison
	 * @throws OrangeRedisException if the old value is null, as CAS operations
	 *         require a non-null value for comparison
	 */
	public Object getOldValue() {
		if(oldValue == null) {
			throw new OrangeRedisException(String.format("The argument annotated with @%s cannot be null", RedisOldValue.class));
		}
		return oldValue;
	}
}