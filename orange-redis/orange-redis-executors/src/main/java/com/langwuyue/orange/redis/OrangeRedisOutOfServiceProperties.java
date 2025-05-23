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
package com.langwuyue.orange.redis;

import java.time.Duration;

/**
 * @author Liang.Zhong
 * @since 1.0.0
 */
public class OrangeRedisOutOfServiceProperties {
	
	/**
	 * When Redis health check failures exceed the threshold, operations will throw an exception.
	 */
	private int healthCheckThreshold = 3;
	
	/**
	 * Records an event when Redis ping latency exceeds the configured threshold.
	 * And if the number of high-latency pings reaches {@link #warnConsecutiveHighLatencyCount}, the Redis health checker logs a warning.
	 * And The Circuit Breaker triggers automatically when {@link #maxAllowedHighLatencyCount} is reached.
	 */
	private Duration pingLatencyThreshold = Duration.ofSeconds(3);
	
	/**
	 * The Circuit Breaker triggers automatically when high-latency ping counts exceed the threshold.
	 */
	private int maxAllowedHighLatencyCount = 5;
	
	/**
	 * When the number of high-latency pings reaches the configured threshold, the Redis health checker logs a warning.
	 */
	private int warnConsecutiveHighLatencyCount = 3;
	
	private boolean enabled = false;

	public int getHealthCheckThreshold() {
		return healthCheckThreshold;
	}

	public void setHealthCheckThreshold(int healthCheckThreshold) {
		this.healthCheckThreshold = healthCheckThreshold;
	}

	public Duration getPingLatencyThreshold() {
		return pingLatencyThreshold;
	}

	public void setPingLatencyThreshold(Duration pingLatencyThreshold) {
		this.pingLatencyThreshold = pingLatencyThreshold;
	}

	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	public int getMaxAllowedHighLatencyCount() {
		return maxAllowedHighLatencyCount;
	}

	public void setMaxAllowedHighLatencyCount(int maxAllowedHighLatencyCount) {
		this.maxAllowedHighLatencyCount = maxAllowedHighLatencyCount;
	}

	public int getWarnConsecutiveHighLatencyCount() {
		return warnConsecutiveHighLatencyCount;
	}

	public void setWarnConsecutiveHighLatencyCount(int warnConsecutiveHighLatencyCount) {
		this.warnConsecutiveHighLatencyCount = warnConsecutiveHighLatencyCount;
	}
}
