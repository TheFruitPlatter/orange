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
import com.langwuyue.orange.redis.listener.OrangeRedisMultipleSetIfAbsentListener;
import com.langwuyue.orange.redis.mapping.OrangeRedisExecutorIdGenerator;
import com.langwuyue.orange.redis.operations.OrangeRedisHashOperations;

/**
 * An executor that adds multiple members to a Redis hash with expiration support.
 * 
 * <p>This executor extends {@link OrangeAddMembersIfAbsentExecutor} to provide
 * functionality for adding multiple key-value pairs to a Redis hash data structure
 * only if they don't already exist, with the additional capability of setting an
 * expiration time for the entire hash.
 *
 * <p>The executor supports:
 * <ul>
 *   <li>Conditional addition of multiple members (only if absent)</li>
 *   <li>Setting expiration time for the entire hash</li>
 *   <li>Integration with {@link SetExpiration} annotation</li>
 *   <li>Notification of listeners about operation results</li>
 * </ul>
 *
 * @author Liang.Zhong
 * @since 1.0.0
 * @see OrangeAddMembersIfAbsentExecutor
 * @see SetExpiration
 * @see OrangeRedisMultipleSetIfAbsentListener
 */
public class OrangeAddMembersIfAbsentWithExpirationExecutor extends OrangeAddMembersIfAbsentExecutor {
	
	/**
	 * Constructs a new OrangeAddMembersIfAbsentWithExpirationExecutor.
	 *
	 * @param operations the Redis hash operations to be used for adding members and setting expiration
	 * @param idGenerator the generator for creating unique executor IDs
	 * @param listeners the list of listeners to be notified about operation results
	 */
	public OrangeAddMembersIfAbsentWithExpirationExecutor(
		OrangeRedisHashOperations operations,
		OrangeRedisExecutorIdGenerator idGenerator,
		List<OrangeRedisMultipleSetIfAbsentListener> listeners
	) {
		super(operations,idGenerator,listeners);
	}
	
	/**
	 * Executes the operation to add multiple members to a Redis hash if they don't exist,
	 * and sets an expiration time for the hash.
	 *
	 * <p>This method first delegates to the parent class to add members to the hash,
	 * then sets the expiration time if specified in the context through the {@link SetExpiration}
	 * annotation.
	 *
	 * @param context the context containing the Redis key, members to add, and expiration information
	 * @return the result of the operation, typically the number of members added
	 * @throws Exception if an error occurs during the Redis operation
	 */
	@Override
	public Object execute(OrangeRedisContext context) throws Exception {
		Key key = context.getRedisKey();
		getOperations().expire(key.getValue(), key.getExpirationTime(), key.getExpirationTimeUnit());
		return super.execute(context);
	}

	/**
	 * Gets the list of annotation classes supported by this executor.
	 *
	 * <p>This executor supports both the annotations from its parent class
	 * (for adding members if absent) and the {@link SetExpiration} annotation
	 * for setting expiration time on the hash.
	 *
	 * @return a list containing all supported annotation classes, including
	 *         those from the parent class and {@link SetExpiration}
	 */
	@Override
	protected List<Class<? extends Annotation>> getSupportedAnnotationClasses() {
		List<Class<? extends Annotation>> classes = super.getSupportedAnnotationClasses();
		classes.add(SetExpiration.class);
		return classes;
	}

}