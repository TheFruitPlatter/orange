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

import com.langwuyue.orange.redis.annotation.AddMembers;
import com.langwuyue.orange.redis.annotation.RedisValue;
import com.langwuyue.orange.redis.annotation.list.Index;
import com.langwuyue.orange.redis.context.OrangeRedisContext;
import com.langwuyue.orange.redis.executor.OrangeRedisAbstractExecutor;
import com.langwuyue.orange.redis.executor.list.context.OrangeSetContext;
import com.langwuyue.orange.redis.mapping.OrangeRedisExecutorIdGenerator;
import com.langwuyue.orange.redis.operations.OrangeRedisListOperations;
import com.langwuyue.orange.redis.utils.OrangeCollectionUtils;
import com.langwuyue.orange.redis.utils.OrangeReflectionUtils;

/**
 * Executor implementation for setting a value at a specific index in a Redis list.
 * 
 * <p>This executor is responsible for replacing an existing element at the specified index
 * in a Redis list with a new value. It supports the following operations:
 * <ul>
 *   <li>Setting a value at a specific zero-based index in the list</li>
 *   <li>Handling type conversion between Redis string values and Java objects</li>
 *   <li>Returning appropriate values based on the method's return type</li>
 * </ul>
 * 
 * <p>The executor uses {@link AddMembers}, {@link RedisValue}, and {@link Index} annotations
 * to mark methods that perform set operations on Redis lists.
 *
 * @author Liang.Zhong
 * @since 1.0.0
 * @see <a href="https://orange.langwuyue.com/redis/advanced/list">Orange Redis List Documentation</a>
 */
public class OrangeSetMemberExecutor extends OrangeRedisAbstractExecutor {
	
	/**
	 * Redis list operations handler that provides the core functionality for interacting with Redis lists.
	 * This field is used to execute the actual SET command on the Redis server.
	 */
	private OrangeRedisListOperations operations;

	/**
	 * Constructs a new OrangeSetMemberExecutor with the specified operations and ID generator.
	 *
	 * @param operations the Redis list operations handler that will execute the SET command
	 * @param idGenerator the generator used to create unique identifiers for this executor
	 */
	public OrangeSetMemberExecutor(OrangeRedisListOperations operations,OrangeRedisExecutorIdGenerator idGenerator) {
		super(idGenerator);
		this.operations = operations;
	}

	/**
	 * Executes the SET operation on a Redis list.
	 * 
	 * <p>This method sets the value at the specified index in the Redis list identified by the key
	 * provided in the context. The operation replaces the existing element at that position with
	 * the new value.
	 * 
	 * <p>The method returns different values based on the return type of the annotated method:
	 * <ul>
	 *   <li>For boolean return types: returns {@code true} to indicate success</li>
	 *   <li>For integer return types: returns {@code 1} to indicate one element was modified</li>
	 *   <li>For other return types: returns {@code null}</li>
	 * </ul>
	 * 
	 * <p>Note: The index validation code is currently commented out. When enabled, it would:
	 * <ul>
	 *   <li>Check if the list exists and is not empty</li>
	 *   <li>Verify that the index is within the bounds of the list</li>
	 * </ul>
	 *
	 * @param context the execution context containing the key, index, and value to set
	 * @return a value based on the return type of the annotated method
	 * @throws Exception if an error occurs during execution
	 * @throws ClassCastException if the provided context is not an instance of {@link OrangeSetContext}
	 */
	@Override
	public Object execute(OrangeRedisContext context) throws Exception {
		OrangeSetContext ctx = (OrangeSetContext) context;
//		Long size = this.operations.size(ctx.getRedisKey().getValue());
//		if(size == null || size == 0) {
//			throw new OrangeRedisException("List is empty, cannot execute set command.");
//		}
//		if(size <= ctx.getIndex()) {
//			throw new OrangeRedisException("Index out of range, cannot execute set command.");
//		}
		this.operations.set(context.getRedisKey().getValue(), ctx.getIndex(), ctx.getValue(), ctx.getValueType());
		Class<?> returnClass = context.getOperationMethod().getReturnType();
		if(returnClass == Boolean.class || returnClass == boolean.class) {
			return Boolean.TRUE;	
		}
		if(OrangeReflectionUtils.isInteger(returnClass)) {
			return 1;	
		}
		return null;
	}

	/**
	 * Returns the list of annotation classes that this executor supports.
	 * 
	 * <p>This executor supports the following annotations:
	 * <ul>
	 *   <li>{@link AddMembers} - Marks methods that add elements to Redis lists</li>
	 *   <li>{@link RedisValue} - Specifies the value to be set in the list</li>
	 *   <li>{@link Index} - Indicates the position where the value should be set</li>
	 * </ul>
	 *
	 * @return a list of supported annotation classes
	 */
	@Override
	protected List<Class<? extends Annotation>> getSupportedAnnotationClasses() {
		return OrangeCollectionUtils.asList(AddMembers.class,RedisValue.class,Index.class);
	}
	
	/**
	 * Returns the context class used by this executor.
	 * 
	 * <p>This executor uses {@link OrangeSetContext} to store and process:
	 * <ul>
	 *   <li>The Redis key for the target list</li>
	 *   <li>The index where the value should be set</li>
	 *   <li>The value to be set at the specified index</li>
	 *   <li>The type information for value conversion</li>
	 * </ul>
	 *
	 * @return the {@link OrangeSetContext} class
	 */
	@Override
	public Class<? extends OrangeRedisContext> getContextClass() {
		return OrangeSetContext.class;
	}
}