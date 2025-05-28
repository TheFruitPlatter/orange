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

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Binds method parameters to variables in Redis key patterns.
 * This annotation is used to map method parameters to placeholders in the
 * Redis key pattern defined by {@link OrangeRedisKey}.
 * 
 * @author Liang.Zhong
 * @since 1.0.0
 * @see OrangeRedisKey
 */
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface KeyVariable {
    
    /**
     * Specifies the variable name in the Redis key pattern.
     * This name must match a placeholder in the key pattern defined by
     * {@link OrangeRedisKey#key()}.
     * 
     * <p>Pattern Formats:
     * <ul>
     *   <li>Curly Braces: {variableName}</li>
     *   <li>SpEL: ${expression}</li>
     *   <li>Property Access: ${object.property}</li>
     * </ul>
     * 
     * <p>Examples:
     * <pre>{@code
     * // Simple variable
     * "user:{userId}:profile"
     * 
     * // Multiple variables
     * "org:{orgId}:dept:{deptId}"
     * 
     * // SpEL expression
     * "product:${product.category}:${product.id}"
     * 
     * // Complex pattern
     * "order:${order.type}:{year}:{month}:{orderId}"
     * }</pre>
     * 
     * <p>Naming Rules:
     * <ul>
     *   <li>Must be a valid Java identifier</li>
     *   <li>Case-sensitive</li>
     *   <li>Should be descriptive and meaningful</li>
     *   <li>Should match the parameter's semantic meaning</li>
     *   <li>Should be consistent across related methods</li>
     * </ul>
     * 
     * @return the variable name to bind the parameter to
     */
    String name();
}