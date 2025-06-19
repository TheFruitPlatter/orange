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

import com.langwuyue.orange.redis.annotation.Count;
import com.langwuyue.orange.redis.annotation.RedisValue;
import com.langwuyue.orange.redis.annotation.RemoveMembers;
import com.langwuyue.orange.redis.context.OrangeRedisContext;
import com.langwuyue.orange.redis.executor.OrangeRedisAbstractExecutor;
import com.langwuyue.orange.redis.executor.list.context.OrangeRemoveContext;
import com.langwuyue.orange.redis.mapping.OrangeRedisExecutorIdGenerator;
import com.langwuyue.orange.redis.operations.OrangeRedisListOperations;
import com.langwuyue.orange.redis.utils.OrangeCollectionUtils;

/**
 * Executor implementation for removing members from a Redis list.
 * 
 * <p>This executor provides functionality to remove a specified number of occurrences
 * of a given value from a Redis list. It supports both single and multiple element
 * removal operations.
 * 
 * <p>The removal operation is performed using the following parameters:
 * <ul>
 *   <li>The value to be removed</li>
 *   <li>The count of occurrences to remove:
 *     <ul>
 *       <li>count &gt; 0: Remove elements from head to tail</li>
 *       <li>count &lt; 0: Remove elements from tail to head</li>
 *       <li>count = 0: Remove all elements equal to value</li>
 *     </ul>
 *   </li>
 * </ul>
 * 
 * <p>The executor can return either:
 * <ul>
 *   <li>The number of elements removed (as Long)</li>
 *   <li>A boolean indicating whether any elements were removed (for boolean return types)</li>
 * </ul>
 *
 * <p>This executor works with the following annotations:
 * <ul>
 *   <li>{@link RemoveMembers} - Indicates a remove operation</li>
 *   <li>{@link RedisValue} - Specifies the value to remove</li>
 *   <li>{@link Count} - Specifies the number of occurrences to remove</li>
 * </ul>
 *
 * @author Liang.Zhong
 * @since 1.0.0
 * @see <a href="https://orange.langwuyue.com/redis/advanced/list">Orange Redis List Documentation</a>
 */
public class OrangeRemoveMemberExecutor extends OrangeRedisAbstractExecutor {
	
	/**
	 * Redis list operations handler that provides the core functionality for interacting with Redis lists.
	 * This field is used to execute the remove operation on the Redis server.
	 */
	private OrangeRedisListOperations operations;

	/**
	 * Constructs a new OrangeRemoveMemberExecutor with the specified operations and ID generator.
	 *
	 * @param operations the Redis list operations handler that will execute the remove command
	 * @param idGenerator the generator used to create unique identifiers for this executor
	 * @throws IllegalArgumentException if either operations or idGenerator is null
	 */
	public OrangeRemoveMemberExecutor(OrangeRedisListOperations operations,OrangeRedisExecutorIdGenerator idGenerator) {
		super(idGenerator);
		this.operations = operations;
	}

	/**
	 * Executes the remove operation on a Redis list.
	 * 
	 *
	 * @param context the execution context containing the key, value, and count information
	 * @return either a Long representing the number of elements removed, or a Boolean indicating if any elements were removed
	 * @throws Exception if an error occurs during execution
	 */
	@Override
	public Object execute(OrangeRedisContext context) throws Exception {
		OrangeRemoveContext ctx = (OrangeRemoveContext)context;
		Long result = this.operations.remove(
			context.getRedisKey().getValue(), 
			ctx.getCount(), 
			ctx.getValue(), 
			context.getValueType()
		);
		Class<?> returnClass = context.getOperationMethod().getReturnType();
		if(returnClass == Boolean.class || returnClass == boolean.class) {
			return result != null && result > 0;	
		}
		return result;
	}

	/**
	 * Returns the list of annotation classes that this executor supports.
	 * 
	 * <p>This executor supports the following annotations:
	 * <ul>
	 *   <li>{@link RemoveMembers} - Indicates that this operation removes members from a list</li>
	 *   <li>{@link RedisValue} - Specifies the value to be removed from the list</li>
	 *   <li>{@link Count} - Specifies the number of occurrences to remove</li>
	 * </ul>
	 *
	 * @return a list containing the supported annotation classes
	 */
	@Override
	protected List<Class<? extends Annotation>> getSupportedAnnotationClasses() {
		return OrangeCollectionUtils.asList(RemoveMembers.class,RedisValue.class,Count.class);
	}

	/**
	 * Returns the context class required for this executor.
	 * 
	 * <p>This executor uses the {@link OrangeRemoveContext} class which extends
	 * the base {@link OrangeRedisContext} to include additional information needed for
	 * the remove operation, such as:
	 * <ul>
	 *   <li>The value to be removed</li>
	 *   <li>The count parameter specifying how many occurrences to remove</li>
	 * </ul>
	 *
	 * @return the {@link OrangeRemoveContext} class
	 */
	@Override
	public Class<? extends OrangeRedisContext> getContextClass() {
		return OrangeRemoveContext.class;
	}
}