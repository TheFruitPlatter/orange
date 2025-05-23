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
 * @author Liang.Zhong
 * @since 1.0.0
 */
public class OrangeDefaultExpirationTimeAutoInitializer implements OrangeExpirationTimeAutoInitializer {
	
	private OrangeAutoRenewProperties properties;
	
	public OrangeDefaultExpirationTimeAutoInitializer(OrangeAutoRenewProperties properties) {
		this.properties = properties;
	}

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
