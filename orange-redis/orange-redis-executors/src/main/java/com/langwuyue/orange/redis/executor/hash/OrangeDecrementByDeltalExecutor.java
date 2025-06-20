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
 * Executor for decrementing hash field values by a specified delta.
 *
 * <p>This executor handles both integer and floating-point decrement operations on hash fields.
 * If the field does not exist, it will be created with the negated delta as its initial value.
 * If the field exists but contains a non-numeric value, an error will occur.
 *
 * <p>This executor supports the following annotations:
 * <ul>
 *   <li>{@link Decrement} - Marks a method as a decrement operation</li>
 *   <li>{@link HashKey} - Specifies the hash field key</li>
 *   <li>{@link RedisValue} - Specifies the decrement delta value</li>
 * </ul>
 *
 * <p>This executor handles both integer and floating-point decrements:
 * <ul>
 *   <li>For integer values, it performs a long decrement operation</li>
 *   <li>For floating-point values, it performs a double decrement operation</li>
 * </ul>
 *
 * <p>The executor returns a numeric value (Long or Double) representing the new value after the decrement operation.
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
public class OrangeDecrementByDeltalExecutor extends OrangeRedisAbstractExecutor {
	
	/** Redis hash operations used by this executor */
	private OrangeRedisHashOperations operations;

	/**
	 * Constructs a new decrement executor with the required dependencies.
	 *
	 * @param operations the Redis hash operations component for performing decrement operations
	 * @param idGenerator the executor ID generator for creating unique identifiers
	 */
	public OrangeDecrementByDeltalExecutor(OrangeRedisHashOperations operations,OrangeRedisExecutorIdGenerator idGenerator) {
		super(idGenerator);
		this.operations = operations;
	}

	/**
	 * Executes the hash field decrement operation using the provided context.
	 *
	 * @param context the Redis context containing the hash key, field and delta value
	 * @return Long or Double the new value after decrementing
	 * @throws Exception if any error occurs during the operation
	 */
	@Override
	public Object execute(OrangeRedisContext context) throws Exception {
		OrangeHashKeyValueContext ctx = (OrangeHashKeyValueContext) context;
		Object value = ctx.getValue();
		if(OrangeReflectionUtils.isInteger(value.getClass())) {
			return this.operations.increment(ctx.getRedisKey().getValue(), ctx.getHashKey(), -(Long.valueOf(value.toString())), ctx.getKeyType());	
		}else{
			return this.operations.increment(ctx.getRedisKey().getValue(), ctx.getHashKey(), -(Double.valueOf(value.toString())), ctx.getKeyType());
		}
	}

	/**
	 * Returns the list of annotation classes supported by this executor.
	 *
	 * @return a list containing {@link Decrement}, {@link HashKey}, and {@link RedisValue} annotation classes
	 */
	@Override
	protected List<Class<? extends Annotation>> getSupportedAnnotationClasses() {
		return OrangeCollectionUtils.asList(Decrement.class,HashKey.class,RedisValue.class);
	}

	/**
	 * Returns the context class used by this executor for handling decrement operations.
	 *
	 * <p>This executor uses {@link OrangeHashKeyValueContext} to store and manage:
	 * <ul>
	 *   <li>Redis key information</li>
	 *   <li>Hash field key</li>
	 *   <li>Delta value to decrement by</li>
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