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

import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;

import com.langwuyue.orange.redis.registry.OrangeRedisKeyRegistry;

/**
 * Redis key validation checker that runs during application startup.
 * 
 * <p>This component is responsible for validating Redis keys according to the configured
 * check level when the application context is fully initialized. It ensures that all
 * registered Redis keys comply with the naming conventions and validation rules
 * defined in the application configuration.
 * 
 * <p>The key checking process is triggered automatically when the Spring application
 * context is refreshed (typically at application startup), ensuring that any key
 * validation issues are detected early in the application lifecycle.
 * 
 * <p>The validation level can be configured through {@link OrangeRedisProperties}
 * and affects how strictly the keys are validated:
 * <ul>
 *   <li>NONE: No validation is performed</li>
 *   <li>WARN: Issues are logged as warnings but don't prevent application startup</li>
 *   <li>ERROR: Issues are logged as errors and may prevent application startup</li>
 * </ul>
 * 
 * @author Liang.Zhong
 * @since 1.0.0
 * @see OrangeRedisKeyRegistry
 * @see OrangeRedisConnectionConfiguration
 */
public class OrangeRedisKeyChecker {
	
	/**
	 * The Redis connection configuration that contains properties for key validation.
	 * 
	 * <p>This configuration provides access to settings such as the key check level,
	 * which determines how strictly Redis keys are validated during application startup.
	 * The configuration is immutable after initialization to ensure thread safety.
	 */
	private final OrangeRedisConnectionConfiguration configuration;
	
	/**
	 * Constructs a new OrangeRedisKeyChecker with the specified configuration.
	 * 
	 * <p>The provided configuration is used to determine the key check level
	 * that will be applied when validating Redis keys during application startup.
	 * 
	 * @param configuration The Redis connection configuration containing properties
	 *                      such as the key check level setting
	 */
	public OrangeRedisKeyChecker(OrangeRedisConnectionConfiguration configuration) {
		super();
		this.configuration = configuration;
	}

	/**
	 * Handles the application context refresh event to trigger Redis key validation.
	 * 
	 * <p>This method is automatically called when the Spring application context is
	 * fully initialized. It retrieves the configured key check level from the Redis
	 * properties and initiates the key validation process through the
	 * {@link OrangeRedisKeyRegistry}.
	 * 
	 * <p>Depending on the configured check level, this validation may:
	 * <ul>
	 *   <li>Log warnings for non-compliant keys</li>
	 *   <li>Throw exceptions that could prevent application startup</li>
	 *   <li>Be completely skipped if validation is disabled</li>
	 * </ul>
	 * 
	 * @param event The context refreshed event that triggers this method
	 * @see OrangeRedisKeyRegistry#checkKey(com.langwuyue.orange.redis.configuration.OrangeRedisProperties.KeyCheckLevel)
	 */
	@EventListener
    public void onApplicationReady(ContextRefreshedEvent event) {
		OrangeRedisKeyRegistry.checkKey(configuration.getProperties().getKeyCheckLevel());
    }

}