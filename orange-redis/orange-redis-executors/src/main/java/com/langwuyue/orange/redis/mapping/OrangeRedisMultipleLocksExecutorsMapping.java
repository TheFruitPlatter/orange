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
 * A specialized executor mapping for Redis multiple locks operations.
 * 
 * <p>This class extends {@link OrangeRedisAbstractExecutorsMapping} to provide
 * specific support for multiple locks operations in Redis. It registers and manages
 * various executors that handle different aspects of multiple locks functionality,
 * such as lock acquisition, release, expiration management, and auto-renewal.
 * 
 * <p>The mapping provides the following key features:
 * <ul>
 *   <li>Multiple locks acquisition and management</li>
 *   <li>Lock release operations</li>
 *   <li>Expiration time handling</li>
 *   <li>Auto-renewal of locks</li>
 *   <li>Delete operations for locks</li>
 * </ul>
 * 
 * <p>This mapping uses {@link JSONOperationsTemplate} as its template class for
 * performing Redis operations, providing a JSON-based interface for multiple locks
 * management.
 *
 * @author Liang.Zhong
 * @since 1.0.0
 */
public class OrangeRedisMultipleLocksExecutorsMapping extends OrangeRedisAbstractExecutorsMapping {
	
	/**
	 * Constructs a new OrangeRedisMultipleLocksExecutorsMapping with the specified components.
	 * 
	 * <p>This constructor initializes the mapping with all necessary dependencies for
	 * handling multiple locks operations in Redis. It registers the primary
	 * {@link OrangeMultipleLocksExecutor} that handles the core multiple locks functionality.
	 *
	 * @param operations the Redis hash operations for interacting with Redis
	 * @param generator the executor ID generator specific to multiple locks operations
	 * @param listeners collection of listeners for single lock set-if-absent operations
	 * @param scriptOperations the Redis script operations for executing Lua scripts
	 * @param multipleListeners collection of listeners for multiple locks set-if-absent operations
	 * @param wheel the timer wheel for handling lock renewal operations
	 * @param expirationTimeAutoInitializer the initializer for automatic expiration time settings
	 * @param logger the logger for recording operations and errors
	 */
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

	/**
	 * Registers all standard executors supported by this mapping.
	 * 
	 * <p>Each executor is initialized with appropriate dependencies and added to the
	 * provided executors list.
	 *
	 * @param executors the list to which executors will be added
	 * @param operations the Redis operations for data access
	 * @param generator the executor ID generator
	 * @param scriptOperations the Redis script operations for executing Lua scripts
	 * @param listeners collection of listeners for single lock set-if-absent operations
	 * @param multipleListeners collection of listeners for multiple locks set-if-absent operations
	 * @param logger the logger for operation tracking
	 */
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

	/**
	 * Returns the template class used by this mapping for Redis operations.
	 * 
	 * <p>This implementation returns {@link JSONOperationsTemplate} as the template class,
	 * which provides JSON-based operations for multiple locks management in Redis.
	 * The template class defines the interface and behavior for interacting with
	 * Redis in the context of multiple locks operations.
	 *
	 * @return the {@link JSONOperationsTemplate} class
	 */
	@Override
	protected Class<?> getTemplateClass() {
		return JSONOperationsTemplate.class;
	}
}