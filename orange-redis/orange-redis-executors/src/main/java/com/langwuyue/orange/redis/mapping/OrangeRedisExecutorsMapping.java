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
package com.langwuyue.orange.redis.mapping;

import java.lang.reflect.Method;

import com.langwuyue.orange.redis.executor.OrangeRedisExecutor;
import com.langwuyue.orange.redis.logger.OrangeRedisLogger;

/**
 * Core interface for mapping Redis executors to methods. This interface defines the contract
 * for managing the relationship between Java methods and their corresponding Redis executors,
 * providing essential functionality for method execution and logging in the Redis context.
 *
 * <p>Implementations of this interface are responsible for:
 * <ul>
 *   <li>Mapping Java methods to their corresponding Redis executors</li>
 *   <li>Managing method resolution for actual execution</li>
 *   <li>Providing executor ID generation capabilities</li>
 *   <li>Handling logging operations</li>
 * </ul>
 *
 * @author Liang.Zhong
 * @since 1.0.0
 */
public interface OrangeRedisExecutorsMapping {
	
	/**
	 * Retrieves the Redis executor associated with the specified method.
	 * 
	 * @param method the Java method for which to retrieve the executor
	 * @return the Redis executor associated with the specified method
	 */
	OrangeRedisExecutor getExecutor(Method method);
	
	/**
	 * Resolves the actual method to be executed for the given method.
	 * This is useful in cases where method proxying or other transformations are involved.
	 * 
	 * @param method the method to resolve
	 * @return the actual method to be executed
	 */
	Method getActualMethod(Method method);

	/**
	 * Gets the executor ID generator used by this mapping.
	 * The executor ID generator is responsible for generating unique identifiers for executors.
	 * 
	 * @return the executor ID generator instance
	 */
	OrangeRedisExecutorIdGenerator getExecutorIdGenerator();
	
	/**
	 * Gets the logger used by this mapping for logging operations.
	 * 
	 * @return the Redis logger instance
	 */
	OrangeRedisLogger getLogger();

}