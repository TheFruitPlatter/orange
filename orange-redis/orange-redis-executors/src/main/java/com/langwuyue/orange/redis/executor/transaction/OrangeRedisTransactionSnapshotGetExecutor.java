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

import java.lang.reflect.Type;

import com.langwuyue.orange.redis.RedisValueTypeEnum;

/**
 * Executor interface for retrieving data from Redis transaction snapshots.
 * 
 * <p>This interface defines the contract for components that can retrieve values
 * from specific transaction versions in Redis. It allows the system to access
 * point-in-time snapshots of data based on transaction version numbers, which is
 * essential for maintaining transaction isolation and consistency in a distributed
 * environment.
 * 
 * <p>Implementations of this interface are responsible for:
 * <ul>
 *   <li>Locating the correct version of data in Redis based on the provided version number</li>
 *   <li>Deserializing the retrieved data into the appropriate Java type</li>
 *   <li>Handling any Redis communication errors or serialization exceptions</li>
 * </ul>
 * 
 * 
 * <p>This executor is a key component in Orange's multi-version concurrency control
 * (MVCC) implementation for Redis transactions.
 */
public interface OrangeRedisTransactionSnapshotGetExecutor {

	/**
	 * Retrieves a value from a specific transaction version in Redis.
	 * 
	 * <p>This method fetches data from a particular transaction snapshot identified
	 * by the version number. It allows the application to read consistent data
	 * from a specific point in time, regardless of subsequent modifications.
	 *
	 * @param key The Redis key for which to retrieve the value
	 * @param version The transaction version number to retrieve data from. If null,
	 *                the current active version will typically be used
	 * @param valueType The Redis data type of the value being retrieved (e.g., STRING, HASH)
	 * @param returnType The Java type to which the retrieved value should be deserialized
	 * @return The retrieved value, deserialized to the specified return type
	 * @throws Exception If an error occurs during data retrieval or deserialization
	 */
	Object get(String key, Long version, RedisValueTypeEnum valueType, Type returnType) throws Exception;
}