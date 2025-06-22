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
package com.langwuyue.orange.redis;

/**
 * Redis value type enum, identifies data types stored in Redis.
 * 
 * <p>This enum defines various value types supported in Redis operations,
 * used for type conversion and serialization/deserialization processing.</p>
 * 
 * @author Liang.Zhong
 * @since 1.0.0
 */
public enum RedisValueTypeEnum {
	
	/**
	 * String type, suitable for simple string values
	 * <p>Example: Caching simple text information</p>
	 */
	STRING,
	
	/**
	 * JSON format, suitable for complex objects
	 * <p>Example: Storing user information, configuration objects and other structured data</p>
	 */
	JSON,
	
	/**
	 * Long integer number, suitable for integer values
	 * <p>Example: Counters, IDs and other integer values</p>
	 */
	LONG,
	
	/**
	 * Double precision floating point, suitable for decimal values
	 * <p>Example: Prices, ratings and other scenarios requiring decimals</p>
	 */
	DOUBLE,
	
	;

}