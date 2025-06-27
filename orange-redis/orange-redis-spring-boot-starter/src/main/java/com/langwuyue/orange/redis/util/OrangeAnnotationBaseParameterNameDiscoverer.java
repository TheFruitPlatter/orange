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
 * A parameter name discoverer that uses the {@link KeyVariable} annotation to determine parameter names.
 * 
 * <p>This implementation of {@link ParameterNameDiscoverer} looks for parameters annotated with
 * {@link KeyVariable} and uses the name specified in the annotation as the parameter name.
 * Parameters without this annotation will have null names in the returned array.
 * 
 * <p>This class is primarily used in Redis key generation to identify which method parameters
 * should be included in the key and what names they should have.
 *
 * @author Liang.Zhong
 * @since 1.0.0
 * @see KeyVariable
 * @see ParameterNameDiscoverer
 */
public class OrangeAnnotationBaseParameterNameDiscoverer implements ParameterNameDiscoverer {

	/**
	 * Gets parameter names for a method by examining {@link KeyVariable} annotations.
	 * 
	 * <p>This implementation extracts parameter names from the {@link KeyVariable} annotation's
	 * name attribute. Parameters without this annotation will have null names in the returned array.
	 *
	 * @param method the method for which parameter names are requested
	 * @return an array of parameter names, with null entries for parameters without
	 *         the {@link KeyVariable} annotation, or null if the method has no parameters
	 */
	@Override
	@Nullable
	public String[] getParameterNames(Method method) {
		return getParameterNames(method.getParameters());
	}

	/**
	 * Gets parameter names for a constructor by examining {@link KeyVariable} annotations.
	 * 
	 * <p>This implementation extracts parameter names from the {@link KeyVariable} annotation's
	 * name attribute. Parameters without this annotation will have null names in the returned array.
	 *
	 * @param ctor the constructor for which parameter names are requested
	 * @return an array of parameter names, with null entries for parameters without
	 *         the {@link KeyVariable} annotation, or null if the constructor has no parameters
	 */
	@Override
	@Nullable
	public String[] getParameterNames(Constructor<?> ctor) {
		return getParameterNames(ctor.getParameters());
	}

	/**
	 * Internal helper method that extracts parameter names from an array of {@link Parameter} objects.
	 * 
	 * <p>For each parameter, this method checks if it has a {@link KeyVariable} annotation.
	 * If present, the name from the annotation is used; otherwise, the corresponding entry
	 * in the returned array will be null.
	 *
	 * @param parameters the array of parameters to process
	 * @return an array of parameter names, with null entries for parameters without
	 *         the {@link KeyVariable} annotation, or null if the parameters array is empty
	 */
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