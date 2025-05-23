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

import com.langwuyue.orange.redis.context.OrangeRedisContext;
import com.langwuyue.orange.redis.utils.OrangeReflectionUtils;

/**
 * @author Liang.Zhong
 * @since 1.0.0
 */
public interface OrangeOperationArgHandler {
	
	default Object getArgValue(int parameterCount, int parameterIndex, Object[] args) {
		return null;
	}
	
	default void setContextValueByAnnotation(OrangeRedisContext context,Annotation annotation,Field field) {
	}

	default void bindValue(int parameterCount, int parameterIndex, Object[] args, Field field, OrangeRedisContext context) {
		OrangeReflectionUtils.setFieldValue(field, context, getArgValue(parameterCount,parameterIndex,args));
	}
	
	default void bindAnnotation(Field field, OrangeRedisContext context,Annotation annotation) {
		
	}
	
	default void bind(Field field, OrangeRedisContext context,Annotation annotation,int parameterCount, int parameterIndex, Object[] args) {
		bindValue(parameterCount,parameterIndex,args,field,context);
		bindAnnotation(field,context,annotation);
	}
}
