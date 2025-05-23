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
package com.langwuyue.orange.redis.context;

import java.lang.reflect.Method;

import com.langwuyue.orange.redis.OrangeRedisException;
import com.langwuyue.orange.redis.RedisValueTypeEnum;
import com.langwuyue.orange.redis.annotation.EndIndex;
import com.langwuyue.orange.redis.annotation.OrangeRedisOperationArg;
import com.langwuyue.orange.redis.annotation.StartIndex;
import com.langwuyue.orange.redis.utils.OrangeReflectionUtils;

/**
 * @author Liang.Zhong
 * @since 1.0.0
 */
public class OrangeRedisStartIndexEndIndexContext extends OrangeRedisContext {
	
	@OrangeRedisOperationArg(binding = StartIndex.class)
	private Object startIndex;
	
	@OrangeRedisOperationArg(binding = EndIndex.class)
	private Object endIndex;

	public OrangeRedisStartIndexEndIndexContext(
		Class<?> operationOwner, 
		Method operationMethod, 
		Object[] args, 
		Key redisKey,
		RedisValueTypeEnum valueType
	) {
		super(operationOwner, operationMethod, args, redisKey, valueType);
	}
	
	public Long getStartIndex() {
		if(startIndex == null) {
			throw new OrangeRedisException(String.format("The argument annotated with @%s cannot be null", StartIndex.class));
		}
		if(!(OrangeReflectionUtils.isInteger(startIndex.getClass())) && !(startIndex instanceof String)) {
			throw new OrangeRedisException(String.format("The argument annotated with @%s must be a integer or a string", StartIndex.class));
		}
		return Long.valueOf(startIndex.toString());
	}
	
	public Long getEndIndex() {
		if(endIndex == null) {
			throw new OrangeRedisException(String.format("The argument annotated with @%s cannot be null", EndIndex.class));
		}
		if(!(OrangeReflectionUtils.isInteger(endIndex.getClass())) && !(endIndex instanceof String)) {
			throw new OrangeRedisException(String.format("The argument annotated with @%s must be a integer or a string", EndIndex.class));
		}
		return Long.valueOf(endIndex.toString());
	}

}
