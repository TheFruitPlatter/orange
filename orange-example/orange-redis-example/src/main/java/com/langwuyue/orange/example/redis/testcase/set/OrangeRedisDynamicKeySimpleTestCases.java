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
package com.langwuyue.orange.example.redis.testcase.set;

import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.langwuyue.orange.example.redis.api.set.OrangeRedisSetExample3Api;
import com.langwuyue.orange.example.redis.response.OrangeRedisExampleResponse;

/**
 * @author Liang.Zhong
 * @since 1.0.0
 */
@RestController("setOrangeRedisDynamicKeySimpleTestCases")
public class OrangeRedisDynamicKeySimpleTestCases {
	
	@Autowired
	private OrangeRedisSetExample3Api setExample3Api;
	
	@PutMapping("/v1/setDynamicOperations/member")
	public OrangeRedisExampleResponse testCase1(String member,String keyVar) {
		try {
			return new OrangeRedisExampleResponse(setExample3Api.add(keyVar,member));
		}catch (Exception e) {
			e.printStackTrace();
			return new OrangeRedisExampleResponse(1,e.getMessage());
		}
	}
	
	@PutMapping("/v1/setDynamicOperations/members")
	public OrangeRedisExampleResponse testCase2(@RequestBody Set<String> members,String keyVar) {
		try {
			return new OrangeRedisExampleResponse(setExample3Api.add(keyVar,members));
		}catch (Exception e) {
			e.printStackTrace();
			return new OrangeRedisExampleResponse(2,e.getMessage());
		}
	}
	
	@PutMapping("/v1/setDynamicOperations/initMember")
	public OrangeRedisExampleResponse testCase3(String member,String keyVar) {
		try {
			setExample3Api.addIfAbsent(keyVar,member);
			return new OrangeRedisExampleResponse();
		}catch (Exception e) {
			e.printStackTrace();
			return new OrangeRedisExampleResponse(3,e.getMessage());
		}
	}
	
	@PutMapping("/v1/setDynamicOperations/initMembers")
	public OrangeRedisExampleResponse testCase4(@RequestBody Set<String> members,String keyVar) {
		try {
			setExample3Api.addIfAbsent(keyVar,members);
			return new OrangeRedisExampleResponse();
		}catch (Exception e) {
			e.printStackTrace();
			return new OrangeRedisExampleResponse(1,e.getMessage());
		}
	}
	
	@PutMapping("/v1/setDynamicOperations/concurrentMember")
	public OrangeRedisExampleResponse testCase5(String oldMember,String newMember,String keyVar) {
		try {
			return new OrangeRedisExampleResponse(setExample3Api.compareAndSwap(keyVar,oldMember,newMember));
		}catch (Exception e) {
			e.printStackTrace();
			return new OrangeRedisExampleResponse(5,e.getMessage());
		}
	}
	
	@GetMapping("/v1/setDynamicOperations/randomizedMember")
	public OrangeRedisExampleResponse testCase6(String keyVar) {
		try {
			return new OrangeRedisExampleResponse(setExample3Api.randomGetOne(keyVar));
		}catch (Exception e) {
			e.printStackTrace();
			return new OrangeRedisExampleResponse(6,e.getMessage());
		}
	}
	
	@GetMapping("/v1/setDynamicOperations/randomizedMembers")
	public OrangeRedisExampleResponse testCase7(@RequestParam Long count,String keyVar) {
		try {
			return new OrangeRedisExampleResponse(setExample3Api.randomGetMembers(keyVar,count));
		}catch (Exception e) {
			e.printStackTrace();
			return new OrangeRedisExampleResponse(7,e.getMessage());
		}
	}
	
	@GetMapping("/v1/setDynamicOperations/randomizedAndDistinctMembers")
	public OrangeRedisExampleResponse testCase8(@RequestParam Long count,String keyVar) {
		try {
			return new OrangeRedisExampleResponse(setExample3Api.distinctRandomGetMembers(keyVar,count));
		}catch (Exception e) {
			e.printStackTrace();
			return new OrangeRedisExampleResponse(8,e.getMessage());
		}
	}
	
	@GetMapping("/v1/setDynamicOperations/members")
	public OrangeRedisExampleResponse testCase9(String keyVar) {
		try {
			return new OrangeRedisExampleResponse(setExample3Api.getMembers(keyVar));
		}catch (Exception e) {
			e.printStackTrace();
			return new OrangeRedisExampleResponse(9,e.getMessage());
		}
	}
	
	@GetMapping("/v1/setDynamicOperations/memberState")
	public OrangeRedisExampleResponse testCase10(String member,String keyVar) {
		try {
			return new OrangeRedisExampleResponse(setExample3Api.isMember(keyVar,member));
		}catch (Exception e) {
			e.printStackTrace();
			return new OrangeRedisExampleResponse(10,e.getMessage());
		}
	}
	
	@PutMapping("/v1/setDynamicOperations/membersStates")
	public OrangeRedisExampleResponse testCase11(@RequestBody Set<String> members,String keyVar) {
		try {
			return new OrangeRedisExampleResponse(setExample3Api.isMembers(keyVar,members));
		}catch (Exception e) {
			e.printStackTrace();
			return new OrangeRedisExampleResponse(11,e.getMessage());
		}
	}
	
	@DeleteMapping("/v1/setDynamicOperations/consumedMember")
	public OrangeRedisExampleResponse testCase12(String keyVar) {
		try {
			return new OrangeRedisExampleResponse(setExample3Api.pop(keyVar));
		}catch (Exception e) {
			e.printStackTrace();
			return new OrangeRedisExampleResponse(12,e.getMessage());
		}
	}
	
	@DeleteMapping("/v1/setDynamicOperations/consumedMembers")
	public OrangeRedisExampleResponse testCase13(@RequestParam Long count,String keyVar) {
		try {
			return new OrangeRedisExampleResponse(setExample3Api.pop(keyVar,count));
		}catch (Exception e) {
			e.printStackTrace();
			return new OrangeRedisExampleResponse(13,e.getMessage());
		}
	}
	
	@DeleteMapping("/v1/setDynamicOperations/member")
	public OrangeRedisExampleResponse testCase14(String member,String keyVar) {
		try {
			return new OrangeRedisExampleResponse(setExample3Api.remove(keyVar,member));
		}catch (Exception e) {
			e.printStackTrace();
			return new OrangeRedisExampleResponse(14,e.getMessage());
		}
	}
	
	@PutMapping("/v1/setDynamicOperations/removedMembers")
	public OrangeRedisExampleResponse testCase15(@RequestBody Set<String> members,String keyVar) {
		try {
			return new OrangeRedisExampleResponse(setExample3Api.remove(keyVar,members));
		}catch (Exception e) {
			e.printStackTrace();
			return new OrangeRedisExampleResponse(15,e.getMessage());
		}
	}
	
	@GetMapping("/v1/setDynamicOperations/size")
	public OrangeRedisExampleResponse testCase16(String keyVar) {
		try {
			return new OrangeRedisExampleResponse(setExample3Api.getSize(keyVar));
		}catch (Exception e) {
			e.printStackTrace();
			return new OrangeRedisExampleResponse(16,e.getMessage());
		}
	}
	
	@GetMapping("/v1/setDynamicOperations/scanedMembers")
	public OrangeRedisExampleResponse testCase18(String pattern,Long count,Long nextCursor,String keyVar) {
		try {
			return new OrangeRedisExampleResponse(setExample3Api.scan(keyVar,pattern,count,nextCursor));
		}catch (Exception e) {
			e.printStackTrace();
			return new OrangeRedisExampleResponse(18,e.getMessage());
		}
	}
	
}
