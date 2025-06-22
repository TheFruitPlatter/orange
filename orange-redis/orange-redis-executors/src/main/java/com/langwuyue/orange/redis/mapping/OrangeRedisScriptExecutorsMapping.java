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
package com.langwuyue.orange.redis.mapping;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import com.langwuyue.orange.redis.executor.OrangeRedisExecutor;
import com.langwuyue.orange.redis.executor.script.OrangeLuaScriptExecutor;
import com.langwuyue.orange.redis.listener.OrangeRedisMultipleSetIfAbsentListener;
import com.langwuyue.orange.redis.listener.OrangeRedisSetIfAbsentListener;
import com.langwuyue.orange.redis.logger.OrangeRedisLogger;
import com.langwuyue.orange.redis.operations.OrangeRedisOperations;
import com.langwuyue.orange.redis.operations.OrangeRedisScriptOperations;
import com.langwuyue.orange.redis.template.NoTemplate;

/**
 * A specialized executor mapping implementation for Redis script operations.
 * 
 * <p>This class extends {@link OrangeRedisAbstractExecutorsMapping} to provide
 * specific support for script execution in Redis. It manages the registration and
 * configuration of script-related executors, particularly focusing on Lua script
 * execution capabilities.
 * 
 * <p>Key features of this mapping include:
 * <ul>
 *   <li>Registration of {@link OrangeLuaScriptExecutor} for Lua script execution</li>
 *   <li>Support for distinct annotation processing</li>
 *   <li>No template requirement (uses {@link NoTemplate})</li>
 * </ul>
 * 
 * <p>This mapping is designed to work with {@link OrangeRedisScriptExecutorIdGenerator}
 * to properly identify and route script execution requests to the appropriate executor.
 *
 * @author Liang.Zhong
 * @since 1.0.0
 */
public class OrangeRedisScriptExecutorsMapping extends OrangeRedisAbstractExecutorsMapping {
	
	/**
	 * Constructs a new OrangeRedisScriptExecutorsMapping with the specified components.
	 * 
	 * <p>This constructor initializes the script executors mapping with required dependencies
	 * for script execution in Redis. Note that it passes null as the operations parameter
	 * to the parent constructor since script operations don't require standard Redis operations.
	 * 
	 * @param generator the script executor ID generator used to identify script execution methods
	 * @param listeners collection of listeners for single lock set-if-absent operations
	 * @param scriptOperations the Redis script operations for executing Lua scripts
	 * @param multipleListeners collection of listeners for multiple locks set-if-absent operations
	 * @param logger the logger for operation tracking
	 */
	public OrangeRedisScriptExecutorsMapping(
		OrangeRedisScriptExecutorIdGenerator generator,
		Collection<OrangeRedisSetIfAbsentListener> listeners,
		OrangeRedisScriptOperations scriptOperations,
		Collection<OrangeRedisMultipleSetIfAbsentListener> multipleListeners,
		OrangeRedisLogger logger
	) {
		super(null,generator,scriptOperations,listeners,multipleListeners,logger);
	}

	/**
	 * Registers script-specific executors with this mapping.
	 * 
	 * <p>This method overrides the parent implementation to register executors
	 * specifically designed for script execution in Redis. Currently, it registers
	 * a single executor:
	 * <ul>
	 *   <li>{@link OrangeLuaScriptExecutor} - For executing Lua scripts in Redis</li>
	 * </ul>
	 * 
	 * <p>The executor is initialized with the provided script operations and generator
	 * to ensure proper execution and identification of script operations.
	 *
	 * @param executors the list of executors to which new executors will be added
	 * @param operations standard Redis operations (not used in this implementation)
	 * @param generator the executor ID generator
	 * @param scriptOperations the Redis script operations for executing Lua scripts
	 * @param listeners collection of listeners for single lock set-if-absent operations
	 * @param multipleListeners collection of listeners for multiple locks set-if-absent operations
	 * @param logger the logger for operation tracking
	 */
	@Override
	protected void registerExecutors(
		List<OrangeRedisExecutor> executors, 
		OrangeRedisOperations operations,
		OrangeRedisExecutorIdGenerator generator,
		OrangeRedisScriptOperations scriptOperations,
		Collection<OrangeRedisSetIfAbsentListener> listeners,
		Collection<OrangeRedisMultipleSetIfAbsentListener> multipleListeners,
		OrangeRedisLogger logger
	) {
		executors.add(new OrangeLuaScriptExecutor(scriptOperations,generator));
	}

	/**
	 * Retrieves and processes annotation classes from the given method.
	 * 
	 * <p>This method extends the parent implementation by ensuring that the returned
	 * list of annotation classes contains only distinct elements. This is particularly
	 * important for script operations where duplicate annotations could cause
	 * unexpected behavior.
	 * 
	 * <p>The method first calls the parent implementation to get the base list of
	 * annotation classes, then applies a distinct filter to remove any duplicates.
	 *
	 * @param annotations the array of annotations to process
	 * @param method the method from which annotations are extracted
	 * @return a list of distinct annotation classes associated with the method
	 */
	@Override
	protected List<Class<? extends Annotation>> getAnnotationClasses(Annotation[] annotations, Method method) {
		List<Class<? extends Annotation>> annotationClasses = super.getAnnotationClasses(annotations, method);
		return annotationClasses.stream().distinct().collect(Collectors.toList());
	}

	/**
	 * Returns the template class used by this executor mapping.
	 * 
	 * <p> This template just helps clarify error messages.
	 *
	 * @return the {@link NoTemplate} class, indicating no template is required
	 */
	@Override
	protected Class<?> getTemplateClass() {
		return NoTemplate.class;
	}
}