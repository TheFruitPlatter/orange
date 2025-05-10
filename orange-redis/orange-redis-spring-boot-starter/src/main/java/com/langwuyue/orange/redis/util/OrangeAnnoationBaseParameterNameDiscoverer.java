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
package com.langwuyue.orange.redis.util;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;

import org.springframework.core.ParameterNameDiscoverer;
import org.springframework.lang.Nullable;

import com.langwuyue.orange.redis.annotation.KeyVariable;

/**
 * @author Liang.Zhong
 * @since 1.0.0
 */
public class OrangeAnnoationBaseParameterNameDiscoverer implements ParameterNameDiscoverer {

	@Override
	@Nullable
	public String[] getParameterNames(Method method) {
		return getParameterNames(method.getParameters());
	}

	@Override
	@Nullable
	public String[] getParameterNames(Constructor<?> ctor) {
		return getParameterNames(ctor.getParameters());
	}

	@Nullable
	private String[] getParameterNames(Parameter[] parameters) {
		int len = parameters.length;
		String[] parameterNames = new String[len];
		for (int i = 0; i < len; i++) {
			Parameter param = parameters[i];
			KeyVariable keyVariable = param.getAnnotation(KeyVariable.class);
			if (keyVariable != null) {
				parameterNames[i] = keyVariable.name();
			}
		}
		return parameterNames;
	}

}
