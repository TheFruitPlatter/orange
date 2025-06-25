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
 * Auto-configuration class for Orange Redis management components.
 *
 * <p>This class provides Spring Boot auto-configuration for Orange Redis management features including:
 * <ul>
 *   <li>Redis health indicator - Provides health check for Redis connection</li>
 *   <li>Configuration endpoint - Exposes Redis configuration details</li>
 *   <li>Key registry endpoint - Manages registered Redis keys</li>
 *   <li>Dead transaction endpoint - Handles failed Redis transactions</li>
 *   <li>Slow operation endpoint - Monitors and reports slow Redis operations</li>
 * </ul>
 *
 * <p>Implements {@link DisposableBean} to ensure proper shutdown of Redis resources including:
 * <ul>
 *   <li>Redis transaction client factory</li>
 *   <li>Lettuce or Jedis connection factories</li>
 * </ul>
 *
 * @author Liang.Zhong
 * @since 1.0.0
 * @see DisposableBean
 * @see Configuration
 * @see Bean
 */
@Configuration(proxyBeanMethods = false)
class OrangeRedisManagementAutoConfigurer implements DisposableBean {
	
	private OrangeRedisTransactionClientFactoryBean factoryBean;
	
	private OrangeRedisConnectionConfiguration configuration;
	
	/**
	 * Constructs a new OrangeRedisManagementAutoConfigurer.
	 *
	 * @param factoryBean the Redis transaction client factory bean
	 * @param configuration the Redis connection configuration
	 */
	public OrangeRedisManagementAutoConfigurer(
		OrangeRedisTransactionClientFactoryBean factoryBean,
		OrangeRedisConnectionConfiguration configuration
	){
		this.factoryBean = factoryBean;
		this.configuration = configuration;
	}
	
	/**
	 * Creates a primary Redis health indicator bean with custom Orange Redis configuration.
	 * 
	 * <p>This indicator provides detailed health information including:
	 * <ul>
	 *   <li>Connection pool status</li>
	 *   <li>Cluster/Sentinel topology (if applicable)</li>
	 *   <li>Custom health checks configured in OrangeRedisProperties</li>
	 * </ul>
	 *
	 * @param configuration the Redis connection configuration
	 * @param logger the Redis logger for health events
	 * @param properties the Redis properties containing health check configuration
	 * @return configured Redis health indicator
	 * @throws IllegalStateException if Redis health check fails
	 * @condition Only enabled when 'management.health.redis.enabled' is true
	 */
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
	
	/**
	 * Creates a configuration endpoint bean that exposes Redis properties as JSON.
	 * 
	 * <p>The endpoint serializes OrangeRedisProperties to JSON format using Jackson ObjectMapper.
	 * The exposed configuration includes:
	 * <ul>
	 *   <li>Connection settings</li>
	 *   <li>Pool configuration</li>
	 *   <li>Cluster/Sentinel topology</li>
	 * </ul>
	 *
	 * @param objectMapper the Jackson ObjectMapper for JSON serialization
	 * @param properties the Redis properties to expose
	 * @return configured Redis configuration endpoint
	 * @throws JsonProcessingException if properties cannot be serialized to JSON
	 */
	@Bean
	OrangeRedisConfigurationEndpoint newOrangeRedisConfigurationEndpoint(ObjectMapper objectMapper,OrangeRedisProperties properties) throws JsonProcessingException {
		return new OrangeRedisConfigurationEndpoint(objectMapper.writeValueAsString(properties));
	}
	
	/**
	 * Creates a key registry endpoint bean for tracking Redis keys.
	 * 
	 * <p>The registry provides functionality for:
	 * <ul>
	 *   <li>Registering and unregistering keys</li>
	 *   <li>Querying registered keys</li>
	 *   <li>Monitoring key usage patterns</li>
	 * </ul>
	 *
	 * @return configured Redis key registry endpoint
	 */
	@Bean
	OrangeRedisKeytRegistryEndpoint newOrangeRedisKeytRegistryEndpoint() {
		return new OrangeRedisKeytRegistryEndpoint();
	}
	
	/**
	 * Creates a dead transaction endpoint bean for handling failed Redis transactions.
	 * 
	 * <p>The endpoint provides functionality for:
	 * <ul>
	 *   <li>Detecting and logging failed transactions</li>
	 *   <li>Recovering or retrying failed operations</li>
	 *   <li>Reporting transaction failure statistics</li>
	 * </ul>
	 *
	 * @param factoryBean the Redis transaction client factory
	 * @param logger the Redis logger for transaction events
	 * @return configured Redis dead transaction endpoint
	 */
	@Bean
	OrangeRedisDeadTransactionEndpoint newOrangeRedisDeadTransactionEndpoint(OrangeRedisTransactionClientFactoryBean factoryBean,OrangeRedisLogger logger) {
		return new OrangeRedisDeadTransactionEndpoint(factoryBean,logger);
	}
	
	/**
	 * Creates a slow operation endpoint bean for monitoring Redis operations.
	 * 
	 * <p>The endpoint provides functionality for:
	 * <ul>
	 *   <li>Tracking slow operations based on configured thresholds</li>
	 *   <li>Logging slow operation details</li>
	 *   <li>Generating performance reports</li>
	 * </ul>
	 *
	 * @return configured Redis slow operation endpoint
	 */
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