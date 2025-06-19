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
package com.langwuyue.orange.redis.executor.hash.context;

import java.lang.reflect.Method;

import com.langwuyue.orange.redis.RedisValueTypeEnum;
import com.langwuyue.orange.redis.annotation.IfAbsent;
import com.langwuyue.orange.redis.annotation.OrangeRedisOperationArg;
import com.langwuyue.orange.redis.context.builder.OrangeMethodAnnotationHandler;

/**
 * Context class for conditionally adding a member to a Redis hash only if the key is absent.
 * 
 * <p>This class extends OrangeAddMemberContext and implements OrangeAddIfAbsentContext
 * to provide functionality for conditional hash member addition operations.
 * It processes the IfAbsent annotation to determine whether the key should be deleted
 * after the operation is completed.
 *
 * @author Liang.Zhong
 * @since 1.0.0
 */
public class OrangeAddMemberIfAbsentContext extends OrangeAddMemberContext implements OrangeAddIfAbsentContext {
	
	/**
	 * The IfAbsent annotation instance that controls the conditional behavior.
	 * This field is bound to the IfAbsent annotation and processed by OrangeMethodAnnotationHandler.
	 */
	@OrangeRedisOperationArg(binding = IfAbsent.class, valueHandler = OrangeMethodAnnotationHandler.class)
	private IfAbsent ifAbsent;

	/**
	 * Constructs a new OrangeAddMemberIfAbsentContext with the specified parameters.
	 *
	 * @param operationOwner    the class that owns the Redis operation
	 * @param operationMethod   the method representing the Redis operation
	 * @param args             the arguments passed to the operation method
	 * @param redisKey         the Redis key for the operation
	 * @param valueType        the type of value stored in Redis
	 * @param keyType         the type of the Redis key
	 */
	public OrangeAddMemberIfAbsentContext(
		Class<?> operationOwner, 
		Method operationMethod, 
		Object[] args,
		Key redisKey,
		RedisValueTypeEnum valueType,
		RedisValueTypeEnum keyType
	) {
		super(operationOwner, operationMethod, args, redisKey, valueType, keyType);
	}

	/**
	 * Determines whether the key should be deleted after the operation is completed.
	 * This value is obtained from the IfAbsent annotation's deleteInTheEnd setting.
	 *
	 * @return true if the key should be deleted after the operation, false otherwise
	 */
	@Override
	public boolean isDeleteInTheEnd() {
		return ifAbsent.deleteInTheEnd();
	}
}