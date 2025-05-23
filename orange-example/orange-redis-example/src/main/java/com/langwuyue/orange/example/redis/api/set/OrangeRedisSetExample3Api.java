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
package com.langwuyue.orange.example.redis.api.set;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import com.langwuyue.orange.redis.RedisValueTypeEnum;
import com.langwuyue.orange.redis.annotation.AddMembers;
import com.langwuyue.orange.redis.annotation.CAS;
import com.langwuyue.orange.redis.annotation.ContinueOnFailure;
import com.langwuyue.orange.redis.annotation.Count;
import com.langwuyue.orange.redis.annotation.Distinct;
import com.langwuyue.orange.redis.annotation.GetMembers;
import com.langwuyue.orange.redis.annotation.GetSize;
import com.langwuyue.orange.redis.annotation.IfAbsent;
import com.langwuyue.orange.redis.annotation.KeyVariable;
import com.langwuyue.orange.redis.annotation.Multiple;
import com.langwuyue.orange.redis.annotation.OrangeRedisKey;
import com.langwuyue.orange.redis.annotation.PopMembers;
import com.langwuyue.orange.redis.annotation.Random;
import com.langwuyue.orange.redis.annotation.RedisOldValue;
import com.langwuyue.orange.redis.annotation.RedisValue;
import com.langwuyue.orange.redis.annotation.RemoveMembers;
import com.langwuyue.orange.redis.annotation.ScanPattern;
import com.langwuyue.orange.redis.annotation.Timeout;
import com.langwuyue.orange.redis.annotation.TimeoutUnit;
import com.langwuyue.orange.redis.annotation.global.Delete;
import com.langwuyue.orange.redis.annotation.global.GetExpiration;
import com.langwuyue.orange.redis.annotation.global.SetExpiration;
import com.langwuyue.orange.redis.annotation.set.IsMembers;
import com.langwuyue.orange.redis.annotation.set.OrangeRedisSetClient;
import com.langwuyue.orange.redis.annotation.zset.PageNo;

/**
 * @author Liang.Zhong
 * @since 1.0.0
 */
@OrangeRedisSetClient(valueType = RedisValueTypeEnum.STRING)
@OrangeRedisKey(expirationTime = @Timeout(value = 1, unit = TimeUnit.HOURS), key = "orange:set:example3:${var}")
public interface OrangeRedisSetExample3Api {
	
	@SetExpiration
	boolean setExpiration(@KeyVariable(name = "var") String var);
	
	@Delete
	boolean delete(@KeyVariable(name = "var") String var);
	
	@GetExpiration
	Long getExpiration(@KeyVariable(name = "var") String var);
	
	@GetExpiration
	Long getExpiration(@KeyVariable(name = "var") String var,@TimeoutUnit TimeUnit unit);

	
	@AddMembers
	Boolean add(@KeyVariable(name = "var") String var,@RedisValue String value);
	
	@AddMembers
	@ContinueOnFailure(true)
	Map<String, Boolean> add(@KeyVariable(name = "var") String var,@Multiple Set<String> members);
	
	@AddMembers
	@IfAbsent(deleteInTheEnd=false)
	void addIfAbsent(@KeyVariable(name = "var") String var,@RedisValue String value);
	
	@AddMembers
	@IfAbsent(deleteInTheEnd=false)
	@ContinueOnFailure(true)
	void addIfAbsent(@KeyVariable(name = "var") String var,@Multiple Set<String> members);
	
	@CAS
	Boolean compareAndSwap(@KeyVariable(name = "var") String var,@RedisOldValue String oldValue, @RedisValue String newValue);
	
	@GetMembers
	@Random
	String randomGetOne(@KeyVariable(name = "var") String var);
	
	@GetMembers
	@Random
	List<String> randomGetMembers(@KeyVariable(name = "var") String var,@Count Long count);
	
	@GetMembers
	@Distinct
	@Random
	Set<String> distinctRandomGetMembers(@KeyVariable(name = "var") String var,@Count Long count);
	
	@GetMembers
	Set<String> getMembers(@KeyVariable(name = "var") String var);
	
	@IsMembers
	Boolean isMember(@KeyVariable(name = "var") String var,@RedisValue String member);
	
	@IsMembers
	Map<String, Boolean> isMembers(@KeyVariable(name = "var") String var,@Multiple Set<String> members);
	
	@PopMembers
	String pop(@KeyVariable(name = "var") String var);
	
	@PopMembers
	Set<String> pop(@KeyVariable(name = "var") String var,@Count Long count);
	
	@GetSize
	Long getSize(@KeyVariable(name = "var") String var);
	
	@RemoveMembers
	Boolean remove(@KeyVariable(name = "var") String var,@RedisValue String value);
	
	@RemoveMembers
	@ContinueOnFailure(true)
	Map<String,Boolean> remove(@KeyVariable(name = "var") String var,@Multiple Set<String> values);
	
	@GetMembers
	String scan(@KeyVariable(name = "var") String var, @ScanPattern String pattern,@Count Long count,@PageNo Long preCursor);
	
}