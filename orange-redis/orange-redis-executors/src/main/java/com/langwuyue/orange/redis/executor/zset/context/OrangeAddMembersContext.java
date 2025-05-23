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

import java.lang.reflect.Array;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.LinkedHashSet;
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
import com.langwuyue.orange.redis.operations.OrangeRedisZSetOperations.ZSetEntry;

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
		RedisValueTypeEnum valueType
	) {
		super(operationOwner, operationMethod, args, redisKey,valueType);
	}

	public Object getMultipleValue() {
		return multipleValue;
	}
	
	public Set<ZSetEntry> getMembers(){
		return getMembers(null);
	}
	
	private Set<ZSetEntry> getMembers(BiConsumer consumer){
		Set<ZSetEntry> entries = new LinkedHashSet<>();
		if(multipleValue instanceof Collection) {
			Collection members = (Collection)multipleValue;
			for(Object member : members) {
				ZSetEntry entry = toZSetEntry(member,Multiple.class);
				if(entry == null) {
					continue;
				}
				entries.add(entry);
				if(consumer != null) {
					consumer.accept(entry, member);
				}
			}
			return entries;
		}else if(multipleValue instanceof Array) {
			int len = Array.getLength(multipleValue);
			for(int i = 0; i < len; i++) {
				Object member = Array.get(multipleValue, i);
				ZSetEntry entry = toZSetEntry(member,Multiple.class);
				if(entry == null) {
					continue;
				}
				entries.add(entry);
				if(consumer != null) {
					consumer.accept(entry, member);
				}
			}
			return entries;
		}
		else if(multipleValue instanceof Map) {
			Map map = (Map)multipleValue;
			Set<Map.Entry> mapEntries = map.entrySet();
			for(Map.Entry entry : mapEntries) {
				if(entry.getKey() == null || entry.getValue() == null) {
					continue;
				}
				Object score = entry.getValue();
				if(score == null) {
					continue;
				}
				if(!(score instanceof Number) && !(score instanceof String)) {
					throw new OrangeRedisException(String.format("The value of map annotated with @%s must be a number or a string", Multiple.class));
				}
				Object value = entry.getKey();
				if(value == null) {
					throw new OrangeRedisException(String.format("The key of map annotated with @%s cannot be", Multiple.class));
				}
				ZSetEntry zSetEntry = new ZSetEntry(value, Double.valueOf(score.toString()));
				entries.add(zSetEntry);
				if(consumer != null) {
					consumer.accept(zSetEntry, value);
				}
			}
			return entries;	
		}
		
		throw new OrangeRedisException(String.format("The argument annotated with @%s must be a collection or an array or a Map", Multiple.class));
		
	}

	@Override
	public void forEach(BiConsumer t) {
		getMembers(t);
	}

	@Override
	public Object[] toArray() {
		return getMembers().toArray();
	}
	
	@Override
	public boolean continueOnFailure() {
		return continueOnFailure.value(); 
	}
}
