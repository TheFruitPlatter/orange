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
 * @author Liang.Zhong
 * @since 1.0.0
 */
public class OrangeLuaScriptExecutor extends OrangeRedisAbstractExecutor {
	
	private OrangeRedisScriptOperations operations;

	public OrangeLuaScriptExecutor(OrangeRedisScriptOperations operations,OrangeRedisExecutorIdGenerator idGenerator) {
		super(idGenerator);
		this.operations = operations;
	}

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

	@Override
	protected List<Class<? extends Annotation>> getSupportedAnnotationClasses() {
		return OrangeCollectionUtils.asList(ExecuteLuaScript.class,ScriptArg.class);
	}

	@Override
	public Class<? extends OrangeRedisContext> getContextClass() {
		return OrangeScriptContext.class;
	}
}
