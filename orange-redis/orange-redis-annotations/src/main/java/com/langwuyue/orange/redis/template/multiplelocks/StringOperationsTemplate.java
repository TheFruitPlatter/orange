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
package com.langwuyue.orange.redis.template.multiplelocks;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import com.langwuyue.orange.redis.RedisValueTypeEnum;
import com.langwuyue.orange.redis.annotation.multiplelocks.OrangeRedisMultipleLocksClient;

/**
 * Specialized interface template for Redis distributed multiple locks operations with String keys.
 * This template extends {@link JSONOperationsTemplate} but is optimized for simple String lock keys,
 * eliminating the need for JSON serialization/deserialization.
 * 
 * <p>Key features:
 * <ul>
 *   <li>Simplified API for String-based lock keys</li>
 *   <li>Event-driven business logic via listeners</li>
 *   <li>Automatic lock release after listener execution</li>
 *   <li>Improved performance with direct String storage</li>
 *   <li>Compatible with all JSONOperationsTemplate features</li>
 *   <li>No serialization overhead for simple use cases</li>
 * </ul>
 * 
 * <p>Usage example:
 * <blockquote><pre>
 * // 1. Define the interface
 * {@code @OrangeRedisKey(
 *     expirationTime = @Timeout(value = 30, unit = TimeUnit.MINUTES), 
 *     key = "orange:locks:resources"
 * )} 
 * public interface ResourceLockApi extends StringOperationsTemplate {
 *     // No additional methods needed
 * }
 * 
 * // 2. Define a listener to handle business logic
 * {@code @Component}
 * public class ResourceProcessor implements OrangeRedisMultipleLocksListener {
 *     
 *     {@code @Override}
 *     public void onCompleted(Collection{@code<String>} resourceIds) {
 *         // Process resources when locks are acquired
 *         // Business logic goes here
 *         // Locks are automatically released after this method completes
 *     }
 * }
 * 
 * // 3. Use in service
 * {@code @Autowired}
 * private ResourceLockApi resourceLockApi;
 * 
 * public void processResources(List{@code <String>} resourceIds) {
 *     // Simply acquire locks - business logic and lock release are handled by listener
 *     resourceLockApi.lock(resourceIds);
 * }
 * </pre></blockquote>
 * 
 * <p>Implementation notes:
 * <ul>
 *   <li>Uses Redis STRING data type for storage</li>
 *   <li>Business logic is handled by listeners, similar to Spring events</li>
 *   <li>Locks are automatically released after listener execution completes</li>
 *   <li>Inherits all functionality from JSONOperationsTemplate</li>
 *   <li>Provides default implementations for required methods</li>
 * </ul>
 * 
 * @author Liang.Zhong
 * @since 1.0.0
 * @see JSONOperationsTemplate
 * @see com.langwuyue.orange.redis.annotation.multiplelocks.OrangeRedisMultipleLocksClient
 */
@OrangeRedisMultipleLocksClient(valueType = RedisValueTypeEnum.STRING)
public interface StringOperationsTemplate extends JSONOperationsTemplate<String> {

	/**
	 * Releases the specified String locks in a batch operation.
	 * 
	 * <p>This method provides a default implementation that returns null.
	 * At runtime, the Orange Redis framework replaces this implementation
	 * with the actual functionality to release locks.
	 * 
	 * <p><b>Note:</b> Under normal operation, locks are automatically released after
	 * listener execution completes. This method is primarily intended for administrative
	 * purposes or error recovery.
	 * 
	 * <p><b>Example:</b>
	 * <pre>{@code
	 * // For administrative cleanup only - not needed in normal operation
	 * List<String> resourceIds = Arrays.asList("resource1", "resource2");
	 * Map<String, Boolean> results = resourceLockApi.release(resourceIds);
	 * 
	 * // Check individual results
	 * for (Map.Entry<String, Boolean> entry : results.entrySet()) {
	 *     if (!entry.getValue()) {
	 *         log.warn("Failed to release lock for: {}", entry.getKey());
	 *     }
	 * }
	 * }</pre>
	 * 
	 * @param targets Collection of String lock keys to release
	 * @return A map where keys are the lock keys and values indicate whether each lock was successfully released
	 */
	@Override
	default Map<String, Boolean> release(Collection<String> targets) {
		return null;
	}

	/**
	 * Retrieves the remaining time-to-live (TTL) for the specified String locks in seconds.
	 * 
	 * <p>This method provides a default implementation that returns null.
	 * At runtime, the Orange Redis framework replaces this implementation
	 * with the actual functionality to retrieve TTL values.
	 * 
	 * <p><b>Example:</b>
	 * <pre>{@code
	 * List<String> resourceIds = Arrays.asList("resource1", "resource2");
	 * Map<String, Long> ttls = resourceLockApi.getExpiration(resourceIds);
	 * 
	 * // Process TTL information
	 * for (Map.Entry<String, Long> entry : ttls.entrySet()) {
	 *     log.info("Lock {} expires in {} seconds", entry.getKey(), entry.getValue());
	 * }
	 * }</pre>
	 * 
	 * @param targets Collection of String lock keys to check
	 * @return A map where keys are the lock keys and values are the TTLs in seconds
	 */
	@Override
	default Map<String, Long> getExpiration(Collection<String> targets) {
		return null;
	}

	/**
	 * Retrieves the remaining time-to-live (TTL) for the specified String locks in the requested time unit.
	 * 
	 * <p>This method provides a default implementation that returns null.
	 * At runtime, the Orange Redis framework replaces this implementation
	 * with the actual functionality to retrieve TTL values in the specified time unit.
	 * 
	 * <p><b>Example:</b>
	 * <pre>{@code
	 * List<String> resourceIds = Arrays.asList("resource1", "resource2");
	 * Map<String, Long> ttls = resourceLockApi.getExpiration(resourceIds, TimeUnit.MINUTES);
	 * 
	 * // Process TTL information
	 * for (Map.Entry<String, Long> entry : ttls.entrySet()) {
	 *     log.info("Lock {} expires in {} minutes", entry.getKey(), entry.getValue());
	 * }
	 * }</pre>
	 * 
	 * @param targets Collection of String lock keys to check
	 * @param unit The time unit for the returned TTL values
	 * @return A map where keys are the lock keys and values are the TTLs in the specified time unit
	 * @see java.util.concurrent.TimeUnit
	 */
	@Override
	default Map<String, Long> getExpiration(Collection<String> targets, TimeUnit unit) {
		return null;
	}
	
}