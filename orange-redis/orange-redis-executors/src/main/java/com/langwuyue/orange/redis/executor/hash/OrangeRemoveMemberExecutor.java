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

import com.langwuyue.orange.redis.annotation.RemoveMembers;
import com.langwuyue.orange.redis.annotation.hash.HashKey;
import com.langwuyue.orange.redis.context.OrangeRedisContext;
import com.langwuyue.orange.redis.executor.OrangeRedisAbstractExecutor;
import com.langwuyue.orange.redis.executor.hash.context.OrangeHashKeyContext;
import com.langwuyue.orange.redis.mapping.OrangeRedisExecutorIdGenerator;
import com.langwuyue.orange.redis.operations.OrangeRedisHashOperations;
import com.langwuyue.orange.redis.utils.OrangeCollectionUtils;

/**
 * Executor for removing a single member from a Redis Hash.
 *
 * <p>This executor provides functionality to remove a single key-value pair from a Redis Hash.
 * The operation supports various return types including boolean and long.
 *
 * <p>The executor supports the following annotations:
 * <ul>
 *   <li>{@link RemoveMembers} - Marks this as a hash remove operation</li>
 *   <li>{@link HashKey} - Specifies the hash key</li>
 * </ul>
 *
 * <p>Return value handling:
 * <ul>
 *   <li>Boolean/boolean return type: returns true if any fields were removed</li>
 *   <li>Numeric return type: returns the number of fields removed</li>
 * </ul>
 *
 * <p>Note: This executor only removes a single field from the hash. For removing multiple fields,
 * use {@link OrangeRemoveMembersExecutor}.
 *
 * @author Liang.Zhong
 * @since 1.0.0
 * @see <a href="https://orange.langwuyue.com/redis/advanced/hash">Orange Redis Hash Documentation</a>
 */
public class OrangeRemoveMemberExecutor extends OrangeRedisAbstractExecutor {
	
	/** Redis hash operations used by this executor */
	private OrangeRedisHashOperations operations;

	/**
	 * Constructs a new hash member removal executor with the required dependencies.
	 *
	 * @param operations the Redis hash operations component for performing removal operations
	 * @param idGenerator the executor ID generator for creating unique identifiers
	 */
	public OrangeRemoveMemberExecutor(OrangeRedisHashOperations operations,OrangeRedisExecutorIdGenerator idGenerator) {
		super(idGenerator);
		this.operations = operations;
	}

	/**
	 * Returns the list of annotation classes supported by this executor.
	 *
	 * @return a list containing {@link RemoveMembers} and {@link HashKey} annotation classes
	 */
	@Override
	protected List<Class<? extends Annotation>> getSupportedAnnotationClasses() {
		return OrangeCollectionUtils.asList(RemoveMembers.class,HashKey.class);
	}

	/**
	 * Returns the context class used by this executor.
	 *
	 * @return the {@link OrangeHashKeyContext} class
	 */
	@Override
	public Class<? extends OrangeRedisContext> getContextClass() {
		return OrangeHashKeyContext.class;
	}

	/**
	 * Executes the hash member removal operation.
	 *
	 * <p>This method removes a single field from a Redis hash and handles the return value
	 * based on the method's return type:
	 * <ul>
	 *   <li>For boolean return types: returns true if the field was removed</li>
	 *   <li>For numeric return types: returns the number of fields removed (0 or 1)</li>
	 * </ul>
	 *
	 * @param context the Redis operation context containing operation parameters
	 * @return a boolean or numeric result based on the method's return type
	 * @throws Exception if an error occurs during execution
	 */
	@Override
	public Object execute(OrangeRedisContext context) throws Exception {
		OrangeHashKeyContext ctx = (OrangeHashKeyContext) context;
		Long result = this.operations.removeMembers(
				ctx.getRedisKey().getValue(),
				ctx.getKeyType(), 
				ctx.getHashKey()
		);
		Class<?> returnClass = context.getOperationMethod().getReturnType();
		if(returnClass == Boolean.class || returnClass == boolean.class) {
			return result != null && result > 0;	
		}
		return result;
	}
}