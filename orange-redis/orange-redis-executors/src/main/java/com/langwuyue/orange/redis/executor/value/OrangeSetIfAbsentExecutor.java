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
import com.langwuyue.orange.redis.annotation.IfAbsent;
import com.langwuyue.orange.redis.annotation.RedisValue;
import com.langwuyue.orange.redis.annotation.value.SetValue;
import com.langwuyue.orange.redis.context.OrangeRedisContext;
import com.langwuyue.orange.redis.context.OrangeRedisValueContext;
import com.langwuyue.orange.redis.context.OrangeRedisValueIfAbsentContext;
import com.langwuyue.orange.redis.executor.OrangeRedisAbstractExecutor;
import com.langwuyue.orange.redis.listener.OrangeRedisSetIfAbsentListener;
import com.langwuyue.orange.redis.listener.value.OrangeRemoveFailedEvent;
import com.langwuyue.orange.redis.listener.value.OrangeSetIfAbsentFailedEvent;
import com.langwuyue.orange.redis.listener.value.OrangeSetIfAbsentSuccessEvent;
import com.langwuyue.orange.redis.mapping.OrangeRedisExecutorIdGenerator;
import com.langwuyue.orange.redis.operations.OrangeRedisValueOperations;
import com.langwuyue.orange.redis.utils.OrangeCollectionUtils;
import com.langwuyue.orange.redis.utils.OrangeReflectionUtils;

/**
 * Redis executor implementation for setting values in Redis only if the key does not already exist (SETNX operation).
 * 
 * <p>This executor handles the SET IF NOT EXISTS (SETNX) operation in Redis, which stores a value
 * with the specified key only if that key does not already exist in Redis. It supports {@link RedisValue},
 * {@link SetValue}, and {@link IfAbsent} annotations to mark methods that should perform Redis SETNX operations.
 * 
 * <p>The executor takes a key and value from the context and attempts to store the value
 * in Redis under the specified key only if the key doesn't exist. It can handle different return types
 * from the annotated methods, including boolean and integer types.
 * 
 * <p>This executor also supports notification listeners that are called on success or failure
 * of the SETNX operation, and can optionally delete the key after a successful operation
 * if specified in the context.
 *
 * @author Liang.Zhong
 * @since 1.0.0
 * @see RedisValue
 * @see SetValue
 * @see IfAbsent
 * @see OrangeRedisValueIfAbsentContext
 * @see OrangeRedisSetIfAbsentListener
 * @see <a href="https://orange.langwuyue.com/redis/advanced/value">Orange Redis Value Documentation</a>
 */
public class OrangeSetIfAbsentExecutor extends OrangeRedisAbstractExecutor {
	
	/**
	 * Redis value operations instance used to perform SETNX operations.
	 * This provides access to Redis SET IF NOT EXISTS command functionality for storing
	 * values with specified keys only if they don't already exist.
	 */
	private OrangeRedisValueOperations operations;
	
	/**
	 * Collection of listeners that will be notified of SETNX operation events.
	 * These listeners receive notifications about successful operations, failures,
	 * and failed removal attempts after successful operations.
	 */
	private Collection<OrangeRedisSetIfAbsentListener> listeners;

	/**
	 * Constructs a new OrangeSetIfAbsentExecutor with the required dependencies.
	 *
	 * @param operations The Redis value operations instance used to perform SETNX operations
	 * @param idGenerator The ID generator used to create unique identifiers for each
	 *                   executor instance, helping with tracking and debugging
	 * @param listeners Collection of listeners that will be notified of SETNX operation events
	 */
	public OrangeSetIfAbsentExecutor(
		OrangeRedisValueOperations operations,
		OrangeRedisExecutorIdGenerator idGenerator,
		Collection<OrangeRedisSetIfAbsentListener> listeners
	) {
		super(idGenerator);
		this.operations = operations;
		this.listeners = listeners;
	}

	/**
	 * Executes the SETNX operation in Redis using the provided context.
	 * 
	 *
	 * @param context The context containing the key, value, and additional settings for the SETNX operation
	 * @return The result of the SETNX operation, which may be a Boolean, Integer, or other type
	 *         depending on the expected return type specified in the context
	 * @throws Exception If an error occurs during the execution of the Redis operation
	 *                   or if the context is not of the expected type
	 */
	@Override
	public Object execute(OrangeRedisContext context) throws Exception {
		OrangeRedisValueContext ctx = (OrangeRedisValueContext)context;
		Object result = null;
		try {
			result = executeIfAsent(ctx);
		}catch (Exception e) {
			// The operation may have been interrupted by a client timeout or network error, but it was actually completed successfully.
			listeners.forEach(t -> 
				t.onFailure(
					context.getRedisKey().getOriginalKey(),
					new OrangeSetIfAbsentFailedEvent(ctx.getArgs(),getValue(ctx), e)
				)
			);
		}
		
		notifyListeners(ctx,result);
		
		return returnValue(context, result);
	}
	
	/**
	 * Retrieves the value to be set from the context.
	 * 
	 * <p>This method extracts the value that should be stored in Redis from the provided context.
	 * The value can be of any type that Redis supports for storage.
	 *
	 * @param ctx The context containing the value to be stored
	 * @return The value to be stored in Redis
	 */
	protected Object getValue(OrangeRedisValueContext ctx) {
		return ctx.getValue();
	}

	/**
	 * Converts the operation result to the appropriate return type based on the method's return type.
	 * 
	 * <p>This method handles the following return types:
	 * <ul>
	 *   <li>Boolean/boolean: Returns the result directly, or false if result is null</li>
	 *   <li>Integer/int: Returns 1 for true result, 0 for false or null result</li>
	 *   <li>Other types: Returns null</li>
	 * </ul>
	 *
	 * @param context The context containing the method return type information
	 * @param result The raw result from the Redis operation
	 * @return The converted result value appropriate for the method's return type
	 */
	protected Object returnValue(OrangeRedisContext context,Object result) {
		Class<?> returnClass = context.getOperationMethod().getReturnType();
		if(returnClass == Boolean.class || returnClass == boolean.class) {
			return result == null ? Boolean.FALSE : result;	
		}
		if(OrangeReflectionUtils.isInteger(returnClass)) {
			return result != null && (((Boolean)result).booleanValue()) ? 1 : 0;	
		}
		return null;
	}
	
	/**
	 * Notifies registered listeners about the result of the SETNX operation.
	 * 
	 * <p>This notification system allows other components to react to the results
	 * of SETNX operations, such as for logging, metrics, or triggering additional actions.
	 *
	 * @param ctx The context containing operation details
	 * @param result The result of the SETNX operation (Boolean indicating success or failure)
	 */
	protected void notifyListeners(OrangeRedisValueContext ctx,Object result) {
		if(result == null || !(((Boolean)result).booleanValue())) {
			return;
		}
		boolean delFailed = false;
		Exception delFailedException = null;
		try {
			listeners.forEach(t -> 
				t.onSuccess(
					ctx.getRedisKey().getOriginalKey(),
					new OrangeSetIfAbsentSuccessEvent(ctx.getArgs(), getValue(ctx))
				)
			);
		}finally {
			try {
				if(isDeleteInTheEnd(ctx,result)) {
					Boolean deleted = operations.delete(ctx.getRedisKey().getValue());	
					delFailed = deleted == null || !deleted.booleanValue(); 
				}
			}catch (Exception e) {
				// Warning, maybe a network error causes the removal to fail.
				// Also, maybe Redis Client timeout, but the operation success.
				delFailed = true;
				delFailedException = e;
				
			}
		}
		
		if(!delFailed) {
			return;
		}
		
		final Exception exception = delFailedException;
		listeners.forEach(t -> 
			t.onRemoveFailed(
				ctx.getRedisKey().getOriginalKey(),
				new OrangeRemoveFailedEvent(ctx.getArgs(),getValue(ctx), exception)
			)
		);
	}
	
	/**
	 * Determines whether the key should be deleted after a successful SETNX operation.
	 * 
	 * <p>This method evaluates both the context settings and the operation result to decide
	 * if the key should be deleted. The key will be deleted only if:
	 * <ul>
	 *   <li>The context indicates deletion is requested (isDeleteInTheEnd is true)</li>
	 *   <li>The operation was successful (result is true)</li>
	 * </ul>
	 *
	 * @param context The context containing operation settings
	 * @param result The result of the SETNX operation
	 * @return true if the key should be deleted, false otherwise
	 */
	protected boolean isDeleteInTheEnd(OrangeRedisContext context,Object result) {
		OrangeRedisValueIfAbsentContext ctx = (OrangeRedisValueIfAbsentContext)context;
		return ctx.isDeleteInTheEnd() && result != null && (Boolean)result;
	}

	/**
	 * Executes the SETNX (SET IF NOT EXISTS) operation in Redis.
	 * 
	 * <p>This method performs the core SETNX operation, attempting to set a value
	 * in Redis only if the specified key does not already exist. It uses the
	 * {@link OrangeRedisValueOperations#setIfAbsent} method to perform
	 * the actual Redis operation.
	 * 
	 * <p>If the operation fails (returns null or false), this method throws an
	 * {@link OrangeRedisIfAbsentException} to indicate that the key already exists.
	 *
	 * @param ctx The context containing the key and value for the operation
	 * @return A Boolean indicating whether the operation was successful (true if the key
	 *         did not exist and the value was set)
	 * @throws OrangeRedisIfAbsentException If the key already exists or the operation fails
	 * @throws Exception If any other error occurs during the Redis operation
	 */
	protected Object executeIfAsent(OrangeRedisValueContext ctx) throws Exception {
		Boolean result = operations.setIfAbsent(
				ctx.getRedisKey().getValue(), 
				getValue(ctx),
				ctx.getValueType()
		);
		if(result == null || !result.booleanValue()) {
			throw new OrangeRedisIfAbsentException("False returned");
		}
		return result;
	}

	/**
	 * Returns the class of the context that this executor supports.
	 * 
	 * <p>This executor specifically works with {@link OrangeRedisValueIfAbsentContext}
	 * which contains the necessary information for SETNX operations, including the key,
	 * value, and additional settings like whether to delete the key after a successful operation.
	 *
	 * @return The class of the context that this executor supports
	 */
	@Override
	public Class<? extends OrangeRedisContext> getContextClass() {
		return OrangeRedisValueIfAbsentContext.class;
	}

	/**
	 * Returns the list of annotation classes that this executor supports.
	 * 
	 * <p>This executor supports the following annotations:
	 * <ul>
	 *   <li>{@link RedisValue} - Marks a method parameter as a Redis value</li>
	 *   <li>{@link SetValue} - Indicates that the method performs a Redis SET operation</li>
	 *   <li>{@link IfAbsent} - Specifies that the SET operation should only occur if the key doesn't exist</li>
	 * </ul>
	 *
	 * @return A list containing the supported annotation classes
	 */
	@Override
	protected List<Class<? extends Annotation>> getSupportedAnnotationClasses() {
		return OrangeCollectionUtils.asList(RedisValue.class,SetValue.class, IfAbsent.class);
	}

	/**
	 * Gets the Redis value operations instance used by this executor.
	 * 
	 * <p>This method provides access to the underlying Redis operations object
	 * that performs the actual Redis commands. It's primarily used for testing
	 * and internal access to Redis functionality.
	 *
	 * @return The Redis value operations instance
	 */
	OrangeRedisValueOperations getOperations() {
		return operations;
	}
}