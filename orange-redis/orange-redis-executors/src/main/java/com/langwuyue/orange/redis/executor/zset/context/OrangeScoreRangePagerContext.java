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
 * Context class for Redis ZSet score range queries with paging support using a Pager object.
 * This class extends {@link OrangeScoreRangeContext} and implements {@link OrangeRedisPagerContext}
 * to provide pagination functionality through a dedicated pager object.
 *
 * <p>The context processes parameters annotated with {@link Pager} to handle pagination
 * in ZSet score range queries. The pager object encapsulates pagination parameters like
 * page number and items per page.
 *
 * @author Liang.Zhong
 * @since 1.0.0
 * @see OrangeScoreRangeContext
 * @see OrangeRedisPagerContext
 * @see Pager
 * @see <a href="https://orange.langwuyue.com/redis/advanced/zset">Orange Redis ZSet Documentation</a>
 */
public class OrangeScoreRangePagerContext extends OrangeScoreRangeContext implements OrangeRedisPagerContext{
	
	/**
	 * The pager object bound by {@link Pager} annotation.
	 * This object contains pagination information like page number and items per page.
	 */
	@OrangeRedisOperationArg(binding = Pager.class)
	private Object pager;

	/**
	 * Constructs a new score range pager context instance.
	 *
	 * @param operationOwner the class that owns the operation
	 * @param operationMethod the operation method
	 * @param args the method parameter array
	 * @param redisKey Redis key
	 * @param valueType Redis value type
	 */
	public OrangeScoreRangePagerContext(
		Class<?> operationOwner, 
		Method operationMethod, 
		Object[] args,
		Key redisKey,
		RedisValueTypeEnum valueType
	) {
		super(operationOwner, operationMethod, args, redisKey,valueType);
	}

	/**
	 * Gets the pager object for pagination.
	 *
	 * <p>This method converts the pager parameter to a {@link com.langwuyue.orange.redis.operations.OrangeRedisZSetOperations.Pager}
	 * instance using the base class's conversion method.
	 *
	 * @return the pager object as a {@link com.langwuyue.orange.redis.operations.OrangeRedisZSetOperations.Pager} instance
	 */
	public com.langwuyue.orange.redis.operations.OrangeRedisZSetOperations.Pager getPager() {
		return getPager(pager);
	}
}