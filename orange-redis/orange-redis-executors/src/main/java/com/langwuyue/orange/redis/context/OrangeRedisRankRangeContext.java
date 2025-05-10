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

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import com.langwuyue.orange.redis.OrangeRedisException;
import com.langwuyue.orange.redis.RedisValueTypeEnum;
import com.langwuyue.orange.redis.annotation.EndIndex;
import com.langwuyue.orange.redis.annotation.OrangeRedisOperationArg;
import com.langwuyue.orange.redis.annotation.StartIndex;
import com.langwuyue.orange.redis.annotation.zset.RankRange;
import com.langwuyue.orange.redis.utils.OrangeReflectionUtils;

/**
 * @author Liang.Zhong
 * @since 1.0.0
 */
public class OrangeRedisRankRangeContext extends OrangeRedisContext {
	
	@OrangeRedisOperationArg(binding = RankRange.class)
	private Object rankRange;

	public OrangeRedisRankRangeContext(
		Class<?> operationOwner, 
		Method operationMethod, 
		Object[] args,
		Key redisKey,
		RedisValueTypeEnum valueType
	) {
		super(operationOwner, operationMethod, args, redisKey,valueType);
	}

	public com.langwuyue.orange.redis.operations.OrangeRedisZSetOperations.RankRange getRankRange() {
		if(rankRange == null) {
			throw new OrangeRedisException(String.format("The argument annotated @%s cannot be null", RankRange.class));
		}
		if(rankRange instanceof com.langwuyue.orange.redis.operations.OrangeRedisZSetOperations.RankRange) {
			return (com.langwuyue.orange.redis.operations.OrangeRedisZSetOperations.RankRange)rankRange;
		}
		Field startField = null;
		Field endField = null;
		Field[] fields = rankRange.getClass().getDeclaredFields();
		for(Field field : fields) {
			if(field.isAnnotationPresent(StartIndex.class)) {
				startField = field;
			}
			else if(field.isAnnotationPresent(EndIndex.class)) {
				endField = field;
			}
			if(startField != null && endField != null) {
				break;
			}
		}
		if(startField == null || endField == null) {
			throw new OrangeRedisException(String.format("The argument annotated @%s must have two fields annotated with @%s and @%s", RankRange.class,StartIndex.class,EndIndex.class));
		}
		Object startIndex = OrangeReflectionUtils.getFieldValue(startField, rankRange);
		if(startIndex == null) {
			throw new OrangeRedisException(String.format("The field annotated @%s cannot be null", StartIndex.class));
		}
		if(!(OrangeReflectionUtils.isInteger(startIndex.getClass())) && !(startIndex instanceof String)) {
			throw new OrangeRedisException(String.format("The field annotated @%s must be a integer or a string", StartIndex.class));
		}
		Object endIndex = OrangeReflectionUtils.getFieldValue(endField, rankRange);
		if(endIndex == null) {
			throw new OrangeRedisException(String.format("The field annotated @%s cannot be null", EndIndex.class));
		}
		if(!(OrangeReflectionUtils.isInteger(endIndex.getClass())) && !(endIndex instanceof String)) {
			throw new OrangeRedisException(String.format("The field annotated @%s must be a integer or a string", EndIndex.class));
		}
		return new com.langwuyue.orange.redis.operations.OrangeRedisZSetOperations.RankRange(
			Long.valueOf(startIndex.toString()),
			Long.valueOf(endIndex.toString())
		);
	}
}
