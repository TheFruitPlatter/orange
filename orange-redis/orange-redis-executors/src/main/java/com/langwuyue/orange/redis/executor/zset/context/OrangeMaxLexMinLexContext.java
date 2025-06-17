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

import java.lang.reflect.Method;

import com.langwuyue.orange.redis.OrangeRedisException;
import com.langwuyue.orange.redis.RedisValueTypeEnum;
import com.langwuyue.orange.redis.annotation.OrangeRedisOperationArg;
import com.langwuyue.orange.redis.annotation.zset.MaxLex;
import com.langwuyue.orange.redis.annotation.zset.MinLex;
import com.langwuyue.orange.redis.context.OrangeRedisContext;
/**
 * Context class for Redis ZSet lexicographical range queries using @MinLex and @MaxLex annotations.
 * This class provides the foundation for performing lexicographical range operations on Redis Sorted Sets
 * where elements are ordered by their string values rather than by scores.
 * 
 * <p>Lexicographical ordering in Redis ZSets follows these rules:
 * <ul>
 *   <li>Elements are compared byte-by-byte
 *   <li>Shorter strings are considered lexicographically smaller than longer strings that share the same prefix
 *   <li>Binary safe comparison (handles any byte values)
 * </ul>
 * 
 * <p>This context supports defining a range with separate minimum and maximum boundaries,
 * offering more flexibility than single-range approaches. This is particularly useful for:
 * <ul>
 *   <li>Prefix matching and filtering
 *   <li>Alphabetical sorting and retrieval
 *   <li>Date-based range queries (when dates are stored in lexicographically sortable format)
 *   <li>Version comparison (when versions follow lexicographical ordering)
 * </ul>
 * 
 * @author Liang.Zhong
 * @since 1.0.0
 * @see com.langwuyue.orange.redis.annotation.zset.MinLex
 * @see com.langwuyue.orange.redis.annotation.zset.MaxLex
 * @see <a href="https://orange.langwuyue.com/redis/advanced/zset">Orange Redis ZSet Documentation</a>
 */
public class OrangeMaxLexMinLexContext extends OrangeRedisContext {
	
	/**
	 * The maximum lexicographical boundary for the range query, annotated with @MaxLex.
	 * Defines the upper bound (inclusive) of the lexicographical range.
	 */
	@OrangeRedisOperationArg(binding = MaxLex.class)
	private Object maxLex;
	
	/**
	 * The minimum lexicographical boundary for the range query, annotated with @MinLex.
	 * Defines the lower bound (inclusive) of the lexicographical range.
	 */
	@OrangeRedisOperationArg(binding = MinLex.class)
	private Object minLex;

	/**
	 * Constructs a new OrangeMaxLexMinLexContext with the specified operation parameters.
	 * 
	 * @param operationOwner The class that owns the Redis operation method
	 * @param operationMethod The method representing the Redis operation
	 * @param args The arguments passed to the operation method
	 * @param redisKey The Redis key to operate on
	 * @param valueType The type of Redis value (should be ZSET for this context)
	 */
	public OrangeMaxLexMinLexContext(
		Class<?> operationOwner, 
		Method operationMethod, 
		Object[] args, 
		Key redisKey,
		RedisValueTypeEnum valueType
	) {
		super(operationOwner, operationMethod, args, redisKey, valueType);
	}
	
	/**
	 * Retrieves and validates the maximum lexicographical boundary.
	 * 
	 * <p>This method validates that:
	 * <ul>
	 *   <li>The maxLex value is not null
	 *   <li>The maxLex value is a String
	 * </ul>
	 * 
	 * <p>The maximum lexicographical boundary defines the upper limit of the range query.
	 * By default, this is an inclusive boundary in Redis. To make it exclusive, the
	 * implementation can prefix the value with "(" character.
	 * 
	 * <p>Special Values in Redis (not handled by this implementation):
	 * <ul>
	 *   <li>"+" - Positive infinity, matches all elements
	 *   <li>"(value" - Exclusive maximum boundary (less than value)
	 *   <li>"[value" - Inclusive maximum boundary (less than or equal to value)
	 * </ul>
	 * 
	 * @return The maximum lexicographical boundary as a String
	 * @throws OrangeRedisException if maxLex is null or not a String
	 */
	public String getMaxLex() {
		if(maxLex == null) {
			throw new OrangeRedisException(String.format("The field annotated with @%s cannot be null", MaxLex.class));
		}
		if(!(maxLex instanceof String)) {
			throw new OrangeRedisException(String.format("The field annotated with @%s must be a string", MaxLex.class));
		}
		return maxLex.toString();
	}
	
	/**
	 * Retrieves and validates the minimum lexicographical boundary.
	 * 
	 * <p>This method validates that:
	 * <ul>
	 *   <li>The minLex value is not null
	 *   <li>The minLex value is a String
	 * </ul>
	 * 
	 * <p>The minimum lexicographical boundary defines the lower limit of the range query.
	 * By default, this is an inclusive boundary in Redis. To make it exclusive, the
	 * implementation can prefix the value with "(" character.
	 * 
	 * <p>Special Values in Redis (not handled by this implementation):
	 * <ul>
	 *   <li>"-" - Negative infinity, matches all elements
	 *   <li>"(value" - Exclusive minimum boundary (greater than value)
	 *   <li>"[value" - Inclusive minimum boundary (greater than or equal to value)
	 * </ul>
	 * 
	 * <p>Usage Examples:
	 * <ul>
	 *   <li>Prefix matching: minLex="prefix", maxLex="prefix\xff"
	 *   <li>Range query: minLex="a", maxLex="z"
	 *   <li>Open-ended range: minLex="-", maxLex="m"
	 * </ul>
	 * 
	 * @return The minimum lexicographical boundary as a String
	 * @throws OrangeRedisException if minLex is null or not a String
	 */
	public String getMinLex() {
		if(minLex == null) {
			throw new OrangeRedisException(String.format("The field annotated with @%s cannot be null", MinLex.class));
		}
		if(!(minLex instanceof String)) {
			throw new OrangeRedisException(String.format("The field annotated with @%s must be a string", MinLex.class));
		}
		return minLex.toString();
	}

}