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

/**
 * Enhanced Redis health indicator for Orange Redis that extends Spring Boot's RedisHealthIndicator.
 * 
 * <p>This health indicator provides advanced monitoring capabilities for Redis connections including:
 * <ul>
 *   <li>Latency monitoring through ping operations</li>
 *   <li>Consecutive failure tracking</li>
 *   <li>Cluster state monitoring</li>
 *   <li>Automatic service state management</li>
 * </ul>
 * 
 * <p>The indicator supports both standalone Redis and Redis Cluster configurations. It maintains
 * counters for health check failures and high latency occurrences, which can trigger automatic
 * service state changes when thresholds are exceeded.
 * 
 * <p>Key features:
 * <ul>
 *   <li>Configurable latency thresholds for ping operations</li>
 *   <li>Consecutive failure counting for health checks</li>
 *   <li>Automatic state management through {@link OrangeRedisState}</li>
 *   <li>Detailed logging of health status changes</li>
 *   <li>Support for both standalone and cluster Redis deployments</li>
 * </ul>
 *
 * @author Liang.Zhong
 * @see RedisHealthIndicator
 * @see OrangeRedisState
 * @see OrangeRedisOutOfServiceProperties
 */
public class OrangeRedisHealthIndicator extends RedisHealthIndicator {
	
	/** 
	 * Redis connection configuration that provides access to the Redis connection factory.
	 */
	private OrangeRedisConnectionConfiguration configuration;
	
	/**
	 * Logger for Redis-specific logging with contextual information.
	 */
	private OrangeRedisLogger logger;
	
	/**
	 * Counter tracking consecutive health check failures.
	 * Reset to zero when a health check succeeds.
	 */
	private AtomicInteger healthCheckCounter = new AtomicInteger(0);
	
	/**
	 * Counter tracking consecutive high latency ping operations.
	 * Reset to zero when a ping operation completes within the acceptable threshold.
	 */
	private AtomicInteger pingLatencyCounter = new AtomicInteger(0);
	
	/**
	 * Configuration properties for out-of-service detection and management.
	 * Contains thresholds for latency and failure counts.
	 */
	private OrangeRedisOutOfServiceProperties properties;
	
	/**
	 * Constructs a new OrangeRedisHealthIndicator with the specified configuration.
	 * 
	 * <p>This constructor initializes the health indicator with the necessary components
	 * for monitoring Redis health status. It performs the following operations:
	 * <ul>
	 *   <li>Initializes the parent RedisHealthIndicator with the Redis connection factory</li>
	 *   <li>Sets up the logger for health status monitoring</li>
	 *   <li>Extracts out-of-service properties from the main Redis properties</li>
	 *   <li>Stores the Redis connection configuration for later use</li>
	 * </ul>
	 *
	 * @param configuration the Redis connection configuration providing access to the connection factory
	 * @param logger the Redis-specific logger for contextual logging
	 * @param properties the main Redis properties containing out-of-service configuration
	 * 
	 * @see RedisHealthIndicator
	 * @see OrangeRedisConnectionConfiguration
	 * @see OrangeRedisProperties
	 * @see OrangeRedisOutOfServiceProperties
	 */
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
	
	/**
	 * Performs the actual Redis health check with enhanced monitoring capabilities.
	 * 
	 * <p>This method extends the basic Redis health check with additional features:
	 * <ul>
	 *   <li>Checks Redis server or cluster status</li>
	 *   <li>Performs latency monitoring through ping operations</li>
	 *   <li>Tracks consecutive failures and high latency occurrences</li>
	 *   <li>Manages Redis service state based on configured thresholds</li>
	 *   <li>Provides detailed logging of health status changes</li>
	 * </ul>
	 * 
	 * <p>The health check process includes:
	 * <ol>
	 *   <li>Obtaining a Redis connection from the connection factory</li>
	 *   <li>Checking cluster status (if in cluster mode) or server info (standalone)</li>
	 *   <li>Performing a ping operation to measure latency</li>
	 *   <li>Comparing latency against configured thresholds</li>
	 *   <li>Updating failure counters and service state accordingly</li>
	 * </ol>
	 * 
	 * @param builder the Health builder used to construct the health response
	 * @throws Exception if the health check fails or encounters an error
	 * 
	 * @see Health.Builder
	 * @see OrangeRedisState
	 * @see OrangeRedisOutOfServiceProperties
	 */
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
			// Latency monitoring through ping operation
			// Measures the time taken for a Redis PING command and compares
			// against configured thresholds to detect performance issues
			//---------------------------------------------------
			long start = System.currentTimeMillis();
			connection.ping();
			long cost = System.currentTimeMillis() - start;

			// Check if the ping latency is within acceptable threshold
			if(cost < this.properties.getPingLatencyThreshold().toMillis()) {
				// Reset ping latency counter when latency is normal
				// This helps prevent false positives from temporary spikes
				this.pingLatencyCounter = new AtomicInteger(0); 
				// Mark Redis service as available since latency is normal
				OrangeRedisState.up();
				return;
			}

			// Increment the counter for consecutive high latency occurrences
			int count = this.pingLatencyCounter.incrementAndGet();

			// Log a warning if consecutive high latency count reaches warning threshold
			// This helps identify potential performance degradation early
			if(count >= this.properties.getWarnConsecutiveHighLatencyCount()) {
				this.logger.warn(
					"Redis ping latency exceeds threshold: {}ms > {}ms", 
					cost, 
					this.properties.getPingLatencyThreshold().toMillis()
				);
			}

			// Check if consecutive high latency count exceeds maximum allowed threshold
			if(count >= this.properties.getMaxAllowedHighLatencyCount()) {
				// Mark Redis service as unavailable due to persistent high latency
				OrangeRedisState.down();
			}else{
				// Keep Redis service marked as available since high latency count
				// hasn't reached critical threshold yet
				OrangeRedisState.up();
			}
		}catch (Exception e) {
			// Exception handling for health check failures
			if(this.properties.isEnabled()) {
				// Increment the counter for consecutive health check failures
				int count = this.healthCheckCounter.incrementAndGet();
				
				// If consecutive failures exceed the configured threshold,
				// mark the Redis service as unavailable to prevent cascading failures
				if(count >= this.properties.getHealthCheckThreshold()) {
					// Set Redis state to down, which will affect service availability decisions
					OrangeRedisState.down();
				}
			}
			// Log the error with detailed exception information for troubleshooting
			this.logger.error("Redis health check failed: " + e.getMessage(), e);
			// Re-throw the exception to ensure the health check is properly reported as failed
			throw e;
		}finally {
			RedisConnectionUtils.releaseConnection(connection, this.configuration.redisConnectionFactory());
		}
	}
	
	/**
	 * Updates the health builder with Redis server information and marks it as UP.
	 * 
	 * <p>This static helper method adds the following details to the health response:
	 * <ul>
	 *   <li>Redis version from the server info properties</li>
	 * </ul>
	 * 
	 * @param builder the Health builder used to construct the health response
	 * @param info the Redis server info properties containing version and other details
	 * @return the updated Health builder marked as UP
	 * 
	 * @see Health.Builder
	 * @see Properties
	 */
	static Builder up(Health.Builder builder, Properties info) {
		builder.withDetail("version", info.getProperty("redis_version"));
		return builder.up();
	}

	/**
	 * Updates the health builder with Redis cluster information and determines the health status.
	 * 
	 * <p>This static helper method adds the following details to the health response:
	 * <ul>
	 *   <li>Cluster size (number of nodes)</li>
	 *   <li>Number of slots in OK state</li>
	 *   <li>Number of slots in FAIL state</li>
	 * </ul>
	 * 
	 * <p>The method marks the health status as:
	 * <ul>
	 *   <li>DOWN if the cluster state is "fail"</li>
	 *   <li>UP otherwise</li>
	 * </ul>
	 * 
	 * @param builder the Health builder used to construct the health response
	 * @param clusterInfo the Redis cluster info containing size and slot status
	 * @return the updated Health builder with appropriate status (UP or DOWN)
	 * 
	 * @see Health.Builder
	 * @see ClusterInfo
	 */
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