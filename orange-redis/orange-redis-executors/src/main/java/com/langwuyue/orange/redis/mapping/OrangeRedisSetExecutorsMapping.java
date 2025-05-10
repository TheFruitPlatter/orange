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
import com.langwuyue.orange.redis.executor.set.OrangeAddMemberExecutor;
import com.langwuyue.orange.redis.executor.set.OrangeAddMemberIfAbsentExecutor;
import com.langwuyue.orange.redis.executor.set.OrangeAddMembersExecutor;
import com.langwuyue.orange.redis.executor.set.OrangeAddMembersIfAbsentExecutor;
import com.langwuyue.orange.redis.executor.set.OrangeCompareAndSwapExecutor;
import com.langwuyue.orange.redis.executor.set.OrangeDistinctRandomGetMembersExecutor;
import com.langwuyue.orange.redis.executor.set.OrangeGetMembersExecutor;
import com.langwuyue.orange.redis.executor.set.OrangeGetSizeExecutor;
import com.langwuyue.orange.redis.executor.set.OrangeIsMemberExecutor;
import com.langwuyue.orange.redis.executor.set.OrangeIsMembersExecutor;
import com.langwuyue.orange.redis.executor.set.OrangePopMemberExecutor;
import com.langwuyue.orange.redis.executor.set.OrangePopMembersExecutor;
import com.langwuyue.orange.redis.executor.set.OrangeRandomGetMemberExecutor;
import com.langwuyue.orange.redis.executor.set.OrangeRandomGetMembersExecutor;
import com.langwuyue.orange.redis.executor.set.OrangeRemoveMemberExecutor;
import com.langwuyue.orange.redis.executor.set.OrangeRemoveMembersExecutor;
import com.langwuyue.orange.redis.executor.set.OrangeScanMembersExecutor;
import com.langwuyue.orange.redis.listener.OrangeRedisMultipleSetIfAbsentListener;
import com.langwuyue.orange.redis.listener.OrangeRedisSetIfAbsentListener;
import com.langwuyue.orange.redis.logger.OrangeRedisLogger;
import com.langwuyue.orange.redis.operations.OrangeRedisOperations;
import com.langwuyue.orange.redis.operations.OrangeRedisScriptOperations;
import com.langwuyue.orange.redis.operations.OrangeRedisSetOperations;
import com.langwuyue.orange.redis.template.set.JSONOperationsTemplate;

/**
 * @author Liang.Zhong
 * @since 1.0.0
 */
public class OrangeRedisSetExecutorsMapping extends OrangeRedisAbstractExecutorsMapping {
	
	public OrangeRedisSetExecutorsMapping(
		OrangeRedisSetOperations operations,
		OrangeRedisSetExecutorIdGenerator generator,
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
		OrangeRedisSetOperations setOperations = (OrangeRedisSetOperations) operations;
		executors.add(new OrangeAddMemberExecutor(setOperations,generator));
		executors.add(new OrangeAddMemberIfAbsentExecutor(scriptOperations,setOperations,generator,listeners,logger));
		executors.add(new OrangeAddMembersExecutor(setOperations,generator,logger));
		executors.add(new OrangeAddMembersIfAbsentExecutor(scriptOperations,setOperations,generator,multipleListeners,logger));
		executors.add(new OrangeDistinctRandomGetMembersExecutor(setOperations,generator));
		executors.add(new OrangeGetMembersExecutor(setOperations,generator));
		executors.add(new OrangeGetSizeExecutor(setOperations,generator));
		executors.add(new OrangeIsMemberExecutor(setOperations,generator));
		executors.add(new OrangeIsMembersExecutor(setOperations,generator));
		executors.add(new OrangePopMemberExecutor(setOperations,generator));
		executors.add(new OrangePopMembersExecutor(setOperations,generator));
		executors.add(new OrangeRandomGetMemberExecutor(setOperations,generator));
		executors.add(new OrangeRandomGetMembersExecutor(setOperations,generator));
		executors.add(new OrangeRemoveMemberExecutor(setOperations,generator));
		executors.add(new OrangeRemoveMembersExecutor(setOperations,generator,logger));
		executors.add(new OrangeCompareAndSwapExecutor(scriptOperations,generator,logger));
		executors.add(new OrangeScanMembersExecutor(setOperations,generator));
	}

	@Override
	protected Class<?> getTemplateClass() {
		return JSONOperationsTemplate.class;
	}
}
