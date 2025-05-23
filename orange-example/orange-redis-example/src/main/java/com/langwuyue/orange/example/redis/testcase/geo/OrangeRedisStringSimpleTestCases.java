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

import com.langwuyue.orange.example.redis.api.geo.OrangeRedisGeoExample2Api;
import com.langwuyue.orange.example.redis.entity.OrangeGeoStringExampleEntity;
import com.langwuyue.orange.example.redis.entity.OrangeGeoStringLocationDistanceExampleEntity;
import com.langwuyue.orange.example.redis.entity.OrangeGeoStringMultipleExampleEntity;
import com.langwuyue.orange.example.redis.entity.OrangeGeoStringMultipleLocationExampleEntity;
import com.langwuyue.orange.example.redis.response.OrangeRedisExampleResponse;

/**
 * @author Liang.Zhong
 * @since 1.0.0
 */
@RestController("geoOrangeRedisStringSimpleTestCases")
public class OrangeRedisStringSimpleTestCases {
	
	@Autowired
	private OrangeRedisGeoExample2Api geoExample2Api;
	
	@PutMapping("/v1/geoStringOperations/member1")
	public OrangeRedisExampleResponse testCase1(@Valid @RequestBody OrangeGeoStringExampleEntity entity) {
		try {
			return new OrangeRedisExampleResponse(geoExample2Api.add(entity.getLocation(),entity.getLongitude(),entity.getLatitude()));
		}catch (Exception e) {
			e.printStackTrace();
			return new OrangeRedisExampleResponse(1,e.getMessage());
		}
	}
	
	@PutMapping("/v1/geoStringOperations/member2")
	public OrangeRedisExampleResponse testCase2(@Valid @RequestBody OrangeGeoStringExampleEntity entity) {
		try {
			return new OrangeRedisExampleResponse(geoExample2Api.add(entity));
		}catch (Exception e) {
			e.printStackTrace();
			return new OrangeRedisExampleResponse(2,e.getMessage());
		}
	}
	
	@PutMapping("/v1/geoStringOperations/members")
	public OrangeRedisExampleResponse testCase3(@Valid @RequestBody OrangeGeoStringMultipleExampleEntity entity) {
		try {
			return new OrangeRedisExampleResponse(geoExample2Api.add(entity.getEntities()));
		}catch (Exception e) {
			e.printStackTrace();
			return new OrangeRedisExampleResponse(3,e.getMessage());
		}
	}
	
	@PutMapping("/v1/geoStringOperations/distance1")
	public OrangeRedisExampleResponse testCase4(@Valid @RequestBody OrangeGeoStringMultipleLocationExampleEntity entity) {
		try {
			return new OrangeRedisExampleResponse(geoExample2Api.distance(entity.getLocations()));
		}catch (Exception e) {
			e.printStackTrace();
			return new OrangeRedisExampleResponse(4,e.getMessage());
		}
	}
	
	@PutMapping("/v1/geoStringOperations/distance2")
	public OrangeRedisExampleResponse testCase5(@Valid @RequestBody OrangeGeoStringMultipleExampleEntity entity) {
		try {
			return new OrangeRedisExampleResponse(geoExample2Api.distance2(entity.getEntities()));
		}catch (Exception e) {
			e.printStackTrace();
			return new OrangeRedisExampleResponse(5,e.getMessage());
		}
	}
	
	@GetMapping("/v1/geoStringOperations/member")
	public OrangeRedisExampleResponse testCase6(String location) {
		try {
			return new OrangeRedisExampleResponse(geoExample2Api.getMember(location));
		}catch (Exception e) {
			e.printStackTrace();
			return new OrangeRedisExampleResponse(6,e.getMessage());
		}
	}
	
	@PutMapping("/v1/geoStringOperations/membersQueries1")
	public OrangeRedisExampleResponse testCase7(@Valid @RequestBody OrangeGeoStringMultipleLocationExampleEntity entity) {
		try {
			return new OrangeRedisExampleResponse(geoExample2Api.getMembers(entity.getLocations()));
		}catch (Exception e) {
			e.printStackTrace();
			return new OrangeRedisExampleResponse(7,e.getMessage());
		}
	}
	
	@PutMapping("/v1/geoStringOperations/membersQueries2")
	public OrangeRedisExampleResponse testCase8(@Valid @RequestBody OrangeGeoStringMultipleExampleEntity entity) {
		try {
			return new OrangeRedisExampleResponse(geoExample2Api.getMembers2(entity.getEntities()));
		}catch (Exception e) {
			e.printStackTrace();
			return new OrangeRedisExampleResponse(8,e.getMessage());
		}
	}
	
	@PutMapping("/v1/geoStringOperations/membersInRadius1")
	public OrangeRedisExampleResponse testCase9(@Valid @RequestBody OrangeGeoStringLocationDistanceExampleEntity entity) {
		try {
			return new OrangeRedisExampleResponse(geoExample2Api.getMembersInRadius1(entity.getLocation(),entity.getDistance()));
		}catch (Exception e) {
			e.printStackTrace();
			return new OrangeRedisExampleResponse(9,e.getMessage());
		}
	}
	
	@DeleteMapping("/v1/geoStringOperations/member")
	public OrangeRedisExampleResponse testCase10(String location) {
		try {
			return new OrangeRedisExampleResponse(geoExample2Api.remove(location));
		}catch (Exception e) {
			e.printStackTrace();
			return new OrangeRedisExampleResponse(10,e.getMessage());
		}
	}
	
	@PutMapping("/v1/geoStringOperations/removedMembers")
	public OrangeRedisExampleResponse testCase11(@Valid @RequestBody OrangeGeoStringMultipleLocationExampleEntity entity) {
		try {
			return new OrangeRedisExampleResponse(geoExample2Api.remove(entity.getLocations()));
		}catch (Exception e) {
			e.printStackTrace();
			return new OrangeRedisExampleResponse(11,e.getMessage());
		}
	}
	
	@PutMapping("/v1/geoStringOperations/membersInRadius2")
	public OrangeRedisExampleResponse testCase12(@Valid @RequestBody OrangeGeoStringLocationDistanceExampleEntity entity) {
		try {
			return new OrangeRedisExampleResponse(geoExample2Api.getMembersInRadius(entity.getLocation(),entity.getDistance()));
		}catch (Exception e) {
			e.printStackTrace();
			return new OrangeRedisExampleResponse(12,e.getMessage());
		}
	}
	
	@PutMapping("/v1/geoStringOperations/membersInRadius3")
	public OrangeRedisExampleResponse testCase13(@Valid @RequestBody OrangeGeoStringLocationDistanceExampleEntity entity, @RequestParam Long count) {
		try {
			return new OrangeRedisExampleResponse(geoExample2Api.getMembersInRadius3(entity.getLocation(),entity.getDistance(),count));
		}catch (Exception e) {
			e.printStackTrace();
			return new OrangeRedisExampleResponse(13,e.getMessage());
		}
	}
	
	@PutMapping("/v1/geoStringOperations/membersInRadius4")
	public OrangeRedisExampleResponse testCase14(@Valid @RequestBody OrangeGeoStringLocationDistanceExampleEntity entity, @RequestParam Long count) {
		try {
			return new OrangeRedisExampleResponse(geoExample2Api.getMembersInRadius(entity.getLocation(),entity.getDistance(),count));
		}catch (Exception e) {
			e.printStackTrace();
			return new OrangeRedisExampleResponse(14,e.getMessage());
		}
	}
	
	@GetMapping("/v1/geoStringOperations/membersInRadius5")
	public OrangeRedisExampleResponse testCase15(@RequestParam Double longitude, @RequestParam Double latitude, @RequestParam Double distance) {
		try {
			return new OrangeRedisExampleResponse(geoExample2Api.getMembersInRadius(longitude,latitude,distance));
		}catch (Exception e) {
			e.printStackTrace();
			return new OrangeRedisExampleResponse(15,e.getMessage());
		}
	}
	
	@GetMapping("/v1/geoStringOperations/membersInRadius6")
	public OrangeRedisExampleResponse testCase16(
		@RequestParam Double longitude, 
		@RequestParam Double latitude, 
		@RequestParam Double distance,
		@RequestParam Long count
	) {
		try {
			return new OrangeRedisExampleResponse(geoExample2Api.getMembersInRadius(longitude,latitude,distance,count));
		}catch (Exception e) {
			e.printStackTrace();
			return new OrangeRedisExampleResponse(16,e.getMessage());
		}
	}
	
	@GetMapping("/v1/geoStringOperations/membersInBox1")
	public OrangeRedisExampleResponse testCase17(
		@RequestParam Double longitude, 
		@RequestParam Double latitude, 
		@RequestParam Double width, 
		@RequestParam Double height
	) {
		try {
			return new OrangeRedisExampleResponse(geoExample2Api.getMembersInBox(longitude,latitude,width,height));
		}catch (Exception e) {
			e.printStackTrace();
			return new OrangeRedisExampleResponse(17,e.getMessage());
		}
	}
	
	@GetMapping("/v1/geoStringOperations/membersInBox2")
	public OrangeRedisExampleResponse testCase18(
		@RequestParam Double longitude, 
		@RequestParam Double latitude, 
		@RequestParam Double width, 
		@RequestParam Double height, 
		@RequestParam Long count
	) {
		try {
			return new OrangeRedisExampleResponse(geoExample2Api.getMembersInBox(longitude,latitude,width,height,count));
		}catch (Exception e) {
			e.printStackTrace();
			return new OrangeRedisExampleResponse(18,e.getMessage());
		}
	}
	
	@GetMapping("/v1/geoStringOperations/membersInBox3")
	public OrangeRedisExampleResponse testCase19(
		@RequestParam String location,  
		@RequestParam Double width, 
		@RequestParam Double height
	) {
		try {
			return new OrangeRedisExampleResponse(geoExample2Api.getMembersInBox(location,width,height));
		}catch (Exception e) {
			e.printStackTrace();
			return new OrangeRedisExampleResponse(19,e.getMessage());
		}
	}
	
	@GetMapping("/v1/geoStringOperations/membersInBox4")
	public OrangeRedisExampleResponse testCase20(
		@RequestParam String location, 
		@RequestParam Double width, 
		@RequestParam Double height, 
		@RequestParam Long count
	) {
		try {
			return new OrangeRedisExampleResponse(geoExample2Api.getMembersInBox(location,width,height,count));
		}catch (Exception e) {
			e.printStackTrace();
			return new OrangeRedisExampleResponse(20,e.getMessage());
		}
	}
}
