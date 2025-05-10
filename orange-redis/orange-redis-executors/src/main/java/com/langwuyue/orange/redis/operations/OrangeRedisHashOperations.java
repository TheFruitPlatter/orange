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
import java.util.Map;
import java.util.Set;

import com.langwuyue.orange.redis.RedisValueTypeEnum;

/**
 * @author Liang.Zhong
 * @since 1.0.0
 */
public interface OrangeRedisHashOperations extends OrangeRedisOperations {

	void putMember(
		String key, 
		Object hashKey, 
		Object hashValue, 
		RedisValueTypeEnum hashKeyType,
		RedisValueTypeEnum hashValueType
	) throws Exception;

	void putMembers(
		String key, 
		Map members, 
		RedisValueTypeEnum hashKeyType,
		RedisValueTypeEnum hashValueType
	) throws Exception;

	Double increment(
		String key, 
		Object hashKey, 
		double delta, 
		RedisValueTypeEnum hashKeyType
	) throws Exception;

	Long increment(
		String key, 
		Object hashKey, 
		long delta, 
		RedisValueTypeEnum hashKeyType
	) throws Exception;

	Set<Object> keys(
		String key, 
		RedisValueTypeEnum hashKeyType, 
		Type returnType
	) throws Exception;

	List<Object> multiGet(
		String key, 
		List<Object> hashKeys, 
		RedisValueTypeEnum hashKeyType,
		RedisValueTypeEnum hashValueType, 
		Type returnType
	) throws Exception;

	Object get(
		String key, 
		Object hashKey, 
		RedisValueTypeEnum hashKeyType,
		RedisValueTypeEnum hashValueType,
		Type returnType
	) throws Exception;

	Map<Object, Object> entries(
		String key, 
		RedisValueTypeEnum hashKeyType,
		RedisValueTypeEnum hashValueType, 
		Type keyType, 
		Type valueType
	) throws Exception;

	Boolean hasKey(
		String key, 
		Object hashKey, 
		RedisValueTypeEnum hashKeyType
	) throws Exception;

	Map<Object, Object> randomEntries(
		String key, 
		long count, 
		RedisValueTypeEnum hashKeyType,
		RedisValueTypeEnum hashValueType, 
		Type keyType, 
		Type valueType
	) throws Exception;

	List<Object> randomKeys(
		String key, 
		long count, 
		RedisValueTypeEnum hashValueType, 
		Type valueType
	)throws Exception;

	Long size(String key);

	Long removeMembers(
		String key, 
		RedisValueTypeEnum hashKeyType, 
		Object... hashKeys
	) throws Exception;

	Boolean putIfAbsent(
		String key, 
		Object hashKey, 
		Object hashValue, 
		RedisValueTypeEnum hashKeyType,
		RedisValueTypeEnum hashValueType
	) throws Exception;

	Long getLengthByHashKey(
		String key, 
		Object hashKey, 
		RedisValueTypeEnum hashKeyType
	) throws Exception;

	List<Object> getAllValues(String key, RedisValueTypeEnum valueType, Type returnType) throws Exception;

}
