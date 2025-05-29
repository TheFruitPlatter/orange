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
package com.langwuyue.orange.redis.listener;

import com.langwuyue.orange.redis.annotation.OrangeRedisOriginalKey;

/**
 * Listener interface for handling Redis remove operation failures.
 * 
 * <p>This interface defines callbacks that are triggered when Redis remove operations fail.
 * Implementations can provide custom handling logic for these failure scenarios.
 *
 * @param <R> the type of the event object containing information about the failed removal operation
 * 
 * @author Liang.Zhong
 * @since 1.0.0
 * @see OrangeRedisOriginalKey
 */
public interface OrangeRedisRemoveFailedListener<R> {

	/**
	 * Callback method invoked when a Redis remove operation fails, with access to the original key.
	 * 
	 * <p>This is a default implementation that delegates to {@link #onRemoveFailed(Object)}.
	 * Override this method if you need to handle both the original key and the event object
	 * when a remove operation fails.
	 *
	 * @param originalKey the original Redis key that failed to be removed, annotated with {@link OrangeRedisOriginalKey}
	 * @param event the event object containing information about the failed removal operation
	 */
	default void onRemoveFailed(@OrangeRedisOriginalKey String originalKey,R event) {
		onRemoveFailed(event);
	}
	
	/**
	 * Callback method invoked when a Redis remove operation fails.
	 * 
	 * <p>This method is called when a Redis key removal operation fails. Implementations
	 * should provide appropriate error handling, logging, or recovery logic for the
	 * failed operation.
	 * 
	 * <p>Example implementation:
	 * <pre>{@code
	 * @Override
	 * public void onRemoveFailed(String key) {
	 *     logger.error("Failed to remove key from Redis: {}", key);
	 *     // Implement retry logic or notify monitoring system
	 *     retryService.scheduleRetry(key);
	 * }
	 * }</pre>
	 *
	 * @param event the event object containing information about the failed removal operation
	 */
	void onRemoveFailed(R event);
}