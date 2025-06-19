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

import com.langwuyue.orange.redis.OrangeRedisException;
import com.langwuyue.orange.redis.RedisValueTypeEnum;
import com.langwuyue.orange.redis.annotation.OrangeRedisOperationArg;
import com.langwuyue.orange.redis.annotation.hash.HashKey;

/**
 * Redis hash key operation context, used to handle operations with @HashKey annotation.
 * 
 * <p>Extends {@link OrangeHashContext}, adding binding and processing for hash key values.
 *
 * @author Liang.Zhong
 * @since 1.0.0
 */
public class OrangeHashKeyContext extends OrangeHashContext {
	
	/**
	 * Hash key value bound with @HashKey annotation
	 */
	@OrangeRedisOperationArg(binding = HashKey.class)
	private Object hashKey;
	
	/**
	 * Constructs a new Redis hash key operation context instance
	 *
	 * @param operationOwner the class that owns the operation
	 * @param operationMethod the operation method
	 * @param args the method arguments
	 * @param redisKey the Redis key
	 * @param valueType the value type
	 * @param keyType the key type
	 */
	public OrangeHashKeyContext(
		Class<?> operationOwner, 
		Method operationMethod, 
		Object[] args, 
		Key redisKey,
		RedisValueTypeEnum valueType,
		RedisValueTypeEnum keyType
	) {
		super(operationOwner, operationMethod, args, redisKey, valueType,keyType);
	}
	
	/**
	 * Gets the bound hash key value
	 * 
	 * @return the bound hash key value object
	 */
	public Object getHashKey() {
		if(hashKey == null) {
			throw new OrangeRedisException(String.format("The argument annotated with @%s cannot be null", HashKey.class));
		}
		return hashKey;
	}
	
}