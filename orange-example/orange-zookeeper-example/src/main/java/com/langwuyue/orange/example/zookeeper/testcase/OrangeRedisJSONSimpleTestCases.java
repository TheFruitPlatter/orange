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
package com.langwuyue.orange.example.zookeeper.testcase;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RestController;

import com.langwuyue.orange.example.zookeeper.api.ZookeeperExampleApi;
import com.langwuyue.orange.example.zookeeper.response.OrangeZookeeperExampleResponse;


/**
 * @author Liang.Zhong
 * @since 1.0.0
 */
@RestController
public class OrangeRedisJSONSimpleTestCases {
	
	@Autowired
	private ZookeeperExampleApi zookeeperExampleApi;
	
	@PutMapping("/v1/pathCreations")
	public OrangeZookeeperExampleResponse createPathIdempotently() {
		try {
			zookeeperExampleApi.create();
			return new OrangeZookeeperExampleResponse();
		}catch (Exception e) {
			e.printStackTrace();
			return new OrangeZookeeperExampleResponse(1,e.getMessage());
		}
		
	}
}
