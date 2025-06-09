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
 * Context class for Redis ZSet lexicographical range queries with pagination support (no count version).
 * This class extends {@link OrangeLexRangeContext} to provide efficient pagination functionality
 * for lexicographical range queries without calculating the total count of matching elements.
 * 
 * <p>The "no count" pagination approach offers significant performance benefits when working with
 * large datasets, as it avoids the expensive operation of counting all matching elements.
 * This makes it ideal for implementing features like infinite scrolling or "load more" functionality
 * where the total count is not required.
 * 
 * @author Liang.Zhong
 * @since 1.0.0
 * @see OrangeLexRangeContext
 * @see PageNo
 * @see Count
 */
public class OrangeLexRangePageNoCountContext extends OrangeLexRangeContext {
	
	/**
	 * The page number for pagination, annotated with @PageNo.
	 * Represents which page of results to retrieve (1-based indexing).
	 */
	@OrangeRedisOperationArg(binding = PageNo.class)
	private Object pageNo;
	
	/**
	 * The count of items per page, annotated with @Count.
	 * Determines how many items to retrieve in each page.
	 */
	@OrangeRedisOperationArg(binding = Count.class)
	private Object count;

	/**
	 * Constructs a new OrangeLexRangePageNoCountContext with the specified operation parameters.
	 * 
	 * @param operationOwner The class that owns the Redis operation method
	 * @param operationMethod The method representing the Redis operation
	 * @param args The arguments passed to the operation method
	 * @param redisKey The Redis key to operate on
	 * @param valueType The type of Redis value (should be ZSET for this context)
	 */
	public OrangeLexRangePageNoCountContext(
		Class<?> operationOwner, 
		Method operationMethod, 
		Object[] args, 
		Key redisKey,
		RedisValueTypeEnum valueType
	) {
		super(operationOwner, operationMethod, args, redisKey, valueType);
	}
	
	/**
	 * Retrieves the page number for pagination.
	 * 
	 * <p>This method validates that:
	 * <ul>
	 *   <li>The pageNo value is not null
	 *   <li>The pageNo value is either an integer type or a string that can be parsed as a number
	 * </ul>
	 * 
	 * <p>The page number is used to calculate the offset in the ZSet query using the formula:
	 * offset = (pageNo - 1) * count
	 * 
	 * <p>This method supports both numeric types (Integer, Long, etc.) and String representations
	 * of numbers, providing flexibility in how pagination parameters are passed.
	 * 
	 * @return The validated page number as a Long
	 * @throws OrangeRedisException if the pageNo is null or not a valid integer/string representation
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
	 * Retrieves the count of items per page.
	 * 
	 * <p>This method validates that:
	 * <ul>
	 *   <li>The count value is not null
	 *   <li>The count value is either an integer type or a string that can be parsed as a number
	 * </ul>
	 * 
	 * <p>The count determines how many items will be returned in each page of results.
	 * It is used as the LIMIT parameter in the Redis ZLEXCOUNT command.
	 * 
	 * <p>This method supports both numeric types (Integer, Long, etc.) and String representations
	 * of numbers, providing flexibility in how pagination parameters are passed.
	 * 
	 * <p>For optimal performance:
	 * <ul>
	 *   <li>Choose a reasonable count value based on your application's needs
	 *   <li>Very large count values may impact performance and memory usage
	 *   <li>Very small count values may require more frequent queries
	 * </ul>
	 * 
	 * @return The validated count value as a Long
	 * @throws OrangeRedisException if the count is null or not a valid integer/string representation
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