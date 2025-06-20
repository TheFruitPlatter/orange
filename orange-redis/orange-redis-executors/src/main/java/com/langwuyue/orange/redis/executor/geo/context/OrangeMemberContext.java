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

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

import com.langwuyue.orange.redis.OrangeRedisException;
import com.langwuyue.orange.redis.RedisValueTypeEnum;
import com.langwuyue.orange.redis.annotation.RedisValue;
import com.langwuyue.orange.redis.annotation.geo.Latitude;
import com.langwuyue.orange.redis.annotation.geo.Longitude;
import com.langwuyue.orange.redis.context.OrangeRedisContext;
import com.langwuyue.orange.redis.operations.OrangeRedisGeoOperations.GeoEntry;
import com.langwuyue.orange.redis.utils.OrangeReflectionUtils;

/**
 * Context class for handling Redis GEO operation members with location and coordinate information.
 * 
 * <p>This class extends {@link OrangeRedisContext} to provide specialized functionality
 * for working with geographical entries in Redis. It handles the conversion of annotated
 * objects into {@link GeoEntry} instances, which are used in Redis GEO operations.
 * 
 * <p>The class supports objects that have fields annotated with:
 * <ul>
 *   <li>{@link RedisValue} - for the location identifier</li>
 *   <li>{@link Latitude} - for the latitude coordinate</li>
 *   <li>{@link Longitude} - for the longitude coordinate</li>
 * </ul>
 *
 * @author Liang.Zhong
 * @since 1.0.0
 */
public class OrangeMemberContext extends OrangeRedisContext {

	/**
	 * Constructs a new member context with the specified parameters for Redis GEO operations.
	 * 
	 * @param operationOwner the class that owns the Redis operation
	 * @param operationMethod the method representing the Redis operation
	 * @param args the arguments passed to the operation
	 * @param redisKey the Redis key for the operation
	 * @param valueType the type of Redis value being operated on
	 */
	public OrangeMemberContext(
		Class<?> operationOwner, 
		Method operationMethod, 
		Object[] args, 
		Key redisKey,
		RedisValueTypeEnum valueType
	) {
		super(operationOwner, operationMethod, args, redisKey, valueType);
	}

	/**
	 * Converts an object to a GeoEntry for use in Redis GEO operations.
	 * 
	 * <p>This method analyzes the provided object and extracts:
	 * <ul>
	 *   <li>A member name (from fields annotated with {@link RedisValue})</li>
	 *   <li>Longitude and latitude coordinates (from fields annotated with {@link Longitude} and {@link Latitude})</li>
	 * </ul>
	 * 
	 * <p>The method performs extensive validation to ensure:
	 * <ul>
	 *   <li>The member object is not null</li>
	 *   <li>Required annotations are present</li>
	 *   <li>Coordinate values are valid numbers or convertible to numbers</li>
	 * </ul>
	 * 
	 * <p>If the member is already a GeoEntry instance, it is returned directly.
	 * 
	 * @param member the object to convert to a GeoEntry
	 * @param annotationClass the annotation class that marks the member in the calling context
	 * @return a new GeoEntry instance with the extracted member name and coordinates, or null if member is null
	 * @throws OrangeRedisException if required annotations are missing or coordinate values are invalid
	 */
	protected GeoEntry toGeoEntry(Object member, Class<? extends Annotation> annotationClass) {
		if(member == null) {
			return null;
		}
		if(member instanceof GeoEntry) {
			return (GeoEntry)member;
		}
		Field locationField = null;
		Field latitudeField = null;
		Field longitudeField = null;
		Field[] fields = member.getClass().getDeclaredFields();
		for(Field field : fields) {
			if(field.isAnnotationPresent(RedisValue.class)) {
				locationField = field;
			}
			else if(field.isAnnotationPresent(Latitude.class)) {
				latitudeField = field;
			}
			else if(field.isAnnotationPresent(Longitude.class)) {
				longitudeField = field;
			}
			if(locationField != null && latitudeField != null && longitudeField != null) {
				break;
			}
		}
		if(locationField == null || latitudeField == null || longitudeField == null) {
			throw new OrangeRedisException(
					String.format("The argument annotated with @%s must have three fields annotated with @%s @%s @%s", 
							annotationClass,RedisValue.class,Latitude.class,Longitude.class
					)
			);
		}
		Object location = OrangeReflectionUtils.getFieldValue(locationField, member);
		if(location == null) {
			throw new OrangeRedisException(String.format("The field annotated with @%s cannot be null", RedisValue.class));
		}
		Object latitude = OrangeReflectionUtils.getFieldValue(latitudeField, member);
		if(latitude == null) {
			throw new OrangeRedisException(String.format("The field annotated with @%s cannot be null", Latitude.class));
		}
		if(!(latitude instanceof Number) && !(latitude instanceof String)) {
			throw new OrangeRedisException(String.format("The field annotated with @%s must be a number or a string", Latitude.class));
		}
		Object longitude = OrangeReflectionUtils.getFieldValue(longitudeField, member);
		if(longitude == null) {
			throw new OrangeRedisException(String.format("The field annotated with @%s cannot be null", Longitude.class));
		}
		if(!(longitude instanceof Number) && !(longitude instanceof String)) {
			throw new OrangeRedisException(String.format("The field annotated with @%s must be a number or a string", Longitude.class));
		}
		return new GeoEntry(location,Double.valueOf(longitude.toString()),Double.valueOf(latitude.toString()));
	}
}