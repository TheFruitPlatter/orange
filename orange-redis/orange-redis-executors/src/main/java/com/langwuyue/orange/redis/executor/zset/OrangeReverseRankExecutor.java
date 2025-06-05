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
import com.langwuyue.orange.redis.annotation.zset.Reverse;
import com.langwuyue.orange.redis.context.OrangeRedisContext;
import com.langwuyue.orange.redis.context.OrangeRedisValueContext;
import com.langwuyue.orange.redis.executor.OrangeRedisAbstractExecutor;
import com.langwuyue.orange.redis.mapping.OrangeRedisExecutorIdGenerator;
import com.langwuyue.orange.redis.operations.OrangeRedisZSetOperations;
import com.langwuyue.orange.redis.utils.OrangeCollectionUtils;

/**
 * Executor for retrieving the reverse rank of a member in a Redis Sorted Set.
 * 
 * <p>This executor provides functionality to get the position (rank) of a member
 * in a Redis Sorted Set when ordered from high to low (reverse order). The rank
 * is 0-based, meaning the member with the highest score has rank 0.
 *
 * <p>The executor supports the following annotations:
 * <ul>
 *   <li>{@link GetIndexs} - Indicates this is an index/rank retrieval operation</li>
 *   <li>{@link RedisValue} - Specifies the member value to get rank for</li>
 *   <li>{@link Reverse} - Indicates reverse (descending) ordering</li>
 * </ul>
 *
 * <p>The executor uses {@link OrangeRedisZSetOperations} to perform the actual
 * Redis operation and returns null if the member doesn't exist in the set.
 *
 * @author Liang.Zhong
 * @since 1.0.0
 * @see OrangeRedisZSetOperations
 */
public class OrangeReverseRankExecutor extends OrangeRedisAbstractExecutor {

	private OrangeRedisZSetOperations operations;

	public OrangeReverseRankExecutor(OrangeRedisZSetOperations operations,OrangeRedisExecutorIdGenerator idGenerator) {
		super(idGenerator);
		this.operations = operations;
	}

	/**
	 * Executes the reverse rank operation for a member in a Redis Sorted Set.
	 *
	 * <p>This method performs the actual Redis operation to get the reverse rank
	 * (descending order) of a member in a sorted set. The rank is 0-based,
	 * where 0 represents the member with the highest score.
	 *
	 * @param context the execution context containing:
	 *                - Redis key information
	 *                - Member value to query
	 *                - Value type information
	 * @return the reverse rank (0-based) or null if member doesn't exist
	 * @throws Exception if:
	 *                   - context is not of type OrangeRedisValueContext
	 *                   - Redis operation fails
	 *                   - any other execution error occurs
	 */
	@Override
	public Object execute(OrangeRedisContext context) throws Exception {
		OrangeRedisValueContext ctx = (OrangeRedisValueContext) context;
		Long result = this.operations.getMemberReverseRank(context.getRedisKey().getValue(), ctx.getValue(), ctx.getValueType());
		if(result == null) {
			return null;
		}
		return result;
	}

	/**
	 * Gets the list of annotation classes supported by this executor.
	 *
	 * <p>This executor supports the following annotations:
	 * <ul>
	 *   <li>{@link GetIndexs} - Marks this as an index/rank retrieval operation</li>
	 *   <li>{@link RedisValue} - Specifies the member value to get rank for</li>
	 *   <li>{@link Reverse} - Indicates reverse (descending) ordering</li>
	 * </ul>
	 *
	 * @return immutable list of supported annotation classes
	 */
	@Override
	protected List<Class<? extends Annotation>> getSupportedAnnotationClasses() {
		return OrangeCollectionUtils.asList(GetIndexs.class,RedisValue.class,Reverse.class);
	}
	
	/**
	 * Gets the expected context class for this executor.
	 *
	 * <p>This executor requires an {@link OrangeRedisValueContext} which provides:
	 * <ul>
	 *   <li>Redis key information</li>
	 *   <li>Member value to query</li>
	 *   <li>Value type information</li>
	 * </ul>
	 *
	 * @return the OrangeRedisValueContext class
	 * @see OrangeRedisValueContext
	 */
	@Override
	public Class<? extends OrangeRedisContext> getContextClass() {
		return OrangeRedisValueContext.class;
	}

}