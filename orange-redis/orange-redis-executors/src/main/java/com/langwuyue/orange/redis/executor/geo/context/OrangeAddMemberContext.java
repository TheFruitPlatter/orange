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
package com.langwuyue.orange.redis.executor.geo.context;

import java.lang.reflect.Method;

import com.langwuyue.orange.redis.OrangeRedisException;
import com.langwuyue.orange.redis.RedisValueTypeEnum;
import com.langwuyue.orange.redis.annotation.Member;
import com.langwuyue.orange.redis.annotation.OrangeRedisOperationArg;
import com.langwuyue.orange.redis.operations.OrangeRedisGeoOperations.GeoEntry;

/**
 * Context class for adding a single geo member to a Redis geo set.
 * This context holds the necessary information for adding a geo member with its coordinates,
 * including the member object annotated with {@link com.langwuyue.orange.redis.annotation.Member}.
 * 
 * <p>This context is used by {@link com.langwuyue.orange.redis.executor.geo.OrangeAddMemberExecutor}
 * to perform the actual Redis geo add operation.</p>
 *
 * @author Liang.Zhong
 * @since 1.0.0
 */
public class OrangeAddMemberContext extends OrangeMemberContext {

	/**
	 * The member object to be added to the geo set.
	 * This field is bound to a method parameter annotated with {@link com.langwuyue.orange.redis.annotation.Member}.
	 */
	@OrangeRedisOperationArg(binding = Member.class)
	private Object member;

	/**
	 * Constructs a new context for adding a geo member.
	 * 
	 * @param operationOwner the class that declares the Redis operation method
	 * @param operationMethod the method annotated with Redis geo operation
	 * @param args the arguments passed to the operation method
	 * @param redisKey the Redis key associated with this operation
	 * @param valueType the Redis value type for this operation
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
	 * Gets the geo entry to be added to Redis.
	 * 
	 * @return the GeoEntry containing member and coordinates
	 * @throws OrangeRedisException if the member argument is null
	 */
	public GeoEntry getMember() {
		GeoEntry entry = toGeoEntry(member,Member.class);
		if(entry == null) {
			throw new OrangeRedisException(String.format("The argument annotated with @%s cannot be null", Member.class));
		}
		return entry;
	}
}