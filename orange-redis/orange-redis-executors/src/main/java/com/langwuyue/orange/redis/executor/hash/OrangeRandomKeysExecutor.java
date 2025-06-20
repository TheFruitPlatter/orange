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
package com.langwuyue.orange.redis.executor.hash;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.langwuyue.orange.redis.annotation.Count;
import com.langwuyue.orange.redis.annotation.Random;
import com.langwuyue.orange.redis.context.OrangeRedisContext;
import com.langwuyue.orange.redis.executor.hash.context.OrangeCountHashContext;
import com.langwuyue.orange.redis.mapping.OrangeRedisExecutorIdGenerator;
import com.langwuyue.orange.redis.operations.OrangeRedisHashOperations;

/**
 * Executor for randomly retrieving keys from Redis hash.
 * Supports {@link Random} and {@link Count} annotations to control the random selection behavior.
 * 
 * @author Liang.Zhong
 * @since 1.0.0
 * @see <a href="https://orange.langwuyue.com/redis/advanced/hash">Orange Redis Hash Documentation</a>
 */
public class OrangeRandomKeysExecutor extends OrangeGetKeysExecutor {
	
	/**
	 * Redis hash operations instance for executing hash commands
	 */
	private OrangeRedisHashOperations operations;

	/**
	 * Constructs a new OrangeRandomKeysExecutor instance.
	 * 
	 * @param operations the Redis hash operations instance
	 * @param idGenerator the executor ID generator
	 */
	public OrangeRandomKeysExecutor(OrangeRedisHashOperations operations,OrangeRedisExecutorIdGenerator idGenerator) {
		super(operations,idGenerator);
		this.operations = operations;
	}

	/**
	 * Gets the list of supported annotation classes for this executor.
	 * Adds {@link Random} and {@link Count} annotations to the parent class's supported annotations.
	 *
	 * @return list of supported annotation classes
	 */
	@Override
	protected List<Class<? extends Annotation>> getSupportedAnnotationClasses() {
		List classes = super.getSupportedAnnotationClasses();
		classes.add(Random.class);
		classes.add(Count.class);
		return classes;
	}

	/**
	 * Gets the context class used by this executor.
	 * This executor uses {@link OrangeCountHashContext} as its context class.
	 *
	 * @return the context class
	 */
	@Override
	public Class<? extends OrangeRedisContext> getContextClass() {
		return OrangeCountHashContext.class;
	}
	
	/**
	 * Executes the random keys retrieval operation.
	 * Randomly selects keys from the hash based on the context parameters.
	 *
	 * @param context the Redis operation context containing parameters
	 * @param valueField the field being processed (may be null)
	 * @param returnArgumentType the expected return type
	 * @return collection of randomly selected keys
	 * @throws Exception if there's an error during Redis operation or reflection
	 */
	@Override
	protected Collection doGet(OrangeRedisContext context, Field valueField, Type returnArgumentType) throws Exception {
		OrangeCountHashContext ctx = (OrangeCountHashContext) context;
		List result = new ArrayList<>(ctx.getCount());
		for(int i = 0; i < ctx.getCount(); i++) {
			List keys = this.operations.randomKeys(ctx.getRedisKey().getValue(), 1, ctx.getKeyType(), valueField == null ? returnArgumentType : valueField.getGenericType());
			result.addAll(keys);
		}
		return result;
	}
}