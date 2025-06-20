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

import com.langwuyue.orange.redis.annotation.AddMembers;
import com.langwuyue.orange.redis.annotation.IfAbsent;
import com.langwuyue.orange.redis.annotation.Member;
import com.langwuyue.orange.redis.context.OrangeRedisContext;
import com.langwuyue.orange.redis.executor.hash.context.OrangeAddMemberIfAbsentContext;
import com.langwuyue.orange.redis.listener.OrangeRedisSetIfAbsentListener;
import com.langwuyue.orange.redis.mapping.OrangeRedisExecutorIdGenerator;
import com.langwuyue.orange.redis.operations.OrangeRedisHashOperations;
import com.langwuyue.orange.redis.utils.OrangeCollectionUtils;

/**
 * Executor implementation for adding members to a Redis Hash only if they don't already exist,
 * specifically handling method parameters annotated with {@link Member}.
 * 
 * <p>This executor extends {@link OrangeAddMemberIfAbsentExecutor} and provides specialized
 * handling for cases where hash members are identified using the {@code @Member} annotation.
 * It implements a conditional add operation that only adds the member if it's not already
 * present in the hash.
 * 
 * <p>The executor supports the following annotations:
 * <ul>
 *   <li>{@link AddMembers} - Marks a method as a hash member addition operation</li>
 *   <li>{@link Member} - Identifies the parameter containing the member to be added</li>
 *   <li>{@link IfAbsent} - Indicates that the addition should only occur if the member doesn't exist</li>
 * </ul>
 * 
 * <p>This executor also supports listeners that can be notified when a conditional
 * set operation is performed, allowing for custom handling of successful or failed
 * addition attempts.
 *
 * @author Liang.Zhong
 * @since 1.0.0
 * @see OrangeAddMemberIfAbsentExecutor
 * @see AddMembers
 * @see Member
 * @see IfAbsent
 * @see <a href="https://orange.langwuyue.com/redis/advanced/hash">Orange Redis Hash Documentation</a>
 */
public class OrangeAdIfAbsentByMemberAnnotationExecutor extends OrangeAddMemberIfAbsentExecutor {
	
	/**
	 * Constructs a new OrangeAdIfAbsentByMemberAnnotationExecutor with the specified operations,
	 * ID generator, and listeners.
	 * 
	 * <p>This constructor initializes the executor with the necessary components to perform
	 * conditional member addition operations on Redis hashes. It delegates to the parent
	 * constructor for common initialization.
	 *
	 * @param operations the Redis hash operations handler used to interact with Redis
	 * @param idGenerator the generator used to create unique identifiers for operations
	 * @param listeners a collection of listeners that will be notified when conditional
	 *                  set operations are performed
	 */
	public OrangeAdIfAbsentByMemberAnnotationExecutor(
		OrangeRedisHashOperations operations,
		OrangeRedisExecutorIdGenerator idGenerator,
		Collection<OrangeRedisSetIfAbsentListener> listeners
	) {
		super(operations, idGenerator, listeners);
	}

	/**
	 * Returns the list of annotation classes supported by this executor.
	 * 
	 * <p>This executor supports three annotations:
	 * <ul>
	 *   <li>{@link AddMembers} - Identifies methods that add members to a Redis hash</li>
	 *   <li>{@link Member} - Identifies parameters that represent hash members/fields</li>
	 *   <li>{@link IfAbsent} - Indicates that the addition should only occur if the member doesn't exist</li>
	 * </ul>
	 *
	 * @return a list containing the supported annotation classes
	 */
	@Override
	protected List<Class<? extends Annotation>> getSupportedAnnotationClasses() {
		return OrangeCollectionUtils.asList(AddMembers.class, Member.class, IfAbsent.class);
	}

	/**
	 * Returns the context class used by this executor.
	 * 
	 * <p>This executor uses {@link OrangeAddMemberIfAbsentContext} to store and manage
	 * the state and parameters required for conditional member addition operations.
	 * The context encapsulates information such as the Redis key, member/field name,
	 * value to be set, and operation result.
	 *
	 * @return the {@link OrangeAddMemberIfAbsentContext} class
	 */
	@Override
	public Class<? extends OrangeRedisContext> getContextClass() {
		return OrangeAddMemberIfAbsentContext.class;
	}
}