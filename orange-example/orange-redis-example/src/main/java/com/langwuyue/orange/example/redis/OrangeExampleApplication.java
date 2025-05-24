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
package com.langwuyue.orange.example.redis;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.langwuyue.orange.redis.configuration.OrangeRedisClientScan;

/**
 * Main entry point for the Orange Redis Example Application.
 * 
 * <p>This Spring Boot application demonstrates various Redis operations using the Orange framework,
 * including examples for different Redis data types (strings, hashes, lists, sets, etc.) and
 * advanced features like transactions, scripting, and geo operations.</p>
 * 
 * <p>The {@code @OrangeRedisClientScan} annotation configures the package scanning for Redis client
 * interfaces, while {@code @SpringBootApplication} enables Spring Boot auto-configuration.</p>
 * 
 * @author Liang.Zhong
 * @since 1.0.0
 * @see org.springframework.boot.SpringApplication
 * @see com.langwuyue.orange.redis.configuration.OrangeRedisClientScan
 */
@OrangeRedisClientScan(basePackages= {"com.langwuyue.orange.example"})
@SpringBootApplication
public class OrangeExampleApplication {
	
	public static void main(String[] args) {
		SpringApplication.run(OrangeExampleApplication.class, args);
	}

}