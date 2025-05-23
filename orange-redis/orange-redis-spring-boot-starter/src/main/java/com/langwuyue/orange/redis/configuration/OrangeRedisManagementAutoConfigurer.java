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

import org.springframework.beans.factory.DisposableBean;
import org.springframework.boot.actuate.autoconfigure.health.ConditionalOnEnabledHealthIndicator;
import org.springframework.boot.actuate.redis.RedisHealthIndicator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.langwuyue.orange.redis.endpoint.OrangeRedisConfigurationEndpoint;
import com.langwuyue.orange.redis.endpoint.OrangeRedisDeadTransactionEndpoint;
import com.langwuyue.orange.redis.endpoint.OrangeRedisKeytRegistryEndpoint;
import com.langwuyue.orange.redis.endpoint.OrangeRedisSlowOperationEndpoint;
import com.langwuyue.orange.redis.logger.OrangeRedisLogger;

/**
 * @author Liang.Zhong
 * @since 1.0.0
 */
@Configuration(proxyBeanMethods = false)
class OrangeRedisManagementAutoConfigurer implements DisposableBean {
	
	private OrangeRedisTransactionClientFactoryBean factoryBean;
	
	private OrangeRedisConnectionConfiguration configuration;
	
	public OrangeRedisManagementAutoConfigurer(
		OrangeRedisTransactionClientFactoryBean factoryBean,
		OrangeRedisConnectionConfiguration configuration
	){
		this.factoryBean = factoryBean;
		this.configuration = configuration;
	}
	
	@Bean(name = {"redisHealthIndicator","redisHealthContributor"})
	@Primary
	@ConditionalOnEnabledHealthIndicator("redis")
	RedisHealthIndicator newOrangeRedisHealthIndicator(
		OrangeRedisConnectionConfiguration configuration,
		OrangeRedisLogger logger,
		OrangeRedisProperties properties
	) {
		return new OrangeRedisHealthIndicator(configuration,logger,properties);
	}
	
	@Bean
	OrangeRedisConfigurationEndpoint newOrangeRedisConfigurationEndpoint(ObjectMapper objectMapper,OrangeRedisProperties properties) throws JsonProcessingException {
		return new OrangeRedisConfigurationEndpoint(objectMapper.writeValueAsString(properties));
	}
	
	@Bean
	OrangeRedisKeytRegistryEndpoint newOrangeRedisKeytRegistryEndpoint() {
		return new OrangeRedisKeytRegistryEndpoint();
	}
	
	@Bean
	OrangeRedisDeadTransactionEndpoint newOrangeRedisDeadTransactionEndpoint(OrangeRedisTransactionClientFactoryBean factoryBean,OrangeRedisLogger logger) {
		return new OrangeRedisDeadTransactionEndpoint(factoryBean,logger);
	}
	
	@Bean
	OrangeRedisSlowOperationEndpoint newOrangeRedisSlowOperationEndpoint() {
		return new OrangeRedisSlowOperationEndpoint();
	}
	
	/**
	 * shutdown gracefully
	 */
	@Override
	public void destroy() throws Exception {
		if(this.factoryBean != null) {
			this.factoryBean.destroy();
		}
		if(this.configuration != null) {
			RedisConnectionFactory factory = this.configuration.redisConnectionFactory();
			if(factory instanceof LettuceConnectionFactory) {
				((LettuceConnectionFactory)factory).destroy();
			}
			else if(factory instanceof OrangeJedisConnectionFactory) {
				((OrangeJedisConnectionFactory)factory).destroy();
			}
		}
	}
}
