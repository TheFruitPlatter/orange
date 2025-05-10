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
package com.langwuyue.orange.example.redis.api.script;

import java.util.concurrent.TimeUnit;

import com.langwuyue.orange.example.redis.api.value.OrangeRedisValueExample5Api;
import com.langwuyue.orange.redis.RedisValueTypeEnum;
import com.langwuyue.orange.redis.annotation.OrangeRedisKey;
import com.langwuyue.orange.redis.annotation.Timeout;
import com.langwuyue.orange.redis.annotation.script.ExecuteLuaScript;
import com.langwuyue.orange.redis.annotation.script.OrangeRedisScriptClient;
import com.langwuyue.orange.redis.annotation.script.ScriptArg;

/**
 * @author Liang.Zhong
 * @since 1.0.0
 */
@OrangeRedisScriptClient(returnType = RedisValueTypeEnum.STRING)
@OrangeRedisKey(expirationTime = @Timeout(value = 1, unit = TimeUnit.HOURS), key = "orange:script:example1")
public interface OrangeRedisScriptExample1Api {
	
	@ExecuteLuaScript(script = 
	"local key = KEYS[1]; \n" +
	"local expected = ARGV[1]; \n" +
	"local newValue = ARGV[2]; \n" +
	"local current = redis.call('GET', key); \n" +
	"if ((current == nil or current == false) and expected == '[[NIL]]') or current == expected then \n " +
	"    if newValue == '[[NIL]]' then \n" +
	"        redis.call('DEL', KEYS[1]); \n" +
	"    else \n" +
	"        redis.call('SET', key, newValue); \n" +
	"    end \n" +
	"    return '1'; \n" +
	"else \n" +
	"    return '0' \n" +
	"end", keys = {OrangeRedisValueExample5Api.class})
	String cas(
		@ScriptArg(argType = RedisValueTypeEnum.STRING) String arg1,
		@ScriptArg(argType = RedisValueTypeEnum.STRING) String arg2
	);
	
}