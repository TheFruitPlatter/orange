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
package com.langwuyue.orange.example.redis.api.value;

import java.util.concurrent.TimeUnit;

import com.langwuyue.orange.example.redis.entity.OrangeValueExampleEntity;
import com.langwuyue.orange.redis.RedisValueTypeEnum;
import com.langwuyue.orange.redis.annotation.CAS;
import com.langwuyue.orange.redis.annotation.IfAbsent;
import com.langwuyue.orange.redis.annotation.KeyVariable;
import com.langwuyue.orange.redis.annotation.OrangeRedisKey;
import com.langwuyue.orange.redis.annotation.RedisOldValue;
import com.langwuyue.orange.redis.annotation.RedisValue;
import com.langwuyue.orange.redis.annotation.Timeout;
import com.langwuyue.orange.redis.annotation.TimeoutUnit;
import com.langwuyue.orange.redis.annotation.global.Delete;
import com.langwuyue.orange.redis.annotation.global.GetExpiration;
import com.langwuyue.orange.redis.annotation.global.SetExpiration;
import com.langwuyue.orange.redis.annotation.value.GetValue;
import com.langwuyue.orange.redis.annotation.value.OrangeRedisValueClient;
import com.langwuyue.orange.redis.annotation.value.SetValue;

/**
 * @author Liang.Zhong
 * @since 1.0.0
 */
@OrangeRedisValueClient(valueType = RedisValueTypeEnum.JSON)
@OrangeRedisKey(expirationTime = @Timeout(value = 1, unit = TimeUnit.HOURS), key = "orange:value:example4:${var1}")
public interface OrangeRedisValueExample4Api {

	@SetValue
	void setValue(@KeyVariable(name="var1") String keyVar, @RedisValue OrangeValueExampleEntity value);
	
	@SetValue
	@SetExpiration
	void setValueWithExpiration(@KeyVariable(name="var1") String keyVar, @RedisValue OrangeValueExampleEntity value);
	
	@SetValue
	@IfAbsent
	Boolean setValueIfAbsent(@KeyVariable(name="var1") String keyVar, @RedisValue OrangeValueExampleEntity value);
	
	@SetValue
	@IfAbsent
	@SetExpiration
	Boolean setValueIfAbsentWithExpiration(@KeyVariable(name="var1") String keyVar, @RedisValue OrangeValueExampleEntity value);
	
	@CAS
	Boolean compareAndSwap(@KeyVariable(name="var1") String keyVar, @RedisOldValue OrangeValueExampleEntity oldValue, @RedisValue OrangeValueExampleEntity newValue);
	
	@GetValue
	OrangeValueExampleEntity getValue(@KeyVariable(name="var1") String keyVar);
	
	@SetExpiration
	boolean setExpiration(@KeyVariable(name="var1") String keyVar);
	
	@Delete
	boolean delete(@KeyVariable(name="var1") String keyVar);
	
	@GetExpiration
	Long getExpiration(@KeyVariable(name="var1") String keyVar);
	
	@GetExpiration
	Long getExpiration(@KeyVariable(name="var1") String keyVar,@TimeoutUnit TimeUnit unit);
}
