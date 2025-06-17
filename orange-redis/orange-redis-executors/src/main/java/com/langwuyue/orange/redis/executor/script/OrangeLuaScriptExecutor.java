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

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.List;

import com.langwuyue.orange.redis.annotation.RedisValue;
import com.langwuyue.orange.redis.annotation.script.ExecuteLuaScript;
import com.langwuyue.orange.redis.annotation.script.ScriptArg;
import com.langwuyue.orange.redis.context.OrangeRedisContext;
import com.langwuyue.orange.redis.executor.OrangeRedisAbstractExecutor;
import com.langwuyue.orange.redis.executor.script.context.OrangeScriptContext;
import com.langwuyue.orange.redis.mapping.OrangeRedisExecutorIdGenerator;
import com.langwuyue.orange.redis.operations.OrangeRedisScriptOperations;
import com.langwuyue.orange.redis.utils.OrangeCollectionUtils;
import com.langwuyue.orange.redis.utils.OrangeReflectionUtils;

/**
 * Executor implementation for handling Redis Lua script operations.
 * This executor processes Redis operations annotated with {@link ExecuteLuaScript} and {@link ScriptArg},
 * executing Lua scripts against Redis server with proper argument handling and result mapping.
 * 
 *
 * @author Liang.Zhong
 * @since 1.0.0
 * @see ExecuteLuaScript
 * @see ScriptArg
 * @see OrangeRedisAbstractExecutor
 * @see <a href="https://orange.langwuyue.com/redis/advanced/script">Orange Redis Script Documentation</a>
 */
public class OrangeLuaScriptExecutor extends OrangeRedisAbstractExecutor {
	
	private OrangeRedisScriptOperations operations;

	/**
	 * Constructs a new OrangeLuaScriptExecutor with the specified operations and ID generator.
	 *
	 * @param operations the Redis script operations implementation used to execute Lua scripts
	 * @param idGenerator the generator used to create unique identifiers for executor instances
	 */
	public OrangeLuaScriptExecutor(OrangeRedisScriptOperations operations,OrangeRedisExecutorIdGenerator idGenerator) {
		super(idGenerator);
		this.operations = operations;
	}

	/**
	 * {@inheritDoc}
	 * 
	 * <p>Executes a Lua script against Redis using the provided context. This implementation:
	 * <ol>
	 *   <li>Casts the generic context to an {@link OrangeScriptContext}</li>
	 *   <li>Determines the return type and checks for {@link RedisValue} annotated fields</li>
	 *   <li>Executes the script with appropriate arguments and type information</li>
	 *   <li>Handles result mapping, including special handling for {@link RedisValue} annotated fields</li>
	 * </ol>
	 * 
	 * @param context the execution context containing script and its parameters (must be an instance of {@link OrangeScriptContext})
	 * @return the result of the script execution, properly mapped to the expected return type
	 * @throws Exception if any error occurs during script execution or result mapping
	 * @throws ClassCastException if the provided context is not an instance of {@link OrangeScriptContext}
	 */
	@Override
	public Object execute(OrangeRedisContext context) throws Exception {
		OrangeScriptContext ctx = (OrangeScriptContext) context;
		Class<?> returnClass = ctx.getOperationMethod().getReturnType();
		Field[] fields = returnClass.getDeclaredFields();
		Field valueField = null;
		for(Field field : fields) {
			if(field.isAnnotationPresent(RedisValue.class)){
				valueField = field;
			}
		}
		Object result = this.operations.execute(
			ctx.getScript(), 
			ctx.getScriptArgs(), 
			context.getValueType(), 
			valueField == null ? returnClass : valueField.getType(), 
			ctx.getKeys(), 
			ctx.argsToArray()
		);
		
		if(valueField == null) {
			return result;
		}
		
		Object returnValue = returnClass.getConstructor().newInstance();
		OrangeReflectionUtils.setFieldValue(valueField, returnValue, result);
		return returnValue;
	}

	/**
	 * {@inheritDoc}
	 * 
	 * <p>Returns the list of annotation classes that this executor supports.
	 * This executor specifically handles {@link ExecuteLuaScript} and {@link ScriptArg}
	 * annotations for Lua script execution in Redis.
	 *
	 * @return a list containing {@link ExecuteLuaScript} and {@link ScriptArg} classes
	 */
	@Override
	protected List<Class<? extends Annotation>> getSupportedAnnotationClasses() {
		return OrangeCollectionUtils.asList(ExecuteLuaScript.class,ScriptArg.class);
	}

	/**
	 * {@inheritDoc}
	 * 
	 * <p>Returns the context class that this executor uses for script execution.
	 * This executor uses {@link OrangeScriptContext} to hold script content,
	 * keys, and arguments for Lua script execution.
	 *
	 * @return the {@link OrangeScriptContext} class
	 */
	@Override
	public Class<? extends OrangeRedisContext> getContextClass() {
		return OrangeScriptContext.class;
	}
}