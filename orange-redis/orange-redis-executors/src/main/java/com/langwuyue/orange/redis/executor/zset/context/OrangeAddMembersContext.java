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
 * Context class for batch adding multiple members to a Redis ZSet.
 * 
 * <p>This class extends {@link OrangeMemberContext} and implements {@link OrangeRedisIterableContext}
 * to provide functionality for adding multiple members to a Redis sorted set. It handles:
 * <ul>
 *   <li>Multiple member validation and conversion</li>
 *   <li>Support for various collection types (Collection, Array, Map)</li>
 *   <li>Batch processing with optional failure handling</li>
 * </ul>
 * 
 * <p>The class requires a collection of members annotated with {@link Multiple} which can be:
 * <ul>
 *   <li>A Collection of ZSetEntry instances or annotated objects</li>
 *   <li>An Array of ZSetEntry instances or annotated objects</li>
 *   <li>A Map where keys are values and values are scores</li>
 * </ul>
 *
 * @author Liang.Zhong
 * @since 1.0.0
 */
public class OrangeAddMembersContext extends OrangeMemberContext implements OrangeRedisIterableContext {
	
    /**
     * The collection of members to be added to the Redis ZSet.
     * This field is bound to method parameters annotated with {@link Multiple}.
     */
	@OrangeRedisOperationArg(binding = Multiple.class, valueHandler = OrangeOperationArgMultipleHandler.class)
	private Object multipleValue;
	
    /**
     * Configuration for handling failures during batch processing.
     * When true, the operation continues even if some members fail to be added.
     */
	@OrangeRedisOperationArg(binding = ContinueOnFailure.class, valueHandler = OrangeMethodAnnotationHandler.class)
	private ContinueOnFailure continueOnFailure;
	
    /**
     * Constructs a new OrangeAddMembersContext.
     *
     * @param operationOwner The class that owns the Redis operation
     * @param operationMethod The method representing the Redis operation
     * @param args The arguments passed to the operation method
     * @param redisKey The Redis key for the operation
     * @param valueType The type of values stored in the Redis ZSet
     */
	public OrangeAddMembersContext(
		Class<?> operationOwner, 
		Method operationMethod, 
		Object[] args,
		Key redisKey,
		RedisValueTypeEnum valueType
	) {
		super(operationOwner, operationMethod, args, redisKey,valueType);
	}

    /**
     * Gets the raw multiple value object.
     *
     * @return The raw collection, array, or map of members
     */
	public Object getMultipleValue() {
		return multipleValue;
	}
	
    /**
     * Gets all members as a set of ZSetEntry objects.
     * 
     * <p>This method converts all members from the multiple value object
     * to ZSetEntry instances, filtering out null entries.
     *
     * @return A set of ZSetEntry objects ready to be added to Redis
     * @throws OrangeRedisException if the multiple value is of an unsupported type
     */
	public Set<ZSetEntry> getMembers(){
		return getMembers(null);
	}
	
    /**
     * Gets all members as a set of ZSetEntry objects with optional consumer processing.
     * 
     * <p>This method:
     * <ul>
     *   <li>Converts all members from the multiple value object to ZSetEntry instances</li>
     *   <li>Filters out null entries</li>
     *   <li>Optionally applies a consumer function to each entry and its original object</li>
     * </ul>
     *
     * <p>The method supports three types of multiple values:
     * <ul>
     *   <li>Collection - each element is converted to a ZSetEntry</li>
     *   <li>Array - each element is converted to a ZSetEntry</li>
     *   <li>Map - keys become values and values become scores in ZSetEntries</li>
     * </ul>
     *
     * @param consumer Optional BiConsumer to process each entry and its original object
     * @return A set of ZSetEntry objects ready to be added to Redis
     * @throws OrangeRedisException if the multiple value is of an unsupported type or contains invalid entries
     */
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

    /**
     * Iterates through all members, applying the given consumer to each entry.
     * 
     * <p>Implementation of {@link OrangeRedisIterableContext#forEach}.
     * This method processes each member and passes the resulting ZSetEntry
     * and original object to the consumer.
     *
     * @param t The consumer to apply to each entry
     */
	@Override
	public void forEach(BiConsumer t) {
		getMembers(t);
	}

    /**
     * Converts all members to an array.
     * 
     * <p>Implementation of {@link OrangeRedisIterableContext#toArray}.
     *
     * @return An array containing all ZSetEntry objects
     */
	@Override
	public Object[] toArray() {
		return getMembers().toArray();
	}
	
    /**
     * Determines whether to continue processing on failure.
     * 
     * <p>Implementation of {@link OrangeRedisIterableContext#continueOnFailure}.
     * This method returns the value from the {@link ContinueOnFailure} annotation.
     *
     * @return true if processing should continue on failure, false otherwise
     */
	@Override
	public boolean continueOnFailure() {
		return continueOnFailure.value(); 
	}
}