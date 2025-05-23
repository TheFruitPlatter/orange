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
import java.util.concurrent.TimeUnit;

import com.langwuyue.orange.redis.OrangeRedisException;
import com.langwuyue.orange.redis.RedisValueTypeEnum;
import com.langwuyue.orange.redis.annotation.OrangeRedisOperationArg;
import com.langwuyue.orange.redis.annotation.TimeoutUnit;

/**
 * @author Liang.Zhong
 * @since 1.0.0
 */
public class OrangeMultipleLocksGetExpirationsWithUnitArgsContext extends OrangeMultipleLocksGetExpirationsContext {
	
	@OrangeRedisOperationArg(binding = TimeoutUnit.class)
	private Object unit;

	public OrangeMultipleLocksGetExpirationsWithUnitArgsContext(
		Class<?> operationOwner, 
		Method operationMethod, 
		Object[] args, 
		Key redisKey,
		RedisValueTypeEnum valueType
	) {
		super(operationOwner, operationMethod, args, redisKey, valueType);
	}
	
	public TimeUnit getUnit() {
		if(unit == null) {
			throw new OrangeRedisException(String.format("The argument annotated with @%s cannot be null", TimeoutUnit.class));
		}
		if(!(unit instanceof TimeUnit)) {
			throw new OrangeRedisException(String.format("The argument annotated with @%s must be a TimeUnit", TimeoutUnit.class));
		}
		return (TimeUnit) unit;
	}
}
