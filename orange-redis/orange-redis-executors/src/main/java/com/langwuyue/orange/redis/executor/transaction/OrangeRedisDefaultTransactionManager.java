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

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.langwuyue.orange.redis.OrangeRedisException;
import com.langwuyue.orange.redis.executor.multiplelocks.OrangeExpirationTimeAutoInitializer;
import com.langwuyue.orange.redis.listener.OrangeRedisMultipleSetIfAbsentListener;
import com.langwuyue.orange.redis.logger.OrangeRedisLogger;
import com.langwuyue.orange.redis.operations.OrangeRedisHashOperations;
import com.langwuyue.orange.redis.operations.OrangeRedisScriptOperations;
import com.langwuyue.orange.redis.operations.OrangeRedisSetOperations;
import com.langwuyue.orange.redis.operations.OrangeRedisZSetOperations;
import com.langwuyue.orange.redis.timer.OrangeRenewTimerWheel;

/**
 * Default implementation of the Redis transaction manager.
 * 
 * <p>This class manages Redis transactions in conjunction with database transactions,
 * providing the following key features:
 * <ul>
 *   <li>Transaction lifecycle management (commit, rollback)</li>
 *   <li>Automatic garbage collection for expired transactions</li>
 *   <li>Timeout handling with callback mechanisms</li>
 *   <li>Thread-safe transaction information storage</li>
 *   <li>Support for both automatic and manual commit modes</li>
 *   <li>Transaction version control with support for reading uncommitted data</li>
 * </ul>
 * 
 * <p>The transaction manager uses a combination of components to ensure reliable
 * transaction processing:
 * <ul>
 *   <li>Transaction Timeout Callback Executor: Handles transaction timeout events</li>
 *   <li>Transaction Garbage Collector: Cleans up expired transaction data</li>
 *   <li>Transaction Daemon Runner: Maintains transaction lifecycle</li>
 * </ul>
 * 
 * <p>This implementation is designed to work with Spring's transaction management
 * system and provides integration points for database transaction synchronization.
 *
 * @author Liang.Zhong
 * @since 1.0.0
 */
public class OrangeRedisDefaultTransactionManager implements OrangeRedisTransactionManager {
	
	/**
	 * Thread-local storage for transaction commit information.
	 * Ensures transaction isolation at thread level by maintaining separate transaction
	 * information for each thread.
	 */
	private static final ThreadLocal<List<OrangeTransactionCommitInfo>> TRANSACTION_INFOS = new ThreadLocal<>();
	
	/**
	 * Transaction garbage collector responsible for cleaning up expired or invalid transaction data.
	 * Periodically scans and removes transactions that have timed out but were not properly
	 * committed or rolled back.
	 */
	private OrangeRedisTransactionGarbageCollector transactionGC;
	
	/**
	 * Transaction timeout callback executor responsible for handling transaction timeout scenarios.
	 * When a transaction times out, it executes registered callback functions to allow
	 * the application to take appropriate recovery measures.
	 */
	private OrangeRedisTransactionTimeoutCallbackExecutor transactionTimeoutCallbackExecutor;
	
	/**
	 * Processor name mapping that maps transaction commit processors to their names.
	 * Used to identify different processors in logs and error messages.
	 */
	private Map<OrangeTransactionCommitProcessor,String> proccessorNameMap;
	
	/**
	 * Transaction functionality enable flag.
	 * When set to false, all transaction-related operations will be disabled.
	 */
	private boolean enabled = false;
	
	/**
	 * Logger for recording important events and errors during transaction management.
	 */
	private OrangeRedisLogger logger;
	
	/**
	 * Constructs a new Redis transaction manager instance.
	 * 
	 * <p>This constructor receives all necessary components through dependency injection
	 * and initializes various parts of the transaction management system:
	 * <ul>
	 *   <li>Transaction timeout callback executor: Handles transaction timeout scenarios</li>
	 *   <li>Transaction garbage collector: Cleans up expired transaction data</li>
	 *   <li>Transaction daemon process: Maintains transaction lifecycle</li>
	 * </ul>
	 * 
	 * <p>The construction process includes:
	 * <ol>
	 *   <li>Checking if transaction functionality is enabled</li>
	 *   <li>Initializing processor name mapping</li>
	 *   <li>Creating timeout callback executor</li>
	 *   <li>Creating garbage collector</li>
	 *   <li>Setting up event listeners</li>
	 *   <li>Starting transaction daemon process</li>
	 * </ol>
	 *
	 * @param operations Redis hash operations interface for transaction data access
	 * @param setOperations Redis set operations interface for transaction set management
	 * @param zsetOperations Redis sorted set operations interface for transaction timeout management
	 * @param properties Transaction configuration properties, including timeout settings
	 * @param scriptOperations Redis script operations interface for atomic operations
	 * @param renewTimerWheel Timer wheel for transaction timeout detection
	 * @param expirationTimeAutoInitializer Expiration time auto initializer
	 * @param callbacks Transaction timeout callback listener mapping
	 * @param logger Logger for recording events
	 * @param namedProcessorExecutorMap Named processor executor mapping for auto-commit
	 */
	public OrangeRedisDefaultTransactionManager(
		OrangeRedisHashOperations operations,
		OrangeRedisSetOperations setOperations,
		OrangeRedisZSetOperations zsetOperations,
		OrangeRedisTransactionProperties properties,
		OrangeRedisScriptOperations scriptOperations, 
		OrangeRenewTimerWheel renewTimerWheel,
		OrangeExpirationTimeAutoInitializer expirationTimeAutoInitializer,
		Map<String,OrangeRedisTransactionTimeoutListener> callbacks,
		OrangeRedisLogger logger,
		Map<String,OrangeTransactionCommitProcessor> namedProcessorExecutorMap
	) {
		this.enabled = properties.isEnabled();
		this.logger = logger;
		if(!this.enabled) {
			return;
		}
		// Commit executor mapping for auto commit
		this.proccessorNameMap = new HashMap<>();
		namedProcessorExecutorMap.forEach((k,v) -> this.proccessorNameMap.put(v, k));
				
		this.transactionTimeoutCallbackExecutor = new OrangeRedisTransactionTimeoutCallbackExecutor(
			zsetOperations,
			operations,
			properties,
			callbacks,
			namedProcessorExecutorMap,
			logger
		);
		this.transactionGC = new OrangeRedisTransactionGarbageCollector(
			operations,
			setOperations,
			properties,
			logger,
			this.transactionTimeoutCallbackExecutor
		);
		Collection<OrangeRedisMultipleSetIfAbsentListener> listeners = new ArrayList<>();
		listeners.add(this.transactionGC);
		listeners.add(this.transactionTimeoutCallbackExecutor);
		OrangeRedisTransactionDaemonRunner runner = new OrangeRedisTransactionDaemonRunner(
			operations,
			scriptOperations,
			properties,
			renewTimerWheel,
			expirationTimeAutoInitializer,
			listeners,
			logger
		);
		this.transactionGC.setRunner(runner);
		this.transactionTimeoutCallbackExecutor.setRunner(runner);
	}

	/**
	 * Commits all Redis transactions in the current thread.
	 * 
	 * <p>This method is executed after a database transaction is committed and commits
	 * all pending Redis transactions in the current thread to the Redis server.
	 * The commit process iterates through all transaction information saved in the current thread
	 * and performs the following operations for each transaction:
	 * <ol>
	 *   <li>Gets the transaction processor and transaction key</li>
	 *   <li>Calls the processor's commit executor to perform the actual commit operation</li>
	 *   <li>Removes the transaction key from the timeout callback executor to prevent timeout callbacks from triggering</li>
	 *   <li>Records the commit result, logging a warning if the commit fails</li>
	 * </ol>
	 * 
	 * <p>Even if a transaction has already timed out and the timeout callback has returned a failure,
	 * this code will still attempt to execute the commit operation.
	 * After completion, it clears the transaction information from the current thread, releasing related resources.
	 * 
	 * <p>If there is no transaction information in the current thread, this method will return directly
	 * without performing any operations.
	 *
	 * @throws Exception If any error occurs during the commit process
	 */
	@Override
	public void commit() throws Exception {
		List<OrangeTransactionCommitInfo> infos = TRANSACTION_INFOS.get();
		if(infos == null) {
			return;
		}
		for(OrangeTransactionCommitInfo info : infos) {
			OrangeTransactionCommitProcessor processor = info.getProcessor();
			OrangeRedisTransactionKey transactionKey = info.getTransactionKey();
			// Note: This code runs successfully even if the timeout callback returns a failure first.
			boolean success = processor.getCommitExecutor().commit(transactionKey.getKey(), transactionKey.getVersion());
			if(!success) {
				this.logger.warn("Failed to commit Redis transaction {},maybe higher version committed already.", transactionKey);
			}
			this.transactionTimeoutCallbackExecutor.remove(info.getTransactionKey());
		}
		TRANSACTION_INFOS.remove();
	}
	
	/**
	 * Indicates that the transaction has already been committed manually.
	 * 
	 * <p>This method is called when a transaction has been committed through other means
	 * (not through this transaction manager's commit method). It ensures proper cleanup
	 * of all transaction resources by calling the internal remove method.
	 * 
	 * <p>This is useful in scenarios where the application manages transaction commits
	 * directly and needs to inform the transaction manager to clean up related resources.
	 *
	 * @throws Exception If any error occurs during the cleanup process
	 */
	public void alreadyCommittedManually() throws Exception {
		remove();
	}
	
	/**
	 * Rolls back all Redis transactions in the current thread.
	 * 
	 * <p>This method is executed after a database transaction is rolled back and is responsible
	 * for cleaning up all pending Redis transactions in the current thread.
	 * The rollback operation calls the internal remove method to clean up all transaction resources.
	 *
	 * @throws Exception If any error occurs during the rollback process
	 */
	@Override
	public void rollback() throws Exception {
		remove();
	}
	
	/**
	 * Cleans up all transaction resources in the current thread.
	 * 
	 * <p>This is an internal implementation method used to clean up transaction-related resources:
	 * <ol>
	 *   <li>Gets the transaction information list from the current thread</li>
	 *   <li>If the list is empty, returns immediately</li>
	 *   <li>Iterates through all transaction information, removing each transaction key from the timeout callback executor</li>
	 *   <li>Clears the transaction information stored in ThreadLocal</li>
	 * </ol>
	 * 
	 * <p>This method is called by rollback() and alreadyCommittedManually() methods
	 * to ensure proper cleanup of all resources when a transaction ends.
	 *
	 * @throws Exception If any error occurs during the cleanup process
	 */
	private void remove() throws Exception {
		List<OrangeTransactionCommitInfo> infos = TRANSACTION_INFOS.get();
		if(infos == null) {
			return;
		}
		for(OrangeTransactionCommitInfo info : infos) {
			this.transactionTimeoutCallbackExecutor.remove(info.getTransactionKey());
		}
		TRANSACTION_INFOS.remove();	
	}
	
	/**
	 * Saves transaction information for subsequent automatic commit operations.
	 * 
	 * <p>This method is called at the beginning of a transaction and is responsible for
	 * initializing and saving all transaction-related information. It performs the following operations:
	 * <ol>
	 *   <li>Checks if transaction functionality is enabled, throws an exception if not</li>
	 *   <li>Registers the transaction key with the garbage collector for future cleanup</li>
	 *   <li>Sets the transaction start time and type</li>
	 *   <li>Registers the transaction key with the timeout callback executor for handling timeouts</li>
	 *   <li>Saves transaction information in the current thread's ThreadLocal storage</li>
	 * </ol>
	 * 
	 * <p>The transaction information includes:
	 * <ul>
	 *   <li>Transaction Processor: Responsible for executing the actual commit operation</li>
	 *   <li>Transaction Key: Contains version number, original key, transaction start time, etc.</li>
	 * </ul>
	 * 
	 * <p>This method is typically called before business operations begin to initialize
	 * the transaction context.
	 * 
	 * @param processor The transaction commit processor containing commit executor and snapshot retrieval executor
	 * @param transactionKey The transaction key object containing transaction metadata
	 * @throws Exception If transaction functionality is disabled or if an error occurs during saving
	 * @see #commit() The commit method for executing the actual transaction commit
	 */
	public void saveTransactionInfo(
		OrangeTransactionCommitProcessor processor,
		OrangeRedisTransactionKey transactionKey
	) throws Exception {
		if(!this.enabled) {
			throw new OrangeRedisException("Invalid operation, transactions has been disabled. To enable, set `orange.redis.transaction.enabled=true` in application.yml");
		}
		this.transactionGC.register(transactionKey.getKey());
		transactionKey.setTxBeginTime(System.currentTimeMillis());
		transactionKey.setTxType(this.proccessorNameMap.get(processor));
		this.transactionTimeoutCallbackExecutor.register(transactionKey);
		List<OrangeTransactionCommitInfo> infos = TRANSACTION_INFOS.get();
		if(infos == null) {
			infos = new ArrayList<>();
			TRANSACTION_INFOS.set(infos);
		}
		infos.add(new OrangeTransactionCommitInfo(processor,transactionKey));
	}
	
	/**
	 * Gets the version number for reading uncommitted values within the current transaction.
	 * 
	 * <p>This method allows reading of uncommitted data by providing the appropriate version
	 * number for the specified key. This is useful in scenarios where the application needs
	 * to access data that has been modified but not yet committed in the current transaction.
	 * 
	 * <p>The version number is used to identify the specific version of data that should be
	 * retrieved, allowing for consistent reads within a transaction even before commit.
	 * 
	 * @param key The key for which to get the uncommitted version number
	 * @return The version number to use for reading uncommitted values, or null if not in a transaction
	 */
	public Long getTransactionVerion(String key) {
		List<OrangeTransactionCommitInfo> infos = TRANSACTION_INFOS.get();
		if(infos == null) {
			return null;
		}
		
		for(OrangeTransactionCommitInfo info : infos) {
			if(key.equals(info.getTransactionKey().getKey())) {
				// Each thread must have a unique transaction version.
				// Return the first transaction version if multiple versions exist for the same key in the thread.
				return info.getTransactionKey().getVersion();
			}
		}
		return null;
	}
	
	/**
	 * Destroys the transaction manager and its associated resources.
	 * 
	 * <p>This method performs cleanup operations when the transaction manager is being shut down.
	 * It ensures proper cleanup of resources by:
	 * <ul>
	 *   <li>Checking if transaction functionality is enabled</li>
	 *   <li>Destroying the transaction garbage collector if it exists</li>
	 *   <li>Destroying the transaction timeout callback executor if it exists</li>
	 * </ul>
	 * 
	 * <p>This method should be called when the application is shutting down or
	 * when the transaction manager is no longer needed.
	 */
	public void destory() {
		if(!this.enabled) {
			return;
		}
		if(this.transactionGC != null) {
			this.transactionGC.destory();
		}
		if(this.transactionTimeoutCallbackExecutor != null) {
			this.transactionTimeoutCallbackExecutor.destory();
		}
	}
	
	/**
	 * Retrieves a set of dead transactions.
	 * 
	 * <p>A dead transaction is one that has timed out or failed to complete properly.
	 * This method returns a set of transaction identifiers that are considered dead,
	 * which can be useful for monitoring and debugging purposes.
	 * 
	 * <p>If transaction functionality is disabled, this method returns an empty set.
	 *
	 * @return A Set of Object identifiers representing dead transactions
	 * @throws Exception If an error occurs while retrieving dead transactions
	 */
	public Set<Object> getDeadTransaction() throws Exception {
		if(!this.enabled) {
			return new HashSet<>();
		}
		return this.transactionTimeoutCallbackExecutor.getDeadTransaction();
	}
	
	/**
	 * Represents transaction commit information for a specific transaction.
	 * 
	 * <p>This class encapsulates all the necessary information needed to commit a transaction,
	 * including the processor responsible for executing the commit operation and the
	 * transaction key that uniquely identifies the transaction.
	 * 
	 * <p>Instances of this class are stored in thread-local storage to maintain
	 * transaction isolation at the thread level.
	 */
	public static class OrangeTransactionCommitInfo {
		
		/**
		 * The processor responsible for executing the commit operation.
		 */
		private OrangeTransactionCommitProcessor processor;
		
		/**
		 * The transaction key that uniquely identifies this transaction.
		 */
		private OrangeRedisTransactionKey transactionKey;
		
		/**
		 * Constructs a new transaction commit information instance.
		 *
		 * @param processor The processor responsible for executing the commit operation
		 * @param transactionKey The transaction key that uniquely identifies this transaction
		 */
		public OrangeTransactionCommitInfo(
			OrangeTransactionCommitProcessor processor,
			OrangeRedisTransactionKey transactionKey
		) {
			super();
			this.processor = processor;
			this.transactionKey = transactionKey;
		}

		/**
		 * Gets the transaction commit processor.
		 *
		 * @return The processor responsible for executing the commit operation
		 */
		public OrangeTransactionCommitProcessor getProcessor() {
			return processor;
		}

		/**
		 * Sets the transaction commit processor.
		 *
		 * @param processor The processor responsible for executing the commit operation
		 */
		public void setProcessor(OrangeTransactionCommitProcessor processor) {
			this.processor = processor;
		}

		/**
		 * Gets the transaction key.
		 *
		 * @return The transaction key that uniquely identifies this transaction
		 */
		public OrangeRedisTransactionKey getTransactionKey() {
			return transactionKey;
		}

		/**
		 * Sets the transaction key.
		 *
		 * @param transactionKey The transaction key that uniquely identifies this transaction
		 */
		public void setTransactionKey(OrangeRedisTransactionKey transactionKey) {
			this.transactionKey = transactionKey;
		}
	}
	
	/**
	 * Processor responsible for executing transaction commit operations.
	 * 
	 * <p>This class encapsulates the executors needed to commit a transaction and retrieve
	 * transaction snapshots. It also maintains the transaction type information.
	 * 
	 * <p>The processor is responsible for coordinating the commit process, ensuring that
	 * all changes made within a transaction are properly applied to the underlying data store.
	 */
	public static class OrangeTransactionCommitProcessor {
		
		/**
		 * The executor responsible for committing transaction changes.
		 */
		private OrangeRedisTransactionCommitExecutor commitExecutor;
		
		/**
		 * The executor responsible for retrieving transaction snapshots.
		 */
		private OrangeRedisTransactionSnapshotGetExecutor transactionSnapshotGetExecutor;
		
		/**
		 * The type of transaction being processed.
		 */
		private String txType;
		
		/**
		 * Constructs a new transaction commit processor.
		 *
		 * @param commitExecutor The executor responsible for committing transaction changes
		 * @param transactionSnapshotGetExecutor The executor responsible for retrieving transaction snapshots
		 * @param txType The type of transaction being processed
		 */
		public OrangeTransactionCommitProcessor(
			OrangeRedisTransactionCommitExecutor commitExecutor,
			OrangeRedisTransactionSnapshotGetExecutor transactionSnapshotGetExecutor,
			String txType
		) {
			super();
			this.commitExecutor = commitExecutor;
			this.transactionSnapshotGetExecutor = transactionSnapshotGetExecutor;
			this.txType = txType;
		}

		/**
		 * Gets the commit executor.
		 *
		 * @return The executor responsible for committing transaction changes
		 */
		public OrangeRedisTransactionCommitExecutor getCommitExecutor() {
			return commitExecutor;
		}

		/**
		 * Sets the commit executor.
		 *
		 * @param commitExecutor The executor responsible for committing transaction changes
		 */
		public void setCommitExecutor(OrangeRedisTransactionCommitExecutor commitExecutor) {
			this.commitExecutor = commitExecutor;
		}

		/**
		 * Gets the transaction snapshot executor.
		 *
		 * @return The executor responsible for retrieving transaction snapshots
		 */
		public OrangeRedisTransactionSnapshotGetExecutor getTransactionSnapshotGetExecutor() {
			return transactionSnapshotGetExecutor;
		}

		/**
		 * Sets the transaction snapshot executor.
		 *
		 * @param transactionSnapshotGetExecutor The executor responsible for retrieving transaction snapshots
		 */
		public void setTransactionSnapshotGetExecutor(
				OrangeRedisTransactionSnapshotGetExecutor transactionSnapshotGetExecutor) {
			this.transactionSnapshotGetExecutor = transactionSnapshotGetExecutor;
		}

		/**
		 * Gets the transaction type.
		 *
		 * @return The type of transaction being processed
		 */
		public String getTxType() {
			return txType;
		}

		/**
		 * Sets the transaction type.
		 *
		 * @param txType The type of transaction being processed
		 */
		public void setTxType(String txType) {
			this.txType = txType;
		}
	}
	
	
	/**
	 * Represents a transaction key in the Orange Redis transaction system.
	 * 
	 * <p>This class encapsulates all the necessary information to uniquely identify and track
	 * a transaction in the Redis system, including version information, original and transformed keys,
	 * transaction start time, and transaction type.
	 * 
	 * <p>The transaction key is essential for maintaining transaction isolation and managing
	 * concurrent access to Redis data.
	 */
	public static class OrangeRedisTransactionKey {
		
		/**
		 * The version number of the transaction, used for optimistic locking.
		 */
		private long version;
		
		/**
		 * The original key before any transaction-related transformations.
		 */
		private String originKey;
		
		/**
		 * The transformed key used within the transaction system.
		 */
		private String key;
		
		/**
		 * The timestamp when the transaction began.
		 */
		private long txBeginTime;
		
		/**
		 * The type of transaction being executed.
		 */
		private String txType;
		
		/**
		 * Gets the transaction version.
		 *
		 * @return The version number of the transaction
		 */
		public long getVersion() {
			return version;
		}

		/**
		 * Sets the transaction version.
		 *
		 * @param version The version number of the transaction
		 */
		public void setVersion(long version) {
			this.version = version;
		}

		/**
		 * Gets the original key before any transaction-related transformations.
		 *
		 * @return The original key
		 */
		public String getOriginKey() {
			return originKey;
		}

		/**
		 * Sets the original key.
		 *
		 * @param originKey The original key before any transaction-related transformations
		 */
		public void setOriginKey(String originKey) {
			this.originKey = originKey;
		}

		/**
		 * Gets the transformed key used within the transaction system.
		 *
		 * @return The transformed key
		 */
		public String getKey() {
			return key;
		}

		/**
		 * Sets the transformed key.
		 *
		 * @param key The transformed key used within the transaction system
		 */
		public void setKey(String key) {
			this.key = key;
		}

		/**
		 * Gets the timestamp when the transaction began.
		 *
		 * @return The transaction begin timestamp
		 */
		public long getTxBeginTime() {
			return txBeginTime;
		}

		/**
		 * Sets the timestamp when the transaction began.
		 *
		 * @param txBeginTime The transaction begin timestamp
		 */
		public void setTxBeginTime(long txBeginTime) {
			this.txBeginTime = txBeginTime;
		}

		/**
		 * Gets the type of transaction being executed.
		 *
		 * @return The transaction type
		 */
		public String getTxType() {
			return txType;
		}

		/**
		 * Sets the type of transaction being executed.
		 *
		 * @param txType The transaction type
		 */
		public void setTxType(String txType) {
			this.txType = txType;
		}

		@Override
		public String toString() {
			return "OrangeRedisTransactionKey [version=" + version + ", originKey=" + originKey + ", key=" + key
					+ ", txBeginTime=" + txBeginTime + ", txType=" + txType + "]";
		}
	}
}