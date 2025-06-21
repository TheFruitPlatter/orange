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

import java.util.Collection;
import java.util.Map;

/**
 * Event representing the result of a batch "add if absent" operation on Redis sets.
 *
 * <p>This event tracks three categories of members:
 * <ul>
 *   <li><b>Successfully added</b> - Members that didn't exist and were added</li>
 *   <li><b>Failed additions</b> - Members that couldn't be added due to exceptions</li>
 *   <li><b>Unknown status</b> - Members whose addition status couldn't be determined</li>
 * </ul>
 *
 * <p>This class is immutable - all fields are final and the collections/maps are
 * stored as references to the original objects. Callers should not modify these
 * collections after passing them to the event.
 *
 * <p>Typical usage includes:
 * <ul>
 *   <li>Monitoring batch operation success rates</li>
 *   <li>Handling failed additions with retry logic</li>
 *   <li>Logging operation results for auditing</li>
 * </ul>
 *
 * @author Liang.Zhong
 * @since 1.0.0
 */
public class OrangeAddMembersIfAbsentEvent {

	/**
	 * Members that were successfully added to the Redis set.
	 * <p>
	 * Contains all members that were successfully added because they didn't
	 * previously exist in the set.
	 */
	private Collection<Object> successMembers;
	
	/**
	 * Members whose addition status couldn't be determined.
	 * <p>
	 * Contains members where the operation couldn't confirm whether they were
	 * newly added or already existed.
	 */
	private Collection<Object> unknownMembers;
	
	/**
	 * Members that failed to be added, mapped to their exceptions.
	 * <p>
	 * Contains members that encountered errors during addition, with the
	 * corresponding exceptions. 
	 */
	private Map<Object,Exception> failedMembers;
	
	/**
	 * The original arguments passed to the add operation.
	 * <p>
	 * Contains the full context of the operation. The array reference is stored
	 * directly - callers should not modify this array after passing it to the
	 * constructor. 
	 */
	private Object[] args;

	/**
	 * Creates an event representing the result of a batch "add if absent" operation.
	 *
	 * @param args Original operation arguments 
	 * @param successMembers Members successfully added 
	 * @param failedMembers Members that failed to add with exceptions 
	 * @param unknownMembers Members with unknown status
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
	 * Gets the members that were successfully added to the set.
	 *
	 * @return the successfully added members
	 */
	public Collection<Object> getSuccessMembers() {
		return successMembers;
	}

	/**
	 * Gets the members whose addition status couldn't be determined.
	 *
	 * @return the unknown status members
	 */
	public Collection<Object> getUnknownMembers() {
		return unknownMembers;
	}

	/**
	 * Gets the members that failed to be added, with their exceptions.
	 *
	 * @return the failure map
	 */
	public Map<Object, Exception> getFailedMembers() {
		return failedMembers;
	}

	/**
	 * Gets the original operation arguments.
	 *
	 * @return The arguments array
	 */
	public Object[] getArgs() {
		return args;
	}
	
}