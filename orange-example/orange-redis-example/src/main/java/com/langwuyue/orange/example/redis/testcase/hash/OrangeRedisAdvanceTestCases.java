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

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.langwuyue.orange.example.redis.api.hash.OrangeRedisHashExample6Api;
import com.langwuyue.orange.example.redis.entity.OrangeHashExampleEntity;
import com.langwuyue.orange.example.redis.entity.OrangeHashMultipleEntity;
import com.langwuyue.orange.example.redis.response.OrangeRedisExampleResponse;

/**
 * @author Liang.Zhong
 * @since 1.0.0
 */
@RestController("hashOrangeRedisAdvanceTestCases")
public class OrangeRedisAdvanceTestCases {
	
	@Autowired
	private OrangeRedisHashExample6Api hashExample6Api;
	
	@PutMapping("/v1/hashAdvanceOperations/member")
	public OrangeRedisExampleResponse testCase1(@Valid @RequestBody OrangeHashExampleEntity entity) {
		try {
			hashExample6Api.add(entity);
			return new OrangeRedisExampleResponse();
		}catch (Exception e) {
			e.printStackTrace();
			return new OrangeRedisExampleResponse(1,e.getMessage());
		}
	}
	
	@PutMapping("/v1/hashAdvanceOperations/members")
	public OrangeRedisExampleResponse testCase2(@Valid @RequestBody OrangeHashMultipleEntity arg) {
		try {
			hashExample6Api.add(arg.getEntities());
			return new OrangeRedisExampleResponse();
		}catch (Exception e) {
			e.printStackTrace();
			return new OrangeRedisExampleResponse(2,e.getMessage());
		}
	}
	
	@PutMapping("/v1/hashAdvanceOperations/initMember")
	public OrangeRedisExampleResponse testCase3(@Valid @RequestBody OrangeHashExampleEntity arg) {
		try {
			hashExample6Api.addIfAbsent(arg);
			return new OrangeRedisExampleResponse();
		}catch (Exception e) {
			e.printStackTrace();
			return new OrangeRedisExampleResponse(3,e.getMessage());
		}
	}
	
	@PutMapping("/v1/hashAdvanceOperations/initMembers")
	public OrangeRedisExampleResponse testCase4(@Valid @RequestBody OrangeHashMultipleEntity arg) {
		try {
			hashExample6Api.addIfAbsent(arg.getEntities());
			return new OrangeRedisExampleResponse();
		}catch (Exception e) {
			e.printStackTrace();
			return new OrangeRedisExampleResponse(4,e.getMessage());
		}
	}
	
	@GetMapping("/v1/hashAdvanceOperations/size")
	public OrangeRedisExampleResponse testCase7() {
		try {
			return new OrangeRedisExampleResponse(hashExample6Api.getSize());
		}catch (Exception e) {
			e.printStackTrace();
			return new OrangeRedisExampleResponse(7,e.getMessage());
		}
	}
	
	@GetMapping("/v1/hashAdvanceOperations/valueLength")
	public OrangeRedisExampleResponse testCase8(String hashKey) {
		try {
			return new OrangeRedisExampleResponse(hashExample6Api.getHashValueBytesLength(hashKey));
		}catch (Exception e) {
			e.printStackTrace();
			return new OrangeRedisExampleResponse(8,e.getMessage());
		}
	}
	
	@GetMapping("/v1/hashAdvanceOperations/keys")
	public OrangeRedisExampleResponse testCase9() {
		try {
			return new OrangeRedisExampleResponse(hashExample6Api.getAllKeys());
		}catch (Exception e) {
			e.printStackTrace();
			return new OrangeRedisExampleResponse(9,e.getMessage());
		}
	}
	
	@GetMapping("/v1/hashAdvanceOperations/randomizedKey")
	public OrangeRedisExampleResponse testCase10() {
		try {
			return new OrangeRedisExampleResponse(hashExample6Api.randomKey());
		}catch (Exception e) {
			e.printStackTrace();
			return new OrangeRedisExampleResponse(10,e.getMessage());
		}
	}
	
	@GetMapping("/v1/hashAdvanceOperations/randomizedKeys")
	public OrangeRedisExampleResponse testCase11(@RequestParam Long count) {
		try {
			return new OrangeRedisExampleResponse(hashExample6Api.randomKeys(count));
		}catch (Exception e) {
			e.printStackTrace();
			return new OrangeRedisExampleResponse(11,e.getMessage());
		}
	}
	
	@GetMapping("/v1/hashAdvanceOperations/existsKey")
	public OrangeRedisExampleResponse testCase12(@RequestParam String hashKey) {
		try {
			return new OrangeRedisExampleResponse(hashExample6Api.hasKey(hashKey));
		}catch (Exception e) {
			e.printStackTrace();
			return new OrangeRedisExampleResponse(12,e.getMessage());
		}
	}
	
	@PutMapping("/v1/hashAdvanceOperations/existsKeys")
	public OrangeRedisExampleResponse testCase13(@RequestBody List<String> hashKeys) {
		try {
			return new OrangeRedisExampleResponse(hashExample6Api.hasKeys(hashKeys));
		}catch (Exception e) {
			e.printStackTrace();
			return new OrangeRedisExampleResponse(13,e.getMessage());
		}
	}
	
	@GetMapping("/v1/hashAdvanceOperations/allValues")
	public OrangeRedisExampleResponse testCase14() {
		try {
			return new OrangeRedisExampleResponse(hashExample6Api.getValues());
		}catch (Exception e) {
			e.printStackTrace();
			return new OrangeRedisExampleResponse(14,e.getMessage());
		}
	}
	
	@GetMapping("/v1/hashAdvanceOperations/value")
	public OrangeRedisExampleResponse testCase15(@RequestParam String hashKey) {
		try {
			return new OrangeRedisExampleResponse(hashExample6Api.get(hashKey));
		}catch (Exception e) {
			e.printStackTrace();
			return new OrangeRedisExampleResponse(15,e.getMessage());
		}
	}
	
	@PutMapping("/v1/hashAdvanceOperations/values")
	public OrangeRedisExampleResponse testCase16(@RequestBody List<String> hashKeys) {
		try {
			return new OrangeRedisExampleResponse(hashExample6Api.get(hashKeys));
		}catch (Exception e) {
			e.printStackTrace();
			return new OrangeRedisExampleResponse(16,e.getMessage());
		}
	}
	
	@GetMapping("/v1/hashAdvanceOperations/allMembers")
	public OrangeRedisExampleResponse testCase17() {
		try {
			return new OrangeRedisExampleResponse(hashExample6Api.getAllMembers());
		}catch (Exception e) {
			e.printStackTrace();
			return new OrangeRedisExampleResponse(17,e.getMessage());
		}
	}
	
	@DeleteMapping("/v1/hashAdvanceOperations/member")
	public OrangeRedisExampleResponse testCase21(@RequestParam String hashKey) {
		try {
			return new OrangeRedisExampleResponse(hashExample6Api.remove(hashKey));
		}catch (Exception e) {
			e.printStackTrace();
			return new OrangeRedisExampleResponse(21,e.getMessage());
		}
	}
	
	@PutMapping("/v1/hashAdvanceOperations/removedMembers")
	public OrangeRedisExampleResponse testCase22(@RequestBody List<String> hashKeys) {
		try {
			return new OrangeRedisExampleResponse(hashExample6Api.remove(hashKeys));
		}catch (Exception e) {
			e.printStackTrace();
			return new OrangeRedisExampleResponse(22,e.getMessage());
		}
	}
}
