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
package com.langwuyue.orange.redis.configuration;

import java.lang.reflect.Method;

import com.langwuyue.orange.redis.OrangeRedisCircuitBreaker;
import com.langwuyue.orange.redis.RedisValueTypeEnum;
import com.langwuyue.orange.redis.annotation.OrangeRedisKey;
import com.langwuyue.orange.redis.context.builder.OrangeOperationArgHandlerMapping;
import com.langwuyue.orange.redis.context.builder.OrangeRedisContextBuilder;
import com.langwuyue.orange.redis.context.builder.OrangeRedisHashContextBuilder;
import com.langwuyue.orange.redis.executor.OrangeRedisExecutor;
import com.langwuyue.orange.redis.mapping.OrangeRedisExecutorsMapping;

/**
 * @author Liang.Zhong
 * @since 1.0.0
 */
public class OrangeRedisHashClientInvocationHandler extends OrangeRedisClientInvocationHandler {
	
	private RedisValueTypeEnum keyType;
	
	public OrangeRedisHashClientInvocationHandler(
		Class<?> operationOwner,
		OrangeRedisExecutorsMapping mapping,
		OrangeRedisKey redisKey,
		OrangeOperationArgHandlerMapping operationArgHandlerMapping,
		RedisValueTypeEnum valueType,
		RedisValueTypeEnum keyType,
		OrangeRedisCircuitBreaker circuitBreaker,
		OrangeRedisProperties properties
	) {
		super(operationOwner, mapping, redisKey, operationArgHandlerMapping, valueType,circuitBreaker,properties);
		this.keyType = keyType;
	}

	@Override
	protected OrangeRedisContextBuilder newBuilder() {
		return new OrangeRedisHashContextBuilder();
	}

	@Override
	protected OrangeRedisContextBuilder createContextBuilder(
		OrangeRedisExecutor executor, 
		Method method, 
		Object[] args
	) throws Exception {
		OrangeRedisContextBuilder builder = super.createContextBuilder(executor, method, args);
		((OrangeRedisHashContextBuilder)builder).keyType(this.keyType);
		return builder;
	}
	
}
