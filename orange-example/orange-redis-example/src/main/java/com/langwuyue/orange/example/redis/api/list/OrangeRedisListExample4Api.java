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
package com.langwuyue.orange.example.redis.api.list;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import com.langwuyue.orange.example.redis.entity.OrangeValueExampleEntity;
import com.langwuyue.orange.redis.annotation.OrangeRedisKey;
import com.langwuyue.orange.redis.annotation.Timeout;
import com.langwuyue.orange.redis.template.list.JSONOperationsTemplate;

/**
 * @author Liang.Zhong
 * @since 1.0.0
 */
@OrangeRedisKey(expirationTime = @Timeout(value = 1, unit = TimeUnit.HOURS), key = "orange:list:example4")
public interface OrangeRedisListExample4Api extends JSONOperationsTemplate<OrangeValueExampleEntity> {

	@Override
	default Map<OrangeValueExampleEntity, Boolean> leftPush(Collection<OrangeValueExampleEntity> members) {
		
		return null;
	}


	@Override
	default Map<OrangeValueExampleEntity, Boolean> rightPush(Collection<OrangeValueExampleEntity> members) {
		
		return null;
	}


	@Override
	default Map<OrangeValueExampleEntity, Long> getIndex(Collection<OrangeValueExampleEntity> members) {
		
		return null;
	}

	@Override
	default Map<OrangeValueExampleEntity, Long> getLastIndex(Collection<OrangeValueExampleEntity> member) {
		
		return null;
	}

	@Override
	default OrangeValueExampleEntity get(Long index) {
		
		return null;
	}

	@Override
	default List<OrangeValueExampleEntity> getByIndexRange(Long start, Long end) {
		
		return null;
	}

	@Override
	default OrangeValueExampleEntity leftPop() {
		
		return null;
	}

	@Override
	default List<OrangeValueExampleEntity> leftPop(Long count) {
		
		return null;
	}

	@Override
	default List<OrangeValueExampleEntity> leftPop(Long value, TimeUnit unit) {
		
		return null;
	}

	@Override
	default OrangeValueExampleEntity rightPop() {
		
		return null;
	}

	@Override
	default List<OrangeValueExampleEntity> rightPop(Long count) {
		
		return null;
	}

	@Override
	default List<OrangeValueExampleEntity> rightPop(Long value, TimeUnit unit) {
		
		return null;
	}


	@Override
	default OrangeValueExampleEntity randomOne() {
		return null;
	}


	@Override
	default List<OrangeValueExampleEntity> random(Long count) {
		return null;
	}


	@Override
	default List<OrangeValueExampleEntity> randomAndDistinct(Long count) {
		return null;
	}
	
	
	
}