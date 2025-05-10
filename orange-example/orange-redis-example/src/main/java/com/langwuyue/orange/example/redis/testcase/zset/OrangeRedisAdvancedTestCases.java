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

import java.util.concurrent.TimeUnit;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.langwuyue.orange.example.redis.api.zset.OrangeRedisZSetExample3Api;
import com.langwuyue.orange.example.redis.converters.OrangeMapToBooleanExampleEntities2Converter;
import com.langwuyue.orange.example.redis.converters.OrangeMapToBooleanExampleEntities3Converter;
import com.langwuyue.orange.example.redis.entity.OrangePagerEntity;
import com.langwuyue.orange.example.redis.entity.OrangeRankRangeEntity;
import com.langwuyue.orange.example.redis.entity.OrangeScoreRangeEntity;
import com.langwuyue.orange.example.redis.entity.OrangeZSetExampleEntity;
import com.langwuyue.orange.example.redis.entity.OrangeZSetMultipleExampleEntity;
import com.langwuyue.orange.example.redis.entity.OrangeZSetMultipleScoreNullableExampleEntity;
import com.langwuyue.orange.example.redis.entity.OrangeZSetScoreNullableExampleEntity;
import com.langwuyue.orange.example.redis.response.OrangeRedisExampleResponse;

/**
 * @author Liang.Zhong
 * @since 1.0.0
 */
@RestController("zsetOrangeRedisAdvancedTestCases")
public class OrangeRedisAdvancedTestCases {
	
	@Autowired
	private OrangeRedisZSetExample3Api zsetExample3Api;
	
	@PutMapping("/v1/zsetAdvancedOperations/member")
	public OrangeRedisExampleResponse testCase1(@Valid @RequestBody OrangeZSetExampleEntity entity) {
		try {
			zsetExample3Api.add(entity);
			return new OrangeRedisExampleResponse();
		}catch (Exception e) {
			e.printStackTrace();
			return new OrangeRedisExampleResponse(1,e.getMessage());
		}
	}
		
	@DeleteMapping("/v1/zsetAdvancedOperations/zsets")
	public OrangeRedisExampleResponse testCase2() {
		try {
			boolean success = zsetExample3Api.delete();
			if(success) {
				return new OrangeRedisExampleResponse();
			}
			return new OrangeRedisExampleResponse(2,"False returned");
		}catch (Exception e) {
			e.printStackTrace();
			return new OrangeRedisExampleResponse(3,e.getMessage());
		}
	}
	
	@PutMapping("/v1/zsetAdvancedOperations/removedMembers")
	public OrangeRedisExampleResponse testCase3(@Valid @RequestBody OrangeZSetMultipleScoreNullableExampleEntity memberList) {
		try {
			return new OrangeRedisExampleResponse(new OrangeMapToBooleanExampleEntities2Converter().toEntities(zsetExample3Api.remove(memberList.getMembers())));
		}catch (Exception e) {
			e.printStackTrace();
			return new OrangeRedisExampleResponse(4,e.getMessage());
		}
	}
	
	@PutMapping("/v1/zsetAdvancedOperations/members")
	public OrangeRedisExampleResponse testCase4(@Valid @RequestBody OrangeZSetMultipleExampleEntity multipleEntity) {
		try {
			return new OrangeRedisExampleResponse(new OrangeMapToBooleanExampleEntities3Converter().toEntities(zsetExample3Api.add(multipleEntity.getEntities())));
		}catch (Exception e) {
			e.printStackTrace();
			return new OrangeRedisExampleResponse(5,e.getMessage());
		}
	}
	
	@PutMapping("/v1/zsetAdvancedOperations/initMember")
	public OrangeRedisExampleResponse testCase5(@Valid @RequestBody OrangeZSetExampleEntity entity) {
		try {
			zsetExample3Api.addIfAsent(entity);
			return new OrangeRedisExampleResponse();
		}catch (Exception e) {
			e.printStackTrace();
			return new OrangeRedisExampleResponse(6,e.getMessage());
		}
		
	}
	
	@PutMapping("/v1/zsetAdvancedOperations/initMembers")
	public OrangeRedisExampleResponse testCase6(@Valid @RequestBody OrangeZSetMultipleExampleEntity multipleEntity) {
		try {
			zsetExample3Api.addIfAsent(multipleEntity.getEntities());
			return new OrangeRedisExampleResponse();
		}catch (Exception e) {
			e.printStackTrace();
			return new OrangeRedisExampleResponse(7,e.getMessage());
		}
	}
	
	@GetMapping("/v1/zsetAdvancedOperations/randomedValue1")
	public OrangeRedisExampleResponse testCase7() {
		try {
			return new OrangeRedisExampleResponse(zsetExample3Api.randomGetValue1());
		}catch (Exception e) {
			e.printStackTrace();
			return new OrangeRedisExampleResponse(8,e.getMessage());
		}
	}
	
	@GetMapping("/v1/zsetAdvancedOperations/randomedValue2")
	public OrangeRedisExampleResponse testCase8() {
		try {
			return new OrangeRedisExampleResponse(zsetExample3Api.randomGetValue2());
		}catch (Exception e) {
			e.printStackTrace();
			return new OrangeRedisExampleResponse(9,e.getMessage());
		}
	}
	
	@GetMapping("/v1/zsetAdvancedOperations/randomedMember")
	public OrangeRedisExampleResponse testCase9() {
		try {
			return new OrangeRedisExampleResponse(zsetExample3Api.randomGetMember());
		}catch (Exception e) {
			e.printStackTrace();
			return new OrangeRedisExampleResponse(10,e.getMessage());
		}
	}
	
	@GetMapping("/v1/zsetAdvancedOperations/randomedValues1")
	public OrangeRedisExampleResponse testCase10(@RequestParam Integer count) {
		try {
			return new OrangeRedisExampleResponse(zsetExample3Api.randomGetValues1(count));
		}catch (Exception e) {
			e.printStackTrace();
			return new OrangeRedisExampleResponse(11,e.getMessage());
		}
	}
	
	@GetMapping("/v1/zsetAdvancedOperations/randomedValues2")
	public OrangeRedisExampleResponse testCase11(@RequestParam Integer count) {
		try {
			return new OrangeRedisExampleResponse(zsetExample3Api.randomGetValues2(count));
		}catch (Exception e) {
			e.printStackTrace();
			return new OrangeRedisExampleResponse(12,e.getMessage());
		}
	}
	
	@GetMapping("/v1/zsetAdvancedOperations/randomedMembers")
	public OrangeRedisExampleResponse testCase12(@RequestParam Integer count) {
		try {
			return new OrangeRedisExampleResponse(zsetExample3Api.randomGetMembers(count));
		}catch (Exception e) {
			e.printStackTrace();
			return new OrangeRedisExampleResponse(13,e.getMessage());
		}
		
	}
	
	@GetMapping("/v1/zsetAdvancedOperations/randomedAndDistinctedValues1")
	public OrangeRedisExampleResponse testCase13(@RequestParam Integer count) {
		try {
			return new OrangeRedisExampleResponse(zsetExample3Api.distinctRandomGetValues1(count));
		}catch (Exception e) {
			e.printStackTrace();
			return new OrangeRedisExampleResponse(14,e.getMessage());
		}
	}
	
	@GetMapping("/v1/zsetAdvancedOperations/randomedAndDistinctedValues2")
	public OrangeRedisExampleResponse testCase14(@RequestParam Integer count) {
		try {
			return new OrangeRedisExampleResponse(zsetExample3Api.distinctRandomGetValues2(count));
		}catch (Exception e) {
			e.printStackTrace();
			return new OrangeRedisExampleResponse(15,e.getMessage());
		}
	}
	
	@GetMapping("/v1/zsetAdvancedOperations/randomedAndDistinctedMembers")
	public OrangeRedisExampleResponse testCase15(@RequestParam Integer count) {
		try {
			return new OrangeRedisExampleResponse(zsetExample3Api.distinctRandomGetMembers(count));
		}catch (Exception e) {
			e.printStackTrace();
			return new OrangeRedisExampleResponse(16,e.getMessage());
		}
	}
	
	@GetMapping("/v1/zsetAdvancedOperations/scoreRangeValues1")
	public OrangeRedisExampleResponse testCase17(@RequestParam Double maxScore, @RequestParam Double minScore) {
		try {
			return new OrangeRedisExampleResponse(zsetExample3Api.getByScoreRange1(maxScore,minScore));
		}catch (Exception e) {
			e.printStackTrace();
			return new OrangeRedisExampleResponse(18,e.getMessage());
		}
	}
	
	@GetMapping("/v1/zsetAdvancedOperations/scoreRangeValues2")
	public OrangeRedisExampleResponse testCase18(@RequestParam Double maxScore, @RequestParam Double minScore) {
		try {
			return new OrangeRedisExampleResponse(zsetExample3Api.getByScoreRange2(maxScore,minScore));
		}catch (Exception e) {
			e.printStackTrace();
			return new OrangeRedisExampleResponse(19,e.getMessage());
		}
	}
	
	@GetMapping("/v1/zsetAdvancedOperations/scoreRangeValues3")
	public OrangeRedisExampleResponse testCase19(@RequestParam Double maxScore, @RequestParam Double minScore) {
		try {
			return new OrangeRedisExampleResponse(zsetExample3Api.getByScoreRange3(new OrangeScoreRangeEntity(maxScore,minScore)));
		}catch (Exception e) {
			e.printStackTrace();
			return new OrangeRedisExampleResponse(20,e.getMessage());
		}
	}
	
	@GetMapping("/v1/zsetAdvancedOperations/scoreRangeValuesByPage1")
	public OrangeRedisExampleResponse testCase20(@RequestParam Double maxScore,@RequestParam Double minScore,@RequestParam Long pageNo,@RequestParam Long pageSize) {
		try {
			return new OrangeRedisExampleResponse(zsetExample3Api.getByScoreRange1(maxScore,minScore,pageNo,pageSize));
		}catch (Exception e) {
			e.printStackTrace();
			return new OrangeRedisExampleResponse(21,e.getMessage());
		}
	}
	
	@GetMapping("/v1/zsetAdvancedOperations/scoreRangeValuesByPage2")
	public OrangeRedisExampleResponse testCase21(@RequestParam Double maxScore,@RequestParam Double minScore,@RequestParam Long pageNo,@RequestParam Long pageSize) {
		try {
			return new OrangeRedisExampleResponse(zsetExample3Api.getByScoreRange2(maxScore,minScore,pageNo,pageSize));
		}catch (Exception e) {
			e.printStackTrace();
			return new OrangeRedisExampleResponse(22,e.getMessage());
		}
	}
	
	@GetMapping("/v1/zsetAdvancedOperations/scoreRangeValuesByPage3")
	public OrangeRedisExampleResponse testCase22(@RequestParam Double maxScore,@RequestParam Double minScore,@RequestParam Long pageNo,@RequestParam Long pageSize) {
		try {
			return new OrangeRedisExampleResponse(zsetExample3Api.getByScoreRange3(new OrangeScoreRangeEntity(maxScore,minScore),new OrangePagerEntity(pageNo,pageSize)));
		}catch (Exception e) {
			e.printStackTrace();
			return new OrangeRedisExampleResponse(23,e.getMessage());
		}
	}
	
	@GetMapping("/v1/zsetAdvancedOperations/scoreRangeValuesByPage4")
	public OrangeRedisExampleResponse testCase23(@RequestParam Double maxScore,@RequestParam Double minScore,@RequestParam Long pageNo,@RequestParam Long pageSize) {
		try {
			return new OrangeRedisExampleResponse(zsetExample3Api.getByScoreRange4(new OrangeScoreRangeEntity(maxScore,minScore),pageNo,pageSize));
		}catch (Exception e) {
			e.printStackTrace();
			return new OrangeRedisExampleResponse(24,e.getMessage());
		}
	}
	
	@GetMapping("/v1/zsetAdvancedOperations/scoreRangeValuesByPage5")
	public OrangeRedisExampleResponse testCase24(@RequestParam Double maxScore,@RequestParam Double minScore,@RequestParam Long pageNo,@RequestParam Long pageSize) {
		try {
			return new OrangeRedisExampleResponse(zsetExample3Api.getByScoreRange5(maxScore,minScore,new OrangePagerEntity(pageNo,pageSize)));
		}catch (Exception e) {
			e.printStackTrace();
			return new OrangeRedisExampleResponse(25,e.getMessage());
		}
	}
	
	@GetMapping("/v1/zsetAdvancedOperations/rankRangeValues1")
	public OrangeRedisExampleResponse testCase25(@RequestParam Long startIndex,@RequestParam Long endIndex) {
		try {
			return new OrangeRedisExampleResponse(zsetExample3Api.getByRankRange1(startIndex,endIndex));
		}catch (Exception e) {
			e.printStackTrace();
			return new OrangeRedisExampleResponse(26,e.getMessage());
		}
	}
	
	@GetMapping("/v1/zsetAdvancedOperations/rankRangeValues2")
	public OrangeRedisExampleResponse testCase26(@RequestParam Long startIndex,@RequestParam Long endIndex) {
		try {
			return new OrangeRedisExampleResponse(zsetExample3Api.getByRankRange2(startIndex,endIndex));
		}catch (Exception e) {
			e.printStackTrace();
			return new OrangeRedisExampleResponse(27,e.getMessage());
		}
	}
	
	@GetMapping("/v1/zsetAdvancedOperations/rankRangeValues3")
	public OrangeRedisExampleResponse testCase27(@RequestParam Long startIndex,@RequestParam Long endIndex) {
		try {
			return new OrangeRedisExampleResponse(zsetExample3Api.getByRankRange3(new OrangeRankRangeEntity(startIndex,endIndex)));
		}catch (Exception e) {
			e.printStackTrace();
			return new OrangeRedisExampleResponse(28,e.getMessage());
		}
	}
	
	@GetMapping("/v1/zsetAdvancedOperations/scoreRangeMembers1")
	public OrangeRedisExampleResponse testCase28(@RequestParam Double maxScore,@RequestParam Double minScore) {
		try {
			return new OrangeRedisExampleResponse(zsetExample3Api.getByScoreRangeWithScores1(maxScore,minScore));
		}catch (Exception e) {
			e.printStackTrace();
			return new OrangeRedisExampleResponse(29,e.getMessage());
		}
	}
	
	@GetMapping("/v1/zsetAdvancedOperations/scoreRangeMembers2")
	public OrangeRedisExampleResponse testCase29(@RequestParam Double maxScore,@RequestParam Double minScore) {
		try {
			return new OrangeRedisExampleResponse(zsetExample3Api.getByScoreRangeWithScores2(new OrangeScoreRangeEntity(maxScore,minScore)));
		}catch (Exception e) {
			e.printStackTrace();
			return new OrangeRedisExampleResponse(30,e.getMessage());
		}
	}
	
	@GetMapping("/v1/zsetAdvancedOperations/scoreRangeMembersByPage1")
	public OrangeRedisExampleResponse testCase30(@RequestParam Double maxScore,@RequestParam Double minScore,@RequestParam Long pageNo,@RequestParam Long pageSize) {
		try {
			return new OrangeRedisExampleResponse(zsetExample3Api.getByScoreRangeWithScores1(maxScore,minScore,pageNo,pageSize));
		}catch (Exception e) {
			e.printStackTrace();
			return new OrangeRedisExampleResponse(31,e.getMessage());
		}
	}
	
	@GetMapping("/v1/zsetAdvancedOperations/scoreRangeMembersByPage2")
	public OrangeRedisExampleResponse testCase31(@RequestParam Double maxScore,@RequestParam Double minScore,@RequestParam Long pageNo,@RequestParam Long pageSize) {
		try {
			return new OrangeRedisExampleResponse(zsetExample3Api.getByScoreRangeWithScores2(new OrangeScoreRangeEntity(maxScore,minScore),new OrangePagerEntity(pageNo,pageSize)));
		}catch (Exception e) {
			e.printStackTrace();
			return new OrangeRedisExampleResponse(32,e.getMessage());
		}
	}
	
	@GetMapping("/v1/zsetAdvancedOperations/scoreRangeMembersByPage3")
	public OrangeRedisExampleResponse testCase32(@RequestParam Double maxScore,@RequestParam Double minScore,@RequestParam Long pageNo,@RequestParam Long pageSize) {
		try {
			return new OrangeRedisExampleResponse(zsetExample3Api.getByScoreRangeWithScores3(new OrangeScoreRangeEntity(maxScore,minScore),pageNo,pageSize));
		}catch (Exception e) {
			e.printStackTrace();
			return new OrangeRedisExampleResponse(33,e.getMessage());
		}
	}
	
	@GetMapping("/v1/zsetAdvancedOperations/scoreRangeMembersByPage4")
	public OrangeRedisExampleResponse testCase33(@RequestParam Double maxScore,@RequestParam Double minScore,@RequestParam Long pageNo,@RequestParam Long pageSize) {
		try {
			return new OrangeRedisExampleResponse(zsetExample3Api.getByScoreRangeWithScores4(maxScore,minScore,new OrangePagerEntity(pageNo,pageSize)));
		}catch (Exception e) {
			e.printStackTrace();
			return new OrangeRedisExampleResponse(34,e.getMessage());
		}
	}
	
	@GetMapping("/v1/zsetAdvancedOperations/rankRangeMembers1")
	public OrangeRedisExampleResponse testCase34(@RequestParam Long startIndex,@RequestParam Long endIndex) {
		try {
			return new OrangeRedisExampleResponse(zsetExample3Api.getByRankRangeWithScores1(startIndex,endIndex));
		}catch (Exception e) {
			e.printStackTrace();
			return new OrangeRedisExampleResponse(35,e.getMessage());
		}
	}
	
	@GetMapping("/v1/zsetAdvancedOperations/rankRangeMembers2")
	public OrangeRedisExampleResponse testCase35(@RequestParam Long startIndex,@RequestParam Long endIndex) {
		try {
			return new OrangeRedisExampleResponse(zsetExample3Api.getByRankRangeWithScores2(new OrangeRankRangeEntity(startIndex,endIndex)));
		}catch (Exception e) {
			e.printStackTrace();
			return new OrangeRedisExampleResponse(36,e.getMessage());
		}
	}
	
	@GetMapping("/v1/zsetAdvancedOperations/revesedScoreRangeValues1")
	public OrangeRedisExampleResponse testCase36(@RequestParam Double maxScore,@RequestParam Double minScore) {
		try {
			return new OrangeRedisExampleResponse(zsetExample3Api.reveseScoreRange1(maxScore,minScore));
		}catch (Exception e) {
			e.printStackTrace();
			return new OrangeRedisExampleResponse(37,e.getMessage());
		}
	}
	
	@GetMapping("/v1/zsetAdvancedOperations/revesedScoreRangeValues2")
	public OrangeRedisExampleResponse testCase38(@RequestParam Double maxScore,@RequestParam Double minScore) {
		try {
			return new OrangeRedisExampleResponse(zsetExample3Api.reveseScoreRange2(maxScore,minScore));
		}catch (Exception e) {
			e.printStackTrace();
			return new OrangeRedisExampleResponse(39,e.getMessage());
		}
	}
	
	@GetMapping("/v1/zsetAdvancedOperations/revesedScoreRangeValues3")
	public OrangeRedisExampleResponse testCase39(@RequestParam Double maxScore,@RequestParam Double minScore) {
		try {
			return new OrangeRedisExampleResponse(zsetExample3Api.reveseScoreRange3(new OrangeScoreRangeEntity(maxScore,minScore)));
		}catch (Exception e) {
			e.printStackTrace();
			return new OrangeRedisExampleResponse(40,e.getMessage());
		}
	}
	
	@GetMapping("/v1/zsetAdvancedOperations/revesedScoreRangeValuesByPage1")
	public OrangeRedisExampleResponse testCase40(@RequestParam Double maxScore,@RequestParam Double minScore,@RequestParam Long pageNo,@RequestParam Long pageSize) {
		try {
			return new OrangeRedisExampleResponse(zsetExample3Api.reveseScoreRange1(maxScore,minScore,pageNo,pageSize));
		}catch (Exception e) {
			e.printStackTrace();
			return new OrangeRedisExampleResponse(41,e.getMessage());
		}
	}
	
	@GetMapping("/v1/zsetAdvancedOperations/revesedScoreRangeValuesByPage2")
	public OrangeRedisExampleResponse testCase41(@RequestParam Double maxScore,@RequestParam Double minScore,@RequestParam Long pageNo,@RequestParam Long pageSize) {
		try {
			return new OrangeRedisExampleResponse(zsetExample3Api.reveseScoreRange2(maxScore,minScore,pageNo,pageSize));
		}catch (Exception e) {
			e.printStackTrace();
			return new OrangeRedisExampleResponse(42,e.getMessage());
		}
	}
	
	@GetMapping("/v1/zsetAdvancedOperations/revesedScoreRangeValuesByPage3")
	public OrangeRedisExampleResponse testCase42(@RequestParam Double maxScore,@RequestParam Double minScore,@RequestParam Long pageNo,@RequestParam Long pageSize) {
		try {
			return new OrangeRedisExampleResponse(zsetExample3Api.reveseScoreRange3(new OrangeScoreRangeEntity(maxScore,minScore),pageNo,pageSize));
		}catch (Exception e) {
			e.printStackTrace();
			return new OrangeRedisExampleResponse(43,e.getMessage());
		}
	}
	
	@GetMapping("/v1/zsetAdvancedOperations/revesedScoreRangeValuesByPage4")
	public OrangeRedisExampleResponse testCase43(@RequestParam Double maxScore,@RequestParam Double minScore,@RequestParam Long pageNo,@RequestParam Long pageSize) {
		try {
			return new OrangeRedisExampleResponse(zsetExample3Api.reveseScoreRange4(maxScore,minScore,new OrangePagerEntity(pageNo,pageSize)));
		}catch (Exception e) {
			e.printStackTrace();
			return new OrangeRedisExampleResponse(44,e.getMessage());
		}
	}
	
	@GetMapping("/v1/zsetAdvancedOperations/revesedScoreRangeValuesByPage5")
	public OrangeRedisExampleResponse testCase44(@RequestParam Double maxScore,@RequestParam Double minScore,@RequestParam Long pageNo,@RequestParam Long pageSize) {
		try {
			return new OrangeRedisExampleResponse(zsetExample3Api.reveseScoreRange5(new OrangeScoreRangeEntity(maxScore,minScore), new OrangePagerEntity(pageNo,pageSize)));
		}catch (Exception e) {
			e.printStackTrace();
			return new OrangeRedisExampleResponse(45,e.getMessage());
		}
	}
	
	@GetMapping("/v1/zsetAdvancedOperations/revesedRankRangeValues1")
	public OrangeRedisExampleResponse testCase45(@RequestParam Long startIndex,@RequestParam Long endIndex) {
		try {
			return new OrangeRedisExampleResponse(zsetExample3Api.reveseRankRange1(startIndex,endIndex));
		}catch (Exception e) {
			e.printStackTrace();
			return new OrangeRedisExampleResponse(46,e.getMessage());
		}
	}
	
	@GetMapping("/v1/zsetAdvancedOperations/revesedRankRangeValues2")
	public OrangeRedisExampleResponse testCase46(@RequestParam Long startIndex,@RequestParam Long endIndex) {
		try {
			return new OrangeRedisExampleResponse(zsetExample3Api.reveseRankRange2(startIndex,endIndex));
		}catch (Exception e) {
			e.printStackTrace();
			return new OrangeRedisExampleResponse(47,e.getMessage());
		}
	}
	
	@GetMapping("/v1/zsetAdvancedOperations/revesedRankRangeValues3")
	public OrangeRedisExampleResponse testCase47(@RequestParam Long startIndex,@RequestParam Long endIndex) {
		try {
			return new OrangeRedisExampleResponse(zsetExample3Api.reveseRankRange3(new OrangeRankRangeEntity(startIndex,endIndex)));
		}catch (Exception e) {
			e.printStackTrace();
			return new OrangeRedisExampleResponse(48,e.getMessage());
		}
	}
	
	@GetMapping("/v1/zsetAdvancedOperations/revesedScoreRangeMembers1")
	public OrangeRedisExampleResponse testCase48(@RequestParam Double maxScore,@RequestParam Double minScore) {
		try {
			return new OrangeRedisExampleResponse(zsetExample3Api.reveseScoreRangeWithScores1(maxScore,minScore));
		}catch (Exception e) {
			e.printStackTrace();
			return new OrangeRedisExampleResponse(49,e.getMessage());
		}
	}
	
	@GetMapping("/v1/zsetAdvancedOperations/revesedScoreRangeMembers2")
	public OrangeRedisExampleResponse testCase49(@RequestParam Double maxScore,@RequestParam Double minScore) {
		try {
			return new OrangeRedisExampleResponse(zsetExample3Api.reveseScoreRangeWithScores2(new OrangeScoreRangeEntity(maxScore,minScore)));
		}catch (Exception e) {
			e.printStackTrace();
			return new OrangeRedisExampleResponse(50,e.getMessage());
		}
	}
	
	@GetMapping("/v1/zsetAdvancedOperations/revesedScoreRangeMembersByPage1")
	public OrangeRedisExampleResponse testCase50(@RequestParam Double maxScore,@RequestParam Double minScore,@RequestParam Long pageNo,@RequestParam Long pageSize) {
		try {
			return new OrangeRedisExampleResponse(zsetExample3Api.reveseScoreRangeWithScores1(maxScore,minScore,pageNo,pageSize));
		}catch (Exception e) {
			e.printStackTrace();
			return new OrangeRedisExampleResponse(51,e.getMessage());
		}
	}
	
	@GetMapping("/v1/zsetAdvancedOperations/revesedScoreRangeMembersByPage2")
	public OrangeRedisExampleResponse testCase51(@RequestParam Double maxScore,@RequestParam Double minScore,@RequestParam Long pageNo,@RequestParam Long pageSize) {
		try {
			return new OrangeRedisExampleResponse(zsetExample3Api.reveseScoreRangeWithScores2(new OrangeScoreRangeEntity(maxScore,minScore),new OrangePagerEntity(pageNo,pageSize)));
		}catch (Exception e) {
			e.printStackTrace();
			return new OrangeRedisExampleResponse(52,e.getMessage());
		}
	}
	
	@GetMapping("/v1/zsetAdvancedOperations/revesedScoreRangeMembersByPage3")
	public OrangeRedisExampleResponse testCase52(@RequestParam Double maxScore,@RequestParam Double minScore,@RequestParam Long pageNo,@RequestParam Long pageSize) {
		try {
			return new OrangeRedisExampleResponse(zsetExample3Api.reveseScoreRangeWithScores3(new OrangeScoreRangeEntity(maxScore,minScore),pageNo,pageSize));
		}catch (Exception e) {
			e.printStackTrace();
			return new OrangeRedisExampleResponse(53,e.getMessage());
		}
	}
	
	@GetMapping("/v1/zsetAdvancedOperations/revesedScoreRangeMembersByPage4")
	public OrangeRedisExampleResponse testCase53(@RequestParam Double maxScore,@RequestParam Double minScore,@RequestParam Long pageNo,@RequestParam Long pageSize) {
		try {
			return new OrangeRedisExampleResponse(zsetExample3Api.reveseScoreRangeWithScores4(maxScore,minScore,new OrangePagerEntity(pageNo,pageSize)));
		}catch (Exception e) {
			e.printStackTrace();
			return new OrangeRedisExampleResponse(54,e.getMessage());
		}
	}
	
	@GetMapping("/v1/zsetAdvancedOperations/revesedRankRangeMembers1")
	public OrangeRedisExampleResponse testCase54(@RequestParam Long startIndex,@RequestParam Long endIndex) {
		try {
			return new OrangeRedisExampleResponse(zsetExample3Api.reveseRankRangeWithScores1(startIndex,endIndex));
		}catch (Exception e) {
			e.printStackTrace();
			return new OrangeRedisExampleResponse(55,e.getMessage());
		}
	}
	
	@GetMapping("/v1/zsetAdvancedOperations/revesedRankRangeMembers2")
	public OrangeRedisExampleResponse testCase55(@RequestParam Long startIndex,@RequestParam Long endIndex) {
		try {
			return new OrangeRedisExampleResponse(zsetExample3Api.reveseRankRangeWithScores2(new OrangeRankRangeEntity(startIndex,endIndex)));
		}catch (Exception e) {
			e.printStackTrace();
			return new OrangeRedisExampleResponse(56,e.getMessage());
		}
	}
	
	@PutMapping("/v1/zsetAdvancedOperations/scores1")
	public OrangeRedisExampleResponse testCase56(@Valid @RequestBody OrangeZSetMultipleScoreNullableExampleEntity entities) {
		try {
			return new OrangeRedisExampleResponse(zsetExample3Api.getScores1(entities.getMembers()));
		}catch (Exception e) {
			e.printStackTrace();
			return new OrangeRedisExampleResponse(57,e.getMessage());
		}
	}
	
	@PutMapping("/v1/zsetAdvancedOperations/ranks")
	public OrangeRedisExampleResponse testCase57(@Valid @RequestBody OrangeZSetMultipleScoreNullableExampleEntity entities) {
		try {
			return new OrangeRedisExampleResponse(zsetExample3Api.getRanks(entities.getMembers()));
		}catch (Exception e) {
			e.printStackTrace();
			return new OrangeRedisExampleResponse(58,e.getMessage());
		}
		
	}
	
	@PutMapping("/v1/zsetAdvancedOperations/revesedRanks")
	public OrangeRedisExampleResponse testCase58(@Valid @RequestBody OrangeZSetMultipleScoreNullableExampleEntity entities) {
		try {
			return new OrangeRedisExampleResponse(zsetExample3Api.reveseRanks(entities.getMembers()));
		}catch (Exception e) {
			e.printStackTrace();
			return new OrangeRedisExampleResponse(59,e.getMessage());
		}
	}
	
	@DeleteMapping("/v1/zsetAdvancedOperations/popMaxMember")
	public OrangeRedisExampleResponse testCase59() {
		try {
			return new OrangeRedisExampleResponse(zsetExample3Api.popMax());
		}catch (Exception e) {
			e.printStackTrace();
			return new OrangeRedisExampleResponse(60,e.getMessage());
		}
	}
	
	@DeleteMapping("/v1/zsetAdvancedOperations/popMinMember")
	public OrangeRedisExampleResponse testCase60() {
		try {
			return new OrangeRedisExampleResponse(zsetExample3Api.popMin());
		}catch (Exception e) {
			e.printStackTrace();
			return new OrangeRedisExampleResponse(61,e.getMessage());
		}
	}
	
	@DeleteMapping("/v1/zsetAdvancedOperations/popMaxMembers")
	public OrangeRedisExampleResponse testCase61(@RequestParam Integer count) {
		try {
			return new OrangeRedisExampleResponse(zsetExample3Api.popMax(count));
		}catch (Exception e) {
			e.printStackTrace();
			return new OrangeRedisExampleResponse(62,e.getMessage());
		}
	}
	
	@DeleteMapping("/v1/zsetAdvancedOperations/popMinMembers")
	public OrangeRedisExampleResponse testCase62(@RequestParam Integer count) {
		try {
			return new OrangeRedisExampleResponse(zsetExample3Api.popMin(count));
		}catch (Exception e) {
			e.printStackTrace();
			return new OrangeRedisExampleResponse(63,e.getMessage());
		}
	}
	
	@DeleteMapping("/v1/zsetAdvancedOperations/timeLimitedPopMaxMember")
	public OrangeRedisExampleResponse testCase63(@RequestParam Long seconds) {
		try {
			return new OrangeRedisExampleResponse(zsetExample3Api.timeLimitedPopMax(seconds,TimeUnit.SECONDS));
		}catch (Exception e) {
			e.printStackTrace();
			return new OrangeRedisExampleResponse(64,e.getMessage());
		}
	}
	
	@DeleteMapping("/v1/zsetAdvancedOperations/timeLimitedPopMinMember")
	public OrangeRedisExampleResponse testCase64(@RequestParam Long seconds) {
		try {
			return new OrangeRedisExampleResponse(zsetExample3Api.timeLimitedPopMin(seconds,TimeUnit.SECONDS));
		}catch (Exception e) {
			e.printStackTrace();
			return new OrangeRedisExampleResponse(65,e.getMessage());
		}
	}
	
	@PutMapping("/v1/zsetAdvancedOperations/scores2")
	public OrangeRedisExampleResponse testCase65(@Valid @RequestBody OrangeZSetMultipleScoreNullableExampleEntity entities) {
		try {
			return new OrangeRedisExampleResponse(zsetExample3Api.getScores2(entities.getMembers()));
		}catch (Exception e) {
			e.printStackTrace();
			return new OrangeRedisExampleResponse(66,e.getMessage());
		}
	}
	
	@PutMapping("/v1/zsetAdvancedOperations/scores3")
	public OrangeRedisExampleResponse testCase66(@Valid @RequestBody OrangeZSetMultipleScoreNullableExampleEntity entities) {
		try {
			return new OrangeRedisExampleResponse(zsetExample3Api.getScores3(entities.getMembers().toArray(new OrangeZSetScoreNullableExampleEntity[entities.getMembers().size()])));
		}catch (Exception e) {
			e.printStackTrace();
			return new OrangeRedisExampleResponse(67,e.getMessage());
		}
	}
}
