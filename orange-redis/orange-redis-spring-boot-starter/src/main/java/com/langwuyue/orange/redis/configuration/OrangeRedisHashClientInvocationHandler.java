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
package com.langwuyue.orange.redis.configuration;

import java.lang.reflect.Method;

import com.langwuyue.orange.redis.OrangeRedisCircuitBreaker;
import com.langwuyue.orange.redis.RedisValueTypeEnum;
import com.langwuyue.orange.redis.annotation.OrangeRedisKey;
import com.langwuyue.orange.redis.context.builder.OrangeOperationArgHandlerMapping;
import com.langwuyue.orange.redis.context.builder.OrangeRedisContextBuilder;
import com.langwuyue.orange.redis.context.builder.OrangeRedisHashContextBuilder;
import com.langwuyue.orange.redis.executor.OrangeRedisExecutor;
import com.langwuyue.orange.redis.mapping.OrangeRedisExecutorsMapping;

/**
 * Invocation handler for Redis Hash operations.
 * 
 * <p>This class handles method invocations on Redis Hash client interfaces by intercepting
 * method calls and translating them into appropriate Redis Hash operations. It extends the
 * base {@link OrangeRedisClientInvocationHandler} with Hash-specific functionality.
 * 
 * <p>The handler is responsible for:
 * <ul>
 *   <li>Creating context builders specific to Hash operations</li>
 *   <li>Managing Hash key and field value serialization</li>
 *   <li>Routing method calls to appropriate Redis executors</li>
 *   <li>Applying circuit breaker patterns for fault tolerance</li>
 * </ul>
 * 
 * @author Liang.Zhong
 * @since 1.0.0
 * @see OrangeRedisClientInvocationHandler
 * @see OrangeRedisHashContextBuilder
 */
public class OrangeRedisHashClientInvocationHandler extends OrangeRedisClientInvocationHandler {
	
	private RedisValueTypeEnum keyType;
	
	/**
	 * Constructs a new Redis Hash client invocation handler.
	 * 
	 * <p>This constructor initializes a handler that processes Redis Hash operations
	 * by intercepting method calls on the client interface. It configures all necessary
	 * components for handling Redis Hash operations including serialization, execution,
	 * and fault tolerance.
	 * 
	 * @param operationOwner the class that owns the Redis operations (typically an interface)
	 * @param mapping the mapping between method signatures and Redis executors
	 * @param redisKey the Redis key configuration annotation
	 * @param operationArgHandlerMapping the mapping for handling method arguments
	 * @param valueType the type enum for serializing/deserializing hash field values
	 * @param keyType the type enum for serializing/deserializing hash field keys
	 * @param circuitBreaker the circuit breaker for fault tolerance
	 * @param properties the Redis client properties configuration
	 * 
	 * @see OrangeRedisExecutorsMapping
	 * @see OrangeRedisKey
	 * @see OrangeOperationArgHandlerMapping
	 * @see RedisValueTypeEnum
	 * @see OrangeRedisCircuitBreaker
	 * @see OrangeRedisProperties
	 */
	public OrangeRedisHashClientInvocationHandler(
		Class<?> operationOwner,
		OrangeRedisExecutorsMapping mapping,
		OrangeRedisKey redisKey,
		OrangeOperationArgHandlerMapping operationArgHandlerMapping,
		RedisValueTypeEnum valueType,
		RedisValueTypeEnum keyType,
		OrangeRedisCircuitBreaker circuitBreaker,
		OrangeRedisProperties properties
	) {
		super(operationOwner, mapping, redisKey, operationArgHandlerMapping, valueType,circuitBreaker,properties);
		this.keyType = keyType;
	}

	/**
	 * Creates a new Redis Hash context builder.
	 * 
	 * <p>This method instantiates a new {@link OrangeRedisHashContextBuilder} which is
	 * specialized for building execution contexts for Redis Hash operations. The context
	 * builder is responsible for:
	 * 
	 * <ul>
	 *   <li>Constructing operation-specific execution contexts</li>
	 *   <li>Managing Hash key and field serialization</li>
	 *   <li>Handling Hash-specific parameters and configurations</li>
	 *   <li>Building appropriate execution strategies</li>
	 * </ul>
	 * 
	 * @return a new instance of {@link OrangeRedisHashContextBuilder}
	 * @see OrangeRedisHashContextBuilder
	 * @see OrangeRedisContextBuilder
	 */
	@Override
	protected OrangeRedisContextBuilder newBuilder() {
		return new OrangeRedisHashContextBuilder();
	}

	/**
	 * Creates and configures a context builder for a specific method invocation with executor.
	 * 
	 * <p>This method extends the base context builder creation process with Hash-specific
	 * configuration. It first calls the superclass implementation to create a basic
	 * context builder, then casts it to {@link OrangeRedisHashContextBuilder} and
	 * configures Hash-specific properties such as the key type for hash field serialization.
	 * 
	 * <p>The configured context builder is used to:
	 * <ul>
	 *   <li>Create an execution context specific to the invoked method and executor</li>
	 *   <li>Apply method-specific configurations and parameters</li>
	 *   <li>Set up proper serialization for hash keys and values</li>
	 *   <li>Prepare for the execution of the Redis Hash operation</li>
	 * </ul>
	 * 
	 * @param executor the Redis executor that will perform the operation
	 * @param method the method being invoked
	 * @param args the arguments passed to the method
	 * @return a configured {@link OrangeRedisHashContextBuilder}
	 * @throws Exception if an error occurs during context builder creation
	 * @see OrangeRedisHashContextBuilder
	 * @see OrangeRedisContextBuilder
	 * @see OrangeRedisExecutor
	 */
	@Override
	protected OrangeRedisContextBuilder createContextBuilder(
		OrangeRedisExecutor executor, 
		Method method, 
		Object[] args
	) throws Exception {
		OrangeRedisContextBuilder builder = super.createContextBuilder(executor, method, args);
		((OrangeRedisHashContextBuilder)builder).keyType(this.keyType);
		return builder;
	}
	
}