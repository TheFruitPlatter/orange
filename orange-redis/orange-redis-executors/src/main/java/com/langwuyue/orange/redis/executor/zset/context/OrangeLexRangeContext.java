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

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import com.langwuyue.orange.redis.OrangeRedisException;
import com.langwuyue.orange.redis.RedisValueTypeEnum;
import com.langwuyue.orange.redis.annotation.OrangeRedisOperationArg;
import com.langwuyue.orange.redis.annotation.zset.LexRange;
import com.langwuyue.orange.redis.annotation.zset.MaxLex;
import com.langwuyue.orange.redis.annotation.zset.MinLex;
import com.langwuyue.orange.redis.context.OrangeRedisContext;
import com.langwuyue.orange.redis.utils.OrangeReflectionUtils;

/**
 * Context class for Redis ZSet lexicographical range queries.
 * This class extends {@link OrangeRedisContext} to provide functionality for querying Redis ZSets
 * based on lexicographical (alphabetical) ordering of members, rather than by score.
 * 
 * <p>Lexicographical range queries are useful when you need to retrieve elements from a sorted set
 * based on string comparison rather than numeric scores. This is particularly valuable for:
 * <ul>
 *   <li>Alphabetical listings (e.g., dictionaries, directories)
 *   <li>Prefix-based searches (e.g., autocomplete suggestions)
 *   <li>Range queries on string-based identifiers
 *   <li>Natural language sorting of elements
 * </ul>
 * 
 * @author Liang.Zhong
 * @since 1.0.0
 * @see LexRange
 * @see MinLex
 * @see MaxLex
 */
public class OrangeLexRangeContext extends OrangeRedisContext {
	
	/**
	 * The lexicographical range object used for ZSet queries.
	 * This can be either an instance of OrangeRedisZSetOperations.LexRange
	 * or a custom class with fields annotated with @MinLex and @MaxLex.
	 */
	@OrangeRedisOperationArg(binding = LexRange.class)
	private Object lexRange;

	/**
	 * Constructs a new OrangeLexRangeContext with the specified operation parameters.
	 * 
	 * @param operationOwner The class that owns the Redis operation method
	 * @param operationMethod The method representing the Redis operation
	 * @param args The arguments passed to the operation method
	 * @param redisKey The Redis key to operate on
	 * @param valueType The type of Redis value (should be ZSET for this context)
	 */
	public OrangeLexRangeContext(
		Class<?> operationOwner, 
		Method operationMethod, 
		Object[] args,
		Key redisKey,
		RedisValueTypeEnum valueType
	) {
		super(operationOwner, operationMethod, args, redisKey,valueType);
	}

	/**
	 * Retrieves the lexicographical range for ZSet queries from the context.
	 * 
	 * <p>This method handles two possible scenarios:
	 * <ol>
	 *   <li>The lexRange object is already an instance of OrangeRedisZSetOperations.LexRange
	 *   <li>The lexRange object is a custom class with fields annotated with @MinLex and @MaxLex
	 * </ol>
	 * 
	 * <p>For custom classes, this method uses reflection to extract the min and max values
	 * from fields annotated with @MinLex and @MaxLex respectively. Both fields must be
	 * present, non-null, and of String type.
	 * 
	 * <p>The lexicographical range syntax follows Redis conventions:
	 * <ul>
	 *   <li>"[member" - inclusive range starting from member
	 *   <li>"(member" - exclusive range starting after member
	 *   <li>"-" - negative infinity (start from the lowest possible string)
	 *   <li>"+" - positive infinity (end at the highest possible string)
	 * </ul>
	 * 
	 * @return A LexRange object containing the min and max lexicographical bounds
	 * @throws OrangeRedisException if the lexRange is null, missing required annotations,
	 *         or contains invalid field types
	 */
	public com.langwuyue.orange.redis.operations.OrangeRedisZSetOperations.LexRange getLexRange() {
		if(lexRange == null) {
			throw new OrangeRedisException(String.format("The argument annotated with @%s cannot be null", LexRange.class));
		}
		if(lexRange instanceof com.langwuyue.orange.redis.operations.OrangeRedisZSetOperations.LexRange) {
			return (com.langwuyue.orange.redis.operations.OrangeRedisZSetOperations.LexRange)lexRange;
		}
		Field maxLexField = null;
		Field minLexField = null;
		Field[] fields = lexRange.getClass().getDeclaredFields();
		for(Field field : fields) {
			if(field.isAnnotationPresent(MaxLex.class)) {
				maxLexField = field;
			}
			else if(field.isAnnotationPresent(MinLex.class)) {
				minLexField = field;
			}
			if(maxLexField != null && minLexField != null) {
				break;
			}
		}
		if(maxLexField == null || minLexField == null) {
			throw new OrangeRedisException(String.format("The argument annotated with @%s must have two fields annotated with @%s and @%s", LexRange.class,MaxLex.class,MinLex.class));
		}
		Object maxLex = OrangeReflectionUtils.getFieldValue(maxLexField, lexRange);
		if(maxLex == null) {
			throw new OrangeRedisException(String.format("The field annotated with @%s cannot be null", MaxLex.class));
		}
		if(!(maxLex instanceof String)) {
			throw new OrangeRedisException(String.format("The field annotated with @%s must be a string", MaxLex.class));
		}
		Object minLex = OrangeReflectionUtils.getFieldValue(minLexField, lexRange);
		if(minLex == null) {
			throw new OrangeRedisException(String.format("The field annotated with @%s cannot be null", MinLex.class));
		}
		if(!(minLex instanceof String)) {
			throw new OrangeRedisException(String.format("The field annotated with @%s must be a string", MinLex.class));
		}
		return new com.langwuyue.orange.redis.operations.OrangeRedisZSetOperations.LexRange(
			maxLex.toString(),
			minLex.toString()
		);
	}
}