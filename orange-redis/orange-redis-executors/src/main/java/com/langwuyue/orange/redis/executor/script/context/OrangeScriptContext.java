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
package com.langwuyue.orange.redis.executor.script.context;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.langwuyue.orange.redis.RedisValueTypeEnum;
import com.langwuyue.orange.redis.annotation.OrangeRedisOperationArg;
import com.langwuyue.orange.redis.annotation.script.ExecuteLuaScript;
import com.langwuyue.orange.redis.annotation.script.ScriptArg;
import com.langwuyue.orange.redis.context.OrangeRedisContext;
import com.langwuyue.orange.redis.context.builder.OrangeArgAndAnnotationToMapHandler;
import com.langwuyue.orange.redis.context.builder.OrangeMethodAnnotationHandler;

/**
 * @author Liang.Zhong
 * @since 1.0.0
 */
public class OrangeScriptContext extends OrangeRedisContext {
	
	@OrangeRedisOperationArg(binding = ScriptArg.class, valueHandler = OrangeArgAndAnnotationToMapHandler.class)
	private Map<Object,ScriptArg> scriptArgs;
	
	@OrangeRedisOperationArg(binding = ExecuteLuaScript.class,valueHandler = OrangeMethodAnnotationHandler.class)
	private ExecuteLuaScript executeLuaScript;
	
	private List<String> keys;

	public OrangeScriptContext(
		Class<?> operationOwner, 
		Method operationMethod, 
		Object[] args,
		List<String> keys,
		RedisValueTypeEnum valueType
	) {
		super(operationOwner, operationMethod, args, null, valueType);
		this.keys = keys;
	}
	
	public static OrangeRedisContext newInstance(
		Class<? extends OrangeRedisContext> contextClass,
		Class<?> operationOwner, 
		Method operationMethod, 
		Object[] args,
		List<String> keys,
		RedisValueTypeEnum valueType
	) throws Exception{
		Constructor<? extends OrangeRedisContext> constructor = contextClass.getConstructor(
			Class.class,
			Method.class,
			Object[].class,
			List.class,
			RedisValueTypeEnum.class
		);
		return constructor.newInstance(operationOwner,operationMethod,args,keys,valueType);
	}

	public Map<Object,RedisValueTypeEnum> getScriptArgs() {
		Map<Object,RedisValueTypeEnum> argTypesMap = new LinkedHashMap<>();
		if(this.scriptArgs == null) {
			return new LinkedHashMap<>();
		}
		for(Entry<Object,ScriptArg> entry : scriptArgs.entrySet()) {
			argTypesMap.put(entry.getKey(), entry.getValue().argType());
		}
		return argTypesMap;
	}
	
	public Object[] argsToArray() {
		if(this.scriptArgs == null) {
			return new Object[] {};
		}
		return this.scriptArgs.keySet().toArray();
	}

	public String getScript() {
		return executeLuaScript.script();
	}

	public List<String> getKeys() {
		return keys;
	}
}
