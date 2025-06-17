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

import com.langwuyue.orange.redis.annotation.GetIndexs;
import com.langwuyue.orange.redis.annotation.RedisValue;
import com.langwuyue.orange.redis.context.OrangeRedisContext;
import com.langwuyue.orange.redis.context.OrangeRedisValueContext;
import com.langwuyue.orange.redis.executor.OrangeRedisAbstractExecutor;
import com.langwuyue.orange.redis.mapping.OrangeRedisExecutorIdGenerator;
import com.langwuyue.orange.redis.operations.OrangeRedisListOperations;
import com.langwuyue.orange.redis.utils.OrangeCollectionUtils;

/**
 * Executor implementation for retrieving the index of a value in a Redis list.
 * 
 * <p>This executor is responsible for finding the index of a specified value within a Redis list.
 * It supports the following operations:
 * <ul>
 *   <li>Finding the first occurrence of a value in the list</li>
 *   <li>Returns the zero-based index if the value is found</li>
 *   <li>Handles type conversion between Redis string values and Java objects</li>
 * </ul>
 * 
 * <p>The executor uses {@link GetIndexs} and {@link RedisValue} annotations to mark methods
 * that perform index lookup operations on Redis lists.
 *
 * @author Liang.Zhong
 * @since 1.0.0
 * @see <a href="https://orange.langwuyue.com/redis/advanced/list">Orange Redis List Documentation</a>
 */
public class OrangeGetIndexExecutor extends OrangeRedisAbstractExecutor {
	
	private OrangeRedisListOperations operations;

	/**
	 * Constructs a new OrangeGetIndexExecutor with the specified operations and ID generator.
	 * 
	 *
	 * @param operations the Redis list operations implementation
	 * @param idGenerator the executor ID generator
	 */
	public OrangeGetIndexExecutor(OrangeRedisListOperations operations,OrangeRedisExecutorIdGenerator idGenerator) {
		super(idGenerator);
		this.operations = operations;
	}

	/**
	 * Returns the list of annotation classes supported by this executor.
	 * 
	 * <p>This executor supports the following annotations:
	 * <ul>
	 *   <li>{@link GetIndexs} - Marks the method as an index retrieval operation</li>
	 *   <li>{@link RedisValue} - Specifies the value to search for in the list</li>
	 * </ul>
	 *
	 * @return a list containing the GetIndexs and RedisValue annotation classes
	 */
	@Override
	protected List<Class<? extends Annotation>> getSupportedAnnotationClasses() {
		return OrangeCollectionUtils.asList(GetIndexs.class,RedisValue.class);
	}
	
	/**
	 * Returns the context class used by this executor.
	 * 
	 *
	 * @return the OrangeRedisValueContext class
	 */
	@Override
	public Class<? extends OrangeRedisContext> getContextClass() {
		return OrangeRedisValueContext.class;
	}

	/**
	 * Executes the index retrieval operation on a Redis list.
	 * 
	 * @param context the execution context containing the Redis key and value to search for
	 * @return the index of the value in the list (zero-based)
	 * @throws Exception if an error occurs during the execution
	 */
	@Override
	public Object execute(OrangeRedisContext context) throws Exception {
		OrangeRedisValueContext ctx = (OrangeRedisValueContext) context;
		return this.operations.indexOf(
				context.getRedisKey().getValue(), 
				ctx.getValue(), 
				context.getValueType()
		);
	}
}