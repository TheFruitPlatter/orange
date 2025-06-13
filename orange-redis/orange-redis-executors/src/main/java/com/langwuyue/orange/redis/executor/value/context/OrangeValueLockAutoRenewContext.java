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
package com.langwuyue.orange.redis.executor.value.context;

import java.lang.reflect.Method;

import com.langwuyue.orange.redis.RedisValueTypeEnum;
import com.langwuyue.orange.redis.annotation.AutoRenew;
import com.langwuyue.orange.redis.annotation.OrangeRedisOperationArg;
import com.langwuyue.orange.redis.context.OrangeRedisValueContext;
import com.langwuyue.orange.redis.context.builder.OrangeMethodAnnotationHandler;

/**
 * Context class for handling Redis value locks with automatic renewal capabilities.
 * 
 * This context extends the base Redis value context to support automatic lock renewal
 * functionality. It maintains information about the auto-renewal configuration for
 * Redis locks, allowing locks to be automatically extended before they expire.
 * This is particularly useful for long-running operations where the lock needs to
 * be maintained beyond its initial expiration time.
 *
 * The context works in conjunction with the AutoRenew annotation to configure:
 * - The renewal period
 * - The maximum number of renewal attempts
 * - The renewal strategy (e.g., fixed delay, exponential backoff)
 *
 * @author Liang.Zhong
 * @since 1.0.0
 */
public class OrangeValueLockAutoRenewContext extends OrangeRedisValueContext {
	
	/**
	 * The auto-renewal configuration for the Redis lock.
	 * This field is automatically populated using the AutoRenew annotation
	 * through the OrangeMethodAnnotationHandler.
	 */
	@OrangeRedisOperationArg(binding = AutoRenew.class, valueHandler = OrangeMethodAnnotationHandler.class)
	private AutoRenew autoRenew;

	/**
	 * Constructs a new context for Redis value locks with auto-renewal support.
	 *
	 * @param operationOwner the class that owns the Redis operation
	 * @param operationMethod the method that defines the Redis operation
	 * @param args the arguments passed to the operation method
	 * @param redisKey the Redis key for the lock
	 * @param valueType the type of Redis value being operated on
	 */
	public OrangeValueLockAutoRenewContext(
		Class<?> operationOwner, 
		Method operationMethod, 
		Object[] args, 
		Key redisKey,
		RedisValueTypeEnum valueType
	) {
		super(operationOwner, operationMethod, args, redisKey, valueType);
	}

	/**
	 * Returns the auto-renewal configuration for the Redis lock.
	 * 
	 * This configuration contains parameters that control how the lock should be
	 * automatically renewed, including:
	 * - The renewal period (how often to attempt renewal)
	 * - The maximum number of renewal attempts
	 * - Any additional renewal strategy parameters
	 *
	 * @return the AutoRenew annotation instance containing the renewal configuration
	 */
	public AutoRenew getAutoRenew() {
		return autoRenew;
	}
}