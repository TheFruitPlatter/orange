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
import com.langwuyue.orange.redis.annotation.zset.Pager;
import com.langwuyue.orange.redis.context.OrangeRedisContext;
import com.langwuyue.orange.redis.executor.OrangeRedisGetAbstractExecutor;
import com.langwuyue.orange.redis.executor.zset.context.OrangeMaxLexMinLexPagerContext;
import com.langwuyue.orange.redis.mapping.OrangeRedisExecutorIdGenerator;
import com.langwuyue.orange.redis.operations.OrangeRedisZSetOperations;
import com.langwuyue.orange.redis.operations.OrangeRedisZSetOperations.LexRange;
import com.langwuyue.orange.redis.utils.OrangeCollectionUtils;

/**
 * Executor implementation for retrieving members from a Redis ZSet based on maximum and minimum
 * lexicographical values with pagination support.
 * 
 * This executor retrieves members from a sorted set whose string values fall within the specified
 * lexicographical range, with support for pagination through the Pager annotation. The range is defined
 * by maximum and minimum string values, and results are returned in pages according to the pager parameters.
 * 
 * The executor supports the following annotations:
 * - GetMembers: Indicates this is a member retrieval operation
 * - MaxLex: Specifies the maximum lexicographical value for the range
 * - MinLex: Specifies the minimum lexicographical value for the range
 * - Pager: Provides pagination parameters (offset and count)
 *
 * @author Liang.Zhong
 * @since 1.0.0
 * @see <a href="https://orange.langwuyue.com/redis/advanced/zset">Orange Redis ZSet Documentation</a>
 */
public class OrangeGetByMaxLexMinLexPagerExecutor extends OrangeRedisGetAbstractExecutor {
	
	/**
	 * Redis ZSet operations instance used to perform ZSet-specific operations.
	 */
	private OrangeRedisZSetOperations operations;

	/**
	 * Constructs a new executor for retrieving members from a Redis ZSet based on
	 * lexicographical range with pagination support.
	 *
	 * @param operations the Redis ZSet operations instance to use for executing ZSet commands
	 * @param idGenerator the ID generator for creating unique executor identifiers
	 */
	public OrangeGetByMaxLexMinLexPagerExecutor(OrangeRedisZSetOperations operations,OrangeRedisExecutorIdGenerator idGenerator) {
		super(idGenerator);
		this.operations = operations;
	}

	/**
	 * Retrieves members from a Redis ZSet within the specified lexicographical range with pagination.
	 * 
	 * This method retrieves members whose string values fall within the specified lexicographical range,
	 * applying pagination parameters from the context. The results are converted to the appropriate
	 * return type based on the valueField or returnArgumentType.
	 *
	 * @param context the Redis operation context containing the key, range boundaries, and pagination parameters
	 * @param valueField the field representing the value type in the target object, may be null
	 * @param returnArgumentType the expected return type for the collection elements
	 * @return Collection a collection of members within the specified range and page
	 * @throws Exception if an error occurs during the Redis operation or type conversion
	 */
	@Override
	public Collection doGet(OrangeRedisContext context, Field valueField, Type returnArgumentType) throws Exception {
		OrangeMaxLexMinLexPagerContext ctx = (OrangeMaxLexMinLexPagerContext)context;
		return this.operations.rangeByLex(
			ctx.getRedisKey().getValue(), 
			new LexRange(ctx.getMaxLex(),ctx.getMinLex()), 
			ctx.getPager(),
			ctx.getValueType(), 
			valueField == null ? returnArgumentType : valueField.getGenericType()
		);
	}

	/**
	 * Returns a list of annotation classes that this executor supports.
	 * 
	 * @return a list containing GetMembers, MaxLex, MinLex, and Pager annotation classes
	 *         that this executor can process for retrieving members within
	 *         a lexicographical range with pagination
	 */
	@Override
	protected List<Class<? extends Annotation>> getSupportedAnnotationClasses() {
		return OrangeCollectionUtils.asList(GetMembers.class,MaxLex.class,MinLex.class,Pager.class);
	}

	/**
	 * Returns the context class used by this executor.
	 * 
	 * @return the OrangeMaxLexMinLexPagerContext class, which contains the necessary
	 *         parameters for executing lexicographical range operations with pagination,
	 *         including the key, range boundaries, and pagination parameters
	 */
	@Override
	public Class<? extends OrangeRedisContext> getContextClass() {
		return OrangeMaxLexMinLexPagerContext.class;
	}
	
}