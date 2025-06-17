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

import com.langwuyue.orange.redis.annotation.zset.Reverse;
import com.langwuyue.orange.redis.context.OrangeRedisContext;
import com.langwuyue.orange.redis.executor.OrangeGetIndexAbstractExecutor;
import com.langwuyue.orange.redis.logger.OrangeRedisLogger;
import com.langwuyue.orange.redis.mapping.OrangeRedisExecutorIdGenerator;
import com.langwuyue.orange.redis.operations.OrangeRedisListOperations;

/**
 * Executor implementation for retrieving the last indexes of multiple values in a Redis list.
 * 
 * <p>This executor performs reverse index lookups (from right to left) for multiple values
 * in a Redis list. It extends the {@link OrangeGetIndexAbstractExecutor} to provide
 * functionality specific to reverse index operations.
 * 
 * <p>The executor requires the {@link Reverse} annotation to be present on the method,
 * indicating that the search should be performed from right to left (last to first).
 *
 * @author Liang.Zhong
 * @since 1.0.0
 * @see <a href="https://orange.langwuyue.com/redis/advanced/list">Orange Redis List Documentation</a>
 */
public class OrangeReverseGetIndexsExecutor extends OrangeGetIndexAbstractExecutor {
	
	/**
	 * Redis list operations handler that provides the core functionality for list manipulation.
	 */
	private OrangeRedisListOperations operations;

	/**
	 * Constructs a new OrangeReverseGetIndexsExecutor.
	 *
	 * @param operations the Redis list operations handler for performing list-specific operations
	 * @param idGenerator generator for creating unique executor identifiers
	 * @param logger logger instance for recording execution events and errors
	 */
	public OrangeReverseGetIndexsExecutor(OrangeRedisListOperations operations,OrangeRedisExecutorIdGenerator idGenerator,OrangeRedisLogger logger) {
		super(idGenerator,logger);
		this.operations = operations;
	}

	/**
	 * Retrieves the last index of a specific value in the Redis list.
	 * 
	 * <p>This method performs a reverse search (from right to left) in the Redis list
	 * to find the last occurrence of the specified value. The search is performed using
	 * the {@code lastIndexOf} operation from the Redis list operations.
	 *
	 * @param context the execution context containing the Redis key and other parameters
	 * @param value the value to search for in the list
	 * @return the index of the last occurrence of the value in the list (0-based, from right to left),
	 *         or -1 if the value is not found
	 * @throws Exception if an error occurs during the execution
	 */
	@Override
	protected Long getIndex(OrangeRedisContext context, Object value) throws Exception {
		return this.operations.lastIndexOf(context.getRedisKey().getValue(), value, context.getValueType());
	}
	
	/**
	 * Returns the list of annotation classes supported by this executor.
	 * 
	 * <p>This method extends the list of supported annotations from the parent class
	 * by adding the {@link Reverse} annotation, which indicates that the index lookup
	 * should be performed from right to left (last to first).
	 *
	 * @return a list of annotation classes that this executor supports
	 */
	@Override
	protected List<Class<? extends Annotation>> getSupportedAnnotationClasses() {
		List classes = super.getSupportedAnnotationClasses();
		classes.add(Reverse.class);
		return classes;
	}
}