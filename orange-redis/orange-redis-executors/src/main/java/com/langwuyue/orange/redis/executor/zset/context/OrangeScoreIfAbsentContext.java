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

import com.langwuyue.orange.redis.RedisValueTypeEnum;
import com.langwuyue.orange.redis.annotation.IfAbsent;
import com.langwuyue.orange.redis.annotation.OrangeRedisOperationArg;
import com.langwuyue.orange.redis.context.builder.OrangeMethodAnnotationHandler;
import com.langwuyue.orange.redis.operations.OrangeRedisZSetOperations.ZSetEntry;

/**
 * Context class for Redis ZSet operations that add a member with a score only if the member is absent.
 * 
 * <p>This context extends {@link OrangeScoreContext} and implements {@link OrangeAddIfAbsentContext},
 * providing functionality to handle conditional addition of members to a ZSet based on the {@link IfAbsent}
 * annotation. It supports operations like ZADD with the NX option in Redis.
 * 
 * <p>The context processes parameters annotated with {@link IfAbsent} to determine the behavior
 * when a member already exists in the ZSet.
 * 
 * <p>Supported data types for score:
 * <ul>
 *   <li>byte, Byte</li>
 *   <li>short, Short</li>
 *   <li>int, Integer</li>
 *   <li>long, Long</li>
 *   <li>float, Float</li>
 *   <li>double, Double</li>
 *   <li>BigDecimal</li>
 *   <li>String (must be parseable as a number)</li>
 * </ul>
 *
 * @author Liang.Zhong
 * @since 1.0.0
 * @see OrangeScoreContext
 * @see OrangeAddIfAbsentContext
 * @see IfAbsent
 */
public class OrangeScoreIfAbsentContext extends OrangeScoreContext implements OrangeAddIfAbsentContext{
	
	/**
	 * The {@link IfAbsent} annotation instance extracted from the method parameter.
	 * This annotation controls the behavior when a member already exists in the ZSet.
	 */
	@OrangeRedisOperationArg(binding = IfAbsent.class,valueHandler = OrangeMethodAnnotationHandler.class)
	private IfAbsent ifAbsent;

	/**
	 * Constructs a new OrangeScoreIfAbsentContext with the specified parameters.
	 *
	 * @param operationOwner The class that owns the Redis operation method
	 * @param operationMethod The method representing the Redis operation
	 * @param args The arguments passed to the operation method
	 * @param redisKey The Redis key context
	 * @param valueType The type of Redis value being operated on
	 */
	public OrangeScoreIfAbsentContext(
		Class<?> operationOwner, 
		Method operationMethod, 
		Object[] args,
		Key redisKey,
		RedisValueTypeEnum valueType
	) {
		super(operationOwner, operationMethod, args, redisKey,valueType);
	}

	/**
	 * Determines whether the member should be deleted after the operation if it already exists.
	 *
	 * <p>This method delegates to the {@link IfAbsent#deleteInTheEnd()} method of the
	 * {@link IfAbsent} annotation instance.
	 *
	 * @return {@code true} if the member should be deleted when it already exists,
	 *         {@code false} otherwise
	 */
	@Override
	public boolean isDeleteInTheEnd() {
		return ifAbsent.deleteInTheEnd();
	}

	/**
	 * Creates and returns a ZSetEntry representing the member to be added to the ZSet.
	 *
	 * <p>The ZSetEntry combines the value (member) and score extracted from the context.
	 *
	 * @return A new {@link ZSetEntry} containing the member value and score
	 */
	@Override
	public ZSetEntry getMember() {
		return new ZSetEntry(this.getValue(),this.getScore());
	}
}