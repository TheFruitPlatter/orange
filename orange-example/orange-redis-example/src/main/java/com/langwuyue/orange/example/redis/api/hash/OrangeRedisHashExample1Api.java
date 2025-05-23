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
package com.langwuyue.orange.example.redis.api.hash;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import com.langwuyue.orange.example.redis.entity.OrangeValueExampleEntity;
import com.langwuyue.orange.redis.annotation.OrangeRedisKey;
import com.langwuyue.orange.redis.annotation.Timeout;
import com.langwuyue.orange.redis.template.hash.JSONOperationsTemplate;

/**
 * @author Liang.Zhong
 * @since 1.0.0
 */
@OrangeRedisKey(expirationTime = @Timeout(value = 1, unit = TimeUnit.HOURS), key = "orange:hash:example1")
public interface OrangeRedisHashExample1Api extends JSONOperationsTemplate<OrangeValueExampleEntity> {

	@Override
	default List<OrangeValueExampleEntity> getValues() {
		
		return null;
	}

	@Override
	default OrangeValueExampleEntity get(String key) {
		
		return null;
	}

	@Override
	default Map<String, OrangeValueExampleEntity> get(Collection<String> key) {
		
		return null;
	}

	@Override
	default Map<String, OrangeValueExampleEntity> getAllMembers() {
		
		return null;
	}
}