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
 * Constants used for Redis transaction management in the Orange framework.
 * 
 * <p>This interface defines the key names used in Redis hash tables for managing
 * transaction versions and garbage collection. The constants defined here are
 * essential for the versioning mechanism that allows atomic updates and
 * rollbacks in distributed transactions.
 * 
 * <p>The versioning system works as follows:
 * <ol>
 *   <li>Each transaction gets a unique version number from {@code NEXT_VERSION}</li>
 *   <li>Transaction data is stored with {@code VERSION_PREFIX} + version number as the hash key</li>
 *   <li>{@code CURRENT_VERSION} points to the active transaction version</li>
 *   <li>Garbage collection uses {@code CLEAR_OLD_VERSION_CURSOR} to track cleanup progress</li>
 * </ol>
 * 
 *
 * @author Liang.Zhong
 * @since 1.0.0
 */
public interface OrangeRedisTransactionKeyConstants {
	
	/**
	 * Key name for the counter that generates unique version numbers.
	 * 
	 * <p>This key is used with Redis INCR operation to atomically generate
	 * unique, monotonically increasing version numbers for new transactions.
	 * Each increment operation returns a new version number that can be used
	 * to identify a transaction uniquely across the distributed system.
	 */
	String NEXT_VERSION = "next_version";
	
	/**
	 * Key name that stores the currently active transaction version.
	 * 
	 * <p>This key points to the version number of the transaction that is
	 * currently in effect. When a new transaction is committed, this value
	 * is updated to the new version number, making the new transaction data
	 * immediately visible to all nodes in the distributed system.
	 */
	String CURRENT_VERSION = "current_version";
	
	/**
	 * Prefix used to construct hash keys for storing transaction data.
	 * 
	 * <p>Transaction data is stored in Redis hash tables with keys formed by
	 * concatenating this prefix with the transaction's version number
	 * (e.g., "version_42"). The hash value contains the actual transaction data.
	 * This versioning approach enables atomic switching between different
	 * transaction states without data copying.
	 */
	String VERSION_PREFIX = "version_";
	
	/**
	 * Key name that tracks the garbage collection progress for old versions.
	 * 
	 * <p>This key stores the cursor position (version number) up to which
	 * old transaction versions have been cleaned up. The garbage collector
	 * uses this cursor to identify and remove obsolete transaction versions
	 * that are no longer needed, while ensuring that any in-progress operations
	 * using those versions can complete successfully.
	 */
	String CLEAR_OLD_VERSION_CURSOR = "clear_old_version_cursor";
}