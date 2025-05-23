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
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.langwuyue.orange.redis.OrangeRedisException;
import com.langwuyue.orange.redis.annotation.KeyVariable;
import com.langwuyue.orange.redis.executor.OrangeRedisExecutor;
import com.langwuyue.orange.redis.executor.global.OrangeDeleteExecutor;
import com.langwuyue.orange.redis.executor.global.OrangeGetExpirationExecutor;
import com.langwuyue.orange.redis.executor.global.OrangeGetExpirationWithUnitArgsExecutor;
import com.langwuyue.orange.redis.executor.global.OrangeSetExpirationExecutor;
import com.langwuyue.orange.redis.listener.OrangeRedisMultipleSetIfAbsentListener;
import com.langwuyue.orange.redis.listener.OrangeRedisSetIfAbsentListener;
import com.langwuyue.orange.redis.logger.OrangeRedisLogger;
import com.langwuyue.orange.redis.operations.OrangeRedisOperations;
import com.langwuyue.orange.redis.operations.OrangeRedisScriptOperations;

/**
 * @author Liang.Zhong
 * @since 1.0.0
 */
public abstract class OrangeRedisAbstractExecutorsMapping implements OrangeRedisExecutorsMapping {
	
	private OrangeRedisExecutorIdGenerator generator;
	
	private List<OrangeRedisExecutor> executors;
	
	private Map<Long,OrangeRedisExecutor> executorsMap;
	
	private Map<Method,Method> extendMethodMap;
	
	private Map<Method,OrangeRedisExecutor> methodExecutorMap;
	
	private OrangeRedisLogger logger;
	
	protected OrangeRedisAbstractExecutorsMapping(
		OrangeRedisOperations operations,
		OrangeRedisExecutorIdGenerator generator,
		OrangeRedisScriptOperations scriptOperations,
		Collection<OrangeRedisSetIfAbsentListener> listeners,
		Collection<OrangeRedisMultipleSetIfAbsentListener> multipleListeners,
		OrangeRedisLogger logger
	) {
		this.generator = generator;
		this.executors = new ArrayList<>();
		this.executorsMap = new LinkedHashMap<>();
		this.methodExecutorMap = new LinkedHashMap<>();
		this.extendMethodMap = new LinkedHashMap<>();
		this.logger = logger;
		registerExecutors(executors,operations,generator,scriptOperations,listeners,multipleListeners,logger);
		buildExecutorsMap();
	}
	
	protected void registerExecutors(
		List<OrangeRedisExecutor> executors, 
		OrangeRedisOperations operations,
		OrangeRedisExecutorIdGenerator generator,
		OrangeRedisScriptOperations scriptOperations,
		Collection<OrangeRedisSetIfAbsentListener> listeners,
		Collection<OrangeRedisMultipleSetIfAbsentListener> multipleListeners,
		OrangeRedisLogger logger
	) {
		executors.add(new OrangeDeleteExecutor(operations,generator));
		executors.add(new OrangeSetExpirationExecutor(operations,generator));
		executors.add(new OrangeGetExpirationExecutor(operations,generator));
		executors.add(new OrangeGetExpirationWithUnitArgsExecutor(operations,generator));
	}
	
	protected void registerExecutors(OrangeRedisExecutor executor) {
		this.executors.add(executor);
		this.executorsMap.put(executor.getId(), executor);
	}
	
	@Override
	public Method getActualMethod(Method method) {
		Method actualMethod = this.extendMethodMap.get(method);
		if(actualMethod == null || actualMethod.equals(method)) {
			return method;
		}
		return getActualMethod(actualMethod);
	}
	

	@Override
	public OrangeRedisExecutorIdGenerator getExecutorIdGenerator() {
		return generator;
	}

	@Override
	public OrangeRedisExecutor getExecutor(Method method) {
		OrangeRedisExecutor executor = this.methodExecutorMap.get(method);
		if(executor != null) {
			return executor;
		}
		Annotation[] annotations = method.getAnnotations();
		if(annotations == null || annotations.length == 0) {
			return findOrangeRedisExecutorByParentInterfaces(method);
		}
		long id = this.generator.generate(getAnnotationClasses(annotations,method));
		executor = this.executorsMap.get(id);
		if(executor == null) {
			throw new OrangeRedisException(String.format("Operation [%s] not support! Please compare with operation template %s", method, getTemplateClass()));
		}
		this.methodExecutorMap.put(method, executor);
		this.extendMethodMap.put(method, method);
		return executor;
	}
	
	protected OrangeRedisExecutor findOrangeRedisExecutorByParentInterfaces(Method method) {
		Class[] interfaces = method.getDeclaringClass().getInterfaces();
		OrangeRedisExecutor executor = null;
		for(Class interfaceClass : interfaces) {
			try {
				Method superMethod = interfaceClass.getMethod(method.getName(), method.getParameterTypes());
				executor = getExecutor(superMethod);
				if(executor == null) {
					continue;
				}
				this.methodExecutorMap.put(method, executor);
				this.extendMethodMap.put(method, superMethod);
				return executor;
			}catch (Exception e) {
				if(!(e instanceof NoSuchMethodException)) {
					this.logger.warn("Parse Redis operation error", e);
					continue;
				}
				Method[] methods = method.getDeclaringClass().getDeclaredMethods();
				for(Method m : methods) {
					if(!m.isBridge()) {
						continue;
					}
					if(m.equals(method)) {
						continue;
					}
					if(!m.getName().equals(method.getName())) {
						continue;
					}
					if(m.getParameterCount() != method.getParameterCount()) {
						continue;
					}
					Class[] mTypes = m.getParameterTypes();
					Class[] methodTypes = method.getParameterTypes();
					boolean isOverride = true;
					for (int i = 0; i < mTypes.length; i++) {
						if(!mTypes[i].isAssignableFrom(methodTypes[i])) {
							isOverride = false;
							break;
						}
					}
					if(isOverride) {
						executor = getExecutor(m);
						if(executor == null) {
							continue;
						}
						this.methodExecutorMap.put(method, executor);
						this.extendMethodMap.put(method, m);
						return executor;
					}
				}
			}
		}		
		throw new OrangeRedisException(String.format("Operation not support! Please compare with operation template %s", getTemplateClass()));
	}
	
	protected List<Class<? extends Annotation>> getAnnotationClasses(Annotation[] annotations,Method method) {
		List<Class<? extends Annotation>> annotationClasses = new ArrayList<>();
		for(Annotation annotation : annotations) {
			annotationClasses.add(annotation.annotationType());
		}
		Annotation[][] parametersAnnotations = method.getParameterAnnotations();
		if(parametersAnnotations != null && parametersAnnotations.length != 0) {
			for(Annotation[] parameterAnnotations : parametersAnnotations) {
				for(Annotation parameterAnnotation : parameterAnnotations) {
					if(parameterAnnotation.annotationType() == KeyVariable.class) {
						continue;
					}
					annotationClasses.add(parameterAnnotation.annotationType());
				}
			}
		}
		return annotationClasses;
	}

	protected abstract Class<?> getTemplateClass();

	private void buildExecutorsMap() {
		for(OrangeRedisExecutor executor : executors) {
			this.executorsMap.put(executor.getId(), executor);
		}
	}

	@Override
	public OrangeRedisLogger getLogger() {
		return logger;
	}
}
