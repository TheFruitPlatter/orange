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
import com.langwuyue.orange.redis.annotation.cross.Intersect;
import com.langwuyue.orange.redis.annotation.cross.Weights;
import com.langwuyue.orange.redis.annotation.zset.WithScores;
import com.langwuyue.orange.redis.context.OrangeRedisContext;
import com.langwuyue.orange.redis.executor.cross.zset.context.OrangeWeightsContext;
import com.langwuyue.orange.redis.executor.zset.OrangeGetWithScoresAbstractExecutor;
import com.langwuyue.orange.redis.mapping.OrangeRedisExecutorIdGenerator;
import com.langwuyue.orange.redis.operations.OrangeRedisZSetOperations;
import com.langwuyue.orange.redis.utils.OrangeCollectionUtils;

/**
 * Redis sorted set intersection operation executor with scores and weights support.
 * 
 * <p>This executor handles methods annotated with {@link Intersect}, {@link CrossOperationKeys}, 
 * {@link WithScores}, {@link Aggregate}, and {@link Weights} to compute the intersection of multiple 
 * sorted sets and return the result set with scores. The weights annotation allows specifying 
 * different weights for each sorted set during the intersection operation.</p>
 * 
 * @author Liang.Zhong
 * @since 1.0.0
 */
public class OrangeIntersectWithScoresByWeightsExecutor extends OrangeGetWithScoresAbstractExecutor {
	
	/**
	 * Redis sorted set operations interface for executing intersection calculations
	 */
	private OrangeRedisZSetOperations operations;

	/**
	 * Constructor to initialize the executor
	 * 
	 * @param operations Redis sorted set operations interface providing intersection functionality
	 * @param idGenerator Executor ID generator for creating unique executor identifiers
	 */
	public OrangeIntersectWithScoresByWeightsExecutor(OrangeRedisZSetOperations operations,OrangeRedisExecutorIdGenerator idGenerator) {
		super(idGenerator);
		this.operations = operations;
	}

	/**
	 * Gets the list of annotation classes supported by this executor
	 * 
	 * <p>This executor supports the following annotations:</p>
	 * <ul>
	 * <li>{@link Intersect} - Identifies this as an intersection operation</li>
	 * <li>{@link CrossOperationKeys} - Specifies other keys to perform intersection with</li>
	 * <li>{@link WithScores} - Indicates that member scores should be returned</li>
	 * <li>{@link Aggregate} - Specifies how to aggregate scores from different sets</li>
	 * <li>{@link Weights} - Defines the weights to apply to each sorted set</li>
	 * </ul>
	 * 
	 * @return List of supported annotation classes
	 */
	@Override
	protected List<Class<? extends Annotation>> getSupportedAnnotationClasses() {
		return OrangeCollectionUtils.asList(Intersect.class,CrossOperationKeys.class,WithScores.class,Aggregate.class,Weights.class);
	}

	/**
	 * Executes the intersection calculation on sorted sets with weights and returns the result collection
	 * 
	 * <p>This method performs the actual intersection calculation operation and processes scores
	 * according to the configured aggregation operator and weights. The weights are applied to each
	 * sorted set before the aggregation operation is performed. The returned result contains members
	 * in the intersection and their calculated scores.</p>
	 * 
	 * @param context Redis operation context containing keys, aggregation operator, and weights needed for the operation
	 * @param valueField Reflection information of the value field, used for type conversion
	 * @param returnArgumentType Type information of the method return value
	 * @return The calculated intersection collection containing members and score information
	 * @throws Exception If an error occurs during execution
	 */
	@Override
	protected Collection doGet(OrangeRedisContext context, Field valueField, Type returnArgumentType) throws Exception {
		OrangeWeightsContext ctx = (OrangeWeightsContext) context;
		return this.operations.intersectWithScores(
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
	 * <p>This method returns the context class used by the executor for storing and passing
	 * operation-related data during execution. For intersection operations with weights,
	 * the {@link OrangeWeightsContext} context class is used, which provides functionality
	 * to get weights along with aggregation operators.</p>
	 * 
	 * @return The {@link OrangeWeightsContext} class for handling intersection calculations with weights
	 */
	@Override
	public Class<? extends OrangeRedisContext> getContextClass() {
		return OrangeWeightsContext.class;
	}

}