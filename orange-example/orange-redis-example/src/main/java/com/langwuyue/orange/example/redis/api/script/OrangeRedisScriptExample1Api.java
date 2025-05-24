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
 * Redis Script Client Example 1 - CAS (Compare-And-Swap) Operation.
 * 
 * <p>This interface demonstrates how to implement a Redis script client for performing
 * atomic compare-and-swap operations using Lua scripts.</p>
 *
 * <p>The script will atomically compare the current value with an expected value,
 * and only if they match, update to the new value.</p>
 *
 * @author Liang.Zhong
 * @since 1.0.0
 */
@OrangeRedisScriptClient(returnType = RedisValueTypeEnum.STRING)
@OrangeRedisKey(expirationTime = @Timeout(value = 1, unit = TimeUnit.HOURS), key = "orange:script:example1")
public interface OrangeRedisScriptExample1Api {
	
	/**
	 * Performs an atomic Compare-And-Swap (CAS) operation with simplified parameters.
	 * 
	 * <p>The operation will:
	 * <ol>
	 *   <li>Get current value for the key (arg1)</li>
	 *   <li>Compare with expected value (arg2)</li>
	 *   <li>If matches (or both are nil):</li>
	 *   <ul>
	 *     <li>Delete key if new value is '[[NIL]]'</li>
	 *     <li>Set new value otherwise</li>
	 *     <li>Return '1' indicating success</li>
	 *   </ul>
	 *   <li>Else return '0' indicating failure</li>
	 * </ol>
	 * </p>
	 *
	 * <p>Special values:
	 * <ul>
	 *   <li>'[[NIL]]' represents nil/empty value</li>
	 *   <li>Returns '1' for success, '0' for failure</li>
	 * </ul>
	 * </p>
	 *
	 * @param arg1 The Redis key to operate on
	 * @param arg2 The expected value (use '[[NIL]]' for nil) and new value combined:
	 *             Format is "expectedValue:newValue"
	 * @return "1" if update was successful, "0" otherwise
	 */
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