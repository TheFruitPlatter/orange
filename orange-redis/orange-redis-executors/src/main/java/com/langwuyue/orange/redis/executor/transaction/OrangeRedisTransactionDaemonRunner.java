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
 * @author Liang.Zhong
 * @since 1.0.0
 */
class OrangeRedisTransactionDaemonRunner {
	
	public static final String GC = "orange:transaction:gc";
	
	public static final String CALLBACK = "orange:transaction:callback";
	
	private static final String TRANSACTION_DAEMON_RUNNER_LOCK = "orange:transaction:daemon:runner:lock";

	private OrangeMultipleLocksExecutor locksExecutor;
	
	private OrangeRedisTransactionProperties properties;
	
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
	
	public void run(String type,OrangeRedisIterableContext ctx, long lockExpirationMillis) {
		Key key = new Key(TRANSACTION_DAEMON_RUNNER_LOCK, TRANSACTION_DAEMON_RUNNER_LOCK, lockExpirationMillis, TimeUnit.MILLISECONDS);
		locksExecutor.doLock(ctx , key, true, 3, RedisValueTypeEnum.JSON, new Object[] {this.properties.getServiceName(),type});
	}
	
	public static class LockKey {
		
		private String lockType;
		
		private String serviceName;
		
		private Object lockMetaData;

		public String getLockType() {
			return lockType;
		}

		public void setLockType(String lockType) {
			this.lockType = lockType;
		}

		public String getServiceName() {
			return serviceName;
		}

		public void setServiceName(String serviceName) {
			this.serviceName = serviceName;
		}

		public Object getLockMetaData() {
			return lockMetaData;
		}

		public void setLockMetaData(Object lockMetaData) {
			this.lockMetaData = lockMetaData;
		}
	}
}
