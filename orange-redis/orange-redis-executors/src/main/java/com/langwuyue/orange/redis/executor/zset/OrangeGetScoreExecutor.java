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
package com.langwuyue.orange.redis.executor.zset;

import java.lang.annotation.Annotation;
import java.math.BigDecimal;
import java.util.List;

import com.langwuyue.orange.redis.annotation.RedisValue;
import com.langwuyue.orange.redis.annotation.zset.GetScores;
import com.langwuyue.orange.redis.context.OrangeRedisContext;
import com.langwuyue.orange.redis.context.OrangeRedisValueContext;
import com.langwuyue.orange.redis.executor.OrangeRedisAbstractExecutor;
import com.langwuyue.orange.redis.mapping.OrangeRedisExecutorIdGenerator;
import com.langwuyue.orange.redis.operations.OrangeRedisZSetOperations;
import com.langwuyue.orange.redis.utils.OrangeCollectionUtils;
/**
 * Executor for retrieving the score of a member in a Redis Sorted Set.
 * 
 * <p>This executor provides functionality to get the score associated with a member
 * in a Redis Sorted Set. It supports automatic type conversion of the score to
 * various numeric types based on the method's return type.
 *
 * <p>Key features:
 * <ul>
 *   <li>Retrieves score for a specific member in the sorted set</li>
 *   <li>Supports automatic type conversion to various numeric types:
 *     <ul>
 *       <li>Double (default)</li>
 *       <li>Long/long</li>
 *       <li>Integer/int</li>
 *       <li>Float/float</li>
 *       <li>BigDecimal</li>
 *     </ul>
 *   </li>
 *   <li>Returns null if the member does not exist in the set</li>
 *   <li>Supports both simple types and complex objects as members</li>
 * </ul>
 *
 * @author Liang.Zhong
 * @since 1.0.0
 */
public class OrangeGetScoreExecutor extends OrangeRedisAbstractExecutor {

	/** The Redis Sorted Set operations implementation */
	private OrangeRedisZSetOperations operations;

	/**
	 * Constructs a new OrangeGetScoreExecutor.
	 *
	 * @param operations the Redis Sorted Set operations implementation
	 * @param idGenerator the executor ID generator for tracking and monitoring
	 */
	public OrangeGetScoreExecutor(OrangeRedisZSetOperations operations,OrangeRedisExecutorIdGenerator idGenerator) {
		super(idGenerator);
		this.operations = operations;
	}

	/**
	 * Executes the score retrieval operation for a member in a Redis Sorted Set.
	 *
	 * <p>This method retrieves the score of a member and performs automatic type
	 * conversion based on the method's return type. The conversion process follows
	 * these steps:
	 * <ol>
	 *   <li>Retrieves the score from Redis as a Double</li>
	 *   <li>Checks if the member exists (returns null if not found)</li>
	 *   <li>Converts the score to the appropriate return type based on the method signature</li>
	 * </ol>
	 *
	 * <p>Supported return types and conversion rules:
	 * <ul>
	 *   <li>Double/double - Returns the original score value</li>
	 *   <li>Long/long - Converts using {@link Double#longValue()}</li>
	 *   <li>Integer/int - Converts using {@link Double#intValue()}</li>
	 *   <li>Float/float - Converts using {@link Double#floatValue()}</li>
	 *   <li>BigDecimal - Creates a new BigDecimal from the score's string representation</li>
	 * </ul>
	 *
	 * @param context the execution context containing key and member information
	 * @return the member's score converted to the appropriate return type, or null if member doesn't exist
	 * @throws Exception if an error occurs during the operation
	 */
	@Override
	public Object execute(OrangeRedisContext context) throws Exception {
		OrangeRedisValueContext ctx = (OrangeRedisValueContext) context;
		List<Double> results = this.operations.score(context.getRedisKey().getValue(), ctx.getValueType(), ctx.getValue());
		if(results == null || results.isEmpty()) {
			return null;
		}
		Double result = results.get(0);
		Class returnClass = context.getOperationMethod().getReturnType();
		if(returnClass == Long.class || returnClass == long.class) {
			return result.longValue();
		}
		if(returnClass == Integer.class || returnClass == int.class) {
			return result.intValue();
		}
		if(returnClass == Float.class || returnClass == float.class) {
			return result.floatValue();
		}
		if(returnClass == BigDecimal.class) {
			return new BigDecimal(result.toString());
		}
		return result;
	}

	/**
	 * Returns the list of supported annotations for this executor.
	 *
	 * <p>This executor supports:
	 * <ul>
	 *   <li>{@link GetScores} - for marking methods that retrieve member scores</li>
	 *   <li>{@link RedisValue} - for marking parameters that represent set members</li>
	 * </ul>
	 *
	 * @return a list of supported annotation classes
	 */
	@Override
	protected List<Class<? extends Annotation>> getSupportedAnnotationClasses() {
		return OrangeCollectionUtils.asList(GetScores.class,RedisValue.class);
	}
	
	/**
	 * Returns the context class used by this executor.
	 *
	 * <p>This executor uses {@link OrangeRedisValueContext} to handle:
	 * <ul>
	 *   <li>Redis key information</li>
	 *   <li>Member value and type information</li>
	 * </ul>
	 *
	 * @return the class of the context used by this executor
	 */
	@Override
	public Class<? extends OrangeRedisContext> getContextClass() {
		return OrangeRedisValueContext.class;
	}

}