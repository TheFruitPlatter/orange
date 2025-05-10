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
 * copy {@code org.springframework.boot.autoconfigure.data.redis.LettuceConnectionConfiguration}
 * 
 * @author Liang.Zhong
 * @since 1.0.0
 */
@Configuration(proxyBeanMethods = false)
@ConditionalOnClass(RedisClient.class)
@ConditionalOnProperty(name = "orange.redis.client-type", havingValue = "lettuce", matchIfMissing = true)
class OrangeLettuceConnectionConfiguration extends OrangeRedisConnectionConfiguration {
	
	private ObjectProvider<LettuceClientConfigurationBuilderCustomizer> builderCustomizers;
	
	private ClientResources clientResources;
	
	private LettuceConnectionFactory redisConnectionFactory;
	
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
	
	@Override
	protected Pool getPool() {
		return getProperties().getLettuce().getPool();
	}

	private LettuceConnectionFactory createLettuceConnectionFactory(LettuceClientConfiguration clientConfiguration) {
		if (getSentinelConfig() != null) {
			return new OrangeLettuceConnectionFactory(getSentinelConfig(), clientConfiguration);
		}
		if (getClusterConfiguration() != null) {
			return new OrangeLettuceConnectionFactory(getClusterConfiguration(), clientConfiguration);
		}
		return new OrangeLettuceConnectionFactory(getStandaloneConfig(), clientConfiguration);
	}

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

	private LettuceClientConfigurationBuilder createBuilder(Pool pool) {
		if (isPoolEnabled(pool)) {
			return new PoolBuilderFactory().createBuilder(pool);
		}
		return LettuceClientConfiguration.builder();
	}

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

	private ClientOptions createClientOptions() {
		ClientOptions.Builder builder = initializeClientOptionsBuilder();
		Duration connectTimeout = getProperties().getConnectTimeout();
		if (connectTimeout != null) {
			builder.socketOptions(SocketOptions.builder().connectTimeout(connectTimeout).build());
		}
		return builder.timeoutOptions(TimeoutOptions.enabled()).build();
	}

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

	private void customizeConfigurationFromUrl(LettuceClientConfiguration.LettuceClientConfigurationBuilder builder) {
		ConnectionInfo connectionInfo = parseUrl(getProperties().getUrl());
		if (connectionInfo.isUseSsl()) {
			builder.useSsl();
		}
	}

	private static class PoolBuilderFactory {

		LettuceClientConfigurationBuilder createBuilder(Pool properties) {
			return LettucePoolingClientConfiguration.builder().poolConfig(getPoolConfig(properties));
		}

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
