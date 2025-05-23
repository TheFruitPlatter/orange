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
package com.langwuyue.orange.example.redis.api.list;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import com.langwuyue.orange.redis.RedisValueTypeEnum;
import com.langwuyue.orange.redis.annotation.AddMembers;
import com.langwuyue.orange.redis.annotation.CAS;
import com.langwuyue.orange.redis.annotation.ContinueOnFailure;
import com.langwuyue.orange.redis.annotation.Count;
import com.langwuyue.orange.redis.annotation.Distinct;
import com.langwuyue.orange.redis.annotation.EndIndex;
import com.langwuyue.orange.redis.annotation.GetIndexs;
import com.langwuyue.orange.redis.annotation.GetMembers;
import com.langwuyue.orange.redis.annotation.GetSize;
import com.langwuyue.orange.redis.annotation.KeyVariable;
import com.langwuyue.orange.redis.annotation.Multiple;
import com.langwuyue.orange.redis.annotation.OrangeRedisKey;
import com.langwuyue.orange.redis.annotation.PopMembers;
import com.langwuyue.orange.redis.annotation.Random;
import com.langwuyue.orange.redis.annotation.RedisOldValue;
import com.langwuyue.orange.redis.annotation.RedisValue;
import com.langwuyue.orange.redis.annotation.RemoveMembers;
import com.langwuyue.orange.redis.annotation.StartIndex;
import com.langwuyue.orange.redis.annotation.Timeout;
import com.langwuyue.orange.redis.annotation.TimeoutUnit;
import com.langwuyue.orange.redis.annotation.TimeoutValue;
import com.langwuyue.orange.redis.annotation.global.Delete;
import com.langwuyue.orange.redis.annotation.global.GetExpiration;
import com.langwuyue.orange.redis.annotation.global.SetExpiration;
import com.langwuyue.orange.redis.annotation.list.Index;
import com.langwuyue.orange.redis.annotation.list.Left;
import com.langwuyue.orange.redis.annotation.list.OrangeRedisListClient;
import com.langwuyue.orange.redis.annotation.list.Pivot;
import com.langwuyue.orange.redis.annotation.list.Right;
import com.langwuyue.orange.redis.annotation.zset.Reverse;

/**
 * @author Liang.Zhong
 * @since 1.0.0
 */
@OrangeRedisListClient(valueType = RedisValueTypeEnum.STRING)
@OrangeRedisKey(expirationTime = @Timeout(value = 1, unit = TimeUnit.HOURS), key = "orange:list:example3:${var}")
public interface OrangeRedisListExample3Api {
	
	@SetExpiration
	boolean setExpiration(@KeyVariable(name = "var") String keyVar);
	
	@Delete
	boolean delete(@KeyVariable(name = "var") String keyVar);
	
	@GetExpiration
	Long getExpiration(@KeyVariable(name = "var") String keyVar);
	
	@GetExpiration
	Long getExpiration(@KeyVariable(name = "var") String keyVar,@TimeoutUnit TimeUnit unit);

	@AddMembers
	void set(@KeyVariable(name = "var") String keyVar,@Index Long index, @RedisValue String member);
	
	@CAS
	Boolean compareAndSwap(@KeyVariable(name = "var") String keyVar,@Index Long index, @RedisOldValue String oldMember, @RedisValue String newMember);

	@AddMembers
	@Left
	Long leftPush(@KeyVariable(name = "var") String keyVar,@RedisValue String member);
	
	@AddMembers
	@Left
	Long leftPush(@KeyVariable(name = "var") String keyVar,@Pivot String pivot, @RedisValue String member);
	
	@AddMembers
	@Left
	@ContinueOnFailure(true)
	Map<String,Boolean> leftPush(@KeyVariable(name = "var") String keyVar,@Multiple Collection<String> members);
	
	@AddMembers
	@Right
	Long rightPush(@KeyVariable(name = "var") String keyVar,@RedisValue String member);
	
	@AddMembers
	@Right
	Long rightPush(@KeyVariable(name = "var") String keyVar,@Pivot String pivot, @RedisValue String member);
	
	@AddMembers
	@Right
	@ContinueOnFailure(true)
	Map<String,Boolean> rightPush(@KeyVariable(name = "var") String keyVar,@Multiple Collection<String> members);
	
	@GetSize
	Long getSize(@KeyVariable(name = "var") String keyVar);
	
	@GetIndexs
	Long getIndex(@KeyVariable(name = "var") String keyVar,@RedisValue String member);
	
	@GetIndexs
	@ContinueOnFailure(true)
	Map<String, Long> getIndex(@KeyVariable(name = "var") String keyVar,@Multiple Collection<String> members);
	
	@GetIndexs
	@Reverse
	Long getLastIndex(@KeyVariable(name = "var") String keyVar,@RedisValue String member);
	
	@GetIndexs
	@Reverse
	@ContinueOnFailure(true)
	Map<String, Long> getLastIndex(@KeyVariable(name = "var") String keyVar,@Multiple Collection<String> member);
	
	@GetMembers
	String get(@KeyVariable(name = "var") String keyVar,@Index Long index);
	
	@GetMembers
	List<String> getByIndexRange(@KeyVariable(name = "var") String keyVar,@StartIndex Long start, @EndIndex Long end);
	
	@PopMembers
	@Left
	String leftPop(@KeyVariable(name = "var") String keyVar);
	
	@PopMembers
	@Left
	List<String> leftPop(@KeyVariable(name = "var") String keyVar,@Count Long count);

	@PopMembers
	@Left
	List<String> leftPop(@KeyVariable(name = "var") String keyVar,@TimeoutValue Long value, @TimeoutUnit TimeUnit unit);
	
	@PopMembers
	@Right
	String rightPop(@KeyVariable(name = "var") String keyVar);
	
	@PopMembers
	@Right
	List<String> rightPop(@KeyVariable(name = "var") String keyVar,@Count Long count);

	@PopMembers
	@Right
	List<String> rightPop(@KeyVariable(name = "var") String keyVar,@TimeoutValue Long value, @TimeoutUnit TimeUnit unit);
	
	@RemoveMembers
	Long remove(@KeyVariable(name = "var") String keyVar,@RedisValue String member, @Count Long count);
	
	@RemoveMembers
	@Reverse
	void trim(@KeyVariable(name = "var") String keyVar,@StartIndex Long start, @EndIndex Long end);
	
	@GetMembers
	@Random
	List<String> randomOne(@KeyVariable(name = "var") String keyVar);
	
	@GetMembers
	@Random
	List<String> random(@KeyVariable(name = "var") String keyVar,@Count Long count);
	
	@GetMembers
	@Random
	@Distinct
	List<String> randomAndDistinct(@KeyVariable(name = "var") String keyVar,@Count Long count);
	
}