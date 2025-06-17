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
package com.langwuyue.orange.redis.executor.zset.range.rank;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.List;

import com.langwuyue.orange.redis.annotation.GetMembers;
import com.langwuyue.orange.redis.annotation.zset.RankRange;
import com.langwuyue.orange.redis.context.OrangeRedisContext;
import com.langwuyue.orange.redis.context.OrangeRedisRankRangeContext;
import com.langwuyue.orange.redis.executor.OrangeRedisGetAbstractExecutor;
import com.langwuyue.orange.redis.mapping.OrangeRedisExecutorIdGenerator;
import com.langwuyue.orange.redis.operations.OrangeRedisZSetOperations;
import com.langwuyue.orange.redis.utils.OrangeCollectionUtils;

/**
 * Executor implementation for retrieving members from a Redis Sorted Set (ZSet) 
 * within a specified rank range.
 * 
 * <p>This executor retrieves members from a ZSet based on their position (rank) in the sorted set,
 * rather than by their score. It supports the following annotations:
 * <ul>
 *   <li>{@link GetMembers} - Indicates this is a member retrieval operation
 *   <li>{@link RankRange} - Specifies the start and end ranks (positions) for the range query
 * </ul>
 * 
 * <p>The executor uses {@link OrangeRedisZSetOperations#range} to perform the actual Redis operation.
 * It retrieves all members whose ranks are within the specified range [start, end].
 * 
 * @author Liang.Zhong
 * @since 1.0.0
 * @see OrangeRedisGetAbstractExecutor
 * @see OrangeRedisRankRangeContext
 * @see OrangeRedisZSetOperations
 * @see <a href="https://orange.langwuyue.com/redis/advanced/zset">Orange Redis ZSet Documentation</a>
 */
public class OrangeGetByRankRangeExecutor extends OrangeRedisGetAbstractExecutor {
	
	/**
	 * Redis ZSet operations instance used to execute the actual Redis commands.
	 */
	private OrangeRedisZSetOperations operations;

	/**
	 * Constructs a new executor for retrieving ZSet members using a rank range.
	 * 
	 * @param operations The Redis ZSet operations implementation to use for executing commands
	 * @param idGenerator The ID generator for creating unique executor identifiers
	 */
	public OrangeGetByRankRangeExecutor(OrangeRedisZSetOperations operations,OrangeRedisExecutorIdGenerator idGenerator) {
		super(idGenerator);
		this.operations = operations;
	}

	/**
	 * Retrieves members from the Redis ZSet within the specified rank range.
	 * 
	 * <p>The method handles type conversion and generic type resolution to ensure the returned
	 * collection matches the expected return type of the annotated method.
	 * 
	 * @param context The operation context containing key and rank range parameters
	 * @param valueField The field representing the value type, may be null
	 * @param returnArgumentType The expected return type from the annotated method
	 * @return A Collection of members within the specified rank range
	 * @throws Exception if the Redis operation fails or type conversion fails
	 */
	@Override
	public Collection doGet(OrangeRedisContext context, Field valueField, Type returnArgumentType) throws Exception {
		OrangeRedisRankRangeContext ctx = (OrangeRedisRankRangeContext)context;
		return this.operations.range(
				ctx.getRedisKey().getValue(), 
				ctx.getRankRange(), 
				ctx.getValueType(), 
				valueField == null ? returnArgumentType : valueField.getGenericType()
		);
	}

	/**
	 * Specifies which annotations this executor supports.
	 * 
	 * <p>This executor supports two annotations:
	 * <ul>
	 *   <li>{@link GetMembers} - Indicates this is a member retrieval operation
	 *   <li>{@link RankRange} - Specifies the start and end ranks for range query
	 * </ul>
	 * 
	 * <p>The combination of these annotations allows the framework to identify methods that should
	 * retrieve members from within a specified rank range.
	 * 
	 * @return A List containing the supported annotation classes
	 */
	@Override
	protected List<Class<? extends Annotation>> getSupportedAnnotationClasses() {
		return OrangeCollectionUtils.asList(GetMembers.class,RankRange.class);
	}

	/**
	 * Specifies the context class used by this executor.
	 * 
	 * @return The OrangeRedisRankRangeContext class
	 */
	@Override
	public Class<? extends OrangeRedisContext> getContextClass() {
		return OrangeRedisRankRangeContext.class;
	}
	
	
}