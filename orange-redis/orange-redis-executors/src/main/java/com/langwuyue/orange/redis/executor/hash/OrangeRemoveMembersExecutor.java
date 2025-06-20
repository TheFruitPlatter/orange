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
package com.langwuyue.orange.redis.executor.hash;

import com.langwuyue.orange.redis.context.OrangeRedisContext;
import com.langwuyue.orange.redis.executor.OrangeRemoveMembersAbstractExecutor;
import com.langwuyue.orange.redis.executor.hash.context.OrangeHashKeysContext;
import com.langwuyue.orange.redis.logger.OrangeRedisLogger;
import com.langwuyue.orange.redis.mapping.OrangeRedisExecutorIdGenerator;
import com.langwuyue.orange.redis.operations.OrangeRedisHashOperations;

/**
 * Executor implementation for removing multiple members (fields) from a Redis hash.
 * 
 * <p>This executor handles the removal of multiple fields from a Redis hash in a single operation.
 * It processes methods annotated with appropriate annotations and converts the method parameters
 * into Redis hash removal operations.
 * 
 * @author Liang.Zhong
 * @since 1.0.0
 * @see <a href="https://orange.langwuyue.com/redis/advanced/hash">Orange Redis Hash Documentation</a>
 */
public class OrangeRemoveMembersExecutor extends OrangeRemoveMembersAbstractExecutor {
	
	/** Redis hash operations used by this executor for member removal operations */
	private OrangeRedisHashOperations operations;

	/**
	 * Constructs a new hash members removal executor with the required dependencies.
	 *
	 * @param operations the Redis hash operations component for performing removal operations
	 * @param idGenerator the executor ID generator for creating unique identifiers
	 * @param logger the Redis logger for operation logging
	 */
	public OrangeRemoveMembersExecutor(OrangeRedisHashOperations operations,OrangeRedisExecutorIdGenerator idGenerator,OrangeRedisLogger logger) {
		super(idGenerator,logger);
		this.operations = operations;
	}

	/**
	 * Returns the context class used by this executor.
	 *
	 * @return the {@link OrangeHashKeysContext} class
	 */
	@Override
	public Class<? extends OrangeRedisContext> getContextClass() {
		return OrangeHashKeysContext.class;
	}

	/**
	 * Performs the actual removal operation for multiple hash fields.
	 *
	 * @param ctx the Redis operation context containing the key and field information
	 * @return the number of fields that were removed from the hash
	 * @throws Exception if an error occurs during the removal operation
	 */
	@Override
	protected Long doRemove(OrangeRedisContext ctx) throws Exception {
		OrangeHashKeysContext context = (OrangeHashKeysContext) ctx;
		return this.operations.removeMembers(ctx.getRedisKey().getValue(), context.getKeyType(), context.toArray());
	}

	/**
	 * Performs the actual removal operation for hash fields with a specific value.
	 *
	 * @param ctx the Redis operation context containing the key information
	 * @param value the value containing the fields to be removed
	 * @return the number of fields that were removed from the hash
	 * @throws Exception if an error occurs during the removal operation
	 */
	@Override
	protected Long doRemove(OrangeRedisContext ctx, Object value) throws Exception {
		OrangeHashKeysContext context = (OrangeHashKeysContext) ctx;
		return this.operations.removeMembers(ctx.getRedisKey().getValue(), context.getKeyType(), value);
	}
}