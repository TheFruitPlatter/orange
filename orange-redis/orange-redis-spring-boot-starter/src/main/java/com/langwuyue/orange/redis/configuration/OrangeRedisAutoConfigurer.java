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

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;

import com.langwuyue.orange.redis.OrangeRedisDefaultCircuitBreaker;
import com.langwuyue.orange.redis.endpoint.OrangeRedisKeytRegistryEndpoint;
import com.langwuyue.orange.redis.executor.multiplelocks.OrangeDefaultExpirationTimeAutoInitializer;
import com.langwuyue.orange.redis.executor.multiplelocks.OrangeExpirationTimeAutoInitializer;
import com.langwuyue.orange.redis.logger.OrangeRedisDefaultLogger;
import com.langwuyue.orange.redis.logger.OrangeRedisLogger;
import com.langwuyue.orange.redis.timer.OrangeRenewTimerWheel;

/**
 * Auto-configuration class for Orange Redis framework components.
 * 
 * <p>This class provides automatic configuration for the Orange Redis framework
 * by registering necessary beans in the Spring application context. It enables
 * property binding through {@link OrangeRedisProperties} and scans for components
 * in relevant packages.
 * 
 * <p>All beans are conditionally registered with {@code @ConditionalOnMissingBean}
 * to allow for custom implementations to be provided by the application if needed.
 *
 * @author Liang.Zhong
 * @since 1.0.0
 * @see OrangeRedisProperties
 * @see OrangeRedisKeyChecker
 * @see OrangeRenewTimerWheel
 * @see OrangeExpirationTimeAutoInitializer
 * @see OrangeRedisLogger
 * @see OrangeRedisDefaultCircuitBreaker
 */
@EnableConfigurationProperties(OrangeRedisProperties.class)
@ComponentScan(basePackageClasses = {OrangeRedisAutoConfigurer.class,OrangeRedisKeytRegistryEndpoint.class})
class OrangeRedisAutoConfigurer {
	
	/**
	 * Creates a new {@link OrangeRedisKeyChecker} bean.
	 * 
	 * <p>The key checker is responsible for validating Redis keys according to
	 * the configured Redis connection settings.
	 *
	 * @param configuration the Redis connection configuration
	 * @return a new instance of {@link OrangeRedisKeyChecker}
	 */
	@Bean
	@ConditionalOnMissingBean
	OrangeRedisKeyChecker newOrangeRedisKeyChecker(OrangeRedisConnectionConfiguration configuration) {
		return new OrangeRedisKeyChecker(configuration);
	}
	
	/**
	 * Creates a new {@link OrangeRenewTimerWheel} bean.
	 * 
	 * <p>The timer wheel is used for scheduling and managing automatic key renewal
	 * operations based on the configured auto-renewal properties.
	 *
	 * @param properties the Redis properties containing auto-renewal settings
	 * @param logger the Redis logger for logging renewal operations
	 * @return a new instance of {@link OrangeRenewTimerWheel}
	 */
	@Bean
	@ConditionalOnMissingBean
	OrangeRenewTimerWheel newOrangeRenewTimerWheel(OrangeRedisProperties properties,OrangeRedisLogger logger) {
		return new OrangeRenewTimerWheel(properties.getAutoRenew(),logger);
	}
	
	/**
	 * Creates a new {@link OrangeExpirationTimeAutoInitializer} bean.
	 * 
	 * <p>The initializer is responsible for setting up default expiration times
	 * for Redis keys based on the configured auto-renewal properties.
	 *
	 * @param properties the Redis properties containing auto-renewal settings
	 * @return a new instance of {@link OrangeDefaultExpirationTimeAutoInitializer}
	 */
	@Bean
	@ConditionalOnMissingBean
	OrangeExpirationTimeAutoInitializer newOrangeDefaultExpirationTimeAutoInitializer(OrangeRedisProperties properties) {
		return new OrangeDefaultExpirationTimeAutoInitializer(properties.getAutoRenew());
	}
	
	/**
	 * Creates a new {@link OrangeRedisLogger} bean.
	 * 
	 * <p>The logger provides Redis-specific logging capabilities for the Orange Redis framework.
	 *
	 * @return a new instance of {@link OrangeRedisDefaultLogger}
	 */
	@Bean
	@ConditionalOnMissingBean
	OrangeRedisLogger newOrangeRedisLogger() {
		return new OrangeRedisDefaultLogger();
	}
	
	/**
	 * Creates a new {@link OrangeRedisDefaultCircuitBreaker} bean.
	 * 
	 * <p>The circuit breaker provides fault tolerance for Redis operations,
	 * preventing cascading failures when Redis is unavailable or experiencing issues.
	 *
	 * @return a new instance of {@link OrangeRedisDefaultCircuitBreaker}
	 */
	@Bean
	@ConditionalOnMissingBean
	OrangeRedisDefaultCircuitBreaker newOrangeRedisDefaultCircuitBreaker() {
		return new OrangeRedisDefaultCircuitBreaker();
	}
}