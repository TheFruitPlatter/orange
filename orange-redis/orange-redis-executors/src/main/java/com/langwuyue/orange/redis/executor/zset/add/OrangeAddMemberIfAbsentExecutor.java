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
import java.util.List;

import com.langwuyue.orange.redis.OrangeRedisIfAbsentException;
import com.langwuyue.orange.redis.annotation.AddMembers;
import com.langwuyue.orange.redis.annotation.IfAbsent;
import com.langwuyue.orange.redis.annotation.RedisValue;
import com.langwuyue.orange.redis.annotation.zset.Score;
import com.langwuyue.orange.redis.context.OrangeRedisContext;
import com.langwuyue.orange.redis.executor.OrangeRedisAbstractExecutor;
import com.langwuyue.orange.redis.executor.zset.context.OrangeAddIfAbsentContext;
import com.langwuyue.orange.redis.executor.zset.context.OrangeScoreIfAbsentContext;
import com.langwuyue.orange.redis.listener.OrangeRedisSetIfAbsentListener;
import com.langwuyue.orange.redis.listener.zset.OrangeAddMemberIfAbsentFailedEvent;
import com.langwuyue.orange.redis.listener.zset.OrangeAddMemberIfAbsentSuccessEvent;
import com.langwuyue.orange.redis.listener.zset.OrangeRemoveMemberFailedEvent;
import com.langwuyue.orange.redis.mapping.OrangeRedisExecutorIdGenerator;
import com.langwuyue.orange.redis.operations.OrangeRedisZSetOperations;
import com.langwuyue.orange.redis.operations.OrangeRedisZSetOperations.ZSetEntry;
import com.langwuyue.orange.redis.utils.OrangeCollectionUtils;

/**
 * Executor implementation for conditionally adding members to ZSet (only if not exists).
 * 
 * <p>Features:
 * <ul>
 *   <li>Adds to Redis sorted set only when member does not exist</li>
 *   <li>Supports configuring member values and scores via annotations</li>
 *   <li>Provides complete listener mechanism for operation result notification</li>
 *   <li>Supports automatic member deletion after operation (for testing scenarios)</li>
 * </ul>
 *
 * @author Liang.Zhong
 * @since 1.0.0
 */
public class OrangeAddMemberIfAbsentExecutor extends OrangeRedisAbstractExecutor {

	private OrangeRedisZSetOperations operations;
	
	private Collection<OrangeRedisSetIfAbsentListener> listeners;
	
	/**
	 * Constructs a ZSet conditional addition executor.
	 *
	 * @param operations Redis ZSet operations interface implementation, must not be null
	 * @param idGenerator Executor ID generator for monitoring and tracing, must not be null
	 * @param listeners Listener collection for operation result notification, can be empty but must not be null
	 */
	public OrangeAddMemberIfAbsentExecutor(
			OrangeRedisZSetOperations operations,
			OrangeRedisExecutorIdGenerator idGenerator,
			Collection<OrangeRedisSetIfAbsentListener> listeners
	) {
		super(idGenerator);
		this.operations = operations;
		this.listeners = listeners;
	}

	/**
	 * Executes conditional member addition operation.
	 *
	 * <p>Implementation details:
	 * <ul>
	 *   <li>Attempts to add member (only if not exists)</li>
	 *   <li>Handles operation result (success/failure)</li>
	 *   <li>Notifies all listeners about operation result</li>
	 *   <li>Decides whether to delete member after operation based on configuration</li>
	 *   <li>Handles deletion failure cases and notifies listeners</li>
	 * </ul>
	 *
	 * @param context Execution context containing Redis key, member and score info
	 * @return No return value (operation results are notified through listeners)
	 * @throws Exception if Redis operation fails or type conversion fails
	 */
	@Override
	public Object execute(OrangeRedisContext context) throws Exception {
		OrangeAddIfAbsentContext ctx = (OrangeAddIfAbsentContext)context;
		ZSetEntry member = ctx.getMember();
		Boolean success = Boolean.FALSE;
		try {
			success = executeIfAbsent(context,member);
			if(success == null || !success.booleanValue()) {
				throw new OrangeRedisIfAbsentException("False returned");
			}
		}catch (Exception e) {
			// The operation may have been interrupted by a client timeout or network error, but it was actually completed successfully.
			listeners.forEach(t -> 
				t.onFailure(
					context.getRedisKey().getOriginalKey(),
					new OrangeAddMemberIfAbsentFailedEvent(
						context.getArgs(),
						member.getValue(),
						member.getScore(), 
						e
					)
				)
			);
		}
		
		notifyListeners(context,member,success);
		
		return null;
	}
	
	/**
	 * Notifies all listeners about operation result.
	 *
	 * <p>Notification includes:
	 * <ul>
	 *   <li>Sends success event when operation succeeds</li>
	 *   <li>Decides whether to delete member after operation based on configuration</li>
	 *   <li>Sends failure event when deletion fails</li>
	 * </ul>
	 *
	 * @param context Execution context
	 * @param member Member to operate on
	 * @param success Whether operation was successful
	 */
	private void notifyListeners(OrangeRedisContext context,ZSetEntry member,Boolean success) {
		OrangeAddIfAbsentContext ctx = (OrangeAddIfAbsentContext)context;
		if(success == null || !(success.booleanValue())) {
			return;
		}
		boolean removeFailed = false;
		Exception removeFailedException = null;
		try {
			listeners.forEach(t -> 
				t.onSuccess(
					context.getRedisKey().getOriginalKey(),
					new OrangeAddMemberIfAbsentSuccessEvent(context.getArgs(),member.getValue(),member.getScore())
				)
			);
		}finally {
			try {
				if(ctx.isDeleteInTheEnd()) {
					Long removed = operations.remove(context.getRedisKey().getValue(), context.getValueType(), member.getValue());	
					removeFailed = removed == null || removed <= 0;
				}
			}catch (Exception e) {
				// Warning, maybe a network error causes the removal to fail.
				// Also, maybe Redis Client timeout, but the operation success.
				removeFailed = true;
				removeFailedException = e;
				
			}
		}
		
		if(!removeFailed) {
			return;
		}
		
		final Exception e = removeFailedException;
		listeners.forEach(t -> 
			t.onRemoveFailed(context.getRedisKey().getOriginalKey(),
				new OrangeRemoveMemberFailedEvent(
					context.getArgs(),
					member.getValue(),
					member.getScore(), 
					e
				)
			)
		);
	}

	/**
	 * Core implementation of conditional member addition operation.
	 *
	 * <p>Features:
	 * <ul>
	 *   <li>Uses Redis ZADD NX option for atomic conditional addition</li>
	 *   <li>Adds to Redis sorted set only when member does not exist</li>
	 *   <li>Returns operation result (whether member was actually added)</li>
	 * </ul>
	 *
	 * @param ctx Execution context containing Redis key and value type info
	 * @param member Member object to add
	 * @return Whether member was successfully added (null indicates operation failure)
	 * @throws Exception if Redis operation fails
	 */
	protected Boolean executeIfAbsent(OrangeRedisContext ctx,ZSetEntry member) throws Exception {
		 return this.operations.addIfAbsent(
			ctx.getRedisKey().getValue(), 
			member, 
			ctx.getValueType()
		);
	}

	/**
	 * Gets the list of annotation types supported by this executor.
	 *
	 * <p>Supported annotation types:
	 * <ul>
	 *   <li>{@link AddMembers} - Marks batch addition operations</li>
	 *   <li>{@link RedisValue} - Specifies member value</li>
	 *   <li>{@link Score} - Specifies member score</li>
	 *   <li>{@link IfAbsent} - Marks conditional addition operations</li>
	 * </ul>
	 *
	 * @return Immutable list of annotation classes
	 */
	@Override
	protected List<Class<? extends Annotation>> getSupportedAnnotationClasses() {
		return OrangeCollectionUtils.asList(AddMembers.class,RedisValue.class,Score.class,IfAbsent.class);
	}

	/**
	 * Gets the context class required by this executor.
	 *
	 * <p>Requires {@link OrangeScoreIfAbsentContext} to provide:
	 * <ul>
	 *   <li>Redis key information</li>
	 *   <li>Value type information</li>
	 *   <li>Conditional addition related configurations</li>
	 *   <li>Member score information</li>
	 * </ul>
	 *
	 * @return Required context class
	 */
	@Override
	public Class<? extends OrangeRedisContext> getContextClass() {
		return OrangeScoreIfAbsentContext.class;
	}

	/**
	 * Gets the Redis ZSet operations interface.
	 *
	 * <p>Used to perform actual Redis operations including:
	 * <ul>
	 *   <li>Conditional member addition</li>
	 *   <li>Member removal</li>
	 *   <li>Other ZSet related operations</li>
	 * </ul>
	 *
	 * @return Redis ZSet operations interface instance
	 */
	OrangeRedisZSetOperations getOperations() {
		return operations;
	}
}