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
import com.langwuyue.orange.redis.annotation.cross.Weights;
import com.langwuyue.orange.redis.annotation.zset.WithScores;
import com.langwuyue.orange.redis.context.OrangeRedisContext;
import com.langwuyue.orange.redis.executor.cross.zset.context.OrangeWeightsContext;
import com.langwuyue.orange.redis.executor.zset.OrangeGetWithScoresAbstractExecutor;
import com.langwuyue.orange.redis.mapping.OrangeRedisExecutorIdGenerator;
import com.langwuyue.orange.redis.operations.OrangeRedisZSetOperations;
import com.langwuyue.orange.redis.utils.OrangeCollectionUtils;

/**
 * Executor for performing weighted union operations on Redis sorted sets with scores
 * 
 * <p>This executor handles Redis ZUNIONSTORE operations with score retrieval, aggregation,
 * and custom weights. It combines multiple sorted sets into a single result set, applying
 * specified weights to each set's scores before aggregation.</p>
 * 
 * <p>Key features:</p>
 * <ul>
 * <li>Supports custom weights for each input sorted set</li>
 * <li>Performs score aggregation (SUM, MIN, or MAX) after applying weights</li>
 * <li>Returns results with member-score pairs</li>
 * </ul>
 * 
 * @author Liang.Zhong
 * @since 1.0.0
 */
public class OrangeUnionWithScoresByWeightsExecutor extends OrangeGetWithScoresAbstractExecutor {
	
	/**
	 * Redis sorted set operations handler for executing weighted union operations
	 */
	private OrangeRedisZSetOperations operations;

	/**
	 * Constructs a new weighted union executor with scores support
	 * 
	 * @param operations The Redis sorted set operations handler for executing weighted union commands
	 * @param idGenerator The generator for creating unique executor identifiers
	 */
	public OrangeUnionWithScoresByWeightsExecutor(OrangeRedisZSetOperations operations,OrangeRedisExecutorIdGenerator idGenerator) {
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
	 * <li>{@link Weights} - Defines custom weights to apply to each input set's scores</li>
	 * </ul>
	 * 
	 * @return List of supported annotation classes
	 */
	@Override
	protected List<Class<? extends Annotation>> getSupportedAnnotationClasses() {
		return OrangeCollectionUtils.asList(Union.class,CrossOperationKeys.class,WithScores.class,Aggregate.class,Weights.class);
	}

	/**
	 * Executes the weighted union operation on sorted sets and returns the result collection with scores
	 * 
	 * <p>This method performs the actual weighted union calculation operation by:
	 * <ol>
	 * <li>Applying specified weights from {@link OrangeWeightsContext} to each input set's scores</li>
	 * <li>Aggregating the weighted scores using the configured operator (SUM, MIN or MAX)</li>
	 * <li>Returning the result with members and their final aggregated scores</li>
	 * </ol>
	 * 
	 * <p>The weights allow for differential treatment of scores from different sets,
	 * enabling use cases like weighted averages or prioritized scoring.</p>
	 * 
	 * @param context Redis operation context containing keys, weights and aggregation operator
	 * @param valueField Reflection information of the value field, used for type conversion
	 * @param returnArgumentType Type information of the method return value
	 * @return The calculated weighted union collection containing members and score information
	 * @throws Exception If an error occurs during execution
	 */
	@Override
	protected Collection doGet(OrangeRedisContext context, Field valueField, Type returnArgumentType) throws Exception {
		OrangeWeightsContext ctx = (OrangeWeightsContext) context;
		return this.operations.unionWithScores(
			ctx.getReferenceKey(), 
			ctx.getComparisonKeys(), 
			ctx.getAggregate().operator(),
			ctx.getWeights(), 
			ctx.getValueType(),
			valueField == null ? returnArgumentType : valueField.getGenericType()
		);
	}
	
	/**
	 * Gets the context class used by this executor
	 * 
	 * <p>This executor uses {@link OrangeWeightsContext} which provides:</p>
	 * <ul>
	 * <li>Reference key and comparison keys for the union operation</li>
	 * <li>Aggregation operator (SUM, MIN or MAX)</li>
	 * <li>Custom weights for each input set</li>
	 * <li>Type information for value conversion</li>
	 * </ul>
	 * 
	 * @return The {@link OrangeWeightsContext} class used by this executor
	 */
	@Override
	public Class<? extends OrangeRedisContext> getContextClass() {
		return OrangeWeightsContext.class;
	}

}