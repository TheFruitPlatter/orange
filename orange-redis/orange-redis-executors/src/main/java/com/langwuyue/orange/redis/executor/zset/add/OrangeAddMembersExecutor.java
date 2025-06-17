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
package com.langwuyue.orange.redis.executor.zset.add;

import java.util.LinkedHashSet;
import java.util.Set;

import com.langwuyue.orange.redis.context.OrangeRedisContext;
import com.langwuyue.orange.redis.executor.OrangeAddMembersAbstractExecutor;
import com.langwuyue.orange.redis.executor.zset.context.OrangeAddMembersContext;
import com.langwuyue.orange.redis.logger.OrangeRedisLogger;
import com.langwuyue.orange.redis.mapping.OrangeRedisExecutorIdGenerator;
import com.langwuyue.orange.redis.operations.OrangeRedisZSetOperations;
import com.langwuyue.orange.redis.operations.OrangeRedisZSetOperations.ZSetEntry;

/**
 * Base executor implementation for batch adding members to ZSet.
 * 
 * <p>Features:
 * <ul>
 *   <li>Batch adds multiple members to Redis sorted set</li>
 *   <li>Supports configuring member values and scores via annotations</li>
 *   <li>Automatically handles different return value types (long)</li>
 *   <li>Supports both single and batch addition operations</li>
 * </ul>
 *
 * @author Liang.Zhong
 * @since 1.0.0
 * @see <a href="https://orange.langwuyue.com/redis/advanced/zset">Orange Redis ZSet Documentation</a>
 */
public class OrangeAddMembersExecutor extends OrangeAddMembersAbstractExecutor {

	private OrangeRedisZSetOperations operations;
	
	/**
	 * Constructs a ZSet batch addition executor.
	 *
	 * @param operations Redis ZSet operations interface implementation, must not be null
	 * @param idGenerator Executor ID generator for monitoring and tracing, must not be null
	 * @param logger Logger for execution logging, must not be null
	 */
	public OrangeAddMembersExecutor(OrangeRedisZSetOperations operations,OrangeRedisExecutorIdGenerator idGenerator,OrangeRedisLogger logger) {
		super(idGenerator,logger);
		this.operations = operations;
	}

	/**
	 * Gets the required context type for this executor.
	 *
	 * <p>Requires {@link OrangeAddMembersContext} to provide:
	 * <ul>
	 *   <li>Redis key information</li>
	 *   <li>Member collection</li>
	 *   <li>Value type information</li>
	 * </ul>
	 *
	 * @return Required context class
	 */
	@Override
	public Class<? extends OrangeRedisContext> getContextClass() {
		return OrangeAddMembersContext.class;
	}

	/**
	 * Executes batch member addition operation.
	 *
	 * <p>Implementation details:
	 * <ul>
	 *   <li>Gets member collection from context</li>
	 *   <li>Batch adds to Redis sorted set</li>
	 *   <li>Returns count of successfully added members</li>
	 * </ul>
	 *
	 * @param context Execution context containing Redis key and member collection info
	 * @return Count of successfully added members
	 * @throws Exception if Redis operation fails
	 */
	@Override
	protected Long doAdd(OrangeRedisContext context) throws Exception {
		OrangeAddMembersContext ctx = (OrangeAddMembersContext) context;
		return this.operations.add(context.getRedisKey().getValue(), ctx.getMembers(), context.getValueType());
	}

	/**
	 * Executes single member addition operation.
	 *
	 * <p>Implementation details:
	 * <ul>
	 *   <li>Wraps single member into a collection</li>
	 *   <li>Adds to Redis sorted set</li>
	 *   <li>Returns addition result (1 indicates success)</li>
	 * </ul>
	 *
	 * @param context Execution context containing Redis key information
	 * @param value Member object to add, must be of ZSetEntry type
	 * @return Addition result (1 indicates success)
	 * @throws Exception if Redis operation fails or type conversion fails
	 */
	@Override
	protected Long doAdd(OrangeRedisContext context, Object value) throws Exception {
		Set<ZSetEntry> entries = new LinkedHashSet<>(1);
		entries.add((ZSetEntry)value);
		return this.operations.add(context.getRedisKey().getValue(), entries, context.getValueType());
	}

}