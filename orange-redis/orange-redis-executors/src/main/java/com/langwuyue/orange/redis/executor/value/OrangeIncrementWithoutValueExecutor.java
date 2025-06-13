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
package com.langwuyue.orange.redis.executor.value;

import java.lang.annotation.Annotation;
import java.util.List;

import com.langwuyue.orange.redis.annotation.Increment;
import com.langwuyue.orange.redis.context.OrangeRedisContext;
import com.langwuyue.orange.redis.executor.OrangeRedisAbstractExecutor;
import com.langwuyue.orange.redis.mapping.OrangeRedisExecutorIdGenerator;
import com.langwuyue.orange.redis.operations.OrangeRedisValueOperations;
import com.langwuyue.orange.redis.utils.OrangeCollectionUtils;

/**
 * Redis executor implementation for incrementing values by 1 in Redis.
 * 
 * <p>This executor is a specialized version of the increment executor that always
 * increments the value by 1. Unlike {@link OrangeIncrementExecutor}, it doesn't
 * require a value parameter and only needs the Redis key to perform the increment
 * operation.</p>
 * 
 * <p>This executor is particularly useful for simple counter scenarios where you
 * only need to increment a value by 1, such as page view counters, hit counters,
 * or any other use case where the increment value is always 1.</p>
 *
 * <p>It only supports the {@link Increment} annotation and does not require
 * the {@link RedisValue} annotation since the increment value is fixed.</p>
 *
 * @author Liang.Zhong
 * @since 1.0.0
 * @see OrangeIncrementExecutor
 */
public class OrangeIncrementWithoutValueExecutor extends OrangeRedisAbstractExecutor {
	
	/**
	 * Redis value operations instance used to perform increment operations.
	 * This provides access to Redis INCR command functionality for incrementing
	 * values by 1.
	 */
	private OrangeRedisValueOperations operations;
	
	/**
	 * Constructs a new OrangeIncrementWithoutValueExecutor with the required dependencies.
	 *
	 * @param operations The Redis value operations instance used to perform increment operations
	 * @param idGenerator The ID generator used to create unique identifiers for each
	 *                   executor instance, helping with tracking and debugging
	 */
	public OrangeIncrementWithoutValueExecutor(OrangeRedisValueOperations operations,OrangeRedisExecutorIdGenerator idGenerator) {
		super(idGenerator);
		this.operations = operations;
	}



	/**
	 * Returns the list of annotation classes supported by this executor.
	 * 
	 * <p>This executor only supports the {@link Increment} annotation, as it
	 * performs a fixed increment by 1 and does not require a value parameter.</p>
	 *
	 * @return A list containing only the Increment annotation class
	 */
	@Override
	public List<Class<? extends Annotation>> getSupportedAnnotationClasses() {
		return OrangeCollectionUtils.asList(Increment.class);
	}

	/**
	 * Executes the increment by 1 operation on a Redis value.
	 * 
	 * <p>Unlike the standard increment executor, this method always increments by 1
	 * and does not need to extract an increment value from the context.</p>
	 *
	 * @param context The OrangeRedisContext containing the key for the increment operation
	 * @return The new value after the increment operation (as a Long)
	 * @throws Exception If an error occurs during the execution of the Redis operation
	 */
	@Override
	public Object execute(OrangeRedisContext context) throws Exception {
		return operations.increment(context.getRedisKey().getValue(), 1);
	}
}