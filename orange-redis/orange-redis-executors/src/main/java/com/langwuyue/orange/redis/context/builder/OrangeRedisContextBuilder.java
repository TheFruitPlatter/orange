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
package com.langwuyue.orange.redis.context.builder;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;

import com.langwuyue.orange.redis.RedisValueTypeEnum;
import com.langwuyue.orange.redis.annotation.KeyVariable;
import com.langwuyue.orange.redis.context.OrangeRedisContext;
import com.langwuyue.orange.redis.context.OrangeRedisContext.Key;

/**
 * A builder class for creating Redis operation context objects.
 * 
 * <p>This builder follows the builder pattern to construct various types of
 * {@link OrangeRedisContext} objects. It handles the complex initialization process
 * including method parameter processing, annotation handling, and field value binding.
 * 
 * <p>Example usage:
 * <pre>{@code
 * OrangeRedisContext context = new OrangeRedisContextBuilder()
 *     .operationOwner(MyRedisOperations.class)
 *     .operationMethod(method)
 *     .args(new Object[]{"key", "value"})
 *     .redisKey(new Key("myKey"))
 *     .valueType(RedisValueTypeEnum.STRING)
 *     .contextClass(OrangeRedisValueContext.class)
 *     .build();
 * }</pre>
 *
 * @author Liang.Zhong
 * @since 1.0.0
 * @see OrangeRedisContext
 * @see OrangeOperationArgHandlerMapping
 */
public class OrangeRedisContextBuilder {
	
	/**
	 * The class that owns the Redis operation method.
	 * This is typically the interface or class that defines the Redis operations.
	 */
	private Class<?> operationOwner;
	
	/**
	 * The method representing the Redis operation.
	 * This is the method that will be invoked to perform the Redis operation.
	 */
	private Method operationMethod;
	
	/**
	 * The actual method being called, which may be different from the operation method
	 * in case of proxy-based invocation or method overriding.
	 */
	private Method actualMethod;
	
	/**
	 * The arguments to be passed to the operation method.
	 */
	private Object[] args;
	
	/**
	 * The mapping that handles operation arguments and their annotations.
	 * This mapping is used to bind method parameters to context fields based on annotations.
	 */
	private OrangeOperationArgHandlerMapping operationArgHandlerMapping;
	
	/**
	 * The Redis key information for the operation.
	 */
	private Key redisKey;
	
	/**
	 * The type of Redis value being operated on (e.g., STRING, LIST, HASH).
	 */
	private RedisValueTypeEnum valueType;
	
	/**
	 * The specific context class to be instantiated.
	 * This determines the type of Redis operation context that will be created.
	 */
	private Class<? extends OrangeRedisContext> contextClass;
	
	/**
	 * Constructs a new Redis context builder with default settings.
	 * 
	 * <p>After creating the builder, you need to set the required properties
	 * using the builder methods before calling {@link #build()}.
	 */
	public OrangeRedisContextBuilder() {}
	
	/**
	 * Sets the class that owns the Redis operation method.
	 *
	 * @param operationOwner the class or interface that defines the Redis operations
	 * @return this builder instance for method chaining
	 */
	public OrangeRedisContextBuilder operationOwner(Class<?> operationOwner) {
		this.operationOwner = operationOwner;
		return this;
	}
	
	/**
	 * Sets the method representing the Redis operation.
	 *
	 * @param operationMethod the method that will be invoked to perform the Redis operation
	 * @return this builder instance for method chaining
	 */
	public OrangeRedisContextBuilder operationMethod(Method operationMethod) {
		this.operationMethod = operationMethod;
		return this;
	}
	
	/**
	 * Sets the actual method being called, which may be different from the operation method
	 * in case of proxy-based invocation or method overriding.
	 *
	 * @param actualMethod the actual method being invoked
	 * @return this builder instance for method chaining
	 */
	public OrangeRedisContextBuilder actualMethod(Method actualMethod) {
		this.actualMethod = actualMethod;
		return this;
	}
	
	/**
	 * Sets the arguments to be passed to the operation method.
	 *
	 * @param args the array of arguments for the operation method
	 * @return this builder instance for method chaining
	 */
	public OrangeRedisContextBuilder args(Object[] args) {
		this.args = args;
		return this;
	}

	/**
	 * Sets the Redis key information for the operation.
	 *
	 * @param redisKey the key object containing Redis key information
	 * @return this builder instance for method chaining
	 */
	public OrangeRedisContextBuilder redisKey(Key redisKey) {
		this.redisKey = redisKey;
		return this;
	}

	/**
	 * Sets the type of Redis value being operated on.
	 *
	 * @param valueType the enum representing the Redis value type (e.g., STRING, LIST, HASH)
	 * @return this builder instance for method chaining
	 */
	public OrangeRedisContextBuilder valueType(RedisValueTypeEnum valueType) {
		this.valueType = valueType;
		return this;
	}

	/**
	 * Sets the specific context class to be instantiated.
	 *
	 * @param contextClass the class of Redis operation context to create
	 * @return this builder instance for method chaining
	 */
	public OrangeRedisContextBuilder contextClass(Class<? extends OrangeRedisContext> contextClass) {
		this.contextClass = contextClass;
		return this;
	}

	/**
	 * Sets the mapping that handles operation arguments and their annotations.
	 *
	 * @param operationArgHandlerMapping the mapping used to bind method parameters to context fields
	 * @return this builder instance for method chaining
	 */
	public OrangeRedisContextBuilder operationArgHandlerMapping(OrangeOperationArgHandlerMapping operationArgHandlerMapping) {
		this.operationArgHandlerMapping = operationArgHandlerMapping;
		return this;
	}
	
	/**
	 * Builds and returns a new Redis operation context instance.
	 * 
	 * <p>This method creates a new context instance and processes all annotations
	 * on both the method level and parameter level. It performs the following steps:
	 * <ol>
	 *   <li>Creates a new context instance using {@link #newContext()}</li>
	 *   <li>Processes method-level annotations and binds them to context fields</li>
	 *   <li>Processes parameter-level annotations and binds them to context fields</li>
	 * </ol>
	 *
	 * @return a fully initialized Redis operation context
	 * @throws Exception if any error occurs during context creation or annotation processing
	 */
	public OrangeRedisContext build() throws Exception {
		OrangeRedisContext context = newContext();
		Annotation[] annotations = this.actualMethod.getAnnotations();
		Map<Class<? extends Annotation>, List<Field>> annotationFieldMap = this.operationArgHandlerMapping.getContextFieldsMap().get(this.contextClass);
		Map<Field, OrangeOperationArgHandler> valueHandlerMap = this.operationArgHandlerMapping.getFieldValueHandlerMap();
		for(Annotation annotation : annotations) {
			if(annotationFieldMap == null) {
				continue;
			}
			List<Field> fields = annotationFieldMap.get(annotation.annotationType());
			if(fields == null || fields.isEmpty()) {
				continue;
			}
			for(Field field : fields) {
				valueHandlerMap.get(field).setContextValueByAnnotation(context,annotation,field);	
			}
			
		}
		Annotation[][] parametersAnnotations = this.actualMethod.getParameterAnnotations();
		int len = parametersAnnotations.length;
		for(int i = 0; i < len; i++) {
			Annotation[] parameterAnnotations = parametersAnnotations[i];
			for(Annotation annotation : parameterAnnotations) {
				copyValueToContext(annotation,context,len, i,valueHandlerMap,annotationFieldMap);
			}
		}
		return context;
	}
	
	/**
	 * Creates a new instance of the Redis context using the specified context class.
	 * 
	 * <p>This method delegates the context creation to {@link OrangeRedisContext#newInstance},
	 * passing all the necessary parameters to initialize the context properly.
	 *
	 * @return a new instance of the context class with basic properties set
	 * @throws Exception if the context cannot be instantiated or initialized
	 * @see OrangeRedisContext#newInstance
	 */
	protected OrangeRedisContext newContext() throws Exception {
		return OrangeRedisContext.newInstance(
				this.contextClass,
				this.operationOwner,
				this.operationMethod,
				this.args,
				this.redisKey,
				this.valueType
		);
	}
	
	/**
	 * Processes a single annotation on a method parameter and binds its value to the context.
	 * 
	 * <p>This method handles the binding of parameter annotations to context fields by:
	 * <ol>
	 *   <li>Skipping {@link KeyVariable} annotations as they are handled separately</li>
	 *   <li>Finding fields in the context that are annotated with the same annotation type</li>
	 *   <li>Using the appropriate value handler to bind the parameter value to the field</li>
	 * </ol>
	 *
	 * @param annotation the annotation on the method parameter
	 * @param context the Redis operation context to update
	 * @param parameterCount the total number of parameters in the method
	 * @param parameterIndex the index of the current parameter being processed
	 * @param valueHandlerMap mapping of fields to their value handlers
	 * @param annotationFieldMap mapping of annotation types to context fields
	 */
	private void copyValueToContext(
			Annotation annotation,
			OrangeRedisContext context,
			int parameterCount,
			int parameterIndex,
			Map<Field, OrangeOperationArgHandler> valueHandlerMap,
			Map<Class<? extends Annotation>, List<Field>> annotationFieldMap
	) {
		if(annotation.annotationType() == KeyVariable.class) {
			return;
		}
		List<Field> fields = annotationFieldMap.get(annotation.annotationType());
		if(fields == null || fields.isEmpty()) {
			return;
		}
		for(Field field : fields) {
			valueHandlerMap.get(field).bind(field, context, annotation,parameterCount, parameterIndex, this.args);	
		}
	}

	/**
	 * Gets the class that owns the Redis operation method.
	 *
	 * @return the class or interface that defines the Redis operations
	 */
	protected Class<?> getOperationOwner() {
		return operationOwner;
	}

	/**
	 * Gets the method representing the Redis operation.
	 *
	 * @return the method that will be invoked to perform the Redis operation
	 */
	protected Method getOperationMethod() {
		return operationMethod;
	}

	/**
	 * Gets the actual method being called.
	 *
	 * @return the actual method being invoked, which may differ from operation method
	 */
	protected Method getActualMethod() {
		return actualMethod;
	}

	/**
	 * Gets the arguments to be passed to the operation method.
	 *
	 * @return the array of arguments for the operation method
	 */
	protected Object[] getArgs() {
		return args;
	}

	/**
	 * Gets the Redis key information for the operation.
	 *
	 * @return the key object containing Redis key information
	 */
	protected Key getRedisKey() {
		return redisKey;
	}

	/**
	 * Gets the type of Redis value being operated on.
	 *
	 * @return the enum representing the Redis value type
	 */
	protected RedisValueTypeEnum getValueType() {
		return valueType;
	}

	/**
	 * Gets the specific context class to be instantiated.
	 *
	 * @return the class of Redis operation context to create
	 */
	protected Class<? extends OrangeRedisContext> getContextClass() {
		return contextClass;
	}

	/**
	 * Gets the mapping that handles operation arguments and their annotations.
	 *
	 * @return the mapping used to bind method parameters to context fields
	 */
	protected OrangeOperationArgHandlerMapping getOperationArgHandlerMapping() {
		return operationArgHandlerMapping;
	}
	
}