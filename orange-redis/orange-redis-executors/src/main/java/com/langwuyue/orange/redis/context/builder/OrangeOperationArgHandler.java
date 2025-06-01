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
 * Defines the contract for handling Redis operation arguments and their annotations.
 * 
 * <p>This interface provides methods to process method parameters and annotations
 * during Redis operation execution. It serves as a bridge between method invocation
 * parameters and the Redis operation context, allowing for flexible parameter
 * binding and annotation processing.
 * 
 * <p>The handler supports:
 * <ul>
 *   <li>Extracting values from method arguments</li>
 *   <li>Processing parameter annotations</li>
 *   <li>Binding values to context fields</li>
 *   <li>Handling special annotations for Redis operations</li>
 * </ul>
 * 
 * <p>Implementations of this interface can provide custom logic for different
 * types of parameters and annotations, making it extensible for various Redis
 * operation scenarios.
 *
 * @author Liang.Zhong
 * @since 1.0.0
 * @see OrangeRedisContext
 * @see OrangeOperationArgSimpleHandler
 * @see OrangeOperationArgMultipleHandler
 */
public interface OrangeOperationArgHandler {
	
	/**
	 * Extracts a value from the method arguments based on parameter position.
	 * 
	 * <p>This method is responsible for retrieving the appropriate value from the
	 * method arguments array based on the parameter's position and the total number
	 * of parameters.
	 *
	 * @param parameterCount total number of parameters in the method
	 * @param parameterIndex index of the current parameter being processed (0-based)
	 * @param args array of method arguments
	 * @return the extracted value, or null if no value could be extracted
	 */
	default Object getArgValue(int parameterCount, int parameterIndex, Object[] args) {
		return null;
	}
	
	/**
	 * Sets a context value based on an annotation.
	 * 
	 * <p>This method processes an annotation and sets the corresponding value in the
	 * Redis operation context. It's typically used for handling special Redis operation
	 * annotations that provide configuration or metadata.
	 *
	 * @param context the Redis operation context to be modified
	 * @param annotation the annotation to process
	 * @param field the field to be set in the context
	 */
	default void setContextValueByAnnotation(OrangeRedisContext context, Annotation annotation, Field field) {
	}

	/**
	 * Binds a method argument value to a context field.
	 * 
	 * <p>This method extracts a value from the method arguments and sets it in the
	 * specified field of the Redis operation context. It uses reflection to set
	 * the field value.
	 *
	 * @param parameterCount total number of parameters in the method
	 * @param parameterIndex index of the current parameter being processed (0-based)
	 * @param args array of method arguments
	 * @param field the field to be set in the context
	 * @param context the Redis operation context to be modified
	 */
	default void bindValue(int parameterCount, int parameterIndex, Object[] args, Field field, OrangeRedisContext context) {
		OrangeReflectionUtils.setFieldValue(field, context, getArgValue(parameterCount, parameterIndex, args));
	}
	
	/**
	 * Processes an annotation and binds its information to a context field.
	 * 
	 * <p>This method handles any special processing required by the annotation
	 * and updates the context field accordingly.
	 *
	 * @param field the field to be processed
	 * @param context the Redis operation context to be modified
	 * @param annotation the annotation to process
	 */
	default void bindAnnotation(Field field, OrangeRedisContext context, Annotation annotation) {
		
	}
	
	/**
	 * Combines value binding and annotation processing in a single operation.
	 * 
	 * <p>This method serves as a convenience method that performs both value binding
	 * and annotation processing in one step. It first binds the method argument value
	 * to the context field, then processes any annotations on that field.
	 *
	 * @param field the field to be processed
	 * @param context the Redis operation context to be modified
	 * @param annotation the annotation to process
	 * @param parameterCount total number of parameters in the method
	 * @param parameterIndex index of the current parameter being processed (0-based)
	 * @param args array of method arguments
	 */
	default void bind(Field field, OrangeRedisContext context, Annotation annotation, int parameterCount, int parameterIndex, Object[] args) {
		bindValue(parameterCount, parameterIndex, args, field, context);
		bindAnnotation(field, context, annotation);
	}
}