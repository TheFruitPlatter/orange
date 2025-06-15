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
package com.langwuyue.orange.redis.executor.transaction;

import com.langwuyue.orange.redis.executor.transaction.OrangeRedisDefaultTransactionManager.OrangeRedisTransactionKey;
import com.langwuyue.orange.redis.executor.transaction.OrangeRedisTransactionTimeoutCallbackExecutor.OrangeTransactionTimeoutCallbackMetric;

/**
 * Listener interface for handling Redis transaction timeout events.
 * 
 * <p>This interface defines a callback mechanism that is invoked when a Redis transaction
 * exceeds its configured timeout threshold. Implementations of this interface provide
 * custom logic to handle these timeout situations, allowing for flexible transaction
 * management strategies such as automatic retry, forced commit, or rollback.
 * 
 * <p>The listener is generic, allowing it to work with different types of transaction values.
 * The type parameter {@code <T>} represents the type of value associated with the transaction
 * that will be provided to the callback method when a timeout occurs.
 * 
 * <p>When a transaction timeout is detected, the registered listener's callback method is
 * invoked with the transaction key, the associated value, and metrics about the timeout event.
 * The implementation should then determine the appropriate action to take and return the
 * corresponding transaction state.
 * 
 * @param <T> The type of value associated with the transaction
 * @author Liang.Zhong
 * @since 1.0.0
 */
public interface OrangeRedisTransactionTimeoutListener<T> {
	
	/**
	 * Callback method invoked when a transaction exceeds its timeout threshold.
	 * 
	 * <p>This method is called by the transaction timeout executor when a transaction
	 * has been active for longer than its configured timeout period. The implementation
	 * should analyze the transaction state and metrics to determine the appropriate
	 * action to take.
	 * 
	 * <p>Based on the implementation's logic, it should return one of the following states:
	 * <ul>
	 *   <li>{@code OrangeRedisTransactionState.SUCCESS} - Force commit the transaction</li>
	 *   <li>{@code OrangeRedisTransactionState.UNKNOWN} - Retry the transaction</li>
	 *   <li>{@code OrangeRedisTransactionState.FAILED} - Roll back the transaction</li>
	 * </ul>
	 * 
	 * <p>The implementation can use the provided metric information to make decisions
	 * based on factors such as:
	 * <ul>
	 *   <li>How many times the transaction has already been retried</li>
	 *   <li>How long the transaction has been active</li>
	 *   <li>The specific transaction key and value</li>
	 * </ul>
	 * 
	 * @param key The key identifying the timed-out transaction
	 * @param value The value associated with the transaction, of type T
	 * @param metric Metrics about the transaction timeout, including attempt count and timing information
	 * @return The desired transaction state (SUCCESS, UNKNOWN, or FAILED)
	 */
	OrangeRedisTransactionState callback(OrangeRedisTransactionKey key, T value, OrangeTransactionTimeoutCallbackMetric metric);
}