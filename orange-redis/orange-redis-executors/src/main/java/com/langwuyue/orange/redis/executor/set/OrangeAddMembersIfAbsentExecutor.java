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
package com.langwuyue.orange.redis.executor.set;

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
import com.langwuyue.orange.redis.executor.set.context.OrangeAddMembersIfAbsentContext;
import com.langwuyue.orange.redis.listener.OrangeRedisMultipleSetIfAbsentListener;
import com.langwuyue.orange.redis.listener.set.OrangRemoveMembersFailedEvent;
import com.langwuyue.orange.redis.listener.set.OrangeAddMembersIfAbsentEvent;
import com.langwuyue.orange.redis.logger.OrangeRedisLogger;
import com.langwuyue.orange.redis.mapping.OrangeRedisExecutorIdGenerator;
import com.langwuyue.orange.redis.operations.OrangeRedisScriptOperations;
import com.langwuyue.orange.redis.operations.OrangeRedisSetOperations;
import com.langwuyue.orange.redis.utils.OrangeCollectionUtils;

/**
 * Executor implementation for conditionally adding multiple members to a Redis Set only if they are absent.
 * 
 * <p>This executor extends the single-member conditional add functionality to handle multiple members.
 * It processes each member individually, tracking success and failure for each operation, and
 * provides comprehensive event notifications through registered listeners.
 * 
 * <p>The executor supports the following features:
 * <ul>
 *   <li>Conditional addition of multiple members to a Redis Set</li>
 *   <li>Configurable behavior to continue or abort on failure</li>
 *   <li>Detailed tracking of successful, failed, and unknown operations</li>
 *   <li>Optional automatic cleanup of added members at the end of processing</li>
 *   <li>Event notifications for operation completion and cleanup failures</li>
 * </ul>
 * 
 * <p>This implementation uses Lua scripts for atomic operations and provides detailed
 * event information to registered listeners.
 *
 * @author Liang.Zhong
 * @since 1.0.0
 * @see OrangeAddMemberIfAbsentExecutor
 * @see OrangeAddMembersIfAbsentContext
 * @see OrangeRedisMultipleSetIfAbsentListener
 * @see <a href="https://orange.langwuyue.com/redis/advanced/set">Orange Redis Set Documentation</a>
 */
public class OrangeAddMembersIfAbsentExecutor extends OrangeAddMemberIfAbsentExecutor {

	private Collection<OrangeRedisMultipleSetIfAbsentListener> listeners;
	
	/**
	 * Constructs a new OrangeAddMembersIfAbsentExecutor with the specified dependencies.
	 * 
	 * <p>This constructor initializes the executor with all required components for
	 * performing conditional add operations on Redis Sets with multiple members.
	 * 
	 * @param scriptOperations the Redis script operations for executing Lua scripts
	 * @param operations the Redis Set operations for performing Set-specific operations
	 * @param idGenerator the generator for creating unique executor IDs
	 * @param listeners a collection of listeners to be notified of operation events
	 * @param logger the logger for recording debug information and trace IDs
	 */
	public OrangeAddMembersIfAbsentExecutor(
		OrangeRedisScriptOperations scriptOperations,
		OrangeRedisSetOperations operations,
		OrangeRedisExecutorIdGenerator idGenerator,
		Collection<OrangeRedisMultipleSetIfAbsentListener> listeners,
		OrangeRedisLogger logger
	) {
		super(scriptOperations,operations,idGenerator,null,logger);
		this.listeners = listeners;
	}

	/**
	 * Executes the conditional add operation for multiple members to a Redis Set.
	 * 
	 * <p>This method processes each member in the context, attempting to add them to the Redis Set
	 * only if they are absent. It tracks successful additions, failures, and unknown states
	 * for each member, and provides comprehensive event notifications through registered listeners.
	 * 
	 * <p>The method supports configurable behavior to continue or abort on failure, and can
	 * optionally clean up added members at the end of processing.
	 *
	 * @param context the execution context containing the Redis key, members to add, and operation parameters
	 * @return null, as the result is tracked through the context and event notifications
	 * @throws Exception if an error occurs during execution
	 */
	@Override
	public Object execute(OrangeRedisContext context) throws Exception {
		OrangeAddMembersIfAbsentContext ctx = (OrangeAddMembersIfAbsentContext)context;
		Set<Object> successEntries = new LinkedHashSet<>();
		Set<Object> successMembers = new LinkedHashSet<>();
		Map<Object,Exception> failedEntries = new LinkedHashMap<>();
		Set<Object> unknownEnties = new LinkedHashSet<>();
		boolean continueOnFailure = ctx.continueOnFailure();
		ctx.forEach((t,o) -> {
			if(!continueOnFailure && !failedEntries.isEmpty()) {
				unknownEnties.add(o);
				return;
			}
			try {
				Boolean success = executeIfAbsent(ctx,t);
				if(success != null && success.booleanValue()) {
					successEntries.add(o);
					successMembers.add(t);
				}else{
					throw new OrangeRedisIfAbsentException("False returned");
				}
			}catch (Exception e) {
				// The operation may have been interrupted by a client timeout or network error, but it was actually completed successfully.
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
		}finally {
			removeMembers(ctx,successEntries,successMembers,failedEntries,unknownEnties);
		}
		return null;
	}
	
	/**
	 * Removes successfully added members from the Redis Set if cleanup is requested.
	 * 
	 * <p>This method is called after the add operations complete to clean up any members
	 * that were successfully added, if the context specifies deletion at the end.
	 * If removal fails or is incomplete, it notifies listeners with detailed failure information.
	 *
	 * @param ctx the context containing operation parameters and Redis key
	 * @param successEntries the set of original entries that were successfully added
	 * @param successMembers the set of processed members that were successfully added
	 * @param failedEntries a map of entries to their failure exceptions
	 * @param unknownEnties the set of entries whose status is unknown due to errors
	 */
	private void removeMembers(
		OrangeAddMembersIfAbsentContext ctx,
		Set<Object> successEntries,
		Set<Object> successMembers,
		Map<Object,Exception> failedEntries,
		Set<Object> unknownEnties
	) {
		if(!ctx.isDeleteInTheEnd() || successMembers.isEmpty()) {
			return;
		}
		Long removed = null;
		try {
			removed = getOperations().remove(
				ctx.getRedisKey().getValue(), 
				ctx.getValueType(), 
				successMembers.toArray()
			);	
			if(removed == null || removed < successMembers.size()) {
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
						successMembers.size(),
						e
					)
				)
			);
		}
	}
	
	/**
	 * Returns the list of annotation classes supported by this executor.
	 * 
	 * <p>This executor supports multiple annotations for configuring the add operation:
	 * <ul>
	 *   <li>{@link AddMembers} - Indicates multiple members should be added</li>
	 *   <li>{@link Multiple} - Marks the operation as handling multiple items</li>
	 *   <li>{@link ContinueOnFailure} - Controls behavior when failures occur</li>
	 *   <li>{@link IfAbsent} - Specifies the conditional nature of the add operation</li>
	 * </ul>
	 *
	 * @return a list of supported annotation classes
	 */
	@Override
	protected List<Class<? extends Annotation>> getSupportedAnnotationClasses() {
		return OrangeCollectionUtils.asList(AddMembers.class,Multiple.class,ContinueOnFailure.class,IfAbsent.class);
	}

	/**
	 * Returns the context class used by this executor.
	 * 
	 * <p>This executor uses the {@link OrangeAddMembersIfAbsentContext} class to
	 * store and manage operation parameters and results for multiple member additions.
	 *
	 * @return the {@link OrangeAddMembersIfAbsentContext} class
	 */
	@Override
	public Class<? extends OrangeRedisContext> getContextClass() {
		return OrangeAddMembersIfAbsentContext.class;
	}

}