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

import com.langwuyue.orange.redis.RedisValueTypeEnum;
import com.langwuyue.orange.redis.annotation.IfAbsent;
import com.langwuyue.orange.redis.annotation.OrangeRedisOperationArg;
import com.langwuyue.orange.redis.context.builder.OrangeMethodAnnotationHandler;

/**
 * Context class for handling Redis operations with if-absent semantics.
 * 
 * <p>This class extends {@link OrangeRedisValueContext} to provide support for
 * Redis operations that need to check if a key exists before performing the
 * operation. It's particularly useful for implementing atomic operations like:
 * <ul>
 *   <li>Setting a value only if the key doesn't exist (SET NX)</li>
 *   <li>Performing conditional operations based on key existence</li>
 *   <li>Implementing distributed locks with automatic cleanup</li>
 * </ul>
 * 
 * <p>The class processes the {@link IfAbsent} annotation which can be used to
 * specify whether the key should be automatically deleted after the operation
 * completes, making it useful for temporary locks or ephemeral data.
 * 
 *
 * @author Liang.Zhong
 * @since 1.0.0
 * @see IfAbsent
 * @see OrangeRedisValueContext
 * @see OrangeMethodAnnotationHandler
 */
public class OrangeRedisValueIfAbsentContext extends OrangeRedisValueContext {
	
	/**
	 * The if-absent configuration for the Redis operation.
	 * 
	 * <p>This field holds the {@link IfAbsent} annotation instance that was used
	 * to mark the Redis operation. It's processed by {@link OrangeMethodAnnotationHandler}
	 * to extract configuration such as whether to delete the key after the operation
	 * completes.
	 */
	@OrangeRedisOperationArg(binding = IfAbsent.class,valueHandler = OrangeMethodAnnotationHandler.class)
	private IfAbsent ifAbsent;

	/**
	 * Constructs a new Redis value if-absent context with the specified parameters.
	 * 
	 * <p>This constructor initializes the context with the necessary information
	 * for handling Redis operations that need if-absent semantics.
	 *
	 * @param operationOwner the class that owns the Redis operation method
	 * @param operationMethod the method representing the Redis operation
	 * @param args the arguments to be passed to the operation method
	 * @param redisKey the Redis key information
	 * @param valueType the type of Redis value being operated on
	 */
	public OrangeRedisValueIfAbsentContext(
		Class<?> operationOwner, 
		Method operationMethod, 
		Object[] args,
		Key redisKey,
		RedisValueTypeEnum valueType
	) {
		super(operationOwner, operationMethod, args, redisKey,valueType);
	}

	/**
	 * Checks if the key should be deleted after the operation completes.
	 * 
	 * <p>This method returns the value of the {@code deleteInTheEnd} parameter
	 * from the {@link IfAbsent} annotation. When true, it indicates that the key
	 * should be automatically removed from Redis after the operation completes,
	 * which is useful for implementing temporary locks or ephemeral data storage.
	 *
	 * @return true if the key should be deleted after the operation completes,
	 *         false otherwise
	 */
	public boolean isDeleteInTheEnd() {
		return ifAbsent.deleteInTheEnd();
	}
}