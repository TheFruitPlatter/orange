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
import com.langwuyue.orange.redis.annotation.cross.Intersect;
import com.langwuyue.orange.redis.annotation.cross.StoreTo;
import com.langwuyue.orange.redis.annotation.cross.Weights;
import com.langwuyue.orange.redis.context.OrangeRedisContext;
import com.langwuyue.orange.redis.executor.OrangeRedisAbstractExecutor;
import com.langwuyue.orange.redis.executor.cross.zset.context.OrangeWeightsContext;
import com.langwuyue.orange.redis.mapping.OrangeRedisExecutorIdGenerator;
import com.langwuyue.orange.redis.operations.OrangeRedisZSetOperations;
import com.langwuyue.orange.redis.utils.OrangeCollectionUtils;

/**
 * Executor for performing weighted intersection operations on Redis Sorted Sets with aggregation.
 * 
 * <p>This executor handles the computation of weighted intersections between multiple sorted sets
 * and stores the result in a destination key. The intersection operation returns elements
 * that exist in all of the specified sorted sets, with scores modified by
 * the specified weights and aggregation method.
 * 
 * <p>For each input sorted set, a weight can be specified. The score of each element in
 * each input sorted set is multiplied by the corresponding weight before being
 * aggregated according to the specified aggregation type (SUM, MIN, or MAX).
 * 
 * <p>This executor supports the following annotations:
 * <ul>
 *   <li>{@link Intersect} - Marks methods for intersection operations</li>
 *   <li>{@link CrossOperationKeys} - Specifies the keys to operate on</li>
 *   <li>{@link StoreTo} - Specifies the destination key</li>
 *   <li>{@link Aggregate} - Specifies the aggregation type for scores</li>
 *   <li>{@link Weights} - Specifies the weights for input sorted sets</li>
 * </ul>
 *
 * @author Liang.Zhong
 * @since 1.0.0
 * @see Intersect
 * @see CrossOperationKeys
 * @see StoreTo
 * @see Aggregate
 * @see Weights
 * @see OrangeWeightsContext
 * @see OrangeRedisZSetOperations#intersectAndStore
 */
public class OrangeIntersectAndStoreByWeightsExecutor extends OrangeRedisAbstractExecutor {
	
	private OrangeRedisZSetOperations operations;

	/**
	 * Constructs a new OrangeIntersectAndStoreByWeightsExecutor with the specified operations and ID generator.
	 *
	 * @param operations the Redis ZSet operations to be used for weighted intersection operations
	 * @param idGenerator the generator for creating unique executor IDs
	 */
	public OrangeIntersectAndStoreByWeightsExecutor(OrangeRedisZSetOperations operations,OrangeRedisExecutorIdGenerator idGenerator) {
		super(idGenerator);
		this.operations = operations;
	}

	/**
	 * Returns a list of annotation classes that this executor supports.
	 * 
	 * <p>This executor supports the following annotations:
	 * <ul>
	 *   <li>{@link Intersect} - Identifies methods for intersection operations</li>
	 *   <li>{@link CrossOperationKeys} - Specifies the keys to operate on</li>
	 *   <li>{@link StoreTo} - Specifies the destination key</li>
	 *   <li>{@link Aggregate} - Specifies the aggregation type (SUM, MIN, MAX)</li>
	 *   <li>{@link Weights} - Specifies the weights for input sorted sets</li>
	 * </ul>
	 *
	 * @return a list of supported annotation classes
	 */
	@Override
	protected List<Class<? extends Annotation>> getSupportedAnnotationClasses() {
		return OrangeCollectionUtils.asList(Intersect.class,CrossOperationKeys.class,StoreTo.class,Aggregate.class,Weights.class);
	}

	/**
	 * Executes the weighted intersection operation with aggregation and stores the result.
	 * 
	 * <p>This method performs a weighted intersection between the reference key and comparison keys,
	 * and stores the result in the destination key. The scores of elements in each input
	 * sorted set are multiplied by the corresponding weight before being aggregated
	 * according to the specified aggregation type.
	 *
	 * <p>The intersection operation only includes elements that exist in all of the specified
	 * sorted sets. The aggregation type determines how scores are combined:
	 * <ul>
	 *   <li>SUM - The weighted scores are summed</li>
	 *   <li>MIN - The minimum weighted score is used</li>
	 *   <li>MAX - The maximum weighted score is used</li>
	 * </ul>
	 *
	 * @param context the Redis context containing operation parameters including weights and aggregate type
	 * @return the number of elements in the resulting sorted set
	 * @throws Exception if an error occurs during the operation
	 */
	@Override
	public Object execute(OrangeRedisContext context) throws Exception {
		OrangeWeightsContext ctx = (OrangeWeightsContext) context;
		return this.operations.intersectAndStore(
			ctx.getReferenceKey(), 
			ctx.getComparisonKeys(), 
			ctx.getStoreTo(), 
			ctx.getAggregate().operator(),
			ctx.getWeights()
		);
	}
	
	/**
	 * Returns the context class used by this executor.
	 * 
	 * <p>This executor uses {@link OrangeWeightsContext} which provides access to:
	 * <ul>
	 *   <li>The reference key (the first sorted set)</li>
	 *   <li>The comparison keys (additional sorted sets)</li>
	 *   <li>The destination key where the result will be stored</li>
	 *   <li>The weights to apply to each sorted set</li>
	 *   <li>The aggregation type for combining scores</li>
	 * </ul>
	 *
	 * @return the context class for this executor
	 */
	@Override
	public Class<? extends OrangeRedisContext> getContextClass() {
		return OrangeWeightsContext.class;
	}
}