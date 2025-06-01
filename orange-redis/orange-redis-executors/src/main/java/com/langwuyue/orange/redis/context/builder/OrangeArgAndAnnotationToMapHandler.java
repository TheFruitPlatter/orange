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
import java.util.LinkedHashMap;
import java.util.Map;

import com.langwuyue.orange.redis.context.OrangeRedisContext;
import com.langwuyue.orange.redis.utils.OrangeReflectionUtils;

/**
 * Handler for binding method parameters and their annotations to a Map field in Redis context.
 * 
 * <p>This handler implements {@link OrangeOperationArgHandler} to provide a mechanism for
 * storing method parameters and their associated annotations in a Map structure. This is
 * particularly useful when you need to maintain the relationship between parameters and
 * their annotations during Redis operations.
 * 
 * <p>Key features:
 * <ul>
 *   <li>Lazily initializes Map field if not already present</li>
 *   <li>Maintains parameter-to-annotation mapping in a LinkedHashMap</li>
 *   <li>Preserves insertion order of parameters</li>
 *   <li>Thread-safe when used with thread-local contexts</li>
 * </ul>
 * 
 * <p>Usage scenario:
 * <pre>{@code
 * // Example field in context class
 * private Map<Object, Annotation> parameterAnnotations;
 * 
 * // This handler will populate the map with:
 * // key: method parameter value
 * // value: annotation associated with that parameter
 * }</pre>
 *
 * @author Liang.Zhong
 * @since 1.0.0
 * @see OrangeOperationArgHandler
 * @see OrangeRedisContext
 */
public class OrangeArgAndAnnotationToMapHandler implements OrangeOperationArgHandler {

	/**
	 * Binds a method parameter and its annotation to a Map field in the Redis context.
	 * 
	 * <p>This method performs the following operations:
	 * <ol>
	 *   <li>Retrieves the current value of the specified field from the context</li>
	 *   <li>If the field is null, initializes it with a new LinkedHashMap</li>
	 *   <li>Casts the field value to a Map</li>
	 *   <li>Adds the parameter value as key and its annotation as value to the map</li>
	 * </ol>
	 * 
	 * <p>The method uses reflection to access and modify the field in the context object.
	 * The parameter value is retrieved from the args array using the parameterIndex.
	 *
	 * @param field the field in the context to bind to (must be of Map type)
	 * @param context the Redis operation context
	 * @param annotation the annotation associated with the parameter
	 * @param parameterCount total number of parameters in the method
	 * @param parameterIndex index of the current parameter being processed
	 * @param args array of method arguments
	 */
	@Override
	public void bind(
		Field field, 
		OrangeRedisContext context, 
		Annotation annotation, 
		int parameterCount,
		int parameterIndex, 
		Object[] args
	) {
		Object obj = OrangeReflectionUtils.getFieldValue(field, context);
		if(obj == null) {
			obj = new LinkedHashMap<>();
			OrangeReflectionUtils.setFieldValue(field, context, obj);
		}
		Map map = (Map)obj;
		map.put(args[parameterIndex], annotation);
	}
}