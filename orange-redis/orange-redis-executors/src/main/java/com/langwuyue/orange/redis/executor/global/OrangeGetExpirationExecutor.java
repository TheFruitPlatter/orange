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
package com.langwuyue.orange.redis.executor.global;

import java.lang.annotation.Annotation;
import java.util.List;

import com.langwuyue.orange.redis.annotation.global.GetExpiration;
import com.langwuyue.orange.redis.context.OrangeRedisContext;
import com.langwuyue.orange.redis.context.OrangeRedisContext.Key;
import com.langwuyue.orange.redis.executor.OrangeRedisAbstractExecutor;
import com.langwuyue.orange.redis.mapping.OrangeRedisExecutorIdGenerator;
import com.langwuyue.orange.redis.operations.OrangeRedisOperations;
import com.langwuyue.orange.redis.utils.OrangeCollectionUtils;

/**
 * Executor implementation for retrieving Redis key expiration time.
 * 
 * <p>This executor handles methods annotated with {@link GetExpiration} annotation
 * and retrieves the expiration time of a Redis key. The expiration time is returned
 * in the time unit specified in the Redis key context.
 *
 * @author Liang.Zhong
 * @since 1.0.0
 */
public class OrangeGetExpirationExecutor extends OrangeRedisAbstractExecutor {
	
	private OrangeRedisOperations operations;

	/**
	 * Constructs a new OrangeGetExpirationExecutor with the specified Redis operations and ID generator.
	 *
	 * @param operations the Redis operations to use for retrieving expiration times
	 * @param idGenerator the generator for creating executor IDs
	 */
	public OrangeGetExpirationExecutor(OrangeRedisOperations operations,OrangeRedisExecutorIdGenerator idGenerator) {
		super(idGenerator);
		this.operations = operations;
	}

	/**
	 * Executes the get expiration operation on the Redis key specified in the context.
	 * Retrieves the time-to-live value for the key in the time unit specified in the context.
	 *
	 * @param context the Redis operation context containing the key and time unit information
	 * @return the expiration time of the key in the specified time unit, or null if the key has no expiration
	 * @throws Exception if an error occurs during the operation
	 */
	@Override
	public Object execute(OrangeRedisContext context) throws Exception {
		Key key = context.getRedisKey();
		return this.operations.getExpiration(key.getValue(),key.getExpirationTimeUnit());
	}

	/**
	 * Returns the list of annotation classes that this executor supports.
	 *
	 * @return a list containing the GetExpiration annotation class
	 */
	@Override
	protected List<Class<? extends Annotation>> getSupportedAnnotationClasses() {
		return OrangeCollectionUtils.asList(GetExpiration.class);
	}

}