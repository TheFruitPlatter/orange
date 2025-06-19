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
import java.util.List;

import com.langwuyue.orange.redis.annotation.Decrement;
import com.langwuyue.orange.redis.annotation.hash.HashKey;
import com.langwuyue.orange.redis.context.OrangeRedisContext;
import com.langwuyue.orange.redis.executor.OrangeRedisAbstractExecutor;
import com.langwuyue.orange.redis.executor.hash.context.OrangeHashKeyContext;
import com.langwuyue.orange.redis.executor.hash.context.OrangeHashKeyValueContext;
import com.langwuyue.orange.redis.mapping.OrangeRedisExecutorIdGenerator;
import com.langwuyue.orange.redis.operations.OrangeRedisHashOperations;
import com.langwuyue.orange.redis.utils.OrangeCollectionUtils;

/**
 * Executor for decrementing hash field values by 1 (default decrement operation).
 *
 * <p>This executor handles integer decrement operations on hash fields.
 * It supports the following annotations:
 * <ul>
 *   <li>{@link Decrement} - Marks a method as a decrement operation</li>
 *   <li>{@link HashKey} - Specifies the hash field key</li>
 * </ul>
 *
 * <p>Note: This executor always decrements by 1. For custom delta values,
 * use {@link OrangeDecrementByDeltalExecutor} instead.
 *
 * @author Liang.Zhong
 * @since 1.0.0
 */
public class OrangeDecrementExecutor extends OrangeRedisAbstractExecutor {
	
	private OrangeRedisHashOperations operations;

	/**
	 * Constructs a new decrement executor with the required dependencies.
	 *
	 * @param operations the Redis hash operations component for performing decrement operations
	 * @param idGenerator the executor ID generator for creating unique identifiers
	 */
	public OrangeDecrementExecutor(OrangeRedisHashOperations operations,OrangeRedisExecutorIdGenerator idGenerator) {
		super(idGenerator);
		this.operations = operations;
	}

	/**
	 * Executes the hash field decrement operation using the provided context.
	 *
	 * <p>This method performs a decrement by 1 operation on the specified hash field
	 * using Redis's HINCRBY command. The operation is atomic and thread-safe.
	 *
	 * @param context the Redis context containing the hash key and field information
	 * @return Long the new value after decrementing by 1
	 * @throws Exception if any error occurs during the operation
	 */
	@Override
	public Object execute(OrangeRedisContext context) throws Exception {
		OrangeHashKeyContext ctx = (OrangeHashKeyContext) context;
		return this.operations.increment(ctx.getRedisKey().getValue(), ctx.getHashKey(), -1L, ctx.getKeyType());
	}

	/**
	 * Returns the list of annotation classes supported by this executor.
	 *
	 * @return a list containing {@link Decrement} and {@link HashKey} annotation classes
	 */
	@Override
	protected List<Class<? extends Annotation>> getSupportedAnnotationClasses() {
		return OrangeCollectionUtils.asList(Decrement.class,HashKey.class);
	}

	/**
	 * Returns the context class used by this executor for handling decrement operations.
	 *
	 * <p>This executor uses {@link OrangeHashKeyValueContext} to store and manage:
	 * <ul>
	 *   <li>Redis key information</li>
	 *   <li>Hash field key</li>
	 *   <li>Key type information</li>
	 * </ul>
	 *
	 * @return the class object for {@link OrangeHashKeyValueContext}
	 */
	@Override
	public Class<? extends OrangeRedisContext> getContextClass() {
		return OrangeHashKeyValueContext.class;
	}
	
	

}