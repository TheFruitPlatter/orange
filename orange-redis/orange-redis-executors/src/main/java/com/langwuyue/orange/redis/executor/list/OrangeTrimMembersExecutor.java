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
import java.util.List;

import com.langwuyue.orange.redis.annotation.EndIndex;
import com.langwuyue.orange.redis.annotation.RemoveMembers;
import com.langwuyue.orange.redis.annotation.StartIndex;
import com.langwuyue.orange.redis.annotation.zset.Reverse;
import com.langwuyue.orange.redis.context.OrangeRedisContext;
import com.langwuyue.orange.redis.context.OrangeRedisStartIndexEndIndexContext;
import com.langwuyue.orange.redis.executor.OrangeRedisAbstractExecutor;
import com.langwuyue.orange.redis.mapping.OrangeRedisExecutorIdGenerator;
import com.langwuyue.orange.redis.operations.OrangeRedisListOperations;
import com.langwuyue.orange.redis.utils.OrangeCollectionUtils;

/**
 * Executor implementation for trimming Redis lists to a specified range.
 * 
 * <p>This executor provides functionality to trim a Redis list, keeping only elements
 * within the specified start and end indices (inclusive). All elements outside this range
 * are removed. This operation corresponds to the Redis LTRIM command and is useful for:
 * <ul>
 *   <li>Limiting list size by removing excess elements</li>
 *   <li>Extracting a specific portion of a list</li>
 *   <li>Removing elements from either end of a list</li>
 *   <li>Implementing fixed-size lists or circular buffers</li>
 * </ul>
 * 
 * <p>The trim operation modifies the list in-place and does not return the removed elements.
 * It operates with O(N) time complexity, where N is the number of elements to be removed.
 * 
 * <p>Index values follow Redis conventions:
 * <ul>
 *   <li>Zero-based indices (0 is the first element)</li>
 *   <li>Negative indices count from the end (-1 is the last element)</li>
 *   <li>Indices beyond list bounds are automatically adjusted (start &lt; 0 becomes 0, end &gt; list length becomes list length - 1)</li>
 * </ul>
 * 
 * @author Liang.Zhong
 * @since 1.0.0
 * @see com.langwuyue.orange.redis.annotation.RemoveMembers
 * @see com.langwuyue.orange.redis.annotation.StartIndex
 * @see com.langwuyue.orange.redis.annotation.EndIndex
 * @see <a href="https://orange.langwuyue.com/redis/advanced/list">Orange Redis List Documentation</a>
 */
public class OrangeTrimMembersExecutor extends OrangeRedisAbstractExecutor {
	
	/**
	 * Redis list operations handler that provides the core functionality for interacting with Redis lists.
	 * This field is used to execute the actual TRIM command on the Redis server.
	 */
	private OrangeRedisListOperations operations;

	/**
	 * Constructs a new OrangeTrimMembersExecutor with the specified operations and ID generator.
	 *
	 * @param operations the Redis list operations handler that will execute the TRIM command
	 * @param idGenerator the generator used to create unique identifiers for this executor
	 */
	public OrangeTrimMembersExecutor(OrangeRedisListOperations operations,OrangeRedisExecutorIdGenerator idGenerator) {
		super(idGenerator);
		this.operations = operations;
	}

	/**
	 * Executes the list trim operation on the specified Redis key.
	 * 
	 * <p>This method performs the actual LTRIM command on the Redis server, keeping only
	 * the elements within the specified start and end indices (inclusive) and removing
	 * all other elements from the list. The operation modifies the list in-place.
	 * 
	 * <p>The method extracts the start and end indices from the context object and passes
	 * them to the Redis list operations handler along with the Redis key.
	 *
	 * @param context the Redis operation context containing the key and index parameters
	 * @return null as the LTRIM operation doesn't return a value (the list is modified in-place)
	 * @throws Exception if an error occurs during the Redis operation
	 */
	@Override
	public Object execute(OrangeRedisContext context) throws Exception {
		OrangeRedisStartIndexEndIndexContext ctx = (OrangeRedisStartIndexEndIndexContext)context;
		this.operations.trim(
			context.getRedisKey().getValue(), 
			ctx.getStartIndex(), 
			ctx.getEndIndex()
		);
		return null;
	}

	/**
	 * Returns the list of annotation classes supported by this executor.
	 * 
	 * <p>This executor supports the following annotations:
	 * <ul>
	 *   <li>{@link RemoveMembers} - Marks a method as a list trim operation</li>
	 *   <li>{@link Reverse} - Optional annotation to indicate reverse order processing</li>
	 *   <li>{@link StartIndex} - Marks a parameter as the start index for the trim range</li>
	 *   <li>{@link EndIndex} - Marks a parameter as the end index for the trim range</li>
	 * </ul>
	 *
	 * @return a list of supported annotation classes
	 */
	@Override
	protected List<Class<? extends Annotation>> getSupportedAnnotationClasses() {
		return OrangeCollectionUtils.asList(RemoveMembers.class,Reverse.class,StartIndex.class,EndIndex.class);
	}

	/**
	 * Returns the context class used by this executor.
	 * 
	 * <p>This executor uses {@link OrangeRedisStartIndexEndIndexContext} which provides
	 * methods to access the start and end indices for the trim operation. The context
	 * is responsible for extracting and validating these indices from the method parameters.
	 *
	 * @return the class of the context used by this executor
	 */
	@Override
	public Class<? extends OrangeRedisContext> getContextClass() {
		return OrangeRedisStartIndexEndIndexContext.class;
	}
}