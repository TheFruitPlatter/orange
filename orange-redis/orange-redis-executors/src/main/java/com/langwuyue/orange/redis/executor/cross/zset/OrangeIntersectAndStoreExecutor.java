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
import java.util.List;

import com.langwuyue.orange.redis.annotation.cross.CrossOperationKeys;
import com.langwuyue.orange.redis.annotation.cross.Intersect;
import com.langwuyue.orange.redis.annotation.cross.StoreTo;
import com.langwuyue.orange.redis.context.OrangeRedisContext;
import com.langwuyue.orange.redis.executor.OrangeRedisAbstractExecutor;
import com.langwuyue.orange.redis.executor.cross.context.OrangeCrossOperationContext;
import com.langwuyue.orange.redis.mapping.OrangeRedisExecutorIdGenerator;
import com.langwuyue.orange.redis.operations.OrangeRedisZSetOperations;
import com.langwuyue.orange.redis.utils.OrangeCollectionUtils;

/**
 * Executor for performing intersection operations on Redis Sorted Sets and storing the result.
 * 
 * <p>This executor handles the computation of the intersection between multiple sorted sets
 * and stores the result in a destination key. The intersection operation returns elements that
 * exist in all the specified sorted sets.
 * 
 * <p>This executor supports the {@link Intersect}, {@link CrossOperationKeys}, and {@link StoreTo}
 * annotations to mark methods for intersection operations and specify the keys to operate on
 * and the destination key.
 *
 * @author Liang.Zhong
 * @since 1.0.0
 * @see Intersect
 * @see CrossOperationKeys
 * @see StoreTo
 * @see OrangeCrossOperationContext
 * @see OrangeRedisZSetOperations#intersectAndStore
 */
public class OrangeIntersectAndStoreExecutor extends OrangeRedisAbstractExecutor {
	
	private OrangeRedisZSetOperations operations;

	/**
	 * Constructs a new OrangeIntersectAndStoreExecutor with the specified operations and ID generator.
	 *
	 * @param operations the Redis ZSet operations to be used for intersection and storage
	 * @param idGenerator the generator for creating unique executor IDs
	 */
	public OrangeIntersectAndStoreExecutor(OrangeRedisZSetOperations operations,OrangeRedisExecutorIdGenerator idGenerator) {
		super(idGenerator);
		this.operations = operations;
	}

	/**
	 * Returns a list of annotation classes that this executor supports.
	 * 
	 * <p>This executor supports the {@link Intersect}, {@link CrossOperationKeys}, 
	 * and {@link StoreTo} annotations.
	 *
	 * @return a list of supported annotation classes
	 */
	@Override
	protected List<Class<? extends Annotation>> getSupportedAnnotationClasses() {
		return OrangeCollectionUtils.asList(Intersect.class,CrossOperationKeys.class,StoreTo.class);
	}

	/**
	 * Executes the intersection operation and stores the result.
	 * 
	 * <p>This method performs an intersection between the reference key and comparison keys,
	 * and stores the result in the destination key. The scores of the resulting sorted set
	 * are the sum of the scores in the input sorted sets.
	 *
	 * @param context the Redis context containing operation parameters
	 * @return the number of elements in the resulting sorted set
	 * @throws Exception if an error occurs during the operation
	 */
	@Override
	public Object execute(OrangeRedisContext context) throws Exception {
		OrangeCrossOperationContext ctx = (OrangeCrossOperationContext) context;
		return this.operations.intersectAndStore(ctx.getReferenceKey(), ctx.getComparisonKeys(), ctx.getStoreTo());
	}
	
	/**
	 * Returns the context class used by this executor.
	 * 
	 * <p>This executor uses {@link OrangeCrossOperationContext} to handle intersection operations
	 * between multiple sorted sets.
	 *
	 * @return the class of {@link OrangeCrossOperationContext}
	 */
	@Override
	public Class<? extends OrangeRedisContext> getContextClass() {
		return OrangeCrossOperationContext.class;
	}
}