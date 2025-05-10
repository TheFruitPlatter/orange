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
package com.langwuyue.orange.redis.template.multiplelocks;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import com.langwuyue.orange.redis.RedisValueTypeEnum;
import com.langwuyue.orange.redis.annotation.multiplelocks.OrangeRedisMultipleLocksClient;

/**
 * @author Liang.Zhong
 * @since 1.0.0
 */
@OrangeRedisMultipleLocksClient(valueType = RedisValueTypeEnum.STRING)
public interface StringOperationsTemplate extends JSONOperationsTemplate<String> {

	@Override
	default Map<String, Boolean> release(Collection<String> targets) {
		return null;
	}

	@Override
	default Map<String, Long> getExpiration(Collection<String> targets) {
		return null;
	}

	@Override
	default Map<String, Long> getExpiration(Collection<String> targets, TimeUnit unit) {
		return null;
	}
	
}
