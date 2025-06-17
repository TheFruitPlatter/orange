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
package com.langwuyue.orange.redis.executor.list;

import com.langwuyue.orange.redis.context.OrangeRedisContext;
import com.langwuyue.orange.redis.executor.OrangeGetIndexAbstractExecutor;
import com.langwuyue.orange.redis.logger.OrangeRedisLogger;
import com.langwuyue.orange.redis.mapping.OrangeRedisExecutorIdGenerator;
import com.langwuyue.orange.redis.operations.OrangeRedisListOperations;

/**
 * Executor implementation for retrieving multiple indices of values in a Redis list.
 * 
 * <p>This executor extends the abstract {@link OrangeGetIndexAbstractExecutor} class
 * and provides specific implementation for finding indices of multiple values in a Redis list.
 * It uses the {@link OrangeRedisListOperations} to perform the actual Redis operations.
 * 
 * <p>Unlike {@link OrangeGetIndexExecutor} which retrieves a single index, this executor
 * is designed to handle multiple index retrievals in a batch operation.
 *
 * @author Liang.Zhong
 * @since 1.0.0
 * @see <a href="https://orange.langwuyue.com/redis/advanced/list">Orange Redis List Documentation</a>
 */
public class OrangeGetIndexsExecutor extends OrangeGetIndexAbstractExecutor {
	
	/**
	 * Redis list operations implementation used to perform index retrieval operations.
	 * This field provides access to the underlying Redis commands for list manipulation.
	 */
	private OrangeRedisListOperations operations;

	/**
	 * Constructs a new OrangeGetIndexsExecutor instance.
	 *
	 * @param operations the Redis list operations implementation to use for executing commands
	 * @param idGenerator the generator for creating unique executor IDs
	 * @param logger the logger for recording execution information and errors
	 */
	public OrangeGetIndexsExecutor(OrangeRedisListOperations operations,OrangeRedisExecutorIdGenerator idGenerator,OrangeRedisLogger logger) {
		super(idGenerator,logger);
		this.operations = operations;
	}

	/**
	 * Retrieves the index of a specific value in a Redis list.
	 * 
	 * <p>This method is called by the parent class for each value that needs to be
	 * looked up in the Redis list. It delegates the actual index retrieval operation
	 * to the {@link OrangeRedisListOperations#indexOf} method.
	 *
	 * @param context the Redis context containing the key and other operation parameters
	 * @param value the value to find in the list
	 * @return the index of the value in the list, or null if not found
	 * @throws Exception if an error occurs during the Redis operation
	 */
	@Override
	protected Long getIndex(OrangeRedisContext context, Object value) throws Exception {
		return this.operations.indexOf(context.getRedisKey().getValue(), value, context.getValueType());
	}
}