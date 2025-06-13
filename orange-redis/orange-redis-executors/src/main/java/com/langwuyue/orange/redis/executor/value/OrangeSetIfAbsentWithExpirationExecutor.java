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
import java.util.Collection;
import java.util.List;

import com.langwuyue.orange.redis.annotation.global.SetExpiration;
import com.langwuyue.orange.redis.context.OrangeRedisContext.Key;
import com.langwuyue.orange.redis.listener.OrangeRedisSetIfAbsentListener;
import com.langwuyue.orange.redis.context.OrangeRedisValueContext;
import com.langwuyue.orange.redis.mapping.OrangeRedisExecutorIdGenerator;
import com.langwuyue.orange.redis.operations.OrangeRedisValueOperations;

/**
 * Redis executor that performs SETNX (SET IF NOT EXISTS) operations with expiration time.
 * 
 * <p>This executor extends {@link OrangeSetIfAbsentExecutor} to add support for setting
 * expiration time on keys when performing SETNX operations. It allows setting a value in Redis
 * only if the key doesn't already exist, and additionally sets an expiration time on the key.</p>
 * 
 * <p>This is particularly useful for implementing distributed locks with automatic expiration,
 * temporary caches, or any scenario where you need to ensure a key doesn't exist before setting it
 * and want it to automatically expire after a certain period.</p>
 * 
 * <p>The executor supports the {@link SetExpiration} annotation in addition to the annotations
 * supported by the parent class.</p>
 *
 * @author Liang.Zhong
 * @since 1.0.0
 * @see OrangeSetIfAbsentExecutor
 * @see SetExpiration
 */
public class OrangeSetIfAbsentWithExpirationExecutor extends OrangeSetIfAbsentExecutor {
	
	/**
	 * Constructs a new OrangeSetIfAbsentWithExpirationExecutor.
	 * 
	 * <p>This constructor initializes the executor with the necessary dependencies to perform
	 * SETNX operations with expiration time in Redis.</p>
	 *
	 * @param operations The Redis value operations used to interact with Redis
	 * @param idGenerator The generator used to create unique IDs for Redis operations
	 * @param listeners A collection of listeners that will be notified of SETNX operation events
	 */
	public OrangeSetIfAbsentWithExpirationExecutor(
		OrangeRedisValueOperations operations,
		OrangeRedisExecutorIdGenerator idGenerator,
		Collection<OrangeRedisSetIfAbsentListener> listeners
	) {
		super(operations,idGenerator,listeners);
	}

	/**
	 * Executes the SETNX (SET IF NOT EXISTS) operation with expiration in Redis.
	 * 
	 * <p>This method overrides the parent class implementation to add support for setting
	 * an expiration time on the key when performing the SETNX operation. It uses the
	 * {@link OrangeRedisValueOperations#setIfAbsent} method to perform the actual Redis operation,
	 * passing the expiration time and time unit from the Redis key.</p>
	 *
	 * @param ctx The context containing the key, value, and expiration settings for the operation
	 * @return A Boolean indicating whether the operation was successful (true if the key
	 *         did not exist and the value was set with expiration)
	 * @throws Exception If any error occurs during the Redis operation
	 */
	@Override
	protected Boolean executeIfAsent(OrangeRedisValueContext ctx) throws Exception {
		Key key = ctx.getRedisKey();
		return this.getOperations().setIfAbsent(
				key.getValue(), 
				ctx.getValue(), 
				key.getExpirationTime(), 
				key.getExpirationTimeUnit(), 
				ctx.getValueType()
		);
	}

	/**
	 * Returns the list of annotation classes that this executor supports.
	 * 
	 * <p>This method extends the parent class implementation to add support for the
	 * {@link SetExpiration} annotation, which allows specifying an expiration time
	 * for the Redis key.</p>
	 * 
	 * <p>The supported annotations include all those from the parent class plus
	 * the {@link SetExpiration} annotation.</p>
	 *
	 * @return A list containing all supported annotation classes
	 */
	@Override
	protected List<Class<? extends Annotation>> getSupportedAnnotationClasses() {
		List<Class<? extends Annotation>> classes = super.getSupportedAnnotationClasses();
		classes.add(SetExpiration.class);
		return classes;
	}

}