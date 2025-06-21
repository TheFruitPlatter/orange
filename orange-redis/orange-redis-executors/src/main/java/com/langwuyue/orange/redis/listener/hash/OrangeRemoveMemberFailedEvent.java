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
 * Event representing a failure in a single Redis HASH field removal operation.
 *
 * <p>This event extends {@link OrangeAddMemberIfAbsentFailedEvent} to track failures when
 * attempting to remove a single field from a Redis hash. Unlike {@link OrangRemoveMembersFailedEvent}
 * which handles batch operations, this class is specifically for single field removal failures.
 *
 * @see OrangeAddMemberIfAbsentFailedEvent for base functionality
 * @see OrangRemoveMembersFailedEvent for batch removal failures
 * @author Liang.Zhong
 * @since 1.0.0
 */
public class OrangeRemoveMemberFailedEvent extends OrangeAddMemberIfAbsentFailedEvent {

	/**
	 * Constructs a new single field removal failure event.
	 *
	 * @param args Original operation arguments, typically contains:
	 *             <ul>
	 *               <li>Redis hash key at index 0</li>
	 *               <li>Field name to remove at index 1</li>
	 *             </ul>
	 * @param exception Exception that caused the removal operation to fail
	 */
	public OrangeRemoveMemberFailedEvent(Object[] args, Exception exception) {
		super(args, exception);
	}
}