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
package com.langwuyue.orange.example.redis.testcase.hash;

import java.util.List;
import java.util.stream.Collectors;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.langwuyue.orange.example.redis.api.hash.OrangeRedisHashExample1Api;
import com.langwuyue.orange.example.redis.entity.OrangeHashCompareAndSwapEntity;
import com.langwuyue.orange.example.redis.entity.OrangeHashExampleEntity;
import com.langwuyue.orange.example.redis.entity.OrangeHashMultipleEntity;
import com.langwuyue.orange.example.redis.response.OrangeRedisExampleResponse;

/**
 * @author Liang.Zhong
 * @since 1.0.0
 */
@RestController("hashOrangeRedisJSONSimpleTestCases")
public class OrangeRedisJSONSimpleTestCases {
	
	@Autowired
	private OrangeRedisHashExample1Api hashExample1Api;
	
	@PutMapping("/v1/hashOperations/member")
	public OrangeRedisExampleResponse testCase1(@Valid @RequestBody OrangeHashExampleEntity entity) {
		try {
			hashExample1Api.add(entity.getKey(),entity.getValue());
			return new OrangeRedisExampleResponse();
		}catch (Exception e) {
			e.printStackTrace();
			return new OrangeRedisExampleResponse(1,e.getMessage());
		}
	}
	
	@PutMapping("/v1/hashOperations/members")
	public OrangeRedisExampleResponse testCase2(@Valid @RequestBody OrangeHashMultipleEntity arg) {
		try {
			hashExample1Api.add(arg.getEntities().stream().collect(Collectors.toMap(OrangeHashExampleEntity::getKey, OrangeHashExampleEntity::getValue)));
			return new OrangeRedisExampleResponse();
		}catch (Exception e) {
			e.printStackTrace();
			return new OrangeRedisExampleResponse(2,e.getMessage());
		}
	}
	
	@PutMapping("/v1/hashOperations/initMember")
	public OrangeRedisExampleResponse testCase3(@Valid @RequestBody OrangeHashExampleEntity arg) {
		try {
			hashExample1Api.addIfAbsent(arg.getKey(),arg.getValue());
			return new OrangeRedisExampleResponse();
		}catch (Exception e) {
			e.printStackTrace();
			return new OrangeRedisExampleResponse(3,e.getMessage());
		}
	}
	
	@PutMapping("/v1/hashOperations/initMembers")
	public OrangeRedisExampleResponse testCase4(@Valid @RequestBody OrangeHashMultipleEntity arg) {
		try {
			hashExample1Api.addIfAbsent(arg.getEntities().stream().collect(Collectors.toMap(OrangeHashExampleEntity::getKey, OrangeHashExampleEntity::getValue)));
			return new OrangeRedisExampleResponse();
		}catch (Exception e) {
			e.printStackTrace();
			return new OrangeRedisExampleResponse(4,e.getMessage());
		}
	}
	
	@PutMapping("/v1/hashOperations/concurrentMember")
	public OrangeRedisExampleResponse testCase5(@Valid @RequestBody OrangeHashCompareAndSwapEntity arg) {
		try {
			return new OrangeRedisExampleResponse(hashExample1Api.compareAndSwap(arg.getKey(),arg.getOldValue(),arg.getNewValue()));
		}catch (Exception e) {
			e.printStackTrace();
			return new OrangeRedisExampleResponse(5,e.getMessage());
		}
	}
	
	@GetMapping("/v1/hashOperations/size")
	public OrangeRedisExampleResponse testCase7() {
		try {
			return new OrangeRedisExampleResponse(hashExample1Api.getSize());
		}catch (Exception e) {
			e.printStackTrace();
			return new OrangeRedisExampleResponse(7,e.getMessage());
		}
	}
	
	@GetMapping("/v1/hashOperations/valueLength")
	public OrangeRedisExampleResponse testCase8(String hashKey) {
		try {
			return new OrangeRedisExampleResponse(hashExample1Api.getHashValueBytesLength(hashKey));
		}catch (Exception e) {
			e.printStackTrace();
			return new OrangeRedisExampleResponse(8,e.getMessage());
		}
	}
	
	@GetMapping("/v1/hashOperations/keys")
	public OrangeRedisExampleResponse testCase9() {
		try {
			return new OrangeRedisExampleResponse(hashExample1Api.getAllKeys());
		}catch (Exception e) {
			e.printStackTrace();
			return new OrangeRedisExampleResponse(9,e.getMessage());
		}
	}
	
	@GetMapping("/v1/hashOperations/randomizedKey")
	public OrangeRedisExampleResponse testCase10() {
		try {
			return new OrangeRedisExampleResponse(hashExample1Api.randomKey());
		}catch (Exception e) {
			e.printStackTrace();
			return new OrangeRedisExampleResponse(10,e.getMessage());
		}
	}
	
	@GetMapping("/v1/hashOperations/randomizedKeys")
	public OrangeRedisExampleResponse testCase11(@RequestParam Long count) {
		try {
			return new OrangeRedisExampleResponse(hashExample1Api.randomKeys(count));
		}catch (Exception e) {
			e.printStackTrace();
			return new OrangeRedisExampleResponse(11,e.getMessage());
		}
	}
	
	@GetMapping("/v1/hashOperations/existsKey")
	public OrangeRedisExampleResponse testCase12(@RequestParam String hashKey) {
		try {
			return new OrangeRedisExampleResponse(hashExample1Api.hasKey(hashKey));
		}catch (Exception e) {
			e.printStackTrace();
			return new OrangeRedisExampleResponse(12,e.getMessage());
		}
	}
	
	@PutMapping("/v1/hashOperations/existsKeys")
	public OrangeRedisExampleResponse testCase13(@RequestBody List<String> hashKeys) {
		try {
			return new OrangeRedisExampleResponse(hashExample1Api.hasKeys(hashKeys));
		}catch (Exception e) {
			e.printStackTrace();
			return new OrangeRedisExampleResponse(13,e.getMessage());
		}
	}
	
	@GetMapping("/v1/hashOperations/allValues")
	public OrangeRedisExampleResponse testCase14() {
		try {
			return new OrangeRedisExampleResponse(hashExample1Api.getValues());
		}catch (Exception e) {
			e.printStackTrace();
			return new OrangeRedisExampleResponse(14,e.getMessage());
		}
	}
	
	@GetMapping("/v1/hashOperations/value")
	public OrangeRedisExampleResponse testCase15(@RequestParam String hashKey) {
		try {
			return new OrangeRedisExampleResponse(hashExample1Api.get(hashKey));
		}catch (Exception e) {
			e.printStackTrace();
			return new OrangeRedisExampleResponse(15,e.getMessage());
		}
	}
	
	@PutMapping("/v1/hashOperations/values")
	public OrangeRedisExampleResponse testCase16(@RequestBody List<String> hashKeys) {
		try {
			return new OrangeRedisExampleResponse(hashExample1Api.get(hashKeys));
		}catch (Exception e) {
			e.printStackTrace();
			return new OrangeRedisExampleResponse(16,e.getMessage());
		}
	}
	
	@GetMapping("/v1/hashOperations/allMembers")
	public OrangeRedisExampleResponse testCase17() {
		try {
			return new OrangeRedisExampleResponse(hashExample1Api.getAllMembers());
		}catch (Exception e) {
			e.printStackTrace();
			return new OrangeRedisExampleResponse(17,e.getMessage());
		}
	}
	
//  java.lang.UnsupportedOperationException	
//  java.lang.UnsupportedOperationException
//  java.lang.UnsupportedOperationException
//	@GetMapping("/v1/hashOperations/randomizedMember")
//	public OrangeRedisExampleResponse testCase18() {
//		try {
//			return new OrangeRedisExampleResponse(hashExample1Api.randomMember());
//		}catch (Exception e) {
//			e.printStackTrace();
//			return new OrangeRedisExampleResponse(18,e.getMessage());
//		}
//	}
//	
//	@GetMapping("/v1/hashOperations/randomizedMembers")
//	public OrangeRedisExampleResponse testCase19(@RequestParam Long count) {
//		try {
//			return new OrangeRedisExampleResponse(hashExample1Api.randomMembers(count));
//		}catch (Exception e) {
//			e.printStackTrace();
//			return new OrangeRedisExampleResponse(19,e.getMessage());
//		}
//	}
//	
//	@GetMapping("/v1/hashOperations/randomizedAndDistinctMembers")
//	public OrangeRedisExampleResponse testCase20(@RequestParam Long count) {
//		try {
//			return new OrangeRedisExampleResponse(hashExample1Api.randomAndDistinctMembers(count));
//		}catch (Exception e) {
//			e.printStackTrace();
//			return new OrangeRedisExampleResponse(20,e.getMessage());
//		}
//	}
	
	@DeleteMapping("/v1/hashOperations/member")
	public OrangeRedisExampleResponse testCase21(@RequestParam String hashKey) {
		try {
			return new OrangeRedisExampleResponse(hashExample1Api.remove(hashKey));
		}catch (Exception e) {
			e.printStackTrace();
			return new OrangeRedisExampleResponse(21,e.getMessage());
		}
	}
	
	@PutMapping("/v1/hashOperations/removedMembers")
	public OrangeRedisExampleResponse testCase22(@RequestBody List<String> hashKeys) {
		try {
			return new OrangeRedisExampleResponse(hashExample1Api.remove(hashKeys));
		}catch (Exception e) {
			e.printStackTrace();
			return new OrangeRedisExampleResponse(22,e.getMessage());
		}
	}
	
	@GetMapping("/v1/hashOperations/randomizedAndDistinctKeys")
	public OrangeRedisExampleResponse testCase23(@RequestParam Long count) {
		try {
			return new OrangeRedisExampleResponse(hashExample1Api.randomAndDistinctKeys(count));
		}catch (Exception e) {
			e.printStackTrace();
			return new OrangeRedisExampleResponse(23,e.getMessage());
		}
	}
}
