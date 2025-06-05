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
import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.List;

import com.langwuyue.orange.redis.annotation.Count;
import com.langwuyue.orange.redis.annotation.Distinct;
import com.langwuyue.orange.redis.annotation.GetMembers;
import com.langwuyue.orange.redis.annotation.Random;
import com.langwuyue.orange.redis.annotation.zset.WithScores;
import com.langwuyue.orange.redis.context.OrangeRedisContext;
import com.langwuyue.orange.redis.context.OrangeRedisCountContext;
import com.langwuyue.orange.redis.mapping.OrangeRedisExecutorIdGenerator;
import com.langwuyue.orange.redis.operations.OrangeRedisZSetOperations;
import com.langwuyue.orange.redis.utils.OrangeCollectionUtils;

/**
 * Executor for retrieving distinct random members with their scores from a Redis Sorted Set.
 * 
 * <p>This executor extends {@link OrangeGetWithScoresAbstractExecutor} to provide functionality
 * for randomly selecting a specified number of unique members along with their scores from
 * a Redis Sorted Set. It ensures that all returned members are distinct (no duplicates).
 *
 * <p>The executor supports the following annotations:
 * <ul>
 *   <li>{@link GetMembers} - Indicates this is a member retrieval operation</li>
 *   <li>{@link Count} - Specifies the number of members to retrieve</li>
 *   <li>{@link WithScores} - Indicates that scores should be included in the result</li>
 *   <li>{@link Distinct} - Ensures returned members are unique</li>
 *   <li>{@link Random} - Indicates random selection of members</li>
 * </ul>
 *
 * <p>The returned collection contains tuples of members and their scores, where each tuple
 * represents a member-score pair from the sorted set.
 *
 * @author Liang.Zhong
 * @since 1.0.0
 * @see OrangeGetWithScoresAbstractExecutor
 */
public class OrangeDistinctRandomGetMembersWithScoresExecutor extends OrangeGetWithScoresAbstractExecutor {

	private OrangeRedisZSetOperations operations;

	/**
	 * Constructs a new distinct random members with scores executor for Redis Sorted Sets.
	 *
	 * @param operations the Redis Sorted Set operations implementation to use
	 * @param idGenerator the generator for creating unique operation IDs
	 */
	public OrangeDistinctRandomGetMembersWithScoresExecutor(OrangeRedisZSetOperations operations,OrangeRedisExecutorIdGenerator idGenerator) {
		super(idGenerator);
		this.operations = operations;
	}

	/**
	 * Returns the list of annotations supported by this executor.
	 *
	 * <p>This executor supports five annotations:
	 * <ul>
	 *   <li>{@link GetMembers} - For member retrieval operations</li>
	 *   <li>{@link Count} - To specify the number of members to retrieve</li>
	 *   <li>{@link WithScores} - To include scores in the result</li>
	 *   <li>{@link Distinct} - To ensure unique results</li>
	 *   <li>{@link Random} - For random selection</li>
	 * </ul>
	 *
	 * @return a list of supported annotation classes
	 */
	@Override
	protected List<Class<? extends Annotation>> getSupportedAnnotationClasses() {
		return OrangeCollectionUtils.asList(GetMembers.class,Count.class,WithScores.class,Distinct.class,Random.class);
	}

	/**
	 * Performs the actual retrieval of distinct random members with their scores from the Redis Sorted Set.
	 *
	 * <p>This method retrieves a specified number of unique random members along with their scores
	 * from the Sorted Set. The number of members to retrieve is determined by the count value
	 * in the context. The method handles type conversion based on the provided value field
	 * and return argument type.
	 *
	 * <p>The returned collection contains tuples where each tuple contains a member and its
	 * associated score from the sorted set.
	 *
	 * @param context the context containing Redis key and operation parameters
	 * @param valueField the field representing the value type, may be null
	 * @param returnArgumentType the expected return type for the member-score tuples
	 * @return a collection of randomly selected distinct members with their scores
	 * @throws Exception if an error occurs during Redis operation or type conversion
	 */
	@Override
	protected Collection doGet(OrangeRedisContext context, Field valueField, Type returnArgumentType) throws Exception {
		OrangeRedisCountContext ctx = (OrangeRedisCountContext) context;
		return this.operations.distinctRandomMembersWithScores(
				context.getRedisKey().getValue(), 
				ctx.getCount(), 
				context.getValueType(), 
				valueField == null ? returnArgumentType : valueField.getGenericType()
		);
	}

	/**
	 * Returns the context class used by this executor.
	 *
	 * <p>This executor uses {@link OrangeRedisCountContext} to handle operations
	 * that require a count parameter for specifying the number of members to retrieve.
	 *
	 * @return the class of {@link OrangeRedisCountContext}
	 */
	@Override
	public Class<? extends OrangeRedisContext> getContextClass() {
		return OrangeRedisCountContext.class;
	}
}