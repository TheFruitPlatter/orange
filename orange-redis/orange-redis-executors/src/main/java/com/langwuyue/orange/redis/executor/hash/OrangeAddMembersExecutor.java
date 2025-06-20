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

import java.util.Map;
import java.util.Map.Entry;

import com.langwuyue.orange.redis.context.OrangeRedisContext;
import com.langwuyue.orange.redis.executor.OrangeAddMembersAbstractExecutor;
import com.langwuyue.orange.redis.executor.hash.context.OrangeAddMembersContext;
import com.langwuyue.orange.redis.logger.OrangeRedisLogger;
import com.langwuyue.orange.redis.mapping.OrangeRedisExecutorIdGenerator;
import com.langwuyue.orange.redis.operations.OrangeRedisHashOperations;

/**
 * An executor that adds multiple members to a Redis hash.
 * 
 * <p>This executor extends {@link OrangeAddMembersAbstractExecutor} to provide
 * functionality for adding multiple key-value pairs to a Redis hash data structure.
 * It handles the conversion of input data to the appropriate types and delegates
 * the actual Redis operations to {@link OrangeRedisHashOperations}.
 *
 * <p>The executor supports two modes of operation:
 * <ul>
 *   <li>Adding multiple members at once using a Map</li>
 *   <li>Adding a single member using a Map.Entry</li>
 * </ul>
 *
 * @author Liang.Zhong
 * @since 1.0.0
 * @see OrangeAddMembersAbstractExecutor
 * @see OrangeRedisHashOperations
 * @see <a href="https://orange.langwuyue.com/redis/advanced/hash">Orange Redis Hash Documentation</a>
 */
public class OrangeAddMembersExecutor extends OrangeAddMembersAbstractExecutor {
	
	private OrangeRedisHashOperations operations;

	/**
	 * Constructs a new OrangeAddMembersExecutor.
	 *
	 * @param operations the Redis hash operations to be used for adding members
	 * @param idGenerator the generator for creating unique executor IDs
	 * @param logger the logger for recording operation events
	 */
	public OrangeAddMembersExecutor(OrangeRedisHashOperations operations,OrangeRedisExecutorIdGenerator idGenerator,OrangeRedisLogger logger) {
		super(idGenerator,logger);
		this.operations = operations;
	}

	/**
	 * Gets the context class used by this executor.
	 *
	 * @return the {@link OrangeAddMembersContext} class that defines the context
	 *         for adding multiple members to a Redis hash
	 */
	@Override
	public Class<? extends OrangeRedisContext> getContextClass() {
		return OrangeAddMembersContext.class;
	}

	/**
	 * Adds multiple members to a Redis hash.
	 *
	 * <p>This method extracts the members from the context and adds them to the Redis hash
	 * using the configured Redis hash operations.
	 *
	 * @param context the context containing the Redis key, members, and type information
	 * @return the number of members added to the hash
	 * @throws Exception if an error occurs during the Redis operation
	 */
	@Override
	protected Long doAdd(OrangeRedisContext context) throws Exception {
		OrangeAddMembersContext ctx = (OrangeAddMembersContext) context;
		Map members = ctx.getMembers();
		this.operations.putMembers(ctx.getRedisKey().getValue(), members, ctx.getKeyType(), ctx.getValueType());
		return (long) members.size();
	}

	/**
	 * Adds a single member to a Redis hash.
	 *
	 * <p>This method adds a single member (represented by a Map.Entry) to the Redis hash
	 * using the configured Redis hash operations.
	 *
	 * @param context the context containing the Redis key and type information
	 * @param value the Map.Entry containing the field name and value to be added
	 * @return 1 if the member was added successfully, 0 otherwise
	 * @throws Exception if an error occurs during the Redis operation or if the value
	 *         is not a valid Map.Entry
	 */
	@Override
	protected Long doAdd(OrangeRedisContext context, Object value) throws Exception {
		OrangeAddMembersContext ctx = (OrangeAddMembersContext) context;
		Entry entry = (Entry) value;
		this.operations.putMember(ctx.getRedisKey().getValue(), entry.getKey(), entry.getValue(), ctx.getKeyType(), ctx.getValueType());
		return 1L;
	}
}