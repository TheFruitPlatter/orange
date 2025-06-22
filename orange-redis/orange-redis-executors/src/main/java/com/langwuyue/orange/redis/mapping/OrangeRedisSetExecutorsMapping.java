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
 * Mapping class for Redis Set operations executors.
 * 
 * <p>This class extends {@link OrangeRedisAbstractExecutorsMapping} to provide
 * specific support for mapping Redis Set operations to their corresponding executors.
 * It registers various executors for set operations like adding, removing, and querying
 * set members.
 * 
 * <p>The mapping handles the following set operations:
 * <ul>
 *   <li>Adding single or multiple members to a set</li>
 *   <li>Conditional adding of members with if-absent checks</li>
 *   <li>Removing single or multiple members from a set</li>
 *   <li>Retrieving all members or random members from a set</li>
 *   <li>Checking if elements are members of a set</li>
 *   <li>Getting the size of a set</li>
 *   <li>Popping members from a set</li>
 *   <li>Scanning through set members</li>
 *   <li>Compare-and-swap operations on sets</li>
 * </ul>
 * 
 * <p>This mapping works in conjunction with {@link OrangeRedisSetExecutorIdGenerator}
 * to properly route set operations to the appropriate executor implementation.
 *
 * @author Liang.Zhong
 * @since 1.0.0
 */
public class OrangeRedisSetExecutorsMapping extends OrangeRedisAbstractExecutorsMapping {
	
	/**
	 * Constructs a new OrangeRedisSetExecutorsMapping instance.
	 * 
	 * <p>This constructor initializes the mapping with all necessary components
	 * for handling Redis Set operations. It sets up the operations, generator,
	 * listeners, and logger required for proper execution of set operations.
	 *
	 * @param operations the Redis Set operations implementation
	 * @param generator the executor ID generator for set operations
	 * @param listeners collection of listeners for single-key set operations
	 * @param scriptOperations the Redis script operations implementation
	 * @param multipleListeners collection of listeners for multi-key set operations
	 * @param logger the logger instance for Redis operations
	 */
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

	/**
	 * Registers all supported Redis Set operation executors.
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

	/**
	 * Returns the template class used by this executor mapping.
	 * 
	 * <p> This template just helps clarify error messages.
	 *
	 * @return {@link JSONOperationsTemplate}.class indicating JSON templating is used
	 */
	@Override
	protected Class<?> getTemplateClass() {
		return JSONOperationsTemplate.class;
	}
}