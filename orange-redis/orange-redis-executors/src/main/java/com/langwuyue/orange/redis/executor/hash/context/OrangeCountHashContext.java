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
package com.langwuyue.orange.redis.executor.hash.context;

import java.lang.reflect.Method;

import com.langwuyue.orange.redis.OrangeRedisException;
import com.langwuyue.orange.redis.RedisValueTypeEnum;
import com.langwuyue.orange.redis.annotation.Count;
import com.langwuyue.orange.redis.annotation.OrangeRedisOperationArg;
import com.langwuyue.orange.redis.utils.OrangeReflectionUtils;

/**
 * Context class for Redis hash operations that involve a count parameter.
 * 
 * <p>This context extends the basic hash context by adding support for a count parameter,
 * which is typically used in operations that need to limit or specify the number of
 * elements to process, such as pagination or batch operations.
 * 
 * <p>The count parameter is bound to method parameters annotated with {@link Count}
 * and is validated to ensure it's either an integer or a string that can be parsed as an integer.
 *
 * @author Liang.Zhong
 * @since 1.0.0
 */
public class OrangeCountHashContext extends OrangeHashContext {
	
	/**
	 * The count parameter bound from the method argument annotated with {@link Count}.
	 * This can be either an Integer or a String that can be parsed to an Integer.
	 */
	@OrangeRedisOperationArg(binding = Count.class)
	private Object count;
	
	/**
	 * Constructs a new OrangeCountHashContext with the specified parameters.
	 *
	 * @param operationOwner  the class that owns the Redis operation
	 * @param operationMethod the method representing the Redis operation
	 * @param args            the arguments passed to the operation method
	 * @param redisKey        the Redis key for the operation
	 * @param valueType       the type of value stored in Redis
	 * @param keyType         the type of the Redis key
	 */
	public OrangeCountHashContext(
		Class<?> operationOwner, 
		Method operationMethod, 
		Object[] args,
		Key redisKey,
		RedisValueTypeEnum valueType,
		RedisValueTypeEnum keyType
	) {
		super(operationOwner, operationMethod, args, redisKey, valueType, keyType);
	}
	
	/**
	 * Gets the count value as an Integer.
	 *
	 * @return the count value as an Integer
	 * @throws OrangeRedisException if the count parameter is null or not a valid integer
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

}