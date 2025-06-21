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
package com.langwuyue.orange.redis.listener.hash;

/**
 * Event representing a failed operation to add a single field to a Redis HASH if it doesn't exist.
 *
 * <p>This event is fired when an attempt to add a single field to a Redis hash structure
 * fails due to an exception. It contains the original operation arguments, the exception
 * that occurred, and the error message.
 *
 * <p>This event is typically used in conjunction with {@link OrangeRedisHashAddMemberIfAbsentListener}
 * to handle failures in single field additions to Redis hash structures and implement appropriate
 * error handling or retry logic.
 *
 * @see OrangeRedisHashAddMemberIfAbsentListener for the associated listener interface
 * @see OrangeAddMemberIfAbsentSuccessEvent for the corresponding success event
 * @author Liang.Zhong
 * @since 1.0.0
 */
public class OrangeAddMemberIfAbsentFailedEvent {
	
	/**
	 * Original arguments passed to the Redis operation.
	 * <p>Typically contains:
	 * <ul>
	 *   <li>The hash key at index 0</li>
	 *   <li>The field name/object at index 1</li>
	 *   <li>The value to be set at index 2</li>
	 * </ul>
	 */
	private Object[] args;
	
	/**
	 * The exception that caused the operation to fail.
	 * This could be a Redis-specific exception or any other runtime exception
	 * that occurred during the operation.
	 */
	private Exception exception;
	
	/**
	 * The error message extracted from the exception.
	 * This provides a human-readable description of what went wrong.
	 */
	private String message;
	
	/**
	 * Constructs a new failure event with the specified operation arguments and exception.
	 *
	 * @param args The original arguments passed to the Redis operation
	 * @param exception The exception that caused the operation to fail
	 */
	public OrangeAddMemberIfAbsentFailedEvent(Object[] args, Exception exception) {
		super();
		// Store the original operation arguments
		this.args = args;
		// Store the exception that caused the failure
		this.exception = exception;
		// Extract and store the error message from the exception
		this.message = exception.getMessage();
	}

	/**
	 * Returns the original arguments passed to the Redis operation.
	 * 
	 * <p>The returned array typically contains:
	 * <ul>
	 *   <li>The hash key at index 0</li>
	 *   <li>The field name/object at index 1</li>
	 *   <li>The value to be set at index 2</li>
	 * </ul>
	 *
	 * @return An array containing the original arguments of the Redis operation
	 */
	public Object[] getArgs() {
		return args;
	}

	/**
	 * Returns the exception that caused the operation to fail.
	 * 
	 * <p>This exception can be used to determine the specific cause of failure
	 * and implement appropriate error handling or retry logic.
	 *
	 * @return The exception that caused the operation failure
	 */
	public Exception getException() {
		return exception;
	}

	/**
	 * Returns the error message extracted from the exception.
	 * 
	 * <p>This provides a human-readable description of what went wrong during
	 * the operation. Useful for logging and error reporting purposes.
	 *
	 * @return The error message describing the failure cause
	 */
	public String getMessage() {
		return message;
	}
	
}