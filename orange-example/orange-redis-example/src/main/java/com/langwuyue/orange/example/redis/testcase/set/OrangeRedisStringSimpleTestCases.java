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

import com.langwuyue.orange.example.redis.api.set.OrangeRedisSetExample2Api;
import com.langwuyue.orange.example.redis.response.OrangeRedisExampleResponse;

/**
 * @author Liang.Zhong
 * @since 1.0.0
 */
@RestController("setOrangeRedisStringSimpleTestCases")
public class OrangeRedisStringSimpleTestCases {
	
	@Autowired
	private OrangeRedisSetExample2Api setExample2Api;
	
	@PutMapping("/v1/setStringOperations/member")
	public OrangeRedisExampleResponse testCase1(String member) {
		try {
			return new OrangeRedisExampleResponse(setExample2Api.add(member));
		}catch (Exception e) {
			e.printStackTrace();
			return new OrangeRedisExampleResponse(1,e.getMessage());
		}
	}
	
	@PutMapping("/v1/setStringOperations/members")
	public OrangeRedisExampleResponse testCase2(@RequestBody Set<String> members) {
		try {
			return new OrangeRedisExampleResponse(setExample2Api.add(members));
		}catch (Exception e) {
			e.printStackTrace();
			return new OrangeRedisExampleResponse(2,e.getMessage());
		}
	}
	
	@PutMapping("/v1/setStringOperations/initMember")
	public OrangeRedisExampleResponse testCase3(String member) {
		try {
			setExample2Api.addIfAbsent(member);
			return new OrangeRedisExampleResponse();
		}catch (Exception e) {
			e.printStackTrace();
			return new OrangeRedisExampleResponse(3,e.getMessage());
		}
	}
	
	@PutMapping("/v1/setStringOperations/initMembers")
	public OrangeRedisExampleResponse testCase4(@RequestBody Set<String> members) {
		try {
			setExample2Api.addIfAbsent(members);
			return new OrangeRedisExampleResponse();
		}catch (Exception e) {
			e.printStackTrace();
			return new OrangeRedisExampleResponse(1,e.getMessage());
		}
	}
	
	@PutMapping("/v1/setStringOperations/concurrentMember")
	public OrangeRedisExampleResponse testCase5(String oldMember,String newMember) {
		try {
			return new OrangeRedisExampleResponse(setExample2Api.compareAndSwap(oldMember,newMember));
		}catch (Exception e) {
			e.printStackTrace();
			return new OrangeRedisExampleResponse(5,e.getMessage());
		}
	}
	
	@GetMapping("/v1/setStringOperations/randomizedMember")
	public OrangeRedisExampleResponse testCase6() {
		try {
			return new OrangeRedisExampleResponse(setExample2Api.randomGetOne());
		}catch (Exception e) {
			e.printStackTrace();
			return new OrangeRedisExampleResponse(6,e.getMessage());
		}
	}
	
	@GetMapping("/v1/setStringOperations/randomizedMembers")
	public OrangeRedisExampleResponse testCase7(@RequestParam Long count) {
		try {
			return new OrangeRedisExampleResponse(setExample2Api.randomGetMembers(count));
		}catch (Exception e) {
			e.printStackTrace();
			return new OrangeRedisExampleResponse(7,e.getMessage());
		}
	}
	
	@GetMapping("/v1/setStringOperations/randomizedAndDistinctMembers")
	public OrangeRedisExampleResponse testCase8(@RequestParam Long count) {
		try {
			return new OrangeRedisExampleResponse(setExample2Api.distinctRandomGetMembers(count));
		}catch (Exception e) {
			e.printStackTrace();
			return new OrangeRedisExampleResponse(8,e.getMessage());
		}
	}
	
	@GetMapping("/v1/setStringOperations/members")
	public OrangeRedisExampleResponse testCase9() {
		try {
			return new OrangeRedisExampleResponse(setExample2Api.getMembers());
		}catch (Exception e) {
			e.printStackTrace();
			return new OrangeRedisExampleResponse(9,e.getMessage());
		}
	}
	
	@GetMapping("/v1/setStringOperations/memberState")
	public OrangeRedisExampleResponse testCase10(String member) {
		try {
			return new OrangeRedisExampleResponse(setExample2Api.isMember(member));
		}catch (Exception e) {
			e.printStackTrace();
			return new OrangeRedisExampleResponse(10,e.getMessage());
		}
	}
	
	@PutMapping("/v1/setStringOperations/membersStates")
	public OrangeRedisExampleResponse testCase11(@RequestBody Set<String> members) {
		try {
			return new OrangeRedisExampleResponse(setExample2Api.isMembers(members));
		}catch (Exception e) {
			e.printStackTrace();
			return new OrangeRedisExampleResponse(11,e.getMessage());
		}
	}
	
	@DeleteMapping("/v1/setStringOperations/consumedMember")
	public OrangeRedisExampleResponse testCase12() {
		try {
			return new OrangeRedisExampleResponse(setExample2Api.pop());
		}catch (Exception e) {
			e.printStackTrace();
			return new OrangeRedisExampleResponse(12,e.getMessage());
		}
	}
	
	@DeleteMapping("/v1/setStringOperations/consumedMembers")
	public OrangeRedisExampleResponse testCase13(@RequestParam Long count) {
		try {
			return new OrangeRedisExampleResponse(setExample2Api.pop(count));
		}catch (Exception e) {
			e.printStackTrace();
			return new OrangeRedisExampleResponse(13,e.getMessage());
		}
	}
	
	@DeleteMapping("/v1/setStringOperations/member")
	public OrangeRedisExampleResponse testCase14(String member) {
		try {
			return new OrangeRedisExampleResponse(setExample2Api.remove(member));
		}catch (Exception e) {
			e.printStackTrace();
			return new OrangeRedisExampleResponse(14,e.getMessage());
		}
	}
	
	@PutMapping("/v1/setStringOperations/removedMembers")
	public OrangeRedisExampleResponse testCase15(@RequestBody Set<String> members) {
		try {
			return new OrangeRedisExampleResponse(setExample2Api.remove(members));
		}catch (Exception e) {
			e.printStackTrace();
			return new OrangeRedisExampleResponse(15,e.getMessage());
		}
	}
	
	@GetMapping("/v1/setStringOperations/size")
	public OrangeRedisExampleResponse testCase16() {
		try {
			return new OrangeRedisExampleResponse(setExample2Api.getSize());
		}catch (Exception e) {
			e.printStackTrace();
			return new OrangeRedisExampleResponse(16,e.getMessage());
		}
	}
	
	@GetMapping("/v1/setStringOperations/scanedMembers")
	public OrangeRedisExampleResponse testCase17(String pattern,Long count,Long cursor) {
		try {
			return new OrangeRedisExampleResponse(setExample2Api.scan(pattern,count,cursor));
		}catch (Exception e) {
			e.printStackTrace();
			return new OrangeRedisExampleResponse(17,e.getMessage());
		}
	}
	
	
}
