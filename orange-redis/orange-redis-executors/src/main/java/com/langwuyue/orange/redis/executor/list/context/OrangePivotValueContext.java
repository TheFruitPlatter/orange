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
import com.langwuyue.orange.redis.annotation.list.Pivot;
import com.langwuyue.orange.redis.context.OrangeRedisValueContext;

/**
 * Context class for Redis list operations that require a pivot value.
 * 
 * <p>This context extends the basic Redis value context to include a pivot field
 * that specifies a reference element in the list. The pivot value is typically used
 * in list insertion operations to indicate where new elements should be placed
 * relative to an existing element (e.g., inserting before or after a specific value).
 * 
 * <p>This context class captures the pivot value from method parameters
 * annotated with {@link Pivot} and provides validation to ensure the pivot
 * value is properly specified.
 *
 * @author Liang.Zhong
 * @since 1.0.0
 */
public class OrangePivotValueContext extends OrangeRedisValueContext {
	
	/**
	 * The pivot value used as a reference point in Redis list operations.
	 * 
	 * <p>This field is bound to method parameters annotated with {@link Pivot}
	 * and represents an existing element in the list that serves as a reference point
	 * for operations like insertion (before/after) or relative positioning.
	 */
	@OrangeRedisOperationArg(binding = Pivot.class)
	private Object pivot;

	/**
	 * Constructs a new OrangePivotValueContext with the specified parameters.
	 *
	 * @param operationOwner the class that owns the Redis operation method
	 * @param operationMethod the method representing the Redis operation
	 * @param args the arguments passed to the operation method
	 * @param redisKey the Redis key to operate on
	 * @param valueType the type of values stored in the Redis list
	 */
	public OrangePivotValueContext(
		Class<?> operationOwner, 
		Method operationMethod, 
		Object[] args,
		Key redisKey,
		RedisValueTypeEnum valueType
	) {
		super(operationOwner, operationMethod, args, redisKey,valueType);
	}

	/**
	 * Retrieves and validates the pivot value for the list operation.
	 *
	 * <p>This method ensures that a pivot value has been provided, as it is
	 * required for operations that need a reference point within the list,
	 * such as inserting elements before or after a specific value.
	 *
	 * <p>The pivot value should match an existing element in the Redis list.
	 * If the pivot value doesn't exist in the list, the behavior depends on
	 * the specific Redis command being executed.
	 *
	 * @return the pivot value to use as a reference point
	 * @throws OrangeRedisException if the pivot value is null
	 */
	public Object getPivot() {
		if(pivot == null) {
			throw new OrangeRedisException(String.format("The argument annotated with @%s cannot be null", Pivot.class));
		}
		return pivot;
	}
}