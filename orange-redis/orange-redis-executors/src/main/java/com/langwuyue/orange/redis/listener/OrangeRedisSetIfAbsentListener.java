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
 * Listener interface for handling Redis set-if-absent operations.
 * 
 * <p>This interface defines callbacks for handling the success or failure of Redis
 * set-if-absent operations. It extends {@link OrangeRedisRemoveFailedListener} to also
 * handle removal failures for the same operations.
 * 
 * <p>Set-if-absent operations in Redis attempt to set a key's value only if the key does not
 * already exist (similar to Redis SETNX command). This listener provides callbacks for both
 * successful and failed attempts.
 * 
 * <p>Example usage:
 * <pre>{@code
 * @Component
 * public class UserCacheListener implements OrangeRedisSetIfAbsentListener<SuccessEvent, FailureEvent, String> {
 *     @Override
 *     public void onSuccess(SuccessEvent event) {
 *         // Handle successful set-if-absent operation
 *     }
 *     
 *     {@code @Override}
 *     public void onFailure(FailureEvent event) {
 *         // Handle failed set-if-absent operation
 *     }
 *     
 *     {@code @Override}
 *     public void onRemoveFailed(String key) {
 *         // Handle failure when removing a key
 *     }
 * }
 * }</pre>
 *
 * @param <S> the type of event object containing information about successful operations
 * @param <F> the type of event object containing information about failed operations
 * @param <R> the type of key used in remove operations
 * 
 * @author Liang.Zhong
 * @since 1.0.0
 * @see OrangeRedisRemoveFailedListener
 * @see OrangeRedisOriginalKey
 */
public interface OrangeRedisSetIfAbsentListener<S,F,R> extends OrangeRedisRemoveFailedListener<R> {

	/**
	 * Callback method invoked when a set-if-absent operation fails, with access to the original key.
	 * 
	 * <p>This is a default implementation that delegates to {@link #onFailure(Object)}.
	 * Override this method if you need to handle both the original key and the failure event.
	 *
	 * @param originalKey the original Redis key that failed to be set, annotated with {@link OrangeRedisOriginalKey}
	 * @param event the event object containing information about the failed operation
	 */
	default void onFailure(@OrangeRedisOriginalKey String originalKey,F event) {
		onFailure(event);
	}

	/**
	 * Callback method invoked when a set-if-absent operation succeeds, with access to the original key.
	 * 
	 * <p>This is a default implementation that delegates to {@link #onSuccess(Object)}.
	 * Override this method if you need to handle both the original key and the success event.
	 *
	 * @param originalKey the original Redis key that was successfully set, annotated with {@link OrangeRedisOriginalKey}
	 * @param event the event object containing information about the successful operation
	 */
	default void onSuccess(@OrangeRedisOriginalKey String originalKey,S event) {
		onSuccess(event);
	}
	
	/**
	 * Callback method invoked when a set-if-absent operation fails.
	 * 
	 * <p>This method is called when a Redis set-if-absent operation fails, which typically
	 * occurs when the key already exists or when there are other Redis-related issues.
	 * Implementations should provide appropriate error handling or recovery logic.
	 * 
	 * <p>Example implementation:
	 * <pre>{@code
	 * @Override
	 * public void onFailure(FailureEvent event) {
	 *     logger.error("Failed to set key: {}, reason: {}", 
	 *                 event.getKey(), event.getReason());
	 *     // Implement retry logic or alternative processing
	 * }
	 * }</pre>
	 *
	 * @param event the event object containing information about the failed operation
	 */
	void onFailure(F event);

	/**
	 * Callback method invoked when a set-if-absent operation succeeds.
	 * 
	 * <p>This method is called when a Redis set-if-absent operation successfully sets
	 * a key because it did not previously exist. Implementations can perform any
	 * necessary follow-up actions or logging.
	 * 
	 * <p>Example implementation:
	 * <pre>{@code
	 * @Override
	 * public void onSuccess(SuccessEvent event) {
	 *     logger.info("Successfully set key: {}", event.getKey());
	 *     // Perform any additional processing
	 *     notifyDownstreamSystems(event);
	 * }
	 * }</pre>
	 *
	 * @param event the event object containing information about the successful operation
	 */
	void onSuccess(S event);

}