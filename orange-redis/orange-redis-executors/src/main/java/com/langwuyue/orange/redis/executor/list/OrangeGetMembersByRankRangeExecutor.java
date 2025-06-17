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

import com.langwuyue.orange.redis.annotation.GetMembers;
import com.langwuyue.orange.redis.annotation.zset.RankRange;
import com.langwuyue.orange.redis.context.OrangeRedisContext;
import com.langwuyue.orange.redis.context.OrangeRedisRankRangeContext;
import com.langwuyue.orange.redis.executor.OrangeRedisGetAbstractExecutor;
import com.langwuyue.orange.redis.mapping.OrangeRedisExecutorIdGenerator;
import com.langwuyue.orange.redis.operations.OrangeRedisListOperations;
import com.langwuyue.orange.redis.utils.OrangeCollectionUtils;

/**
 * Executor implementation for retrieving elements from a Redis list based on rank range.
 * 
 * <p>This executor provides functionality to retrieve elements from a Redis list
 * within a specified rank range. The rank represents the position of elements in
 * the sorted list, where ranks are zero-based indices.
 * 
 * <p>The rank range is inclusive of both start and end ranks. Negative ranks can
 * be used to count from the end of the list, where -1 represents the last element,
 * -2 the penultimate element, and so on.
 * 
 * <p>The executor requires the following annotations to be present:
 * <ul>
 *   <li>{@link GetMembers} - Indicates a request to retrieve elements from a list</li>
 *   <li>{@link RankRange} - Specifies the rank range for element retrieval</li>
 * </ul>
 * 
 * @author Liang.Zhong
 * @since 1.0.0
 * @see <a href="https://orange.langwuyue.com/redis/advanced/list">Orange Redis List Documentation</a>
 */
public class OrangeGetMembersByRankRangeExecutor extends OrangeRedisGetAbstractExecutor {
	
	/**
	 * Redis list operations handler that provides the core functionality for interacting with Redis lists.
	 * This field is used to execute the rank-based range retrieval operation on the Redis server.
	 */
	private OrangeRedisListOperations operations;

	/**
	 * Constructs a new OrangeGetMembersByRankRangeExecutor with the specified operations and ID generator.
	 *
	 * @param operations the Redis list operations handler that will execute the rank-based range command
	 * @param idGenerator the generator used to create unique identifiers for this executor
	 * @throws IllegalArgumentException if either operations or idGenerator is null
	 */
	public OrangeGetMembersByRankRangeExecutor(OrangeRedisListOperations operations,OrangeRedisExecutorIdGenerator idGenerator) {
		super(idGenerator);
		this.operations = operations;
	}

	/**
	 * Returns the list of annotation classes that this executor supports.
	 * 
	 * <p>This executor supports the following annotations:
	 * <ul>
	 *   <li>{@link GetMembers} - Indicates a request to retrieve elements from a list</li>
	 *   <li>{@link RankRange} - Specifies the rank range for element retrieval</li>
	 * </ul>
	 *
	 * @return a list containing the supported annotation classes
	 */
	@Override
	protected List<Class<? extends Annotation>> getSupportedAnnotationClasses() {
		return OrangeCollectionUtils.asList(GetMembers.class,RankRange.class);
	}
	
	/**
	 * Returns the context class required for this executor.
	 * 
	 * <p>This executor uses the {@link OrangeRedisRankRangeContext} class which extends
	 * the base {@link OrangeRedisContext} to include additional information needed for
	 * rank-based range operations, specifically:
	 * <ul>
	 *   <li>The rank range information (start and end ranks)</li>
	 *   <li>Additional metadata for rank-based retrieval</li>
	 * </ul>
	 *
	 * @return the {@link OrangeRedisRankRangeContext} class
	 */
	@Override
	public Class<? extends OrangeRedisContext> getContextClass() {
		return OrangeRedisRankRangeContext.class;
	}

	/**
	 * Performs the actual rank-based range retrieval operation on the Redis list.
	 * 
	 * <p>The range operation is inclusive of both start and end ranks. Negative ranks
	 * can be used to count from the end of the list, where -1 represents the last element.
	 *
	 * @param context the Redis operation context containing key and rank range information
	 * @param valueField the field that will store the retrieved values, may be null
	 * @param returnArgumentType the expected return type for proper type conversion
	 * @return a collection containing the elements in the specified rank range
	 * @throws Exception if an error occurs during the operation
	 */
	@Override
	protected Collection doGet(OrangeRedisContext context, Field valueField, Type returnArgumentType) throws Exception {
		OrangeRedisRankRangeContext ctx = (OrangeRedisRankRangeContext) context;
		com.langwuyue.orange.redis.operations.OrangeRedisZSetOperations.RankRange range = ctx.getRankRange();
		return this.operations.range(
				context.getRedisKey().getValue(), 
				range.getStartIndex(),
				range.getEndIndex(),
				context.getValueType(),
				valueField == null ? returnArgumentType : valueField.getGenericType()
		);
	}
}