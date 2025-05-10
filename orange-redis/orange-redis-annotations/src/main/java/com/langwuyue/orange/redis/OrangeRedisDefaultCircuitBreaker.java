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

import java.lang.reflect.Method;

public class OrangeRedisDefaultCircuitBreaker implements OrangeRedisCircuitBreaker {

	@Override
	public void onException(String key, Class<?> operationOwner, Method operation, Object[] args, Exception e) {
		throw new RuntimeException(String.format("key:%s;\n operationOwner: %s;\n operation: %s;\n", key,operationOwner,operation),e);
	}

	@Override
	public void outOfService(String key, Class<?> operationOwner, Method operation, Object[] args) {
		throw new RuntimeException("Redis out of service");
	}
}
