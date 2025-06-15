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

import java.util.LinkedHashSet;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BiConsumer;

import com.langwuyue.orange.redis.OrangeRedisException;
import com.langwuyue.orange.redis.RedisValueTypeEnum;
import com.langwuyue.orange.redis.context.OrangeRedisIterableContext;
import com.langwuyue.orange.redis.executor.transaction.OrangeRedisTransactionDaemonRunner.LockKey;
import com.langwuyue.orange.redis.listener.multiplelocks.OrangeMultipleLocksEvent;
import com.langwuyue.orange.redis.listener.multiplelocks.OrangeMultipleLocksRemoveFailedEvent;
import com.langwuyue.orange.redis.listener.multiplelocks.OrangeRedisMultipleLocksListener;
import com.langwuyue.orange.redis.logger.OrangeRedisLogger;
import com.langwuyue.orange.redis.operations.OrangeRedisHashOperations;
import com.langwuyue.orange.redis.operations.OrangeRedisSetOperations;
import com.langwuyue.orange.redis.operations.OrangeRedisSetOperations.ScanResults;
import com.langwuyue.orange.redis.registry.OrangeRedisKeyRegistry;

/**
 * A garbage collector for Redis transactions that manages cleanup of historical versions and dead transactions.
 * 
 * <p>This class implements a scheduled garbage collection mechanism for Redis transactions.
 * It periodically scans for and cleans up:
 * <ul>
 *   <li>Historical versions of transactions that are no longer needed</li>
 *   <li>Dead transactions that have timed out</li>
 * </ul>
 * 
 * 
 * <p>The garbage collector uses a distributed locking mechanism to ensure that only
 * one instance runs at a time across multiple nodes. It maintains a registry of
 * keys that need to be cleaned up and processes them in batches using Redis SCAN
 * operation to prevent blocking.
 * 
 * <p>Key features:
 * <ul>
 *   <li>Scheduled execution with configurable intervals</li>
 *   <li>Distributed coordination using locks</li>
 *   <li>Batch processing of keys</li>
 *   <li>Performance monitoring with warnings for long-running operations</li>
 *   <li>Automatic cleanup of historical versions and dead transactions</li>
 * </ul>
 * 
 *
 * @author Liang.Zhong
 * @since 1.0.0
 */
public class OrangeRedisTransactionGarbageCollector implements Runnable, OrangeRedisMultipleLocksListener,OrangeRedisIterableContext {
	
	/**
	 * Redis key prefix for the garbage collection registry.
	 * This registry maintains a set of keys that need to be processed by the GC.
	 */
	private static final String TRANSACTION_GC_KEY_REGISTRY = "orange:transaction:gc:keys:registry:";
	
	/**
	 * Scheduled executor service for running periodic GC tasks.
	 * Uses a single thread to avoid concurrent execution.
	 */
	private ScheduledExecutorService executorService;
	
	/**
	 * Redis hash operations for accessing and modifying transaction data.
	 */
	private OrangeRedisHashOperations operations;
	
	/**
	 * Redis set operations for managing the GC registry.
	 */
	private OrangeRedisSetOperations setOperations;
	
	/**
	 * Cursor position for Redis SCAN operation.
	 * Used to track progress when scanning large sets of keys.
	 */
	private long cursor;
	
	/**
	 * The complete Redis key for the GC registry, including service name.
	 */
	private String gcRegistry;
	
	/**
	 * Daemon runner for coordinating GC tasks across distributed nodes.
	 */
	private OrangeRedisTransactionDaemonRunner runner;
	
	/**
	 * Logger for recording GC operations and errors.
	 */
	private OrangeRedisLogger logger;
	
	/**
	 * Configuration properties for the transaction system.
	 */
	private OrangeRedisTransactionProperties properties;
	
	/**
	 * Executor for handling timeout callbacks and dead transaction cleanup.
	 */
	private OrangeRedisTransactionTimeoutCallbackExecutor transactionTimeoutCallbackExecutor;
	
	/**
	 * Constructs a new garbage collector for Redis transactions.
	 * 
	 * <p>Initializes the garbage collector with necessary dependencies, creates a single-threaded
	 * scheduled executor service, and starts the periodic garbage collection process. The GC registry
	 * key is composed of a prefix and the service name to ensure uniqueness across different services.
	 *
	 * @param operations Redis hash operations for transaction data access
	 * @param setOperations Redis set operations for registry management
	 * @param properties Configuration properties for the transaction system
	 * @param logger Logger for recording operations and errors
	 * @param transactionTimeoutCallbackExecutor Executor for timeout callbacks
	 */
	public OrangeRedisTransactionGarbageCollector(
		OrangeRedisHashOperations operations, 
		OrangeRedisSetOperations setOperations,
		OrangeRedisTransactionProperties properties,
		OrangeRedisLogger logger,
		OrangeRedisTransactionTimeoutCallbackExecutor transactionTimeoutCallbackExecutor
	) {
		super();
		// A single schedule thread.
		this.executorService = Executors.newScheduledThreadPool(1,new OrangeTransactionGarbageCollectorThreadFactory());
		this.operations = operations;
		this.properties = properties;
		this.setOperations = setOperations;
		this.cursor = 0;
		this.gcRegistry = TRANSACTION_GC_KEY_REGISTRY + properties.getServiceName();
		this.logger = logger;
		
		// Check for duplicate keys by registering them in the registry. 
		OrangeRedisKeyRegistry.register(this.gcRegistry, null, OrangeRedisDefaultTransactionManager.class);
		
		// Start the thread pool
		this.executorService.scheduleAtFixedRate(
			this, 
			properties.getGcThreadInitialDelay().toMillis(), 
			properties.getGcThreadPeriod().toMillis(), 
			TimeUnit.MILLISECONDS
		);
	}

	/**
	 * Executes the garbage collection task.
	 * 
	 * <p>This method is called periodically by the scheduled executor service.
	 * It uses the daemon runner to coordinate the GC task across distributed nodes,
	 * ensuring that only one instance runs at a time. The method logs the start and
	 * completion of the GC process, as well as any errors that occur.
	 * 
	 * <p>If the runner is not initialized, the method will log a debug message and return
	 * without performing any GC operations.
	 */
	@Override
	public void run() {
		try {
			if(this.runner == null) {
				logger.debug("Transaction GC is not initialized.");
				return;
			}
			logger.debug("Transaction GC is executing now.");
			this.runner.run(OrangeRedisTransactionDaemonRunner.GC,this,this.properties.getGcThreadPeriod().toMillis());
			logger.debug("Transaction GC done.");
		} catch (Exception e) {
			logger.error("Transaction GC error", e);
		}
	}

	/**
	 * Registers a key for garbage collection.
	 * 
	 * <p>Adds the specified key to the GC registry, marking it for cleanup during the next
	 * garbage collection cycle. This method is typically called when a transaction is
	 * completed or becomes invalid.
	 *
	 * @param key The Redis key to be added to the GC registry
	 * @throws Exception If an error occurs while adding the key to the registry
	 */
	void register(String key) throws Exception {
		this.setOperations.add(this.gcRegistry, RedisValueTypeEnum.STRING, key);
	}
	
	/**
	 * Handles the completion of a multiple locks operation.
	 * 
	 * <p>This method is called when a distributed lock operation completes. It performs
	 * the actual garbage collection tasks if the lock was successfully acquired and
	 * the event is related to the GC operation. The method performs two main tasks:
	 * <ol>
	 *   <li>Clearing historical transaction versions</li>
	 *   <li>Clearing dead transactions</li>
	 * </ol>
	 * 
	 * <p>The method also monitors performance by measuring the execution time of each task
	 * and logs a warning if the total execution time exceeds the configured GC interval.
	 *
	 * @param event The event containing information about the completed lock operation
	 */
	@Override
	public void onCompleted(OrangeMultipleLocksEvent event) {
		try {
			Object arg = event.getArgs()[1];
			if(!OrangeRedisTransactionDaemonRunner.GC.equals(arg)) {
				return;
			}
			if(!event.getFailedMembers().isEmpty() || !event.getUnknownMembers().isEmpty()) {
				this.logger.debug("Transaction GC access failed, maybe another server node is running now.");
				return;
			}
			
			// Clear history version
			long start = System.currentTimeMillis();
			this.logger.info("TransactionGC started clearing historical transaction versions.");
			clear();
			long clearCost = System.currentTimeMillis() - start;
			this.logger.info("TTransactionGC cleared historical transaction versions in {}ms.", clearCost);
			
			// Clear dead transaction
			start = System.currentTimeMillis();
			this.logger.info("TransactionGC started clearing dead transaction.");
			transactionTimeoutCallbackExecutor.clearDeadTransaction();
			long clearDeadTxCost = System.currentTimeMillis() - start;
			this.logger.info("TransactionGC cleared dead transactions in {}ms.", clearDeadTxCost);
			
			// Notify the developers when execution time > GC interval.
			long total = clearDeadTxCost + clearCost;
			if(total >= this.properties.getGcThreadPeriod().toMillis()) {
				this.logger.warn("TransactionGC execution time {}ms > GC interval {}ms", total, this.properties.getGcThreadPeriod().toMillis());
			}
		}catch (Exception e) {
			logger.error("Transaction GC error", e);
		}
	}
	
	/**
	 * Clears historical versions of transactions.
	 * 
	 * <p>This method performs the actual cleanup of historical transaction versions by:
	 * <ol>
	 *   <li>Scanning the GC registry for keys to process</li>
	 *   <li>For each key, determining the current version and the last cleared version</li>
	 *   <li>Removing all historical versions between the last cleared version and the current version</li>
	 *   <li>Updating the cursor position for each key to mark progress</li>
	 *   <li>Removing processed keys from the GC registry</li>
	 * </ol>
	 * 
	 * 
	 * <p>The method uses Redis SCAN operation to process keys in batches, preventing blocking
	 * of the Redis server during large cleanup operations. It maintains a cursor position
	 * to continue scanning from where it left off in the next GC cycle.
	 *
	 * @throws Exception If an error occurs during the cleanup process
	 */
	private void clear() throws Exception {
		if(this.setOperations == null) {
			return;
		}
		// Scan keys
		ScanResults results = this.setOperations.scan(this.gcRegistry, "*", 1000, this.cursor, RedisValueTypeEnum.STRING, String.class);
		if(results == null || results.getMembers().isEmpty()) {
			logger.debug("Transaction GC completed key scanning");
			// Reset cursor.
			this.cursor = 1;
			return;
		}
		// Set for collecting invalid keys, the invalid keys will be removed at last.
		Set<String> invalidKeys = new LinkedHashSet<>();
		Set<Object> keys = results.getMembers();
		for(Object keyObject : keys) {
			String key = (String)keyObject;
			logger.debug("Transaction GC is clearing the history versions of {}.", key);
			
			// Get current version
			Object result = this.operations.get(key, OrangeRedisTransactionKeyConstants.CURRENT_VERSION, RedisValueTypeEnum.STRING, RedisValueTypeEnum.LONG, Long.class);
			if(result == null) {
				invalidKeys.add(key);
				logger.debug("The key({}) of Transaction GC is invalid.", key);
				continue;
			}
			
			// Get cursor position of this key
			Long version = (Long) result;
			result = this.operations.get(key, OrangeRedisTransactionKeyConstants.CLEAR_OLD_VERSION_CURSOR, RedisValueTypeEnum.STRING, RedisValueTypeEnum.LONG, Long.class);
			Long current = 0L;
			if(result != null) {
				current = (Long) result;
			}
			
			// Generate version keys before current version.
			Object[] hashKeys = new Object[(int)(version-current)];
			for(int i = 0; current < version; current++,i++) {
				hashKeys[i] = OrangeRedisTransactionKeyConstants.VERSION_PREFIX+current;
			}
			
			// Clear history versions
			logger.debug("The key's({}) history versions({}) will be clear by Transaction GC.", key, hashKeys);
			Long removed = this.operations.removeMembers(key, RedisValueTypeEnum.STRING, hashKeys);
			logger.debug("Transaction GC was cleared {} history versions of {}.", removed, key);
			
			// Update cursor position of this key for the next cleanup cycle.
			this.operations.putMember(key, OrangeRedisTransactionKeyConstants.CLEAR_OLD_VERSION_CURSOR, current, RedisValueTypeEnum.STRING, RedisValueTypeEnum.LONG);
			invalidKeys.add(key);
		}
		
		// Remove invalid keys from GC keys registry.
		this.setOperations.remove(this.gcRegistry, RedisValueTypeEnum.STRING, invalidKeys.toArray());
		if(results.getCursor() != 0) {
			// Update the cursor position for the next iteration of SCAN.
			this.cursor = results.getCursor();
		}
	}
	
	/**
	 * A thread factory for creating garbage collector threads.
	 * 
	 * <p>This inner class implements ThreadFactory to create daemon threads for the
	 * garbage collector. Each thread is named with a unique identifier to distinguish
	 * multiple GC threads if needed.
	 */
	static class OrangeTransactionGarbageCollectorThreadFactory implements ThreadFactory {
		/**
		 * Counter for generating unique thread numbers.
		 */
		private final AtomicInteger threadNumber = new AtomicInteger(1);
		
		/**
		 * Creates a new thread for the garbage collector with a unique name.
		 *
		 * @param r The Runnable to be executed by the new thread
		 * @return A new thread with a unique name
		 */
		@Override
		public Thread newThread(Runnable r) {
			int seq = threadNumber.getAndIncrement();
			return new Thread(r, "orange-redis-tx-gc-" + seq);
		}
	}

	/**
	 * Sets the daemon runner for this garbage collector.
	 * 
	 * <p>The daemon runner is responsible for coordinating GC tasks across distributed nodes,
	 * ensuring that only one instance runs at a time. This method is typically called during
	 * initialization of the transaction system.
	 *
	 * @param runner The daemon runner to use for coordinating GC tasks
	 */
	void setRunner(OrangeRedisTransactionDaemonRunner runner) {
		this.runner = runner;
	}

	/**
	 * Applies the given action to each lock key in the context.
	 * 
	 * <p>This method is part of the OrangeRedisIterableContext interface implementation.
	 * It creates a lock key for the GC operation and applies the specified action to it.
	 * This is used by the daemon runner to manage distributed locks for GC operations.
	 *
	 * @param action The action to be performed on the lock key
	 */
	@Override
	public void forEach(BiConsumer action) {
		LockKey lockKey = new LockKey();
		lockKey.setLockType(OrangeRedisTransactionDaemonRunner.GC);
		lockKey.setServiceName(this.properties.getServiceName());
		action.accept(lockKey, lockKey);
	}

	/**
	 * Returns an array containing all elements in this context.
	 * 
	 * <p>This method is part of the OrangeRedisIterableContext interface but is not
	 * supported by this implementation. The garbage collector does not need to
	 * convert its context to an array, so this method throws an exception if called.
	 *
	 * @return Never returns as this method always throws an exception
	 * @throws OrangeRedisException Always thrown to indicate this operation is not supported
	 */
	@Override
	public Object[] toArray() {
		throw new OrangeRedisException("Not support!");
	}

	/**
	 * Determines whether the garbage collector should continue operation after a failure.
	 * 
	 * <p>This method is part of the OrangeRedisIterableContext interface and indicates
	 * that the garbage collector should stop its operation if a failure occurs. This is
	 * important for maintaining data consistency, as failures in garbage collection
	 * might indicate more serious issues that should be addressed before continuing.
	 *
	 * @return Always returns false to indicate that the GC process should stop on failure
	 */
	@Override
	public boolean continueOnFailure() {
		return false;
	}

	/**
	 * Handles the event when removal of multiple locks fails.
	 * 
	 * <p>This method is called when the system fails to release a distributed lock
	 * used for coordinating GC operations. Instead of taking corrective action,
	 * it logs a warning message and relies on the lock's auto-expiration mechanism.
	 * This approach prevents potential deadlocks while ensuring that another node
	 * can eventually acquire the lock after expiration.
	 *
	 * @param event The event containing information about the failed lock removal
	 */
	@Override
	public void onRemoveFailed(OrangeMultipleLocksRemoveFailedEvent event) {
		this.logger.warn(
			"Failed to release the transaction GC lock. No action taken; the lock should auto-expire in {} milliseconds.",
			TimeUnit.MILLISECONDS.convert(event.getRedisKey().getExpirationTime(), event.getRedisKey().getExpirationTimeUnit())
		);
	}

	/**
	 * Performs cleanup operations when the garbage collector is being destroyed.
	 * 
	 * <p>This method is called during the shutdown process of the garbage collector.
	 * It ensures that the executor service is properly shut down, preventing any
	 * new GC tasks from being scheduled while allowing currently running tasks
	 * to complete gracefully.
	 *
	 * <p>Note: The method name has a typo ('destory' instead of 'destroy'),
	 * but it is maintained for backward compatibility.
	 */
	public void destory() {
		if(this.executorService != null) {
			this.logger.info("Transaction GC thread pool is shutting down now");
			this.executorService.shutdown();
		}
	}
}