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
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.langwuyue.orange.example.redis.api.hash.OrangeRedisHashExample4Api;
import com.langwuyue.orange.example.redis.entity.OrangeHashLongCompareAndSwapEntity;
import com.langwuyue.orange.example.redis.entity.OrangeHashLongExampleEntity;
import com.langwuyue.orange.example.redis.entity.OrangeHashLongMultipleEntity;
import com.langwuyue.orange.example.redis.response.OrangeRedisExampleResponse;

/**
 * @author Liang.Zhong
 * @since 1.0.0
 */
@RestController("hashOrangeRedisLongSimpleTestCases")
public class OrangeRedisLongSimpleTestCases {
	
	@Autowired
	private OrangeRedisHashExample4Api hashExample4Api;
	
	@PutMapping("/v1/hashLongOperations/member")
	public OrangeRedisExampleResponse testCase1(@Valid @RequestBody OrangeHashLongExampleEntity entity) {
		try {
			hashExample4Api.add(entity.getKey(),entity.getValue());
			return new OrangeRedisExampleResponse();
		}catch (Exception e) {
			e.printStackTrace();
			return new OrangeRedisExampleResponse(1,e.getMessage());
		}
	}
	
	@PutMapping("/v1/hashLongOperations/members")
	public OrangeRedisExampleResponse testCase2(@Valid @RequestBody OrangeHashLongMultipleEntity arg) {
		try {
			hashExample4Api.add(arg.getEntities().stream().collect(Collectors.toMap(OrangeHashLongExampleEntity::getKey, OrangeHashLongExampleEntity::getValue)));
			return new OrangeRedisExampleResponse();
		}catch (Exception e) {
			e.printStackTrace();
			return new OrangeRedisExampleResponse(2,e.getMessage());
		}
	}
	
	@PutMapping("/v1/hashLongOperations/initMember")
	public OrangeRedisExampleResponse testCase3(@Valid @RequestBody OrangeHashLongExampleEntity arg) {
		try {
			hashExample4Api.addIfAbsent(arg.getKey(),arg.getValue());
			return new OrangeRedisExampleResponse();
		}catch (Exception e) {
			e.printStackTrace();
			return new OrangeRedisExampleResponse(3,e.getMessage());
		}
	}
	
	@PutMapping("/v1/hashLongOperations/initMembers")
	public OrangeRedisExampleResponse testCase4(@Valid @RequestBody OrangeHashLongMultipleEntity arg) {
		try {
			hashExample4Api.addIfAbsent(arg.getEntities().stream().collect(Collectors.toMap(OrangeHashLongExampleEntity::getKey, OrangeHashLongExampleEntity::getValue)));
			return new OrangeRedisExampleResponse();
		}catch (Exception e) {
			e.printStackTrace();
			return new OrangeRedisExampleResponse(4,e.getMessage());
		}
	}
	
	@PutMapping("/v1/hashLongOperations/concurrentMember")
	public OrangeRedisExampleResponse testCase5(@Valid @RequestBody OrangeHashLongCompareAndSwapEntity arg) {
		try {
			return new OrangeRedisExampleResponse(hashExample4Api.compareAndSwap(arg.getKey(),arg.getOldValue(),arg.getNewValue()));
		}catch (Exception e) {
			e.printStackTrace();
			return new OrangeRedisExampleResponse(5,e.getMessage());
		}
	}
	
	@GetMapping("/v1/hashLongOperations/size")
	public OrangeRedisExampleResponse testCase7() {
		try {
			return new OrangeRedisExampleResponse(hashExample4Api.getSize());
		}catch (Exception e) {
			e.printStackTrace();
			return new OrangeRedisExampleResponse(7,e.getMessage());
		}
	}
	
	@GetMapping("/v1/hashLongOperations/valueLength")
	public OrangeRedisExampleResponse testCase8(String hashKey) {
		try {
			return new OrangeRedisExampleResponse(hashExample4Api.getHashValueBytesLength(hashKey));
		}catch (Exception e) {
			e.printStackTrace();
			return new OrangeRedisExampleResponse(8,e.getMessage());
		}
	}
	
	@GetMapping("/v1/hashLongOperations/keys")
	public OrangeRedisExampleResponse testCase9() {
		try {
			return new OrangeRedisExampleResponse(hashExample4Api.getAllKeys());
		}catch (Exception e) {
			e.printStackTrace();
			return new OrangeRedisExampleResponse(9,e.getMessage());
		}
	}
	
	@GetMapping("/v1/hashLongOperations/randomizedKey")
	public OrangeRedisExampleResponse testCase10() {
		try {
			return new OrangeRedisExampleResponse(hashExample4Api.randomKey());
		}catch (Exception e) {
			e.printStackTrace();
			return new OrangeRedisExampleResponse(10,e.getMessage());
		}
	}
	
	@GetMapping("/v1/hashLongOperations/randomizedKeys")
	public OrangeRedisExampleResponse testCase11(@RequestParam Long count) {
		try {
			return new OrangeRedisExampleResponse(hashExample4Api.randomKeys(count));
		}catch (Exception e) {
			e.printStackTrace();
			return new OrangeRedisExampleResponse(11,e.getMessage());
		}
	}
	
	@GetMapping("/v1/hashLongOperations/existsKey")
	public OrangeRedisExampleResponse testCase12(@RequestParam String hashKey) {
		try {
			return new OrangeRedisExampleResponse(hashExample4Api.hasKey(hashKey));
		}catch (Exception e) {
			e.printStackTrace();
			return new OrangeRedisExampleResponse(12,e.getMessage());
		}
	}
	
	@PutMapping("/v1/hashLongOperations/existsKeys")
	public OrangeRedisExampleResponse testCase13(@RequestBody List<String> hashKeys) {
		try {
			return new OrangeRedisExampleResponse(hashExample4Api.hasKeys(hashKeys));
		}catch (Exception e) {
			e.printStackTrace();
			return new OrangeRedisExampleResponse(13,e.getMessage());
		}
	}
	
	@GetMapping("/v1/hashLongOperations/allValues")
	public OrangeRedisExampleResponse testCase14() {
		try {
			return new OrangeRedisExampleResponse(hashExample4Api.getValues());
		}catch (Exception e) {
			e.printStackTrace();
			return new OrangeRedisExampleResponse(14,e.getMessage());
		}
	}
	
	@GetMapping("/v1/hashLongOperations/value")
	public OrangeRedisExampleResponse testCase15(@RequestParam String hashKey) {
		try {
			return new OrangeRedisExampleResponse(hashExample4Api.get(hashKey));
		}catch (Exception e) {
			e.printStackTrace();
			return new OrangeRedisExampleResponse(15,e.getMessage());
		}
	}
	
	@PutMapping("/v1/hashLongOperations/values")
	public OrangeRedisExampleResponse testCase16(@RequestBody List<String> hashKeys) {
		try {
			return new OrangeRedisExampleResponse(hashExample4Api.get(hashKeys));
		}catch (Exception e) {
			e.printStackTrace();
			return new OrangeRedisExampleResponse(16,e.getMessage());
		}
	}
	
	@GetMapping("/v1/hashLongOperations/allMembers")
	public OrangeRedisExampleResponse testCase17() {
		try {
			return new OrangeRedisExampleResponse(hashExample4Api.getAllMembers());
		}catch (Exception e) {
			e.printStackTrace();
			return new OrangeRedisExampleResponse(17,e.getMessage());
		}
	}
	
	@DeleteMapping("/v1/hashLongOperations/member")
	public OrangeRedisExampleResponse testCase21(@RequestParam String hashKey) {
		try {
			return new OrangeRedisExampleResponse(hashExample4Api.remove(hashKey));
		}catch (Exception e) {
			e.printStackTrace();
			return new OrangeRedisExampleResponse(21,e.getMessage());
		}
	}
	
	@PutMapping("/v1/hashLongOperations/removedMembers")
	public OrangeRedisExampleResponse testCase22(@RequestBody List<String> hashKeys) {
		try {
			return new OrangeRedisExampleResponse(hashExample4Api.remove(hashKeys));
		}catch (Exception e) {
			e.printStackTrace();
			return new OrangeRedisExampleResponse(22,e.getMessage());
		}
	}
	
	@PostMapping("/v1/hashLongOperations/increment")
	public OrangeRedisExampleResponse testCase23(@RequestParam String hashKey) {
		try {
			return new OrangeRedisExampleResponse(hashExample4Api.increment(hashKey));
		}catch (Exception e) {
			e.printStackTrace();
			return new OrangeRedisExampleResponse(23,e.getMessage());
		}
	}
	
	@PostMapping("/v1/hashLongOperations/incrementByDetal")
	public OrangeRedisExampleResponse testCase24(@RequestParam String hashKey,@RequestParam Long detal) {
		try {
			return new OrangeRedisExampleResponse(hashExample4Api.increment(hashKey,detal));
		}catch (Exception e) {
			e.printStackTrace();
			return new OrangeRedisExampleResponse(24,e.getMessage());
		}
	}
	
	@PostMapping("/v1/hashLongOperations/decrement")
	public OrangeRedisExampleResponse testCase25(@RequestParam String hashKey) {
		try {
			return new OrangeRedisExampleResponse(hashExample4Api.decrement(hashKey));
		}catch (Exception e) {
			e.printStackTrace();
			return new OrangeRedisExampleResponse(25,e.getMessage());
		}
	}
	
	@PostMapping("/v1/hashLongOperations/decrementByDetal")
	public OrangeRedisExampleResponse testCase26(@RequestParam String hashKey,@RequestParam Long detal) {
		try {
			return new OrangeRedisExampleResponse(hashExample4Api.decrement(hashKey,detal));
		}catch (Exception e) {
			e.printStackTrace();
			return new OrangeRedisExampleResponse(26,e.getMessage());
		}
	}
}
