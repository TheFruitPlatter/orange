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
import com.langwuyue.orange.redis.annotation.cross.Union;
import com.langwuyue.orange.redis.context.OrangeRedisContext;
import com.langwuyue.orange.redis.executor.OrangeRedisGetAbstractExecutor;
import com.langwuyue.orange.redis.executor.cross.context.OrangeCrossOperationContext;
import com.langwuyue.orange.redis.mapping.OrangeRedisExecutorIdGenerator;
import com.langwuyue.orange.redis.operations.OrangeRedisSetOperations;
import com.langwuyue.orange.redis.utils.OrangeCollectionUtils;

/**
 * Executor for performing union operations on Redis Sets.
 * 
 * <p>This executor computes the union of multiple Redis Sets, returning a new set
 * containing all distinct elements from the input sets. The union operation is
 * performed using the {@link OrangeRedisSetOperations#union} method.
 * 
 * <p>This executor supports the following annotations:
 * <ul>
 *   <li>{@link Union} - Marks a method as a set union operation</li>
 *   <li>{@link CrossOperationKeys} - Specifies additional keys to include in the operation</li>
 * </ul>
 * 
 * @author Liang.Zhong
 * @since 1.0.0
 * @see Union
 * @see CrossOperationKeys
 * @see OrangeCrossOperationContext
 * @see OrangeRedisSetOperations#union
 */
public class OrangeUnionExecutor extends OrangeRedisGetAbstractExecutor {
	
	/**
	 * The Redis set operations handler that performs the actual union operation.
	 */
	private OrangeRedisSetOperations operations;

	/**
	 * Constructs a new union executor with the specified operations handler and ID generator.
	 * 
	 * @param operations the Redis set operations handler
	 * @param idGenerator the executor ID generator
	 */
	public OrangeUnionExecutor(OrangeRedisSetOperations operations, OrangeRedisExecutorIdGenerator idGenerator) {
		super(idGenerator);
		this.operations = operations;
	}

	/**
	 * Returns the list of annotation classes supported by this executor.
	 * 
	 * <p>This executor supports the following annotations:
	 * <ul>
	 *   <li>{@link Union} - Marks a method as a set union operation</li>
	 *   <li>{@link CrossOperationKeys} - Specifies additional keys for the operation</li>
	 * </ul>
	 * 
	 * @return list of supported annotation classes
	 */
	@Override
	protected List<Class<? extends Annotation>> getSupportedAnnotationClasses() {
		return OrangeCollectionUtils.asList(Union.class,CrossOperationKeys.class);
	}

	/**
	 * Performs the actual union operation on Redis sets.
	 * 
	 * @param context the Redis operation context containing keys and value type
	 * @param valueField the field annotated with Redis operation (may be null)
	 * @param returnArgumentType the method return type
	 * @return the result of the union operation as a Collection
	 * @throws Exception if the operation fails
	 */
	@Override
	protected Collection doGet(OrangeRedisContext context, Field valueField, Type returnArgumentType) throws Exception {
		OrangeCrossOperationContext ctx = (OrangeCrossOperationContext) context;
		return this.operations.union(
			ctx.getKeys(), 
			ctx.getValueType(), 
			valueField == null ? returnArgumentType : valueField.getGenericType()
		);
	}
	
	/**
	 * Returns the context class used by this executor for set union operations.
	 * 
	 * @return the OrangeCrossOperationContext class used for set union operations
	 */
	@Override
	public Class<? extends OrangeRedisContext> getContextClass() {
		return OrangeCrossOperationContext.class;
	}

}