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

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;

import com.langwuyue.orange.redis.RedisValueTypeEnum;
import com.langwuyue.orange.redis.configuration.OrangeRedisSerializer;
import com.langwuyue.orange.redis.logger.OrangeRedisLogger;

/**
 * Default implementation of {@link OrangeRedisScriptOperations} that provides Redis script execution capabilities.
 * 
 * <p>This class handles the execution of Redis scripts (Lua scripts) with support for:
 * <ul>
 *   <li>Custom serialization of script arguments</li>
 *   <li>Type-safe deserialization of script results</li>
 *   <li>Detailed logging of script execution</li>
 * </ul>
 *
 * @author Liang.Zhong
 * @since 1.0.0
 * @see OrangeRedisScriptOperations
 */
public class OrangeRedisDefaultScriptOperations implements OrangeRedisScriptOperations {

	/**
	 * Redis template used for executing Redis script operations.
	 * The template is configured to work with String keys and byte[] values.
	 */
	private RedisTemplate<String,byte[]> template;
	
	/**
	 * Serializer for converting between Java objects and Redis byte arrays.
	 * Handles serialization and deserialization of script arguments and results.
	 */
	private OrangeRedisSerializer redisSerializer;
	
	/**
	 * Logger for recording script execution details and debugging information.
	 */
	private OrangeRedisLogger logger;
	
	/**
	 * Constructs a new {@code OrangeRedisDefaultScriptOperations} instance.
	 *
	 * @param template The Redis template used for script execution
	 * @param redisSerializer The serializer for converting between Java objects and Redis byte arrays
	 * @param logger The logger for recording script execution details
	 */
	public OrangeRedisDefaultScriptOperations(
		RedisTemplate<String,byte[]> template,
		OrangeRedisSerializer redisSerializer,
		OrangeRedisLogger logger
	) {
		this.template = template;
		this.redisSerializer = redisSerializer;
		this.logger = logger;
	}
	
	/**
	 * Executes a Redis script with the given arguments and returns the deserialized result.
	 * 
	 * <p>This method provides flexible script execution with support for:
	 * <ul>
	 *   <li>Custom serialization of each argument based on its value type</li>
	 *   <li>Type-safe deserialization of the script result</li>
	 *   <li>Detailed logging of script execution details</li>
	 * </ul>
	 *
	 * @param script The Lua script to execute
	 * @param argsValueType A map specifying the serialization type for each argument
	 * @param returnValueType The expected value type of the script result
	 * @param returnType The Java type to deserialize the result into
	 * @param keys The Redis keys used in the script
	 * @param args The arguments passed to the script
	 * @return The deserialized script result, or null if the script returned nil
	 * @throws Exception If script execution fails or result deserialization fails
	 */
	@Override
	public Object execute(
		String script, 
		Map<Object, RedisValueTypeEnum> argsValueType,
		RedisValueTypeEnum returnValueType,
		Type returnType, 
		List<String> keys, 
		Object... args
	) throws Exception {
		Object[] scriptArgs = args;
		if(!argsValueType.isEmpty()) {
			scriptArgs = new Object[args.length];
			int len = args.length;
			for(int i = 0 ; i < len; i++) {
				Object arg = args[i];
				RedisValueTypeEnum valueType = argsValueType.get(arg);
				if(valueType != null) {
					scriptArgs[i] = redisSerializer.serialize(arg, valueType);
				}else{
					scriptArgs[i] = arg;
				}
			}
		}
		if(logger.isDebugEnabled()) {
			logger.debug("Redis script 'execute' operation executing: execute(script:{} {} keys:{} {} args:{})", 
				script,
				"\n",
				keys,
				"\n",
				redisSerializer.serializeToJSONString(scriptArgs)
			);
		}
		byte[] bytes = this.template.execute(new DefaultRedisScript<byte[]>(script, byte[].class), keys, scriptArgs);
		if(bytes == null) {
			if(logger.isDebugEnabled()) {
				logger.debug("Redis script 'execute' operation returned null");
			}
			return null;
		}
		Object results = redisSerializer.deserialize(bytes, returnValueType, returnType);
		if(logger.isDebugEnabled()) {
			logger.debug("Redis script 'execute' operation returned {}", new String(bytes));
		}
		return results;
	}

}