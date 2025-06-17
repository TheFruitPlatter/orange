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

import com.langwuyue.orange.redis.annotation.global.SetExpiration;
import com.langwuyue.orange.redis.context.OrangeRedisContext;
import com.langwuyue.orange.redis.context.OrangeRedisContext.Key;
import com.langwuyue.orange.redis.listener.OrangeRedisMultipleSetIfAbsentListener;
import com.langwuyue.orange.redis.mapping.OrangeRedisExecutorIdGenerator;
import com.langwuyue.orange.redis.operations.OrangeRedisZSetOperations;

/**
 * Executor for adding members to a ZSet only if they don't exist, with expiration support.
 * 
 * <p>Features:
 * <ul>
 *   <li>Extends {@link OrangeAddMembersIfAbsentExecutor} with expiration capabilities</li>
 *   <li>Sets expiration time on the Redis key after adding members</li>
 *   <li>Supports {@link SetExpiration} annotation for declarative expiration configuration</li>
 *   <li>Maintains all conditional addition behavior from parent class</li>
 * </ul>
 *
 * @author Liang.Zhong
 * @since 1.0.0
 * @see OrangeAddMembersIfAbsentExecutor
 * @see SetExpiration
 * @see <a href="https://orange.langwuyue.com/redis/advanced/zset">Orange Redis ZSet Documentation</a>
 */
public class OrangeAddMembersIfAbsentWithExpirationExecutor extends OrangeAddMembersIfAbsentExecutor {

	/**
	 * Constructor for the executor with expiration support.
	 *
	 * @param operations Redis ZSet operations interface instance
	 * @param idGenerator Executor ID generator
	 * @param listeners Collection of listeners to be notified on conditional addition operations
	 */
	public OrangeAddMembersIfAbsentWithExpirationExecutor(
		OrangeRedisZSetOperations operations,
		OrangeRedisExecutorIdGenerator idGenerator,
		Collection<OrangeRedisMultipleSetIfAbsentListener> listeners
	) {
		super(operations,idGenerator,listeners);
	}
	

	/**
	 * Executes the conditional member addition operation with expiration support.
	 *
	 * <p>Execution flow:
	 * <ol>
	 *   <li>Sets the expiration time on the Redis key before adding members</li>
	 *   <li>Delegates to parent class for conditional member addition</li>
	 * </ol>
	 *
	 * <p>The expiration time is obtained from the {@link Key} configuration,
	 * which can be set via annotations or programmatically. The expiration
	 * is set using the specified time unit from the key configuration.
	 *
	 * @param context Redis operation context containing key, members, and expiration information
	 * @return Result from the parent executor's addition operation (typically number of members added)
	 * @throws Exception if any error occurs during execution or expiration setting
	 */
	@Override
	public Object execute(OrangeRedisContext context) throws Exception {
		Key key = context.getRedisKey();
		getOperations().expire(key.getValue(), key.getExpirationTime(), key.getExpirationTimeUnit());
		return super.execute(context);
	}

	/**
	 * Returns the list of supported annotation classes for this executor.
	 *
	 * <p>Extends the parent's supported annotations by adding {@link SetExpiration}
	 * annotation support for expiration configuration.
	 *
	 * @return List of supported annotation classes including {@link SetExpiration}
	 */
	@Override
	protected List<Class<? extends Annotation>> getSupportedAnnotationClasses() {
		List<Class<? extends Annotation>> classes = super.getSupportedAnnotationClasses();
		classes.add(SetExpiration.class);
		return classes;
	}
}