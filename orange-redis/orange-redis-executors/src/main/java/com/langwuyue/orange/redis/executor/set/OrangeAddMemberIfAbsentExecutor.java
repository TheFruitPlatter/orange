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
import java.util.List;
import java.util.Map;

import com.langwuyue.orange.redis.OrangeRedisIfAbsentException;
import com.langwuyue.orange.redis.RedisValueTypeEnum;
import com.langwuyue.orange.redis.annotation.AddMembers;
import com.langwuyue.orange.redis.annotation.IfAbsent;
import com.langwuyue.orange.redis.annotation.RedisValue;
import com.langwuyue.orange.redis.context.OrangeRedisContext;
import com.langwuyue.orange.redis.context.OrangeRedisValueIfAbsentContext;
import com.langwuyue.orange.redis.executor.OrangeRedisAbstractExecutor;
import com.langwuyue.orange.redis.executor.script.ScriptConstants;
import com.langwuyue.orange.redis.listener.OrangeRedisSetIfAbsentListener;
import com.langwuyue.orange.redis.listener.set.OrangeAddMemberIfAbsentFailedEvent;
import com.langwuyue.orange.redis.listener.set.OrangeAddMemberIfAbsentSuccessEvent;
import com.langwuyue.orange.redis.listener.set.OrangeRemoveMemberFailedEvent;
import com.langwuyue.orange.redis.logger.OrangeRedisLogger;
import com.langwuyue.orange.redis.mapping.OrangeRedisExecutorIdGenerator;
import com.langwuyue.orange.redis.operations.OrangeRedisScriptOperations;
import com.langwuyue.orange.redis.operations.OrangeRedisSetOperations;
import com.langwuyue.orange.redis.utils.OrangeCollectionUtils;

/**
 * Executor implementation for conditionally adding a member to a Redis Set only if it doesn't already exist.
 * 
 * <p>This executor implements an atomic "add if absent" operation for Redis Sets using Lua scripting.
 * It first checks if the member exists in the Set, and only adds it if it's not already present.
 * This ensures the operation is performed atomically, avoiding race conditions that could occur
 * with separate check-then-add operations.
 * 
 * <p>The executor supports event notifications through listeners:
 * <ul>
 *   <li>Success events when a member is successfully added</li>
 *   <li>Failure events when the operation fails</li>
 *   <li>Remove failure events when cleanup operations fail</li>
 * </ul>
 * 
 * <p>The executor supports the following annotations:
 * <ul>
 *   <li>{@link AddMembers} - Marks a method as an operation to add members to a Set</li>
 *   <li>{@link RedisValue} - Identifies the parameter that contains the value to be added</li>
 *   <li>{@link IfAbsent} - Specifies that the operation should only proceed if the member is absent</li>
 * </ul>
 * 
 * <p>This implementation uses Lua scripting to ensure atomicity and provides detailed logging
 * when debug mode is enabled.
 *
 * @author Liang.Zhong
 * @since 1.0.0
 * @see OrangeRedisValueIfAbsentContext
 * @see OrangeRedisSetIfAbsentListener
 * @see OrangeRedisIfAbsentException
 */
public class OrangeAddMemberIfAbsentExecutor extends OrangeRedisAbstractExecutor {
	/**
	 * Script for production
	 */
	private static final String LUA_SCRIPT = String.join("\n",
	    "local key = KEYS[1];",
	    "local member = ARGV[1];",
	    "local exists = redis.call('SISMEMBER', key, member);",
	    "if exists == 0 then",
	    "    return tostring(redis.call('SADD', key, member));",
	    "else",
	    "    return '0';",
	    "end;"
	);
	
	/**
	 * Script for debug
	 */
	private static final String LUA_SCRIPT_DEBUG = String.join("\n",
		"local traceId = tostring(ARGV[2]);",
		"redis.log(redis.LOG_NOTICE, string.format('traceId: %s, SET OrangeAddMemberIfAbsentExecutor executing', traceId));",
	    "local key = KEYS[1];",
	    "redis.log(redis.LOG_NOTICE, string.format('traceId: %s, key: %s', traceId, tostring(key)));",
	    "local member = ARGV[1];",
	    "redis.log(redis.LOG_NOTICE, string.format('traceId: %s, member: %s', traceId, tostring(member)));",
	    "local exists = redis.call('SISMEMBER', key, member);",
	    "redis.log(redis.LOG_NOTICE, string.format('traceId: %s, whether member exists: %s', traceId, tostring(exists)));",
	    "if exists == 0 then",
	    "    return tostring(redis.call('SADD', key, member));",
	    "else",
	    "    return '0';",
	    "end;"
	);

	private OrangeRedisScriptOperations scriptOperations;
	
	private OrangeRedisSetOperations operations;
	
	private Collection<OrangeRedisSetIfAbsentListener> listeners;
	
	private OrangeRedisLogger logger;
	
	/**
	 * Constructs a new OrangeAddMemberIfAbsentExecutor with the specified dependencies.
	 * 
	 * @param scriptOperations the Redis script operations implementation for executing Lua scripts
	 * @param operations the Redis Set operations implementation for performing Set operations
	 * @param idGenerator the generator for creating unique executor IDs
	 * @param listeners collection of listeners to be notified of operation events
	 * @param logger the logger for recording debug information and trace IDs
	 */
	public OrangeAddMemberIfAbsentExecutor(
		OrangeRedisScriptOperations scriptOperations,
		OrangeRedisSetOperations operations,
		OrangeRedisExecutorIdGenerator idGenerator,
		Collection<OrangeRedisSetIfAbsentListener> listeners,
		OrangeRedisLogger logger
	) {
		super(idGenerator);
		this.scriptOperations = scriptOperations;
		this.operations = operations;
		this.listeners = listeners;
		this.logger = logger;
	}

	/**
	 * Executes the "add member if absent" operation using a Lua script for atomicity.
	 * 
	 * <p>This method performs the following steps:
	 * <ol>
	 *   <li>Casts the context to {@link OrangeRedisValueIfAbsentContext} to access operation parameters</li>
	 *   <li>Generates a unique trace ID for logging and tracking</li>
	 *   <li>Executes a Lua script that atomically checks if the member exists and adds it if absent</li>
	 *   <li>Processes the result and notifies appropriate listeners</li>
	 *   <li>Handles any exceptions and performs cleanup if necessary</li>
	 * </ol>
	 * 
	 * <p>The method uses detailed debug logging when enabled, and properly notifies
	 * listeners of success or failure events.
	 *
	 * @param context the operation context containing the Redis key, value, and if-absent parameters
	 * @return Boolean indicating whether the member was successfully added (true) or already existed (false)
	 * @throws Exception if any error occurs during execution
	 */
	@Override
	public Object execute(OrangeRedisContext context) throws Exception {
		OrangeRedisValueIfAbsentContext ctx = (OrangeRedisValueIfAbsentContext)context;
		Boolean success = Boolean.FALSE;
		
		try {
			success = executeIfAbsent(ctx,ctx.getValue());
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
						ctx.getValue(),
						e
					)
				)
			);
		}
		
		notifyListeners(success,ctx);
		
		return null;
	}
	
	/**
	 * Notifies registered listeners about the result of the add operation and handles cleanup.
	 * 
	 * <p>This private method manages the notification process after an "add if absent" operation:
	 * <ol>
	 *   <li>If the operation was not successful (member already existed), it returns immediately</li>
	 *   <li>If successful, it notifies all registered listeners with a success event</li>
	 *   <li>If the context specifies deletion after success (deleteInTheEnd flag), it attempts to remove the member</li>
	 *   <li>If the removal fails, it notifies listeners with a removal failure event</li>
	 * </ol>
	 * 
	 * <p>This method ensures proper cleanup and notification regardless of operation outcome,
	 * and handles any exceptions that might occur during the notification or cleanup process.
	 *
	 * @param success Boolean indicating whether the member was successfully added
	 * @param ctx the context containing operation parameters and configuration
	 */
	private void notifyListeners(Boolean success,OrangeRedisValueIfAbsentContext ctx) {
		if(success == null || !success.booleanValue()) {
			return;
		}
		boolean removeFailed = false;
		Exception removeFailedException = null;
		try {
			listeners.forEach(t -> 
				t.onSuccess(
					ctx.getRedisKey().getOriginalKey(),
					new OrangeAddMemberIfAbsentSuccessEvent(ctx.getArgs(), ctx.getValue())
				)
			);
		}finally {
			try {
				if(ctx.isDeleteInTheEnd()) {
					Long removed = operations.remove(ctx.getRedisKey().getValue(), ctx.getValueType(), ctx.getValue());
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
		
		Exception exception = removeFailedException;
		listeners.forEach(t -> 
			t.onRemoveFailed(
				ctx.getRedisKey().getOriginalKey(), 
				new OrangeRemoveMemberFailedEvent(ctx.getArgs(),ctx.getValue(),exception)
			)
		);
	}

	/**
	 * Executes the core "if absent" logic using a Lua script for atomic operation.
	 * 
	 * <p>This protected method implements the actual execution of the atomic "add if absent" 
	 * operation through a Lua script. The script checks if the member exists in the Set,
	 * and only adds it if it's not already present, all in a single atomic operation.
	 * 
	 * <p>The method performs the following steps:
	 * <ol>
	 *   <li>Prepares the arguments and their value types for the script execution</li>
	 *   <li>Selects the appropriate script based on whether debug logging is enabled</li>
	 *   <li>Adds a trace ID for logging and debugging purposes</li>
	 *   <li>Executes the Lua script via the script operations service</li>
	 *   <li>Interprets the result: "1" means the member was added, "0" means it already existed</li>
	 * </ol>
	 *
	 * @param ctx the Redis context containing operation parameters
	 * @param member the member to be added to the Set if absent
	 * @return Boolean true if the member was successfully added, false if it already existed
	 * @throws Exception if an error occurs during script execution
	 */
	protected Boolean executeIfAbsent(OrangeRedisContext ctx,Object member) throws Exception {
		Map<Object, RedisValueTypeEnum> argsValueTypes = new LinkedHashMap<>();
		argsValueTypes.put(member, ctx.getValueType());
		// Change script when debug is enabled.
		String script = LUA_SCRIPT;
		if(this.logger.isDebugEnabled()) {
			script = LUA_SCRIPT_DEBUG;
		}
		// Id for trace 
		if(this.logger.getTraceId() == null) {
			argsValueTypes.put(ScriptConstants.NIL, RedisValueTypeEnum.STRING);
		}else{
			argsValueTypes.put(this.logger.getTraceId(), RedisValueTypeEnum.STRING);
		}
		Object result = this.scriptOperations.execute(
			script, 
			argsValueTypes, 
			RedisValueTypeEnum.STRING, 
			String.class, 
			OrangeCollectionUtils.asList(ctx.getRedisKey().getValue()), 
			member,
			this.logger.getTraceId() == null ?  ScriptConstants.NIL : this.logger.getTraceId()
		);
		return "1".equals(result);
	}

	/**
	 * Returns the list of annotation classes that this executor supports.
	 * 
	 * <p>This executor supports three annotations:
	 * <ul>
	 *   <li>{@link AddMembers} - For marking methods that add members to a Set</li>
	 *   <li>{@link RedisValue} - For identifying the value parameter to be added</li>
	 *   <li>{@link IfAbsent} - For specifying the conditional "if absent" behavior</li>
	 * </ul>
	 *
	 * @return a list containing the supported annotation classes
	 */
	@Override
	protected List<Class<? extends Annotation>> getSupportedAnnotationClasses() {
		return OrangeCollectionUtils.asList(AddMembers.class,RedisValue.class,IfAbsent.class);
	}
	
	/**
	 * Returns the context class that this executor requires.
	 * 
	 * <p>This executor uses {@link OrangeRedisValueIfAbsentContext} to handle
	 * conditional "if absent" operations for adding a member to a Redis Set.
	 *
	 * @return the {@link OrangeRedisValueIfAbsentContext} class
	 */
	@Override
	public Class<? extends OrangeRedisContext> getContextClass() {
		return OrangeRedisValueIfAbsentContext.class;
	}

	/**
	 * Package-private getter for the script operations.
	 * 
	 * <p>This method is primarily used for testing purposes and internal access
	 * to the script operations implementation.
	 *
	 * @return the Redis script operations implementation used by this executor
	 */
	OrangeRedisScriptOperations getScriptOperations() {
		return scriptOperations;
	}

	/**
	 * Package-private getter for the Set operations.
	 * 
	 * <p>This method is primarily used for testing purposes and internal access
	 * to the Set operations implementation.
	 *
	 * @return the Redis Set operations implementation used by this executor
	 */
	OrangeRedisSetOperations getOperations() {
		return operations;
	}
}