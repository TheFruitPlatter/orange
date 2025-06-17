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
package com.langwuyue.orange.redis.executor.multiplelocks;

import java.util.concurrent.TimeUnit;

import com.langwuyue.orange.redis.context.OrangeRedisContext.Key;
import com.langwuyue.orange.redis.timer.OrangeAutoRenewProperties;

/**
 * Default implementation of {@link OrangeExpirationTimeAutoInitializer} that provides
 * intelligent expiration time calculation for Redis keys.
 * 
 * <p>This implementation addresses several challenges in distributed key expiration management:
 * <ul>
 *   <li><b>Load Balancing:</b> Randomizes initial expiration times to prevent renewal task surges</li>
 *   <li><b>Renewal Optimization:</b> Calculates expiration times based on renewal thresholds</li>
 *   <li><b>System Stability:</b> Ensures keys don't expire before they can be renewed</li>
 * </ul>
 * 
 *
 * @author Liang.Zhong
 * @since 1.0.0
 * @see OrangeExpirationTimeAutoInitializer
 * @see OrangeAutoRenewProperties
 * @see <a href="https://orange.langwuyue.com/redis/advanced/multiple-locks">Orange Redis Multiple Locks Documentation</a>
 */
public class OrangeDefaultExpirationTimeAutoInitializer implements OrangeExpirationTimeAutoInitializer {
	
	/**
	 * Configuration properties for auto-renewal behavior.
	 */
	private OrangeAutoRenewProperties properties;
	
	/**
	 * Constructs a new default expiration time initializer with the specified auto-renewal properties.
	 * 
	 * <p>The provided properties determine key behaviors such as:
	 * <ul>
	 *   <li>Default initial expiration value</li>
	 *   <li>Maximum possible expiration time</li>
	 *   <li>Timing wheel configuration for renewal scheduling</li>
	 * </ul>
	 *
	 * @param properties the configuration properties for auto-renewal behavior
	 */
	public OrangeDefaultExpirationTimeAutoInitializer(OrangeAutoRenewProperties properties) {
		this.properties = properties;
	}

	/**
	 * Initializes a Redis key with an optimized expiration time based on the renewal threshold.
	 * 
	 * <p>This implementation uses two key strategies:
	 * 
	 * <p><b>1. Load Distribution:</b> To prevent renewal task surges when many keys expire simultaneously,
	 * the initial expiration time is randomized within the range [autoInitValue, maxExpirationTime].
	 * This distributes renewal operations over time, reducing system load spikes.
	 * 
	 * <p><b>2. Renewal Threshold Optimization:</b> The final expiration time is calculated to ensure
	 * that keys are renewed before they expire. The formula used is:
	 * <pre>
	 * expirationTime = (autoInitValue * renewThreshold) / (renewThreshold - 1)
	 * </pre>
	 * 
	 * <p>With this formula, the system will auto-renew a key when its remaining time is less than
	 * (expirationTime / renewThreshold). For example, with renewThreshold=3 and autoInitValue=10s,
	 * the calculated expirationTime would be 15s, and the system would renew the key when it has
	 * less than 5s remaining.
	 *
	 * @param originKey the original Redis key with its initial settings
	 * @param renewThreshold the threshold factor for determining when to renew (must be > 1)
	 * @return a new Key with optimized expiration time settings (always in SECONDS time unit)
	 */
	@Override
	public Key init(Key originKey, int renewThreshold) {
		// To avoid a sudden surge of renewal tasks at the same time (when all keys share the same expiration time),
		// the initial expiration time (autoInitValue) is randomized within the range [autoInitValue, maxExpirationTime].
		// This spreads out renewal operations over time, reducing system load spikes.
		long maxExpirationTime = properties.getWheelSize() * properties.getTickDuration().getSeconds();
		long autoInitValue = properties.getAutoInitValue().getSeconds();
		if(autoInitValue < maxExpirationTime) {
			autoInitValue = Math.round(Math.random() * (maxExpirationTime - properties.getAutoInitValue().getSeconds()) + properties.getAutoInitValue().getSeconds());	
		}
		
		// System auto-renews if remaining time < (expirationTime / renewThreshold).
		// autoInitValue is derived as: (expirationTime * renewThreshold) / (renewThreshold - 1).
		//
		// Example (renewThreshold=3):
		//   autoInitValue=10s → expirationTime=15s.
		long expirationTime = (autoInitValue * renewThreshold) / (renewThreshold - 1);
		
		return new Key(originKey.getOriginalKey(),originKey.getValue(),expirationTime,TimeUnit.SECONDS);
	}
}