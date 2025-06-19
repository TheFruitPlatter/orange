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

import java.lang.reflect.Method;
import java.util.Map;

import com.langwuyue.orange.redis.OrangeRedisException;
import com.langwuyue.orange.redis.RedisValueTypeEnum;
import com.langwuyue.orange.redis.annotation.Member;
import com.langwuyue.orange.redis.annotation.OrangeRedisOperationArg;

/**
 * Context class for adding a single member to a Redis hash.
 * 
 * <p>This class extends OrangeMemberContext and provides functionality
 * for handling operations that add a single member to a Redis hash.
 * It processes the member object annotated with {@link Member} and converts it to a map.
 *
 * @author Liang.Zhong
 * @since 1.0.0
 */
public class OrangeAddMemberContext extends OrangeMemberContext {
	
	/**
	 * The member object to be added to the Redis hash.
	 * This field is bound to arguments annotated with {@link Member}.
	 */
	@OrangeRedisOperationArg(binding = Member.class)
	private Object member;

	/**
	 * Constructs a new OrangeAddMemberContext with the specified parameters.
	 *
	 * @param operationOwner    the class that owns the Redis operation
	 * @param operationMethod   the method representing the Redis operation
	 * @param args             the arguments passed to the operation method
	 * @param redisKey         the Redis key for the operation
	 * @param valueType        the type of value stored in Redis
	 * @param keyType         the type of the Redis key
	 */
	public OrangeAddMemberContext(
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
	 * Converts and retrieves the member object as a Map.
	 *
	 * @return a Map representation of the member object
	 * @throws OrangeRedisException if the member object is null after conversion
	 */
	public Map getMember() {
		Map map = toMap(member, Member.class);
		if(map == null) {
			throw new OrangeRedisException(String.format("The argument annotated with @%s cannot be null", Member.class));
		}
		return map;
	}
}