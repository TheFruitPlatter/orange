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

import com.langwuyue.orange.redis.annotation.cross.CrossOperationKeys;
import com.langwuyue.orange.redis.annotation.cross.Intersect;
import com.langwuyue.orange.redis.annotation.zset.WithScores;
import com.langwuyue.orange.redis.context.OrangeRedisContext;
import com.langwuyue.orange.redis.executor.cross.context.OrangeCrossOperationContext;
import com.langwuyue.orange.redis.executor.zset.OrangeGetWithScoresAbstractExecutor;
import com.langwuyue.orange.redis.mapping.OrangeRedisExecutorIdGenerator;
import com.langwuyue.orange.redis.operations.OrangeRedisZSetOperations;
import com.langwuyue.orange.redis.utils.OrangeCollectionUtils;

/**
 * Executor for performing intersection operations with scores on Redis Sorted Sets.
 * 
 * <p>This executor handles the computation of the intersection between multiple sorted sets,
 * returning both the members and their scores. The intersection operation returns the set of 
 * elements that exist in all of the specified sets, along with their scores.
 * 
 * <p>This executor supports the {@link Intersect}, {@link CrossOperationKeys}, and {@link WithScores} 
 * annotations to mark methods for intersection operations with scores and specify the keys to operate on.
 *
 * @author Liang.Zhong
 * @since 1.0.0
 * @see Intersect
 * @see CrossOperationKeys
 * @see WithScores
 * @see OrangeCrossOperationContext
 * @see OrangeRedisZSetOperations#intersectWithScores
 */
public class OrangeIntersectWithScoresExecutor extends OrangeGetWithScoresAbstractExecutor {
	
	/**
	 * The Redis Sorted Set operations instance used to perform intersection operations with scores.
	 */
	private OrangeRedisZSetOperations operations;

	/**
	 * Constructs a new OrangeIntersectWithScoresExecutor.
	 *
	 * @param operations the Redis Sorted Set operations instance to use for intersection with scores calculations
	 * @param idGenerator the generator for creating unique executor IDs
	 */
	public OrangeIntersectWithScoresExecutor(OrangeRedisZSetOperations operations,OrangeRedisExecutorIdGenerator idGenerator) {
		super(idGenerator);
		this.operations = operations;
	}

	/**
	 * Returns the list of annotation classes that this executor supports.
	 * 
	 * <p>This executor supports the following annotations:
	 * <ul>
	 *   <li>{@link Intersect} - Marks the method as an intersection operation</li>
	 *   <li>{@link CrossOperationKeys} - Specifies the keys to operate on</li>
	 *   <li>{@link WithScores} - Indicates that scores should be included in the result</li>
	 * </ul>
	 *
	 * @return a list of supported annotation classes
	 */
	@Override
	protected List<Class<? extends Annotation>> getSupportedAnnotationClasses() {
		return OrangeCollectionUtils.asList(Intersect.class,CrossOperationKeys.class,WithScores.class);
	}

	/**
	 * Executes the intersection operation with scores on Redis Sorted Sets.
	 * 
	 * <p>This method computes the intersection between the first sorted set (reference key) and all 
	 * successive sorted sets (comparison keys) specified in the context, returning both the members 
	 * and their scores. The intersection operation returns the set of elements that exist in all 
	 * of the specified sets, along with their associated scores.
	 *
	 * @param context the Redis context containing the operation parameters
	 * @param valueField the field that will store the result value, may be null
	 * @param returnArgumentType the expected return type
	 * @return a Collection containing the members and their scores from the resulting intersection set
	 * @throws Exception if an error occurs during the Redis operation
	 */
	@Override
	protected Collection doGet(OrangeRedisContext context, Field valueField, Type returnArgumentType) throws Exception {
		OrangeCrossOperationContext ctx = (OrangeCrossOperationContext) context;
		return this.operations.intersectWithScores(
			ctx.getReferenceKey(), 
			ctx.getComparisonKeys(), 
			ctx.getValueType(), 
			valueField == null ? returnArgumentType : valueField.getGenericType()
		);
	}
	
	/**
	 * Returns the context class that this executor uses for operation parameters.
	 * 
	 * <p>This executor uses {@link OrangeCrossOperationContext} to handle the parameters
	 * required for intersection operations with scores, including reference key and comparison keys.
	 *
	 * @return the class of {@link OrangeCrossOperationContext}
	 */
	@Override
	public Class<? extends OrangeRedisContext> getContextClass() {
		return OrangeCrossOperationContext.class;
	}

}