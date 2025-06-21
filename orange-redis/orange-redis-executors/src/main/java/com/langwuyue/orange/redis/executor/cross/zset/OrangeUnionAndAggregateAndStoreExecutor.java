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

import com.langwuyue.orange.redis.annotation.cross.Aggregate;
import com.langwuyue.orange.redis.annotation.cross.CrossOperationKeys;
import com.langwuyue.orange.redis.annotation.cross.StoreTo;
import com.langwuyue.orange.redis.annotation.cross.Union;
import com.langwuyue.orange.redis.context.OrangeRedisContext;
import com.langwuyue.orange.redis.executor.OrangeRedisAbstractExecutor;
import com.langwuyue.orange.redis.executor.cross.zset.context.OrangeAggregateContext;
import com.langwuyue.orange.redis.mapping.OrangeRedisExecutorIdGenerator;
import com.langwuyue.orange.redis.operations.OrangeRedisZSetOperations;
import com.langwuyue.orange.redis.utils.OrangeCollectionUtils;

/**
 * Executor for performing union operations with aggregation on Redis Sorted Sets and storing the result.
 * 
 * <p>This executor handles the computation of the union between multiple sorted sets with
 * aggregation options and stores the result in a destination key. The union operation returns
 * elements that exist in at least one of the specified sorted sets.
 * 
 * <p>The aggregation feature allows customizing how the scores of elements are combined
 * when an element exists in multiple input sorted sets. Supported aggregation types include:
 * <ul>
 *   <li>SUM - Add up the scores from all input sorted sets (default)</li>
 *   <li>MIN - Take the minimum score across all input sorted sets</li>
 *   <li>MAX - Take the maximum score across all input sorted sets</li>
 * </ul>
 * 
 * <p>This executor supports the following annotations:
 * <ul>
 *   <li>{@link Union} - Marks methods for union operations</li>
 *   <li>{@link CrossOperationKeys} - Specifies the keys to operate on</li>
 *   <li>{@link StoreTo} - Specifies the destination key</li>
 *   <li>{@link Aggregate} - Specifies aggregation type and optional weights</li>
 * </ul>
 *
 * @author Liang.Zhong
 * @since 1.0.0
 * @see Union
 * @see CrossOperationKeys
 * @see StoreTo
 * @see Aggregate
 * @see OrangeAggregateContext
 * @see OrangeRedisZSetOperations#unionAndStore
 */
public class OrangeUnionAndAggregateAndStoreExecutor extends OrangeRedisAbstractExecutor {
	
	private OrangeRedisZSetOperations operations;

	/**
	 * Constructs a new OrangeUnionAndAggregateAndStoreExecutor with the specified operations and ID generator.
	 *
	 * @param operations the Redis ZSet operations to be used for union with aggregation and storage
	 * @param idGenerator the generator for creating unique executor IDs
	 */
	public OrangeUnionAndAggregateAndStoreExecutor(OrangeRedisZSetOperations operations,OrangeRedisExecutorIdGenerator idGenerator) {
		super(idGenerator);
		this.operations = operations;
	}

	/**
	 * Returns a list of annotation classes that this executor supports.
	 * 
	 * <p>This executor supports the {@link Union}, {@link CrossOperationKeys}, 
	 * {@link StoreTo}, and {@link Aggregate} annotations.
	 *
	 * @return a list of supported annotation classes
	 */
	@Override
	protected List<Class<? extends Annotation>> getSupportedAnnotationClasses() {
		return OrangeCollectionUtils.asList(Union.class,CrossOperationKeys.class,StoreTo.class,Aggregate.class);
	}

	/**
	 * Executes the union operation with aggregation and stores the result.
	 * 
	 * <p>This method performs a union between the reference key and comparison keys,
	 * applies the specified aggregation function to combine scores, and stores the result
	 * in the destination key.
	 * 
	 * <p>The aggregation type determines how scores are combined when an element exists
	 * in multiple input sorted sets. Optional weights can be applied to the scores from
	 * each input sorted set before aggregation.
	 *
	 * @param context the Redis context containing operation parameters
	 * @return the number of elements in the resulting sorted set
	 * @throws Exception if an error occurs during the operation
	 */
	@Override
	public Object execute(OrangeRedisContext context) throws Exception {
		OrangeAggregateContext ctx = (OrangeAggregateContext) context;
		return this.operations.unionAndStore(
			ctx.getReferenceKey(), 
			ctx.getComparisonKeys(), 
			ctx.getStoreTo(), 
			ctx.getAggregate().operator(),
			ctx.getAggregate().weights()
		);
	}
	
	/**
	 * Returns the context class used by this executor.
	 * 
	 * <p>This executor uses {@link OrangeAggregateContext} which provides access to:
	 * <ul>
	 *   <li>Reference key - The primary sorted set</li>
	 *   <li>Comparison keys - Additional sorted sets to union with</li>
	 *   <li>Store destination - Where to store the result</li>
	 *   <li>Aggregate settings - Type of aggregation and optional weights</li>
	 * </ul>
	 *
	 * @return the class of {@link OrangeAggregateContext}
	 */
	@Override
	public Class<? extends OrangeRedisContext> getContextClass() {
		return OrangeAggregateContext.class;
	}
}