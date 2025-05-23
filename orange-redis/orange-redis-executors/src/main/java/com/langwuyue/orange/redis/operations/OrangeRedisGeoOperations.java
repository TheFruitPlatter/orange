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
package com.langwuyue.orange.redis.operations;

import java.lang.reflect.Type;
import java.util.List;

import com.langwuyue.orange.redis.GeoDistanceUnitEnum;
import com.langwuyue.orange.redis.RedisValueTypeEnum;
import com.langwuyue.orange.redis.annotation.RedisValue;
import com.langwuyue.orange.redis.annotation.geo.Distance;
import com.langwuyue.orange.redis.annotation.geo.Latitude;
import com.langwuyue.orange.redis.annotation.geo.Longitude;

/**
 * @author Liang.Zhong
 * @since 1.0.0
 */
public interface OrangeRedisGeoOperations extends OrangeRedisOperations {

	Double distance(
		String key, 
		Object location1, 
		Object location2, 
		GeoDistanceUnitEnum geoUnit,
		RedisValueTypeEnum valueType
	) throws Exception;

	Long remove(String key, RedisValueTypeEnum valueType, Object... value) throws Exception;

	Long add(String key, List<GeoEntry> members, RedisValueTypeEnum valueType) throws Exception;

	List<GeoEntry> position(String key, RedisValueTypeEnum valueType, Object... values) throws Exception;
	
	List<GeoEntryInRadius> radius(
		String key, 
		Object location, 
		double radius, 
		GeoDistanceUnitEnum unit,
		RedisValueTypeEnum valueType, 
		Type returnType, 
		SearchArguments searchArguments
	) throws Exception;
	
	List<GeoEntryInRadius> radius(
		String key, 
		Double longitude, 
		Double latitude, 
		Double distance, 
		GeoDistanceUnitEnum unit,
		RedisValueTypeEnum valueType, 
		Type returnType, 
		SearchArguments searchArguments
	) throws Exception;
	
	Long searchRadiusAndStore(
		String key, 
		String destKey,
		Object location, 
		Double distance, 
		GeoDistanceUnitEnum unit,
		RedisValueTypeEnum valueType, 
		SearchArguments searchArguments
	) throws Exception;
	
	Long searchRadiusAndStore(
		String key, 
		String destKey,
		Double longitude, 
		Double latitude, 
		Double distance, 
		GeoDistanceUnitEnum unit,
		SearchArguments searchArguments
	) throws Exception;
	
	List<GeoEntryInRadius> box(
		String key, 
		Object location, 
		Double width, 
		GeoDistanceUnitEnum widthUnit,
		Double height, 
		GeoDistanceUnitEnum heightUnit,
		RedisValueTypeEnum valueType, 
		Type returnType, 
		SearchArguments searchArguments
	) throws Exception;
	
	List<GeoEntryInRadius> box(
		String key, 
		Double longitude, 
		Double latitude, 
		GeoDistanceUnitEnum widthUnit,
		Double width, 
		GeoDistanceUnitEnum heightUnit,
		Double height, 
		RedisValueTypeEnum valueType, 
		Type returnType, 
		SearchArguments searchArguments
	) throws Exception;
	
	Long searchBoxAndStore(
		String key, 
		String destKey,
		Object location, 
		Double width, 
		GeoDistanceUnitEnum widthUnit,
		Double height,
		GeoDistanceUnitEnum heightUnit,
		RedisValueTypeEnum valueType, 
		SearchArguments searchArguments
	) throws Exception;
	
	Long searchBoxAndStore(
		String key, 
		String destKey,
		Double longitude, 
		Double latitude, 
		GeoDistanceUnitEnum widthUnit,
		Double width, 
		GeoDistanceUnitEnum heightUnit,
		Double height,
		SearchArguments searchArguments
	) throws Exception;
	
	public static class GeoEntry {
		@RedisValue
		private Object location;
		@Latitude
		private Double latitude;
		@Longitude
		private Double longitude;
		
		public GeoEntry() {
			super();
		}
		public GeoEntry(Object location, Double longitude, Double latitude) {
			super();
			this.location = location;
			this.latitude = latitude;
			this.longitude = longitude;
		}
		public GeoEntry(Double longitude, Double latitude) {
			this.latitude = latitude;
			this.longitude = longitude;
		}
		public Object getLocation() {
			return location;
		}
		public void setLocation(Object location) {
			this.location = location;
		}
		public Double getLatitude() {
			return latitude;
		}
		public void setLatitude(Double latitude) {
			this.latitude = latitude;
		}
		public Double getLongitude() {
			return longitude;
		}
		public void setLongitude(Double longitude) {
			this.longitude = longitude;
		}
	}
	
	public static class GeoEntryInRadius extends GeoEntry {
		
		@Distance
		double distance;
		
		public GeoEntryInRadius() {
			super();
		}
		public GeoEntryInRadius(Object location, Double longitude, Double latitude, Double distance) {
			super(location,longitude,latitude);
			this.distance = distance;
		}
		public Double getDistance() {
			return distance;
		}
		public void setDistance(Double distance) {
			this.distance = distance;
		}
	}
	
	public static class SearchArguments {
		
		private boolean includeDistance;
		
		private boolean includeCoordinates;
		
		private boolean sortAscending;
		
		private boolean any;
		
		private int count;

		public boolean isIncludeDistance() {
			return includeDistance;
		}

		public void setIncludeDistance(boolean includeDistance) {
			this.includeDistance = includeDistance;
		}

		public boolean isIncludeCoordinates() {
			return includeCoordinates;
		}

		public void setIncludeCoordinates(boolean includeCoordinates) {
			this.includeCoordinates = includeCoordinates;
		}

		public boolean isSortAscending() {
			return sortAscending;
		}

		public void setSortAscending(boolean sortAscending) {
			this.sortAscending = sortAscending;
		}

		public int getCount() {
			return count;
		}

		public void setCount(int count) {
			this.count = count;
		}

		public boolean isAny() {
			return any;
		}

		public void setAny(boolean any) {
			this.any = any;
		}
	}
	
}
