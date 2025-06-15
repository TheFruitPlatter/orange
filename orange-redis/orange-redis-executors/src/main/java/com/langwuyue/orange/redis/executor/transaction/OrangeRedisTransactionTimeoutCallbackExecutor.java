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

import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
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
import com.langwuyue.orange.redis.executor.transaction.OrangeRedisDefaultTransactionManager.OrangeTransactionCommitProcessor;
import com.langwuyue.orange.redis.executor.transaction.OrangeRedisDefaultTransactionManager.OrangeRedisTransactionKey;
import com.langwuyue.orange.redis.listener.multiplelocks.OrangeMultipleLocksEvent;
import com.langwuyue.orange.redis.listener.multiplelocks.OrangeMultipleLocksRemoveFailedEvent;
import com.langwuyue.orange.redis.listener.multiplelocks.OrangeRedisMultipleLocksListener;
import com.langwuyue.orange.redis.logger.OrangeRedisLogger;
import com.langwuyue.orange.redis.operations.OrangeRedisHashOperations;
import com.langwuyue.orange.redis.operations.OrangeRedisZSetOperations;
import com.langwuyue.orange.redis.operations.OrangeRedisZSetOperations.ScoreRange;
import com.langwuyue.orange.redis.operations.OrangeRedisZSetOperations.ZSetEntry;
import com.langwuyue.orange.redis.registry.OrangeRedisKeyRegistry;
import com.langwuyue.orange.redis.utils.OrangeReflectionUtils;

/**
 * Executor responsible for handling Redis transaction timeout callbacks and cleaning up dead transactions.
 * 
 * <p>This class implements a scheduled executor that periodically checks for transactions
 * that have exceeded their timeout threshold. When a transaction times out, this executor
 * invokes the appropriate callback handler to determine whether the transaction should be
 * committed, retried, or rolled back.
 * 
 * <p>Key responsibilities include:
 * <ul>
 *   <li>Monitoring uncommitted transactions and detecting timeouts</li>
 *   <li>Executing registered timeout callback handlers when transactions exceed their timeout threshold</li>
 *   <li>Managing transaction state based on callback responses (SUCCESS, UNKNOWN, FAILED)</li>
 *   <li>Committing successful transactions after callback confirmation</li>
 *   <li>Cleaning up dead transactions that have exceeded their retention period</li>
 *   <li>Tracking callback metrics including retry attempts and warning notifications</li>
 * </ul>
 * 
 * 
 * <p>The executor uses a Redis sorted set to track uncommitted transactions, with scores
 * based on transaction start times. This allows efficient retrieval of transactions
 * that have exceeded their timeout threshold.
 * 
 * @author Liang.Zhong
 * @since 1.0.0
 */
public class OrangeRedisTransactionTimeoutCallbackExecutor implements Runnable, OrangeRedisMultipleLocksListener,OrangeRedisIterableContext {
	
	/**
	 * Redis key prefix for storing uncommitted transaction keys in a sorted set.
	 * Each service will have its own registry by appending the service name.
	 */
	private static final String UNCOMMITTED_TRANSACTION_KEYS_REGISTRY = "orange:transaction:uncommit:keys:registry:";
	
	/**
	 * Redis key prefix for storing transaction callback metrics in a hash.
	 * Each service will have its own metric storage by appending the service name.
	 */
	private static final String CALLBACK_METRIC_DATA_KEY = "orange:transaction:callback:metric:";
	
	/**
	 * Redis sorted set operations for managing transaction keys.
	 * Used for adding, removing, and querying transaction keys based on their scores.
	 */
	private OrangeRedisZSetOperations operations;
	
	/**
	 * Redis hash operations for managing transaction callback metrics.
	 * Used for storing and retrieving metrics associated with transaction keys.
	 */
	private OrangeRedisHashOperations hashOperations;
	
	/**
	 * Scheduled executor service for periodic transaction timeout checking.
	 * Uses a single thread to periodically check for timed-out transactions.
	 */
	private ScheduledExecutorService executorService;
	
	/**
	 * Configuration properties for transaction timeout handling.
	 * Includes timeout thresholds, callback periods, and retry limits.
	 */
	private OrangeRedisTransactionProperties properties;
	
	/**
	 * Service-specific Redis key for the uncommitted transaction registry.
	 * Formed by concatenating UNCOMMITTED_TRANSACTION_KEYS_REGISTRY with the service name.
	 */
	private String registry;
	
	/**
	 * Service-specific Redis key for transaction callback metrics.
	 * Formed by concatenating CALLBACK_METRIC_DATA_KEY with the service name.
	 */
	private String callbackMetric;
	
	/**
	 * Transaction daemon runner for executing transaction-related operations.
	 * Handles locking and coordination of transaction processing across multiple nodes.
	 */
	private OrangeRedisTransactionDaemonRunner runner;
	
	/**
	 * Map of registered transaction timeout listeners indexed by Redis key.
	 * Each listener is responsible for handling timeout callbacks for a specific transaction type.
	 */
	private Map<String,OrangeRedisTransactionTimeoutListener> callbacks;
	
	/**
	 * Map associating each timeout listener with the type of value it expects.
	 * Used to deserialize transaction values correctly when invoking callbacks.
	 */
	private Map<OrangeRedisTransactionTimeoutListener,Type> valueTypeMap;
	
	/**
	 * Logger for recording transaction timeout events and errors.
	 * Provides different log levels for debugging, information, warnings, and errors.
	 */
	private OrangeRedisLogger logger;
	
	/**
	 * Map of transaction commit processors indexed by transaction type.
	 * Each processor is responsible for committing a specific type of transaction.
	 */
	private Map<String,OrangeTransactionCommitProcessor> namedProcessorExecutorMap;
	
	/**
	 * Constructs a new transaction timeout callback executor with the specified dependencies.
	 * 
	 * <p>This constructor initializes the executor with the required Redis operations,
	 * transaction properties, callback handlers, commit processors, and logging components.
	 * It sets up the service-specific Redis keys for transaction registry and metrics
	 * based on the configured service name.
	 * 
	 * <p>The executor uses a single-threaded scheduled executor service to periodically
	 * check for timed-out transactions. This ensures that timeout checks are performed
	 * sequentially and prevents concurrent processing of the same transaction.
	 * 
	 * @param operations Redis sorted set operations for managing transaction keys
	 * @param hashOperations Redis hash operations for managing transaction metrics
	 * @param properties Configuration properties for transaction timeout handling
	 * @param callbacks Map of registered transaction timeout listeners indexed by transaction key prefix
	 * @param namedProcessorExecutorMap Map of transaction commit processors indexed by transaction type
	 * @param logger Logger for recording transaction timeout events and errors
	 */
	public OrangeRedisTransactionTimeoutCallbackExecutor(
		OrangeRedisZSetOperations operations,
		OrangeRedisHashOperations hashOperations,
		OrangeRedisTransactionProperties properties,
		Map<String,OrangeRedisTransactionTimeoutListener> callbacks,
		Map<String,OrangeTransactionCommitProcessor> namedProcessorExecutorMap,
		OrangeRedisLogger logger
	) {
		super();
		this.callbacks = callbacks;
		this.operations = operations;
		this.hashOperations = hashOperations;
		this.properties = properties;
		this.logger = logger;
		this.namedProcessorExecutorMap = namedProcessorExecutorMap;
		
		// A single schedule thread.
		this.executorService = Executors.newScheduledThreadPool(1, new OrangeTransactionTimeoutCallbackExecutorThreadFactory());
		
		// Different service has different key
		this.registry = UNCOMMITTED_TRANSACTION_KEYS_REGISTRY + properties.getServiceName();
		this.callbackMetric = CALLBACK_METRIC_DATA_KEY + properties.getServiceName();
		
		// Get value type from the transaction timeout callback class's argument type. 
		this.valueTypeMap = new HashMap<>();
		callbacks.forEach((k,v) -> {
			Type[] argsTypes = OrangeReflectionUtils.getSuperIntrefaceArgumentTypes(v,OrangeRedisTransactionTimeoutListener.class);
			if(argsTypes != null && argsTypes.length != 0) {
				this.valueTypeMap.put(v, argsTypes[0]);
			}
		});
		
		// Check for duplicate keys by registering them in the registry.
		OrangeRedisKeyRegistry.register(this.registry, null, OrangeRedisDefaultTransactionManager.class);
		OrangeRedisKeyRegistry.register(this.callbackMetric, null, OrangeRedisDefaultTransactionManager.class);
		
		// Start the thread pool
		this.executorService.scheduleAtFixedRate(
			this, 
			properties.getTimeoutCallbackThreadInitialDelay().toMillis(), 
			properties.getTimeoutCallbackThreadPeriod().toMillis(), 
			TimeUnit.MILLISECONDS
		);
	}

	@Override
	public void run() {
		try {
			if(this.runner == null) {
				logger.debug("Transaction timeout callback executor is not initialized.");
				return;
			}
			logger.debug("Transaction timeout callback is executing now.");
			this.runner.run(OrangeRedisTransactionDaemonRunner.CALLBACK,this,this.properties.getTimeoutCallbackPeriod().toMillis());
			logger.debug("Transaction timeout callback done.");
		} catch (Exception e) {
			logger.error("Transaction timeout callback execute error", e);
		}
	}
	
	public void destory() {
		if(this.executorService != null) {
			this.logger.info("Transaction callback thread pool is shutting down now");
			this.executorService.shutdown();
		}
	}
	
	/**
	 * Registers a transaction key for timeout monitoring and callback processing.
	 * 
	 * <p>This method adds the transaction key to a Redis sorted set that serves as the
	 * transaction timeout callback queue. The queue is sorted by transaction begin time,
	 * allowing efficient retrieval of transactions that have exceeded their timeout threshold.
	 * 
	 * <p>Additionally, this method initializes callback metrics for the transaction, including:
	 * <ul>
	 *   <li>Total callback attempts (initialized to 0)</li>
	 *   <li>Warning notification status (initialized to 0, indicating no warning sent)</li>
	 * </ul>
	 * 
	 * 
	 * <p>These metrics are used to track retry attempts and ensure that warnings are
	 * only sent once per transaction.
	 * 
	 * @param transactionKey The transaction key to register for timeout monitoring
	 * @throws Exception If an error occurs during registration
	 */
	public void register(OrangeRedisTransactionKey transactionKey) throws Exception {
		// Add a transaction key into sorted set, and order by score.
		ZSetEntry entry = new ZSetEntry(transactionKey,computeScore(transactionKey.getTxBeginTime()));
		Set<ZSetEntry> entries = new HashSet<>();
		entries.add(entry);
		this.operations.add(this.registry, entries, RedisValueTypeEnum.JSON);
		
		// Save metric data, include callback times and whether the notification was sent when the callback exceeded the maximum retry attempts.
		OrangeTransactionTimeoutCallbackMetric metric = new OrangeTransactionTimeoutCallbackMetric();
		metric.setCallbackedTimes(0);
		metric.setIsWarn(0);
		this.hashOperations.putMember(this.callbackMetric, transactionKey, metric, RedisValueTypeEnum.JSON, RedisValueTypeEnum.JSON);
	}
	
	public void remove(OrangeRedisTransactionKey transactionKey) throws Exception {
		// Remove metric first, then all the data will be removed permanently, even after the service restart.
		this.hashOperations.removeMembers(this.callbackMetric, RedisValueTypeEnum.JSON, transactionKey);
		this.operations.remove(this.registry, RedisValueTypeEnum.JSON, transactionKey);
	}
	
	/**
	 * Computes a score value for transaction sorting in Redis sorted sets.
	 * 
	 * <p>This method converts a timestamp (in milliseconds) to a double score value
	 * that can be used for sorting transactions in Redis sorted sets.
	 * 
	 * <p>The resulting score format:
	 * <pre>
	 * [seconds].[milliseconds]
	 * Example: 1234567890.123 (representing 2009-02-13 23:31:30.123)
	 * </pre>
	 * 
	 * 
	 * <p>Note: Future implementations may extend precision to nanoseconds if needed.
	 * 
	 * @param millis The timestamp in milliseconds to convert
	 * @return A double value representing the timestamp as a sortable score
	 */
	private Double computeScore(long millis) {
		return new BigDecimal(millis+"").divide(new BigDecimal("1000"), 3, RoundingMode.HALF_UP).doubleValue();
	}
	
	/**
	 * Thread factory for creating transaction timeout callback executor threads.
	 * 
	 * <p>This factory creates threads with a standardized naming pattern to make
	 * them easily identifiable in thread dumps and monitoring tools. Each thread
	 * is named using the format: {@code orange-redis-tx-timeout-[sequence]}
	 * 
	 * <p>The factory ensures that:
	 * <ul>
	 *   <li>Each thread has a unique sequence number</li>
	 *   <li>Thread names are consistent and descriptive</li>
	 *   <li>Threads can be easily identified for monitoring and debugging</li>
	 * </ul>
	 */
	static class OrangeTransactionTimeoutCallbackExecutorThreadFactory implements ThreadFactory {
		
		/**
		 * Atomic counter for generating unique thread sequence numbers.
		 * Ensures thread-safe incrementation of sequence numbers.
		 */
		private final AtomicInteger threadNumber = new AtomicInteger(1);

		/**
		 * Creates a new thread to run the specified task.
		 * 
		 * @param r The runnable task to be executed by the new thread
		 * @return A new thread with a standardized name pattern
		 */
		@Override
		public Thread newThread(Runnable r) {
			int seq = threadNumber.getAndIncrement();
			return new Thread(r, "orange-redis-tx-timeout-" + seq);
		}
	}
	
	/**
	 * Metric class for tracking transaction timeout callback statistics and state.
	 * 
	 * <p>This class stores important metrics related to transaction timeout callbacks,
	 * including the number of callback attempts and warning notification status.
	 * These metrics are persisted in Redis to ensure consistent tracking across
	 * service restarts and across multiple nodes in a distributed environment.
	 * 
	 * <p>The metrics are used to:
	 * <ul>
	 *   <li>Track the number of callback attempts for retry limiting</li>
	 *   <li>Prevent duplicate warning notifications for the same transaction</li>
	 *   <li>Provide visibility into transaction timeout handling</li>
	 * </ul>
	 * 
	 */
	public static class OrangeTransactionTimeoutCallbackMetric {
		
		/**
		 * Flag indicating whether a warning notification has been sent for this transaction.
		 * 0 = No warning sent, 1 = Warning sent.
		 * Used to prevent duplicate warnings for the same transaction.
		 */
		private int isWarn;
		
		/**
		 * Counter tracking the number of callback attempts for this transaction.
		 * Incremented each time a callback is attempted, and used to enforce
		 * the maximum retry limit defined in transaction properties.
		 */
		private int callbackedTimes;

		public int getIsWarn() {
			return isWarn;
		}

		public void setIsWarn(int isWarn) {
			this.isWarn = isWarn;
		}

		public int getCallbackedTimes() {
			return callbackedTimes;
		}

		public void setCallbackedTimes(int callbackedTimes) {
			this.callbackedTimes = callbackedTimes;
		}
	}
	
	/**
	 * Handles the completion of a multiple locks event for transaction timeout processing.
	 * 
	 * <p>This method is called when a multiple locks operation completes, specifically
	 * for transaction timeout callback processing. It performs the following operations:
	 * 
	 * <ul>
	 *   <li>Verifies that the event is related to transaction callback processing</li>
	 *   <li>Processes each transaction that has successfully acquired a lock</li>
	 *   <li>Retrieves and validates transaction metrics from Redis</li>
	 *   <li>Checks if the transaction has exceeded maximum retry attempts</li>
	 *   <li>Determines if the transaction is ready for callback processing based on timing</li>
	 *   <li>Executes the appropriate transaction timeout callback</li>
	 *   <li>Handles the transaction based on the callback response (commit, retry, or fail)</li>
	 *   <li>Updates transaction metrics and logs warnings when necessary</li>
	 * </ul>
	 * 
	 * <p>This method is a critical part of the transaction timeout handling mechanism,
	 * ensuring that transactions that exceed their timeout threshold are properly
	 * processed according to the registered callback handlers.
	 * 
	 * @param event The multiple locks event containing information about the locks operation
	 */
	@Override
	public void onCompleted(OrangeMultipleLocksEvent event) {
		try {
			Object arg = event.getArgs()[1];
			if(!OrangeRedisTransactionDaemonRunner.CALLBACK.equals(arg)) {
				return;
			}
			if(event.getSuccessMembers().isEmpty()) {
				logger.debug("Transaction timeout callback executor access failed, maybe another server node is running now.");
				return;
			}
			long start = System.currentTimeMillis();
			logger.info("Transaction timeout callback begin");
			for(Object uncommittedKey : event.getSuccessMembers()) {
				// Get transaction timeout callback info from Redis.
				OrangeRedisTransactionKey transactionKey = (OrangeRedisTransactionKey)uncommittedKey;
				OrangeTransactionTimeoutCallbackMetric metric = (OrangeTransactionTimeoutCallbackMetric) this.hashOperations.get(
					this.callbackMetric, 
					transactionKey, 
					RedisValueTypeEnum.JSON, 
					RedisValueTypeEnum.JSON, 
					OrangeTransactionTimeoutCallbackMetric.class
				);
				if(metric == null) {
					/*
					 * During the previous committing cycle:
					 * 1. The 'remove' method was called
					 * 2. The service restarted before the method completed.
					 */
					logger.debug("The key's({}) transaction timeout callback metric is not found.", transactionKey);
					
					// Remove again.
					remove(transactionKey);
					continue;
				}
				
				if(metric.getCallbackedTimes() > this.properties.getTimeoutCallbackTimes()) {
					// Callback exceeded the maximum retry attempts.
					warning(transactionKey,metric,"The key's({}) transaction timeout callback exceeded the maximum retry attempts. Please verify the callback logic and manually commit the transaction if necessary.",transactionKey);
					// remove(transactionKey);
					continue;
				}
				
				// Compute expected callback time
				metric.setCallbackedTimes(metric.getCallbackedTimes() + 1);
				long expectedCallbackTime = (metric.getCallbackedTimes() * this.properties.getTimeoutCallbackPeriod().toMillis()) 
																	   + this.properties.getTimeoutThreshold().toMillis() 
																	   + transactionKey.getTxBeginTime();
				
				if(expectedCallbackTime > System.currentTimeMillis()) {
					// Timing not right
					continue;
				}
				
				// Get callback by the origin key
				OrangeRedisTransactionTimeoutListener listener = this.callbacks.get(transactionKey.getOriginKey());
				if(listener == null) {
					warning(transactionKey,metric,"The key's({}) transaction timeout callback is not found. Please commit this transaction manually.",transactionKey);
					continue;
				}
				
				// Get value type from callback
				Type valueType = this.valueTypeMap.get(listener);
				if(valueType == null) {
					valueType = String.class;
				}
				
				// Get processor
				OrangeTransactionCommitProcessor processor = this.namedProcessorExecutorMap.get(transactionKey.getTxType());
				if(processor == null) {
					warning(transactionKey,metric,"No transaction commit handler was found for the key ({}) . Please commit this transaction manually.",transactionKey);
					continue;
				}
				
				//Get uncommitted value for callback.
				Object uncommittedValue = processor.getTransactionSnapshotGetExecutor().get(
					transactionKey.getKey(), 
					transactionKey.getVersion(), 
					getValueTypeEnum(valueType), 
					valueType
				);
				
				// Callback
				OrangeRedisTransactionState state = listener.callback(transactionKey,uncommittedValue,metric);
				
				// Handle transaction sate.
				if(state == OrangeRedisTransactionState.FAILED) {
					// Clear key from transaction keys registry
					remove(transactionKey);
					continue;
				}
				else if(state == OrangeRedisTransactionState.UNKNOWN) {
					// Update callback times
					updateMetric(transactionKey,metric);	
					continue;
				}
				
				// Commit
				boolean isCommitted = processor.getCommitExecutor().commit(transactionKey.getKey(), transactionKey.getVersion());
				if(isCommitted) {
					logger.debug("The key({}) commit version {} successfully.", transactionKey.getKey(), transactionKey.getVersion());
					remove(transactionKey);
				}else{
					warning(transactionKey,metric,"The transaction of the key({}) commit failed.Please contact maintainer and report this issue",transactionKey);
				}
			}
			long cost = System.currentTimeMillis() - start;
			logger.info("Transaction timeout callback finished in {}ms", cost);
			if(cost >= this.properties.getTimeoutCallbackPeriod().toMillis()) {
				this.logger.warn("Transaction timeout callback execution time {}ms > Transaction timeout callback interval {}ms.", cost, this.properties.getTimeoutCallbackPeriod().toMillis());
			}
		} catch (Exception e) {
			 logger.error("Transaction timeout callback execute error", e);
		}
	}
	
	/**
	 * Logs a warning message for a transaction and updates its warning status.
	 * 
	 * <p>This method ensures that warning messages for a transaction are only logged once
	 * to prevent log flooding. It updates the transaction's metric to indicate that
	 * a warning has been issued.
	 * 
	 * <p>In production environments, this method could be extended to send notifications
	 * through various channels (e.g., WeCom, FeiShu, or other IM platforms) to alert
	 * operations teams about problematic transactions.
	 * 
	 * @param transactionKey The key of the transaction that triggered the warning
	 * @param metric The metric data associated with the transaction
	 * @param message The warning message template with placeholders for arguments
	 * @param args Arguments to be substituted into the message template
	 * @throws Exception If an error occurs while updating the metric
	 */
	private void warning(OrangeRedisTransactionKey transactionKey,OrangeTransactionTimeoutCallbackMetric metric, String message, Object... args) throws Exception {
		if(metric.getIsWarn() <= 0) {
			// Print warning log.
			// Instead of log, add a listener to receive notifications via WeCom, FeiShu, or other IM platforms.
			logger.warn(message, args);
			metric.setIsWarn(1);
			updateMetric(transactionKey,metric);
		}
	}
	
	/**
	 * Updates the transaction timeout callback metric in Redis.
	 * 
	 * <p>This method persists the updated transaction metric data to Redis, ensuring that
	 * the latest state of the transaction is available for monitoring and decision-making.
	 * 
	 * <p>The metric data is stored with the transaction key as the hash key in Redis,
	 * allowing efficient retrieval and updates of individual transaction metrics
	 * without affecting other transactions.
	 * 
	 * @param transactionKey The key of the transaction whose metric is being updated
	 * @param metric The updated metric data to be stored
	 * @throws Exception If an error occurs during the Redis operation
	 */
	private void updateMetric(OrangeRedisTransactionKey transactionKey,OrangeTransactionTimeoutCallbackMetric metric) throws Exception {
		this.hashOperations.putMember(
			this.callbackMetric, 
			transactionKey,  
			metric, 
			RedisValueTypeEnum.JSON, 
			RedisValueTypeEnum.JSON
		);
	}

	/**
	 * Sets the transaction daemon runner for this executor.
	 * 
	 * <p>The transaction daemon runner is responsible for executing transaction-related
	 * operations in a coordinated manner across multiple nodes in a distributed environment.
	 * It handles locking and ensures that only one node processes a specific transaction
	 * at a time, preventing race conditions and duplicate processing.
	 * 
	 * <p>This method is typically called during application initialization after the
	 * executor has been constructed, as part of a dependency injection setup.
	 * 
	 * @param runner The transaction daemon runner to be used by this executor
	 */
	void setRunner(OrangeRedisTransactionDaemonRunner runner) {
		this.runner = runner;
	}
	
	/**
	 * Determines the appropriate Redis value type enumeration based on a Java type.
	 * 
	 * <p>This method maps Java types to their corresponding Redis value type enumerations,
	 * which are used to properly serialize and deserialize values when interacting with Redis.
	 * The mapping follows these rules:
	 * 
	 * <ul>
	 *   <li>{@code String.class} → {@code RedisValueTypeEnum.STRING}</li>
	 *   <li>Float/Double types → {@code RedisValueTypeEnum.DOUBLE}</li>
	 *   <li>Integer/Long types → {@code RedisValueTypeEnum.LONG}</li>
	 *   <li>All other types → {@code RedisValueTypeEnum.JSON} (serialized as JSON)</li>
	 * </ul>
	 * 
	 * <p>This method is used internally when retrieving transaction values from Redis
	 * to ensure proper deserialization based on the expected value type of the
	 * transaction timeout listener.
	 * 
	 * @param valueType The Java type to map to a Redis value type
	 * @return The corresponding Redis value type enumeration
	 */
	private RedisValueTypeEnum getValueTypeEnum(Type valueType) {
		if(valueType == String.class) {
			return RedisValueTypeEnum.STRING;
		}
		if(OrangeReflectionUtils.isFloat(valueType)) {
			return RedisValueTypeEnum.DOUBLE;
		}
		if(OrangeReflectionUtils.isInteger(valueType)) {
			return RedisValueTypeEnum.LONG;
		}
		return RedisValueTypeEnum.JSON;
	}
	
	@Override
	public void forEach(BiConsumer action) {
		try {
			// Get all uncommitted keys from Redis.
			Set<Object> uncommittedKeys = getTimeoutTransactinKeys();
			for(Object uncommittedKey : uncommittedKeys) {
				// Get lock for handling this uncommitted key
				LockKey lockKey = new LockKey();
				lockKey.setLockType(OrangeRedisTransactionDaemonRunner.CALLBACK);
				lockKey.setServiceName(this.properties.getServiceName());
				lockKey.setLockMetaData(uncommittedKey);;
				action.accept(lockKey, uncommittedKey);
			}
		}catch (Exception e) {
			throw new OrangeRedisException("Transaction timeout callback execute error",e);
		}
	}

	@Override
	public Object[] toArray() {
		throw new OrangeRedisException("Not support!");
	}

	@Override
	public boolean continueOnFailure() {
		return true;
	}
	
	/**
	 * Retrieves transaction keys that have exceeded their timeout threshold and are eligible for callback processing.
	 * 
	 * <p>This method identifies transactions that have been active for longer than the configured
	 * timeout threshold and should be processed by the timeout callback mechanism.
	 * 
	 * <p>The method queries the transaction monitoring queue using a score range from the
	 * calculated minimum score (based on the begin time) to the maximum score (based on the
	 * current time) to retrieve all transactions that fall within this time window.
	 * 
	 * <p>These transactions are then processed by the timeout callback mechanism to determine
	 * whether they should be committed, retried, or rolled back.
	 * 
	 * @return A set of transaction keys that have exceeded their timeout threshold
	 * @throws Exception If an error occurs during the retrieval process
	 */
	private Set<Object> getTimeoutTransactinKeys() throws Exception{
		long now = System.currentTimeMillis();
		long beginTime = now - this.properties.getTimeoutThreshold().toMillis() - (this.properties.getTimeoutCallbackPeriod().toMillis() * this.properties.getTimeoutCallbackTimes()) - 1000;
		Double maxScore = computeScore(now);
		Double minScore = computeScore(beginTime);
		return this.operations.rangeByScore(this.registry, new ScoreRange(maxScore,minScore), RedisValueTypeEnum.JSON, OrangeRedisTransactionKey.class);
	}

	/**
	 * Cleans up dead transactions that have exceeded their retention period.
	 * 
	 * <p>This method identifies and removes transactions that are considered "dead" based on
	 * configurable thresholds.
	 * 
	 * <p>For each identified dead transaction:
	 * <ul>
	 *   <li>The transaction is removed from the monitoring queue</li>
	 *   <li>Associated callback metrics are cleaned up</li>
	 *   <li>Resources associated with the transaction are released</li>
	 * </ul>
	 * 
	 * 
	 * <p>This cleanup process helps prevent resource leaks and ensures that the
	 * transaction monitoring system remains efficient by removing transactions
	 * that are no longer relevant.
	 * 
	 * @throws Exception If an error occurs during the cleanup process
	 */
	protected void clearDeadTransaction() throws Exception {
		long end = System.currentTimeMillis() - this.properties.getDeadTransactionKeepThreshold().toMillis() 
											  - this.properties.getTimeoutThreshold().toMillis() 
											  - (
													  this.properties.getTimeoutCallbackPeriod().toMillis() 
													  * this.properties.getTimeoutCallbackTimes()
												);
		Double maxScore = computeScore(end);
		Set<Object> transactinKeys = this.operations.rangeByScore(this.registry, new ScoreRange(maxScore,Double.valueOf(0)), RedisValueTypeEnum.JSON, OrangeRedisTransactionKey.class);
		for(Object deadKey : transactinKeys) {
			remove((OrangeRedisTransactionKey)deadKey);
		}
	}

	@Override
	public void onRemoveFailed(OrangeMultipleLocksRemoveFailedEvent event) {
		this.logger.warn(
			"Failed to release the transaction callback lock. No action taken; the lock should auto-expire in {} milliseconds.",
			TimeUnit.MILLISECONDS.convert(event.getRedisKey().getExpirationTime(), event.getRedisKey().getExpirationTimeUnit())
		);
	}

	/**
	 * Retrieves a set of transaction keys that are considered "dead" and eligible for cleanup.
	 * 
	 * <p>This method identifies transactions that have exceeded their retention period
	 * and should be removed from the system.
	 * 
	 * <p>The method queries the transaction monitoring queue using time-based criteria
	 * to efficiently identify transactions that have been inactive for too long. It uses
	 * a score range from 0 to the calculated maximum score (based on the begin time threshold)
	 * to retrieve all transactions that started before the threshold.
	 * 
	 * <p>The returned set of transaction keys can be used by the cleanup process
	 * to remove these dead transactions from the system and free up associated resources.
	 * 
	 * @return A set of transaction keys representing dead transactions
	 * @throws Exception If an error occurs during the retrieval process
	 */
	public Set<Object> getDeadTransaction() throws Exception {
		long beginTime = System.currentTimeMillis() - this.properties.getTimeoutThreshold().toMillis() 
													- (this.properties.getTimeoutCallbackPeriod().toMillis() * this.properties.getTimeoutCallbackTimes()) 
													- 1000;
		Double maxScore = computeScore(beginTime);
		Double minScore = computeScore(0);
		return this.operations.rangeByScore(this.registry, new ScoreRange(maxScore,minScore), RedisValueTypeEnum.JSON, OrangeRedisTransactionKey.class);
	}
}