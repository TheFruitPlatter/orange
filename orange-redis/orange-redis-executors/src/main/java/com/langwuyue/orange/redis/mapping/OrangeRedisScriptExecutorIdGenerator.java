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
import java.util.List;

import com.langwuyue.orange.redis.annotation.script.ExecuteLuaScript;
import com.langwuyue.orange.redis.annotation.script.ScriptArg;

/**
 * A specialized executor ID generator for Redis script operations.
 * 
 * <p>This class extends {@link OrangeRedisExecutorIdAbstractGenerator} to provide
 * specific support for generating executor IDs for Redis script operations. It registers
 * script-related annotations that are used to identify and process script execution
 * requests in the Orange Redis framework.
 * 
 * <p>This generator is responsible for creating unique identifiers for script executors
 * based on the annotations present on interface methods, allowing the framework to
 * route script execution requests to the appropriate executor implementation.
 *
 * @author Liang.Zhong
 * @since 1.0.0
 */
public class OrangeRedisScriptExecutorIdGenerator extends OrangeRedisExecutorIdAbstractGenerator {

	/**
	 * Registers the annotation classes supported by this executor ID generator.
	 * 
	 * @param supportedAnnotationClasses the list to which supported annotation classes will be added
	 */
	@Override
	protected void registerSupportedAnnotationClasses(List<Class<? extends Annotation>> supportedAnnotationClasses) {
		supportedAnnotationClasses.add(ExecuteLuaScript.class);
		supportedAnnotationClasses.add(ScriptArg.class);
	}
	
}