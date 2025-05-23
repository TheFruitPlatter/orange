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

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import com.langwuyue.orange.redis.OrangeRedisException;
import com.langwuyue.orange.redis.RedisValueTypeEnum;
import com.langwuyue.orange.redis.annotation.OrangeRedisOperationArg;
import com.langwuyue.orange.redis.annotation.zset.LexRange;
import com.langwuyue.orange.redis.annotation.zset.MaxLex;
import com.langwuyue.orange.redis.annotation.zset.MinLex;
import com.langwuyue.orange.redis.context.OrangeRedisContext;
import com.langwuyue.orange.redis.utils.OrangeReflectionUtils;

/**
 * @author Liang.Zhong
 * @since 1.0.0
 */
public class OrangeLexRangeContext extends OrangeRedisContext {
	
	@OrangeRedisOperationArg(binding = LexRange.class)
	private Object lexRange;

	public OrangeLexRangeContext(
		Class<?> operationOwner, 
		Method operationMethod, 
		Object[] args,
		Key redisKey,
		RedisValueTypeEnum valueType
	) {
		super(operationOwner, operationMethod, args, redisKey,valueType);
	}

	public com.langwuyue.orange.redis.operations.OrangeRedisZSetOperations.LexRange getLexRange() {
		if(lexRange == null) {
			throw new OrangeRedisException(String.format("The argument annotated with @%s cannot be null", LexRange.class));
		}
		if(lexRange instanceof com.langwuyue.orange.redis.operations.OrangeRedisZSetOperations.RankRange) {
			return (com.langwuyue.orange.redis.operations.OrangeRedisZSetOperations.LexRange)lexRange;
		}
		Field maxLexField = null;
		Field minLexField = null;
		Field[] fields = lexRange.getClass().getDeclaredFields();
		for(Field field : fields) {
			if(field.isAnnotationPresent(MaxLex.class)) {
				maxLexField = field;
			}
			else if(field.isAnnotationPresent(MinLex.class)) {
				minLexField = field;
			}
			if(maxLexField != null && minLexField != null) {
				break;
			}
		}
		if(maxLexField == null || minLexField == null) {
			throw new OrangeRedisException(String.format("The argument annotated with @%s must have two fields annotated with @%s and @%s", LexRange.class,MaxLex.class,MinLex.class));
		}
		Object maxLex = OrangeReflectionUtils.getFieldValue(maxLexField, lexRange);
		if(maxLex == null) {
			throw new OrangeRedisException(String.format("The field annotated with @%s cannot be null", MaxLex.class));
		}
		if(!(maxLex instanceof String)) {
			throw new OrangeRedisException(String.format("The field annotated with @%s must be a string", MaxLex.class));
		}
		Object minLex = OrangeReflectionUtils.getFieldValue(minLexField, lexRange);
		if(minLex == null) {
			throw new OrangeRedisException(String.format("The field annotated with @%s cannot be null", MinLex.class));
		}
		if(!(minLex instanceof String)) {
			throw new OrangeRedisException(String.format("The field annotated with @%s must be a string", MinLex.class));
		}
		return new com.langwuyue.orange.redis.operations.OrangeRedisZSetOperations.LexRange(
			maxLex.toString(),
			minLex.toString()
		);
	}
}
