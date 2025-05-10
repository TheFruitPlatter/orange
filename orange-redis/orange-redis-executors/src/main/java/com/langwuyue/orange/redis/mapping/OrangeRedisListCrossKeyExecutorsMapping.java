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
import com.langwuyue.orange.redis.executor.cross.list.OrangeMoveByTimeoutArgExecutor;
import com.langwuyue.orange.redis.executor.cross.list.OrangeMoveExecutor;
import com.langwuyue.orange.redis.executor.cross.list.OrangeTimeLimitedMoveExecutor;
import com.langwuyue.orange.redis.listener.OrangeRedisMultipleSetIfAbsentListener;
import com.langwuyue.orange.redis.listener.OrangeRedisSetIfAbsentListener;
import com.langwuyue.orange.redis.logger.OrangeRedisLogger;
import com.langwuyue.orange.redis.operations.OrangeRedisListOperations;
import com.langwuyue.orange.redis.operations.OrangeRedisOperations;
import com.langwuyue.orange.redis.operations.OrangeRedisScriptOperations;
import com.langwuyue.orange.redis.template.NoTemplate;

/**
 * @author Liang.Zhong
 * @since 1.0.0
 */
public class OrangeRedisListCrossKeyExecutorsMapping extends OrangeRedisAbstractExecutorsMapping {
	
	public OrangeRedisListCrossKeyExecutorsMapping(
		OrangeRedisListOperations operations,
		OrangeRedisListCrossKeyExecutorIdGenerator generator,
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
		OrangeRedisListOperations listOperations = (OrangeRedisListOperations) operations;
		executors.add(new OrangeMoveExecutor(listOperations,generator));
		executors.add(new OrangeMoveByTimeoutArgExecutor(listOperations,generator));
		executors.add(new OrangeTimeLimitedMoveExecutor(listOperations,generator));
	}

	@Override
	protected Class<?> getTemplateClass() {
		return NoTemplate.class;
	}
}
