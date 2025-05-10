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
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.langwuyue.orange.example.redis.api.value.OrangeRedisValueExample2Api;
import com.langwuyue.orange.example.redis.response.OrangeRedisExampleResponse;

/**
 * @author Liang.Zhong
 * @since 1.0.0
 */
@RestController
public class OrangeRedisLongTestCases {
	
	@Autowired
	private OrangeRedisValueExample2Api valueExample2Api;
	
	@PostMapping("/v1/valueOperations/longValues")
	public OrangeRedisExampleResponse testCase1(Long detal) {
		try {
			if(detal == null) {
				return new OrangeRedisExampleResponse(valueExample2Api.increment());	
			}else {
				return new OrangeRedisExampleResponse(valueExample2Api.increment(detal));
			}
		}catch (Exception e) {
			e.printStackTrace();
			return new OrangeRedisExampleResponse(1,e.getMessage());
		}
		
	}
	
	@PutMapping("/v1/valueOperations/modifiedLongValues")
	public OrangeRedisExampleResponse testCase3(@RequestParam Long oldValue,@RequestParam Long newValue) {
		try {
			return new OrangeRedisExampleResponse(valueExample2Api.compareAndSwap(oldValue,newValue));
		}catch (Exception e) {
			e.printStackTrace();
			return new OrangeRedisExampleResponse(3,e.getMessage());
		}
	}
	
	@GetMapping("/v1/valueOperations/longValues")
	public OrangeRedisExampleResponse testCase4() {
		try {
			Object value = valueExample2Api.getValue();
			return new OrangeRedisExampleResponse(value);	
		}catch (Exception e) {
			e.printStackTrace();
			return new OrangeRedisExampleResponse(4,e.getMessage());
		}
		
	}
	
	@DeleteMapping("/v1/valueOperations/deletedLongValues")
	public OrangeRedisExampleResponse testCase5() {
		try {
			boolean success = valueExample2Api.delete();
			if(success) {
				return new OrangeRedisExampleResponse();
			}
			return new OrangeRedisExampleResponse(5,"False returned");
		}catch (Exception e) {
			e.printStackTrace();
			return new OrangeRedisExampleResponse(6,e.getMessage());
		}
	}
	
	@PutMapping("/v1/valueOperations/initedLongValues")
	public OrangeRedisExampleResponse testCase5(@RequestParam Long initValue) {
		try {
			boolean success = valueExample2Api.init(initValue);
			if(success) {
				return new OrangeRedisExampleResponse();
			}
			return new OrangeRedisExampleResponse(7,"False returned");
		}catch (Exception e) {
			e.printStackTrace();
			return new OrangeRedisExampleResponse(8,e.getMessage());
		}
	}
	
	@GetMapping("/v1/valueOperations/longValuesExpirations")
	public OrangeRedisExampleResponse testCase6() {
		try {
			return new OrangeRedisExampleResponse(valueExample2Api.getExpiration(TimeUnit.SECONDS));
		}catch (Exception e) {
			e.printStackTrace();
			return new OrangeRedisExampleResponse(8,e.getMessage());
		}
	}
	
	@PutMapping("/v1/valueOperations/longValuesExpirations")
	public OrangeRedisExampleResponse testCase7() {
		try {
			valueExample2Api.setExpiration();
			return new OrangeRedisExampleResponse();
		}catch (Exception e) {
			e.printStackTrace();
			return new OrangeRedisExampleResponse(10,e.getMessage());
		}
	}
}
