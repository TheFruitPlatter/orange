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
 * Event class representing a failed remove operation for Redis value type.
 * 
 * <p>This event is triggered when a Redis remove operation (similar to DEL command)
 * fails to delete a value. The failure could be due to various reasons such as:
 * <ul>
 *   <li>Connection issues with Redis server</li>
 *   <li>Redis server errors</li>
 *   <li>Insufficient permissions</li>
 *   <li>Other operational exceptions</li>
 * </ul>
 * 
 * <p>This class extends {@link OrangeSetIfAbsentFailedEvent} to reuse its failure
 * reporting structure while specifically representing remove operation failures.
 * It doesn't add new fields but provides more specific semantic meaning for
 * remove operations.
 *
 * <p>This class is used as a parameter type in {@link OrangeRedisValueSetIfAbsentListener#onRemoveFailed(Object)}
 * to provide information about the failed remove operation to event handlers.
 * 
 * @author Liang.Zhong
 * @since 1.0.0
 * @see OrangeSetIfAbsentFailedEvent
 * @see OrangeRedisValueSetIfAbsentListener
 */
public class OrangeRemoveFailedEvent extends OrangeSetIfAbsentFailedEvent {

	/**
	 * Creates a remove failure event with an exception cause.
	 *
	 * @param args Additional arguments used in the remove operation (cannot be null)
	 * @param value The value that failed to be removed (can be null)
	 * @param exception The exception that caused the failure (can be null)
	 * <p>
	 * Delegates to {@link OrangeSetIfAbsentFailedEvent#OrangeSetIfAbsentFailedEvent(Object[], Object, Exception)}
	 * while providing specific semantic meaning for remove operations.
	 */
	public OrangeRemoveFailedEvent(Object[] args, Object value, Exception exception) {
		super(args, value, exception);
	}

	/**
	 * Creates a remove failure event with a custom reason message.
	 *
	 * @param args Additional arguments used in the remove operation (cannot be null)
	 * @param value The value that failed to be removed (can be null)
	 * @param reason Human-readable description of the failure (cannot be null)
	 * <p>
	 * Delegates to {@link OrangeSetIfAbsentFailedEvent#OrangeSetIfAbsentFailedEvent(Object[], Object, String)}
	 * while providing specific semantic meaning for remove operations.
	 * <p>
	 * Use this constructor when the failure wasn't caused by an exception but
	 * still needs to be reported (e.g., when key didn't exist).
	 */
	public OrangeRemoveFailedEvent(Object[] args, Object value, String reason) {
		super(args, value, reason);
	}
}