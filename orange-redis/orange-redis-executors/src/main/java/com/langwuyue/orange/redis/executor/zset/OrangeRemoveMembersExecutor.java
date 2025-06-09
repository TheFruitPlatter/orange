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
import com.langwuyue.orange.redis.context.OrangeRedisMultipleValueContext;
import com.langwuyue.orange.redis.executor.OrangeRemoveMembersAbstractExecutor;
import com.langwuyue.orange.redis.logger.OrangeRedisLogger;
import com.langwuyue.orange.redis.mapping.OrangeRedisExecutorIdGenerator;
import com.langwuyue.orange.redis.operations.OrangeRedisZSetOperations;
/**
 * Executor for removing one or more members from a Redis Sorted Set (ZSET).
 *
 * <p>This executor provides functionality to remove members from a Redis Sorted Set by their values.
 * It supports removing either a single member or multiple members in one operation.
 *
 * <p>Key features:
 * <ul>
 *   <li>Removes specified members from the sorted set by their values</li>
 *   <li>Returns the number of members that were actually removed</li>
 *   <li>Ignores members that don't exist in the sorted set</li>
 *   <li>Supports both single and bulk removal operations</li>
 *   <li>Automatically handles type conversion of member values</li>
 *   <li>Only removes the members, their scores are automatically removed as well</li>
 * </ul>
 *
 * <p>Return value handling:
 * <ul>
 *   <li>Returns the number of members that were removed from the sorted set</li>
 *   <li>Returns 0 if none of the specified members existed in the sorted set</li>
 * </ul>
 *
 * <p>Performance considerations:
 * <ul>
 *   <li>Time complexity is O(M*log(N)) where N is the number of elements in the sorted set
 *       and M is the number of members to be removed</li>
 *   <li>Bulk removal is more efficient than multiple single removals</li>
 *   <li>Performance is generally good for moderate-sized sorted sets</li>
 * </ul>
 *
 * <p>Underlying Redis command:
 * This executor uses the ZREM command internally to perform the removal operation.
 *
 * <p>Comparison with Set removal:
 * <ul>
 *   <li>This executor works with sorted sets (with scores) while Set executor works with regular sets</li>
 *   <li>Both use similar removal semantics but with different underlying data structures</li>
 * </ul>
 *
 * @author Liang.Zhong
 * @since 1.0.0
 * @see OrangeRedisZSetOperations
 * @see com.langwuyue.orange.redis.executor.set.OrangeRemoveMembersExecutor
 */
public class OrangeRemoveMembersExecutor extends OrangeRemoveMembersAbstractExecutor {
	
	private OrangeRedisZSetOperations operations;

	public OrangeRemoveMembersExecutor(OrangeRedisZSetOperations operations,OrangeRedisExecutorIdGenerator idGenerator,OrangeRedisLogger logger) {
		super(idGenerator,logger);
		this.operations = operations;
	}

	/**
	 * Removes multiple members from the Redis Sorted Set in a batch operation.
	 *
	 * @param context the execution context containing:
	 *                - Redis key information
	 *                - Value type information
	 *                - Multiple values to be removed
	 * @return the number of members that were actually removed (including their scores)
	 * @throws Exception if:
	 *                   - context is not of type OrangeRedisMultipleValueContext
	 *                   - Redis operation fails
	 *                   - any other execution error occurs
	 */
	@Override
	protected Long doRemove(OrangeRedisContext context) throws Exception {
		OrangeRedisMultipleValueContext ctx = (OrangeRedisMultipleValueContext) context;
		return this.operations.remove(context.getRedisKey().getValue(), context.getValueType(), ctx.toArray());
	}

	/**
	 * Removes a single member (and its score) from the Redis Sorted Set.
	 *
	 * @param context the execution context containing:
	 *                - Redis key information
	 *                - Value type information
	 * @param value the member value to be removed
	 * @return 1 if the member was removed, 0 if it didn't exist
	 * @throws Exception if:
	 *                   - Redis operation fails
	 *                   - any other execution error occurs
	 */
	@Override
	protected Long doRemove(OrangeRedisContext context, Object value) throws Exception {
		return this.operations.remove(context.getRedisKey().getValue(), context.getValueType(), value);
	}
}