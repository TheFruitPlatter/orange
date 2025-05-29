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
 * Marks a class as a listener for Redis key absence events.
 * This annotation is used to define listeners that will be notified
 * when specified Redis keys are not found in the cache.
 *
 * <p>Classes annotated with {@code @OrangeRedisIfAbsentListener} must specify
 * one or more target classes that are annotated with {@code @OrangeRedisKey}.
 * When a cache miss occurs for any of the specified keys, the listener will
 * be invoked to handle the absence.</p>
 *
 * <p>The handler methods in the listener class can use {@code @OrangeRedisOriginalKey}
 * to identify which parameter represents the original key pattern (containing variables)
 * that triggered the absence event. This is useful when you need to know both the
 * original pattern and the resolved key that was missing.</p>
 *
 *
 * @see OrangeRedisKey
 * @see OrangeRedisOriginalKey
 * @author Liang.Zhong
 * @since 1.0.0
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface OrangeRedisIfAbsentListener {

	/**
	 * Specifies the classes that define the Redis keys to be monitored.
	 * Each class in the array must be annotated with {@code @OrangeRedisKey}.
	 * When any of the keys defined in these classes are not found in Redis,
	 * the listener will be notified.
	 *
	 * <p>The specified classes serve as key definitions and should contain
	 * the Redis key patterns or templates that this listener is interested in.
	 * At least one class must be specified.</p>
	 *
	 * @return an array of classes that are annotated with {@code @OrangeRedisKey}
	 */
	Class<?>[] keys();
	
}