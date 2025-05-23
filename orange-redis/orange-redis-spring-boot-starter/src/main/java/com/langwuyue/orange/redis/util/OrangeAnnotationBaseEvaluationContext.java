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
 * @author Liang.Zhong
 * @since 1.0.0
 */
public class OrangeAnnotationBaseEvaluationContext extends StandardEvaluationContext {
	
	private final Method method;

	private final Object[] arguments;

	private final ParameterNameDiscoverer parameterNameDiscoverer;
	
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
