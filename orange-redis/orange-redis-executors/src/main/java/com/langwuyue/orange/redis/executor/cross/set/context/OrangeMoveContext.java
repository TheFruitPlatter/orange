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
package com.langwuyue.orange.redis.executor.cross.set.context;

import java.lang.reflect.Method;
import java.util.List;

import com.langwuyue.orange.redis.OrangeRedisException;
import com.langwuyue.orange.redis.RedisValueTypeEnum;
import com.langwuyue.orange.redis.annotation.OrangeRedisOperationArg;
import com.langwuyue.orange.redis.annotation.RedisValue;
import com.langwuyue.orange.redis.executor.cross.context.OrangeCrossOperationContext;

/**
 * Context class for Redis set move operations.
 *
 * <p>This context holds all necessary information for moving an element from one set to another,
 * including:
 * <ul>
 *   <li>The source and destination set keys (inherited from parent class)</li>
 *   <li>The value to be moved between sets</li>
 *   <li>Operation metadata (owner class, method, arguments)</li>
 * </ul>
 *
 * <p>The value to be moved must be annotated with {@link RedisValue} in the method parameters.
 *
 * @author Liang.Zhong
 * @since 1.0.0
 */
public class OrangeMoveContext extends OrangeCrossOperationContext {
	
	/**
	 * The value to be moved between sets.
	 * <p>This field is automatically populated from the method parameter annotated with {@link RedisValue}.
	 */
	@OrangeRedisOperationArg(binding = RedisValue.class)
	private Object value;

	/**
	 * Creates a new context for set move operations.
	 *
	 * @param operationOwner the class containing the operation method
	 * @param operationMethod the method annotated with set move operation
	 * @param args the method arguments
	 * @param keys the source set keys
	 * @param storeTo the destination set key
	 * @param valueType the Redis value type of the elements
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
	 * Gets the value to be moved between sets.
	 * 
	 * @return the value to be moved, as specified in the method parameter annotated with {@link RedisValue}
	 */
	public Object getValue() {
		if(value == null) {
			throw new OrangeRedisException(String.format("The argument annotated with @%s cannot be null", RedisValue.class));
		}
		return value;
	}

}