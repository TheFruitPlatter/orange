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
package com.langwuyue.orange.redis.listener.set;

/**
 * Event representing failed attempts to add members to a Redis set when they already exist.
 *
 * <p>This event is triggered in two scenarios:
 * <ul>
 *   <li>The member already exists in the set (non-exception case)</li>
 *   <li>An exception occurred during the add operation</li>
 * </ul>
 *
 * <p>This class is immutable - all fields are final and the args array is stored
 * as a reference to the original array. Callers should not modify the args array
 * after passing it to the event.
 *
 * <p>Typical usage includes:
 * <ul>
 *   <li>Logging failed add operations</li>
 *   <li>Implementing retry logic</li>
 *   <li>Monitoring set operation patterns</li>
 * </ul>
 *
 * @author Liang.Zhong
 * @since 1.0.0
 */
public class OrangeAddMemberIfAbsentFailedEvent {

	/**
	 * The member that could not be added to the Redis set.
	 * <p>
	 * Contains the object that was attempted to be added to the set when
	 * the operation failed.
	 */
	private Object member;
	
	/**
	 * The reason for the failure, either from the exception or a custom message.
	 * <p>
	 * When constructed with an exception, contains the exception message.
	 * When constructed with a custom reason, contains that reason string.
	 */
	private String reason;
	
	/**
	 * The exception that caused the failure, if any.
	 * <p>
	 * Null when failure was due to member already existing in set.
	 * Non-null when failure was due to an operational exception.
	 */
	private Exception exception;

	/**
	 * The original arguments passed to the add operation.
	 * <p>
	 * Contains the full context of the operation that failed.
	 * The array reference is stored directly - callers should not modify
	 * this array after passing it to the constructor.
	 */
	private Object[] args;

	/**
	 * Creates a failed event when member already exists in set (non-exception case).
	 *
	 * @param args Original operation arguments
	 * @param member The member that couldn't be added
	 * @param reason Description of why the add failed
	 */
	public OrangeAddMemberIfAbsentFailedEvent(Object[] args, Object member, String reason) {
		this.member = member;
		this.args = args;
		this.reason = reason;
	}
	
	/**
	 * Creates a failed event when an exception occurs during add operation.
	 *
	 * @param args Original operation arguments
	 * @param member The member that couldn't be added
	 * @param exception The exception that caused the failure
	 */
	public OrangeAddMemberIfAbsentFailedEvent(Object[] args, Object member, Exception exception) {
		this.member = member;
		this.args = args;
		this.reason = exception.getMessage();
		this.exception = exception;
	}

	/**
	 * Gets the member that couldn't be added to the set.
	 *
	 * @return The member object that failed to be added 
	 */
	public Object getMember() {
		return member;
	}

	/**
	 * Gets the reason for the failure.
	 *
	 * @return The failure reason string 
	 */
	public String getReason() {
		return reason;
	}

	/**
	 * Gets the exception that caused the failure, if any.
	 *
	 * @return The exception object, or null if failure wasn't due to an exception
	 */
	public Exception getException() {
		return exception;
	}

	/**
	 * Gets the original operation arguments.
	 *
	 * @return The arguments array passed to the operation 
	 */
	public Object[] getArgs() {
		return args;
	}
}