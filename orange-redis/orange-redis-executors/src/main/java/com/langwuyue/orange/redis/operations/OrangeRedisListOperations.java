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
package com.langwuyue.orange.redis.operations;

import java.lang.reflect.Type;
import java.util.List;
import java.util.concurrent.TimeUnit;

import com.langwuyue.orange.redis.RedisValueTypeEnum;
import com.langwuyue.orange.redis.annotation.cross.ListMoveDirection;

/**
 * @author Liang.Zhong
 * @since 1.0.0
 */
public interface OrangeRedisListOperations extends OrangeRedisOperations {

	Object index(String key, long index, RedisValueTypeEnum valueType, Type returnType) throws Exception;

	Long indexOf(String key, Object value, RedisValueTypeEnum valueType) throws Exception;

	Long lastIndexOf(String key, Object value, RedisValueTypeEnum valueType) throws Exception;

	Long size(String key);
	
	Long remove(String key, long count, Object value, RedisValueTypeEnum valueType) throws Exception;

	Long leftPush(String key, RedisValueTypeEnum valueType, Object... values) throws Exception;

	Long leftPush(String key, Object pivot, Object value, RedisValueTypeEnum valueType) throws Exception;
	
	void set(String key, long index, Object value, RedisValueTypeEnum valueType) throws Exception;
	
	List<Object> leftPop(String key, long count, RedisValueTypeEnum valueType, Type returnType) throws Exception;

	Object leftPop(String key, long timeout, TimeUnit timeUnit,RedisValueTypeEnum valueType, Type returnType) throws Exception;

	Long rightPush(String key, RedisValueTypeEnum valueType, Object... values) throws Exception;

	Long rightPush(String key, Object pivot, Object value, RedisValueTypeEnum valueType) throws Exception;
	
	List<Object> rightPop(String key, long count, RedisValueTypeEnum valueType, Type returnType) throws Exception;

	Object rightPop(String key, long timeout, TimeUnit timeUnit,RedisValueTypeEnum valueType, Type returnType) throws Exception;

	List<Object> range(String key, Long startIndex, Long endIndex,RedisValueTypeEnum valueType, Type returnType) throws Exception;

	void trim(String value, Long startIndex, Long endIndex);

	Object move(String referenceKey, String destKey, ListMoveDirection direction, RedisValueTypeEnum valueType, Type returnType) throws Exception;

	Object move(
		String referenceKey, 
		String destKey, 
		ListMoveDirection direction, 
		RedisValueTypeEnum valueType,
		Type returnType, 
		long timeoutValue, 
		TimeUnit timeoutUnit
	) throws Exception;
}
