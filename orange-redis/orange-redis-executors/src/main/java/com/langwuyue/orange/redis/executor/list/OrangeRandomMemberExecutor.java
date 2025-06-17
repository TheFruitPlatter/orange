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
package com.langwuyue.orange.redis.executor.list;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.langwuyue.orange.redis.annotation.GetMembers;
import com.langwuyue.orange.redis.annotation.Random;
import com.langwuyue.orange.redis.context.OrangeRedisContext;
import com.langwuyue.orange.redis.executor.OrangeRedisGetOneAbstractExecutor;
import com.langwuyue.orange.redis.mapping.OrangeRedisExecutorIdGenerator;
import com.langwuyue.orange.redis.operations.OrangeRedisListOperations;
import com.langwuyue.orange.redis.utils.OrangeCollectionUtils;

/**
 * Executor implementation for retrieving a single random member from a Redis list.
 * 
 * <p>This executor provides functionality to randomly select one element from a Redis list.
 * Unlike {@link OrangeRandomMembersExecutor} which retrieves multiple elements, this executor
 * is specifically designed to return a single random element.
 * 
 * <p>The random selection is achieved by:
 * <ul>
 *   <li>Determining the size of the list</li>
 *   <li>Generating a random index within the range [0, size-1]</li>
 *   <li>Retrieving the element at that index</li>
 * </ul>
 * 
 * <p>The executor extends {@link OrangeRedisGetOneAbstractExecutor} and works with
 * the {@link GetMembers} and {@link Random} annotations to provide its functionality.
 *
 * @author Liang.Zhong
 * @since 1.0.0
 * @see <a href="https://orange.langwuyue.com/redis/advanced/list">Orange Redis List Documentation</a>
 */
public class OrangeRandomMemberExecutor extends OrangeRedisGetOneAbstractExecutor {
	
	/**
	 * Redis list operations handler that provides the core functionality for interacting with Redis lists.
	 * This field is used to execute operations like size() and index() on the Redis server.
	 */
	private OrangeRedisListOperations operations;

	/**
	 * Constructs a new OrangeRandomMemberExecutor with the specified operations and ID generator.
	 *
	 * @param operations the Redis list operations handler that will execute the list commands
	 * @param idGenerator the generator used to create unique identifiers for this executor
	 * @throws IllegalArgumentException if either operations or idGenerator is null
	 */
	public OrangeRandomMemberExecutor(OrangeRedisListOperations operations,OrangeRedisExecutorIdGenerator idGenerator) {
		super(idGenerator);
		this.operations = operations;
	}

	/**
	 * Returns the list of annotation classes that this executor supports.
	 * 
	 * <p>This executor supports the following annotations:
	 * <ul>
	 *   <li>{@link GetMembers} - Indicates that this operation retrieves a member from a collection</li>
	 *   <li>{@link Random} - Specifies that the selection should be random</li>
	 * </ul>
	 * 
	 * <p>Unlike {@link OrangeRandomMembersExecutor}, this executor does not support the {@link Count}
	 * annotation as it is designed to return only a single element.
	 *
	 * @return a list containing the supported annotation classes
	 */
	@Override
	protected List<Class<? extends Annotation>> getSupportedAnnotationClasses() {
		return OrangeCollectionUtils.asList(GetMembers.class,Random.class);
	}
	
	/**
	 * Returns the context class required for this executor.
	 * 
	 * <p>This executor uses the base {@link OrangeRedisContext} class since it doesn't
	 * require any additional context information beyond what the base class provides.
	 * Unlike {@link OrangeRandomMembersExecutor} which needs count information, this
	 * executor only needs the Redis key to perform its operation.
	 *
	 * @return the {@link OrangeRedisContext} class
	 */
	@Override
	public Class<? extends OrangeRedisContext> getContextClass() {
		return OrangeRedisContext.class;
	}

	/**
	 * Retrieves a single random element from a Redis list.
	 * 
	 * <p>The method follows these steps:
	 * <ol>
	 *   <li>Gets the size of the list:
	 *     <ul>
	 *       <li>If size is null or less than or equal to 0, returns an empty list</li>
	 *     </ul>
	 *   </li>
	 *   <li>Generates a random index within the range [0, size-1]</li>
	 *   <li>Retrieves the element at that index</li>
	 *   <li>Returns the element wrapped in a single-element collection</li>
	 * </ol>
	 *
	 * <p>The method uses type information from either the valueField or returnArgumentType
	 * to properly deserialize the Redis value into the expected Java type.
	 *
	 * @param context the execution context containing the key information
	 * @param valueField the field that will store the retrieved value, may be null
	 * @param returnArgumentType the expected return type for the value
	 * @return a Collection containing the single randomly selected element, or an empty collection if the list is empty
	 * @throws Exception if an error occurs during execution
	 */
	@Override
	protected Collection doGet(OrangeRedisContext context, Field valueField, Type returnArgumentType) throws Exception {
		String key = context.getRedisKey().getValue();
		Long size = this.operations.size(key);
		if(size == null || size <= 0) {
			return new ArrayList<>();
		}
		Long index = Math.round(Math.random() * (size-1));
		Object result = this.operations.index(
			key, 
			index, 
			context.getValueType(),
			valueField == null ? returnArgumentType : valueField.getGenericType()
		);
		return OrangeCollectionUtils.asList(result);
	}

}