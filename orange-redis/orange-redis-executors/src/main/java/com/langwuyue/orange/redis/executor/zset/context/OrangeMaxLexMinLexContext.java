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
package com.langwuyue.orange.redis.executor.zset.context;

import java.lang.reflect.Method;

import com.langwuyue.orange.redis.OrangeRedisException;
import com.langwuyue.orange.redis.RedisValueTypeEnum;
import com.langwuyue.orange.redis.annotation.OrangeRedisOperationArg;
import com.langwuyue.orange.redis.annotation.zset.MaxLex;
import com.langwuyue.orange.redis.annotation.zset.MinLex;
import com.langwuyue.orange.redis.context.OrangeRedisContext;
/**
 * @author Liang.Zhong
 * @since 1.0.0
 */
public class OrangeMaxLexMinLexContext extends OrangeRedisContext {
	
	@OrangeRedisOperationArg(binding = MaxLex.class)
	private Object maxLex;
	
	@OrangeRedisOperationArg(binding = MinLex.class)
	private Object minLex;

	public OrangeMaxLexMinLexContext(
		Class<?> operationOwner, 
		Method operationMethod, 
		Object[] args, 
		Key redisKey,
		RedisValueTypeEnum valueType
	) {
		super(operationOwner, operationMethod, args, redisKey, valueType);
	}
	
	public String getMaxLex() {
		if(maxLex == null) {
			throw new OrangeRedisException(String.format("The field annotated with @%s cannot be null", MaxLex.class));
		}
		if(!(maxLex instanceof String)) {
			throw new OrangeRedisException(String.format("The field annotated with @%s must be a string", MaxLex.class));
		}
		return maxLex.toString();
	}
	
	public String getMinLex() {
		if(minLex == null) {
			throw new OrangeRedisException(String.format("The field annotated with @%s cannot be null", MinLex.class));
		}
		if(!(minLex instanceof String)) {
			throw new OrangeRedisException(String.format("The field annotated with @%s must be a string", MinLex.class));
		}
		return minLex.toString();
	}

}
