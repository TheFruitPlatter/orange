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
import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.List;

import com.langwuyue.orange.redis.annotation.GetMembers;
import com.langwuyue.orange.redis.annotation.zset.MaxLex;
import com.langwuyue.orange.redis.annotation.zset.MinLex;
import com.langwuyue.orange.redis.context.OrangeRedisContext;
import com.langwuyue.orange.redis.executor.OrangeRedisGetAbstractExecutor;
import com.langwuyue.orange.redis.executor.zset.context.OrangeMaxLexMinLexContext;
import com.langwuyue.orange.redis.mapping.OrangeRedisExecutorIdGenerator;
import com.langwuyue.orange.redis.operations.OrangeRedisZSetOperations;
import com.langwuyue.orange.redis.operations.OrangeRedisZSetOperations.LexRange;
import com.langwuyue.orange.redis.utils.OrangeCollectionUtils;

/**
 * Executor implementation for retrieving members from a Redis ZSet based on maximum and minimum
 * lexicographical values.
 * 
 * This executor retrieves members from a sorted set whose string values fall between the specified
 * maximum and minimum lexicographical values (inclusive). This is particularly useful for applications
 * that need to retrieve elements within a specific string range, such as implementing alphabetical
 * filtering or text-based range queries.
 * 
 * The executor supports the following annotations:
 * - GetMembers: Indicates this is a member retrieval operation
 * - MaxLex: Specifies the maximum lexicographical value
 * - MinLex: Specifies the minimum lexicographical value
 *
 * @author Liang.Zhong
 * @since 1.0.0
 * @see <a href="https://orange.langwuyue.com/redis/advanced/zset">Orange Redis ZSet Documentation</a>
 */
public class OrangeGetByMaxLexMinLexExecutor extends OrangeRedisGetAbstractExecutor {
	
	/**
	 * Redis ZSet operations instance used to perform ZSet-specific operations.
	 */
	private OrangeRedisZSetOperations operations;

	/**
	 * Constructs a new executor for retrieving members from a Redis ZSet based on lexicographical range.
	 *
	 * @param operations the Redis ZSet operations instance to use for executing ZSet commands
	 * @param idGenerator the ID generator for creating unique executor identifiers
	 */
	public OrangeGetByMaxLexMinLexExecutor(OrangeRedisZSetOperations operations,OrangeRedisExecutorIdGenerator idGenerator) {
		super(idGenerator);
		this.operations = operations;
	}

	/**
	 * Retrieves members from a Redis ZSet based on maximum and minimum lexicographical values.
	 * 
	 * This method retrieves members whose string values fall between the specified maximum and minimum
	 * lexicographical values in the sorted set. The method first casts the context to OrangeMaxLexMinLexContext
	 * to access the key and lexicographical range parameters. It then uses the ZSet operations to perform
	 * the retrieval based on the lexicographical range.
	 *
	 * @param context the Redis operation context containing the key and lexicographical range parameters
	 * @param valueField the field that will store the retrieved values, used to determine the generic type
	 * @param returnArgumentType the expected return type for the retrieved members
	 * @return a collection of members that fall within the lexicographical range
	 * @throws Exception if an error occurs during the Redis operation
	 */
	@Override
	public Collection doGet(OrangeRedisContext context, Field valueField, Type returnArgumentType) throws Exception {
		OrangeMaxLexMinLexContext ctx = (OrangeMaxLexMinLexContext)context;
		return this.operations.rangeByLex(
			ctx.getRedisKey().getValue(), 
			new LexRange(ctx.getMaxLex(),ctx.getMinLex()), 
			ctx.getValueType(), 
			valueField == null ? returnArgumentType : valueField.getGenericType()
		);
	}

	/**
	 * Returns a list of annotation classes that this executor supports.
	 * 
	 * @return a list containing GetMembers, MaxLex, and MinLex annotation classes
	 *         that this executor can process for retrieving members within
	 *         a lexicographical range defined by maximum and minimum values
	 */
	@Override
	protected List<Class<? extends Annotation>> getSupportedAnnotationClasses() {
		return OrangeCollectionUtils.asList(GetMembers.class,MaxLex.class,MinLex.class);
	}

	/**
	 * Returns the context class used by this executor.
	 * 
	 * @return the OrangeMaxLexMinLexContext class, which contains the necessary
	 *         parameters for executing lexicographical range operations, including
	 *         the key, maximum and minimum string values
	 */
	@Override
	public Class<? extends OrangeRedisContext> getContextClass() {
		return OrangeMaxLexMinLexContext.class;
	}
	
}