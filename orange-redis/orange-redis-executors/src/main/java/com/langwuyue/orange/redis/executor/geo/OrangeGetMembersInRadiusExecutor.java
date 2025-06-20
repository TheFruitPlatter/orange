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
 * Executor for retrieving geo members within a specified radius.
 * <p>
 * This executor handles operations annotated with {@link GetMembers} and processes
 * geo radius search operations. It supports returning results as:
 * <ul>
 *   <li>Map - with geo location objects as keys and distances as values</li>
 *   <li>Collection - of objects containing value, distance, latitude and longitude</li>
 *   <li>Array - of objects containing value, distance, latitude and longitude</li>
 * </ul>
 * <p>
 * The return type objects must have fields annotated with appropriate annotations:
 * {@link RedisValue}, {@link Distance}, {@link Latitude}, and {@link Longitude}.
 *
 * @author Liang.Zhong
 * @since 1.0.0
 * @see <a href="https://orange.langwuyue.com/redis/advanced/geo">Orange Redis Geo Documentation</a>
 */
public class OrangeGetMembersInRadiusExecutor extends OrangeRedisAbstractExecutor {

	private OrangeRedisGeoOperations operations;

	/**
	 * Constructs a new OrangeGetMembersInRadiusExecutor.
	 *
	 * @param operations the Redis geo operations instance to perform radius searches
	 * @param idGenerator the executor ID generator for generating unique identifiers
	 */
	public OrangeGetMembersInRadiusExecutor(OrangeRedisGeoOperations operations,OrangeRedisExecutorIdGenerator idGenerator) {
		super(idGenerator);
		this.operations = operations;
	}

	/**
	 * Executes the geo radius search operation.
	 * <p>
	 * Retrieves geo members within a specified radius from a given point.
	 * The results are converted to the appropriate return type as specified
	 * in the method's return type.
	 *
	 * @param context the execution context containing method parameters and return type information
	 * @return the result of the geo radius search operation, converted to the appropriate return type
	 * @throws Exception if an error occurs during execution or result conversion
	 */
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

	/**
	 * Returns the list of annotation classes supported by this executor.
	 * 
	 * @return a list of annotation classes that this executor can process
	 */
	@Override
	protected List<Class<? extends Annotation>> getSupportedAnnotationClasses() {
		return OrangeCollectionUtils.asList(GetMembers.class,Distance.class,RedisValue.class,SearchArgs.class);
	}

	/**
	 * Returns the context class used by this executor.
	 * 
	 * @return the class of the context used for radius search operations
	 */
	@Override
	public Class<? extends OrangeRedisContext> getContextClass() {
		return OrangeRadiusContext.class;
	}
	
	/**
	 * Performs the actual geo radius search operation.
	 * 
	 * @param context the execution context containing the search parameters
	 * @param valueField the field annotated with {@link RedisValue} to store the member value
	 * @return a list of geo entries within the specified radius
	 * @throws Exception if an error occurs during the Redis operation
	 */
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
	
	/**
	 * Converts a list of geo entries to a map representation.
	 * <p>
	 * Creates a map where each key is an object containing the geo location information
	 * (value, latitude, longitude) and each value is the distance from the search point.
	 *
	 * @param entries the list of geo entries to convert
	 * @param valueField the field to store the member value
	 * @param latitudeField the field to store the latitude
	 * @param longitudeField the field to store the longitude
	 * @param argumentClass the class of the map key objects
	 * @param returnClass the class of the map to create
	 * @return a map containing geo location objects as keys and distances as values
	 * @throws Exception if an error occurs during map creation or population
	 */
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
	
	/**
	 * Converts a list of geo entries to either a collection or array.
	 * <p>
	 * Creates and populates either a collection or array (based on returnClass) where each element
	 * is an object containing the complete geo location information (value, distance, latitude, longitude).
	 *
	 * @param entries the list of geo entries to convert
	 * @param valueField the field to store the member value
	 * @param distanceField the field to store the distance
	 * @param latitudeField the field to store the latitude
	 * @param longitudeField the field to store the longitude
	 * @param argumentClass the class of the elements in the collection/array
	 * @param returnClass the class of the collection/array to create
	 * @return a collection or array containing objects with geo location information
	 * @throws Exception if an error occurs during instance creation or population
	 */
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
	
	/**
	 * Creates an empty array or collection instance based on the return type.
	 * <p>
	 * This method determines whether to create an array or a collection based on the return type,
	 * and initializes it with the specified size.
	 *
	 * @param returnType the class of the return type (array or collection)
	 * @param size the size of the array or initial capacity of the collection
	 * @return an empty array or collection instance
	 */
	protected Object getArrayOrCollectionInstance(Class<?> returnType,int size) {
		return OrangeReflectionUtils.getArrayOrCollectionInstance(returnType, size);
	}
	
	/**
	 * Gets the raw class type from a generic Type object.
	 * <p>
	 * This method extracts the underlying Class from a Type object,
	 * which is useful when working with generic types.
	 *
	 * @param type the Type object to extract the raw class from
	 * @return the raw Class object
	 */
	protected Class<?> getRawType(Type type){
		return OrangeReflectionUtils.getRawType(type);
	}

	/**
	 * Returns the Redis geo operations instance.
	 * <p>
	 * This method provides access to the Redis operations specific to geo data,
	 * which are used to execute the radius search.
	 *
	 * @return the OrangeRedisGeoOperations instance
	 */
	protected OrangeRedisGeoOperations getOperations() {
		return operations;
	}
	
	
}