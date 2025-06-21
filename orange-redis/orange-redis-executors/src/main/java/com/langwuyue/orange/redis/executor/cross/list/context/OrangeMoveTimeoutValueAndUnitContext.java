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
package com.langwuyue.orange.redis.executor.cross.list.context;

import java.lang.reflect.Method;
import java.util.List;
import java.util.concurrent.TimeUnit;

import com.langwuyue.orange.redis.RedisValueTypeEnum;
import com.langwuyue.orange.redis.annotation.OrangeRedisOperationArg;
import com.langwuyue.orange.redis.annotation.TimeoutUnit;
import com.langwuyue.orange.redis.annotation.TimeoutValue;

/**
 * Context class for Redis list move operations with separate timeout value and unit support.
 * 
 * <p>Extends {@link OrangeMoveContext} to add timeout capabilities with independently specified
 * value and time unit for list move operations. This context holds all necessary information including:
 * <ul>
 *   <li>Source and destination keys</li>
 *   <li>Move direction (LEFT/RIGHT)</li>
 *   <li>Timeout value (numeric)</li>
 *   <li>Timeout unit (TimeUnit)</li>
 *   <li>Operation owner and method details</li>
 *   <li>Value type information</li>
 * </ul>
 *
 * @author Liang.Zhong
 * @since 1.0.0
 */
public class OrangeMoveTimeoutValueAndUnitContext extends OrangeMoveContext {
	
	/**
	 * The timeout value for the list move operation.
	 * <p>This field is populated by the {@link OrangeRedisOperationArg} annotation
	 * with {@link TimeoutValue} binding and specifies the numeric timeout duration.
	 */
	@OrangeRedisOperationArg(binding = TimeoutValue.class)
	private Long value;
	
	/**
	 * The timeout unit for the list move operation.
	 * <p>This field is populated by the {@link OrangeRedisOperationArg} annotation
	 * with {@link TimeoutUnit} binding and specifies the time unit (e.g. SECONDS, MILLISECONDS)
	 * for the timeout duration.
	 */
	@OrangeRedisOperationArg(binding = TimeoutUnit.class)
	private TimeUnit unit;

	/**
	 * Creates a new OrangeMoveTimeoutValueAndUnitContext instance.
	 * 
	 * @param operationOwner the class that owns the operation method
	 * @param operationMethod the method annotated with Redis operation
	 * @param args the method arguments
	 * @param keys the Redis keys involved in the operation
	 * @param storeTo the destination key for the move operation
	 * @param valueType the type of value being moved
	 */
	public OrangeMoveTimeoutValueAndUnitContext(
		Class<?> operationOwner, 
		Method operationMethod, 
		Object[] args, 
		List<String> keys,
		String storeTo, 
		RedisValueTypeEnum valueType
	) {
		super(operationOwner, operationMethod, args, keys, storeTo, valueType);
	}
	
	/**
	 * Gets the timeout value for the list move operation.
	 * 
	 * @return the numeric timeout duration value
	 */
	public Long getValue() {
		return value;
	}

	/**
	 * Gets the timeout unit for the list move operation.
	 * 
	 * @return the time unit (e.g. SECONDS, MILLISECONDS) for the timeout duration
	 */
	public TimeUnit getUnit() {
		return unit;
	}
	
}