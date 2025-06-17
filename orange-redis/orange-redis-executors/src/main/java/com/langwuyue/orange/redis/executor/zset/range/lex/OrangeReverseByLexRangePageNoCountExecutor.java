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

import com.langwuyue.orange.redis.annotation.Count;
import com.langwuyue.orange.redis.annotation.GetMembers;
import com.langwuyue.orange.redis.annotation.zset.LexRange;
import com.langwuyue.orange.redis.annotation.zset.PageNo;
import com.langwuyue.orange.redis.annotation.zset.Reverse;
import com.langwuyue.orange.redis.context.OrangeRedisContext;
import com.langwuyue.orange.redis.executor.OrangeRedisGetAbstractExecutor;
import com.langwuyue.orange.redis.executor.zset.context.OrangeLexRangePageNoCountContext;
import com.langwuyue.orange.redis.mapping.OrangeRedisExecutorIdGenerator;
import com.langwuyue.orange.redis.operations.OrangeRedisZSetOperations;
import com.langwuyue.orange.redis.operations.OrangeRedisZSetOperations.Pager;
import com.langwuyue.orange.redis.utils.OrangeCollectionUtils;

/**
 * Executor implementation for retrieving paginated members from a Redis ZSet in reverse lexicographical order.
 * 
 * This executor retrieves members from a sorted set whose string values fall within the specified
 * lexicographical range, returning them in reverse order (from highest to lowest lexicographical value).
 * The results are paginated based on page number and count parameters, without performing a separate
 * count operation for total elements.
 * 
 * The executor supports the following annotations:
 * - GetMembers: Indicates this is a member retrieval operation
 * - LexRange: Specifies the lexicographical range for the query
 * - PageNo: Specifies the page number for pagination
 * - Count: Specifies the number of elements per page
 * - Reverse: Indicates that results should be returned in reverse order
 *
 * @author Liang.Zhong
 * @since 1.0.0
 * @see <a href="https://orange.langwuyue.com/redis/advanced/zset">Orange Redis ZSet Documentation</a>
 */
public class OrangeReverseByLexRangePageNoCountExecutor extends OrangeRedisGetAbstractExecutor {
	
	/**
	 * Redis ZSet operations instance used to perform ZSet-specific operations.
	 */
	private OrangeRedisZSetOperations operations;

	/**
	 * Constructs a new executor for retrieving paginated members from a Redis ZSet in reverse
	 * lexicographical order.
	 *
	 * @param operations the Redis ZSet operations instance to use for executing ZSet commands
	 * @param idGenerator the ID generator for creating unique executor identifiers
	 */
	public OrangeReverseByLexRangePageNoCountExecutor(OrangeRedisZSetOperations operations,OrangeRedisExecutorIdGenerator idGenerator) {
		super(idGenerator);
		this.operations = operations;
	}

	/**
	 * Retrieves paginated members from a Redis ZSet within the specified lexicographical range in reverse order.
	 *
	 * @param context the context containing the Redis key, lexicographical range boundaries, and pagination parameters
	 * @param valueField the field representing the value type, may be null
	 * @param returnArgumentType the expected return type for the collection elements
	 * @return Collection the paginated collection of members within the specified range in reverse order
	 * @throws Exception if an error occurs during the Redis operation or type conversion
	 */
	@Override
	public Collection doGet(OrangeRedisContext context, Field valueField, Type returnArgumentType) throws Exception {
		OrangeLexRangePageNoCountContext ctx = (OrangeLexRangePageNoCountContext)context;
		return this.operations.reverseRangeByLex(
			ctx.getRedisKey().getValue(), 
			ctx.getLexRange(), 
			new Pager(ctx.getPageNo(),ctx.getCount()),
			ctx.getValueType(), 
			valueField == null ? returnArgumentType : valueField.getGenericType()
		);
	}

	/**
	 * Returns the list of annotation classes that this executor supports.
	 * 
	 * This executor supports the following annotations:
	 * - GetMembers: For retrieving members from a Redis ZSet
	 * - LexRange: For specifying the lexicographical range boundaries
	 * - PageNo: For specifying the page number for pagination
	 * - Count: For specifying the number of elements per page
	 * - Reverse: For indicating that results should be returned in reverse order
	 *
	 * @return List of supported annotation classes including GetMembers, LexRange, PageNo, Count, and Reverse
	 */
	@Override
	protected List<Class<? extends Annotation>> getSupportedAnnotationClasses() {
		return OrangeCollectionUtils.asList(GetMembers.class,LexRange.class,PageNo.class,Count.class,Reverse.class);
	}

	/**
	 * Returns the context class used by this executor.
	 * 
	 * @return The OrangeLexRangePageNoCountContext class
	 */
	@Override
	public Class<? extends OrangeRedisContext> getContextClass() {
		return OrangeLexRangePageNoCountContext.class;
	}
	
	
}