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
import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.List;

import com.langwuyue.orange.redis.annotation.hash.GetHashValues;
import com.langwuyue.orange.redis.context.OrangeRedisContext;
import com.langwuyue.orange.redis.executor.hash.context.OrangeHashContext;
import com.langwuyue.orange.redis.mapping.OrangeRedisExecutorIdGenerator;
import com.langwuyue.orange.redis.operations.OrangeRedisHashOperations;
import com.langwuyue.orange.redis.utils.OrangeCollectionUtils;

/**
 * Executor for retrieving all values from a Redis hash.
 * 
 * <p>This executor extends {@link OrangeGetValuesExecutor} and provides functionality
 * to retrieve all values from a Redis hash without specifying field names.
 * 
 * <p>The executor supports the {@link GetHashValues} annotation and returns all values
 * from the specified hash key. The return type can be either a Collection of values
 * or a Map of field names to values.
 * 
 *
 * @author Liang.Zhong
 * @since 1.0.0
 * @see <a href="https://orange.langwuyue.com/redis/advanced/hash">Orange Redis Hash Documentation</a>
 */
public class OrangeGetAllValuesExecutor extends OrangeGetValuesExecutor {
	
	/**
	 * Constructs a new OrangeGetAllValuesExecutor with the specified Redis hash operations and ID generator.
	 *
	 * @param operations the Redis hash operations implementation to use for executing commands
	 * @param idGenerator the generator for creating executor IDs
	 */
	public OrangeGetAllValuesExecutor(OrangeRedisHashOperations operations,OrangeRedisExecutorIdGenerator idGenerator) {
		super(operations,idGenerator);
	}

	/**
	 * Returns the list of annotation classes supported by this executor.
	 * 
	 * @return a list containing the {@link GetHashValues} annotation class
	 */
	@Override
	protected List<Class<? extends Annotation>> getSupportedAnnotationClasses() {
		return OrangeCollectionUtils.asList(GetHashValues.class);
	}

	/**
	 * Returns the context class used by this executor.
	 * 
	 * @return the {@link OrangeHashContext} class
	 */
	@Override
	public Class<? extends OrangeRedisContext> getContextClass() {
		return OrangeHashContext.class;
	}

	/**
	 * Executes the get operation to retrieve all values from the Redis hash.
	 * 
	 * <p>This method retrieves all values from the Redis hash specified in the context.
	 * Unlike {@link OrangeGetValuesExecutor}, this method doesn't require specific field names
	 * and returns all values in the hash.
	 *
	 * @param context the context containing the hash key and other execution parameters
	 * @param valueField the field representing the method being executed
	 * @param returnArgumentType the generic type of the return value
	 * @return a collection of all values from the hash
	 * @throws Exception if an error occurs during the operation
	 */
	@Override
	protected Collection doGet(OrangeRedisContext context, Field valueField, Type returnArgumentType) throws Exception {
		OrangeHashContext ctx = (OrangeHashContext) context;
		return this.getOperations().getAllValues(
				ctx.getRedisKey().getValue(),
				ctx.getValueType(),
				valueField == null ? returnArgumentType : valueField.getGenericType()
		);
	}
}