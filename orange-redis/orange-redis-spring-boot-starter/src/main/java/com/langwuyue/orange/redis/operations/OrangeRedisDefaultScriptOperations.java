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
 * @author Liang.Zhong
 * @since 1.0.0
 */
public class OrangeRedisDefaultScriptOperations implements OrangeRedisScriptOperations {

	private RedisTemplate<String,byte[]> template;
	
	private OrangeRedisSerializer redisSerializer;
	
	private OrangeRedisLogger logger;
	
	public OrangeRedisDefaultScriptOperations(
		RedisTemplate<String,byte[]> template,
		OrangeRedisSerializer redisSerializer,
		OrangeRedisLogger logger
	) {
		this.template = template;
		this.redisSerializer = redisSerializer;
		this.logger = logger;
	}
	
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
