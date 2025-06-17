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

import java.lang.annotation.Annotation;
import java.util.List;

import com.langwuyue.orange.redis.annotation.GetSize;
import com.langwuyue.orange.redis.context.OrangeRedisContext;
import com.langwuyue.orange.redis.executor.OrangeRedisAbstractExecutor;
import com.langwuyue.orange.redis.mapping.OrangeRedisExecutorIdGenerator;
import com.langwuyue.orange.redis.operations.OrangeRedisListOperations;
import com.langwuyue.orange.redis.utils.OrangeCollectionUtils;

/**
 * Executor implementation for counting elements in a Redis list.
 * 
 * <p>This executor provides functionality to retrieve the size (number of elements)
 * of a Redis list identified by a specific key. It uses the Redis LIST data structure's
 * size operation to efficiently count elements without retrieving the actual data.
 * 
 * <p>The executor supports:
 * <ul>
 *   <li>Getting the total number of elements in a list</li>
 *   <li>Handling empty lists (returns 0)</li>
 *   <li>Non-existent keys (returns 0)</li>
 * </ul>
 *
 * @author Liang.Zhong
 * @since 1.0.0
 * @see <a href="https://orange.langwuyue.com/redis/advanced/list">Orange Redis List Documentation</a>
 */
public class OrangeCountExecutor extends OrangeRedisAbstractExecutor {
	
	private OrangeRedisListOperations operations;

	/**
	 * Constructs a new Count executor for Redis list operations.
	 * 
	 *
	 * @param operations the Redis list operations implementation
	 * @param idGenerator the ID generator for creating unique operation identifiers
	 */
	public OrangeCountExecutor(OrangeRedisListOperations operations,OrangeRedisExecutorIdGenerator idGenerator) {
		super(idGenerator);
		this.operations = operations;
	}

	/**
	 * Returns the list of annotation classes supported by this executor.
	 * 
	 * <p>This executor supports the following annotations:
	 * <ul>
	 *   <li>{@link GetSize} - Marks the method as a size/count operation for Redis lists</li>
	 * </ul>
	 *
	 * @return a list containing the GetSize annotation class
	 */
	@Override
	protected List<Class<? extends Annotation>> getSupportedAnnotationClasses() {
		return OrangeCollectionUtils.asList(GetSize.class);
	}
	
	/**
	 * Executes the count operation on a Redis list.
	 * 
	 * 
	 * <p>If the list doesn't exist, this method returns 0.
	 *
	 * @param context the Redis operation context containing the key
	 * @return a Long representing the number of elements in the list
	 * @throws Exception if an error occurs during the execution
	 */
	@Override
	public Object execute(OrangeRedisContext context) throws Exception {
		return this.operations.size(context.getRedisKey().getValue());
	}
}