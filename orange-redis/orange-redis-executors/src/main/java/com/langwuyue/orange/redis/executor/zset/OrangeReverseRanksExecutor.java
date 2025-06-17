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

import com.langwuyue.orange.redis.annotation.zset.Reverse;
import com.langwuyue.orange.redis.context.OrangeRedisContext;
import com.langwuyue.orange.redis.executor.OrangeGetIndexAbstractExecutor;
import com.langwuyue.orange.redis.logger.OrangeRedisLogger;
import com.langwuyue.orange.redis.mapping.OrangeRedisExecutorIdGenerator;
import com.langwuyue.orange.redis.operations.OrangeRedisZSetOperations;

/**
 * Executor for retrieving reverse rank (descending order) of members in a Redis Sorted Set (ZSET).
 *
 * <p>This executor provides functionality to get the reverse rank of a member in a sorted set,
 * where ranks are ordered from highest to lowest score (descending order).
 *
 * <p>Key features:
 * <ul>
 *   <li>Returns the reverse rank (0-based index) of a member in the sorted set</li>
 *   <li>Reverse rank means highest score has rank 0, next highest rank 1, etc.</li>
 *   <li>Returns null if the member does not exist in the sorted set</li>
 *   <li>Automatically handles type conversion of member values</li>
 * </ul>
 *
 * <p>Return value handling:
 * <ul>
 *   <li>Returns the 0-based reverse rank (Long) of the member</li>
 *   <li>Returns null if the member does not exist in the sorted set</li>
 * </ul>
 *
 * <p>Performance considerations:
 * <ul>
 *   <li>Time complexity is O(log(N)) where N is the number of elements in the sorted set</li>
 *   <li>Performance is consistent regardless of the sorted set size</li>
 * </ul>
 *
 * <p>Underlying Redis command:
 * This executor uses the ZREVRANK command internally to perform the operation.
 *
 * <p>Comparison with forward rank:
 * <ul>
 *   <li>Reverse rank counts from highest to lowest score (descending order)</li>
 *   <li>Forward rank counts from lowest to highest score (ascending order)</li>
 *   <li>Both use the same time complexity</li>
 * </ul>
 *
 * @author Liang.Zhong
 * @since 1.0.0
 * @see <a href="https://orange.langwuyue.com/redis/advanced/zset">Orange Redis ZSet Documentation</a>
 */
public class OrangeReverseRanksExecutor extends OrangeGetIndexAbstractExecutor {

	private OrangeRedisZSetOperations operations;

	public OrangeReverseRanksExecutor(OrangeRedisZSetOperations operations,OrangeRedisExecutorIdGenerator idGenerator,OrangeRedisLogger logger) {
		super(idGenerator,logger);
		this.operations = operations;
	}
	
	/**
	 * Gets the reverse rank (descending order) of a member in the sorted set.
	 *
	 * @param context the execution context containing:
	 *                - Redis key information
	 *                - Value type information
	 * @param value the member value to get the rank for
	 * @return the 0-based reverse rank (null if member doesn't exist)
	 * @throws Exception if:
	 *                   - Redis operation fails
	 *                   - any other execution error occurs
	 */
	@Override
	protected Long getIndex(OrangeRedisContext context,Object value) throws Exception {
		return this.operations.getMemberReverseRank(context.getRedisKey().getValue(), value, context.getValueType());
	}

	/**
	 * Gets the list of supported annotation classes for this executor.
	 *
	 * <p>This executor supports the {@link Reverse} annotation in addition to
	 * the annotations supported by the parent class.
	 *
	 * @return list of supported annotation classes including:
	 *         - All annotations from parent class
	 *         - {@link Reverse} annotation
	 */
	@Override
	protected List<Class<? extends Annotation>> getSupportedAnnotationClasses() {
		List classes = super.getSupportedAnnotationClasses();
		classes.add(Reverse.class);
		return classes;
	}
}