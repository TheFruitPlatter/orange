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
import com.langwuyue.orange.redis.executor.geo.OrangeAddByMemberAnnotationExecutor;
import com.langwuyue.orange.redis.executor.geo.OrangeAddMemberExecutor;
import com.langwuyue.orange.redis.executor.geo.OrangeAddMembersExecutor;
import com.langwuyue.orange.redis.executor.geo.OrangeDistanceExecutor;
import com.langwuyue.orange.redis.executor.geo.OrangeGetMemberExecutor;
import com.langwuyue.orange.redis.executor.geo.OrangeGetMembersExecutor;
import com.langwuyue.orange.redis.executor.geo.OrangeGetMembersInBoxByExistsMemberExecutor;
import com.langwuyue.orange.redis.executor.geo.OrangeGetMembersInBoxExecutor;
import com.langwuyue.orange.redis.executor.geo.OrangeGetMembersInCricleExecutor;
import com.langwuyue.orange.redis.executor.geo.OrangeGetMembersInRadiusExecutor;
import com.langwuyue.orange.redis.executor.geo.OrangeLimitedGetMembersInBoxByExistsMemberExecutor;
import com.langwuyue.orange.redis.executor.geo.OrangeLimitedGetMembersInBoxExecutor;
import com.langwuyue.orange.redis.executor.geo.OrangeLimitedGetMembersInCricleExecutor;
import com.langwuyue.orange.redis.executor.geo.OrangeLimitedGetMembersInRadiusExecutor;
import com.langwuyue.orange.redis.executor.geo.OrangeRemoveMemberExecutor;
import com.langwuyue.orange.redis.executor.geo.OrangeRemoveMembersExecutor;
import com.langwuyue.orange.redis.listener.OrangeRedisMultipleSetIfAbsentListener;
import com.langwuyue.orange.redis.listener.OrangeRedisSetIfAbsentListener;
import com.langwuyue.orange.redis.logger.OrangeRedisLogger;
import com.langwuyue.orange.redis.operations.OrangeRedisGeoOperations;
import com.langwuyue.orange.redis.operations.OrangeRedisOperations;
import com.langwuyue.orange.redis.operations.OrangeRedisScriptOperations;
import com.langwuyue.orange.redis.template.geo.JSONOperationsTemplate;

/**
 * @author Liang.Zhong
 * @since 1.0.0
 */
public class OrangeRedisGeoExecutorsMapping extends OrangeRedisAbstractExecutorsMapping {
	
	public OrangeRedisGeoExecutorsMapping(
		OrangeRedisGeoOperations operations,
		OrangeRedisGeoExecutorIdGenerator generator,
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
		OrangeRedisGeoOperations hashOperations = (OrangeRedisGeoOperations) operations;
		
		executors.add(new OrangeAddByMemberAnnotationExecutor(hashOperations,generator));
		executors.add(new OrangeAddMemberExecutor(hashOperations,generator));
		executors.add(new OrangeAddMembersExecutor(hashOperations,generator,logger));
		executors.add(new OrangeDistanceExecutor(hashOperations,generator));
		executors.add(new OrangeGetMemberExecutor(hashOperations,generator));
		executors.add(new OrangeGetMembersExecutor(hashOperations,generator));
		executors.add(new OrangeGetMembersInRadiusExecutor(hashOperations,generator));
		executors.add(new OrangeRemoveMemberExecutor(hashOperations,generator));
		executors.add(new OrangeRemoveMembersExecutor(hashOperations,generator,logger));
		executors.add(new OrangeLimitedGetMembersInRadiusExecutor(hashOperations,generator));
		executors.add(new OrangeGetMembersInCricleExecutor(hashOperations,generator));
		executors.add(new OrangeLimitedGetMembersInCricleExecutor(hashOperations,generator));
		executors.add(new OrangeGetMembersInBoxByExistsMemberExecutor(hashOperations,generator));
		executors.add(new OrangeGetMembersInBoxExecutor(hashOperations,generator));
		executors.add(new OrangeLimitedGetMembersInBoxByExistsMemberExecutor(hashOperations,generator));
		executors.add(new OrangeLimitedGetMembersInBoxExecutor(hashOperations,generator));
	}

	@Override
	protected Class<?> getTemplateClass() {
		return JSONOperationsTemplate.class;
	}
}
