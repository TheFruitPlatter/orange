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
 * Event representing successful addition of members to a Redis set when they didn't previously exist.
 *
 * <p>This event is triggered when:
 * <ul>
 *   <li>A member is successfully added to a Redis set</li>
 *   <li>The member did not previously exist in the set</li>
 * </ul>
 *
 * <p>This class is immutable - all fields are final and the args array is stored
 * as a reference to the original array. Callers should not modify the args array
 * after passing it to the event.
 *
 * <p>Typical usage includes:
 * <ul>
 *   <li>Logging successful add operations</li>
 *   <li>Monitoring set operation patterns</li>
 *   <li>Implementing post-add processing logic</li>
 * </ul>
 *
 * @author Liang.Zhong
 * @since 1.0.0
 */
public class OrangeAddMemberIfAbsentSuccessEvent {

	/**
	 * The member that was successfully added to the Redis set.
	 * <p>
	 * Contains the object that was added to the set when the operation succeeded.
	 */
	private Object member;
	
	/**
	 * The original arguments passed to the add operation.
	 * <p>
	 * Contains the full context of the operation that succeeded.
	 * The array reference is stored directly - callers should not modify
	 * this array after passing it to the constructor.
	 */
	private Object[] args;

	/**
	 * Creates a success event for a Redis set member addition.
	 *
	 * @param args Original operation arguments
	 * @param member The member that was successfully added
	 */
	public OrangeAddMemberIfAbsentSuccessEvent(Object[] args, Object member) {
		this.member = member;
		this.args = args;
	}

	/**
	 * Gets the member that was successfully added to the set.
	 *
	 * @return The member object that was added
	 */
	public Object getMember() {
		return member;
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