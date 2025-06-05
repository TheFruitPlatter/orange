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
package com.langwuyue.orange.redis.executor.zset.add;

import java.lang.annotation.Annotation;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import com.langwuyue.orange.redis.annotation.AddMembers;
import com.langwuyue.orange.redis.annotation.Member;
import com.langwuyue.orange.redis.context.OrangeRedisContext;
import com.langwuyue.orange.redis.executor.OrangeRedisAbstractExecutor;
import com.langwuyue.orange.redis.executor.zset.context.OrangeAddMemberContext;
import com.langwuyue.orange.redis.mapping.OrangeRedisExecutorIdGenerator;
import com.langwuyue.orange.redis.operations.OrangeRedisZSetOperations;
import com.langwuyue.orange.redis.operations.OrangeRedisZSetOperations.ZSetEntry;
import com.langwuyue.orange.redis.utils.OrangeCollectionUtils;

/**
 * Annotation-based ZSet member addition executor implementation.
 * 
 * <p>Features:
 * <ul>
 *   <li>Driven by {@link AddMembers} and {@link Member} annotations</li>
 *   <li>Automatic return value conversion to Boolean/Integer/Long types</li>
 *   <li>Supports single member addition operation</li>
 *   <li>Handles different return type conversions automatically</li>
 * </ul>
 *
 * @author Liang.Zhong
 * @since 1.0.0
 */
public class OrangeAddByMemberAnnotationExecutor extends OrangeRedisAbstractExecutor {

	private OrangeRedisZSetOperations operations;
	
	/**
	 * Constructor.
	 *
	 * @param operations Redis ZSet operations interface instance
	 * @param idGenerator Executor ID generator
	 */
	public OrangeAddByMemberAnnotationExecutor(OrangeRedisZSetOperations operations,OrangeRedisExecutorIdGenerator idGenerator) {
		super(idGenerator);
		this.operations = operations;
	}

	/**
	 * Executes the member addition operation.
	 *
	 * <p>Execution flow:
	 * <ol>
	 *   <li>Converts context to OrangeAddMemberContext</li>
	 *   <li>Creates ZSetEntry set and adds member</li>
	 *   <li>Calls Redis operations interface to perform addition</li>
	 *   <li>Converts result based on method return type</li>
	 * </ol>
	 *
	 * @param context Redis operation context
	 * @return Addition result, automatically converted based on method return type:
	 *         <ul>
	 *           <li>Boolean/boolean: Whether addition succeeded</li>
	 *           <li>Integer/int: Number of members added</li>
	 *           <li>Others: Original Long type result</li>
	 *         </ul>
	 * @throws Exception Possible exceptions during execution
	 */
	@Override
	public Object execute(OrangeRedisContext context) throws Exception {
		OrangeAddMemberContext ctx = (OrangeAddMemberContext)context;
		Set<ZSetEntry> entries = new LinkedHashSet<>();
		entries.add(ctx.getMember());
		Long result = this.operations.add(ctx.getRedisKey().getValue(), entries, context.getValueType());
		
		Class<?> returnClass = context.getOperationMethod().getReturnType();
		if(returnClass == Boolean.class || returnClass == boolean.class) {
			return result != null && result > 0;	
		}
		if(returnClass == Integer.class || returnClass == int.class) {
			return result == null ? 0 : Integer.valueOf(result.toString());	
		}
		return result;
	}

	/**
	 * Gets the list of annotation types supported by this executor.
	 *
	 * <p>Supported annotation types:
	 * <ul>
	 *   <li>{@link AddMembers} - Marks batch addition operation</li>
	 *   <li>{@link Member} - Specifies member information</li>
	 * </ul>
	 *
	 * @return Immutable list of annotation classes
	 */
	@Override
	protected List<Class<? extends Annotation>> getSupportedAnnotationClasses() {
		return OrangeCollectionUtils.asList(AddMembers.class,Member.class);
	}

	/**
	 * Gets the context class required by this executor.
	 *
	 * <p>Requires {@link OrangeAddMemberContext} to provide:
	 * <ul>
	 *   <li>Redis key information</li>
	 *   <li>Member information</li>
	 *   <li>Value type information</li>
	 * </ul>
	 *
	 * @return Required context class
	 */
	@Override
	public Class<? extends OrangeRedisContext> getContextClass() {
		return OrangeAddMemberContext.class;
	}

}