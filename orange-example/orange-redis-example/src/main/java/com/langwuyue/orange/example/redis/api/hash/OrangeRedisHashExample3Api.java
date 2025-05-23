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
package com.langwuyue.orange.example.redis.api.hash;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import com.langwuyue.orange.redis.RedisValueTypeEnum;
import com.langwuyue.orange.redis.annotation.AddMembers;
import com.langwuyue.orange.redis.annotation.CAS;
import com.langwuyue.orange.redis.annotation.ContinueOnFailure;
import com.langwuyue.orange.redis.annotation.Count;
import com.langwuyue.orange.redis.annotation.GetMembers;
import com.langwuyue.orange.redis.annotation.GetSize;
import com.langwuyue.orange.redis.annotation.IfAbsent;
import com.langwuyue.orange.redis.annotation.KeyVariable;
import com.langwuyue.orange.redis.annotation.Multiple;
import com.langwuyue.orange.redis.annotation.OrangeRedisKey;
import com.langwuyue.orange.redis.annotation.Random;
import com.langwuyue.orange.redis.annotation.RedisOldValue;
import com.langwuyue.orange.redis.annotation.RedisValue;
import com.langwuyue.orange.redis.annotation.RemoveMembers;
import com.langwuyue.orange.redis.annotation.Timeout;
import com.langwuyue.orange.redis.annotation.TimeoutUnit;
import com.langwuyue.orange.redis.annotation.global.Delete;
import com.langwuyue.orange.redis.annotation.global.GetExpiration;
import com.langwuyue.orange.redis.annotation.global.SetExpiration;
import com.langwuyue.orange.redis.annotation.hash.GetHashKeys;
import com.langwuyue.orange.redis.annotation.hash.GetHashValueLength;
import com.langwuyue.orange.redis.annotation.hash.GetHashValues;
import com.langwuyue.orange.redis.annotation.hash.HasKeys;
import com.langwuyue.orange.redis.annotation.hash.HashKey;
import com.langwuyue.orange.redis.annotation.hash.OrangeRedisHashClient;

/**
 * @author Liang.Zhong
 * @since 1.0.0
 */
@OrangeRedisHashClient(
		hashKeyType = RedisValueTypeEnum.STRING,
		hashValueType = RedisValueTypeEnum.STRING
	)
@OrangeRedisKey(expirationTime = @Timeout(value = 1, unit = TimeUnit.HOURS), key = "orange:hash:example3:${var}")
public interface OrangeRedisHashExample3Api  {

	@SetExpiration
	boolean setExpiration(@KeyVariable(name = "var") String keyVar);
	
	@Delete
	boolean delete(@KeyVariable(name = "var") String keyVar);
	
	@GetExpiration
	Long getExpiration(@KeyVariable(name = "var") String keyVar);
	
	@GetExpiration
	Long getExpiration(@KeyVariable(name = "var") String keyVar,@TimeoutUnit TimeUnit unit);
	
	@AddMembers
	void add(@KeyVariable(name = "var") String keyVar,@HashKey String key, @RedisValue String value);
	
	@AddMembers
	@ContinueOnFailure(true)
	void add(@KeyVariable(name = "var") String keyVar,@Multiple Map<String, String> members);
	
	@AddMembers
	@IfAbsent(deleteInTheEnd = false)
	void addIfAbsent(@KeyVariable(name = "var") String keyVar,@HashKey String key, @RedisValue String value);
	
	@AddMembers
	@IfAbsent(deleteInTheEnd = false)
	@ContinueOnFailure(true)
	void addIfAbsent(@KeyVariable(name = "var") String keyVar,@Multiple Map<String, String> members);
	
	@CAS
	Boolean compareAndSwap(@KeyVariable(name = "var") String keyVar,@HashKey String hashKey, @RedisOldValue String oldValue, @RedisValue String newValue);
	
	@GetSize
	Long getSize(@KeyVariable(name = "var") String keyVar);
	
	@GetHashValueLength
	Long getHashValueBytesLength(@KeyVariable(name = "var") String keyVar,@HashKey String hashKey);
	
	@GetHashKeys
	Set<String> getAllKeys(@KeyVariable(name = "var") String keyVar);
	
	@GetHashKeys
	@Random
	String randomKey(@KeyVariable(name = "var") String keyVar);
	
	@GetHashKeys
	@Random
	Set<String> randomKeys(@KeyVariable(name = "var") String keyVar,@Count Long count);
	
	@HasKeys
	Boolean hasKey(@KeyVariable(name = "var") String keyVar,@HashKey String hashKey);
	
	@HasKeys
	@ContinueOnFailure(true)
	Map<String, Boolean> hasKeys(@KeyVariable(name = "var") String keyVar,@Multiple Collection<String> hashKeys);
	
	@GetHashValues
	List<String> getValues(@KeyVariable(name = "var") String keyVar);
	
	@GetHashValues
	String get(@KeyVariable(name = "var") String keyVar,@HashKey String hashKey);
	
	@GetHashValues
	Map<String,String> get(@KeyVariable(name = "var") String keyVar,@Multiple Collection<String> hashKeys);
	
	@GetMembers
	Map<String, String> getAllMembers(@KeyVariable(name = "var") String keyVar);
	
	@RemoveMembers
	Boolean remove(@KeyVariable(name = "var") String keyVar,@HashKey String hashKey);
	
	@RemoveMembers
	@ContinueOnFailure(true)
	Map<String,Boolean> remove(@KeyVariable(name = "var") String keyVar,@Multiple Collection<String> hashKeys);
	
}