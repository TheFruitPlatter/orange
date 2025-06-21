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
package com.langwuyue.orange.redis.executor.cross.geo.context;

import java.lang.reflect.Method;
import java.util.List;

import com.langwuyue.orange.redis.GeoDistanceUnitEnum;
import com.langwuyue.orange.redis.OrangeRedisException;
import com.langwuyue.orange.redis.RedisValueTypeEnum;
import com.langwuyue.orange.redis.annotation.Count;
import com.langwuyue.orange.redis.annotation.OrangeRedisOperationArg;
import com.langwuyue.orange.redis.annotation.RedisValue;
import com.langwuyue.orange.redis.annotation.geo.Distance;
import com.langwuyue.orange.redis.annotation.geo.SearchArgs;
import com.langwuyue.orange.redis.context.builder.OrangeMethodAnnotationHandler;
import com.langwuyue.orange.redis.context.builder.OrangeOperationArgAnnotationHandler;
import com.langwuyue.orange.redis.executor.cross.context.OrangeCrossOperationContext;
import com.langwuyue.orange.redis.operations.OrangeRedisGeoOperations.SearchArguments;

/**
 * Context class for Redis geo radius operations.
 * <p>
 * Handles the execution context for geo radius queries, including distance, unit,
 * search arguments and result count parameters. Validates and converts these parameters
 * for use with Redis geo commands.
 * 
 * @author Liang.Zhong
 * @since 1.0.0
 * @see OrangeCrossOperationContext Base class for cross operation contexts
 * @see GeoDistanceUnitEnum Distance unit enumeration
 * @see SearchArguments Geo search arguments container
 */
public class OrangeRadiusContext extends OrangeCrossOperationContext {

	/** 
	 * The distance parameter for the radius search, annotated with @Distance.
	 * Must be either a Number or String representing the radius distance.
	 */
	@OrangeRedisOperationArg(binding = Distance.class)
	private Object distance;
	
	/** 
	 * The @Distance annotation instance for this operation.
	 * Provides access to the distance unit and other annotation properties.
	 */
	@OrangeRedisOperationArg(binding = Distance.class, valueHandler = OrangeOperationArgAnnotationHandler.class)
	private Distance distanceAnnotation;
	
	/** 
	 * The @SearchArgs annotation instance for this operation.
	 * Contains additional search parameters like sorting, coordinate inclusion etc.
	 */
	@OrangeRedisOperationArg(binding = SearchArgs.class, valueHandler = OrangeMethodAnnotationHandler.class)
	private SearchArgs radiusArg;
	
	/** 
	 * The count parameter for limiting results, annotated with @Count.
	 * Optional parameter that can be either a Number or String.
	 */
	@OrangeRedisOperationArg(binding = Count.class)
	private Object count;
	
	/** 
	 * The value parameter for the operation, annotated with @RedisValue.
	 * Represents the main value being operated on.
	 */
	@OrangeRedisOperationArg(binding = RedisValue.class)
	private Object value;
	
	/**
	 * Constructs a new context for geo radius operations.
	 * <p>
	 * Initializes the context by processing the operation arguments and validating
	 * the required distance and other geo parameters.
	 *
	 * @param operationOwner The class that owns the Redis operation method
	 * @param operationMethod The Redis operation method being executed
	 * @param args The arguments passed to the operation method
	 * @param keys The Redis keys involved in the operation
	 * @param storeTo The target key where results should be stored (optional)
	 * @param valueType The type of Redis value being operated on
	 * @throws IllegalArgumentException if required arguments are missing or invalid
	 */
	public OrangeRadiusContext(
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
	 * Gets the distance parameter for the radius search.
	 * <p>
	 * Converts the distance parameter to a Double value. Validates that the distance
	 * is present and of the correct type (Number or String) before conversion.
	 *
	 * @return The radius distance as a Double
	 * @throws OrangeRedisException if distance is null or of invalid type
	 */
	public Double getDistance() {
		if(distance == null) {
			throw new OrangeRedisException(String.format("The field annotated with @%s cannot be null", Distance.class));
		}
		if(!(distance instanceof Number) && !(distance instanceof String)) {
			throw new OrangeRedisException(String.format("The field annotated with @%s must be a number or a string", Distance.class));
		}
		return Double.valueOf(distance.toString());
	}
	
	/**
	 * Gets the distance unit for the radius search.
	 * <p>
	 * Extracts the unit from the @Distance annotation. Defaults to METERS if not specified.
	 *
	 * @return The distance unit (METERS, KILOMETERS, MILES, or FEET)
	 * @see GeoDistanceUnitEnum Distance unit enumeration
	 */
	public GeoDistanceUnitEnum getUnit() {
		return distanceAnnotation.unit();
	}
	
	/**
	 * Gets the search arguments for the radius operation.
	 * <p>
	 * Converts the @SearchArgs annotation into a SearchArguments object containing
	 * parameters like sorting direction, coordinate inclusion flags, etc.
	 *
	 * @return Configured search arguments for the radius query
	 * @throws OrangeRedisException if arguments are invalid or missing
	 * @see SearchArguments Geo search arguments container
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
	 * Gets the count parameter for limiting results.
	 * <p>
	 * First checks if an explicit count parameter was provided via @Count annotation.
	 * If not, falls back to the count specified in the @SearchArgs annotation.
	 * 
	 * @return The result count limit as an int
	 * @throws NumberFormatException if count cannot be parsed to an integer
	 */
	public int getCount() {
		if(count != null) {
			return Integer.valueOf(count.toString());
		}
		return radiusArg.count();
	}
	
	/**
	 * Gets the main value parameter for the operation.
	 * <p>
	 * Validates that the value annotated with @RedisValue is not null.
	 * The actual type of the returned object depends on the operation.
	 *
	 * @return The operation's main value
	 * @throws OrangeRedisException if value is null
	 */
	public Object getValue() {
		if(value == null) {
			throw new OrangeRedisException(String.format("The argument annotated with @%s cannot be null", RedisValue.class));
		}
		return value;
	}
}