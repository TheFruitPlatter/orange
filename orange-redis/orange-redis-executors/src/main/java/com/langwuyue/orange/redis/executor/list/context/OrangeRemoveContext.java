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
import com.langwuyue.orange.redis.annotation.Count;
import com.langwuyue.orange.redis.annotation.OrangeRedisOperationArg;
import com.langwuyue.orange.redis.annotation.RedisValue;
import com.langwuyue.orange.redis.context.OrangeRedisContext;
import com.langwuyue.orange.redis.utils.OrangeReflectionUtils;

/**
 * Context class for Redis list removal operations.
 * 
 * <p>This context extends the basic Redis context to include fields for the value
 * to be removed from the list and the count of occurrences to remove. This corresponds
 * to Redis's LREM command, which removes elements equal to a specified value.
 * 
 * <p>The count parameter determines how many occurrences of the value should be removed:
 * <ul>
 *   <li>count > 0: Remove elements equal to value moving from head to tail, up to count occurrences</li>
 *   <li>count < 0: Remove elements equal to value moving from tail to head, up to abs(count) occurrences</li>
 *   <li>count = 0: Remove all elements equal to value</li>
 * </ul>
 * 
 * <p>This context class captures:
 * <ul>
 *   <li>The value to remove from method parameters annotated with {@link RedisValue}</li>
 *   <li>The count of occurrences to remove from parameters annotated with {@link Count}</li>
 * </ul>
 *
 *
 * @author Liang.Zhong
 * @since 1.0.0
 */
public class OrangeRemoveContext extends OrangeRedisContext {
	
	/**
	 * The count parameter that determines how many occurrences to remove.
	 * 
	 * <p>This field is bound to method parameters annotated with {@link Count}
	 * and specifies the number and direction of elements to remove:
	 * <ul>
	 *   <li>Positive count: Remove from head to tail</li>
	 *   <li>Negative count: Remove from tail to head</li>
	 *   <li>Zero count: Remove all occurrences</li>
	 * </ul>
	 */
	@OrangeRedisOperationArg(binding = Count.class)
	private Object count;
	
	/**
	 * The value to be removed from the Redis list.
	 * 
	 * <p>This field is bound to method parameters annotated with {@link RedisValue}
	 * and represents the element value that should be removed from the list.
	 * All occurrences of this value will be considered for removal, subject to
	 * the count parameter's constraints.
	 */
	@OrangeRedisOperationArg(binding = RedisValue.class)
	private Object value;

	/**
	 * Constructs a new OrangeRemoveContext with the specified parameters.
	 *
	 * @param operationOwner the class that owns the Redis operation method
	 * @param operationMethod the method representing the Redis operation
	 * @param args the arguments passed to the operation method
	 * @param redisKey the Redis key to operate on
	 * @param valueType the type of values stored in the Redis list
	 */
	public OrangeRemoveContext(
		Class<?> operationOwner, 
		Method operationMethod, 
		Object[] args, 
		Key redisKey,
		RedisValueTypeEnum valueType
	) {
		super(operationOwner, operationMethod, args, redisKey, valueType);
	}
	
	/**
	 * Retrieves and validates the count parameter for the removal operation.
	 *
	 * <p>The count value determines both the number of elements to remove and
	 * the direction of removal:
	 * <ul>
	 *   <li>count > 0: Remove up to count occurrences from head to tail</li>
	 *   <li>count < 0: Remove up to |count| occurrences from tail to head</li>
	 *   <li>count = 0: Remove all occurrences</li>
	 * </ul>
	 *
	 * @return the validated count value as an Integer
	 * @throws OrangeRedisException if the count is null or not a valid integer representation
	 */
	public Integer getCount() {
		if(count == null) {
			throw new OrangeRedisException(String.format("The argument annotated with @%s cannot be null", Count.class));
		}
		if(!(OrangeReflectionUtils.isInteger(count.getClass())) && !(count instanceof String)) {
			throw new OrangeRedisException(String.format("The argument annotated with @%s must be a integer or a string", Count.class));
		}
		return Integer.valueOf(count.toString());
	}
	
	/**
	 * Retrieves and validates the value to be removed from the list.
	 *
	 * <p>This value represents the element that should be removed from the Redis list.
	 * The actual number of elements removed depends on how many occurrences of this
	 * value exist in the list and the count parameter's constraints.
	 *
	 * @return the value to be removed from the list
	 * @throws OrangeRedisException if the value is null
	 */
	public Object getValue() {
		if(value == null) {
			throw new OrangeRedisException(String.format("The argument annotated with @%s cannot be null", RedisValue.class));
		}
		return value;
	}
	
}