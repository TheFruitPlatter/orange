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
import org.springframework.data.redis.connection.RedisConfiguration;
import org.springframework.data.redis.connection.RedisSentinelConfiguration;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceClientConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;

/**
 * Orange framework's extension of {@link LettuceConnectionFactory} that provides
 * Redis connection factory implementations using the Lettuce Redis client.
 * 
 * <p>This factory supports various Redis deployment topologies:
 * <ul>
 *   <li>Standalone Redis servers</li>
 *   <li>Redis Sentinel configurations</li>
 *   <li>Redis Cluster configurations</li>
 * </ul>
 * 
 * <p>Each deployment topology can be configured with custom client settings
 * through {@link LettuceClientConfiguration}.
 *
 * @author Liang.Zhong
 * @since 1.0.0
 * @see LettuceConnectionFactory
 * @see LettuceClientConfiguration
 */
class OrangeLettuceConnectionFactory extends LettuceConnectionFactory {

	/**
	 * Creates a new {@link OrangeLettuceConnectionFactory} with the given Redis configuration.
	 *
	 * @param configuration the Redis configuration to use
	 */
	public OrangeLettuceConnectionFactory(RedisConfiguration configuration) {
		super(configuration);
	}

	/**
	 * Creates a new {@link OrangeLettuceConnectionFactory} for Redis Cluster with
	 * the given cluster configuration and client configuration.
	 *
	 * @param clusterConfiguration the Redis cluster configuration
	 * @param clientConfig the Lettuce client configuration
	 */
	public OrangeLettuceConnectionFactory(RedisClusterConfiguration clusterConfiguration,
			LettuceClientConfiguration clientConfig) {
		super(clusterConfiguration, clientConfig);
	}

	/**
	 * Creates a new {@link OrangeLettuceConnectionFactory} for Redis Sentinel with
	 * the given sentinel configuration and client configuration.
	 *
	 * @param sentinelConfiguration the Redis sentinel configuration
	 * @param clientConfig the Lettuce client configuration
	 */
	public OrangeLettuceConnectionFactory(RedisSentinelConfiguration sentinelConfiguration,
			LettuceClientConfiguration clientConfig) {
		super(sentinelConfiguration, clientConfig);
	}

	/**
	 * Creates a new {@link OrangeLettuceConnectionFactory} for standalone Redis with
	 * the given standalone configuration and client configuration.
	 *
	 * @param standaloneConfig the Redis standalone configuration
	 * @param clientConfig the Lettuce client configuration
	 */
	public OrangeLettuceConnectionFactory(RedisStandaloneConfiguration standaloneConfig,
			LettuceClientConfiguration clientConfig) {
		super(standaloneConfig, clientConfig);
	}


	
}