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
import com.langwuyue.orange.redis.context.OrangeRedisContext;
import com.langwuyue.orange.redis.executor.OrangeRedisGetAbstractExecutor;
import com.langwuyue.orange.redis.executor.zset.context.OrangeLexRangePageNoCountContext;
import com.langwuyue.orange.redis.mapping.OrangeRedisExecutorIdGenerator;
import com.langwuyue.orange.redis.operations.OrangeRedisZSetOperations;
import com.langwuyue.orange.redis.operations.OrangeRedisZSetOperations.Pager;
import com.langwuyue.orange.redis.utils.OrangeCollectionUtils;

/**
 * Executor implementation for retrieving paginated members from a Redis ZSet based on lexicographical range
 * without performing a separate count operation.
 * 
 * This executor retrieves a specific page of members from a sorted set whose string values fall within
 * a specified lexicographical range. The pagination is controlled by page number and count parameters.
 * Unlike other paging executors, this one does not perform a separate count operation to determine
 * the total number of elements, making it more efficient for large sets where the total count is not needed.
 * 
 * The executor supports the following annotations:
 * - GetMembers: Indicates this is a member retrieval operation
 * - LexRange: Specifies the lexicographical range parameters
 * - PageNo: Specifies the page number for pagination
 * - Count: Specifies the number of items per page
 *
 * @author Liang.Zhong
 * @since 1.0.0
 * @see <a href="https://orange.langwuyue.com/redis/advanced/zset">Orange Redis ZSet Documentation</a>
 */
public class OrangeGetByLexRangePageNoCountExecutor extends OrangeRedisGetAbstractExecutor {
	
	/**
	 * Redis ZSet operations instance used to perform ZSet-specific operations.
	 */
	private OrangeRedisZSetOperations operations;

	/**
	 * Constructs a new executor for retrieving paginated members from a Redis ZSet based on lexicographical range.
	 *
	 * @param operations the Redis ZSet operations instance to use for executing ZSet commands
	 * @param idGenerator the ID generator for creating unique executor identifiers
	 */
	public OrangeGetByLexRangePageNoCountExecutor(OrangeRedisZSetOperations operations,OrangeRedisExecutorIdGenerator idGenerator) {
		super(idGenerator);
		this.operations = operations;
	}

	/**
	 * Retrieves a paginated set of members from a Redis ZSet based on lexicographical range.
	 * 
	 * This method retrieves a specific page of members whose string values fall within the specified
	 * lexicographical range in the sorted set. The method first casts the context to OrangeLexRangePageNoCountContext
	 * to access the key, range parameters, page number, and count. It then calculates the appropriate offset
	 * and limit values for pagination and uses the ZSet operations to perform the retrieval.
	 * 
	 * Unlike other paging executors, this method does not perform a separate count operation to determine
	 * the total number of elements, making it more efficient for large sets where the total count is not needed.
	 *
	 * @param context the Redis operation context containing the key, lexicographical range parameters, page number, and count
	 * @param valueField the field that will store the retrieved values, used to determine the generic type
	 * @param returnArgumentType the expected return type for the retrieved members
	 * @return a collection of members for the specified page that fall within the lexicographical range
	 * @throws Exception if an error occurs during the Redis operation
	 */
	@Override
	public Collection doGet(OrangeRedisContext context, Field valueField, Type returnArgumentType) throws Exception {
		OrangeLexRangePageNoCountContext ctx = (OrangeLexRangePageNoCountContext)context;
		return this.operations.rangeByLex(
			ctx.getRedisKey().getValue(), 
			ctx.getLexRange(), 
			new Pager(ctx.getPageNo(),ctx.getCount()),
			ctx.getValueType(), 
			valueField == null ? returnArgumentType : valueField.getGenericType()
		);
	}

	/**
	 * Returns a list of annotation classes that this executor supports.
	 * 
	 * @return a list containing GetMembers, LexRange, PageNo, and Count annotation classes
	 *         that this executor can process for retrieving paginated members within
	 *         a lexicographical range without performing a separate count operation
	 */
	@Override
	protected List<Class<? extends Annotation>> getSupportedAnnotationClasses() {
		return OrangeCollectionUtils.asList(GetMembers.class,LexRange.class,PageNo.class,Count.class);
	}

	/**
	 * Returns the context class used by this executor.
	 * 
	 * @return the OrangeLexRangePageNoCountContext class, which contains the necessary
	 *         parameters for executing lexicographical range pagination operations, including
	 *         the key, minimum and maximum string values, inclusion flags, page number, and count
	 */
	@Override
	public Class<? extends OrangeRedisContext> getContextClass() {
		return OrangeLexRangePageNoCountContext.class;
	}
	
	
}