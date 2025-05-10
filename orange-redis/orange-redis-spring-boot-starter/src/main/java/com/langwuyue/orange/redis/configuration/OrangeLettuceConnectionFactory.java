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
 * @author Liang.Zhong
 * @since 1.0.0
 */
class OrangeLettuceConnectionFactory extends LettuceConnectionFactory {

	public OrangeLettuceConnectionFactory(RedisConfiguration configuration) {
		super(configuration);
	}

	public OrangeLettuceConnectionFactory(RedisClusterConfiguration clusterConfiguration,
			LettuceClientConfiguration clientConfig) {
		super(clusterConfiguration, clientConfig);
	}



	public OrangeLettuceConnectionFactory(RedisSentinelConfiguration sentinelConfiguration,
			LettuceClientConfiguration clientConfig) {
		super(sentinelConfiguration, clientConfig);
	}


	public OrangeLettuceConnectionFactory(RedisStandaloneConfiguration standaloneConfig,
			LettuceClientConfiguration clientConfig) {
		super(standaloneConfig, clientConfig);
	}


	
}
