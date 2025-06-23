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
package com.langwuyue.orange.redis.registry;

import java.time.Duration;

/**
 * Configuration properties for slow Redis operation monitoring.
 * 
 * This class defines the configuration properties used to monitor and log slow Redis
 * operations. It allows setting a threshold duration beyond which operations are
 * considered "slow" and enables/disables the monitoring functionality.
 * 
 * These properties can be configured through application configuration to adjust
 * the sensitivity of slow operation detection based on the specific requirements
 * of the application environment.
 * 
 * @author Liang.Zhong
 * @since 1.0.0
 */
public class OrangeSlowOperationProperties {
	
	/**
	 * The threshold duration beyond which a Redis operation is considered slow.
	 * Triggers a WARN log when execution time exceeds this threshold.
	 * Default value is 1 second.
	 */
	private Duration slowOperationThreshold = Duration.ofSeconds(1);
	
	/**
	 * Flag to enable or disable slow operation monitoring.
	 * When set to false, no slow operation monitoring or logging will occur.
	 * Default value is true (enabled).
	 */
	private boolean enabled = true;

	/**
	 * Gets the threshold duration for slow operations.
	 * 
	 * @return The duration threshold beyond which operations are considered slow
	 */
	public Duration getSlowOperationThreshold() {
		return slowOperationThreshold;
	}

	/**
	 * Sets the threshold duration for slow operations.
	 * 
	 * @param slowOperationThreshold The duration threshold to set
	 */
	public void setSlowOperationThreshold(Duration slowOperationThreshold) {
		this.slowOperationThreshold = slowOperationThreshold;
	}

	/**
	 * Checks if slow operation monitoring is enabled.
	 * 
	 * @return true if slow operation monitoring is enabled, false otherwise
	 */
	public boolean isEnabled() {
		return enabled;
	}

	/**
	 * Enables or disables slow operation monitoring.
	 * 
	 * @param enabled true to enable monitoring, false to disable
	 */
	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}
}