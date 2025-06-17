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

import com.langwuyue.orange.redis.annotation.RedisValue;
import com.langwuyue.orange.redis.annotation.value.SetValue;
import com.langwuyue.orange.redis.context.OrangeRedisContext;
import com.langwuyue.orange.redis.context.OrangeRedisValueContext;
import com.langwuyue.orange.redis.executor.OrangeRedisAbstractExecutor;
import com.langwuyue.orange.redis.mapping.OrangeRedisExecutorIdGenerator;
import com.langwuyue.orange.redis.operations.OrangeRedisValueOperations;
import com.langwuyue.orange.redis.utils.OrangeCollectionUtils;
import com.langwuyue.orange.redis.utils.OrangeReflectionUtils;

/**
 * Redis executor implementation for setting values in Redis.
 * 
 * <p>This executor handles the SET operation in Redis, which stores a value
 * with the specified key. It supports both {@link RedisValue} and {@link SetValue}
 * annotations to mark methods that should perform Redis SET operations.
 * 
 * <p>The executor takes a key and value from the context and stores the value
 * in Redis under the specified key. It can handle different return types from
 * the annotated methods, including boolean and integer types.
 *
 * @author Liang.Zhong
 * @since 1.0.0
 * @see RedisValue
 * @see SetValue
 * @see OrangeRedisValueContext
 * @see <a href="https://orange.langwuyue.com/redis/advanced/value">Orange Redis Value Documentation</a>
 */
public class OrangeSetExecutor extends OrangeRedisAbstractExecutor {
	
	/**
	 * Redis value operations instance used to perform SET operations.
	 * This provides access to Redis SET command functionality for storing
	 * values with specified keys.
	 */
	private OrangeRedisValueOperations operations;
	
	/**
	 * Constructs a new OrangeSetExecutor with the required dependencies.
	 *
	 * @param operations The Redis value operations instance used to perform SET operations
	 * @param idGenerator The ID generator used to create unique identifiers for each
	 *                   executor instance, helping with tracking and debugging
	 */
	public OrangeSetExecutor(OrangeRedisValueOperations operations,OrangeRedisExecutorIdGenerator idGenerator) {
		super(idGenerator);
		this.operations = operations;
	}

	/**
	 * Executes the SET operation in Redis using the provided context.
	 * 
	 * @param context The context containing the key and value for the SET operation
	 * @return The result of the SET operation, typically Boolean.TRUE indicating success
	 * @throws Exception If an error occurs during the execution of the Redis operation
	 *                   or if the context is not of the expected type
	 */
	@Override
	public Object execute(OrangeRedisContext context) throws Exception {
		OrangeRedisValueContext ctx = (OrangeRedisValueContext)context;
		this.operations.set(ctx.getRedisKey().getValue(),ctx.getValue(),ctx.getValueType());
		Class<?> returnClass = context.getOperationMethod().getReturnType();
		if(returnClass == Boolean.class || returnClass == boolean.class) {
			return Boolean.TRUE;	
		}
		if(OrangeReflectionUtils.isInteger(returnClass)) {
			return 1;	
		}
		return null;
	}

	/**
	 * Returns the list of annotation classes that this executor supports.
	 * 
	 * <p>This executor supports two annotations:
	 * <ul>
	 *   <li>{@link RedisValue} - The general Redis value operation annotation</li>
	 *   <li>{@link SetValue} - The specific annotation for SET operations</li>
	 * </ul>
	 * 
	 * <p>Methods annotated with either of these annotations can be processed
	 * by this executor to perform Redis SET operations.
	 *
	 * @return A list containing the RedisValue and SetValue annotation classes
	 */
	@Override
	public List<Class<? extends Annotation>> getSupportedAnnotationClasses() {
		return OrangeCollectionUtils.asList(RedisValue.class, SetValue.class);
	}

	/**
	 * Returns the context class that this executor requires for operation.
	 * 
	 * <p>This executor specifically requires an {@link OrangeRedisValueContext}
	 * because it needs both a key and a value to perform the SET operation in Redis.
	 * 
	 * <p>The OrangeRedisValueContext provides methods to access both the key
	 * and the value that should be stored in Redis.
	 *
	 * @return The OrangeRedisValueContext class
	 */
	@Override
	public Class<? extends OrangeRedisContext> getContextClass() {
		return OrangeRedisValueContext.class;
	}
}