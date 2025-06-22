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

import com.langwuyue.orange.redis.RedisValueTypeEnum;

/**
 * Interface for Redis script operations supporting execution of Lua scripts.
 * Equivalent to Redis EVAL and EVALSHA commands with enhanced type handling.
 * 
 * @author Liang.Zhong
 * @since 1.0.0
 */
public interface OrangeRedisScriptOperations {

	/**
	 * Executes a Redis Lua script with proper type handling for arguments and return value.
	 * 
	 * @param script the Lua script to execute (or SHA1 digest for cached scripts)
	 * @param argsValueTypes map of argument values to their Redis value types
	 * @param returnValueType the expected Redis value type of the return value
	 * @param returnType the Java type to convert the return value to
	 * @param keys list of keys accessed by the script
	 * @param args arguments to pass to the script
	 * @return the script result converted to the specified return type
	 * @throws Exception if script execution fails or type conversion fails
	 * 
	 * @see <a href="https://redis.io/commands/eval">Redis EVAL command</a>
	 * @see <a href="https://redis.io/commands/evalsha">Redis EVALSHA command</a>
	 */
	Object execute(
		String script, 
		Map<Object, RedisValueTypeEnum> argsValueTypes, 
		RedisValueTypeEnum returnValueType,
		Type returnType, 
		List<String> keys,
		Object... args
	) throws Exception;

}