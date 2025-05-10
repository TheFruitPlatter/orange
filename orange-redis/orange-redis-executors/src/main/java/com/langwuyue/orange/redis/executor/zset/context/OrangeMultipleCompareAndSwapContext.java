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
import com.langwuyue.orange.redis.annotation.Multiple;
import com.langwuyue.orange.redis.annotation.RedisValue;
import com.langwuyue.orange.redis.annotation.zset.OldScore;
import com.langwuyue.orange.redis.operations.OrangeRedisZSetOperations.CASZSetEntry;
import com.langwuyue.orange.redis.operations.OrangeRedisZSetOperations.ZSetEntry;
import com.langwuyue.orange.redis.utils.OrangeReflectionUtils;

/**
 * @author Liang.Zhong
 * @since 1.0.0
 */
public class OrangeMultipleCompareAndSwapContext extends OrangeAddMembersContext {
	
	public OrangeMultipleCompareAndSwapContext(
		Class<?> operationOwner, 
		Method operationMethod, 
		Object[] args,
		Key redisKey,
		RedisValueTypeEnum valueType
	) {
		super(operationOwner, operationMethod, args, redisKey,valueType);
	}

	protected ZSetEntry toZSetEntry(Object member) {
		if(member == null) {
			return null;
		}
		ZSetEntry entry = super.toZSetEntry(member,Multiple.class);
		if(entry == null) {
			entry = toEmptyZSetEntry(member);
		}
		CASZSetEntry casEntry = (CASZSetEntry) entry;
		casEntry.setOldScore(getOldScore(member));
		return casEntry;
	}

	@Override
	public ZSetEntry newZSetEntry(Object value, Object score) {
		return new CASZSetEntry(value, Double.valueOf(score.toString()));
	}
	
	private Double getNullableScore(Object score, Class<? extends Annotation> annotationClass) {
		if(score == null) {
			return null;
		}
		if(score instanceof Number || score instanceof String) {
			// It's risky for a long value greater than 2^53 when converting to double due to potential precision loss.
			return Double.valueOf(score.toString());
		}
		throw new OrangeRedisException(String.format("The argument annotated with @%s must be a number or a string", annotationClass));
	}
	
	private ZSetEntry toEmptyZSetEntry(Object member) {
		Field valueField = null;
		Field[] fields = member.getClass().getDeclaredFields();
		for(Field field : fields) {
			if(field.isAnnotationPresent(RedisValue.class)) {
				valueField = field;
				break;
			}
		}
		Object value = OrangeReflectionUtils.getFieldValue(valueField, member);
		return new CASZSetEntry(value, null);
	}
	
	private Double getOldScore(Object member) {
		Field oldScoreField = null;
		Field[] fields = member.getClass().getDeclaredFields();
		for(Field field : fields) {
			if(field.isAnnotationPresent(OldScore.class)) {
				oldScoreField = field;
				break;
			}
		}
		if(oldScoreField == null) {
			throw new OrangeRedisException(String.format("The field annotated with @%s cannot be null", OldScore.class));
		}
		Object oldScore = OrangeReflectionUtils.getFieldValue(oldScoreField, member);
		return getNullableScore(oldScore,OldScore.class);
	}
}
