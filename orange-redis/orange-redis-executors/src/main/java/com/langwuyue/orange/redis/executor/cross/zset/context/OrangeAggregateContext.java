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
package com.langwuyue.orange.redis.executor.cross.zset.context;

import java.lang.reflect.Method;
import java.util.List;

import com.langwuyue.orange.redis.RedisValueTypeEnum;
import com.langwuyue.orange.redis.annotation.OrangeRedisOperationArg;
import com.langwuyue.orange.redis.annotation.cross.Aggregate;
import com.langwuyue.orange.redis.context.builder.OrangeMethodAnnotationHandler;
import com.langwuyue.orange.redis.executor.cross.context.OrangeCrossOperationContext;

/**
 * Context class for handling Redis Sorted Set (ZSet) aggregate operations.
 * 
 * <p>This class extends {@link OrangeCrossOperationContext} to provide specific functionality
 * for managing aggregate operations on multiple Redis Sorted Sets. It handles the aggregation
 * strategy defined by the {@link Aggregate} annotation when performing operations like
 * ZUNIONSTORE and ZINTERSTORE with multiple sets.
 * 
 * <p>The aggregation strategy determines how the scores from multiple sets should be
 * combined in the result set. For example, scores can be summed, averaged, or the
 * minimum/maximum value can be taken.
 *
 * @author Liang.Zhong
 * @since 1.0.0
 * @see Aggregate
 * @see OrangeCrossOperationContext
 * @see OrangeMethodAnnotationHandler
 */
public class OrangeAggregateContext extends OrangeCrossOperationContext {
	
	/**
	 * The aggregation strategy to be used when combining scores from multiple sorted sets.
	 * 
	 * <p>This field holds the {@link Aggregate} annotation value that was bound from the operation method.
	 * It defines how scores should be combined when performing operations across multiple sorted sets.
	 * 
	 * <p>The aggregation strategy can be:
	 * <ul>
	 *   <li>SUM - Sum the scores from all sets (default)</li>
	 *   <li>MIN - Take the minimum score across all sets</li>
	 *   <li>MAX - Take the maximum score across all sets</li>
	 * </ul>
	 * 
	 * <p>This field is populated by the {@link OrangeMethodAnnotationHandler} which extracts
	 * the annotation from the operation method.
	 */
	@OrangeRedisOperationArg(binding = Aggregate.class, valueHandler = OrangeMethodAnnotationHandler.class)
	private Aggregate aggregate;

	/**
	 * Constructs a new OrangeAggregateContext with the specified parameters.
	 * 
	 * <p>This constructor initializes a context for handling Redis Sorted Set aggregate operations.
	 * It sets up the necessary context for performing operations that combine multiple sorted sets
	 * using a specific aggregation strategy.
	 *
	 * @param operationOwner the class that owns the operation method
	 * @param operationMethod the method representing the Redis operation
	 * @param args the arguments passed to the operation method
	 * @param keys the list of Redis keys to operate on
	 * @param storeTo the destination key where the result will be stored
	 * @param valueType the type of Redis value being operated on
	 */
	public OrangeAggregateContext(
		Class<?> operationOwner, 
		Method operationMethod, 
		Object[] args,
		List<String> keys,
		String storeTo,
		RedisValueTypeEnum valueType
	) {
		super(operationOwner, operationMethod, args, keys, storeTo, valueType);
	}
	
	/**
	 * Gets the aggregation strategy to be used in the operation.
	 * 
	 * <p>Returns the {@link Aggregate} value that defines how scores from multiple
	 * sorted sets should be combined. This value is extracted from the {@link Aggregate}
	 * annotation on the operation method.
	 *
	 * @return the {@link Aggregate} strategy to be used for combining scores
	 */
	public Aggregate getAggregate() {
		return aggregate;
	}
}