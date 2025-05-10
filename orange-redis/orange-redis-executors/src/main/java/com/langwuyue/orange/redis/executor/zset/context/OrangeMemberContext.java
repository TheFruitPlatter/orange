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
import java.lang.reflect.Field;
import java.lang.reflect.Method;

import com.langwuyue.orange.redis.OrangeRedisException;
import com.langwuyue.orange.redis.RedisValueTypeEnum;
import com.langwuyue.orange.redis.annotation.RedisValue;
import com.langwuyue.orange.redis.annotation.zset.Score;
import com.langwuyue.orange.redis.context.OrangeRedisContext;
import com.langwuyue.orange.redis.operations.OrangeRedisZSetOperations.ZSetEntry;
import com.langwuyue.orange.redis.utils.OrangeReflectionUtils;

/**
 * @author Liang.Zhong
 * @since 1.0.0
 */
public class OrangeMemberContext extends OrangeRedisContext {

	public OrangeMemberContext(
		Class<?> operationOwner, 
		Method operationMethod, 
		Object[] args, 
		Key redisKey,
		RedisValueTypeEnum valueType
	) {
		super(operationOwner, operationMethod, args, redisKey, valueType);
	}

	protected ZSetEntry toZSetEntry(Object member,Class<? extends Annotation> annotationClass) {
		if(member == null) {
			return null;
		}
		if(member instanceof ZSetEntry) {
			return (ZSetEntry)member;
		}
		Field scoreField = null;
		Field valueField = null;
		Field[] fields = member.getClass().getDeclaredFields();
		for(Field field : fields) {
			if(field.isAnnotationPresent(Score.class)) {
				scoreField = field;
			}
			else if(field.isAnnotationPresent(RedisValue.class)) {
				valueField = field;
			}
			if(scoreField != null && valueField != null) {
				break;
			}
		}
		if(scoreField == null || valueField == null) {
			throw new OrangeRedisException(String.format("The argument annotated with @%s must have two fields annotated with @%s and @%s", annotationClass,RedisValue.class,Score.class));
		}
		Object score = OrangeReflectionUtils.getFieldValue(scoreField, member);
		if(score == null) {
			return null;
		}
		if(!(score instanceof Number) && !(score instanceof String)) {
			throw new OrangeRedisException(String.format("The field annotated with @%s must be a number or a string", Score.class));
		}
		Object value = OrangeReflectionUtils.getFieldValue(valueField, member);
		if(value == null) {
			throw new OrangeRedisException(String.format("The field annotated with @%s cannot be null", RedisValue.class));
		}
		return newZSetEntry(value,score);
	}
	
	protected ZSetEntry newZSetEntry(Object value,Object score) {
		return new ZSetEntry(value, Double.valueOf(score.toString()));
	}
}
