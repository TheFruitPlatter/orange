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
 * @author Liang.Zhong
 * @since 1.0.0
 */
public class OrangeRedisDefaultTransactionManager implements OrangeRedisTransactionManager {
	
	private static final ThreadLocal<List<OrangeTransactionCommitInfo>> TRANSACTION_INFOS = new ThreadLocal<>();
	
	private OrangeRedisTransactionGarbageCollector transactionGC;
	
	private OrangeRedisTransactionTimeoutCallbackExecutor transactionTimeoutCallbackExecutor;
	
	private Map<OrangeTransactionCommitProcessor,String> proccessorNameMap;
	
	private boolean enabled = false;
	
	private OrangeRedisLogger logger;
	
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
	 * Execute once after the database transaction is committed.
	 * 
	 * @throws Exception
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
	
	public void alreadyCommittedManually() throws Exception {
		remove();
	}
	
	@Override
	public void rollback() throws Exception {
		remove();
	}
	
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
	 * Save transaction information for executing commit operation automatically. 
	 * 
	 * @see {@link #commit()}
	 * @param commitExecutor
	 * @param context
	 * @param version
	 * @throws Exception
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
	 * Get version for reading uncommitted value within the current transaction.
	 * 
	 * @param key
	 * @return version number
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
	
	public Set<Object> getDeadTransaction() throws Exception {
		if(!this.enabled) {
			return new HashSet<>();
		}
		return this.transactionTimeoutCallbackExecutor.getDeadTransaction();
	}
	
	public static class OrangeTransactionCommitInfo {
		
		private OrangeTransactionCommitProcessor processor;
		
		private OrangeRedisTransactionKey transactionKey;
		
		public OrangeTransactionCommitInfo(
			OrangeTransactionCommitProcessor processor,
			OrangeRedisTransactionKey transactionKey
		) {
			super();
			this.processor = processor;
			this.transactionKey = transactionKey;
		}

		public OrangeTransactionCommitProcessor getProcessor() {
			return processor;
		}

		public void setProcessor(OrangeTransactionCommitProcessor processor) {
			this.processor = processor;
		}

		public OrangeRedisTransactionKey getTransactionKey() {
			return transactionKey;
		}

		public void setTransactionKey(OrangeRedisTransactionKey transactionKey) {
			this.transactionKey = transactionKey;
		}
	}
	
	public static class OrangeTransactionCommitProcessor {
		
		private OrangeRedisTransactionCommitExecutor commitExecutor;
		
		private OrangeRedisTransactionSnapshotGetExecutor transactionSnapshotGetExecutor;
		
		private String txType;
		
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

		public OrangeRedisTransactionCommitExecutor getCommitExecutor() {
			return commitExecutor;
		}

		public void setCommitExecutor(OrangeRedisTransactionCommitExecutor commitExecutor) {
			this.commitExecutor = commitExecutor;
		}

		public OrangeRedisTransactionSnapshotGetExecutor getTransactionSnapshotGetExecutor() {
			return transactionSnapshotGetExecutor;
		}

		public void setTransactionSnapshotGetExecutor(
				OrangeRedisTransactionSnapshotGetExecutor transactionSnapshotGetExecutor) {
			this.transactionSnapshotGetExecutor = transactionSnapshotGetExecutor;
		}

		public String getTxType() {
			return txType;
		}

		public void setTxType(String txType) {
			this.txType = txType;
		}
	}
	
	
	public static class OrangeRedisTransactionKey {
		
		private  long version;
		
		private String originKey;
		
		private String key;
		
		private long txBeginTime;
		
		private String txType;
		
		public long getVersion() {
			return version;
		}

		public void setVersion(long version) {
			this.version = version;
		}

		public String getOriginKey() {
			return originKey;
		}

		public void setOriginKey(String originKey) {
			this.originKey = originKey;
		}

		public String getKey() {
			return key;
		}

		public void setKey(String key) {
			this.key = key;
		}

		public long getTxBeginTime() {
			return txBeginTime;
		}

		public void setTxBeginTime(long txBeginTime) {
			this.txBeginTime = txBeginTime;
		}

		public String getTxType() {
			return txType;
		}

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
