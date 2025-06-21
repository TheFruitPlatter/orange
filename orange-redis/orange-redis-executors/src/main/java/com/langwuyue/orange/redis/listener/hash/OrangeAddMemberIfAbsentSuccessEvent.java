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
 * Event representing a successful operation to add a single field to a Redis HASH if it doesn't exist.
 *
 * <p>This event is fired when a single field is successfully added to a Redis hash structure
 * using a conditional addition operation (only if the field doesn't already exist).
 * It contains the original arguments passed to the Redis operation.
 *
 * <p>This event is typically used in conjunction with {@link OrangeRedisHashAddMemberIfAbsentListener}
 * to handle successful single field additions to Redis hash structures.
 *
 * @see OrangeRedisHashAddMemberIfAbsentListener for the associated listener interface
 * @see OrangeAddMemberIfAbsentFailedEvent for the corresponding failure event
 * @author Liang.Zhong
 * @since 1.0.0
 */
public class OrangeAddMemberIfAbsentSuccessEvent {
	
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
	 * Constructs a new success event with the specified operation arguments.
	 *
	 * @param args Original arguments passed to the Redis operation
	 */
	public OrangeAddMemberIfAbsentSuccessEvent(Object[] args) {
		// Store the original operation arguments
		this.args = args;
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
}