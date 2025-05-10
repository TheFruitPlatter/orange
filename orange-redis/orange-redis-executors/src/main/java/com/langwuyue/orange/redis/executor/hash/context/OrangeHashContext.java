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

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

import com.langwuyue.orange.redis.RedisValueTypeEnum;
import com.langwuyue.orange.redis.context.OrangeRedisContext;

/**
 * @author Liang.Zhong
 * @since 1.0.0
 */
public class OrangeHashContext extends OrangeRedisContext {
	
	private RedisValueTypeEnum keyType;
	
	public OrangeHashContext(
		Class<?> operationOwner, 
		Method operationMethod, 
		Object[] args,
		Key redisKey,
		RedisValueTypeEnum valueType,
		RedisValueTypeEnum keyType
	) {
		super(operationOwner,operationMethod,args,redisKey,valueType);
		this.keyType = keyType;
	}
	
	public static OrangeHashContext newInstance(
		Class<? extends OrangeRedisContext> contextClass,
		Class<?> operationOwner, 
		Method operationMethod, 
		Object[] args,
		Key redisKey,
		RedisValueTypeEnum valueType,
		RedisValueTypeEnum keyType
	) throws Exception{
		Constructor<? extends OrangeRedisContext> constructor = contextClass.getConstructor(
				Class.class,
				Method.class,
				Object[].class,
				Key.class,
				RedisValueTypeEnum.class,
				RedisValueTypeEnum.class
		);
		return (OrangeHashContext) constructor.newInstance(operationOwner,operationMethod,args,redisKey,valueType,keyType);
	}

	public RedisValueTypeEnum getKeyType() {
		return keyType;
	}
}
