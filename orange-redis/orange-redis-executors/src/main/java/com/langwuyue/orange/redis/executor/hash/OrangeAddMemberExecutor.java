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

import com.langwuyue.orange.redis.annotation.AddMembers;
import com.langwuyue.orange.redis.annotation.RedisValue;
import com.langwuyue.orange.redis.annotation.hash.HashKey;
import com.langwuyue.orange.redis.context.OrangeRedisContext;
import com.langwuyue.orange.redis.executor.OrangeRedisAbstractExecutor;
import com.langwuyue.orange.redis.executor.hash.context.OrangeHashKeyValueContext;
import com.langwuyue.orange.redis.mapping.OrangeRedisExecutorIdGenerator;
import com.langwuyue.orange.redis.operations.OrangeRedisHashOperations;
import com.langwuyue.orange.redis.utils.OrangeCollectionUtils;
import com.langwuyue.orange.redis.utils.OrangeReflectionUtils;

/**
 * Executor for adding a single member to a Redis Hash.
 *
 * <p>This executor provides functionality to add a single key-value pair to a Redis Hash.
 * The operation supports various return types including boolean, integer and void.
 *
 * <p>The executor supports the following annotations:
 * <ul>
 *   <li>{@link AddMembers} - Marks this as a hash add operation</li>
 *   <li>{@link HashKey} - Specifies the hash key</li>
 *   <li>{@link RedisValue} - Marks the value parameter</li>
 * </ul>
 *
 * <p>Return value handling:
 * <ul>
 *   <li>Boolean/boolean return type: returns true</li>
 *   <li>Numeric return type: returns 1</li>
 *   <li>Void return type: returns null</li>
 * </ul>
 *
 * @author Liang.Zhong
 * @since 1.0.0
 */
public class OrangeAddMemberExecutor extends OrangeRedisAbstractExecutor {
	
	private OrangeRedisHashOperations operations;

	/**
	 * Constructs a new OrangeAddMemberExecutor with the specified operations
	 * and ID generator.
	 * 
	 * <p>This constructor initializes the executor with the necessary components to perform
	 * member addition operations on Redis hashes. It delegates to the parent constructor
	 * for common initialization and stores the Redis hash operations for later use.
	 *
	 * @param operations the Redis hash operations handler used to interact with Redis
	 * @param idGenerator the generator used to create unique identifiers for operations
	 */
	public OrangeAddMemberExecutor(OrangeRedisHashOperations operations, OrangeRedisExecutorIdGenerator idGenerator) {
		super(idGenerator);
		this.operations = operations;
	}

	/**
	 * Executes the Redis hash member addition operation.
	 * 
	 * <p>This method performs the actual addition of a member to a Redis hash using the
	 * provided context information. It extracts the hash key and value from the context
	 * and uses the Redis hash operations to add the member to the hash.
	 * 
	 * <p>The return value depends on the method's return type:
	 * <ul>
	 *   <li>For boolean return types, it returns TRUE</li>
	 *   <li>For integer return types, it returns 1</li>
	 *   <li>For other return types, it returns null</li>
	 * </ul>
	 *
	 * @param context the operation context containing the Redis key, hash key, and value information
	 * @return an object representing the result of the operation, based on the method's return type
	 * @throws Exception if an error occurs during the execution
	 * @throws ClassCastException if the provided context is not an instance of OrangeHashKeyValueContext
	 */
	@Override
	public Object execute(OrangeRedisContext context) throws Exception {
		OrangeHashKeyValueContext ctx = (OrangeHashKeyValueContext) context;
		this.operations.putMember(context.getRedisKey().getValue(), ctx.getHashKey(), ctx.getValue(), ctx.getKeyType(), ctx.getValueType());
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
	 * <p>This executor supports three annotations:
	 * <ul>
	 *   <li>{@link AddMembers} - Marks a method as a hash member addition operation</li>
	 *   <li>{@link HashKey} - Identifies parameters that represent hash keys</li>
	 *   <li>{@link RedisValue} - Identifies parameters that represent values to be stored</li>
	 * </ul>
	 *
	 * @return a list containing the supported annotation classes
	 */
	@Override
	protected List<Class<? extends Annotation>> getSupportedAnnotationClasses() {
		return OrangeCollectionUtils.asList(AddMembers.class, HashKey.class, RedisValue.class);
	}

	/**
	 * Returns the context class used by this executor.
	 * 
	 * <p>This executor uses {@link OrangeHashKeyValueContext} to store and manage
	 * the state and parameters required for hash member addition operations.
	 * The context encapsulates information such as the Redis key, hash key,
	 * value, and their respective type information.
	 *
	 * @return the {@link OrangeHashKeyValueContext} class
	 */
	@Override
	public Class<? extends OrangeRedisContext> getContextClass() {
		return OrangeHashKeyValueContext.class;
	}
	
	

}