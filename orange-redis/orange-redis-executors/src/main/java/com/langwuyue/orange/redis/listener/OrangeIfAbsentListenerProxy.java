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
 * A utility class for creating dynamic proxies for Redis if-absent operation listeners.
 * 
 * <p>This class provides functionality to create proxy instances that wrap actual listener
 * implementations with filtering capabilities. The proxies intercept method calls and apply
 * key-based filtering logic before delegating to the actual listeners.
 * 
 * <p>The proxy mechanism allows for:
 * <ul>
 *   <li>Selective processing of Redis operations based on key patterns</li>
 *   <li>Centralized handling of key filtering logic</li>
 *   <li>Runtime adaptation of listeners without modifying their implementation</li>
 * </ul>
 *
 * @author Liang.Zhong
 * @since 1.0.0
 * @see OrangeIfAbsentListenerFilter
 * @see OrangeRedisOriginalKey
 */
public abstract class OrangeIfAbsentListenerProxy {
	
	/**
	 * Private constructor to prevent instantiation of this utility class.
	 */
	private OrangeIfAbsentListenerProxy() {}

	/**
	 * Creates proxy instances for a collection of Redis operation listeners.
	 * 
	 * <p>This method analyzes the interface methods to find parameters annotated with
	 * {@link OrangeRedisOriginalKey}, creates proxy instances for each target listener,
	 * and wraps them with filtering logic through {@link OrangeIfAbsentListenerFilter}.
	 *
	 *
	 * @param <T> the type of the listener interface
	 * @param targets the collection of listener implementations to be proxied
	 * @param interfaceClass the interface class that defines the listener methods
	 * @param prefix the prefix to be applied to all Redis keys
	 * @return a list of proxied listener instances with filtering capabilities
	 */
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