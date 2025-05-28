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
import java.util.concurrent.TimeUnit;

/**
 * Configures timeout/expiration settings for Redis operations.
 * This annotation can be used at both method and type level to specify
 * the duration and time unit for various timeout scenarios.
 * 
 * @author Liang.Zhong
 * @since 1.0.0
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Timeout {
    
    /**
     * Specifies the timeout duration in the units specified by {@link #unit()}.
     * 
     * <p>The interpretation of this value depends on the context:
     * <ul>
     *   <li>For key expiration - Time until key expires</li>
     *   <li>For locks - Maximum lock hold time</li>
     *   <li>For operations - Maximum operation wait time</li>
     * </ul>
     * 
     * <p>Guidelines:
     * <ul>
     *   <li>Must be positive</li>
     *   <li>Consider operation complexity</li>
     *   <li>Account for network latency</li>
     *   <li>Plan for worst-case scenarios</li>
     * </ul>
     * 
     * @return the timeout duration
     */
    long value();
    
    /**
     * Specifies the time unit for the {@link #value()}.
     * Defaults to {@link TimeUnit#SECONDS} if not specified.
     * 
     * <p>Supported Time Units:
     * <ul>
     *   <li>{@link TimeUnit#NANOSECONDS} - Rarely used, may cause precision loss</li>
     *   <li>{@link TimeUnit#MICROSECONDS} - Rarely used, may cause precision loss</li>
     *   <li>{@link TimeUnit#MILLISECONDS} - High precision, good for short operations</li>
     *   <li>{@link TimeUnit#SECONDS} - Default, most common, good balance</li>
     *   <li>{@link TimeUnit#MINUTES} - Medium duration, good for sessions</li>
     *   <li>{@link TimeUnit#HOURS} - Long duration, good for caching</li>
     *   <li>{@link TimeUnit#DAYS} - Very long duration, good for persistence</li>
     * </ul>
     * 
     * <p>Selection Guidelines:
     * <ul>
     *   <li>Use SECONDS for most operations</li>
     *   <li>Use MILLISECONDS for precise timing</li>
     *   <li>Use MINUTES/HOURS for caching</li>
     *   <li>Use DAYS for long-term storage</li>
     * </ul>
     * 
     * @return the time unit for the timeout value
     */
    TimeUnit unit() default TimeUnit.SECONDS;
}