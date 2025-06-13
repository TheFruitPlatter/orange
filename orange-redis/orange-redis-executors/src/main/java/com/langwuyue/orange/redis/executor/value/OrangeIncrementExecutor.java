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
import com.langwuyue.orange.redis.annotation.Increment;
import com.langwuyue.orange.redis.annotation.RedisValue;
import com.langwuyue.orange.redis.context.OrangeRedisContext;
import com.langwuyue.orange.redis.context.OrangeRedisValueContext;
import com.langwuyue.orange.redis.executor.OrangeRedisAbstractExecutor;
import com.langwuyue.orange.redis.mapping.OrangeRedisExecutorIdGenerator;
import com.langwuyue.orange.redis.operations.OrangeRedisValueOperations;
import com.langwuyue.orange.redis.utils.OrangeCollectionUtils;
import com.langwuyue.orange.redis.utils.OrangeReflectionUtils;

/**
 * Redis executor implementation for incrementing values in Redis.
 * 
 * <p>This executor handles operations to increment numeric values stored in Redis.
 * It supports methods annotated with {@link Increment} and {@link RedisValue} annotations
 * and increments the value associated with the specified Redis key.</p>
 * 
 * <p>The executor supports incrementing both integer values (using Redis INCRBY)
 * and floating-point values (using Redis INCRBYFLOAT). The type of increment
 * operation is determined automatically based on the value type provided.</p>
 *
 * @author Liang.Zhong
 * @since 1.0.0
 */
public class OrangeIncrementExecutor extends OrangeRedisAbstractExecutor {
	
	/**
	 * Redis value operations instance used to perform increment operations on Redis keys.
	 * This provides access to Redis INCRBY and INCRBYFLOAT command functionality.
	 */
	private OrangeRedisValueOperations operations;
	
	/**
	 * Constructs a new OrangeIncrementExecutor with the required dependencies.
	 *
	 * @param operations The Redis value operations instance used to perform increment operations
	 * @param idGenerator The ID generator used to create unique identifiers for each
	 *                   executor instance, helping with tracking and debugging
	 */
	public OrangeIncrementExecutor(OrangeRedisValueOperations operations,OrangeRedisExecutorIdGenerator idGenerator) {
		super(idGenerator);
		this.operations = operations;
	}



	/**
	 * Returns the list of annotation classes supported by this executor.
	 * 
	 * <p>This executor supports two annotations:</p>
	 * <ul>
	 *   <li>{@link Increment} - Marks a method for incrementing values in Redis</li>
	 *   <li>{@link RedisValue} - Used to specify the increment value</li>
	 * </ul>
	 *
	 * @return A list containing the Increment and RedisValue annotation classes
	 */
	@Override
	public List<Class<? extends Annotation>> getSupportedAnnotationClasses() {
		return OrangeCollectionUtils.asList(Increment.class,RedisValue.class);
	}

	/**
	 * Executes the increment operation on a Redis value.
	 * 
	 * <p>The method supports both integer and floating-point increments, automatically
	 * choosing the appropriate Redis command based on the value type:</p>
	 * <ul>
	 *   <li>For floating-point values: Uses INCRBYFLOAT command</li>
	 *   <li>For integer values: Uses INCRBY command</li>
	 * </ul>
	 *
	 * @param context The OrangeRedisContext containing the key and increment value
	 * @return The new value after the increment operation
	 * @throws OrangeRedisException If the value type is neither double nor long
	 * @throws Exception If an error occurs during the execution of the Redis operation
	 */
	@Override
	public Object execute(OrangeRedisContext context) throws Exception {
		OrangeRedisValueContext ctx = (OrangeRedisValueContext)context;
		Object value = ctx.getValue();
		if(OrangeReflectionUtils.isFloat(value.getClass())) {
			return operations.increment(ctx.getRedisKey().getValue(),Double.valueOf(value.toString()));
		}else if(OrangeReflectionUtils.isInteger(value.getClass())) {
			return operations.increment(ctx.getRedisKey().getValue(),Long.valueOf(value.toString()));	
		}
		throw new OrangeRedisException("The value type of the increment operation must be either a double or a long");
	}



	/**
	 * Returns the context class that this executor can process.
	 * 
	 * <p>This executor works with {@link OrangeRedisValueContext} which contains
	 * both the Redis key and the increment value needed for the operation.</p>
	 *
	 * @return The OrangeRedisValueContext class
	 */
	@Override
	public Class<? extends OrangeRedisContext> getContextClass() {
		return OrangeRedisValueContext.class;
	}
}