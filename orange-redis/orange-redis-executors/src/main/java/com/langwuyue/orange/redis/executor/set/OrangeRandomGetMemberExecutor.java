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
import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.List;

import com.langwuyue.orange.redis.annotation.GetMembers;
import com.langwuyue.orange.redis.annotation.Random;
import com.langwuyue.orange.redis.context.OrangeRedisContext;
import com.langwuyue.orange.redis.executor.OrangeRedisGetOneAbstractExecutor;
import com.langwuyue.orange.redis.mapping.OrangeRedisExecutorIdGenerator;
import com.langwuyue.orange.redis.operations.OrangeRedisSetOperations;
import com.langwuyue.orange.redis.utils.OrangeCollectionUtils;

/**
 * Executor implementation for randomly retrieving members from a Redis Set.
 * 
 * <p>This executor handles operations annotated with both {@link GetMembers} and {@link Random}
 * annotations, providing functionality to randomly select and retrieve members from a Redis Set.
 * It extends {@link OrangeRedisGetOneAbstractExecutor} to leverage common Redis get operation
 * functionality while adding specific random member selection behavior.
 *
 * <p>The executor uses {@link OrangeRedisSetOperations} to perform the actual Redis operations
 * and supports retrieving a single random member from the set.
 *
 * @author Liang.Zhong
 * @since 1.0.0
 * @see <a href="https://orange.langwuyue.com/redis/advanced/set">Orange Redis Set Documentation</a>
 */
public class OrangeRandomGetMemberExecutor extends OrangeRedisGetOneAbstractExecutor {
	
	private OrangeRedisSetOperations operations;

	/**
	 * Constructs a new OrangeRandomGetMemberExecutor with the specified operations and ID generator.
	 * 
	 * <p>This constructor initializes the executor with the Redis set operations implementation
	 * that will be used to perform the actual random member retrieval, and an ID generator
	 * that will be used to generate unique identifiers for the executor instances.
	 *
	 * @param operations the Redis set operations implementation to use
	 * @param idGenerator the executor ID generator to use
	 */
	public OrangeRandomGetMemberExecutor(OrangeRedisSetOperations operations,OrangeRedisExecutorIdGenerator idGenerator) {
		super(idGenerator);
		this.operations = operations;
	}

	/**
	 * Returns the list of annotation classes supported by this executor.
	 * 
	 * <p>This executor supports methods annotated with both {@link GetMembers} and {@link Random}
	 * annotations. The combination of these annotations indicates that the method should
	 * retrieve random members from a Redis Set.
	 *
	 * @return a list containing the {@link GetMembers} and {@link Random} annotation classes
	 */
	@Override
	protected List<Class<? extends Annotation>> getSupportedAnnotationClasses() {
		return OrangeCollectionUtils.asList(GetMembers.class,Random.class);
	}

	/**
	 * Performs the actual random member retrieval operation from a Redis Set.
	 * 
	 * <p>This method implements the core functionality of randomly retrieving a member
	 * from a Redis Set. It uses the {@link OrangeRedisSetOperations#randomMembers} method
	 * to retrieve exactly one random member from the set identified by the Redis key
	 * in the provided context.
	 *
	 * <p>The method determines the appropriate generic type for value conversion based on
	 * either the provided value field (if available) or the return argument type.
	 *
	 * @param context the Redis context containing operation parameters and Redis key
	 * @param valueField the field representing the value, may be null
	 * @param returnArgumentType the type to which the retrieved value should be converted
	 * @return a collection containing the randomly retrieved member
	 * @throws Exception if an error occurs during the retrieval operation
	 */
	@Override
	protected Collection doGet(OrangeRedisContext context, Field valueField, Type returnArgumentType) throws Exception {
		return this.operations.randomMembers(
			context.getRedisKey().getValue(), 
			1,
			context.getValueType(), 
			valueField == null ? returnArgumentType : valueField.getGenericType()
		);
	}
	
}