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

import org.springframework.data.redis.connection.RedisClusterConfiguration;
import org.springframework.data.redis.connection.RedisSentinelConfiguration;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.jedis.JedisClientConfiguration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;

/**
 * Orange framework's extension of Spring Data Redis {@link JedisConnectionFactory}.
 * 
 * <p>This factory creates Redis connections using the Jedis client library and supports
 * all Redis deployment topologies:
 * <ul>
 *   <li>Standalone - Single Redis server instance</li>
 *   <li>Sentinel - High availability Redis setup with master-slave replication and automatic failover</li>
 *   <li>Cluster - Horizontally scaled Redis setup with data sharding across multiple nodes</li>
 * </ul>
 * 
 * <p>The factory can be configured with different client configurations to customize connection
 * properties such as timeouts, SSL settings, and connection pooling.
 *
 * @author Liang.Zhong
 * @since 1.0.0
 * @see JedisConnectionFactory
 * @see JedisClientConfiguration
 */
class OrangeJedisConnectionFactory extends JedisConnectionFactory {

	/**
	 * Creates a new {@link OrangeJedisConnectionFactory} with default settings.
	 * 
	 * <p>This constructor uses default connection settings and is suitable for connecting
	 * to a Redis server running on localhost with default port (6379).
	 */
	public OrangeJedisConnectionFactory() {
		super();
		
	}

	/**
	 * Creates a new {@link OrangeJedisConnectionFactory} for connecting to a Redis Cluster
	 * with custom client configuration.
	 * 
	 * <p>This constructor is suitable for connecting to a Redis Cluster deployment
	 * with customized client settings such as timeouts, SSL, and connection pooling.
	 *
	 * @param clusterConfig the Redis Cluster configuration
	 * @param clientConfig the Jedis client configuration
	 */
	public OrangeJedisConnectionFactory(RedisClusterConfiguration clusterConfig,
			JedisClientConfiguration clientConfig) {
		super(clusterConfig, clientConfig);
		
	}

	/**
	 * Creates a new {@link OrangeJedisConnectionFactory} for connecting to a Redis Cluster
	 * with default client configuration.
	 * 
	 * <p>This constructor is suitable for connecting to a Redis Cluster deployment
	 * using default client settings.
	 *
	 * @param clusterConfig the Redis Cluster configuration
	 */
	public OrangeJedisConnectionFactory(RedisClusterConfiguration clusterConfig) {
		super(clusterConfig);
		
	}

	/**
	 * Creates a new {@link OrangeJedisConnectionFactory} for connecting to a Redis Sentinel
	 * deployment with custom client configuration.
	 * 
	 * <p>This constructor is suitable for connecting to a Redis Sentinel deployment
	 * with customized client settings such as timeouts, SSL, and connection pooling.
	 * Redis Sentinel provides high availability through master-slave replication and
	 * automatic failover.
	 *
	 * @param sentinelConfig the Redis Sentinel configuration
	 * @param clientConfig the Jedis client configuration
	 */
	public OrangeJedisConnectionFactory(RedisSentinelConfiguration sentinelConfig,
			JedisClientConfiguration clientConfig) {
		super(sentinelConfig, clientConfig);
		
	}

	/**
	 * Creates a new {@link OrangeJedisConnectionFactory} for connecting to a Redis Sentinel
	 * deployment with default client configuration.
	 * 
	 * <p>This constructor is suitable for connecting to a Redis Sentinel deployment
	 * using default client settings. Redis Sentinel provides high availability through
	 * master-slave replication and automatic failover.
	 *
	 * @param sentinelConfig the Redis Sentinel configuration
	 */
	public OrangeJedisConnectionFactory(RedisSentinelConfiguration sentinelConfig) {
		super(sentinelConfig);
		
	}

	/**
	 * Creates a new {@link OrangeJedisConnectionFactory} for connecting to a standalone Redis server
	 * with custom client configuration.
	 * 
	 * <p>This constructor is suitable for connecting to a single Redis server instance
	 * with customized client settings such as timeouts, SSL, and connection pooling.
	 *
	 * @param standaloneConfig the Redis standalone configuration
	 * @param clientConfig the Jedis client configuration
	 */
	public OrangeJedisConnectionFactory(RedisStandaloneConfiguration standaloneConfig,
			JedisClientConfiguration clientConfig) {
		super(standaloneConfig, clientConfig);
		
	}

	/**
	 * Creates a new {@link OrangeJedisConnectionFactory} for connecting to a standalone Redis server
	 * with default client configuration.
	 * 
	 * <p>This constructor is suitable for connecting to a single Redis server instance
	 * using default client settings.
	 *
	 * @param standaloneConfig the Redis standalone configuration
	 */
	public OrangeJedisConnectionFactory(RedisStandaloneConfiguration standaloneConfig) {
		super(standaloneConfig);
		
	}

	
}