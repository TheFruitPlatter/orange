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
package com.langwuyue.orange.redis.executor.hash.context;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.LinkedHashMap;
import java.util.Map;

import com.langwuyue.orange.redis.OrangeRedisException;
import com.langwuyue.orange.redis.RedisValueTypeEnum;
import com.langwuyue.orange.redis.annotation.RedisValue;
import com.langwuyue.orange.redis.annotation.hash.HashKey;
import com.langwuyue.orange.redis.utils.OrangeReflectionUtils;

/**
 * @author Liang.Zhong
 * @since 1.0.0
 */
public class OrangeMemberContext extends OrangeHashContext {
	
	public OrangeMemberContext(
		Class<?> operationOwner, 
		Method operationMethod, 
		Object[] args,
		Key redisKey,
		RedisValueTypeEnum valueType,
		RedisValueTypeEnum keyType
	) {
		super(operationOwner, operationMethod, args, redisKey,valueType,keyType);
	}

	protected Map toMap(Object member,Class<? extends Annotation> annotationClass) {
		Map map = new LinkedHashMap<>();
		if(member == null) {
			return map;
		}
		if(member instanceof Map) {
			return (Map)member;
		}
		Field valueField = null;
		Field keyField = null;
		Field[] fields = member.getClass().getDeclaredFields();
		for(Field field : fields) {
			if(field.isAnnotationPresent(HashKey.class)) {
				keyField = field;
			}
			else if(field.isAnnotationPresent(RedisValue.class)) {
				valueField = field;
			}
			if(keyField != null && valueField != null) {
				break;
			}
		}
		if(keyField == null || valueField == null) {
			throw new OrangeRedisException(String.format("The argument annotated with @%s must have two fields annotated with @%s and @%s", annotationClass,RedisValue.class,HashKey.class));
		}
		Object key = OrangeReflectionUtils.getFieldValue(keyField, member);
		if(key == null) {
			return map;
		}
		Object value = OrangeReflectionUtils.getFieldValue(valueField, member);
		if(value == null) {
			throw new OrangeRedisException(String.format("The field annotated with @%s cannot be null", RedisValue.class));
		}
		map.put(key, value);
		return map;
	}
	
}
