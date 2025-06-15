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

import java.util.Collection;
import java.util.concurrent.TimeUnit;

import com.langwuyue.orange.redis.RedisValueTypeEnum;
import com.langwuyue.orange.redis.context.OrangeRedisContext.Key;
import com.langwuyue.orange.redis.context.OrangeRedisIterableContext;
import com.langwuyue.orange.redis.executor.multiplelocks.OrangeExpirationTimeAutoInitializer;
import com.langwuyue.orange.redis.executor.multiplelocks.OrangeMultipleLocksExecutor;
import com.langwuyue.orange.redis.listener.OrangeRedisMultipleSetIfAbsentListener;
import com.langwuyue.orange.redis.logger.OrangeRedisLogger;
import com.langwuyue.orange.redis.mapping.OrangeRedisMultipleLocksExecutorIdGenerator;
import com.langwuyue.orange.redis.operations.OrangeRedisHashOperations;
import com.langwuyue.orange.redis.operations.OrangeRedisScriptOperations;
import com.langwuyue.orange.redis.registry.OrangeRedisKeyRegistry;
import com.langwuyue.orange.redis.timer.OrangeRenewTimerWheel;

/**
 * A daemon runner responsible for executing background tasks related to Redis transactions.
 * 
 * <p>This class manages the execution of periodic tasks such as garbage collection
 * and callback processing for Redis transactions. It uses a locking mechanism to ensure
 * that only one instance of each task runs at a time across a distributed system.
 * 
 * <p>The daemon runner is designed to work with the Orange Redis transaction system
 * and integrates with the multiple locks executor to provide distributed coordination.
 *
 * @author Liang.Zhong
 * @since 1.0.0
 */
class OrangeRedisTransactionDaemonRunner {
	
	/**
	 * Key prefix for transaction garbage collection tasks.
	 */
	public static final String GC = "orange:transaction:gc";
	
	/**
	 * Key prefix for transaction callback processing tasks.
	 */
	public static final String CALLBACK = "orange:transaction:callback";
	
	/**
	 * Lock key used to ensure only one daemon runner is active at a time.
	 */
	private static final String TRANSACTION_DAEMON_RUNNER_LOCK = "orange:transaction:daemon:runner:lock";

	/**
	 * Executor for managing distributed locks across multiple Redis instances.
	 */
	private OrangeMultipleLocksExecutor locksExecutor;
	
	/**
	 * Configuration properties for the transaction system.
	 */
	private OrangeRedisTransactionProperties properties;
	
	/**
	 * Constructs a new transaction daemon runner.
	 * 
	 * <p>Initializes the daemon runner with the necessary dependencies for managing
	 * distributed transaction tasks. This includes setting up the locks executor
	 * and registering the daemon runner's lock key in the registry to prevent
	 * duplicate key usage.
	 *
	 * @param operations Redis hash operations for data manipulation
	 * @param scriptOperations Redis script operations for executing Lua scripts
	 * @param properties Configuration properties for the transaction system
	 * @param renewTimerWheel Timer wheel for lock renewal operations
	 * @param expirationTimeAutoInitializer Initializer for setting expiration times
	 * @param listeners Collection of listeners for lock acquisition events
	 * @param logger Logger for recording transaction-related events
	 */
	OrangeRedisTransactionDaemonRunner(
		OrangeRedisHashOperations operations,
		OrangeRedisScriptOperations scriptOperations, 
		OrangeRedisTransactionProperties properties,
		OrangeRenewTimerWheel renewTimerWheel,
		OrangeExpirationTimeAutoInitializer expirationTimeAutoInitializer,
		Collection<OrangeRedisMultipleSetIfAbsentListener> listeners,
		OrangeRedisLogger logger
	) {
		
		this.locksExecutor = new OrangeMultipleLocksExecutor(
			scriptOperations,
			operations,
			new OrangeRedisMultipleLocksExecutorIdGenerator(),
			listeners,
			renewTimerWheel,
			expirationTimeAutoInitializer,
			logger
		);
		this.properties = properties;
		
		// Check for duplicate keys by registering them in the registry. 
		OrangeRedisKeyRegistry.register(TRANSACTION_DAEMON_RUNNER_LOCK, null, OrangeRedisTransactionDaemonRunner.class);
	}
	
	/**
	 * Runs a transaction daemon task of the specified type.
	 * 
	 * <p>This method attempts to acquire a distributed lock before executing the task,
	 * ensuring that only one instance of the task runs at a time across the distributed
	 * system. The lock includes metadata about the service name and task type.
	 *
	 * @param type The type of task to run (e.g., "gc" for garbage collection or "callback" for callback processing)
	 * @param ctx The Redis context for executing commands
	 * @param lockExpirationMillis The expiration time for the lock in milliseconds
	 */
	public void run(String type, OrangeRedisIterableContext ctx, long lockExpirationMillis) {
		Key key = new Key(TRANSACTION_DAEMON_RUNNER_LOCK, TRANSACTION_DAEMON_RUNNER_LOCK, lockExpirationMillis, TimeUnit.MILLISECONDS);
		locksExecutor.doLock(ctx, key, true, 3, RedisValueTypeEnum.JSON, new Object[] {this.properties.getServiceName(), type});
	}
	
	/**
	 * Represents metadata for a transaction lock.
	 * 
	 * <p>This class encapsulates information about a transaction lock, including
	 * the type of lock, the service that owns the lock, and any additional
	 * metadata associated with the lock. It is used to store structured information
	 * within the Redis lock value.
	 */
	public static class LockKey {
		
		/**
		 * The type of lock (e.g., "gc" or "callback").
		 */
		private String lockType;
		
		/**
		 * The name of the service that owns the lock.
		 */
		private String serviceName;
		
		/**
		 * Additional metadata associated with the lock.
		 */
		private Object lockMetaData;

		/**
		 * Gets the lock type.
		 *
		 * @return The lock type
		 */
		public String getLockType() {
			return lockType;
		}

		/**
		 * Sets the lock type.
		 *
		 * @param lockType The lock type to set
		 */
		public void setLockType(String lockType) {
			this.lockType = lockType;
		}

		/**
		 * Gets the service name.
		 *
		 * @return The service name
		 */
		public String getServiceName() {
			return serviceName;
		}

		/**
		 * Sets the service name.
		 *
		 * @param serviceName The service name to set
		 */
		public void setServiceName(String serviceName) {
			this.serviceName = serviceName;
		}

		/**
		 * Gets the lock metadata.
		 *
		 * @return The lock metadata
		 */
		public Object getLockMetaData() {
			return lockMetaData;
		}

		/**
		 * Sets the lock metadata.
		 *
		 * @param lockMetaData The lock metadata to set
		 */
		public void setLockMetaData(Object lockMetaData) {
			this.lockMetaData = lockMetaData;
		}
	}
}