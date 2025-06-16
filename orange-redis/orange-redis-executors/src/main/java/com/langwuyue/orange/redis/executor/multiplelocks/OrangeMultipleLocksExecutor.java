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
package com.langwuyue.orange.redis.executor.multiplelocks;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.langwuyue.orange.redis.OrangeRedisIfAbsentException;
import com.langwuyue.orange.redis.RedisValueTypeEnum;
import com.langwuyue.orange.redis.annotation.AutoRenew;
import com.langwuyue.orange.redis.annotation.ContinueOnFailure;
import com.langwuyue.orange.redis.annotation.Multiple;
import com.langwuyue.orange.redis.annotation.global.SetExpiration;
import com.langwuyue.orange.redis.annotation.multiplelocks.MultipleLocks;
import com.langwuyue.orange.redis.context.OrangeRedisContext;
import com.langwuyue.orange.redis.context.OrangeRedisContext.Key;
import com.langwuyue.orange.redis.context.OrangeRedisIterableContext;
import com.langwuyue.orange.redis.executor.OrangeRedisAbstractExecutor;
import com.langwuyue.orange.redis.executor.hash.OrangeCompareAndSwapExecutor;
import com.langwuyue.orange.redis.executor.multiplelocks.context.OrangeMultipleLocksContext;
import com.langwuyue.orange.redis.executor.script.ScriptConstants;
import com.langwuyue.orange.redis.listener.OrangeRedisMultipleSetIfAbsentListener;
import com.langwuyue.orange.redis.listener.multiplelocks.OrangeMultipleLocksEvent;
import com.langwuyue.orange.redis.listener.multiplelocks.OrangeMultipleLocksRemoveFailedEvent;
import com.langwuyue.orange.redis.logger.OrangeRedisLogger;
import com.langwuyue.orange.redis.mapping.OrangeRedisExecutorIdGenerator;
import com.langwuyue.orange.redis.mapping.OrangeRedisHashExecutorIdGenerator;
import com.langwuyue.orange.redis.operations.OrangeRedisHashOperations;
import com.langwuyue.orange.redis.operations.OrangeRedisScriptOperations;
import com.langwuyue.orange.redis.timer.OrangeRenewTask;
import com.langwuyue.orange.redis.timer.OrangeRenewTimerWheel;
import com.langwuyue.orange.redis.utils.OrangeCollectionUtils;

/**
 * Redis executor implementation for handling multiple locks operations.
 * 
 * <p>This executor provides functionality to acquire and release multiple Redis locks atomically.
 * It uses Lua scripts to ensure atomic operations when interacting with Redis. The implementation
 * supports the following features:
 * 
 * <ul>
 *   <li>Atomic acquisition of multiple locks using Redis hash operations</li>
 *   <li>Automatic lock renewal through a timer wheel mechanism</li>
 *   <li>Configurable expiration times for locks</li>
 *   <li>Failure handling with optional continue-on-failure behavior</li>
 *   <li>Event notifications through listeners for lock acquisition and release events</li>
 * </ul>
 * 
 * <p>The executor works with {@link OrangeMultipleLocksContext} to process lock operations
 * and supports annotations like {@link MultipleLocks}, {@link Multiple}, {@link ContinueOnFailure},
 * {@link AutoRenew}, and {@link SetExpiration}.
 *
 * @author Liang.Zhong
 * @since 1.0.0
 * @see OrangeMultipleLocksContext
 * @see MultipleLocks
 * @see AutoRenew
 */
public class OrangeMultipleLocksExecutor extends OrangeRedisAbstractExecutor {
	/**
	 * Lua script for handling multiple lock operations in production environment.
	 * 
	 * <p>This script performs the following operations:
	 * <ol>
	 *   <li>Checks if the hash key exists in the Redis hash</li>
	 *   <li>If the key doesn't exist or its value is less than current time, sets the new deadline</li>
	 *   <li>Returns '1' if lock was acquired, '0' otherwise</li>
	 * </ol>
	 * 
	 * <p>Script arguments:
	 * <ul>
	 *   <li>KEYS[1]: The Redis key for the hash</li>
	 *   <li>ARGV[1]: The hash field (lock identifier)</li>
	 *   <li>ARGV[2]: The deadline timestamp</li>
	 *   <li>ARGV[3]: The current timestamp</li>
	 * </ul>
	 */
	private static final String MULTI_LOCK_LUA_SCRIPT = String.join("\n",
	    "local deadline = ARGV[2];",
	    "local currentTime = tonumber(ARGV[3]);",
	    "local hashKey = ARGV[1];",
	    "local key = KEYS[1];",
	    "local currentValue = redis.call('HGET', key, hashKey);",
	    "if (currentValue == nil or currentValue == false) or (tonumber(currentValue) < currentTime) then",
	    "    redis.call('HSET', key, hashKey, deadline);",
	    "    return '1';",
	    "else",
	    "    return '0';",
	    "end;"
	);
	/**
	 * Lua script for handling multiple lock operations in debug environment.
	 * 
	 * <p>This script is similar to the production script but includes additional
	 * debug information in the return value. It performs the following operations:
	 * <ol>
	 *   <li>Checks if the hash key exists in the Redis hash</li>
	 *   <li>If the key doesn't exist or its value is less than current time, sets the new deadline</li>
	 *   <li>Returns a table with detailed information about the operation</li>
	 * </ol>
	 * 
	 * <p>Script arguments:
	 * <ul>
	 *   <li>KEYS[1]: The Redis key for the hash</li>
	 *   <li>ARGV[1]: The hash field (lock identifier)</li>
	 *   <li>ARGV[2]: The deadline timestamp</li>
	 *   <li>ARGV[3]: The current timestamp</li>
	 *   <li>ARGV[4]: The trace id</li>
	 * </ul>
	 * 
	 * <p>Return value is a table with the following fields:
	 * <ul>
	 *   <li>success: 1 if lock was acquired, 0 otherwise</li>
	 *   <li>exists: 1 if the key already exists, 0 otherwise</li>
	 *   <li>value: The current value of the key if it exists</li>
	 *   <li>now: The current timestamp passed to the script</li>
	 * </ul>
	 */
	private static final String MULTI_LOCK_LUA_SCRIPT_DEBUG = String.join("\n",
		"local traceId = tostring(ARGV[4]);",
		"redis.log(redis.LOG_NOTICE, string.format('traceId: %s, OrangeMultipleLocksExecutor executing', traceId));",
	    "local deadline = ARGV[2];",
	    "redis.log(redis.LOG_NOTICE, string.format('traceId: %s, deadline: %s', traceId, tostring(deadline)));",
	    "local currentTime = tonumber(ARGV[3]);",
	    "redis.log(redis.LOG_NOTICE, string.format('traceId: %s, currentTime: %d', traceId, currentTime));",
	    "local hashKey = ARGV[1];",
	    "redis.log(redis.LOG_NOTICE, string.format('traceId: %s, hashKey: %s', traceId, tostring(hashKey)));",
	    "local key = KEYS[1];",
	    "redis.log(redis.LOG_NOTICE, string.format('traceId: %s, key: %s', traceId, tostring(key)));",
	    "local currentValue = redis.call('HGET', key, hashKey);",
	    "redis.log(redis.LOG_NOTICE, string.format('traceId: %s, currentValue: %s', traceId, tostring(currentValue)));",
	    "if (currentValue == nil or currentValue == false) or (tonumber(currentValue) < currentTime) then",
	    "    redis.log(redis.LOG_NOTICE, string.format('traceId: %s, set deadline', traceId));",
	    "    redis.call('HSET', key, hashKey, deadline);",
	    "    return '1';",
	    "else",
	    "    return '0';",
	    "end;"
	);
	private OrangeRedisScriptOperations scriptOperations;
	
	private OrangeRedisHashOperations operations;
	
	private OrangeRenewTimerWheel renewTimerWheel;
	
	private OrangeExpirationTimeAutoInitializer expirationTimeAutoInitializer;

	private Collection<OrangeRedisMultipleSetIfAbsentListener> listeners;
	
	private OrangeCompareAndSwapExecutor casExecutor;
	
	private OrangeRedisLogger logger;

	/**
	 * Constructs a new OrangeMultipleLocksExecutor with the specified dependencies.
	 *
	 * @param scriptOperations the Redis script operations for executing Lua scripts
	 * @param operations the Redis hash operations for managing hash data structures
	 * @param idGenerator the generator for creating unique executor IDs
	 * @param listeners collection of listeners to be notified of lock events
	 * @param wheel timer wheel for managing lock renewal tasks
	 * @param expirationTimeAutoInitializer initializer for automatic expiration time management
	 * @param logger logger for recording operation details and debug information
	 */
	public OrangeMultipleLocksExecutor(
		OrangeRedisScriptOperations scriptOperations,
		OrangeRedisHashOperations operations,
		OrangeRedisExecutorIdGenerator idGenerator,
		Collection<OrangeRedisMultipleSetIfAbsentListener> listeners,
		OrangeRenewTimerWheel wheel,
		OrangeExpirationTimeAutoInitializer expirationTimeAutoInitializer,
		OrangeRedisLogger logger
	) {
		super(idGenerator);
		this.scriptOperations = scriptOperations;
		this.operations = operations;
		this.listeners = listeners;
		this.renewTimerWheel = wheel;
		this.expirationTimeAutoInitializer = expirationTimeAutoInitializer;
		this.casExecutor = new OrangeCompareAndSwapExecutor(scriptOperations,new OrangeRedisHashExecutorIdGenerator(),logger);
		this.logger = logger;
	}

	/**
	 * {@inheritDoc}
	 * 
	 * <p>Executes the multiple locks operation based on the provided context.
	 *
	 * @param context the execution context containing lock information and parameters
	 * @return null as this operation doesn't return a value
	 * @throws Exception if an error occurs during lock acquisition or if the context type is invalid
	 * @see OrangeMultipleLocksContext
	 * @see #doLock(OrangeRedisIterableContext, Key, boolean, int, RedisValueTypeEnum, Object[])
	 */
	@Override
	public Object execute(OrangeRedisContext context) throws Exception {
		OrangeMultipleLocksContext ctx = (OrangeMultipleLocksContext) context;
		doLock(
			(OrangeRedisIterableContext)context,
			context.getRedisKey(),
			ctx.getAutoRenew().autoInitKeyExpirationTime(),
			ctx.getAutoRenew().threshold(),
			context.getValueType(),
			context.getArgs()
		);
		return null;
	}
	
	/**
	 * Performs the actual lock acquisition operation for multiple locks.
	 * 
	 * <p>This method handles the core logic for acquiring multiple locks atomically. åå
	 *
	 * @param ctx the iterable context containing the lock entries
	 * @param redisKey the Redis key for the lock operations
	 * @param autoInitKeyExpirationTime whether to automatically initialize key expiration time
	 * @param threshold the threshold value for lock renewal
	 * @param valueType the type of value being stored
	 * @param args additional arguments for the lock operation
	 * @see OrangeRedisIterableContext
	 * @see OrangeRenewTask
	 * @see OrangeMultipleLocksEvent
	 */
	public void doLock(OrangeRedisIterableContext ctx,Key redisKey,boolean autoInitKeyExpirationTime,int threshold, RedisValueTypeEnum valueType, Object[] args) {
		Set<Object> successEntries = new LinkedHashSet<>();
		Set<Object> successMembers = new LinkedHashSet<>();
		Map<Object,Exception> failedEntries = new LinkedHashMap<>();
		Set<Object> unknownEnties = new LinkedHashSet<>();
		boolean continueOnFailure = ctx.continueOnFailure();
		List<OrangeRenewTask> renewTasks = new ArrayList<>();
		ctx.forEach((t,o) -> {
			if(!continueOnFailure && !failedEntries.isEmpty()) {
				unknownEnties.add(o);
				return;
			}
			try {
				Key key = redisKey;
				if(autoInitKeyExpirationTime) {
					key = this.expirationTimeAutoInitializer.init(key, threshold);
					redisKey.setExpirationTime(key.getExpirationTime());
					redisKey.setExpirationTimeUnit(key.getExpirationTimeUnit());
				}
				OrangeRenewTask task = new OrangeRenewTask(
					key,
					this.casExecutor,
					t,
					valueType,
					threshold
				);
				Boolean success = doLock(task);
				if(success != null && success.booleanValue()) {
					successEntries.add(o);
					successMembers.add(t);
					renewTimerWheel.addRenewTask(task);
					renewTasks.add(task);
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
		
		if(successEntries.isEmpty() && !unknownEnties.isEmpty() && failedEntries.isEmpty()) {
			return;
		}
		
		try {
			this.listeners.forEach(t -> 
				t.onCompleted(
					redisKey.getOriginalKey(),
					new OrangeMultipleLocksEvent(
						args,
						successEntries,
						failedEntries,
						unknownEnties
					)
				)
			);
		}finally {
			// Remove auto renew tasks
			renewTasks.forEach(t -> t.setRemove(true));
			// Release locks
			releaseLocks(successMembers,successEntries,failedEntries,unknownEnties,redisKey,valueType,args);
		}
	}
	
	/**
	 * Releases all successfully acquired locks after the operation completes.
	 * 
	 * <p>This method is responsible for cleaning up locks after the main operation,
	 * whether it succeeded or failed.
	 * 
	 * <p>This method is crucial for preventing resource leaks and ensuring that
	 * locks don't remain indefinitely when they're no longer needed.
	 *
	 * @param successMembers the set of lock members that were successfully acquired
	 * @param successEntries the set of entries that were successfully processed
	 * @param failedEntries a map of entries that failed during processing and their exceptions
	 * @param unknownEnties the set of entries with unknown status
	 * @param redisKey the Redis key for the lock operations
	 * @param valueType the type of value being stored
	 * @param args additional arguments for the lock operation
	 * @see OrangeMultipleLocksRemoveFailedEvent
	 */
	private void releaseLocks(
		Set<Object> successMembers,
		Set<Object> successEntries,
		Map<Object,Exception> failedEntries,
		Set<Object> unknownEnties,
		Key redisKey, 
		RedisValueTypeEnum valueType, 
		Object[] args
	) {
		if(successMembers.isEmpty()) {
			return;
		}
		Long removed = null;
		try {
			removed = operations.removeMembers(redisKey.getValue(), valueType, successMembers.toArray());
			if(removed == null || removed < successMembers.size()) {
				throw new OrangeRedisIfAbsentException("The number of members removed was below expectations.");
			}
		}catch (Exception e) {
			// Warning, maybe a network error causes the removal to fail.
			// Also, maybe Redis Client timeout, but the operation success.
			final Long removeCount = removed;
			this.listeners.forEach(t -> 
				t.onCompleted(
					redisKey.getOriginalKey(),
					new OrangeMultipleLocksRemoveFailedEvent(
						args,
						successEntries,
						failedEntries,
						unknownEnties,
						removeCount,
						successMembers.size(),
						e,
						redisKey
					)
				)
			);
		}
	}
	
	/**
	 * Executes the actual lock operation for a single entry using a Lua script.
	 * 
	 * @param task the renewal task containing lock information
	 * @return true if the lock was successfully acquired, false otherwise
	 * @throws Exception if an error occurs during script execution
	 * @see OrangeRenewTask
	 */
	private boolean doLock(OrangeRenewTask task) throws Exception {
		Map<Object, RedisValueTypeEnum> argsValueTypes = new LinkedHashMap<>();
		argsValueTypes.put(task.getValue(), task.getValueType());
		argsValueTypes.put(task.getDeadlineMillis(), RedisValueTypeEnum.LONG);
		argsValueTypes.put(task.getStartMillis(), RedisValueTypeEnum.LONG);
		// Change script when debug is enabled.
		String script = MULTI_LOCK_LUA_SCRIPT;
		if(this.logger.isDebugEnabled()) {
			script = MULTI_LOCK_LUA_SCRIPT_DEBUG;
		}
		// Id for trace 
		if(this.logger.getTraceId() == null) {
			argsValueTypes.put(ScriptConstants.NIL, RedisValueTypeEnum.STRING);
		}else{
			argsValueTypes.put(this.logger.getTraceId(), RedisValueTypeEnum.STRING);
		}
		Object result = scriptOperations.execute(
			script, 
			argsValueTypes, 
			RedisValueTypeEnum.STRING, 
			String.class, 
			OrangeCollectionUtils.asList(task.getKey().getValue()), 
			task.getValue(),
			task.getDeadlineMillis(),
			task.getStartMillis(),
			this.logger.getTraceId() == null ?  ScriptConstants.NIL : this.logger.getTraceId()
		);
		return "1".equals(result);
	}

	/**
	 * {@inheritDoc}
	 * 
	 * <p>Returns the list of annotation classes that this executor supports.
	 * This executor supports multiple annotations related to Redis lock operations:
	 * <ul>
	 *   <li>{@link MultipleLocks} - Primary annotation for multiple locks operations</li>
	 *   <li>{@link Multiple} - Indicates multiple entries to be processed</li>
	 *   <li>{@link ContinueOnFailure} - Controls behavior when lock acquisition fails</li>
	 *   <li>{@link AutoRenew} - Enables automatic renewal of acquired locks</li>
	 *   <li>{@link SetExpiration} - Configures expiration time for locks</li>
	 * </ul>
	 *
	 * @return a list of supported annotation classes
	 */
	@Override
	protected List<Class<? extends Annotation>> getSupportedAnnotationClasses() {
		return OrangeCollectionUtils.asList(MultipleLocks.class,Multiple.class,ContinueOnFailure.class,AutoRenew.class,SetExpiration.class);
	}

	/**
	 * {@inheritDoc}
	 * 
	 * <p>Returns the context class that this executor uses for multiple locks operations.
	 * This executor uses {@link OrangeMultipleLocksContext} to hold lock information,
	 * keys, and other parameters required for multiple locks operations.
	 *
	 * @return the {@link OrangeMultipleLocksContext} class
	 */
	@Override
	public Class<? extends OrangeRedisContext> getContextClass() {
		return OrangeMultipleLocksContext.class;
	}
}