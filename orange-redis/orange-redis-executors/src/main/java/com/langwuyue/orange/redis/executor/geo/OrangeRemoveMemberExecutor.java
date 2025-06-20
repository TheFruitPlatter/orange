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
package com.langwuyue.orange.redis.executor.geo;

import java.lang.annotation.Annotation;
import java.util.List;

import com.langwuyue.orange.redis.annotation.RedisValue;
import com.langwuyue.orange.redis.annotation.RemoveMembers;
import com.langwuyue.orange.redis.context.OrangeRedisContext;
import com.langwuyue.orange.redis.context.OrangeRedisValueContext;
import com.langwuyue.orange.redis.executor.OrangeRedisAbstractExecutor;
import com.langwuyue.orange.redis.mapping.OrangeRedisExecutorIdGenerator;
import com.langwuyue.orange.redis.operations.OrangeRedisGeoOperations;
import com.langwuyue.orange.redis.utils.OrangeCollectionUtils;

/**
 * Executor for removing members from Redis GEO data structures.
 * 
 * <p>This executor handles the removal of members from GEO sets based on the
 * {@link RemoveMembers} annotation. It supports both direct removal operations
 * and conditional removal based on method return type.
 * 
 * <p>Supported annotations:
 * <ul>
 *   <li>{@link RemoveMembers} - Marks a method as a GEO member removal operation</li>
 *   <li>{@link RedisValue} - Specifies the value type for the member to be removed</li>
 * </ul>
 *
 * @author Liang.Zhong
 * @since 1.0.0
 * @see <a href="https://orange.langwuyue.com/redis/advanced/geo">Orange Redis Geo Documentation</a>
 */
public class OrangeRemoveMemberExecutor extends OrangeRedisAbstractExecutor {
	
	private OrangeRedisGeoOperations operations;

	/**
	 * Constructs a new executor for GEO member removal operations.
	 * 
	 * @param operations the Redis GEO operations implementation
	 * @param idGenerator the executor ID generator
	 */
	public OrangeRemoveMemberExecutor(OrangeRedisGeoOperations operations,OrangeRedisExecutorIdGenerator idGenerator) {
		super(idGenerator);
		this.operations = operations;
	}

	/**
	 * Gets the list of annotation classes supported by this executor.
	 * 
	 * @return list of supported annotation classes including:
	 *         - RemoveMembers: marks a method as a GEO member removal operation
	 *         - RedisValue: specifies the value type for the member to be removed
	 */
	@Override
	protected List<Class<? extends Annotation>> getSupportedAnnotationClasses() {
		return OrangeCollectionUtils.asList(RemoveMembers.class,RedisValue.class);
	}

	/**
	 * Gets the context class used by this executor.
	 * 
	 * @return the OrangeRedisValueContext class used for operation execution
	 */
	@Override
	public Class<? extends OrangeRedisContext> getContextClass() {
		return OrangeRedisValueContext.class;
	}

	/**
	 * Executes the GEO member removal operation.
	 * 
	 * <p>This method performs the actual removal of a member from a Redis GEO set.
	 * The return value is determined by the method's return type:
	 * <ul>
	 *   <li>If the method returns boolean/Boolean, returns true if any members were removed</li>
	 *   <li>Otherwise, returns the number of members removed</li>
	 * </ul>
	 * 
	 * @param context the execution context containing key, value and method information
	 * @return either a boolean indicating success or the number of members removed
	 * @throws Exception if the operation fails
	 */
	@Override
	public Object execute(OrangeRedisContext context) throws Exception {
		OrangeRedisValueContext ctx = (OrangeRedisValueContext) context;
		Long result = this.operations.remove(
			ctx.getRedisKey().getValue(),
			ctx.getValueType(), 
			ctx.getValue()
		);
		Class<?> returnClass = context.getOperationMethod().getReturnType();
		if(returnClass == Boolean.class || returnClass == boolean.class) {
			return result != null && result > 0;	
		}
		return result;
	}
}