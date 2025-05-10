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

import com.langwuyue.orange.example.redis.api.list.OrangeRedisListExample3Api;
import com.langwuyue.orange.example.redis.response.OrangeRedisExampleResponse;

/**
 * @author Liang.Zhong
 * @since 1.0.0
 */
@RestController("listOrangeRedisDynamicKeySimpleTestCases")
public class OrangeRedisDynamicKeySimpleTestCases {
	
	@Autowired
	private OrangeRedisListExample3Api lisExample3Api;
	
	@PostMapping("/v1/listDynamicOperations/leftMember")
	public OrangeRedisExampleResponse testCase1(@RequestParam String member,@RequestParam String keyVar) {
		try {
			return new OrangeRedisExampleResponse(lisExample3Api.leftPush(keyVar,member));
		}catch (Exception e) {
			e.printStackTrace();
			return new OrangeRedisExampleResponse(1,e.getMessage());
		}
	}
	
	@PostMapping("/v1/listDynamicOperations/leftMembers")
	public OrangeRedisExampleResponse testCase2(@RequestBody Set<String> members,@RequestParam String keyVar) {
		try {
			return new OrangeRedisExampleResponse(lisExample3Api.leftPush(keyVar,members));
		}catch (Exception e) {
			e.printStackTrace();
			return new OrangeRedisExampleResponse(2,e.getMessage());
		}
	}
	
	@PostMapping("/v1/listDynamicOperations/memberOnPivotLeft")
	public OrangeRedisExampleResponse testCase3(@RequestParam String pivot,@RequestParam String member,@RequestParam String keyVar) {
		try {
			return new OrangeRedisExampleResponse(lisExample3Api.leftPush(keyVar,pivot,member));
		}catch (Exception e) {
			e.printStackTrace();
			return new OrangeRedisExampleResponse(3,e.getMessage());
		}
	}
	
	@PostMapping("/v1/listDynamicOperations/rightMember")
	public OrangeRedisExampleResponse testCase4(@RequestParam String member,@RequestParam String keyVar) {
		try {
			return new OrangeRedisExampleResponse(lisExample3Api.rightPush(keyVar,member));
		}catch (Exception e) {
			e.printStackTrace();
			return new OrangeRedisExampleResponse(4,e.getMessage());
		}
	} 
	
	@PostMapping("/v1/listDynamicOperations/rightMembers")
	public OrangeRedisExampleResponse testCase5(@RequestBody Set<String> members,@RequestParam String keyVar) {
		try {
			return new OrangeRedisExampleResponse(lisExample3Api.rightPush(keyVar,members));
		}catch (Exception e) {
			e.printStackTrace();
			return new OrangeRedisExampleResponse(5,e.getMessage());
		}
	} 
	
	@PostMapping("/v1/listDynamicOperations/memberOnPivotRight")
	public OrangeRedisExampleResponse testCase6(@RequestParam String pivot,@RequestParam String member,@RequestParam String keyVar) {
		try {
			return new OrangeRedisExampleResponse(lisExample3Api.rightPush(keyVar,pivot,member));
		}catch (Exception e) {
			e.printStackTrace();
			return new OrangeRedisExampleResponse(6,e.getMessage());
		}
	}
	
	@PutMapping("/v1/listDynamicOperations/member")
	public OrangeRedisExampleResponse testCase7(@RequestParam Long index,@RequestParam String member,@RequestParam String keyVar) {
		try {
			lisExample3Api.set(keyVar,index,member);
			return new OrangeRedisExampleResponse();
		}catch (Exception e) {
			e.printStackTrace();
			return new OrangeRedisExampleResponse(7,e.getMessage());
		}
	}
	
	@PutMapping("/v1/listDynamicOperations/concurrentMember")
	public OrangeRedisExampleResponse testCase8(@RequestParam Long index,@RequestParam String oldMember,@RequestParam String newMember,@RequestParam String keyVar) {
		try {
			return new OrangeRedisExampleResponse(lisExample3Api.compareAndSwap(keyVar,index,oldMember,newMember));
		}catch (Exception e) {
			e.printStackTrace();
			return new OrangeRedisExampleResponse(8,e.getMessage());
		}
	}
	
	@GetMapping("/v1/listDynamicOperations/size")
	public OrangeRedisExampleResponse testCase9(@RequestParam String keyVar) {
		try {
			return new OrangeRedisExampleResponse(lisExample3Api.getSize(keyVar));
		}catch (Exception e) {
			e.printStackTrace();
			return new OrangeRedisExampleResponse(9,e.getMessage());
		}
	}
	
	@GetMapping("/v1/listDynamicOperations/index")
	public OrangeRedisExampleResponse testCase10(String member,@RequestParam String keyVar) {
		try {
			return new OrangeRedisExampleResponse(lisExample3Api.getIndex(keyVar,member));
		}catch (Exception e) {
			e.printStackTrace();
			return new OrangeRedisExampleResponse(10,e.getMessage());
		}
	}
	
	@PutMapping("/v1/listDynamicOperations/indexs")
	public OrangeRedisExampleResponse testCase11(@RequestBody Set<String> members,@RequestParam String keyVar) {
		try {
			return new OrangeRedisExampleResponse(lisExample3Api.getIndex(keyVar,members));
		}catch (Exception e) {
			e.printStackTrace();
			return new OrangeRedisExampleResponse(11,e.getMessage());
		}
	}
	
	@GetMapping("/v1/listDynamicOperations/lastIndex")
	public OrangeRedisExampleResponse testCase12(@RequestParam String member,@RequestParam String keyVar) {
		try {
			return new OrangeRedisExampleResponse(lisExample3Api.getLastIndex(keyVar,member));
		}catch (Exception e) {
			e.printStackTrace();
			return new OrangeRedisExampleResponse(12,e.getMessage());
		}
	}
	
	@PutMapping("/v1/listDynamicOperations/lastIndexs")
	public OrangeRedisExampleResponse testCase12(@RequestBody Set<String> members,@RequestParam String keyVar) {
		try {
			return new OrangeRedisExampleResponse(lisExample3Api.getLastIndex(keyVar,members));
		}catch (Exception e) {
			e.printStackTrace();
			return new OrangeRedisExampleResponse(12,e.getMessage());
		}
	}
	
	@GetMapping("/v1/listDynamicOperations/member")
	public OrangeRedisExampleResponse testCase13(@RequestParam Long index,@RequestParam String keyVar) {
		try {
			return new OrangeRedisExampleResponse(lisExample3Api.get(keyVar,index));
		}catch (Exception e) {
			e.printStackTrace();
			return new OrangeRedisExampleResponse(13,e.getMessage());
		}
	}
	
	@GetMapping("/v1/listDynamicOperations/members")
	public OrangeRedisExampleResponse testCase14(@RequestParam Long startIndex,@RequestParam Long endIndex,@RequestParam String keyVar) {
		try {
			return new OrangeRedisExampleResponse(lisExample3Api.getByIndexRange(keyVar,startIndex,endIndex));
		}catch (Exception e) {
			e.printStackTrace();
			return new OrangeRedisExampleResponse(14,e.getMessage());
		}
	}
	
	@DeleteMapping("/v1/listDynamicOperations/leftMember")
	public OrangeRedisExampleResponse testCase15(@RequestParam String keyVar) {
		try {
			return new OrangeRedisExampleResponse(lisExample3Api.leftPop(keyVar));
		}catch (Exception e) {
			e.printStackTrace();
			return new OrangeRedisExampleResponse(15,e.getMessage());
		}
	}
	
	@DeleteMapping("/v1/listDynamicOperations/leftMembers")
	public OrangeRedisExampleResponse testCase15(@RequestParam Long count,@RequestParam String keyVar) {
		try {
			return new OrangeRedisExampleResponse(lisExample3Api.leftPop(keyVar,count));
		}catch (Exception e) {
			e.printStackTrace();
			return new OrangeRedisExampleResponse(15,e.getMessage());
		}
	}
	
	@DeleteMapping("/v1/listDynamicOperations/leftMemberTimeLimit")
	public OrangeRedisExampleResponse testCase16(@RequestParam Long seconds,@RequestParam String keyVar) {
		try {
			return new OrangeRedisExampleResponse(lisExample3Api.leftPop(keyVar,seconds,TimeUnit.SECONDS));
		}catch (Exception e) {
			e.printStackTrace();
			return new OrangeRedisExampleResponse(16,e.getMessage());
		}
	}
	
	@DeleteMapping("/v1/listDynamicOperations/rightMember")
	public OrangeRedisExampleResponse testCase17(@RequestParam String keyVar) {
		try {
			return new OrangeRedisExampleResponse(lisExample3Api.rightPop(keyVar));
		}catch (Exception e) {
			e.printStackTrace();
			return new OrangeRedisExampleResponse(17,e.getMessage());
		}
	}
	
	@DeleteMapping("/v1/listDynamicOperations/rightMembers")
	public OrangeRedisExampleResponse testCase18(@RequestParam Long count,@RequestParam String keyVar) {
		try {
			return new OrangeRedisExampleResponse(lisExample3Api.rightPop(keyVar,count));
		}catch (Exception e) {
			e.printStackTrace();
			return new OrangeRedisExampleResponse(18,e.getMessage());
		}
	}
	
	@DeleteMapping("/v1/listDynamicOperations/rightMemberTimeLimit")
	public OrangeRedisExampleResponse testCase19(@RequestParam Long seconds,@RequestParam String keyVar) {
		try {
			return new OrangeRedisExampleResponse(lisExample3Api.rightPop(keyVar,seconds,TimeUnit.SECONDS));
		}catch (Exception e) {
			e.printStackTrace();
			return new OrangeRedisExampleResponse(19,e.getMessage());
		}
	}
	
	@DeleteMapping("/v1/listDynamicOperations/members")
	public OrangeRedisExampleResponse testCase20(@RequestParam String member, @RequestParam Long count,@RequestParam String keyVar) {
		try {
			return new OrangeRedisExampleResponse(lisExample3Api.remove(keyVar,member,count));
		}catch (Exception e) {
			e.printStackTrace();
			return new OrangeRedisExampleResponse(20,e.getMessage());
		}
	}
	
	@DeleteMapping("/v1/listDynamicOperations/membersExcludeRankRange")
	public OrangeRedisExampleResponse testCase21(@RequestParam Long startIndex, @RequestParam Long endIndex,@RequestParam String keyVar) {
		try {
			lisExample3Api.trim(keyVar,startIndex,endIndex);
			return new OrangeRedisExampleResponse();
		}catch (Exception e) {
			e.printStackTrace();
			return new OrangeRedisExampleResponse(21,e.getMessage());
		}
	}
	
	@GetMapping("/v1/listDynamicOperations/randomizedMember")
	public OrangeRedisExampleResponse testCase22(@RequestParam String keyVar) {
		try {
			return new OrangeRedisExampleResponse(lisExample3Api.randomOne(keyVar));
		}catch (Exception e) {
			e.printStackTrace();
			return new OrangeRedisExampleResponse(22,e.getMessage());
		}
	}
	
	@GetMapping("/v1/listDynamicOperations/randomizedMembers")
	public OrangeRedisExampleResponse testCase23(@RequestParam Long count,@RequestParam String keyVar) {
		try {
			return new OrangeRedisExampleResponse(lisExample3Api.random(keyVar,count));
		}catch (Exception e) {
			e.printStackTrace();
			return new OrangeRedisExampleResponse(23,e.getMessage());
		}
	}
	
	@GetMapping("/v1/listDynamicOperations/randomizedAndDistinctMembers")
	public OrangeRedisExampleResponse testCase24(@RequestParam Long count,@RequestParam String keyVar) {
		try {
			return new OrangeRedisExampleResponse(lisExample3Api.randomAndDistinct(keyVar,count));
		}catch (Exception e) {
			e.printStackTrace();
			return new OrangeRedisExampleResponse(24,e.getMessage());
		}
	}
	
}
