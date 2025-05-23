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

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;

import com.langwuyue.orange.redis.OrangeRedisException;
import com.langwuyue.orange.redis.RedisValueTypeEnum;
import com.langwuyue.orange.redis.annotation.ContinueOnFailure;
import com.langwuyue.orange.redis.annotation.Multiple;
import com.langwuyue.orange.redis.annotation.OrangeRedisOperationArg;
import com.langwuyue.orange.redis.annotation.RedisValue;
import com.langwuyue.orange.redis.annotation.geo.Latitude;
import com.langwuyue.orange.redis.annotation.geo.Longitude;
import com.langwuyue.orange.redis.context.OrangeRedisIterableContext;
import com.langwuyue.orange.redis.context.builder.OrangeMethodAnnotationHandler;
import com.langwuyue.orange.redis.context.builder.OrangeOperationArgMultipleHandler;
import com.langwuyue.orange.redis.operations.OrangeRedisGeoOperations.GeoEntry;
import com.langwuyue.orange.redis.utils.OrangeReflectionUtils;

/**
 * @author Liang.Zhong
 * @since 1.0.0
 */
public class OrangeAddMembersContext extends OrangeMemberContext implements OrangeRedisIterableContext {
	
	@OrangeRedisOperationArg(binding = Multiple.class, valueHandler = OrangeOperationArgMultipleHandler.class)
	private Object multipleValue;
	
	@OrangeRedisOperationArg(binding = ContinueOnFailure.class, valueHandler = OrangeMethodAnnotationHandler.class)
	private ContinueOnFailure continueOnFailure;
	
	public OrangeAddMembersContext(
		Class<?> operationOwner, 
		Method operationMethod, 
		Object[] args,
		Key redisKey,
		RedisValueTypeEnum valueType
	) {
		super(operationOwner, operationMethod, args, redisKey,valueType);
	}

	public Object getMultipleValue() {
		return multipleValue;
	}
	
	public List<GeoEntry> getMembers(){
		List<GeoEntry> entries = new ArrayList<>();
		if(multipleValue instanceof Collection) {
			Collection members = (Collection)multipleValue;
			for(Object member : members) {
				GeoEntry entry = toGeoEntry(member,Multiple.class);
				if(entry == null) {
					continue;
				}
				entries.add(entry);
			}
			return entries;
		}else if(multipleValue instanceof Array) {
			int len = Array.getLength(multipleValue);
			for(int i = 0; i < len; i++) {
				Object member = Array.get(multipleValue, i);
				GeoEntry entry = toGeoEntry(member,Multiple.class);
				if(entry == null) {
					continue;
				}
				entries.add(entry);
			}
			return entries;
		}
		else if(multipleValue instanceof Map) {
			Map map = (Map)multipleValue;
			Set<Map.Entry> mapEntries = map.entrySet();
			for(Map.Entry entry : mapEntries) {
				if(entry.getKey() == null || entry.getValue() == null) {
					continue;
				}
				Object value = entry.getValue();
				if(value == null) {
					throw new OrangeRedisException(String.format("The key of map annotated with @%s cannot be", Multiple.class));
				}
				GeoEntry geoEntry = toGeoEntryWithoutLocation(value);
				geoEntry.setLocation(entry.getKey());
				entries.add(geoEntry);
			}
			return entries;	
		}
		throw new OrangeRedisException(String.format("The argument annotated with @%s must be a collection or an array or a Map", Multiple.class));
	}
	
	private List<GeoEntry> getMembers(BiConsumer consumer){
		List<GeoEntry> entries = new ArrayList<>();
		if(multipleValue instanceof Collection) {
			Collection members = (Collection)multipleValue;
			for(Object member : members) {
				GeoEntry entry = toGeoEntry(member,Multiple.class);
				if(entry == null) {
					continue;
				}
				entries.add(entry);
				if(consumer != null) {
					consumer.accept(entry, member);
				}
			}
			return entries;
		}else if(multipleValue instanceof Array) {
			int len = Array.getLength(multipleValue);
			for(int i = 0; i < len; i++) {
				Object member = Array.get(multipleValue, i);
				GeoEntry entry = toGeoEntry(member,Multiple.class);
				if(entry == null) {
					continue;
				}
				entries.add(entry);
				if(consumer != null) {
					consumer.accept(entry, member);
				}
			}
			return entries;
		}
		else if(multipleValue instanceof Map) {
			Map map = (Map)multipleValue;
			Set<Map.Entry> mapEntries = map.entrySet();
			for(Map.Entry entry : mapEntries) {
				if(entry.getKey() == null || entry.getValue() == null) {
					continue;
				}
				Object value = entry.getValue();
				if(value == null) {
					throw new OrangeRedisException(String.format("The key of map annotated with @%s cannot be", Multiple.class));
				}
				GeoEntry geoEntry = toGeoEntryWithoutLocation(value);
				geoEntry.setLocation(entry.getKey());
				entries.add(geoEntry);
				if(consumer != null) {
					consumer.accept(geoEntry, entry.getKey());
				}
			}
			return entries;	
		}
		throw new OrangeRedisException(String.format("The argument annotated with @%s must be a collection or an array or a Map", Multiple.class));
	}
	
	protected GeoEntry toGeoEntryWithoutLocation(Object member) {
		if(member == null) {
			return null;
		}
		if(member instanceof GeoEntry) {
			return (GeoEntry)member;
		}
		Field latitudeField = null;
		Field longitudeField = null;
		Field[] fields = member.getClass().getDeclaredFields();
		for(Field field : fields) {
			if(field.isAnnotationPresent(Latitude.class)) {
				latitudeField = field;
			}
			else if(field.isAnnotationPresent(Longitude.class)) {
				longitudeField = field;
			}
			if(latitudeField != null && longitudeField != null) {
				break;
			}
		}
		if(latitudeField == null || longitudeField == null) {
			throw new OrangeRedisException(
				String.format(
					"The argument annotated with @%s must have two fields annotated with @%s @%s", 
					Multiple.class,Latitude.class,Longitude.class
				)
			);
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
		return new GeoEntry(Double.valueOf(longitude.toString()),Double.valueOf(latitude.toString()));
	}

	@Override
	public void forEach(BiConsumer t) {
		getMembers(t);
	}

	@Override
	public Object[] toArray() {
		return getMembers().toArray();
	}
	
	@Override
	public boolean continueOnFailure() {
		return continueOnFailure.value();
	}
}
