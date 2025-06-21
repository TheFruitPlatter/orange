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

import com.langwuyue.orange.redis.listener.OrangeRedisMultipleSetIfAbsentListener;

/**
 * Listener interface for Redis HASH field batch addition operations.
 *
 * <p>This interface extends {@link OrangeRedisMultipleSetIfAbsentListener} to specifically
 * handle events related to batch addition of fields to Redis hash structures. It listens for
 * both successful additions and failures during the process of adding multiple fields to a hash
 * if they don't already exist.
 *
 * @see OrangeAddMembersIfAbsentEvent for successful batch addition events
 * @see OrangRemoveMembersFailedEvent for failed batch addition events
 * @see OrangeRedisMultipleSetIfAbsentListener for the base listener functionality
 * @author Liang.Zhong
 * @since 1.0.0
 */
public interface OrangeRedisHashAddMembersIfAbsentListener extends OrangeRedisMultipleSetIfAbsentListener<OrangeAddMembersIfAbsentEvent,OrangRemoveMembersFailedEvent> {

}