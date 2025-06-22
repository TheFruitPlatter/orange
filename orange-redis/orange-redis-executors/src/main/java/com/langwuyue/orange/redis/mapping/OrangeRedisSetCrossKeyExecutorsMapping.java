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
import com.langwuyue.orange.redis.executor.cross.set.OrangeDifferenceAndStoreExecutor;
import com.langwuyue.orange.redis.executor.cross.set.OrangeDifferenceExecutor;
import com.langwuyue.orange.redis.executor.cross.set.OrangeIntersectAndStoreExecutor;
import com.langwuyue.orange.redis.executor.cross.set.OrangeIntersectExecutor;
import com.langwuyue.orange.redis.executor.cross.set.OrangeMoveExecutor;
import com.langwuyue.orange.redis.executor.cross.set.OrangeUnionAndStoreExecutor;
import com.langwuyue.orange.redis.executor.cross.set.OrangeUnionExecutor;
import com.langwuyue.orange.redis.listener.OrangeRedisMultipleSetIfAbsentListener;
import com.langwuyue.orange.redis.listener.OrangeRedisSetIfAbsentListener;
import com.langwuyue.orange.redis.logger.OrangeRedisLogger;
import com.langwuyue.orange.redis.operations.OrangeRedisOperations;
import com.langwuyue.orange.redis.operations.OrangeRedisScriptOperations;
import com.langwuyue.orange.redis.operations.OrangeRedisSetOperations;
import com.langwuyue.orange.redis.template.NoTemplate;

/**
 * Mapping class for Redis Set cross-key operations executors.
 * 
 * <p>This class extends {@link OrangeRedisAbstractExecutorsMapping} to provide
 * specific support for mapping Redis Set operations that work across multiple keys
 * to their corresponding executors. It registers various executors for set operations
 * like union, intersection, difference, and other cross-key operations.
 * 
 * <p>The mapping handles the following cross-key set operations:
 * <ul>
 *   <li>Difference - Set difference operations between multiple sets</li>
 *   <li>DifferenceAndStore - Set difference operations with result storage</li>
 *   <li>Union - Set union operations combining multiple sets</li>
 *   <li>UnionAndStore - Set union operations with result storage</li>
 *   <li>Intersect - Set intersection operations between multiple sets</li>
 *   <li>IntersectAndStore - Set intersection operations with result storage</li>
 *   <li>Move - Moving elements between sets</li>
 * </ul>
 * 
 * <p>This mapping works in conjunction with {@link OrangeRedisSetCrossKeyExecutorIdGenerator}
 * to properly route cross-key set operations to the appropriate executor implementation.
 *
 * @author Liang.Zhong
 * @since 1.0.0
 */
public class OrangeRedisSetCrossKeyExecutorsMapping extends OrangeRedisAbstractExecutorsMapping {
	
	/**
	 * Constructs a new OrangeRedisSetCrossKeyExecutorsMapping instance.
	 * 
	 * <p>This constructor initializes the mapping with all necessary components
	 * for handling Redis Set cross-key operations. It sets up the operations,
	 * generator, listeners, and logger required for proper execution of cross-key
	 * set operations.
	 *
	 * @param operations the Redis Set operations implementation
	 * @param generator the executor ID generator for cross-key set operations
	 * @param listeners collection of listeners for single-key set operations
	 * @param scriptOperations the Redis script operations implementation
	 * @param multipleListeners collection of listeners for multi-key set operations
	 * @param logger the logger instance for Redis operations
	 */
	public OrangeRedisSetCrossKeyExecutorsMapping(
		OrangeRedisSetOperations operations,
		OrangeRedisSetCrossKeyExecutorIdGenerator generator,
		Collection<OrangeRedisSetIfAbsentListener> listeners,
		OrangeRedisScriptOperations scriptOperations,
		Collection<OrangeRedisMultipleSetIfAbsentListener> multipleListeners,
		OrangeRedisLogger logger
	) {
		super(operations,generator,scriptOperations,listeners,multipleListeners,logger);
	}

	/**
	 * Registers all supported Redis Set cross-key operation executors.
	 * 
	 * @param executors the list to which executors will be added
	 * @param operations the Redis operations implementation
	 * @param generator the executor ID generator
	 * @param scriptOperations the Redis script operations implementation
	 * @param listeners collection of listeners for single-key set operations
	 * @param multipleListeners collection of listeners for multi-key set operations
	 * @param logger the logger instance for Redis operations
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
		OrangeRedisSetOperations setOperations = (OrangeRedisSetOperations) operations;
		executors.add(new OrangeDifferenceAndStoreExecutor(setOperations,generator));
		executors.add(new OrangeDifferenceExecutor(setOperations,generator));
		executors.add(new OrangeIntersectAndStoreExecutor(setOperations,generator));
		executors.add(new OrangeIntersectExecutor(setOperations,generator));
		executors.add(new OrangeMoveExecutor(setOperations,generator));
		executors.add(new OrangeUnionAndStoreExecutor(setOperations,generator));
		executors.add(new OrangeUnionExecutor(setOperations,generator));
	}

	/**
	 * Returns the template class used by this executor mapping.
	 * 
	 * <p> This template just helps clarify error messages.
	 *
	 * @return {@link NoTemplate}.class indicating no template is used
	 */
	@Override
	protected Class<?> getTemplateClass() {
		return NoTemplate.class;
	}
}