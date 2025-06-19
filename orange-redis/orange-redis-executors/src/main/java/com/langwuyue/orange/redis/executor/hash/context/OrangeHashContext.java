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
 * Base context class for Redis hash operations, providing common context information needed for hash operations.
 * 
 * <p>Contains hash key type information used by Redis hash operation executors to handle different types of data.
 *
 * @author Liang.Zhong
 * @since 1.0.0
 */
public class OrangeHashContext extends OrangeRedisContext {
	
	/**
	 * Data type of the Redis hash key
	 */
	private RedisValueTypeEnum keyType;
	
	/**
	 * Constructs a new Redis hash operation context instance
	 *
	 * @param operationOwner the class owning the operation
	 * @param operationMethod the operation method
	 * @param args method parameters
	 * @param redisKey Redis key
	 * @param valueType value type
	 * @param keyType key type
	 */
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
	
	/**
	 * Creates a new Redis hash operation context instance
	 *
	 * @param contextClass the context class type
	 * @param operationOwner the class owning the operation
	 * @param operationMethod the operation method
	 * @param args method parameters
	 * @param redisKey Redis key
	 * @param valueType value type
	 * @param keyType key type
	 * @return newly created context instance
	 * @throws Exception if reflection creation fails
	 */
	public static OrangeHashContext newInstance(
		Class<? extends OrangeRedisContext> contextClass,
		Class<?> operationOwner, 
		Method operationMethod, 
		Object[] args,
		Key redisKey,
		RedisValueTypeEnum valueType,
		RedisValueTypeEnum keyType
	) throws Exception {
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

	/**
	 * Gets the data type of the Redis hash key
	 * 
	 * @return the key's data type enum
	 */
	public RedisValueTypeEnum getKeyType() {
		return keyType;
	}
}