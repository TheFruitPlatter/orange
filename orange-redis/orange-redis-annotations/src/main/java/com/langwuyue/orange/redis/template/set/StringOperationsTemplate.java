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
package com.langwuyue.orange.redis.template.set;

import java.util.List;
import java.util.Map;
import java.util.Set;

import com.langwuyue.orange.redis.RedisValueTypeEnum;
import com.langwuyue.orange.redis.annotation.set.OrangeRedisSetClient;

/**
 * Interface template for Redis Set operations. 
 * Developers should extend this interface and annotate the child interface with {@code OrangeRedisKey}.
 * 
 * <p>A example is:
 * <blockquote><pre>
 *  {@code @OrangeRedisKey(expirationTime = @Timeout(value = 1, unit = TimeUnit.HOURS), key = "orange:set:example1")} 
 *  public interface OrangeRedisSetExample1Api extends StringOperationsTemplate {
 *  
 *  }
 * </pre></blockquote>
 * 
 * 
 * <p>Please review examples for more information.
 * 
 * @author Liang.Zhong
 * @since 1.0.0
 */
@OrangeRedisSetClient(valueType = RedisValueTypeEnum.STRING)
public interface StringOperationsTemplate extends JSONOperationsTemplate<String> {
	
	@Override
	default Map<String, Boolean> add(Set<String> members) {
		
		return null;
	}

	@Override
	default String randomGetOne() {
		
		return null;
	}

	@Override
	default List<String> randomGetMembers(Long count) {
		
		return null;
	}

	@Override
	default Set<String> distinctRandomGetMembers(Long count) {
		
		return null;
	}

	@Override
	default Set<String> getMembers() {
		
		return null;
	}


	@Override
	default Map<String, Boolean> isMembers(Set<String> members) {
		
		return null;
	}

	@Override
	default String pop() {
		
		return null;
	}

	@Override
	default Set<String> pop(Long count) {
		
		return null;
	}

	@Override
	default Map<String, Boolean> remove(Set<String> values) {
		
		return null;
	}

	@Override
	default Set<String> scan(String pattern, Long count, Long cursor) {
		return null;
	}
}
