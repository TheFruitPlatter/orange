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

import com.langwuyue.orange.redis.annotation.GetMembers;
import com.langwuyue.orange.redis.annotation.Random;
import com.langwuyue.orange.redis.annotation.zset.WithScores;
import com.langwuyue.orange.redis.context.OrangeRedisContext;
import com.langwuyue.orange.redis.mapping.OrangeRedisExecutorIdGenerator;
import com.langwuyue.orange.redis.operations.OrangeRedisZSetOperations;
import com.langwuyue.orange.redis.utils.OrangeCollectionUtils;
/**
 * Executor for randomly retrieving a single member with its score from a Redis Sorted Set (ZSET).
 *
 * <p>This executor provides functionality to get a random member-score pair from a Redis Sorted Set.
 * The operation is useful for scenarios requiring a single random sample while preserving score information.
 *
 * <p>The executor supports the following annotations:
 * <ul>
 *   <li>{@link GetMembers} - Marks this as a member retrieval operation</li>
 *   <li>{@link WithScores} - Indicates that score should be included in the result</li>
 *   <li>{@link Random} - Specifies that the selection should be random</li>
 * </ul>
 *
 * <p>Key features:
 * <ul>
 *   <li>Returns a single random member with its associated score</li>
 *   <li>Uses Redis's ZRANDMEMBER command with WITHSCORES option internally</li>
 *   <li>Supports both simple and complex return types</li>
 *   <li>Automatically handles type conversion of returned value and score</li>
 *   <li>Always returns exactly one member (or null if set is empty)</li>
 * </ul>
 *
 * <p>Return value handling:
 * <ul>
 *   <li>Returns a ScoredValue object containing both the member and its score</li>
 *   <li>For empty sets, returns null</li>
 * </ul>
 *
 * <p>Performance considerations:
 * The ZRANDMEMBER operation has a time complexity of O(1) for single member retrieval.
 * Performance is consistent regardless of the sorted set size.
 *
 * <p>Comparison with {@link OrangeRandomGetMembersWithScoresExecutor}:
 * <ul>
 *   <li>This executor retrieves exactly one random member</li>
 *   <li>The plural form executor can retrieve multiple random members</li>
 *   <li>This executor is more efficient for single-member retrieval</li>
 * </ul>
 *
 *
 * @author Liang.Zhong
 * @since 1.0.0
 * @see <a href="https://orange.langwuyue.com/redis/advanced/zset">Orange Redis ZSet Documentation</a>
 */
public class OrangeRandomGetMemberWithScoresExecutor extends OrangeGetOneWithScoresAbstractExecutor {

	private OrangeRedisZSetOperations operations;

	/**
	 * Constructs a new executor for random member with scores retrieval.
	 *
	 * @param operations the Redis ZSet operations implementation, must not be null
	 * @param idGenerator the executor ID generator for monitoring and tracking, must not be null
	 */
	public OrangeRandomGetMemberWithScoresExecutor(OrangeRedisZSetOperations operations,OrangeRedisExecutorIdGenerator idGenerator) {
		super(idGenerator);
		this.operations = operations;
	}
	
	/**
	 * Returns the list of supported annotations for this executor.
	 *
	 * <p>This executor supports:
	 * <ul>
	 *   <li>{@link GetMembers} - marks member retrieval operations</li>
	 *   <li>{@link WithScores} - indicates score inclusion requirement</li>
	 *   <li>{@link Random} - specifies random selection behavior</li>
	 * </ul>
	 *
	 * @return immutable list of supported annotation classes
	 */
	@Override
	protected List<Class<? extends Annotation>> getSupportedAnnotationClasses() {
		return OrangeCollectionUtils.asList(GetMembers.class,WithScores.class,Random.class);
	}

	/**
	 * Executes the random member with scores retrieval operation.
	 *
	 * <p>Implementation details:
	 * <ul>
	 *   <li>Requests exactly one random member with score from Redis</li>
	 *   <li>Uses Redis's ZRANDMEMBER WITHSCORES command internally</li>
	 *   <li>Handles type conversion for both member and score</li>
	 *   <li>Returns null if the sorted set is empty</li>
	 * </ul>
	 *
	 * @param context the execution context containing Redis key and type information
	 * @param valueField the field annotated with @RedisValue, or null if not present
	 * @param returnArgumentType the expected return type from the method
	 * @return Collection containing one ScoredValue, or null if empty
	 * @throws Exception if Redis operation fails or type conversion fails
	 */
	@Override
	protected Collection doGet(OrangeRedisContext context, Field valueField, Type returnArgumentType) throws Exception {
		return this.operations.randomMembersWithScores(
				context.getRedisKey().getValue(), 
				1, 
				context.getValueType(), 
				valueField == null ? returnArgumentType : valueField.getGenericType()
		);
	}
}