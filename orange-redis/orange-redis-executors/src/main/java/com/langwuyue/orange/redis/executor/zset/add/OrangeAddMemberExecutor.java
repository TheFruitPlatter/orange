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
package com.langwuyue.orange.redis.executor.zset.add;

import java.lang.annotation.Annotation;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import com.langwuyue.orange.redis.annotation.AddMembers;
import com.langwuyue.orange.redis.annotation.RedisValue;
import com.langwuyue.orange.redis.annotation.zset.Score;
import com.langwuyue.orange.redis.context.OrangeRedisContext;
import com.langwuyue.orange.redis.executor.OrangeRedisAbstractExecutor;
import com.langwuyue.orange.redis.executor.zset.context.OrangeScoreContext;
import com.langwuyue.orange.redis.mapping.OrangeRedisExecutorIdGenerator;
import com.langwuyue.orange.redis.operations.OrangeRedisZSetOperations;
import com.langwuyue.orange.redis.operations.OrangeRedisZSetOperations.ZSetEntry;
import com.langwuyue.orange.redis.utils.OrangeCollectionUtils;

/**
 * Base executor implementation for adding single member to ZSet.
 * 
 * <p>Features:
 * <ul>
 *   <li>Adds single member to Redis sorted set</li>
 *   <li>Supports configuring member value and score via annotations</li>
 *   <li>Automatically handles different return types (int/boolean/long)</li>
 * </ul>
 *
 * @author Liang.Zhong
 * @since 1.0.0
 * @see <a href="https://orange.langwuyue.com/redis/advanced/zset">Orange Redis ZSet Documentation</a>
 */
public class OrangeAddMemberExecutor extends OrangeRedisAbstractExecutor {

	private OrangeRedisZSetOperations operations;
	
	/**
	 * Constructs ZSet member addition executor.
	 *
	 * @param operations Redis ZSet operations interface implementation, cannot be null
	 * @param idGenerator Executor ID generator for monitoring and tracing, cannot be null
	 */
	public OrangeAddMemberExecutor(OrangeRedisZSetOperations operations,OrangeRedisExecutorIdGenerator idGenerator) {
		super(idGenerator);
		this.operations = operations;
	}

	/**
	 * Executes member addition operation.
	 *
	 * <p>Implementation details:
	 * <ul>
	 *   <li>Gets member value and score from context</li>
	 *   <li>Creates ZSet entry and adds to Redis</li>
	 *   <li>Automatically converts result based on method return type:
	 *     <ul>
	 *       <li>boolean: returns whether addition was successful</li>
	 *       <li>int: returns number of members added</li>
	 *       <li>long: returns raw Redis response</li>
	 *     </ul>
	 *   </li>
	 * </ul>
	 *
	 * @param context Execution context containing Redis key, member value and score
	 * @return Addition result converted according to method return type
	 * @throws Exception If Redis operation fails or type conversion fails
	 */
	@Override
	public Object execute(OrangeRedisContext context) throws Exception {
		OrangeScoreContext ctx = (OrangeScoreContext)context;
		Set<ZSetEntry> entries = new LinkedHashSet<>();
		entries.add(new ZSetEntry(ctx.getValue(), ctx.getScore()));
		Long result = this.operations.add(ctx.getRedisKey().getValue(), entries, context.getValueType());
		Class<?> returnClass = context.getOperationMethod().getReturnType();
		if(returnClass == Boolean.class || returnClass == boolean.class) {
			return result != null && result > 0;	
		}
		if(returnClass == Integer.class || returnClass == int.class) {
			return result == null ? 0 : Integer.valueOf(result.toString());	
		}
		return result;
	}
	
	/**
	 * Gets the list of annotation types supported by this executor.
	 *
	 * <p>Supported annotations:
	 * <ul>
	 *   <li>{@link AddMembers} - Marks batch addition operation</li>
	 *   <li>{@link RedisValue} - Specifies member value</li>
	 *   <li>{@link Score} - Specifies member score</li>
	 * </ul>
	 *
	 * @return Immutable list of annotation classes
	 */
	@Override
	protected List<Class<? extends Annotation>> getSupportedAnnotationClasses() {
		return OrangeCollectionUtils.asList(AddMembers.class,RedisValue.class,Score.class);
	}

	/**
	 * Gets the required context type for this executor.
	 *
	 * <p>Requires {@link OrangeScoreContext} to provide:
	 * <ul>
	 *   <li>Redis key information</li>
	 *   <li>Member value</li>
	 *   <li>Score value</li>
	 * </ul>
	 *
	 * @return Required context class
	 */
	@Override
	public Class<? extends OrangeRedisContext> getContextClass() {
		return OrangeScoreContext.class;
	}

}