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
import com.langwuyue.orange.redis.executor.global.OrangeDeleteExecutor;
import com.langwuyue.orange.redis.executor.multiplelocks.OrangeExpirationTimeAutoInitializer;
import com.langwuyue.orange.redis.executor.multiplelocks.OrangeGetExpirationExecutor;
import com.langwuyue.orange.redis.executor.multiplelocks.OrangeGetExpirationWithUnitArgsExecutor;
import com.langwuyue.orange.redis.executor.multiplelocks.OrangeMultipleLocksExecutor;
import com.langwuyue.orange.redis.executor.multiplelocks.OrangeMultipleLocksReleaseExecutor;
import com.langwuyue.orange.redis.listener.OrangeRedisMultipleSetIfAbsentListener;
import com.langwuyue.orange.redis.listener.OrangeRedisSetIfAbsentListener;
import com.langwuyue.orange.redis.logger.OrangeRedisLogger;
import com.langwuyue.orange.redis.operations.OrangeRedisHashOperations;
import com.langwuyue.orange.redis.operations.OrangeRedisOperations;
import com.langwuyue.orange.redis.operations.OrangeRedisScriptOperations;
import com.langwuyue.orange.redis.template.multiplelocks.JSONOperationsTemplate;
import com.langwuyue.orange.redis.timer.OrangeRenewTimerWheel;

/**
 * @author Liang.Zhong
 * @since 1.0.0
 */
public class OrangeRedisMultipleLocksExecutorsMapping extends OrangeRedisAbstractExecutorsMapping {
	
	public OrangeRedisMultipleLocksExecutorsMapping(
		OrangeRedisHashOperations operations,
		OrangeRedisMultipleLocksExecutorIdGenerator generator,
		Collection<OrangeRedisSetIfAbsentListener> listeners,
		OrangeRedisScriptOperations scriptOperations,
		Collection<OrangeRedisMultipleSetIfAbsentListener> multipleListeners,
		OrangeRenewTimerWheel wheel,
		OrangeExpirationTimeAutoInitializer expirationTimeAutoInitializer,
		OrangeRedisLogger logger
	) {
		super(operations,generator,scriptOperations,listeners,multipleListeners,logger);
		OrangeRedisHashOperations hashOperations = (OrangeRedisHashOperations) operations;
		this.registerExecutors(new OrangeMultipleLocksExecutor(
				scriptOperations, 
				hashOperations, 
				generator,
				multipleListeners,
				wheel,
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
		OrangeRedisHashOperations hashOperations = (OrangeRedisHashOperations) operations;
		executors.add(new OrangeDeleteExecutor(hashOperations,generator));
		executors.add(new OrangeGetExpirationExecutor(hashOperations,generator));
		executors.add(new OrangeGetExpirationWithUnitArgsExecutor(hashOperations,generator));
		executors.add(new OrangeMultipleLocksReleaseExecutor(hashOperations,generator,logger));
	}

	@Override
	protected Class<?> getTemplateClass() {
		return JSONOperationsTemplate.class;
	}
}
