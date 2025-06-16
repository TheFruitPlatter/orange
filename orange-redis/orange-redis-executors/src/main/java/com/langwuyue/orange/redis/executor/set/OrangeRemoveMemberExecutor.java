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
package com.langwuyue.orange.redis.executor.set;

import java.lang.annotation.Annotation;
import java.util.List;

import com.langwuyue.orange.redis.annotation.RedisValue;
import com.langwuyue.orange.redis.annotation.RemoveMembers;
import com.langwuyue.orange.redis.context.OrangeRedisContext;
import com.langwuyue.orange.redis.context.OrangeRedisValueContext;
import com.langwuyue.orange.redis.executor.OrangeRedisAbstractExecutor;
import com.langwuyue.orange.redis.mapping.OrangeRedisExecutorIdGenerator;
import com.langwuyue.orange.redis.operations.OrangeRedisSetOperations;
import com.langwuyue.orange.redis.utils.OrangeCollectionUtils;

/**
 * Executor implementation for removing members from a Redis Set.
 * 
 * <p>This executor handles operations annotated with both {@link RemoveMembers} and {@link RedisValue}
 * annotations, providing functionality to remove specified members from a Redis Set.
 * It extends {@link OrangeRedisAbstractExecutor} to implement the core Redis set removal operations.
 *
 * <p>The executor uses {@link OrangeRedisSetOperations} to perform the actual Redis operations
 * and supports returning either a boolean indicating success/failure or the number of
 * removed elements based on the method's return type.
 *
 * @author Liang.Zhong
 * @since 1.0.0
 */
public class OrangeRemoveMemberExecutor extends OrangeRedisAbstractExecutor {
	
	private OrangeRedisSetOperations operations;

	/**
	 * Constructs a new OrangeRemoveMemberExecutor with the specified operations and ID generator.
	 * 
	 * <p>This constructor initializes the executor with the Redis set operations implementation
	 * that will be used to perform the actual member removal operations, and an ID generator
	 * that will be used to generate unique identifiers for the executor instances.
	 *
	 * @param operations the Redis set operations implementation to use
	 * @param idGenerator the executor ID generator to use
	 */
	public OrangeRemoveMemberExecutor(OrangeRedisSetOperations operations,OrangeRedisExecutorIdGenerator idGenerator) {
		super(idGenerator);
		this.operations = operations;
	}

	/**
	 * Executes the member removal operation on a Redis Set.
	 * 
	 * <p>This method implements the core functionality of removing specified members from a Redis Set.
	 * It casts the provided context to {@link OrangeRedisValueContext} to access the value to be removed,
	 * then uses the {@link OrangeRedisSetOperations#remove} method to perform the actual removal operation.
	 *
	 * <p>The method adapts the return value based on the method's declared return type:
	 * <ul>
	 *   <li>If the return type is {@code Boolean} or {@code boolean}, it returns {@code true} if at least
	 *       one member was removed, {@code false} otherwise.</li>
	 *   <li>For other return types, it returns the actual number of removed elements as a {@code Long}.</li>
	 * </ul>
	 *
	 * @param context the Redis context containing operation parameters and Redis key
	 * @return either a boolean indicating success or the number of removed elements
	 * @throws Exception if an error occurs during the removal operation
	 */
	@Override
	public Object execute(OrangeRedisContext context) throws Exception {
		OrangeRedisValueContext ctx = (OrangeRedisValueContext)context;
		Long result = this.operations.remove(context.getRedisKey().getValue(),ctx.getValueType(),ctx.getValue());
		Class<?> returnClass = context.getOperationMethod().getReturnType();
		if(returnClass == Boolean.class || returnClass == boolean.class) {
			return result != null && result > 0;	
		}
		return result;
	}

	/**
	 * Returns the list of annotation classes supported by this executor.
	 * 
	 * <p>This executor supports methods annotated with both {@link RemoveMembers} and {@link RedisValue}
	 * annotations. The combination of these annotations indicates that the method should
	 * remove specified members from a Redis Set.
	 *
	 * @return a list containing the {@link RemoveMembers} and {@link RedisValue} annotation classes
	 */
	@Override
	protected List<Class<? extends Annotation>> getSupportedAnnotationClasses() {
		return OrangeCollectionUtils.asList(RemoveMembers.class,RedisValue.class);
	}
	
	/**
	 * Returns the context class used by this executor.
	 * 
	 * <p>This executor uses {@link OrangeRedisValueContext} to store and access
	 * the Redis key and value information needed for the member removal operation.
	 *
	 * @return the {@link OrangeRedisValueContext} class
	 */
	@Override
	public Class<? extends OrangeRedisContext> getContextClass() {
		return OrangeRedisValueContext.class;
	}
	
}