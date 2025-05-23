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
import com.langwuyue.orange.redis.annotation.zset.MaxScore;
import com.langwuyue.orange.redis.annotation.zset.MinScore;
import com.langwuyue.orange.redis.annotation.zset.ScoreRange;
import com.langwuyue.orange.redis.context.OrangeRedisContext;
import com.langwuyue.orange.redis.utils.OrangeReflectionUtils;

/**
 * @author Liang.Zhong
 * @since 1.0.0
 */
public class OrangeScoreRangeContext extends OrangeRedisContext {
	
	@OrangeRedisOperationArg(binding = ScoreRange.class)
	private Object scoreRange;

	public OrangeScoreRangeContext(
		Class<?> operationOwner, 
		Method operationMethod, 
		Object[] args,
		Key redisKey,
		RedisValueTypeEnum valueType
	) {
		super(operationOwner, operationMethod, args, redisKey,valueType);
	}

	public com.langwuyue.orange.redis.operations.OrangeRedisZSetOperations.ScoreRange getScoreRange() {
		if(scoreRange == null) {
			throw new OrangeRedisException(String.format("The argument annotated with @%s cannot be null", ScoreRange.class));
		}
		if(scoreRange instanceof com.langwuyue.orange.redis.operations.OrangeRedisZSetOperations.ScoreRange) {
			return (com.langwuyue.orange.redis.operations.OrangeRedisZSetOperations.ScoreRange)scoreRange;
		}
		Field maxScoreField = null;
		Field minScoreField = null;
		Field[] fields = scoreRange.getClass().getDeclaredFields();
		for(Field field : fields) {
			if(field.isAnnotationPresent(MaxScore.class)) {
				maxScoreField = field;
			}
			else if(field.isAnnotationPresent(MinScore.class)) {
				minScoreField = field;
			}
			if(maxScoreField != null && minScoreField != null) {
				break;
			}
		}
		if(maxScoreField == null || minScoreField == null) {
			throw new OrangeRedisException(String.format("The argument annotated with @%s must have two fields annotated with @%s and @%s", ScoreRange.class,MaxScore.class,MinScore.class));
		}
		Object maxScore = OrangeReflectionUtils.getFieldValue(maxScoreField, scoreRange);
		if(maxScore == null) {
			throw new OrangeRedisException(String.format("The field annotated with @%s cannot be null", MaxScore.class));
		}
		if(!(maxScore instanceof Number) && !(maxScore instanceof String)) {
			throw new OrangeRedisException(String.format("The field annotated with @%s must be a number or a string", MaxScore.class));
		}
		Object minScore = OrangeReflectionUtils.getFieldValue(minScoreField, scoreRange);
		if(minScore == null) {
			throw new OrangeRedisException(String.format("The field annotated with @%s cannot be null", MinScore.class));
		}
		if(!(minScore instanceof Number) && !(minScore instanceof String)) {
			throw new OrangeRedisException(String.format("The field annotated with @%s must be a number or a string", MinScore.class));
		}
		return new com.langwuyue.orange.redis.operations.OrangeRedisZSetOperations.ScoreRange(
			Double.valueOf(maxScore.toString()),
			Double.valueOf(minScore.toString())
		);
	}
}
