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
import com.langwuyue.orange.redis.executor.cross.zset.OrangeDifferenceAndStoreExecutor;
import com.langwuyue.orange.redis.executor.cross.zset.OrangeDifferenceExecutor;
import com.langwuyue.orange.redis.executor.cross.zset.OrangeDifferenceWithScoresExecutor;
import com.langwuyue.orange.redis.executor.cross.zset.OrangeIntersectAndAggregateAndStoreExecutor;
import com.langwuyue.orange.redis.executor.cross.zset.OrangeIntersectAndStoreByWeightsExecutor;
import com.langwuyue.orange.redis.executor.cross.zset.OrangeIntersectAndStoreExecutor;
import com.langwuyue.orange.redis.executor.cross.zset.OrangeIntersectExecutor;
import com.langwuyue.orange.redis.executor.cross.zset.OrangeIntersectWithScoresAndAggregateExecutor;
import com.langwuyue.orange.redis.executor.cross.zset.OrangeIntersectWithScoresByWeightsExecutor;
import com.langwuyue.orange.redis.executor.cross.zset.OrangeIntersectWithScoresExecutor;
import com.langwuyue.orange.redis.executor.cross.zset.OrangeUnionAndAggregateAndStoreExecutor;
import com.langwuyue.orange.redis.executor.cross.zset.OrangeUnionAndStoreByWeightsExecutor;
import com.langwuyue.orange.redis.executor.cross.zset.OrangeUnionAndStoreExecutor;
import com.langwuyue.orange.redis.executor.cross.zset.OrangeUnionExecutor;
import com.langwuyue.orange.redis.executor.cross.zset.OrangeUnionWithScoresAndAggregateExecutor;
import com.langwuyue.orange.redis.executor.cross.zset.OrangeUnionWithScoresByWeightsExecutor;
import com.langwuyue.orange.redis.executor.cross.zset.OrangeUnionWithScoresExecutor;
import com.langwuyue.orange.redis.listener.OrangeRedisMultipleSetIfAbsentListener;
import com.langwuyue.orange.redis.listener.OrangeRedisSetIfAbsentListener;
import com.langwuyue.orange.redis.logger.OrangeRedisLogger;
import com.langwuyue.orange.redis.operations.OrangeRedisOperations;
import com.langwuyue.orange.redis.operations.OrangeRedisScriptOperations;
import com.langwuyue.orange.redis.operations.OrangeRedisZSetOperations;
import com.langwuyue.orange.redis.template.NoTemplate;

/**
 * Mapping class for Redis sorted set cross-key operation executors.
 * 
 * <p>This class extends {@link OrangeRedisAbstractExecutorsMapping} to provide
 * executor mapping for Redis sorted set operations that involve multiple keys.
 * It supports the following cross-key operations:
 * <ul>
 *   <li>Difference operations (with/without scores, with/without storage)</li>
 *   <li>Intersection operations (with/without scores, with/without storage)</li> 
 *   <li>Union operations (with/without scores, with/without storage)</li>
 *   <li>Operations with weights and aggregation</li>
 * </ul>
 * 
 * <p>The mapping creates and registers executors for all supported operation
 * variants, ensuring proper operation handling and routing.
 *
 * @author Liang.Zhong
 * @since 1.0.0
 */
public class OrangeRedisZSetCrossKeyExecutorsMapping extends OrangeRedisAbstractExecutorsMapping {
	
	/**
	 * Constructs a new mapping for Redis sorted set cross-key operations.
	 * 
	 * @param operations the Redis sorted set operations instance
	 * @param generator the executor ID generator for cross-key operations
	 * @param listeners collection of set-if-absent listeners
	 * @param scriptOperations Redis script operations instance
	 * @param multipleListeners collection of multiple set-if-absent listeners
	 * @param logger Redis operation logger
	 */
	public OrangeRedisZSetCrossKeyExecutorsMapping(
		OrangeRedisZSetOperations operations,
		OrangeRedisZSetCrossKeyExecutorIdGenerator generator,
		Collection<OrangeRedisSetIfAbsentListener> listeners,
		OrangeRedisScriptOperations scriptOperations,
		Collection<OrangeRedisMultipleSetIfAbsentListener> multipleListeners,
		OrangeRedisLogger logger
	) {
		super(operations,generator,scriptOperations,listeners,multipleListeners,logger);
	}

	/**
	 * Registers all executors for Redis sorted set cross-key operations.
	 * 
	 * <p>This method populates the executors list with implementations for various
	 * sorted set cross-key operations, including:
	 * <ul>
	 *   <li>Difference operations (with/without scores, with/without storage)</li>
	 *   <li>Intersection operations (with/without scores, with/without storage)</li>
	 *   <li>Union operations (with/without scores, with/without storage)</li>
	 *   <li>Operations with weights and aggregation</li>
	 * </ul>
	 * 
	 * @param executors the list to which executors should be added
	 * @param operations the Redis operations instance
	 * @param generator the executor ID generator
	 * @param scriptOperations Redis script operations instance
	 * @param listeners collection of set-if-absent listeners
	 * @param multipleListeners collection of multiple set-if-absent listeners
	 * @param logger Redis operation logger
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
		OrangeRedisZSetOperations zsetOperations = (OrangeRedisZSetOperations) operations;
		executors.add(new OrangeDifferenceAndStoreExecutor(zsetOperations,generator));
		executors.add(new OrangeDifferenceExecutor(zsetOperations,generator));
		executors.add(new OrangeDifferenceWithScoresExecutor(zsetOperations,generator));
		executors.add(new OrangeIntersectAndAggregateAndStoreExecutor(zsetOperations,generator));
		executors.add(new OrangeIntersectAndStoreExecutor(zsetOperations,generator));
		executors.add(new OrangeIntersectExecutor(zsetOperations,generator));
		executors.add(new OrangeIntersectWithScoresAndAggregateExecutor(zsetOperations,generator));
		executors.add(new OrangeIntersectWithScoresExecutor(zsetOperations,generator));
		executors.add(new OrangeUnionAndAggregateAndStoreExecutor(zsetOperations,generator));
		executors.add(new OrangeUnionAndStoreExecutor(zsetOperations,generator));
		executors.add(new OrangeUnionExecutor(zsetOperations,generator));
		executors.add(new OrangeUnionWithScoresAndAggregateExecutor(zsetOperations,generator));
		executors.add(new OrangeUnionWithScoresExecutor(zsetOperations,generator));
		executors.add(new OrangeIntersectAndStoreByWeightsExecutor(zsetOperations,generator));
		executors.add(new OrangeIntersectWithScoresByWeightsExecutor(zsetOperations,generator));
		executors.add(new OrangeUnionAndStoreByWeightsExecutor(zsetOperations,generator));
		executors.add(new OrangeUnionWithScoresByWeightsExecutor(zsetOperations,generator));
	}

	/**
	 * Returns the template class used for JSON serialization/deserialization of sorted set values.
	 * 
	 * <p> This template just helps clarify error messages.
	 * 
	 * @return the template class for JSON operations
	 */
	@Override
	protected Class<?> getTemplateClass() {
		return NoTemplate.class;
	}
}