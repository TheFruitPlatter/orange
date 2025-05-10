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
 * @author Liang.Zhong
 * @since 1.0.0
 */
public class OrangeRedisScriptExecutorIdGenerator extends OrangeRedisExecutorIdAbstractGenerator {

	@Override
	protected void registerSupportedAnnotationClasses(List<Class<? extends Annotation>> supportedAnnotationClasses) {
		supportedAnnotationClasses.add(ExecuteLuaScript.class);
		supportedAnnotationClasses.add(ScriptArg.class);
	}
	
}
