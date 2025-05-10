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
 * @author Liang.Zhong
 * @since 1.0.0
 */
public class OrangeMemberContext extends OrangeRedisContext {

	public OrangeMemberContext(
		Class<?> operationOwner, 
		Method operationMethod, 
		Object[] args, 
		Key redisKey,
		RedisValueTypeEnum valueType
	) {
		super(operationOwner, operationMethod, args, redisKey, valueType);
	}

	protected GeoEntry toGeoEntry(Object member,Class<? extends Annotation> annotationClass) {
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
