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
package com.langwuyue.orange.redis.executor.zset;

import com.langwuyue.orange.redis.context.OrangeRedisContext;
import com.langwuyue.orange.redis.executor.OrangeGetIndexAbstractExecutor;
import com.langwuyue.orange.redis.logger.OrangeRedisLogger;
import com.langwuyue.orange.redis.mapping.OrangeRedisExecutorIdGenerator;
import com.langwuyue.orange.redis.operations.OrangeRedisZSetOperations;
/**
 * Executor for retrieving the ranks of multiple members in a Redis Sorted Set.
 * 
 * <p>This executor provides functionality to get the ranks (positions) of multiple members
 * in a Redis Sorted Set. The rank represents the position of a member in the sorted set,
 * where the position is 0-based and determined by the member's score.
 *
 * <p>Key features:
 * <ul>
 *   <li>Retrieves ranks for multiple members in a single operation</li>
 *   <li>Returns null for members that don't exist in the set</li>
 *   <li>Supports both simple types and complex objects as members</li>
 *   <li>Returns 0-based positions (0 is the lowest rank)</li>
 *   <li>Ranks are determined by the members' scores in ascending order</li>
 * </ul>
 *
 * <p>Note: This executor extends {@link OrangeGetIndexAbstractExecutor} which provides
 * common functionality for index-based operations in Redis data structures.
 *
 * @author Liang.Zhong
 * @since 1.0.0
 * @see <a href="https://orange.langwuyue.com/redis/advanced/zset">Orange Redis ZSet Documentation</a>
 */
public class OrangeGetRanksExecutor extends OrangeGetIndexAbstractExecutor {

	private OrangeRedisZSetOperations operations;

	/**
	 * Constructs a new OrangeGetRanksExecutor.
	 *
	 * @param operations the Redis Sorted Set operations implementation
	 * @param idGenerator the executor ID generator for tracking and monitoring
	 * @param logger the logger instance for recording execution details and errors
	 */
	public OrangeGetRanksExecutor(OrangeRedisZSetOperations operations,OrangeRedisExecutorIdGenerator idGenerator,OrangeRedisLogger logger) {
		super(idGenerator,logger);
		this.operations = operations;
	}

	/**
	 * Gets the rank (position) of a single member in the Redis Sorted Set.
	 *
	 * <p>This method is called by the parent class for each member in the input list.
	 * It performs the actual Redis operation to retrieve the member's rank.
	 *
	 * <p>The rank is:
	 * <ul>
	 *   <li>0-based (0 is the first position)</li>
	 *   <li>Determined by ascending score order</li>
	 *   <li>Null if the member doesn't exist in the set</li>
	 * </ul>
	 *
	 * @param context the execution context containing Redis key and value type info
	 * @param value the member value to get the rank for
	 * @return the member's rank as Long, or null if member doesn't exist
	 * @throws Exception if:
	 *                   - Redis operation fails
	 *                   - value type conversion fails
	 *                   - any other execution error occurs
	 */
	@Override
	protected Long getIndex(OrangeRedisContext context,Object value) throws Exception {
		return this.operations.getMemberRank(context.getRedisKey().getValue(), value, context.getValueType());
	}

}