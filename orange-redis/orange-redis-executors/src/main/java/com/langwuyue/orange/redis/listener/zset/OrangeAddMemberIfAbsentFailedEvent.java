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
package com.langwuyue.orange.redis.listener.zset;

/**
 * Represents a failure event when attempting to add a member to a Redis sorted set fails.
 * <p>
 * This event triggers when the 'add if absent' operation completes fails,
 * either due to the member already existing or other Redis-related errors. It contains
 * all relevant information about the failed operation including:
 * <ul>
 *   <li>The member value that failed to be added</li>
 *   <li>The score associated with the member</li>
 *   <li>The reason for failure (either as a message or exception)</li>
 *   <li>The original method arguments</li>
 * </ul>
 *
 * @author Liang.Zhong
 * @since 1.0.0
 */
public class OrangeAddMemberIfAbsentFailedEvent {

	/** The member value that failed to be added to the sorted set */
	private Object value;
	
	/** The score that was attempted to be associated with the member */
	private Double score;
	
	/** Description of why the operation failed */
	private String reason;
	
	/** The exception that caused the failure, if any */
	private Exception exception;

	/** Original method arguments that led to this operation */
	private Object[] args;

	/**
	 * Creates a new failure event with a reason message.
	 *
	 * @param args Original method arguments
	 * @param value The member value that failed to be added
	 * @param score The score associated with the member
	 * @param reason Description of why the operation failed
	 */
	public OrangeAddMemberIfAbsentFailedEvent(Object[] args, Object value, Double score, String reason) {
		this.value = value;
		this.args = args;
		this.score = score;
		this.reason = reason;
	}
	
	/**
	 * Creates a new failure event with an exception.
	 *
	 * @param args Original method arguments
	 * @param value The member value that failed to be added
	 * @param score The score associated with the member
	 * @param exception The exception that caused the failure
	 */
	public OrangeAddMemberIfAbsentFailedEvent(Object[] args, Object value, Double score, Exception exception) {
		this.value = value;
		this.args = args;
		this.score = score;
		this.reason = exception.getMessage();
		this.exception = exception;
	}

	/**
	 * Gets the member value that failed to be added to the sorted set.
	 *
	 * @return The member value
	 */
	public Object getValue() {
		return value;
	}

	/**
	 * Gets the score that was attempted to be associated with the member.
	 *
	 * @return The score value, or null if not specified
	 */
	public Double getScore() {
		return score;
	}

	/**
	 * Gets the reason description for the failure.
	 *
	 * @return The failure reason message, or null if the failure was caused by an exception
	 */
	public String getReason() {
		return reason;
	}

	/**
	 * Gets the exception that caused the failure, if any.
	 *
	 * @return The exception object, or null if the failure was described by a reason message
	 */
	public Exception getException() {
		return exception;
	}

	/**
	 * Gets the original method arguments that led to this operation.
	 *
	 * @return Array of arguments passed to the original method call
	 */
	public Object[] getArgs() {
		return args;
	}
}