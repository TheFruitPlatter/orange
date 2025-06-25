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
package com.langwuyue.orange.redis.configuration;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.context.annotation.Import;

/**
 * Annotation to enable scanning for Redis client interfaces.
 * 
 * <p>This annotation is used to automatically detect interfaces annotated with
 * {@code OrangeRedisClient} and register them as Spring beans. It works similarly
 * to Spring's {@code @ComponentScan} but specifically for Redis client interfaces.
 * 
 * @author Liang.Zhong
 * @since 1.0.0
 * @see OrangeRedisClientScannerRegistrar
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import(OrangeRedisClientScannerRegistrar.class)
public @interface OrangeRedisClientScan {

	/**
	 * Specifies the base packages to scan for Redis client interfaces.
	 * 
	 * <p>These packages and their sub-packages will be scanned for interfaces
	 * annotated with {@code OrangeRedisClient}. The scanning process is similar
	 * to Spring's component scanning but specifically targets Redis client interfaces.
	 * 
	 * @return an array of base packages to scan
	 */
	String[] basePackages();
}