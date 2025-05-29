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
 * Marks a method parameter as the original Redis key pattern in listener methods.
 * This annotation is used to identify which parameter represents the unprocessed
 * key pattern that may contain variable placeholders.
 *
 * <p>In Redis listeners, keys often contain variables that need to be resolved
 * at runtime. For example, a key pattern like "user:profile:${userId}" contains
 * a variable placeholder ${userId}. The listener's proxy needs to know the original
 * key pattern (with unresolved variables) to properly filter and route Redis events.</p>
 *
 * <p>When a method parameter is annotated with {@code @OrangeRedisOriginalKey},
 * the listener's proxy will use this parameter value as the original key pattern
 * for filtering and matching purposes, before any variable resolution occurs.</p>
 *
 *
 * @see OrangeRedisListener
 * @author Liang.Zhong
 * @since 1.0.0
 */
@Target({ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface OrangeRedisOriginalKey {

	
}