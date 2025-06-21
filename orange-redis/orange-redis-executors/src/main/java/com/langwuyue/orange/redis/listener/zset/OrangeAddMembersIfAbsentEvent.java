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

import java.util.Collection;
import java.util.Map;

/**
 * Represents an event for batch adding members to a Redis sorted set with partial results.
 * <p>
 * This event is triggered after attempting to add multiple members to a sorted set.
 * It provides detailed information about the operation outcome including:
 * <ul>
 *   <li>Successfully added members</li>
 *   <li>Members that failed to add (with exceptions)</li>
 *   <li>Members with unknown status</li>
 * </ul>
 * <p>
 * Typical usage includes monitoring batch operations and handling partial failures.
 *
 * @author Liang.Zhong
 * @since 1.0.0
 */
public class OrangeAddMembersIfAbsentEvent {

	/** Collection of members that were successfully added to the sorted set */
	private Collection<Object> successMembers;
	
	/** Collection of members whose addition status couldn't be determined */
	private Collection<Object> unknownMembers;
	
	/** Map of members that failed to add, with their corresponding exceptions */
	private Map<Object,Exception> failedMembers;
	
	/** Original arguments passed to the batch add operation */
	private Object[] args;

	/**
	 * Creates a new batch add members event with operation results.
	 *
	 * @param args Original method arguments
	 * @param successMembers Members successfully added to the set
	 * @param failedMembers Members that failed to add with their exceptions
	 * @param unknownMembers Members with undetermined addition status
	 */
	public OrangeAddMembersIfAbsentEvent(
		Object[] args, 
		Collection<Object> successMembers, 
		Map<Object,Exception> failedMembers,
		Collection<Object> unknownMembers
	) {
		this.successMembers = successMembers;
		this.failedMembers = failedMembers;
		this.unknownMembers = unknownMembers;
		this.args = args;
	}

	/**
	 * Gets the collection of members that were successfully added to the sorted set.
	 *
	 * @return Collection of successfully added members, may be empty but never null
	 */
	public Collection<Object> getSuccessMembers() {
		return successMembers;
	}

	/**
	 * Gets the collection of members with undetermined addition status.
	 *
	 * @return Collection of members with unknown status, may be empty but never null
	 */
	public Collection<Object> getUnknownMembers() {
		return unknownMembers;
	}

	/**
	 * Gets the map of members that failed to be added, along with their corresponding exceptions.
	 *
	 * @return Map where keys are failed members and values are the exceptions that occurred,
	 *         may be empty but never null
	 */
	public Map<Object, Exception> getFailedMembers() {
		return failedMembers;
	}

	/**
	 * Gets the original arguments passed to the batch add operation.
	 *
	 * @return Array of arguments used in the original method call,
	 *         may be empty but never null
	 */
	public Object[] getArgs() {
		return args;
	}
	
}