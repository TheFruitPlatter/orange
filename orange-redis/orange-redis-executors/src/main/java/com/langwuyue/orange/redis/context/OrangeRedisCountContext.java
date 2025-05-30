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

import com.langwuyue.orange.redis.OrangeRedisException;
import com.langwuyue.orange.redis.RedisValueTypeEnum;
import com.langwuyue.orange.redis.annotation.Count;
import com.langwuyue.orange.redis.annotation.OrangeRedisOperationArg;
import com.langwuyue.orange.redis.utils.OrangeReflectionUtils;

/**
 * Context class for Redis operations that require a count parameter.
 * 
 * <p>This class extends {@link OrangeRedisContext} to provide support for Redis operations
 * that involve counting or limiting operations, such as:
 * <ul>
 *   <li>LRANGE - when limiting the number of elements to retrieve</li>
 *   <li>ZRANGE - when specifying the number of elements to return</li>
 *   <li>SCAN - when setting the count hint for iteration</li>
 * </ul>
 * 
 * <p>The count value must be:
 * <ul>
 *   <li>Non-null</li>
 *   <li>Either an integer type or a string that can be parsed to an integer</li>
 *   <li>Greater than zero</li>
 * </ul>
 *
 * @author Liang.Zhong
 * @since 1.0.0
 */
public class OrangeRedisCountContext extends OrangeRedisContext {
	
	/**
	 * The count value for the Redis operation.
	 * 
	 * <p>This field is automatically populated with the value of the method parameter
	 * annotated with {@link Count}. The value can be either an integer type or a
	 * string that can be parsed to an integer.
	 * 
	 * <p>The field is marked with {@link OrangeRedisOperationArg} to indicate that
	 * it should be bound to parameters annotated with {@link Count}.
	 */
	@OrangeRedisOperationArg(binding = Count.class)
	private Object count;

	/**
	 * Constructs a new Redis count operation context with the specified parameters.
	 *
	 * @param operationOwner the class that owns the Redis operation method
	 * @param operationMethod the method representing the Redis operation
	 * @param args the arguments to be passed to the operation method
	 * @param redisKey the Redis key information
	 * @param valueType the type of Redis value being operated on
	 */
	public OrangeRedisCountContext(
		Class<?> operationOwner, 
		Method operationMethod, 
		Object[] args, 
		Key redisKey,
		RedisValueTypeEnum valueType
	) {
		super(operationOwner, operationMethod, args, redisKey, valueType);
	}
	
	/**
	 * Returns the validated count value as an Integer.
	 * 
	 * <p>This method performs several validations on the count value:
	 * <ul>
	 *   <li>Checks that the value is not null</li>
	 *   <li>Verifies that the value is either an integer type or a string</li>
	 *   <li>Ensures that the value, when converted to an integer, is greater than zero</li>
	 * </ul>
	 *
	 * @return the validated count value as an Integer
	 * @throws OrangeRedisException if:
	 *         <ul>
	 *           <li>The count value is null</li>
	 *           <li>The count value is neither an integer type nor a string</li>
	 *           <li>The count value, when converted to an integer, is less than or equal to zero</li>
	 *           <li>The count value cannot be parsed to an integer (if it's a string)</li>
	 *         </ul>
	 */
	public Integer getCount() {
		if(this.count == null) {
			throw new OrangeRedisException(String.format("The argument annotated with @%s cannot be null", Count.class));
		}
		if(!(OrangeReflectionUtils.isInteger(this.count.getClass())) && !(this.count instanceof String)) {
			throw new OrangeRedisException(String.format("The argument annotated with @%s must be a integer or a string", Count.class));
		}
		Integer value = Integer.valueOf(this.count.toString());
		if(value <= 0) {
			throw new OrangeRedisException(String.format("The argument annotated with @%s must be greater than zero", Count.class));
		}
		return value;
	}
	
}