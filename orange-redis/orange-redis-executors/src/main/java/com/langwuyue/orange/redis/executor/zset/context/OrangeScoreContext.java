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

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

import com.langwuyue.orange.redis.OrangeRedisException;
import com.langwuyue.orange.redis.RedisValueTypeEnum;
import com.langwuyue.orange.redis.annotation.OrangeRedisOperationArg;
import com.langwuyue.orange.redis.annotation.zset.Score;
import com.langwuyue.orange.redis.context.OrangeRedisValueContext;

/**
 * @author Liang.Zhong
 * @since 1.0.0
 */
public class OrangeScoreContext extends OrangeRedisValueContext {
	
	@OrangeRedisOperationArg(binding = Score.class)
	private Object score;

	public OrangeScoreContext(
		Class<?> operationOwner, 
		Method operationMethod, 
		Object[] args,
		Key redisKey,
		RedisValueTypeEnum valueType
	) {
		super(operationOwner, operationMethod, args, redisKey,valueType);
	}

	public Double getScore() {
		return getScore(score,Score.class);
	}
	
	public Double getNullableScore() {
		return getScore(score,Score.class);
	}
	
	protected Double getScore(Object score,Class<? extends Annotation> annotationClass) {
		Double doubleScore = getNullableScore(score,annotationClass);
		if(doubleScore == null) {
			throw new OrangeRedisException(String.format("The argument annotated with @%s cannot be null", annotationClass));
		}
		return doubleScore;
	}
	
	protected Double getNullableScore(Object score, Class<? extends Annotation> annotationClass) {
		if(score == null) {
			return null;
		}
		if(score instanceof Number || score instanceof String) {
			// It's risky for a long value greater than 2^53 when converting to double due to potential precision loss.
			return Double.valueOf(score.toString());
		}
		throw new OrangeRedisException(String.format("The argument annotated with @%s must be a number or a string", annotationClass));
	}
}
