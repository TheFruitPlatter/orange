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
import com.langwuyue.orange.redis.annotation.cross.ListMoveDirection;
import com.langwuyue.orange.redis.context.builder.OrangeMethodAnnotationHandler;
import com.langwuyue.orange.redis.executor.cross.context.OrangeCrossOperationContext;

/**
 * Context class for Redis list move operations.
 * 
 * <p>This context holds all necessary information for executing a Redis list move operation,
 * including:
 * <ul>
 *   <li>Source and destination keys</li>
 *   <li>Move direction (LEFT/RIGHT)</li>
 *   <li>Operation owner and method details</li>
 *   <li>Value type information</li>
 * </ul>
 *
 * @author Liang.Zhong
 * @since 1.0.0
 */
public class OrangeMoveContext extends OrangeCrossOperationContext {
	
	/**
	 * The direction of the list move operation (LEFT/RIGHT).
	 * <p>This field is populated by the {@link OrangeMethodAnnotationHandler}
	 * based on the {@link ListMoveDirection} annotation.
	 */
	@OrangeRedisOperationArg(valueHandler = OrangeMethodAnnotationHandler.class, binding = ListMoveDirection.class)
	private ListMoveDirection direction;

	/**
	 * Creates a new OrangeMoveContext instance.
	 * 
	 * @param operationOwner the class that owns the operation method
	 * @param operationMethod the method annotated with Redis operation
	 * @param args the method arguments
	 * @param keys the Redis keys involved in the operation
	 * @param storeTo the destination key for the move operation
	 * @param valueType the type of value being moved
	 */
	public OrangeMoveContext(
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
	 * Gets the direction of the list move operation.
	 * 
	 * @return the move direction (LEFT/RIGHT) which determines:
	 * <ul>
	 *   <li>From which end of the source list to pop the value</li>
	 *   <li>To which end of the destination list to push the value</li>
	 * </ul>
	 */
	public ListMoveDirection getDirection() {
		return direction;
	}
}