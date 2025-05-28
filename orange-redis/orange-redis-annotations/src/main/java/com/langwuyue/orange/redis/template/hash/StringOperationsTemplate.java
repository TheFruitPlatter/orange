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
package com.langwuyue.orange.redis.template.hash;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import com.langwuyue.orange.redis.RedisValueTypeEnum;
import com.langwuyue.orange.redis.annotation.hash.OrangeRedisHashClient;

/**
 * Interface template for Redis Hash operations with String values.
 * This template extends {@link JSONOperationsTemplate} but is optimized for String values,
 * providing better performance by eliminating JSON serialization/deserialization overhead.
 * 
 * <p>Key features:
 * <ul>
 *   <li>Direct String value storage without serialization</li>
 *   <li>Improved performance over JSON operations</li>
 *   <li>String-based hash keys and values</li>
 *   <li>Inherits all Redis Hash operations from parent</li>
 *   <li>Automatic null handling</li>
 *   <li>Thread-safe operations</li>
 * </ul>
 * 
 * <p>Usage example:
 * <pre>{@code
 * // 1. Define your Redis Hash interface
 * @OrangeRedisKey(
 *     expirationTime = @Timeout(value = 1, unit = TimeUnit.DAYS),
 *     key = "orange:hash:config"
 * )
 * public interface ConfigurationHash extends StringOperationsTemplate {
 *     // Inherit all operations from template
 * }
 * 
 * // 2. Use in your service
 * {@code @Service}
 * public class ConfigService {
 *     {@code @Autowired}
 *     private ConfigurationHash configHash;
 *     
 *     public void saveConfig(String key, String value) {
 *         configHash.add(key, value);
 *     }
 *     
 *     public String getConfig(String key) {
 *         return configHash.get(key);
 *     }
 *     
 *     public Map<String, String> getAllConfig() {
 *         return configHash.getAllMembers();
 *     }
 *     
 *     public void updateConfigIfNotChanged(String key, String oldValue, String newValue) {
 *         boolean updated = configHash.compareAndSwap(key, oldValue, newValue);
 *         if (!updated) {
 *             throw new ConcurrentModificationException("Config was modified by another process");
 *         }
 *     }
 * }
 * }</pre>
 * 
 * <p>Performance considerations:
 * <ul>
 *   <li>Uses Redis STRING data type directly</li>
 *   <li>No serialization/deserialization overhead</li>
 *   <li>Ideal for simple string storage</li>
 *   <li>Better memory usage than JSON storage</li>
 *   <li>Faster operation execution</li>
 * </ul>
 * 
 * <p>Implementation notes:
 * <ul>
 *   <li>All methods are implemented by the framework at runtime</li>
 *   <li>Default implementations return null</li>
 *   <li>Thread-safe operations</li>
 *   <li>Automatic connection handling</li>
 *   <li>Integrated error handling</li>
 * </ul>
 * 
 * @author Liang.Zhong
 * @since 1.0.0
 * @see JSONOperationsTemplate
 * @see com.langwuyue.orange.redis.annotation.hash.OrangeRedisHashClient
 * @see com.langwuyue.orange.redis.annotation.OrangeRedisKey
 */
@OrangeRedisHashClient(
	hashKeyType = RedisValueTypeEnum.STRING,
	hashValueType = RedisValueTypeEnum.STRING
)
public interface StringOperationsTemplate extends JSONOperationsTemplate<String> {

	/**
	 * Gets all values from the hash (without keys).
	 * This operation is equivalent to the Redis HVALS command.
	 * 
	 * <p>This method overrides the parent method to provide String-specific implementation.
	 * 
	 * <p>Example:
	 * <pre>{@code
	 * List<String> allValues = configHash.getValues();
	 * log.info("Retrieved {} configuration values", allValues.size());
	 * }</pre>
	 * 
	 * @return List of all String values in the hash
	 */
	@Override
	default List<String> getValues() {
		return null;
	}

	/**
	 * Gets a single String value by field name.
	 * This operation is equivalent to the Redis HGET command.
	 * 
	 * <p>This method overrides the parent method to provide String-specific implementation.
	 * 
	 * <p>Example:
	 * <pre>{@code
	 * String value = configHash.get("max-connections");
	 * if (value != null) {
	 *     int maxConnections = Integer.parseInt(value);
	 *     log.info("Max connections: {}", maxConnections);
	 * } else {
	 *     log.info("Max connections not configured");
	 * }
	 * }</pre>
	 * 
	 * @param key Field name to retrieve
	 * @return The String value, or null if not found
	 */
	@Override
	default String get(String key) {
		return null;
	}

	/**
	 * Batch retrieval of multiple String values.
	 * This operation is equivalent to the Redis HMGET command.
	 * 
	 * <p>This method overrides the parent method to provide String-specific implementation.
	 * 
	 * <p>Example:
	 * <pre>{@code
	 * List<String> keys = Arrays.asList("max-connections", "timeout", "retry-count");
	 * Map<String, String> configs = configHash.get(keys);
	 * 
	 * configs.forEach((key, value) -> {
	 *     if (value != null) {
	 *         log.info("{}: {}", key, value);
	 *     } else {
	 *         log.info("{} not configured", key);
	 *     }
	 * });
	 * }</pre>
	 * 
	 * @param key Collection of field names to retrieve
	 * @return Map of field names to String values (null for non-existent fields)
	 */
	@Override
	default Map<String, String> get(Collection<String> key) {
		return null;
	}

	/**
	 * Gets all key-value pairs from the hash.
	 * This operation is equivalent to the Redis HGETALL command.
	 * 
	 * <p>This method overrides the parent method to provide String-specific implementation.
	 * 
	 * <p>Example:
	 * <pre>{@code
	 * Map<String, String> allConfigs = configHash.getAllMembers();
	 * log.info("Retrieved {} configuration entries", allConfigs.size());
	 * 
	 * allConfigs.forEach((key, value) -> {
	 *     log.info("{}: {}", key, value);
	 * });
	 * }</pre>
	 * 
	 * @return Map of all field names to String values
	 */
	@Override
	default Map<String, String> getAllMembers() {
		return null;
	}
}