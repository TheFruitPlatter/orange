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
import java.util.List;
import java.util.Map;
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

import com.langwuyue.orange.example.redis.api.zset.OrangeRedisZSetExample2Api;
import com.langwuyue.orange.example.redis.entity.OrangeZSetMultipleZSetStringValues;
import com.langwuyue.orange.example.redis.entity.OrangeZSetStringValueCASExampleEntity;
import com.langwuyue.orange.example.redis.entity.OrangeZSetStringValueExampleEntity;
import com.langwuyue.orange.example.redis.entity.OrangeZSetStringValueMultipleExampleEntity;
import com.langwuyue.orange.example.redis.entity.OrangeZSetStringValueScoreNullableExampleEntity;
import com.langwuyue.orange.example.redis.response.OrangeRedisExampleResponse;

/**
 * @author Liang.Zhong
 * @since 1.0.0
 */
@RestController("zsetOrangeRedisStringSimpleTestCases")
public class OrangeRedisStringSimpleTestCases {
	
	@Autowired
	private OrangeRedisZSetExample2Api zsetExample2Api;
	
	@PutMapping("/v1/zsetStringOperations/member")
	public OrangeRedisExampleResponse testCase1(@Valid @RequestBody OrangeZSetStringValueExampleEntity entity) {
		try {
			zsetExample2Api.add(entity.getValue(),entity.getScore());
			return new OrangeRedisExampleResponse();
		}catch (Exception e) {
			e.printStackTrace();
			return new OrangeRedisExampleResponse(1,e.getMessage());
		}
		
	}
	
	@GetMapping("/v1/zsetStringOperations/score")
	public OrangeRedisExampleResponse testCase2(@RequestParam String value) {
		try {
			return new OrangeRedisExampleResponse(zsetExample2Api.getScore(value));
		}catch (Exception e) {
			e.printStackTrace();
			return new OrangeRedisExampleResponse(2,e.getMessage());
		}
	}
	
	@GetMapping("/v1/zsetStringOperations/rank")
	public OrangeRedisExampleResponse testCase3(@RequestParam String value) {
		try {
			return new OrangeRedisExampleResponse(zsetExample2Api.getRank(value));
		}catch (Exception e) {
			e.printStackTrace();
			return new OrangeRedisExampleResponse(3,e.getMessage());
		}
		
	}
	
	@GetMapping("/v1/zsetStringOperations/revesedRank")
	public OrangeRedisExampleResponse testCase4(@RequestParam String value) {
		try {
			return new OrangeRedisExampleResponse(zsetExample2Api.reveseRank(value));
		}catch (Exception e) {
			e.printStackTrace();
			return new OrangeRedisExampleResponse(4,e.getMessage());
		}
		
	}
	
	@GetMapping("/v1/zsetStringOperations/size")
	public OrangeRedisExampleResponse testCase5() {
		try {
			return new OrangeRedisExampleResponse(zsetExample2Api.getSize());
		}catch (Exception e) {
			e.printStackTrace();
			return new OrangeRedisExampleResponse(5,e.getMessage());
		}
		
	}
	
	@DeleteMapping("/v1/zsetStringOperations/zsets")
	public OrangeRedisExampleResponse testCase6() {
		try {
			boolean success = zsetExample2Api.delete();
			if(success) {
				return new OrangeRedisExampleResponse();
			}
			return new OrangeRedisExampleResponse(6,"False returned");
		}catch (Exception e) {
			e.printStackTrace();
			return new OrangeRedisExampleResponse(7,e.getMessage());
		}
	}
	
	@DeleteMapping("/v1/zsetStringOperations/members")
	public OrangeRedisExampleResponse testCase7(@RequestParam String value) {
		try {
			boolean success = zsetExample2Api.remove(value);
			if(success) {
				return new OrangeRedisExampleResponse();
			}
			return new OrangeRedisExampleResponse(8,"False returned");
		}catch (Exception e) {
			e.printStackTrace();
			return new OrangeRedisExampleResponse(9,e.getMessage());
		}
		
	}
	
	@PutMapping("/v1/zsetStringOperations/removedMembers")
	public OrangeRedisExampleResponse testCase8(@Valid @RequestBody OrangeZSetMultipleZSetStringValues values) {
		try {
			return new OrangeRedisExampleResponse(zsetExample2Api.remove(values.getValues()));
		}catch (Exception e) {
			e.printStackTrace();
			return new OrangeRedisExampleResponse(9,e.getMessage());
		}
	}
	
	@PutMapping("/v1/zsetStringOperations/members")
	public OrangeRedisExampleResponse testCase9(@Valid @RequestBody OrangeZSetStringValueMultipleExampleEntity multipleEntity) {
		try {
			Map<String,Double>  members = new HashMap<>();
			List<OrangeZSetStringValueExampleEntity> entities = multipleEntity.getEntities();
			for(OrangeZSetStringValueExampleEntity entity : entities) {
				members.put(entity.getValue(), entity.getScore());
			}
			return new OrangeRedisExampleResponse(zsetExample2Api.add(members));
		}catch (Exception e) {
			e.printStackTrace();
			return new OrangeRedisExampleResponse(10,e.getMessage());
		}
	}
	
	@PutMapping("/v1/zsetStringOperations/initMember")
	public OrangeRedisExampleResponse testCase10(@Valid @RequestBody OrangeZSetStringValueExampleEntity entity) {
		try {
			zsetExample2Api.addIfAsent(entity.getValue(),entity.getScore());
			return new OrangeRedisExampleResponse();
		}catch (Exception e) {
			e.printStackTrace();
			return new OrangeRedisExampleResponse(11,e.getMessage());
		}
		
	}
	
	@PutMapping("/v1/zsetStringOperations/initMembers")
	public OrangeRedisExampleResponse testCase11(@Valid @RequestBody OrangeZSetStringValueMultipleExampleEntity multipleEntity) {
		try {
			Map<String,Double>  members = new HashMap<>();
			List<OrangeZSetStringValueExampleEntity> entities = multipleEntity.getEntities();
			for(OrangeZSetStringValueExampleEntity entity : entities) {
				members.put(entity.getValue(), entity.getScore());
			}
			zsetExample2Api.addIfAsent(members);
			return new OrangeRedisExampleResponse();
		}catch (Exception e) {
			e.printStackTrace();
			return new OrangeRedisExampleResponse(12,e.getMessage());
		}
	}
	
	@PutMapping("/v1/zsetStringOperations/concurrentScores")
	public OrangeRedisExampleResponse testCase12(@Valid @RequestBody OrangeZSetStringValueCASExampleEntity casEntity) {
		try {
			return new OrangeRedisExampleResponse(zsetExample2Api.compareAndSwap(casEntity.getValue(), casEntity.getOldScore(), casEntity.getNewScore()));
		}catch (Exception e) {
			e.printStackTrace();
			return new OrangeRedisExampleResponse(14,e.getMessage());
		}
	}
	
	@GetMapping("/v1/zsetStringOperations/randomedValue")
	public OrangeRedisExampleResponse testCase13() {
		try {
			return new OrangeRedisExampleResponse(zsetExample2Api.randomGetValue());
		}catch (Exception e) {
			e.printStackTrace();
			return new OrangeRedisExampleResponse(15,e.getMessage());
		}
	}
	
	@GetMapping("/v1/zsetStringOperations/randomedMember")
	public OrangeRedisExampleResponse testCase14() {
		try {
			return new OrangeRedisExampleResponse(zsetExample2Api.randomGetMember());
		}catch (Exception e) {
			e.printStackTrace();
			return new OrangeRedisExampleResponse(16,e.getMessage());
		}
		
	}
	
	@GetMapping("/v1/zsetStringOperations/randomedValues")
	public OrangeRedisExampleResponse testCase15(@RequestParam Integer count) {
		try {
			return new OrangeRedisExampleResponse(zsetExample2Api.randomGetValues(count));
		}catch (Exception e) {
			e.printStackTrace();
			return new OrangeRedisExampleResponse(15,e.getMessage());
		}
	}
	
	@GetMapping("/v1/zsetStringOperations/randomedMembers")
	public OrangeRedisExampleResponse testCase16(@RequestParam Integer count) {
		try {
			return new OrangeRedisExampleResponse(zsetExample2Api.randomGetMembers(count));
		}catch (Exception e) {
			e.printStackTrace();
			return new OrangeRedisExampleResponse(16,e.getMessage());
		}
		
	}
	
	@GetMapping("/v1/zsetStringOperations/randomedAndDistinctedValues")
	public OrangeRedisExampleResponse testCase17(@RequestParam Integer count) {
		try {
			return new OrangeRedisExampleResponse(zsetExample2Api.distinctRandomGetValues(count));
		}catch (Exception e) {
			e.printStackTrace();
			return new OrangeRedisExampleResponse(17,e.getMessage());
		}
	}
	
	@GetMapping("/v1/zsetStringOperations/randomedAndDistinctedMembers")
	public OrangeRedisExampleResponse testCase18(@RequestParam Integer count) {
		try {
			return new OrangeRedisExampleResponse(zsetExample2Api.distinctRandomGetMembers(count));
		}catch (Exception e) {
			e.printStackTrace();
			return new OrangeRedisExampleResponse(18,e.getMessage());
		}
	}
	
	@GetMapping("/v1/zsetStringOperations/scoreRangeValues")
	public OrangeRedisExampleResponse testCase19(@RequestParam Double maxScore,@RequestParam Double minScore) {
		try {
			return new OrangeRedisExampleResponse(zsetExample2Api.getByScoreRange(maxScore,minScore));
		}catch (Exception e) {
			e.printStackTrace();
			return new OrangeRedisExampleResponse(19,e.getMessage());
		}
	}
	
	@GetMapping("/v1/zsetStringOperations/scoreRangeValuesByPage")
	public OrangeRedisExampleResponse testCase20(@RequestParam Double maxScore,@RequestParam Double minScore,@RequestParam Long pageNo,@RequestParam Long pageSize) {
		try {
			return new OrangeRedisExampleResponse(zsetExample2Api.getByScoreRange(maxScore,minScore,pageNo,pageSize));
		}catch (Exception e) {
			e.printStackTrace();
			return new OrangeRedisExampleResponse(20,e.getMessage());
		}
	}
	
	@GetMapping("/v1/zsetStringOperations/rankRangeValues")
	public OrangeRedisExampleResponse testCase21(@RequestParam Long startIndex,@RequestParam Long endIndex) {
		try {
			return new OrangeRedisExampleResponse(zsetExample2Api.getByRankRange(startIndex,endIndex));
		}catch (Exception e) {
			e.printStackTrace();
			return new OrangeRedisExampleResponse(21,e.getMessage());
		}
	}
	
	@GetMapping("/v1/zsetStringOperations/scoreRangeMembers")
	public OrangeRedisExampleResponse testCase22(@RequestParam Double maxScore,@RequestParam Double minScore) {
		try {
			return new OrangeRedisExampleResponse(zsetExample2Api.getByScoreRangeWithScores(maxScore,minScore));
		}catch (Exception e) {
			e.printStackTrace();
			return new OrangeRedisExampleResponse(22,e.getMessage());
		}
	}
	
	@GetMapping("/v1/zsetStringOperations/scoreRangeMembersByPage")
	public OrangeRedisExampleResponse testCase23(@RequestParam Double maxScore,@RequestParam Double minScore,@RequestParam Long pageNo,@RequestParam Long pageSize) {
		try {
			return new OrangeRedisExampleResponse(zsetExample2Api.getByScoreRangeWithScores(maxScore,minScore,pageNo,pageSize));
		}catch (Exception e) {
			e.printStackTrace();
			return new OrangeRedisExampleResponse(23,e.getMessage());
		}
	}
	
	@GetMapping("/v1/zsetStringOperations/rankRangeMembers")
	public OrangeRedisExampleResponse testCase24(@RequestParam Long startIndex,@RequestParam Long endIndex) {
		try {
			return new OrangeRedisExampleResponse(zsetExample2Api.getByRankRangeWithScores(startIndex,endIndex));
		}catch (Exception e) {
			e.printStackTrace();
			return new OrangeRedisExampleResponse(24,e.getMessage());
		}
	}
	
	@GetMapping("/v1/zsetStringOperations/revesedScoreRangeValues")
	public OrangeRedisExampleResponse testCase25(@RequestParam Double maxScore,@RequestParam Double minScore) {
		try {
			return new OrangeRedisExampleResponse(zsetExample2Api.reveseScoreRange(maxScore,minScore));
		}catch (Exception e) {
			e.printStackTrace();
			return new OrangeRedisExampleResponse(25,e.getMessage());
		}
	}
	
	@GetMapping("/v1/zsetStringOperations/revesedScoreRangeValuesByPage")
	public OrangeRedisExampleResponse testCase26(@RequestParam Double maxScore,@RequestParam Double minScore,@RequestParam Long pageNo,@RequestParam Long pageSize) {
		try {
			return new OrangeRedisExampleResponse(zsetExample2Api.reveseScoreRange(maxScore,minScore,pageNo,pageSize));
		}catch (Exception e) {
			e.printStackTrace();
			return new OrangeRedisExampleResponse(26,e.getMessage());
		}
	}
	
	@GetMapping("/v1/zsetStringOperations/revesedRankRangeValues")
	public OrangeRedisExampleResponse testCase27(@RequestParam Long startIndex,@RequestParam Long endIndex) {
		try {
			return new OrangeRedisExampleResponse(zsetExample2Api.reveseRankRange(startIndex,endIndex));
		}catch (Exception e) {
			e.printStackTrace();
			return new OrangeRedisExampleResponse(27,e.getMessage());
		}
	}
	
	@GetMapping("/v1/zsetStringOperations/revesedScoreRangeMembers")
	public OrangeRedisExampleResponse testCase28(@RequestParam Double maxScore,@RequestParam Double minScore) {
		try {
			return new OrangeRedisExampleResponse(zsetExample2Api.reveseScoreRangeWithScores(maxScore,minScore));
		}catch (Exception e) {
			e.printStackTrace();
			return new OrangeRedisExampleResponse(28,e.getMessage());
		}
	}
	
	@GetMapping("/v1/zsetStringOperations/revesedScoreRangeMembersByPage")
	public OrangeRedisExampleResponse testCase29(@RequestParam Double maxScore,@RequestParam Double minScore,@RequestParam Long pageNo,@RequestParam Long pageSize) {
		try {
			return new OrangeRedisExampleResponse(zsetExample2Api.reveseScoreRangeWithScores(maxScore,minScore,pageNo,pageSize));
		}catch (Exception e) {
			e.printStackTrace();
			return new OrangeRedisExampleResponse(29,e.getMessage());
		}
	}
	
	@GetMapping("/v1/zsetStringOperations/revesedRankRangeMembers")
	public OrangeRedisExampleResponse testCase30(@RequestParam Long startIndex,@RequestParam Long endIndex) {
		try {
			return new OrangeRedisExampleResponse(zsetExample2Api.reveseRankRangeWithScores(startIndex,endIndex));
		}catch (Exception e) {
			e.printStackTrace();
			return new OrangeRedisExampleResponse(30,e.getMessage());
		}
	}
	
	@PutMapping("/v1/zsetStringOperations/scores")
	public OrangeRedisExampleResponse testCase31(@Valid @RequestBody OrangeZSetMultipleZSetStringValues values) {
		try {
			return new OrangeRedisExampleResponse(zsetExample2Api.getScores(values.getValues()));
		}catch (Exception e) {
			e.printStackTrace();
			return new OrangeRedisExampleResponse(31,e.getMessage());
		}
	}
	
	@PutMapping("/v1/zsetStringOperations/ranks")
	public OrangeRedisExampleResponse testCase32(@Valid @RequestBody OrangeZSetMultipleZSetStringValues values) {
		try {
			return new OrangeRedisExampleResponse(zsetExample2Api.getRanks(values.getValues()));
		}catch (Exception e) {
			e.printStackTrace();
			return new OrangeRedisExampleResponse(32,e.getMessage());
		}
		
	}
	
	@PutMapping("/v1/zsetStringOperations/revesedRanks")
	public OrangeRedisExampleResponse testCase33(@Valid @RequestBody OrangeZSetMultipleZSetStringValues values) {
		try {
			return new OrangeRedisExampleResponse(zsetExample2Api.reveseRanks(values.getValues()));
		}catch (Exception e) {
			e.printStackTrace();
			return new OrangeRedisExampleResponse(33,e.getMessage());
		}
	}
	
	@GetMapping("/v1/zsetStringOperations/sizeInScoreRange")
	public OrangeRedisExampleResponse testCase34(@RequestParam Double maxScore,@RequestParam Double minScore) {
		try {
			return new OrangeRedisExampleResponse(zsetExample2Api.getSizeByScoreRange(maxScore,minScore));
		}catch (Exception e) {
			e.printStackTrace();
			return new OrangeRedisExampleResponse(34,e.getMessage());
		}
	}
	
	@DeleteMapping("/v1/zsetStringOperations/removedMembersInScoreRange")
	public OrangeRedisExampleResponse testCase35(@RequestParam Double maxScore,@RequestParam Double minScore) {
		try {
			return new OrangeRedisExampleResponse(zsetExample2Api.removeByScoreRange(maxScore,minScore));
		}catch (Exception e) {
			e.printStackTrace();
			return new OrangeRedisExampleResponse(35,e.getMessage());
		}
	}
	
	@DeleteMapping("/v1/zsetStringOperations/removedMembersInRankRange")
	public OrangeRedisExampleResponse testCase35(@RequestParam Long startIndex,@RequestParam Long endIndex) {
		try {
			return new OrangeRedisExampleResponse(zsetExample2Api.removeByRankRange(startIndex,endIndex));
		}catch (Exception e) {
			e.printStackTrace();
			return new OrangeRedisExampleResponse(35,e.getMessage());
		}
	}
	
	@DeleteMapping("/v1/zsetStringOperations/popMaxMember")
	public OrangeRedisExampleResponse testCase36() {
		try {
			return new OrangeRedisExampleResponse(zsetExample2Api.popMax());
		}catch (Exception e) {
			e.printStackTrace();
			return new OrangeRedisExampleResponse(36,e.getMessage());
		}
	}
	
	@DeleteMapping("/v1/zsetStringOperations/popMinMember")
	public OrangeRedisExampleResponse testCase37() {
		try {
			return new OrangeRedisExampleResponse(zsetExample2Api.popMin());
		}catch (Exception e) {
			e.printStackTrace();
			return new OrangeRedisExampleResponse(37,e.getMessage());
		}
	}
	
	@DeleteMapping("/v1/zsetStringOperations/popMaxMembers")
	public OrangeRedisExampleResponse testCase38(@RequestParam Integer count) {
		try {
			return new OrangeRedisExampleResponse(zsetExample2Api.popMax(count));
		}catch (Exception e) {
			e.printStackTrace();
			return new OrangeRedisExampleResponse(38,e.getMessage());
		}
	}
	
	@DeleteMapping("/v1/zsetStringOperations/popMinMembers")
	public OrangeRedisExampleResponse testCase39(@RequestParam Integer count) {
		try {
			return new OrangeRedisExampleResponse(zsetExample2Api.popMin(count));
		}catch (Exception e) {
			e.printStackTrace();
			return new OrangeRedisExampleResponse(39,e.getMessage());
		}
	}
	
	@DeleteMapping("/v1/zsetStringOperations/timeLimitedPopMaxMember")
	public OrangeRedisExampleResponse testCase40(@RequestParam Long seconds) {
		try {
			return new OrangeRedisExampleResponse(zsetExample2Api.timeLimitedPopMax(seconds,TimeUnit.SECONDS));
		}catch (Exception e) {
			e.printStackTrace();
			return new OrangeRedisExampleResponse(40,e.getMessage());
		}
	}
	
	@DeleteMapping("/v1/zsetStringOperations/timeLimitedPopMinMember")
	public OrangeRedisExampleResponse testCase41(@RequestParam Long seconds) {
		try {
			return new OrangeRedisExampleResponse(zsetExample2Api.timeLimitedPopMin(seconds,TimeUnit.SECONDS));
		}catch (Exception e) {
			e.printStackTrace();
			return new OrangeRedisExampleResponse(41,e.getMessage());
		}
	}
	
	@PostMapping("/v1/zsetStringOperations/increment")
	public OrangeRedisExampleResponse testCase42(@Valid @RequestBody OrangeZSetStringValueScoreNullableExampleEntity entity) {
		try {
			Double result;
			if(entity.getScore() == null) {
				result = zsetExample2Api.increment(entity.getValue());	
			}else {
				result = zsetExample2Api.increment(entity.getValue(),entity.getScore());
			}
			return new OrangeRedisExampleResponse(result);
		}catch (Exception e) {
			e.printStackTrace();
			return new OrangeRedisExampleResponse(42,e.getMessage());
		}
		
	}
	
	@PostMapping("/v1/zsetStringOperations/decrement")
	public OrangeRedisExampleResponse testCase43(@Valid @RequestBody OrangeZSetStringValueScoreNullableExampleEntity entity) {
		try {
			Double result;
			if(entity.getScore() == null) {
				result = zsetExample2Api.decrement(entity.getValue());	
			}else{
				result = zsetExample2Api.decrement(entity.getValue(),entity.getScore());
			}
			return new OrangeRedisExampleResponse(result);
		}catch (Exception e) {
			e.printStackTrace();
			return new OrangeRedisExampleResponse(43,e.getMessage());
		}
		
	}
	
	@PutMapping("/v1/zsetStringOperations/lock")
	public OrangeRedisExampleResponse testCase44(@Valid @RequestBody OrangeZSetStringValueExampleEntity entity) {
		try {
			zsetExample2Api.addIfAsent(entity.getValue(),entity.getScore());
			return new OrangeRedisExampleResponse();
		}catch (Exception e) {
			e.printStackTrace();
			return new OrangeRedisExampleResponse(44,e.getMessage());
		}
	}
	
	@PutMapping("/v1/zsetStringOperations/locks")
	public OrangeRedisExampleResponse testCase45(@Valid @RequestBody OrangeZSetStringValueMultipleExampleEntity multipleEntity) {
		try {
			Map<String,Double>  members = new HashMap<>();
			List<OrangeZSetStringValueExampleEntity> entities = multipleEntity.getEntities();
			for(OrangeZSetStringValueExampleEntity entity : entities) {
				members.put(entity.getValue(), entity.getScore());
			}
			zsetExample2Api.addIfAsent(members);
			return new OrangeRedisExampleResponse();
		}catch (Exception e) {
			e.printStackTrace();
			return new OrangeRedisExampleResponse(45,e.getMessage());
		}
	}
	
	@GetMapping("/v1/zsetStringOperations/lexRangeMembers")
	public OrangeRedisExampleResponse testCase46(@RequestParam String maxLex,@RequestParam String minLex) {
		try {
			return new OrangeRedisExampleResponse(zsetExample2Api.getByLexRange(maxLex, minLex));
		}catch (Exception e) {
			e.printStackTrace();
			return new OrangeRedisExampleResponse(46,e.getMessage());
		}
	}
	
	@GetMapping("/v1/zsetStringOperations/lexRangeMembersByPage")
	public OrangeRedisExampleResponse testCase47(@RequestParam String maxLex,@RequestParam String minLex,@RequestParam Long pageNo,@RequestParam Long pageSize) {
		try {
			return new OrangeRedisExampleResponse(zsetExample2Api.getByLexRange(maxLex, minLex,pageNo,pageSize));
		}catch (Exception e) {
			e.printStackTrace();
			return new OrangeRedisExampleResponse(47,e.getMessage());
		}
	}
	
	@GetMapping("/v1/zsetStringOperations/reversedLexRangeMembers")
	public OrangeRedisExampleResponse testCase48(@RequestParam String maxLex,@RequestParam String minLex) {
		try {
			return new OrangeRedisExampleResponse(zsetExample2Api.reveseByLexRange(maxLex, minLex));
		}catch (Exception e) {
			e.printStackTrace();
			return new OrangeRedisExampleResponse(48,e.getMessage());
		}
	}
	
	@GetMapping("/v1/zsetStringOperations/reversedLexRangeMembersByPage")
	public OrangeRedisExampleResponse testCase49(@RequestParam String maxLex,@RequestParam String minLex,@RequestParam Long pageNo,@RequestParam Long pageSize) {
		try {
			return new OrangeRedisExampleResponse(zsetExample2Api.reveseByLexRange(maxLex, minLex,pageNo,pageSize));
		}catch (Exception e) {
			e.printStackTrace();
			return new OrangeRedisExampleResponse(49,e.getMessage());
		}
	}
	
	@GetMapping("/v1/zsetStringOperations/sizeInLexRange")
	public OrangeRedisExampleResponse testCase50(@RequestParam String maxLex,@RequestParam String minLex) {
		try {
			return new OrangeRedisExampleResponse(zsetExample2Api.countByLexRange(maxLex, minLex));
		}catch (Exception e) {
			e.printStackTrace();
			return new OrangeRedisExampleResponse(50,e.getMessage());
		}
	}
	
	@DeleteMapping("/v1/zsetStringOperations/lexRangeMembers")
	public OrangeRedisExampleResponse testCase51(@RequestParam String maxLex,@RequestParam String minLex) {
		try {
			return new OrangeRedisExampleResponse(zsetExample2Api.removeByLexRange(maxLex, minLex));
		}catch (Exception e) {
			e.printStackTrace();
			return new OrangeRedisExampleResponse(51,e.getMessage());
		}
	}
}
