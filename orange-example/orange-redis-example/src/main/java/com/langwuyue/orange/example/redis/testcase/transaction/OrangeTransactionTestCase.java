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
package com.langwuyue.orange.example.redis.testcase.transaction;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.langwuyue.orange.example.redis.api.transaction.OrangeRedisTransactionExample1Api;
import com.langwuyue.orange.example.redis.entity.OrangeValueExampleEntity;
import com.langwuyue.orange.example.redis.response.OrangeRedisExampleResponse;

/**
 * @author Liang.Zhong
 * @since 1.0.0
 */
@RestController
public class OrangeTransactionTestCase {
	
	@Autowired
	private OrangeRedisTransactionExample1Api transactionExampleApi;
	
	@Autowired
	private OrangeNormalService service;
	
	@Autowired
	private OrangeTransactionService txService;
	
	@PutMapping("/v1/transactionOperations/values")
	public OrangeRedisExampleResponse testCase1(@Valid @RequestBody OrangeValueExampleEntity entity) {
		try {
			return new OrangeRedisExampleResponse(transactionExampleApi.setValue(entity));
		}catch (Exception e) {
			e.printStackTrace();
			return new OrangeRedisExampleResponse(1,e.getMessage());
		}
	}
	
	@PutMapping("/v1/transactionOperations/commitedValues")
	public OrangeRedisExampleResponse testCase2(@RequestParam Long version) {
		try {
			return new OrangeRedisExampleResponse(transactionExampleApi.commit(version));
		}catch (Exception e) {
			e.printStackTrace();
			return new OrangeRedisExampleResponse(2,e.getMessage());
		}
	}
	
	@GetMapping("/v1/transactionOperations/values")
	public OrangeRedisExampleResponse testCase4() {
		try {
			return new OrangeRedisExampleResponse(transactionExampleApi.getValue());
		}catch (Exception e) {
			e.printStackTrace();
			return new OrangeRedisExampleResponse(4,e.getMessage());
		}
	}
	
	@PostMapping("/v1/transactionOperations/values")
	public OrangeRedisExampleResponse testCase5(@Valid @RequestBody OrangeValueExampleEntity entity) {
		try {
			service.addUserWithTransaction(entity);
			return new OrangeRedisExampleResponse();
		}catch (Exception e) {
			e.printStackTrace();
			return new OrangeRedisExampleResponse(5,e.getMessage());
		}
	}
	
	@PutMapping("/v1/transactionOperations/modification")
	public OrangeRedisExampleResponse testCase6(@Valid @RequestBody OrangeValueExampleEntity entity) {
		try {
			txService.updateUserWithTransaction(entity);
			return new OrangeRedisExampleResponse();
		}catch (Exception e) {
			e.printStackTrace();
			return new OrangeRedisExampleResponse(5,e.getMessage());
		}
	}
}
