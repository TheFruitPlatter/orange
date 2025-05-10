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
 * @author Liang.Zhong
 * @since 1.0.0
 */
public class OrangeRedisSetCrossKeyExecutorsMapping extends OrangeRedisAbstractExecutorsMapping {
	
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

	@Override
	protected Class<?> getTemplateClass() {
		return NoTemplate.class;
	}
}
