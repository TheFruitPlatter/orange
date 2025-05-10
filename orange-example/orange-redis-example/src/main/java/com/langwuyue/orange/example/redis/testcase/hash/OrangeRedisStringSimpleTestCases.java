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

import com.langwuyue.orange.example.redis.api.hash.OrangeRedisHashExample2Api;
import com.langwuyue.orange.example.redis.entity.OrangeHashStringCompareAndSwapEntity;
import com.langwuyue.orange.example.redis.entity.OrangeHashStringExampleEntity;
import com.langwuyue.orange.example.redis.entity.OrangeHashStringMultipleEntity;
import com.langwuyue.orange.example.redis.response.OrangeRedisExampleResponse;

/**
 * @author Liang.Zhong
 * @since 1.0.0
 */
@RestController("hashOrangeRedisStringSimpleTestCases")
public class OrangeRedisStringSimpleTestCases {
	
	@Autowired
	private OrangeRedisHashExample2Api hashExample2Api;
	
	@PutMapping("/v1/hashStringOperations/member")
	public OrangeRedisExampleResponse testCase1(@Valid @RequestBody OrangeHashStringExampleEntity entity) {
		try {
			hashExample2Api.add(entity.getKey(),entity.getValue());
			return new OrangeRedisExampleResponse();
		}catch (Exception e) {
			e.printStackTrace();
			return new OrangeRedisExampleResponse(1,e.getMessage());
		}
	}
	
	@PutMapping("/v1/hashStringOperations/members")
	public OrangeRedisExampleResponse testCase2(@Valid @RequestBody OrangeHashStringMultipleEntity arg) {
		try {
			hashExample2Api.add(arg.getEntities().stream().collect(Collectors.toMap(OrangeHashStringExampleEntity::getKey, OrangeHashStringExampleEntity::getValue)));
			return new OrangeRedisExampleResponse();
		}catch (Exception e) {
			e.printStackTrace();
			return new OrangeRedisExampleResponse(2,e.getMessage());
		}
	}
	
	@PutMapping("/v1/hashStringOperations/initMember")
	public OrangeRedisExampleResponse testCase3(@Valid @RequestBody OrangeHashStringExampleEntity arg) {
		try {
			hashExample2Api.addIfAbsent(arg.getKey(),arg.getValue());
			return new OrangeRedisExampleResponse();
		}catch (Exception e) {
			e.printStackTrace();
			return new OrangeRedisExampleResponse(3,e.getMessage());
		}
	}
	
	@PutMapping("/v1/hashStringOperations/initMembers")
	public OrangeRedisExampleResponse testCase4(@Valid @RequestBody OrangeHashStringMultipleEntity arg) {
		try {
			hashExample2Api.addIfAbsent(arg.getEntities().stream().collect(Collectors.toMap(OrangeHashStringExampleEntity::getKey, OrangeHashStringExampleEntity::getValue)));
			return new OrangeRedisExampleResponse();
		}catch (Exception e) {
			e.printStackTrace();
			return new OrangeRedisExampleResponse(4,e.getMessage());
		}
	}
	
	@PutMapping("/v1/hashStringOperations/concurrentMember")
	public OrangeRedisExampleResponse testCase5(@Valid @RequestBody OrangeHashStringCompareAndSwapEntity arg) {
		try {
			return new OrangeRedisExampleResponse(hashExample2Api.compareAndSwap(arg.getKey(),arg.getOldValue(),arg.getNewValue()));
		}catch (Exception e) {
			e.printStackTrace();
			return new OrangeRedisExampleResponse(5,e.getMessage());
		}
	}
	
	@GetMapping("/v1/hashStringOperations/size")
	public OrangeRedisExampleResponse testCase7() {
		try {
			return new OrangeRedisExampleResponse(hashExample2Api.getSize());
		}catch (Exception e) {
			e.printStackTrace();
			return new OrangeRedisExampleResponse(7,e.getMessage());
		}
	}
	
	@GetMapping("/v1/hashStringOperations/valueLength")
	public OrangeRedisExampleResponse testCase8(String hashKey) {
		try {
			return new OrangeRedisExampleResponse(hashExample2Api.getHashValueBytesLength(hashKey));
		}catch (Exception e) {
			e.printStackTrace();
			return new OrangeRedisExampleResponse(8,e.getMessage());
		}
	}
	
	@GetMapping("/v1/hashStringOperations/keys")
	public OrangeRedisExampleResponse testCase9() {
		try {
			return new OrangeRedisExampleResponse(hashExample2Api.getAllKeys());
		}catch (Exception e) {
			e.printStackTrace();
			return new OrangeRedisExampleResponse(9,e.getMessage());
		}
	}
	
	@GetMapping("/v1/hashStringOperations/randomizedKey")
	public OrangeRedisExampleResponse testCase10() {
		try {
			return new OrangeRedisExampleResponse(hashExample2Api.randomKey());
		}catch (Exception e) {
			e.printStackTrace();
			return new OrangeRedisExampleResponse(10,e.getMessage());
		}
	}
	
	@GetMapping("/v1/hashStringOperations/randomizedKeys")
	public OrangeRedisExampleResponse testCase11(@RequestParam Long count) {
		try {
			return new OrangeRedisExampleResponse(hashExample2Api.randomKeys(count));
		}catch (Exception e) {
			e.printStackTrace();
			return new OrangeRedisExampleResponse(11,e.getMessage());
		}
	}
	
	@GetMapping("/v1/hashStringOperations/existsKey")
	public OrangeRedisExampleResponse testCase12(@RequestParam String hashKey) {
		try {
			return new OrangeRedisExampleResponse(hashExample2Api.hasKey(hashKey));
		}catch (Exception e) {
			e.printStackTrace();
			return new OrangeRedisExampleResponse(12,e.getMessage());
		}
	}
	
	@PutMapping("/v1/hashStringOperations/existsKeys")
	public OrangeRedisExampleResponse testCase13(@RequestBody List<String> hashKeys) {
		try {
			return new OrangeRedisExampleResponse(hashExample2Api.hasKeys(hashKeys));
		}catch (Exception e) {
			e.printStackTrace();
			return new OrangeRedisExampleResponse(13,e.getMessage());
		}
	}
	
	@GetMapping("/v1/hashStringOperations/allValues")
	public OrangeRedisExampleResponse testCase14() {
		try {
			return new OrangeRedisExampleResponse(hashExample2Api.getValues());
		}catch (Exception e) {
			e.printStackTrace();
			return new OrangeRedisExampleResponse(14,e.getMessage());
		}
	}
	
	@GetMapping("/v1/hashStringOperations/value")
	public OrangeRedisExampleResponse testCase15(@RequestParam String hashKey) {
		try {
			return new OrangeRedisExampleResponse(hashExample2Api.get(hashKey));
		}catch (Exception e) {
			e.printStackTrace();
			return new OrangeRedisExampleResponse(15,e.getMessage());
		}
	}
	
	@PutMapping("/v1/hashStringOperations/values")
	public OrangeRedisExampleResponse testCase16(@RequestBody List<String> hashKeys) {
		try {
			return new OrangeRedisExampleResponse(hashExample2Api.get(hashKeys));
		}catch (Exception e) {
			e.printStackTrace();
			return new OrangeRedisExampleResponse(16,e.getMessage());
		}
	}
	
	@GetMapping("/v1/hashStringOperations/allMembers")
	public OrangeRedisExampleResponse testCase17() {
		try {
			return new OrangeRedisExampleResponse(hashExample2Api.getAllMembers());
		}catch (Exception e) {
			e.printStackTrace();
			return new OrangeRedisExampleResponse(17,e.getMessage());
		}
	}
	
	@DeleteMapping("/v1/hashStringOperations/member")
	public OrangeRedisExampleResponse testCase21(@RequestParam String hashKey) {
		try {
			return new OrangeRedisExampleResponse(hashExample2Api.remove(hashKey));
		}catch (Exception e) {
			e.printStackTrace();
			return new OrangeRedisExampleResponse(21,e.getMessage());
		}
	}
	
	@PutMapping("/v1/hashStringOperations/removedMembers")
	public OrangeRedisExampleResponse testCase22(@RequestBody List<String> hashKeys) {
		try {
			return new OrangeRedisExampleResponse(hashExample2Api.remove(hashKeys));
		}catch (Exception e) {
			e.printStackTrace();
			return new OrangeRedisExampleResponse(22,e.getMessage());
		}
	}
}
