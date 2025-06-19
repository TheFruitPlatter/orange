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
import java.util.List;

import com.langwuyue.orange.redis.annotation.global.SetExpiration;
import com.langwuyue.orange.redis.context.OrangeRedisContext;
import com.langwuyue.orange.redis.context.OrangeRedisContext.Key;
import com.langwuyue.orange.redis.listener.OrangeRedisSetIfAbsentListener;
import com.langwuyue.orange.redis.mapping.OrangeRedisExecutorIdGenerator;
import com.langwuyue.orange.redis.operations.OrangeRedisHashOperations;

/**
 * An executor that adds a member to a Redis hash if it's absent and sets an expiration time.
 * 
 * <p>This executor extends {@link OrangeAddMemberIfAbsentExecutor} by adding the ability
 * to set an expiration time on the hash key after the member is added. It supports all the
 * annotations from its parent class plus the {@link SetExpiration} annotation for
 * specifying the expiration time.
 *
 * <p>The executor performs the following operations:
 * <ol>
 *   <li>Attempts to add the member to the hash if it doesn't exist</li>
 *   <li>If the addition is successful, sets the specified expiration time on the hash key</li>
 *   <li>Notifies registered listeners about the operation result</li>
 * </ol>
 *
 * @author Liang.Zhong
 * @since 1.0.0
 * @see OrangeAddMemberIfAbsentExecutor
 * @see SetExpiration
 */
public class OrangeAddMemberIfAbsentWithExpirationExecutor extends OrangeAddMemberIfAbsentExecutor {
	
	/**
	 * Constructs a new OrangeAddMemberIfAbsentWithExpirationExecutor.
	 * 
	 * <p>This constructor initializes the executor with the necessary dependencies
	 * for performing hash operations with expiration time settings.
	 *
	 * @param operations the Redis hash operations implementation to use
	 * @param idGenerator the generator for creating unique executor IDs
	 * @param listeners a list of listeners to be notified when a member is added if absent
	 */
	public OrangeAddMemberIfAbsentWithExpirationExecutor(
		OrangeRedisHashOperations operations,
		OrangeRedisExecutorIdGenerator idGenerator,
		List<OrangeRedisSetIfAbsentListener> listeners
	) {
		super(operations,idGenerator,listeners);
	}
	
	/**
	 * Executes the add-member-if-absent operation with expiration time setting.
	 * 
	 * <p>This method first sets the expiration time for the Redis key based on the
	 * context's key configuration, then delegates to the parent class to perform
	 * the actual member addition operation.
	 *
	 * <p>The expiration time is set using:
	 * <ul>
	 *   <li>The key value from the context</li>
	 *   <li>The expiration time value from the context</li>
	 *   <li>The time unit from the context</li>
	 * </ul>
	 *
	 * @param context the operation context containing the hash key and its configuration
	 * @return the result of the operation from the parent class execution
	 * @throws Exception if any error occurs during the operation
	 * @see OrangeRedisContext.Key
	 */
	@Override
	public Object execute(OrangeRedisContext context) throws Exception {
		Key key = context.getRedisKey();
		getOperations().expire(key.getValue(), key.getExpirationTime(), key.getExpirationTimeUnit());
		return super.execute(context);
	}

	/**
	 * Returns the list of annotation classes supported by this executor.
	 * 
	 * <p>This method extends the parent class's supported annotations by adding
	 * the {@link SetExpiration} annotation. This allows the executor to recognize
	 * and process expiration time settings in addition to the annotations supported
	 * by the parent class.
	 *
	 * @return a list of annotation classes that this executor can process
	 * @see SetExpiration
	 */
	@Override
	protected List<Class<? extends Annotation>> getSupportedAnnotationClasses() {
		List<Class<? extends Annotation>> classes = super.getSupportedAnnotationClasses();
		classes.add(SetExpiration.class);
		return classes;
	}

}