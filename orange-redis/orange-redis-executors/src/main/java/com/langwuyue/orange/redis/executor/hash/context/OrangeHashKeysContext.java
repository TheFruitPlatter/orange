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

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.BiConsumer;

import com.langwuyue.orange.redis.OrangeRedisException;
import com.langwuyue.orange.redis.RedisValueTypeEnum;
import com.langwuyue.orange.redis.annotation.ContinueOnFailure;
import com.langwuyue.orange.redis.annotation.Multiple;
import com.langwuyue.orange.redis.annotation.OrangeRedisOperationArg;
import com.langwuyue.orange.redis.annotation.hash.HashKey;
import com.langwuyue.orange.redis.context.OrangeRedisIterableContext;
import com.langwuyue.orange.redis.context.builder.OrangeMethodAnnotationHandler;
import com.langwuyue.orange.redis.context.builder.OrangeOperationArgMultipleHandler;
import com.langwuyue.orange.redis.utils.OrangeReflectionUtils;

/**
 * @author Liang.Zhong
 * @since 1.0.0
 */
public class OrangeHashKeysContext extends OrangeHashContext implements OrangeRedisIterableContext {
	
	@OrangeRedisOperationArg(binding = Multiple.class, valueHandler = OrangeOperationArgMultipleHandler.class)
	private Object hashKeys;
	
	@OrangeRedisOperationArg(binding = ContinueOnFailure.class, valueHandler = OrangeMethodAnnotationHandler.class)
	private ContinueOnFailure continueOnFailure;
	
	private List cachedKeys;
	
	public OrangeHashKeysContext(
		Class<?> operationOwner, 
		Method operationMethod, 
		Object[] args, 
		Key redisKey,
		RedisValueTypeEnum valueType,
		RedisValueTypeEnum keyType
	) {
		super(operationOwner, operationMethod, args, redisKey, valueType,keyType);
	}
	
	public List getHashKeys(){
		return getHashKeys(null);
	}
	
	private List getHashKeys(BiConsumer consumer){
		if(hashKeys instanceof Collection) {
			List keys = new ArrayList<>();	
			Collection values = (Collection)hashKeys;
			int i = 0;
			for(Object value : values) {
				Object key = getKey(value);
				if(key == null) {
					continue;
				}
				keys.add(i,key);
				if(consumer != null) {
					consumer.accept(key, value);
				}
				i++;
			}
			this.cachedKeys = keys;
			return keys;
		}else if(hashKeys instanceof Array) {
			int len = Array.getLength(hashKeys);
			List keys = new ArrayList<>();
			for(int i = 0; i < len; i++) {
				Object value = Array.get(hashKeys, i);
				Object key = getKey(value);
				if(key == null) {
					continue;
				}
				keys.add(i,key);
				if(consumer != null) {
					consumer.accept(key, value);
				}
			}
			this.cachedKeys = keys;
			return keys;
		}
		throw new OrangeRedisException(String.format("The argument annotated with @%s must be a collection or an array", Multiple.class));
	}
	
	private Object getKey(Object member) {
		Field keyField = null;
		Field[] fields = member.getClass().getDeclaredFields();
		for(Field field : fields) {
			if(field.isAnnotationPresent(HashKey.class)) {
				keyField = field;
				break;
			}
		}
		if(keyField == null) {
			return member;
		}
		Object key = OrangeReflectionUtils.getFieldValue(keyField, member);
		if(key == null) {
			return null;
		}
		return key;
	}

	public List getCachedKeys() {
		return cachedKeys;
	}

	@Override
	public void forEach(BiConsumer t) {
		getHashKeys(t);
	}

	@Override
	public Object[] toArray() {
		return getHashKeys().toArray();
	}
	
	@Override
	public boolean continueOnFailure() {
		return continueOnFailure.value();
	}

}
