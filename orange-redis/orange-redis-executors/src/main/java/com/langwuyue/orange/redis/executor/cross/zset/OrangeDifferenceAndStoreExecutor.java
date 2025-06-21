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
import com.langwuyue.orange.redis.annotation.cross.Difference;
import com.langwuyue.orange.redis.annotation.cross.StoreTo;
import com.langwuyue.orange.redis.context.OrangeRedisContext;
import com.langwuyue.orange.redis.executor.OrangeRedisAbstractExecutor;
import com.langwuyue.orange.redis.executor.cross.context.OrangeCrossOperationContext;
import com.langwuyue.orange.redis.mapping.OrangeRedisExecutorIdGenerator;
import com.langwuyue.orange.redis.operations.OrangeRedisZSetOperations;
import com.langwuyue.orange.redis.utils.OrangeCollectionUtils;

/**
 * Executor for performing difference operations on Redis Sorted Sets and storing the result.
 * 
 * <p>This executor handles the ZDIFFSTORE Redis command, which computes the difference between
 * the first sorted set and all successive sorted sets, and stores the result in a destination key.
 * The difference is the set of elements that exist in the first set but not in any of the other sets.
 * 
 * <p>This executor supports the following annotations:
 * <ul>
 *   <li>{@link Difference} - Marks the method as a difference operation</li>
 *   <li>{@link CrossOperationKeys} - Specifies the keys to operate on</li>
 *   <li>{@link StoreTo} - Specifies the destination key where the result will be stored</li>
 * </ul>
 * 
 * <p>The executor uses {@link OrangeCrossOperationContext} to process the operation parameters
 * and delegates the actual operation to {@link OrangeRedisZSetOperations}.
 *
 * @author Liang.Zhong
 * @since 1.0.0
 * @see Difference
 * @see CrossOperationKeys
 * @see StoreTo
 * @see OrangeCrossOperationContext
 * @see OrangeRedisZSetOperations#differenceAndStore
 */
public class OrangeDifferenceAndStoreExecutor extends OrangeRedisAbstractExecutor {
	
	/**
	 * The Redis ZSet operations implementation used to perform the actual difference and store operation.
	 */
	private OrangeRedisZSetOperations operations;

	/**
	 * Constructs a new OrangeDifferenceAndStoreExecutor with the specified operations and ID generator.
	 *
	 * @param operations the Redis ZSet operations implementation to use for executing the difference and store command
	 * @param idGenerator the generator used to create unique IDs for this executor
	 */
	public OrangeDifferenceAndStoreExecutor(OrangeRedisZSetOperations operations,OrangeRedisExecutorIdGenerator idGenerator) {
		super(idGenerator);
		this.operations = operations;
	}

	/**
	 * Returns the list of annotation classes that this executor supports.
	 * 
	 * <p>This executor supports the following annotations:
	 * <ul>
	 *   <li>{@link Difference} - Marks the method as a difference operation</li>
	 *   <li>{@link CrossOperationKeys} - Specifies the keys to operate on</li>
	 *   <li>{@link StoreTo} - Specifies the destination key where the result will be stored</li>
	 * </ul>
	 *
	 * @return a list of supported annotation classes
	 */
	@Override
	protected List<Class<? extends Annotation>> getSupportedAnnotationClasses() {
		return OrangeCollectionUtils.asList(Difference.class,CrossOperationKeys.class,StoreTo.class);
	}

	/**
	 * Executes the difference and store operation on Redis Sorted Sets.
	 * 
	 * <p>This method computes the difference between the first sorted set (reference key) and all 
	 * successive sorted sets (comparison keys) specified in the context, and stores the result in 
	 * the destination key. The difference operation returns the set of elements that exist in the 
	 * first set but not in any of the other sets.
	 *
	 * @param context the Redis context containing the operation parameters
	 * @return the number of elements in the resulting sorted set stored at the destination key
	 * @throws Exception if an error occurs during the Redis operation
	 */
	@Override
	public Object execute(OrangeRedisContext context) throws Exception {
		OrangeCrossOperationContext ctx = (OrangeCrossOperationContext) context;
		return this.operations.differenceAndStore(ctx.getReferenceKey(), ctx.getComparisonKeys(), ctx.getStoreTo());
	}
	
	/**
	 * Returns the context class that this executor uses for operation parameters.
	 * 
	 * <p>This executor uses {@link OrangeCrossOperationContext} to handle the parameters
	 * required for difference and store operations, including reference key, comparison keys,
	 * and destination key.
	 *
	 * @return the class of {@link OrangeCrossOperationContext}
	 */
	@Override
	public Class<? extends OrangeRedisContext> getContextClass() {
		return OrangeCrossOperationContext.class;
	}
}