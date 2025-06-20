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
import com.langwuyue.orange.redis.OrangeRedisException;
import com.langwuyue.orange.redis.RedisValueTypeEnum;
import com.langwuyue.orange.redis.annotation.Count;
import com.langwuyue.orange.redis.annotation.OrangeRedisOperationArg;
import com.langwuyue.orange.redis.annotation.geo.Distance;
import com.langwuyue.orange.redis.annotation.geo.SearchArgs;
import com.langwuyue.orange.redis.context.OrangeRedisValueContext;
import com.langwuyue.orange.redis.context.builder.OrangeMethodAnnotationHandler;
import com.langwuyue.orange.redis.context.builder.OrangeOperationArgAnnotationHandler;
import com.langwuyue.orange.redis.operations.OrangeRedisGeoOperations.SearchArguments;

/**
 * Context class for handling Redis GEO radius search operations.
 * 
 * <p>This class extends {@link OrangeRedisValueContext} to provide specialized functionality
 * for working with geographical radius searches in Redis. It manages distance values, units,
 * and search arguments required for GEO radius operations.
 * 
 * <p>The class supports parameters annotated with:
 * <ul>
 *   <li>{@link Distance} - for the radius distance value</li>
 *   <li>{@link SearchArgs} - for configuring search behavior</li>
 *   <li>{@link Count} - for limiting the number of results</li>
 * </ul>
 *
 * @author Liang.Zhong
 * @since 1.0.0
 */
public class OrangeRadiusContext extends OrangeRedisValueContext {

	/**
	 * The radius distance value for the GEO search.
	 * Must be a number or a string that can be converted to a number.
	 */
	@OrangeRedisOperationArg(binding = Distance.class)
	private Object value;
	
	/**
	 * The distance annotation containing unit information for the radius search.
	 * Handled by {@link OrangeOperationArgAnnotationHandler}.
	 */
	@OrangeRedisOperationArg(binding = Distance.class, valueHandler = OrangeOperationArgAnnotationHandler.class)
	private Distance distance;
	
	/**
	 * Search arguments configuration for the radius search operation.
	 * Handled by {@link OrangeMethodAnnotationHandler}.
	 */
	@OrangeRedisOperationArg(binding = SearchArgs.class, valueHandler = OrangeMethodAnnotationHandler.class)
	private SearchArgs radiusArg;
	
	/**
	 * Optional count parameter to limit the number of results.
	 * If not provided, defaults to the count specified in {@link SearchArgs}.
	 */
	@OrangeRedisOperationArg(binding = Count.class)
	private Object count;
	
	/**
	 * Constructs a new radius context with the specified parameters for Redis GEO radius operations.
	 * 
	 * @param operationOwner the class that owns the Redis operation
	 * @param operationMethod the method representing the Redis operation
	 * @param args the arguments passed to the operation
	 * @param redisKey the Redis key for the operation
	 * @param valueType the type of Redis value being operated on
	 */
	public OrangeRadiusContext(
		Class<?> operationOwner, 
		Method operationMethod, 
		Object[] args, 
		Key redisKey,
		RedisValueTypeEnum valueType
	) {
		super(operationOwner, operationMethod, args, redisKey,valueType);
	}

	/**
	 * Gets the radius distance value for the GEO search.
	 * 
	 * <p>This method converts the distance value to a Double. The value can be:
	 * <ul>
	 *   <li>A numeric type (Double, Integer, etc.)</li>
	 *   <li>A String that can be parsed to a number</li>
	 * </ul>
	 * 
	 * @return the radius distance as a Double value
	 * @throws OrangeRedisException if the value is null or cannot be converted to a Double
	 */
	public Double getDistance() {
		if(value == null) {
			throw new OrangeRedisException(String.format("The field annotated with @%s cannot be null", Distance.class));
		}
		if(!(value instanceof Number) && !(value instanceof String)) {
			throw new OrangeRedisException(String.format("The field annotated with @%s must be a number or a string", Distance.class));
		}
		return Double.valueOf(value.toString());
	}
	
	/**
	 * Gets the distance unit for the GEO radius search.
	 * 
	 * <p>The unit is specified in the {@link Distance} annotation and determines
	 * how the distance value should be interpreted (e.g., meters, kilometers, miles).
	 * 
	 * @return the geographic distance unit enum value
	 */
	public GeoDistanceUnitEnum getUnit() {
		return distance.unit();
	}
	
	/**
	 * Gets the search arguments configuration for the GEO radius search.
	 * 
	 * <p>These arguments control the behavior of the radius search operation, including:
	 * <ul>
	 *   <li>Sort order (ASC/DESC)</li>
	 *   <li>Whether to include distances in results</li>
	 *   <li>Whether to include coordinates in results</li>
	 *   <li>Any other search-specific parameters</li>
	 * </ul>
	 * 
	 * @return the search arguments configuration with all parameters set
	 */
	public SearchArguments getRadiusArguments() {
		SearchArguments arguments = new SearchArguments();
		arguments.setIncludeCoordinates(radiusArg.includeCoordinates());
		arguments.setIncludeDistance(radiusArg.includeDistance());
		arguments.setSortAscending(radiusArg.sortAscending());
		arguments.setCount(getCount());
		arguments.setAny(radiusArg.any());
		return arguments;
	}
	
	/**
	 * Gets the count limit for the number of results to return.
	 * 
	 * <p>The count can be specified in two ways:
	 * <ul>
	 *   <li>Through a parameter annotated with {@link Count}</li>
	 *   <li>Through the count value in {@link SearchArgs} annotation</li>
	 * </ul>
	 * 
	 * <p>If a count parameter is provided, it takes precedence over the count
	 * specified in the SearchArgs annotation.
	 * 
	 * @return the maximum number of results to return
	 */
	public int getCount() {
		if(count != null) {
			return Integer.valueOf(count.toString());
		}
		return radiusArg.count();
	}
}