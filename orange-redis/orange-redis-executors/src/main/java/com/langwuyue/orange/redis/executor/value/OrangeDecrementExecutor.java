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

import com.langwuyue.orange.redis.OrangeRedisException;
import com.langwuyue.orange.redis.annotation.Decrement;
import com.langwuyue.orange.redis.annotation.RedisValue;
import com.langwuyue.orange.redis.context.OrangeRedisContext;
import com.langwuyue.orange.redis.context.OrangeRedisValueContext;
import com.langwuyue.orange.redis.executor.OrangeRedisAbstractExecutor;
import com.langwuyue.orange.redis.mapping.OrangeRedisExecutorIdGenerator;
import com.langwuyue.orange.redis.operations.OrangeRedisValueOperations;
import com.langwuyue.orange.redis.utils.OrangeCollectionUtils;
import com.langwuyue.orange.redis.utils.OrangeReflectionUtils;

/**
 * Redis executor implementation for decrement operations.
 * 
 * <p>This executor handles the decrement operations on Redis numeric values. It supports
 * both integer and floating-point decrements, automatically detecting the value type
 * and applying the appropriate Redis operation.
 *
 * @author Liang.Zhong
 * @since 1.0.0
 * @see <a href="https://orange.langwuyue.com/redis/advanced/value">Orange Redis Value Documentation</a>
 */
public class OrangeDecrementExecutor extends OrangeRedisAbstractExecutor {
	
	/**
	 * Redis value operations instance used to perform decrement operations on Redis keys.
	 * This provides access to Redis commands like INCRBY and INCRBYFLOAT (used with negative values).
	 */
	private OrangeRedisValueOperations operations;
	
	/**
	 * Constructs a new OrangeDecrementExecutor with the required dependencies.
	 *
	 * @param operations The Redis value operations instance used to perform decrement operations
	 * @param idGenerator The ID generator used to create unique identifiers for each
	 *                   executor instance, helping with tracking and debugging
	 */
	public OrangeDecrementExecutor(OrangeRedisValueOperations operations,OrangeRedisExecutorIdGenerator idGenerator) {
		super(idGenerator);
		this.operations = operations;
	}



	/**
	 * Returns the list of annotation classes supported by this executor.
	 * 
	 * <p>This executor supports the following annotations:
	 * <ul>
	 *   <li>{@link Decrement}: Specifically marks a method for decrement operations</li>
	 *   <li>{@link RedisValue}: General annotation for Redis value operations that can be
	 *       configured for decrement operations</li>
	 * </ul>
	 *
	 * @return A list containing the Decrement and RedisValue annotation classes
	 */
	@Override
	public List<Class<? extends Annotation>> getSupportedAnnotationClasses() {
		return OrangeCollectionUtils.asList(Decrement.class,RedisValue.class);
	}

	/**
	 * Executes the decrement operation on a Redis key.
	 * 
	 * @param context The OrangeRedisContext containing the operation parameters,
	 *                must be an instance of OrangeDecrementContext
	 * @return The new value after the decrement operation
	 * @throws Exception If an error occurs during the execution of the Redis operation
	 */
	@Override
	public Object execute(OrangeRedisContext context) throws Exception {
		OrangeRedisValueContext ctx = (OrangeRedisValueContext)context;
		Object value = ctx.getValue();
		if(OrangeReflectionUtils.isFloat(value.getClass())) {
			return operations.increment(ctx.getRedisKey().getValue(),-(Double.valueOf(value.toString())));
		}else if(OrangeReflectionUtils.isInteger(value.getClass())) {
			return operations.increment(ctx.getRedisKey().getValue(), -(Long.valueOf(value.toString())));	
		}
		throw new OrangeRedisException("The value type of the decrement operation must be either a double or a long");
	}



	/**
	 * Returns the context class used by this executor.
	 * 
	 * <p>This executor uses the {@link OrangeDecrementContext} class to store
	 * and manage the parameters required for the decrement operation, including
	 * the key and the decrement value.
	 *
	 * @return The OrangeDecrementContext class
	 */
	@Override
	public Class<? extends OrangeRedisContext> getContextClass() {
		return OrangeRedisValueContext.class;
	}
}