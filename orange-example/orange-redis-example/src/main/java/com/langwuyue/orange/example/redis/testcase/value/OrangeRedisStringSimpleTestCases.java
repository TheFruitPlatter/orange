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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.langwuyue.orange.example.redis.api.value.OrangeRedisValueExample5Api;
import com.langwuyue.orange.example.redis.response.OrangeRedisExampleResponse;

/**
 * @author Liang.Zhong
 * @since 1.0.0
 */
@RestController
public class OrangeRedisStringSimpleTestCases {
	
	@Autowired
	private OrangeRedisValueExample5Api valueExample5Api;
	
	@PutMapping("/v1/valueOperations/stringValues")
	public OrangeRedisExampleResponse testCase1(@RequestParam String value) {
		try {
			valueExample5Api.setValue(value);
			return new OrangeRedisExampleResponse();
		}catch (Exception e) {
			e.printStackTrace();
			return new OrangeRedisExampleResponse(1,e.getMessage());
		}
		
	}
	
	@PutMapping("/v1/valueOperations/concurrentStringValues")
	public OrangeRedisExampleResponse testCase2(@RequestParam String oldValue,@RequestParam String newValue) {
		try {
			return new OrangeRedisExampleResponse(valueExample5Api.compareAndSwap(oldValue,newValue));
		}catch (Exception e) {
			e.printStackTrace();
			return new OrangeRedisExampleResponse(3,e.getMessage());
		}
	}
	
	@GetMapping("/v1/valueOperations/stringValues")
	public OrangeRedisExampleResponse testCase3() {
		try {
			Object value = valueExample5Api.getValue();
			return new OrangeRedisExampleResponse(value);	
		}catch (Exception e) {
			e.printStackTrace();
			return new OrangeRedisExampleResponse(4,e.getMessage());
		}
		
	}
	
	@DeleteMapping("/v1/valueOperations/deletedStringValues")
	public OrangeRedisExampleResponse testCase4() {
		try {
			boolean success = valueExample5Api.delete();
			if(success) {
				return new OrangeRedisExampleResponse();
			}
			return new OrangeRedisExampleResponse(5,"False returned");
		}catch (Exception e) {
			e.printStackTrace();
			return new OrangeRedisExampleResponse(6,e.getMessage());
		}
	}
	
	@PutMapping("/v1/valueOperations/firstStringValues")
	public OrangeRedisExampleResponse testCase5(@RequestParam String value) {
		try {
			valueExample5Api.setValueIfAbsent(value);
			return new OrangeRedisExampleResponse();
		}catch (Exception e) {
			e.printStackTrace();
			return new OrangeRedisExampleResponse(8,e.getMessage());
		}
	}
	
	@GetMapping("/v1/valueOperations/stringValuesExpirations")
	public OrangeRedisExampleResponse testCase6() {
		try {
			return new OrangeRedisExampleResponse(valueExample5Api.getExpiration(TimeUnit.SECONDS));
		}catch (Exception e) {
			e.printStackTrace();
			return new OrangeRedisExampleResponse(9,e.getMessage());
		}
	}
	
	@PutMapping("/v1/valueOperations/stringValuesExpirations")
	public OrangeRedisExampleResponse testCase7() {
		try {
			valueExample5Api.setExpiration();
			return new OrangeRedisExampleResponse();
		}catch (Exception e) {
			e.printStackTrace();
			return new OrangeRedisExampleResponse(10,e.getMessage());
		}
	}
}
