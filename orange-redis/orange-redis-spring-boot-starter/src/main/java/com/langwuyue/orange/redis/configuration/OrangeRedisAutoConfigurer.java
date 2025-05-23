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
 * @author Liang.Zhong
 * @since 1.0.0
 */
@EnableConfigurationProperties(OrangeRedisProperties.class)
@ComponentScan(basePackageClasses = {OrangeRedisAutoConfigurer.class,OrangeRedisKeytRegistryEndpoint.class})
class OrangeRedisAutoConfigurer {
	
	
	@Bean
	@ConditionalOnMissingBean
	OrangeRedisKeyChecker newOrangeRedisKeyChecker(OrangeRedisConnectionConfiguration configuration) {
		return new OrangeRedisKeyChecker(configuration);
	}
	
	@Bean
	@ConditionalOnMissingBean
	OrangeRenewTimerWheel newOrangeRenewTimerWheel(OrangeRedisProperties properties,OrangeRedisLogger logger) {
		return new OrangeRenewTimerWheel(properties.getAutoRenew(),logger);
	}
	
	@Bean
	@ConditionalOnMissingBean
	OrangeExpirationTimeAutoInitializer newOrangeDefaultExpirationTimeAutoInitializer(OrangeRedisProperties properties) {
		return new OrangeDefaultExpirationTimeAutoInitializer(properties.getAutoRenew());
	}
	
	@Bean
	@ConditionalOnMissingBean
	OrangeRedisLogger newOrangeRedisLogger() {
		return new OrangeRedisDefaultLogger();
	}
	
	@Bean
	@ConditionalOnMissingBean
	OrangeRedisDefaultCircuitBreaker newOrangeRedisDefaultCircuitBreaker() {
		return new OrangeRedisDefaultCircuitBreaker();
	}
}
