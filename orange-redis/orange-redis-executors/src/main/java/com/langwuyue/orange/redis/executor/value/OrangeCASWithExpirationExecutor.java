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
package com.langwuyue.orange.redis.executor.value;

import java.lang.annotation.Annotation;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.langwuyue.orange.redis.RedisValueTypeEnum;
import com.langwuyue.orange.redis.annotation.global.SetExpiration;
import com.langwuyue.orange.redis.context.OrangeCompareAndSwapContext;
import com.langwuyue.orange.redis.context.OrangeRedisContext;
import com.langwuyue.orange.redis.logger.OrangeRedisLogger;
import com.langwuyue.orange.redis.mapping.OrangeRedisExecutorIdGenerator;
import com.langwuyue.orange.redis.operations.OrangeRedisScriptOperations;
import com.langwuyue.orange.redis.operations.OrangeRedisValueOperations;

/**
 * Executor implementation for performing atomic Compare-And-Swap (CAS) operations
 * with expiration settings on Redis values.
 * 
 * This executor extends the basic CAS functionality by adding the ability to set
 * an expiration time on the Redis key after a successful CAS operation. This is
 * particularly useful for implementing time-limited atomic updates, such as
 * session data updates, temporary locks with automatic expiration, or any scenario
 * where the updated value should only persist for a limited time.
 * 
 * The executor supports the following annotations:
 * - All annotations supported by the parent OrangeCompareAndSwapExecutor
 * - SetExpiration: Indicates that the Redis key should have an expiration time set
 *
 * @author Liang.Zhong
 * @since 1.0.0
 */
public class OrangeCASWithExpirationExecutor extends OrangeCompareAndSwapExecutor {
	
	/**
	 * Redis value operations instance used to perform value-specific operations,
	 * particularly setting expiration times on Redis keys.
	 */
	private OrangeRedisValueOperations operations;
	
	/**
	 * Constructs a new executor for performing CAS operations with expiration settings.
	 *
	 * @param scriptOperations the Redis script operations instance for executing Lua scripts
	 * @param operations the Redis value operations instance for setting expiration times
	 * @param idGenerator the ID generator for creating unique executor identifiers
	 * @param logger the logger for recording operation details and errors
	 */
	public OrangeCASWithExpirationExecutor(
			OrangeRedisScriptOperations scriptOperations,
			OrangeRedisValueOperations operations,
			OrangeRedisExecutorIdGenerator idGenerator,
			OrangeRedisLogger logger
	) {
		super(scriptOperations,idGenerator,logger);
		this.operations = operations;
	}

	/**
	 * Executes a Compare-And-Swap operation with expiration setting.
	 * 
	 * The expiration is set before the actual CAS operation. If setting the expiration fails,
	 * the method returns false without attempting the CAS operation. This ensures that
	 * successful operations always have the expiration time properly set.
	 *
	 * @param context the context containing the Redis key, old value, new value, and expiration settings
	 * @return boolean indicating whether both the expiration setting and CAS operation were successful
	 * @throws Exception if an error occurs during the Redis operations
	 */
	@Override
	public Object execute(OrangeRedisContext context) throws Exception {
		OrangeCompareAndSwapContext ctx = (OrangeCompareAndSwapContext)context;
		Map<Object, RedisValueTypeEnum> argsValueTypes = new LinkedHashMap<>();
		argsValueTypes.put(ctx.getOldValue(), context.getValueType());
		argsValueTypes.put(ctx.getValue(), context.getValueType());
		boolean success = this.operations.expire(
			ctx.getRedisKey().getValue(), 
			ctx.getRedisKey().getExpirationTime(), 
			ctx.getRedisKey().getExpirationTimeUnit()
		);
		if(!success) {
			return success;
		}
		return super.execute(context);
	}

	/**
	 * Returns the list of annotation classes that this executor supports.
	 * 
	 * This executor supports all annotations from the parent OrangeCompareAndSwapExecutor
	 * plus the SetExpiration annotation for configuring key expiration.
	 * 
	 * @return List of supported annotation classes
	 */
	@Override
	public List<Class<? extends Annotation>> getSupportedAnnotationClasses() {
		List<Class<? extends Annotation>> classes = super.getSupportedAnnotationClasses();
		classes.add(SetExpiration.class);
		return classes;
	}

	/**
	 * Returns the context class used by this executor.
	 * 
	 * This executor uses OrangeCompareAndSwapContext which provides the necessary
	 * fields and methods for performing CAS operations with expiration settings,
	 * including:
	 * - Redis key information
	 * - Old and new values for comparison
	 * - Expiration time settings
	 *
	 * @return The OrangeCompareAndSwapContext class
	 */
	@Override
	public Class<? extends OrangeRedisContext> getContextClass() {
		return OrangeCompareAndSwapContext.class;
	}
}