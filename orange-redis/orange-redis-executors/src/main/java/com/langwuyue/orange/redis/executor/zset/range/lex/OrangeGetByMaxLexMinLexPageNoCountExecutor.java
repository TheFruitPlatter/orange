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
import com.langwuyue.orange.redis.annotation.zset.MaxLex;
import com.langwuyue.orange.redis.annotation.zset.MinLex;
import com.langwuyue.orange.redis.annotation.zset.PageNo;
import com.langwuyue.orange.redis.context.OrangeRedisContext;
import com.langwuyue.orange.redis.executor.OrangeRedisGetAbstractExecutor;
import com.langwuyue.orange.redis.executor.zset.context.OrangeMaxLexMinLexPageNoCountContext;
import com.langwuyue.orange.redis.mapping.OrangeRedisExecutorIdGenerator;
import com.langwuyue.orange.redis.operations.OrangeRedisZSetOperations;
import com.langwuyue.orange.redis.operations.OrangeRedisZSetOperations.LexRange;
import com.langwuyue.orange.redis.operations.OrangeRedisZSetOperations.Pager;
import com.langwuyue.orange.redis.utils.OrangeCollectionUtils;

/**
 * Executor implementation for retrieving paginated members from a Redis ZSet based on maximum and minimum
 * lexicographical values without counting the total number of elements.
 * 
 * This executor retrieves a specific page of members from a sorted set whose string values fall between 
 * the specified maximum and minimum lexicographical values (inclusive). Unlike other paging executors,
 * this implementation does not calculate the total count of matching elements, which makes it more
 * efficient for large datasets where only pagination navigation is needed without displaying total counts.
 * 
 * The executor supports the following annotations:
 * - GetMembers: Indicates this is a member retrieval operation
 * - MaxLex: Specifies the maximum lexicographical value
 * - MinLex: Specifies the minimum lexicographical value
 * - PageNo: Specifies the page number to retrieve
 * - Count: Specifies the number of elements per page
 *
 * @author Liang.Zhong
 * @since 1.0.0
 */
public class OrangeGetByMaxLexMinLexPageNoCountExecutor extends OrangeRedisGetAbstractExecutor {
	
	/**
	 * Redis ZSet operations instance used to perform ZSet-specific operations.
	 */
	private OrangeRedisZSetOperations operations;

	/**
	 * Constructs a new executor for retrieving paginated members from a Redis ZSet based on 
	 * lexicographical range without counting total elements.
	 *
	 * @param operations the Redis ZSet operations instance to use for executing ZSet commands
	 * @param idGenerator the ID generator for creating unique executor identifiers
	 */
	public OrangeGetByMaxLexMinLexPageNoCountExecutor(OrangeRedisZSetOperations operations,OrangeRedisExecutorIdGenerator idGenerator) {
		super(idGenerator);
		this.operations = operations;
	}

	/**
	 * Retrieves a page of members from a Redis ZSet based on maximum and minimum lexicographical values.
	 * 
	 * This method retrieves a specific page of members whose string values fall between the specified 
	 * maximum and minimum lexicographical values in the sorted set. The method first casts the context 
	 * to OrangeMaxLexMinLexPageNoCountContext to access the key, lexicographical range, and pagination 
	 * parameters. It then uses the ZSet operations to perform the retrieval based on these parameters.
	 * 
	 * The pagination is performed without calculating the total count of matching elements, making it
	 * more efficient for large datasets where only page navigation is needed.
	 *
	 * @param context the Redis operation context containing the key, lexicographical range, and pagination parameters
	 * @param valueField the field that will store the retrieved values, used to determine the generic type
	 * @param returnArgumentType the expected return type for the retrieved members
	 * @return a collection of members for the requested page that fall within the lexicographical range
	 * @throws Exception if an error occurs during the Redis operation
	 */
	@Override
	public Collection doGet(OrangeRedisContext context, Field valueField, Type returnArgumentType) throws Exception {
		OrangeMaxLexMinLexPageNoCountContext ctx = (OrangeMaxLexMinLexPageNoCountContext)context;
		return this.operations.rangeByLex(
			ctx.getRedisKey().getValue(), 
			new LexRange(ctx.getMaxLex(),ctx.getMinLex()), 
			new Pager(ctx.getPageNo(),ctx.getCount()),
			ctx.getValueType(), 
			valueField == null ? returnArgumentType : valueField.getGenericType()
		);
	}

	/**
	 * Returns a list of annotation classes that this executor supports.
	 * 
	 * @return a list containing GetMembers, MaxLex, MinLex, PageNo, and Count annotation classes
	 *         that this executor can process for retrieving paginated members within
	 *         a lexicographical range defined by maximum and minimum values
	 */
	@Override
	protected List<Class<? extends Annotation>> getSupportedAnnotationClasses() {
		return OrangeCollectionUtils.asList(GetMembers.class,MaxLex.class,MinLex.class,PageNo.class,Count.class);
	}

	/**
	 * Returns the context class used by this executor.
	 * 
	 * @return the OrangeMaxLexMinLexPageNoCountContext class, which contains the necessary
	 *         parameters for executing paginated lexicographical range operations, including
	 *         the key, maximum and minimum string values, page number, and count per page
	 */
	@Override
	public Class<? extends OrangeRedisContext> getContextClass() {
		return OrangeMaxLexMinLexPageNoCountContext.class;
	}
	
}