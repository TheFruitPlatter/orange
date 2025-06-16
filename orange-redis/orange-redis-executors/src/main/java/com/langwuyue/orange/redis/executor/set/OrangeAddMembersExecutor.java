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
package com.langwuyue.orange.redis.executor.set;

import com.langwuyue.orange.redis.context.OrangeRedisContext;
import com.langwuyue.orange.redis.context.OrangeRedisMultipleValueContext;
import com.langwuyue.orange.redis.executor.OrangeAddMembersAbstractExecutor;
import com.langwuyue.orange.redis.logger.OrangeRedisLogger;
import com.langwuyue.orange.redis.mapping.OrangeRedisExecutorIdGenerator;
import com.langwuyue.orange.redis.operations.OrangeRedisSetOperations;

/**
 * Executor implementation for adding multiple members to a Redis Set.
 * 
 * <p>This executor handles the SADD Redis command for adding multiple values to a Set at once.
 * It processes the values from a {@link OrangeRedisMultipleValueContext} and executes the
 * appropriate Redis operations to add them to the specified Set.
 * 
 * <p>The executor supports batch operations for efficiency when adding multiple members,
 * as well as single-value operations when needed. It returns the number of members that
 * were actually added to the Set (excluding members that were already present).
 * 
 * <p>This implementation delegates the actual Redis operations to an injected
 * {@link OrangeRedisSetOperations} instance, which handles the low-level Redis communication.
 *
 * @author Liang.Zhong
 * @since 1.0.0
 * @see OrangeAddMembersAbstractExecutor
 * @see OrangeRedisMultipleValueContext
 * @see OrangeRedisSetOperations
 */
public class OrangeAddMembersExecutor extends OrangeAddMembersAbstractExecutor {
	
	private OrangeRedisSetOperations operations;

	/**
	 * Constructs a new OrangeAddMembersExecutor with the specified dependencies.
	 * 
	 * @param operations the Redis Set operations implementation for performing Set operations
	 * @param idGenerator the generator for creating unique executor IDs
	 * @param logger the logger for recording debug information and trace IDs
	 */
	public OrangeAddMembersExecutor(OrangeRedisSetOperations operations,OrangeRedisExecutorIdGenerator idGenerator,OrangeRedisLogger logger) {
		super(idGenerator,logger);
		this.operations = operations;
	}

	/**
	 * Executes the operation to add multiple members to a Redis Set.
	 * 
	 * <p>This method casts the context to {@link OrangeRedisMultipleValueContext} to access
	 * multiple values, then delegates to the {@link OrangeRedisSetOperations#add} method
	 * to perform the actual Redis SADD operation with all values from the context.
	 *
	 * @param ctx the Redis operation context containing the key and multiple values to add
	 * @return the number of members that were actually added to the Set (not including members already present)
	 * @throws Exception if any error occurs during execution
	 */
	@Override
	protected Long doAdd(OrangeRedisContext ctx) throws Exception {
		OrangeRedisMultipleValueContext context = (OrangeRedisMultipleValueContext) ctx;
		return this.operations.add(context.getRedisKey().getValue(), context.getValueType(), context.toArray());
	}

	/**
	 * Executes the operation to add a single member to a Redis Set.
	 * 
	 * <p>This method delegates to the {@link OrangeRedisSetOperations#add} method
	 * to perform the actual Redis SADD operation with a single value.
	 *
	 * @param ctx the Redis operation context containing the key
	 * @param value the single value to add to the Set
	 * @return 1 if the member was added, 0 if the member was already present
	 * @throws Exception if any error occurs during execution
	 */
	@Override
	protected Long doAdd(OrangeRedisContext ctx, Object value) throws Exception {
		return this.operations.add(ctx.getRedisKey().getValue(), ctx.getValueType(), value);
	}
}