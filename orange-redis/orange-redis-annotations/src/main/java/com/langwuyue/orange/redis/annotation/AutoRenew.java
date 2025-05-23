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
 * @author Liang.Zhong
 * @since 1.0.0
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface AutoRenew {
	
	/**
	 * Triggers auto-renew when remaining TTL ≤ (initialTTL / n).
	 * Default: 3 (renew at 1/3 of initial TTL).
	 */
	int threshold() default 3;
	
	/**
	 * When {@code autoInitKeyExpirationTime} is true, 
	 * the auto-renew mechanism will: 
	 * 1. Ignore {@link com.langwuyue.orange.redis.annotation.OrangeRedisKey#expirationTime()}
	 * 2. Calculate a new TTL 
	 */
	boolean autoInitKeyExpirationTime();

}
