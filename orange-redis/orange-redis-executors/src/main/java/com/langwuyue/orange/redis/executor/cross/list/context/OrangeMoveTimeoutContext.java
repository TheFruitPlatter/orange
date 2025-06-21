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

import com.langwuyue.orange.redis.RedisValueTypeEnum;
import com.langwuyue.orange.redis.annotation.OrangeRedisOperationArg;
import com.langwuyue.orange.redis.annotation.Timeout;
import com.langwuyue.orange.redis.context.builder.OrangeMethodAnnotationHandler;

/**
 * Context class for Redis list move operations with timeout support.
 * 
 * <p>Extends {@link OrangeMoveContext} to add timeout capabilities for list move operations.
 * This context holds all necessary information including:
 * <ul>
 *   <li>Source and destination keys</li>
 *   <li>Move direction (LEFT/RIGHT)</li>
 *   <li>Timeout configuration</li>
 *   <li>Operation owner and method details</li>
 *   <li>Value type information</li>
 * </ul>
 *
 * @author Liang.Zhong
 * @since 1.0.0
 */
public class OrangeMoveTimeoutContext extends OrangeMoveContext {
	
	/**
	 * The timeout configuration for the list move operation.
	 * <p>This field is populated by the {@link OrangeMethodAnnotationHandler}
	 * based on the {@link Timeout} annotation and specifies:
	 * <ul>
	 *   <li>Timeout value</li>
	 *   <li>Timeout unit</li>
	 * </ul>
	 */
	@OrangeRedisOperationArg(valueHandler = OrangeMethodAnnotationHandler.class, binding = Timeout.class)
	private Timeout timeout;

	/**
	 * Creates a new OrangeMoveTimeoutContext instance.
	 * 
	 * @param operationOwner the class that owns the operation method
	 * @param operationMethod the method annotated with Redis operation
	 * @param args the method arguments
	 * @param keys the Redis keys involved in the operation
	 * @param storeTo the destination key for the move operation
	 * @param valueType the type of value being moved
	 */
	public OrangeMoveTimeoutContext(
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
	 * Gets the timeout configuration for the list move operation.
	 * 
	 * @return the timeout configuration which determines:
	 * <ul>
	 *   <li>How long to wait for an element to become available</li>
	 *   <li>The time unit for the wait period</li>
	 * </ul>
	 */
	public Timeout getTimeout() {
		return timeout;
	}
	
}