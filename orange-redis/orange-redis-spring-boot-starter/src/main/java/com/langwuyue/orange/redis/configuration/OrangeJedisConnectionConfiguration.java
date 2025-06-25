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

import org.apache.commons.pool2.impl.GenericObjectPool;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.data.redis.JedisClientConfigurationBuilderCustomizer;
import org.springframework.boot.context.properties.PropertyMapper;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisClusterConfiguration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisSentinelConfiguration;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.jedis.JedisClientConfiguration;
import org.springframework.data.redis.connection.jedis.JedisClientConfiguration.JedisClientConfigurationBuilder;
import org.springframework.data.redis.connection.jedis.JedisConnection;
import org.springframework.util.StringUtils;

import com.langwuyue.orange.redis.configuration.OrangeRedisProperties.Pool;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPoolConfig;


/**
 * Configuration class for setting up Redis connections using the Jedis client library.
 * 
 * This class extends {@code OrangeRedisConnectionConfiguration} and is responsible for creating
 * and configuring Jedis-based Redis connection factories. It is activated when:
 * <ul>
 *   <li>The Jedis client library is present on the classpath</li>
 *   <li>No other {@link RedisConnectionFactory} bean is defined</li>
 *   <li>The property 'orange.redis.client-type' is set to 'jedis'</li>
 * </ul>
 * 
 * It supports various Redis deployment topologies:
 * <ul>
 *   <li>Standalone Redis servers</li>
 *   <li>Redis Sentinel configurations</li>
 *   <li>Redis Cluster configurations</li>
 * </ul>
 * 
 * The configuration allows customization of connection properties including:
 * <ul>
 *   <li>Connection pooling settings</li>
 *   <li>SSL configuration</li>
 *   <li>Timeout settings</li>
 *   <li>Client name</li>
 * </ul>
 * 
 * This is an adaptation of Spring Boot's {@code JedisConnectionConfiguration} tailored for
 * the Orange Redis framework.
 *
 * @author Liang.Zhong
 * @since 1.0.0
 * @see OrangeRedisConnectionConfiguration
 * @see OrangeJedisConnectionFactory
 * @see JedisClientConfiguration
 */
@Configuration(proxyBeanMethods = false)
@ConditionalOnClass({ GenericObjectPool.class, JedisConnection.class, Jedis.class })
@ConditionalOnMissingBean(RedisConnectionFactory.class)
@ConditionalOnProperty(name = "orange.redis.client-type", havingValue = "jedis")
class OrangeJedisConnectionConfiguration extends OrangeRedisConnectionConfiguration {
	
	/**
	 * Provider for customizers that can modify the Jedis client configuration builder.
	 */
	private ObjectProvider<JedisClientConfigurationBuilderCustomizer> builderCustomizers;
	
	/**
	 * Cached instance of the Redis connection factory.
	 */
	private OrangeJedisConnectionFactory redisConnectionFactory;

	/**
	 * Constructs a new OrangeJedisConnectionConfiguration.
	 *
	 * @param properties the Redis properties to use for configuration
	 * @param standaloneConfigurationProvider provider for standalone Redis configuration
	 * @param sentinelConfiguration provider for Redis Sentinel configuration
	 * @param clusterConfiguration provider for Redis Cluster configuration
	 * @param builderCustomizers provider for Jedis client configuration builder customizers
	 */
	OrangeJedisConnectionConfiguration(OrangeRedisProperties properties,
			ObjectProvider<RedisStandaloneConfiguration> standaloneConfigurationProvider,
			ObjectProvider<RedisSentinelConfiguration> sentinelConfiguration,
			ObjectProvider<RedisClusterConfiguration> clusterConfiguration,
			ObjectProvider<JedisClientConfigurationBuilderCustomizer> builderCustomizers) {
		super(properties, standaloneConfigurationProvider, sentinelConfiguration, clusterConfiguration);
		this.builderCustomizers = builderCustomizers;
	}

	/**
	 * Creates and initializes a Redis connection factory.
	 * 
	 * This method:
	 * <ul>
	 *   <li>Returns a cached connection factory if one has already been created</li>
	 *   <li>Otherwise creates a new Jedis connection factory with appropriate configuration</li>
	 *   <li>Initializes the factory by calling afterPropertiesSet()</li>
	 *   <li>Caches the factory for future use</li>
	 * </ul>
	 *
	 * @return a configured and initialized {@link RedisConnectionFactory}
	 */
	@Override
	protected RedisConnectionFactory redisConnectionFactory() {
		if(this.redisConnectionFactory != null) {
			return this.redisConnectionFactory;
		}
		OrangeJedisConnectionFactory factory = createJedisConnectionFactory(builderCustomizers);
		factory.afterPropertiesSet();
		this.redisConnectionFactory = factory;
		return factory;
	}
	
	/**
	 * Returns the connection pool configuration from properties.
	 * 
	 * @return the pool configuration for Lettuce client
	 */
	@Override
	protected Pool getPool() {
		return getProperties().getLettuce().getPool();
	}

	/**
	 * Creates a Jedis connection factory based on the available Redis configuration.
	 * 
	 * This method determines the appropriate factory type based on the Redis deployment topology:
	 * <ul>
	 *   <li>If a Sentinel configuration is available, creates a Sentinel-based factory</li>
	 *   <li>If a Cluster configuration is available, creates a Cluster-based factory</li>
	 *   <li>Otherwise, creates a standalone factory</li>
	 * </ul>
	 *
	 * @param builderCustomizers customizers for the Jedis client configuration
	 * @return a configured {@link OrangeJedisConnectionFactory}
	 */
	private OrangeJedisConnectionFactory createJedisConnectionFactory(
			ObjectProvider<JedisClientConfigurationBuilderCustomizer> builderCustomizers) {
		JedisClientConfiguration clientConfiguration = getJedisClientConfiguration(builderCustomizers);
		if (getSentinelConfig() != null) {
			return new OrangeJedisConnectionFactory(getSentinelConfig(), clientConfiguration);
		}
		if (getClusterConfiguration() != null) {
			return new OrangeJedisConnectionFactory(getClusterConfiguration(), clientConfiguration);
		}
		return new OrangeJedisConnectionFactory(getStandaloneConfig(), clientConfiguration);
	}

	/**
	 * Creates and configures a Jedis client configuration.
	 * 
	 * This method:
	 * <ul>
	 *   <li>Applies basic properties from the Redis properties</li>
	 *   <li>Configures connection pooling if enabled</li>
	 *   <li>Applies any URL-specific configuration</li>
	 *   <li>Applies any custom configuration from builder customizers</li>
	 * </ul>
	 *
	 * @param builderCustomizers customizers for the Jedis client configuration builder
	 * @return a configured {@link JedisClientConfiguration}
	 */
	private JedisClientConfiguration getJedisClientConfiguration(
			ObjectProvider<JedisClientConfigurationBuilderCustomizer> builderCustomizers) {
		JedisClientConfigurationBuilder builder = applyProperties(JedisClientConfiguration.builder());
		OrangeRedisProperties.Pool pool = getProperties().getJedis().getPool();
		if (isPoolEnabled(pool)) {
			applyPooling(pool, builder);
		}
		if (StringUtils.hasText(getProperties().getUrl())) {
			customizeConfigurationFromUrl(builder);
		}
		builderCustomizers.orderedStream().forEach((customizer) -> customizer.customize(builder));
		return builder.build();
	}

	/**
	 * Applies Redis properties to the Jedis client configuration builder.
	 * 
	 * This method uses PropertyMapper to map properties from the Redis configuration to the builder:
	 * <ul>
	 *   <li>SSL configuration</li>
	 *   <li>Read timeout</li>
	 *   <li>Connection timeout</li>
	 *   <li>Client name</li>
	 * </ul>
	 *
	 * @param builder the Jedis client configuration builder to configure
	 * @return the configured builder
	 */
	private JedisClientConfigurationBuilder applyProperties(JedisClientConfigurationBuilder builder) {
		PropertyMapper map = PropertyMapper.get().alwaysApplyingWhenNonNull();
		map.from(getProperties().isSsl()).whenTrue().toCall(builder::useSsl);
		map.from(getProperties().getTimeout()).to(builder::readTimeout);
		map.from(getProperties().getConnectTimeout()).to(builder::connectTimeout);
		map.from(getProperties().getClientName()).whenHasText().to(builder::clientName);
		return builder;
	}

	/**
	 * Configures connection pooling for the Jedis client.
	 * 
	 * This method enables connection pooling and applies the pool configuration
	 * settings from the provided pool properties.
	 *
	 * @param pool the pool configuration properties
	 * @param builder the Jedis client configuration builder to configure
	 */
	private void applyPooling(OrangeRedisProperties.Pool pool,
			JedisClientConfiguration.JedisClientConfigurationBuilder builder) {
		builder.usePooling().poolConfig(jedisPoolConfig(pool));
	}

	/**
	 * Creates a JedisPoolConfig with settings from the provided pool properties.
	 * 
	 * Configures the following pool settings:
	 * <ul>
	 *   <li>Maximum total connections</li>
	 *   <li>Maximum idle connections</li>
	 *   <li>Minimum idle connections</li>
	 *   <li>Time between eviction runs (if specified)</li>
	 *   <li>Maximum wait time (if specified)</li>
	 * </ul>
	 *
	 * @param pool the pool configuration properties
	 * @return a configured {@link JedisPoolConfig}
	 */
	private JedisPoolConfig jedisPoolConfig(OrangeRedisProperties.Pool pool) {
		JedisPoolConfig config = new JedisPoolConfig();
		config.setMaxTotal(pool.getMaxActive());
		config.setMaxIdle(pool.getMaxIdle());
		config.setMinIdle(pool.getMinIdle());
		if (pool.getTimeBetweenEvictionRuns() != null) {
			config.setTimeBetweenEvictionRuns(pool.getTimeBetweenEvictionRuns());
		}
		if (pool.getMaxWait() != null) {
			config.setMaxWait(pool.getMaxWait());
		}
		return config;
	}

	private void customizeConfigurationFromUrl(JedisClientConfiguration.JedisClientConfigurationBuilder builder) {
		ConnectionInfo connectionInfo = parseUrl(getProperties().getUrl());
		if (connectionInfo.isUseSsl()) {
			builder.useSsl();
		}
	}

}