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

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.langwuyue.orange.example.redis.api.cross.OrangeRedisCrossExample1Api;
import com.langwuyue.orange.example.redis.api.zset.OrangeRedisZSetExample5Api;
import com.langwuyue.orange.example.redis.api.zset.OrangeRedisZSetExample6Api;
import com.langwuyue.orange.example.redis.api.zset.OrangeRedisZSetExample7Api;
import com.langwuyue.orange.example.redis.api.zset.OrangeRedisZSetExample8Api;
import com.langwuyue.orange.example.redis.converters.OrangeMapToBooleanExampleEntitiesConverter;
import com.langwuyue.orange.example.redis.converters.OrangeMapToExampleEntitiesConverter;
import com.langwuyue.orange.example.redis.entity.OrangeZSetExampleEntity;
import com.langwuyue.orange.example.redis.entity.OrangeZSetExampleEntity.ZSetValue;
import com.langwuyue.orange.example.redis.entity.OrangeZSetMultipleExampleEntity;
import com.langwuyue.orange.example.redis.response.OrangeRedisExampleResponse;

/**
 * @author Liang.Zhong
 * @since 1.0.0
 */
@RestController("crossOrangeRedisZSetCrossKeyTestCases")
public class OrangeRedisZSetCrossKeyTestCases {
	
	@Autowired
	private OrangeRedisCrossExample1Api crossExample1Api;
	
	@Autowired
	private OrangeRedisZSetExample5Api zsetExample5Api;
	
	@Autowired
	private OrangeRedisZSetExample6Api zsetExample6Api;

	@Autowired
	private OrangeRedisZSetExample7Api zsetExample7Api;
	
	@Autowired
	private OrangeRedisZSetExample8Api zsetExample8Api;
	
	@PutMapping("/v1/zsetCrossOperations/members1")
	public OrangeRedisExampleResponse testCase1(@Valid @RequestBody OrangeZSetMultipleExampleEntity multipleEntity) {
		try {
			Map<ZSetValue,Double>  members = new HashMap<>();
			Set<OrangeZSetExampleEntity> entities = multipleEntity.getEntities();
			for(OrangeZSetExampleEntity entity : entities) {
				members.put(entity.getValue(), entity.getScore());
			}
			return new OrangeRedisExampleResponse(new OrangeMapToBooleanExampleEntitiesConverter().toEntities(zsetExample5Api.add(members)));
		}catch (Exception e) {
			e.printStackTrace();
			return new OrangeRedisExampleResponse(1,e.getMessage());
		}
	}
	
	@PutMapping("/v1/zsetCrossOperations/members2")
	public OrangeRedisExampleResponse testCase2(@Valid @RequestBody OrangeZSetMultipleExampleEntity multipleEntity) {
		try {
			Map<ZSetValue,Double>  members = new HashMap<>();
			Set<OrangeZSetExampleEntity> entities = multipleEntity.getEntities();
			for(OrangeZSetExampleEntity entity : entities) {
				members.put(entity.getValue(), entity.getScore());
			}
			return new OrangeRedisExampleResponse(new OrangeMapToBooleanExampleEntitiesConverter().toEntities(zsetExample6Api.add(members)));
		}catch (Exception e) {
			e.printStackTrace();
			return new OrangeRedisExampleResponse(2,e.getMessage());
		}
	}
	
	@GetMapping("/v1/zsetCrossOperations/rankRangeMembers1")
	public OrangeRedisExampleResponse testCase3(@RequestParam Long startIndex,@RequestParam Long endIndex) {
		try {
			return new OrangeRedisExampleResponse(new OrangeMapToExampleEntitiesConverter().toEntities(zsetExample5Api.getByRankRangeWithScores(startIndex,endIndex)));
		}catch (Exception e) {
			e.printStackTrace();
			return new OrangeRedisExampleResponse(3,e.getMessage());
		}
	}
	
	@GetMapping("/v1/zsetCrossOperations/rankRangeMembers2")
	public OrangeRedisExampleResponse testCase4(@RequestParam Long startIndex,@RequestParam Long endIndex) {
		try {
			return new OrangeRedisExampleResponse(new OrangeMapToExampleEntitiesConverter().toEntities(zsetExample6Api.getByRankRangeWithScores(startIndex,endIndex)));
		}catch (Exception e) {
			e.printStackTrace();
			return new OrangeRedisExampleResponse(4,e.getMessage());
		}
	}
	
	@GetMapping("/v1/zsetCrossOperations/size1")
	public OrangeRedisExampleResponse testCase5() {
		try {
			return new OrangeRedisExampleResponse(zsetExample5Api.getSize());
		}catch (Exception e) {
			e.printStackTrace();
			return new OrangeRedisExampleResponse(5,e.getMessage());
		}
		
	}
	
	@GetMapping("/v1/zsetCrossOperations/size2")
	public OrangeRedisExampleResponse testCase6() {
		try {
			return new OrangeRedisExampleResponse(zsetExample6Api.getSize());
		}catch (Exception e) {
			e.printStackTrace();
			return new OrangeRedisExampleResponse(6,e.getMessage());
		}
		
	}
	
	@GetMapping("/v1/zsetCrossOperations/differenceValues")
	public OrangeRedisExampleResponse testCase7() {
		try {
			return new OrangeRedisExampleResponse(crossExample1Api.difference());
		}catch (Exception e) {
			e.printStackTrace();
			return new OrangeRedisExampleResponse(7,e.getMessage());
		}
	}
	
	@GetMapping("/v1/zsetCrossOperations/differenceMembers")
	public OrangeRedisExampleResponse testCase8() {
		try {
			return new OrangeRedisExampleResponse(crossExample1Api.differenceWithScores());
		}catch (Exception e) {
			e.printStackTrace();
			return new OrangeRedisExampleResponse(8,e.getMessage());
		}
	}
	
	@PostMapping("/v1/zsetCrossOperations/differenceMembers")
	public OrangeRedisExampleResponse testCase9() {
		try {
			return new OrangeRedisExampleResponse(crossExample1Api.differenceAndStore());
		}catch (Exception e) {
			e.printStackTrace();
			return new OrangeRedisExampleResponse(9,e.getMessage());
		}
	}
	

	@GetMapping("/v1/zsetCrossOperations/unionValues")
	public OrangeRedisExampleResponse testCase10() {
		try {
			return new OrangeRedisExampleResponse(crossExample1Api.union());
		}catch (Exception e) {
			e.printStackTrace();
			return new OrangeRedisExampleResponse(10,e.getMessage());
		}
	}
	
	@GetMapping("/v1/zsetCrossOperations/unionMembers")
	public OrangeRedisExampleResponse testCase11() {
		try {
			return new OrangeRedisExampleResponse(crossExample1Api.unionWithScores());
		}catch (Exception e) {
			e.printStackTrace();
			return new OrangeRedisExampleResponse(11,e.getMessage());
		}
	}
	
	@GetMapping("/v1/zsetCrossOperations/unionAndAggregateMembers")
	public OrangeRedisExampleResponse testCase12() {
		try {
			return new OrangeRedisExampleResponse(crossExample1Api.unionWithScoresAndAggregate());
		}catch (Exception e) {
			e.printStackTrace();
			return new OrangeRedisExampleResponse(12,e.getMessage());
		}
	}
	
	@PostMapping("/v1/zsetCrossOperations/unionMembers")
	public OrangeRedisExampleResponse testCase13() {
		try {
			return new OrangeRedisExampleResponse(crossExample1Api.unionAndStore());
		}catch (Exception e) {
			e.printStackTrace();
			return new OrangeRedisExampleResponse(13,e.getMessage());
		}
	}
	
	@PostMapping("/v1/zsetCrossOperations/unionAndAggregateMembers")
	public OrangeRedisExampleResponse testCase14() {
		try {
			return new OrangeRedisExampleResponse(crossExample1Api.unionAndAggregateAndStore());
		}catch (Exception e) {
			e.printStackTrace();
			return new OrangeRedisExampleResponse(14,e.getMessage());
		}
	}
	
	@GetMapping("/v1/zsetCrossOperations/size3")
	public OrangeRedisExampleResponse testCase15() {
		try {
			return new OrangeRedisExampleResponse(zsetExample7Api.getSize());
		}catch (Exception e) {
			e.printStackTrace();
			return new OrangeRedisExampleResponse(15,e.getMessage());
		}
		
	}
	
	@GetMapping("/v1/zsetCrossOperations/rankRangeMembers3")
	public OrangeRedisExampleResponse testCase16(@RequestParam Long startIndex,@RequestParam Long endIndex) {
		try {
			return new OrangeRedisExampleResponse(new OrangeMapToExampleEntitiesConverter().toEntities(zsetExample7Api.getByRankRangeWithScores(startIndex,endIndex)));
		}catch (Exception e) {
			e.printStackTrace();
			return new OrangeRedisExampleResponse(16,e.getMessage());
		}
	}
	
	@GetMapping("/v1/zsetCrossOperations/intersectValues")
	public OrangeRedisExampleResponse testCase17() {
		try {
			return new OrangeRedisExampleResponse(crossExample1Api.intersect());
		}catch (Exception e) {
			e.printStackTrace();
			return new OrangeRedisExampleResponse(17,e.getMessage());
		}
	}
	
	@GetMapping("/v1/zsetCrossOperations/intersectMembers")
	public OrangeRedisExampleResponse testCase18() {
		try {
			return new OrangeRedisExampleResponse(crossExample1Api.intersectWithScores());
		}catch (Exception e) {
			e.printStackTrace();
			return new OrangeRedisExampleResponse(18,e.getMessage());
		}
	}
	
	@GetMapping("/v1/zsetCrossOperations/intersectAndAggregateMembers")
	public OrangeRedisExampleResponse testCase19() {
		try {
			return new OrangeRedisExampleResponse(crossExample1Api.intersectWithScoresAndAggregate());
		}catch (Exception e) {
			e.printStackTrace();
			return new OrangeRedisExampleResponse(19,e.getMessage());
		}
	}
	
	@PostMapping("/v1/zsetCrossOperations/intersectMembers")
	public OrangeRedisExampleResponse testCase20() {
		try {
			return new OrangeRedisExampleResponse(crossExample1Api.intersectAndStore());
		}catch (Exception e) {
			e.printStackTrace();
			return new OrangeRedisExampleResponse(20,e.getMessage());
		}
	}
	
	@PostMapping("/v1/zsetCrossOperations/intersectAndAggregateMembers")
	public OrangeRedisExampleResponse testCase21() {
		try {
			return new OrangeRedisExampleResponse(crossExample1Api.intersectAndAggregateAndStore());
		}catch (Exception e) {
			e.printStackTrace();
			return new OrangeRedisExampleResponse(21,e.getMessage());
		}
	}
	
	@GetMapping("/v1/zsetCrossOperations/size4")
	public OrangeRedisExampleResponse testCase22() {
		try {
			return new OrangeRedisExampleResponse(zsetExample8Api.getSize());
		}catch (Exception e) {
			e.printStackTrace();
			return new OrangeRedisExampleResponse(22,e.getMessage());
		}
		
	}
	
	@GetMapping("/v1/zsetCrossOperations/rankRangeMembers4")
	public OrangeRedisExampleResponse testCase23(@RequestParam Long startIndex,@RequestParam Long endIndex) {
		try {
			return new OrangeRedisExampleResponse(new OrangeMapToExampleEntitiesConverter().toEntities(zsetExample8Api.getByRankRangeWithScores(startIndex,endIndex)));
		}catch (Exception e) {
			e.printStackTrace();
			return new OrangeRedisExampleResponse(23,e.getMessage());
		}
	}
	
	@DeleteMapping("/v1/zsetCrossOperations/allMembers")
	public OrangeRedisExampleResponse testCase24() {
		try {
			zsetExample5Api.delete();
			zsetExample6Api.delete();
			zsetExample7Api.delete();
			zsetExample8Api.delete();
			return new OrangeRedisExampleResponse();
		}catch (Exception e) {
			e.printStackTrace();
			return new OrangeRedisExampleResponse(24,e.getMessage());
		}
	}
	
	@PutMapping("/v1/zsetCrossOperations/intersectWithScoresByDynamicWeight")
	public OrangeRedisExampleResponse testCase25(@RequestBody List<Double> weights) {
		try {
			return new OrangeRedisExampleResponse(crossExample1Api.intersectWithScoresByDynamicWeights(weights));
		}catch (Exception e) {
			e.printStackTrace();
			return new OrangeRedisExampleResponse(25,e.getMessage());
		}
	}
	
	@PostMapping("/v1/zsetCrossOperations/intersectByDynamicWeight")
	public OrangeRedisExampleResponse testCase26(@RequestBody List<Double> weights) {
		try {
			return new OrangeRedisExampleResponse(crossExample1Api.intersectAndStoreByDynamicWeights(weights));
		}catch (Exception e) {
			e.printStackTrace();
			return new OrangeRedisExampleResponse(26,e.getMessage());
		}
	}
	
	@PutMapping("/v1/zsetCrossOperations/unionWithScoresByDynamicWeight")
	public OrangeRedisExampleResponse testCase27(@RequestBody List<Double> weights) {
		try {
			return new OrangeRedisExampleResponse(crossExample1Api.unionWithScoresByDynamicWeights(weights));
		}catch (Exception e) {
			e.printStackTrace();
			return new OrangeRedisExampleResponse(27,e.getMessage());
		}
	}
	
	@PostMapping("/v1/zsetCrossOperations/unionByDynamicWeight")
	public OrangeRedisExampleResponse testCase28(@RequestBody List<Double> weights) {
		try {
			return new OrangeRedisExampleResponse(crossExample1Api.unionAndStoreByDynamicWeights(weights));
		}catch (Exception e) {
			e.printStackTrace();
			return new OrangeRedisExampleResponse(28,e.getMessage());
		}
	}
	
}
