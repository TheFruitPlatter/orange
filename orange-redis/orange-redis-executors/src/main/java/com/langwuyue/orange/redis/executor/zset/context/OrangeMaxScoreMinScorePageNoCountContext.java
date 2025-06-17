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
package com.langwuyue.orange.redis.executor.zset.context;

import java.lang.reflect.Method;

import com.langwuyue.orange.redis.OrangeRedisException;
import com.langwuyue.orange.redis.RedisValueTypeEnum;
import com.langwuyue.orange.redis.annotation.Count;
import com.langwuyue.orange.redis.annotation.OrangeRedisOperationArg;
import com.langwuyue.orange.redis.annotation.zset.PageNo;
import com.langwuyue.orange.redis.utils.OrangeReflectionUtils;

/**
 * Context class for Redis ZSet operations that involve maximum and minimum score bounds with pagination support.
 * This class extends {@link OrangeMaxScoreMinScoreContext} to add pagination functionality using page number
 * and count parameters.
 *
 * <p>This context combines score range functionality with pagination capabilities, allowing for
 * paginated access to ZSet members within a specified score range. It processes parameters
 * annotated with {@link PageNo} and {@link Count} to handle pagination.
 *
 * <p>Both pageNo and count parameters support:
 * <ul>
 *   <li>Integer types (Integer, Long)
 *   <li>Strings that can be parsed as integers
 * </ul>
 *
 * @author Liang.Zhong
 * @since 1.0.0
 * @see OrangeMaxScoreMinScoreContext
 * @see PageNo
 * @see Count
 * @see <a href="https://orange.langwuyue.com/redis/advanced/zset">Orange Redis ZSet Documentation</a>
 */
public class OrangeMaxScoreMinScorePageNoCountContext extends OrangeMaxScoreMinScoreContext {
	
	/**
	 * The page number parameter, bound by {@link PageNo} annotation.
	 * Can be either an integer type or a string that can be parsed as an integer.
	 */
	@OrangeRedisOperationArg(binding = PageNo.class)
	private Object pageNo;
	
	/**
	 * The count (items per page) parameter, bound by {@link Count} annotation.
	 * Can be either an integer type or a string that can be parsed as an integer.
	 */
	@OrangeRedisOperationArg(binding = Count.class)
	private Object count;

	/**
	 * Constructs a new max/min score pagination context instance.
	 *
	 * @param operationOwner the class that owns the operation
	 * @param operationMethod the operation method
	 * @param args the method parameter array
	 * @param redisKey Redis key
	 * @param valueType Redis value type
	 */
	public OrangeMaxScoreMinScorePageNoCountContext(
		Class<?> operationOwner, 
		Method operationMethod, 
		Object[] args, 
		Key redisKey,
		RedisValueTypeEnum valueType
	) {
		super(operationOwner, operationMethod, args, redisKey, valueType);
	}
	
	/**
	 * Gets the page number for pagination.
	 *
	 * <p>This method validates and converts the page number parameter to a Long value.
	 * The parameter can be either an integer type or a string that can be parsed as an integer.
	 *
	 * @return the page number as a Long value
	 * @throws OrangeRedisException if:
	 *         <ul>
	 *           <li>The page number parameter is null
	 *           <li>The page number parameter is neither an integer type nor a parseable string
	 *         </ul>
	 */
	public Long getPageNo() {
		if(pageNo == null) {
			throw new OrangeRedisException(String.format("The argument annotated with @%s cannot be null", PageNo.class));
		}
		if(!(OrangeReflectionUtils.isInteger(pageNo.getClass())) && !(pageNo instanceof String)) {
			throw new OrangeRedisException(String.format("The argument annotated with @%s must be a integer or a string", PageNo.class));
		}
		return Long.valueOf(pageNo.toString());
		
	}
	
	/**
	 * Gets the count (items per page) for pagination.
	 *
	 * <p>This method validates and converts the count parameter to a Long value.
	 * The parameter can be either an integer type or a string that can be parsed as an integer.
	 *
	 * @return the count as a Long value
	 * @throws OrangeRedisException if:
	 *         <ul>
	 *           <li>The count parameter is null
	 *           <li>The count parameter is neither an integer type nor a parseable string
	 *         </ul>
	 */
	public Long getCount() {
		if(count == null) {
			throw new OrangeRedisException(String.format("The argument annotated with @%s cannot be null", Count.class));
		}
		if(!(OrangeReflectionUtils.isInteger(count.getClass())) && !(count instanceof String)) {
			throw new OrangeRedisException(String.format("The argument annotated with @%s must be a integer or a string", Count.class));
		}
		return Long.valueOf(count.toString());
	}

}