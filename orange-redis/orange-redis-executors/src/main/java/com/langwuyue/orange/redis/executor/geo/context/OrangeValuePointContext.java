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
import com.langwuyue.orange.redis.context.OrangeRedisValueContext;
import com.langwuyue.orange.redis.operations.OrangeRedisGeoOperations.GeoEntry;

/**
 * Context class for handling geographical point information in Redis GEO operations.
 * 
 * <p>This class extends {@link OrangeRedisValueContext} to provide specific functionality
 * for managing geographical coordinates (longitude and latitude) associated with a value
 * in Redis GEO operations. It handles the binding of longitude and latitude values
 * through {@link Longitude} and {@link Latitude} annotations respectively.
 * 
 * <p>The context is used to create {@link GeoEntry} objects that represent geographical
 * points with their associated values in Redis GEO operations. It includes validation
 * of coordinate values to ensure they are properly formatted and within valid ranges.
 *
 * @author Liang.Zhong
 * @since 1.0.0
 * @see Longitude
 * @see Latitude
 * @see GeoEntry
 * @see OrangeRedisValueContext
 */
public class OrangeValuePointContext extends OrangeRedisValueContext {

	/**
	 * The longitude coordinate for the geographical point.
	 * 
	 * <p>This field holds the longitude value that was bound from a parameter
	 * annotated with {@link Longitude} in the operation method. It represents
	 * the east-west position of a point on the Earth's surface.
	 * 
	 * <p>The value must be a number or a string that can be converted to a double.
	 * Valid longitude values range from -180 to 180 degrees.
	 */
	@OrangeRedisOperationArg(binding = Longitude.class)
	private Object longitude;
	
	/**
	 * The latitude coordinate for the geographical point.
	 * 
	 * <p>This field holds the latitude value that was bound from a parameter
	 * annotated with {@link Latitude} in the operation method. It represents
	 * the north-south position of a point on the Earth's surface.
	 * 
	 * <p>The value must be a number or a string that can be converted to a double.
	 * Valid latitude values range from -90 to 90 degrees.
	 */
	@OrangeRedisOperationArg(binding = Latitude.class)
	private Object latitude;

	/**
	 * Constructs a new OrangeValuePointContext with the specified parameters.
	 * 
	 * <p>This constructor initializes a context for handling geographical point information
	 * in Redis GEO operations. It sets up the necessary context for creating GeoEntry objects
	 * that represent points with their associated values.
	 *
	 * @param operationOwner the class that owns the operation method
	 * @param operationMethod the method representing the Redis operation
	 * @param args the arguments passed to the operation method
	 * @param redisKey the Redis key to operate on
	 * @param valueType the type of Redis value being operated on
	 */
	public OrangeValuePointContext(
		Class<?> operationOwner, 
		Method operationMethod, 
		Object[] args,
		Key redisKey,
		RedisValueTypeEnum valueType
	) {
		super(operationOwner, operationMethod, args, redisKey,valueType);
	}

	/**
	 * Creates and returns a GeoEntry object representing a geographical point with its associated value.
	 * 
	 * <p>This method validates the longitude and latitude values to ensure they are not null
	 * and are either numbers or strings that can be converted to double values. It then creates
	 * a GeoEntry object that combines the value with its geographical coordinates.
	 *
	 * @return a GeoEntry object containing the value and its geographical coordinates
	 * @throws OrangeRedisException if the latitude or longitude is null, or if they are not
	 *         numbers or strings that can be converted to double values
	 * @see GeoEntry
	 */
	public GeoEntry getMember() {
		Object value = getValue();
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
		return new GeoEntry(value,Double.valueOf(longitude.toString()),Double.valueOf(latitude.toString()));
	}
}