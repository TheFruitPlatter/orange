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
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import com.langwuyue.orange.redis.annotation.Count;
import com.langwuyue.orange.redis.annotation.Distinct;
import com.langwuyue.orange.redis.annotation.GetMembers;
import com.langwuyue.orange.redis.annotation.Random;
import com.langwuyue.orange.redis.context.OrangeRedisContext;
import com.langwuyue.orange.redis.context.OrangeRedisCountContext;
import com.langwuyue.orange.redis.executor.OrangeRedisGetAbstractExecutor;
import com.langwuyue.orange.redis.mapping.OrangeRedisExecutorIdGenerator;
import com.langwuyue.orange.redis.operations.OrangeRedisListOperations;
import com.langwuyue.orange.redis.utils.OrangeCollectionUtils;

/**
 * Executor implementation for retrieving random distinct members from a Redis list.
 * 
 * <p>This executor provides functionality to randomly select a specified number of distinct
 * elements from a Redis list. The implementation ensures that all returned elements are unique
 * by using a LinkedHashSet to store the results.
 * 
 * <p>The random selection is achieved by:
 * <ul>
 *   <li>Determining the size of the list</li>
 *   <li>Generating random indices within the range of the list size</li>
 *   <li>Retrieving elements at those indices</li>
 *   <li>Collecting unique elements until the requested count is reached or all possible unique elements are exhausted</li>
 * </ul>
 * 
 *
 * @author Liang.Zhong
 * @since 1.0.0
 * @see <a href="https://orange.langwuyue.com/redis/advanced/list">Orange Redis List Documentation</a>
 */
public class OrangeRandomAndDistinctMembersExecutor extends OrangeRedisGetAbstractExecutor {
	
	/**
	 * Redis list operations handler that provides the core functionality for interacting with Redis lists.
	 * This field is used to execute operations like size() and index() on the Redis server.
	 */
	private OrangeRedisListOperations operations;

	/**
	 * Constructs a new OrangeRandomAndDistinctMembersExecutor with the specified operations and ID generator.
	 *
	 * @param operations the Redis list operations handler that will execute the list commands
	 * @param idGenerator the generator used to create unique identifiers for this executor
	 */
	public OrangeRandomAndDistinctMembersExecutor(OrangeRedisListOperations operations,OrangeRedisExecutorIdGenerator idGenerator) {
		super(idGenerator);
		this.operations = operations;
	}

	/**
	 * Returns the list of annotation classes that this executor supports.
	 * 
	 * <p>This executor supports the following annotations:
	 * <ul>
	 *   <li>{@link GetMembers} - Indicates that this operation retrieves members from a collection</li>
	 *   <li>{@link Random} - Specifies that the selection should be random</li>
	 *   <li>{@link Count} - Specifies the number of elements to retrieve</li>
	 *   <li>{@link Distinct} - Indicates that the returned elements should be unique</li>
	 * </ul>
	 *
	 * @return a list containing the supported annotation classes
	 */
	@Override
	protected List<Class<? extends Annotation>> getSupportedAnnotationClasses() {
		return OrangeCollectionUtils.asList(GetMembers.class,Random.class,Count.class,Distinct.class);
	}
	
	/**
	 * Returns the context class required for this executor.
	 * 
	 * <p>This executor requires {@link OrangeRedisCountContext} which provides
	 * the number of random distinct elements to retrieve from the list.
	 *
	 * @return the {@link OrangeRedisCountContext} class
	 */
	@Override
	public Class<? extends OrangeRedisContext> getContextClass() {
		return OrangeRedisCountContext.class;
	}

	/**
	 * Retrieves a specified number of random distinct elements from a Redis list.
	 * 
	 * <p>The method follows these steps:
	 * <ol>
	 *   <li>Validates the count parameter:
	 *     <ul>
	 *       <li>If count is null or less than or equal to 0, returns an empty list</li>
	 *     </ul>
	 *   </li>
	 *   <li>Gets the size of the list:
	 *     <ul>
	 *       <li>If size is null or less than or equal to 0, returns an empty list</li>
	 *     </ul>
	 *   </li>
	 *   <li>Randomly selects elements and adds them to a LinkedHashSet to ensure uniqueness</li>
	 * </ol>
	 *
	 * <p>The method uses type information from either the valueField or returnArgumentType
	 * to properly deserialize the Redis values into the expected Java types.
	 *
	 * @param context the execution context containing the key and count information
	 * @param valueField the field that will store the retrieved value, may be null
	 * @param returnArgumentType the expected return type for the values
	 * @return a Collection containing the randomly selected distinct elements
	 * @throws Exception if an error occurs during execution
	 * @throws ClassCastException if the provided context is not an instance of {@link OrangeRedisCountContext}
	 */
	@Override
	protected Collection doGet(OrangeRedisContext context, Field valueField, Type returnArgumentType) throws Exception {
		OrangeRedisCountContext ctx = (OrangeRedisCountContext) context;
		Integer count = ctx.getCount();
		if(count == null || count <= 0) {
			return new ArrayList<>();
		}
		String key = context.getRedisKey().getValue();
		Long size = this.operations.size(key);
		if(size == null || size <= 0) {
			return new ArrayList<>();
		}
		
		Set<Object> results = new LinkedHashSet<>(count);
		for(int i = 0; i < count; i++) {
			Long index = Math.round(Math.random() * (size-1));
			Object result = this.operations.index(
				key, 
				index, 
				context.getValueType(),
				valueField == null ? returnArgumentType : valueField.getGenericType()
			);
			results.add(result);
		}
		return results;
	}

}