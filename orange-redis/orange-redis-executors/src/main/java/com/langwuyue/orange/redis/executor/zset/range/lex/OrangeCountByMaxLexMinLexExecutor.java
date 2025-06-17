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
package com.langwuyue.orange.redis.executor.zset.range.lex;

import java.lang.annotation.Annotation;
import java.util.List;

import com.langwuyue.orange.redis.annotation.GetSize;
import com.langwuyue.orange.redis.annotation.zset.MaxLex;
import com.langwuyue.orange.redis.annotation.zset.MinLex;
import com.langwuyue.orange.redis.context.OrangeRedisContext;
import com.langwuyue.orange.redis.executor.OrangeRedisAbstractExecutor;
import com.langwuyue.orange.redis.executor.zset.context.OrangeMaxLexMinLexContext;
import com.langwuyue.orange.redis.mapping.OrangeRedisExecutorIdGenerator;
import com.langwuyue.orange.redis.operations.OrangeRedisZSetOperations;
import com.langwuyue.orange.redis.operations.OrangeRedisZSetOperations.LexRange;
import com.langwuyue.orange.redis.utils.OrangeCollectionUtils;

/**
 * Executor implementation for counting members in a Redis ZSet based on maximum and minimum lexicographical values.
 * 
 * This executor counts the number of members in a sorted set whose string values fall within a specified
 * lexicographical range defined by maximum and minimum string values. Unlike the standard lexicographical range
 * which uses min-to-max ordering, this executor specifically uses max-to-min ordering, which can be useful
 * for certain specialized use cases where reverse lexicographical ordering is required.
 * 
 * The executor supports the following annotations:
 * - GetSize: Indicates this is a counting operation
 * - MaxLex: Specifies the maximum lexicographical value
 * - MinLex: Specifies the minimum lexicographical value
 *
 * @author Liang.Zhong
 * @since 1.0.0
 * @see <a href="https://orange.langwuyue.com/redis/advanced/zset">Orange Redis ZSet Documentation</a>
 */
public class OrangeCountByMaxLexMinLexExecutor extends OrangeRedisAbstractExecutor {
	
	/**
	 * Redis ZSet operations instance used to perform ZSet-specific operations.
	 */
	private OrangeRedisZSetOperations operations;

	/**
	 * Constructs a new executor for counting members in a Redis ZSet based on maximum and minimum lexicographical values.
	 *
	 * @param operations the Redis ZSet operations instance to use for executing ZSet commands
	 * @param idGenerator the ID generator for creating unique executor identifiers
	 */
	public OrangeCountByMaxLexMinLexExecutor(OrangeRedisZSetOperations operations,OrangeRedisExecutorIdGenerator idGenerator) {
		super(idGenerator);
		this.operations = operations;
	}

	/**
	 * Returns a list of annotation classes that this executor supports.
	 * 
	 * @return a list containing GetSize, MaxLex, and MinLex annotation classes
	 *         that this executor can process for counting members within
	 *         a lexicographical range defined by maximum and minimum values
	 */
	@Override
	protected List<Class<? extends Annotation>> getSupportedAnnotationClasses() {
		return OrangeCollectionUtils.asList(GetSize.class,MaxLex.class,MinLex.class);
	}

	/**
	 * Returns the context class used by this executor.
	 * 
	 * @return the OrangeMaxLexMinLexContext class, which contains the necessary
	 *         parameters for executing lexicographical range operations with
	 *         maximum and minimum values, including the key, maximum and minimum
	 *         string values, and inclusion flags
	 */
	@Override
	public Class<? extends OrangeRedisContext> getContextClass() {
		return OrangeMaxLexMinLexContext.class;
	}

	/**
	 * Executes the counting of members in a Redis ZSet based on maximum and minimum lexicographical values.
	 * 
	 * This method counts all members whose string values fall within the specified lexicographical
	 * range in the sorted set, where the range is defined by maximum and minimum string values.
	 * The method first casts the context to OrangeMaxLexMinLexContext to access the key and range
	 * parameters, then uses the ZSet operations to perform the count. The range boundaries can be
	 * inclusive or exclusive based on the context parameters.
	 *
	 * @param context the Redis operation context containing the key, maximum and minimum lexicographical values
	 * @return the number of members that fall within the specified lexicographical range
	 * @throws Exception if an error occurs during the Redis operation
	 */
	@Override
	public Object execute(OrangeRedisContext context) throws Exception {
		OrangeMaxLexMinLexContext ctx = (OrangeMaxLexMinLexContext)context;
		return this.operations.countByLex(context.getRedisKey().getValue(), new LexRange(ctx.getMaxLex(),ctx.getMinLex()));
	}
	
}