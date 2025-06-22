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
package com.langwuyue.orange.redis.operations;

import java.util.concurrent.TimeUnit;

/**
 * Base interface for Redis operations providing common key operations.
 * All Redis operation interfaces should extend this interface.
 * 
 * @author Liang.Zhong
 * @since 1.0.0
 */
public interface OrangeRedisOperations {

	/**
	 * Deletes the key from Redis.
	 * Equivalent to Redis DEL command.
	 *
	 * @param key the key to delete
	 * @return true if the key was deleted, false if the key didn't exist
	 */
	Boolean delete(String key);

	/**
	 * Sets a timeout on a key.
	 * Equivalent to Redis EXPIRE command.
	 *
	 * @param key the key to set expiration for
	 * @param expirationTime the duration before the key expires
	 * @param expirationTimeUnit the time unit of the expiration duration
	 * @return true if the timeout was set, false if key doesn't exist or timeout couldn't be set
	 */
	Boolean expire(String key, long expirationTime, TimeUnit expirationTimeUnit);
	
	/**
	 * Gets the remaining time to live of a key with timeout.
	 * Equivalent to Redis TTL command.
	 *
	 * @param key the key to check
	 * @param unit the time unit for the return value
	 * @return the remaining time in the specified time unit, or null if key doesn't exist or has no timeout
	 */
	Long getExpiration(String key, TimeUnit unit);

}