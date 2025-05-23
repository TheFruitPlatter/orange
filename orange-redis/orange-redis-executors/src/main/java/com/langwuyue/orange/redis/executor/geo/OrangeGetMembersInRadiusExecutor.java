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
import com.langwuyue.orange.redis.annotation.RedisValue;
import com.langwuyue.orange.redis.annotation.geo.Distance;
import com.langwuyue.orange.redis.annotation.geo.Latitude;
import com.langwuyue.orange.redis.annotation.geo.Longitude;
import com.langwuyue.orange.redis.annotation.geo.SearchArgs;
import com.langwuyue.orange.redis.context.OrangeRedisContext;
import com.langwuyue.orange.redis.executor.OrangeRedisAbstractExecutor;
import com.langwuyue.orange.redis.executor.geo.context.OrangeRadiusContext;
import com.langwuyue.orange.redis.mapping.OrangeRedisExecutorIdGenerator;
import com.langwuyue.orange.redis.operations.OrangeRedisGeoOperations;
import com.langwuyue.orange.redis.operations.OrangeRedisGeoOperations.GeoEntryInRadius;
import com.langwuyue.orange.redis.utils.OrangeCollectionUtils;
import com.langwuyue.orange.redis.utils.OrangeReflectionUtils;

/**
 * @author Liang.Zhong
 * @since 1.0.0
 */
public class OrangeGetMembersInRadiusExecutor extends OrangeRedisAbstractExecutor {

	private OrangeRedisGeoOperations operations;

	public OrangeGetMembersInRadiusExecutor(OrangeRedisGeoOperations operations,OrangeRedisExecutorIdGenerator idGenerator) {
		super(idGenerator);
		this.operations = operations;
	}

	@Override
	public Object execute(OrangeRedisContext context) throws Exception {
		Class returnClass = context.getOperationMethod().getReturnType();
		Field valueField = null;
		Field latitudeField = null;
		Field longitudeField = null;
		Type returnType = context.getOperationMethod().getGenericReturnType();
		if(Map.class.isAssignableFrom(returnClass)) {
			Type keyType = OrangeReflectionUtils.getMapKeyType(returnType);
			Class keyClass = OrangeReflectionUtils.getRawType(keyType);
			Field[] fields = keyClass.getDeclaredFields();
			for(Field field : fields) {
				if(field.isAnnotationPresent(RedisValue.class)) {
					valueField = field;
				}
				else if(field.isAnnotationPresent(Latitude.class)) {
					latitudeField = field;
				}
				else if(field.isAnnotationPresent(Longitude.class)) {
					longitudeField = field;
				}
				if(valueField != null && latitudeField != null && longitudeField != null) {
					break;
				}
			}
			if(valueField == null || latitudeField == null || longitudeField == null) {
				throw new OrangeRedisException(String.format("The return value must contain three fields annotated with @%s @%s @%s", 
					RedisValue.class,Latitude.class,Longitude.class
				));
			}
			List<GeoEntryInRadius> entries = doGet(context,valueField);
			return toMap(entries,valueField,latitudeField,longitudeField,keyClass,returnClass);
		} else {
			Type returnArgumentType = OrangeReflectionUtils.getCollectionOrArrayArgumentType(returnType);
			Class returnArgumentClass = OrangeReflectionUtils.getRawType(returnArgumentType); 
			Field[] fields = returnArgumentClass.getDeclaredFields();
			Field distanceField = null;
			for(Field field : fields) {
				if(field.isAnnotationPresent(Distance.class)) {
					distanceField = field;
				}
				else if(field.isAnnotationPresent(RedisValue.class)) {
					valueField = field;
				}
				else if(field.isAnnotationPresent(Latitude.class)) {
					latitudeField = field;
				}
				else if(field.isAnnotationPresent(Longitude.class)) {
					longitudeField = field;
				}
				if(valueField != null && distanceField != null && latitudeField != null && longitudeField != null) {
					break;
				}
			}
			
			if(valueField == null || distanceField == null || latitudeField == null || longitudeField == null) {
				throw new OrangeRedisException(String.format("The return value must contain four fields annotated with @%s @%s @%s @%s", 
						Distance.class,RedisValue.class,Latitude.class,Longitude.class
				));
			}
			List<GeoEntryInRadius> entries = doGet(context,valueField);
			return toReturnValue(entries,valueField,distanceField,latitudeField,longitudeField,returnArgumentClass,returnClass);
		}
	}

	@Override
	protected List<Class<? extends Annotation>> getSupportedAnnotationClasses() {
		return OrangeCollectionUtils.asList(GetMembers.class,Distance.class,RedisValue.class,SearchArgs.class);
	}

	@Override
	public Class<? extends OrangeRedisContext> getContextClass() {
		return OrangeRadiusContext.class;
	}
	
	protected List<GeoEntryInRadius> doGet(OrangeRedisContext context,Field valueField) throws Exception{
		OrangeRadiusContext ctx = (OrangeRadiusContext) context;
		return this.operations.radius(
			ctx.getRedisKey().getValue(), 
			ctx.getValue(), 
			ctx.getDistance(), 
			ctx.getUnit(), 
			ctx.getValueType(),
			valueField.getGenericType(),
			ctx.getRadiusArguments()
		);
	}
	
	protected Object toMap(
		List<GeoEntryInRadius> entries, 
		Field valueField, 
		Field latitudeField, 
		Field longitudeField, 
		Class argumentClass,
		Class returnClass
	) throws Exception {
		Map map = OrangeReflectionUtils.newMap(returnClass);
		for(GeoEntryInRadius entry : entries) {
			Object obj = argumentClass.getConstructor().newInstance();
			OrangeReflectionUtils.setFieldValue(valueField, obj, entry.getLocation());
			OrangeReflectionUtils.setFieldValue(latitudeField, obj, entry.getLatitude());
			OrangeReflectionUtils.setFieldValue(longitudeField, obj, entry.getLongitude());
			map.put(obj, entry.getDistance());
		}
		return map;
	}
	
	protected Object toReturnValue(
		List<GeoEntryInRadius> entries, 
		Field valueField, 
		Field distanceField, 
		Field latitudeField, 
		Field longitudeField, 
		Class argumentClass,
		Class returnClass
	) throws Exception {
		Object instance = getArrayOrCollectionInstance(returnClass, entries.size());
		if(instance instanceof Collection) {
			Collection collection = (Collection) instance;
			for(GeoEntryInRadius entry : entries) {
				Object obj = argumentClass.getConstructor().newInstance();
				OrangeReflectionUtils.setFieldValue(valueField, obj, entry.getLocation());
				OrangeReflectionUtils.setFieldValue(distanceField, obj, entry.getDistance());
				OrangeReflectionUtils.setFieldValue(latitudeField, obj, entry.getLatitude());
				OrangeReflectionUtils.setFieldValue(longitudeField, obj, entry.getLongitude());
				collection.add(obj);
			}
			return collection;
		}else{
			int i = 0;
			for(GeoEntryInRadius entry : entries) {
				Object obj = argumentClass.getConstructor().newInstance();
				OrangeReflectionUtils.setFieldValue(valueField, obj, entry.getLocation());
				OrangeReflectionUtils.setFieldValue(distanceField, obj, entry.getDistance());
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

	protected OrangeRedisGeoOperations getOperations() {
		return operations;
	}
	
	
}
