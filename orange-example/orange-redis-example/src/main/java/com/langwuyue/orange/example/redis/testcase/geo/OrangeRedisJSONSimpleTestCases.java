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
package com.langwuyue.orange.example.redis.testcase.geo;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.langwuyue.orange.example.redis.api.geo.OrangeRedisGeoExample1Api;
import com.langwuyue.orange.example.redis.entity.OrangeGeoExampleEntity;
import com.langwuyue.orange.example.redis.entity.OrangeGeoLocationDistanceExampleEntity;
import com.langwuyue.orange.example.redis.entity.OrangeGeoMultipleExampleEntity;
import com.langwuyue.orange.example.redis.entity.OrangeGeoMultipleLocationExampleEntity;
import com.langwuyue.orange.example.redis.entity.OrangeValueExampleEntity;
import com.langwuyue.orange.example.redis.response.OrangeRedisExampleResponse;

/**
 * @author Liang.Zhong
 * @since 1.0.0
 */
@RestController("geoOrangeRedisJSONSimpleTestCases")
public class OrangeRedisJSONSimpleTestCases {
	
	@Autowired
	private OrangeRedisGeoExample1Api geoExample1Api;
	
	@PutMapping("/v1/geoOperations/member1")
	public OrangeRedisExampleResponse testCase1(@Valid @RequestBody OrangeGeoExampleEntity entity) {
		try {
			return new OrangeRedisExampleResponse(geoExample1Api.add(entity.getLocation(),entity.getLongitude(),entity.getLatitude()));
		}catch (Exception e) {
			e.printStackTrace();
			return new OrangeRedisExampleResponse(1,e.getMessage());
		}
	}
	
	@PutMapping("/v1/geoOperations/member2")
	public OrangeRedisExampleResponse testCase2(@Valid @RequestBody OrangeGeoExampleEntity entity) {
		try {
			return new OrangeRedisExampleResponse(geoExample1Api.add(entity));
		}catch (Exception e) {
			e.printStackTrace();
			return new OrangeRedisExampleResponse(2,e.getMessage());
		}
	}
	
	@PutMapping("/v1/geoOperations/members")
	public OrangeRedisExampleResponse testCase3(@Valid @RequestBody OrangeGeoMultipleExampleEntity entity) {
		try {
			return new OrangeRedisExampleResponse(geoExample1Api.add(entity.getEntities()));
		}catch (Exception e) {
			e.printStackTrace();
			return new OrangeRedisExampleResponse(3,e.getMessage());
		}
	}
	
	@PutMapping("/v1/geoOperations/distance1")
	public OrangeRedisExampleResponse testCase4(@Valid @RequestBody OrangeGeoMultipleLocationExampleEntity entity) {
		try {
			return new OrangeRedisExampleResponse(geoExample1Api.distance(entity.getLocations()));
		}catch (Exception e) {
			e.printStackTrace();
			return new OrangeRedisExampleResponse(4,e.getMessage());
		}
	}
	
	@PutMapping("/v1/geoOperations/distance2")
	public OrangeRedisExampleResponse testCase5(@Valid @RequestBody OrangeGeoMultipleExampleEntity entity) {
		try {
			return new OrangeRedisExampleResponse(geoExample1Api.distance2(entity.getEntities()));
		}catch (Exception e) {
			e.printStackTrace();
			return new OrangeRedisExampleResponse(5,e.getMessage());
		}
	}
	
	@GetMapping("/v1/geoOperations/member")
	public OrangeRedisExampleResponse testCase6(@Valid OrangeValueExampleEntity entity) {
		try {
			return new OrangeRedisExampleResponse(geoExample1Api.getMember(entity));
		}catch (Exception e) {
			e.printStackTrace();
			return new OrangeRedisExampleResponse(6,e.getMessage());
		}
	}
	
	@PutMapping("/v1/geoOperations/membersQueries1")
	public OrangeRedisExampleResponse testCase7(@Valid @RequestBody OrangeGeoMultipleLocationExampleEntity entity) {
		try {
			return new OrangeRedisExampleResponse(geoExample1Api.getMembers(entity.getLocations()));
		}catch (Exception e) {
			e.printStackTrace();
			return new OrangeRedisExampleResponse(7,e.getMessage());
		}
	}
	
	@PutMapping("/v1/geoOperations/membersQueries2")
	public OrangeRedisExampleResponse testCase8(@Valid @RequestBody OrangeGeoMultipleExampleEntity entity) {
		try {
			return new OrangeRedisExampleResponse(geoExample1Api.getMembers2(entity.getEntities()));
		}catch (Exception e) {
			e.printStackTrace();
			return new OrangeRedisExampleResponse(8,e.getMessage());
		}
	}
	
	@PutMapping("/v1/geoOperations/membersInRadius1")
	public OrangeRedisExampleResponse testCase9(@Valid @RequestBody OrangeGeoLocationDistanceExampleEntity entity) {
		try {
			return new OrangeRedisExampleResponse(geoExample1Api.getMembersInRadius1(entity.getLocation(),entity.getDistance()));
		}catch (Exception e) {
			e.printStackTrace();
			return new OrangeRedisExampleResponse(9,e.getMessage());
		}
	}
	
	@DeleteMapping("/v1/geoOperations/member")
	public OrangeRedisExampleResponse testCase10(@Valid OrangeValueExampleEntity entity) {
		try {
			return new OrangeRedisExampleResponse(geoExample1Api.remove(entity));
		}catch (Exception e) {
			e.printStackTrace();
			return new OrangeRedisExampleResponse(10,e.getMessage());
		}
	}
	
	@PutMapping("/v1/geoOperations/removedMembers")
	public OrangeRedisExampleResponse testCase11(@Valid @RequestBody OrangeGeoMultipleLocationExampleEntity entity) {
		try {
			return new OrangeRedisExampleResponse(geoExample1Api.remove(entity.getLocations()));
		}catch (Exception e) {
			e.printStackTrace();
			return new OrangeRedisExampleResponse(11,e.getMessage());
		}
	}
	
	@PutMapping("/v1/geoOperations/membersInRadius2")
	public OrangeRedisExampleResponse testCase12(@Valid @RequestBody OrangeGeoLocationDistanceExampleEntity entity) {
		try {
			return new OrangeRedisExampleResponse(geoExample1Api.getMembersInRadius(entity.getLocation(),entity.getDistance()));
		}catch (Exception e) {
			e.printStackTrace();
			return new OrangeRedisExampleResponse(12,e.getMessage());
		}
	}
	
	@PutMapping("/v1/geoOperations/membersInRadius3")
	public OrangeRedisExampleResponse testCase13(@Valid @RequestBody OrangeGeoLocationDistanceExampleEntity entity, @RequestParam Long count) {
		try {
			return new OrangeRedisExampleResponse(geoExample1Api.getMembersInRadius3(entity.getLocation(),entity.getDistance(),count));
		}catch (Exception e) {
			e.printStackTrace();
			return new OrangeRedisExampleResponse(13,e.getMessage());
		}
	}
	
	@PutMapping("/v1/geoOperations/membersInRadius4")
	public OrangeRedisExampleResponse testCase14(@Valid @RequestBody OrangeGeoLocationDistanceExampleEntity entity, @RequestParam Long count) {
		try {
			return new OrangeRedisExampleResponse(geoExample1Api.getMembersInRadius(entity.getLocation(),entity.getDistance(),count));
		}catch (Exception e) {
			e.printStackTrace();
			return new OrangeRedisExampleResponse(14,e.getMessage());
		}
	}
	
	@GetMapping("/v1/geoOperations/membersInRadius5")
	public OrangeRedisExampleResponse testCase15(@RequestParam Double longitude, @RequestParam Double latitude, @RequestParam Double distance) {
		try {
			return new OrangeRedisExampleResponse(geoExample1Api.getMembersInRadius(longitude,latitude,distance));
		}catch (Exception e) {
			e.printStackTrace();
			return new OrangeRedisExampleResponse(15,e.getMessage());
		}
	}
	
	@GetMapping("/v1/geoOperations/membersInRadius6")
	public OrangeRedisExampleResponse testCase16(
		@RequestParam Double longitude, 
		@RequestParam Double latitude, 
		@RequestParam Double distance,
		@RequestParam Long count
	) {
		try {
			return new OrangeRedisExampleResponse(geoExample1Api.getMembersInRadius(longitude,latitude,distance,count));
		}catch (Exception e) {
			e.printStackTrace();
			return new OrangeRedisExampleResponse(16,e.getMessage());
		}
	}
	
	@GetMapping("/v1/geoOperations/membersInBox1")
	public OrangeRedisExampleResponse testCase17(
		@RequestParam Double longitude, 
		@RequestParam Double latitude, 
		@RequestParam Double width, 
		@RequestParam Double height
	) {
		try {
			return new OrangeRedisExampleResponse(geoExample1Api.getMembersInBox(longitude,latitude,width,height));
		}catch (Exception e) {
			e.printStackTrace();
			return new OrangeRedisExampleResponse(17,e.getMessage());
		}
	}
	
	@GetMapping("/v1/geoOperations/membersInBox2")
	public OrangeRedisExampleResponse testCase18(
		@RequestParam Double longitude, 
		@RequestParam Double latitude, 
		@RequestParam Double width, 
		@RequestParam Double height, 
		@RequestParam Long count
	) {
		try {
			return new OrangeRedisExampleResponse(geoExample1Api.getMembersInBox(longitude,latitude,width,height,count));
		}catch (Exception e) {
			e.printStackTrace();
			return new OrangeRedisExampleResponse(18,e.getMessage());
		}
	}
	
	@PutMapping("/v1/geoOperations/membersInBox3")
	public OrangeRedisExampleResponse testCase19(
		@Valid @RequestBody OrangeValueExampleEntity location,  
		@RequestParam Double width, 
		@RequestParam Double height
	) {
		try {
			return new OrangeRedisExampleResponse(geoExample1Api.getMembersInBox(location,width,height));
		}catch (Exception e) {
			e.printStackTrace();
			return new OrangeRedisExampleResponse(19,e.getMessage());
		}
	}
	
	@PutMapping("/v1/geoOperations/membersInBox4")
	public OrangeRedisExampleResponse testCase20(
		@Valid @RequestBody OrangeValueExampleEntity location, 
		@RequestParam Double width, 
		@RequestParam Double height, 
		@RequestParam Long count
	) {
		try {
			return new OrangeRedisExampleResponse(geoExample1Api.getMembersInBox(location,width,height,count));
		}catch (Exception e) {
			e.printStackTrace();
			return new OrangeRedisExampleResponse(20,e.getMessage());
		}
	}
}
