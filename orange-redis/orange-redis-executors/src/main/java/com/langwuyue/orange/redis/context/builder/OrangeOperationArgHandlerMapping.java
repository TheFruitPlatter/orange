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
 * @author Liang.Zhong
 * @since 1.0.0
 */
public class OrangeOperationArgHandlerMapping {
	
	private OrangeRedisExecutorsMapping executorsMapping;
	
	private Class operationOwner;
	
	private Map<Class<? extends OrangeRedisContext>, Map<Class<? extends Annotation>, List<Field>>> contextFieldsMap;
	
	private Map<Field, OrangeOperationArgHandler> fieldValueHandlerMap;
	
	private Map<Class<? extends OrangeOperationArgHandler>, OrangeOperationArgHandler> valueHandlerMap;
	
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


	public void buildMapping() {
		Method[] methods = this.operationOwner.getMethods();
		for(Method method : methods) {
			try {
				OrangeRedisExecutor executor = getOrangeRedisExecutor(method);
				Class<? extends OrangeRedisContext> contextClass = executor.getContextClass();
				Map<Class<? extends Annotation>, List<Field>> annotationFieldMap = this.contextFieldsMap.get(contextClass);
				if(this.contextFieldsMap.containsKey(contextClass)) {
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
	
	protected OrangeRedisExecutor getOrangeRedisExecutor(Method method) {
		return this.executorsMapping.getExecutor(method);
	}
	
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


	public OrangeRedisExecutorsMapping getExecutorsMapping() {
		return executorsMapping;
	}


	public Class getOperationOwner() {
		return operationOwner;
	}


	public Map<Class<? extends OrangeRedisContext>, Map<Class<? extends Annotation>, List<Field>>> getContextFieldsMap() {
		return contextFieldsMap;
	}


	public Map<Field, OrangeOperationArgHandler> getFieldValueHandlerMap() {
		return fieldValueHandlerMap;
	}


	public Map<Class<? extends OrangeOperationArgHandler>, OrangeOperationArgHandler> getValueHandlerMap() {
		return valueHandlerMap;
	}
}
