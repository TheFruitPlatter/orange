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
package com.langwuyue.orange.redis.executor.cross.set;

import java.lang.annotation.Annotation;
import java.util.List;

import com.langwuyue.orange.redis.annotation.cross.CrossOperationKeys;
import com.langwuyue.orange.redis.annotation.cross.StoreTo;
import com.langwuyue.orange.redis.annotation.cross.Union;
import com.langwuyue.orange.redis.context.OrangeRedisContext;
import com.langwuyue.orange.redis.executor.OrangeRedisAbstractExecutor;
import com.langwuyue.orange.redis.executor.cross.context.OrangeCrossOperationContext;
import com.langwuyue.orange.redis.mapping.OrangeRedisExecutorIdGenerator;
import com.langwuyue.orange.redis.operations.OrangeRedisSetOperations;
import com.langwuyue.orange.redis.utils.OrangeCollectionUtils;

/**
 * Executor for performing union operations on Redis sets and storing the result.
 *
 * <p>This executor handles the union of multiple Redis sets and stores the result in a destination set.
 * It supports the following annotations:
 * <ul>
 *   <li>{@link Union} - Marks a method as a set union operation</li>
 *   <li>{@link CrossOperationKeys} - Specifies the source keys for the union operation</li>
 *   <li>{@link StoreTo} - Specifies the destination key where the union result will be stored</li>
 * </ul>
 *
 * @author Liang.Zhong
 * @since 1.0.0
 */
public class OrangeUnionAndStoreExecutor extends OrangeRedisAbstractExecutor {
	
	/**
	 * Redis set operations instance used to perform the union and store operation.
	 */
	private OrangeRedisSetOperations operations;

	/**
	 * Constructs a new union and store executor.
	 * 
	 * @param operations the Redis set operations instance
	 * @param idGenerator the executor ID generator
	 */
	public OrangeUnionAndStoreExecutor(OrangeRedisSetOperations operations, OrangeRedisExecutorIdGenerator idGenerator) {
		super(idGenerator);
		this.operations = operations;
	}

	/**
	 * Returns the list of annotation classes supported by this executor.
	 * 
	 * <p>This executor supports the following annotations:
	 * <ul>
	 *   <li>{@link Union} - Marks a method as a set union operation</li>
	 *   <li>{@link CrossOperationKeys} - Specifies the source keys for the union operation</li>
	 *   <li>{@link StoreTo} - Specifies the destination key where the union result will be stored</li>
	 * </ul>
	 * 
	 * @return list of supported annotation classes
	 */
	@Override
	protected List<Class<? extends Annotation>> getSupportedAnnotationClasses() {
		return OrangeCollectionUtils.asList(Union.class,CrossOperationKeys.class,StoreTo.class);
	}

	/**
	 * Executes the union and store operation on Redis sets.
	 * 
	 * <p>Performs the following steps:
	 * <ol>
	 *   <li>Computes the union of all specified source sets</li>
	 *   <li>Stores the result in the destination set</li>
	 *   <li>Returns the size of the resulting set</li>
	 * </ol>
	 * 
	 * @param context the Redis operation context containing source and destination keys
	 * @return the number of elements in the resulting set
	 * @throws Exception if the operation fails
	 */
	@Override
	public Object execute(OrangeRedisContext context) throws Exception {
		OrangeCrossOperationContext ctx = (OrangeCrossOperationContext) context;
		return this.operations.unionAndStore(ctx.getKeys(), ctx.getStoreTo());
	}
	
	/**
	 * Returns the context class used by this executor for union and store operations.
	 * 
	 * @return the OrangeCrossOperationContext class used for this operation
	 */
	@Override
	public Class<? extends OrangeRedisContext> getContextClass() {
		return OrangeCrossOperationContext.class;
	}
}