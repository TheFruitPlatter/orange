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

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.langwuyue.orange.example.redis.api.cross.OrangeRedisCrossExample2Api;
import com.langwuyue.orange.example.redis.api.set.OrangeRedisSetExample4Api;
import com.langwuyue.orange.example.redis.api.set.OrangeRedisSetExample5Api;
import com.langwuyue.orange.example.redis.api.set.OrangeRedisSetExample6Api;
import com.langwuyue.orange.example.redis.api.set.OrangeRedisSetExample7Api;
import com.langwuyue.orange.example.redis.api.set.OrangeRedisSetExample8Api;
import com.langwuyue.orange.example.redis.entity.OrangeValueExampleEntity;
import com.langwuyue.orange.example.redis.entity.OrangeValueMultipleExampleEntity;
import com.langwuyue.orange.example.redis.response.OrangeRedisExampleResponse;

/**
 * @author Liang.Zhong
 * @since 1.0.0
 */
@RestController("crossOrangeRedisSetCrossKeyTestCases")
public class OrangeRedisSetCrossKeyTestCases {
	
	@Autowired
	private OrangeRedisCrossExample2Api crossExample2Api;
	
	@Autowired
	private OrangeRedisSetExample4Api setExample4Api;
	
	@Autowired
	private OrangeRedisSetExample5Api setExample5Api;

	@Autowired
	private OrangeRedisSetExample6Api setExample6Api;
	
	@Autowired
	private OrangeRedisSetExample7Api setExample7Api;
	
	@Autowired
	private OrangeRedisSetExample8Api setExample8Api;
	
	@PutMapping("/v1/setCrossOperations/members1")
	public OrangeRedisExampleResponse testCase1(@Valid @RequestBody OrangeValueMultipleExampleEntity entity) {
		try {
			return new OrangeRedisExampleResponse(setExample4Api.add(entity.getEntities()));
		}catch (Exception e) {
			e.printStackTrace();
			return new OrangeRedisExampleResponse(1,e.getMessage());
		}
	}
	
	@PutMapping("/v1/setCrossOperations/members2")
	public OrangeRedisExampleResponse testCase2(@Valid @RequestBody OrangeValueMultipleExampleEntity entity) {
		try {
			return new OrangeRedisExampleResponse(setExample5Api.add(entity.getEntities()));
		}catch (Exception e) {
			e.printStackTrace();
			return new OrangeRedisExampleResponse(2,e.getMessage());
		}
	}
	
	@GetMapping("/v1/setCrossOperations/members1")
	public OrangeRedisExampleResponse testCase3() {
		try {
			return new OrangeRedisExampleResponse(setExample4Api.getMembers());
		}catch (Exception e) {
			e.printStackTrace();
			return new OrangeRedisExampleResponse(3,e.getMessage());
		}
	}
	
	@GetMapping("/v1/setCrossOperations/members2")
	public OrangeRedisExampleResponse testCase4() {
		try {
			return new OrangeRedisExampleResponse(setExample5Api.getMembers());
		}catch (Exception e) {
			e.printStackTrace();
			return new OrangeRedisExampleResponse(4,e.getMessage());
		}
	}
	
	@GetMapping("/v1/setCrossOperations/differenceMembers")
	public OrangeRedisExampleResponse testCase5() {
		try {
			return new OrangeRedisExampleResponse(crossExample2Api.difference());
		}catch (Exception e) {
			e.printStackTrace();
			return new OrangeRedisExampleResponse(5,e.getMessage());
		}
	}
	
	@PostMapping("/v1/setCrossOperations/differenceMembers")
	public OrangeRedisExampleResponse testCase6() {
		try {
			return new OrangeRedisExampleResponse(crossExample2Api.differenceAndStore());
		}catch (Exception e) {
			e.printStackTrace();
			return new OrangeRedisExampleResponse(6,e.getMessage());
		}
	}
	
	@GetMapping("/v1/setCrossOperations/unionMembers")
	public OrangeRedisExampleResponse testCase7() {
		try {
			return new OrangeRedisExampleResponse(crossExample2Api.union());
		}catch (Exception e) {
			e.printStackTrace();
			return new OrangeRedisExampleResponse(7,e.getMessage());
		}
	}
	
	@PostMapping("/v1/setCrossOperations/unionMembers")
	public OrangeRedisExampleResponse testCase8() {
		try {
			return new OrangeRedisExampleResponse(crossExample2Api.unionAndStore());
		}catch (Exception e) {
			e.printStackTrace();
			return new OrangeRedisExampleResponse(8,e.getMessage());
		}
	}
	
	@GetMapping("/v1/setCrossOperations/intersectMembers")
	public OrangeRedisExampleResponse testCase9() {
		try {
			return new OrangeRedisExampleResponse(crossExample2Api.intersect());
		}catch (Exception e) {
			e.printStackTrace();
			return new OrangeRedisExampleResponse(9,e.getMessage());
		}
	}
	
	@PostMapping("/v1/setCrossOperations/intersectMembers")
	public OrangeRedisExampleResponse testCase10() {
		try {
			return new OrangeRedisExampleResponse(crossExample2Api.intersectAndStore());
		}catch (Exception e) {
			e.printStackTrace();
			return new OrangeRedisExampleResponse(10,e.getMessage());
		}
	}
	
	@PostMapping("/v1/setCrossOperations/members")
	public OrangeRedisExampleResponse testCase11(@Valid @RequestBody OrangeValueExampleEntity member) {
		try {
			return new OrangeRedisExampleResponse(crossExample2Api.move(member));
		}catch (Exception e) {
			e.printStackTrace();
			return new OrangeRedisExampleResponse(11,e.getMessage());
		}
	}
	
	@GetMapping("/v1/setCrossOperations/members3")
	public OrangeRedisExampleResponse testCase12() {
		try {
			return new OrangeRedisExampleResponse(setExample6Api.getMembers());
		}catch (Exception e) {
			e.printStackTrace();
			return new OrangeRedisExampleResponse(12,e.getMessage());
		}
	}
	
	@GetMapping("/v1/setCrossOperations/members4")
	public OrangeRedisExampleResponse testCase13() {
		try {
			return new OrangeRedisExampleResponse(setExample7Api.getMembers());
		}catch (Exception e) {
			e.printStackTrace();
			return new OrangeRedisExampleResponse(13,e.getMessage());
		}
	}
	
	@GetMapping("/v1/setCrossOperations/members5")
	public OrangeRedisExampleResponse testCase14() {
		try {
			return new OrangeRedisExampleResponse(setExample8Api.getMembers());
		}catch (Exception e) {
			e.printStackTrace();
			return new OrangeRedisExampleResponse(14,e.getMessage());
		}
	}
	
	@DeleteMapping("/v1/setCrossOperations/allMembers")
	public OrangeRedisExampleResponse testCase24() {
		try {
			setExample4Api.delete();
			setExample5Api.delete();
			setExample6Api.delete();
			setExample7Api.delete();
			setExample8Api.delete();
			return new OrangeRedisExampleResponse();
		}catch (Exception e) {
			e.printStackTrace();
			return new OrangeRedisExampleResponse(24,e.getMessage());
		}
	}
	
}
