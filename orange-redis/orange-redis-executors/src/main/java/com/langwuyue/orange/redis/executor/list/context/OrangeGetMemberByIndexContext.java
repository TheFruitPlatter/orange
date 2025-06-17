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
import com.langwuyue.orange.redis.context.OrangeRedisContext;
import com.langwuyue.orange.redis.utils.OrangeReflectionUtils;

/**
 * Context class for Redis list operations that retrieve elements by index.
 * 
 * <p>This context extends the basic Redis context to include an index field
 * that specifies which element to retrieve from a Redis list. Redis lists
 * are zero-indexed, with 0 being the first element, 1 being the second element,
 * and so on. Negative indices can also be used to count from the end of the list,
 * with -1 being the last element, -2 being the second-to-last element, etc.
 * 
 * <p>This context class captures the index value from method parameters
 * annotated with {@link Index} and provides validation to ensure the index
 * is properly formatted.
 *
 * @author Liang.Zhong
 * @since 1.0.0
 */
public class OrangeGetMemberByIndexContext extends OrangeRedisContext {
	
	/**
	 * The index value for retrieving an element from a Redis list.
	 * 
	 * <p>This field is bound to method parameters annotated with {@link Index}
	 * and represents the position of the element to retrieve from the list.
	 * It can be a positive integer (0-based index from the start of the list)
	 * or a negative integer (counting from the end of the list, where -1 is the last element).
	 */
	@OrangeRedisOperationArg(binding = Index.class)
	private Object index;

	/**
	 * Constructs a new OrangeGetMemberByIndexContext with the specified parameters.
	 *
	 * @param operationOwner the class that owns the Redis operation method
	 * @param operationMethod the method representing the Redis operation
	 * @param args the arguments passed to the operation method
	 * @param redisKey the Redis key to operate on
	 * @param valueType the type of values stored in the Redis list
	 */
	public OrangeGetMemberByIndexContext(
		Class<?> operationOwner, 
		Method operationMethod, 
		Object[] args,
		Key redisKey,
		RedisValueTypeEnum valueType
	) {
		super(operationOwner, operationMethod, args, redisKey,valueType);
	}

	/**
	 * Retrieves and validates the index value for the list operation.
	 *
	 * <p>This method performs several validations:
	 * <ul>
	 *   <li>Checks that the index is not null</li>
	 *   <li>Verifies that the index is either an integer type or a string that can be parsed as an integer</li>
	 *   <li>Converts the index to a Long value for use in Redis operations</li>
	 * </ul>
	 *
	 * <p>The index can be positive (0-based from the start of the list) or negative
	 * (counting from the end of the list, where -1 is the last element).
	 *
	 * @return the validated index as a Long value
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