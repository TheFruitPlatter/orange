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
import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import com.langwuyue.orange.redis.RedisValueTypeEnum;
import com.langwuyue.orange.redis.annotation.ContinueOnFailure;
import com.langwuyue.orange.redis.annotation.Multiple;
import com.langwuyue.orange.redis.annotation.global.GetExpiration;
import com.langwuyue.orange.redis.context.OrangeRedisContext;
import com.langwuyue.orange.redis.executor.OrangeRedisGetAbstractExecutor;
import com.langwuyue.orange.redis.executor.multiplelocks.context.OrangeMultipleLocksGetExpirationsContext;
import com.langwuyue.orange.redis.mapping.OrangeRedisExecutorIdGenerator;
import com.langwuyue.orange.redis.operations.OrangeRedisHashOperations;
import com.langwuyue.orange.redis.utils.OrangeCollectionUtils;
import com.langwuyue.orange.redis.utils.OrangeReflectionUtils;

/**
 * An executor implementation that retrieves expiration times for Redis locks.
 * 
 * <p>This executor is responsible for fetching the remaining time-to-live (TTL) for 
 * multiple lock entries from Redis hash structures. It supports batch operations through 
 * annotations and provides failure handling mechanisms. The executor extends 
 * {@link OrangeRedisGetAbstractExecutor} to leverage common retrieval functionality
 * while adding specific expiration time handling behavior.
 * 
 * <p>The executor converts the raw expiration timestamps from Redis (which are stored as 
 * absolute millisecond timestamps) into relative time remaining values in the time unit 
 * specified in the Redis key configuration.
 * 
 * <p>Supported annotations:
 * <ul>
 *   <li>{@link GetExpiration} - Marks methods that retrieve lock expiration times</li>
 *   <li>{@link Multiple} - Indicates multiple entries to be queried</li>
 *   <li>{@link ContinueOnFailure} - Controls behavior when retrieval operation fails</li>
 * </ul>
 * 
 * <p>This executor supports returning results as either a collection or a map, where
 * the map keys are the lock identifiers and the values are the corresponding expiration times.
 *
 * @author Liang.Zhong
 * @since 1.0.0
 * @see OrangeRedisGetAbstractExecutor
 * @see OrangeRedisHashOperations
 * @see OrangeMultipleLocksGetExpirationsContext
 * @see <a href="https://orange.langwuyue.com/redis/advanced/multiple-locks">Orange Redis Multiple Locks Documentation</a>
 */
public class OrangeGetExpirationExecutor extends OrangeRedisGetAbstractExecutor {
	
	private OrangeRedisHashOperations operations;

	/**
	 * Constructs a new expiration time retrieval executor.
	 * 
	 * @param operations the Redis hash operations implementation to use for data retrieval
	 * @param idGenerator the ID generator for creating unique operation identifiers
	 */
	public OrangeGetExpirationExecutor(OrangeRedisHashOperations operations,OrangeRedisExecutorIdGenerator idGenerator) {
		super(idGenerator);
		this.operations = operations;
	}

	/**
	 * Returns the list of annotation classes supported by this executor.
	 * 
	 *
	 * @return a list of supported annotation classes
	 */
	@Override
	protected List<Class<? extends Annotation>> getSupportedAnnotationClasses() {
		return OrangeCollectionUtils.asList(GetExpiration.class, Multiple.class,ContinueOnFailure.class);
	}
	
	/**
	 * Returns the context class used by this executor.
	 * 
	 *
	 * @return the class of {@link OrangeMultipleLocksGetExpirationsContext} used by this executor
	 */
	@Override
	public Class<? extends OrangeRedisContext> getContextClass() {
		return OrangeMultipleLocksGetExpirationsContext.class;
	}

	/**
	 * Retrieves expiration times for multiple lock entries and processes them.
	 * 
	 *
	 * @param context the Redis operation context
	 * @param valueField the field that will receive the result values
	 * @param returnArgumentType the expected return type
	 * @return a collection of processed expiration times
	 * @throws Exception if an error occurs during the retrieval or processing
	 */
	@Override
	protected Collection doGet(OrangeRedisContext context, Field valueField, Type returnArgumentType) throws Exception {
		OrangeMultipleLocksGetExpirationsContext ctx = (OrangeMultipleLocksGetExpirationsContext) context;
		List<Object> results = this.operations.multiGet(context.getRedisKey().getValue(), ctx.toList(), ctx.getValueType(),RedisValueTypeEnum.LONG,Long.class);
		for(int i = 0; i < results.size(); i++) {
			Object result = results.get(i);
			if(result == null) {
				continue;
			}
			Long deadline = (Long)result;
			results.set(i, convert(context, deadline - System.currentTimeMillis(), TimeUnit.MILLISECONDS));
		}
		return results;
	}
	
	/**
	 * Converts a time duration from one time unit to another.
	 * 
	 * <p>This method converts the given time duration from the source time unit to the
	 * time unit specified in the Redis key configuration. This allows the expiration times
	 * to be returned in the time unit expected by the client code.
	 *
	 * <p>For example, if the source duration is in milliseconds but the Redis key is configured
	 * to use seconds, this method will convert the milliseconds value to seconds.
	 *
	 * @param ctx the Redis operation context containing the target time unit information
	 * @param source the time duration to convert
	 * @param sourceTimeUnit the time unit of the source duration
	 * @return the converted time duration in the target time unit
	 */
	protected Long convert(OrangeRedisContext ctx, Long source,TimeUnit sourceTimeUnit) {
		return ctx.getRedisKey().getExpirationTimeUnit().convert(source, sourceTimeUnit);
	}
	
	/**
	 * Determines the return argument type for the operation result.
	 * 
	 * <p>This method handles two scenarios:
	 * <ul>
	 *   <li>For Map return types: extracts the value type from the map's generic type parameters</li>
	 *   <li>For Collection return types: delegates to the parent class implementation</li>
	 * </ul>
	 * 
	 * <p>For example:
	 * <ul>
	 *   <li>If return type is Map&lt;String, Long&gt;, it returns Long.class</li>
	 *   <li>If return type is List&lt;Long&gt;, it delegates to super class</li>
	 * </ul>
	 *
	 * @param context the Redis operation context containing method return type information
	 * @return the Type object representing the actual type argument of the return value
	 */
	@Override
	protected Type getReturnArgumentType(OrangeRedisContext context) {
		Class<?> returnType = context.getOperationMethod().getReturnType();
		if(Map.class.isAssignableFrom(returnType)) {
			Type genericType = context.getOperationMethod().getGenericReturnType();
			return OrangeReflectionUtils.getMapActaulTypeArguments(genericType)[1];
		} else {
			return super.getReturnArgumentType(context);	
		}
	}

	/**
	 * Converts the operation result collection to the appropriate return value type.
	 * 
	 * <p>This method handles different return type scenarios:
	 * <ul>
	 *   <li>For Map return types: converts the collection to a Map with keys and their expiration times</li>
	 *   <li>For Collection return types: delegates to the parent class implementation</li>
	 *   <li>For single value return types: extracts the first element from the collection</li>
	 * </ul>
	 * 
	 * <p>The method also handles type conversion between different numeric types
	 * (Long, Integer, etc.) based on the expected return type.
	 *
	 * @param context the Redis operation context
	 * @param result the collection of operation results
	 * @param returnArgumentType the expected return argument type
	 * @param field the field metadata for the return value
	 * @return the converted return value in the appropriate type
	 * @throws Exception if an error occurs during the conversion process
	 */
	@Override
	protected Object toReturnValue(
		OrangeRedisContext context, 
		Collection result, 
		Type returnArgumentType, 
		Field field
	)throws Exception {
		Class<?> returnClass = context.getOperationMethod().getReturnType();
		if(Map.class.isAssignableFrom(returnClass)) {
			OrangeMultipleLocksGetExpirationsContext ctx = (OrangeMultipleLocksGetExpirationsContext) context;
			Map map = OrangeReflectionUtils.newMap(returnClass);
			List keys = ctx.getCachedKeys();
			List values = (List)result;
			int size = values.size();
			for(int i = 0; i < size; i++) {
				map.put(keys.get(i), values.get(i));
			}
			return map;
		}else{
			return super.toReturnValue(context, result, returnArgumentType, field);
		}
	}

}