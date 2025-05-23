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

import com.langwuyue.orange.example.redis.api.zset.OrangeRedisZSetExample4Api;
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
@RestController("zsetOrangeRedisDynamicSimpleTestCases")
public class OrangeRedisDynamicSimpleTestCases {
	
	@Autowired
	private OrangeRedisZSetExample4Api zsetExample4Api;
	
	@PutMapping("/v1/zsetDynamicOperations/member")
	public OrangeRedisExampleResponse testCase1(@Valid @RequestBody OrangeZSetExampleEntity entity,@RequestParam String keyVar) {
		try {
			zsetExample4Api.add(keyVar, entity.getValue(),entity.getScore());
			return new OrangeRedisExampleResponse();
		}catch (Exception e) {
			e.printStackTrace();
			return new OrangeRedisExampleResponse(1,e.getMessage());
		}
	}
	
	@GetMapping("/v1/zsetDynamicOperations/score")
	public OrangeRedisExampleResponse testCase2(@RequestParam Long id,@RequestParam String name,@RequestParam String keyVar) {
		try {
			ZSetValue value = new ZSetValue();
			value.setId(id);
			value.setName(name);
			return new OrangeRedisExampleResponse(zsetExample4Api.getScore(keyVar,value));
		}catch (Exception e) {
			e.printStackTrace();
			return new OrangeRedisExampleResponse(2,e.getMessage());
		}
	}
	
	@GetMapping("/v1/zsetDynamicOperations/rank")
	public OrangeRedisExampleResponse testCase3(@RequestParam Long id,@RequestParam String name,@RequestParam String keyVar) {
		try {
			ZSetValue value = new ZSetValue();
			value.setId(id);
			value.setName(name);
			return new OrangeRedisExampleResponse(zsetExample4Api.getRank(keyVar,value));
		}catch (Exception e) {
			e.printStackTrace();
			return new OrangeRedisExampleResponse(3,e.getMessage());
		}
		
	}
	
	@GetMapping("/v1/zsetDynamicOperations/revesedRank")
	public OrangeRedisExampleResponse testCase4(@RequestParam Long id,@RequestParam String name,@RequestParam String keyVar) {
		try {
			ZSetValue value = new ZSetValue();
			value.setId(id);
			value.setName(name);
			return new OrangeRedisExampleResponse(zsetExample4Api.reveseRank(keyVar,value));
		}catch (Exception e) {
			e.printStackTrace();
			return new OrangeRedisExampleResponse(4,e.getMessage());
		}
		
	}
	
	@GetMapping("/v1/zsetDynamicOperations/size")
	public OrangeRedisExampleResponse testCase5(@RequestParam String keyVar) {
		try {
			return new OrangeRedisExampleResponse(zsetExample4Api.getSize(keyVar));
		}catch (Exception e) {
			e.printStackTrace();
			return new OrangeRedisExampleResponse(5,e.getMessage());
		}
		
	}
	
	@DeleteMapping("/v1/zsetDynamicOperations/zsets")
	public OrangeRedisExampleResponse testCase6(@RequestParam String keyVar) {
		try {
			boolean success = zsetExample4Api.delete(keyVar);
			if(success) {
				return new OrangeRedisExampleResponse();
			}
			return new OrangeRedisExampleResponse(6,"False returned");
		}catch (Exception e) {
			e.printStackTrace();
			return new OrangeRedisExampleResponse(7,e.getMessage());
		}
	}
	
	@DeleteMapping("/v1/zsetDynamicOperations/members")
	public OrangeRedisExampleResponse testCase7(@RequestParam Long id,@RequestParam String name,@RequestParam String keyVar) {
		try {
			ZSetValue value = new ZSetValue();
			value.setId(id);
			value.setName(name);
			boolean success = zsetExample4Api.remove(keyVar,value);
			if(success) {
				return new OrangeRedisExampleResponse();
			}
			return new OrangeRedisExampleResponse(8,"False returned");
		}catch (Exception e) {
			e.printStackTrace();
			return new OrangeRedisExampleResponse(9,e.getMessage());
		}
		
	}
	
	@PutMapping("/v1/zsetDynamicOperations/removedMembers")
	public OrangeRedisExampleResponse testCase8(@Valid @RequestBody OrangeZSetMultipleZSetValues values,@RequestParam String keyVar) {
		try {
			return new OrangeRedisExampleResponse(new OrangeMapToBooleanExampleEntitiesConverter().toEntities(zsetExample4Api.remove(keyVar,values.getValues())));
		}catch (Exception e) {
			e.printStackTrace();
			return new OrangeRedisExampleResponse(9,e.getMessage());
		}
	}
	
	@PutMapping("/v1/zsetDynamicOperations/members")
	public OrangeRedisExampleResponse testCase9(@Valid @RequestBody OrangeZSetMultipleExampleEntity multipleEntity,@RequestParam String keyVar) {
		try {
			Map<ZSetValue,Double>  members = new HashMap<>();
			Set<OrangeZSetExampleEntity> entities = multipleEntity.getEntities();
			for(OrangeZSetExampleEntity entity : entities) {
				members.put(entity.getValue(), entity.getScore());
			}
			return new OrangeRedisExampleResponse(new OrangeMapToBooleanExampleEntitiesConverter().toEntities(zsetExample4Api.add(keyVar,members)));
		}catch (Exception e) {
			e.printStackTrace();
			return new OrangeRedisExampleResponse(10,e.getMessage());
		}
	}
	
	@PutMapping("/v1/zsetDynamicOperations/initMember")
	public OrangeRedisExampleResponse testCase10(@Valid @RequestBody OrangeZSetExampleEntity entity,@RequestParam String keyVar) {
		try {
			zsetExample4Api.addIfAsent(keyVar,entity.getValue(),entity.getScore());
			return new OrangeRedisExampleResponse();
		}catch (Exception e) {
			e.printStackTrace();
			return new OrangeRedisExampleResponse(11,e.getMessage());
		}
	}
	
	@PutMapping("/v1/zsetDynamicOperations/initMembers")
	public OrangeRedisExampleResponse testCase11(@Valid @RequestBody OrangeZSetMultipleExampleEntity multipleEntity,@RequestParam String keyVar) {
		try {
			Map<ZSetValue,Double>  members = new HashMap<>();
			Set<OrangeZSetExampleEntity> entities = multipleEntity.getEntities();
			for(OrangeZSetExampleEntity entity : entities) {
				members.put(entity.getValue(), entity.getScore());
			}
			zsetExample4Api.addIfAsent(keyVar,members);
			return new OrangeRedisExampleResponse();
		}catch (Exception e) {
			e.printStackTrace();
			return new OrangeRedisExampleResponse(12,e.getMessage());
		}
	}
	
	@PutMapping("/v1/zsetDynamicOperations/concurrentScores")
	public OrangeRedisExampleResponse testCase12(@Valid @RequestBody OrangeZSetCASExampleEntity casEntity,@RequestParam String keyVar) {
		try {
			return new OrangeRedisExampleResponse(zsetExample4Api.compareAndSwap(keyVar,casEntity.getValue(), casEntity.getOldScore(), casEntity.getNewScore()));
		}catch (Exception e) {
			e.printStackTrace();
			return new OrangeRedisExampleResponse(14,e.getMessage());
		}
	}
	
	@GetMapping("/v1/zsetDynamicOperations/randomedValue")
	public OrangeRedisExampleResponse testCase13(@RequestParam String keyVar) {
		try {
			return new OrangeRedisExampleResponse(zsetExample4Api.randomGetValue(keyVar));
		}catch (Exception e) {
			e.printStackTrace();
			return new OrangeRedisExampleResponse(15,e.getMessage());
		}
	}
	
	@GetMapping("/v1/zsetDynamicOperations/randomedMember")
	public OrangeRedisExampleResponse testCase14(@RequestParam String keyVar) {
		try {
			return new OrangeRedisExampleResponse(new OrangeMapToExampleEntitiesConverter().toEntities(zsetExample4Api.randomGetMember(keyVar)));
		}catch (Exception e) {
			e.printStackTrace();
			return new OrangeRedisExampleResponse(16,e.getMessage());
		}
		
	}
	
	@GetMapping("/v1/zsetDynamicOperations/randomedValues")
	public OrangeRedisExampleResponse testCase15(@RequestParam Integer count,@RequestParam String keyVar) {
		try {
			return new OrangeRedisExampleResponse(zsetExample4Api.randomGetValues(keyVar,count));
		}catch (Exception e) {
			e.printStackTrace();
			return new OrangeRedisExampleResponse(15,e.getMessage());
		}
	}
	
	@GetMapping("/v1/zsetDynamicOperations/randomedMembers")
	public OrangeRedisExampleResponse testCase16(@RequestParam Integer count,@RequestParam String keyVar) {
		try {
			return new OrangeRedisExampleResponse(new OrangeMapToExampleEntitiesConverter().toEntities(zsetExample4Api.randomGetMembers(keyVar,count)));
		}catch (Exception e) {
			e.printStackTrace();
			return new OrangeRedisExampleResponse(16,e.getMessage());
		}
		
	}
	
	@GetMapping("/v1/zsetDynamicOperations/randomedAndDistinctedValues")
	public OrangeRedisExampleResponse testCase17(@RequestParam Integer count,@RequestParam String keyVar) {
		try {
			return new OrangeRedisExampleResponse(zsetExample4Api.distinctRandomGetValues(keyVar,count));
		}catch (Exception e) {
			e.printStackTrace();
			return new OrangeRedisExampleResponse(17,e.getMessage());
		}
	}
	
	@GetMapping("/v1/zsetDynamicOperations/randomedAndDistinctedMembers")
	public OrangeRedisExampleResponse testCase18(@RequestParam Integer count,@RequestParam String keyVar) {
		try {
			return new OrangeRedisExampleResponse(new OrangeMapToExampleEntitiesConverter().toEntities(zsetExample4Api.distinctRandomGetMembers(keyVar,count)));
		}catch (Exception e) {
			e.printStackTrace();
			return new OrangeRedisExampleResponse(18,e.getMessage());
		}
	}
	
	@GetMapping("/v1/zsetDynamicOperations/scoreRangeValues")
	public OrangeRedisExampleResponse testCase19(@RequestParam Double maxScore,@RequestParam Double minScore,@RequestParam String keyVar) {
		try {
			return new OrangeRedisExampleResponse(zsetExample4Api.getByScoreRange(keyVar,maxScore,minScore));
		}catch (Exception e) {
			e.printStackTrace();
			return new OrangeRedisExampleResponse(19,e.getMessage());
		}
	}
	
	@GetMapping("/v1/zsetDynamicOperations/scoreRangeValuesByPage")
	public OrangeRedisExampleResponse testCase20(@RequestParam Double maxScore,@RequestParam Double minScore,@RequestParam Long pageNo,@RequestParam Long pageSize,@RequestParam String keyVar) {
		try {
			return new OrangeRedisExampleResponse(zsetExample4Api.getByScoreRange(keyVar,maxScore,minScore,pageNo,pageSize));
		}catch (Exception e) {
			e.printStackTrace();
			return new OrangeRedisExampleResponse(20,e.getMessage());
		}
	}
	
	@GetMapping("/v1/zsetDynamicOperations/rankRangeValues")
	public OrangeRedisExampleResponse testCase21(@RequestParam Long startIndex,@RequestParam Long endIndex,@RequestParam String keyVar) {
		try {
			return new OrangeRedisExampleResponse(zsetExample4Api.getByRankRange(keyVar,startIndex,endIndex));
		}catch (Exception e) {
			e.printStackTrace();
			return new OrangeRedisExampleResponse(21,e.getMessage());
		}
	}
	
	@GetMapping("/v1/zsetDynamicOperations/scoreRangeMembers")
	public OrangeRedisExampleResponse testCase22(@RequestParam Double maxScore,@RequestParam Double minScore,@RequestParam String keyVar) {
		try {
			return new OrangeRedisExampleResponse(new OrangeMapToExampleEntitiesConverter().toEntities(zsetExample4Api.getByScoreRangeWithScores(keyVar,maxScore,minScore)));
		}catch (Exception e) {
			e.printStackTrace();
			return new OrangeRedisExampleResponse(22,e.getMessage());
		}
	}
	
	@GetMapping("/v1/zsetDynamicOperations/scoreRangeMembersByPage")
	public OrangeRedisExampleResponse testCase23(@RequestParam Double maxScore,@RequestParam Double minScore,@RequestParam Long pageNo,@RequestParam Long pageSize,@RequestParam String keyVar) {
		try {
			return new OrangeRedisExampleResponse(new OrangeMapToExampleEntitiesConverter().toEntities(zsetExample4Api.getByScoreRangeWithScores(keyVar,maxScore,minScore,pageNo,pageSize)));
		}catch (Exception e) {
			e.printStackTrace();
			return new OrangeRedisExampleResponse(23,e.getMessage());
		}
	}
	
	@GetMapping("/v1/zsetDynamicOperations/rankRangeMembers")
	public OrangeRedisExampleResponse testCase24(@RequestParam Long startIndex,@RequestParam Long endIndex,@RequestParam String keyVar) {
		try {
			return new OrangeRedisExampleResponse(new OrangeMapToExampleEntitiesConverter().toEntities(zsetExample4Api.getByRankRangeWithScores(keyVar,startIndex,endIndex)));
		}catch (Exception e) {
			e.printStackTrace();
			return new OrangeRedisExampleResponse(24,e.getMessage());
		}
	}
	
	@GetMapping("/v1/zsetDynamicOperations/revesedScoreRangeValues")
	public OrangeRedisExampleResponse testCase25(@RequestParam Double maxScore,@RequestParam Double minScore,@RequestParam String keyVar) {
		try {
			return new OrangeRedisExampleResponse(zsetExample4Api.reveseScoreRange(keyVar,maxScore,minScore));
		}catch (Exception e) {
			e.printStackTrace();
			return new OrangeRedisExampleResponse(25,e.getMessage());
		}
	}
	
	@GetMapping("/v1/zsetDynamicOperations/revesedScoreRangeValuesByPage")
	public OrangeRedisExampleResponse testCase26(@RequestParam Double maxScore,@RequestParam Double minScore,@RequestParam Long pageNo,@RequestParam Long pageSize,@RequestParam String keyVar) {
		try {
			return new OrangeRedisExampleResponse(zsetExample4Api.reveseScoreRange(keyVar,maxScore,minScore,pageNo,pageSize));
		}catch (Exception e) {
			e.printStackTrace();
			return new OrangeRedisExampleResponse(26,e.getMessage());
		}
	}
	
	@GetMapping("/v1/zsetDynamicOperations/revesedRankRangeValues")
	public OrangeRedisExampleResponse testCase27(@RequestParam Long startIndex,@RequestParam Long endIndex,@RequestParam String keyVar) {
		try {
			return new OrangeRedisExampleResponse(zsetExample4Api.reveseRankRange(keyVar,startIndex,endIndex));
		}catch (Exception e) {
			e.printStackTrace();
			return new OrangeRedisExampleResponse(27,e.getMessage());
		}
	}
	
	@GetMapping("/v1/zsetDynamicOperations/revesedScoreRangeMembers")
	public OrangeRedisExampleResponse testCase28(@RequestParam Double maxScore,@RequestParam Double minScore,@RequestParam String keyVar) {
		try {
			return new OrangeRedisExampleResponse(new OrangeMapToExampleEntitiesConverter().toEntities(zsetExample4Api.reveseScoreRangeWithScores(keyVar,maxScore,minScore)));
		}catch (Exception e) {
			e.printStackTrace();
			return new OrangeRedisExampleResponse(28,e.getMessage());
		}
	}
	
	@GetMapping("/v1/zsetDynamicOperations/revesedScoreRangeMembersByPage")
	public OrangeRedisExampleResponse testCase29(@RequestParam Double maxScore,@RequestParam Double minScore,@RequestParam Long pageNo,@RequestParam Long pageSize,@RequestParam String keyVar) {
		try {
			return new OrangeRedisExampleResponse(new OrangeMapToExampleEntitiesConverter().toEntities(zsetExample4Api.reveseScoreRangeWithScores(keyVar,maxScore,minScore,pageNo,pageSize)));
		}catch (Exception e) {
			e.printStackTrace();
			return new OrangeRedisExampleResponse(29,e.getMessage());
		}
	}
	
	@GetMapping("/v1/zsetDynamicOperations/revesedRankRangeMembers")
	public OrangeRedisExampleResponse testCase30(@RequestParam Long startIndex,@RequestParam Long endIndex,@RequestParam String keyVar) {
		try {
			return new OrangeRedisExampleResponse(new OrangeMapToExampleEntitiesConverter().toEntities(zsetExample4Api.reveseRankRangeWithScores(keyVar,startIndex,endIndex)));
		}catch (Exception e) {
			e.printStackTrace();
			return new OrangeRedisExampleResponse(30,e.getMessage());
		}
	}
	
	@PutMapping("/v1/zsetDynamicOperations/scores")
	public OrangeRedisExampleResponse testCase31(@Valid @RequestBody OrangeZSetMultipleZSetValues values,@RequestParam String keyVar) {
		try {
			return new OrangeRedisExampleResponse(new OrangeMapToExampleEntitiesConverter().toEntities(zsetExample4Api.getScores(keyVar,values.getValues())));
		}catch (Exception e) {
			e.printStackTrace();
			return new OrangeRedisExampleResponse(31,e.getMessage());
		}
	}
	
	@PutMapping("/v1/zsetDynamicOperations/ranks")
	public OrangeRedisExampleResponse testCase32(@Valid @RequestBody OrangeZSetMultipleZSetValues values,@RequestParam String keyVar) {
		try {
			return new OrangeRedisExampleResponse(new OrangeMapToRankExampleEntitiesConverter().toEntities(zsetExample4Api.getRanks(keyVar,values.getValues())));
		}catch (Exception e) {
			e.printStackTrace();
			return new OrangeRedisExampleResponse(32,e.getMessage());
		}
		
	}
	
	@PutMapping("/v1/zsetDynamicOperations/revesedRanks")
	public OrangeRedisExampleResponse testCase33(@Valid @RequestBody OrangeZSetMultipleZSetValues values,@RequestParam String keyVar) {
		try {
			return new OrangeRedisExampleResponse(new OrangeMapToRankExampleEntitiesConverter().toEntities(zsetExample4Api.reveseRanks(keyVar,values.getValues())));
		}catch (Exception e) {
			e.printStackTrace();
			return new OrangeRedisExampleResponse(33,e.getMessage());
		}
	}
	
	@GetMapping("/v1/zsetDynamicOperations/sizeInScoreRange")
	public OrangeRedisExampleResponse testCase34(@RequestParam Double maxScore,@RequestParam Double minScore,@RequestParam String keyVar) {
		try {
			return new OrangeRedisExampleResponse(zsetExample4Api.getSizeByScoreRange(keyVar,maxScore,minScore));
		}catch (Exception e) {
			e.printStackTrace();
			return new OrangeRedisExampleResponse(34,e.getMessage());
		}
	}
	
	@DeleteMapping("/v1/zsetDynamicOperations/removedMembersInScoreRange")
	public OrangeRedisExampleResponse testCase35(@RequestParam Double maxScore,@RequestParam Double minScore,@RequestParam String keyVar) {
		try {
			return new OrangeRedisExampleResponse(zsetExample4Api.removeByScoreRange(keyVar,maxScore,minScore));
		}catch (Exception e) {
			e.printStackTrace();
			return new OrangeRedisExampleResponse(35,e.getMessage());
		}
	}
	
	@DeleteMapping("/v1/zsetDynamicOperations/removedMembersInRankRange")
	public OrangeRedisExampleResponse testCase35(@RequestParam Long startIndex,@RequestParam Long endIndex,@RequestParam String keyVar) {
		try {
			return new OrangeRedisExampleResponse(zsetExample4Api.removeByRankRange(keyVar,startIndex,endIndex));
		}catch (Exception e) {
			e.printStackTrace();
			return new OrangeRedisExampleResponse(35,e.getMessage());
		}
	}
	
	@DeleteMapping("/v1/zsetDynamicOperations/popMaxMember")
	public OrangeRedisExampleResponse testCase36(@RequestParam String keyVar) {
		try {
			return new OrangeRedisExampleResponse(new OrangeMapToExampleEntitiesConverter().toEntities(zsetExample4Api.popMax(keyVar)));
		}catch (Exception e) {
			e.printStackTrace();
			return new OrangeRedisExampleResponse(36,e.getMessage());
		}
	}
	
	@DeleteMapping("/v1/zsetDynamicOperations/popMinMember")
	public OrangeRedisExampleResponse testCase37(@RequestParam String keyVar) {
		try {
			return new OrangeRedisExampleResponse(new OrangeMapToExampleEntitiesConverter().toEntities(zsetExample4Api.popMin(keyVar)));
		}catch (Exception e) {
			e.printStackTrace();
			return new OrangeRedisExampleResponse(37,e.getMessage());
		}
	}
	
	@DeleteMapping("/v1/zsetDynamicOperations/popMaxMembers")
	public OrangeRedisExampleResponse testCase38(@RequestParam Integer count,@RequestParam String keyVar) {
		try {
			return new OrangeRedisExampleResponse(new OrangeMapToExampleEntitiesConverter().toEntities(zsetExample4Api.popMax(keyVar,count)));
		}catch (Exception e) {
			e.printStackTrace();
			return new OrangeRedisExampleResponse(38,e.getMessage());
		}
	}
	
	@DeleteMapping("/v1/zsetDynamicOperations/popMinMembers")
	public OrangeRedisExampleResponse testCase39(@RequestParam Integer count,@RequestParam String keyVar) {
		try {
			return new OrangeRedisExampleResponse(new OrangeMapToExampleEntitiesConverter().toEntities(zsetExample4Api.popMin(keyVar,count)));
		}catch (Exception e) {
			e.printStackTrace();
			return new OrangeRedisExampleResponse(39,e.getMessage());
		}
	}
	
	@DeleteMapping("/v1/zsetDynamicOperations/timeLimitedPopMaxMember")
	public OrangeRedisExampleResponse testCase40(@RequestParam Long seconds,@RequestParam String keyVar) {
		try {
			return new OrangeRedisExampleResponse(new OrangeMapToExampleEntitiesConverter().toEntities(zsetExample4Api.timeLimitedPopMax(keyVar,seconds,TimeUnit.SECONDS)));
		}catch (Exception e) {
			e.printStackTrace();
			return new OrangeRedisExampleResponse(40,e.getMessage());
		}
	}
	
	@DeleteMapping("/v1/zsetDynamicOperations/timeLimitedPopMinMember")
	public OrangeRedisExampleResponse testCase41(@RequestParam Long seconds,@RequestParam String keyVar) {
		try {
			return new OrangeRedisExampleResponse(new OrangeMapToExampleEntitiesConverter().toEntities(zsetExample4Api.timeLimitedPopMin(keyVar,seconds,TimeUnit.SECONDS)));
		}catch (Exception e) {
			e.printStackTrace();
			return new OrangeRedisExampleResponse(41,e.getMessage());
		}
	}
	
	@PostMapping("/v1/zsetDynamicOperations/increment")
	public OrangeRedisExampleResponse testCase42(@Valid @RequestBody OrangeZSetScoreNullableExampleEntity entity,@RequestParam String keyVar) {
		try {
			Double result;
			if(entity.getScore() == null) {
				result = zsetExample4Api.increment(keyVar,entity.getValue());	
			}else {
				result = zsetExample4Api.increment(keyVar,entity.getValue(),entity.getScore());
			}
			return new OrangeRedisExampleResponse(result);
		}catch (Exception e) {
			e.printStackTrace();
			return new OrangeRedisExampleResponse(42,e.getMessage());
		}
		
	}
	
	@PostMapping("/v1/zsetDynamicOperations/decrement")
	public OrangeRedisExampleResponse testCase43(@Valid @RequestBody OrangeZSetScoreNullableExampleEntity entity,@RequestParam String keyVar) {
		try {
			Double result;
			if(entity.getScore() == null) {
				result = zsetExample4Api.decrement(keyVar,entity.getValue());	
			}else{
				result = zsetExample4Api.decrement(keyVar,entity.getValue(),entity.getScore());
			}
			return new OrangeRedisExampleResponse(result);
		}catch (Exception e) {
			e.printStackTrace();
			return new OrangeRedisExampleResponse(43,e.getMessage());
		}
		
	}
	
	@PutMapping("/v1/zsetDynamicOperations/lock")
	public OrangeRedisExampleResponse testCase44(@Valid @RequestBody OrangeZSetExampleEntity entity,@RequestParam String keyVar) {
		try {
			zsetExample4Api.addIfAsent(keyVar,entity.getValue(),entity.getScore());
			return new OrangeRedisExampleResponse();
		}catch (Exception e) {
			e.printStackTrace();
			return new OrangeRedisExampleResponse(44,e.getMessage());
		}
	}
	
	@PutMapping("/v1/zsetDynamicOperations/locks")
	public OrangeRedisExampleResponse testCase45(@Valid @RequestBody OrangeZSetMultipleExampleEntity multipleEntity,@RequestParam String keyVar) {
		try {
			Map<ZSetValue,Double>  members = new HashMap<>();
			Set<OrangeZSetExampleEntity> entities = multipleEntity.getEntities();
			for(OrangeZSetExampleEntity entity : entities) {
				members.put(entity.getValue(), entity.getScore());
			}
			zsetExample4Api.addIfAsent(keyVar,members);
			return new OrangeRedisExampleResponse();
		}catch (Exception e) {
			e.printStackTrace();
			return new OrangeRedisExampleResponse(45,e.getMessage());
		}
	}
}
