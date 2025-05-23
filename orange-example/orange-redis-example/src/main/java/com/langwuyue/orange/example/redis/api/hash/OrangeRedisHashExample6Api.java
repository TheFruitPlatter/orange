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

import com.langwuyue.orange.example.redis.entity.OrangeHashExampleEntity;
import com.langwuyue.orange.redis.RedisValueTypeEnum;
import com.langwuyue.orange.redis.annotation.AddMembers;
import com.langwuyue.orange.redis.annotation.ContinueOnFailure;
import com.langwuyue.orange.redis.annotation.Count;
import com.langwuyue.orange.redis.annotation.GetMembers;
import com.langwuyue.orange.redis.annotation.GetSize;
import com.langwuyue.orange.redis.annotation.IfAbsent;
import com.langwuyue.orange.redis.annotation.Member;
import com.langwuyue.orange.redis.annotation.Multiple;
import com.langwuyue.orange.redis.annotation.OrangeRedisKey;
import com.langwuyue.orange.redis.annotation.Random;
import com.langwuyue.orange.redis.annotation.RemoveMembers;
import com.langwuyue.orange.redis.annotation.Timeout;
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
@OrangeRedisHashClient(hashKeyType = RedisValueTypeEnum.STRING, hashValueType = RedisValueTypeEnum.JSON)
@OrangeRedisKey(expirationTime = @Timeout(value = 1, unit = TimeUnit.HOURS), key = "orange:hash:example6")
public interface OrangeRedisHashExample6Api {

	@AddMembers
	void add(@Member OrangeHashExampleEntity entity);

	@AddMembers
	@ContinueOnFailure(true)
	void add(@Multiple List<OrangeHashExampleEntity> members);

	@AddMembers
	@IfAbsent(deleteInTheEnd = false)
	void addIfAbsent(@Member OrangeHashExampleEntity entity);

	@AddMembers
	@IfAbsent(deleteInTheEnd = false)
	@ContinueOnFailure(true)
	void addIfAbsent(@Multiple List<OrangeHashExampleEntity> members);

	@GetSize
	Long getSize();

	@GetHashValueLength
	Long getHashValueBytesLength(@HashKey String key);

	@GetHashKeys
	Set<String> getAllKeys();

	@GetHashKeys
	@Random
	String randomKey();

	@GetHashKeys
	@Random
	Set<String> randomKeys(@Count Long count);

	@HasKeys
	Boolean hasKey(@HashKey String key);

	@HasKeys
	@ContinueOnFailure(true)
	Map<String, Boolean> hasKeys(@Multiple Collection<String> key);

	@GetHashValues
	List<OrangeHashExampleEntity> getValues();

	@GetHashValues
	OrangeHashExampleEntity get(@HashKey String key);

	@GetHashValues
	List<OrangeHashExampleEntity> get(@Multiple Collection<String> key);

	@GetMembers
	List<OrangeHashExampleEntity> getAllMembers();

	@RemoveMembers
	Boolean remove(@HashKey String key);

	@RemoveMembers
	@ContinueOnFailure(true)
	Map<String, Boolean> remove(@Multiple Collection<String> keys);
}