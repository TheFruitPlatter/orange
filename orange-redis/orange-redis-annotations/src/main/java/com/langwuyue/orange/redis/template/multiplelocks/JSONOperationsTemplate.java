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
import com.langwuyue.orange.redis.annotation.AutoRenew;
import com.langwuyue.orange.redis.annotation.ContinueOnFailure;
import com.langwuyue.orange.redis.annotation.Multiple;
import com.langwuyue.orange.redis.annotation.TimeoutUnit;
import com.langwuyue.orange.redis.annotation.global.Delete;
import com.langwuyue.orange.redis.annotation.global.GetExpiration;
import com.langwuyue.orange.redis.annotation.global.SetExpiration;
import com.langwuyue.orange.redis.annotation.multiplelocks.MultipleLocks;
import com.langwuyue.orange.redis.annotation.multiplelocks.OrangeRedisMultipleLocksClient;
import com.langwuyue.orange.redis.annotation.transaction.Release;

/**
 * Interface template for Redis distributed multiple locks operations with JSON serialization support.
 * This template provides a comprehensive API for managing multiple distributed locks in Redis,
 * with automatic JSON serialization/deserialization of lock keys.
 * 
 * <p>Key features:
 * <ul>
 *   <li>Distributed locking with multiple keys</li>
 *   <li>Automatic JSON serialization of complex lock keys</li>
 *   <li>Event-driven business logic processing via listeners</li>
 *   <li>Automatic lock release after listener execution</li>
 *   <li>Configurable lock expiration and auto-renewal</li>
 *   <li>Batch operations for lock management</li>
 *   <li>Flexible error handling with continue-on-failure options</li>
 * </ul>
 * 
 * <p>Usage example:
 * <blockquote><pre>
 * // 1. Define the interface
 * {@code @OrangeRedisKey(
 *     expirationTime = @Timeout(value = 1, unit = TimeUnit.HOURS), 
 *     key = "orange:multiplelocks:orders"
 * )} 
 * public interface OrderLocksApi extends JSONOperationsTemplate{@code<OrderLock>} {
 *     // No additional methods needed
 * }
 * 
 * // 2. Define a listener to handle business logic
 * {@code @Component}
 * public class OrderProcessingListener implements OrangeRedisMultipleLocksListener {
 *     
 *     {@code @Override}
 *     public void onCompleted(OrangeMultipleLocksEvent event) {
 *         // Process orders when locks are acquired
 *         // Business logic goes here
 *         // Locks are automatically released after this method completes
 *     }
 * }
 * 
 * // 3. Use in service
 * {@code @Autowired}
 * private OrderLocksApi orderLocksApi;
 * 
 * public void processOrders(List{@code<OrderLock>} orderLocks) {
 *     // Simply acquire locks - business logic and lock release are handled by listener
 *     orderLocksApi.lock(orderLocks);
 * }
 * </pre></blockquote>
 * 
 * <p>Implementation notes:
 * <ul>
 *   <li>Lock operations are atomic and distributed</li>
 *   <li>Business logic is handled by listeners, similar to Spring events</li>
 *   <li>Locks are automatically released after listener execution completes</li>
 *   <li>Supports automatic TTL extension during long operations</li>
 *   <li>Provides batch operations for better performance</li>
 * </ul>
 * 
 * @param <T> The type of the lock key (will be serialized as JSON)
 * @author Liang.Zhong
 * @since 1.0.0
 * @see com.langwuyue.orange.redis.annotation.multiplelocks.OrangeRedisMultipleLocksClient
 */
@OrangeRedisMultipleLocksClient(valueType = RedisValueTypeEnum.JSON)
public interface JSONOperationsTemplate<T> {
	
	/**
	 * Acquires multiple distributed locks atomically and triggers the associated listener for business logic processing.
	 * 
	 * <p>This method attempts to acquire all specified locks in a batch operation.
	 * Each lock key is serialized to JSON before storage in Redis. The method
	 * applies the configured expiration time to each acquired lock.
	 * 
	 * <p><b>Event Handling:</b>
	 * After lock acquisition, the {@code OrangeRedisMultipleLocksListener} component is triggered.
	 * Developers should configure this listener to handle post-acquisition business logic.
	 * The listener implementation must be annotated with Spring's {@code @Component}.
	 * <b>Locks are automatically released after the listener execution completes</b>, so
	 * manual lock release is not required.
	 * 
	 * <p><b>Auto-Renewal:</b>
	 * The method automatically extends lock TTL when:
	 * <ul>
	 *   <li>Remaining TTL ≤ {@link AutoRenew#threshold()} * {@link com.langwuyue.orange.redis.annotation.OrangeRedisKey#expirationTime()}</li>
	 * </ul>
	 * This prevents locks from expiring during long-running operations in the listener.
	 * 
	 * <p><b>Error Handling:</b>
	 * The {@code @ContinueOnFailure(true)} annotation indicates that if acquiring a lock fails,
	 * the method will continue attempting to acquire remaining locks rather than aborting.
	 * 
	 * <p><b>Expiration:</b>
	 * The {@code @SetExpiration} annotation automatically sets a TTL for each acquired lock
	 * based on the configuration in {@code @OrangeRedisKey}.
	 * 
	 * <p><b>Example:</b>
	 * <pre>{@code
	 * // Define a listener to process orders
	 * @Component
	 * public class OrderProcessor implements OrangeRedisMultipleLocksListener<OrderLock> {
	 *     @Override
	 *     public void onLockAcquired(Collection<OrderLock> locks) {
	 *         // Process orders
	 *         // Locks are automatically released after this method completes
	 *     }
	 * }
	 * 
	 * // In your service
	 * List<OrderLock> orderLocks = Arrays.asList(
	 *     new OrderLock("order123"),
	 *     new OrderLock("order456")
	 * );
	 * orderLockApi.lock(orderLocks); // Triggers OrderProcessor.onLockAcquired()
	 * }</pre>
	 * 
	 * @param targets Collection of lock keys to acquire
	 * @see com.langwuyue.orange.redis.annotation.OrangeRedisKey
	 */
	@MultipleLocks
	@SetExpiration
	@ContinueOnFailure(true)
	@AutoRenew(autoInitKeyExpirationTime = true)
	void lock(@Multiple Collection<T> targets);
	
	/**
	 * Releases all locks forcibly, regardless of which process acquired them.
	 * 
	 * <p>This method performs a global release operation that removes all locks
	 * associated with the current key pattern. It's useful for administrative
	 * cleanup operations or recovering from error states.
	 * 
	 * <p><b>Note:</b> Under normal operation, locks are automatically released after
	 * listener execution completes. This method is primarily intended for administrative
	 * purposes or error recovery.
	 * 
	 * <p><b>Warning:</b> This is a destructive operation that will release locks
	 * even if they are currently being used by other processes. Use with caution.
	 * 
	 * <p><b>Example:</b>
	 * <pre>{@code
	 * // Emergency cleanup of all locks
	 * Boolean allReleased = orderLockApi.releaseAll();
	 * if (allReleased) {
	 *     log.info("All locks successfully released");
	 * } else {
	 *     log.warn("Some locks could not be released");
	 * }
	 * }</pre>
	 * 
	 * @return true if all locks were successfully released, false otherwise
	 */
	@Delete
	Boolean releaseAll();
	
	/**
	 * Releases the specified locks in a batch operation.
	 * 
	 * <p>This method attempts to release each of the specified locks. It returns
	 * a map indicating which locks were successfully released and which were not.
	 * 
	 * <p><b>Note:</b> Under normal operation, locks are automatically released after
	 * listener execution completes. This method is primarily intended for manual
	 * intervention or special cases where early lock release is required.
	 * 
	 * <p><b>Error Handling:</b>
	 * The {@code @ContinueOnFailure(true)} annotation indicates that if releasing a lock fails,
	 * the method will continue attempting to release remaining locks rather than aborting.
	 * This ensures maximum possible cleanup even in partial failure scenarios.
	 * 
	 * <p><b>Implementation Note:</b>
	 * Developers must override this method when extending this interface due to
	 * its generic return type involving type parameter T. Failure to override will
	 * result in runtime exceptions.
	 * 
	 * <p><b>Example:</b>
	 * <pre>{@code
	 * // For special cases where manual release is needed
	 * List<OrderLock> orderLocks = Arrays.asList(
	 *     new OrderLock("order123"),
	 *     new OrderLock("order456")
	 * );
	 * Map<OrderLock, Boolean> results = orderLockApi.release(orderLocks);
	 * 
	 * // Check individual results
	 * for (Map.Entry<OrderLock, Boolean> entry : results.entrySet()) {
	 *     if (!entry.getValue()) {
	 *         log.warn("Failed to release lock for: {}", entry.getKey());
	 *     }
	 * }
	 * }</pre>
	 * 
	 * @param targets Collection of lock keys to release
	 * @return A LinkedHashMap where keys are the lock keys and values indicate whether each lock was successfully released
	 */
	@Release
	@ContinueOnFailure(true)
	Map<T,Boolean> release(@Multiple Collection<T> targets);
	
	/**
	 * Retrieves the remaining time-to-live (TTL) for the specified locks in seconds.
	 * 
	 * <p>This method returns a map containing the TTL for each specified lock key.
	 * The TTL values are returned in seconds.
	 * 
	 * <p><b>Error Handling:</b>
	 * The {@code @ContinueOnFailure(true)} annotation indicates that if retrieving the TTL for a lock fails,
	 * the method will continue attempting to retrieve TTLs for remaining locks rather than aborting.
	 * 
	 * <p><b>Implementation Note:</b>
	 * Developers must override this method when extending this interface due to
	 * its generic return type involving type parameter T. Failure to override will
	 * result in runtime exceptions.
	 * 
	 * <p><b>Example:</b>
	 * <pre>{@code
	 * List<OrderLock> orderLocks = Arrays.asList(
	 *     new OrderLock("order123"),
	 *     new OrderLock("order456")
	 * );
	 * Map<OrderLock, Long> ttls = orderLockApi.getExpiration(orderLocks);
	 * 
	 * // Process TTL information
	 * for (Map.Entry<OrderLock, Long> entry : ttls.entrySet()) {
	 *     log.info("Lock {} expires in {} seconds", entry.getKey(), entry.getValue());
	 * }
	 * }</pre>
	 * 
	 * @param targets Collection of lock keys to check
	 * @return A LinkedHashMap where keys are the lock keys and values are the TTLs in seconds
	 */
	@GetExpiration
	@ContinueOnFailure(true)
	Map<T,Long> getExpiration(@Multiple Collection<T> targets);
	
	/**
	 * Retrieves the remaining time-to-live (TTL) for the specified locks in the requested time unit.
	 * 
	 * <p>This method returns a map containing the TTL for each specified lock key.
	 * The TTL values are converted to the specified time unit.
	 * 
	 * <p><b>Error Handling:</b>
	 * The {@code @ContinueOnFailure(true)} annotation indicates that if retrieving the TTL for a lock fails,
	 * the method will continue attempting to retrieve TTLs for remaining locks rather than aborting.
	 * 
	 * <p><b>Implementation Note:</b>
	 * Developers must override this method when extending this interface due to
	 * its generic return type involving type parameter T. Failure to override will
	 * result in runtime exceptions.
	 * 
	 * <p><b>Example:</b>
	 * <pre>{@code
	 * List<OrderLock> orderLocks = Arrays.asList(
	 *     new OrderLock("order123"),
	 *     new OrderLock("order456")
	 * );
	 * Map<OrderLock, Long> ttls = orderLockApi.getExpiration(orderLocks, TimeUnit.MINUTES);
	 * 
	 * // Process TTL information
	 * for (Map.Entry<OrderLock, Long> entry : ttls.entrySet()) {
	 *     log.info("Lock {} expires in {} minutes", entry.getKey(), entry.getValue());
	 * }
	 * }</pre>
	 * 
	 * @param targets Collection of lock keys to check
	 * @param unit The time unit for the returned TTL values
	 * @return A LinkedHashMap where keys are the lock keys and values are the TTLs in the specified time unit
	 * @see java.util.concurrent.TimeUnit
	 */
	@GetExpiration
	@ContinueOnFailure(true)
	Map<T,Long> getExpiration(@Multiple Collection<T> targets,@TimeoutUnit TimeUnit unit);
}