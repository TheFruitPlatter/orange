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

import java.util.Properties;
import java.util.concurrent.atomic.AtomicInteger;

import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.Health.Builder;
import org.springframework.boot.actuate.redis.RedisHealthIndicator;
import org.springframework.data.redis.connection.ClusterInfo;
import org.springframework.data.redis.connection.RedisClusterConnection;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.RedisConnectionUtils;

import com.langwuyue.orange.redis.OrangeRedisOutOfServiceProperties;
import com.langwuyue.orange.redis.OrangeRedisState;
import com.langwuyue.orange.redis.logger.OrangeRedisLogger;

public class OrangeRedisHealthIndicator extends RedisHealthIndicator {
	
	private OrangeRedisConnectionConfiguration configuration;
	
	private OrangeRedisLogger logger;
	
	private AtomicInteger healthCheckCounter = new AtomicInteger(0);
	
	private AtomicInteger pingLatencyCounter = new AtomicInteger(0);
	
	private OrangeRedisOutOfServiceProperties properties;
	
	public OrangeRedisHealthIndicator(
		OrangeRedisConnectionConfiguration configuration,
		OrangeRedisLogger logger,
		OrangeRedisProperties properties
	) {
		super(configuration.redisConnectionFactory());
		this.logger = logger;
		this.properties = properties.getOutOfService();
		this.configuration = configuration;
	}
	
	@Override
	protected void doHealthCheck(Builder builder) throws Exception {
		RedisConnection connection = null;
		try {
			connection = RedisConnectionUtils.getConnection(this.configuration.redisConnectionFactory());
			if (connection instanceof RedisClusterConnection) {
				fromClusterInfo(builder, ((RedisClusterConnection) connection).clusterGetClusterInfo());
			}
			else {
				up(builder, connection.info("server"));
			}
			if(!this.properties.isEnabled()) {
				return;
			}
			// Reset health check counter
			this.healthCheckCounter = new AtomicInteger(0);
			
			//----------------------------------------------------
			// ping
			//---------------------------------------------------
			long start = System.currentTimeMillis();
			connection.ping();
			long cost = System.currentTimeMillis() - start;

			if(cost < this.properties.getPingLatencyThreshold().toMillis()) {
				// Reset ping latency counter
				this.pingLatencyCounter = new AtomicInteger(0); 
				// Set Redis state to up
				OrangeRedisState.up();
				return;
			}
			int count = this.pingLatencyCounter.incrementAndGet();
			if(count >= this.properties.getWarnConsecutiveHighLatencyCount()) {
				this.logger.warn(
					"Redis ping latency exceeds threshold: {}ms > {}ms", 
					cost, 
					this.properties.getPingLatencyThreshold().toMillis()
				);
			}
			if(count >= this.properties.getMaxAllowedHighLatencyCount()) {
				// Set Redis state to down
				OrangeRedisState.down();
			}else{
				// Set Redis state to up
				OrangeRedisState.up();
			}
		}catch (Exception e) {
			if(this.properties.isEnabled()) {
				int count = this.healthCheckCounter.incrementAndGet();
				if(count >= this.properties.getHealthCheckThreshold()) {
					// Set Redis state to down
					OrangeRedisState.down();
				}
			}
			this.logger.error("Redis health check failed: " + e.getMessage(), e);
			throw e;
		}finally {
			RedisConnectionUtils.releaseConnection(connection, this.configuration.redisConnectionFactory());
		}
	}
	
	static Builder up(Health.Builder builder, Properties info) {
		builder.withDetail("version", info.getProperty("redis_version"));
		return builder.up();
	}

	static Builder fromClusterInfo(Health.Builder builder, ClusterInfo clusterInfo) {
		builder.withDetail("cluster_size", clusterInfo.getClusterSize());
		builder.withDetail("slots_up", clusterInfo.getSlotsOk());
		builder.withDetail("slots_fail", clusterInfo.getSlotsFail());

		if ("fail".equalsIgnoreCase(clusterInfo.getState())) {
			return builder.down();
		}
		else {
			return builder.up();
		}
	}
}
