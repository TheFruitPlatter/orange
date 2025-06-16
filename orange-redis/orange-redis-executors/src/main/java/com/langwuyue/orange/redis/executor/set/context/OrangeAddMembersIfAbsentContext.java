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
package com.langwuyue.orange.redis.executor.set.context;

import java.lang.reflect.Method;

import com.langwuyue.orange.redis.RedisValueTypeEnum;
import com.langwuyue.orange.redis.annotation.IfAbsent;
import com.langwuyue.orange.redis.annotation.OrangeRedisOperationArg;
import com.langwuyue.orange.redis.context.OrangeRedisMultipleValueContext;
import com.langwuyue.orange.redis.context.builder.OrangeMethodAnnotationHandler;

/**
 * Context class for handling Redis Set add-if-absent operations.
 * 
 * <p>This class extends {@link OrangeRedisMultipleValueContext} to provide specific
 * functionality for conditional member addition to Redis Sets. It manages the context
 * for operations that add members to a set only if they are not already present,
 * with optional cleanup functionality.
 * 
 * <p>The class processes {@link IfAbsent} annotations to determine operation behavior,
 * particularly whether to perform cleanup after the operation (deleteInTheEnd flag).
 *
 * @author Liang.Zhong
 * @since 1.0.0
 */
public class OrangeAddMembersIfAbsentContext extends OrangeRedisMultipleValueContext {

	/**
	 * The {@link IfAbsent} annotation instance bound to this context.
	 * 
	 * <p>This field holds the reference to the {@link IfAbsent} annotation that configures
	 * the behavior of the add-if-absent operation. It is automatically populated through
	 * the {@link OrangeRedisOperationArg} binding mechanism, which uses the
	 * {@link OrangeMethodAnnotationHandler} to extract the annotation from the method.
	 */
	@OrangeRedisOperationArg(binding = IfAbsent.class,valueHandler = OrangeMethodAnnotationHandler.class)
	private IfAbsent ifAbsent;
	
	/**
	 * Constructs a new OrangeAddMembersIfAbsentContext with the specified parameters.
	 * 
	 * <p>This constructor initializes the context with essential information needed for
	 * Redis Set add-if-absent operations. It passes all parameters to the parent class
	 * {@link OrangeRedisMultipleValueContext} constructor to establish the base context.
	 *
	 * @param operationOwner The class that owns the Redis operation method
	 * @param operationMethod The method that represents the Redis operation
	 * @param args The arguments passed to the operation method
	 * @param redisKey The Redis key to operate on
	 * @param valueType The type of values stored in the Redis set
	 */
	public OrangeAddMembersIfAbsentContext(
		Class<?> operationOwner, 
		Method operationMethod, 
		Object[] args,
		Key redisKey,
		RedisValueTypeEnum valueType
	) {
		super(operationOwner, operationMethod, args, redisKey, valueType);
	}
	
	/**
	 * Determines whether the set should be deleted after the operation completes.
	 * 
	 * <p>This method checks the {@link IfAbsent} annotation's deleteInTheEnd flag to determine
	 * if cleanup should be performed after adding members to the set. If the flag is set to true,
	 * the Redis set will be deleted after the add-if-absent operation completes.
	 *
	 * @return true if the set should be deleted after the operation, false otherwise
	 */
	public boolean isDeleteInTheEnd() {
		return ifAbsent != null && ifAbsent.deleteInTheEnd();
	}

}