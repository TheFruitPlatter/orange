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
 * Context class for executing Lua scripts in Redis operations.
 * 
 * <p>This class extends the base Redis context to provide specific functionality
 * for Lua script execution. It manages script arguments, their value types, and
 * the script itself, along with Redis keys that the script will operate on.
 * 
 * <p>The context is typically created during the processing of methods annotated
 * with {@link ExecuteLuaScript} and contains all necessary information to execute
 * the script against Redis.
 *
 * @author Liang.Zhong
 * @since 1.0.0
 */
public class OrangeScriptContext extends OrangeRedisContext {
	
	/**
	 * Map of script arguments and their corresponding {@link ScriptArg} annotations.
	 * This field is populated automatically through the {@link OrangeRedisOperationArg} mechanism.
	 */
	@OrangeRedisOperationArg(binding = ScriptArg.class, valueHandler = OrangeArgAndAnnotationToMapHandler.class)
	private Map<Object,ScriptArg> scriptArgs;
	
	/**
	 * The {@link ExecuteLuaScript} annotation from the operation method.
	 * Contains the Lua script to be executed and other execution parameters.
	 */
	@OrangeRedisOperationArg(binding = ExecuteLuaScript.class,valueHandler = OrangeMethodAnnotationHandler.class)
	private ExecuteLuaScript executeLuaScript;
	
	/**
	 * List of Redis keys that the script will operate on.
	 * In Redis Lua scripts, keys must be provided separately from other arguments.
	 */
	private List<String> keys;

	/**
	 * Constructs a new script context with the specified parameters.
	 *
	 * @param operationOwner the class that owns the operation method
	 * @param operationMethod the method annotated with {@link ExecuteLuaScript}
	 * @param args the arguments passed to the operation method
	 * @param keys the Redis keys that the script will operate on
	 * @param valueType the type of values being processed
	 */
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
	
	/**
	 * Creates a new instance of the specified Redis context class with the given parameters.
	 * This factory method uses reflection to instantiate the context class.
	 *
	 * @param contextClass the class of the context to instantiate
	 * @param operationOwner the class that owns the operation method
	 * @param operationMethod the method annotated with {@link ExecuteLuaScript}
	 * @param args the arguments passed to the operation method
	 * @param keys the Redis keys that the script will operate on
	 * @param valueType the type of values being processed
	 * @return a new instance of the specified context class
	 * @throws Exception if an error occurs during instantiation
	 */
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

	/**
	 * Gets a map of script arguments with their corresponding Redis value types.
	 * This method transforms the internal scriptArgs map (which contains ScriptArg annotations)
	 * into a map of argument values and their Redis value types.
	 *
	 * @return a map where keys are script argument values and values are their Redis value types
	 */
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
	
	/**
	 * Converts the script arguments to an array.
	 * This method extracts all argument values from the scriptArgs map and returns them as an array.
	 * If no arguments are present, returns an empty array.
	 *
	 * @return an array containing all script argument values
	 */
	public Object[] argsToArray() {
		if(this.scriptArgs == null) {
			return new Object[] {};
		}
		return this.scriptArgs.keySet().toArray();
	}

	/**
	 * Gets the Lua script to be executed.
	 * This method retrieves the script content from the {@link ExecuteLuaScript} annotation.
	 *
	 * @return the Lua script content as a string
	 */
	public String getScript() {
		return executeLuaScript.script();
	}

	/**
	 * Gets the list of Redis keys that the script will operate on.
	 * In Redis Lua scripts, keys must be provided separately from other arguments
	 * for proper script execution and key-based sharding.
	 *
	 * @return a list of Redis key names
	 */
	public List<String> getKeys() {
		return keys;
	}
}