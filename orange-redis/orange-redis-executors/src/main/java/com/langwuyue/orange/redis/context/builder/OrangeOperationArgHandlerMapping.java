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
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.langwuyue.orange.redis.OrangeRedisException;
import com.langwuyue.orange.redis.annotation.OrangeRedisOperationArg;
import com.langwuyue.orange.redis.context.OrangeRedisContext;
import com.langwuyue.orange.redis.executor.OrangeRedisExecutor;
import com.langwuyue.orange.redis.mapping.OrangeRedisExecutorsMapping;

/**
 * Maps Redis operation argument handlers to their corresponding context fields.
 * 
 * <p>This class is responsible for scanning context classes to identify fields annotated with
 * {@link OrangeRedisOperationArg} and mapping them to their appropriate handlers. It builds
 * a comprehensive mapping structure that connects:
 * <ul>
 *   <li>Context classes to their annotated fields</li>
 *   <li>Annotation types to the fields they should bind to</li>
 *   <li>Fields to their value handlers</li>
 * </ul>
 * 
 * <p>The mapping process involves:
 * <ol>
 *   <li>Scanning all methods in the operation owner class</li>
 *   <li>For each method, retrieving its associated Redis executor</li>
 *   <li>Examining the executor's context class for annotated fields</li>
 *   <li>Building mappings between annotations, fields, and handlers</li>
 * </ol>
 * 
 * <p>This mapping is essential for the dynamic binding of method parameters and annotations
 * to their corresponding fields in the Redis context during operation execution.
 *
 * @author Liang.Zhong
 * @since 1.0.0
 * @see OrangeRedisOperationArg
 * @see OrangeOperationArgHandler
 * @see OrangeRedisContext
 */
public class OrangeOperationArgHandlerMapping {
	
	/** Mapping of methods to their Redis executors */
	private OrangeRedisExecutorsMapping executorsMapping;
	
	/** Class containing Redis operations to be mapped */
	private Class operationOwner;
	
	/**
	 * Multi-level mapping structure:
	 * - Context class → Annotation type → List of annotated fields
	 * Used to track which fields in each context class are bound to which annotations
	 */
	private Map<Class<? extends OrangeRedisContext>, Map<Class<? extends Annotation>, List<Field>>> contextFieldsMap;
	
	/**
	 * Maps fields to their value handlers
	 * Used to determine which handler should process a given field's value
	 */
	private Map<Field, OrangeOperationArgHandler> fieldValueHandlerMap;
	
	/**
	 * Registry of available handler implementations indexed by their class
	 * Used to look up handler instances when processing fields
	 */
	private Map<Class<? extends OrangeOperationArgHandler>, OrangeOperationArgHandler> valueHandlerMap;
	
	/**
	 * Creates a new mapping for Redis operation argument handlers.
	 *
	 * @param executorsMapping mapping of methods to their Redis executors
	 * @param operationOwner class containing the Redis operations to be mapped
	 * @param valueHandlerMap registry of available handler implementations
	 */
	public OrangeOperationArgHandlerMapping(
		OrangeRedisExecutorsMapping executorsMapping, 
		Class operationOwner,
		Map<Class<? extends OrangeOperationArgHandler>, OrangeOperationArgHandler> valueHandlerMap
	) {
		super();
		this.executorsMapping = executorsMapping;
		this.operationOwner = operationOwner;
		this.valueHandlerMap = valueHandlerMap;
		this.contextFieldsMap = new LinkedHashMap<>();
		this.fieldValueHandlerMap = new LinkedHashMap<>();
	}


	/**
	 * Builds the complete mapping structure for all Redis operations in the owner class.
	 * 
	 * <p>This method performs the following steps:
	 * <ol>
	 *   <li>Retrieves all methods from the operation owner class</li>
	 *   <li>For each method:
	 *     <ul>
	 *       <li>Gets its Redis executor</li>
	 *       <li>Retrieves the context class from the executor</li>
	 *       <li>If the context class hasn't been processed yet:
	 *         <ul>
	 *           <li>Creates a new annotation-to-fields mapping</li>
	 *           <li>Scans the context class fields for annotations</li>
	 *           <li>Stores the mapping if any annotated fields were found</li>
	 *         </ul>
	 *       </li>
	 *     </ul>
	 *   </li>
	 * </ol>
	 * 
	 * @throws OrangeRedisException if an error occurs during the mapping process
	 */
	public void buildMapping() {
		Method[] methods = this.operationOwner.getMethods();
		for(Method method : methods) {
			try {
				OrangeRedisExecutor executor = getOrangeRedisExecutor(method);
				Class<? extends OrangeRedisContext> contextClass = executor.getContextClass();
				Map<Class<? extends Annotation>, List<Field>> annotationFieldMap = this.contextFieldsMap.get(contextClass);
				if(annotationFieldMap != null) {
					continue;
				}
				annotationFieldMap = new LinkedHashMap<>();
				fieldsScan(contextClass,annotationFieldMap);
				if(!annotationFieldMap.isEmpty()) {
					this.contextFieldsMap.put(contextClass, annotationFieldMap);
				}
			}catch (Exception e) {
				throw new OrangeRedisException(String.format("Execution Error! %n Operation Owner: %s %n Operation : %s", this.operationOwner, method),e);
			}
		}
	}
	
	/**
	 * Retrieves the Redis executor associated with the given method.
	 * 
	 * <p>This method delegates to the {@link OrangeRedisExecutorsMapping} to find
	 * the appropriate executor for the specified method. The executor contains
	 * information about the context class and other execution details.
	 *
	 * @param method the method for which to retrieve the executor
	 * @return the Redis executor for the method, or null if no executor is found
	 * @see OrangeRedisExecutorsMapping#getExecutor(Method)
	 */
	protected OrangeRedisExecutor getOrangeRedisExecutor(Method method) {
		return this.executorsMapping.getExecutor(method);
	}
	
	/**
	 * Recursively scans fields in the context class and its superclasses for Redis operation annotations.
	 * 
	 * <p>This method performs the following operations:
	 * <ol>
	 *   <li>Scans all declared fields in the current context class</li>
	 *   <li>For each field with {@link OrangeRedisOperationArg} annotation:
	 *     <ul>
	 *       <li>Maps the field to its value handler</li>
	 *       <li>Associates the field with its binding annotation type</li>
	 *     </ul>
	 *   </li>
	 *   <li>If the superclass is a {@link OrangeRedisContext}, recursively scans it</li>
	 * </ol>
	 *
	 * <p>This method handles the inheritance hierarchy by recursively processing
	 * parent classes that extend {@link OrangeRedisContext}, ensuring all relevant
	 * fields are properly mapped.
	 *
	 * @param contextClass the context class to scan for annotated fields
	 * @param annotationFieldMap the map to populate with annotation-to-fields mappings
	 */
	private void fieldsScan(Class<? extends OrangeRedisContext> contextClass, Map<Class<? extends Annotation>, List<Field>> annotationFieldMap) {
		Field[] fields = contextClass.getDeclaredFields();
		for(Field field : fields) {
			OrangeRedisOperationArg value = field.getAnnotation(OrangeRedisOperationArg.class);
			if(value != null) {
				this.fieldValueHandlerMap.put(field, this.valueHandlerMap.get(value.valueHandler()));
				List<Field> annotationBindingFields = annotationFieldMap.get(value.binding());
				if(annotationBindingFields == null) {
					annotationBindingFields = new ArrayList<>();
					annotationFieldMap.put(value.binding(), annotationBindingFields);
				}
				annotationBindingFields.add(field);
			}
		}
		Class<?> superClass = contextClass.getSuperclass();
		if(OrangeRedisContext.class.isAssignableFrom(superClass)) {
			fieldsScan((Class<? extends OrangeRedisContext>)superClass, annotationFieldMap);
		}
	}


	/**
	 * Returns the mapping of methods to their Redis executors.
	 *
	 * @return the executors mapping
	 */
	public OrangeRedisExecutorsMapping getExecutorsMapping() {
		return executorsMapping;
	}

	/**
	 * Returns the class containing Redis operations that were mapped.
	 *
	 * @return the operation owner class
	 */
	public Class getOperationOwner() {
		return operationOwner;
	}

	/**
	 * Returns the multi-level mapping structure that connects context classes to their
	 * annotated fields, organized by annotation type.
	 * 
	 * <p>The structure is:
	 * <ul>
	 *   <li>Context class → Annotation type → List of annotated fields</li>
	 * </ul>
	 *
	 * @return the context fields mapping
	 */
	public Map<Class<? extends OrangeRedisContext>, Map<Class<? extends Annotation>, List<Field>>> getContextFieldsMap() {
		return contextFieldsMap;
	}

	/**
	 * Returns the mapping of fields to their value handlers.
	 * 
	 * <p>This map is used to determine which handler should process a given field's value
	 * during Redis operation execution.
	 *
	 * @return the field-to-handler mapping
	 */
	public Map<Field, OrangeOperationArgHandler> getFieldValueHandlerMap() {
		return fieldValueHandlerMap;
	}

	/**
	 * Returns the registry of available handler implementations indexed by their class.
	 * 
	 * <p>This map contains all registered handler instances that can be used to process
	 * field values during Redis operation execution.
	 *
	 * @return the handler registry
	 */
	public Map<Class<? extends OrangeOperationArgHandler>, OrangeOperationArgHandler> getValueHandlerMap() {
		return valueHandlerMap;
	}
}