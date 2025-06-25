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

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.ObjectProvider;
import org.springframework.data.redis.connection.RedisClusterConfiguration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisNode;
import org.springframework.data.redis.connection.RedisPassword;
import org.springframework.data.redis.connection.RedisSentinelConfiguration;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.util.ClassUtils;
import org.springframework.util.StringUtils;

import com.langwuyue.orange.redis.OrangeRedisException;
import com.langwuyue.orange.redis.configuration.OrangeRedisProperties.Pool;

/**
 * Configuration class for setting up Redis connection configurations.
 * 
 * <p>This class handles the configuration of different Redis deployment modes:
 * <ul>
 *   <li>Standalone - Single Redis instance</li>
 *   <li>Sentinel - High availability Redis setup</li>
 *   <li>Cluster - Distributed Redis setup</li>
 * </ul>
 * 
 * <p>It supports configuration through both properties and URL-based configuration,
 * and handles authentication, SSL, and connection pooling settings.
 *
 * @author Liang.Zhong
 * @since 1.0.0
 */
class OrangeRedisConnectionConfiguration {
	/**
	 * Flag indicating whether Apache Commons Pool 2 is available on the classpath.
	 * This is used to determine if connection pooling can be enabled by default.
	 */
	private static final boolean COMMONS_POOL2_AVAILABLE = ClassUtils.isPresent("org.apache.commons.pool2.ObjectPool",
			OrangeRedisConnectionConfiguration.class.getClassLoader());

	/**
	 * Redis properties containing configuration settings for Redis connections.
	 */
	private final OrangeRedisProperties properties;

	/**
	 * Configuration for standalone Redis connection, if provided externally.
	 */
	private final RedisStandaloneConfiguration standaloneConfiguration;

	/**
	 * Configuration for Redis Sentinel connection, if provided externally.
	 */
	private final RedisSentinelConfiguration sentinelConfiguration;

	/**
	 * Configuration for Redis Cluster connection, if provided externally.
	 */
	private final RedisClusterConfiguration clusterConfiguration;
	
	/**
	 * Creates a Redis connection factory based on the configured properties.
	 * 
	 * <p>This method is intended to be overridden by subclasses to provide
	 * specific implementation of the connection factory.
	 *
	 * @return a Redis connection factory instance
	 */
	protected RedisConnectionFactory redisConnectionFactory() {
		return null;
	}

	/**
	 * Retrieves the connection pool configuration.
	 * 
	 * <p>This method is intended to be overridden by subclasses to provide
	 * specific pool configuration.
	 *
	 * @return the connection pool configuration
	 */
	protected Pool getPool() {
		return null;
	}

	/**
	 * Creates a new Redis connection configuration.
	 * 
	 * <p>This constructor initializes the connection configuration with the provided
	 * properties and optional external configurations for different Redis deployment modes.
	 * The external configurations, if provided, take precedence over the properties.
	 *
	 * @param properties the Redis properties containing connection settings
	 * @param standaloneConfigurationProvider provider for standalone Redis configuration
	 * @param sentinelConfigurationProvider provider for Redis Sentinel configuration
	 * @param clusterConfigurationProvider provider for Redis Cluster configuration
	 */
	protected OrangeRedisConnectionConfiguration(OrangeRedisProperties properties,
			ObjectProvider<RedisStandaloneConfiguration> standaloneConfigurationProvider,
			ObjectProvider<RedisSentinelConfiguration> sentinelConfigurationProvider,
			ObjectProvider<RedisClusterConfiguration> clusterConfigurationProvider) {
		this.properties = properties;
		this.standaloneConfiguration = standaloneConfigurationProvider.getIfAvailable();
		this.sentinelConfiguration = sentinelConfigurationProvider.getIfAvailable();
		this.clusterConfiguration = clusterConfigurationProvider.getIfAvailable();
	}

	/**
	 * Creates and returns a Redis standalone configuration.
	 * 
	 * <p>This method creates a standalone Redis configuration based on either:
	 * <ul>
	 *   <li>The externally provided standalone configuration if available</li>
	 *   <li>The URL-based configuration if a URL is provided in properties</li>
	 *   <li>The individual property settings (host, port, etc.)</li>
	 * </ul>
	 *
	 * @return the Redis standalone configuration, never null
	 */
	protected final RedisStandaloneConfiguration getStandaloneConfig() {
		if (this.standaloneConfiguration != null) {
			return this.standaloneConfiguration;
		}
		RedisStandaloneConfiguration config = new RedisStandaloneConfiguration();
		if (StringUtils.hasText(this.properties.getUrl())) {
			ConnectionInfo connectionInfo = parseUrl(this.properties.getUrl());
			config.setHostName(connectionInfo.getHostName());
			config.setPort(connectionInfo.getPort());
			config.setUsername(connectionInfo.getUsername());
			config.setPassword(RedisPassword.of(connectionInfo.getPassword()));
		}
		else {
			config.setHostName(this.properties.getHost());
			config.setPort(this.properties.getPort());
			config.setUsername(this.properties.getUsername());
			config.setPassword(RedisPassword.of(this.properties.getPassword()));
		}
		config.setDatabase(this.properties.getDatabase());
		return config;
	}

	/**
	 * Creates and returns a Redis Sentinel configuration.
	 * 
	 * <p>This method creates a Sentinel configuration based on either:
	 * <ul>
	 *   <li>The externally provided sentinel configuration if available</li>
	 *   <li>The properties-based sentinel configuration if sentinel properties are defined</li>
	 * </ul>
	 * <p>If neither is available, this method returns null.
	 *
	 * @return the Redis Sentinel configuration, or null if not applicable
	 */
	protected final RedisSentinelConfiguration getSentinelConfig() {
		if (this.sentinelConfiguration != null) {
			return this.sentinelConfiguration;
		}
		OrangeRedisProperties.Sentinel sentinelProperties = this.properties.getSentinel();
		if (sentinelProperties != null) {
			RedisSentinelConfiguration config = new RedisSentinelConfiguration();
			config.master(sentinelProperties.getMaster());
			config.setSentinels(createSentinels(sentinelProperties));
			config.setUsername(this.properties.getUsername());
			if (this.properties.getPassword() != null) {
				config.setPassword(RedisPassword.of(this.properties.getPassword()));
			}
			config.setSentinelUsername(sentinelProperties.getUsername());
			if (sentinelProperties.getPassword() != null) {
				config.setSentinelPassword(RedisPassword.of(sentinelProperties.getPassword()));
			}
			config.setDatabase(this.properties.getDatabase());
			return config;
		}
		return null;
	}

	protected final RedisClusterConfiguration getClusterConfiguration() {
		if (this.clusterConfiguration != null) {
			return this.clusterConfiguration;
		}
		if (this.properties.getCluster() == null) {
			return null;
		}
		OrangeRedisProperties.Cluster clusterProperties = this.properties.getCluster();
		RedisClusterConfiguration config = new RedisClusterConfiguration(clusterProperties.getNodes());
		if (clusterProperties.getMaxRedirects() != null) {
			config.setMaxRedirects(clusterProperties.getMaxRedirects());
		}
		config.setUsername(this.properties.getUsername());
		if (this.properties.getPassword() != null) {
			config.setPassword(RedisPassword.of(this.properties.getPassword()));
		}
		return config;
	}

	protected final OrangeRedisProperties getProperties() {
		return this.properties;
	}

	/**
	 * Determines whether connection pooling is enabled for the given pool configuration.
	 * 
	 * <p>Connection pooling is enabled if:
	 * <ul>
	 *   <li>The pool configuration explicitly enables it</li>
	 *   <li>The pool configuration doesn't specify and Commons Pool 2 is available on the classpath</li>
	 * </ul>
	 *
	 * @param pool the pool configuration to check
	 * @return true if connection pooling is enabled, false otherwise
	 */
	protected boolean isPoolEnabled(Pool pool) {
		Boolean enabled = pool.getEnabled();
		return (enabled != null) ? enabled : COMMONS_POOL2_AVAILABLE;
	}

	/**
	 * Creates a list of Redis sentinel nodes from the sentinel configuration.
	 * 
	 * <p>This method converts the string representation of sentinel nodes from the
	 * properties into RedisNode objects that can be used in the sentinel configuration.
	 * If any node string is invalid, an IllegalStateException is thrown.
	 *
	 * @param sentinel the sentinel properties containing node information
	 * @return a list of Redis sentinel nodes
	 * @throws IllegalStateException if any node string is invalid
	 */
	private List<RedisNode> createSentinels(OrangeRedisProperties.Sentinel sentinel) {
		List<RedisNode> nodes = new ArrayList<>();
		for (String node : sentinel.getNodes()) {
			try {
				nodes.add(RedisNode.fromString(node));
			}
			catch (RuntimeException ex) {
				throw new IllegalStateException("Invalid redis sentinel property '" + node + "'", ex);
			}
		}
		return nodes;
	}

	/**
	 * Parses a Redis URL and creates a connection information object.
	 * 
	 * <p>This method parses a Redis URL into its components and validates the format.
	 * It supports both standard Redis ('redis://') and SSL Redis ('rediss://') URLs.
	 * The URL can include authentication information in the format:
	 * <ul>
	 *   <li>redis://[username:password@]host[:port]</li>
	 *   <li>rediss://[username:password@]host[:port]</li>
	 * </ul>
	 *
	 * @param url the Redis URL to parse
	 * @return a ConnectionInfo object containing the parsed connection details
	 * @throws OrangeRedisException if the URL is invalid or uses an unsupported scheme
	 */
	protected ConnectionInfo parseUrl(String url) {
		try {
			URI uri = new URI(url);
			String scheme = uri.getScheme();
			if (!"redis".equals(scheme) && !"rediss".equals(scheme)) {
				throw new OrangeRedisException("Invalid Redis URL " + url);
			}
			boolean useSsl = ("rediss".equals(scheme));
			String username = null;
			String password = null;
			if (uri.getUserInfo() != null) {
				String candidate = uri.getUserInfo();
				int index = candidate.indexOf(':');
				if (index >= 0) {
					username = candidate.substring(0, index);
					password = candidate.substring(index + 1);
				}
				else {
					password = candidate;
				}
			}
			return new ConnectionInfo(uri, useSsl, username, password);
		}
		catch (URISyntaxException ex) {
			throw new OrangeRedisException("Invalid Redis URL " + url, ex);
		}
	}

	/**
	 * Inner class that holds connection information parsed from a Redis URL.
	 * 
	 * <p>This class encapsulates all the connection details extracted from a Redis URL,
	 * including host, port, authentication credentials, and SSL usage flag.
	 */
	static class ConnectionInfo {

		private final URI uri;

		private final boolean useSsl;

		private final String username;

		private final String password;

		/**
		 * Creates a new ConnectionInfo instance with the specified parameters.
		 *
		 * @param uri the parsed URI object
		 * @param useSsl flag indicating whether SSL should be used for the connection
		 * @param username the username for authentication, may be null
		 * @param password the password for authentication, may be null
		 */
		ConnectionInfo(URI uri, boolean useSsl, String username, String password) {
			this.uri = uri;
			this.useSsl = useSsl;
			this.username = username;
			this.password = password;
		}

		/**
		 * Returns whether SSL should be used for the connection.
		 *
		 * @return true if SSL should be used, false otherwise
		 */
		boolean isUseSsl() {
			return this.useSsl;
		}

		/**
		 * Returns the hostname of the Redis server.
		 *
		 * @return the hostname extracted from the URI
		 */
		String getHostName() {
			return this.uri.getHost();
		}

		/**
		 * Returns the port number of the Redis server.
		 *
		 * @return the port number extracted from the URI
		 */
		int getPort() {
			return this.uri.getPort();
		}

		/**
		 * Returns the username for authentication.
		 *
		 * @return the username, may be null if not specified
		 */
		String getUsername() {
			return this.username;
		}

		/**
		 * Returns the password for authentication.
		 *
		 * @return the password, may be null if not specified
		 */
		String getPassword() {
			return this.password;
		}

	}
}