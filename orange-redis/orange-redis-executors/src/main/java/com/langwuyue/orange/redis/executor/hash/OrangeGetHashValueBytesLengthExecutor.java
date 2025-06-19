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

import com.langwuyue.orange.redis.annotation.hash.GetHashValueLength;
import com.langwuyue.orange.redis.annotation.hash.HashKey;
import com.langwuyue.orange.redis.context.OrangeRedisContext;
import com.langwuyue.orange.redis.executor.OrangeRedisAbstractExecutor;
import com.langwuyue.orange.redis.executor.hash.context.OrangeHashKeyContext;
import com.langwuyue.orange.redis.mapping.OrangeRedisExecutorIdGenerator;
import com.langwuyue.orange.redis.operations.OrangeRedisHashOperations;
import com.langwuyue.orange.redis.utils.OrangeCollectionUtils;

/**
 * Executor for retrieving the length (in bytes) of a hash field value.
 *
 * <p>This executor provides functionality to get the length of a value stored
 * in a Redis hash field. The length is measured in bytes rather than characters,
 * which is particularly useful when dealing with binary data or when precise
 * memory usage information is needed.
 *
 * <p>This executor supports the following annotations:
 * <ul>
 *   <li>{@link GetHashValueLength} - Marks a method as a hash value length retrieval operation</li>
 *   <li>{@link HashKey} - Specifies the hash field key whose value length should be retrieved</li>
 * </ul>
 *
 * <p>The executor returns a Long value representing the length in bytes of the
 * value stored at the specified hash field. If the field does not exist, it
 * returns 0.
 *
 * @author Liang.Zhong
 * @since 1.0.0
 */
public class OrangeGetHashValueBytesLengthExecutor extends OrangeRedisAbstractExecutor {
	
	private OrangeRedisHashOperations operations;

	/**
	 * Constructs a new hash value length executor with the required dependencies.
	 *
	 * @param operations the Redis hash operations component for performing length retrieval operations
	 * @param idGenerator the executor ID generator for creating unique identifiers
	 */
	public OrangeGetHashValueBytesLengthExecutor(OrangeRedisHashOperations operations,OrangeRedisExecutorIdGenerator idGenerator) {
		super(idGenerator);
		this.operations = operations;
	}

	/**
	 * Returns the list of annotation classes supported by this executor.
	 *
	 * @return a list containing {@link GetHashValueLength} and {@link HashKey} annotation classes
	 */
	@Override
	protected List<Class<? extends Annotation>> getSupportedAnnotationClasses() {
		return OrangeCollectionUtils.asList(GetHashValueLength.class,HashKey.class);
	}

	/**
	 * Returns the context class used by this executor for handling hash value length operations.
	 *
	 * <p>This executor uses {@link OrangeHashKeyContext} to store and manage:
	 * <ul>
	 *   <li>Redis key information</li>
	 *   <li>Hash field key</li>
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
	 * Executes the hash value length retrieval operation using the provided context.
	 *
	 * <p>This method retrieves the length in bytes of the value stored at the specified
	 * hash field using Redis's HSTRLEN command. If the field does not exist, it returns 0.
	 *
	 * @param context the Redis context containing the hash key and field information
	 * @return Long the length in bytes of the value stored at the specified hash field
	 * @throws Exception if any error occurs during the operation
	 */
	@Override
	public Object execute(OrangeRedisContext context) throws Exception {
		OrangeHashKeyContext ctx = (OrangeHashKeyContext) context;
		return this.operations.getLengthByHashKey(
				ctx.getRedisKey().getValue(),
				ctx.getHashKey(),
				ctx.getKeyType()
		);
	}
}