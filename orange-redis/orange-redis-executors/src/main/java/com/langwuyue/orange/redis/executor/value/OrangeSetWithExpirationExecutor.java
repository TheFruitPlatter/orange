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
import com.langwuyue.orange.redis.annotation.global.SetExpiration;
import com.langwuyue.orange.redis.annotation.value.SetValue;
import com.langwuyue.orange.redis.context.OrangeRedisContext;
import com.langwuyue.orange.redis.context.OrangeRedisContext.Key;
import com.langwuyue.orange.redis.context.OrangeRedisValueContext;
import com.langwuyue.orange.redis.executor.OrangeRedisAbstractExecutor;
import com.langwuyue.orange.redis.mapping.OrangeRedisExecutorIdGenerator;
import com.langwuyue.orange.redis.operations.OrangeRedisValueOperations;
import com.langwuyue.orange.redis.utils.OrangeCollectionUtils;

/**
 * Redis executor that performs SET operations with expiration time.
 * 
 * <p>This executor handles Redis SET operations with expiration time, allowing values to be stored
 * in Redis with an automatic expiration. It processes {@link RedisValue}, {@link SetValue}, and
 * {@link SetExpiration} annotations to determine how to store values in Redis.</p>
 * 
 * <p>The executor sets a value in Redis and applies an expiration time to the key, after which
 * Redis will automatically remove the key-value pair. This is useful for caching scenarios,
 * temporary data storage, or any situation where data should be automatically removed after
 * a certain period.</p>
 *
 * @author Liang.Zhong
 * @since 1.0.0
 * @see OrangeRedisAbstractExecutor
 * @see RedisValue
 * @see SetValue
 * @see SetExpiration
 */
public class OrangeSetWithExpirationExecutor extends OrangeRedisAbstractExecutor {
	
	/**
	 * The Redis value operations used to interact with Redis.
	 * This field provides access to Redis commands for manipulating values.
	 */
	private OrangeRedisValueOperations operations;
	
	/**
	 * Constructs a new OrangeSetWithExpirationExecutor.
	 * 
	 * <p>This constructor initializes the executor with the necessary dependencies to perform
	 * SET operations with expiration time in Redis.</p>
	 *
	 * @param operations The Redis value operations used to interact with Redis
	 * @param idGenerator The generator used to create unique IDs for Redis operations
	 */
	public OrangeSetWithExpirationExecutor(OrangeRedisValueOperations operations,OrangeRedisExecutorIdGenerator idGenerator) {
		super(idGenerator);
		this.operations = operations;
	}

	/**
	 * Executes the SET operation with expiration in Redis.
	 * 
	 * <p>This method sets a value in Redis with an expiration time. It extracts the key, value,
	 * expiration time, and time unit from the context and uses the Redis operations to perform
	 * the SET command with these parameters.</p>
	 *
	 * @param context The context containing the key, value, and expiration settings for the operation
	 * @return null as this operation doesn't return a value
	 * @throws Exception If any error occurs during the Redis operation
	 */
	@Override
	public Object execute(OrangeRedisContext context) throws Exception {
		OrangeRedisValueContext ctx = (OrangeRedisValueContext)context;
		Key key = ctx.getRedisKey();
		this.operations.set(
				key.getValue(), 
				ctx.getValue(),
				key.getExpirationTime(), 
				key.getExpirationTimeUnit(),
				ctx.getValueType()
		);
		return null;
	}

	/**
	 * Returns the list of annotation classes that this executor supports.
	 * 
	 * <p>This executor supports three types of annotations:</p>
	 * <ul>
	 *   <li>{@link RedisValue} - Base annotation for Redis operations</li>
	 *   <li>{@link SetValue} - Specific annotation for SET operations</li>
	 *   <li>{@link SetExpiration} - Annotation for setting expiration time</li>
	 * </ul>
	 *
	 * @return A list containing {@link RedisValue}, {@link SetValue}, and {@link SetExpiration} classes
	 */
	@Override
	public List<Class<? extends Annotation>> getSupportedAnnotationClasses() {
		return OrangeCollectionUtils.asList(RedisValue.class, SetValue.class, SetExpiration.class);
	}

	/**
	 * Returns the context class that this executor uses.
	 * 
	 * <p>This executor uses {@link OrangeRedisValueContext} to store and retrieve
	 * the key, value, and expiration information needed for SET operations with expiration.</p>
	 *
	 * @return The {@link OrangeRedisValueContext} class
	 */
	@Override
	public Class<? extends OrangeRedisContext> getContextClass() {
		return OrangeRedisValueContext.class;
	}
}