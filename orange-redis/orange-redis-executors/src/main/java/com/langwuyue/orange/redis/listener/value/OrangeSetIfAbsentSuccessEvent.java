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
package com.langwuyue.orange.redis.listener.value;

/**
 * Event class representing a successful set-if-absent operation for Redis value type.
 * 
 * <p>This event is triggered when a Redis set-if-absent operation (similar to SETNX command)
 * successfully sets a value because the key did not previously exist. The event contains
 * the value that was set and any additional arguments that were provided during the operation.
 * 
 * <p>This class is used as a parameter type in {@link OrangeRedisValueSetIfAbsentListener#onSuccess(Object)}
 * to provide information about the successful operation to event handlers.
 * 
 * @author Liang.Zhong
 * @since 1.0.0
 * @see OrangeRedisValueSetIfAbsentListener
 */
public class OrangeSetIfAbsentSuccessEvent {
	
	/**
	 * The value that was successfully set in Redis.
	 * <p>
	 * This is the value that was stored when the set-if-absent operation succeeded
	 * because the key did not previously exist.
	 */
	private Object value;
	
	/**
	 * Additional arguments that were provided during the set-if-absent operation.
	 * <p>
	 * May include metadata or configuration parameters that influenced the operation.
	 * Can be empty but never null.
	 */
	private Object[] args;
	
	/**
	 * Creates a new success event for a set-if-absent operation.
	 *
	 * @param args Additional arguments used in the operation (cannot be null)
	 * @param value The value that was successfully set in Redis (can be null)
	 */
	public OrangeSetIfAbsentSuccessEvent(Object[] args, Object value) {
		this.value = value;
		this.args = args;
	}

	/**
	 * Gets the value that was successfully set in Redis.
	 * <p>
	 * May return null if a null value was explicitly set.
	 *
	 * @return The value that was set, or null
	 */
	public Object getValue() {
		return value;
	}

	/**
	 * Gets the additional arguments used in the set-if-absent operation.
	 * <p>
	 * The returned array is guaranteed not to be null, though it may be empty.
	 * The contents depend on the specific operation context.
	 *
	 * @return Array of operation arguments (never null)
	 */
	public Object[] getArgs() {
		return args;
	}
	
}