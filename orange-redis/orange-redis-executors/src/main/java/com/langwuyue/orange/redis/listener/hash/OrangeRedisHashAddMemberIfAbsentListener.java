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

import com.langwuyue.orange.redis.listener.OrangeRedisSetIfAbsentListener;

/**
 * Listener interface for Redis HASH single field addition operations.
 *
 * <p>This interface extends {@link OrangeRedisSetIfAbsentListener} to specifically
 * handle events related to adding a single field to Redis hash structures. It listens for
 * both successful additions and failures during the process of adding a field to a hash
 * if it doesn't already exist.
 *
 * @see OrangeAddMemberIfAbsentSuccessEvent for successful single field addition events
 * @see OrangeAddMemberIfAbsentFailedEvent for failed single field addition events
 * @see OrangeRemoveMemberFailedEvent for failed single field removal events
 * @see OrangeRedisSetIfAbsentListener for the base listener functionality
 * @see OrangeRedisHashAddMembersIfAbsentListener for batch field addition operations
 * @author Liang.Zhong
 * @since 1.0.0
 */
public interface OrangeRedisHashAddMemberIfAbsentListener extends OrangeRedisSetIfAbsentListener<OrangeAddMemberIfAbsentSuccessEvent,OrangeAddMemberIfAbsentFailedEvent,OrangeRemoveMemberFailedEvent> {

}