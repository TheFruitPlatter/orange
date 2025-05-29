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
package com.langwuyue.orange.redis.annotation;

import java.lang.annotation.Annotation;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.langwuyue.orange.redis.context.builder.OrangeOperationArgHandler;
import com.langwuyue.orange.redis.context.builder.OrangeOperationArgSimpleHandler;

/**
 * Defines a mapping between a field and a method parameter annotation for Redis operations.
 * This annotation is used to bind field values to method parameters in Redis operations,
 * allowing for dynamic parameter handling and value transformation, particularly useful
 * in resolving dynamic Redis key patterns.
 *
 * <p>The annotation provides two key components:</p>
 * <ul>
 *   <li>A binding to another annotation that marks method parameters</li>
 *   <li>A handler class that processes the parameter values</li>
 * </ul>
 *
 *
 * @see OrangeOperationArgHandler
 * @see OrangeOperationArgSimpleHandler
 * @author Liang.Zhong
 * @since 1.0.0
 */
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface OrangeRedisOperationArg {
	
	/**
	 * Specifies the annotation class that will be used to mark method parameters.
	 * This creates a binding between the annotated field and method parameters
	 * that are marked with the specified annotation.
	 *
	 * <p>When a Redis operation is executed, the framework will look for method
	 * parameters annotated with this binding annotation and apply the field's
	 * value to those parameters.</p>
	 *
	 * @return the annotation class that will be used to identify target method parameters
	 */
	Class<? extends Annotation> binding();
	
	/**
	 * Specifies the handler class that will process the field's value before
	 * it is applied to method parameters. This handler can perform value
	 * transformations, validations, or other custom processing.
	 *
	 * <p>The default handler ({@link OrangeOperationArgSimpleHandler}) performs
	 * a direct value assignment without any transformation. Custom handlers can
	 * be implemented by extending {@link OrangeOperationArgHandler}.</p>
	 *
	 * @return the handler class that will process the field's value
	 * @see OrangeOperationArgHandler
	 * @see OrangeOperationArgSimpleHandler
	 */
	Class<? extends OrangeOperationArgHandler> valueHandler() default OrangeOperationArgSimpleHandler.class;
	
	
}