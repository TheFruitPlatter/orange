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
package com.langwuyue.orange.example.redis.testcase.multiplelocks;

import java.util.List;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.langwuyue.orange.example.redis.api.multiplelocks.OrangeRedisMultipleLocksExample1Api;
import com.langwuyue.orange.example.redis.response.OrangeRedisExampleResponse;

/**
 * @author Liang.Zhong
 * @since 1.0.0
 */
@RestController
public class OrangeMultipleLocksTestCase {

	@Autowired
	private OrangeRedisMultipleLocksExample1Api example1Api;
	
	@PutMapping("/v1/multipleLocksOperations/locks")
	public OrangeRedisExampleResponse testCase1(@RequestBody List<String> entities) {
		try {
			example1Api.lock(entities);
			return new OrangeRedisExampleResponse();
		}catch (Exception e) {
			e.printStackTrace();
			return new OrangeRedisExampleResponse(1,e.getMessage());
		}
	}
	
	@DeleteMapping("/v1/multipleLocksOperations/locks")
	public OrangeRedisExampleResponse testCase2() {
		try {
			return new OrangeRedisExampleResponse(example1Api.releaseAll());
		}catch (Exception e) {
			e.printStackTrace();
			return new OrangeRedisExampleResponse(2,e.getMessage());
		}
	}
	
	@PutMapping("/v1/multipleLocksOperations/releasedLocks")
	public OrangeRedisExampleResponse testCase3(@RequestBody List<String> entities) {
		try {
			return new OrangeRedisExampleResponse(example1Api.release(entities));
		}catch (Exception e) {
			e.printStackTrace();
			return new OrangeRedisExampleResponse(3,e.getMessage());
		}
	}
	
	@PutMapping("/v1/multipleLocksOperations/locksExpirations")
	public OrangeRedisExampleResponse testCase4(@RequestBody List<String> entities) {
		try {
			return new OrangeRedisExampleResponse(example1Api.getExpiration(entities));
		}catch (Exception e) {
			e.printStackTrace();
			return new OrangeRedisExampleResponse(4,e.getMessage());
		}
	}
	
	@PutMapping("/v1/multipleLocksOperations/locksMillis")
	public OrangeRedisExampleResponse testCase5(@RequestBody List<String> entities) {
		try {
			return new OrangeRedisExampleResponse(example1Api.getExpiration(entities,TimeUnit.MILLISECONDS));
		}catch (Exception e) {
			e.printStackTrace();
			return new OrangeRedisExampleResponse(5,e.getMessage());
		}
	}
	
	
}
