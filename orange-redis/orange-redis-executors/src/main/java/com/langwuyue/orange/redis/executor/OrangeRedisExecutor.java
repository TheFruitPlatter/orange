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

import com.langwuyue.orange.redis.context.OrangeRedisContext;

/**
 * Defines the contract for executing Redis operations in the Orange framework.
 * 
 * <p>This interface serves as the foundation for all Redis operation executors,
 * providing a standardized way to execute Redis commands with proper context
 * handling and error management.
 * 
 * <p>Implementations of this interface are responsible for:
 * <ul>
 *   <li>Executing specific Redis operations (e.g., GET, SET, HGET)</li>
 *   <li>Managing operation context and parameters</li>
 *   <li>Handling Redis connection lifecycle</li>
 *   <li>Providing proper error handling and recovery</li>
 * </ul>
 * 
 * @author Liang.Zhong
 * @since 1.0.0
 * @see OrangeRedisContext
 */
public interface OrangeRedisExecutor {

	/**
	 * Executes a Redis operation with the provided context.
	 * 
	 * <p>This method is responsible for:
	 * <ul>
	 *   <li>Executing the specific Redis operation</li>
	 *   <li>Managing Redis connections</li>
	 *   <li>Handling operation parameters</li>
	 *   <li>Processing operation results</li>
	 * </ul>
	 * 
	 *
	 * @param context the operation context containing all necessary information
	 *        for executing the Redis operation, including connection, keys,
	 *        and parameters
	 * @return the result of the Redis operation, type depends on the specific
	 *         operation and context configuration
	 * @throws Exception if any error occurs during execution, including Redis
	 *         connection issues, invalid parameters, or operation failures
	 * @see OrangeRedisContext
	 */
	Object execute(OrangeRedisContext context) throws Exception;
	
	/**
	 * Returns the unique identifier for this executor.
	 * 
	 * <p>Each executor must have a unique ID to distinguish it from other
	 * executors in the system. This ID is used for:
	 * <ul>
	 *   <li>Executor registration and lookup</li>
	 *   <li>Operation tracking and monitoring</li>
	 *   <li>Debugging and logging</li>
	 * </ul>
	 * 
	 * @return a unique long value identifying this executor
	 */
	long getId();
	
	/**
	 * Returns the context class supported by this executor.
	 * 
	 * <p>This method defines the type of context this executor can handle.
	 * The default implementation returns {@link OrangeRedisContext}, which
	 * is suitable for basic Redis operations.
	 * 
	 * <p>Specialized executors should override this method to return their
	 * specific context class. For example:
	 * <ul>
	 *   <li>Hash operations should return {@code OrangeHashContext.class}</li>
	 *   <li>Script operations should return {@code OrangeScriptContext.class}</li>
	 * </ul>
	 *
	 * @return the class object representing the context type this executor
	 *         supports
	 * @see OrangeRedisContext
	 */
	default Class<? extends OrangeRedisContext> getContextClass(){
		return OrangeRedisContext.class;
	}
	
}