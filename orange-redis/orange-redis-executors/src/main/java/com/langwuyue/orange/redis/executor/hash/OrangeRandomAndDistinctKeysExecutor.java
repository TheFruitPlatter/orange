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
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;

import com.langwuyue.orange.redis.annotation.Distinct;
import com.langwuyue.orange.redis.context.OrangeRedisContext;
import com.langwuyue.orange.redis.executor.hash.context.OrangeCountHashContext;
import com.langwuyue.orange.redis.mapping.OrangeRedisExecutorIdGenerator;
import com.langwuyue.orange.redis.operations.OrangeRedisHashOperations;

/**
 * An executor that retrieves random and distinct keys from Redis hash structures.
 * This executor extends the functionality of {@link OrangeRandomKeysExecutor} by ensuring
 * that the returned keys are unique (distinct) by using a {@link LinkedHashSet} to store
 * the results.
 * 
 * <p>This executor supports the {@link Distinct} annotation in addition to annotations
 * supported by the parent class.</p>
 * 
 * <p>The context used by this executor is {@link OrangeCountHashContext}, which provides
 * information about the Redis key and the count of random keys to retrieve.</p>
 *
 * @author Liang.Zhong
 * @since 1.0.0
 * @see <a href="https://orange.langwuyue.com/redis/advanced/hash">Orange Redis Hash Documentation</a>
 */
public class OrangeRandomAndDistinctKeysExecutor extends OrangeRandomKeysExecutor {
	
	/**
	 * Redis hash operations instance used for executing hash-related commands.
	 */
	private OrangeRedisHashOperations operations;

	/**
	 * Constructs a new OrangeRandomAndDistinctKeysExecutor with the specified operations and ID generator.
	 *
	 * @param operations the Redis hash operations to use for executing commands
	 * @param idGenerator the generator used for creating executor IDs
	 */
	public OrangeRandomAndDistinctKeysExecutor(OrangeRedisHashOperations operations,OrangeRedisExecutorIdGenerator idGenerator) {
		super(operations,idGenerator);
		this.operations = operations;
	}

	/**
	 * Returns the list of annotation classes supported by this executor.
	 * Adds the {@link Distinct} annotation to the list of annotations supported by the parent class.
	 *
	 * @return a list of supported annotation classes
	 */
	@Override
	protected List<Class<? extends Annotation>> getSupportedAnnotationClasses() {
		List classes = super.getSupportedAnnotationClasses();
		classes.add(Distinct.class);
		return classes;
	}

	/**
	 * Returns the context class used by this executor.
	 * This executor uses {@link OrangeCountHashContext} to handle Redis hash operations
	 * with count-based random key retrieval.
	 *
	 * @return the class of {@link OrangeCountHashContext}
	 */
	@Override
	public Class<? extends OrangeRedisContext> getContextClass() {
		return OrangeCountHashContext.class;
	}
	
	/**
	 * Retrieves a collection of random and distinct keys from a Redis hash structure.
	 * This implementation ensures that the returned keys are unique by using a {@link LinkedHashSet}.
	 * When the requested key count exceeds the total available keys, all available keys will be returned.
	 *
	 * @param context the Redis context containing operation parameters, must be an instance of {@link OrangeCountHashContext}
	 * @param valueField the field representing the value type, can be null
	 * @param returnArgumentType the expected return type for the operation
	 * @return a collection of unique random keys from the Redis hash
	 * @throws Exception if any error occurs during the operation
	 */
	@Override
	protected Collection doGet(OrangeRedisContext context, Field valueField, Type returnArgumentType) throws Exception {
		OrangeCountHashContext ctx = (OrangeCountHashContext) context;
		// Note: When the requested key count exceeds the total size,
		// 'randomKeys' method automatically returns all available keys instead. 
		List<Object> keys = this.operations.randomKeys(ctx.getRedisKey().getValue(), ctx.getCount(), ctx.getKeyType(), valueField == null ? returnArgumentType : valueField.getGenericType());
		// Proactively handle cases where the keys's size may exceed total size,
		// as the randomKeys method behavior might change in the future
		LinkedHashSet result = new LinkedHashSet<>();
		if(keys == null) {
			return result;
		}
		for(Object key : keys) {
			result.add(key);
		}
		return result;
	}
}