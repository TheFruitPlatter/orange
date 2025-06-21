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
package com.langwuyue.orange.redis.executor.cross.zset;

import java.lang.annotation.Annotation;
import java.util.List;

import com.langwuyue.orange.redis.annotation.cross.CrossOperationKeys;
import com.langwuyue.orange.redis.annotation.cross.StoreTo;
import com.langwuyue.orange.redis.annotation.cross.Union;
import com.langwuyue.orange.redis.context.OrangeRedisContext;
import com.langwuyue.orange.redis.executor.OrangeRedisAbstractExecutor;
import com.langwuyue.orange.redis.executor.cross.context.OrangeCrossOperationContext;
import com.langwuyue.orange.redis.mapping.OrangeRedisExecutorIdGenerator;
import com.langwuyue.orange.redis.operations.OrangeRedisZSetOperations;
import com.langwuyue.orange.redis.utils.OrangeCollectionUtils;

/**
 * Executor for performing union operations on Redis Sorted Sets and storing the result.
 * 
 * <p>This executor handles the computation of the union between multiple sorted sets
 * and stores the result in a destination key. The union operation returns elements
 * that exist in at least one of the specified sorted sets.
 * 
 * <p>When an element exists in multiple input sorted sets, the default behavior is to
 * use the highest score of that element across all the input sorted sets. Unlike
 * {@link OrangeUnionAndAggregateAndStoreExecutor}, this executor does not support
 * custom aggregation types or weights.
 * 
 * <p>This executor supports the following annotations:
 * <ul>
 *   <li>{@link Union} - Marks methods for union operations</li>
 *   <li>{@link CrossOperationKeys} - Specifies the keys to operate on</li>
 *   <li>{@link StoreTo} - Specifies the destination key</li>
 * </ul>
 *
 * @author Liang.Zhong
 * @since 1.0.0
 * @see Union
 * @see CrossOperationKeys
 * @see StoreTo
 * @see OrangeCrossOperationContext
 * @see OrangeRedisZSetOperations#unionAndStore
 */
public class OrangeUnionAndStoreExecutor extends OrangeRedisAbstractExecutor {
	
	private OrangeRedisZSetOperations operations;

	/**
	 * Constructs a new OrangeUnionAndStoreExecutor with the specified operations and ID generator.
	 *
	 * @param operations the Redis ZSet operations to be used for union and storage
	 * @param idGenerator the generator for creating unique executor IDs
	 */
	public OrangeUnionAndStoreExecutor(OrangeRedisZSetOperations operations,OrangeRedisExecutorIdGenerator idGenerator) {
		super(idGenerator);
		this.operations = operations;
	}

	/**
	 * Returns a list of annotation classes that this executor supports.
	 * 
	 * <p>This executor supports the {@link Union}, {@link CrossOperationKeys}, 
	 * and {@link StoreTo} annotations.
	 *
	 * @return a list of supported annotation classes
	 */
	@Override
	protected List<Class<? extends Annotation>> getSupportedAnnotationClasses() {
		return OrangeCollectionUtils.asList(Union.class,CrossOperationKeys.class,StoreTo.class);
	}

	/**
	 * Executes the union operation and stores the result.
	 * 
	 * <p>This method performs a union between the reference key and comparison keys,
	 * and stores the result in the destination key. When an element exists in multiple
	 * input sorted sets, the highest score of that element is used in the resulting set.
	 *
	 * @param context the Redis context containing operation parameters
	 * @return the number of elements in the resulting sorted set
	 * @throws Exception if an error occurs during the operation
	 */
	@Override
	public Object execute(OrangeRedisContext context) throws Exception {
		OrangeCrossOperationContext ctx = (OrangeCrossOperationContext) context;
		return this.operations.unionAndStore(ctx.getReferenceKey(), ctx.getComparisonKeys(), ctx.getStoreTo());
	}
	
	/**
	 * Returns the context class used by this executor.
	 * 
	 * <p>This executor uses {@link OrangeCrossOperationContext} which provides access to:
	 * <ul>
	 *   <li>Reference key - The primary sorted set</li>
	 *   <li>Comparison keys - Additional sorted sets to union with</li>
	 *   <li>Store destination - Where to store the result</li>
	 * </ul>
	 *
	 * @return the class of {@link OrangeCrossOperationContext}
	 */
	@Override
	public Class<? extends OrangeRedisContext> getContextClass() {
		return OrangeCrossOperationContext.class;
	}
}