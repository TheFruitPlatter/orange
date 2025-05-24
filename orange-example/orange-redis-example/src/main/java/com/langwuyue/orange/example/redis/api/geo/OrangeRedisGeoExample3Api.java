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
package com.langwuyue.orange.example.redis.api.geo;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import com.langwuyue.orange.example.redis.entity.OrangeGeoStringDistanceExampleEntity;
import com.langwuyue.orange.example.redis.entity.OrangeGeoStringExampleEntity;
import com.langwuyue.orange.redis.RedisValueTypeEnum;
import com.langwuyue.orange.redis.annotation.AddMembers;
import com.langwuyue.orange.redis.annotation.ContinueOnFailure;
import com.langwuyue.orange.redis.annotation.Count;
import com.langwuyue.orange.redis.annotation.GetMembers;
import com.langwuyue.orange.redis.annotation.KeyVariable;
import com.langwuyue.orange.redis.annotation.Member;
import com.langwuyue.orange.redis.annotation.Multiple;
import com.langwuyue.orange.redis.annotation.OrangeRedisKey;
import com.langwuyue.orange.redis.annotation.RedisValue;
import com.langwuyue.orange.redis.annotation.RemoveMembers;
import com.langwuyue.orange.redis.annotation.Timeout;
import com.langwuyue.orange.redis.annotation.geo.Distance;
import com.langwuyue.orange.redis.annotation.geo.Height;
import com.langwuyue.orange.redis.annotation.geo.Latitude;
import com.langwuyue.orange.redis.annotation.geo.Longitude;
import com.langwuyue.orange.redis.annotation.geo.OrangeRedisGeoClient;
import com.langwuyue.orange.redis.annotation.geo.SearchArgs;
import com.langwuyue.orange.redis.annotation.geo.Width;


/**
 * @author Liang.Zhong
 * @since 1.0.0
 */
@OrangeRedisGeoClient(valueType = RedisValueTypeEnum.STRING)
@OrangeRedisKey(expirationTime = @Timeout(value = 1, unit = TimeUnit.HOURS), key = "orange:geo:example3:${var}")
public interface OrangeRedisGeoExample3Api {
	
	/**
	 * Adds a new geo location to the set with specified coordinates.
	 * 
	 * <p>Adds a member with the given name and coordinates to the geo set.
	 * 
	 * <p>Operation: GEOADD key longitude latitude member
	 * 
	 * @param keyVar Variable part of the Redis key
	 * @param Location Name of the location to add
	 * @param longitude Longitude coordinate of the location
	 * @param latitude Latitude coordinate of the location
	 * @return true if the member was added, false if it already existed and was updated
	 */
	@AddMembers
	Boolean add(
		@KeyVariable(name = "var")String keyVar,
		@RedisValue String Location, 
		@Longitude Double longitude, 
		@Latitude Double latitude
	);
	
	/**
	 * Adds a geo entity to the set.
	 * 
	 * <p>Adds a member using the entity which contains both the name and coordinates.
	 * 
	 * <p>Operation: GEOADD key longitude latitude member
	 * 
	 * @param keyVar Variable part of the Redis key
	 * @param member The geo entity containing location name and coordinates
	 * @return true if the member was added, false if it already existed and was updated
	 */
	@AddMembers
	Boolean add(@KeyVariable(name = "var")String keyVar,@Member OrangeGeoStringExampleEntity member);
	
	/**
	 * Adds multiple geo entities to the set.
	 * 
	 * <p>Adds multiple members in a single operation, continuing even if some additions fail.
	 * 
	 * <p>Operation: GEOADD key longitude1 latitude1 member1 [longitude2 latitude2 member2 ...]
	 * 
	 * @param keyVar Variable part of the Redis key
	 * @param members Collection of geo entities to add
	 * @return Map of entities to boolean indicating success/failure for each entity
	 */
	@AddMembers
	@ContinueOnFailure(true)
	Map<OrangeGeoStringExampleEntity,Boolean> add(@KeyVariable(name = "var")String keyVar,@Multiple Collection<OrangeGeoStringExampleEntity> members);
	
	/**
	 * Calculates the distance between geo locations.
	 * 
	 * <p>Returns the distance between consecutive pairs of locations in the list.
	 * 
	 * <p>Operation: GEODIST key member1 member2 [unit]
	 * 
	 * @param keyVar Variable part of the Redis key
	 * @param locations List of location names to calculate distances between
	 * @return The distance in meters between consecutive locations
	 */
	@Distance
	Double distance(@KeyVariable(name = "var")String keyVar,@Multiple List<String> locations);
	
	/**
	 * Retrieves a geo member by its name.
	 * 
	 * <p>Returns the geo information (coordinates and member data) for the specified location.
	 * 
	 * <p>Operation: GEOPOS key member
	 * 
	 * @param keyVar Variable part of the Redis key
	 * @param location Name of the location to retrieve
	 * @return Geo entity containing the coordinates and data for the location, or null if not found
	 */
	@GetMembers
	OrangeGeoStringExampleEntity getMember(@KeyVariable(name = "var")String keyVar,@RedisValue String location);

	/**
	 * Retrieves multiple geo members by their names.
	 * 
	 * <p>Returns the geo information for multiple specified locations.
	 * 
	 * <p>Operation: GEOPOS key member [member ...]
	 * 
	 * @param keyVar Variable part of the Redis key
	 * @param locations List of location names to retrieve
	 * @return List of geo entities containing the coordinates and data for each location
	 */
	@GetMembers
	List<OrangeGeoStringExampleEntity> getMembers(@KeyVariable(name = "var")String keyVar,@Multiple List<String> locations);
	
	/**
	 * Searches for members within a circular radius of a given location.
	 * 
	 * <p>Returns members that are within the specified distance from the given location,
	 * including their distances from the center point.
	 * 
	 * <p>Operation: GEOSEARCH key FROMMEMBER member BYRADIUS radius m
	 * 
	 * @param keyVar Variable part of the Redis key
	 * @param location Name of the center location for the radius search
	 * @param distance Search radius in meters
	 * @return Map of members within the radius to their distances from the center
	 */
	@GetMembers
	@SearchArgs
	Map<OrangeGeoStringExampleEntity,Double> getMembersInRadius(@KeyVariable(name = "var")String keyVar,@RedisValue String location,@Distance Double distance);
	
	/**
	 * Searches for members within a circular radius of given coordinates.
	 * 
	 * <p>Returns members that are within the specified distance from the given coordinates,
	 * including their distances from the center point.
	 * 
	 * <p>Operation: GEOSEARCH key FROMLONLAT longitude latitude BYRADIUS radius m
	 * 
	 * @param keyVar Variable part of the Redis key
	 * @param longitude Longitude of the center point
	 * @param latitude Latitude of the center point
	 * @param distance Search radius in meters
	 * @return Map of members within the radius to their distances from the center
	 */
	@GetMembers
	@SearchArgs
	Map<OrangeGeoStringExampleEntity,Double> getMembersInRadius(@KeyVariable(name = "var")String keyVar,@Longitude Double longitude, @Latitude Double latitude, @Distance Double distance);
	
	/**
	 * Removes a geo member from the set.
	 * 
	 * <p>Removes the specified location from the geo set.
	 * 
	 * <p>Operation: ZREM key member
	 * 
	 * @param keyVar Variable part of the Redis key
	 * @param location Name of the location to remove
	 * @return true if the member was removed, false if it didn't exist
	 */
	@RemoveMembers
	Boolean remove(@KeyVariable(name = "var")String keyVar,@RedisValue String location);
	
	/**
	 * Removes multiple geo members from the set.
	 * 
	 * <p>Removes multiple locations in a single operation, continuing even if some removals fail.
	 * 
	 * <p>Operation: ZREM key member [member ...]
	 * 
	 * @param keyVar Variable part of the Redis key
	 * @param locations Collection of location names to remove
	 * @return Map of location names to boolean indicating success/failure for each removal
	 */
	@RemoveMembers
	@ContinueOnFailure(true)
	Map<String,Boolean> remove(@KeyVariable(name = "var")String keyVar,@Multiple Collection<String> locations);

	/**
	 * Calculates the distance between multiple geo entities.
	 * 
	 * <p>Returns the distance between consecutive pairs of entities in the list.
	 * For example, if the list contains [A, B, C], it will calculate the distance
	 * between A and B, then B and C.
	 * 
	 * <p>Operation: GEODIST key member1 member2 [unit]
	 * 
	 * @param keyVar Variable part of the Redis key
	 * @param locations List of geo entities to calculate distances between
	 * @return The distance in meters between consecutive locations
	 */
	@Distance
	Double distance2(@KeyVariable(name = "var")String keyVar,@Multiple List<OrangeGeoStringExampleEntity> locations);
	
	/**
	 * Retrieves multiple geo members by their entities.
	 * 
	 * <p>Returns the geo information (coordinates and member data) for the specified members.
	 * If a member doesn't exist, its corresponding entry in the result will be null.
	 * 
	 * <p>Operation: GEOPOS key member [member ...]
	 * 
	 * @param keyVar Variable part of the Redis key
	 * @param members List of geo entities to retrieve
	 * @return List of geo entities containing the coordinates and data for each member
	 */
	@GetMembers
	List<OrangeGeoStringExampleEntity> getMembers2(@KeyVariable(name = "var")String keyVar,@Multiple List<OrangeGeoStringExampleEntity> members);
	
	/**
	 * Searches for members within a circular radius of a given location.
	 * 
	 * <p>Returns members that are within the specified distance from the given location,
	 * including their distances from the center point.
	 * 
	 * <p>Operation: GEOSEARCH key FROMMEMBER member BYRADIUS radius m
	 * 
	 * @param keyVar Variable part of the Redis key
	 * @param location Name of the center location for the radius search
	 * @param distance Search radius in meters
	 * @return List of members within the radius, including their distances from the center
	 */
	@GetMembers
	@SearchArgs
	List<OrangeGeoStringDistanceExampleEntity> getMembersInRadius1(@KeyVariable(name = "var")String keyVar,@RedisValue String location,@Distance Double distance);
	
	/**
	 * Searches for a limited number of members within a circular radius.
	 * 
	 * <p>Returns up to the specified number of members that are within the given distance
	 * from the center location, including their distances from the center point.
	 * 
	 * <p>Operation: GEOSEARCH key FROMMEMBER member BYRADIUS radius m COUNT count
	 * 
	 * @param keyVar Variable part of the Redis key
	 * @param location Name of the center location for the radius search
	 * @param distance Search radius in meters
	 * @param count Maximum number of members to return
	 * @return Limited list of members within the radius, including their distances from the center
	 */
	@GetMembers
	@SearchArgs
	List<OrangeGeoStringDistanceExampleEntity> getMembersInRadius3(@KeyVariable(name = "var")String keyVar,@RedisValue String location,@Distance Double distance,@Count Long count);
	
	/**
	 * Searches for a limited number of members within a circular radius.
	 * 
	 * <p>Returns up to the specified number of members that are within the given distance
	 * from the center location, with their distances from the center point.
	 * 
	 * <p>Operation: GEOSEARCH key FROMMEMBER member BYRADIUS radius m COUNT count
	 * 
	 * @param keyVar Variable part of the Redis key
	 * @param location Name of the center location for the radius search
	 * @param distance Search radius in meters
	 * @param count Maximum number of members to return
	 * @return Map of members within the radius to their distances from the center
	 */
	@GetMembers
	@SearchArgs
	Map<OrangeGeoStringExampleEntity,Double> getMembersInRadius4(@KeyVariable(name = "var")String keyVar,@RedisValue String location,@Distance Double distance,@Count Long count);
	
	/**
	 * Searches for a limited number of members within a circular radius of given coordinates.
	 * 
	 * <p>Returns up to the specified number of members that are within the given distance
	 * from the specified coordinates, with their distances from the center point.
	 * 
	 * <p>Operation: GEOSEARCH key FROMLONLAT longitude latitude BYRADIUS radius m COUNT count
	 * 
	 * @param keyVar Variable part of the Redis key
	 * @param longitude Longitude of the center point
	 * @param latitude Latitude of the center point
	 * @param distance Search radius in meters
	 * @param count Maximum number of members to return
	 * @return Map of members within the radius to their distances from the center
	 */
	@GetMembers
	@SearchArgs
	Map<OrangeGeoStringExampleEntity,Double> getMembersInRadius(@KeyVariable(name = "var")String keyVar,@Longitude Double longitude, @Latitude Double latitude, @Distance Double distance, @Count Long count);
	
	/**
	 * Searches for members within a rectangular box centered on given coordinates.
	 * 
	 * <p>Returns members that are within the specified rectangular area, with their
	 * distances from the center point.
	 * 
	 * <p>Operation: GEOSEARCH key FROMLONLAT longitude latitude BYBOX width height m
	 * 
	 * @param keyVar Variable part of the Redis key
	 * @param longitude Longitude of the center point
	 * @param latitude Latitude of the center point
	 * @param width Width of the box in meters
	 * @param height Height of the box in meters
	 * @return Map of members within the box to their distances from the center
	 */
	@GetMembers
	@SearchArgs
	Map<OrangeGeoStringExampleEntity,Double> getMembersInBox(@KeyVariable(name = "var")String keyVar,@Longitude Double longitude, @Latitude Double latitude, @Width Double width, @Height Double height);
	
	/**
	 * Searches for a limited number of members within a rectangular box centered on given coordinates.
	 * 
	 * <p>Returns up to the specified number of members that are within the specified rectangular area,
	 * with their distances from the center point.
	 * 
	 * <p>Operation: GEOSEARCH key FROMLONLAT longitude latitude BYBOX width height m COUNT count
	 * 
	 * @param keyVar Variable part of the Redis key
	 * @param longitude Longitude of the center point
	 * @param latitude Latitude of the center point
	 * @param width Width of the box in meters
	 * @param height Height of the box in meters
	 * @param count Maximum number of members to return
	 * @return Map of members within the box to their distances from the center
	 */
	@GetMembers
	@SearchArgs
	Map<OrangeGeoStringExampleEntity,Double> getMembersInBox(@KeyVariable(name = "var")String keyVar,@Longitude Double longitude, @Latitude Double latitude, @Width Double width, @Height Double height, @Count Long count);
	
	/**
	 * Searches for members within a rectangular box centered on a given location.
	 * 
	 * <p>Returns members that are within the specified rectangular area, with their
	 * distances from the center point.
	 * 
	 * <p>Operation: GEOSEARCH key FROMMEMBER member BYBOX width height m
	 * 
	 * @param keyVar Variable part of the Redis key
	 * @param location Name of the center location for the box search
	 * @param width Width of the box in meters
	 * @param height Height of the box in meters
	 * @return Map of members within the box to their distances from the center
	 */
	@GetMembers
	@SearchArgs
	Map<OrangeGeoStringExampleEntity,Double> getMembersInBox(@KeyVariable(name = "var")String keyVar,@RedisValue String location, @Width Double width, @Height Double height);
	
	/**
	 * Searches for a limited number of members within a rectangular box centered on a given location.
	 * 
	 * <p>Returns up to the specified number of members that are within the specified rectangular area,
	 * with their distances from the center point.
	 * 
	 * <p>Operation: GEOSEARCH key FROMMEMBER member BYBOX width height m COUNT count
	 * 
	 * @param keyVar Variable part of the Redis key
	 * @param location Name of the center location for the box search
	 * @param width Width of the box in meters
	 * @param height Height of the box in meters
	 * @param count Maximum number of members to return
	 * @return Map of members within the box to their distances from the center
	 */
	@GetMembers
	@SearchArgs
	Map<OrangeGeoStringExampleEntity,Double> getMembersInBox(@KeyVariable(name = "var")String keyVar,@RedisValue String location, @Width Double width, @Height Double height, @Count Long count);
	
}