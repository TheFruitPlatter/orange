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
package com.langwuyue.orange.example.redis.testcase.cross;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.langwuyue.orange.example.redis.api.cross.OrangeRedisCrossExample4Api;
import com.langwuyue.orange.example.redis.api.geo.OrangeRedisGeoExample4Api;
import com.langwuyue.orange.example.redis.api.zset.OrangeRedisZSetExample9Api;
import com.langwuyue.orange.example.redis.entity.OrangeGeoLocationDistanceExampleEntity;
import com.langwuyue.orange.example.redis.entity.OrangeValueExampleEntity;
import com.langwuyue.orange.example.redis.response.OrangeRedisExampleResponse;

/**
 * @author Liang.Zhong
 * @since 1.0.0
 */
@RestController("crossOrangeRedisGeoCrossKeyTestCases")
public class OrangeRedisGeoCrossKeyTestCases {
	
	@Autowired
	private OrangeRedisCrossExample4Api crossExample4Api;
	
	@Autowired
	private OrangeRedisGeoExample4Api geoExample4Api;
	
	@Autowired
	private OrangeRedisZSetExample9Api zsetExample9Api;
	
	@PostMapping("/v1/geoCrossOperations/memberWithDistanceInRadius1")
	public OrangeRedisExampleResponse testCase1(@Valid @RequestBody OrangeGeoLocationDistanceExampleEntity entity) {
		try {
			return new OrangeRedisExampleResponse(crossExample4Api.searchInRadiusAndStoreWithDistance(entity.getLocation(),entity.getDistance()));
		}catch (Exception e) {
			e.printStackTrace();
			return new OrangeRedisExampleResponse(1,e.getMessage());
		}
	}
	
	@PostMapping("/v1/geoCrossOperations/memberWithDistanceInRadius2")
	public OrangeRedisExampleResponse testCase2(
		@RequestParam Double longitude,
		@RequestParam Double latitude,
		@RequestParam Double distance
	) {
		try {
			return new OrangeRedisExampleResponse(crossExample4Api.searchInRadiusAndStoreWithDistance(longitude,latitude,distance));
		}catch (Exception e) {
			e.printStackTrace();
			return new OrangeRedisExampleResponse(2,e.getMessage());
		}
	}
	
	@PostMapping("/v1/geoCrossOperations/memberWithDistanceInBox1")
	public OrangeRedisExampleResponse testCase3(
		@Valid @RequestBody OrangeValueExampleEntity location,
		@RequestParam Double width,
		@RequestParam Double height
	) {
		try {
			return new OrangeRedisExampleResponse(crossExample4Api.searchInBoxAndStoreWithDistance(location,width,height));
		}catch (Exception e) {
			e.printStackTrace();
			return new OrangeRedisExampleResponse(3,e.getMessage());
		}
	}
	
	@PostMapping("/v1/geoCrossOperations/memberWithDistanceInBox2")
	public OrangeRedisExampleResponse testCase3(
		@RequestParam Double longitude,
		@RequestParam Double latitude,
		@RequestParam Double width,
		@RequestParam Double height
	) {
		try {
			return new OrangeRedisExampleResponse(crossExample4Api.searchInBoxAndStoreWithDistance(longitude,latitude,width,height));
		}catch (Exception e) {
			e.printStackTrace();
			return new OrangeRedisExampleResponse(3,e.getMessage());
		}
	}
	
	
	@PostMapping("/v1/geoCrossOperations/memberInRadius1")
	public OrangeRedisExampleResponse testCase4(@Valid @RequestBody OrangeGeoLocationDistanceExampleEntity entity) {
		try {
			return new OrangeRedisExampleResponse(crossExample4Api.searchInRadiusAndStore(entity.getLocation(),entity.getDistance()));
		}catch (Exception e) {
			e.printStackTrace();
			return new OrangeRedisExampleResponse(4,e.getMessage());
		}
	}
	
	@PostMapping("/v1/geoCrossOperations/memberInRadius2")
	public OrangeRedisExampleResponse testCase5(
		@RequestParam Double longitude,
		@RequestParam Double latitude,
		@RequestParam Double distance
	) {
		try {
			return new OrangeRedisExampleResponse(crossExample4Api.searchInRadiusAndStore(longitude,latitude,distance));
		}catch (Exception e) {
			e.printStackTrace();
			return new OrangeRedisExampleResponse(5,e.getMessage());
		}
	}
	
	@PostMapping("/v1/geoCrossOperations/memberInBox1")
	public OrangeRedisExampleResponse testCase6(
		@Valid @RequestBody OrangeValueExampleEntity location,
		@RequestParam Double width,
		@RequestParam Double height
	) {
		try {
			return new OrangeRedisExampleResponse(crossExample4Api.searchInBoxAndStore(location,width,height));
		}catch (Exception e) {
			e.printStackTrace();
			return new OrangeRedisExampleResponse(6,e.getMessage());
		}
	}
	
	@PostMapping("/v1/geoCrossOperations/memberInBox2")
	public OrangeRedisExampleResponse testCase7(
		@RequestParam Double longitude,
		@RequestParam Double latitude,
		@RequestParam Double width,
		@RequestParam Double height
	) {
		try {
			return new OrangeRedisExampleResponse(crossExample4Api.searchInBoxAndStore(longitude,latitude,width,height));
		}catch (Exception e) {
			e.printStackTrace();
			return new OrangeRedisExampleResponse(7,e.getMessage());
		}
	}
	
	@GetMapping("/v1/geoCrossOperations/rankRangeMembers")
	public OrangeRedisExampleResponse testCase8(@RequestParam Long startIndex,@RequestParam Long endIndex) {
		try {
			return new OrangeRedisExampleResponse(zsetExample9Api.getByRankRangeWithScores(startIndex,endIndex));
		}catch (Exception e) {
			e.printStackTrace();
			return new OrangeRedisExampleResponse(8,e.getMessage());
		}
	}
	
	@GetMapping("/v1/geoCrossOperations/member")
	public OrangeRedisExampleResponse testCase6(@Valid OrangeValueExampleEntity entity) {
		try {
			return new OrangeRedisExampleResponse(geoExample4Api.getMember(entity));
		}catch (Exception e) {
			e.printStackTrace();
			return new OrangeRedisExampleResponse(6,e.getMessage());
		}
	}
}
