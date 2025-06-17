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
import com.langwuyue.orange.redis.annotation.GetMembers;
import com.langwuyue.orange.redis.annotation.Random;
import com.langwuyue.orange.redis.annotation.zset.WithScores;
import com.langwuyue.orange.redis.context.OrangeRedisContext;
import com.langwuyue.orange.redis.context.OrangeRedisCountContext;
import com.langwuyue.orange.redis.mapping.OrangeRedisExecutorIdGenerator;
import com.langwuyue.orange.redis.operations.OrangeRedisZSetOperations;
import com.langwuyue.orange.redis.utils.OrangeCollectionUtils;
/**
 * Executor for randomly retrieving members with their scores from a Redis Sorted Set (ZSET).
 *
 * <p>This executor provides functionality to get random member-score pairs from a Redis Sorted Set.
 * The operation is useful for scenarios requiring random sampling while preserving score information.
 *
 * <p>The executor supports the following annotations:
 * <ul>
 *   <li>{@link GetMembers} - Marks this as a member retrieval operation</li>
 *   <li>{@link Count} - Specifies the number of random members to retrieve</li>
 *   <li>{@link WithScores} - Indicates that scores should be included in the result</li>
 *   <li>{@link Random} - Specifies that the selection should be random</li>
 * </ul>
 *
 * <p>Key features:
 * <ul>
 *   <li>Returns random members with their associated scores</li>
 *   <li>Uses Redis's ZRANDMEMBER command with WITHSCORES option internally</li>
 *   <li>Supports both simple and complex return types</li>
 *   <li>Automatically handles type conversion of returned values and scores</li>
 *   <li>Can return multiple random members in a single operation</li>
 * </ul>
 *
 * <p>Return value handling:
 * <ul>
 *   <li>Returns a collection of ScoredValue objects containing both the member and its score</li>
 *   <li>For single member requests, still returns a collection with one element</li>
 * </ul>
 *
 * <p>Performance considerations:
 * The ZRANDMEMBER operation has a time complexity of O(N) where N is the number of requested members.
 * Performance is generally good but degrades as the requested count increases.
 *
 * @author Liang.Zhong
 * @since 1.0.0
 * @see <a href="https://orange.langwuyue.com/redis/advanced/zset">Orange Redis ZSet Documentation</a>
 */
public class OrangeRandomGetMembersWithScoresExecutor extends OrangeGetWithScoresAbstractExecutor {

	private OrangeRedisZSetOperations operations;

	/**
	 * Constructs a new executor for random members with scores retrieval.
	 *
	 * @param operations the Redis ZSet operations implementation, must not be null
	 * @param idGenerator the executor ID generator for monitoring and tracking, must not be null
	 */
	public OrangeRandomGetMembersWithScoresExecutor(OrangeRedisZSetOperations operations,OrangeRedisExecutorIdGenerator idGenerator) {
		super(idGenerator);
		this.operations = operations;
	}

	/**
	 * Returns the list of supported annotations for this executor.
	 *
	 * <p>This executor supports:
	 * <ul>
	 *   <li>{@link GetMembers} - marks member retrieval operations</li>
	 *   <li>{@link Count} - specifies the number of random members to retrieve</li>
	 *   <li>{@link WithScores} - indicates score inclusion requirement</li>
	 *   <li>{@link Random} - specifies random selection behavior</li>
	 * </ul>
	 *
	 * @return immutable list of supported annotation classes
	 */
	@Override
	protected List<Class<? extends Annotation>> getSupportedAnnotationClasses() {
		return OrangeCollectionUtils.asList(GetMembers.class,Count.class,WithScores.class,Random.class);
	}

	/**
	 * Executes the random members with scores retrieval operation.
	 *
	 * <p>Implementation details:
	 * <ul>
	 *   <li>Requests specified number of random members with scores from Redis</li>
	 *   <li>Uses Redis's ZRANDMEMBER WITHSCORES command internally</li>
	 *   <li>Handles type conversion for both members and scores</li>
	 *   <li>Returns empty collection if the sorted set is empty</li>
	 *   <li>Count is obtained from the {@link OrangeRedisCountContext}</li>
	 * </ul>
	 *
	 * @param context the execution context containing Redis key and type information
	 * @param valueField the field annotated with @RedisValue, or null if not present
	 * @param returnArgumentType the expected return type from the method
	 * @return Collection containing ScoredValue objects, or empty collection if no members
	 * @throws Exception if Redis operation fails or type conversion fails
	 */
	@Override
	protected Collection doGet(OrangeRedisContext context, Field valueField, Type returnArgumentType) throws Exception {
		OrangeRedisCountContext ctx = (OrangeRedisCountContext) context;
		return this.operations.randomMembersWithScores(
				context.getRedisKey().getValue(), 
				ctx.getCount(), 
				context.getValueType(), 
				valueField == null ? returnArgumentType : valueField.getGenericType()
		);
	}

	/**
	 * Returns the context class required by this executor.
	 *
	 * <p>This executor requires {@link OrangeRedisCountContext} to obtain the count
	 * of random members to retrieve.
	 *
	 * @return the OrangeRedisCountContext class
	 */
	@Override
	public Class<? extends OrangeRedisContext> getContextClass() {
		return OrangeRedisCountContext.class;
	}
}