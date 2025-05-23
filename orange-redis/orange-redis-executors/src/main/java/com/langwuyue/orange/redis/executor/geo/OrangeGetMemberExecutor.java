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
 * @author Liang.Zhong
 * @since 1.0.0
 */
public class OrangeGetMemberExecutor extends OrangeGetMembersExecutor {

	public OrangeGetMemberExecutor(OrangeRedisGeoOperations operations,OrangeRedisExecutorIdGenerator idGenerator) {
		super(operations,idGenerator);
	}
	
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

	@Override
	protected Object[] getValues(OrangeRedisContext ctx) {
		OrangeRedisValueContext context = (OrangeRedisValueContext) ctx;
		return new Object[] {context.getValue()};
	}

	@Override
	public Class<? extends OrangeRedisContext> getContextClass() {
		return OrangeRedisValueContext.class;
	}
	
	@Override
	protected List<Class<? extends Annotation>> getSupportedAnnotationClasses() {
		return OrangeCollectionUtils.asList(GetMembers.class,RedisValue.class);
	}
}
