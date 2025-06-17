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

import com.langwuyue.orange.redis.annotation.value.GetValue;
import com.langwuyue.orange.redis.context.OrangeRedisContext;
import com.langwuyue.orange.redis.executor.OrangeRedisAbstractExecutor;
import com.langwuyue.orange.redis.mapping.OrangeRedisExecutorIdGenerator;
import com.langwuyue.orange.redis.operations.OrangeRedisValueOperations;
import com.langwuyue.orange.redis.utils.OrangeCollectionUtils;

/**
 * Redis executor implementation for retrieving values from Redis.
 * 
 * <p>This executor handles operations to fetch values stored in Redis using a key.
 * It supports methods annotated with {@link GetValue} annotation and retrieves
 * the value associated with the specified Redis key.
 * 
 * <p>The executor uses Redis GET operation to retrieve values and supports automatic
 * conversion of the retrieved value to the method's return type.
 *
 * @author Liang.Zhong
 * @since 1.0.0
 * @see <a href="https://orange.langwuyue.com/redis/advanced/value">Orange Redis Value Documentation</a>
 */
public class OrangeGetExecutor extends OrangeRedisAbstractExecutor {
	
	/**
	 * Redis value operations instance used to perform get operations on Redis keys.
	 * This provides access to Redis GET command functionality.
	 */
	private OrangeRedisValueOperations operations;
	
	/**
	 * Constructs a new OrangeGetExecutor with the required dependencies.
	 *
	 * @param operations The Redis value operations instance used to perform get operations
	 * @param idGenerator The ID generator used to create unique identifiers for each
	 *                   executor instance, helping with tracking and debugging
	 */
	public OrangeGetExecutor(OrangeRedisValueOperations operations,OrangeRedisExecutorIdGenerator idGenerator) {
		super(idGenerator);
		this.operations = operations;
	}

	/**
	 * Executes the get operation to retrieve a value from Redis.
	 * 
	 *
	 * <p>The method handles the conversion of the Redis string value to the appropriate
	 * return type expected by the annotated method.
	 *
	 * @param context The OrangeRedisContext containing the operation parameters
	 * @return The value retrieved from Redis, or null if the key doesn't exist
	 * @throws Exception If an error occurs during the execution of the Redis operation
	 */
	@Override
	public Object execute(OrangeRedisContext context) throws Exception {
		return this.operations.get(context.getRedisKey().getValue(), context.getValueType(), context.getOperationMethod().getGenericReturnType());
	}

	/**
	 * Returns the list of annotation classes supported by this executor.
	 * 
	 * <p>This executor supports the {@link GetValue} annotation, which marks
	 * a method for retrieving values from Redis.
	 *
	 * @return A list containing only the GetValue annotation class
	 */
	@Override
	public List<Class<? extends Annotation>> getSupportedAnnotationClasses() {
		return OrangeCollectionUtils.asList(GetValue.class);
	}
	
}