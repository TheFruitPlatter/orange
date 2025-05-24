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
package com.langwuyue.orange.redis.template.geo;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import com.langwuyue.orange.redis.RedisValueTypeEnum;
import com.langwuyue.orange.redis.annotation.AddMembers;
import com.langwuyue.orange.redis.annotation.ContinueOnFailure;
import com.langwuyue.orange.redis.annotation.Count;
import com.langwuyue.orange.redis.annotation.GetMembers;
import com.langwuyue.orange.redis.annotation.Member;
import com.langwuyue.orange.redis.annotation.Multiple;
import com.langwuyue.orange.redis.annotation.RedisValue;
import com.langwuyue.orange.redis.annotation.RemoveMembers;
import com.langwuyue.orange.redis.annotation.geo.Distance;
import com.langwuyue.orange.redis.annotation.geo.Height;
import com.langwuyue.orange.redis.annotation.geo.Latitude;
import com.langwuyue.orange.redis.annotation.geo.Longitude;
import com.langwuyue.orange.redis.annotation.geo.OrangeRedisGeoClient;
import com.langwuyue.orange.redis.annotation.geo.SearchArgs;
import com.langwuyue.orange.redis.annotation.geo.Width;
import com.langwuyue.orange.redis.template.global.GlobalOperationsTemplate;

/**
 * Interface template for Redis Geo operations. 
 * Developers should extend this interface and annotate the child interface with {@code OrangeRedisKey}.
 * 
 * <p>A example is:
 * <blockquote><pre>
 *  {@code @OrangeRedisKey(expirationTime = @Timeout(value = 1, unit = TimeUnit.HOURS), key = "orange:geo:example1")} 
 *  public interface OrangeRedisGeoExample1Api extends JSONOperationsTemplate{@code<Member,Location>} {
 *  	
 *  }
 * </pre></blockquote>
 * 
 * 
 * <p>Please review examples for more information.
 * 
 * @author Liang.Zhong
 * @since 1.0.0
 */
@OrangeRedisGeoClient(valueType = RedisValueTypeEnum.JSON)
public interface JSONOperationsTemplate<M,L> extends GlobalOperationsTemplate {
	
	/**
	 * Adds a location with longitude and latitude coordinates to Redis
	 * @param location The location value to store
	 * @param longitude The longitude coordinate
	 * @param latitude The latitude coordinate
	 * @return true if successfully added
	 */
	@AddMembers
	Boolean add(
		@RedisValue L Location, 
		@Longitude Double longitude, 
		@Latitude Double latitude
	);
	
	/**
	 * Adds a member object containing location information to Redis
	 * @param member The member object containing location data
	 * @return true if successfully added
	 */
	@AddMembers
	Boolean add(@Member M member);
	
	/**
	 * Adds multiple member objects containing location information to Redis
	 * 
	 * <p>
	 * The {@code @ContinueOnFailure} annotation's value determines 
	 * whether the method continues to add subsequent members into Redis geo
	 * even if an exception occurs or some members fail.
	 * True: continue | False: break
	 * 
	 * 
	 * <p>
	 * Developers must override this method when another interface extends this template; 
	 * otherwise, an exception will occur, 
	 * because the method's return type involves a generic argument M.
	 * 
	 * 
	 * 
	 * @param members Collection of member objects to add
	 * @return Map of member objects to their add operation status
	 */
	@AddMembers
	@ContinueOnFailure(true)
	Map<M,Boolean> add(@Multiple Collection<M> members);
	
	/**
	 * Calculates distance between multiple locations
	 * 
	 * 
	 * @param locations List of locations to calculate distances between
	 * @return The calculated distance
	 */
	@Distance
	Double distance(@Multiple List<L> locations);
	
	/**
	 * Gets member information for a single location
	 * 
	 * <p>
	 * Developers must override this method when another interface extends this template; 
	 * otherwise, an exception will occur, 
	 * because the method's return type involves a generic argument M.
	 * 
	 * 
	 * @param location The location to query
	 * @return The member object associated with the location
	 */
	@GetMembers
	M getMember(@RedisValue L location);

	/**
	 * Gets member information for multiple locations
	 * 
	 * <p>
	 * Developers must override this method when another interface extends this template; 
	 * otherwise, an exception will occur, 
	 * because the method's return type involves a generic argument M.
	 * 
	 * 
	 * 
	 * @param locations List of locations to query
	 * @return List of member objects associated with the locations
	 */
	@GetMembers
	List<M> getMembers(@Multiple List<L> locations);
	
	/**
	 * Finds members within a radius of a location
	 * 
	 * 
	 * <p>
	 * Developers must override this method when another interface extends this template; 
	 * otherwise, an exception will occur, 
	 * because the method's return type involves a generic argument M.
	 * 
	 * 
	 * @param location The center location
	 * @param distance The search radius
	 * @return Map of members to their distances from the center
	 */
	@GetMembers
	@SearchArgs
	Map<M,Double> getMembersInRadius(@RedisValue L location,@Distance Double distance);
	
	/**
	 * Finds members within a radius of a location with result limit
	 * 
	 * <p>
	 * Developers must override this method when another interface extends this template; 
	 * otherwise, an exception will occur, 
	 * because the method's return type involves a generic argument M.
	 * 
	 * 
	 * 
	 * @param location The center location
	 * @param distance The search radius
	 * @param count Maximum number of results to return
	 * @return Map of members to their distances from the center
	 */
	@GetMembers
	@SearchArgs
	Map<M,Double> getMembersInRadius(@RedisValue L location,@Distance Double distance, @Count Long count);
	
	/**
	 * Finds members within a radius of coordinates
	 * 
	 * 
	 * <p>
	 * Developers must override this method when another interface extends this template; 
	 * otherwise, an exception will occur, 
	 * because the method's return type involves a generic argument M.
	 * 
	 * 
	 * @param longitude The center longitude
	 * @param latitude The center latitude
	 * @param distance The search radius
	 * @return Map of members to their distances from the center
	 */
	@GetMembers
	@SearchArgs
	Map<M,Double> getMembersInRadius(@Longitude Double longitude, @Latitude Double latitude, @Distance Double distance);
	
	/**
	 * Finds members within a radius of coordinates with result limit
	 * 
	 * <p>
	 * Developers must override this method when another interface extends this template; 
	 * otherwise, an exception will occur, 
	 * because the method's return type involves a generic argument M.
	 * 
	 * 
	 * 
	 * @param longitude The center longitude
	 * @param latitude The center latitude
	 * @param distance The search radius
	 * @param count Maximum number of results to return
	 * @return Map of members to their distances from the center
	 */
	@GetMembers
	@SearchArgs
	Map<M,Double> getMembersInRadius(@Longitude Double longitude, @Latitude Double latitude, @Distance Double distance, @Count Long count);
	
	/**
	 * Finds members within a rectangular area defined by coordinates and dimensions
	 * 
	 * <p>
	 * Developers must override this method when another interface extends this template; 
	 * otherwise, an exception will occur, 
	 * because the method's return type involves a generic argument M.
	 * 
	 * 
	 * 
	 * @param longitude The center longitude
	 * @param latitude The center latitude
	 * @param width The width of the search area
	 * @param height The height of the search area
	 * @return Map of members to their distances from the center
	 */
	@GetMembers
	@SearchArgs
	Map<M,Double> getMembersInBox(@Longitude Double longitude, @Latitude Double latitude, @Width Double width, @Height Double height);
	
	/**
	 * Finds members within a rectangular area with result limit
	 * 
	 * <p>
	 * Developers must override this method when another interface extends this template; 
	 * otherwise, an exception will occur, 
	 * because the method's return type involves a generic argument M.
	 * 
	 * 
	 * 
	 * @param longitude The center longitude
	 * @param latitude The center latitude
	 * @param width The width of the search area
	 * @param height The height of the search area
	 * @param count Maximum number of results to return
	 * @return Map of members to their distances from the center
	 */
	@GetMembers
	@SearchArgs
	Map<M,Double> getMembersInBox(@Longitude Double longitude, @Latitude Double latitude, @Width Double width, @Height Double height, @Count Long count);
	
	/**
	 * Finds members within a rectangular area around a location
	 * 
	 * <p>
	 * Developers must override this method when another interface extends this template; 
	 * otherwise, an exception will occur, 
	 * because the method's return type involves a generic argument M.
	 * 
	 * 
	 * @param location The center location
	 * @param width The width of the search area
	 * @param height The height of the search area
	 * @return Map of members to their distances from the center
	 */
	@GetMembers
	@SearchArgs
	Map<M,Double> getMembersInBox(@RedisValue L location, @Width Double width, @Height Double height);
	
	/**
	 * Finds members within a rectangular area around a location with result limit
	 * 
	 * <p>
	 * Developers must override this method when another interface extends this template; 
	 * otherwise, an exception will occur, 
	 * because the method's return type involves a generic argument M.
	 * 
	 * 
	 * @param location The center location
	 * @param width The width of the search area
	 * @param height The height of the search area
	 * @param count Maximum number of results to return
	 * @return Map of members to their distances from the center
	 */
	@GetMembers
	@SearchArgs
	Map<M,Double> getMembersInBox(@RedisValue L location, @Width Double width, @Height Double height, @Count Long count);
	
	/**
	 * Removes a location from Redis
	 * 
	 * @param location The location to remove
	 * @return true if successfully removed
	 */
	@RemoveMembers
	Boolean remove(@RedisValue L location);
	
	/**
	 * Removes multiple locations from Redis
	 * 
	 * <p>
	 * The {@code @ContinueOnFailure} annotation's value determines 
	 * whether the method continues to remove subsequent members from Redis geo
	 * even if an exception occurs or some members fail.
	 * True: continue | False: break
	 * 
	 * 
	 * <p>
	 * Developers must override this method when another interface extends this template; 
	 * otherwise, an exception will occur, 
	 * because the method's return type involves a generic argument L.
	 * 
	 * 
	 * @param locations Collection of locations to remove
	 * @return Map of locations to their remove operation status
	 */
	@RemoveMembers
	@ContinueOnFailure(true)
	Map<L,Boolean> remove(@Multiple Collection<L> locations);
	
}