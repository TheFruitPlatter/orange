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
import com.langwuyue.orange.redis.annotation.geo.Height;
import com.langwuyue.orange.redis.annotation.geo.SearchArgs;
import com.langwuyue.orange.redis.annotation.geo.Width;
import com.langwuyue.orange.redis.context.OrangeRedisValueContext;
import com.langwuyue.orange.redis.context.builder.OrangeMethodAnnotationHandler;
import com.langwuyue.orange.redis.context.builder.OrangeOperationArgAnnotationHandler;
import com.langwuyue.orange.redis.operations.OrangeRedisGeoOperations.SearchArguments;

/**
 * Context class for handling geographic bounding box operations in Redis.
 * 
 * <p>This context manages the parameters for searching within a rectangular area defined by:
 * <ul>
 *   <li>Width - the horizontal dimension of the box</li>
 *   <li>Height - the vertical dimension of the box</li>
 * </ul>
 * 
 * <p>Supports additional search parameters like:
 * <ul>
 *   <li>Result count limit</li>
 *   <li>Sorting order</li>
 *   <li>Whether to include coordinates/distance in results</li>
 * </ul>
 * 
 *
 * @author Liang.Zhong
 * @since 1.0.0
 */
public class OrangeBoxContext extends OrangeRedisValueContext {

	/**
	 * The width value of the bounding box, can be a Number or String.
	 * Bound to method parameter annotated with {@link com.langwuyue.orange.redis.annotation.geo.Width}.
	 */
	@OrangeRedisOperationArg(binding = Width.class)
	private Object width;
	
	/**
	 * The width annotation containing unit information.
	 * Bound to method parameter annotated with {@link com.langwuyue.orange.redis.annotation.geo.Width}.
	 */
	@OrangeRedisOperationArg(binding = Width.class, valueHandler = OrangeOperationArgAnnotationHandler.class)
	private Width widthAnnotation;
	
	/**
	 * The height value of the bounding box, can be a Number or String.
	 * Bound to method parameter annotated with {@link com.langwuyue.orange.redis.annotation.geo.Height}.
	 */
	@OrangeRedisOperationArg(binding = Height.class)
	private Object height;
	
	/**
	 * The height annotation containing unit information.
	 * Bound to method parameter annotated with {@link com.langwuyue.orange.redis.annotation.geo.Height}.
	 */
	@OrangeRedisOperationArg(binding = Height.class, valueHandler = OrangeOperationArgAnnotationHandler.class)
	private Height heightAnnotation;
	
	/**
	 * Additional search arguments for the bounding box query.
	 * Bound to method annotation {@link com.langwuyue.orange.redis.annotation.geo.SearchArgs}.
	 */
	@OrangeRedisOperationArg(binding = SearchArgs.class, valueHandler = OrangeMethodAnnotationHandler.class)
	private SearchArgs searchArgs;
	
	/**
	 * Optional result count limit for the search.
	 * Bound to method parameter annotated with {@link com.langwuyue.orange.redis.annotation.Count}.
	 */
	@OrangeRedisOperationArg(binding = Count.class)
	private Object count;
	
	/**
	 * Constructs a new context for bounding box geo operations.
	 * 
	 * @param operationOwner the class that declares the Redis operation method
	 * @param operationMethod the method annotated with Redis geo operation
	 * @param args the arguments passed to the operation method
	 * @param redisKey the Redis key associated with this operation
	 * @param valueType the Redis value type for this operation
	 */
	public OrangeBoxContext(
		Class<?> operationOwner, 
		Method operationMethod, 
		Object[] args,
		Key redisKey,
		RedisValueTypeEnum valueType
	) {
		super(operationOwner, operationMethod, args, redisKey,valueType);
	}

	/**
	 * Gets the height value of the bounding box as a Double.
	 * 
	 * @return the height value converted to Double
	 * @throws OrangeRedisException if the height value is null or not a Number/String
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
	 * Gets the width value of the bounding box as a Double.
	 * 
	 * @return the width value converted to Double
	 * @throws OrangeRedisException if the width value is null or not a Number/String
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
	 * Gets the unit of measurement for the width value.
	 * 
	 * @return the width unit enum (e.g. METERS, KILOMETERS, MILES, FEET)
	 * @throws OrangeRedisException if width annotation is not present
	 */
	public GeoDistanceUnitEnum getWidthUnit() {
		return widthAnnotation.unit();
	}
	
	/**
	 * Gets the unit of measurement for the height value.
	 * 
	 * @return the height unit enum (e.g. METERS, KILOMETERS, MILES, FEET)
	 * @throws OrangeRedisException if height annotation is not present
	 */
	public GeoDistanceUnitEnum getHeightUnit() {
		return heightAnnotation.unit();
	}
	
	/**
	 * Gets the search arguments for the bounding box query.
	 * 
	 * @return SearchArguments object containing:
	 *         - includeCoordinates flag
	 *         - includeDistance flag  
	 *         - sortAscending flag
	 *         - count limit
	 *         - any flag
	 * @throws OrangeRedisException if search arguments are not properly configured
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
	 * Gets the count limit for search results.
	 * 
	 * @return the count value from either the Count annotation parameter or SearchArgs annotation,
	 *         whichever is specified (Count parameter takes precedence)
	 */
	public int getCount() {
		if(count != null) {
			return Integer.valueOf(count.toString());
		}
		return searchArgs.count();
	}
}