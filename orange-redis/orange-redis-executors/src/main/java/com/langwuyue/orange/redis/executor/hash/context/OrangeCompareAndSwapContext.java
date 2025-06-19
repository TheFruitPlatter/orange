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
package com.langwuyue.orange.redis.executor.hash.context;

import java.lang.reflect.Method;

import com.langwuyue.orange.redis.RedisValueTypeEnum;
import com.langwuyue.orange.redis.annotation.OrangeRedisOperationArg;
import com.langwuyue.orange.redis.annotation.RedisOldValue;

/**
 * Context class for Redis Compare-And-Swap (CAS) operations on hash fields.
 * 
 * <p>This context extends the basic hash key-value context by adding support for
 * the "old value" parameter required in CAS operations. The old value represents
 * the expected current value in the Redis hash that will be compared against before
 * performing the update.
 * 
 * <p>CAS operations provide atomic conditional updates by ensuring that a value is
 * only modified if it matches the expected old value, which helps prevent race conditions
 * in concurrent environments.
 *
 * @author Liang.Zhong
 * @since 1.0.0
 * @see com.langwuyue.orange.redis.executor.hash.OrangeCompareAndSwapExecutor
 */
public class OrangeCompareAndSwapContext extends OrangeHashKeyValueContext {
	
	/**
	 * The expected current value in the Redis hash for CAS operations.
	 * 
	 * <p>This field holds the value that will be compared against the actual value
	 * in the Redis hash before performing the update. The update will only proceed
	 * if the actual value matches this expected value.
	 * 
	 * <p>This field is bound to method parameters annotated with {@link RedisOldValue}.
	 */
	@OrangeRedisOperationArg(binding = RedisOldValue.class)
	private Object oldValue;
	
	/**
	 * Constructs a new OrangeCompareAndSwapContext with basic operation parameters.
	 * 
	 * <p>This constructor initializes the context with the operation metadata and Redis key information,
	 * but does not set the hash key, value, or old value. These must be set separately before
	 * performing a CAS operation.
	 *
	 * @param operationOwner the class that owns the Redis operation method
	 * @param operationMethod the method representing the Redis operation
	 * @param args the arguments passed to the operation method
	 * @param redisKey the Redis key to operate on
	 * @param valueType the type of value stored in the Redis hash
	 * @param keyType the type of key used in the Redis hash
	 */
	public OrangeCompareAndSwapContext(
		Class<?> operationOwner, 
		Method operationMethod, 
		Object[] args,
		Key redisKey,
		RedisValueTypeEnum valueType,
		RedisValueTypeEnum keyType
	) {
		super(operationOwner,operationMethod,args,redisKey,valueType,keyType);
	}
	
	/**
	 * Constructs a fully initialized OrangeCompareAndSwapContext with all required parameters.
	 * 
	 * <p>This constructor initializes the context with all parameters needed for a CAS operation,
	 * including the hash key, new value, and expected old value.
	 *
	 * @param operationOwner the class that owns the Redis operation method
	 * @param operationMethod the method representing the Redis operation
	 * @param args the arguments passed to the operation method
	 * @param redisKey the Redis key to operate on
	 * @param valueType the type of value stored in the Redis hash
	 * @param keyType the type of key used in the Redis hash
	 * @param hashKey the field name in the Redis hash
	 * @param value the new value to set if the CAS operation succeeds
	 * @param oldValue the expected current value in the Redis hash
	 */
	public OrangeCompareAndSwapContext(
		Class<?> operationOwner, 
		Method operationMethod, 
		Object[] args,
		Key redisKey,
		RedisValueTypeEnum valueType,
		RedisValueTypeEnum keyType,
		Object hashKey,
		Object value,
		Object oldValue
	) {
		super(operationOwner,operationMethod,args,redisKey,valueType,keyType,hashKey,value);
		this.oldValue = oldValue;
	}

	/**
	 * Gets the expected old value for the CAS operation.
	 * 
	 * <p>This method returns the value that will be compared against the actual value
	 * in the Redis hash before performing the update. The CAS operation will only
	 * proceed if the actual value matches this expected value.
	 *
	 * @return the expected old value in the Redis hash
	 */
	public Object getOldValue() {
		return oldValue;
	}
}