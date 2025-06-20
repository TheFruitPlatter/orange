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
package com.langwuyue.orange.redis.executor.geo.context;

import java.lang.reflect.Method;

import com.langwuyue.orange.redis.GeoDistanceUnitEnum;
import com.langwuyue.orange.redis.RedisValueTypeEnum;
import com.langwuyue.orange.redis.annotation.OrangeRedisOperationArg;
import com.langwuyue.orange.redis.annotation.geo.Distance;
import com.langwuyue.orange.redis.context.OrangeRedisMultipleValueContext;
import com.langwuyue.orange.redis.context.builder.OrangeMethodAnnotationHandler;

/**
 * Context class for Redis GEO distance computation operations.
 * 
 * <p>This class extends {@link OrangeRedisMultipleValueContext} to provide specific functionality
 * for computing distances between GEO points in Redis. It handles the distance unit configuration
 * through the {@link Distance} annotation and provides access to the distance unit settings.
 * 
 * <p>The context is used in Redis GEO operations that involve distance calculations between
 * geographical points, such as determining the distance between two locations stored in
 * a Redis GEO index.
 *
 * @author Liang.Zhong
 * @since 1.0.0
 * @see Distance
 * @see GeoDistanceUnitEnum
 * @see OrangeRedisMultipleValueContext
 */
public class OrangeRedisComputeDistanceContext extends OrangeRedisMultipleValueContext {
	
	/**
	 * The distance configuration for GEO operations.
	 * 
	 * <p>This field holds the {@link Distance} annotation instance that was used to mark
	 * a parameter in the operation method. It contains configuration for the distance unit
	 * to be used in GEO distance calculations.
	 * 
	 * <p>The field is bound to a parameter annotated with {@link Distance} and its value
	 * is handled by {@link OrangeMethodAnnotationHandler}.
	 */
	@OrangeRedisOperationArg(binding = Distance.class, valueHandler = OrangeMethodAnnotationHandler.class)
	private Distance distance;
	
	/**
	 * Constructs a new OrangeRedisComputeDistanceContext with the specified parameters.
	 * 
	 * <p>This constructor initializes a context for Redis GEO distance computation operations.
	 * It sets up the necessary context for calculating distances between geographical points
	 * stored in Redis.
	 *
	 * @param operationOwner the class that owns the operation method
	 * @param operationMethod the method representing the Redis operation
	 * @param args the arguments passed to the operation method
	 * @param redisKey the Redis key to operate on
	 * @param valueType the type of Redis value being operated on
	 */
	public OrangeRedisComputeDistanceContext(
		Class<?> operationOwner, 
		Method operationMethod, 
		Object[] args,
		Key redisKey,
		RedisValueTypeEnum valueType
	) {
		super(operationOwner, operationMethod, args, redisKey,valueType);
	}

	/**
	 * Retrieves the distance unit configured for this GEO operation.
	 * 
	 * <p>This method returns the distance unit specified in the {@link Distance} annotation
	 * that was used to configure the GEO operation. The unit determines how distances
	 * between geographical points will be measured and returned.
	 *
	 * @return the {@link GeoDistanceUnitEnum} value representing the configured distance unit
	 * @see Distance#unit()
	 * @see GeoDistanceUnitEnum
	 */
	public GeoDistanceUnitEnum getUnit() {
		return distance.unit();
	}
	
}