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
import com.langwuyue.orange.redis.annotation.zset.LexRange;
import com.langwuyue.orange.redis.annotation.zset.Pager;
import com.langwuyue.orange.redis.context.OrangeRedisContext;
import com.langwuyue.orange.redis.executor.OrangeRedisGetAbstractExecutor;
import com.langwuyue.orange.redis.executor.zset.context.OrangeLexRangePagerContext;
import com.langwuyue.orange.redis.mapping.OrangeRedisExecutorIdGenerator;
import com.langwuyue.orange.redis.operations.OrangeRedisZSetOperations;
import com.langwuyue.orange.redis.utils.OrangeCollectionUtils;

/**
 * Executor implementation for retrieving paginated members from a Redis ZSet based on lexicographical range
 * with comprehensive paging information.
 * 
 * This executor retrieves members from a sorted set whose string values fall within a specified
 * lexicographical range, and returns them along with paging metadata (such as total count, 
 * current page, etc.). This is useful for applications that need to display paginated results
 * with navigation controls.
 * 
 * The executor supports the following annotations:
 * - GetMembers: Indicates this is a member retrieval operation
 * - LexRange: Specifies the lexicographical range parameters
 * - Pager: Specifies the pagination parameters and controls
 *
 * @author Liang.Zhong
 * @since 1.0.0
 */
public class OrangeGetByLexRangePagerExecutor extends OrangeRedisGetAbstractExecutor {
	
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
	public OrangeGetByLexRangePagerExecutor(OrangeRedisZSetOperations operations,OrangeRedisExecutorIdGenerator idGenerator) {
		super(idGenerator);
		this.operations = operations;
	}

	/**
	 * Retrieves a paginated set of members from a Redis ZSet based on lexicographical range,
	 * along with comprehensive paging information.
	 * 
	 * This method retrieves members whose string values fall within the specified lexicographical range
	 * in the sorted set, and returns them with paging metadata. The method first casts the context to
	 * OrangeLexRangePagerContext to access the key, range parameters, and pager information. It then
	 * uses the ZSet operations to perform the retrieval with pagination.
	 *
	 * @param context the Redis operation context containing the key, lexicographical range parameters, and pager information
	 * @param valueField the field that will store the retrieved values, used to determine the generic type
	 * @param returnArgumentType the expected return type for the retrieved members
	 * @return a collection of members that fall within the lexicographical range, along with paging metadata
	 * @throws Exception if an error occurs during the Redis operation
	 */
	@Override
	public Collection doGet(OrangeRedisContext context, Field valueField, Type returnArgumentType) throws Exception {
		OrangeLexRangePagerContext ctx = (OrangeLexRangePagerContext)context;
		return this.operations.rangeByLex(
			ctx.getRedisKey().getValue(), 
			ctx.getLexRange(), 
			ctx.getPager(),
			ctx.getValueType(), 
			valueField == null ? returnArgumentType : valueField.getGenericType()
		);
	}

	/**
	 * Returns a list of annotation classes that this executor supports.
	 * 
	 * @return a list containing GetMembers, LexRange, and Pager annotation classes
	 *         that this executor can process for retrieving paginated members within
	 *         a lexicographical range with comprehensive paging information
	 */
	@Override
	protected List<Class<? extends Annotation>> getSupportedAnnotationClasses() {
		return OrangeCollectionUtils.asList(GetMembers.class,LexRange.class,Pager.class);
	}

	/**
	 * Returns the context class used by this executor.
	 * 
	 * @return the OrangeLexRangePagerContext class, which contains the necessary
	 *         parameters for executing lexicographical range pagination operations, including
	 *         the key, minimum and maximum string values, inclusion flags, and pager information
	 */
	@Override
	public Class<? extends OrangeRedisContext> getContextClass() {
		return OrangeLexRangePagerContext.class;
	}
	
	
}