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

import java.util.Collection;
import java.util.Map;

/**
 * Event representing the result of a batch operation to add multiple fields to a Redis HASH if they don't exist.
 *
 * <p>This event captures the outcome of attempting to add multiple fields to a Redis hash in a single operation.
 *
 * <p>This event is typically fired after a batch hash field addition operation completes, regardless of
 * whether all fields were successfully added or some failed.
 *
 * @see OrangeRedisHashAddMembersIfAbsentListener for the associated listener interface
 * @see OrangRemoveMembersFailedEvent for the corresponding failure event
 * @author Liang.Zhong
 * @since 1.0.0
 */
public class OrangeAddMembersIfAbsentEvent {
	
	/**
	 * Collection of fields that were successfully added to the hash.
	 */
	private Collection<Object> successMembers;
	
	/**
	 * Collection of fields with unknown status (where the operation result couldn't be determined).
	 */
	private Collection<Object> unknownMembers;
	
	/**
	 * Map of fields that failed to be added, with their associated exceptions.
	 * The key is the field name/object, and the value is the exception that occurred.
	 */
	private Map<Object,Exception> failedMembers;
	
	/**
	 * Original arguments passed to the Redis operation.
	 * Typically contains the hash key at index 0, followed by field-value pairs.
	 */
	private Object[] args;

	/**
	 * Constructs a new batch field addition event with the specified parameters.
	 *
	 * @param args Original arguments passed to the Redis operation
	 * @param successMembers Collection of fields that were successfully added
	 * @param failedMembers Map of fields that failed to be added, with their associated exceptions
	 * @param unknownMembers Collection of fields with unknown status
	 */
	public OrangeAddMembersIfAbsentEvent(
		Object[] args, 
		Collection<Object> successMembers, 
		Map<Object,Exception> failedMembers,
		Collection<Object> unknownMembers
	) {
		// Store the collection of successfully added fields
		this.successMembers = successMembers;
		// Store the map of fields that failed to be added with their exceptions
		this.failedMembers = failedMembers;
		// Store the collection of fields with unknown status
		this.unknownMembers = unknownMembers;
		// Store the original operation arguments
		this.args = args;
	}

	/**
	 * Returns the collection of fields that were successfully added to the hash.
	 *
	 * @return A collection of objects representing the fields that were successfully added
	 */
	public Collection<Object> getSuccessMembers() {
		return successMembers;
	}

	/**
	 * Returns the collection of fields with unknown status.
	 * 
	 * <p>These are fields where the operation result couldn't be determined,
	 * typically due to network issues or timeouts.
	 *
	 * @return A collection of objects representing the fields with unknown addition status
	 */
	public Collection<Object> getUnknownMembers() {
		return unknownMembers;
	}

	/**
	 * Returns a map of fields that failed to be added, with their associated exceptions.
	 * 
	 * <p>The key is the field name/object, and the value is the exception that occurred
	 * during the attempt to add that field.
	 *
	 * @return A map where keys are fields that failed to be added and values are the exceptions that occurred
	 */
	public Map<Object, Exception> getFailedMembers() {
		return failedMembers;
	}

	/**
	 * Returns the original arguments passed to the Redis operation.
	 * 
	 * <p>Typically, the array contains:
	 * <ul>
	 *   <li>The hash key at index 0</li>
	 *   <li>Field-value pairs in subsequent indices</li>
	 * </ul>
	 *
	 * @return An array containing the original arguments of the Redis operation
	 */
	public Object[] getArgs() {
		return args;
	}
}