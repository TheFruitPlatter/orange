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
package com.langwuyue.orange.redis.listener;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.langwuyue.orange.redis.annotation.OrangeRedisOriginalKey;

/**
 * @author Liang.Zhong
 * @since 1.0.0
 */
public abstract class OrangeIfAbsentListenerProxy {
	
	private OrangeIfAbsentListenerProxy() {}

	public static <T> List<T> proxy(Collection<? extends T> targets, Class<T> interfaceClass,String prefix){
		List<T> listeners = new ArrayList<>();
		Method[] methods = interfaceClass.getMethods();
		Map<Method,Integer> originalKeyIndexMap = new LinkedHashMap<>();
		for(Method method : methods) {
			Parameter[] parameters = method.getParameters();
			if(parameters == null || parameters.length == 0) {
				continue;
			}
			int len = parameters.length;
			for (int i = 0; i < len; i++) {
				if(parameters[i].isAnnotationPresent(OrangeRedisOriginalKey.class)) {
					originalKeyIndexMap.put(method, i);
					break;
				}
			}
		}
		for(Object target : targets) {
			Class<?> targetClass = target.getClass();
			if(!OrangeIfAbsentListenerFilter.isInvalid(targetClass)) {
				continue;
			}
			T listener = (T) Proxy.newProxyInstance(
				targetClass.getClassLoader(), 
				new Class[] {interfaceClass}, 
				new OrangeIfAbsentListenerFilter(target, originalKeyIndexMap,prefix)
			);
			listeners.add(listener);
		}
		return listeners;
	}
}
