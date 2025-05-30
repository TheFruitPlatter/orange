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

import com.langwuyue.orange.redis.RedisValueTypeEnum;
import com.langwuyue.orange.redis.annotation.OrangeRedisOperationArg;
import com.langwuyue.orange.redis.annotation.Timeout;
import com.langwuyue.orange.redis.context.builder.OrangeMethodAnnotationHandler;

/**
 * Context class for Redis operations that require a timeout parameter.
 * 
 * <p>This class extends {@link OrangeRedisContext} to provide support for Redis operations
 * that involve timeouts, such as:
 * <ul>
 *   <li>SET with expiration - when setting a key with a specific time-to-live</li>
 *   <li>EXPIRE - when explicitly setting a key's expiration time</li>
 *   <li>SETEX - when setting a value with expiration in a single operation</li>
 * </ul>
 * 
 * <p>The timeout information is extracted from the {@link Timeout} annotation,
 * which can be applied at the method level to specify both the timeout value
 * and the time unit.
 * 
 *
 * @author Liang.Zhong
 * @since 1.0.0
 */
public class OrangeRedisTimeoutContext extends OrangeRedisContext {

	/**
	 * The timeout configuration for the Redis operation.
	 * 
	 * <p>This field is populated with the {@link Timeout} annotation from the method level.
	 * The {@link OrangeRedisOperationArg} annotation is configured with:
	 * <ul>
	 *   <li>{@code binding = Timeout.class} - Binds to method-level {@link Timeout} annotations</li>
	 *   <li>{@code valueHandler = OrangeMethodAnnotationHandler.class} - Uses a special handler
	 *       to extract the annotation from the method rather than from a parameter</li>
	 * </ul>
	 */
	@OrangeRedisOperationArg(binding = Timeout.class, valueHandler = OrangeMethodAnnotationHandler.class)
	private Timeout timeout;
	
	/**
	 * Constructs a new Redis timeout operation context with the specified parameters.
	 *
	 * @param operationOwner the class that owns the Redis operation method
	 * @param operationMethod the method representing the Redis operation
	 * @param args the arguments to be passed to the operation method
	 * @param redisKey the Redis key information
	 * @param valueType the type of Redis value being operated on
	 */
	public OrangeRedisTimeoutContext(
		Class<?> operationOwner, 
		Method operationMethod, 
		Object[] args,
		Key redisKey,
		RedisValueTypeEnum valueType
	) {
		super(operationOwner,operationMethod,args,redisKey,valueType);
	}
	
	/**
	 * Returns the timeout value specified in the {@link Timeout} annotation.
	 * 
	 * <p>This value represents the duration of the timeout. The actual time unit
	 * for this value is specified by {@link #getTimeoutUnit()}.
	 *
	 * @return the timeout value as specified in the annotation
	 */
	public long getTimeout() {
		return timeout.value();
	}
	
	/**
	 * Returns the time unit for the timeout value.
	 * 
	 * <p>This unit is specified in the {@link Timeout} annotation and determines
	 * how the timeout value should be interpreted (e.g., seconds, minutes, hours).
	 *
	 * @return the {@link TimeUnit} specified in the annotation
	 */
	public TimeUnit getTimeoutUnit() {
		return timeout.unit();
	}
}