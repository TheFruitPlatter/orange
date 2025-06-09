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

import com.langwuyue.orange.redis.RedisValueTypeEnum;
import com.langwuyue.orange.redis.annotation.OrangeRedisOperationArg;
import com.langwuyue.orange.redis.annotation.zset.Pager;

/**
 * Context class for Redis ZSet lexicographical range queries with pagination support.
 * This class extends {@link OrangeLexRangeContext} to add pagination capabilities
 * for lexicographical range operations on Redis Sorted Sets.
 * 
 * <p>The pagination is implemented using the {@link Pager} annotation, which allows
 * for efficient retrieval of large result sets in smaller, manageable chunks.
 * This approach is particularly useful when dealing with large ZSets where
 * returning all matching elements at once would be inefficient or impractical.
 * 
 * <p>Key features of lexicographical range pagination:
 * <ul>
 *   <li>Efficient memory usage by retrieving only a subset of results
 *   <li>Support for both offset-based and cursor-based pagination
 *   <li>Compatible with all lexicographical range operations
 *   <li>Maintains the lexicographical ordering of elements
 * </ul>
 * 
 * @author Liang.Zhong
 * @since 1.0.0
 * @see OrangeLexRangeContext
 * @see OrangeRedisPagerContext
 * @see com.langwuyue.orange.redis.annotation.zset.Pager
 * @see com.langwuyue.orange.redis.operations.OrangeRedisZSetOperations.Pager
 */
public class OrangeLexRangePagerContext extends OrangeLexRangeContext implements OrangeRedisPagerContext {
	
	/**
	 * The pagination parameter annotated with @Pager.
	 * This field holds the pagination information such as offset and count
	 * for the lexicographical range query.
	 */
	@OrangeRedisOperationArg(binding = Pager.class)
	private Object pager;

	/**
	 * Constructs a new OrangeLexRangePagerContext with the specified operation parameters.
	 * 
	 * @param operationOwner The class that owns the Redis operation method
	 * @param operationMethod The method representing the Redis operation
	 * @param args The arguments passed to the operation method
	 * @param redisKey The Redis key to operate on
	 * @param valueType The type of Redis value (should be ZSET for this context)
	 */
	public OrangeLexRangePagerContext(
		Class<?> operationOwner, 
		Method operationMethod, 
		Object[] args,
		Key redisKey,
		RedisValueTypeEnum valueType
	) {
		super(operationOwner, operationMethod, args, redisKey,valueType);
	}

	/**
	 * Retrieves the pagination parameter for this lexicographical range query.
	 * 
	 * <p>This method delegates to the parent class's getPager method, which performs
	 * validation on the pager object to ensure it is not null and is of the correct type.
	 * 
	 * <p>The pager object contains pagination parameters such as:
	 * <ul>
	 *   <li>offset: The starting position for the range query (zero-based)
	 *   <li>count: The maximum number of elements to return
	 * </ul>
	 * 
	 * <p>These parameters are used with the Redis ZRANGEBYLEX command's LIMIT option
	 * to implement server-side pagination, which is more efficient than retrieving
	 * all elements and performing pagination on the client side.
	 * 
	 * @return The validated pager object for the lexicographical range query
	 */
	public com.langwuyue.orange.redis.operations.OrangeRedisZSetOperations.Pager getPager(){
		return getPager(pager);
	}
	
}