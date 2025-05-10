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

import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.langwuyue.orange.example.redis.api.list.OrangeRedisListExample2Api;
import com.langwuyue.orange.example.redis.response.OrangeRedisExampleResponse;

/**
 * @author Liang.Zhong
 * @since 1.0.0
 */
@RestController("listOrangeRedisStringSimpleTestCases")
public class OrangeRedisStringSimpleTestCases {
	
	@Autowired
	private OrangeRedisListExample2Api lisExample2Api;
	
	@PostMapping("/v1/listStringOperations/leftMember")
	public OrangeRedisExampleResponse testCase1(@RequestParam String member) {
		try {
			return new OrangeRedisExampleResponse(lisExample2Api.leftPush(member));
		}catch (Exception e) {
			e.printStackTrace();
			return new OrangeRedisExampleResponse(1,e.getMessage());
		}
	}
	
	@PostMapping("/v1/listStringOperations/leftMembers")
	public OrangeRedisExampleResponse testCase2(@RequestBody Set<String> members) {
		try {
			return new OrangeRedisExampleResponse(lisExample2Api.leftPush(members));
		}catch (Exception e) {
			e.printStackTrace();
			return new OrangeRedisExampleResponse(2,e.getMessage());
		}
	}
	
	@PostMapping("/v1/listStringOperations/memberOnPivotLeft")
	public OrangeRedisExampleResponse testCase3(@RequestParam String pivot,@RequestParam String member) {
		try {
			return new OrangeRedisExampleResponse(lisExample2Api.leftPush(pivot,member));
		}catch (Exception e) {
			e.printStackTrace();
			return new OrangeRedisExampleResponse(3,e.getMessage());
		}
	}
	
	@PostMapping("/v1/listStringOperations/rightMember")
	public OrangeRedisExampleResponse testCase4(@RequestParam String member) {
		try {
			return new OrangeRedisExampleResponse(lisExample2Api.rightPush(member));
		}catch (Exception e) {
			e.printStackTrace();
			return new OrangeRedisExampleResponse(4,e.getMessage());
		}
	} 
	
	@PostMapping("/v1/listStringOperations/rightMembers")
	public OrangeRedisExampleResponse testCase5(@RequestBody Set<String> members) {
		try {
			return new OrangeRedisExampleResponse(lisExample2Api.rightPush(members));
		}catch (Exception e) {
			e.printStackTrace();
			return new OrangeRedisExampleResponse(5,e.getMessage());
		}
	} 
	
	@PostMapping("/v1/listStringOperations/memberOnPivotRight")
	public OrangeRedisExampleResponse testCase6(@RequestParam String pivot,@RequestParam String member) {
		try {
			return new OrangeRedisExampleResponse(lisExample2Api.rightPush(pivot,member));
		}catch (Exception e) {
			e.printStackTrace();
			return new OrangeRedisExampleResponse(6,e.getMessage());
		}
	}
	
	@PutMapping("/v1/listStringOperations/member")
	public OrangeRedisExampleResponse testCase7(@RequestParam Long index,@RequestParam String member) {
		try {
			lisExample2Api.set(index,member);
			return new OrangeRedisExampleResponse();
		}catch (Exception e) {
			e.printStackTrace();
			return new OrangeRedisExampleResponse(7,e.getMessage());
		}
	}
	
	@PutMapping("/v1/listStringOperations/concurrentMember")
	public OrangeRedisExampleResponse testCase8(@RequestParam Long index,@RequestParam String oldMember,@RequestParam String newMember) {
		try {
			return new OrangeRedisExampleResponse(lisExample2Api.compareAndSwap(index,oldMember,newMember));
		}catch (Exception e) {
			e.printStackTrace();
			return new OrangeRedisExampleResponse(8,e.getMessage());
		}
	}
	
	@GetMapping("/v1/listStringOperations/size")
	public OrangeRedisExampleResponse testCase9() {
		try {
			return new OrangeRedisExampleResponse(lisExample2Api.getSize());
		}catch (Exception e) {
			e.printStackTrace();
			return new OrangeRedisExampleResponse(9,e.getMessage());
		}
	}
	
	@GetMapping("/v1/listStringOperations/index")
	public OrangeRedisExampleResponse testCase10(String member) {
		try {
			return new OrangeRedisExampleResponse(lisExample2Api.getIndex(member));
		}catch (Exception e) {
			e.printStackTrace();
			return new OrangeRedisExampleResponse(10,e.getMessage());
		}
	}
	
	@PutMapping("/v1/listStringOperations/indexs")
	public OrangeRedisExampleResponse testCase11(@RequestBody Set<String> members) {
		try {
			return new OrangeRedisExampleResponse(lisExample2Api.getIndex(members));
		}catch (Exception e) {
			e.printStackTrace();
			return new OrangeRedisExampleResponse(11,e.getMessage());
		}
	}
	
	@GetMapping("/v1/listStringOperations/lastIndex")
	public OrangeRedisExampleResponse testCase12(@RequestParam String member) {
		try {
			return new OrangeRedisExampleResponse(lisExample2Api.getLastIndex(member));
		}catch (Exception e) {
			e.printStackTrace();
			return new OrangeRedisExampleResponse(12,e.getMessage());
		}
	}
	
	@PutMapping("/v1/listStringOperations/lastIndexs")
	public OrangeRedisExampleResponse testCase12(@RequestBody Set<String> members) {
		try {
			return new OrangeRedisExampleResponse(lisExample2Api.getLastIndex(members));
		}catch (Exception e) {
			e.printStackTrace();
			return new OrangeRedisExampleResponse(12,e.getMessage());
		}
	}
	
	@GetMapping("/v1/listStringOperations/member")
	public OrangeRedisExampleResponse testCase13(@RequestParam Long index) {
		try {
			return new OrangeRedisExampleResponse(lisExample2Api.get(index));
		}catch (Exception e) {
			e.printStackTrace();
			return new OrangeRedisExampleResponse(13,e.getMessage());
		}
	}
	
	@GetMapping("/v1/listStringOperations/members")
	public OrangeRedisExampleResponse testCase14(@RequestParam Long startIndex,@RequestParam Long endIndex) {
		try {
			return new OrangeRedisExampleResponse(lisExample2Api.getByIndexRange(startIndex,endIndex));
		}catch (Exception e) {
			e.printStackTrace();
			return new OrangeRedisExampleResponse(14,e.getMessage());
		}
	}
	
	@DeleteMapping("/v1/listStringOperations/leftMember")
	public OrangeRedisExampleResponse testCase15() {
		try {
			return new OrangeRedisExampleResponse(lisExample2Api.leftPop());
		}catch (Exception e) {
			e.printStackTrace();
			return new OrangeRedisExampleResponse(15,e.getMessage());
		}
	}
	
	@DeleteMapping("/v1/listStringOperations/leftMembers")
	public OrangeRedisExampleResponse testCase15(@RequestParam Long count) {
		try {
			return new OrangeRedisExampleResponse(lisExample2Api.leftPop(count));
		}catch (Exception e) {
			e.printStackTrace();
			return new OrangeRedisExampleResponse(15,e.getMessage());
		}
	}
	
	@DeleteMapping("/v1/listStringOperations/leftMemberTimeLimit")
	public OrangeRedisExampleResponse testCase16(@RequestParam Long seconds) {
		try {
			return new OrangeRedisExampleResponse(lisExample2Api.leftPop(seconds,TimeUnit.SECONDS));
		}catch (Exception e) {
			e.printStackTrace();
			return new OrangeRedisExampleResponse(16,e.getMessage());
		}
	}
	
	@DeleteMapping("/v1/listStringOperations/rightMember")
	public OrangeRedisExampleResponse testCase17() {
		try {
			return new OrangeRedisExampleResponse(lisExample2Api.rightPop());
		}catch (Exception e) {
			e.printStackTrace();
			return new OrangeRedisExampleResponse(17,e.getMessage());
		}
	}
	
	@DeleteMapping("/v1/listStringOperations/rightMembers")
	public OrangeRedisExampleResponse testCase18(@RequestParam Long count) {
		try {
			return new OrangeRedisExampleResponse(lisExample2Api.rightPop(count));
		}catch (Exception e) {
			e.printStackTrace();
			return new OrangeRedisExampleResponse(18,e.getMessage());
		}
	}
	
	@DeleteMapping("/v1/listStringOperations/rightMemberTimeLimit")
	public OrangeRedisExampleResponse testCase19(@RequestParam Long seconds) {
		try {
			return new OrangeRedisExampleResponse(lisExample2Api.rightPop(seconds,TimeUnit.SECONDS));
		}catch (Exception e) {
			e.printStackTrace();
			return new OrangeRedisExampleResponse(19,e.getMessage());
		}
	}
	
	@DeleteMapping("/v1/listStringOperations/members")
	public OrangeRedisExampleResponse testCase20(@RequestParam String member, @RequestParam Long count) {
		try {
			return new OrangeRedisExampleResponse(lisExample2Api.remove(member,count));
		}catch (Exception e) {
			e.printStackTrace();
			return new OrangeRedisExampleResponse(20,e.getMessage());
		}
	}
	
	@DeleteMapping("/v1/listStringOperations/membersExcludeRankRange")
	public OrangeRedisExampleResponse testCase21(@RequestParam Long startIndex, @RequestParam Long endIndex) {
		try {
			lisExample2Api.trim(startIndex,endIndex);
			return new OrangeRedisExampleResponse();
		}catch (Exception e) {
			e.printStackTrace();
			return new OrangeRedisExampleResponse(21,e.getMessage());
		}
	}
	
	@GetMapping("/v1/listStringOperations/randomizedMember")
	public OrangeRedisExampleResponse testCase22() {
		try {
			return new OrangeRedisExampleResponse(lisExample2Api.randomOne());
		}catch (Exception e) {
			e.printStackTrace();
			return new OrangeRedisExampleResponse(22,e.getMessage());
		}
	}
	
	@GetMapping("/v1/listStringOperations/randomizedMembers")
	public OrangeRedisExampleResponse testCase23(@RequestParam Long count) {
		try {
			return new OrangeRedisExampleResponse(lisExample2Api.random(count));
		}catch (Exception e) {
			e.printStackTrace();
			return new OrangeRedisExampleResponse(23,e.getMessage());
		}
	}
	
	@GetMapping("/v1/listStringOperations/randomizedAndDistinctMembers")
	public OrangeRedisExampleResponse testCase24(@RequestParam Long count) {
		try {
			return new OrangeRedisExampleResponse(lisExample2Api.randomAndDistinct(count));
		}catch (Exception e) {
			e.printStackTrace();
			return new OrangeRedisExampleResponse(24,e.getMessage());
		}
	}
	
}
