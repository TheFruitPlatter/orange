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

import com.langwuyue.orange.redis.annotation.hash.HasKeys;
import com.langwuyue.orange.redis.annotation.hash.HashKey;
import com.langwuyue.orange.redis.context.OrangeRedisContext;
import com.langwuyue.orange.redis.executor.OrangeRedisAbstractExecutor;
import com.langwuyue.orange.redis.executor.hash.context.OrangeHashKeyContext;
import com.langwuyue.orange.redis.mapping.OrangeRedisExecutorIdGenerator;
import com.langwuyue.orange.redis.operations.OrangeRedisHashOperations;
import com.langwuyue.orange.redis.utils.OrangeCollectionUtils;

/**
 * Executor for checking if a specific field exists in a Redis hash.
 *
 * <p>This executor provides functionality to check whether a given field name exists
 * in a Redis hash. It returns a boolean value indicating the existence of the field.
 *
 * <p>This executor supports the following annotations:
 * <ul>
 *   <li>{@link HasKeys} - Marks a method as a hash field existence check operation</li>
 *   <li>{@link HashKey} - Used to specify the field name to check for existence</li>
 * </ul>
 *
 * <p>The executor returns a boolean value:
 * <ul>
 *   <li>true - if the specified field exists in the hash</li>
 *   <li>false - if the field does not exist or the hash itself does not exist</li>
 * </ul>
 *
 * @author Liang.Zhong
 * @since 1.0.0
 * @see <a href="https://orange.langwuyue.com/redis/advanced/hash">Orange Redis Hash Documentation</a>
 */
public class OrangeHasKeyExecutor extends OrangeRedisAbstractExecutor {
	
	/** Redis hash operations used by this executor */
	private OrangeRedisHashOperations operations;

	/**
	 * Constructs a new hash field existence check executor with the required dependencies.
	 *
	 * @param operations the Redis hash operations component for performing existence checks
	 * @param idGenerator the executor ID generator for creating unique identifiers
	 */
	public OrangeHasKeyExecutor(OrangeRedisHashOperations operations,OrangeRedisExecutorIdGenerator idGenerator) {
		super(idGenerator);
		this.operations = operations;
	}

	/**
	 * Returns the list of annotation classes supported by this executor.
	 *
	 * @return a list containing {@link HasKeys} and {@link HashKey} annotation classes
	 */
	@Override
	protected List<Class<? extends Annotation>> getSupportedAnnotationClasses() {
		return OrangeCollectionUtils.asList(HasKeys.class,HashKey.class);
	}

	/**
	 * Returns the context class used by this executor for handling hash field existence checks.
	 *
	 * <p>This executor uses {@link OrangeHashKeyContext} to store and manage:
	 * <ul>
	 *   <li>Redis key information</li>
	 *   <li>Hash field key to check</li>
	 *   <li>Key type information</li>
	 * </ul>
	 *
	 * @return the class object for {@link OrangeHashKeyContext}
	 */
	@Override
	public Class<? extends OrangeRedisContext> getContextClass() {
		return OrangeHashKeyContext.class;
	}

	/**
	 * Executes the hash field existence check operation.
	 *
	 * <p>This method checks if a specific field exists in the Redis hash and
	 * returns a boolean result.
	 *
	 * @param context the Redis context containing the hash key and field information
	 * @return Boolean true if the field exists in the hash, false otherwise
	 * @throws Exception if any error occurs during the operation
	 */
	@Override
	public Object execute(OrangeRedisContext context) throws Exception {
		OrangeHashKeyContext ctx = (OrangeHashKeyContext) context;
		return this.operations.hasKey(
				ctx.getRedisKey().getValue(),
				ctx.getHashKey(),
				ctx.getKeyType()
		);
	}
}