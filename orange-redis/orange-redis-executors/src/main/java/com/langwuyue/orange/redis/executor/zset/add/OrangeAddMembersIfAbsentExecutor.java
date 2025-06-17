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
package com.langwuyue.orange.redis.executor.zset.add;

import java.lang.annotation.Annotation;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.langwuyue.orange.redis.OrangeRedisIfAbsentException;
import com.langwuyue.orange.redis.annotation.AddMembers;
import com.langwuyue.orange.redis.annotation.ContinueOnFailure;
import com.langwuyue.orange.redis.annotation.IfAbsent;
import com.langwuyue.orange.redis.annotation.Multiple;
import com.langwuyue.orange.redis.context.OrangeRedisContext;
import com.langwuyue.orange.redis.executor.OrangeRedisAbstractExecutor;
import com.langwuyue.orange.redis.executor.zset.context.OrangeAddMembersContext;
import com.langwuyue.orange.redis.executor.zset.context.OrangeAddMembersIfAbsentContext;
import com.langwuyue.orange.redis.listener.OrangeRedisMultipleSetIfAbsentListener;
import com.langwuyue.orange.redis.listener.zset.OrangRemoveMembersFailedEvent;
import com.langwuyue.orange.redis.listener.zset.OrangeAddMembersIfAbsentEvent;
import com.langwuyue.orange.redis.mapping.OrangeRedisExecutorIdGenerator;
import com.langwuyue.orange.redis.operations.OrangeRedisZSetOperations;
import com.langwuyue.orange.redis.operations.OrangeRedisZSetOperations.ZSetEntry;
import com.langwuyue.orange.redis.utils.OrangeCollectionUtils;

/**
 * Executor for conditionally adding multiple members to a Redis ZSet only if they don't already exist.
 * 
 * <p>Features:
 * <ul>
 *   <li>Batch addition of multiple members with conditional logic</li>
 *   <li>Supports continuation on failure through {@link ContinueOnFailure} annotation</li>
 *   <li>Provides detailed success/failure tracking for each member</li>
 *   <li>Supports optional automatic cleanup of added members</li>
 *   <li>Notifies registered listeners about operation results</li>
 * </ul>
 * 
 * <p>This executor processes {@link AddMembers} operations with {@link IfAbsent} 
 * and {@link Multiple} annotations, ensuring members are only added when they don't
 * already exist in the target ZSet.
 *
 * @author Liang.Zhong
 * @since 1.0.0
 * @see <a href="https://orange.langwuyue.com/redis/advanced/zset">Orange Redis ZSet Documentation</a>
 */
public class OrangeAddMembersIfAbsentExecutor extends OrangeRedisAbstractExecutor {

	private OrangeRedisZSetOperations operations;
	
	private Collection<OrangeRedisMultipleSetIfAbsentListener> listeners;
	
	/**
	 * Constructs a new executor instance for handling conditional batch member additions to Redis ZSets.
	 *
	 * @param operations Redis ZSet operations implementation for performing ZSet-specific operations
	 * @param idGenerator Generator for creating unique executor IDs
	 * @param listeners Collection of listeners to be notified about operation results
	 */
	public OrangeAddMembersIfAbsentExecutor(
		OrangeRedisZSetOperations operations,
		OrangeRedisExecutorIdGenerator idGenerator,
		Collection<OrangeRedisMultipleSetIfAbsentListener> listeners
	) {
		super(idGenerator);
		this.operations = operations;
		this.listeners = listeners;
	}

	/**
	 * Executes the conditional batch addition operation for multiple ZSet members.
	 * 
	 * <p>This method:
	 * <ul>
	 *   <li>Processes each entry in the context</li>
	 *   <li>Tracks successful, failed, and unknown status entries</li>
	 *   <li>Respects the continueOnFailure flag to determine behavior on errors</li>
	 *   <li>Notifies registered listeners about operation results</li>
	 *   <li>Handles cleanup of added members if configured</li>
	 * </ul>
	 *
	 * @param context Execution context containing Redis key and members to add
	 * @return null after processing all entries and notifying listeners
	 * @throws Exception if Redis operation fails
	 */
	@Override
	public Object execute(OrangeRedisContext context) throws Exception {
		OrangeAddMembersIfAbsentContext ctx = (OrangeAddMembersIfAbsentContext)context;
		Set<Object> successEntries = new LinkedHashSet<>();
		Set<Object> successValues = new LinkedHashSet<>();
		Map<Object,Exception> failedEntries = new LinkedHashMap<>();
		Set<Object> unknownEnties = new LinkedHashSet<>();
		boolean continueOnFailure = ctx.continueOnFailure();
		ctx.forEach((t,o) -> {
			if(!continueOnFailure && !failedEntries.isEmpty()) {
				unknownEnties.add(o);
				return;
			}
			try {
				ZSetEntry entry = (ZSetEntry)t;
				Boolean success = executeIfAbsent(ctx,entry);
				if(success == null || !success.booleanValue()) {
					throw new OrangeRedisIfAbsentException("False returned");
				}
				successEntries.add(o);
				successValues.add(entry.getValue());
			}catch (Exception e) {
				// The operation may have been interrupted by a client timeout or network error, 
				// but it was actually completed successfully.
				if(!(e instanceof OrangeRedisIfAbsentException)) {
					unknownEnties.add(o);
				}
				failedEntries.put(o, e);
			}
		});
		if(successEntries.isEmpty() && unknownEnties.isEmpty() && failedEntries.isEmpty()) {
			return null;
		}
		try {
			this.listeners.forEach(t -> 
				t.onCompleted(
					context.getRedisKey().getOriginalKey(),
					new OrangeAddMembersIfAbsentEvent(
						ctx.getArgs(),
						successEntries,
						failedEntries,
						unknownEnties
					)
				)
			);
		}finally{
			removeMembers(ctx,successEntries,successValues,failedEntries,unknownEnties);
		}
		
		return null;
	}
	
	/**
	 * Removes members from the ZSet based on the operation context and results.
	 * 
	 * <p>This cleanup method is called when:
	 * <ul>
	 *   <li>The context has deleteInTheEnd flag set to true</li>
	 *   <li>There are successfully added members to remove</li>
	 * </ul>
	 * 
	 * <p>If the removal operation fails or removes fewer members than expected:
	 * <ul>
	 *   <li>Throws OrangeRedisIfAbsentException for incomplete removals</li>
	 *   <li>Notifies listeners about the failure through OrangRemoveMembersFailedEvent</li>
	 *   <li>Includes detailed information about successful, failed, and unknown entries</li>
	 * </ul>
	 *
	 * @param ctx The operation context containing configuration and Redis key
	 * @param successEntries Set of entries that were successfully added
	 * @param successValues Set of values that were successfully added
	 * @param failedEntries Map of entries that failed with their exceptions
	 * @param unknownEnties Set of entries with unknown status
	 */
	private void removeMembers(
		OrangeAddMembersIfAbsentContext ctx,
		Set<Object> successEntries,
		Set<Object> successValues,
		Map<Object,Exception> failedEntries,
		Set<Object> unknownEnties
	) {
		if(!ctx.isDeleteInTheEnd() || successEntries.isEmpty()) {
			return;
		}
		Long removed = null;
		try {
			removed = operations.remove(
				ctx.getRedisKey().getValue(), 
				ctx.getValueType(), 
				successValues.toArray()
			);
			if(removed == null || removed.longValue() < successEntries.size()) {
				throw new OrangeRedisIfAbsentException("The number of members removed was below expectations.");
			}
		}catch (Exception e) {
			// Warning, maybe a network error causes the removal to fail.
			// Also, maybe Redis Client timeout, but the operation success.
			final Long removeCount = removed;
			this.listeners.forEach(t -> 
				t.onCompleted(
					ctx.getRedisKey().getOriginalKey(),
					new OrangRemoveMembersFailedEvent(
						ctx.getArgs(),
						successEntries,
						failedEntries,
						unknownEnties,
						removeCount,
						successEntries.size(),
						e
					)
				)
			);
		}
	}
	
	/**
	 * Executes the core conditional addition logic for a single ZSet entry.
	 * 
	 * <p>This method attempts to add a single entry to the ZSet only if it doesn't
	 * already exist. It delegates the actual operation to the underlying Redis
	 * ZSet operations implementation.
	 *
	 * @param ctx The context containing Redis key and operation parameters
	 * @param entry The ZSet entry to be added, containing both value and score
	 * @return Boolean indicating whether the addition was successful (true) or the member already existed (false)
	 * @throws Exception if the Redis operation fails
	 */
	protected Boolean executeIfAbsent(OrangeAddMembersContext ctx,ZSetEntry entry) throws Exception {
		 return this.operations.addIfAbsent(ctx.getRedisKey().getValue(), entry, ctx.getValueType());
	}

	/**
	 * Returns the list of annotation classes supported by this executor.
	 * 
	 * <p>Supported annotations:
	 * <ul>
	 *   <li>{@link AddMembers} - Indicates a ZSet member addition operation</li>
	 *   <li>{@link Multiple} - Enables batch processing of multiple members</li>
	 *   <li>{@link ContinueOnFailure} - Controls behavior when operations fail</li>
	 *   <li>{@link IfAbsent} - Specifies conditional addition based on existence</li>
	 * </ul>
	 *
	 * @return List of supported annotation classes that this executor can process
	 */
	@Override
	protected List<Class<? extends Annotation>> getSupportedAnnotationClasses() {
		return OrangeCollectionUtils.asList(AddMembers.class,Multiple.class,ContinueOnFailure.class,IfAbsent.class);
	}

	/**
	 * Returns the context class used by this executor.
	 * 
	 * <p>{@link OrangeAddMembersIfAbsentContext} provides:
	 * <ul>
	 *   <li>Access to Redis key and operation parameters</li>
	 *   <li>Configuration for conditional addition behavior</li>
	 *   <li>Settings for cleanup and error handling</li>
	 *   <li>Support for batch processing of multiple members</li>
	 * </ul>
	 *
	 * @return The class of context object required for this executor's operations
	 */
	@Override
	public Class<? extends OrangeRedisContext> getContextClass() {
		return OrangeAddMembersIfAbsentContext.class;
	}

	/**
	 * Package-private accessor for the Redis ZSet operations.
	 * 
	 * <p>Provides access to the underlying Redis ZSet operations implementation.
	 * This method is primarily used for testing and internal access within the
	 * same package.
	 *
	 * @return The Redis ZSet operations instance used by this executor
	 */
	OrangeRedisZSetOperations getOperations() {
		return operations;
	}
}