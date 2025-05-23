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
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;

import com.langwuyue.orange.redis.OrangeRedisException;
import com.langwuyue.orange.redis.RedisValueTypeEnum;
import com.langwuyue.orange.redis.annotation.ContinueOnFailure;
import com.langwuyue.orange.redis.annotation.Multiple;
import com.langwuyue.orange.redis.annotation.OrangeRedisOperationArg;
import com.langwuyue.orange.redis.context.OrangeRedisIterableContext;
import com.langwuyue.orange.redis.context.builder.OrangeMethodAnnotationHandler;
import com.langwuyue.orange.redis.context.builder.OrangeOperationArgMultipleHandler;

/**
 * @author Liang.Zhong
 * @since 1.0.0
 */
public class OrangeAddMembersContext extends OrangeMemberContext implements OrangeRedisIterableContext {
	
	@OrangeRedisOperationArg(binding = Multiple.class, valueHandler = OrangeOperationArgMultipleHandler.class)
	private Object multipleValue;
	
	@OrangeRedisOperationArg(binding = ContinueOnFailure.class, valueHandler = OrangeMethodAnnotationHandler.class)
	private ContinueOnFailure continueOnFailure;
	
	public OrangeAddMembersContext(
		Class<?> operationOwner, 
		Method operationMethod, 
		Object[] args, 
		Key redisKey,
		RedisValueTypeEnum valueType,
		RedisValueTypeEnum keyType
	) {
		super(operationOwner, operationMethod, args, redisKey, valueType,keyType);
	}
	
	private Map getMembers(BiConsumer consumer){
		Map members = new LinkedHashMap();
		if(multipleValue instanceof Collection) {
			Collection values = (Collection)multipleValue;
			for(Object value : values) {
				Map member = toMap(value,Multiple.class);
				if(member == null) {
					continue;
				}
				members.putAll(member);
				if(consumer != null) {
					consumer.accept(member, value);
				}
			}
			return members;
		}else if(multipleValue instanceof Array) {
			int len = Array.getLength(multipleValue);
			for(int i = 0; i < len; i++) {
				Object value = Array.get(multipleValue, i);
				Map member = toMap(value,Multiple.class);
				if(member == null) {
					continue;
				}
				members.putAll(member);
				if(consumer != null) {
					consumer.accept(member, value);
				}
			}
			return members;
		}
		else if(multipleValue instanceof Map) {
			Map map = (Map)multipleValue;
			Set<Map.Entry> mapEntries = map.entrySet();
			for(Map.Entry entry : mapEntries) {
				if(entry.getKey() == null || entry.getValue() == null) {
					continue;
				}
				Object value = entry.getValue();
				Object key = entry.getKey();
				if(key == null) {
					throw new OrangeRedisException(String.format("The key of map annotated with @%s cannot be", Multiple.class));
				}
				members.put(key, value);
				if(consumer != null) {
					Map member = new LinkedHashMap<>();
					member.put(key, value);
					consumer.accept(member, key);
				}
			}
			return members;	
		}
		
		throw new OrangeRedisException(String.format("The argument annotated with @%s must be a collection or an array or a Map", Multiple.class));
		
	}
	
	public Map getMembers(){
		return getMembers(null);
	}

	@Override
	public void forEach(BiConsumer t) {
		getMembers(t);
	}

	@Override
	public Object[] toArray() {
		return getMembers().entrySet().toArray();
	}

	@Override
	public boolean continueOnFailure() {
		return continueOnFailure.value();
	}
}
