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

import com.langwuyue.orange.redis.annotation.global.SetExpiration;
import com.langwuyue.orange.redis.context.OrangeRedisContext;
import com.langwuyue.orange.redis.context.OrangeRedisContext.Key;
import com.langwuyue.orange.redis.executor.OrangeRedisAbstractExecutor;
import com.langwuyue.orange.redis.mapping.OrangeRedisExecutorIdGenerator;
import com.langwuyue.orange.redis.operations.OrangeRedisOperations;
import com.langwuyue.orange.redis.utils.OrangeCollectionUtils;

/**
 * Executor implementation for setting Redis key expiration time.
 * 
 * <p>This executor handles methods annotated with {@link SetExpiration} annotation
 * and sets the expiration time for a Redis key. The expiration time and time unit
 * are obtained from the Redis key context.
 * 
 * <p>When executed, this executor calls the Redis EXPIRE command to set the key's
 * time-to-live value according to the specified parameters.
 *
 * @author Liang.Zhong
 * @since 1.0.0
 */
public class OrangeSetExpirationExecutor extends OrangeRedisAbstractExecutor {
	
	private OrangeRedisOperations operations;

	/**
	 * Constructs a new OrangeSetExpirationExecutor with the specified Redis operations and ID generator.
	 *
	 * @param operations the Redis operations to use for setting expiration times
	 * @param idGenerator the generator for creating executor IDs
	 */
	public OrangeSetExpirationExecutor(OrangeRedisOperations operations,OrangeRedisExecutorIdGenerator idGenerator) {
		super(idGenerator);
		this.operations = operations;
	}

	/**
	 * Executes the set expiration operation for a Redis key.
	 * Sets the specified expiration time for the key in the Redis database.
	 *
	 * @param context the Redis operation context containing the key and expiration time
	 * @return Boolean value indicating whether the expiration was set successfully
	 * @throws Exception if an error occurs during the operation
	 */
	@Override
	public Object execute(OrangeRedisContext context) throws Exception {
		Key key = context.getRedisKey();
		return this.operations.expire(key.getValue(),key.getExpirationTime(),key.getExpirationTimeUnit());
	}

	/**
	 * Returns the list of annotation classes that this executor supports.
	 *
	 * @return a list containing the SetExpiration annotation class
	 */
	@Override
	protected List<Class<? extends Annotation>> getSupportedAnnotationClasses() {
		return OrangeCollectionUtils.asList(SetExpiration.class);
	}

}