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

import com.langwuyue.orange.redis.OrangeRedisException;
import com.langwuyue.orange.redis.RedisValueTypeEnum;
import com.langwuyue.orange.redis.annotation.OrangeRedisOperationArg;
import com.langwuyue.orange.redis.annotation.geo.Latitude;
import com.langwuyue.orange.redis.annotation.geo.Longitude;
import com.langwuyue.orange.redis.operations.OrangeRedisGeoOperations.GeoEntry;

/**
 * Context class for Redis GEO radius search operations that use longitude and latitude coordinates
 * as the center point reference.
 * 
 * <p>This class extends {@link OrangeRadiusContext} to support GEO radius searches where the center
 * point is specified by explicit longitude and latitude coordinates, rather than by a member name.
 * It handles parameters annotated with {@link Longitude} and {@link Latitude} to define the
 * center point of the radius search.
 *
 * @author Liang.Zhong
 * @since 1.0.0
 * @see OrangeRadiusContext
 * @see Longitude
 * @see Latitude
 */
public class OrangeRadiusPointReferenceContext extends OrangeRadiusContext {

	/**
	 * The longitude coordinate of the center point for the GEO radius search.
	 * 
	 * <p>This field is bound to a parameter annotated with {@link Longitude} and
	 * represents the east-west position on the Earth's surface.
	 * 
	 * <p>The value can be a number or a string that can be parsed to a double.
	 */
	@OrangeRedisOperationArg(binding = Longitude.class)
	private Object longitude;
	
	/**
	 * The latitude coordinate of the center point for the GEO radius search.
	 * 
	 * <p>This field is bound to a parameter annotated with {@link Latitude} and
	 * represents the north-south position on the Earth's surface.
	 * 
	 * <p>The value can be a number or a string that can be parsed to a double.
	 */
	@OrangeRedisOperationArg(binding = Latitude.class)
	private Object latitude;
	
	/**
	 * Constructs a new OrangeRadiusPointReferenceContext with the specified parameters.
	 * 
	 * <p>This constructor initializes a context for GEO radius search operations where the
	 * center point is specified by longitude and latitude coordinates.
	 *
	 * @param operationOwner the class that owns the operation method
	 * @param operationMethod the method representing the Redis operation
	 * @param args the arguments passed to the operation method
	 * @param redisKey the Redis key to operate on
	 * @param valueType the type of Redis value being operated on
	 */
	public OrangeRadiusPointReferenceContext(
		Class<?> operationOwner, 
		Method operationMethod, 
		Object[] args,
		Key redisKey,
		RedisValueTypeEnum valueType
	) {
		super(operationOwner, operationMethod, args, redisKey,valueType);
	}

	/**
	 * Creates and returns a GeoEntry representing the center point for the radius search.
	 * 
	 * <p>This method validates the longitude and latitude values and creates a GeoEntry
	 * with these coordinates. The GeoEntry is used as the reference point for the
	 * GEO radius search operation.
	 * 
	 * <p>The method performs the following validations:
	 * <ul>
	 *   <li>Checks that both latitude and longitude are not null</li>
	 *   <li>Verifies that both values are either Number or String instances</li>
	 * </ul>
	 *
	 * @return a GeoEntry with the specified longitude and latitude coordinates
	 * @throws OrangeRedisException if latitude or longitude is null or not a number/string
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