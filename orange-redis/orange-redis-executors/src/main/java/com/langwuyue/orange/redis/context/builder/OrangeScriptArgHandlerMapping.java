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

import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.langwuyue.orange.redis.annotation.script.ExecuteLuaScript;
import com.langwuyue.orange.redis.executor.OrangeRedisExecutor;
import com.langwuyue.orange.redis.mapping.OrangeRedisExecutorsMapping;
import com.langwuyue.orange.redis.utils.OrangeCollectionUtils;

/**
 * @author Liang.Zhong
 * @since 1.0.0
 */
public class OrangeScriptArgHandlerMapping extends OrangeOperationArgHandlerMapping {
	
	private static final Map<Method, List<Class<?>>> OPEATION_KEYS_MAPPING = new ConcurrentHashMap<>();
	
	public OrangeScriptArgHandlerMapping(
		OrangeRedisExecutorsMapping executorsMapping, 
		Class<?> operationOwner,
		Map<Class<? extends OrangeOperationArgHandler>, OrangeOperationArgHandler> valueHandlerMap
	) {
		super(executorsMapping,operationOwner,valueHandlerMap);
	}
	
	@Override
	protected OrangeRedisExecutor getOrangeRedisExecutor(Method method) {
		OrangeRedisExecutor executor = super.getOrangeRedisExecutor(method);
		Method actualMethod = getExecutorsMapping().getActualMethod(method);
		ExecuteLuaScript script = actualMethod.getAnnotation(ExecuteLuaScript.class);
		OPEATION_KEYS_MAPPING.put(method, OrangeCollectionUtils.asList(script.keys()));
		return executor;
	}
	
	public List<Class<?>> getKeyClasses(Method method){
		return OPEATION_KEYS_MAPPING.get(method);
	}
}
