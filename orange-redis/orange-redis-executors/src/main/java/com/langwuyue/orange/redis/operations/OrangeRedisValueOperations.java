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
import java.util.concurrent.TimeUnit;

import com.langwuyue.orange.redis.RedisValueTypeEnum;

/**
 * @author Liang.Zhong
 * @since 1.0.0
 */
public interface OrangeRedisValueOperations extends OrangeRedisOperations {

	Long increment(String key, long value);

	Double increment(String key, double value);

	Long increment(String key);

	void set(String key, Object value, long expirationTime, TimeUnit expirationTimeUnit, RedisValueTypeEnum valueType) throws Exception;
	
	void set(String key, Object value, RedisValueTypeEnum valueType) throws Exception;

	Boolean setIfAbsent(String key, Object value, long expirationTime, TimeUnit expirationTimeUnit,RedisValueTypeEnum valueType) throws Exception;
	
	Boolean setIfAbsent(String key, Object value, RedisValueTypeEnum valueType) throws Exception;

	Object get(String key, RedisValueTypeEnum valueType, Type genericReturnType) throws Exception;

}
