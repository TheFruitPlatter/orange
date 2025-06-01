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
 * Specialized argument handler mapping for Redis Lua script operations.
 * 
 * <p>This class extends {@link OrangeOperationArgHandlerMapping} to provide specific
 * handling for Lua script execution in Redis. It manages the mapping between operation
 * methods and their key parameters as defined in {@link ExecuteLuaScript} annotations.
 * 
 * <p>Key features:
 * <ul>
 *   <li>Extracts and stores key parameter classes from {@link ExecuteLuaScript} annotations</li>
 *   <li>Provides access to key classes for script execution</li>
 *   <li>Maintains thread-safe mapping of methods to their key parameters</li>
 * </ul>
 *
 * @author Liang.Zhong
 * @since 1.0.0
 * @see ExecuteLuaScript
 * @see OrangeOperationArgHandlerMapping
 */
public class OrangeScriptArgHandlerMapping extends OrangeOperationArgHandlerMapping {
	
	/**
	 * Thread-safe mapping between operation methods and their key parameter classes.
	 * This map stores the key parameter types defined in {@link ExecuteLuaScript} annotations
	 * for each Lua script operation method.
	 */
	private static final Map<Method, List<Class<?>>> OPEATION_KEYS_MAPPING = new ConcurrentHashMap<>();
	
	/**
	 * Creates a new instance of OrangeScriptArgHandlerMapping.
	 *
	 * @param executorsMapping the mapping of Redis executors
	 * @param operationOwner the class that owns the Redis operations
	 * @param valueHandlerMap map of value handlers for different parameter types
	 */
	public OrangeScriptArgHandlerMapping(
		OrangeRedisExecutorsMapping executorsMapping, 
		Class<?> operationOwner,
		Map<Class<? extends OrangeOperationArgHandler>, OrangeOperationArgHandler> valueHandlerMap
	) {
		super(executorsMapping,operationOwner,valueHandlerMap);
	}
	
	/**
	 * {@inheritDoc}
	 * 
	 * <p>Extends the parent implementation to handle Lua script operations:
	 * <ol>
	 *   <li>Gets the executor from parent implementation</li>
	 *   <li>Retrieves the actual method from executor mapping</li>
	 *   <li>Extracts key classes from {@link ExecuteLuaScript} annotation</li>
	 *   <li>Stores the key classes in the mapping for later use</li>
	 * </ol>
	 *
	 * @param method the method to get executor for
	 * @return the Redis executor for the method
	 */
	@Override
	protected OrangeRedisExecutor getOrangeRedisExecutor(Method method) {
		OrangeRedisExecutor executor = super.getOrangeRedisExecutor(method);
		Method actualMethod = getExecutorsMapping().getActualMethod(method);
		ExecuteLuaScript script = actualMethod.getAnnotation(ExecuteLuaScript.class);
		OPEATION_KEYS_MAPPING.put(method, OrangeCollectionUtils.asList(script.keys()));
		return executor;
	}
	
	/**
	 * Retrieves the list of key parameter classes for a given method.
	 * 
	 * <p>These classes represent the types of keys that will be passed to the
	 * Lua script as defined in the {@link ExecuteLuaScript} annotation.
	 *
	 * @param method the method to get key classes for
	 * @return list of classes representing the key parameters, or null if method not found
	 */
	public List<Class<?>> getKeyClasses(Method method){
		return OPEATION_KEYS_MAPPING.get(method);
	}
}