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

import com.langwuyue.orange.example.redis.entity.OrangeGeoDistanceExampleEntity;
import com.langwuyue.orange.example.redis.entity.OrangeGeoExampleEntity;
import com.langwuyue.orange.example.redis.entity.OrangeValueExampleEntity;
import com.langwuyue.orange.redis.annotation.Count;
import com.langwuyue.orange.redis.annotation.GetMembers;
import com.langwuyue.orange.redis.annotation.Multiple;
import com.langwuyue.orange.redis.annotation.OrangeRedisKey;
import com.langwuyue.orange.redis.annotation.RedisValue;
import com.langwuyue.orange.redis.annotation.Timeout;
import com.langwuyue.orange.redis.annotation.geo.Distance;
import com.langwuyue.orange.redis.annotation.geo.SearchArgs;
import com.langwuyue.orange.redis.template.geo.JSONOperationsTemplate;


/**
 * @author Liang.Zhong
 * @since 1.0.0
 */
@OrangeRedisKey(expirationTime = @Timeout(value = 1, unit = TimeUnit.HOURS), key = "orange:geo:example1")
public interface OrangeRedisGeoExample1Api extends JSONOperationsTemplate<OrangeGeoExampleEntity,OrangeValueExampleEntity> {
	
	@Override
	default Map<OrangeGeoExampleEntity, Boolean> add(Collection<OrangeGeoExampleEntity> members) {
		return null;
	}

	@Override
	OrangeGeoExampleEntity getMember(OrangeValueExampleEntity location);

	@Override
	default List<OrangeGeoExampleEntity> getMembers(List<OrangeValueExampleEntity> locations) {
		return null;
	}

	@Override
	default Map<OrangeGeoExampleEntity, Double> getMembersInRadius(OrangeValueExampleEntity location,
			Double distance) {
		return null;
	}
	
	@Override
	default Map<OrangeGeoExampleEntity, Double> getMembersInRadius(Double longitude, Double latitude, Double distance) {
		return null;
	}
	
	@Override
	default Map<OrangeGeoExampleEntity, Double> getMembersInRadius(OrangeValueExampleEntity location, Double distance, Long count) {
		return null;
	}

	@Override
	default Map<OrangeGeoExampleEntity, Double> getMembersInRadius(Double longitude, Double latitude,
			Double distance, Long count) {
		return null;
	}

	@Override
	default Map<OrangeGeoExampleEntity, Double> getMembersInBox(Double longitude, Double latitude, Double width,
			Double height) {
		return null;
	}

	@Override
	default Map<OrangeGeoExampleEntity, Double> getMembersInBox(Double longitude, Double latitude, Double width,
			Double height, Long count) {
		return null;
	}
	
	@Override
	default Map<OrangeGeoExampleEntity, Double> getMembersInBox(OrangeValueExampleEntity location, Double width,
			Double height) {
		return null;
	}

	@Override
	default Map<OrangeGeoExampleEntity, Double> getMembersInBox(OrangeValueExampleEntity location, Double width,
			Double height, Long count) {
		return null;
	}

	@Override
	default Map<OrangeValueExampleEntity, Boolean> remove(Collection<OrangeValueExampleEntity> members) {
		return null;
	}

	/**
	 * Calculates the distance between multiple geo locations.
	 * 
	 * <p>Returns the distance between consecutive pairs of locations in the list.
	 * For example, if the list contains [A, B, C], it will calculate the distance
	 * between A and B, then B and C.
	 * 
	 * <p>Operation: GEODIST key member1 member2 [unit]
	 * 
	 * @param locations List of locations to calculate distances between
	 * @return The distance in meters between consecutive locations
	 */
	@Distance
	Double distance2(@Multiple List<OrangeGeoExampleEntity> locations);
	
	/**
	 * Retrieves multiple geo members by their values.
	 * 
	 * <p>Returns the geo information (coordinates and member data) for the specified members.
	 * If a member doesn't exist, its corresponding entry in the result will be null.
	 * 
	 * <p>Operation: GEOPOS key member [member ...]
	 * 
	 * @param members List of members to retrieve
	 * @return List of geo entities containing the coordinates and data for each member
	 */
	@GetMembers
	List<OrangeGeoExampleEntity> getMembers2(@Multiple List<OrangeGeoExampleEntity> members);
	
	/**
	 * Searches for members within a circular radius of a given location.
	 * 
	 * <p>Returns members that are within the specified distance from the given location,
	 * including their distances from the center point.
	 * 
	 * <p>Operation: GEOSEARCH key FROMMEMBER member BYRADIUS radius m
	 * 
	 * @param location Center point for the radius search
	 * @param distance Search radius in meters
	 * @return List of members within the radius, including their distances from the center
	 */
	@GetMembers
	@SearchArgs
	List<OrangeGeoDistanceExampleEntity> getMembersInRadius1(@RedisValue OrangeValueExampleEntity location,@Distance Double distance);
	
	/**
	 * Searches for a limited number of members within a circular radius.
	 * 
	 * <p>Returns up to the specified number of members that are within the given distance
	 * from the center location, including their distances from the center point.
	 * 
	 * <p>Operation: GEOSEARCH key FROMMEMBER member BYRADIUS radius m COUNT count
	 * 
	 * @param location Center point for the radius search
	 * @param distance Search radius in meters
	 * @param count Maximum number of members to return
	 * @return Limited list of members within the radius, including their distances from the center
	 */
	@GetMembers
	@SearchArgs
	List<OrangeGeoDistanceExampleEntity> getMembersInRadius3(@RedisValue OrangeValueExampleEntity location,@Distance Double distance,@Count Long count);
	
}