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
import com.langwuyue.orange.redis.annotation.zset.WithScores;
import com.langwuyue.orange.redis.context.OrangeRedisContext;
import com.langwuyue.orange.redis.executor.cross.zset.context.OrangeAggregateContext;
import com.langwuyue.orange.redis.executor.zset.OrangeGetWithScoresAbstractExecutor;
import com.langwuyue.orange.redis.mapping.OrangeRedisExecutorIdGenerator;
import com.langwuyue.orange.redis.operations.OrangeRedisZSetOperations;
import com.langwuyue.orange.redis.utils.OrangeCollectionUtils;

/**
 * Redis sorted set intersection operation executor with scores and aggregation support.
 * 
 * <p>This executor handles methods annotated with {@link Intersect}, {@link CrossOperationKeys}, 
 * {@link WithScores}, and {@link Aggregate} to compute the intersection of multiple sorted sets
 * and return the result set with scores. The aggregation operation allows specifying how scores
 * from different sorted sets should be combined.</p>
 * 
 * @author Liang.Zhong
 * @since 1.0.0
 */
public class OrangeIntersectWithScoresAndAggregateExecutor extends OrangeGetWithScoresAbstractExecutor {
	
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
	public OrangeIntersectWithScoresAndAggregateExecutor(OrangeRedisZSetOperations operations,OrangeRedisExecutorIdGenerator idGenerator) {
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
	 * </ul>
	 * 
	 * @return List of supported annotation classes
	 */
	@Override
	protected List<Class<? extends Annotation>> getSupportedAnnotationClasses() {
		return OrangeCollectionUtils.asList(Intersect.class,CrossOperationKeys.class,WithScores.class,Aggregate.class);
	}

	/**
	 * Executes the intersection calculation on sorted sets and returns the result collection
	 * 
	 * <p>This method performs the actual intersection calculation operation and processes scores
	 * according to the configured aggregation operator and weights.
	 * The returned result contains members in the intersection and their calculated scores.</p>
	 * 
	 * @param context Redis operation context containing keys, aggregation operator, and weights needed for the operation
	 * @param valueField Reflection information of the value field, used for type conversion
	 * @param returnArgumentType Type information of the method return value
	 * @return The calculated intersection collection containing members and score information
	 * @throws Exception If an error occurs during execution
	 */
	@Override
	protected Collection doGet(OrangeRedisContext context, Field valueField, Type returnArgumentType) throws Exception {
		OrangeAggregateContext ctx = (OrangeAggregateContext) context;
		return this.operations.intersectWithScores(
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
	 * <p>This method returns the context class used by the executor for storing and passing
	 * operation-related data during execution. For intersection operations, the
	 * {@link OrangeAggregateContext} context class is used, which provides functionality
	 * to get aggregation operators and weights.</p>
	 * 
	 * @return The {@link OrangeAggregateContext} class for handling intersection calculations with aggregation operations
	 */
	@Override
	public Class<? extends OrangeRedisContext> getContextClass() {
		return OrangeAggregateContext.class;
	}

}