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

import com.langwuyue.orange.redis.context.OrangeRedisContext.Key;

/**
 * Interface for automatically initializing expiration times for Redis keys.
 * 
 * <p>This interface defines the contract for components that handle automatic initialization
 * of expiration times for Redis keys. 
 * 
 * 
 * @author Liang.Zhong
 * @since 1.0.0
 * @see Key
 * @see OrangeDefaultExpirationTimeAutoInitializer
 */
public interface OrangeExpirationTimeAutoInitializer {
	
	/**
	 * Initializes a Redis key with an appropriate expiration time based on the renewal threshold.
	 * 
	 * <p>This method takes an original Redis key and a renewal threshold value, then calculates
	 * and sets an appropriate expiration time for the key. The calculation typically ensures that
	 * the expiration time is sufficient to accommodate the renewal process, preventing keys from
	 * expiring before they can be renewed.
	 * 
	 *
	 * @param originKey the original Redis key with its initial expiration settings
	 * @param renewThreshold auto-renews if remaining TTL &lt; (TTL / renewThreshold).
	 * @return a new or modified Key with appropriate expiration time settings
	 */
	Key init(Key originKey, int renewThreshold);

}