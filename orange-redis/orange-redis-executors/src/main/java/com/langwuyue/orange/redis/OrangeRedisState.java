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

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Manages the service state of Orange Redis components.
 * This utility class provides thread-safe operations to control and check
 * the service status of Redis-related functionalities.
 *
 * <p>The state is managed through an atomic boolean flag that indicates
 * whether the service is out of service (down) or in service (up).
 * All state transitions are handled atomically to ensure thread safety.</p>
 *
 * @author Liang.Zhong
 * @since 1.0.0
 */
public class OrangeRedisState {
	
	/**
	 * Thread-safe atomic boolean flag that tracks the service state.
	 * When {@code true}, the Redis service is considered out of service (down).
	 * When {@code false}, the Redis service is considered in service (up).
	 * Default state is {@code false} (in service).
	 */
	private static final AtomicBoolean outOfService = new AtomicBoolean(false);
	
	/**
	 * Private constructor to prevent instantiation of this utility class.
	 * This class only provides static methods and should not be instantiated.
	 */
	private OrangeRedisState() {}

	/**
	 * Checks if the Redis service is currently out of service.
	 *
	 * @return {@code true} if the service is down (out of service),
	 *         {@code false} if the service is up and running
	 */
	public static boolean isOutOfService() {
		return outOfService.get();
	}

	/**
	 * Marks the Redis service as out of service (down).
	 * This operation is performed atomically using compare-and-set
	 * to ensure thread safety. The state will only change if
	 * the service is currently up.
	 */
	public static void down() {
		outOfService.compareAndSet(false, true);
	}
	
	/**
	 * Marks the Redis service as in service (up).
	 * This operation is performed atomically using compare-and-set
	 * to ensure thread safety. The state will only change if
	 * the service is currently down.
	 */
	public static void up() {
		outOfService.compareAndSet(true, false);
	}

}