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
 * @author Liang.Zhong
 * @since 1.0.0
 */
public class OrangeRedisContextBuilder {
	
	private Class<?> operationOwner;
	
	private Method operationMethod;
	
	private Method actualMethod;
	
	private Object[] args;
	
	private OrangeOperationArgHandlerMapping operationArgHandlerMapping;
	
	private Key redisKey;
	
	private RedisValueTypeEnum valueType;
	
	private Class<? extends OrangeRedisContext> contextClass;
	
	public OrangeRedisContextBuilder() {}
	
	public OrangeRedisContextBuilder operationOwner(Class<?> operationOwner) {
		this.operationOwner = operationOwner;
		return this;
	}
	
	public OrangeRedisContextBuilder operationMethod(Method operationMethod) {
		this.operationMethod = operationMethod;
		return this;
	}
	
	public OrangeRedisContextBuilder actualMethod(Method actualMethod) {
		this.actualMethod = actualMethod;
		return this;
	}
	
	public OrangeRedisContextBuilder args(Object[] args) {
		this.args = args;
		return this;
	}
	public OrangeRedisContextBuilder redisKey(Key redisKey) {
		this.redisKey = redisKey;
		return this;
	}
	public OrangeRedisContextBuilder valueType(RedisValueTypeEnum valueType) {
		this.valueType = valueType;
		return this;
	}
	public OrangeRedisContextBuilder contextClass(Class<? extends OrangeRedisContext> contextClass) {
		this.contextClass = contextClass;
		return this;
	}
	public OrangeRedisContextBuilder operationArgHandlerMapping(OrangeOperationArgHandlerMapping operationArgHandlerMapping) {
		this.operationArgHandlerMapping = operationArgHandlerMapping;
		return this;
	}
	
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

	protected Class<?> getOperationOwner() {
		return operationOwner;
	}

	protected Method getOperationMethod() {
		return operationMethod;
	}

	protected Method getActualMethod() {
		return actualMethod;
	}

	protected Object[] getArgs() {
		return args;
	}

	protected Key getRedisKey() {
		return redisKey;
	}

	protected RedisValueTypeEnum getValueType() {
		return valueType;
	}

	protected Class<? extends OrangeRedisContext> getContextClass() {
		return contextClass;
	}

	protected OrangeOperationArgHandlerMapping getOperationArgHandlerMapping() {
		return operationArgHandlerMapping;
	}
	
}
