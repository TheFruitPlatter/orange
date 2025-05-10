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
 * @author Liang.Zhong
 * @since 1.0.0
 */
public class OrangeSlowOperationProperties {
	
	/**
	 * Triggers a WARN log when execution time > threshold.
	 */
	private Duration slowOperationThreshold = Duration.ofSeconds(1);
	
	private boolean enabled = true;

	public Duration getSlowOperationThreshold() {
		return slowOperationThreshold;
	}

	public void setSlowOperationThreshold(Duration slowOperationThreshold) {
		this.slowOperationThreshold = slowOperationThreshold;
	}

	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}
}
