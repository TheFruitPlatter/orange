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
package com.langwuyue.orange.redis.executor.transaction.context;

import java.lang.reflect.Method;

import com.langwuyue.orange.redis.OrangeRedisException;
import com.langwuyue.orange.redis.RedisValueTypeEnum;
import com.langwuyue.orange.redis.annotation.OrangeRedisOperationArg;
import com.langwuyue.orange.redis.annotation.transaction.Version;
import com.langwuyue.orange.redis.context.OrangeRedisContext;

/**
 * Transaction control context for Redis operations with version-based data management.
 * 
 * <p>This class manages the versioning mechanism for Redis transactions, where each operation
 * generates a unique version number. The data for each version is stored in a Redis hash
 * structure, allowing for version tracking and data retrieval based on version numbers.
 * 
 * <p>Key features:
 * <ul>
 *   <li>Generates unique version numbers for each Redis operation</li>
 *   <li>Maintains transaction data in Redis hash structures</li>
 *   <li>Tracks the current effective version</li>
 *   <li>Enables data retrieval based on the latest effective version</li>
 * </ul>
 * 
 * <p>When querying Redis, this context ensures that data is retrieved according to
 * the most recent effective version, maintaining data consistency across transactions.
 *
 * @author Liang.Zhong
 * @since 1.0.0
 */
public class OrangeTransactionVersionContext extends OrangeRedisContext {
	
	/**
	 * The version identifier for the current Redis operation.
	 * 
	 * <p>Each Redis operation generates a unique version number, which is used to:
	 * <ul>
	 *   <li>Track different versions of data in Redis hash structures</li>
	 *   <li>Identify the current effective version for data retrieval</li>
	 *   <li>Maintain transaction consistency across multiple operations</li>
	 * </ul>
	 * 
	 * <p>The version value is stored in Redis along with the corresponding data,
	 * allowing the system to maintain a history of changes and retrieve the most
	 * recent effective version of the data.
	 * 
	 * <p>This field is populated from method arguments annotated with {@link Version}
	 * and must be either a number or a string that can be converted to a Long.
	 */
	@OrangeRedisOperationArg(binding = Version.class)
	private Object version;

	/**
	 * Constructs a new OrangeTransactionVersionContext with the specified parameters.
	 * 
	 * <p>This constructor initializes the transaction version context that manages version-based
	 * data storage and retrieval in Redis. It sets up the context with all necessary information
	 * for tracking versions in Redis hash structures and maintaining transaction consistency.
	 * 
	 * <p>Each transaction operation will generate a unique version identifier that is stored
	 * in Redis along with the corresponding data, enabling version-based data access and
	 * ensuring that queries retrieve the most recent effective version of the data.
	 *
	 * @param operationOwner The class that owns the Redis operation method
	 * @param operationMethod The method representing the Redis operation
	 * @param args The arguments passed to the operation method, including the version identifier
	 * @param redisKey The Redis key to operate on, which will be associated with version information
	 * @param valueType The type of Redis value being operated on within the versioned hash structure
	 */
	public OrangeTransactionVersionContext(
		Class<?> operationOwner, 
		Method operationMethod, 
		Object[] args, 
		Key redisKey,
		RedisValueTypeEnum valueType
	) {
		super(operationOwner, operationMethod, args, redisKey, valueType);
	}
	
	/**
	 * Gets the version identifier for the current Redis operation.
	 * 
	 * <p>This method retrieves and validates the version number that is used to track data
	 * versions in Redis hash structures. The version number is essential for:
	 * <ul>
	 *   <li>Identifying specific versions of data in the Redis hash</li>
	 *   <li>Determining the current effective version for data retrieval</li>
	 *   <li>Maintaining data consistency across transactions</li>
	 * </ul>
	 * 
	 * <p>The method performs the following validations:
	 * <ul>
	 *   <li>Checks that the version is not null (required for version tracking)</li>
	 *   <li>Ensures the version is either a Number or a String that can be converted to a Long</li>
	 * </ul>
	 *
	 * @return The version identifier as a Long, used for version-based data access in Redis
	 * @throws OrangeRedisException if the version is null or cannot be converted to a Long,
	 *         which would prevent proper version tracking
	 */
	public Long getVersion() {
		if(version == null) {
			throw new OrangeRedisException(String.format("The argument annotated with @%s cannot be null", Version.class));
		}
		if(!(version instanceof Number) && !(version instanceof String)) {
			throw new OrangeRedisException(String.format("The argument annotated with @%s must be a number or a string", Version.class));
		}
		return Long.valueOf(version.toString());
	}

}