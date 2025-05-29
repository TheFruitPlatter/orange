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
 * Listener interface for handling multiple Redis set-if-absent operations.
 * 
 * <p>This interface defines callbacks for handling the completion of multiple Redis
 * set-if-absent operations. It extends {@link OrangeRedisRemoveFailedListener} to also
 * handle removal failures for the same operations.
 * 
 * <p>Implementations of this interface can be used to perform actions after multiple
 * Redis keys have been set conditionally (only if they didn't exist previously).
 *
 * @param <T> the type of event object containing information about the completed operation
 * @param <R> the type of key used in remove operations
 * 
 * @author Liang.Zhong
 * @since 1.0.0
 * @see OrangeRedisRemoveFailedListener
 * @see OrangeRedisOriginalKey
 */
public interface OrangeRedisMultipleSetIfAbsentListener<T,R> extends OrangeRedisRemoveFailedListener<R> {

	/**
	 * Callback method invoked when multiple set-if-absent operations complete, with access to the original key.
	 * 
	 * <p>This is a default implementation that delegates to {@link #onCompleted(Object)}.
	 * Override this method if you need to handle both the original key and the event.
	 *
	 * @param originalKey the original Redis key that was operated on, annotated with {@link OrangeRedisOriginalKey}
	 * @param event the event object containing information about the completed operation
	 */
	default void onCompleted(@OrangeRedisOriginalKey String originalKey,T event) {
		onCompleted(event);
	}
	
	/**
	 * Callback method invoked when multiple set-if-absent operations complete.
	 * 
	 * <p>This method is called after all the set-if-absent operations have been processed,
	 * regardless of whether they were successful or not. The event object contains
	 * information about the operations' results.
	 *
	 * @param event the event object containing information about the completed operation
	 */
	void onCompleted(T event);

}