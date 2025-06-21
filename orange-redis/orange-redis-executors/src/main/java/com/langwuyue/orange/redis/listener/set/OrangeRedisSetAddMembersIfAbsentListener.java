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

import com.langwuyue.orange.redis.listener.OrangeRedisMultipleSetIfAbsentListener;

/**
 * Listener interface for Redis batch SET "add members if absent" operations.
 *
 * <p>This interface specializes {@link OrangeRedisMultipleSetIfAbsentListener} to handle
 * batch operations where multiple members are added to a Redis set in a single operation.
 * It provides events that contain collections of members rather than individual members.
 *
 * <p>The interface handles two event types:
 * <ul>
 *   <li>{@link OrangeAddMembersIfAbsentEvent} - Contains results for batch member additions,
 *       including collections of:
 *       <ul>
 *         <li>Successfully added members</li>
 *         <li>Members that failed to add (with exceptions)</li>
 *         <li>Members with unknown status</li>
 *       </ul>
 *   </li>
 *   <li>{@link OrangRemoveMembersFailedEvent} - Contains failures from batch member removals</li>
 * </ul>
 *
 * @author Liang.Zhong
 * @since 1.0.0
 */
public interface OrangeRedisSetAddMembersIfAbsentListener extends OrangeRedisMultipleSetIfAbsentListener<OrangeAddMembersIfAbsentEvent,OrangRemoveMembersFailedEvent> {

}