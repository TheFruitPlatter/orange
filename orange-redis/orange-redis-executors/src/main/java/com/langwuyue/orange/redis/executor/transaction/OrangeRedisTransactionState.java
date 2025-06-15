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

/**
 * Represents the possible states of a Redis transaction in the Orange framework.
 * 
 * <p>This enum is used in transaction callbacks to determine the final state of a
 * transaction and control its lifecycle. The state returned by the callback
 * determines whether the transaction should be committed, retried, or rolled back.
 * 
 * <p>The transaction state workflow is as follows:
 * <ul>
 *   <li>When a callback returns SUCCESS, the transaction will be committed</li>
 *   <li>When a callback returns UNKNOWN, the system will maintain the current state
 *       and continue calling the callback</li>
 *   <li>When a callback returns FAILED, the callback process will stop and the
 *       transaction data will be marked for garbage collection</li>
 * </ul>
 * 
 *
 * @author Liang.Zhong
 * @since 1.0.0
 */
public enum OrangeRedisTransactionState {
	
	/**
	 * Indicates that the transaction has completed successfully.
	 * 
	 * <p>When a callback returns this state, the transaction will be committed
	 * and its changes will become permanent. This is the final state for a
	 * successful transaction.
	 */
	SUCCESS,
	
	/**
	 * Indicates that the transaction state is uncertain and needs further verification.
	 * 
	 * <p>When a callback returns this state, the system will:
	 * <ul>
	 *   <li>Maintain the current transaction state</li>
	 *   <li>Keep the transaction data intact</li>
	 *   <li>Continue calling the callback for further verification</li>
	 * </ul>
	 * This state is useful when the callback needs more time or information to
	 * determine the final transaction state.
	 */
	UNKNOWN,
	
	/**
	 * Indicates that the transaction has failed and should be rolled back.
	 * 
	 * <p>When a callback returns this state:
	 * <ul>
	 *   <li>The callback process will be stopped immediately</li>
	 *   <li>The transaction data will be marked as invalid</li>
	 *   <li>The garbage collector will eventually clean up the invalid transaction data</li>
	 * </ul>
	 * This is the final state for a failed transaction.
	 */
	FAILED;
}