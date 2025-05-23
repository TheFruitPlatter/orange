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
package com.langwuyue.orange.example.redis.testcase.zset;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.langwuyue.orange.example.redis.api.zset.OrangeRedisZSetExample1Api;
import com.langwuyue.orange.example.redis.converters.OrangeMapToBooleanExampleEntitiesConverter;
import com.langwuyue.orange.example.redis.converters.OrangeMapToExampleEntitiesConverter;
import com.langwuyue.orange.example.redis.converters.OrangeMapToRankExampleEntitiesConverter;
import com.langwuyue.orange.example.redis.entity.OrangeZSetCASExampleEntity;
import com.langwuyue.orange.example.redis.entity.OrangeZSetExampleEntity;
import com.langwuyue.orange.example.redis.entity.OrangeZSetExampleEntity.ZSetValue;
import com.langwuyue.orange.example.redis.entity.OrangeZSetMultipleExampleEntity;
import com.langwuyue.orange.example.redis.entity.OrangeZSetMultipleZSetValues;
import com.langwuyue.orange.example.redis.entity.OrangeZSetScoreNullableExampleEntity;
import com.langwuyue.orange.example.redis.response.OrangeRedisExampleResponse;

/**
 * @author Liang.Zhong
 * @since 1.0.0
 */
@RestController("zsetOrangeRedisJSONSimpleTestCases")
public class OrangeRedisJSONSimpleTestCases {
	
	@Autowired
	private OrangeRedisZSetExample1Api zsetExample1Api;
	
	@PutMapping("/v1/zsetOperations/member")
	public OrangeRedisExampleResponse testCase1(@Valid @RequestBody OrangeZSetExampleEntity entity) {
		try {
			return new OrangeRedisExampleResponse(zsetExample1Api.add(entity.getValue(),entity.getScore()));
		}catch (Exception e) {
			e.printStackTrace();
			return new OrangeRedisExampleResponse(1,e.getMessage());
		}
	}
	
	@GetMapping("/v1/zsetOperations/score")
	public OrangeRedisExampleResponse testCase2(@RequestParam Long id,@RequestParam String name) {
		try {
			ZSetValue value = new ZSetValue();
			value.setId(id);
			value.setName(name);
			return new OrangeRedisExampleResponse(zsetExample1Api.getScore(value));
		}catch (Exception e) {
			e.printStackTrace();
			return new OrangeRedisExampleResponse(2,e.getMessage());
		}
	}
	
	@GetMapping("/v1/zsetOperations/rank")
	public OrangeRedisExampleResponse testCase3(@RequestParam Long id,@RequestParam String name) {
		try {
			ZSetValue value = new ZSetValue();
			value.setId(id);
			value.setName(name);
			return new OrangeRedisExampleResponse(zsetExample1Api.getRank(value));
		}catch (Exception e) {
			e.printStackTrace();
			return new OrangeRedisExampleResponse(3,e.getMessage());
		}
		
	}
	
	@GetMapping("/v1/zsetOperations/revesedRank")
	public OrangeRedisExampleResponse testCase4(@RequestParam Long id,@RequestParam String name) {
		try {
			ZSetValue value = new ZSetValue();
			value.setId(id);
			value.setName(name);
			return new OrangeRedisExampleResponse(zsetExample1Api.reveseRank(value));
		}catch (Exception e) {
			e.printStackTrace();
			return new OrangeRedisExampleResponse(4,e.getMessage());
		}
		
	}
	
	@GetMapping("/v1/zsetOperations/size")
	public OrangeRedisExampleResponse testCase5() {
		try {
			return new OrangeRedisExampleResponse(zsetExample1Api.getSize());
		}catch (Exception e) {
			e.printStackTrace();
			return new OrangeRedisExampleResponse(5,e.getMessage());
		}
		
	}
	
	@DeleteMapping("/v1/zsetOperations/zsets")
	public OrangeRedisExampleResponse testCase6() {
		try {
			boolean success = zsetExample1Api.delete();
			if(success) {
				return new OrangeRedisExampleResponse();
			}
			return new OrangeRedisExampleResponse(6,"False returned");
		}catch (Exception e) {
			e.printStackTrace();
			return new OrangeRedisExampleResponse(7,e.getMessage());
		}
	}
	
	@DeleteMapping("/v1/zsetOperations/members")
	public OrangeRedisExampleResponse testCase7(@RequestParam Long id,@RequestParam String name) {
		try {
			ZSetValue value = new ZSetValue();
			value.setId(id);
			value.setName(name);
			boolean success = zsetExample1Api.remove(value);
			if(success) {
				return new OrangeRedisExampleResponse();
			}
			return new OrangeRedisExampleResponse(8,"False returned");
		}catch (Exception e) {
			e.printStackTrace();
			return new OrangeRedisExampleResponse(9,e.getMessage());
		}
		
	}
	
	@PutMapping("/v1/zsetOperations/removedMembers")
	public OrangeRedisExampleResponse testCase8(@Valid @RequestBody OrangeZSetMultipleZSetValues values) {
		try {
			return new OrangeRedisExampleResponse(new OrangeMapToBooleanExampleEntitiesConverter().toEntities(zsetExample1Api.remove(values.getValues())));
		}catch (Exception e) {
			e.printStackTrace();
			return new OrangeRedisExampleResponse(9,e.getMessage());
		}
	}
	
	@PutMapping("/v1/zsetOperations/members")
	public OrangeRedisExampleResponse testCase9(@Valid @RequestBody OrangeZSetMultipleExampleEntity multipleEntity) {
		try {
			Map<ZSetValue,Double>  members = new HashMap<>();
			Set<OrangeZSetExampleEntity> entities = multipleEntity.getEntities();
			for(OrangeZSetExampleEntity entity : entities) {
				members.put(entity.getValue(), entity.getScore());
			}
			return new OrangeRedisExampleResponse(new OrangeMapToBooleanExampleEntitiesConverter().toEntities(zsetExample1Api.add(members)));
		}catch (Exception e) {
			e.printStackTrace();
			return new OrangeRedisExampleResponse(10,e.getMessage());
		}
	}
	
	@PutMapping("/v1/zsetOperations/initMember")
	public OrangeRedisExampleResponse testCase10(@Valid @RequestBody OrangeZSetExampleEntity entity) {
		try {
			zsetExample1Api.addIfAsent(entity.getValue(),entity.getScore());
			return new OrangeRedisExampleResponse();
		}catch (Exception e) {
			e.printStackTrace();
			return new OrangeRedisExampleResponse(11,e.getMessage());
		}
	}
	
	@PutMapping("/v1/zsetOperations/initMembers")
	public OrangeRedisExampleResponse testCase11(@Valid @RequestBody OrangeZSetMultipleExampleEntity multipleEntity) {
		try {
			Map<ZSetValue,Double>  members = new HashMap<>();
			Set<OrangeZSetExampleEntity> entities = multipleEntity.getEntities();
			for(OrangeZSetExampleEntity entity : entities) {
				members.put(entity.getValue(), entity.getScore());
			}
			zsetExample1Api.addIfAsent(members);
			return new OrangeRedisExampleResponse();
		}catch (Exception e) {
			e.printStackTrace();
			return new OrangeRedisExampleResponse(12,e.getMessage());
		}
	}
	
	@PutMapping("/v1/zsetOperations/concurrentScores")
	public OrangeRedisExampleResponse testCase12(@Valid @RequestBody OrangeZSetCASExampleEntity casEntity) {
		try {
			return new OrangeRedisExampleResponse(zsetExample1Api.compareAndSwap(casEntity.getValue(), casEntity.getOldScore(), casEntity.getNewScore()));
		}catch (Exception e) {
			e.printStackTrace();
			return new OrangeRedisExampleResponse(14,e.getMessage());
		}
	}
	
	@GetMapping("/v1/zsetOperations/randomedValue")
	public OrangeRedisExampleResponse testCase13() {
		try {
			return new OrangeRedisExampleResponse(zsetExample1Api.randomGetValue());
		}catch (Exception e) {
			e.printStackTrace();
			return new OrangeRedisExampleResponse(15,e.getMessage());
		}
	}
	
	@GetMapping("/v1/zsetOperations/randomedMember")
	public OrangeRedisExampleResponse testCase14() {
		try {
			return new OrangeRedisExampleResponse(new OrangeMapToExampleEntitiesConverter().toEntities(zsetExample1Api.randomGetMember()));
		}catch (Exception e) {
			e.printStackTrace();
			return new OrangeRedisExampleResponse(16,e.getMessage());
		}
		
	}
	
	@GetMapping("/v1/zsetOperations/randomedValues")
	public OrangeRedisExampleResponse testCase15(@RequestParam Integer count) {
		try {
			return new OrangeRedisExampleResponse(zsetExample1Api.randomGetValues(count));
		}catch (Exception e) {
			e.printStackTrace();
			return new OrangeRedisExampleResponse(15,e.getMessage());
		}
	}
	
	@GetMapping("/v1/zsetOperations/randomedMembers")
	public OrangeRedisExampleResponse testCase16(@RequestParam Integer count) {
		try {
			return new OrangeRedisExampleResponse(new OrangeMapToExampleEntitiesConverter().toEntities(zsetExample1Api.randomGetMembers(count)));
		}catch (Exception e) {
			e.printStackTrace();
			return new OrangeRedisExampleResponse(16,e.getMessage());
		}
		
	}
	
	@GetMapping("/v1/zsetOperations/randomedAndDistinctedValues")
	public OrangeRedisExampleResponse testCase17(@RequestParam Integer count) {
		try {
			return new OrangeRedisExampleResponse(zsetExample1Api.distinctRandomGetValues(count));
		}catch (Exception e) {
			e.printStackTrace();
			return new OrangeRedisExampleResponse(17,e.getMessage());
		}
	}
	
	@GetMapping("/v1/zsetOperations/randomedAndDistinctedMembers")
	public OrangeRedisExampleResponse testCase18(@RequestParam Integer count) {
		try {
			return new OrangeRedisExampleResponse(new OrangeMapToExampleEntitiesConverter().toEntities(zsetExample1Api.distinctRandomGetMembers(count)));
		}catch (Exception e) {
			e.printStackTrace();
			return new OrangeRedisExampleResponse(18,e.getMessage());
		}
	}
	
	@GetMapping("/v1/zsetOperations/scoreRangeValues")
	public OrangeRedisExampleResponse testCase19(@RequestParam Double maxScore,@RequestParam Double minScore) {
		try {
			return new OrangeRedisExampleResponse(zsetExample1Api.getByScoreRange(maxScore,minScore));
		}catch (Exception e) {
			e.printStackTrace();
			return new OrangeRedisExampleResponse(19,e.getMessage());
		}
	}
	
	@GetMapping("/v1/zsetOperations/scoreRangeValuesByPage")
	public OrangeRedisExampleResponse testCase20(@RequestParam Double maxScore,@RequestParam Double minScore,@RequestParam Long pageNo,@RequestParam Long pageSize) {
		try {
			return new OrangeRedisExampleResponse(zsetExample1Api.getByScoreRange(maxScore,minScore,pageNo,pageSize));
		}catch (Exception e) {
			e.printStackTrace();
			return new OrangeRedisExampleResponse(20,e.getMessage());
		}
	}
	
	@GetMapping("/v1/zsetOperations/rankRangeValues")
	public OrangeRedisExampleResponse testCase21(@RequestParam Long startIndex,@RequestParam Long endIndex) {
		try {
			return new OrangeRedisExampleResponse(zsetExample1Api.getByRankRange(startIndex,endIndex));
		}catch (Exception e) {
			e.printStackTrace();
			return new OrangeRedisExampleResponse(21,e.getMessage());
		}
	}
	
	@GetMapping("/v1/zsetOperations/scoreRangeMembers")
	public OrangeRedisExampleResponse testCase22(@RequestParam Double maxScore,@RequestParam Double minScore) {
		try {
			return new OrangeRedisExampleResponse(new OrangeMapToExampleEntitiesConverter().toEntities(zsetExample1Api.getByScoreRangeWithScores(maxScore,minScore)));
		}catch (Exception e) {
			e.printStackTrace();
			return new OrangeRedisExampleResponse(22,e.getMessage());
		}
	}
	
	@GetMapping("/v1/zsetOperations/scoreRangeMembersByPage")
	public OrangeRedisExampleResponse testCase23(@RequestParam Double maxScore,@RequestParam Double minScore,@RequestParam Long pageNo,@RequestParam Long pageSize) {
		try {
			return new OrangeRedisExampleResponse(new OrangeMapToExampleEntitiesConverter().toEntities(zsetExample1Api.getByScoreRangeWithScores(maxScore,minScore,pageNo,pageSize)));
		}catch (Exception e) {
			e.printStackTrace();
			return new OrangeRedisExampleResponse(23,e.getMessage());
		}
	}
	
	@GetMapping("/v1/zsetOperations/rankRangeMembers")
	public OrangeRedisExampleResponse testCase24(@RequestParam Long startIndex,@RequestParam Long endIndex) {
		try {
			return new OrangeRedisExampleResponse(new OrangeMapToExampleEntitiesConverter().toEntities(zsetExample1Api.getByRankRangeWithScores(startIndex,endIndex)));
		}catch (Exception e) {
			e.printStackTrace();
			return new OrangeRedisExampleResponse(24,e.getMessage());
		}
	}
	
	@GetMapping("/v1/zsetOperations/revesedScoreRangeValues")
	public OrangeRedisExampleResponse testCase25(@RequestParam Double maxScore,@RequestParam Double minScore) {
		try {
			return new OrangeRedisExampleResponse(zsetExample1Api.reveseScoreRange(maxScore,minScore));
		}catch (Exception e) {
			e.printStackTrace();
			return new OrangeRedisExampleResponse(25,e.getMessage());
		}
	}
	
	@GetMapping("/v1/zsetOperations/revesedScoreRangeValuesByPage")
	public OrangeRedisExampleResponse testCase26(@RequestParam Double maxScore,@RequestParam Double minScore,@RequestParam Long pageNo,@RequestParam Long pageSize) {
		try {
			return new OrangeRedisExampleResponse(zsetExample1Api.reveseScoreRange(maxScore,minScore,pageNo,pageSize));
		}catch (Exception e) {
			e.printStackTrace();
			return new OrangeRedisExampleResponse(26,e.getMessage());
		}
	}
	
	@GetMapping("/v1/zsetOperations/revesedRankRangeValues")
	public OrangeRedisExampleResponse testCase27(@RequestParam Long startIndex,@RequestParam Long endIndex) {
		try {
			return new OrangeRedisExampleResponse(zsetExample1Api.reveseRankRange(startIndex,endIndex));
		}catch (Exception e) {
			e.printStackTrace();
			return new OrangeRedisExampleResponse(27,e.getMessage());
		}
	}
	
	@GetMapping("/v1/zsetOperations/revesedScoreRangeMembers")
	public OrangeRedisExampleResponse testCase28(@RequestParam Double maxScore,@RequestParam Double minScore) {
		try {
			return new OrangeRedisExampleResponse(new OrangeMapToExampleEntitiesConverter().toEntities(zsetExample1Api.reveseScoreRangeWithScores(maxScore,minScore)));
		}catch (Exception e) {
			e.printStackTrace();
			return new OrangeRedisExampleResponse(28,e.getMessage());
		}
	}
	
	@GetMapping("/v1/zsetOperations/revesedScoreRangeMembersByPage")
	public OrangeRedisExampleResponse testCase29(@RequestParam Double maxScore,@RequestParam Double minScore,@RequestParam Long pageNo,@RequestParam Long pageSize) {
		try {
			return new OrangeRedisExampleResponse(new OrangeMapToExampleEntitiesConverter().toEntities(zsetExample1Api.reveseScoreRangeWithScores(maxScore,minScore,pageNo,pageSize)));
		}catch (Exception e) {
			e.printStackTrace();
			return new OrangeRedisExampleResponse(29,e.getMessage());
		}
	}
	
	@GetMapping("/v1/zsetOperations/revesedRankRangeMembers")
	public OrangeRedisExampleResponse testCase30(@RequestParam Long startIndex,@RequestParam Long endIndex) {
		try {
			return new OrangeRedisExampleResponse(new OrangeMapToExampleEntitiesConverter().toEntities(zsetExample1Api.reveseRankRangeWithScores(startIndex,endIndex)));
		}catch (Exception e) {
			e.printStackTrace();
			return new OrangeRedisExampleResponse(30,e.getMessage());
		}
	}
	
	@PutMapping("/v1/zsetOperations/scores")
	public OrangeRedisExampleResponse testCase31(@Valid @RequestBody OrangeZSetMultipleZSetValues values) {
		try {
			return new OrangeRedisExampleResponse(new OrangeMapToExampleEntitiesConverter().toEntities(zsetExample1Api.getScores(values.getValues())));
		}catch (Exception e) {
			e.printStackTrace();
			return new OrangeRedisExampleResponse(31,e.getMessage());
		}
	}
	
	@PutMapping("/v1/zsetOperations/ranks")
	public OrangeRedisExampleResponse testCase32(@Valid @RequestBody OrangeZSetMultipleZSetValues values) {
		try {
			return new OrangeRedisExampleResponse(new OrangeMapToRankExampleEntitiesConverter().toEntities(zsetExample1Api.getRanks(values.getValues())));
		}catch (Exception e) {
			e.printStackTrace();
			return new OrangeRedisExampleResponse(32,e.getMessage());
		}
		
	}
	
	@PutMapping("/v1/zsetOperations/revesedRanks")
	public OrangeRedisExampleResponse testCase33(@Valid @RequestBody OrangeZSetMultipleZSetValues values) {
		try {
			return new OrangeRedisExampleResponse(new OrangeMapToRankExampleEntitiesConverter().toEntities(zsetExample1Api.reveseRanks(values.getValues())));
		}catch (Exception e) {
			e.printStackTrace();
			return new OrangeRedisExampleResponse(33,e.getMessage());
		}
	}
	
	@GetMapping("/v1/zsetOperations/sizeInScoreRange")
	public OrangeRedisExampleResponse testCase34(@RequestParam Double maxScore,@RequestParam Double minScore) {
		try {
			return new OrangeRedisExampleResponse(zsetExample1Api.getSizeByScoreRange(maxScore,minScore));
		}catch (Exception e) {
			e.printStackTrace();
			return new OrangeRedisExampleResponse(34,e.getMessage());
		}
	}
	
	@DeleteMapping("/v1/zsetOperations/removedMembersInScoreRange")
	public OrangeRedisExampleResponse testCase35(@RequestParam Double maxScore,@RequestParam Double minScore) {
		try {
			return new OrangeRedisExampleResponse(zsetExample1Api.removeByScoreRange(maxScore,minScore));
		}catch (Exception e) {
			e.printStackTrace();
			return new OrangeRedisExampleResponse(35,e.getMessage());
		}
	}
	
	@DeleteMapping("/v1/zsetOperations/removedMembersInRankRange")
	public OrangeRedisExampleResponse testCase35(@RequestParam Long startIndex,@RequestParam Long endIndex) {
		try {
			return new OrangeRedisExampleResponse(zsetExample1Api.removeByRankRange(startIndex,endIndex));
		}catch (Exception e) {
			e.printStackTrace();
			return new OrangeRedisExampleResponse(35,e.getMessage());
		}
	}
	
	@DeleteMapping("/v1/zsetOperations/popMaxMember")
	public OrangeRedisExampleResponse testCase36() {
		try {
			return new OrangeRedisExampleResponse(new OrangeMapToExampleEntitiesConverter().toEntities(zsetExample1Api.popMax()));
		}catch (Exception e) {
			e.printStackTrace();
			return new OrangeRedisExampleResponse(36,e.getMessage());
		}
	}
	
	@DeleteMapping("/v1/zsetOperations/popMinMember")
	public OrangeRedisExampleResponse testCase37() {
		try {
			return new OrangeRedisExampleResponse(new OrangeMapToExampleEntitiesConverter().toEntities(zsetExample1Api.popMin()));
		}catch (Exception e) {
			e.printStackTrace();
			return new OrangeRedisExampleResponse(37,e.getMessage());
		}
	}
	
	@DeleteMapping("/v1/zsetOperations/popMaxMembers")
	public OrangeRedisExampleResponse testCase38(@RequestParam Integer count) {
		try {
			return new OrangeRedisExampleResponse(new OrangeMapToExampleEntitiesConverter().toEntities(zsetExample1Api.popMax(count)));
		}catch (Exception e) {
			e.printStackTrace();
			return new OrangeRedisExampleResponse(38,e.getMessage());
		}
	}
	
	@DeleteMapping("/v1/zsetOperations/popMinMembers")
	public OrangeRedisExampleResponse testCase39(@RequestParam Integer count) {
		try {
			return new OrangeRedisExampleResponse(new OrangeMapToExampleEntitiesConverter().toEntities(zsetExample1Api.popMin(count)));
		}catch (Exception e) {
			e.printStackTrace();
			return new OrangeRedisExampleResponse(39,e.getMessage());
		}
	}
	
	@DeleteMapping("/v1/zsetOperations/timeLimitedPopMaxMember")
	public OrangeRedisExampleResponse testCase40(@RequestParam Long seconds) {
		try {
			return new OrangeRedisExampleResponse(new OrangeMapToExampleEntitiesConverter().toEntities(zsetExample1Api.timeLimitedPopMax(seconds,TimeUnit.SECONDS)));
		}catch (Exception e) {
			e.printStackTrace();
			return new OrangeRedisExampleResponse(40,e.getMessage());
		}
	}
	
	@DeleteMapping("/v1/zsetOperations/timeLimitedPopMinMember")
	public OrangeRedisExampleResponse testCase41(@RequestParam Long seconds) {
		try {
			return new OrangeRedisExampleResponse(new OrangeMapToExampleEntitiesConverter().toEntities(zsetExample1Api.timeLimitedPopMin(seconds,TimeUnit.SECONDS)));
		}catch (Exception e) {
			e.printStackTrace();
			return new OrangeRedisExampleResponse(41,e.getMessage());
		}
	}
	
	@PostMapping("/v1/zsetOperations/increment")
	public OrangeRedisExampleResponse testCase42(@Valid @RequestBody OrangeZSetScoreNullableExampleEntity entity) {
		try {
			Double result;
			if(entity.getScore() == null) {
				result = zsetExample1Api.increment(entity.getValue());	
			}else {
				result = zsetExample1Api.increment(entity.getValue(),entity.getScore());
			}
			return new OrangeRedisExampleResponse(result);
		}catch (Exception e) {
			e.printStackTrace();
			return new OrangeRedisExampleResponse(42,e.getMessage());
		}
		
	}
	
	@PostMapping("/v1/zsetOperations/decrement")
	public OrangeRedisExampleResponse testCase43(@Valid @RequestBody OrangeZSetScoreNullableExampleEntity entity) {
		try {
			Double result;
			if(entity.getScore() == null) {
				result = zsetExample1Api.decrement(entity.getValue());	
			}else{
				result = zsetExample1Api.decrement(entity.getValue(),entity.getScore());
			}
			return new OrangeRedisExampleResponse(result);
		}catch (Exception e) {
			e.printStackTrace();
			return new OrangeRedisExampleResponse(43,e.getMessage());
		}
		
	}
	
	@PutMapping("/v1/zsetOperations/lock")
	public OrangeRedisExampleResponse testCase44(@Valid @RequestBody OrangeZSetExampleEntity entity) {
		try {
			zsetExample1Api.acquire(entity.getValue(),entity.getScore());
			return new OrangeRedisExampleResponse();
		}catch (Exception e) {
			e.printStackTrace();
			return new OrangeRedisExampleResponse(44,e.getMessage());
		}
	}
	
	@PutMapping("/v1/zsetOperations/locks")
	public OrangeRedisExampleResponse testCase45(@Valid @RequestBody OrangeZSetMultipleExampleEntity multipleEntity) {
		try {
			Map<ZSetValue,Double>  members = new HashMap<>();
			Set<OrangeZSetExampleEntity> entities = multipleEntity.getEntities();
			for(OrangeZSetExampleEntity entity : entities) {
				members.put(entity.getValue(), entity.getScore());
			}
			zsetExample1Api.acquire(members);
			return new OrangeRedisExampleResponse();
		}catch (Exception e) {
			e.printStackTrace();
			return new OrangeRedisExampleResponse(45,e.getMessage());
		}
	}
}
