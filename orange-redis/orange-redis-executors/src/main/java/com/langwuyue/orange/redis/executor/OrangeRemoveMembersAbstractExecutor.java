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
package com.langwuyue.orange.redis.executor;

import java.lang.annotation.Annotation;
import java.util.List;
import java.util.Map;

import com.langwuyue.orange.redis.OrangeRedisException;
import com.langwuyue.orange.redis.annotation.ContinueOnFailure;
import com.langwuyue.orange.redis.annotation.Multiple;
import com.langwuyue.orange.redis.annotation.RemoveMembers;
import com.langwuyue.orange.redis.context.OrangeRedisContext;
import com.langwuyue.orange.redis.context.OrangeRedisIterableContext;
import com.langwuyue.orange.redis.context.OrangeRedisMultipleValueContext;
import com.langwuyue.orange.redis.logger.OrangeRedisLogger;
import com.langwuyue.orange.redis.mapping.OrangeRedisExecutorIdGenerator;
import com.langwuyue.orange.redis.utils.OrangeCollectionUtils;
import com.langwuyue.orange.redis.utils.OrangeReflectionUtils;

/**
 * Abstract executor for Redis member removal operations.
 * 
 * <p>This abstract class provides the base implementation for executors that handle
 * member removal operations in Redis collections (such as Sets or Sorted Sets). It supports
 * both single and multiple member removal operations with flexible return type handling.
 *
 * <p>The executor supports the following features:
 * <ul>
 *   <li>Single and batch member removal</li>
 *   <li>Configurable error handling with continue-on-failure support</li>
 *   <li>Flexible return types (Long, Integer, Map)</li>
 *   <li>Operation result tracking for batch operations</li>
 * </ul>
 *
 * <p>When returning a Map, the executor tracks the success status of each removal operation,
 * with the removed member as the key and a boolean success indicator as the value.
 *
 * @author Liang.Zhong
 * @since 1.0.0
 */
public abstract class OrangeRemoveMembersAbstractExecutor extends OrangeRedisAbstractExecutor {
	
	private OrangeRedisLogger logger;

	/**
	 * Constructs a new member removal executor.
	 * 
	 * <p>Initializes the executor with the specified ID generator and logger.
	 * The ID generator is used for generating unique operation identifiers,
	 * while the logger is used for recording execution events and errors.
	 *
	 * @param idGenerator the generator for creating unique operation IDs
	 * @param logger the logger for recording execution events and errors
	 */
	protected OrangeRemoveMembersAbstractExecutor(OrangeRedisExecutorIdGenerator idGenerator,OrangeRedisLogger logger) {
		super(idGenerator);
		this.logger = logger;
	}

	/**
	 * Executes the member removal operation based on the provided context.
	 * 
	 * <p>This method handles different return types for removal operations:
	 * <ul>
	 *   <li>Long/Integer: Returns the number of successfully removed members</li>
	 *   <li>Map: Returns a map of members to their removal status (boolean)</li>
	 * </ul>
	 *
	 * <p>For batch operations, the method respects the continue-on-failure flag:
	 * <ul>
	 *   <li>If enabled: continues processing after errors, logging them</li>
	 *   <li>If disabled: stops on the first error and throws an exception</li>
	 * </ul>
	 *
	 * @param context the context containing operation parameters and configuration
	 * @return the operation result as either a number (Long/Integer) or a Map
	 * @throws OrangeRedisException if an error occurs during execution and continue-on-failure is disabled
	 * @throws Exception if any other error occurs during execution
	 */
	@Override
	public Object execute(OrangeRedisContext context) throws Exception {
		OrangeRedisIterableContext ctx = (OrangeRedisIterableContext)context;
		Class<?> returnClass = context.getOperationMethod().getReturnType();
		boolean continueOnFailure = ctx.continueOnFailure();
		if(!Map.class.isAssignableFrom(returnClass)) {
			Long result = doRemove(context);
			if(returnClass == Integer.class || returnClass == int.class) {
				return result == null ? 0 : Integer.valueOf(result.toString());	
			}
			return result;
		}
		Map resultMap = OrangeReflectionUtils.newMap(returnClass);
		try {
			ctx.forEach((t,o) -> {
				boolean success = false;
				try {
					Long result = doRemove(context,t);
					success = result != null && result > 0;
					resultMap.put(o, success);
					if(!success) {
						throw new OrangeRedisException(
							String.format(
								"Faled returned! Operation: %s %n Operatin owner: %s", 
								context.getOperationMethod(),
								context.getOperationOwner()
							)
						);
					}
				} catch (Exception e) {
					// The operation may have been interrupted by a client timeout or network error, 
					// but it was actually completed successfully.
					if(!continueOnFailure) {
						if(e instanceof OrangeRedisException) {
							throw (OrangeRedisException)e;
						}
						throw new OrangeRedisException(
							String.format(
								"An exception occurred during removal! Operation: %s %n Operatin owner: %s", 
								context.getOperationMethod(),
								context.getOperationOwner()
							),
							e
						);	
					}else{
						if(!(e instanceof OrangeRedisException)) {
							this.logger.warn(
								String.format(
									"An exception occurred during removal! Operation: %s %n Operatin owner: %s", 
									context.getOperationMethod(),
									context.getOperationOwner()
								),
								e
							);
						}
					}
				}
			});
		}catch (Exception e) {
			this.logger.warn("An exception occurred during member removal.",e);
		}
		return resultMap;
	}
	
	/**
	 * Performs the actual member removal operation for all values in the context.
	 * 
	 * <p>This abstract method should be implemented by concrete classes to perform
	 * the actual Redis member removal operation for all values in the context.
	 *
	 * @param ctx the context containing the operation parameters
	 * @return the number of members successfully removed, or null if the operation failed
	 * @throws Exception if an error occurs during the removal operation
	 */
	protected abstract Long doRemove(OrangeRedisContext ctx) throws Exception;
	
	/**
	 * Performs the actual member removal operation for a specific value.
	 * 
	 * <p>This abstract method should be implemented by concrete classes to perform
	 * the actual Redis member removal operation for a single value.
	 *
	 * @param ctx the context containing the operation parameters
	 * @param value the specific value to remove
	 * @return the number of members successfully removed, or null if the operation failed
	 * @throws Exception if an error occurs during the removal operation
	 */
	protected abstract Long doRemove(OrangeRedisContext ctx,Object value) throws Exception;
	
	/**
	 * Returns the list of annotation classes supported by this executor.
	 * 
	 * <p>This executor supports the following annotations:
	 * <ul>
	 *   <li>{@link RemoveMembers} - Marks methods that remove members from Redis collections</li>
	 *   <li>{@link Multiple} - Indicates that the operation handles multiple values</li>
	 *   <li>{@link ContinueOnFailure} - Specifies error handling behavior</li>
	 * </ul>
	 *
	 * @return a list of supported annotation classes
	 */
	@Override
	protected List<Class<? extends Annotation>> getSupportedAnnotationClasses() {
		return OrangeCollectionUtils.asList(RemoveMembers.class,Multiple.class,ContinueOnFailure.class);
	}

	/**
	 * Returns the context class used by this executor.
	 * 
	 * <p>This executor uses {@link OrangeRedisMultipleValueContext} to handle
	 * multiple value operations and support batch member removal.
	 *
	 * @return the class of {@link OrangeRedisMultipleValueContext}
	 */
	@Override
	public Class<? extends OrangeRedisContext> getContextClass() {
		return OrangeRedisMultipleValueContext.class;
	}
}