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
package com.langwuyue.orange.redis.executor.geo;

import com.langwuyue.orange.redis.context.OrangeRedisContext;
import com.langwuyue.orange.redis.context.OrangeRedisMultipleValueContext;
import com.langwuyue.orange.redis.executor.OrangeRemoveMembersAbstractExecutor;
import com.langwuyue.orange.redis.logger.OrangeRedisLogger;
import com.langwuyue.orange.redis.mapping.OrangeRedisExecutorIdGenerator;
import com.langwuyue.orange.redis.operations.OrangeRedisGeoOperations;

/**
 * Executor for removing multiple members from Redis GEO data structures.
 * 
 * <p>This executor handles batch removal of members from GEO sets. It extends
 * {@link OrangeRemoveMembersAbstractExecutor} to provide GEO-specific removal
 * operations using {@link OrangeRedisGeoOperations}.
 * 
 * <p>Supported operations:
 * <ul>
 *   <li>Batch removal of multiple members</li>
 *   <li>Single member removal with value validation</li>
 * </ul>
 *
 * @author Liang.Zhong
 * @since 1.0.0
 * @see <a href="https://orange.langwuyue.com/redis/advanced/geo">Orange Redis Geo Documentation</a>
 */
public class OrangeRemoveMembersExecutor extends OrangeRemoveMembersAbstractExecutor {
	
	private OrangeRedisGeoOperations operations;

	/**
	 * Constructs a new executor for GEO members removal operations.
	 * 
	 * @param operations the Redis GEO operations implementation
	 * @param idGenerator the executor ID generator
	 * @param logger the logger for operation logging
	 */
	public OrangeRemoveMembersExecutor(OrangeRedisGeoOperations operations,OrangeRedisExecutorIdGenerator idGenerator, OrangeRedisLogger logger) {
		super(idGenerator,logger);
		this.operations = operations;
	}

	/**
	 * Removes multiple members from a Redis GEO set.
	 * 
	 * @param ctx the execution context containing key and values
	 * @return the number of members actually removed
	 * @throws Exception if the operation fails
	 */
	@Override
	protected Long doRemove(OrangeRedisContext ctx) throws Exception {
		OrangeRedisMultipleValueContext context = (OrangeRedisMultipleValueContext) ctx;
		return this.operations.remove(ctx.getRedisKey().getValue(), context.getValueType(), context.toArray());
	}

	/**
	 * Removes a single member from a Redis GEO set.
	 * 
	 * @param ctx the execution context containing key and value type
	 * @param value the member to be removed
	 * @return 1 if the member was removed, 0 otherwise
	 * @throws Exception if the operation fails
	 */
	@Override
	protected Long doRemove(OrangeRedisContext ctx, Object value) throws Exception {
		return this.operations.remove(ctx.getRedisKey().getValue(), ctx.getValueType(), value);
	}
}