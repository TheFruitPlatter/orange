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
package com.langwuyue.orange.redis.executor.geo;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import com.langwuyue.orange.redis.OrangeRedisException;
import com.langwuyue.orange.redis.annotation.GetMembers;
import com.langwuyue.orange.redis.annotation.RedisValue;
import com.langwuyue.orange.redis.annotation.geo.Latitude;
import com.langwuyue.orange.redis.annotation.geo.Longitude;
import com.langwuyue.orange.redis.context.OrangeRedisContext;
import com.langwuyue.orange.redis.context.OrangeRedisValueContext;
import com.langwuyue.orange.redis.mapping.OrangeRedisExecutorIdGenerator;
import com.langwuyue.orange.redis.operations.OrangeRedisGeoOperations;
import com.langwuyue.orange.redis.operations.OrangeRedisGeoOperations.GeoEntry;
import com.langwuyue.orange.redis.utils.OrangeCollectionUtils;
import com.langwuyue.orange.redis.utils.OrangeReflectionUtils;

/**
 * Executor implementation for retrieving a single geo member from Redis.
 * This executor extends {@link OrangeGetMembersExecutor} and specializes in handling
 * single member retrieval operations. It processes the {@link GetMembers} and {@link RedisValue}
 * annotations to fetch geo data for a specific member.
 *
 * @author Liang.Zhong
 * @since 1.0.0
 * @see <a href="https://orange.langwuyue.com/redis/advanced/geo">Orange Redis Geo Documentation</a>
 */
public class OrangeGetMemberExecutor extends OrangeGetMembersExecutor {

	/**
	 * Constructs a new OrangeGetMemberExecutor with the specified operations and ID generator.
	 *
	 * @param operations the Redis geo operations to be used for member retrieval
	 * @param idGenerator the generator for creating executor IDs
	 */
	public OrangeGetMemberExecutor(OrangeRedisGeoOperations operations,OrangeRedisExecutorIdGenerator idGenerator) {
		super(operations,idGenerator);
	}
	
	/**
	 * Converts the list of geo entries to a single return value.
	 * This method processes the geo entries and constructs a return object based on the method's return type.
	 * The return object's fields are populated using the geo entry data and annotations.
	 *
	 * @param ctx the execution context containing method and parameter information
	 * @param entries the list of geo entries retrieved from Redis
	 * @return the constructed return object or null if no valid entry is found
	 * @throws Exception if there's an error during object construction or field population
	 */
	@Override
	protected Object toReturnValue(
		OrangeRedisContext ctx, 
		List<GeoEntry> entries
	) throws Exception {
		Class<?> returnClass = ctx.getOperationMethod().getReturnType();
		if(Collection.class.isAssignableFrom(returnClass) 
				|| returnClass.isArray() 
				|| Map.class.isAssignableFrom(returnClass)) {
			return super.toReturnValue(ctx, entries);
		}
		Field latitudeField = null;
		Field longitudeField = null;
		Field valueField = null;
		Field[] fields = returnClass.getDeclaredFields();
		for(Field field : fields) {
			if(field.isAnnotationPresent(Latitude.class)) {
				latitudeField = field;
			}
			else if(field.isAnnotationPresent(Longitude.class)) {
				longitudeField = field;
			}
			else if(field.isAnnotationPresent(RedisValue.class)) {
				valueField = field;
			}
			if(latitudeField != null && longitudeField != null) {
				break;
			}
		}
		if(latitudeField == null || longitudeField == null) {
			throw new OrangeRedisException(
					String.format("The return value must contain two fields annotated with @%s @%s", Latitude.class,Longitude.class)
			);
		}
		for(GeoEntry entry : entries) {
			if(entry == null) {
				continue;
			}
			Object obj = returnClass.getConstructor().newInstance();
			if(valueField != null) {
				OrangeReflectionUtils.setFieldValue(valueField, obj, entry.getLocation());	
			}
			OrangeReflectionUtils.setFieldValue(latitudeField, obj, entry.getLatitude());
			OrangeReflectionUtils.setFieldValue(longitudeField, obj, entry.getLongitude());
			return obj;
		}
		return null;
	}

	/**
	 * Extracts the value to be used for member retrieval from the context.
	 * This method retrieves the single value from the OrangeRedisValueContext.
	 *
	 * @param ctx the execution context containing the value
	 * @return an array containing the single value from the context
	 */
	@Override
	protected Object[] getValues(OrangeRedisContext ctx) {
		OrangeRedisValueContext context = (OrangeRedisValueContext) ctx;
		return new Object[] {context.getValue()};
	}

	/**
	 * Returns the context class required by this executor.
	 * This executor uses {@link OrangeRedisValueContext} to handle single value operations.
	 *
	 * @return the OrangeRedisValueContext class
	 */
	@Override
	public Class<? extends OrangeRedisContext> getContextClass() {
		return OrangeRedisValueContext.class;
	}
	
	/**
	 * Returns the list of annotation classes supported by this executor.
	 * This executor supports both {@link GetMembers} and {@link RedisValue} annotations.
	 *
	 * @return a list containing the supported annotation classes
	 */
	@Override
	protected List<Class<? extends Annotation>> getSupportedAnnotationClasses() {
		return OrangeCollectionUtils.asList(GetMembers.class,RedisValue.class);
	}
}