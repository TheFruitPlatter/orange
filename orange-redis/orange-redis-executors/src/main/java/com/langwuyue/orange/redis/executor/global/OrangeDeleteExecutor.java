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

import com.langwuyue.orange.redis.annotation.global.Delete;
import com.langwuyue.orange.redis.context.OrangeRedisContext;
import com.langwuyue.orange.redis.executor.OrangeRedisAbstractExecutor;
import com.langwuyue.orange.redis.mapping.OrangeRedisExecutorIdGenerator;
import com.langwuyue.orange.redis.operations.OrangeRedisOperations;
import com.langwuyue.orange.redis.utils.OrangeCollectionUtils;

/**
 * Executor implementation for Redis DELETE operations.
 * 
 * <p>This executor handles methods annotated with {@link Delete} annotation
 * and performs delete operations on Redis keys. It removes the specified key
 * from Redis database if it exists.
 *
 * @author Liang.Zhong
 * @since 1.0.0
 */
public class OrangeDeleteExecutor extends OrangeRedisAbstractExecutor {
	
	private OrangeRedisOperations operations;

	/**
	 * Constructs a new OrangeDeleteExecutor with the specified Redis operations and ID generator.
	 *
	 * @param operations the Redis operations to use for executing delete commands
	 * @param idGenerator the generator for creating executor IDs
	 */
	public OrangeDeleteExecutor(OrangeRedisOperations operations,OrangeRedisExecutorIdGenerator idGenerator) {
		super(idGenerator);
		this.operations = operations;
	}

	/**
	 * Executes the delete operation on the Redis key specified in the context.
	 *
	 * @param context the Redis operation context containing the key to delete
	 * @return Boolean result indicating whether the key was successfully deleted
	 * @throws Exception if an error occurs during the delete operation
	 */
	@Override
	public Object execute(OrangeRedisContext context) throws Exception {
		return this.operations.delete(context.getRedisKey().getValue());
	}

	/**
	 * Returns the list of annotation classes that this executor supports.
	 *
	 * @return a list containing the Delete annotation class
	 */
	@Override
	protected List<Class<? extends Annotation>> getSupportedAnnotationClasses() {
		return OrangeCollectionUtils.asList(Delete.class);
	}

}