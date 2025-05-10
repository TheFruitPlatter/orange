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
package com.langwuyue.orange.example.redis.api.set;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import com.langwuyue.orange.example.redis.entity.OrangeValueExampleEntity;
import com.langwuyue.orange.redis.annotation.OrangeRedisKey;
import com.langwuyue.orange.redis.annotation.Timeout;
import com.langwuyue.orange.redis.template.set.JSONOperationsTemplate;

/**
 * @author Liang.Zhong
 * @since 1.0.0
 */
@OrangeRedisKey(expirationTime = @Timeout(value = 1, unit = TimeUnit.HOURS), key = "orange:set:example7")
public interface OrangeRedisSetExample7Api extends JSONOperationsTemplate<OrangeValueExampleEntity> {


	@Override
	default Map<OrangeValueExampleEntity, Boolean> add(Set<OrangeValueExampleEntity> members) {
		
		return null;
	}

	@Override
	default OrangeValueExampleEntity randomGetOne() {
		
		return null;
	}

	@Override
	default List<OrangeValueExampleEntity> randomGetMembers(Long count) {
		
		return null;
	}

	@Override
	default Set<OrangeValueExampleEntity> distinctRandomGetMembers(Long count) {
		
		return null;
	}

	@Override
	default Set<OrangeValueExampleEntity> getMembers() {
		
		return null;
	}


	@Override
	default Map<OrangeValueExampleEntity, Boolean> isMembers(Set<OrangeValueExampleEntity> members) {
		
		return null;
	}

	@Override
	default OrangeValueExampleEntity pop() {
		
		return null;
	}

	@Override
	default Set<OrangeValueExampleEntity> pop(Long count) {
		
		return null;
	}

	@Override
	default Map<OrangeValueExampleEntity, Boolean> remove(Set<OrangeValueExampleEntity> values) {
		
		return null;
	}

	
}