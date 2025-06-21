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

import com.langwuyue.orange.redis.listener.OrangeRedisSetIfAbsentListener;

/**
 * Listener interface for Redis sorted set (ZSET) operations with "add if absent" semantics.
 * <p>
 * This interface extends {@link OrangeRedisSetIfAbsentListener} to provide callbacks for:
 * <ul>
 *   <li>Successful addition of members to a sorted set</li>
 *   <li>Failed attempts to add members</li>
 *   <li>Failed removal operations</li>
 * </ul>
 * <p>
 * The generic type parameters specify the event types for different operation outcomes:
 * <ol>
 *   <li>{@link OrangeAddMemberIfAbsentSuccessEvent} - Triggered when a member is successfully added</li>
 *   <li>{@link OrangeAddMemberIfAbsentFailedEvent} - Triggered when adding a member fails</li>
 *   <li>{@link OrangeRemoveMemberFailedEvent} - Triggered when removing a member fails</li>
 * </ol>
 * <p>
 * Implement this interface to receive notifications about sorted set operations.
 *
 * @author Liang.Zhong
 * @since 1.0.0
 */
public interface OrangeRedisZSetAddMemberIfAbsentListener extends OrangeRedisSetIfAbsentListener<OrangeAddMemberIfAbsentSuccessEvent,OrangeAddMemberIfAbsentFailedEvent,OrangeRemoveMemberFailedEvent> {

	

}