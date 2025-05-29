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
package com.langwuyue.orange.redis.listener.value;

import com.langwuyue.orange.redis.listener.OrangeRedisSetIfAbsentListener;

/**
 * Listener interface specifically designed for Redis value type set-if-absent operations.
 * 
 * <p>This interface extends {@link OrangeRedisSetIfAbsentListener} with concrete event types
 * for handling Redis value operations. It provides type-safe callbacks for success, failure,
 * and remove failure events when performing set-if-absent operations on Redis string values.
 * 
 * <p>The interface uses the following event types:
 * <ul>
 *   <li>{@link OrangeSetIfAbsentSuccessEvent} for successful set-if-absent operations</li>
 *   <li>{@link OrangeSetIfAbsentFailedEvent} for failed set-if-absent operations</li>
 *   <li>{@link OrangeRemoveFailedEvent} for failed remove operations</li>
 * </ul>
 * 
 * <p>Example usage:
 * <pre>{@code
 * @Component
 * public class UserCacheListener implements OrangeRedisValueSetIfAbsentListener {
 *     @Override
 *     public void onSuccess(OrangeSetIfAbsentSuccessEvent event) {
 *         // Handle successful set-if-absent operation for user cache
 *         logger.info("Successfully cached user: {}", event.getValue());
 *     }
 *     
 *     @Override
 *     public void onFailure(OrangeSetIfAbsentFailedEvent event) {
 *         // Handle failed set-if-absent operation
 *         logger.warn("Failed to cache user: {}", event.getReason());
 *     }
 *     
 *     @Override
 *     public void onRemoveFailed(OrangeRemoveFailedEvent event) {
 *         // Handle failed remove operation
 *         logger.error("Failed to remove user from cache: {}", event.getKey());
 *     }
 * }
 * }</pre>
 *
 * @author Liang.Zhong
 * @since 1.0.0
 * @see OrangeRedisSetIfAbsentListener
 * @see OrangeSetIfAbsentSuccessEvent
 * @see OrangeSetIfAbsentFailedEvent
 * @see OrangeRemoveFailedEvent
 */
public interface OrangeRedisValueSetIfAbsentListener extends OrangeRedisSetIfAbsentListener<OrangeSetIfAbsentSuccessEvent,OrangeSetIfAbsentFailedEvent,OrangeRemoveFailedEvent> {


}