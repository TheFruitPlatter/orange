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
package com.langwuyue.orange.redis.executor.multiplelocks;

import java.lang.annotation.Annotation;
import java.util.List;
import java.util.concurrent.TimeUnit;

import com.langwuyue.orange.redis.annotation.ContinueOnFailure;
import com.langwuyue.orange.redis.annotation.Multiple;
import com.langwuyue.orange.redis.annotation.TimeoutUnit;
import com.langwuyue.orange.redis.annotation.global.GetExpiration;
import com.langwuyue.orange.redis.context.OrangeRedisContext;
import com.langwuyue.orange.redis.executor.multiplelocks.context.OrangeMultipleLocksGetExpirationsWithUnitArgsContext;
import com.langwuyue.orange.redis.mapping.OrangeRedisExecutorIdGenerator;
import com.langwuyue.orange.redis.operations.OrangeRedisHashOperations;
import com.langwuyue.orange.redis.utils.OrangeCollectionUtils;

/**
 * Executor for retrieving expiration times of multiple locks with custom time unit specification.
 * 
 * <p>This executor extends {@link OrangeGetExpirationExecutor} to support the {@link TimeoutUnit} annotation,
 * which allows clients to specify the desired time unit for returned expiration values. Unlike the parent class
 * which uses the time unit configured in the Redis key, this executor converts expiration times to the
 * unit specified in the method invocation.
 * 
 * <p>This executor supports the following annotations:
 * <ul>
 *   <li>{@link GetExpiration} - For retrieving lock expiration times</li>
 *   <li>{@link TimeoutUnit} - For specifying the desired time unit for returned values</li>
 *   <li>{@link Multiple} - For handling multiple lock entries in a single operation</li>
 *   <li>{@link ContinueOnFailure} - For specifying failure handling behavior</li>
 * </ul>
 * 
 * @author Liang.Zhong
 * @since 1.0.0
 * @see OrangeGetExpirationExecutor
 * @see TimeoutUnit
 * @see OrangeMultipleLocksGetExpirationsWithUnitArgsContext
 */
public class OrangeGetExpirationWithUnitArgsExecutor extends OrangeGetExpirationExecutor {
	
	/**
	 * Constructs a new executor for handling expiration time retrieval with custom time units.
	 *
	 * @param operations the Redis hash operations to use for interacting with Redis
	 * @param idGenerator the generator for creating unique executor identifiers
	 */
	public OrangeGetExpirationWithUnitArgsExecutor(OrangeRedisHashOperations operations,OrangeRedisExecutorIdGenerator idGenerator) {
		super(operations,idGenerator);
	}

	/**
	 * Returns the list of annotation classes supported by this executor.
	 * 
	 * <p>This executor supports:
	 * <ul>
	 *   <li>{@link GetExpiration} - For retrieving expiration times</li>
	 *   <li>{@link TimeoutUnit} - For specifying return value time unit</li>
	 *   <li>{@link Multiple} - For batch operations</li>
	 *   <li>{@link ContinueOnFailure} - For error handling configuration</li>
	 * </ul>
	 *
	 * @return list of supported annotation classes
	 */
	@Override
	protected List<Class<? extends Annotation>> getSupportedAnnotationClasses() {
		return OrangeCollectionUtils.asList(GetExpiration.class,TimeoutUnit.class,Multiple.class,ContinueOnFailure.class);
	}

	/**
	 * Returns the context class used by this executor.
	 * 
	 * <p>This executor uses {@link OrangeMultipleLocksGetExpirationsWithUnitArgsContext} which extends
	 * the base expiration context to include time unit configuration from the {@link TimeoutUnit} annotation.
	 *
	 * @return the class of {@link OrangeMultipleLocksGetExpirationsWithUnitArgsContext}
	 */
	@Override
	public Class<? extends OrangeRedisContext> getContextClass() {
		return OrangeMultipleLocksGetExpirationsWithUnitArgsContext.class;
	}

	/**
	 * Converts a time duration from the source time unit to the target time unit specified by {@link TimeoutUnit}.
	 * 
	 * <p>Unlike the parent class which uses the Redis key's configured time unit, this implementation
	 * uses the time unit specified in the method invocation via the {@link TimeoutUnit} annotation.
	 * This allows for flexible time unit conversion based on the caller's requirements.
	 *
	 * @param ctx the Redis operation context containing the target time unit
	 * @param source the time duration to convert
	 * @param sourceTimeUnit the time unit of the source duration
	 * @return the converted time duration in the target time unit
	 */
	@Override
	protected Long convert(OrangeRedisContext ctx, Long source, TimeUnit sourceTimeUnit) {
		OrangeMultipleLocksGetExpirationsWithUnitArgsContext context = (OrangeMultipleLocksGetExpirationsWithUnitArgsContext) ctx;
		return context.getUnit().convert(source, sourceTimeUnit);
	}
}