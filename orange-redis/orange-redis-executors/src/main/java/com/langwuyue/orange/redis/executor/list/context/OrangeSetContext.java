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
import com.langwuyue.orange.redis.annotation.list.Index;
import com.langwuyue.orange.redis.context.OrangeRedisValueContext;
import com.langwuyue.orange.redis.utils.OrangeReflectionUtils;

/**
 * Context class for Redis list set operations.
 * 
 * <p>This context extends the basic Redis value context to include an index field
 * that specifies the position in the list where an element should be set. This corresponds
 * to Redis's LSET command, which replaces an element at a specific index in the list.
 * 
 * <p>The index is zero-based, where 0 is the first element, 1 is the second element, and so on.
 * Negative indices can be used to specify positions from the end of the list, where -1 is the
 * last element, -2 is the penultimate element, and so on.
 * 
 * <p>This context class captures the index value from method parameters annotated with {@link Index}
 * and provides validation to ensure the index is properly specified.
 *
 * @author Liang.Zhong
 * @since 1.0.0
 */
public class OrangeSetContext extends OrangeRedisValueContext {
	
	/**
	 * The index position in the Redis list where the element should be set.
	 * 
	 * <p>This field is bound to method parameters annotated with {@link Index}
	 * and represents the position in the list where the new value should be placed.
	 * 
	 * <p>The index can be:
	 * <ul>
	 *   <li>Zero-based positive integer (0, 1, 2, ...) to count from the start of the list</li>
	 *   <li>Negative integer (-1, -2, -3, ...) to count from the end of the list</li>
	 * </ul>
	 */
	@OrangeRedisOperationArg(binding = Index.class)
	private Object index;

	/**
	 * Constructs a new OrangeSetContext with the specified parameters.
	 *
	 * @param operationOwner the class that owns the Redis operation method
	 * @param operationMethod the method representing the Redis operation
	 * @param args the arguments passed to the operation method
	 * @param redisKey the Redis key to operate on
	 * @param valueType the type of values stored in the Redis list
	 */
	public OrangeSetContext(
		Class<?> operationOwner, 
		Method operationMethod, 
		Object[] args,
		Key redisKey,
		RedisValueTypeEnum valueType
	) {
		super(operationOwner, operationMethod, args, redisKey,valueType);
	}

	/**
	 * Retrieves and validates the index for the set operation.
	 *
	 * <p>This method ensures that an index has been provided and that it is
	 * a valid integer representation. The index specifies the position in the
	 * list where the new value should be set.
	 *
	 * <p>Valid indices include:
	 * <ul>
	 *   <li>Zero-based positive integers (0, 1, 2, ...) to count from the start of the list</li>
	 *   <li>Negative integers (-1, -2, -3, ...) to count from the end of the list</li>
	 * </ul>
	 *
	 * <p>Note: If the index is out of range (not between 0 and the list length minus 1),
	 * the Redis LSET operation will fail with an error.
	 *
	 * @return the validated index value as a Long
	 * @throws OrangeRedisException if the index is null or not a valid integer representation
	 */
	public Long getIndex() {
		if(index == null) {
			throw new OrangeRedisException(String.format("The argument annotated with @%s cannot be null", Index.class));
		}
		if(!(OrangeReflectionUtils.isInteger(index.getClass())) && !(index instanceof String)) {
			throw new OrangeRedisException(String.format("The argument annotated with @%s must be a integer or a string", Index.class));
		}
		return Long.valueOf(index.toString());
	}
}