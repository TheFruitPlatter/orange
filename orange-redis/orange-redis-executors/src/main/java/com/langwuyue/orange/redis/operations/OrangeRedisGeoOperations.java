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
 * Interface defining Redis geospatial operations.
 * 
 * <p>This interface provides methods for interacting with Redis geospatial data structures,
 * which allow storing and querying geographical coordinates. Redis geospatial features
 * support storing coordinates, calculating distances, searching by radius, and more.
 * 
 * <p>The operations include:
 * <ul>
 *   <li>Adding geospatial items to a key</li>
 *   <li>Removing items from a geospatial index</li>
 *   <li>Calculating distance between points</li>
 *   <li>Retrieving positions of members</li>
 *   <li>Searching by radius around a point</li>
 *   <li>Searching within a bounding box</li>
 *   <li>Storing search results in another key</li>
 * </ul>
 * 
 * <p>All methods support different value types through the {@link RedisValueTypeEnum}
 * parameter, allowing for flexible serialization strategies.
 *
 * @author Liang.Zhong
 * @since 1.0.0
 */
public interface OrangeRedisGeoOperations extends OrangeRedisOperations {

	/**
	 * Calculates the distance between two locations in a geospatial index.
	 *
	 * @param key the key of the geospatial index
	 * @param location1 the first location member
	 * @param location2 the second location member
	 * @param geoUnit the unit of distance measurement (e.g., meters, kilometers)
	 * @param valueType the type of value serialization to use
	 * @return the distance between the two locations in the specified unit
	 * @throws Exception if an error occurs during the operation
	 */
	Double distance(
		String key, 
		Object location1, 
		Object location2, 
		GeoDistanceUnitEnum geoUnit,
		RedisValueTypeEnum valueType
	) throws Exception;

	/**
	 * Removes one or more members from a geospatial index.
	 *
	 * @param key the key of the geospatial index
	 * @param valueType the type of value serialization to use
	 * @param value one or more members to remove
	 * @return the number of members removed from the index
	 * @throws Exception if an error occurs during the operation
	 */
	Long remove(String key, RedisValueTypeEnum valueType, Object... value) throws Exception;

	/**
	 * Adds geospatial entries to an index.
	 *
	 * @param key the key of the geospatial index
	 * @param members list of {@link GeoEntry} objects containing location data
	 * @param valueType the type of value serialization to use
	 * @return the number of elements added to the index
	 * @throws Exception if an error occurs during the operation
	 */
	Long add(String key, List<GeoEntry> members, RedisValueTypeEnum valueType) throws Exception;

	/**
	 * Retrieves the positions (coordinates) of one or more members in a geospatial index.
	 *
	 * @param key the key of the geospatial index
	 * @param valueType the type of value serialization to use
	 * @param values one or more members to look up
	 * @return list of {@link GeoEntry} objects containing the positions of the requested members
	 * @throws Exception if an error occurs during the operation
	 */
	List<GeoEntry> position(String key, RedisValueTypeEnum valueType, Object... values) throws Exception;
	
	/**
	 * Searches for members within a radius of a specified location.
	 *
	 * @param key the key of the geospatial index
	 * @param location the member to use as center point
	 * @param radius the radius of the search area
	 * @param unit the unit of distance measurement
	 * @param valueType the type of value serialization to use
	 * @param returnType the type of the returned GeoEntryInRadius objects
	 * @param searchArguments additional search arguments (e.g., sorting, limiting results)
	 * @return list of {@link GeoEntryInRadius} containing members within the radius
	 * @throws Exception if an error occurs during the operation
	 */
	List<GeoEntryInRadius> radius(
		String key, 
		Object location, 
		double radius, 
		GeoDistanceUnitEnum unit,
		RedisValueTypeEnum valueType, 
		Type returnType, 
		SearchArguments searchArguments
	) throws Exception;
	
	/**
	 * Searches for members within a radius of specified coordinates.
	 *
	 * @param key the key of the geospatial index
	 * @param longitude the longitude of the center point
	 * @param latitude the latitude of the center point
	 * @param distance the radius of the search area
	 * @param unit the unit of distance measurement
	 * @param valueType the type of value serialization to use
	 * @param returnType the type of the returned GeoEntryInRadius objects
	 * @param searchArguments additional search arguments (e.g., sorting, limiting results)
	 * @return list of {@link GeoEntryInRadius} containing members within the radius
	 * @throws Exception if an error occurs during the operation
	 */
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
	
	/**
	 * Searches for members within a radius of a specified location and stores the result in another key.
	 *
	 * @param key the source key of the geospatial index
	 * @param destKey the destination key to store results
	 * @param location the member to use as center point
	 * @param distance the radius of the search area
	 * @param unit the unit of distance measurement
	 * @param valueType the type of value serialization to use
	 * @param searchArguments additional search arguments (e.g., sorting, limiting results)
	 * @return the number of elements stored in the destination key
	 * @throws Exception if an error occurs during the operation
	 */
	Long searchRadiusAndStore(
		String key, 
		String destKey,
		Object location, 
		Double distance, 
		GeoDistanceUnitEnum unit,
		RedisValueTypeEnum valueType, 
		SearchArguments searchArguments
	) throws Exception;
	
	/**
	 * Searches for members within a radius of specified coordinates and stores the result in another key.
	 *
	 * @param key the source key of the geospatial index
	 * @param destKey the destination key to store results
	 * @param longitude the longitude of the center point
	 * @param latitude the latitude of the center point
	 * @param distance the radius of the search area
	 * @param unit the unit of distance measurement
	 * @param searchArguments additional search arguments (e.g., sorting, limiting results)
	 * @return the number of elements stored in the destination key
	 * @throws Exception if an error occurs during the operation
	 */
	Long searchRadiusAndStore(
		String key, 
		String destKey,
		Double longitude, 
		Double latitude, 
		Double distance, 
		GeoDistanceUnitEnum unit,
		SearchArguments searchArguments
	) throws Exception;
	
	/**
	 * Searches for members within a rectangular area centered at a specified location.
	 *
	 * @param key the key of the geospatial index
	 * @param location the member to use as center point
	 * @param width the width of the rectangle
	 * @param widthUnit the unit of measurement for width
	 * @param height the height of the rectangle
	 * @param heightUnit the unit of measurement for height
	 * @param valueType the type of value serialization to use
	 * @param returnType the type of the returned GeoEntryInRadius objects
	 * @param searchArguments additional search arguments (e.g., sorting, limiting results)
	 * @return list of {@link GeoEntryInRadius} containing members within the rectangle
	 * @throws Exception if an error occurs during the operation
	 */
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
	
	/**
	 * Searches for members within a rectangular area centered at specified coordinates.
	 *
	 * @param key the key of the geospatial index
	 * @param longitude the longitude of the center point
	 * @param latitude the latitude of the center point
	 * @param widthUnit the unit of measurement for width
	 * @param width the width of the rectangle
	 * @param heightUnit the unit of measurement for height
	 * @param height the height of the rectangle
	 * @param valueType the type of value serialization to use
	 * @param returnType the type of the returned GeoEntryInRadius objects
	 * @param searchArguments additional search arguments (e.g., sorting, limiting results)
	 * @return list of {@link GeoEntryInRadius} containing members within the rectangle
	 * @throws Exception if an error occurs during the operation
	 */
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
	
	/**
	 * Searches for members within a rectangular area centered at a specified location and stores the result in another key.
	 *
	 * @param key the source key of the geospatial index
	 * @param destKey the destination key to store results
	 * @param location the member to use as center point
	 * @param width the width of the rectangle
	 * @param widthUnit the unit of measurement for width
	 * @param height the height of the rectangle
	 * @param heightUnit the unit of measurement for height
	 * @param valueType the type of value serialization to use
	 * @param searchArguments additional search arguments (e.g., sorting, limiting results)
	 * @return the number of elements stored in the destination key
	 * @throws Exception if an error occurs during the operation
	 */
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
	
	/**
	 * Searches for members within a rectangular area centered at specified coordinates and stores the result in another key.
	 *
	 * @param key the source key of the geospatial index
	 * @param destKey the destination key to store results
	 * @param longitude the longitude of the center point
	 * @param latitude the latitude of the center point
	 * @param widthUnit the unit of measurement for width
	 * @param width the width of the rectangle
	 * @param heightUnit the unit of measurement for height
	 * @param height the height of the rectangle
	 * @param searchArguments additional search arguments (e.g., sorting, limiting results)
	 * @return the number of elements stored in the destination key
	 * @throws Exception if an error occurs during the operation
	 */
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
	
	/**
	 * Represents a geospatial entry with coordinates.
	 * 
	 * <p>This class is used to store and transfer geospatial data between Redis and the application.
	 * It includes the location identifier and its geographical coordinates (latitude and longitude).
	 * 
	 * <p>The class is annotated with {@link RedisValue}, {@link Latitude}, and {@link Longitude}
	 * to facilitate serialization and deserialization when interacting with Redis.
	 */
	public static class GeoEntry {
		/** The location identifier, annotated with {@link RedisValue} for serialization */
		@RedisValue
		private Object location;
		
		/** The latitude coordinate, annotated with {@link Latitude} */
		@Latitude
		private Double latitude;
		
		/** The longitude coordinate, annotated with {@link Longitude} */
		@Longitude
		private Double longitude;
		
		/**
		 * Default constructor.
		 */
		public GeoEntry() {
			super();
		}
		
		/**
		 * Constructs a GeoEntry with location identifier and coordinates.
		 * 
		 * @param location the location identifier
		 * @param longitude the longitude coordinate
		 * @param latitude the latitude coordinate
		 */
		public GeoEntry(Object location, Double longitude, Double latitude) {
			super();
			this.location = location;
			this.latitude = latitude;
			this.longitude = longitude;
		}
		
		/**
		 * Constructs a GeoEntry with coordinates only.
		 * 
		 * @param longitude the longitude coordinate
		 * @param latitude the latitude coordinate
		 */
		public GeoEntry(Double longitude, Double latitude) {
			this.latitude = latitude;
			this.longitude = longitude;
		}
		
		/**
		 * Gets the location identifier.
		 * 
		 * @return the location identifier
		 */
		public Object getLocation() {
			return location;
		}
		
		/**
		 * Sets the location identifier.
		 * 
		 * @param location the location identifier to set
		 */
		public void setLocation(Object location) {
			this.location = location;
		}
		
		/**
		 * Gets the latitude coordinate.
		 * 
		 * @return the latitude coordinate
		 */
		public Double getLatitude() {
			return latitude;
		}
		
		/**
		 * Sets the latitude coordinate.
		 * 
		 * @param latitude the latitude coordinate to set
		 */
		public void setLatitude(Double latitude) {
			this.latitude = latitude;
		}
		
		/**
		 * Gets the longitude coordinate.
		 * 
		 * @return the longitude coordinate
		 */
		public Double getLongitude() {
			return longitude;
		}
		
		/**
		 * Sets the longitude coordinate.
		 * 
		 * @param longitude the longitude coordinate to set
		 */
		public void setLongitude(Double longitude) {
			this.longitude = longitude;
		}
	}
	
	/**
	 * Represents a geospatial entry with coordinates and distance from a reference point.
	 * 
	 * <p>Extends {@link GeoEntry} to include distance information, typically used for
	 * search results where the distance from the search center point is relevant.
	 * 
	 * <p>The distance field is annotated with {@link Distance} to facilitate serialization
	 * and deserialization when interacting with Redis.
	 */
	public static class GeoEntryInRadius extends GeoEntry {
		
		/** The distance from the reference point, annotated with {@link Distance} */
		@Distance
		double distance;
		
		/**
		 * Default constructor.
		 */
		public GeoEntryInRadius() {
			super();
		}
		
		/**
		 * Constructs a GeoEntryInRadius with location identifier, coordinates, and distance.
		 * 
		 * @param location the location identifier
		 * @param longitude the longitude coordinate
		 * @param latitude the latitude coordinate
		 * @param distance the distance from the reference point
		 */
		public GeoEntryInRadius(Object location, Double longitude, Double latitude, Double distance) {
			super(location,longitude,latitude);
			this.distance = distance;
		}
		
		/**
		 * Gets the distance from the reference point.
		 * 
		 * @return the distance
		 */
		public Double getDistance() {
			return distance;
		}
		
		/**
		 * Sets the distance from the reference point.
		 * 
		 * @param distance the distance to set
		 */
		public void setDistance(Double distance) {
			this.distance = distance;
		}
	}
	
	/**
	 * Represents search arguments for geospatial queries.
	 * 
	 * <p>This class encapsulates various options that can be used to customize
	 * geospatial search operations, including:
	 * <ul>
	 *   <li>Whether to include distance in results</li>
	 *   <li>Whether to include coordinates in results</li>
	 *   <li>Sorting direction (ascending/descending by distance)</li>
	 *   <li>Result count limit</li>
	 *   <li>Whether to return any result when no exact match is found</li>
	 * </ul>
	 */
	public static class SearchArguments {
		
		/** Flag indicating whether to include distance in results */
		private boolean includeDistance;
		
		/** Flag indicating whether to include coordinates in results */
		private boolean includeCoordinates;
		
		/** Flag indicating sorting direction (true for ascending, false for descending) */
		private boolean sortAscending;
		
		/** Flag indicating whether to return any result when no exact match is found */
		private boolean any;
		
		/** Maximum number of results to return (0 for no limit) */
		private int count;

		/**
		 * Checks if distance should be included in results.
		 * 
		 * @return true if distance should be included, false otherwise
		 */
		public boolean isIncludeDistance() {
			return includeDistance;
		}

		/**
		 * Sets whether to include distance in results.
		 * 
		 * @param includeDistance true to include distance, false otherwise
		 */
		public void setIncludeDistance(boolean includeDistance) {
			this.includeDistance = includeDistance;
		}

		/**
		 * Checks if coordinates should be included in results.
		 * 
		 * @return true if coordinates should be included, false otherwise
		 */
		public boolean isIncludeCoordinates() {
			return includeCoordinates;
		}

		/**
		 * Sets whether to include coordinates in results.
		 * 
		 * @param includeCoordinates true to include coordinates, false otherwise
		 */
		public void setIncludeCoordinates(boolean includeCoordinates) {
			this.includeCoordinates = includeCoordinates;
		}

		/**
		 * Checks the sorting direction.
		 * 
		 * @return true for ascending order (nearest first), false for descending
		 */
		public boolean isSortAscending() {
			return sortAscending;
		}

		/**
		 * Sets the sorting direction.
		 * 
		 * @param sortAscending true for ascending order (nearest first), false for descending
		 */
		public void setSortAscending(boolean sortAscending) {
			this.sortAscending = sortAscending;
		}

		/**
		 * Gets the maximum number of results to return.
		 * 
		 * @return the maximum result count (0 means no limit)
		 */
		public int getCount() {
			return count;
		}

		/**
		 * Sets the maximum number of results to return.
		 * 
		 * @param count the maximum result count (0 means no limit)
		 */
		public void setCount(int count) {
			this.count = count;
		}

		/**
		 * Checks if any result should be returned when no exact match is found.
		 * 
		 * @return true if any result is acceptable, false otherwise
		 */
		public boolean isAny() {
			return any;
		}

		/**
		 * Sets whether to return any result when no exact match is found.
		 * 
		 * @param any true to accept any result, false otherwise
		 */
		public void setAny(boolean any) {
			this.any = any;
		}
	}
	
}