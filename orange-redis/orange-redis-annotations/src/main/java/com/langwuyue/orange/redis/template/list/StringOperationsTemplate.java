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
package com.langwuyue.orange.redis.template.list;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import com.langwuyue.orange.redis.RedisValueTypeEnum;
import com.langwuyue.orange.redis.annotation.list.OrangeRedisListClient;

/**
 * Interface template for Redis multiple locks operations. 
 * Developers should extend this interface and annotate the child interface with {@code OrangeRedisKey}.
 * 
 * <p>A example is:
 * <blockquote><pre>
 *  {@code @OrangeRedisKey(expirationTime = @Timeout(value = 1, unit = TimeUnit.HOURS), key = "orange:list:example1")} 
 *  public interface OrangeRedisMultipleLocksExample1Api extends StringOperationsTemplate {
 *  	
 *  }
 * </pre></blockquote>
 * </p>
 * 
 * <p>Please review examples for more information.</p>
 * 
 * @author Liang.Zhong
 * @since 1.0.0
 */
@OrangeRedisListClient(valueType = RedisValueTypeEnum.STRING)
public interface StringOperationsTemplate extends JSONOperationsTemplate<String> {

	@Override
	default Map<String, Boolean> leftPush(Collection<String> members) {
		
		return null;
	}


	@Override
	default Map<String, Boolean> rightPush(Collection<String> members) {
		
		return null;
	}


	@Override
	default Map<String, Long> getIndex(Collection<String> members) {
		
		return null;
	}

	@Override
	default Map<String, Long> getLastIndex(Collection<String> member) {
		
		return null;
	}

	@Override
	default String get(Long index) {
		
		return null;
	}

	@Override
	default List<String> getByIndexRange(Long start, Long end) {
		
		return null;
	}

	@Override
	default String leftPop() {
		
		return null;
	}

	@Override
	default List<String> leftPop(Long count) {
		
		return null;
	}

	@Override
	default List<String> leftPop(Long value, TimeUnit unit) {
		
		return null;
	}

	@Override
	default String rightPop() {
		
		return null;
	}

	@Override
	default List<String> rightPop(Long count) {
		
		return null;
	}

	@Override
	default List<String> rightPop(Long value, TimeUnit unit) {
		
		return null;
	}
	
	@Override
	default String randomOne() {
		return null;
	}


	@Override
	default List<String> random(Long count) {
		return null;
	}


	@Override
	default List<String> randomAndDistinct(Long count) {
		return null;
	}
}
