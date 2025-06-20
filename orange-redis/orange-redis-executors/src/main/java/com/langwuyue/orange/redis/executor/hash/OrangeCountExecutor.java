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

import com.langwuyue.orange.redis.annotation.GetSize;
import com.langwuyue.orange.redis.context.OrangeRedisContext;
import com.langwuyue.orange.redis.executor.OrangeRedisAbstractExecutor;
import com.langwuyue.orange.redis.executor.hash.context.OrangeHashContext;
import com.langwuyue.orange.redis.mapping.OrangeRedisExecutorIdGenerator;
import com.langwuyue.orange.redis.operations.OrangeRedisHashOperations;
import com.langwuyue.orange.redis.utils.OrangeCollectionUtils;

/**
 * Executor for counting the number of members in a Redis Hash.
 *
 * <p>This executor provides functionality to get the size (number of fields) of a Redis Hash.
 * The operation returns the count as a long value.
 *
 * <p>The executor supports the following annotation:
 * <ul>
 *   <li>{@link GetSize} - Marks this as a hash size operation</li>
 * </ul>
 *
 * <p>Key features:
 * <ul>
 *   <li>Returns the number of fields in the hash as a long value</li>
 *   <li>Uses Redis's HLEN command internally</li>
 *   <li>Supports any hash key type</li>
 * </ul>
 *
 * <p>Performance considerations:
 * The HLEN operation has a time complexity of O(1), making it very efficient
 * regardless of the hash size.
 *
 * @author Liang.Zhong
 * @since 1.0.0
 * @see OrangeRedisHashOperations
 * @see GetSize
 * @see <a href="https://orange.langwuyue.com/redis/advanced/hash">Orange Redis Hash Documentation</a>
 */
public class OrangeCountExecutor extends OrangeRedisAbstractExecutor {
	
	private OrangeRedisHashOperations operations;

	/**
	 * Constructs a new OrangeCountExecutor with the required dependencies.
	 *
	 * @param operations the Redis hash operations component for performing size operations
	 * @param idGenerator the executor ID generator for creating unique identifiers
	 */
	public OrangeCountExecutor(OrangeRedisHashOperations operations,OrangeRedisExecutorIdGenerator idGenerator) {
		super(idGenerator);
		this.operations = operations;
	}

	/**
	 * Executes the hash size operation using the provided Redis context.
	 *
	 * <p>This method retrieves the number of fields in the specified Redis hash.
	 * The operation is performed using Redis's HLEN command.
	 *
	 * @param context the Redis context containing the hash key information
	 * @return Long the number of fields in the hash
	 * @throws Exception if any error occurs during the operation
	 */
	@Override
	public Object execute(OrangeRedisContext context) throws Exception {
		return this.operations.size(context.getRedisKey().getValue());
	}

	@Override
	protected List<Class<? extends Annotation>> getSupportedAnnotationClasses() {
		return OrangeCollectionUtils.asList(GetSize.class);
	}
	
	/**
	 * Returns the context class used by this executor for handling hash size operations.
	 *
	 * <p>This executor uses {@link OrangeHashContext} to store and manage:
	 * <ul>
	 *   <li>Redis key information</li>
	 *   <li>Key type information</li>
	 * </ul>
	 *
	 * @return the class object for {@link OrangeHashContext}
	 */
	@Override
	public Class<? extends OrangeRedisContext> getContextClass() {
		return OrangeHashContext.class;
	}
}