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

import com.langwuyue.orange.redis.OrangeRedisException;
import com.langwuyue.orange.redis.RedisValueTypeEnum;
import com.langwuyue.orange.redis.annotation.OrangeRedisOperationArg;
import com.langwuyue.orange.redis.annotation.geo.Latitude;
import com.langwuyue.orange.redis.annotation.geo.Longitude;
import com.langwuyue.orange.redis.operations.OrangeRedisGeoOperations.GeoEntry;

/**
 * Context for Redis GEO radius operations that use point references (longitude/latitude).
 * <p>
 * Extends {@link OrangeRadiusContext} to handle operations where the center point
 * is specified directly via longitude and latitude coordinates rather than a member key.
 * Validates and converts the coordinate values before creating a {@link GeoEntry}.
 *
 * @author Liang.Zhong
 * @since 1.0.0
 * @see OrangeRadiusContext Base radius operation context
 * @see GeoEntry Redis GEO entry container
 */
public class OrangeRadiusPointReferenceContext extends OrangeRadiusContext {

	/**
	 * The longitude coordinate for the center point.
	 * <p>
	 * Annotated with {@link Longitude} to indicate this field holds the longitude value.
	 * Must be either a Number or String that can be parsed to a double.
	 */
	@OrangeRedisOperationArg(binding = Longitude.class)
	private Object longitude;
	
	/**
	 * The latitude coordinate for the center point.
	 * <p>
	 * Annotated with {@link Latitude} to indicate this field holds the latitude value.
	 * Must be either a Number or String that can be parsed to a double.
	 */
	@OrangeRedisOperationArg(binding = Latitude.class)
	private Object latitude;
	
	/**
	 * Constructs a new context for point-based radius operations.
	 * <p>
	 * Initializes the context with operation metadata and validates the longitude/latitude values.
	 * Ensures both coordinates are provided and valid before creating a GeoEntry.
	 *
	 * @param operationOwner The class containing the Redis operation method
	 * @param operationMethod The method annotated with Redis operation
	 * @param args The method arguments
	 * @param keys The Redis keys involved in the operation
	 * @param storeTo The target field to store results (if any)
	 * @param valueType The type of values being operated on
	 * @throws OrangeRedisException if longitude or latitude is missing or invalid
	 */
	public OrangeRadiusPointReferenceContext(
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
	 * Creates a GeoEntry from the longitude and latitude coordinates.
	 * <p>
	 * Validates that both coordinates are present and can be converted to doubles.
	 * The returned GeoEntry will have a null member name since this is a point reference.
	 *
	 * @return A new GeoEntry containing the coordinates
	 * @throws OrangeRedisException if either coordinate is null or not a valid number/string
	 */
	public GeoEntry getMember() {
		if(latitude == null) {
			throw new OrangeRedisException(String.format("The field annotated with @%s cannot be null", Latitude.class));
		}
		if(!(latitude instanceof Number) && !(latitude instanceof String)) {
			throw new OrangeRedisException(String.format("The field annotated with @%s must be a number or a string", Latitude.class));
		}
		if(longitude == null) {
			throw new OrangeRedisException(String.format("The field annotated with @%s cannot be null", Longitude.class));
		}
		if(!(longitude instanceof Number) && !(longitude instanceof String)) {
			throw new OrangeRedisException(String.format("The field annotated with @%s must be a number or a string", Longitude.class));
		}
		return new GeoEntry(null,Double.valueOf(longitude.toString()),Double.valueOf(latitude.toString()));
	}
}