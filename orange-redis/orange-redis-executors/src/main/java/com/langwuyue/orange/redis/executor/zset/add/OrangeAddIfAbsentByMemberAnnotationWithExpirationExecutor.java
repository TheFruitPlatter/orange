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
 * Annotation-based ZSet member conditional addition executor with expiration support.
 * 
 * <p>Features:
 * <ul>
 *   <li>Extends {@link OrangeAddIfAbsentByMemberAnnotationExecutor} base implementation</li>
 *   <li>Supports setting key expiration via {@link SetExpiration} annotation</li>
 *   <li>Sets key expiration before adding members</li>
 *   <li>Maintains all conditional addition functionality from parent class</li>
 * </ul>
 *
 * @author Liang.Zhong
 * @since 1.0.0
 * @see <a href="https://orange.langwuyue.com/redis/advanced/zset">Orange Redis ZSet Documentation</a>
 */
public class OrangeAddIfAbsentByMemberAnnotationWithExpirationExecutor extends OrangeAddIfAbsentByMemberAnnotationExecutor {

	/**
	 * Constructs a new executor instance with expiration support.
	 *
	 * @param operations Redis ZSet operations interface instance
	 * @param idGenerator Executor ID generator
	 * @param listeners Conditional addition listeners collection
	 */
	public OrangeAddIfAbsentByMemberAnnotationWithExpirationExecutor(
		OrangeRedisZSetOperations operations,
		OrangeRedisExecutorIdGenerator idGenerator,
		Collection<OrangeRedisSetIfAbsentListener> listeners
	) {
		super(operations,idGenerator,listeners);
	}

	/**
	 * Executes conditional member addition operation with expiration time.
	 *
	 * <p>Execution flow:
	 * <ol>
	 *   <li>Gets Redis key information from context</li>
	 *   <li>Sets key expiration time</li>
	 *   <li>Calls parent class method to perform conditional addition</li>
	 * </ol>
	 *
	 * @param context Redis operation context
	 * @return Addition result, inherits parent class's return value handling logic
	 * @throws Exception Possible exceptions during execution
	 */
	@Override
	public Object execute(OrangeRedisContext context) throws Exception {
		Key key = context.getRedisKey();
		getOperations().expire(key.getValue(), key.getExpirationTime(), key.getExpirationTimeUnit());
		return super.execute(context);
	}
	
	/**
	 * Gets the list of annotation types supported by this executor.
	 *
	 * <p>Adds to the parent class supported annotations:
	 * <ul>
	 *   <li>{@link SetExpiration} - Sets key expiration time</li>
	 * </ul>
	 *
	 * @return Immutable list of annotation classes
	 */
	@Override
	protected List<Class<? extends Annotation>> getSupportedAnnotationClasses() {
		List<Class<? extends Annotation>> classes = super.getSupportedAnnotationClasses();
		classes.add(SetExpiration.class);
		return classes;
	}
}