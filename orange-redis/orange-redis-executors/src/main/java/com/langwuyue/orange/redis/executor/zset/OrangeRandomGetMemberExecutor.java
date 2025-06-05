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
package com.langwuyue.orange.redis.executor.zset;

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
import com.langwuyue.orange.redis.operations.OrangeRedisZSetOperations;
import com.langwuyue.orange.redis.utils.OrangeCollectionUtils;
/**
 * Executor for retrieving a single random member from a Redis Sorted Set.
 * 
 * <p>This executor provides functionality to randomly select one member from
 * a Redis Sorted Set. It is typically used when you need to get a random
 * element from a set without any specific ordering requirements.
 *
 * <p>The executor supports the following annotations:
 * <ul>
 *   <li>{@link GetMembers} - Indicates this is a member retrieval operation</li>
 *   <li>{@link Random} - Indicates random selection of members</li>
 * </ul>
 *
 * <p>The executor uses {@link OrangeRedisZSetOperations} to perform the actual
 * Redis operation and supports generic return types for flexible member type mapping.
 *
 * @author Liang.Zhong
 * @since 1.0.0
 */
public class OrangeRandomGetMemberExecutor extends OrangeRedisGetOneAbstractExecutor {

	private OrangeRedisZSetOperations operations;

	/**
	 * Constructs a new OrangeRandomGetMemberExecutor.
	 *
	 * @param operations the Redis Sorted Set operations implementation
	 * @param idGenerator the executor ID generator for tracking and monitoring
	 */
	public OrangeRandomGetMemberExecutor(OrangeRedisZSetOperations operations,OrangeRedisExecutorIdGenerator idGenerator) {
		super(idGenerator);
		this.operations = operations;
	}
	
	/**
	 * Returns the list of supported annotations for this executor.
	 *
	 * <p>This executor supports:
	 * <ul>
	 *   <li>{@link GetMembers} - for marking methods that retrieve set members</li>
	 *   <li>{@link Random} - for marking methods that require random selection</li>
	 * </ul>
	 *
	 * @return a list of supported annotation classes
	 */
	@Override
	protected List<Class<? extends Annotation>> getSupportedAnnotationClasses() {
		return OrangeCollectionUtils.asList(GetMembers.class,Random.class);
	}

	/**
	 * Performs the actual random member retrieval from Redis Sorted Set.
	 *
	 * <p>This method:
	 * <ol>
	 *   <li>Requests exactly one random member from the Redis Sorted Set</li>
	 *   <li>Handles type conversion based on the method's return type</li>
	 *   <li>Supports both simple types and complex objects as members</li>
	 * </ol>
	 *
	 * <p>The random selection follows Redis's SRANDMEMBER semantics:
	 * <ul>
	 *   <li>Selection is uniform across all members</li>
	 *   <li>No guarantees about uniqueness across multiple calls</li>
	 *   <li>Returns null if the set is empty</li>
	 * </ul>
	 *
	 * @param context the execution context containing Redis key and value type info
	 * @param valueField the field annotated with @RedisValue (null if not present)
	 * @param returnArgumentType the expected return type from the method
	 * @return a Collection containing one randomly selected member, or null if empty
	 * @throws Exception if:
	 *                   - Redis operation fails
	 *                   - value type conversion fails
	 *                   - any other execution error occurs
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