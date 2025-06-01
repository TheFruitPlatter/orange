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
package com.langwuyue.orange.redis.context.builder;

import java.util.List;

import com.langwuyue.orange.redis.context.OrangeRedisContext;
import com.langwuyue.orange.redis.executor.script.context.OrangeScriptContext;

/**
 * A specialized context builder for Redis Lua script operations.
 * 
 * <p>This builder extends {@link OrangeRedisContextBuilder} to support Redis Lua script
 * execution through EVAL and EVALSHA commands. It provides configuration for:
 * <ul>
 *   <li>Script keys that will be accessed by the Lua script</li>
 *   <li>Script arguments that will be passed to the script</li>
 * </ul>
 * 
 * <p>In Redis script execution:
 * <ul>
 *   <li>Keys are provided separately from other arguments for proper key distribution in cluster mode</li>
 *   <li>Keys can be accessed in the Lua script using KEYS[n] notation</li>
 *   <li>Other arguments can be accessed using ARGV[n] notation</li>
 * </ul>
 * 
 *
 * @author Liang.Zhong
 * @since 1.0.0
 * @see OrangeRedisContextBuilder
 * @see OrangeScriptContext
 */
public class OrangeRedisScriptContextBuilder extends OrangeRedisContextBuilder {
	
	/**
	 * The list of Redis keys that will be accessed by the Lua script.
	 * <p>These keys are provided separately from other arguments to support proper
	 * key distribution in Redis cluster mode. In the Lua script, these keys can be
	 * accessed using the KEYS[n] array, where n is the 1-based index of the key
	 * in this list.
	 */
	private List<String> keys;
	
	/**
	 * Creates a new instance of OrangeRedisScriptContextBuilder.
	 * 
	 * <p>Initializes a new builder with default settings. The following properties
	 * must be configured before building the context:
	 * <ul>
	 *   <li>Script keys (via {@link #keys})</li>
	 *   <li>Value type (via {@link #valueType})</li>
	 *   <li>Operation metadata (owner, method, arguments)</li>
	 * </ul>
	 * 
	 * @see #keys(List)
	 * @see #valueType(RedisValueTypeEnum)
	 */
	public OrangeRedisScriptContextBuilder() {}
	
	/**
	 * Creates a new context for Redis Lua script operations.
	 * 
	 * <p>This method overrides the base implementation to create a specialized
	 * {@link OrangeScriptContext} that supports Lua script execution. The context
	 * creation process includes:
	 * 
	 * <ol>
	 *   <li>Validation of required builder properties:
	 *     <ul>
	 *       <li>Operation owner class must be set</li>
	 *       <li>Operation method must be set</li>
	 *       <li>Value type must be specified</li>
	 *     </ul>
	 *   </li>
	 *   <li>Context initialization with:
	 *     <ul>
	 *       <li>Script keys (can be null for scripts not accessing any keys)</li>
	 *       <li>Script arguments from the method invocation</li>
	 *       <li>Operation metadata for execution tracking</li>
	 *     </ul>
	 *   </li>
	 * </ol>
	 * 
	 * <p>The created context provides access to:
	 * <ul>
	 *   <li>Script keys via {@code getKeys()}</li>
	 *   <li>Script arguments via {@code getArgs()}</li>
	 *   <li>Value type information for result handling</li>
	 *   <li>Operation metadata for debugging and monitoring</li>
	 * </ul>
	 *
	 * @return a new {@link OrangeScriptContext} configured with the current builder settings
	 * @throws Exception if context creation or initialization fails
	 * @see OrangeScriptContext
	 * @see #keys(List)
	 */
	@Override
	protected OrangeRedisContext newContext() throws Exception {
		return OrangeScriptContext.newInstance(
			this.getContextClass(),
			this.getOperationOwner(),
			this.getOperationMethod(),
			this.getArgs(),
			this.keys,
			this.getValueType()
		);
	}

	/**
	 * Sets the Redis keys that will be accessed by the Lua script.
	 * 
	 * <p>In Redis script execution, keys must be provided separately from other arguments
	 * for two main reasons:
	 * <ul>
	 *   <li>Key distribution: Redis cluster needs to know which keys the script will
	 *       access to route the command to the correct nodes</li>
	 *   <li>Script safety: By declaring keys explicitly, Redis can better manage
	 *       script execution and prevent unintended key access</li>
	 * </ul>
	 * 
	 * <p>Inside the Lua script, these keys can be accessed using the KEYS array:
	 * <pre>{@code
	 * -- Lua script example
	 * local firstKey = KEYS[1]
	 * local secondKey = KEYS[2]
	 * }</pre>
	 *
	 * <p>Example usage:
	 * <pre>{@code
	 * builder.keys(Arrays.asList("user:profile", "user:settings"))
	 * }</pre>
	 *
	 * @param keys a list of Redis keys that the script will access. Can be null or empty
	 *            if the script doesn't need to access any keys.
	 * @return this builder instance for method chaining
	 */
	public OrangeRedisScriptContextBuilder keys(List<String> keys) {
		this.keys = keys;
		return this;
	}
}