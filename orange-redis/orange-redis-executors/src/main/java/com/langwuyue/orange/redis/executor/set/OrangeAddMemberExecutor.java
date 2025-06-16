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
package com.langwuyue.orange.redis.executor.set;

import java.lang.annotation.Annotation;
import java.util.List;

import com.langwuyue.orange.redis.annotation.AddMembers;
import com.langwuyue.orange.redis.annotation.RedisValue;
import com.langwuyue.orange.redis.context.OrangeRedisContext;
import com.langwuyue.orange.redis.context.OrangeRedisValueContext;
import com.langwuyue.orange.redis.executor.OrangeRedisAbstractExecutor;
import com.langwuyue.orange.redis.mapping.OrangeRedisExecutorIdGenerator;
import com.langwuyue.orange.redis.operations.OrangeRedisSetOperations;
import com.langwuyue.orange.redis.utils.OrangeCollectionUtils;

/**
 * Executor implementation for adding a single member to a Redis Set.
 * 
 * <p>This executor handles the Redis SADD command for a single value, adding
 * the specified member to the Set stored at the given key. If the member already
 * exists in the Set, it will not be added again (Sets only contain unique elements).
 * 
 * <p>The executor supports the following annotations:
 * <ul>
 *   <li>{@link AddMembers} - Marks a method as an operation to add members to a Set</li>
 *   <li>{@link RedisValue} - Identifies the parameter that contains the value to be added</li>
 * </ul>
 * 
 * <p>Return type conversion is supported for:
 * <ul>
 *   <li>Boolean/boolean - Returns true if the member was added, false if it already existed</li>
 *   <li>Integer/int - Returns 1 if the member was added, 0 if it already existed</li>
 *   <li>Long - Returns the raw Redis response (1 if added, 0 if already existed)</li>
 * </ul>
 *
 * @author Liang.Zhong
 * @since 1.0.0
 * @see OrangeRedisSetOperations#add
 * @see AddMembers
 * @see RedisValue
 */
public class OrangeAddMemberExecutor extends OrangeRedisAbstractExecutor {
	
	private OrangeRedisSetOperations operations;

	/**
	 * Constructs a new OrangeAddMemberExecutor with the specified operations and ID generator.
	 * 
	 * @param operations the Redis Set operations implementation to use for executing the add command
	 * @param idGenerator the generator for creating unique executor IDs
	 */
	public OrangeAddMemberExecutor(OrangeRedisSetOperations operations,OrangeRedisExecutorIdGenerator idGenerator) {
		super(idGenerator);
		this.operations = operations;
	}

	/**
	 * Executes the SADD operation to add a single member to a Redis Set.
	 * 
	 * <p>This method performs the following steps:
	 * <ol>
	 *   <li>Casts the context to {@link OrangeRedisValueContext} to access the value to be added</li>
	 *   <li>Executes the SADD command using the provided Redis key and value</li>
	 *   <li>Converts the result based on the method's return type:
	 *     <ul>
	 *       <li>For boolean returns: true if added, false if already existed</li>
	 *       <li>For integer returns: 1 if added, 0 if already existed</li>
	 *       <li>For other types: returns the raw Redis response</li>
	 *     </ul>
	 *   </li>
	 * </ol>
	 *
	 * @param context the operation context containing the Redis key and value to be added
	 * @return the operation result, converted according to the method's return type
	 * @throws Exception if any error occurs during execution
	 */
	@Override
	public Object execute(OrangeRedisContext context) throws Exception {
		OrangeRedisValueContext ctx = (OrangeRedisValueContext)context;
		Long result = this.operations.add(context.getRedisKey().getValue(), context.getValueType(), ctx.getValue());
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
	 * Returns the list of annotation classes that this executor supports.
	 * 
	 * <p>This executor supports two annotations:
	 * <ul>
	 *   <li>{@link AddMembers} - For marking methods that add members to a Set</li>
	 *   <li>{@link RedisValue} - For identifying the value parameter to be added</li>
	 * </ul>
	 *
	 * @return a list containing the supported annotation classes
	 */
	@Override
	protected List<Class<? extends Annotation>> getSupportedAnnotationClasses() {
		return OrangeCollectionUtils.asList(AddMembers.class,RedisValue.class);
	}
	
	/**
	 * Returns the context class that this executor requires.
	 * 
	 * <p>This executor uses {@link OrangeRedisValueContext} to handle single value
	 * operations for adding a member to a Redis Set.
	 *
	 * @return the {@link OrangeRedisValueContext} class
	 */
	@Override
	public Class<? extends OrangeRedisContext> getContextClass() {
		return OrangeRedisValueContext.class;
	}
}