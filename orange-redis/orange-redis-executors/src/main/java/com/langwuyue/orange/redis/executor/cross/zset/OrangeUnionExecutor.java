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
package com.langwuyue.orange.redis.executor.cross.zset;

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
import com.langwuyue.orange.redis.operations.OrangeRedisZSetOperations;
import com.langwuyue.orange.redis.utils.OrangeCollectionUtils;

/**
 * Executor for performing union operations on Redis Sorted Sets.
 * 
 * <p>This executor handles the computation of the union between multiple sorted sets.
 * The union operation returns the set of elements that exist in at least one of the specified sets.
 * 
 * <p>This executor supports the {@link Union} and {@link CrossOperationKeys} annotations
 * to mark methods for union operations and specify the keys to operate on.
 * 
 * <p>Note: For union operations that need to preserve scores, use {@link OrangeUnionWithScoresExecutor}.
 *
 * @author Liang.Zhong
 * @since 1.0.0
 * @see Union
 * @see CrossOperationKeys
 * @see OrangeCrossOperationContext
 * @see OrangeRedisZSetOperations#union
 */
public class OrangeUnionExecutor extends OrangeRedisGetAbstractExecutor {
	
	/**
	 * The Redis Sorted Set operations instance used to perform union operations.
	 */
	private OrangeRedisZSetOperations operations;

	/**
	 * Constructs a new OrangeUnionExecutor.
	 *
	 * @param operations the Redis Sorted Set operations instance to use for union calculations
	 * @param idGenerator the generator for creating unique executor IDs
	 */
	public OrangeUnionExecutor(OrangeRedisZSetOperations operations,OrangeRedisExecutorIdGenerator idGenerator) {
		super(idGenerator);
		this.operations = operations;
	}

	@Override
	protected List<Class<? extends Annotation>> getSupportedAnnotationClasses() {
		return OrangeCollectionUtils.asList(Union.class,CrossOperationKeys.class);
	}

	/**
	 * Executes the union operation on Redis sorted sets.
	 * 
	 * <p>This method performs the following steps:
	 * <ol>
	 *   <li>Retrieves the reference key and comparison keys from the context</li>
	 *   <li>Determines the appropriate value type for conversion</li>
	 *   <li>Delegates the union operation to the Redis operations handler</li>
	 * </ol>
	 * 
	 * <p>The union operation combines all elements from the specified sorted sets,
	 * removing duplicates (each element will appear only once in the result).
	 * 
	 * @param context the Redis operation context containing keys and type information
	 * @param valueField the field that may contain type information for value conversion
	 * @param returnArgumentType the declared return type of the method
	 * @return the union of all specified sorted sets
	 * @throws Exception if any error occurs during the operation
	 */
	@Override
	protected Collection doGet(OrangeRedisContext context, Field valueField, Type returnArgumentType) throws Exception {
		OrangeCrossOperationContext ctx = (OrangeCrossOperationContext) context;
		return this.operations.union(
			ctx.getReferenceKey(), 
			ctx.getComparisonKeys(), 
			ctx.getValueType(), 
			valueField == null ? returnArgumentType : valueField.getGenericType()
		);
	}
	
	/**
	 * Returns the context class used by this executor.
	 * 
	 * <p>The {@link OrangeCrossOperationContext} provides:
	 * <ul>
	 *   <li>The reference key for the operation</li>
	 *   <li>The comparison keys to union with</li>
	 *   <li>Type information for proper value conversion</li>
	 * </ul>
	 * 
	 * @return the OrangeCrossOperationContext class used by this executor
	 */
	@Override
	public Class<? extends OrangeRedisContext> getContextClass() {
		return OrangeCrossOperationContext.class;
	}

}