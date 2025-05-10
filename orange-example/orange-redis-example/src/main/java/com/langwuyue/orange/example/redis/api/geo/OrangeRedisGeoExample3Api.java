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
	
	@AddMembers
	Boolean add(
		@KeyVariable(name = "var")String keyVar,
		@RedisValue String Location, 
		@Longitude Double longitude, 
		@Latitude Double latitude
	);
	
	@AddMembers
	Boolean add(@KeyVariable(name = "var")String keyVar,@Member OrangeGeoStringExampleEntity member);
	
	@AddMembers
	@ContinueOnFailure(true)
	Map<OrangeGeoStringExampleEntity,Boolean> add(@KeyVariable(name = "var")String keyVar,@Multiple Collection<OrangeGeoStringExampleEntity> members);
	
	@Distance
	Double distance(@KeyVariable(name = "var")String keyVar,@Multiple List<String> locations);
	
	@GetMembers
	OrangeGeoStringExampleEntity getMember(@KeyVariable(name = "var")String keyVar,@RedisValue String location);

	@GetMembers
	List<OrangeGeoStringExampleEntity> getMembers(@KeyVariable(name = "var")String keyVar,@Multiple List<String> locations);
	
	@GetMembers
	@SearchArgs
	Map<OrangeGeoStringExampleEntity,Double> getMembersInRadius(@KeyVariable(name = "var")String keyVar,@RedisValue String location,@Distance Double distance);
	
	@GetMembers
	@SearchArgs
	Map<OrangeGeoStringExampleEntity,Double> getMembersInRadius(@KeyVariable(name = "var")String keyVar,@Longitude Double longitude, @Latitude Double latitude, @Distance Double distance);
	
	@RemoveMembers
	Boolean remove(@KeyVariable(name = "var")String keyVar,@RedisValue String location);
	
	@RemoveMembers
	@ContinueOnFailure(true)
	Map<String,Boolean> remove(@KeyVariable(name = "var")String keyVar,@Multiple Collection<String> locations);

	@Distance
	Double distance2(@KeyVariable(name = "var")String keyVar,@Multiple List<OrangeGeoStringExampleEntity> locations);
	
	@GetMembers
	List<OrangeGeoStringExampleEntity> getMembers2(@KeyVariable(name = "var")String keyVar,@Multiple List<OrangeGeoStringExampleEntity> members);
	
	@GetMembers
	@SearchArgs
	List<OrangeGeoStringDistanceExampleEntity> getMembersInRadius1(@KeyVariable(name = "var")String keyVar,@RedisValue String location,@Distance Double distance);
	
	@GetMembers
	@SearchArgs
	List<OrangeGeoStringDistanceExampleEntity> getMembersInRadius3(@KeyVariable(name = "var")String keyVar,@RedisValue String location,@Distance Double distance,@Count Long count);
	
	@GetMembers
	@SearchArgs
	Map<OrangeGeoStringExampleEntity,Double> getMembersInRadius4(@KeyVariable(name = "var")String keyVar,@RedisValue String location,@Distance Double distance,@Count Long count);
	
	@GetMembers
	@SearchArgs
	Map<OrangeGeoStringExampleEntity,Double> getMembersInRadius(@KeyVariable(name = "var")String keyVar,@Longitude Double longitude, @Latitude Double latitude, @Distance Double distance, @Count Long count);
	
	@GetMembers
	@SearchArgs
	Map<OrangeGeoStringExampleEntity,Double> getMembersInBox(@KeyVariable(name = "var")String keyVar,@Longitude Double longitude, @Latitude Double latitude, @Width Double width, @Height Double height);
	
	@GetMembers
	@SearchArgs
	Map<OrangeGeoStringExampleEntity,Double> getMembersInBox(@KeyVariable(name = "var")String keyVar,@Longitude Double longitude, @Latitude Double latitude, @Width Double width, @Height Double height, @Count Long count);
	
	@GetMembers
	@SearchArgs
	Map<OrangeGeoStringExampleEntity,Double> getMembersInBox(@KeyVariable(name = "var")String keyVar,@RedisValue String location, @Width Double width, @Height Double height);
	
	@GetMembers
	@SearchArgs
	Map<OrangeGeoStringExampleEntity,Double> getMembersInBox(@KeyVariable(name = "var")String keyVar,@RedisValue String location, @Width Double width, @Height Double height, @Count Long count);
	
}