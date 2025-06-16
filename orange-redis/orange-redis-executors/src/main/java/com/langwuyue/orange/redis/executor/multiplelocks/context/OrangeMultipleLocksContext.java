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
package com.langwuyue.orange.redis.executor.multiplelocks.context;

import java.lang.reflect.Method;

import com.langwuyue.orange.redis.RedisValueTypeEnum;
import com.langwuyue.orange.redis.annotation.AutoRenew;
import com.langwuyue.orange.redis.annotation.OrangeRedisOperationArg;
import com.langwuyue.orange.redis.context.OrangeRedisMultipleValueContext;
import com.langwuyue.orange.redis.context.builder.OrangeMethodAnnotationHandler;

/**
 * Context class for multiple lock operations, used for handling distributed lock acquisition and management.
 * This class extends {@link OrangeRedisMultipleValueContext} and adds support for automatic lock renewal.
 * 
 * @author Liang.Zhong
 * @since 1.0.0
 * @see AutoRenew
 * @see OrangeRedisMultipleValueContext
 */
public class OrangeMultipleLocksContext extends OrangeRedisMultipleValueContext {
	
	/**
	 * Instance of auto-renewal annotation used to configure the lock renewal strategy.
	 * Bound through {@link OrangeRedisOperationArg} annotation and processed by {@link OrangeMethodAnnotationHandler}
	 * to extract {@link AutoRenew} annotation from the method.
	 */
	@OrangeRedisOperationArg(binding = AutoRenew.class, valueHandler = OrangeMethodAnnotationHandler.class)
	private AutoRenew autoRenew;

	/**
	 * Constructs a new multiple locks context instance.
	 *
	 * @param operationOwner    The class that owns the operation
	 * @param operationMethod   The method representing the operation
	 * @param args             The arguments array passed to the method
	 * @param redisKey         The Redis key for the operation
	 * @param valueType        The Redis value type enum
	 */
	public OrangeMultipleLocksContext(
		Class<?> operationOwner, 
		Method operationMethod, 
		Object[] args, 
		Key redisKey,
		RedisValueTypeEnum valueType
	) {
		super(operationOwner, operationMethod, args, redisKey, valueType);
	}

	/**
	 * Gets the auto-renewal annotation instance.
	 *
	 * @return The auto-renewal annotation instance, may be null if not configured
	 */
	public AutoRenew getAutoRenew() {
		return autoRenew;
	}
}