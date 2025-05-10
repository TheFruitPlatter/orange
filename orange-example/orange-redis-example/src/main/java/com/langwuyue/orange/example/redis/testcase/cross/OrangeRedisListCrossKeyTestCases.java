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
package com.langwuyue.orange.example.redis.testcase.cross;

import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.langwuyue.orange.example.redis.api.cross.OrangeRedisCrossExample3Api;
import com.langwuyue.orange.example.redis.api.list.OrangeRedisListExample4Api;
import com.langwuyue.orange.example.redis.response.OrangeRedisExampleResponse;

/**
 * @author Liang.Zhong
 * @since 1.0.0
 */
@RestController("crossOrangeRedisListCrossKeyTestCases")
public class OrangeRedisListCrossKeyTestCases {
	
	@Autowired
	private OrangeRedisCrossExample3Api crossExample3Api;
	
	@Autowired
	private OrangeRedisListExample4Api listExample4Api;
	
	
	@PostMapping("/v1/listCrossOperations/members1")
	public OrangeRedisExampleResponse testCase1() {
		try {
			return new OrangeRedisExampleResponse(crossExample3Api.move());
		}catch (Exception e) {
			e.printStackTrace();
			return new OrangeRedisExampleResponse(1,e.getMessage());
		}
	}
	
	@GetMapping("/v1/listCrossOperations/members")
	public OrangeRedisExampleResponse testCase2(@RequestParam Long startIndex,@RequestParam Long endIndex) {
		try {
			return new OrangeRedisExampleResponse(listExample4Api.getByIndexRange(startIndex,endIndex));
		}catch (Exception e) {
			e.printStackTrace();
			return new OrangeRedisExampleResponse(2,e.getMessage());
		}
	}
	
	@PostMapping("/v1/listCrossOperations/members2")
	public OrangeRedisExampleResponse testCase3() {
		try {
			return new OrangeRedisExampleResponse(crossExample3Api.moveByTimeout());
		}catch (Exception e) {
			e.printStackTrace();
			return new OrangeRedisExampleResponse(3,e.getMessage());
		}
	}
	
	
	@PostMapping("/v1/listCrossOperations/members3")
	public OrangeRedisExampleResponse testCase4(Long seconds) {
		try {
			return new OrangeRedisExampleResponse(crossExample3Api.moveByTimeout(seconds,TimeUnit.SECONDS));
		}catch (Exception e) {
			e.printStackTrace();
			return new OrangeRedisExampleResponse(4,e.getMessage());
		}
	}
	
	@DeleteMapping("/v1/listCrossOperations/allMembers")
	public OrangeRedisExampleResponse testCase24() {
		try {
			return new OrangeRedisExampleResponse(listExample4Api.delete());
		}catch (Exception e) {
			e.printStackTrace();
			return new OrangeRedisExampleResponse(24,e.getMessage());
		}
	}
	
	
}
