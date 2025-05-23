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
package com.langwuyue.orange.example.redis.testcase.value;

import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.langwuyue.orange.example.redis.api.value.OrangeRedisValueExample4Api;
import com.langwuyue.orange.example.redis.entity.OrangeCompareAndSwapEntity;
import com.langwuyue.orange.example.redis.entity.OrangeValueExampleEntity;
import com.langwuyue.orange.example.redis.response.OrangeRedisExampleResponse;

/**
 * @author Liang.Zhong
 * @since 1.0.0
 */
@RestController
public class OrangeRedisDynamicKeyTestCases {
	
	@Autowired
	private OrangeRedisValueExample4Api valueExample4Api;
	
	@PutMapping("/v1/valueOperations/dynamicKeys/values")
	public OrangeRedisExampleResponse testCase1(@RequestBody OrangeValueExampleEntity entity) {
		try {
			valueExample4Api.setValue(entity.getId().toString(),entity);
			return new OrangeRedisExampleResponse();
		}catch (Exception e) {
			e.printStackTrace();
			return new OrangeRedisExampleResponse(1,e.getMessage());
		}
		
	}
	
	@PutMapping("/v1/valueOperations/dynamicKeys/concurrentValues")
	public OrangeRedisExampleResponse testCase2(@RequestBody OrangeCompareAndSwapEntity entity) {
		try {
			return new OrangeRedisExampleResponse(valueExample4Api.compareAndSwap(entity.getOldValue().getId().toString(),entity.getOldValue(),entity.getNewValue()));
		}catch (Exception e) {
			e.printStackTrace();
			return new OrangeRedisExampleResponse(3,e.getMessage());
		}
	}
	
	@GetMapping("/v1/valueOperations/dynamicKeys/values")
	public OrangeRedisExampleResponse testCase3(@RequestParam Long id) {
		try {
			Object value = valueExample4Api.getValue(id.toString());
			return new OrangeRedisExampleResponse(value);	
		}catch (Exception e) {
			e.printStackTrace();
			return new OrangeRedisExampleResponse(4,e.getMessage());
		}
		
	}
	
	@DeleteMapping("/v1/valueOperations/dynamicKeys/deletedValues")
	public OrangeRedisExampleResponse testCase4(@RequestParam Long id) {
		try {
			boolean success = valueExample4Api.delete(id.toString());
			if(success) {
				return new OrangeRedisExampleResponse();
			}
			return new OrangeRedisExampleResponse(5,"False returned");
		}catch (Exception e) {
			e.printStackTrace();
			return new OrangeRedisExampleResponse(6,e.getMessage());
		}
	}
	
	@PutMapping("/v1/valueOperations/dynamicKeys/firstValues")
	public OrangeRedisExampleResponse testCase5(@RequestBody OrangeValueExampleEntity entity) {
		try {
			boolean success = valueExample4Api.setValueIfAbsent(entity.getId().toString(),entity);
			if(success) {
				return new OrangeRedisExampleResponse();
			}
			return new OrangeRedisExampleResponse(7,"False returned");
		}catch (Exception e) {
			e.printStackTrace();
			return new OrangeRedisExampleResponse(8,e.getMessage());
		}
	}
	
	@GetMapping("/v1/valueOperations/dynamicKeys/expirations")
	public OrangeRedisExampleResponse testCase6(@RequestParam Long id) {
		try {
			return new OrangeRedisExampleResponse(valueExample4Api.getExpiration(id.toString(),TimeUnit.SECONDS));
		}catch (Exception e) {
			e.printStackTrace();
			return new OrangeRedisExampleResponse(9,e.getMessage());
		}
	}
	
	@PutMapping("/v1/valueOperations/dynamicKeys/expirations")
	public OrangeRedisExampleResponse testCase7(@RequestParam Long id) {
		try {
			valueExample4Api.setExpiration(id.toString());
			return new OrangeRedisExampleResponse();
		}catch (Exception e) {
			e.printStackTrace();
			return new OrangeRedisExampleResponse(10,e.getMessage());
		}
	}
}
