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
 * Context class for adding multiple members to a Redis hash.
 * 
 * <p>This class extends OrangeMemberContext and implements OrangeRedisIterableContext
 * to provide functionality for batch operations that add multiple members to a Redis hash.
 * It supports different input types including Collections, Arrays, and Maps.
 * 
 * <p>The class processes multiple values annotated with {@link Multiple} and provides
 * iteration capabilities over these values. It also supports continuous operation on
 * failure through the {@link ContinueOnFailure} annotation.
 *
 * @author Liang.Zhong
 * @since 1.0.0
 */
public class OrangeAddMembersContext extends OrangeMemberContext implements OrangeRedisIterableContext {
	
	/**
	 * The multiple values to be added to the Redis hash.
	 * This field can hold a Collection, Array, or Map of values and is processed by OrangeOperationArgMultipleHandler.
	 * It is bound to arguments annotated with {@link Multiple}.
	 */
	@OrangeRedisOperationArg(binding = Multiple.class, valueHandler = OrangeOperationArgMultipleHandler.class)
	private Object multipleValue;
	
	/**
	 * Configuration for handling failures during batch operations.
	 * When true, the operation continues even if some members fail to be added.
	 * When false, the operation stops at the first failure.
	 * This field is bound to the {@link ContinueOnFailure} annotation.
	 */
	@OrangeRedisOperationArg(binding = ContinueOnFailure.class, valueHandler = OrangeMethodAnnotationHandler.class)
	private ContinueOnFailure continueOnFailure;
	
	/**
	 * Constructs a new OrangeAddMembersContext with the specified parameters.
	 *
	 * @param operationOwner    the class that owns the Redis operation
	 * @param operationMethod   the method representing the Redis operation
	 * @param args             the arguments passed to the operation method
	 * @param redisKey         the Redis key for the operation
	 * @param valueType        the type of value stored in Redis
	 * @param keyType         the type of the Redis key
	 */
	public OrangeAddMembersContext(
		Class<?> operationOwner, 
		Method operationMethod, 
		Object[] args, 
		Key redisKey,
		RedisValueTypeEnum valueType,
		RedisValueTypeEnum keyType
	) {
		super(operationOwner, operationMethod, args, redisKey, valueType, keyType);
	}
	
	/**
	 * Internal method to process and convert multiple values into a Map.
	 * Supports Collection, Array, and Map input types.
	 *
	 * @param consumer optional BiConsumer to process each member as it's converted
	 * @return Map containing all processed members
	 * @throws OrangeRedisException if the input type is not supported or if there are null keys in a Map
	 */
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
	
	/**
	 * Gets all members to be added as a Map.
	 * This is a convenience method that calls getMembers(null).
	 *
	 * @return a Map containing all members to be added
	 */
	public Map getMembers(){
		return getMembers(null);
	}

	/**
	 * Applies the given BiConsumer to each member in the multiple values.
	 * This method processes each member and passes it to the consumer along with its original value.
	 *
	 * @param t the BiConsumer to apply to each member
	 */
	@Override
	public void forEach(BiConsumer t) {
		getMembers(t);
	}

	/**
	 * Converts all members to an array of Map.Entry objects.
	 *
	 * @return an array containing all member entries
	 */
	@Override
	public Object[] toArray() {
		return getMembers().entrySet().toArray();
	}

	/**
	 * Determines whether the operation should continue when a failure occurs.
	 * This method returns the value from the ContinueOnFailure annotation.
	 *
	 * @return true if the operation should continue on failure, false if it should stop
	 */
	@Override
	public boolean continueOnFailure() {
		return continueOnFailure.value();
	}
}