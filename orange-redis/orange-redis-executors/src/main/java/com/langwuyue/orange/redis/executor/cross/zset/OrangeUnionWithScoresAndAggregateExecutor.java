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
import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.List;

import com.langwuyue.orange.redis.annotation.cross.Aggregate;
import com.langwuyue.orange.redis.annotation.cross.CrossOperationKeys;
import com.langwuyue.orange.redis.annotation.cross.Union;
import com.langwuyue.orange.redis.annotation.zset.WithScores;
import com.langwuyue.orange.redis.context.OrangeRedisContext;
import com.langwuyue.orange.redis.executor.cross.zset.context.OrangeAggregateContext;
import com.langwuyue.orange.redis.executor.zset.OrangeGetWithScoresAbstractExecutor;
import com.langwuyue.orange.redis.mapping.OrangeRedisExecutorIdGenerator;
import com.langwuyue.orange.redis.operations.OrangeRedisZSetOperations;
import com.langwuyue.orange.redis.utils.OrangeCollectionUtils;

/**
 * Executor for performing union operations on Redis sorted sets with scores and aggregation
 * 
 * <p>This executor handles Redis ZUNIONSTORE operations with score retrieval and aggregation.
 * It combines multiple sorted sets into a single result set, calculating scores based on
 * the specified aggregation function (SUM, MIN, or MAX) and optional weights.</p>
 * 
 * @author Liang.Zhong
 * @since 1.0.0
 */
public class OrangeUnionWithScoresAndAggregateExecutor extends OrangeGetWithScoresAbstractExecutor {
	
	/**
	 * Redis sorted set operations handler for executing union operations
	 */
	private OrangeRedisZSetOperations operations;

	/**
	 * Constructs a new union executor with scores and aggregation support
	 * 
	 * @param operations The Redis sorted set operations handler for executing union commands
	 * @param idGenerator The generator for creating unique executor identifiers
	 */
	public OrangeUnionWithScoresAndAggregateExecutor(OrangeRedisZSetOperations operations,OrangeRedisExecutorIdGenerator idGenerator) {
		super(idGenerator);
		this.operations = operations;
	}

	/**
	 * Gets the list of annotation classes supported by this executor
	 * 
	 * <p>This executor supports the following annotations:</p>
	 * <ul>
	 * <li>{@link Union} - Identifies this as a union operation</li>
	 * <li>{@link CrossOperationKeys} - Specifies other keys to perform union with</li>
	 * <li>{@link WithScores} - Indicates that member scores should be returned</li>
	 * <li>{@link Aggregate} - Specifies how to aggregate scores from different sets</li>
	 * </ul>
	 * 
	 * @return List of supported annotation classes
	 */
	@Override
	protected List<Class<? extends Annotation>> getSupportedAnnotationClasses() {
		return OrangeCollectionUtils.asList(Union.class,CrossOperationKeys.class,WithScores.class,Aggregate.class);
	}

	/**
	 * Executes the union operation on sorted sets and returns the result collection with scores
	 * 
	 * <p>This method performs the actual union calculation operation and processes scores
	 * according to the configured aggregation operator and weights. The returned result contains
	 * all members from the union of the sets along with their aggregated scores.</p>
	 * 
	 * <p>The operation performs the following steps:</p>
	 * <ol>
	 * <li>Combines all members from the reference key and comparison keys</li>
	 * <li>Applies weights to each set's scores if specified</li>
	 * <li>Aggregates scores using the specified operator (SUM, MIN or MAX)</li>
	 * <li>Returns the result with members and their final scores</li>
	 * </ol>
	 * 
	 * @param context Redis operation context containing keys, aggregation operator and weights
	 * @param valueField Reflection information of the value field, used for type conversion
	 * @param returnArgumentType Type information of the method return value
	 * @return The calculated union collection containing members and score information
	 * @throws Exception If an error occurs during execution
	 */
	@Override
	protected Collection doGet(OrangeRedisContext context, Field valueField, Type returnArgumentType) throws Exception {
		OrangeAggregateContext ctx = (OrangeAggregateContext) context;
		return this.operations.unionWithScores(
			ctx.getReferenceKey(), 
			ctx.getComparisonKeys(), 
			ctx.getAggregate().operator(),
			ctx.getAggregate().weights(), 
			ctx.getValueType(),
			valueField == null ? returnArgumentType : valueField.getGenericType()
		);
	}
	
	/**
	 * Gets the context class used by this executor
	 * 
	 * <p>This executor uses {@link OrangeAggregateContext} which provides:</p>
	 * <ul>
	 * <li>Reference key and comparison keys for the union operation</li>
	 * <li>Aggregation operator (SUM, MIN or MAX)</li>
	 * <li>Optional weights for each input set</li>
	 * <li>Type information for value conversion</li>
	 * </ul>
	 * 
	 * @return The {@link OrangeAggregateContext} class used by this executor
	 */
	@Override
	public Class<? extends OrangeRedisContext> getContextClass() {
		return OrangeAggregateContext.class;
	}

}