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

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.context.expression.MapAccessor;
import org.springframework.core.ParameterNameDiscoverer;
import org.springframework.expression.PropertyAccessor;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.util.ObjectUtils;

/**
 * A specialized evaluation context for processing annotations in Orange Redis operations.
 * 
 * <p>This context extends Spring's {@link StandardEvaluationContext} to provide enhanced
 * support for evaluating expressions within Redis annotations. It automatically handles
 * method parameters and their values, making them available for expression evaluation.
 * 
 * <p>Key features:
 * <ul>
 *   <li>Automatically loads method arguments into the evaluation context
 *   <li>Supports both named parameters and varargs
 *   <li>Integrates with {@link MapAccessor} for enhanced property access
 *   <li>Maintains parameter name discovery through {@link ParameterNameDiscoverer}
 * </ul>
 * 
 * @author Liang.Zhong
 * @since 1.0.0
 * @see StandardEvaluationContext
 * @see ParameterNameDiscoverer
 * @see MapAccessor
 */
public class OrangeAnnotationBaseEvaluationContext extends StandardEvaluationContext {
	
	/**
	 * The method whose parameters are being evaluated in this context.
	 */
	private final Method method;

	/**
	 * The argument values passed to the method during invocation.
	 */
	private final Object[] arguments;

	/**
	 * The discoverer used to determine parameter names from the method.
	 */
	private final ParameterNameDiscoverer parameterNameDiscoverer;
	
	/**
	 * Creates a new evaluation context for the specified method and its arguments.
	 * 
	 * <p>This constructor initializes the context with the method parameters and their values,
	 * making them available for expression evaluation. It also adds a {@link MapAccessor}
	 * to the property accessors to enhance property access capabilities.
	 *
	 * @param method the method whose parameters should be included in the context
	 * @param arguments the argument values passed to the method during invocation
	 * @param parameterNameDiscoverer the discoverer used to determine parameter names from the method
	 */
	public OrangeAnnotationBaseEvaluationContext(Method method, 
			Object[] arguments,
			ParameterNameDiscoverer parameterNameDiscoverer
	) {
		super(new HashMap<>());
		this.method = method;
		this.arguments = arguments;
		this.parameterNameDiscoverer = parameterNameDiscoverer;
		List<PropertyAccessor> accessors = getPropertyAccessors();
		accessors.add(new MapAccessor());
		this.setPropertyAccessors(accessors);
		loadArguments();
	}

	/**
	 * Loads method arguments into the evaluation context.
	 * 
	 * <p>This method processes the arguments passed to the method and makes them
	 * available in the evaluation context. It handles both named parameters and varargs.
	 * If parameter names are available through the {@link ParameterNameDiscoverer},
	 * arguments are also exposed as named variables.
	 * 
	 * <p>Special handling is provided for varargs, where the last parameter receives
	 * all remaining arguments as an array if there are more arguments than parameters.
	 */
	protected void loadArguments() {
		// Shortcut if no args need to be loaded
		if (ObjectUtils.isEmpty(this.arguments)) {
			return;
		}

		// Expose indexed variables as well as parameter names (if discoverable)
		String[] paramNames = this.parameterNameDiscoverer.getParameterNames(this.method);
		int paramCount = (paramNames != null ? paramNames.length : this.method.getParameterCount());
		int argsCount = this.arguments.length;

		for (int i = 0; i < paramCount; i++) {
			Object value = null;
			if (argsCount > paramCount && i == paramCount - 1) {
				// Expose remaining arguments as vararg array for last parameter
				value = Arrays.copyOfRange(this.arguments, i, argsCount);
			}
			else if (argsCount > i) {
				// Actual argument found - otherwise left as null
				value = this.arguments[i];
			}
			if (paramNames != null && paramNames[i] != null) {
				setVariable(paramNames[i], value);
				Map map = (Map)(this.getRootObject().getValue());
				if(map == null) {
					continue;
				}
				map.put(paramNames[i], value);
			}
		}
	}
	
}