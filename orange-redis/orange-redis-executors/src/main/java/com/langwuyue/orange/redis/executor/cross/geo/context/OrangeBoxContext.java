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
import com.langwuyue.orange.redis.annotation.geo.Height;
import com.langwuyue.orange.redis.annotation.geo.SearchArgs;
import com.langwuyue.orange.redis.annotation.geo.Width;
import com.langwuyue.orange.redis.context.builder.OrangeMethodAnnotationHandler;
import com.langwuyue.orange.redis.context.builder.OrangeOperationArgAnnotationHandler;
import com.langwuyue.orange.redis.executor.cross.context.OrangeCrossOperationContext;
import com.langwuyue.orange.redis.operations.OrangeRedisGeoOperations.SearchArguments;

/**
 * Context class for Redis GEO search operations within a bounding box.
 * <p>
 * Contains parameters and logic for executing geo searches within a rectangular area
 * defined by width and height dimensions. Handles validation and conversion of
 * geo search parameters including distance units, result counts, and search arguments.
 * 
 * @author Liang.Zhong
 * @since 1.0.0
 */
public class OrangeBoxContext extends OrangeCrossOperationContext {

	/** 
	 * The width parameter for the bounding box, annotated with {@code @Width}.
	 * Can be either a Number or String representing the width distance.
	 */
	@OrangeRedisOperationArg(binding = Width.class)
	private Object width;
	
	/**
	 * The {@code @Width} annotation instance containing metadata about the width parameter,
	 * including the distance unit (meters, kilometers, miles, feet).
	 */
	@OrangeRedisOperationArg(binding = Width.class, valueHandler = OrangeOperationArgAnnotationHandler.class)
	private Width widthAnnotation;
	
	/** 
	 * The height parameter for the bounding box, annotated with {@code @Height}.
	 * Can be either a Number or String representing the height distance.
	 */
	@OrangeRedisOperationArg(binding = Height.class)
	private Object height;
	
	/**
	 * The {@code @Height} annotation instance containing metadata about the height parameter,
	 * including the distance unit (meters, kilometers, miles, feet).
	 */
	@OrangeRedisOperationArg(binding = Height.class, valueHandler = OrangeOperationArgAnnotationHandler.class)
	private Height heightAnnotation;
	
	/**
	 * The {@code @SearchArgs} annotation instance containing geo search configuration:
	 * - Whether to include coordinates/distances in results
	 * - Sort direction
	 * - Default result count
	 * - Whether to return any match
	 */
	@OrangeRedisOperationArg(binding = SearchArgs.class, valueHandler = OrangeMethodAnnotationHandler.class)
	private SearchArgs searchArgs;
	
	/**
	 * The count parameter for limiting search results, annotated with @Count.
	 * If null, falls back to the default count from {@code  @SearchArgs}.
	 */
	@OrangeRedisOperationArg(binding = Count.class)
	private Object count;
	
	/**
	 * The value object containing search criteria, annotated with {@code @RedisValue}.
	 * Must not be null and contains the parameters for the geo search operation.
	 */
	@OrangeRedisOperationArg(binding = RedisValue.class)
	private Object value;
	
	/**
	 * Constructs a new context for bounding box geo search operations.
	 *
	 * @param operationOwner The class containing the Redis operation method
	 * @param operationMethod The method annotated with Redis operation
	 * @param args The arguments passed to the operation method
	 * @param keys The Redis keys involved in the operation
	 * @param storeTo The target key to store results
	 * @param valueType The type of value being stored
	 */
	public OrangeBoxContext(
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
	 * Gets the height dimension of the bounding box.
	 * <p>
	 * Validates that the height value is not null and is either a Number or String.
	 * 
	 * @return The height value converted to Double
	 * @throws OrangeRedisException if height is null or not a valid numeric type
	 */
	public Double getHeight() {
		if(height == null) {
			throw new OrangeRedisException(String.format("The field annotated with @%s cannot be null", Height.class));
		}
		if(!(height instanceof Number) && !(width instanceof String)) {
			throw new OrangeRedisException(String.format("The field annotated with @%s must be a number or a string", Height.class));
		}
		return Double.valueOf(height.toString());
	}
	
	/**
	 * Gets the width dimension of the bounding box.
	 * <p>
	 * Validates that the width value is not null and is either a Number or String.
	 * 
	 * @return The width value converted to Double
	 * @throws OrangeRedisException if width is null or not a valid numeric type
	 */
	public Double getWidth() {
		if(width == null) {
			throw new OrangeRedisException(String.format("The field annotated with @%s cannot be null", Width.class));
		}
		if(!(width instanceof Number) && !(width instanceof String)) {
			throw new OrangeRedisException(String.format("The field annotated with @%s must be a number or a string", Width.class));
		}
		return Double.valueOf(width.toString());
	}
	
	/**
	 * Gets the distance unit for the width dimension.
	 * 
	 * @return The width distance unit from the Width annotation
	 */
	public GeoDistanceUnitEnum getWidthUnit() {
		return widthAnnotation.unit();
	}
	
	/**
	 * Gets the distance unit for the height dimension.
	 * 
	 * @return The height distance unit from the Height annotation
	 */
	public GeoDistanceUnitEnum getHeightUnit() {
		return heightAnnotation.unit();
	}
	
	/**
	 * Gets the search arguments configuration for the geo search operation.
	 * <p>
	 * Combines annotation configuration with runtime parameters to create a complete
	 * SearchArguments object containing:
	 * <ul>
	 *   <li>Whether to include coordinates in results</li>
	 *   <li>Whether to include distances in results</li>
	 *   <li>Sort direction (ascending/descending)</li>
	 *   <li>Maximum number of results to return</li>
	 *   <li>Whether to return any match (true) or all matches (false)</li>
	 * </ul>
	 * 
	 * @return Configured SearchArguments object containing all geo search parameters
	 */
	public SearchArguments getSearchArguments() {
		SearchArguments arguments = new SearchArguments();
		arguments.setIncludeCoordinates(searchArgs.includeCoordinates());
		arguments.setIncludeDistance(searchArgs.includeDistance());
		arguments.setSortAscending(searchArgs.sortAscending());
		arguments.setCount(getCount());
		arguments.setAny(searchArgs.any());
		return arguments;
	}
	
	/**
	 * Gets the maximum number of results to return from the geo search.
	 * <p>
	 * Uses either the explicitly provided count parameter or falls back to
	 * the default value specified in the SearchArgs annotation.
	 * 
	 * @return The maximum number of results to return
	 */
	public int getCount() {
		if(count != null) {
			return Integer.valueOf(count.toString());
		}
		return searchArgs.count();
	}
	
	/**
	 * Gets the value object containing the search criteria.
	 * <p>
	 * The value object can be of any type and contains the parameters
	 * used for the geo search operation. The actual type depends on
	 * the specific search implementation.
	 * 
	 * @return The search criteria value object
	 */
	public Object getValue() {
		if(value == null) {
			throw new OrangeRedisException(String.format("The argument annotated with @%s cannot be null", RedisValue.class));
		}
		return value;
	}
}