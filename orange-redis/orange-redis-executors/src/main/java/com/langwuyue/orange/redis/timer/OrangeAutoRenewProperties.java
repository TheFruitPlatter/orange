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
package com.langwuyue.orange.redis.timer;

import java.time.Duration;

/**
 * Configuration for Auto-Renew
 * 
 * @author Liang.Zhong
 * @since 1.0.0
 *
 */
public class OrangeAutoRenewProperties {
	
	/**
	 * The duration of each tick in the timing wheel, defining the minimum time resolution.
	 * 
	 * <p>Example: If set to 1000ms, the wheel can schedule tasks with 1000ms precision.
	 * 
	 */
	private Duration tickDuration = Duration.ofSeconds(1);
	
	/**
	 * When {@link com.langwuyue.orange.redis.annotation.AutoRenew#autoInitKeyExpirationTime()} is true, 
	 * the auto-renew mechanism will: 
	 * 1. Ignore {@link com.langwuyue.orange.redis.annotation.OrangeRedisKey#expirationTime()}
	 * 2. Calculate a new TTL based on {@code autoInitValue} 
	 */
	private Duration autoInitValue = Duration.ofSeconds(10);
	
	/**
	 * The auto-renew mechanism uses a timing wheel, 
	 * and this field configures the number of buckets in the wheel.  
	 */
	private int wheelSize = 60;
	
	private boolean enabled = true;

	public Duration getTickDuration() {
		return tickDuration;
	}

	public void setTickDuration(Duration tickDuration) {
		this.tickDuration = tickDuration;
	}

	public int getWheelSize() {
		return wheelSize;
	}

	public void setWheelSize(int wheelSize) {
		this.wheelSize = wheelSize;
	}

	public Duration getAutoInitValue() {
		return autoInitValue;
	}

	public void setAutoInitValue(Duration autoInitValue) {
		this.autoInitValue = autoInitValue;
	}

	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

}
