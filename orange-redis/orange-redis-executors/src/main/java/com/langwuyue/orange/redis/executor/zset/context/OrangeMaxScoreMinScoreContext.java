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
import com.langwuyue.orange.redis.annotation.zset.MaxScore;
import com.langwuyue.orange.redis.annotation.zset.MinScore;
import com.langwuyue.orange.redis.context.OrangeRedisContext;

/**
 * @author Liang.Zhong
 * @since 1.0.0
 */
public class OrangeMaxScoreMinScoreContext extends OrangeRedisContext {
	
	@OrangeRedisOperationArg(binding = MaxScore.class)
	private Object maxScore;
	
	@OrangeRedisOperationArg(binding = MinScore.class)
	private Object minScore;

	public OrangeMaxScoreMinScoreContext(
		Class<?> operationOwner, 
		Method operationMethod, 
		Object[] args, 
		Key redisKey,
		RedisValueTypeEnum valueType
	) {
		super(operationOwner, operationMethod, args, redisKey, valueType);
	}
	
	public Double getMaxScore() {
		return getScore(maxScore,MaxScore.class);
	}
	
	public Double getMinScore() {
		return getScore(minScore,MinScore.class);
	}
	
	protected Double getScore(Object score,Class<? extends Annotation> annotationClass) {
		if(score instanceof Number || score instanceof String) {
			return Double.valueOf(score.toString());
		}
		throw new OrangeRedisException(String.format("The argument annotated with @%s must be a number or a string", annotationClass));
	}

}
