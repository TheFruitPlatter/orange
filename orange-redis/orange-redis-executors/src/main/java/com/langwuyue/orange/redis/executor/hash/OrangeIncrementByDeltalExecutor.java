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
import com.langwuyue.orange.redis.annotation.RedisValue;
import com.langwuyue.orange.redis.annotation.hash.HashKey;
import com.langwuyue.orange.redis.context.OrangeRedisContext;
import com.langwuyue.orange.redis.executor.OrangeRedisAbstractExecutor;
import com.langwuyue.orange.redis.executor.hash.context.OrangeHashKeyValueContext;
import com.langwuyue.orange.redis.mapping.OrangeRedisExecutorIdGenerator;
import com.langwuyue.orange.redis.operations.OrangeRedisHashOperations;
import com.langwuyue.orange.redis.utils.OrangeCollectionUtils;
import com.langwuyue.orange.redis.utils.OrangeReflectionUtils;

/**
 * Executor for incrementing numeric values in Redis hash fields by a specified delta.
 *
 * <p>This executor provides functionality to increment a numeric value stored in a hash field
 * by a specified delta amount. If the field does not exist, it will be created with the delta as its initial value.
 * If the field exists but contains a non-numeric value, an error will occur.
 *
 * <p>This executor supports the following annotations:
 * <ul>
 *   <li>{@link Increment} - Marks a method as a hash field increment operation</li>
 *   <li>{@link HashKey} - Used to specify the field name whose value should be incremented</li>
 *   <li>{@link RedisValue} - Used to specify the delta value for the increment operation</li>
 * </ul>
 *
 * <p>This executor handles both integer and floating-point increments:
 * <ul>
 *   <li>For integer values, it performs a long increment operation</li>
 *   <li>For floating-point values, it performs a double increment operation</li>
 * </ul>
 *
 * <p>The executor returns a numeric value (Long or Double) representing the new value after the increment operation.
 * The return type depends on the type of the delta value:
 * <ul>
 *   <li>Long - if the delta is an integer type</li>
 *   <li>Double - if the delta is a floating-point type</li>
 * </ul>
 *
 * @author Liang.Zhong
 * @since 1.0.0
 * @see <a href="https://orange.langwuyue.com/redis/advanced/hash">Orange Redis Hash Documentation</a>
 */
public class OrangeIncrementByDeltalExecutor extends OrangeRedisAbstractExecutor {
	
	/** Redis hash operations used by this executor */
	private OrangeRedisHashOperations operations;

	/**
	 * Constructs a new hash field increment by delta executor with the required dependencies.
	 *
	 * @param operations the Redis hash operations component for performing increment operations
	 * @param idGenerator the executor ID generator for creating unique identifiers
	 */
	public OrangeIncrementByDeltalExecutor(OrangeRedisHashOperations operations,OrangeRedisExecutorIdGenerator idGenerator) {
		super(idGenerator);
		this.operations = operations;
	}

	/**
	 * Executes the hash field increment by delta operation.
	 *
	 * <p>This method increments the numeric value stored in the specified hash field by the given delta.
	 * The operation type (long or double) is determined by the delta value type:
	 * <ul>
	 *   <li>Integer types (byte, short, int, long) use long increment</li>
	 *   <li>Floating-point types (float, double) use double increment</li>
	 * </ul>
	 *
	 * @param context the Redis context containing the hash key, field and delta value information
	 * @return Object the new value after the increment operation (Long or Double)
	 * @throws Exception if the field contains a non-numeric value or any other error occurs
	 */
	@Override
	public Object execute(OrangeRedisContext context) throws Exception {
		OrangeHashKeyValueContext ctx = (OrangeHashKeyValueContext) context;
		Object value = ctx.getValue();
		if(OrangeReflectionUtils.isInteger(value.getClass())) {
			return this.operations.increment(ctx.getRedisKey().getValue(), ctx.getHashKey(), Long.valueOf(value.toString()), ctx.getKeyType());	
		}else{
			return this.operations.increment(ctx.getRedisKey().getValue(), ctx.getHashKey(), Double.valueOf(value.toString()), ctx.getKeyType());
		}
	}

	/**
	 * Returns the list of annotation classes supported by this executor.
	 *
	 * @return a list containing {@link Increment}, {@link HashKey}, and {@link RedisValue} annotation classes
	 */
	@Override
	protected List<Class<? extends Annotation>> getSupportedAnnotationClasses() {
		return OrangeCollectionUtils.asList(Increment.class,HashKey.class,RedisValue.class);
	}

	/**
	 * Returns the context class used by this executor for handling hash field increment by delta operations.
	 *
	 * <p>This executor uses {@link OrangeHashKeyValueContext} to store and manage:
	 * <ul>
	 *   <li>Redis key information</li>
	 *   <li>Hash field key to increment</li>
	 *   <li>Delta value for increment</li>
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