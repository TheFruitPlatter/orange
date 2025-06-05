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
import java.util.List;

import com.langwuyue.orange.redis.annotation.GetIndexs;
import com.langwuyue.orange.redis.annotation.RedisValue;
import com.langwuyue.orange.redis.context.OrangeRedisContext;
import com.langwuyue.orange.redis.context.OrangeRedisValueContext;
import com.langwuyue.orange.redis.executor.OrangeRedisAbstractExecutor;
import com.langwuyue.orange.redis.mapping.OrangeRedisExecutorIdGenerator;
import com.langwuyue.orange.redis.operations.OrangeRedisZSetOperations;
import com.langwuyue.orange.redis.utils.OrangeCollectionUtils;
/**
 * Executor for retrieving the rank (position) of a member in a Redis Sorted Set.
 * 
 * <p>This executor provides functionality to get the rank (zero-based position) of
 * a member in a Redis Sorted Set. The rank is determined by the member's score,
 * with members being ordered from the lowest to the highest score.
 *
 * <p>Key features:
 * <ul>
 *   <li>Returns the zero-based position of the member in the sorted set</li>
 *   <li>Returns null if the member does not exist in the set</li>
 *   <li>Supports both simple types and complex objects as members</li>
 *   <li>Uses ascending score order for ranking</li>
 * </ul>
 *
 * <p>The executor uses {@link OrangeRedisZSetOperations} to perform the actual
 * Redis operation and supports both {@link GetIndexs} and {@link RedisValue} annotations
 * for method mapping.
 *
 * @author Liang.Zhong
 * @since 1.0.0
 */
public class OrangeGetRankExecutor extends OrangeRedisAbstractExecutor {

	/** The Redis Sorted Set operations implementation */
	private OrangeRedisZSetOperations operations;

	/**
	 * Constructs a new OrangeGetRankExecutor.
	 *
	 * @param operations the Redis Sorted Set operations implementation
	 * @param idGenerator the executor ID generator for tracking and monitoring
	 */
	public OrangeGetRankExecutor(OrangeRedisZSetOperations operations,OrangeRedisExecutorIdGenerator idGenerator) {
		super(idGenerator);
		this.operations = operations;
	}

	/**
	 * Executes the rank retrieval operation for a member in a Redis Sorted Set.
	 *
	 * <p>This method retrieves the zero-based position of a member in the sorted set.
	 * The position is determined by the member's score in ascending order.
	 *
	 * <p>The method:
	 * <ul>
	 *   <li>Extracts the key and member value from the context</li>
	 *   <li>Queries Redis for the member's rank</li>
	 *   <li>Returns null if the member is not found in the set</li>
	 * </ul>
	 *
	 * @param context the execution context containing key and member information
	 * @return the rank of the member as a Long, or null if the member doesn't exist
	 * @throws Exception if an error occurs during the operation
	 */
	@Override
	public Object execute(OrangeRedisContext context) throws Exception {
		OrangeRedisValueContext ctx = (OrangeRedisValueContext) context;
		Long result = this.operations.getMemberRank(context.getRedisKey().getValue(), ctx.getValue(), ctx.getValueType());
		if(result == null) {
			return null;
		}
		return result;
	}

	/**
	 * Returns the list of supported annotations for this executor.
	 *
	 * <p>This executor supports:
	 * <ul>
	 *   <li>{@link GetIndexs} - for marking methods that retrieve member ranks</li>
	 *   <li>{@link RedisValue} - for marking parameters that represent set members</li>
	 * </ul>
	 *
	 * @return a list of supported annotation classes
	 */
	@Override
	protected List<Class<? extends Annotation>> getSupportedAnnotationClasses() {
		return OrangeCollectionUtils.asList(GetIndexs.class,RedisValue.class);
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