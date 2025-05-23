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
package com.langwuyue.orange.redis.template.global;

import java.util.concurrent.TimeUnit;

import com.langwuyue.orange.redis.annotation.TimeoutUnit;
import com.langwuyue.orange.redis.annotation.global.Delete;
import com.langwuyue.orange.redis.annotation.global.GetExpiration;
import com.langwuyue.orange.redis.annotation.global.SetExpiration;

/**
 * @author Liang.Zhong
 * @since 1.0.0
 */
public interface GlobalOperationsTemplate {
	
	/**
	 * Set the expiration time (configured in {@code RedisKey}).
	 * 
	 * @return True if the expiration time was set successfully, false otherwise.
	 */
	@SetExpiration
	Boolean setExpiration();
	
	/**
	 * Delete a Redis key (configured in {@code RedisKey}).
	 * 
	 * @return True if deleted successfully, false otherwise.
	 */
	@Delete
	Boolean delete();
	
	/**
	 * Get the remaining TTL for the key.
	 * 
	 * @return Returns the remaining TTL in seconds (like Redis' `TTL` command) or milliseconds (like `PTTL`), depending on {@code RedisKey}'s configuration.
	 */
	@GetExpiration
	Long getExpiration();
	
	/**
	 * Get the remaining TTL for the key.
	 * 
	 * @param unit TTL unit
	 * @return Returns the remaining TTL in seconds (like Redis' `TTL` command) or milliseconds (like `PTTL`) , depending on {@code unit}.
	 */
	@GetExpiration
	Long getExpiration(@TimeoutUnit TimeUnit unit);
}
