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
import com.langwuyue.orange.redis.executor.list.OrangeCompareAndSwapExecutor;
import com.langwuyue.orange.redis.executor.list.OrangeCountExecutor;
import com.langwuyue.orange.redis.executor.list.OrangeGetIndexExecutor;
import com.langwuyue.orange.redis.executor.list.OrangeGetIndexsExecutor;
import com.langwuyue.orange.redis.executor.list.OrangeGetMemberExecutor;
import com.langwuyue.orange.redis.executor.list.OrangeGetMembersByRankRangeExecutor;
import com.langwuyue.orange.redis.executor.list.OrangeGetMembersExecutor;
import com.langwuyue.orange.redis.executor.list.OrangeLeftPopMemberExecutor;
import com.langwuyue.orange.redis.executor.list.OrangeLeftPopMemberTimeoutExecutor;
import com.langwuyue.orange.redis.executor.list.OrangeLeftPopMembersExecutor;
import com.langwuyue.orange.redis.executor.list.OrangeLeftPushMemberExecutor;
import com.langwuyue.orange.redis.executor.list.OrangeLeftPushMembersExecutor;
import com.langwuyue.orange.redis.executor.list.OrangePushMemberOnLeftOfPivotExecutor;
import com.langwuyue.orange.redis.executor.list.OrangePushMemberOnRightOfPivotExecutor;
import com.langwuyue.orange.redis.executor.list.OrangeRandomAndDistinctMembersExecutor;
import com.langwuyue.orange.redis.executor.list.OrangeRandomMemberExecutor;
import com.langwuyue.orange.redis.executor.list.OrangeRandomMembersExecutor;
import com.langwuyue.orange.redis.executor.list.OrangeRemoveMemberExecutor;
import com.langwuyue.orange.redis.executor.list.OrangeReverseGetIndexExecutor;
import com.langwuyue.orange.redis.executor.list.OrangeReverseGetIndexsExecutor;
import com.langwuyue.orange.redis.executor.list.OrangeRightPopMemberExecutor;
import com.langwuyue.orange.redis.executor.list.OrangeRightPopMemberTimeoutExecutor;
import com.langwuyue.orange.redis.executor.list.OrangeRightPopMembersExecutor;
import com.langwuyue.orange.redis.executor.list.OrangeRightPushMemberExecutor;
import com.langwuyue.orange.redis.executor.list.OrangeRightPushMembersExecutor;
import com.langwuyue.orange.redis.executor.list.OrangeSetMemberExecutor;
import com.langwuyue.orange.redis.executor.list.OrangeTimeLimitedLeftPopMemberExecutor;
import com.langwuyue.orange.redis.executor.list.OrangeTimeLimitedRightPopMemberExecutor;
import com.langwuyue.orange.redis.executor.list.OrangeTrimMembersByRankRangeExecutor;
import com.langwuyue.orange.redis.executor.list.OrangeTrimMembersExecutor;
import com.langwuyue.orange.redis.listener.OrangeRedisMultipleSetIfAbsentListener;
import com.langwuyue.orange.redis.listener.OrangeRedisSetIfAbsentListener;
import com.langwuyue.orange.redis.logger.OrangeRedisLogger;
import com.langwuyue.orange.redis.operations.OrangeRedisListOperations;
import com.langwuyue.orange.redis.operations.OrangeRedisOperations;
import com.langwuyue.orange.redis.operations.OrangeRedisScriptOperations;
import com.langwuyue.orange.redis.template.list.JSONOperationsTemplate;

/**
 * A specialized executor mapping for Redis List operations.
 * 
 * <p>This class extends {@link OrangeRedisAbstractExecutorsMapping} to provide
 * comprehensive support for Redis List operations. It registers a wide variety
 * of executors that handle different list operations such as pushing, popping,
 * retrieving, and manipulating list elements.
 * 
 * <p>The mapping uses {@link OrangeRedisListExecutorIdGenerator} to generate
 * unique identifiers for each executor based on the operation characteristics.
 * 
 * <p>This class is responsible for:
 * <ul>
 *   <li>Registering all supported Redis List operation executors</li>
 *   <li>Providing access to these executors through the mapping mechanism</li>
 *   <li>Ensuring proper initialization of executors with required dependencies</li>
 *   <li>Defining the template class used for list operations</li>
 * </ul>
 *
 * @author Liang.Zhong
 * @since 1.0.0
 */
public class OrangeRedisListExecutorsMapping extends OrangeRedisAbstractExecutorsMapping {
	
	/**
	 * Constructs a new OrangeRedisListExecutorsMapping with the specified dependencies.
	 * 
	 * <p>This constructor initializes the mapping with all necessary components for
	 * handling Redis List operations. It delegates to the parent class constructor
	 * for basic initialization while providing the specific components needed for
	 * list operations.
	 *
	 * @param operations the Redis List operations interface providing core list functionality
	 * @param generator the executor ID generator specific to list operations
	 * @param listeners collection of listeners for single-key set-if-absent operations
	 * @param scriptOperations the Redis script operations interface for executing Lua scripts
	 * @param multipleListeners collection of listeners for multiple-key set-if-absent operations
	 * @param logger the logger instance for recording operation events and errors
	 */
	public OrangeRedisListExecutorsMapping(
		OrangeRedisListOperations operations,
		OrangeRedisListExecutorIdGenerator generator,
		Collection<OrangeRedisSetIfAbsentListener> listeners,
		OrangeRedisScriptOperations scriptOperations,
		Collection<OrangeRedisMultipleSetIfAbsentListener> multipleListeners,
		OrangeRedisLogger logger
	) {
		super(operations,generator,scriptOperations,listeners,multipleListeners,logger);
	}

	/**
	 * Registers all supported Redis List operation executors.
	 * 
	 * <p>This method extends the base implementation by registering executors specific
	 * to Redis List operations. Each executor is registered with a unique ID
	 * generated by the {@link OrangeRedisListExecutorIdGenerator}.
	 *
	 * @param executors the list to which executors will be added
	 * @param operations the Redis operations interface
	 * @param generator the executor ID generator
	 * @param scriptOperations the Redis script operations interface
	 * @param listeners collection of listeners for single-key set-if-absent operations
	 * @param multipleListeners collection of listeners for multiple-key set-if-absent operations
	 * @param logger the logger instance for recording operation events and errors
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
		super.registerExecutors(executors, operations, generator,scriptOperations,listeners,multipleListeners,logger);
		OrangeRedisListOperations listOperations = (OrangeRedisListOperations) operations;
		
		executors.add(new OrangeCompareAndSwapExecutor(scriptOperations,generator,logger));
		executors.add(new OrangeCountExecutor(listOperations,generator));
		executors.add(new OrangeGetIndexExecutor(listOperations,generator));
		executors.add(new OrangeGetIndexsExecutor(listOperations,generator,logger));
		executors.add(new OrangeGetMemberExecutor(listOperations,generator));
		executors.add(new OrangeGetMembersByRankRangeExecutor(listOperations,generator));
		executors.add(new OrangeGetMembersExecutor(listOperations,generator));
		executors.add(new OrangeLeftPopMemberExecutor(listOperations,generator));
		executors.add(new OrangeLeftPopMembersExecutor(listOperations,generator));
		executors.add(new OrangeLeftPopMemberTimeoutExecutor(listOperations,generator));
		executors.add(new OrangeLeftPushMemberExecutor(listOperations,generator));
		executors.add(new OrangeLeftPushMembersExecutor(listOperations,generator,logger));
		executors.add(new OrangePushMemberOnLeftOfPivotExecutor(listOperations,generator));
		executors.add(new OrangePushMemberOnRightOfPivotExecutor(listOperations,generator));
		executors.add(new OrangeRemoveMemberExecutor(listOperations,generator));
		executors.add(new OrangeReverseGetIndexExecutor(listOperations,generator));
		executors.add(new OrangeReverseGetIndexsExecutor(listOperations,generator,logger));
		executors.add(new OrangeRightPopMemberExecutor(listOperations,generator));
		executors.add(new OrangeRightPopMembersExecutor(listOperations,generator));
		executors.add(new OrangeRightPopMemberTimeoutExecutor(listOperations,generator));
		executors.add(new OrangeRightPushMemberExecutor(listOperations,generator));
		executors.add(new OrangeRightPushMembersExecutor(listOperations,generator,logger));
		executors.add(new OrangeSetMemberExecutor(listOperations,generator));
		executors.add(new OrangeTimeLimitedLeftPopMemberExecutor(listOperations,generator));
		executors.add(new OrangeTimeLimitedRightPopMemberExecutor(listOperations,generator));
		executors.add(new OrangeTrimMembersExecutor(listOperations,generator));
		executors.add(new OrangeTrimMembersByRankRangeExecutor(listOperations,generator));
		executors.add(new OrangeRandomAndDistinctMembersExecutor(listOperations,generator));
		executors.add(new OrangeRandomMemberExecutor(listOperations,generator));
		executors.add(new OrangeRandomMembersExecutor(listOperations,generator));
	}

	/**
	 * Returns the template class used by this executor mapping.
	 * 
	 * <p> This template just helps clarify error messages.
	 *
	 * @return the {@link JSONOperationsTemplate} class
	 */
	@Override
	protected Class<?> getTemplateClass() {
		return JSONOperationsTemplate.class;
	}
}