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
package com.langwuyue.orange.example.redis.entity;

import javax.validation.constraints.NotNull;

import com.langwuyue.orange.redis.annotation.RedisValue;
import com.langwuyue.orange.redis.annotation.geo.Distance;
import com.langwuyue.orange.redis.annotation.geo.Latitude;
import com.langwuyue.orange.redis.annotation.geo.Longitude;

/**
 * Entity representing a geographical location with distance information for Redis GEO operations.
 * 
 * <p>This class is used with Redis GEO commands to store and query locations with:
 * <ul>
 *   <li>A named location ({@link OrangeValueExampleEntity})</li>
 *   <li>Geographical coordinates (latitude/longitude)</li>
 *   <li>Calculated distance from a reference point</li>
 * </ul>
 * 
 * <p>Typical usage includes:
 * <ol>
 *   <li>Storing locations with {@code GEOADD}</li>
 *   <li>Querying locations within radius using {@code GEORADIUS}</li>
 *   <li>Calculating distances between points with {@code GEODIST}</li>
 * </ol>
 * 
 * @author Liang.Zhong
 * @since 1.0.0
 * @see com.langwuyue.orange.redis.annotation.geo.Latitude
 * @see com.langwuyue.orange.redis.annotation.geo.Longitude
 * @see com.langwuyue.orange.redis.annotation.geo.Distance
 */
public class OrangeGeoDistanceExampleEntity {

	/** 
	 * The named location associated with these coordinates.
	 * Annotated with {@link RedisValue} to mark it as the primary Redis value.
	 */
	@NotNull
	@RedisValue
	private OrangeValueExampleEntity location;
	
	/**
	 * The latitude coordinate of the location in decimal degrees.
	 * Must be between -90 and 90 degrees.
	 */
	@NotNull
	@Latitude
	private Double latitude;
	
	/**
	 * The longitude coordinate of the location in decimal degrees.
	 * Must be between -180 and 180 degrees.
	 */
	@NotNull
	@Longitude
	private Double longitude;
	
	/**
	 * The calculated distance from a reference point in meters.
	 * Populated by Redis GEO commands like GEORADIUS.
	 */
	@NotNull
	@Distance
	private Double distance;

	/**
	 * Gets the named location associated with these coordinates.
	 * 
	 * @return The location entity containing the name/identifier
	 */
	public OrangeValueExampleEntity getLocation() {
		return location;
	}

	/**
	 * Sets the named location associated with these coordinates.
	 * 
	 * @param location The location entity containing the name/identifier
	 */
	public void setLocation(OrangeValueExampleEntity location) {
		this.location = location;
	}

	/**
	 * Gets the latitude coordinate in decimal degrees.
	 * 
	 * @return Latitude value between -90 and 90 degrees
	 */
	public Double getLatitude() {
		return latitude;
	}

	/**
	 * Sets the latitude coordinate in decimal degrees.
	 * 
	 * @param latitude Must be between -90 and 90 degrees
	 * @throws IllegalArgumentException if latitude is out of valid range
	 */
	public void setLatitude(Double latitude) {
		this.latitude = latitude;
	}

	/**
	 * Gets the longitude coordinate in decimal degrees.
	 * 
	 * @return Longitude value between -180 and 180 degrees
	 */
	public Double getLongitude() {
		return longitude;
	}

	/**
	 * Sets the longitude coordinate in decimal degrees.
	 * 
	 * @param longitude Must be between -180 and 180 degrees
	 * @throws IllegalArgumentException if longitude is out of valid range
	 */
	public void setLongitude(Double longitude) {
		this.longitude = longitude;
	}

	/**
	 * Gets the calculated distance from reference point.
	 * 
	 * @return Distance in meters, or null if not calculated yet
	 */
	public Double getDistance() {
		return distance;
	}

	/**
	 * Sets the calculated distance from reference point.
	 * 
	 * @param distance Distance in meters, typically set by Redis GEO commands
	 */
	public void setDistance(Double distance) {
		this.distance = distance;
	}
}