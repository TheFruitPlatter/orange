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
package com.langwuyue.orange.redis.executor.script;

/**
 * Constants used in Lua script operations for Redis.
 * 
 * <p>This interface defines string constants that are used in Lua script
 * execution contexts, particularly for special value representations.
 */
public interface ScriptConstants {

	/**
	 * Represents a NIL value in Lua scripts.
	 * 
	 * <p>This constant is used as a placeholder for null values when executing
	 * Lua scripts, as Redis Lua scripts cannot directly handle null values.
	 * The double bracket notation ensures this string is unlikely to conflict
	 * with actual data values.
	 */
	String NIL = "[[NIL]]";
}