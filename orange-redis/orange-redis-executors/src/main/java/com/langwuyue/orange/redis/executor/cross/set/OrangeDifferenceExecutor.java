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
package com.langwuyue.orange.redis.executor.cross.set;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.List;

import com.langwuyue.orange.redis.annotation.cross.CrossOperationKeys;
import com.langwuyue.orange.redis.annotation.cross.Difference;
import com.langwuyue.orange.redis.context.OrangeRedisContext;
import com.langwuyue.orange.redis.executor.OrangeRedisGetAbstractExecutor;
import com.langwuyue.orange.redis.executor.cross.context.OrangeCrossOperationContext;
import com.langwuyue.orange.redis.mapping.OrangeRedisExecutorIdGenerator;
import com.langwuyue.orange.redis.operations.OrangeRedisSetOperations;
import com.langwuyue.orange.redis.utils.OrangeCollectionUtils;

/**
 * Executor for Redis set difference operations.
 *
 * <p>This executor handles the difference between Redis sets (elements in first set but not in others).
 * It supports the following annotations:
 * <ul>
 *   <li>{@link Difference} - Marks a method as a set difference operation</li>
 *   <li>{@link CrossOperationKeys} - Specifies additional keys for the operation</li>
 * </ul>
 *
 * @author Liang.Zhong
 * @since 1.0.0
 */
public class OrangeDifferenceExecutor extends OrangeRedisGetAbstractExecutor {
	
	/**
	 * Redis set operations instance used to perform the difference operation.
	 */
	private OrangeRedisSetOperations operations;

	/**
	 * Constructs a new difference executor.
	 * 
	 * @param operations the Redis set operations instance
	 * @param idGenerator the executor ID generator
	 */
	public OrangeDifferenceExecutor(OrangeRedisSetOperations operations, OrangeRedisExecutorIdGenerator idGenerator) {
		super(idGenerator);
		this.operations = operations;
	}

	/**
	 * Returns the list of annotation classes supported by this executor.
	 * 
	 * <p>This executor supports the following annotations:
	 * <ul>
	 *   <li>{@link Difference} - Marks a method as a set difference operation</li>
	 *   <li>{@link CrossOperationKeys} - Specifies additional keys for the operation</li>
	 * </ul>
	 * 
	 * @return list of supported annotation classes
	 */
	@Override
	protected List<Class<? extends Annotation>> getSupportedAnnotationClasses() {
		return OrangeCollectionUtils.asList(Difference.class,CrossOperationKeys.class);
	}

	/**
	 * Performs the difference operation on Redis sets.
	 * 
	 * @param context the Redis operation context containing the keys
	 * @param valueField the field annotated with value mapping (can be null)
	 * @param returnArgumentType the return type of the method
	 * @return the result set containing elements in first set but not in others
	 * @throws Exception if the operation fails
	 */
	@Override
	protected Collection doGet(OrangeRedisContext context, Field valueField, Type returnArgumentType) throws Exception {
		OrangeCrossOperationContext ctx = (OrangeCrossOperationContext) context;
		return this.operations.difference(
			ctx.getKeys(), 
			ctx.getValueType(), 
			valueField == null ? returnArgumentType : valueField.getGenericType()
		);
	}
	
	/**
	 * Returns the context class used by this executor for difference operations.
	 * 
	 * @return the OrangeCrossOperationContext class used for this operation
	 */
	@Override
	public Class<? extends OrangeRedisContext> getContextClass() {
		return OrangeCrossOperationContext.class;
	}

}