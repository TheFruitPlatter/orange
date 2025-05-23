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
 * @author Liang.Zhong
 * @since 1.0.0
 */
public class OrangeRedisTransactionGarbageCollector implements Runnable, OrangeRedisMultipleLocksListener,OrangeRedisIterableContext {
	
	private static final String TRANSACTION_GC_KEY_REGISTRY = "orange:transaction:gc:keys:registry:";
	
	private ScheduledExecutorService executorService;
	
	private OrangeRedisHashOperations operations;
	
	private OrangeRedisSetOperations setOperations;
	
	private long cursor;
	
	private String gcRegistry;
	
	private OrangeRedisTransactionDaemonRunner runner;
	
	private OrangeRedisLogger logger;
	
	private OrangeRedisTransactionProperties properties;
	
	private OrangeRedisTransactionTimeoutCallbackExecutor transactionTimeoutCallbackExecutor;
	
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

	void register(String key) throws Exception {
		this.setOperations.add(this.gcRegistry, RedisValueTypeEnum.STRING, key);
	}
	
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
	
	static class OrangeTransactionGarbageCollectorThreadFactory implements ThreadFactory {

		private final AtomicInteger threadNumber = new AtomicInteger(1);
		
		@Override
		public Thread newThread(Runnable r) {
			int seq = threadNumber.getAndIncrement();
			return new Thread(r, "orange-redis-tx-gc-" + seq);
		}
	}

	void setRunner(OrangeRedisTransactionDaemonRunner runner) {
		this.runner = runner;
	}

	@Override
	public void forEach(BiConsumer action) {
		LockKey lockKey = new LockKey();
		lockKey.setLockType(OrangeRedisTransactionDaemonRunner.GC);
		lockKey.setServiceName(this.properties.getServiceName());
		action.accept(lockKey, lockKey);
	}

	@Override
	public Object[] toArray() {
		throw new OrangeRedisException("Not support!");
	}

	@Override
	public boolean continueOnFailure() {
		return false;
	}

	@Override
	public void onRemoveFailed(OrangeMultipleLocksRemoveFailedEvent event) {
		this.logger.warn(
			"Failed to release the transaction GC lock. No action taken; the lock should auto-expire in {} milliseconds.",
			TimeUnit.MILLISECONDS.convert(event.getRedisKey().getExpirationTime(), event.getRedisKey().getExpirationTimeUnit())
		);
	}

	public void destory() {
		if(this.executorService != null) {
			this.logger.info("Transaction GC thread pool is shutting down now");
			this.executorService.shutdown();
		}
	}
}