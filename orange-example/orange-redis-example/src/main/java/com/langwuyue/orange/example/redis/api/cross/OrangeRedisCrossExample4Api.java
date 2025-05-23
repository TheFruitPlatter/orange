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
 * @author Liang.Zhong
 * @since 1.0.0
 */
@OrangeRedisGeoCrossKeyClient
public interface OrangeRedisCrossExample4Api {
	
	@GetMembers
	@CrossOperationKeys({OrangeRedisGeoExample1Api.class})
	@StoreTo(OrangeRedisZSetExample9Api.class)
	@SearchArgs
	Long searchInRadiusAndStoreWithDistance(@RedisValue OrangeValueExampleEntity location,@Distance Double distance);
	
	@GetMembers
	@CrossOperationKeys({OrangeRedisGeoExample1Api.class})
	@StoreTo(OrangeRedisZSetExample9Api.class)
	@SearchArgs
	Long searchInRadiusAndStoreWithDistance(
		@Longitude Double longitude,
		@Latitude Double latitude, 
		@Distance Double distance
	);

	@GetMembers
	@CrossOperationKeys({OrangeRedisGeoExample1Api.class})
	@StoreTo(OrangeRedisZSetExample9Api.class)
	@SearchArgs
	Long searchInBoxAndStoreWithDistance(@RedisValue OrangeValueExampleEntity location,@Width Double width,@Height Double height);
	
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
	
	
	@GetMembers
	@CrossOperationKeys({OrangeRedisGeoExample1Api.class})
	@StoreTo(OrangeRedisGeoExample4Api.class)
	@SearchArgs(includeDistance = false)
	Long searchInRadiusAndStore(@RedisValue OrangeValueExampleEntity location,@Distance Double distance);
	
	@GetMembers
	@CrossOperationKeys({OrangeRedisGeoExample1Api.class})
	@StoreTo(OrangeRedisGeoExample4Api.class)
	@SearchArgs(includeDistance = false)
	Long searchInRadiusAndStore(
		@Longitude Double longitude,
		@Latitude Double latitude, 
		@Distance Double distance
	);

	@GetMembers
	@CrossOperationKeys({OrangeRedisGeoExample1Api.class})
	@StoreTo(OrangeRedisGeoExample4Api.class)
	@SearchArgs(includeDistance = false)
	Long searchInBoxAndStore(@RedisValue OrangeValueExampleEntity location,@Width Double width,@Height Double height);
	
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
