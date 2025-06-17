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
import java.util.Collection;
import java.util.List;

import com.langwuyue.orange.redis.annotation.EndIndex;
import com.langwuyue.orange.redis.annotation.GetMembers;
import com.langwuyue.orange.redis.annotation.StartIndex;
import com.langwuyue.orange.redis.context.OrangeRedisContext;
import com.langwuyue.orange.redis.context.OrangeRedisStartIndexEndIndexContext;
import com.langwuyue.orange.redis.executor.OrangeRedisGetAbstractExecutor;
import com.langwuyue.orange.redis.mapping.OrangeRedisExecutorIdGenerator;
import com.langwuyue.orange.redis.operations.OrangeRedisListOperations;
import com.langwuyue.orange.redis.utils.OrangeCollectionUtils;

/**
 * Executor implementation for retrieving a range of elements from a Redis list.
 * 
 * <p>This executor provides functionality to retrieve elements from a Redis list
 * within a specified range defined by start and end indices. The range is inclusive
 * of both the start and end indices.
 * 
 * <p>Redis list indices are zero-based, with 0 being the first element of the list.
 * Negative indices can be used to specify elements starting from the end of the list,
 * with -1 being the last element, -2 the penultimate element, and so on.
 * 
 * <p>The executor requires the following annotations to be present:
 * <ul>
 *   <li>{@link GetMembers} - Indicates a request to retrieve elements from a list</li>
 *   <li>{@link StartIndex} - Specifies the starting index of the range (inclusive)</li>
 *   <li>{@link EndIndex} - Specifies the ending index of the range (inclusive)</li>
 * </ul>
 * 
 * @author Liang.Zhong
 * @since 1.0.0
 * @see <a href="https://orange.langwuyue.com/redis/advanced/list">Orange Redis List Documentation</a>
 */
public class OrangeGetMembersExecutor extends OrangeRedisGetAbstractExecutor {
	
	/**
	 * Redis list operations handler that provides the core functionality for interacting with Redis lists.
	 * This field is used to execute the range retrieval operation on the Redis server.
	 */
	private OrangeRedisListOperations operations;

	/**
	 * Constructs a new OrangeGetMembersExecutor with the specified operations and ID generator.
	 *
	 * @param operations the Redis list operations handler that will execute the range command
	 * @param idGenerator the generator used to create unique identifiers for this executor
	 * @throws IllegalArgumentException if either operations or idGenerator is null
	 */
	public OrangeGetMembersExecutor(OrangeRedisListOperations operations,OrangeRedisExecutorIdGenerator idGenerator) {
		super(idGenerator);
		this.operations = operations;
	}

	/**
	 * Returns the list of annotation classes that this executor supports.
	 * 
	 * <p>This executor supports the following annotations:
	 * <ul>
	 *   <li>{@link GetMembers} - Indicates a request to retrieve elements from a list</li>
	 *   <li>{@link StartIndex} - Specifies the starting index of the range (inclusive)</li>
	 *   <li>{@link EndIndex} - Specifies the ending index of the range (inclusive)</li>
	 * </ul>
	 *
	 * @return a list containing the supported annotation classes
	 */
	@Override
	protected List<Class<? extends Annotation>> getSupportedAnnotationClasses() {
		return OrangeCollectionUtils.asList(GetMembers.class,StartIndex.class,EndIndex.class);
	}
	
	/**
	 * Returns the context class required for this executor.
	 * 
	 * <p>This executor uses the {@link OrangeRedisStartIndexEndIndexContext} class which extends
	 * the base {@link OrangeRedisContext} to include additional information needed for
	 * the range operation, specifically:
	 * <ul>
	 *   <li>The start index of the range</li>
	 *   <li>The end index of the range</li>
	 * </ul>
	 *
	 * @return the {@link OrangeRedisStartIndexEndIndexContext} class
	 */
	@Override
	public Class<? extends OrangeRedisContext> getContextClass() {
		return OrangeRedisStartIndexEndIndexContext.class;
	}

	/**
	 * Performs the actual range retrieval operation on the Redis list.
	 * 
	 * <p>The range operation is inclusive of both start and end indices. Negative indices
	 * can be used to count from the end of the list, where -1 is the last element.
	 *
	 * @param context the Redis operation context containing key and index information
	 * @param valueField the field that will store the retrieved values, may be null
	 * @param returnArgumentType the expected return type for proper type conversion
	 * @return a collection containing the elements in the specified range
	 * @throws Exception if an error occurs during the operation
	 */
	@Override
	protected Collection doGet(OrangeRedisContext context, Field valueField, Type returnArgumentType) throws Exception {
		OrangeRedisStartIndexEndIndexContext ctx = (OrangeRedisStartIndexEndIndexContext) context;
		return this.operations.range(
				context.getRedisKey().getValue(), 
				ctx.getStartIndex(),
				ctx.getEndIndex(),
				context.getValueType(),
				valueField == null ? returnArgumentType : valueField.getGenericType()
		);
	}
}