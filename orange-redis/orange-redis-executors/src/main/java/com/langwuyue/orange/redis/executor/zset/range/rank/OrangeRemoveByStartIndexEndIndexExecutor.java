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
import java.util.List;

import com.langwuyue.orange.redis.annotation.EndIndex;
import com.langwuyue.orange.redis.annotation.RemoveMembers;
import com.langwuyue.orange.redis.annotation.StartIndex;
import com.langwuyue.orange.redis.context.OrangeRedisContext;
import com.langwuyue.orange.redis.context.OrangeRedisStartIndexEndIndexContext;
import com.langwuyue.orange.redis.executor.OrangeRedisAbstractExecutor;
import com.langwuyue.orange.redis.mapping.OrangeRedisExecutorIdGenerator;
import com.langwuyue.orange.redis.operations.OrangeRedisZSetOperations;
import com.langwuyue.orange.redis.operations.OrangeRedisZSetOperations.RankRange;
import com.langwuyue.orange.redis.utils.OrangeCollectionUtils;

/**
 * Executor implementation for removing members from a Redis ZSet based on start and end index range.
 * 
 * This executor removes members from a sorted set within a specified index range. The range is defined
 * by a start index and an end index, both inclusive. This provides a flexible way to remove a specific
 * portion of elements from a sorted set, such as removing elements from position 5 to position 10.
 * 
 * The executor supports the following annotations:
 * - RemoveMembers: Indicates this is a member removal operation
 * - StartIndex: Specifies the starting position (inclusive)
 * - EndIndex: Specifies the ending position (inclusive)
 *
 * @author Liang.Zhong
 * @since 1.0.0
 * @see <a href="https://orange.langwuyue.com/redis/advanced/zset">Orange Redis ZSet Documentation</a>
 */
public class OrangeRemoveByStartIndexEndIndexExecutor extends OrangeRedisAbstractExecutor {
	
	/**
	 * Redis ZSet operations instance used to perform ZSet-specific operations.
	 */
	private OrangeRedisZSetOperations operations;

	/**
	 * Constructs a new executor for removing members from a Redis ZSet based on start and end index range.
	 *
	 * @param operations the Redis ZSet operations instance to use for executing ZSet commands
	 * @param idGenerator the ID generator for creating unique executor identifiers
	 */
	public OrangeRemoveByStartIndexEndIndexExecutor(OrangeRedisZSetOperations operations,OrangeRedisExecutorIdGenerator idGenerator) {
		super(idGenerator);
		this.operations = operations;
	}

	/**
	 * Returns a list of annotation classes that this executor supports.
	 * 
	 * @return a list containing RemoveMembers, StartIndex, and EndIndex annotation classes
	 *         that this executor can process for removing members based on index range
	 */
	@Override
	protected List<Class<? extends Annotation>> getSupportedAnnotationClasses() {
		return OrangeCollectionUtils.asList(RemoveMembers.class,StartIndex.class,EndIndex.class);
	}

	/**
	 * Returns the context class used by this executor.
	 * 
	 * @return the OrangeStartIndexEndIndexContext class, which contains the necessary
	 *         parameters for executing index range operations, including the key
	 *         and the start and end index values
	 */
	@Override
	public Class<? extends OrangeRedisContext> getContextClass() {
		return OrangeRedisStartIndexEndIndexContext.class;
	}

	/**
	 * Executes the removal of members from a Redis ZSet based on start and end index range.
	 * 
	 * This method removes all members whose positions fall within the specified index range from
	 * the sorted set. The index range is inclusive on both ends. The method first casts the context
	 * to OrangeStartIndexEndIndexContext to access the key, start index, and end index parameters,
	 * then uses the ZSet operations to perform the removal.
	 *
	 * @param context the Redis operation context containing the key, start index, and end index parameters
	 * @return the number of members that were removed from the sorted set
	 * @throws Exception if an error occurs during the Redis operation
	 */
	@Override
	public Object execute(OrangeRedisContext context) throws Exception {
		OrangeRedisStartIndexEndIndexContext ctx = (OrangeRedisStartIndexEndIndexContext)context;
		return this.operations.removeRange(context.getRedisKey().getValue(), new RankRange(ctx.getStartIndex(),ctx.getEndIndex()));
	}
	
	
}