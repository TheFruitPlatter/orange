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
package com.langwuyue.orange.redis.executor.hash;

import java.lang.annotation.Annotation;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.langwuyue.orange.redis.annotation.AddMembers;
import com.langwuyue.orange.redis.annotation.Member;
import com.langwuyue.orange.redis.context.OrangeRedisContext;
import com.langwuyue.orange.redis.executor.OrangeRedisAbstractExecutor;
import com.langwuyue.orange.redis.executor.hash.context.OrangeAddMemberContext;
import com.langwuyue.orange.redis.mapping.OrangeRedisExecutorIdGenerator;
import com.langwuyue.orange.redis.operations.OrangeRedisHashOperations;
import com.langwuyue.orange.redis.utils.OrangeCollectionUtils;
import com.langwuyue.orange.redis.utils.OrangeReflectionUtils;

/**
 * Executor implementation for adding members to a Redis Hash, specifically handling
 * method parameters annotated with {@link Member}.
 * 
 * <p>This executor extends {@link OrangeRedisAbstractExecutor} and provides specialized
 * handling for cases where hash members are identified using the {@code @Member} annotation.
 * It processes the annotated parameter as a key-value pair to be added to the Redis hash.
 * 
 * <p>The executor supports the following annotations:
 * <ul>
 *   <li>{@link AddMembers} - Marks a method as a hash member addition operation</li>
 *   <li>{@link Member} - Identifies the parameter containing the member to be added</li>
 * </ul>
 *
 * @author Liang.Zhong
 * @since 1.0.0
 * @see OrangeRedisAbstractExecutor
 * @see AddMembers
 * @see Member
 * @see <a href="https://orange.langwuyue.com/redis/advanced/hash">Orange Redis Hash Documentation</a>
 */
public class OrangeAddByMemberAnnotationExecutor extends OrangeRedisAbstractExecutor {
	
	private OrangeRedisHashOperations operations;

	/**
	 * Constructs a new OrangeAddByMemberAnnotationExecutor with the specified operations
	 * and ID generator.
	 * 
	 * <p>This constructor initializes the executor with the necessary components to perform
	 * member addition operations on Redis hashes. It delegates to the parent constructor
	 * for common initialization and stores the Redis hash operations for later use.
	 *
	 * @param operations the Redis hash operations handler used to interact with Redis
	 * @param idGenerator the generator used to create unique identifiers for operations
	 */
	public OrangeAddByMemberAnnotationExecutor(OrangeRedisHashOperations operations, OrangeRedisExecutorIdGenerator idGenerator) {
		super(idGenerator);
		this.operations = operations;
	}

	/**
	 * Executes the Redis hash member addition operation.
	 * 
	 * <p>This method performs the actual addition of a member to a Redis hash using the
	 * provided context information. It extracts the key-value pair from the member map
	 * in the context and uses the Redis hash operations to add the member to the hash.
	 * 
	 * <p>The return value depends on the method's return type:
	 * <ul>
	 *   <li>For boolean return types, it returns TRUE</li>
	 *   <li>For integer return types, it returns 1</li>
	 *   <li>For other return types, it returns null</li>
	 * </ul>
	 *
	 * @param context the operation context containing the key and member information
	 * @return an object representing the result of the operation, based on the method's return type
	 * @throws Exception if an error occurs during the execution
	 */
	@Override
	public Object execute(OrangeRedisContext context) throws Exception {
		OrangeAddMemberContext ctx = (OrangeAddMemberContext) context;
		Map map = ctx.getMember();
		Entry entry = (Entry) map.entrySet().iterator().next();
		this.operations.putMember(context.getRedisKey().getValue(), entry.getKey(), entry.getValue(), ctx.getKeyType(), ctx.getValueType());
		Class<?> returnClass = context.getOperationMethod().getReturnType();
		if(returnClass == Boolean.class || returnClass == boolean.class) {
			return Boolean.TRUE;	
		}
		if(OrangeReflectionUtils.isInteger(returnClass)) {
			return 1;	
		}
		return null;
	}

	/**
	 * Returns the list of annotation classes supported by this executor.
	 * 
	 * <p>This executor supports two annotations:
	 * <ul>
	 *   <li>{@link AddMembers} - Marks a method as a hash member addition operation</li>
	 *   <li>{@link Member} - Identifies parameters that represent hash members/fields</li>
	 * </ul>
	 *
	 * @return a list containing the supported annotation classes
	 */
	@Override
	protected List<Class<? extends Annotation>> getSupportedAnnotationClasses() {
		return OrangeCollectionUtils.asList(AddMembers.class, Member.class);
	}

	/**
	 * Returns the context class used by this executor.
	 * 
	 * <p>This executor uses {@link OrangeAddMemberContext} to store and manage
	 * the state and parameters required for member addition operations.
	 * The context encapsulates information such as the Redis key, member map,
	 * and type information for key-value serialization.
	 *
	 * @return the {@link OrangeAddMemberContext} class
	 */
	@Override
	public Class<? extends OrangeRedisContext> getContextClass() {
		return OrangeAddMemberContext.class;
	}
}