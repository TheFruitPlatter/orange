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

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import com.langwuyue.orange.redis.OrangeRedisException;
import com.langwuyue.orange.redis.annotation.OrangeRedisIfAbsentListener;
import com.langwuyue.orange.redis.annotation.OrangeRedisKey;

/**
 * @author Liang.Zhong
 * @since 1.0.0
 */
public class OrangeIfAbsentListenerFilter implements InvocationHandler {
	
	private Object target;
	
	private Set<String> keys;
	
	private Map<Method,Integer> originalKeyIndexMap;
	
	public OrangeIfAbsentListenerFilter(Object target, Map<Method,Integer> originalKeyIndexMap,String keyPrefix) {
		super();
		this.target = target;
		this.originalKeyIndexMap = originalKeyIndexMap;
		this.keys = new LinkedHashSet<>();
		Class<?> targetClass = target.getClass();
		OrangeRedisIfAbsentListener listener = targetClass.getAnnotation(OrangeRedisIfAbsentListener.class);
		Class<?>[] keysClasses = listener.keys();
		if(keysClasses != null) {
			for(Class<?> keyClass : keysClasses) {
				OrangeRedisKey redisKey = keyClass.getAnnotation(OrangeRedisKey.class);
				if(redisKey == null) {
					throw new OrangeRedisException(String.format("@%s not found on the class %s, please make sure the listener %s is correct.", OrangeRedisKey.class,keyClass,targetClass));
				}
				this.keys.add(keyPrefix + redisKey.key());
			}	
		}
	}

	@Override
	public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
		Class<?> declaringClass = method.getDeclaringClass();
		if(Object.class.equals(declaringClass)) {
			return method.invoke(this, args);
		}
		Integer originalKeyParameterIndex = this.originalKeyIndexMap.get(method);
		if(originalKeyParameterIndex == null) {
			// Just in case
			return method.invoke(target, args);
		}
		String originalKey = (String) args[originalKeyParameterIndex];
		if(!keys.isEmpty()) {
			if(keys.contains(originalKey)) {
				return method.invoke(target, args);
			}
			return null;
		}
		// Filter all keys
		return method.invoke(target, args);
	}

	public static boolean isInvalid(Class<?> targetClass) {
		return targetClass.isAnnotationPresent(OrangeRedisIfAbsentListener.class);
	}
}
