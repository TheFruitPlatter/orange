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
 * Event representing a failed Redis SET member removal operation.
 *
 * <p>This specialized event extends {@link OrangeAddMemberIfAbsentFailedEvent} to represent
 * failures that occur when attempting to remove members from a Redis set. It provides
 * the same information as its parent class but is semantically distinct for removal operations.
 *
 * @see OrangeAddMemberIfAbsentFailedEvent for complete method documentation
 * @author Liang.Zhong
 * @since 1.0.0
 */
public class OrangeRemoveMemberFailedEvent extends OrangeAddMemberIfAbsentFailedEvent {

	public OrangeRemoveMemberFailedEvent(Object[] args, Object member, Exception exception) {
		super(args, member, exception);
	}

	public OrangeRemoveMemberFailedEvent(Object[] args, Object member, String reason) {
		super(args, member, reason);
	}
}