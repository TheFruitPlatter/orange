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
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import com.langwuyue.orange.redis.OrangeRedisException;
import com.langwuyue.orange.redis.annotation.GetMembers;
import com.langwuyue.orange.redis.annotation.Multiple;
import com.langwuyue.orange.redis.annotation.RedisValue;
import com.langwuyue.orange.redis.annotation.geo.Latitude;
import com.langwuyue.orange.redis.annotation.geo.Longitude;
import com.langwuyue.orange.redis.context.OrangeRedisContext;
import com.langwuyue.orange.redis.context.OrangeRedisMultipleValueContext;
import com.langwuyue.orange.redis.executor.OrangeRedisAbstractExecutor;
import com.langwuyue.orange.redis.mapping.OrangeRedisExecutorIdGenerator;
import com.langwuyue.orange.redis.operations.OrangeRedisGeoOperations;
import com.langwuyue.orange.redis.operations.OrangeRedisGeoOperations.GeoEntry;
import com.langwuyue.orange.redis.utils.OrangeCollectionUtils;
import com.langwuyue.orange.redis.utils.OrangeReflectionUtils;

/**
 * @author Liang.Zhong
 * @since 1.0.0
 */
public class OrangeGetMembersExecutor extends OrangeRedisAbstractExecutor {

	private OrangeRedisGeoOperations operations;

	public OrangeGetMembersExecutor(OrangeRedisGeoOperations operations,OrangeRedisExecutorIdGenerator idGenerator) {
		super(idGenerator);
		this.operations = operations;
	}
	
	@Override
	public Object execute(OrangeRedisContext ctx) throws Exception{
		Object[] locations = getValues(ctx);
		List<GeoEntry> entries = this.operations.position(ctx.getRedisKey().getValue(), ctx.getValueType(), locations);
		if(entries == null || entries.isEmpty()) {
			return null;
		}
		return toReturnValue(ctx,entries);
	}

	protected Object toReturnValue(
		OrangeRedisContext ctx, 
		List<GeoEntry> entries
	) throws Exception {
		Class<?> returnClass = ctx.getOperationMethod().getReturnType();
		if(Collection.class.isAssignableFrom(returnClass) || returnClass.isArray()) {
			return toCollectionOrArray(ctx,entries);
		}else if(Map.class.isAssignableFrom(returnClass)) {
			return toMap(ctx,entries);
		}
		throw new OrangeRedisException("The return value must be a collection or an array or a map");
	}

	protected Object[] getValues(OrangeRedisContext ctx) {
		OrangeRedisMultipleValueContext context = (OrangeRedisMultipleValueContext) ctx;
		return context.toArray();
	}

	@Override
	protected List<Class<? extends Annotation>> getSupportedAnnotationClasses() {
		return OrangeCollectionUtils.asList(GetMembers.class,Multiple.class);
	}

	@Override
	public Class<? extends OrangeRedisContext> getContextClass() {
		return OrangeRedisMultipleValueContext.class;
	}
	
	protected Object toMap(
		OrangeRedisContext ctx, 
		List<GeoEntry> entries
	) throws Exception {
		Type[] types = OrangeReflectionUtils.getMapActaulTypeArguments(ctx.getOperationMethod().getGenericReturnType());
		Class argumentClass = OrangeReflectionUtils.getRawType(types[1]);
		Class keyClass = OrangeReflectionUtils.getRawType(types[0]);
		Field[] fields = argumentClass.getDeclaredFields();
		Field latitudeField = null;
		Field longitudeField = null;
		Field valueField = null;
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
					String.format("The value of return map must contain two fields annotated with @%s @%s", 
							Latitude.class,Longitude.class
					)
			);
		}
		
		fields = keyClass.getDeclaredFields();
		Field keyValueField = null;
		for(Field field : fields) {
			if(field.isAnnotationPresent(RedisValue.class)) {
				keyValueField = field;
				break;
			}
		}
		
		Class<?> returnClass = ctx.getOperationMethod().getReturnType();
		Map map = OrangeReflectionUtils.newMap(returnClass);
		for(GeoEntry entry : entries) {
			Object obj = argumentClass.getConstructor().newInstance();
			if(valueField != null) {
				OrangeReflectionUtils.setFieldValue(valueField, obj, entry.getLocation());	
			}
			OrangeReflectionUtils.setFieldValue(latitudeField, obj, entry.getLatitude());
			OrangeReflectionUtils.setFieldValue(longitudeField, obj, entry.getLongitude());
			if(keyValueField == null) {
				map.put(entry.getLocation(), obj);	
			}else{
				Object keyObj = keyClass.getConstructor().newInstance();
				OrangeReflectionUtils.setFieldValue(keyValueField, keyObj, entry.getLocation());	
				map.put(keyObj, obj);
			}
		}
		return map;
	}
	
	protected Object toCollectionOrArray(
		OrangeRedisContext ctx, 
		List<GeoEntry> entries
	) throws Exception {
		Type returnArgumentType = OrangeReflectionUtils.getCollectionOrArrayArgumentType(ctx.getOperationMethod().getGenericReturnType());
		Class<?> argumentClass = getRawType(returnArgumentType);
		Field[] fields = argumentClass.getDeclaredFields();
		Field latitudeField = null;
		Field longitudeField = null;
		Field valueField = null;
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
					String.format("The return value must contain two fields annotated with @%s @%s", 
							Latitude.class,Longitude.class
					)
			);
		}
		Class<?> returnClass = ctx.getOperationMethod().getReturnType();
		Object instance = getArrayOrCollectionInstance(returnClass, entries.size());
		if(instance instanceof Collection) {
			Collection collection = (Collection) instance;
			for(GeoEntry entry : entries) {
				Object obj = argumentClass.getConstructor().newInstance();
				if(valueField != null) {
					OrangeReflectionUtils.setFieldValue(valueField, obj, entry.getLocation());	
				}
				OrangeReflectionUtils.setFieldValue(latitudeField, obj, entry.getLatitude());
				OrangeReflectionUtils.setFieldValue(longitudeField, obj, entry.getLongitude());
				collection.add(obj);
			}
			return collection;
		}else{
			int i = 0;
			for(GeoEntry entry : entries) {
				Object obj = argumentClass.getConstructor().newInstance();
				if(valueField != null) {
					OrangeReflectionUtils.setFieldValue(valueField, obj, entry.getLocation());	
				}
				OrangeReflectionUtils.setFieldValue(latitudeField, obj, entry.getLatitude());
				OrangeReflectionUtils.setFieldValue(longitudeField, obj, entry.getLongitude());
				Array.set(instance, i, obj);
				i++;
			}
			return instance;
		}
	}
	
	protected Object getArrayOrCollectionInstance(Class<?> returnType,int size) {
		return OrangeReflectionUtils.getArrayOrCollectionInstance(returnType, size);
	}
	
	protected Class<?> getRawType(Type type){
		return OrangeReflectionUtils.getRawType(type);
	}
}
