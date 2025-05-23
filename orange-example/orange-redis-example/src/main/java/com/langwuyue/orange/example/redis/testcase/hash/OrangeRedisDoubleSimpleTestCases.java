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

import com.langwuyue.orange.example.redis.api.hash.OrangeRedisHashExample5Api;
import com.langwuyue.orange.example.redis.entity.OrangeHashDoubleCompareAndSwapEntity;
import com.langwuyue.orange.example.redis.entity.OrangeHashDoubleExampleEntity;
import com.langwuyue.orange.example.redis.entity.OrangeHashDoubleMultipleEntity;
import com.langwuyue.orange.example.redis.response.OrangeRedisExampleResponse;

/**
 * @author Liang.Zhong
 * @since 1.0.0
 */
@RestController("hashOrangeRedisDoubleSimpleTestCases")
public class OrangeRedisDoubleSimpleTestCases {
	
	@Autowired
	private OrangeRedisHashExample5Api hashExample5Api;
	
	@PutMapping("/v1/hashDoubleOperations/member")
	public OrangeRedisExampleResponse testCase1(@Valid @RequestBody OrangeHashDoubleExampleEntity entity) {
		try {
			hashExample5Api.add(entity.getKey(),entity.getValue());
			return new OrangeRedisExampleResponse();
		}catch (Exception e) {
			e.printStackTrace();
			return new OrangeRedisExampleResponse(1,e.getMessage());
		}
	}
	
	@PutMapping("/v1/hashDoubleOperations/members")
	public OrangeRedisExampleResponse testCase2(@Valid @RequestBody OrangeHashDoubleMultipleEntity arg) {
		try {
			hashExample5Api.add(arg.getEntities().stream().collect(Collectors.toMap(OrangeHashDoubleExampleEntity::getKey, OrangeHashDoubleExampleEntity::getValue)));
			return new OrangeRedisExampleResponse();
		}catch (Exception e) {
			e.printStackTrace();
			return new OrangeRedisExampleResponse(2,e.getMessage());
		}
	}
	
	@PutMapping("/v1/hashDoubleOperations/initMember")
	public OrangeRedisExampleResponse testCase3(@Valid @RequestBody OrangeHashDoubleExampleEntity arg) {
		try {
			hashExample5Api.addIfAbsent(arg.getKey(),arg.getValue());
			return new OrangeRedisExampleResponse();
		}catch (Exception e) {
			e.printStackTrace();
			return new OrangeRedisExampleResponse(3,e.getMessage());
		}
	}
	
	@PutMapping("/v1/hashDoubleOperations/initMembers")
	public OrangeRedisExampleResponse testCase4(@Valid @RequestBody OrangeHashDoubleMultipleEntity arg) {
		try {
			hashExample5Api.addIfAbsent(arg.getEntities().stream().collect(Collectors.toMap(OrangeHashDoubleExampleEntity::getKey, OrangeHashDoubleExampleEntity::getValue)));
			return new OrangeRedisExampleResponse();
		}catch (Exception e) {
			e.printStackTrace();
			return new OrangeRedisExampleResponse(4,e.getMessage());
		}
	}
	
	@PutMapping("/v1/hashDoubleOperations/concurrentMember")
	public OrangeRedisExampleResponse testCase5(@Valid @RequestBody OrangeHashDoubleCompareAndSwapEntity arg) {
		try {
			return new OrangeRedisExampleResponse(hashExample5Api.compareAndSwap(arg.getKey(),arg.getOldValue(),arg.getNewValue()));
		}catch (Exception e) {
			e.printStackTrace();
			return new OrangeRedisExampleResponse(5,e.getMessage());
		}
	}
	
	@GetMapping("/v1/hashDoubleOperations/size")
	public OrangeRedisExampleResponse testCase7() {
		try {
			return new OrangeRedisExampleResponse(hashExample5Api.getSize());
		}catch (Exception e) {
			e.printStackTrace();
			return new OrangeRedisExampleResponse(7,e.getMessage());
		}
	}
	
	@GetMapping("/v1/hashDoubleOperations/valueLength")
	public OrangeRedisExampleResponse testCase8(String hashKey) {
		try {
			return new OrangeRedisExampleResponse(hashExample5Api.getHashValueBytesLength(hashKey));
		}catch (Exception e) {
			e.printStackTrace();
			return new OrangeRedisExampleResponse(8,e.getMessage());
		}
	}
	
	@GetMapping("/v1/hashDoubleOperations/keys")
	public OrangeRedisExampleResponse testCase9() {
		try {
			return new OrangeRedisExampleResponse(hashExample5Api.getAllKeys());
		}catch (Exception e) {
			e.printStackTrace();
			return new OrangeRedisExampleResponse(9,e.getMessage());
		}
	}
	
	@GetMapping("/v1/hashDoubleOperations/randomizedKey")
	public OrangeRedisExampleResponse testCase10() {
		try {
			return new OrangeRedisExampleResponse(hashExample5Api.randomKey());
		}catch (Exception e) {
			e.printStackTrace();
			return new OrangeRedisExampleResponse(10,e.getMessage());
		}
	}
	
	@GetMapping("/v1/hashDoubleOperations/randomizedKeys")
	public OrangeRedisExampleResponse testCase11(@RequestParam Long count) {
		try {
			return new OrangeRedisExampleResponse(hashExample5Api.randomKeys(count));
		}catch (Exception e) {
			e.printStackTrace();
			return new OrangeRedisExampleResponse(11,e.getMessage());
		}
	}
	
	@GetMapping("/v1/hashDoubleOperations/existsKey")
	public OrangeRedisExampleResponse testCase12(@RequestParam String hashKey) {
		try {
			return new OrangeRedisExampleResponse(hashExample5Api.hasKey(hashKey));
		}catch (Exception e) {
			e.printStackTrace();
			return new OrangeRedisExampleResponse(12,e.getMessage());
		}
	}
	
	@PutMapping("/v1/hashDoubleOperations/existsKeys")
	public OrangeRedisExampleResponse testCase13(@RequestBody List<String> hashKeys) {
		try {
			return new OrangeRedisExampleResponse(hashExample5Api.hasKeys(hashKeys));
		}catch (Exception e) {
			e.printStackTrace();
			return new OrangeRedisExampleResponse(13,e.getMessage());
		}
	}
	
	@GetMapping("/v1/hashDoubleOperations/allValues")
	public OrangeRedisExampleResponse testCase14() {
		try {
			return new OrangeRedisExampleResponse(hashExample5Api.getValues());
		}catch (Exception e) {
			e.printStackTrace();
			return new OrangeRedisExampleResponse(14,e.getMessage());
		}
	}
	
	@GetMapping("/v1/hashDoubleOperations/value")
	public OrangeRedisExampleResponse testCase15(@RequestParam String hashKey) {
		try {
			return new OrangeRedisExampleResponse(hashExample5Api.get(hashKey));
		}catch (Exception e) {
			e.printStackTrace();
			return new OrangeRedisExampleResponse(15,e.getMessage());
		}
	}
	
	@PutMapping("/v1/hashDoubleOperations/values")
	public OrangeRedisExampleResponse testCase16(@RequestBody List<String> hashKeys) {
		try {
			return new OrangeRedisExampleResponse(hashExample5Api.get(hashKeys));
		}catch (Exception e) {
			e.printStackTrace();
			return new OrangeRedisExampleResponse(16,e.getMessage());
		}
	}
	
	@GetMapping("/v1/hashDoubleOperations/allMembers")
	public OrangeRedisExampleResponse testCase17() {
		try {
			return new OrangeRedisExampleResponse(hashExample5Api.getAllMembers());
		}catch (Exception e) {
			e.printStackTrace();
			return new OrangeRedisExampleResponse(17,e.getMessage());
		}
	}
	
	@DeleteMapping("/v1/hashDoubleOperations/member")
	public OrangeRedisExampleResponse testCase21(@RequestParam String hashKey) {
		try {
			return new OrangeRedisExampleResponse(hashExample5Api.remove(hashKey));
		}catch (Exception e) {
			e.printStackTrace();
			return new OrangeRedisExampleResponse(21,e.getMessage());
		}
	}
	
	@PutMapping("/v1/hashDoubleOperations/removedMembers")
	public OrangeRedisExampleResponse testCase22(@RequestBody List<String> hashKeys) {
		try {
			return new OrangeRedisExampleResponse(hashExample5Api.remove(hashKeys));
		}catch (Exception e) {
			e.printStackTrace();
			return new OrangeRedisExampleResponse(22,e.getMessage());
		}
	}
	
	@PostMapping("/v1/hashDoubleOperations/incrementByDetal")
	public OrangeRedisExampleResponse testCase24(@RequestParam String hashKey,@RequestParam Double detal) {
		try {
			return new OrangeRedisExampleResponse(hashExample5Api.increment(hashKey,detal));
		}catch (Exception e) {
			e.printStackTrace();
			return new OrangeRedisExampleResponse(24,e.getMessage());
		}
	}
	
	@PostMapping("/v1/hashDoubleOperations/decrementByDetal")
	public OrangeRedisExampleResponse testCase26(@RequestParam String hashKey,@RequestParam Double detal) {
		try {
			return new OrangeRedisExampleResponse(hashExample5Api.decrement(hashKey,detal));
		}catch (Exception e) {
			e.printStackTrace();
			return new OrangeRedisExampleResponse(26,e.getMessage());
		}
	}
}
