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
import com.langwuyue.orange.redis.executor.hash.OrangeAdIfAbsentByMemberAnnotationExecutor;
import com.langwuyue.orange.redis.executor.hash.OrangeAddByMemberAnnotationExecutor;
import com.langwuyue.orange.redis.executor.hash.OrangeAddMemberExecutor;
import com.langwuyue.orange.redis.executor.hash.OrangeAddMemberIfAbsentExecutor;
import com.langwuyue.orange.redis.executor.hash.OrangeAddMembersExecutor;
import com.langwuyue.orange.redis.executor.hash.OrangeAddMembersIfAbsentExecutor;
import com.langwuyue.orange.redis.executor.hash.OrangeCompareAndSwapExecutor;
import com.langwuyue.orange.redis.executor.hash.OrangeCountExecutor;
import com.langwuyue.orange.redis.executor.hash.OrangeDecrementByDeltalExecutor;
import com.langwuyue.orange.redis.executor.hash.OrangeDecrementExecutor;
import com.langwuyue.orange.redis.executor.hash.OrangeGetAllValuesExecutor;
import com.langwuyue.orange.redis.executor.hash.OrangeGetHashValueBytesLengthExecutor;
import com.langwuyue.orange.redis.executor.hash.OrangeGetKeysExecutor;
import com.langwuyue.orange.redis.executor.hash.OrangeGetMembersExecutor;
import com.langwuyue.orange.redis.executor.hash.OrangeGetValueExecutor;
import com.langwuyue.orange.redis.executor.hash.OrangeGetValuesExecutor;
import com.langwuyue.orange.redis.executor.hash.OrangeHasKeyExecutor;
import com.langwuyue.orange.redis.executor.hash.OrangeHasKeysExecutor;
import com.langwuyue.orange.redis.executor.hash.OrangeIncrementByDeltalExecutor;
import com.langwuyue.orange.redis.executor.hash.OrangeIncrementExecutor;
import com.langwuyue.orange.redis.executor.hash.OrangeRandomAndDistinctKeysExecutor;
import com.langwuyue.orange.redis.executor.hash.OrangeRandomAndDistinctMembersExecutor;
import com.langwuyue.orange.redis.executor.hash.OrangeRandomKeyExecutor;
import com.langwuyue.orange.redis.executor.hash.OrangeRandomKeysExecutor;
import com.langwuyue.orange.redis.executor.hash.OrangeRandomMemberExecutor;
import com.langwuyue.orange.redis.executor.hash.OrangeRandomMembersExecutor;
import com.langwuyue.orange.redis.executor.hash.OrangeRemoveMemberExecutor;
import com.langwuyue.orange.redis.executor.hash.OrangeRemoveMembersExecutor;
import com.langwuyue.orange.redis.listener.OrangeRedisMultipleSetIfAbsentListener;
import com.langwuyue.orange.redis.listener.OrangeRedisSetIfAbsentListener;
import com.langwuyue.orange.redis.logger.OrangeRedisLogger;
import com.langwuyue.orange.redis.operations.OrangeRedisHashOperations;
import com.langwuyue.orange.redis.operations.OrangeRedisOperations;
import com.langwuyue.orange.redis.operations.OrangeRedisScriptOperations;
import com.langwuyue.orange.redis.template.hash.JSONOperationsTemplate;

/**
 * @author Liang.Zhong
 * @since 1.0.0
 */
public class OrangeRedisHashExecutorsMapping extends OrangeRedisAbstractExecutorsMapping {
	
	public OrangeRedisHashExecutorsMapping(
		OrangeRedisHashOperations operations,
		OrangeRedisHashExecutorIdGenerator generator,
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
		super.registerExecutors(executors, operations, generator,scriptOperations,listeners,multipleListeners,logger);
		OrangeRedisHashOperations hashOperations = (OrangeRedisHashOperations) operations;
		
		executors.add(new OrangeAddByMemberAnnotationExecutor(hashOperations,generator));
		executors.add(new OrangeAddMemberExecutor(hashOperations,generator));
		executors.add(new OrangeAddMemberIfAbsentExecutor(hashOperations,generator,listeners));
		//executors.add(new OrangeAddMemberIfAbsentWithExpirationExecutor(hashOperations,generator));
		executors.add(new OrangeAddMembersExecutor(hashOperations,generator,logger));
		executors.add(new OrangeAddMembersIfAbsentExecutor(hashOperations,generator,multipleListeners));
		//executors.add(new OrangeAddMembersIfAbsentWithExpirationExecutor(hashOperations,generator));
		executors.add(new OrangeCompareAndSwapExecutor(scriptOperations,generator,logger));
		executors.add(new OrangeCountExecutor(hashOperations,generator));
		executors.add(new OrangeDecrementByDeltalExecutor(hashOperations,generator));
		executors.add(new OrangeDecrementExecutor(hashOperations,generator));
		executors.add(new OrangeGetHashValueBytesLengthExecutor(hashOperations,generator));
		executors.add(new OrangeGetKeysExecutor(hashOperations,generator));
		executors.add(new OrangeGetMembersExecutor(hashOperations,generator));
		executors.add(new OrangeGetValueExecutor(hashOperations,generator));
		executors.add(new OrangeGetValuesExecutor(hashOperations,generator));
		executors.add(new OrangeHasKeyExecutor(hashOperations,generator));
		executors.add(new OrangeHasKeysExecutor(hashOperations,generator,logger));
		executors.add(new OrangeIncrementByDeltalExecutor(hashOperations,generator));
		executors.add(new OrangeIncrementExecutor(hashOperations,generator));
		executors.add(new OrangeRandomKeyExecutor(hashOperations,generator));
		executors.add(new OrangeRandomKeysExecutor(hashOperations,generator));
		executors.add(new OrangeRandomMemberExecutor(hashOperations,generator));
		executors.add(new OrangeRandomMembersExecutor(hashOperations,generator));
		executors.add(new OrangeRemoveMemberExecutor(hashOperations,generator));
		executors.add(new OrangeRemoveMembersExecutor(hashOperations,generator,logger));
		executors.add(new OrangeGetAllValuesExecutor(hashOperations,generator));
		executors.add(new OrangeRandomAndDistinctMembersExecutor(hashOperations,generator));
		executors.add(new OrangeAdIfAbsentByMemberAnnotationExecutor(hashOperations,generator,listeners));
		executors.add(new OrangeRandomAndDistinctKeysExecutor(hashOperations,generator));
	}

	@Override
	protected Class<?> getTemplateClass() {
		return JSONOperationsTemplate.class;
	}
}
