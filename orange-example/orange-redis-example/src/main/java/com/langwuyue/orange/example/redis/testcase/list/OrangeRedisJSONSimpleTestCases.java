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
package com.langwuyue.orange.example.redis.testcase.list;

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

import com.langwuyue.orange.example.redis.api.list.OrangeRedisListExample1Api;
import com.langwuyue.orange.example.redis.entity.OrangeListCompareAndSwapEntity;
import com.langwuyue.orange.example.redis.entity.OrangeListSetEntity;
import com.langwuyue.orange.example.redis.entity.OrangePivotExampleEntity;
import com.langwuyue.orange.example.redis.entity.OrangeValueExampleEntity;
import com.langwuyue.orange.example.redis.entity.OrangeValueMultipleExampleEntity;
import com.langwuyue.orange.example.redis.response.OrangeRedisExampleResponse;

/**
 * @author Liang.Zhong
 * @since 1.0.0
 */
@RestController("listOrangeRedisJSONSimpleTestCases")
public class OrangeRedisJSONSimpleTestCases {
	
	@Autowired
	private OrangeRedisListExample1Api lisExample1Api;
	
	@PostMapping("/v1/listOperations/leftMember")
	public OrangeRedisExampleResponse testCase1(@Valid @RequestBody OrangeValueExampleEntity member) {
		try {
			return new OrangeRedisExampleResponse(lisExample1Api.leftPush(member));
		}catch (Exception e) {
			e.printStackTrace();
			return new OrangeRedisExampleResponse(1,e.getMessage());
		}
	}
	
	@PostMapping("/v1/listOperations/leftMembers")
	public OrangeRedisExampleResponse testCase2(@Valid @RequestBody OrangeValueMultipleExampleEntity entity) {
		try {
			return new OrangeRedisExampleResponse(lisExample1Api.leftPush(entity.getEntities()));
		}catch (Exception e) {
			e.printStackTrace();
			return new OrangeRedisExampleResponse(2,e.getMessage());
		}
	}
	
	@PostMapping("/v1/listOperations/memberOnPivotLeft")
	public OrangeRedisExampleResponse testCase3(@Valid @RequestBody OrangePivotExampleEntity entity) {
		try {
			return new OrangeRedisExampleResponse(lisExample1Api.leftPush(entity.getPivot(),entity.getMember()));
		}catch (Exception e) {
			e.printStackTrace();
			return new OrangeRedisExampleResponse(3,e.getMessage());
		}
	}
	
	@PostMapping("/v1/listOperations/rightMember")
	public OrangeRedisExampleResponse testCase4(@Valid @RequestBody OrangeValueExampleEntity entity) {
		try {
			return new OrangeRedisExampleResponse(lisExample1Api.rightPush(entity));
		}catch (Exception e) {
			e.printStackTrace();
			return new OrangeRedisExampleResponse(4,e.getMessage());
		}
	} 
	
	@PostMapping("/v1/listOperations/rightMembers")
	public OrangeRedisExampleResponse testCase5(@Valid @RequestBody OrangeValueMultipleExampleEntity entity) {
		try {
			return new OrangeRedisExampleResponse(lisExample1Api.rightPush(entity.getEntities()));
		}catch (Exception e) {
			e.printStackTrace();
			return new OrangeRedisExampleResponse(5,e.getMessage());
		}
	} 
	
	@PostMapping("/v1/listOperations/memberOnPivotRight")
	public OrangeRedisExampleResponse testCase6(@Valid @RequestBody OrangePivotExampleEntity entity) {
		try {
			return new OrangeRedisExampleResponse(lisExample1Api.rightPush(entity.getPivot(),entity.getMember()));
		}catch (Exception e) {
			e.printStackTrace();
			return new OrangeRedisExampleResponse(6,e.getMessage());
		}
	}
	
	@PutMapping("/v1/listOperations/member")
	public OrangeRedisExampleResponse testCase7(@Valid @RequestBody OrangeListSetEntity entity) {
		try {
			lisExample1Api.set(entity.getIndex(),entity.getMember());
			return new OrangeRedisExampleResponse();
		}catch (Exception e) {
			e.printStackTrace();
			return new OrangeRedisExampleResponse(7,e.getMessage());
		}
	}
	
	@PutMapping("/v1/listOperations/concurrentMember")
	public OrangeRedisExampleResponse testCase8(@Valid @RequestBody OrangeListCompareAndSwapEntity entity) {
		try {
			return new OrangeRedisExampleResponse(lisExample1Api.compareAndSwap(entity.getIndex(),entity.getOldValue(),entity.getNewValue()));
		}catch (Exception e) {
			e.printStackTrace();
			return new OrangeRedisExampleResponse(8,e.getMessage());
		}
	}
	
	@GetMapping("/v1/listOperations/size")
	public OrangeRedisExampleResponse testCase9() {
		try {
			return new OrangeRedisExampleResponse(lisExample1Api.getSize());
		}catch (Exception e) {
			e.printStackTrace();
			return new OrangeRedisExampleResponse(9,e.getMessage());
		}
	}
	
	@GetMapping("/v1/listOperations/index")
	public OrangeRedisExampleResponse testCase10(@Valid OrangeValueExampleEntity entity) {
		try {
			return new OrangeRedisExampleResponse(lisExample1Api.getIndex(entity));
		}catch (Exception e) {
			e.printStackTrace();
			return new OrangeRedisExampleResponse(10,e.getMessage());
		}
	}
	
	@PutMapping("/v1/listOperations/indexs")
	public OrangeRedisExampleResponse testCase11(@Valid @RequestBody OrangeValueMultipleExampleEntity entity) {
		try {
			return new OrangeRedisExampleResponse(lisExample1Api.getIndex(entity.getEntities()));
		}catch (Exception e) {
			e.printStackTrace();
			return new OrangeRedisExampleResponse(11,e.getMessage());
		}
	}
	
	@GetMapping("/v1/listOperations/lastIndex")
	public OrangeRedisExampleResponse testCase12(@Valid OrangeValueExampleEntity entity) {
		try {
			return new OrangeRedisExampleResponse(lisExample1Api.getLastIndex(entity));
		}catch (Exception e) {
			e.printStackTrace();
			return new OrangeRedisExampleResponse(12,e.getMessage());
		}
	}
	
	@PutMapping("/v1/listOperations/lastIndexs")
	public OrangeRedisExampleResponse testCase12(@Valid @RequestBody OrangeValueMultipleExampleEntity entity) {
		try {
			return new OrangeRedisExampleResponse(lisExample1Api.getLastIndex(entity.getEntities()));
		}catch (Exception e) {
			e.printStackTrace();
			return new OrangeRedisExampleResponse(12,e.getMessage());
		}
	}
	
	@GetMapping("/v1/listOperations/member")
	public OrangeRedisExampleResponse testCase13(@RequestParam Long index) {
		try {
			return new OrangeRedisExampleResponse(lisExample1Api.get(index));
		}catch (Exception e) {
			e.printStackTrace();
			return new OrangeRedisExampleResponse(13,e.getMessage());
		}
	}
	
	@GetMapping("/v1/listOperations/members")
	public OrangeRedisExampleResponse testCase14(@RequestParam Long startIndex,@RequestParam Long endIndex) {
		try {
			return new OrangeRedisExampleResponse(lisExample1Api.getByIndexRange(startIndex,endIndex));
		}catch (Exception e) {
			e.printStackTrace();
			return new OrangeRedisExampleResponse(14,e.getMessage());
		}
	}
	
	@DeleteMapping("/v1/listOperations/leftMember")
	public OrangeRedisExampleResponse testCase15() {
		try {
			return new OrangeRedisExampleResponse(lisExample1Api.leftPop());
		}catch (Exception e) {
			e.printStackTrace();
			return new OrangeRedisExampleResponse(15,e.getMessage());
		}
	}
	
	@DeleteMapping("/v1/listOperations/leftMembers")
	public OrangeRedisExampleResponse testCase15(@RequestParam Long count) {
		try {
			return new OrangeRedisExampleResponse(lisExample1Api.leftPop(count));
		}catch (Exception e) {
			e.printStackTrace();
			return new OrangeRedisExampleResponse(15,e.getMessage());
		}
	}
	
	@DeleteMapping("/v1/listOperations/leftMemberTimeLimit")
	public OrangeRedisExampleResponse testCase16(@RequestParam Long seconds) {
		try {
			return new OrangeRedisExampleResponse(lisExample1Api.leftPop(seconds,TimeUnit.SECONDS));
		}catch (Exception e) {
			e.printStackTrace();
			return new OrangeRedisExampleResponse(16,e.getMessage());
		}
	}
	
	@DeleteMapping("/v1/listOperations/rightMember")
	public OrangeRedisExampleResponse testCase17() {
		try {
			return new OrangeRedisExampleResponse(lisExample1Api.rightPop());
		}catch (Exception e) {
			e.printStackTrace();
			return new OrangeRedisExampleResponse(17,e.getMessage());
		}
	}
	
	@DeleteMapping("/v1/listOperations/rightMembers")
	public OrangeRedisExampleResponse testCase18(@RequestParam Long count) {
		try {
			return new OrangeRedisExampleResponse(lisExample1Api.rightPop(count));
		}catch (Exception e) {
			e.printStackTrace();
			return new OrangeRedisExampleResponse(18,e.getMessage());
		}
	}
	
	@DeleteMapping("/v1/listOperations/rightMemberTimeLimit")
	public OrangeRedisExampleResponse testCase19(@RequestParam Long seconds) {
		try {
			return new OrangeRedisExampleResponse(lisExample1Api.rightPop(seconds,TimeUnit.SECONDS));
		}catch (Exception e) {
			e.printStackTrace();
			return new OrangeRedisExampleResponse(19,e.getMessage());
		}
	}
	
	@DeleteMapping("/v1/listOperations/members")
	public OrangeRedisExampleResponse testCase20(@RequestParam Long id, @RequestParam String text, @RequestParam Long count) {
		try {
			OrangeValueExampleEntity entity = new OrangeValueExampleEntity();
			entity.setId(id);
			entity.setText(text);
			return new OrangeRedisExampleResponse(lisExample1Api.remove(entity,count));
		}catch (Exception e) {
			e.printStackTrace();
			return new OrangeRedisExampleResponse(20,e.getMessage());
		}
	}
	
	@DeleteMapping("/v1/listOperations/membersExcludeRankRange")
	public OrangeRedisExampleResponse testCase21(@RequestParam Long startIndex, @RequestParam Long endIndex) {
		try {
			lisExample1Api.trim(startIndex,endIndex);
			return new OrangeRedisExampleResponse();
		}catch (Exception e) {
			e.printStackTrace();
			return new OrangeRedisExampleResponse(21,e.getMessage());
		}
	}
	
	@GetMapping("/v1/listOperations/randomizedMember")
	public OrangeRedisExampleResponse testCase22() {
		try {
			return new OrangeRedisExampleResponse(lisExample1Api.randomOne());
		}catch (Exception e) {
			e.printStackTrace();
			return new OrangeRedisExampleResponse(22,e.getMessage());
		}
	}
	
	@GetMapping("/v1/listOperations/randomizedMembers")
	public OrangeRedisExampleResponse testCase23(@RequestParam Long count) {
		try {
			return new OrangeRedisExampleResponse(lisExample1Api.random(count));
		}catch (Exception e) {
			e.printStackTrace();
			return new OrangeRedisExampleResponse(23,e.getMessage());
		}
	}
	
	@GetMapping("/v1/listOperations/randomizedAndDistinctMembers")
	public OrangeRedisExampleResponse testCase24(@RequestParam Long count) {
		try {
			return new OrangeRedisExampleResponse(lisExample1Api.randomAndDistinct(count));
		}catch (Exception e) {
			e.printStackTrace();
			return new OrangeRedisExampleResponse(24,e.getMessage());
		}
	}
	
}
