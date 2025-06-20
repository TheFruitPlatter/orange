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

import java.util.ArrayList;
import java.util.List;

import com.langwuyue.orange.redis.context.OrangeRedisContext;
import com.langwuyue.orange.redis.executor.OrangeAddMembersAbstractExecutor;
import com.langwuyue.orange.redis.executor.geo.context.OrangeAddMembersContext;
import com.langwuyue.orange.redis.logger.OrangeRedisLogger;
import com.langwuyue.orange.redis.mapping.OrangeRedisExecutorIdGenerator;
import com.langwuyue.orange.redis.operations.OrangeRedisGeoOperations;
import com.langwuyue.orange.redis.operations.OrangeRedisGeoOperations.GeoEntry;

/**
 * Executor for adding multiple geo members with coordinates to a Redis geo set.
 * This executor handles the addition of multiple geo points with longitude and latitude
 * to a Redis geo set using the AddMembers annotation.
 *
 * @author Liang.Zhong
 * @since 1.0.0
 * @see <a href="https://orange.langwuyue.com/redis/advanced/geo">Orange Redis Geo Documentation</a>
 */
public class OrangeAddMembersExecutor extends OrangeAddMembersAbstractExecutor {
	
	private OrangeRedisGeoOperations operations;

	/**
	 * Constructs a new OrangeAddMembersExecutor.
	 *
	 * @param operations the Redis geo operations instance to perform geo-related operations
	 * @param idGenerator the executor ID generator for generating unique executor identifiers
	 * @param logger the Redis logger for logging operations and errors
	 */
	public OrangeAddMembersExecutor(OrangeRedisGeoOperations operations,OrangeRedisExecutorIdGenerator idGenerator,OrangeRedisLogger logger) {
		super(idGenerator,logger);
		this.operations = operations;
	}

	/**
	 * Returns the context class that this executor uses.
	 *
	 * @return the OrangeAddMembersContext class for handling multiple geo members addition operations
	 */
	@Override
	public Class<? extends OrangeRedisContext> getContextClass() {
		return OrangeAddMembersContext.class;
	}

	/**
	 * Performs the geo add operation for multiple members with their coordinates.
	 * This method processes the context and adds multiple geo points to the specified Redis geo set.
	 *
	 * @param ctx the Redis context containing the key and list of geo points to be added
	 * @return Long value indicating the number of elements added to the geo set
	 * @throws Exception if an error occurs during the operation
	 */
	@Override
	protected Long doAdd(OrangeRedisContext ctx) throws Exception {
		OrangeAddMembersContext context = (OrangeAddMembersContext) ctx;
		return this.operations.add(context.getRedisKey().getValue(), context.getMembers(), ctx.getValueType());
	}

	/**
	 * Performs the geo add operation for a single member with its coordinates.
	 * This method processes the context and adds a single geo point to the specified Redis geo set.
	 *
	 * @param ctx the Redis context containing the key and geo point to be added
	 * @param value the GeoEntry object containing the member and its coordinates
	 * @return Long value indicating the number of elements added to the geo set
	 * @throws Exception if an error occurs during the operation
	 */
	@Override
	protected Long doAdd(OrangeRedisContext ctx, Object value) throws Exception {
		List<GeoEntry> entries = new ArrayList<>();
		entries.add((GeoEntry) value);
		return this.operations.add(ctx.getRedisKey().getValue(), entries, ctx.getValueType());
	}
}