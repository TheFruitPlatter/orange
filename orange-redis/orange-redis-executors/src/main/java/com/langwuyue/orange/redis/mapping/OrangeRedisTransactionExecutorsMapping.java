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
package com.langwuyue.orange.redis.mapping;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import com.langwuyue.orange.redis.executor.OrangeRedisExecutor;
import com.langwuyue.orange.redis.executor.multiplelocks.OrangeExpirationTimeAutoInitializer;
import com.langwuyue.orange.redis.executor.transaction.OrangeRedisDefaultTransactionManager;
import com.langwuyue.orange.redis.executor.transaction.OrangeRedisDefaultTransactionManager.OrangeTransactionCommitProcessor;
import com.langwuyue.orange.redis.executor.transaction.OrangeRedisTransactionProperties;
import com.langwuyue.orange.redis.executor.transaction.OrangeRedisTransactionSnapshotGetExecutor;
import com.langwuyue.orange.redis.executor.transaction.OrangeRedisTransactionTimeoutListener;
import com.langwuyue.orange.redis.executor.transaction.value.OrangeTransactionCommitExecutor;
import com.langwuyue.orange.redis.executor.transaction.value.OrangeTransactionCommitWithExpirationExecutor;
import com.langwuyue.orange.redis.executor.transaction.value.OrangeTransactionGetExecutor;
import com.langwuyue.orange.redis.executor.transaction.value.OrangeTransactionSetExecutor;
import com.langwuyue.orange.redis.executor.transaction.value.OrangeTransactionSnapshotGetExecutor;
import com.langwuyue.orange.redis.listener.OrangeRedisMultipleSetIfAbsentListener;
import com.langwuyue.orange.redis.listener.OrangeRedisSetIfAbsentListener;
import com.langwuyue.orange.redis.logger.OrangeRedisLogger;
import com.langwuyue.orange.redis.operations.OrangeRedisHashOperations;
import com.langwuyue.orange.redis.operations.OrangeRedisScriptOperations;
import com.langwuyue.orange.redis.operations.OrangeRedisSetOperations;
import com.langwuyue.orange.redis.operations.OrangeRedisZSetOperations;
import com.langwuyue.orange.redis.template.hash.JSONOperationsTemplate;
import com.langwuyue.orange.redis.timer.OrangeRenewTimerWheel;

/**
 * @author Liang.Zhong
 * @since 1.0.0
 */
public class OrangeRedisTransactionExecutorsMapping extends OrangeRedisAbstractExecutorsMapping {
	
	private OrangeRedisDefaultTransactionManager transactionManager;
	
	public OrangeRedisTransactionExecutorsMapping(
		OrangeRedisHashOperations operations,
		OrangeRedisSetOperations setOperations,
		OrangeRedisZSetOperations zsetOperations,
		OrangeRedisTransactionExecutorIdGenerator generator,
		Collection<OrangeRedisSetIfAbsentListener> listeners,
		OrangeRedisScriptOperations scriptOperations,
		Collection<OrangeRedisMultipleSetIfAbsentListener> multipleListeners,
		OrangeRedisTransactionProperties properties,
		OrangeRenewTimerWheel wheel,
		OrangeExpirationTimeAutoInitializer expirationTimeAutoInitializer,
		Map<String,OrangeRedisTransactionTimeoutListener> callbacks,
		OrangeRedisLogger logger
		
	) {
		super(operations,generator,scriptOperations,listeners,multipleListeners,logger);
		// Processor for auto committing.
		OrangeTransactionCommitExecutor commitExecutor = new OrangeTransactionCommitExecutor(scriptOperations,generator,logger);
		OrangeRedisTransactionSnapshotGetExecutor snapshotGetExecutor = new OrangeTransactionSnapshotGetExecutor(operations,generator);
		OrangeTransactionCommitProcessor processor = new OrangeTransactionCommitProcessor(commitExecutor,snapshotGetExecutor,"value");
		
		// Processors map
		Map<String,OrangeTransactionCommitProcessor> namedProcessMap = new HashMap<>();
		namedProcessMap.put(processor.getTxType(), processor);
		
		// Build transaction manager
		this.transactionManager = new OrangeRedisDefaultTransactionManager(
			operations,
			setOperations,
			zsetOperations,
			properties,
			scriptOperations,
			wheel,
			expirationTimeAutoInitializer,
			callbacks,
			logger,
			namedProcessMap
		);
		commitExecutor.setTransactionManager(this.transactionManager);
		

		// Register executor with transaction manager.
		this.registerExecutors(new OrangeTransactionSetExecutor(
			operations,
			generator,
			this.transactionManager,
			processor
		));
		this.registerExecutors(new OrangeTransactionGetExecutor(scriptOperations,generator,this.transactionManager,logger));
		this.registerExecutors(new OrangeTransactionCommitWithExpirationExecutor(scriptOperations,operations,generator,this.transactionManager,logger));
		this.registerExecutors(commitExecutor);
		this.registerExecutors((OrangeRedisExecutor)snapshotGetExecutor);
	}

	@Override
	protected Class<?> getTemplateClass() {
		return JSONOperationsTemplate.class;
	}
	
	public OrangeRedisDefaultTransactionManager getTransactionManager() {
		return transactionManager;
	}
	
	
}
