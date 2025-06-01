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
 * Handler for processing parameter annotations in Redis operations.
 * 
 * <p>This handler implements {@link OrangeOperationArgHandler} to provide a mechanism for
 * storing parameter annotations directly in the Redis context. Parameter annotations are
 * applied to specific method parameters and contain metadata that can influence how
 * the Redis operation processes those parameters.
 * 
 * <p>Key features:
 * <ul>
 *   <li>Stores parameter-level annotations in context fields</li>
 *   <li>Uses reflection to set annotation values</li>
 *   <li>Enables access to annotation metadata during Redis operations</li>
 * </ul>
 * 
 *
 * @author Liang.Zhong
 * @since 1.0.0
 * @see OrangeOperationArgHandler
 * @see OrangeRedisContext
 */
public class OrangeOperationArgAnnotationHandler implements OrangeOperationArgHandler {

	/**
	 * Binds a parameter annotation to its corresponding field in the Redis context.
	 * 
	 * <p>This method uses reflection to set the annotation object as the value of the
	 * specified field in the context. The field type in the context must match the
	 * annotation type to avoid runtime errors.
	 * 
	 *
	 * @param field the field in the context where the annotation should be stored
	 * @param context the Redis operation context object
	 * @param annotation the parameter annotation to bind
	 * @see OrangeReflectionUtils#setFieldValue(Field, Object, Object)
	 */
	@Override
	public void bindAnnotation(Field field, OrangeRedisContext context, Annotation annotation) {
		OrangeReflectionUtils.setFieldValue(field, context, annotation);
	}
}