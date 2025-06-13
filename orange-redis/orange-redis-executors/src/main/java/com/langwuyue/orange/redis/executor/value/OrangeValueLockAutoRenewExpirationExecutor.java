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
package com.langwuyue.orange.redis.executor.value;

import java.lang.annotation.Annotation;
import java.util.Collection;
import java.util.List;

import com.langwuyue.orange.redis.OrangeRedisIfAbsentException;
import com.langwuyue.orange.redis.RedisValueTypeEnum;
import com.langwuyue.orange.redis.annotation.AutoRenew;
import com.langwuyue.orange.redis.annotation.global.SetExpiration;
import com.langwuyue.orange.redis.annotation.value.Lock;
import com.langwuyue.orange.redis.context.OrangeRedisContext;
import com.langwuyue.orange.redis.context.OrangeRedisContext.Key;
import com.langwuyue.orange.redis.context.OrangeRedisValueContext;
import com.langwuyue.orange.redis.executor.multiplelocks.OrangeExpirationTimeAutoInitializer;
import com.langwuyue.orange.redis.executor.value.context.OrangeValueLockAutoRenewContext;
import com.langwuyue.orange.redis.listener.OrangeRedisSetIfAbsentListener;
import com.langwuyue.orange.redis.logger.OrangeRedisLogger;
import com.langwuyue.orange.redis.mapping.OrangeRedisExecutorIdGenerator;
import com.langwuyue.orange.redis.operations.OrangeRedisValueOperations;
import com.langwuyue.orange.redis.timer.OrangeRenewTimerWheel;
import com.langwuyue.orange.redis.timer.OrangeValueLockRenewTask;
import com.langwuyue.orange.redis.utils.OrangeCollectionUtils;

/**
 * An executor that implements Redis distributed lock with automatic renewal functionality.
 * 
 * <p>This executor extends {@link OrangeSetIfAbsentExecutor} to provide distributed locking
 * capabilities with the following features:</p>
 * <ul>
 *   <li>Automatic lock renewal using a timer wheel mechanism</li>
 *   <li>Configurable expiration time initialization</li>
 *   <li>Support for lock value tracing</li>
 *   <li>Integration with lock acquisition listeners</li>
 * </ul>
 * 
 * <p>The executor supports the following annotations:</p>
 * <ul>
 *   <li>{@link Lock} - For basic locking functionality</li>
 *   <li>{@link AutoRenew} - For configuring automatic lock renewal</li>
 *   <li>{@link SetExpiration} - For setting lock expiration time</li>
 * </ul>
 *
 * @author Liang.Zhong
 * @since 1.0.0
 */
public class OrangeValueLockAutoRenewExpirationExecutor extends OrangeSetIfAbsentExecutor {
	
	/**
	 * Timer wheel used for scheduling lock renewal tasks.
	 * This component manages the periodic renewal of locks to prevent expiration.
	 */
	private OrangeRenewTimerWheel renewTimerWheel;
	
	/**
	 * Initializer for automatically setting appropriate expiration times for locks.
	 * This component calculates and sets the expiration time based on configuration.
	 */
	private OrangeExpirationTimeAutoInitializer expirationTimeAutoInitializer;
	
	/**
	 * Logger for recording operations and providing trace IDs for lock values.
	 * The trace ID from this logger is used as the lock value when available.
	 */
	private OrangeRedisLogger logger;

	/**
	 * Constructs a new OrangeValueLockAutoRenewExpirationExecutor with the specified components.
	 *
	 * @param operations The Redis value operations for interacting with Redis
	 * @param idGenerator The generator for creating unique executor IDs
	 * @param listeners Collection of listeners to be notified of lock acquisition events
	 * @param renewTimerWheel The timer wheel for scheduling lock renewal tasks
	 * @param expirationTimeAutoInitializer The initializer for setting appropriate expiration times
	 * @param logger The logger for recording operations and providing trace IDs
	 */
	public OrangeValueLockAutoRenewExpirationExecutor(
		OrangeRedisValueOperations operations,
		OrangeRedisExecutorIdGenerator idGenerator,
		Collection<OrangeRedisSetIfAbsentListener> listeners,
		OrangeRenewTimerWheel renewTimerWheel,
		OrangeExpirationTimeAutoInitializer expirationTimeAutoInitializer,
		OrangeRedisLogger logger
	) {
		super(operations, idGenerator, listeners);
		this.renewTimerWheel = renewTimerWheel;
		this.expirationTimeAutoInitializer = expirationTimeAutoInitializer;
		this.logger = logger;
	}

	/**
	 * Determines whether the lock should be deleted at the end of execution.
	 * 
	 * <p>This method marks the renewal task for removal if a valid result exists,
	 * which effectively stops the automatic renewal process.</p>
	 *
	 * @param context The Redis context containing operation information
	 * @param result The result object, expected to be an {@link OrangeValueLockRenewTask}
	 * @return {@code true} if the lock should be deleted, {@code false} otherwise
	 */
	@Override
	protected boolean isDeleteInTheEnd(OrangeRedisContext context, Object result) {
		if(result == null) {
			return false;
		}
		OrangeValueLockRenewTask task = (OrangeValueLockRenewTask)result;
		task.setRemove(true);
		return true;
	}

	/**
	 * Executes the lock acquisition with automatic renewal setup.
	 * 
	 * @param context The Redis value context containing operation parameters
	 * @return An {@link OrangeValueLockRenewTask} instance if the lock was acquired successfully
	 * @throws OrangeRedisIfAbsentException if the lock acquisition fails
	 * @throws Exception if any other error occurs during execution
	 */
	@Override
	protected Object executeIfAsent(OrangeRedisValueContext context) throws Exception {
		OrangeValueLockAutoRenewContext ctx = (OrangeValueLockAutoRenewContext) context;
		Key key = ctx.getRedisKey();
		AutoRenew autoRenew = ctx.getAutoRenew();
		if(autoRenew.autoInitKeyExpirationTime()) {
			key = this.expirationTimeAutoInitializer.init(key, autoRenew.threshold());
		}
		OrangeValueLockRenewTask task = new OrangeValueLockRenewTask(
			key,
			getOperations(),
			getValue(ctx),
			RedisValueTypeEnum.STRING,
			autoRenew.threshold()
		);
		Boolean result = this.getOperations().setIfAbsent(
			key.getValue(), 
			getValue(ctx), 
			key.getExpirationTime(), 
			key.getExpirationTimeUnit(), 
			RedisValueTypeEnum.STRING
		);
		if(result == null || !result.booleanValue()) {
			throw new OrangeRedisIfAbsentException("False returned");
		}
		renewTimerWheel.addRenewTask(task);
		return task;
	}
	
	

	/**
	 * Processes the result before returning it to the caller.
	 * Converts the result to a boolean indicating whether the lock was acquired.
	 *
	 * @param context The Redis context containing operation information
	 * @param result The result object from the execution
	 * @return A boolean indicating whether the lock was successfully acquired
	 */
	@Override
	protected Object returnValue(OrangeRedisContext context, Object result) {
		return super.returnValue(context, result != null);
	}

	/**
	 * Notifies registered listeners about the lock acquisition result.
	 * The result is converted to a boolean indicating success or failure.
	 *
	 * @param ctx The Redis value context containing operation information
	 * @param result The result object from the execution
	 */
	@Override
	protected void notifyListeners(OrangeRedisValueContext ctx, Object result) {
		super.notifyListeners(ctx, result != null);
	}

	/**
	 * Returns the context class used by this executor.
	 *
	 * @return The {@link OrangeValueLockAutoRenewContext} class
	 */
	@Override
	public Class<? extends OrangeRedisContext> getContextClass() {
		return OrangeValueLockAutoRenewContext.class;
	}

	/**
	 * Returns the list of annotation classes supported by this executor.
	 * 
	 * <p>Supported annotations include:</p>
	 * <ul>
	 *   <li>{@link Lock} - For basic locking functionality</li>
	 *   <li>{@link AutoRenew} - For configuring automatic lock renewal</li>
	 *   <li>{@link SetExpiration} - For setting lock expiration time</li>
	 * </ul>
	 *
	 * @return A list of supported annotation classes
	 */
	@Override
	protected List<Class<? extends Annotation>> getSupportedAnnotationClasses() {
		return OrangeCollectionUtils.asList(Lock.class, AutoRenew.class,SetExpiration.class);
	}

	/**
	 * Gets the value to be used for the lock.
	 * Uses the trace ID from the logger if available, otherwise returns "1".
	 *
	 * @param ctx The Redis value context
	 * @return The trace ID if available, or "1" as a default value
	 */
	@Override
	protected Object getValue(OrangeRedisValueContext ctx) {
		String traceId = logger.getTraceId();
		return traceId != null && !traceId.isEmpty() ? traceId : "1";
	}
}