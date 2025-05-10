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

	@Distance
	Double distance2(@Multiple List<OrangeGeoExampleEntity> locations);
	
	@GetMembers
	List<OrangeGeoExampleEntity> getMembers2(@Multiple List<OrangeGeoExampleEntity> members);
	
	@GetMembers
	@SearchArgs
	List<OrangeGeoDistanceExampleEntity> getMembersInRadius1(@RedisValue OrangeValueExampleEntity location,@Distance Double distance);
	
	@GetMembers
	@SearchArgs
	List<OrangeGeoDistanceExampleEntity> getMembersInRadius3(@RedisValue OrangeValueExampleEntity location,@Distance Double distance,@Count Long count);
	
}