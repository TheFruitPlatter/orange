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
 * Transaction timeout callback executor
 * 1. Callback when transaction timeout
 * 2. Clear dead transaction
 * 
 * 
 * @author Liang.Zhong
 * @since 1.0.0
 */
public class OrangeRedisTransactionTimeoutCallbackExecutor implements Runnable, OrangeRedisMultipleLocksListener,OrangeRedisIterableContext {
	
	private static final String UNCOMMITTED_TRANSACTION_KEYS_REGISTRY = "orange:transaction:uncommit:keys:registry:";
	
	private static final String CALLBACK_METRIC_DATA_KEY = "orange:transaction:callback:metric:";
	
	private OrangeRedisZSetOperations operations;
	
	private OrangeRedisHashOperations hashOperations;
	
	private ScheduledExecutorService executorService;
	
	private OrangeRedisTransactionProperties properties;
	
	private String registry;
	
	private String callbackMetric;
	
	private OrangeRedisTransactionDaemonRunner runner;
	
	private Map<String,OrangeRedisTransactionTimeoutListener> callbacks;
	
	private Map<OrangeRedisTransactionTimeoutListener,Type> valueTypeMap;
	
	private OrangeRedisLogger logger;
	
	private Map<String,OrangeTransactionCommitProcessor> namedProcessorExecutorMap;
	
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
	 * Add a transaction key into transaction timeout callback queue
	 * <p>
	 * The queue will sorted by transaction begin time.
	 * 
	 * 
	 * Saves callback attempt metrics including
	 * <p>
	 * - Total callback attempts 
	 * - Whether the notification was sent 
	 *  
	 * 
	 * @param transactionKey
	 * @param commitExecutor
	 * @throws Exception
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
	 * Compute the score
	 * 
	 * <p>
	 * Convert the timestamp to a double score.
	 * The decimal part of the score represents milliseconds(Nanoseconds may be used in future implementations).
	 * 
	 * 
	 * @param transactionKey
	 * @return
	 */
	private Double computeScore(long millis) {
		return new BigDecimal(millis+"").divide(new BigDecimal("1000"), 3, RoundingMode.HALF_UP).doubleValue();
	}
	
	static class OrangeTransactionTimeoutCallbackExecutorThreadFactory implements ThreadFactory {
		
		private final AtomicInteger threadNumber = new AtomicInteger(1);

		@Override
		public Thread newThread(Runnable r) {
			int seq = threadNumber.getAndIncrement();
			return new Thread(r, "orange-redis-tx-timeout-" + seq);
		}
	}
	
	public static class OrangeTransactionTimeoutCallbackMetric {
		
		private int isWarn;
		
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
				else if(state == OrangeRedisTransactionState.UNKNOW) {
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
	
	private void warning(OrangeRedisTransactionKey transactionKey,OrangeTransactionTimeoutCallbackMetric metric, String message, Object... args) throws Exception {
		if(metric.getIsWarn() <= 0) {
			// Print warning log.
			// Instead of log, add a listener to receive notifications via WeCom, FeiShu, or other IM platforms.
			logger.warn(message, args);
			metric.setIsWarn(1);
			updateMetric(transactionKey,metric);
		}
	}
	
	private void updateMetric(OrangeRedisTransactionKey transactionKey,OrangeTransactionTimeoutCallbackMetric metric) throws Exception {
		this.hashOperations.putMember(
			this.callbackMetric, 
			transactionKey,  
			metric, 
			RedisValueTypeEnum.JSON, 
			RedisValueTypeEnum.JSON
		);
	}

	void setRunner(OrangeRedisTransactionDaemonRunner runner) {
		this.runner = runner;
	}
	
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
	 * Get timeout transaction keys
	 * 
	 * @return
	 * @throws Exception
	 */
	private Set<Object> getTimeoutTransactinKeys() throws Exception{
		long now = System.currentTimeMillis();
		long beginTime = now - this.properties.getTimeoutThreshold().toMillis() - (this.properties.getTimeoutCallbackPeriod().toMillis() * this.properties.getTimeoutCallbackTimes()) - 1000;
		Double maxScore = computeScore(now);
		Double minScore = computeScore(beginTime);
		return this.operations.rangeByScore(this.registry, new ScoreRange(maxScore,minScore), RedisValueTypeEnum.JSON, OrangeRedisTransactionKey.class);
	}

	/**
	 * Clear Dead transaction.
	 * 
	 * @throws Exception
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

	public Set<Object> getDeadTransaction() throws Exception {
		long beginTime = System.currentTimeMillis() - this.properties.getTimeoutThreshold().toMillis() 
													- (this.properties.getTimeoutCallbackPeriod().toMillis() * this.properties.getTimeoutCallbackTimes()) 
													- 1000;
		Double maxScore = computeScore(beginTime);
		Double minScore = computeScore(0);
		return this.operations.rangeByScore(this.registry, new ScoreRange(maxScore,minScore), RedisValueTypeEnum.JSON, OrangeRedisTransactionKey.class);
	}
}
