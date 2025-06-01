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
 * Handler for processing method-level annotations in Redis operations.
 * 
 * <p>This handler implements {@link OrangeOperationArgHandler} to provide a mechanism for
 * storing method annotations directly in the Redis context. Unlike parameter annotations,
 * method annotations apply to the entire operation rather than specific parameters.
 * 
 * <p>Key features:
 * <ul>
 *   <li>Stores method-level annotations in context fields</li>
 *   <li>Uses reflection to set annotation values</li>
 *   <li>Enables access to annotation metadata during Redis operations</li>
 * </ul>
 * 
 * @author Liang.Zhong
 * @since 1.0.0
 * @see OrangeOperationArgHandler
 * @see OrangeRedisContext
 */
public class OrangeMethodAnnotationHandler implements OrangeOperationArgHandler {

	/**
	 * Sets the annotation value to the specified field in the Redis context.
	 * 
	 * <p>This method implements the {@link OrangeOperationArgHandler} interface to handle
	 * method-level annotations. It uses reflection utilities to set the annotation object
	 * directly as the field value in the context.
	 * 
	 * <p>The method assumes that the field type in the context matches the annotation type.
	 * For example, if the annotation is of type {@code @MyAnnotation}, the field in the
	 * context should be declared as {@code private MyAnnotation myAnnotation;}.
	 *
	 * @param context the Redis context object containing the field
	 * @param annotation the method-level annotation to set as the field value
	 * @param field the field to set the annotation value on
	 * @see OrangeReflectionUtils#setFieldValue(Field, Object, Object)
	 */
	@Override
	public void setContextValueByAnnotation(OrangeRedisContext context, Annotation annotation, Field field) {
		OrangeReflectionUtils.setFieldValue(field, context, annotation);
	}
}