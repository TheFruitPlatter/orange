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
 * Event class representing a failed set-if-absent operation for Redis value type.
 * 
 * <p>This event is triggered when a Redis set-if-absent operation (similar to SETNX command)
 * fails to set a value. The failure could be due to various reasons such as:
 * <ul>
 *   <li>The key already exists in Redis</li>
 *   <li>Connection issues with Redis server</li>
 *   <li>Redis server errors</li>
 *   <li>Other operational exceptions</li>
 * </ul>
 * 
 * <p>The event contains the value that was attempted to be set, any additional arguments
 * that were provided during the operation, the exception that caused the failure (if available),
 * and a reason message explaining the failure.
 * 
 * <p>This class is used as a parameter type in {@link OrangeRedisValueSetIfAbsentListener#onFailure(Object)}
 * to provide information about the failed operation to event handlers.
 * 
 * @author Liang.Zhong
 * @since 1.0.0
 * @see OrangeRedisValueSetIfAbsentListener
 */
public class OrangeSetIfAbsentFailedEvent {
	
	/**
	 * The value that failed to be set in Redis.
	 * <p>
	 * This is the value that was attempted to be stored when the set-if-absent operation failed.
	 * Can be null if attempting to set a null value.
	 */
	private Object value;
	
	/**
	 * Additional arguments that were provided during the failed operation.
	 * <p>
	 * May include metadata or configuration parameters that influenced the operation.
	 * Can be empty but never null.
	 */
	private Object[] args;
	
	/**
	 * The exception that caused the operation to fail.
	 * <p>
	 * May be null if the failure wasn't caused by an exception (e.g., key already exists).
	 */
	private Exception exception;
	
	/**
	 * Human-readable description of the failure reason.
	 * <p>
	 * Typically contains the exception message when failure was caused by an exception,
	 * or a custom description for other failure scenarios.
	 * Never null.
	 */
	private String reason;

	/**
	 * Creates a new failure event with an exception cause.
	 *
	 * @param args Additional arguments used in the operation (cannot be null)
	 * @param value The value that failed to be set (can be null)
	 * @param exception The exception that caused the failure (can be null)
	 * <p>
	 * The reason field will be initialized with the exception's message if available,
	 * or an empty string if the exception is null.
	 */
	public OrangeSetIfAbsentFailedEvent(Object[] args, Object value, Exception exception) {
		this.args = args;
		this.value = value;
		this.exception = exception;
		this.reason = exception.getMessage();
	}
	
	public OrangeSetIfAbsentFailedEvent(Object[] args, Object value, String reason) {
		this.args = args;
		this.value = value;
		this.reason = reason;
	}

	/**
	 * Gets the value that failed to be set in Redis.
	 * <p>
	 * This is the value that was attempted to be stored when the operation failed.
	 * May return null if attempting to set a null value.
	 *
	 * @return The attempted value or null
	 */
	public Object getValue() {
		return value;
	}

	/**
	 * Gets the exception that caused the operation to fail.
	 * <p>
	 * Returns null if the failure wasn't caused by an exception
	 * (e.g., when key already exists).
	 *
	 * @return The exception or null
	 */
	public Exception getException() {
		return exception;
	}

	/**
	 * Gets the human-readable description of the failure reason.
	 * <p>
	 * The returned string is never null. When failure was caused by an exception,
	 * this typically contains the exception message.
	 *
	 * @return Non-null description of the failure reason
	 */
	public String getReason() {
		return reason;
	}

	/**
	 * Gets the additional arguments used in the operation.
	 * <p>
	 * The returned array is never null, though it may be empty.
	 * The contents depend on the specific operation context.
	 *
	 * @return Array of operation arguments (never null)
	 */
	public Object[] getArgs() {
		return args;
	}
}