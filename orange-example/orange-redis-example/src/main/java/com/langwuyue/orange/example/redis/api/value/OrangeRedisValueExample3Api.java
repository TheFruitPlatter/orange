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
package com.langwuyue.orange.example.redis.api.value;

import java.util.concurrent.TimeUnit;

import com.langwuyue.orange.redis.annotation.OrangeRedisKey;
import com.langwuyue.orange.redis.annotation.Timeout;
import com.langwuyue.orange.redis.template.value.DoubleOperationsTemplate;

/**
 * Example interface demonstrating Redis value operations for Double values.
 * 
 * <p>Similar to {@link OrangeRedisValueExample2Api} but for {@code double} values.
 * Uses the same Redis commands (INCRBYFLOAT, DECRBYFLOAT, GET, SET) but with
 * floating-point precision.</p>
 * 
 * <p>Key configuration:
 * <ul>
 *   <li>Fixed key prefix: "orange:value:example3"</li>
 *   <li>Default expiration: 1 hour</li>
 * </ul>
 * 
 * @author Liang.Zhong
 * @since 1.0.0
 * @see DoubleOperationsTemplate Base template for Double operations
 * @see OrangeRedisValueExample2Api Similar example for Long values
 */
@OrangeRedisKey(expirationTime = @Timeout(value = 1, unit = TimeUnit.HOURS), key = "orange:value:example3")
public interface OrangeRedisValueExample3Api extends DoubleOperationsTemplate {
	
}