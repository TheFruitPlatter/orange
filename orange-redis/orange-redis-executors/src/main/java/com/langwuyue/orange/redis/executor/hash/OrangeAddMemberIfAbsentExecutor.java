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
package com.langwuyue.orange.redis.executor.hash;

import java.lang.annotation.Annotation;
import java.util.Collection;
import java.util.List;
import java.util.Map.Entry;

import com.langwuyue.orange.redis.OrangeRedisIfAbsentException;
import com.langwuyue.orange.redis.annotation.AddMembers;
import com.langwuyue.orange.redis.annotation.IfAbsent;
import com.langwuyue.orange.redis.annotation.RedisValue;
import com.langwuyue.orange.redis.annotation.hash.HashKey;
import com.langwuyue.orange.redis.context.OrangeRedisContext;
import com.langwuyue.orange.redis.executor.OrangeRedisAbstractExecutor;
import com.langwuyue.orange.redis.executor.hash.context.OrangeAddIfAbsentContext;
import com.langwuyue.orange.redis.executor.hash.context.OrangeHashKeyValueIfAbsentContext;
import com.langwuyue.orange.redis.listener.OrangeRedisSetIfAbsentListener;
import com.langwuyue.orange.redis.listener.hash.OrangeAddMemberIfAbsentFailedEvent;
import com.langwuyue.orange.redis.listener.hash.OrangeAddMemberIfAbsentSuccessEvent;
import com.langwuyue.orange.redis.listener.hash.OrangeRemoveMemberFailedEvent;
import com.langwuyue.orange.redis.mapping.OrangeRedisExecutorIdGenerator;
import com.langwuyue.orange.redis.operations.OrangeRedisHashOperations;
import com.langwuyue.orange.redis.utils.OrangeCollectionUtils;
import com.langwuyue.orange.redis.utils.OrangeReflectionUtils;

/**
 * Executor implementation for adding a member to a Redis hash only if the hash field does not already exist.
 * 
 * <p>This executor handles operations annotated with {@link AddMembers} and {@link IfAbsent},
 * providing conditional addition semantics for hash members. If the addition is successful,
 * success events are triggered. If the addition fails, failure events are triggered.
 * 
 * <p>The executor also supports an optional "delete in the end" behavior, where the added
 * member is removed after successful addition and event notification. This is useful for
 * implementing distributed locks or other temporary markers.
 *
 * @author Liang.Zhong
 * @since 1.0.0
 */
public class OrangeAddMemberIfAbsentExecutor extends OrangeRedisAbstractExecutor {
	
	private OrangeRedisHashOperations operations;
	
	private Collection<OrangeRedisSetIfAbsentListener> listeners;

	/**
	 * Constructs a new OrangeAddMemberIfAbsentExecutor with the specified operations,
	 * ID generator, and event listeners.
	 * 
	 * <p>This constructor initializes the executor with the necessary components to perform
	 * conditional member addition operations on Redis hashes. It delegates to the parent constructor
	 * for common initialization and stores the Redis hash operations and event listeners for later use.
	 *
	 * @param operations the Redis hash operations handler used to interact with Redis
	 * @param idGenerator the generator used to create unique identifiers for operations
	 * @param listeners collection of listeners that will be notified of operation success or failure events
	 */
	public OrangeAddMemberIfAbsentExecutor(
		OrangeRedisHashOperations operations,
		OrangeRedisExecutorIdGenerator idGenerator,
		Collection<OrangeRedisSetIfAbsentListener> listeners
	) {
		super(idGenerator);
		this.operations = operations;
		this.listeners = listeners;
	}

	/**
	 * Executes the conditional Redis hash member addition operation.
	 * 
	 * <p>This method attempts to add a member to a Redis hash only if the hash field
	 * does not already exist. The operation follows these steps:
	 * <ol>
	 *   <li>Extracts the key-value pair to be added from the context</li>
	 *   <li>Attempts to add the member to the hash if it's not present</li>
	 *   <li>If the addition fails, notifies failure listeners</li>
	 *   <li>If successful, notifies success listeners and optionally removes the member</li>
	 *   <li>Returns an appropriate value based on the method's return type</li>
	 * </ol>
	 *
	 * <p>The return value depends on the method's return type:
	 * <ul>
	 *   <li>For boolean types: returns the success status directly</li>
	 *   <li>For integer types: returns 1 for success, 0 for failure</li>
	 *   <li>For other types: returns null</li>
	 * </ul>
	 *
	 * @param context the operation context containing Redis key, hash key, and value information
	 * @return an object representing the operation result, based on the method's return type
	 * @throws Exception if an error occurs during execution
	 * @throws ClassCastException if the provided context is not an instance of OrangeAddIfAbsentContext
	 */
	@Override
	public Object execute(OrangeRedisContext context) throws Exception {
		OrangeAddIfAbsentContext ctx = (OrangeAddIfAbsentContext) context;
		Entry member = (Entry) ctx.getMember().entrySet().iterator().next();
		Boolean success = false;
		try {
			success = this.operations.putIfAbsent(
					context.getRedisKey().getValue(), 
					member.getKey(), 
					member.getValue(), 
					ctx.getKeyType(), 
					context.getValueType()
			);
			if(success == null || !success.booleanValue()) {
				throw new OrangeRedisIfAbsentException("False returned");
			}
		}catch (Exception e) {
			// The operation may have been interrupted by a client timeout or network error, but it was actually completed successfully.
			this.listeners.forEach(t -> 
				t.onFailure(
					context.getRedisKey().getOriginalKey(),
					new OrangeAddMemberIfAbsentFailedEvent(context.getArgs(),e)
				)
			);
		}
		
		notifyListeners(context,success,member);
		
		Class<?> returnClass = context.getOperationMethod().getReturnType();
		if(returnClass == Boolean.class || returnClass == boolean.class) {
			return success == null ? Boolean.FALSE : success;	
		}
		if(OrangeReflectionUtils.isInteger(returnClass)) {
			return success == null || !success.booleanValue() ? 0 : 1;	
		}
		return null;
	}
	
	/**
	 * Notifies registered listeners of operation success and handles optional member removal.
	 * 
	 * <p>This method is responsible for:
	 * <ol>
	 *   <li>Checking if the operation was successful</li>
	 *   <li>Notifying listeners of successful operation</li>
	 *   <li>If deleteInTheEnd is true, attempting to remove the added member</li>
	 *   <li>If removal fails, notifying listeners of the removal failure</li>
	 * </ol>
	 *
	 * <p>The method uses a try-finally block to ensure that removal is attempted even if
	 * success notification throws an exception. If removal fails, either due to Redis
	 * operation failure or network issues, appropriate error events are triggered.
	 *
	 * @param context the Redis operation context
	 * @param success the result of the add operation
	 * @param member the key-value entry that was added
	 */
	private void notifyListeners(OrangeRedisContext context,Boolean success,Entry member) {
		if(success == null || !success.booleanValue()) {
			return;
		}
		OrangeAddIfAbsentContext ctx = (OrangeAddIfAbsentContext) context;
		boolean removeFailed = false;
		Exception removeFailedException = null;
		try {
			this.listeners.forEach(t -> 
				t.onSuccess(
					context.getRedisKey().getOriginalKey(),
					new OrangeAddMemberIfAbsentSuccessEvent(context.getArgs())
				)
			);
		}finally{
			try {
				if(ctx.isDeleteInTheEnd()) {
					Long removed = operations.removeMembers(context.getRedisKey().getValue(),ctx.getKeyType(),member.getKey());
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
		final Exception exception = removeFailedException;
		this.listeners.forEach(t -> 
			t.onRemoveFailed(
				context.getRedisKey().getOriginalKey(),
				new OrangeRemoveMemberFailedEvent(context.getArgs(),exception)
			)
		);
	}

	/**
	 * Returns the list of annotation classes supported by this executor.
	 * 
	 * <p>This executor supports four annotations:
	 * <ul>
	 *   <li>{@link AddMembers} - Marks a method as a hash member addition operation</li>
	 *   <li>{@link HashKey} - Identifies parameters that represent hash keys</li>
	 *   <li>{@link RedisValue} - Identifies parameters that represent values to be stored</li>
	 *   <li>{@link IfAbsent} - Indicates the addition should only occur if the member doesn't exist</li>
	 * </ul>
	 *
	 * @return a list containing the supported annotation classes
	 */
	@Override
	protected List<Class<? extends Annotation>> getSupportedAnnotationClasses() {
		return OrangeCollectionUtils.asList(AddMembers.class,HashKey.class,RedisValue.class,IfAbsent.class);
	}

	/**
	 * Returns the context class used by this executor.
	 * 
	 * <p>This executor uses {@link OrangeHashKeyValueIfAbsentContext} to store and process
	 * the operation details, including hash keys, values, and conditional execution flags.
	 * The context class provides specialized methods and properties needed for conditional
	 * hash member addition operations.
	 *
	 * @return the class object representing the context type used by this executor
	 */
	@Override
	public Class<? extends OrangeRedisContext> getContextClass() {
		return OrangeHashKeyValueIfAbsentContext.class;
	}

	/**
	 * Returns the Redis hash operations object used by this executor.
	 * 
	 * <p>This package-private method provides access to the underlying
	 * {@link OrangeRedisHashOperations} instance for testing and internal use.
	 * The operations object handles the actual Redis hash commands and their execution.
	 *
	 * @return the Redis hash operations object used by this executor
	 */
	OrangeRedisHashOperations getOperations() {
		return operations;
	}
}