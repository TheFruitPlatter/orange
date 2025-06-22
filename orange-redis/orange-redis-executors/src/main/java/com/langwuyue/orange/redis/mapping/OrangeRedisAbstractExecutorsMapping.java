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
 * Abstract base implementation of the {@link OrangeRedisExecutorsMapping} interface.
 * This class provides core functionality for mapping Redis executors to methods,
 * including executor registration, method resolution, and executor lookup.
 * 
 * <p>This abstract class handles:
 * <ul>
 *   <li>Registration of common Redis executors</li>
 *   <li>Mapping between methods and their corresponding executors</li>
 *   <li>Resolution of actual methods through inheritance hierarchies</li>
 *   <li>Generation of executor IDs based on method annotations</li>
 * </ul>
 * 
 * <p>Subclasses must implement the {@link #getTemplateClass()} method to specify
 * the template class for operation comparison.
 *
 * @author Liang.Zhong
 * @since 1.0.0
 */
public abstract class OrangeRedisAbstractExecutorsMapping implements OrangeRedisExecutorsMapping {
	
	/**
	 * Generator for creating unique executor IDs based on method annotations.
	 */
	private OrangeRedisExecutorIdGenerator generator;
	
	/**
	 * List of all registered Redis executors.
	 */
	private List<OrangeRedisExecutor> executors;
	
	/**
	 * Map of executor IDs to their corresponding executor instances.
	 */
	private Map<Long,OrangeRedisExecutor> executorsMap;
	
	/**
	 * Map of methods to their actual implementation methods, used for method resolution
	 * in inheritance hierarchies.
	 */
	private Map<Method,Method> extendMethodMap;
	
	/**
	 * Cache mapping methods to their corresponding executors for faster lookup.
	 */
	private Map<Method,OrangeRedisExecutor> methodExecutorMap;
	
	/**
	 * Logger for recording operations and errors.
	 */
	private OrangeRedisLogger logger;
	
	/**
	 * Constructs a new abstract executors mapping with the specified components.
	 * Initializes internal data structures and registers common executors.
	 *
	 * @param operations the Redis operations to use for executing commands
	 * @param generator the executor ID generator to use for creating unique IDs
	 * @param scriptOperations the script operations for executing Redis scripts
	 * @param listeners collection of listeners for set-if-absent operations
	 * @param multipleListeners collection of listeners for multiple set-if-absent operations
	 * @param logger the logger to use for recording operations and errors
	 */
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
	
	/**
	 * Registers the common Redis executors with the provided components.
	 * This method initializes the basic set of executors including delete, expiration setting,
	 * and expiration retrieval executors.
	 *
	 * @param executors list to store the registered executors
	 * @param operations Redis operations for executing commands
	 * @param generator executor ID generator for creating unique IDs
	 * @param scriptOperations script operations for executing Redis scripts
	 * @param listeners collection of listeners for set-if-absent operations
	 * @param multipleListeners collection of listeners for multiple set-if-absent operations
	 * @param logger logger for recording operations and errors
	 */
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
	
	/**
	 * Registers a single Redis executor.
	 * This method adds the executor to the internal collections and maps it by its ID.
	 *
	 * @param executor the executor to register
	 */
	protected void registerExecutors(OrangeRedisExecutor executor) {
		this.executors.add(executor);
		this.executorsMap.put(executor.getId(), executor);
	}
	
	/**
	 * Resolves the actual method implementation through the inheritance hierarchy.
	 * This method recursively traverses the method mapping to find the actual implementation
	 * method that should be used for executor lookup.
	 *
	 * @param method the method to resolve
	 * @return the actual implementation method
	 */
	@Override
	public Method getActualMethod(Method method) {
		Method actualMethod = this.extendMethodMap.get(method);
		if(actualMethod == null || actualMethod.equals(method)) {
			return method;
		}
		return getActualMethod(actualMethod);
	}
	

	/**
	 * Returns the executor ID generator used by this mapping.
	 * The generator is responsible for creating unique IDs for executors based on method annotations.
	 *
	 * @return the executor ID generator instance
	 */
	@Override
	public OrangeRedisExecutorIdGenerator getExecutorIdGenerator() {
		return generator;
	}

	/**
	 * Retrieves the Redis executor associated with the given method.
	 * This method first checks the method-executor cache. If no executor is found,
	 * it examines the method's annotations to generate an executor ID and looks up
	 * the corresponding executor. If the method has no annotations, it attempts to
	 * find an executor through the parent interfaces.
	 *
	 * <p>The method-executor mapping is cached for future lookups once found.
	 *
	 * @param method the method to find an executor for
	 * @return the associated Redis executor
	 * @throws OrangeRedisException if no suitable executor is found for the method
	 */
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
	
	/**
	 * Finds a Redis executor by examining the parent interfaces of the method's declaring class.
	 * This method is used when a method has no direct annotations but might inherit behavior
	 * from parent interfaces or bridge methods.
	 * 
	 * <p>The algorithm works by:
	 * <ol>
	 *   <li>Checking all interfaces implemented by the method's declaring class</li>
	 *   <li>Looking for matching methods in those interfaces</li>
	 *   <li>If not found, examining bridge methods for potential matches</li>
	 * </ol>
	 *
	 * @param method the method to find an executor for
	 * @return the associated Redis executor from parent interfaces
	 * @throws OrangeRedisException if no suitable executor is found in any parent interface
	 */
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
	
	/**
	 * Extracts annotation classes from both method-level and parameter-level annotations.
	 * This method collects all annotation types from the method itself and its parameters,
	 * excluding {@link KeyVariable} annotations which are handled separately.
	 *
	 * <p>The collected annotation classes are used to generate a unique executor ID
	 * that identifies which Redis executor should handle the method.
	 *
	 * @param annotations array of method-level annotations
	 * @param method the method whose annotations are being processed
	 * @return list of annotation classes found on the method and its parameters
	 */
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

	/**
	 * Returns the template class used for operation comparison.
	 * This method must be implemented by subclasses to specify which class
	 * should be used as a reference when comparing operations.
	 * 
	 * <p>The template class is used in error messages when an operation
	 * is not supported, providing users with information about which class
	 * they should compare their operations against.
	 *
	 * @return the class to use as a template for operation comparison
	 */
	protected abstract Class<?> getTemplateClass();

	/**
	 * Builds the internal map of executor IDs to executor instances.
	 * This method populates the executorsMap by iterating through all registered
	 * executors and mapping them by their unique IDs.
	 * 
	 * <p>This method is called during initialization to prepare the mapping
	 * for efficient executor lookups by ID.
	 */
	private void buildExecutorsMap() {
		for(OrangeRedisExecutor executor : executors) {
			this.executorsMap.put(executor.getId(), executor);
		}
	}

	/**
	 * Returns the logger instance used by this mapping.
	 * This logger is used for recording operations, warnings, and errors
	 * that occur during executor mapping and method resolution.
	 *
	 * @return the OrangeRedisLogger instance used by this mapping
	 */
	@Override
	public OrangeRedisLogger getLogger() {
		return logger;
	}
}