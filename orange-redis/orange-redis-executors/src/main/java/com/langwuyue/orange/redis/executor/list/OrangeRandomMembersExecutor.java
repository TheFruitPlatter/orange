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

import com.langwuyue.orange.redis.annotation.Count;
import com.langwuyue.orange.redis.annotation.GetMembers;
import com.langwuyue.orange.redis.annotation.Random;
import com.langwuyue.orange.redis.context.OrangeRedisContext;
import com.langwuyue.orange.redis.context.OrangeRedisCountContext;
import com.langwuyue.orange.redis.executor.OrangeRedisGetAbstractExecutor;
import com.langwuyue.orange.redis.mapping.OrangeRedisExecutorIdGenerator;
import com.langwuyue.orange.redis.operations.OrangeRedisListOperations;
import com.langwuyue.orange.redis.utils.OrangeCollectionUtils;

/**
 * Executor implementation for retrieving random members from a Redis list.
 * 
 * <p>This executor provides functionality to randomly select a specified number of
 * elements from a Redis list. Unlike {@link OrangeRandomAndDistinctMembersExecutor},
 * this implementation does not guarantee uniqueness of the returned elements, meaning
 * the same element may appear multiple times in the result.
 * 
 * <p>The random selection is achieved by:
 * <ul>
 *   <li>Determining the size of the list</li>
 *   <li>Generating random indices within the range of the list size</li>
 *   <li>Retrieving elements at those indices</li>
 *   <li>Collecting the elements until the requested count is reached</li>
 * </ul>
 * 
 *
 * @author Liang.Zhong
 * @since 1.0.0
 * @see <a href="https://orange.langwuyue.com/redis/advanced/list">Orange Redis List Documentation</a>
 */
public class OrangeRandomMembersExecutor extends OrangeRedisGetAbstractExecutor {
	
	/**
	 * Redis list operations handler that provides the core functionality for interacting with Redis lists.
	 * This field is used to execute operations like size() and index() on the Redis server.
	 */
	private OrangeRedisListOperations operations;

	/**
	 * Constructs a new OrangeRandomMembersExecutor with the specified operations and ID generator.
	 *
	 * @param operations the Redis list operations handler that will execute the list commands
	 * @param idGenerator the generator used to create unique identifiers for this executor
	 */
	public OrangeRandomMembersExecutor(OrangeRedisListOperations operations,OrangeRedisExecutorIdGenerator idGenerator) {
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
	 * </ul>
	 * 
	 * <p>Note that unlike {@link OrangeRandomAndDistinctMembersExecutor}, this executor does not
	 * support the {@link Distinct} annotation as it allows duplicate elements in the result.
	 *
	 * @return a list containing the supported annotation classes
	 */
	@Override
	protected List<Class<? extends Annotation>> getSupportedAnnotationClasses() {
		return OrangeCollectionUtils.asList(GetMembers.class,Random.class,Count.class);
	}
	
	/**
	 * Returns the context class required for this executor.
	 * 
	 * <p>This executor requires {@link OrangeRedisCountContext} which provides
	 * the number of random elements to retrieve from the list.
	 *
	 * @return the {@link OrangeRedisCountContext} class
	 */
	@Override
	public Class<? extends OrangeRedisContext> getContextClass() {
		return OrangeRedisCountContext.class;
	}

	/**
	 * Retrieves a specified number of random elements from a Redis list.
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
	 *   <li>Randomly selects elements from the list based on the count</li>
	 * </ol>
	 *
	 * <p>Unlike {@link OrangeRandomAndDistinctMembersExecutor}, this method does not
	 * ensure uniqueness of the selected elements, so the same element may appear
	 * multiple times in the result if randomly selected more than once.
	 *
	 * <p>The method uses type information from either the valueField or returnArgumentType
	 * to properly deserialize the Redis values into the expected Java types.
	 *
	 * @param context the execution context containing the key and count information
	 * @param valueField the field that will store the retrieved value, may be null
	 * @param returnArgumentType the expected return type for the values
	 * @return a Collection containing the randomly selected elements
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
		
		List<Object> results = new ArrayList<>(count);
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