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
import java.util.LinkedHashMap;
import java.util.Map;

import com.langwuyue.orange.redis.RedisValueTypeEnum;
import com.langwuyue.orange.redis.annotation.IfAbsent;
import com.langwuyue.orange.redis.annotation.OrangeRedisOperationArg;
import com.langwuyue.orange.redis.context.builder.OrangeMethodAnnotationHandler;

/**
 * Redis hash key-value operation context (with IfAbsent condition), used to handle hash operations with @IfAbsent annotation.
 * 
 * <p>Extends {@link OrangeHashKeyValueContext}, implements {@link OrangeAddIfAbsentContext} interface,
 * adds support for IfAbsent condition, mainly used for conditional operation commands such as HSETNX.
 *
 * @author Liang.Zhong
 * @since 1.0.0
 */
public class OrangeHashKeyValueIfAbsentContext extends OrangeHashKeyValueContext implements OrangeAddIfAbsentContext {
	
	/**
	 * Condition parameter bound with @IfAbsent annotation, used to control operations that only execute when the key does not exist
	 */
	@OrangeRedisOperationArg(binding = IfAbsent.class,valueHandler = OrangeMethodAnnotationHandler.class)
	private IfAbsent ifAbsent;

	/**
	 * Constructs a new Redis hash key-value operation context instance with IfAbsent condition
	 *
	 * @param operationOwner the class that owns the operation
	 * @param operationMethod the operation method
	 * @param args the method arguments
	 * @param redisKey the Redis key
	 * @param valueType the value type
	 * @param keyType the key type
	 */
	public OrangeHashKeyValueIfAbsentContext(
		Class<?> operationOwner, 
		Method operationMethod, 
		Object[] args, 
		Key redisKey,
		RedisValueTypeEnum valueType,
		RedisValueTypeEnum keyType
	) {
		super(operationOwner, operationMethod, args, redisKey,valueType,keyType);
	}

	/**
	 * Determines whether the key needs to be deleted after the operation completes
	 * 
	 * @return true if deleteInTheEnd is configured, false otherwise
	 */
	@Override
	public boolean isDeleteInTheEnd() {
		return ifAbsent.deleteInTheEnd();
	}

	/**
	 * Gets the member mapping of hash key-value pairs
	 * 
	 * @return Map object containing hash keys and values
	 */
	@Override
	public Map getMember() {
		Map map = new LinkedHashMap<>();
		map.put(getHashKey(), getValue());
		return map;
	}
}