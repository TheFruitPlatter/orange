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
import java.util.List;

import com.langwuyue.orange.redis.executor.OrangeRedisExecutor;
import com.langwuyue.orange.redis.executor.multiplelocks.OrangeExpirationTimeAutoInitializer;
import com.langwuyue.orange.redis.executor.value.OrangeCASWithExpirationExecutor;
import com.langwuyue.orange.redis.executor.value.OrangeCompareAndSwapExecutor;
import com.langwuyue.orange.redis.executor.value.OrangeDecrementExecutor;
import com.langwuyue.orange.redis.executor.value.OrangeDecrementWithoutValueExecutor;
import com.langwuyue.orange.redis.executor.value.OrangeGetExecutor;
import com.langwuyue.orange.redis.executor.value.OrangeIncrementExecutor;
import com.langwuyue.orange.redis.executor.value.OrangeIncrementWithoutValueExecutor;
import com.langwuyue.orange.redis.executor.value.OrangeSetExecutor;
import com.langwuyue.orange.redis.executor.value.OrangeSetIfAbsentExecutor;
import com.langwuyue.orange.redis.executor.value.OrangeSetIfAbsentWithExpirationExecutor;
import com.langwuyue.orange.redis.executor.value.OrangeSetWithExpirationExecutor;
import com.langwuyue.orange.redis.executor.value.OrangeValueLockAutoRenewExpirationExecutor;
import com.langwuyue.orange.redis.listener.OrangeRedisMultipleSetIfAbsentListener;
import com.langwuyue.orange.redis.listener.OrangeRedisSetIfAbsentListener;
import com.langwuyue.orange.redis.logger.OrangeRedisLogger;
import com.langwuyue.orange.redis.operations.OrangeRedisOperations;
import com.langwuyue.orange.redis.operations.OrangeRedisScriptOperations;
import com.langwuyue.orange.redis.operations.OrangeRedisValueOperations;
import com.langwuyue.orange.redis.template.value.JSONOperationsTemplate;
import com.langwuyue.orange.redis.timer.OrangeRenewTimerWheel;

/**
 * @author Liang.Zhong
 * @since 1.0.0
 */
public class OrangeRedisValueExecutorsMapping extends OrangeRedisAbstractExecutorsMapping {
	
	public OrangeRedisValueExecutorsMapping(
		OrangeRedisValueOperations operations,
		OrangeRedisValueExecutorIdGenerator generator,
		Collection<OrangeRedisSetIfAbsentListener> listeners,
		OrangeRedisScriptOperations scriptOperations,
		Collection<OrangeRedisMultipleSetIfAbsentListener> multipleListeners,
		OrangeRenewTimerWheel renewTimerWheel,
		OrangeExpirationTimeAutoInitializer expirationTimeAutoInitializer,
		OrangeRedisLogger logger
	) {
		super(operations,generator,scriptOperations,listeners,multipleListeners,logger);
		this.registerExecutors(new OrangeValueLockAutoRenewExpirationExecutor(
				operations,
				generator,
				listeners,
				renewTimerWheel,
				expirationTimeAutoInitializer,
				logger
		));
	}

	@Override
	protected void registerExecutors(
		List<OrangeRedisExecutor> executors, 
		OrangeRedisOperations operations,
		OrangeRedisExecutorIdGenerator generator,
		OrangeRedisScriptOperations scriptOperations,
		Collection<OrangeRedisSetIfAbsentListener> listeners,
		Collection<OrangeRedisMultipleSetIfAbsentListener> multipleListeners,
		OrangeRedisLogger logger
	) {
		super.registerExecutors(executors, operations, generator,scriptOperations,listeners,multipleListeners,logger);
		OrangeRedisValueOperations valueOperations = (OrangeRedisValueOperations) operations;
		executors.add(new OrangeCASWithExpirationExecutor(scriptOperations,valueOperations,generator,logger));
		executors.add(new OrangeCompareAndSwapExecutor(scriptOperations,generator,logger));
		executors.add(new OrangeDecrementExecutor(valueOperations,generator));
		executors.add(new OrangeDecrementWithoutValueExecutor(valueOperations,generator));
		executors.add(new OrangeGetExecutor(valueOperations,generator));
		executors.add(new OrangeIncrementExecutor(valueOperations,generator));
		executors.add(new OrangeIncrementWithoutValueExecutor(valueOperations,generator));
		executors.add(new OrangeSetExecutor(valueOperations,generator));
		executors.add(new OrangeSetIfAbsentExecutor(valueOperations,generator,listeners));
		executors.add(new OrangeSetIfAbsentWithExpirationExecutor(valueOperations,generator,listeners));
		executors.add(new OrangeSetWithExpirationExecutor(valueOperations,generator));
	}

	@Override
	protected Class<?> getTemplateClass() {
		return JSONOperationsTemplate.class;
	}
}
