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
import com.langwuyue.orange.redis.listener.OrangeRedisSetIfAbsentListener;
import com.langwuyue.orange.redis.mapping.OrangeRedisExecutorIdGenerator;
import com.langwuyue.orange.redis.operations.OrangeRedisZSetOperations;

/**
 * Conditional ZSet member addition executor with expiration support.
 * 
 * <p>Features:
 * <ul>
 *   <li>Only performs addition when member doesn't exist</li>
 *   <li>Supports setting key expiration time</li>
 *   <li>Supports notification listeners for addition results</li>
 *   <li>Extends {@link OrangeAddMemberIfAbsentExecutor} base implementation</li>
 * </ul>
 *
 * @author Liang.Zhong
 * @since 1.0.0
 */
public class OrangeAddIfAbsentWithExpirationExecutor extends OrangeAddMemberIfAbsentExecutor {

	/**
	 * Constructs a new executor instance with ZSet operations, ID generator and listeners.
	 *
	 * @param operations Redis ZSet operations implementation
	 * @param idGenerator Generator for creating unique executor IDs
	 * @param listeners Collection of listeners for set-if-absent operations
	 */
	public OrangeAddIfAbsentWithExpirationExecutor(
			OrangeRedisZSetOperations operations,
			OrangeRedisExecutorIdGenerator idGenerator,
			Collection<OrangeRedisSetIfAbsentListener> listeners
	) {
		super(operations,idGenerator,listeners);
	}
	
	/**
	 * Executes the conditional member addition with expiration setting.
	 * 
	 * <p>Sets the expiration time for the key before delegating to parent implementation.
	 * The expiration parameters are extracted from the context's Redis key metadata.
	 *
	 * @param context Execution context containing Redis key and expiration information
	 * @return Result of the addition operation
	 * @throws Exception if Redis operation fails
	 */
	@Override
	public Object execute(OrangeRedisContext context) throws Exception {
		Key key = context.getRedisKey();
		getOperations().expire(key.getValue(), key.getExpirationTime(), key.getExpirationTimeUnit());
		return super.execute(context);
	}
	
	/**
	 * Returns the list of supported annotation classes.
	 * 
	 * <p>Adds {@link SetExpiration} to the parent's supported annotations
	 * to enable expiration time configuration.
	 *
	 * @return List of supported annotation classes including SetExpiration
	 */
	@Override
	protected List<Class<? extends Annotation>> getSupportedAnnotationClasses() {
		List<Class<? extends Annotation>> classes = super.getSupportedAnnotationClasses();
		classes.add(SetExpiration.class);
		return classes;
	}
}