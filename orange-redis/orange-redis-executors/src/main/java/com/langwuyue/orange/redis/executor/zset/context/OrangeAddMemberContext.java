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
import com.langwuyue.orange.redis.annotation.Member;
import com.langwuyue.orange.redis.annotation.OrangeRedisOperationArg;
import com.langwuyue.orange.redis.operations.OrangeRedisZSetOperations.ZSetEntry;

/**
 * Context class for handling member addition operations in Redis ZSet.
 * 
 * <p>This class manages the context for adding a single member to a Redis sorted set.
 * It extends {@link OrangeMemberContext} to provide specific functionality for:
 * <ul>
 *   <li>Validating member arguments annotated with {@link Member}</li>
 *   <li>Converting member objects to {@link ZSetEntry} format</li>
 *   <li>Ensuring proper type conversion and null checking</li>
 * </ul>
 * 
 * <p>The class processes member objects that are annotated with {@link Member}
 * and converts them into the appropriate format for Redis ZSet operations.
 *
 * @author Liang.Zhong
 * @since 1.0.0
 */
public class OrangeAddMemberContext extends OrangeMemberContext {
	
	/**
	 * The member object to be added to the Redis sorted set.
	 * This field is annotated with {@link OrangeRedisOperationArg} and bound to {@link Member},
	 * indicating it should be processed as a member value for the ZSet operation.
	 */
	@OrangeRedisOperationArg(binding = Member.class)
	private Object member;

	/**
	 * Constructs a new member addition context instance.
	 *
	 * @param operationOwner the class that owns the operation
	 * @param operationMethod the operation method being executed
	 * @param args the method parameter array
	 * @param redisKey Redis key for the sorted set
	 * @param valueType Redis value type for the operation
	 */
	public OrangeAddMemberContext(
		Class<?> operationOwner, 
		Method operationMethod, 
		Object[] args,
		Key redisKey,
		RedisValueTypeEnum valueType
	) {
		super(operationOwner, operationMethod, args, redisKey,valueType);
	}

	/**
	 * Gets the member as a ZSetEntry for Redis sorted set operations.
	 * 
	 * <p>This method converts the member object to a {@link ZSetEntry} format
	 * required for Redis ZSet operations. It performs validation to ensure
	 * the member is not null and properly formatted.
	 *
	 * @return the member as a ZSetEntry object
	 * @throws OrangeRedisException if the member is null or cannot be converted
	 *         to a valid ZSetEntry
	 */
	public ZSetEntry getMember() {
		ZSetEntry entry = toZSetEntry(member,Member.class);
		if(entry == null) {
			throw new OrangeRedisException(String.format("The argument annotated with @%s cannot be null", Member.class));
		}
		return entry;
	}
}