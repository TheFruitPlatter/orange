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

import com.langwuyue.orange.redis.annotation.Increment;
import com.langwuyue.orange.redis.annotation.hash.HashKey;
import com.langwuyue.orange.redis.context.OrangeRedisContext;
import com.langwuyue.orange.redis.executor.OrangeRedisAbstractExecutor;
import com.langwuyue.orange.redis.executor.hash.context.OrangeHashKeyContext;
import com.langwuyue.orange.redis.executor.hash.context.OrangeHashKeyValueContext;
import com.langwuyue.orange.redis.mapping.OrangeRedisExecutorIdGenerator;
import com.langwuyue.orange.redis.operations.OrangeRedisHashOperations;
import com.langwuyue.orange.redis.utils.OrangeCollectionUtils;

/**
 * Executor for incrementing numeric values in Redis hash fields.
 *
 * <p>This executor provides functionality to increment a numeric value stored in a hash field
 * by 1. If the field does not exist, it will be created with an initial value of 1.
 * If the field exists but contains a non-numeric value, an error will occur.
 *
 * <p>This executor supports the following annotations:
 * <ul>
 *   <li>{@link Increment} - Marks a method as a hash field increment operation</li>
 *   <li>{@link HashKey} - Used to specify the field name whose value should be incremented</li>
 * </ul>
 *
 * <p>The executor returns a Long value representing the new value after the increment operation.
 * The return value will be:
 * <ul>
 *   <li>1 - if the field did not exist and was created</li>
 *   <li>n + 1 - where n was the previous value, if the field existed and contained a numeric value</li>
 * </ul>
 *
 * @author Liang.Zhong
 * @since 1.0.0
 */
public class OrangeIncrementExecutor extends OrangeRedisAbstractExecutor {
	
	/** Redis hash operations used by this executor */
	private OrangeRedisHashOperations operations;

	/**
	 * Constructs a new hash field increment executor with the required dependencies.
	 *
	 * @param operations the Redis hash operations component for performing increment operations
	 * @param idGenerator the executor ID generator for creating unique identifiers
	 */
	public OrangeIncrementExecutor(OrangeRedisHashOperations operations,OrangeRedisExecutorIdGenerator idGenerator) {
		super(idGenerator);
		this.operations = operations;
	}

	/**
	 * Executes the hash field increment operation.
	 *
	 * <p>This method increments the numeric value stored in the specified hash field by 1.
	 * If the field does not exist, it will be created with an initial value of 1.
	 *
	 * @param context the Redis context containing the hash key and field information
	 * @return Long the new value after the increment operation
	 * @throws Exception if the field contains a non-numeric value or any other error occurs
	 */
	@Override
	public Object execute(OrangeRedisContext context) throws Exception {
		OrangeHashKeyContext ctx = (OrangeHashKeyContext) context;
		return this.operations.increment(ctx.getRedisKey().getValue(), ctx.getHashKey(), 1L, ctx.getKeyType());
	}

	/**
	 * Returns the list of annotation classes supported by this executor.
	 *
	 * @return a list containing {@link Increment} and {@link HashKey} annotation classes
	 */
	@Override
	protected List<Class<? extends Annotation>> getSupportedAnnotationClasses() {
		return OrangeCollectionUtils.asList(Increment.class,HashKey.class);
	}

	/**
	 * Returns the context class used by this executor for handling hash field increment operations.
	 *
	 * @return the class object for {@link OrangeHashKeyValueContext}
	 */
	@Override
	public Class<? extends OrangeRedisContext> getContextClass() {
		return OrangeHashKeyValueContext.class;
	}
	
	

}