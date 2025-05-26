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
package com.langwuyue.orange.redis.template.transaction;

import com.langwuyue.orange.redis.RedisValueTypeEnum;
import com.langwuyue.orange.redis.annotation.RedisValue;
import com.langwuyue.orange.redis.annotation.transaction.Commit;
import com.langwuyue.orange.redis.annotation.transaction.OrangeRedisTransactionClient;
import com.langwuyue.orange.redis.annotation.transaction.Version;
import com.langwuyue.orange.redis.annotation.value.GetValue;
import com.langwuyue.orange.redis.annotation.value.SetValue;
import com.langwuyue.orange.redis.template.global.GlobalOperationsTemplate;

/**
 * Interface template for Redis Transaction operations with JSON serialization support.
 * This template provides optimistic locking mechanism for JSON data stored in Redis.
 * Developers should extend this interface and annotate the child interface with 
 * {@code OrangeRedisKey}.
 * 
 * <p>Key features:
 * <ul>
 *   <li>Optimistic locking with version control</li>
 *   <li>JSON serialization/deserialization of complex objects</li>
 *   <li>Atomic commit operations</li>
 *   <li>Integration with Spring transaction management</li>
 * </ul>
 * 
 * <p>Transaction Flow:
 * <ol>
 *   <li>setValue: Stores value with PENDING status</li>
 *   <li>commit: Atomically updates status to COMMITTED if version matches</li>
 *   <li>getValue: Returns value only if transaction is committed</li>
 * </ol>
 * 
 * @param <T> the type of value to be stored in Redis, must be JSON serializable
 * @author Liang.Zhong
 * @since 1.0.0
 * @see com.langwuyue.orange.redis.annotation.transaction.OrangeRedisTransactionClient
 * @see com.langwuyue.orange.redis.annotation.transaction.Commit
 * @see com.langwuyue.orange.redis.annotation.transaction.Version
 */
@OrangeRedisTransactionClient(valueType = RedisValueTypeEnum.JSON)
public interface JSONOperationsTemplate<T> extends GlobalOperationsTemplate {
	
	/**
	 * Sets a value in the transaction context and returns a version identifier.
	 * 
	 * <p>The value will not be accessible via {@link #getValue()} until the 
	 * {@link #commit(Long)} method is called with the version returned by this method.
	 * 
	 * <p>Implementation details:
	 * <ul>
	 *   <li>The value is serialized to JSON format</li>
	 *   <li>A unique version identifier is generated and incremented</li>
	 * </ul>
	 * 
	 * 
	 * @param value The value to be set in the transaction context, can be null
	 * @return A version identifier that must be used when committing the transaction
	 * @see #commit(Long)
	 */
	@SetValue
	Long setValue(@RedisValue T value);
	
	/**
	 * Commits the transaction with the specified version.
	 * 
	 * <p>The commit will only succeed if the provided version is greater than current version.
	 * 
	 * <p>Implementation details:
	 * <ul>
	 *   <li>Uses atomic operations to ensure consistency</li>
	 *   <li>Verifies that the version matches before committing</li>
	 *   <li>Applies the configured expiration time</li>
	 * </ul>
	 * 
	 * @param version The version identifier returned by {@link #setValue(Object)}
	 * @return {@code true} if the commit succeeds, {@code false} if it fails due to version mismatch.
	 * @see #setValue(Object)
	 */
	@Commit
	Boolean commit(@Version Long version);
	
	/**
	 * Retrieves the current committed value from Redis.
	 * 
	 * <p>This operation returns only successfully committed values. Values set using
	 * {@link #setValue(Object)} are not visible through this method until they are 
	 * successfully committed using {@link #commit(Long)}.
	 * 
	 * <p>Implementation details:
	 * <ul>
	 *   <li>Returns the value only if transaction is committed</li>
	 *   <li>Deserializes the JSON to the generic type T</li>
	 *   <li>Returns null if no committed value exists</li>
	 * </ul>
	 * 
	 * <p>Important note for developers:
	 * When extending this interface, you must override this method with the concrete return type
	 * to avoid type erasure issues. For example:
	 * 
	 * <pre>{@code
	 * public interface UserTransactionApi extends JSONOperationsTemplate<User> {
	 *     @Override
	 *     User getValue(); // Must be overridden with concrete type
	 * }
	 * }</pre>
	 * 
	 * @return The current committed value, or null if no committed value exists
	 */
	@GetValue
	T getValue();
}