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

import java.time.Duration;

import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.data.redis.LettuceClientConfigurationBuilderCustomizer;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisClusterConfiguration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisSentinelConfiguration;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceClientConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceClientConfiguration.LettuceClientConfigurationBuilder;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettucePoolingClientConfiguration;
import org.springframework.util.StringUtils;

import com.langwuyue.orange.redis.configuration.OrangeRedisProperties.Lettuce.Cluster.Refresh;
import com.langwuyue.orange.redis.configuration.OrangeRedisProperties.Pool;

import io.lettuce.core.ClientOptions;
import io.lettuce.core.RedisClient;
import io.lettuce.core.SocketOptions;
import io.lettuce.core.TimeoutOptions;
import io.lettuce.core.cluster.ClusterClientOptions;
import io.lettuce.core.cluster.ClusterTopologyRefreshOptions;
import io.lettuce.core.cluster.ClusterTopologyRefreshOptions.Builder;
import io.lettuce.core.resource.ClientResources;


/**
 * Redis connection configuration using Lettuce as the client library.
 * 
 * <p>This configuration class is responsible for creating and configuring a
 * {@link LettuceConnectionFactory} based on the provided {@link OrangeRedisProperties}.
 * It extends {@link OrangeRedisConnectionConfiguration} and is activated when the
 * {@code orange.redis.client-type} property is set to "lettuce" or not specified.
 * 
 * <p>Lettuce is a high-performance, non-blocking Redis client based on Netty.
 * This configuration supports all Redis deployment topologies:
 * <ul>
 *   <li>Standalone - Single Redis server instance</li>
 *   <li>Sentinel - High availability Redis setup with master-slave replication and automatic failover</li>
 *   <li>Cluster - Horizontally scaled Redis setup with data sharding across multiple nodes</li>
 * </ul>
 * 
 * <p>This class is based on Spring Boot's {@code LettuceConnectionConfiguration} but adapted
 * for the Orange framework.
 * 
 * @author Liang.Zhong
 * @since 1.0.0
 * @see OrangeRedisConnectionConfiguration
 * @see LettuceConnectionFactory
 * @see RedisClient
 */
@Configuration(proxyBeanMethods = false)
@ConditionalOnClass(RedisClient.class)
@ConditionalOnProperty(name = "orange.redis.client-type", havingValue = "lettuce", matchIfMissing = true)
class OrangeLettuceConnectionConfiguration extends OrangeRedisConnectionConfiguration {
	
	/**
	 * Provider for customizers that can be used to customize the {@link LettuceClientConfigurationBuilder}
	 * before the client configuration is built.
	 */
	private ObjectProvider<LettuceClientConfigurationBuilderCustomizer> builderCustomizers;
	
	/**
	 * Shared client resources for Lettuce clients.
	 */
	private ClientResources clientResources;
	
	/**
	 * Cached Redis connection factory instance.
	 */
	private LettuceConnectionFactory redisConnectionFactory;
	
	/**
	 * Creates a new {@link OrangeLettuceConnectionConfiguration} instance.
	 *
	 * @param properties the Redis properties to use
	 * @param standaloneConfigurationProvider provider for standalone configuration
	 * @param sentinelConfigurationProvider provider for sentinel configuration
	 * @param clusterConfigurationProvider provider for cluster configuration
	 * @param builderCustomizers customizers for the Lettuce client configuration builder
	 * @param clientResources the Lettuce client resources to use
	 */
	OrangeLettuceConnectionConfiguration(OrangeRedisProperties properties,
			ObjectProvider<RedisStandaloneConfiguration> standaloneConfigurationProvider,
			ObjectProvider<RedisSentinelConfiguration> sentinelConfigurationProvider,
			ObjectProvider<RedisClusterConfiguration> clusterConfigurationProvider,
			ObjectProvider<LettuceClientConfigurationBuilderCustomizer> builderCustomizers,
			ClientResources clientResources) {
		super(properties, standaloneConfigurationProvider, sentinelConfigurationProvider, clusterConfigurationProvider);
		this.builderCustomizers = builderCustomizers;
		this.clientResources = clientResources;
	}

	/**
	 * Creates and initializes a {@link RedisConnectionFactory} based on the configured properties.
	 * 
	 * <p>This method caches the created connection factory to avoid creating multiple instances.
	 *
	 * @return the Redis connection factory
	 */
	@Override
	protected RedisConnectionFactory redisConnectionFactory() {
		if(this.redisConnectionFactory != null) {
			return this.redisConnectionFactory;
		}
		LettuceClientConfiguration clientConfig = getLettuceClientConfiguration();
		LettuceConnectionFactory factory = createLettuceConnectionFactory(clientConfig);
		factory.afterPropertiesSet();
		this.redisConnectionFactory = factory;
		return factory;
	}
	
	/**
	 * Returns the connection pool configuration for Lettuce.
	 *
	 * @return the pool configuration from Lettuce properties
	 */
	@Override
	protected Pool getPool() {
		return getProperties().getLettuce().getPool();
	}

	/**
	 * Creates a {@link LettuceConnectionFactory} based on the available Redis configuration.
	 * 
	 * <p>This method determines the appropriate Redis topology (Sentinel, Cluster, or Standalone)
	 * and creates a corresponding connection factory with the provided client configuration.
	 *
	 * @param clientConfiguration the Lettuce client configuration to use
	 * @return a new {@link OrangeLettuceConnectionFactory} instance configured for the detected topology
	 */
	private LettuceConnectionFactory createLettuceConnectionFactory(LettuceClientConfiguration clientConfiguration) {
		if (getSentinelConfig() != null) {
			return new OrangeLettuceConnectionFactory(getSentinelConfig(), clientConfiguration);
		}
		if (getClusterConfiguration() != null) {
			return new OrangeLettuceConnectionFactory(getClusterConfiguration(), clientConfiguration);
		}
		return new OrangeLettuceConnectionFactory(getStandaloneConfig(), clientConfiguration);
	}

	/**
	 * Creates and configures the Lettuce client configuration.
	 * 
	 * <p>This method:
	 * <ul>
	 *   <li>Creates a configuration builder with or without pool support</li>
	 *   <li>Applies common properties (SSL, timeout, client name)</li>
	 *   <li>Applies URL-specific configurations if a URL is provided</li>
	 *   <li>Configures client options including connection timeouts</li>
	 *   <li>Sets client resources</li>
	 *   <li>Applies any custom configurations through builder customizers</li>
	 * </ul>
	 *
	 * @return the fully configured {@link LettuceClientConfiguration}
	 */
	private LettuceClientConfiguration getLettuceClientConfiguration() {
		LettuceClientConfigurationBuilder builder = createBuilder(getProperties().getLettuce().getPool());
		applyProperties(builder);
		if (StringUtils.hasText(getProperties().getUrl())) {
			customizeConfigurationFromUrl(builder);
		}
		builder.clientOptions(createClientOptions());
		builder.clientResources(clientResources);
		builderCustomizers.orderedStream().forEach((customizer) -> customizer.customize(builder));
		return builder.build();
	}

	/**
	 * Creates a Lettuce client configuration builder based on the pool configuration.
	 * 
	 * <p>If connection pooling is enabled, creates a pooled configuration builder.
	 * Otherwise, creates a standard configuration builder.
	 *
	 * @param pool the connection pool properties
	 * @return a builder for Lettuce client configuration
	 */
	private LettuceClientConfigurationBuilder createBuilder(Pool pool) {
		if (isPoolEnabled(pool)) {
			return new PoolBuilderFactory().createBuilder(pool);
		}
		return LettuceClientConfiguration.builder();
	}

	/**
	 * Applies common Redis properties to the Lettuce client configuration builder.
	 * 
	 * <p>This method configures:
	 * <ul>
	 *   <li>SSL support if enabled</li>
	 *   <li>Command timeout</li>
	 *   <li>Shutdown timeout</li>
	 *   <li>Client name</li>
	 * </ul>
	 *
	 * @param builder the builder to apply properties to
	 * @return the updated builder
	 */
	private LettuceClientConfigurationBuilder applyProperties(
			LettuceClientConfiguration.LettuceClientConfigurationBuilder builder) {
		if (getProperties().isSsl()) {
			builder.useSsl();
		}
		if (getProperties().getTimeout() != null) {
			builder.commandTimeout(getProperties().getTimeout());
		}
		if (getProperties().getLettuce() != null) {
			OrangeRedisProperties.Lettuce lettuce = getProperties().getLettuce();
			if (lettuce.getShutdownTimeout() != null && !lettuce.getShutdownTimeout().isZero()) {
				builder.shutdownTimeout(getProperties().getLettuce().getShutdownTimeout());
			}
		}
		if (StringUtils.hasText(getProperties().getClientName())) {
			builder.clientName(getProperties().getClientName());
		}
		return builder;
	}

	/**
	 * Creates the Lettuce client options with configured timeouts.
	 * 
	 * <p>This method configures:
	 * <ul>
	 *   <li>Connection timeout from properties</li>
	 *   <li>Command timeout options</li>
	 * </ul>
	 *
	 * @return the configured {@link ClientOptions}
	 */
	private ClientOptions createClientOptions() {
		ClientOptions.Builder builder = initializeClientOptionsBuilder();
		Duration connectTimeout = getProperties().getConnectTimeout();
		if (connectTimeout != null) {
			builder.socketOptions(SocketOptions.builder().connectTimeout(connectTimeout).build());
		}
		return builder.timeoutOptions(TimeoutOptions.enabled()).build();
	}

	/**
	 * Initializes the appropriate client options builder based on the Redis deployment topology.
	 * 
	 * <p>For cluster deployments, creates a {@link ClusterClientOptions.Builder} with:
	 * <ul>
	 *   <li>Topology refresh options</li>
	 *   <li>Dynamic refresh sources configuration</li>
	 *   <li>Periodic refresh settings</li>
	 *   <li>Adaptive refresh triggers</li>
	 * </ul>
	 * 
	 * <p>For non-cluster deployments, creates a standard {@link ClientOptions.Builder}.
	 *
	 * @return the initialized builder for client options
	 */
	private ClientOptions.Builder initializeClientOptionsBuilder() {
		if (getProperties().getCluster() != null) {
			ClusterClientOptions.Builder builder = ClusterClientOptions.builder();
			Refresh refreshProperties = getProperties().getLettuce().getCluster().getRefresh();
			Builder refreshBuilder = ClusterTopologyRefreshOptions.builder()
				.dynamicRefreshSources(refreshProperties.isDynamicRefreshSources());
			if (refreshProperties.getPeriod() != null) {
				refreshBuilder.enablePeriodicRefresh(refreshProperties.getPeriod());
			}
			if (refreshProperties.isAdaptive()) {
				refreshBuilder.enableAllAdaptiveRefreshTriggers();
			}
			return builder.topologyRefreshOptions(refreshBuilder.build());
		}
		return ClientOptions.builder();
	}

	/**
	 * Customizes the Lettuce client configuration based on the Redis URL.
	 * 
	 * <p>This method parses the URL to extract connection information and applies
	 * appropriate configurations, such as enabling SSL if the URL indicates secure connection.
	 *
	 * @param builder the builder to customize based on URL information
	 */
	private void customizeConfigurationFromUrl(LettuceClientConfiguration.LettuceClientConfigurationBuilder builder) {
		ConnectionInfo connectionInfo = parseUrl(getProperties().getUrl());
		if (connectionInfo.isUseSsl()) {
			builder.useSsl();
		}
	}

	/**
	 * Factory class for creating pooled Lettuce client configuration builders.
	 * 
	 * <p>This class is responsible for creating and configuring connection pool settings
	 * for Lettuce Redis clients. It translates the pool properties from configuration
	 * into Apache Commons Pool2 configuration objects.
	 */
	private static class PoolBuilderFactory {

		/**
		 * Creates a new Lettuce client configuration builder with pooling support.
		 *
		 * @param properties the pool configuration properties
		 * @return a builder configured with the specified pool settings
		 */
		LettuceClientConfigurationBuilder createBuilder(Pool properties) {
			return LettucePoolingClientConfiguration.builder().poolConfig(getPoolConfig(properties));
		}

		/**
		 * Creates and configures a Generic Object Pool configuration based on the provided properties.
		 * 
		 * <p>Configures the following pool settings:
		 * <ul>
		 *   <li>Maximum total connections</li>
		 *   <li>Maximum idle connections</li>
		 *   <li>Minimum idle connections</li>
		 *   <li>Time between eviction runs</li>
		 *   <li>Maximum wait time for connection acquisition</li>
		 * </ul>
		 *
		 * @param properties the pool properties to use for configuration
		 * @return a configured {@link GenericObjectPoolConfig} instance
		 */
		private GenericObjectPoolConfig<?> getPoolConfig(Pool properties) {
			GenericObjectPoolConfig<?> config = new GenericObjectPoolConfig<>();
			config.setMaxTotal(properties.getMaxActive());
			config.setMaxIdle(properties.getMaxIdle());
			config.setMinIdle(properties.getMinIdle());
			if (properties.getTimeBetweenEvictionRuns() != null) {
				config.setTimeBetweenEvictionRuns(properties.getTimeBetweenEvictionRuns());
			}
			if (properties.getMaxWait() != null) {
				config.setMaxWait(properties.getMaxWait());
			}
			return config;
		}
	}
}