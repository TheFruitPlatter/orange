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
package com.langwuyue.orange.redis.executor.multiplelocks.context;

import java.lang.reflect.Method;
import java.util.concurrent.TimeUnit;

import com.langwuyue.orange.redis.OrangeRedisException;
import com.langwuyue.orange.redis.RedisValueTypeEnum;
import com.langwuyue.orange.redis.annotation.OrangeRedisOperationArg;
import com.langwuyue.orange.redis.annotation.TimeoutUnit;

/**
 * Context class for retrieving multiple locks expirations with time unit parameters.
 * This class extends {@link OrangeMultipleLocksGetExpirationsContext} and adds support for time unit specification.
 * 
 * @author Liang.Zhong
 * @since 1.0.0
 * @see OrangeMultipleLocksGetExpirationsContext
 * @see TimeUnit
 */
public class OrangeMultipleLocksGetExpirationsWithUnitArgsContext extends OrangeMultipleLocksGetExpirationsContext {
	
	/**
	 * The time unit object used to specify the unit for returned expiration times.
	 * Bound to method parameters using {@link OrangeRedisOperationArg} annotation with {@link TimeoutUnit} binding.
	 */
	@OrangeRedisOperationArg(binding = TimeoutUnit.class)
	private Object unit;

	/**
	 * Constructs a new context for retrieving multiple locks expirations with time unit support.
	 *
	 * @param operationOwner    The class that owns the operation
	 * @param operationMethod   The method representing the operation
	 * @param args              The arguments passed to the method
	 * @param redisKey          The Redis key for the operation
	 * @param valueType         The Redis value type enum
	 */
	public OrangeMultipleLocksGetExpirationsWithUnitArgsContext(
		Class<?> operationOwner, 
		Method operationMethod, 
		Object[] args, 
		Key redisKey,
		RedisValueTypeEnum valueType
	) {
		super(operationOwner, operationMethod, args, redisKey, valueType);
	}
	
	/**
	 * Gets the time unit for this operation after validating it.
	 * 
	 * @return The validated {@link TimeUnit} for converting expiration times
	 * @throws OrangeRedisException if the unit is null or not a TimeUnit instance
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