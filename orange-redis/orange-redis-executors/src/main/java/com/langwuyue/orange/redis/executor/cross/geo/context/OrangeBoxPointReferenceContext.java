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
 * Context class for geo operations using point references (longitude/latitude).
 * <p>
 * Extends {@link OrangeBoxContext} to handle geographic point operations,
 * validating and converting longitude/latitude parameters into {@link GeoEntry} objects.
 * 
 * @author Liang.Zhong
 * @since 1.0.0
 * @see OrangeBoxContext Base class for common geo operation context
 * @see GeoEntry Redis geo entry representation
 */
public class OrangeBoxPointReferenceContext extends OrangeBoxContext {

	/** 
	 * The longitude parameter for the geo point, annotated with @Longitude.
	 * Must be either a Number or String representing the longitude coordinate.
	 * Cannot be null.
	 */
	@OrangeRedisOperationArg(binding = Longitude.class)
	private Object longitude;
	
	/** 
	 * The latitude parameter for the geo point, annotated with @Latitude.
	 * Must be either a Number or String representing the latitude coordinate.
	 * Cannot be null.
	 */
	@OrangeRedisOperationArg(binding = Latitude.class)
	private Object latitude;
	
	/**
	 * Constructs a new context for geo point operations.
	 * <p>
	 * Initializes the context by processing the operation arguments and validating
	 * the required longitude and latitude parameters.
	 *
	 * @param operationOwner The class that owns the Redis operation method
	 * @param operationMethod The Redis operation method being executed
	 * @param args The arguments passed to the operation method
	 * @param keys The Redis keys involved in the operation
	 * @param storeTo The target key where results should be stored (optional)
	 * @param valueType The type of Redis value being operated on
	 * @throws IllegalArgumentException if required arguments are missing or invalid
	 */
	public OrangeBoxPointReferenceContext(
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
	 * Converts the longitude and latitude parameters into a {@link GeoEntry} object.
	 * <p>
	 * Validates that both longitude and latitude are present and of the correct type
	 * (Number or String) before conversion.
	 *
	 * @return A new GeoEntry with the converted coordinates
	 * @throws OrangeRedisException if longitude or latitude is null or of invalid type
	 * @see GeoEntry Redis geo entry representation
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