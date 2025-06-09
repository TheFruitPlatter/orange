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

/**
 * Context class for conditionally adding multiple members to a Redis ZSet if they don't exist.
 * 
 * <p>This class extends {@link OrangeAddMembersContext} to provide functionality for
 * conditional batch member addition. It supports:
 * <ul>
 *   <li>Adding multiple members only if they don't exist in the ZSet</li>
 *   <li>Batch processing with conditional checks</li>
 *   <li>Optional cleanup of temporary keys after operation</li>
 * </ul>
 * 
 * <p>The class processes the {@link IfAbsent} annotation to determine:
 * <ul>
 *   <li>Whether to perform conditional additions for the batch</li>
 *   <li>Whether to clean up temporary keys after the operation</li>
 * </ul>
 * 
 * <p>This context is particularly useful when you need to:
 * <ul>
 *   <li>Add multiple members atomically only if they don't exist</li>
 *   <li>Handle batch operations with existence checks</li>
 *   <li>Maintain data consistency in concurrent scenarios</li>
 * </ul>
 *
 * @author Liang.Zhong
 * @since 1.0.0
 */
public class OrangeAddMembersIfAbsentContext extends OrangeAddMembersContext {
	
    /**
     * Configuration for conditional addition behavior.
     * This field is bound to method parameters annotated with {@link IfAbsent}.
     */
	@OrangeRedisOperationArg(binding = IfAbsent.class,valueHandler = OrangeMethodAnnotationHandler.class)
	private IfAbsent ifAbsent;
	
    /**
     * Constructs a new OrangeAddMembersIfAbsentContext.
     *
     * @param operationOwner The class that owns the Redis operation
     * @param operationMethod The method representing the Redis operation
     * @param args The arguments passed to the operation method
     * @param redisKey The Redis key for the operation
     * @param valueType The type of values stored in the Redis ZSet
     */
	public OrangeAddMembersIfAbsentContext(
		Class<?> operationOwner, 
		Method operationMethod, 
		Object[] args,
		Key redisKey,
		RedisValueTypeEnum valueType
	) {
		super(operationOwner, operationMethod, args, redisKey,valueType);
	}
	
    /**
     * Determines whether to delete temporary keys after the operation.
     * 
     * <p>This method returns the value configured in the {@link IfAbsent} annotation's
     * deleteInTheEnd parameter. When true, any temporary keys created during the
     * conditional batch addition operation will be cleaned up after the operation completes.
     * 
     * <p>This is particularly important for batch operations where multiple temporary
     * keys might be created during the atomic conditional addition process.
     *
     * @return true if temporary keys should be deleted after the operation, false otherwise
     */
	public boolean isDeleteInTheEnd() {
		return ifAbsent.deleteInTheEnd();
	}
}