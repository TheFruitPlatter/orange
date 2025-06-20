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
 * Context class for handling geographical point references with longitude and latitude.
 * 
 * <p>This context extends {@link OrangeBoxContext} to provide specific functionality
 * for working with geographical coordinates. It manages longitude and latitude values
 * that are annotated with {@link Longitude} and {@link Latitude} respectively.
 * 
 * <p>The class validates that longitude and latitude values are either numbers or
 * strings that can be converted to double values, and provides a method to create
 * a {@link GeoEntry} from these coordinates.
 *
 * @author Liang.Zhong
 * @since 1.0.0
 */
public class OrangeBoxPointReferenceContext extends OrangeBoxContext {

	/**
	 * The longitude coordinate of the geographical point.
	 * Must be a number or a string that can be converted to a double value.
	 */
	@OrangeRedisOperationArg(binding = Longitude.class)
	private Object longitude;
	
	/**
	 * The latitude coordinate of the geographical point.
	 * Must be a number or a string that can be converted to a double value.
	 */
	@OrangeRedisOperationArg(binding = Latitude.class)
	private Object latitude;
	
	/**
	 * Constructs a new box point reference context with the specified parameters.
	 * 
	 * @param operationOwner the class that owns the Redis operation
	 * @param operationMethod the method representing the Redis operation
	 * @param args the arguments passed to the operation
	 * @param redisKey the Redis key for the operation
	 * @param valueType the type of Redis value being operated on
	 */
	public OrangeBoxPointReferenceContext(
		Class<?> operationOwner, 
		Method operationMethod, 
		Object[] args,
		Key redisKey,
		RedisValueTypeEnum valueType
	) {
		super(operationOwner, operationMethod, args, redisKey,valueType);
	}

	/**
	 * Creates a GeoEntry from the longitude and latitude values stored in this context.
	 * 
	 * <p>This method performs validation on the longitude and latitude values:
	 * <ul>
	 *   <li>Neither value can be null</li>
	 *   <li>Both values must be either numbers or strings that can be converted to double values</li>
	 * </ul>
	 * 
	 * @return a new GeoEntry instance with the specified coordinates
	 * @throws OrangeRedisException if either coordinate is null or of invalid type
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