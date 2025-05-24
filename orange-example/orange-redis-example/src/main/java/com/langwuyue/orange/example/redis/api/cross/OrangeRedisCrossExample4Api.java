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
package com.langwuyue.orange.example.redis.api.cross;

import com.langwuyue.orange.example.redis.api.geo.OrangeRedisGeoExample1Api;
import com.langwuyue.orange.example.redis.api.geo.OrangeRedisGeoExample4Api;
import com.langwuyue.orange.example.redis.api.zset.OrangeRedisZSetExample9Api;
import com.langwuyue.orange.example.redis.entity.OrangeValueExampleEntity;
import com.langwuyue.orange.redis.annotation.GetMembers;
import com.langwuyue.orange.redis.annotation.RedisValue;
import com.langwuyue.orange.redis.annotation.cross.CrossOperationKeys;
import com.langwuyue.orange.redis.annotation.cross.OrangeRedisGeoCrossKeyClient;
import com.langwuyue.orange.redis.annotation.cross.StoreTo;
import com.langwuyue.orange.redis.annotation.geo.Distance;
import com.langwuyue.orange.redis.annotation.geo.Height;
import com.langwuyue.orange.redis.annotation.geo.Latitude;
import com.langwuyue.orange.redis.annotation.geo.Longitude;
import com.langwuyue.orange.redis.annotation.geo.SearchArgs;
import com.langwuyue.orange.redis.annotation.geo.Width;

/**
 * Example interface demonstrating cross-type operations between Redis GEO and ZSET types.
 * 
 * <p>Specialized for searching geographic data and storing results:
 * <ul>
 *   <li>Searches GEO data from {@link OrangeRedisGeoExample1Api}</li>
 *   <li>Stores results either:
 *     <ul>
 *       <li>In ZSET ({@link OrangeRedisZSetExample9Api}) with distance scores</li>
 *       <li>Back to GEO ({@link OrangeRedisGeoExample4Api}) without distances</li>
 *     </ul>
 *   </li>
 * </ul>
 * 
 * <p>Search patterns:
 * <ul>
 *   <li>Radius search - Finds members within circular area</li>
 *   <li>Box search - Finds members within rectangular area</li>
 * </ul>
 * 
 * <p>Parameter variants:
 * <ul>
 *   <li>Using {@code @RedisValue} entity objects</li>
 *   <li>Using raw {@code @Longitude} and {@code @Latitude} coordinates</li>
 * </ul>
 * 
 * <p>Key configuration:
 * <ul>
 *   <li>Source GEO data from {@code @CrossOperationKeys}</li>
 *   <li>Destination storage via {@code @StoreTo}</li>
 *   <li>Search parameters via {@code @SearchArgs}</li>
 *   <li>Distance inclusion controlled by {@code includeDistance}</li>
 * </ul>
 * 
 * <p>Example usage:
 * <pre>{@code
 * // Radius search storing to ZSET with distances
 * OrangeValueExampleEntity location = ...;
 * Long count1 = api.searchInRadiusAndStoreWithDistance(location, 1000.0);
 * 
 * // Box search storing to GEO without distances
 * Long count2 = api.searchInBoxAndStore(121.47, 31.23, 500.0, 500.0);
 * }</pre>
 * 
 * <p>Performance notes:
 * <ul>
 *   <li>Radius searches are O(N+log(M)) where N is members in area</li>
 *   <li>Box searches are O(N) where N is members in area</li>
 *   <li>Storing to ZSET adds O(M) where M is results count</li>
 * </ul>
 * 
 * @author Liang.Zhong
 * @since 1.0.0
 * @see CrossOperationKeys Specifies source GEO data
 * @see StoreTo Specifies destination storage
 * @see SearchArgs Configures search parameters
 * @see OrangeRedisGeoExample1Api Source GEO data
 * @see OrangeRedisZSetExample9Api ZSET storage destination
 * @see OrangeRedisGeoExample4Api GEO storage destination
 */
@OrangeRedisGeoCrossKeyClient
public interface OrangeRedisCrossExample4Api {
	
	/**
	 * Searches for members within a circular radius and stores results in a sorted set with distance scores.
	 * 
	 * <p>Uses an entity object to specify the center location. Members within the specified
	 * distance from this point are stored in a ZSET, with their distances as scores.
	 * 
	 * @param location Entity containing the center coordinates
	 * @param distance Search radius in meters
	 * @return Number of members found and stored
	 * @see OrangeRedisGeoExample1Api Source of GEO data
	 * @see OrangeRedisZSetExample9Api Destination ZSET for results with distances
	 */
	@GetMembers
	@CrossOperationKeys({OrangeRedisGeoExample1Api.class})
	@StoreTo(OrangeRedisZSetExample9Api.class)
	@SearchArgs
	Long searchInRadiusAndStoreWithDistance(@RedisValue OrangeValueExampleEntity location,@Distance Double distance);
	
	/**
	 * Searches for members within a circular radius and stores results in a sorted set with distance scores.
	 * 
	 * <p>Uses raw coordinates to specify the center location. Members within the specified
	 * distance from this point are stored in a ZSET, with their distances as scores.
	 * 
	 * @param longitude Center point longitude
	 * @param latitude Center point latitude
	 * @param distance Search radius in meters
	 * @return Number of members found and stored
	 * @see OrangeRedisGeoExample1Api Source of GEO data
	 * @see OrangeRedisZSetExample9Api Destination ZSET for results with distances
	 */
	@GetMembers
	@CrossOperationKeys({OrangeRedisGeoExample1Api.class})
	@StoreTo(OrangeRedisZSetExample9Api.class)
	@SearchArgs
	Long searchInRadiusAndStoreWithDistance(
		@Longitude Double longitude,
		@Latitude Double latitude, 
		@Distance Double distance
	);

	/**
	 * Searches for members within a rectangular box and stores results in a sorted set with distance scores.
	 * 
	 * <p>Uses an entity object to specify the center location. Members within the rectangular
	 * area defined by width and height from this point are stored in a ZSET, with their
	 * distances from the center as scores.
	 * 
	 * @param location Entity containing the center coordinates
	 * @param width Box width in meters
	 * @param height Box height in meters
	 * @return Number of members found and stored
	 * @see OrangeRedisGeoExample1Api Source of GEO data
	 * @see OrangeRedisZSetExample9Api Destination ZSET for results with distances
	 */
	@GetMembers
	@CrossOperationKeys({OrangeRedisGeoExample1Api.class})
	@StoreTo(OrangeRedisZSetExample9Api.class)
	@SearchArgs
	Long searchInBoxAndStoreWithDistance(@RedisValue OrangeValueExampleEntity location,@Width Double width,@Height Double height);
	
	/**
	 * Searches for members within a rectangular box and stores results in a sorted set with distance scores.
	 * 
	 * <p>Uses raw coordinates to specify the center location. Members within the rectangular
	 * area defined by width and height from this point are stored in a ZSET, with their
	 * distances from the center as scores.
	 * 
	 * @param longitude Center point longitude
	 * @param latitude Center point latitude
	 * @param width Box width in meters
	 * @param height Box height in meters
	 * @return Number of members found and stored
	 * @see OrangeRedisGeoExample1Api Source of GEO data
	 * @see OrangeRedisZSetExample9Api Destination ZSET for results with distances
	 */
	@GetMembers
	@CrossOperationKeys({OrangeRedisGeoExample1Api.class})
	@StoreTo(OrangeRedisZSetExample9Api.class)
	@SearchArgs
	Long searchInBoxAndStoreWithDistance(
		@Longitude Double longitude,
		@Latitude Double latitude, 
		@Width Double width,
		@Height Double height
	);
	
	
	/**
	 * Searches for members within a circular radius and stores results in a GEO set.
	 * 
	 * <p>Uses an entity object to specify the center location. Members within the specified
	 * distance from this point are stored in another GEO set, preserving their original
	 * coordinates but without distance information.
	 * 
	 * @param location Entity containing the center coordinates
	 * @param distance Search radius in meters
	 * @return Number of members found and stored
	 * @see OrangeRedisGeoExample1Api Source of GEO data
	 * @see OrangeRedisGeoExample4Api Destination GEO set for results
	 */
	@GetMembers
	@CrossOperationKeys({OrangeRedisGeoExample1Api.class})
	@StoreTo(OrangeRedisGeoExample4Api.class)
	@SearchArgs(includeDistance = false)
	Long searchInRadiusAndStore(@RedisValue OrangeValueExampleEntity location,@Distance Double distance);
	
	/**
	 * Searches for members within a circular radius and stores results in a GEO set.
	 * 
	 * <p>Uses raw coordinates to specify the center location. Members within the specified
	 * distance from this point are stored in another GEO set, preserving their original
	 * coordinates but without distance information.
	 * 
	 * @param longitude Center point longitude
	 * @param latitude Center point latitude
	 * @param distance Search radius in meters
	 * @return Number of members found and stored
	 * @see OrangeRedisGeoExample1Api Source of GEO data
	 * @see OrangeRedisGeoExample4Api Destination GEO set for results
	 */
	@GetMembers
	@CrossOperationKeys({OrangeRedisGeoExample1Api.class})
	@StoreTo(OrangeRedisGeoExample4Api.class)
	@SearchArgs(includeDistance = false)
	Long searchInRadiusAndStore(
		@Longitude Double longitude,
		@Latitude Double latitude, 
		@Distance Double distance
	);

	/**
	 * Searches for members within a rectangular box and stores results in a GEO set.
	 * 
	 * <p>Uses an entity object to specify the center location. Members within the rectangular
	 * area defined by width and height from this point are stored in another GEO set,
	 * preserving their original coordinates but without distance information.
	 * 
	 * @param location Entity containing the center coordinates
	 * @param width Box width in meters
	 * @param height Box height in meters
	 * @return Number of members found and stored
	 * @see OrangeRedisGeoExample1Api Source of GEO data
	 * @see OrangeRedisGeoExample4Api Destination GEO set for results
	 */
	@GetMembers
	@CrossOperationKeys({OrangeRedisGeoExample1Api.class})
	@StoreTo(OrangeRedisGeoExample4Api.class)
	@SearchArgs(includeDistance = false)
	Long searchInBoxAndStore(@RedisValue OrangeValueExampleEntity location,@Width Double width,@Height Double height);
	
	/**
	 * Searches for members within a rectangular box and stores results in a GEO set.
	 * 
	 * <p>Uses raw coordinates to specify the center location. Members within the rectangular
	 * area defined by width and height from this point are stored in another GEO set,
	 * preserving their original coordinates but without distance information.
	 * 
	 * @param longitude Center point longitude
	 * @param latitude Center point latitude
	 * @param width Box width in meters
	 * @param height Box height in meters
	 * @return Number of members found and stored
	 * @see OrangeRedisGeoExample1Api Source of GEO data
	 * @see OrangeRedisGeoExample4Api Destination GEO set for results
	 */
	@GetMembers
	@CrossOperationKeys({OrangeRedisGeoExample1Api.class})
	@StoreTo(OrangeRedisGeoExample4Api.class)
	@SearchArgs(includeDistance = false)
	Long searchInBoxAndStore(
		@Longitude Double longitude,
		@Latitude Double latitude, 
		@Width Double width,
		@Height Double height
	);
}