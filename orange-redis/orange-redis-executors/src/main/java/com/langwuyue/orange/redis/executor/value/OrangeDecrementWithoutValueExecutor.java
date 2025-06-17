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

import com.langwuyue.orange.redis.annotation.Decrement;
import com.langwuyue.orange.redis.context.OrangeRedisContext;
import com.langwuyue.orange.redis.executor.OrangeRedisAbstractExecutor;
import com.langwuyue.orange.redis.mapping.OrangeRedisExecutorIdGenerator;
import com.langwuyue.orange.redis.operations.OrangeRedisValueOperations;
import com.langwuyue.orange.redis.utils.OrangeCollectionUtils;

/**
 * Redis executor implementation for simple decrement operations without specifying a value.
 * 
 * <p>This executor handles decrement operations on Redis numeric values, specifically
 * decreasing the value by exactly 1. Unlike the {@link OrangeDecrementExecutor}, this
 * executor does not allow specifying the decrement amount - it always decrements by 1.
 * 
 * <p>The executor is triggered by methods annotated with {@link Decrement} annotation
 * and decrements the value stored at the specified Redis key by 1.
 *
 * @author Liang.Zhong
 * @since 1.0.0
 * @see <a href="https://orange.langwuyue.com/redis/advanced/value">Orange Redis Value Documentation</a>
 */
public class OrangeDecrementWithoutValueExecutor extends OrangeRedisAbstractExecutor {
	
	/**
	 * Redis value operations instance used to perform decrement operations on Redis keys.
	 * This provides access to Redis commands like INCRBY (used with negative values).
	 */
	private OrangeRedisValueOperations operations;
	
	/**
	 * Constructs a new OrangeDecrementWithoutValueExecutor with the required dependencies.
	 *
	 * @param operations The Redis value operations instance used to perform decrement operations
	 * @param idGenerator The ID generator used to create unique identifiers for each
	 *                   executor instance, helping with tracking and debugging
	 */
	public OrangeDecrementWithoutValueExecutor(OrangeRedisValueOperations operations,OrangeRedisExecutorIdGenerator idGenerator) {
		super(idGenerator);
		this.operations = operations;
	}



	/**
	 * Returns the list of annotation classes supported by this executor.
	 * 
	 * <p>This executor only supports the {@link Decrement} annotation, which marks
	 * a method for simple decrement operations that decrease a value by 1.
	 * 
	 * <p>Unlike {@link OrangeDecrementExecutor}, this executor does not support
	 * the {@link RedisValue} annotation.
	 *
	 * @return A list containing only the Decrement annotation class
	 */
	@Override
	public List<Class<? extends Annotation>> getSupportedAnnotationClasses() {
		return OrangeCollectionUtils.asList(Decrement.class);
	}

	/**
	 * Executes the decrement operation on a Redis key, decreasing its value by 1.
	 *
	 * <p>Unlike {@link OrangeDecrementExecutor}, this executor always decrements by 1
	 * and does not return the new value after the operation.
	 *
	 * @param context The OrangeRedisContext containing the operation parameters
	 * @return null as this is a void operation
	 * @throws Exception If an error occurs during the execution of the Redis operation
	 */
	@Override
	public Object execute(OrangeRedisContext context) throws Exception {
		return operations.increment(context.getRedisKey().getValue(), -1);
	}
}