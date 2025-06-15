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
 * Executor interface responsible for committing Redis transactions.
 * 
 * <p>This interface defines the contract for components that handle the actual
 * commit operation of Redis transactions in the Orange Redis transaction system.
 * Implementations of this interface are responsible for ensuring that all changes
 * made within a transaction are properly applied to the underlying Redis data store.
 * 
 * <p>The commit operation typically involves validating the transaction state,
 * checking for conflicts with other transactions, and applying the changes
 * atomically if possible.
 *
 * @author Liang.Zhong
 * @since 1.0.0
 */
public interface OrangeRedisTransactionCommitExecutor {
	
	/**
	 * Commits a transaction for the specified key and version.
	 * 
	 * <p>This method attempts to commit all changes made within a transaction
	 * identified by the given key and version. The version is used for optimistic
	 * locking to ensure that the data has not been modified by another transaction
	 * since this transaction began.
	 * 
	 * <p>If the commit is successful, all changes made within the transaction
	 * become visible to other transactions. If the commit fails, the transaction
	 * will be cleared by GC.
	 *
	 * @param key The key identifying the transaction or the data being modified
	 * @param version The version number used for optimistic locking
	 * @return {@code true} if the commit was successful, {@code false} otherwise
	 * @throws Exception If an error occurs during the commit process
	 */
	boolean commit(String key, Long version) throws Exception;
}