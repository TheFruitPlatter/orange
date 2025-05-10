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

import com.langwuyue.orange.example.redis.api.hash.OrangeRedisHashExample3Api;
import com.langwuyue.orange.example.redis.entity.OrangeHashStringCompareAndSwapEntity;
import com.langwuyue.orange.example.redis.entity.OrangeHashStringExampleEntity;
import com.langwuyue.orange.example.redis.entity.OrangeHashStringMultipleEntity;
import com.langwuyue.orange.example.redis.response.OrangeRedisExampleResponse;

/**
 * @author Liang.Zhong
 * @since 1.0.0
 */
@RestController("hashOrangeRedisDynamicKeySimpleTestCases")
public class OrangeRedisDynamicKeySimpleTestCases {
	
	@Autowired
	private OrangeRedisHashExample3Api hashExample3Api;
	
	@PutMapping("/v1/hashDynamicOperations/member")
	public OrangeRedisExampleResponse testCase1(@Valid @RequestBody OrangeHashStringExampleEntity entity,@RequestParam String keyVar) {
		try {
			hashExample3Api.add(keyVar,entity.getKey(),entity.getValue());
			return new OrangeRedisExampleResponse();
		}catch (Exception e) {
			e.printStackTrace();
			return new OrangeRedisExampleResponse(1,e.getMessage());
		}
	}
	
	@PutMapping("/v1/hashDynamicOperations/members")
	public OrangeRedisExampleResponse testCase2(@Valid @RequestBody OrangeHashStringMultipleEntity arg,@RequestParam String keyVar) {
		try {
			hashExample3Api.add(keyVar,arg.getEntities().stream().collect(Collectors.toMap(OrangeHashStringExampleEntity::getKey, OrangeHashStringExampleEntity::getValue)));
			return new OrangeRedisExampleResponse();
		}catch (Exception e) {
			e.printStackTrace();
			return new OrangeRedisExampleResponse(2,e.getMessage());
		}
	}
	
	@PutMapping("/v1/hashDynamicOperations/initMember")
	public OrangeRedisExampleResponse testCase3(@Valid @RequestBody OrangeHashStringExampleEntity arg,@RequestParam String keyVar) {
		try {
			hashExample3Api.addIfAbsent(keyVar,arg.getKey(),arg.getValue());
			return new OrangeRedisExampleResponse();
		}catch (Exception e) {
			e.printStackTrace();
			return new OrangeRedisExampleResponse(3,e.getMessage());
		}
	}
	
	@PutMapping("/v1/hashDynamicOperations/initMembers")
	public OrangeRedisExampleResponse testCase4(@Valid @RequestBody OrangeHashStringMultipleEntity arg,@RequestParam String keyVar) {
		try {
			hashExample3Api.addIfAbsent(keyVar,arg.getEntities().stream().collect(Collectors.toMap(OrangeHashStringExampleEntity::getKey, OrangeHashStringExampleEntity::getValue)));
			return new OrangeRedisExampleResponse();
		}catch (Exception e) {
			e.printStackTrace();
			return new OrangeRedisExampleResponse(4,e.getMessage());
		}
	}
	
	@PutMapping("/v1/hashDynamicOperations/concurrentMember")
	public OrangeRedisExampleResponse testCase5(@Valid @RequestBody OrangeHashStringCompareAndSwapEntity arg,@RequestParam String keyVar) {
		try {
			return new OrangeRedisExampleResponse(hashExample3Api.compareAndSwap(keyVar,arg.getKey(),arg.getOldValue(),arg.getNewValue()));
		}catch (Exception e) {
			e.printStackTrace();
			return new OrangeRedisExampleResponse(5,e.getMessage());
		}
	}
	
	@GetMapping("/v1/hashDynamicOperations/size")
	public OrangeRedisExampleResponse testCase7(@RequestParam String keyVar) {
		try {
			return new OrangeRedisExampleResponse(hashExample3Api.getSize(keyVar));
		}catch (Exception e) {
			e.printStackTrace();
			return new OrangeRedisExampleResponse(7,e.getMessage());
		}
	}
	
	@GetMapping("/v1/hashDynamicOperations/valueLength")
	public OrangeRedisExampleResponse testCase8(String hashKey,@RequestParam String keyVar) {
		try {
			return new OrangeRedisExampleResponse(hashExample3Api.getHashValueBytesLength(keyVar,hashKey));
		}catch (Exception e) {
			e.printStackTrace();
			return new OrangeRedisExampleResponse(8,e.getMessage());
		}
	}
	
	@GetMapping("/v1/hashDynamicOperations/keys")
	public OrangeRedisExampleResponse testCase9(@RequestParam String keyVar) {
		try {
			return new OrangeRedisExampleResponse(hashExample3Api.getAllKeys(keyVar));
		}catch (Exception e) {
			e.printStackTrace();
			return new OrangeRedisExampleResponse(9,e.getMessage());
		}
	}
	
	@GetMapping("/v1/hashDynamicOperations/randomizedKey")
	public OrangeRedisExampleResponse testCase10(@RequestParam String keyVar) {
		try {
			return new OrangeRedisExampleResponse(hashExample3Api.randomKey(keyVar));
		}catch (Exception e) {
			e.printStackTrace();
			return new OrangeRedisExampleResponse(10,e.getMessage());
		}
	}
	
	@GetMapping("/v1/hashDynamicOperations/randomizedKeys")
	public OrangeRedisExampleResponse testCase11(@RequestParam Long count,@RequestParam String keyVar) {
		try {
			return new OrangeRedisExampleResponse(hashExample3Api.randomKeys(keyVar,count));
		}catch (Exception e) {
			e.printStackTrace();
			return new OrangeRedisExampleResponse(11,e.getMessage());
		}
	}
	
	@GetMapping("/v1/hashDynamicOperations/existsKey")
	public OrangeRedisExampleResponse testCase12(@RequestParam String hashKey,@RequestParam String keyVar) {
		try {
			return new OrangeRedisExampleResponse(hashExample3Api.hasKey(keyVar,hashKey));
		}catch (Exception e) {
			e.printStackTrace();
			return new OrangeRedisExampleResponse(12,e.getMessage());
		}
	}
	
	@PutMapping("/v1/hashDynamicOperations/existsKeys")
	public OrangeRedisExampleResponse testCase13(@RequestBody List<String> hashKeys,@RequestParam String keyVar) {
		try {
			return new OrangeRedisExampleResponse(hashExample3Api.hasKeys(keyVar,hashKeys));
		}catch (Exception e) {
			e.printStackTrace();
			return new OrangeRedisExampleResponse(13,e.getMessage());
		}
	}
	
	@GetMapping("/v1/hashDynamicOperations/allValues")
	public OrangeRedisExampleResponse testCase14(@RequestParam String keyVar) {
		try {
			return new OrangeRedisExampleResponse(hashExample3Api.getValues(keyVar));
		}catch (Exception e) {
			e.printStackTrace();
			return new OrangeRedisExampleResponse(14,e.getMessage());
		}
	}
	
	@GetMapping("/v1/hashDynamicOperations/value")
	public OrangeRedisExampleResponse testCase15(@RequestParam String hashKey,@RequestParam String keyVar) {
		try {
			return new OrangeRedisExampleResponse(hashExample3Api.get(keyVar,hashKey));
		}catch (Exception e) {
			e.printStackTrace();
			return new OrangeRedisExampleResponse(15,e.getMessage());
		}
	}
	
	@PutMapping("/v1/hashDynamicOperations/values")
	public OrangeRedisExampleResponse testCase16(@RequestBody List<String> hashKeys,@RequestParam String keyVar) {
		try {
			return new OrangeRedisExampleResponse(hashExample3Api.get(keyVar,hashKeys));
		}catch (Exception e) {
			e.printStackTrace();
			return new OrangeRedisExampleResponse(16,e.getMessage());
		}
	}
	
	@GetMapping("/v1/hashDynamicOperations/allMembers")
	public OrangeRedisExampleResponse testCase17(@RequestParam String keyVar) {
		try {
			return new OrangeRedisExampleResponse(hashExample3Api.getAllMembers(keyVar));
		}catch (Exception e) {
			e.printStackTrace();
			return new OrangeRedisExampleResponse(17,e.getMessage());
		}
	}
	
	@DeleteMapping("/v1/hashDynamicOperations/member")
	public OrangeRedisExampleResponse testCase21(@RequestParam String hashKey,@RequestParam String keyVar) {
		try {
			return new OrangeRedisExampleResponse(hashExample3Api.remove(keyVar,hashKey));
		}catch (Exception e) {
			e.printStackTrace();
			return new OrangeRedisExampleResponse(21,e.getMessage());
		}
	}
	
	@PutMapping("/v1/hashDynamicOperations/removedMembers")
	public OrangeRedisExampleResponse testCase22(@RequestBody List<String> hashKeys,@RequestParam String keyVar) {
		try {
			return new OrangeRedisExampleResponse(hashExample3Api.remove(keyVar,hashKeys));
		}catch (Exception e) {
			e.printStackTrace();
			return new OrangeRedisExampleResponse(22,e.getMessage());
		}
	}
}
