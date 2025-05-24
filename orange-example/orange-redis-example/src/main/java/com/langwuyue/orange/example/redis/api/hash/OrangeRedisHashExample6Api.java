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
package com.langwuyue.orange.example.redis.api.hash;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import com.langwuyue.orange.example.redis.entity.OrangeHashExampleEntity;
import com.langwuyue.orange.redis.RedisValueTypeEnum;
import com.langwuyue.orange.redis.annotation.AddMembers;
import com.langwuyue.orange.redis.annotation.ContinueOnFailure;
import com.langwuyue.orange.redis.annotation.Count;
import com.langwuyue.orange.redis.annotation.GetMembers;
import com.langwuyue.orange.redis.annotation.GetSize;
import com.langwuyue.orange.redis.annotation.IfAbsent;
import com.langwuyue.orange.redis.annotation.Member;
import com.langwuyue.orange.redis.annotation.Multiple;
import com.langwuyue.orange.redis.annotation.OrangeRedisKey;
import com.langwuyue.orange.redis.annotation.Random;
import com.langwuyue.orange.redis.annotation.RemoveMembers;
import com.langwuyue.orange.redis.annotation.Timeout;
import com.langwuyue.orange.redis.annotation.hash.GetHashKeys;
import com.langwuyue.orange.redis.annotation.hash.GetHashValueLength;
import com.langwuyue.orange.redis.annotation.hash.GetHashValues;
import com.langwuyue.orange.redis.annotation.hash.HasKeys;
import com.langwuyue.orange.redis.annotation.hash.HashKey;
import com.langwuyue.orange.redis.annotation.hash.OrangeRedisHashClient;

/**
 * Comprehensive example demonstrating advanced Redis hash operations with JSON values.
 * 
 * <p>Combines features from all previous examples and adds:
 * <ul>
 *   <li>Bulk operations with configurable failure handling</li>
 *   <li>Conditional operations (add-if-absent)</li>
 *   <li>Random key access patterns</li>
 *   <li>Size and memory usage metrics</li>
 *   <li>Complex JSON object storage</li>
 * </ul>
 * 
 * <p>Key configuration:
 * <ul>
 *   <li>Fixed hash key: "orange:hash:example6"</li>
 *   <li>Default expiration: 1 hour</li>
 *   <li>JSON value storage for complex objects</li>
 * </ul>
 * 
 * <p>Advanced patterns:
 * <pre>{@code
 * // Bulk add with failure tolerance
 * List<OrangeHashExampleEntity> users = ...;
 * api.add(users); // Continues on failure by default
 * 
 * // Conditional add
 * OrangeHashExampleEntity newUser = ...;
 * api.addIfAbsent(newUser); // Only adds if key doesn't exist
 * 
 * // Random sampling
 * String randomKey = api.randomKey();
 * Set<String> sampleKeys = api.randomKeys(5);
 * 
 * // Memory analysis
 * Long totalSize = api.getSize();
 * Long valueSize = api.getHashValueBytesLength("user123");
 * }</pre>
 * 
 * <p>Performance notes:
 * <ul>
 *   <li>Bulk operations significantly reduce round-trips</li>
 *   <li>Random access has O(N) complexity - use judiciously</li>
 *   <li>Size operations may be expensive on large hashes</li>
 * </ul>
 * 
 * @author Liang.Zhong
 * @since 1.0.0
 * @see OrangeRedisHashExample1Api Basic JSON hash operations
 * @see OrangeRedisHashExample3Api Dynamic key operations
 * @see com.langwuyue.orange.redis.annotation.ContinueOnFailure Bulk operation configuration
 * @see com.langwuyue.orange.redis.annotation.IfAbsent Conditional operation configuration
 * @see com.langwuyue.orange.redis.annotation.Random Random access configuration
 */
@OrangeRedisHashClient(hashKeyType = RedisValueTypeEnum.STRING, hashValueType = RedisValueTypeEnum.JSON)
@OrangeRedisKey(expirationTime = @Timeout(value = 1, unit = TimeUnit.HOURS), key = "orange:hash:example6")
public interface OrangeRedisHashExample6Api {

	/**
	 * Adds a single entity to the Redis hash.
	 * <p>
	 * This operation corresponds to the Redis HSET command. It adds the entity
	 * to the hash using its natural key as the hash field. The entity is serialized
	 * as JSON before storage.
	 *
	 * @param entity The entity to add to the hash
	 * @see <a href="https://redis.io/commands/hset">Redis HSET command</a>
	 * @see AddMembers Annotation indicating this is a hash add operation
	 * @see Member Annotation marking the parameter as the member to add
	 */
	@AddMembers
	void add(@Member OrangeHashExampleEntity entity);

	/**
	 * Adds multiple entities to the Redis hash with failure tolerance.
	 * <p>
	 * This operation corresponds to multiple Redis HSET commands in a pipeline.
	 * If any operation fails, it will continue processing remaining entities.
	 * Entities are serialized as JSON before storage.
	 *
	 * @param members The list of entities to add to the hash
	 * @see <a href="https://redis.io/commands/hset">Redis HSET command</a>
	 * @see AddMembers Annotation indicating this is a hash add operation
	 * @see Multiple Annotation marking the parameter as multiple members
	 * @see ContinueOnFailure Annotation enabling failure tolerance
	 */
	@AddMembers
	@ContinueOnFailure(true)
	void add(@Multiple List<OrangeHashExampleEntity> members);

	/**
	 * Adds an entity to the Redis hash only if the key does not already exist.
	 * <p>
	 * This operation corresponds to the Redis HSETNX command. It will only add
	 * the entity if its key is not already present in the hash.
	 *
	 * @param entity The entity to conditionally add to the hash
	 * @see <a href="https://redis.io/commands/hsetnx">Redis HSETNX command</a>
	 * @see AddMembers Annotation indicating this is a hash add operation
	 * @see Member Annotation marking the parameter as the member to add
	 * @see IfAbsent Annotation enabling conditional add behavior
	 */
	@AddMembers
	@IfAbsent(deleteInTheEnd = false)
	void addIfAbsent(@Member OrangeHashExampleEntity entity);

	/**
	 * Adds multiple entities to the Redis hash with conditional add and failure tolerance.
	 * <p>
	 * This operation corresponds to multiple Redis HSETNX commands in a pipeline.
	 * Each entity will only be added if its key is not already present. The operation
	 * will continue processing remaining entities even if some fail.
	 *
	 * @param members The list of entities to conditionally add to the hash
	 * @see <a href="https://redis.io/commands/hsetnx">Redis HSETNX command</a>
	 * @see AddMembers Annotation indicating this is a hash add operation
	 * @see Multiple Annotation marking the parameter as multiple members
	 * @see IfAbsent Annotation enabling conditional add behavior
	 * @see ContinueOnFailure Annotation enabling failure tolerance
	 */
	@AddMembers
	@IfAbsent(deleteInTheEnd = false)
	@ContinueOnFailure(true)
	void addIfAbsent(@Multiple List<OrangeHashExampleEntity> members);

	/**
	 * Returns the number of fields in the Redis hash.
	 * <p>
	 * This operation corresponds to the Redis HLEN command. It returns the count
	 * of fields in the hash with O(1) time complexity.
	 *
	 * @return The number of fields in the hash, or 0 if key doesn't exist
	 * @see <a href="https://redis.io/commands/hlen">Redis HLEN command</a>
	 * @see GetSize Annotation indicating this is a size query operation
	 */
	@GetSize
	Long getSize();

	/**
	 * Returns the serialized byte length of a hash field's value.
	 * <p>
	 * This operation corresponds to inspecting the serialized size of the JSON value
	 * stored at the specified hash field. Useful for memory analysis and optimization.
	 *
	 * @param key The hash field key to inspect
	 * @return The byte length of the serialized value, or null if key doesn't exist
	 * @see GetHashValueLength Annotation indicating this is a value length query
	 * @see HashKey Annotation marking the parameter as a hash field key
	 */
	@GetHashValueLength
	Long getHashValueBytesLength(@HashKey String key);

	/**
	 * Returns all field keys in the Redis hash.
	 * <p>
	 * This operation corresponds to the Redis HKEYS command. It returns all
	 * field keys in the hash with O(N) time complexity where N is the field count.
	 *
	 * @return A set containing all field keys in the hash, or empty set if key doesn't exist
	 * @see <a href="https://redis.io/commands/hkeys">Redis HKEYS command</a>
	 * @see GetHashKeys Annotation indicating this is a keys query operation
	 */
	@GetHashKeys
	Set<String> getAllKeys();

	/**
	 * Returns a random field key from the Redis hash.
	 * <p>
	 * This operation corresponds to the Redis HRANDFIELD command with COUNT=1.
	 * It returns a random key from the hash without removing it.
	 *
	 * @return A random field key, or null if hash is empty
	 * @see <a href="https://redis.io/commands/hrandfield">Redis HRANDFIELD command</a>
	 * @see GetHashKeys Annotation indicating this is a keys query operation
	 * @see Random Annotation enabling random selection behavior
	 */
	@GetHashKeys
	@Random
	String randomKey();

	/**
	 * Returns multiple random field keys from the Redis hash.
	 * <p>
	 * This operation corresponds to the Redis HRANDFIELD command with COUNT.
	 * It returns the specified number of random keys from the hash without removing them.
	 * The same key may be returned multiple times.
	 *
	 * @param count The number of random keys to return
	 * @return A set of random field keys, may be smaller than requested if hash is small
	 * @see <a href="https://redis.io/commands/hrandfield">Redis HRANDFIELD command</a>
	 * @see GetHashKeys Annotation indicating this is a keys query operation
	 * @see Random Annotation enabling random selection behavior
	 * @see Count Annotation specifying the count parameter
	 */
	@GetHashKeys
	@Random
	Set<String> randomKeys(@Count Long count);

	/**
	 * Determines if a field exists in the Redis hash.
	 * <p>
	 * This operation corresponds to the Redis HEXISTS command. It checks if the
	 * specified field exists in the hash with O(1) time complexity.
	 *
	 * @param key The field key to check
	 * @return true if the field exists, false otherwise
	 * @see <a href="https://redis.io/commands/hexists">Redis HEXISTS command</a>
	 * @see HasKeys Annotation indicating this is an existence check operation
	 * @see HashKey Annotation marking the parameter as a hash field key
	 */
	@HasKeys
	Boolean hasKey(@HashKey String key);

	/**
	 * Determines which fields exist in the Redis hash with failure tolerance.
	 * <p>
	 * This operation corresponds to multiple Redis HEXISTS commands in a pipeline.
	 * It checks each specified field for existence in a single round-trip.
	 * The operation continues checking remaining fields even if some checks fail.
	 *
	 * @param key The collection of field keys to check
	 * @return A map with each key as key and a boolean indicating existence (true)
	 *         or non-existence (false)
	 * @see <a href="https://redis.io/commands/hexists">Redis HEXISTS command</a>
	 * @see HasKeys Annotation indicating this is an existence check operation
	 * @see Multiple Annotation marking the parameter as multiple keys
	 * @see ContinueOnFailure Annotation enabling failure tolerance
	 */
	@HasKeys
	@ContinueOnFailure(true)
	Map<String, Boolean> hasKeys(@Multiple Collection<String> key);

	/**
	 * Returns all values in the Redis hash.
	 * <p>
	 * This operation corresponds to the Redis HVALS command. It returns all
	 * values in the hash with O(N) time complexity where N is the field count.
	 * Values are deserialized from JSON back to entity objects.
	 *
	 * @return A list containing all values in the hash, or empty list if key doesn't exist
	 * @see <a href="https://redis.io/commands/hvals">Redis HVALS command</a>
	 * @see GetHashValues Annotation indicating this is a values query operation
	 */
	@GetHashValues
	List<OrangeHashExampleEntity> getValues();

	/**
	 * Returns the value associated with a field in the Redis hash.
	 * <p>
	 * This operation corresponds to the Redis HGET command. It returns the value
	 * stored at the specified field, deserialized from JSON back to an entity object.
	 *
	 * @param key The field key to retrieve
	 * @return The entity stored at the field, or null if field doesn't exist
	 * @see <a href="https://redis.io/commands/hget">Redis HGET command</a>
	 * @see GetHashValues Annotation indicating this is a values query operation
	 * @see HashKey Annotation marking the parameter as a hash field key
	 */
	@GetHashValues
	OrangeHashExampleEntity get(@HashKey String key);

	/**
	 * Returns multiple values from the Redis hash.
	 * <p>
	 * This operation corresponds to multiple Redis HGET commands in a pipeline.
	 * It retrieves values for all specified fields in a single round-trip.
	 * Values are deserialized from JSON back to entity objects.
	 *
	 * @param key The collection of field keys to retrieve
	 * @return A list of entities corresponding to the requested fields,
	 *         with null values for non-existent fields
	 * @see <a href="https://redis.io/commands/hget">Redis HGET command</a>
	 * @see GetHashValues Annotation indicating this is a values query operation
	 * @see Multiple Annotation marking the parameter as multiple keys
	 */
	@GetHashValues
	List<OrangeHashExampleEntity> get(@Multiple Collection<String> key);

	/**
	 * Returns all fields and values in the Redis hash.
	 * <p>
	 * This operation corresponds to the Redis HGETALL command. It returns all
	 * fields and their values in the hash with O(N) time complexity where N is the field count.
	 * Values are deserialized from JSON back to entity objects.
	 *
	 * @return A list containing all entities in the hash, or empty list if key doesn't exist
	 * @see <a href="https://redis.io/commands/hgetall">Redis HGETALL command</a>
	 * @see GetMembers Annotation indicating this is a complete hash query operation
	 */
	@GetMembers
	List<OrangeHashExampleEntity> getAllMembers();

	/**
	 * Removes a field from the Redis hash.
	 * <p>
	 * This operation corresponds to the Redis HDEL command. It deletes the specified
	 * field from the hash if it exists.
	 *
	 * @param key The field key to remove
	 * @return true if the field was removed, false if it didn't exist
	 * @see <a href="https://redis.io/commands/hdel">Redis HDEL command</a>
	 * @see RemoveMembers Annotation indicating this is a removal operation
	 * @see HashKey Annotation marking the parameter as a hash field key
	 */
	@RemoveMembers
	Boolean remove(@HashKey String key);

	/**
	 * Removes multiple fields from the Redis hash with failure tolerance.
	 * <p>
	 * This operation corresponds to multiple Redis HDEL commands in a pipeline.
	 * It removes all specified fields from the hash in a single round-trip.
	 * The operation continues processing remaining fields even if some deletions fail.
	 *
	 * @param keys The collection of field keys to remove
	 * @return A map with each key as key and a boolean indicating removal (true)
	 *         or non-existence (false)
	 * @see <a href="https://redis.io/commands/hdel">Redis HDEL command</a>
	 * @see RemoveMembers Annotation indicating this is a removal operation
	 * @see Multiple Annotation marking the parameter as multiple keys
	 * @see ContinueOnFailure Annotation enabling failure tolerance
	 */
	@RemoveMembers
	@ContinueOnFailure(true)
	Map<String, Boolean> remove(@Multiple Collection<String> keys);
}