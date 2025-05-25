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
package com.langwuyue.orange.redis.template.value;

import com.langwuyue.orange.redis.RedisValueTypeEnum;
import com.langwuyue.orange.redis.annotation.CAS;
import com.langwuyue.orange.redis.annotation.IfAbsent;
import com.langwuyue.orange.redis.annotation.RedisOldValue;
import com.langwuyue.orange.redis.annotation.RedisValue;
import com.langwuyue.orange.redis.annotation.global.SetExpiration;
import com.langwuyue.orange.redis.annotation.value.GetValue;
import com.langwuyue.orange.redis.annotation.value.OrangeRedisValueClient;
import com.langwuyue.orange.redis.annotation.value.SetValue;
import com.langwuyue.orange.redis.template.global.GlobalOperationsTemplate;

/**
 * Interface template for Redis JSON value operations with automatic serialization.
 * <p>
 * This template provides thread-safe operations for Redis values with automatic
 * JSON serialization/deserialization using Jackson.
 *
 * <p>Key characteristics:
 * <ul>
 *   <li>Thread-safe: All operations are safe for concurrent use</li>
 *   <li>Non-blocking: Operations do not wait for other threads</li>
 *   <li>JSON support: Automatic serialization/deserialization of complex objects</li>
 *   <li>Atomic operations: CAS and SETNX operations supported</li>
 * </ul>
 *
 * <p>Performance considerations:
 * <ul>
 *   <li>Serialization overhead increases with object complexity</li>
 *   <li>Recommended payload size under 1MB for optimal performance</li>
 *   <li>Large objects may trigger Redis maxmemory policies</li>
 * </ul>
 *
 * <p>Implementation requirements:
 * <ul>
 *   <li>Child interfaces must be annotated with {@code @OrangeRedisKey}</li>
 *   <li>Must override getValue() method</li>
 *   <li>Type T must be JSON-serializable by Jackson</li>
 * </ul>
 *
 * <p>Example implementation:
 * <blockquote><pre>
 * {@code @OrangeRedisKey(expirationTime = @Timeout(value = 1, unit = TimeUnit.HOURS), key = "orange:value:example1")} 
 * public interface OrangeRedisValueExample1Api extends JSONOperationsTemplate{@code<User>} {
 *  
 *     {@code @Override}
 *     User getValue();
 *
 *     // Custom operations can be added here
 * }
 * </pre></blockquote>
 *
 *
 * @param <T> The type of elements stored in the Redis Value (must be JSON-serializable)
 * @author Liang.Zhong
 * @since 1.0.0
 * @see GlobalOperationsTemplate
 * @see <a href="https://redis.io/commands/set">Redis SET command</a>
 * @see <a href="https://redis.io/commands/get">Redis GET command</a>
 */
@OrangeRedisValueClient(valueType = RedisValueTypeEnum.JSON)
public interface JSONOperationsTemplate<T> extends GlobalOperationsTemplate {
	
	/**
	 * Serializes and stores the object as JSON in Redis.
	 * <p>
	 * This is the fundamental SET operation for JSON values in Redis.
	 *
	 * <p>Key characteristics:
	 * <ul>
	 *   <li>O(1) time complexity</li>
	 *   <li>Uses Jackson for JSON serialization</li>
	 *   <li>Supports complex object graphs</li>
	 *   <li>Overwrites existing value if any</li>
	 * </ul>
	 *
	 * <p>Serialization behavior:
	 * <ul>
	 *   <li>Object is converted to JSON string using Jackson</li>
	 *   <li>Follows Jackson's default serialization rules</li>
	 *   <li>Null values are stored as Redis nil</li>
	 * </ul>
	 *
	 * <p>Performance considerations:
	 * <ul>
	 *   <li>Serialization overhead increases with object complexity</li>
	 *   <li>Recommended payload size under 1MB for optimal performance</li>
	 *   <li>Large objects may trigger Redis maxmemory policies</li>
	 * </ul>
	 *
	 * <p>Example usage:
	 * <pre>{@code
	 * User user = new User("John", "john@example.com");
	 * setValue(user); // Serializes and stores user as JSON
	 * }</pre>
	 *
	 * @param value the object to serialize and store, may be null
	 * @throws RedisConnectionFailureException if unable to communicate with Redis
	 * @throws JsonProcessingException if object cannot be serialized to JSON
	 */
	@SetValue
	void setValue(@RedisValue T value);
	
	/**
	 * Serializes and stores the object as JSON in Redis with expiration time.
	 * <p>
	 * This operation combines SET and EXPIRE in a single atomic command.
	 *
	 * <p>Key characteristics:
	 * <ul>
	 *   <li>O(1) time complexity</li>
	 *   <li>Uses Jackson for JSON serialization</li>
	 *   <li>Supports complex object graphs</li>
	 *   <li>Overwrites existing value if any</li>
	 *   <li>Expiration time is set by {@code @OrangeRedisKey} annotation</li>
	 * </ul>
	 *
	 * <p>TTL behavior:
	 * <ul>
	 *   <li>Expiration timer starts immediately after set</li>
	 *   <li>If key is updated before expiration, timer resets</li>
	 *   <li>After expiration, key is automatically deleted</li>
	 * </ul>
	 *
	 * <p>Performance considerations:
	 * <ul>
	 *   <li>Serialization overhead increases with object complexity</li>
	 *   <li>Recommended payload size under 1MB for optimal performance</li>
	 * </ul>
	 *
	 * <p>Example usage:
	 * <pre>{@code
	 * Session session = new Session("user123", Instant.now().plusSeconds(3600));
	 * setValueWithExpiration(session); // Stores with 1-hour TTL
	 * }</pre>
	 *
	 * @param value the object to serialize and store, may be null
	 * @throws RedisConnectionFailureException if unable to communicate with Redis
	 * @throws JsonProcessingException if object cannot be serialized to JSON
	 */
	@SetValue
	@SetExpiration
	void setValueWithExpiration(@RedisValue T value);
	
	
	/**
	 * Atomically sets the JSON value if the key does not exist (SETNX operation).
	 * <p>
	 * This operation provides atomic "set-if-not-exists" semantics for JSON data.
	 *
	 * <p>Key characteristics:
	 * <ul>
	 *   <li>O(1) time complexity</li>
	 *   <li>Uses Jackson for JSON serialization</li>
	 *   <li>Only sets the value if key does not exist</li>
	 *   <li>Triggers {@code OrangeRedisValueSetIfAbsentListener} after operation</li>
	 * </ul>
	 *
	 * <p>Listener behavior:
	 * <ul>
	 *   <li>Listener is triggered after successful set operation</li>
	 *   <li>Listener implementation must be a Spring {@code @Component}</li>
	 *   <li>Use listener for post-set business logic</li>
	 * </ul>
	 *
	 * <p>Performance considerations:
	 * <ul>
	 *   <li>Serialization overhead increases with object complexity</li>
	 *   <li>Recommended payload size under 1MB for optimal performance</li>
	 * </ul>
	 *
	 * <p>Example usage:
	 * <pre>{@code
	 * Config defaultConfig = new Config("default");
	 * setValueIfAbsent(defaultConfig); // Only sets if key doesn't exist
	 * }</pre>
	 *
	 * <p>For TTL version see: {@link JSONOperationsTemplate#setValueIfAbsentWithExpiration(Object)}
	 *
	 * @param value the JSON-serializable object to set if key is absent
	 * @see OrangeRedisValueSetIfAbsentListener
	 */
	@SetValue
	@IfAbsent(deleteInTheEnd = false)
	void setValueIfAbsent(@RedisValue T value);
	
	/**
	 * Atomically sets the JSON value with TTL if the key does not exist (SETNX with EXPIRE).
	 * <p>
	 * This operation combines SETNX and EXPIRE in a single atomic command.
	 *
	 * <p>Key characteristics:
	 * <ul>
	 *   <li>O(1) time complexity</li>
	 *   <li>Uses Jackson for JSON serialization</li>
	 *   <li>Only sets the value if key does not exist</li>
	 *   <li>Sets expiration time from {@code @OrangeRedisKey} annotation</li>
	 *   <li>Triggers {@code OrangeRedisValueSetIfAbsentListener} after operation</li>
	 * </ul>
	 *
	 * <p>TTL behavior:
	 * <ul>
	 *   <li>Expiration timer starts immediately after set</li>
	 *   <li>After expiration, key is automatically deleted</li>
	 * </ul>
	 *
	 * <p>Listener behavior:
	 * <ul>
	 *   <li>Listener is triggered after successful set operation</li>
	 *   <li>Value is deleted after listener completes (deleteInTheEnd=true)</li>
	 *   <li>Listener implementation must be a Spring {@code @Component}</li>
	 * </ul>
	 *
	 * <p>Performance considerations:
	 * <ul>
	 *   <li>Serialization overhead increases with object complexity</li>
	 *   <li>Recommended payload size under 1MB for optimal performance</li>
	 * </ul>
	 *
	 * <p>Example usage:
	 * <pre>{@code
	 * Session tempSession = new Session("user123", Instant.now().plusSeconds(3600));
	 * setValueIfAbsentWithExpiration(tempSession); // Sets with TTL only if new session
	 * }</pre>
	 *
	 * @param value the JSON-serializable object to set if key is absent
	 * @see OrangeRedisValueSetIfAbsentListener
	 */
	@SetValue
	@IfAbsent(deleteInTheEnd = true)
	@SetExpiration
	void setValueIfAbsentWithExpiration(@RedisValue T value);
	
	/**
	 * Atomically compares and swaps the JSON value (CAS operation).
	 * <p>
	 * This operation provides compare-and-set semantics for JSON data.
	 *
	 * <p>Key characteristics:
	 * <ul>
	 *   <li>O(1) time complexity</li>
	 *   <li>Uses JSON string comparison for verification</li>
	 *   <li>Returns true only if the value was changed</li>
	 *   <li>More efficient than WATCH/MULTI for simple CAS scenarios</li>
	 * </ul>
	 *
	 * <p>JSON comparison behavior:
	 * <ul>
	 *   <li>Objects are serialized to JSON strings before comparison</li>
	 *   <li>Comparison is exact (including whitespace and field order)</li>
	 *   <li>Semantically equivalent JSON may fail comparison due to formatting</li>
	 * </ul>
	 *
	 * <p>Concurrency considerations:
	 * <ul>
	 *   <li>Provides atomic check-and-update semantics</li>
	 *   <li>Use for light-weight optimistic locking</li>
	 *   <li>For complex transactions, consider WATCH/MULTI</li>
	 * </ul>
	 *
	 * <p>Example usage:
	 * <pre>{@code
	 * User current = getValue();
	 * User updated = current.withEmail("new@example.com");
	 * if (compareAndSwap(current, updated)) {
	 *     // Update succeeded
	 * } else {
	 *     // Retry or handle contention
	 * }
	 * }</pre>
	 *
	 * @param oldValue the expected current value (exact JSON match required)
	 * @param newValue the new value to set if verification succeeds
	 * @return true if the value was updated, false otherwise
	 */
	@CAS
	Boolean compareAndSwap(@RedisOldValue T oldValue, @RedisValue T newValue);
	
	/**
	 * Retrieves and deserializes the JSON value from Redis.
	 * <p>
	 * This is the fundamental GET operation for JSON values in Redis.
	 *
	 * <p>Key characteristics:
	 * <ul>
	 *   <li>O(1) time complexity</li>
	 *   <li>Returns null if key does not exist</li>
	 *   <li>Uses Jackson for JSON deserialization</li>
	 *   <li>Supports complex object graphs</li>
	 * </ul>
	 *
	 * <p>Deserialization behavior:
	 * <ul>
	 *   <li>JSON string is parsed into Java object of type T</li>
	 *   <li>Follows Jackson's default deserialization rules</li>
	 *   <li>Type T must be compatible with stored JSON structure</li>
	 * </ul>
	 *
	 * <p>Performance considerations:
	 * <ul>
	 *   <li>Deserialization overhead increases with object complexity</li>
	 *   <li>Large JSON payloads may impact network transfer time</li>
	 * </ul>
	 *
	 * <p>Example usage:
	 * <pre>{@code
	 * User user = getValue();
	 * if (user != null) {
	 *     // Use the deserialized object
	 * }
	 * }</pre>
	 *
	 * <p>Implementation note:
	 * <ul>
	 *   <li>Must be overridden in child interfaces</li>
	 *   <li>Concrete return type must be specified</li>
	 * </ul>
	 *
	 * @return the deserialized object of type T, or null if key does not exist
	 */
	@GetValue
	T getValue();

}