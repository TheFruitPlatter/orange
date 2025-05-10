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
import com.langwuyue.orange.redis.annotation.OrangeRedisOperationArg;
import com.langwuyue.orange.redis.annotation.RedisOldValue;

/**
 * @author Liang.Zhong
 * @since 1.0.0
 */
public class OrangeCompareAndSwapContext extends OrangeHashKeyValueContext {
	
	@OrangeRedisOperationArg(binding = RedisOldValue.class)
	private Object oldValue;
	
	public OrangeCompareAndSwapContext(
		Class<?> operationOwner, 
		Method operationMethod, 
		Object[] args,
		Key redisKey,
		RedisValueTypeEnum valueType,
		RedisValueTypeEnum keyType
	) {
		super(operationOwner,operationMethod,args,redisKey,valueType,keyType);
	}
	
	public OrangeCompareAndSwapContext(
		Class<?> operationOwner, 
		Method operationMethod, 
		Object[] args,
		Key redisKey,
		RedisValueTypeEnum valueType,
		RedisValueTypeEnum keyType,
		Object hashKey,
		Object value,
		Object oldValue
	) {
		super(operationOwner,operationMethod,args,redisKey,valueType,keyType,hashKey,value);
		this.oldValue = oldValue;
	}

	public Object getOldValue() {
		return oldValue;
	}
}
