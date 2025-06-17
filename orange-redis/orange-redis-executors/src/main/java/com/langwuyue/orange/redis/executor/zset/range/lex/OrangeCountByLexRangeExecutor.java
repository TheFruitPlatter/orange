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
import com.langwuyue.orange.redis.annotation.zset.LexRange;
import com.langwuyue.orange.redis.context.OrangeRedisContext;
import com.langwuyue.orange.redis.executor.OrangeRedisAbstractExecutor;
import com.langwuyue.orange.redis.executor.zset.context.OrangeLexRangeContext;
import com.langwuyue.orange.redis.mapping.OrangeRedisExecutorIdGenerator;
import com.langwuyue.orange.redis.operations.OrangeRedisZSetOperations;
import com.langwuyue.orange.redis.utils.OrangeCollectionUtils;

/**
 * Executor implementation for counting members in a Redis ZSet based on lexicographical range.
 * 
 * This executor counts the number of members in a sorted set whose string values fall within a specified
 * lexicographical range. The lexicographical range is defined by a minimum and maximum string value,
 * which can be inclusive or exclusive. This is particularly useful when the sorted set contains
 * string values that need to be counted based on alphabetical ordering.
 * 
 * The executor supports the following annotations:
 * - GetSize: Indicates this is a counting operation
 * - LexRange: Specifies the lexicographical range parameters
 *
 * @author Liang.Zhong
 * @since 1.0.0
 * @see <a href="https://orange.langwuyue.com/redis/advanced/zset">Orange Redis ZSet Documentation</a>
 */
public class OrangeCountByLexRangeExecutor extends OrangeRedisAbstractExecutor {
	
	/**
	 * Redis ZSet operations instance used to perform ZSet-specific operations.
	 */
	private OrangeRedisZSetOperations operations;

	/**
	 * Constructs a new executor for counting members in a Redis ZSet based on lexicographical range.
	 *
	 * @param operations the Redis ZSet operations instance to use for executing ZSet commands
	 * @param idGenerator the ID generator for creating unique executor identifiers
	 */
	public OrangeCountByLexRangeExecutor(OrangeRedisZSetOperations operations,OrangeRedisExecutorIdGenerator idGenerator) {
		super(idGenerator);
		this.operations = operations;
	}

	/**
	 * Returns a list of annotation classes that this executor supports.
	 * 
	 * @return a list containing GetSize and LexRange annotation classes
	 *         that this executor can process for counting members within
	 *         a lexicographical range
	 */
	@Override
	protected List<Class<? extends Annotation>> getSupportedAnnotationClasses() {
		return OrangeCollectionUtils.asList(GetSize.class,LexRange.class);
	}

	/**
	 * Returns the context class used by this executor.
	 * 
	 * @return the OrangeLexRangeContext class, which contains the necessary
	 *         parameters for executing lexicographical range operations, including
	 *         the key, minimum and maximum string values, and inclusion flags
	 */
	@Override
	public Class<? extends OrangeRedisContext> getContextClass() {
		return OrangeLexRangeContext.class;
	}

	/**
	 * Executes the counting of members in a Redis ZSet based on lexicographical range.
	 * 
	 * This method counts all members whose string values fall within the specified lexicographical
	 * range in the sorted set. The method first casts the context to OrangeLexRangeContext to
	 * access the key and range parameters, then uses the ZSet operations to perform the count.
	 * The range is defined by minimum and maximum string values, which can be inclusive or exclusive.
	 *
	 * @param context the Redis operation context containing the key and lexicographical range parameters
	 * @return the number of members that fall within the specified lexicographical range
	 * @throws Exception if an error occurs during the Redis operation
	 */
	@Override
	public Object execute(OrangeRedisContext context) throws Exception {
		OrangeLexRangeContext ctx = (OrangeLexRangeContext)context;
		return this.operations.countByLex(ctx.getRedisKey().getValue(),ctx.getLexRange());
	}
	
}