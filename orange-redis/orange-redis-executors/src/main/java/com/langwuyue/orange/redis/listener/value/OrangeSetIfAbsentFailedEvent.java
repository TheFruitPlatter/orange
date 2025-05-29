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
 * <p>This class is used as a parameter type in {@link OrangeRedisValueSetIfAbsentListener#onFailure(OrangeSetIfAbsentFailedEvent)}
 * to provide information about the failed operation to event handlers.
 * 
 * <p>Example usage:
 * <pre>{@code
 * @Override
 * public void onFailure(OrangeSetIfAbsentFailedEvent event) {
 *     Object value = event.getValue();
 *     String reason = event.getReason();
 *     Exception exception = event.getException();
 *     
 *     logger.warn("Failed to set value: {}, reason: {}", value, reason);
 *     if (exception != null) {
 *         logger.error("Exception details:", exception);
 *     }
 * }
 * }</pre>
 *
 * @author Liang.Zhong
 * @since 1.0.0
 * @see OrangeRedisValueSetIfAbsentListener
 */
public class OrangeSetIfAbsentFailedEvent {
	
	private Object value;
	
	private Object[] args;
	
	private Exception exception;
	
	private String reason;

	public OrangeSetIfAbsentFailedEvent(Object[] args, Object value,Exception exception) {
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

	public Object getValue() {
		return value;
	}

	public Exception getException() {
		return exception;
	}

	public String getReason() {
		return reason;
	}

	public Object[] getArgs() {
		return args;
	}
}