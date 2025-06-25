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
import java.util.ArrayList;
import java.util.List;

import com.langwuyue.orange.redis.OrangeRedisCircuitBreaker;
import com.langwuyue.orange.redis.annotation.OrangeRedisKey;
import com.langwuyue.orange.redis.context.OrangeRedisContext.Key;
import com.langwuyue.orange.redis.context.builder.OrangeCrossKeysOperationArgHandlerMapping;
import com.langwuyue.orange.redis.context.builder.OrangeOperationArgHandlerMapping;
import com.langwuyue.orange.redis.context.builder.OrangeRedisContextBuilder;
import com.langwuyue.orange.redis.context.builder.OrangeRedisCrossKeysContextBuilder;
import com.langwuyue.orange.redis.executor.OrangeRedisExecutor;
import com.langwuyue.orange.redis.mapping.OrangeRedisExecutorsMapping;
import com.langwuyue.orange.redis.util.OrangeStringTemlateUtils;

/**
 * Invocation handler for Redis operations that involve multiple keys.
 * 
 * <p>This handler extends the base Redis invocation handler to support operations
 * that need to work with multiple Redis keys simultaneously, such as set operations
 * (union, intersection, etc.) or operations that need to read from multiple sources
 * and store to a destination key.
 * 
 * <p>The handler processes {@link OrangeRedisKey} annotations to extract key information
 * and supports template-based key generation for both source and destination keys.
 * 
 * <p>Key features of this handler include:
 * <ul>
 *   <li>Support for operations across multiple source keys (e.g., SUNION, SINTER)</li>
 *   <li>Support for operations with destination keys (e.g., SUNIONSTORE, SINTERSTORE)</li>
 *   <li>Dynamic key resolution using templates and method parameters</li>
 *   <li>Integration with the circuit breaker pattern for fault tolerance</li>
 * </ul>
 * 
 * <p>This handler uses {@link OrangeCrossKeysOperationArgHandlerMapping} to determine
 * which classes define the source and destination keys for each operation.
 *
 * @author Liang.Zhong
 * @since 1.0.0
 * @see OrangeRedisClientInvocationHandler
 * @see OrangeRedisKey
 * @see OrangeCrossKeysOperationArgHandlerMapping
 * @see OrangeRedisCrossKeysContextBuilder
 */
public class OrangeRedisCrossKeysClientInvocationHandler extends OrangeRedisClientInvocationHandler {
	
	/**
	 * Creates a new invocation handler for Redis operations involving multiple keys.
	 * 
	 * <p>This constructor initializes the handler with all necessary components for
	 * handling cross-keys Redis operations. It extends the base Redis invocation handler
	 * with additional support for operations that work with multiple keys.
	 *
	 * @param operationOwner the class that owns the Redis operations (typically the client interface)
	 * @param mapping the mapping between methods and their corresponding Redis executors
	 * @param operationArgHandlerMapping the mapping for handling method arguments in operations
	 * @param circuitBreaker the circuit breaker for fault tolerance (may be null if not used)
	 * @param properties the Redis configuration properties
	 */
	public OrangeRedisCrossKeysClientInvocationHandler(
		Class<?> operationOwner,
		OrangeRedisExecutorsMapping mapping,
		OrangeOperationArgHandlerMapping operationArgHandlerMapping,
		OrangeRedisCircuitBreaker circuitBreaker​​,
		OrangeRedisProperties properties
	) {
		super(operationOwner, mapping, null, operationArgHandlerMapping,null,circuitBreaker​​,properties);
	}

	/**
	 * Creates a new instance of the cross-keys context builder.
	 * 
	 * <p>This method overrides the base implementation to provide a specialized
	 * context builder that supports operations involving multiple Redis keys.
	 * The returned builder can handle both source keys and destination keys
	 * for operations like SUNION, SINTER, SUNIONSTORE, etc.
	 *
	 * @return a new instance of {@link OrangeRedisCrossKeysContextBuilder}
	 * @see OrangeRedisCrossKeysContextBuilder
	 */
	@Override
	protected OrangeRedisContextBuilder newBuilder() {
		return new OrangeRedisCrossKeysContextBuilder();
	}
	
	/**
	 * Returns null as this method is not used for cross-keys operations.
	 * 
	 * <p>For cross-keys operations, the {@link #getKeys(Method, Object[])} method
	 * is used instead to retrieve multiple keys.
	 *
	 * @param method the method being invoked
	 * @param args the arguments to the method
	 * @return always returns null
	 */
	@Override
	protected Key getKey(Method method,Object[] args) {
		return null;
	}
	
	/**
	 * Creates and configures a context builder for cross-keys Redis operations.
	 * 
	 * <p>This method extends the base implementation to add support for multiple source
	 * keys and an optional destination key. It retrieves the keys from method annotations
	 * and configures the context builder accordingly.
	 *
	 * @param executor the Redis executor that will perform the operation
	 * @param method the method being invoked
	 * @param args the arguments to the method
	 * @return a configured Redis context builder for cross-keys operations
	 * @throws Exception if an error occurs during context builder creation
	 */
	protected OrangeRedisContextBuilder createContextBuilder(OrangeRedisExecutor executor,Method method, Object[] args) throws Exception {
		OrangeRedisCrossKeysContextBuilder builder = (OrangeRedisCrossKeysContextBuilder)super.createContextBuilder(executor, method, args);
		builder.keys(getKeys(method,args));
		builder.storeTo(getStoreTo(method,args));
		return builder;
	}
	
	/**
	 * Retrieves the destination key for storing operation results.
	 * 
	 * <p>This method uses the {@link OrangeCrossKeysOperationArgHandlerMapping} to determine
	 * the class that defines the destination key, then processes its {@link OrangeRedisKey}
	 * annotation to generate the actual key using the method parameters.
	 *
	 * @param method the method being invoked
	 * @param args the arguments to the method
	 * @return the generated destination key, or null if no destination is specified
	 */
	private String getStoreTo(Method method, Object[] args) {
		OrangeCrossKeysOperationArgHandlerMapping mapping = (OrangeCrossKeysOperationArgHandlerMapping)this.getOperationArgHandlerMapping();
		Class<?> keyClass = mapping.getStoreTo(method);
		if(keyClass == null) {
			return null;
		}
		OrangeRedisKey redisKey = keyClass.getAnnotation(OrangeRedisKey.class);
		return OrangeStringTemlateUtils.getString(getOriginKey(redisKey.key()), method, args);
	}

	/**
	 * Retrieves the list of source keys for the Redis operation.
	 * 
	 * <p>This method uses the {@link OrangeCrossKeysOperationArgHandlerMapping} to determine
	 * the classes that define the source keys, then processes their {@link OrangeRedisKey}
	 * annotations to generate the actual keys using the method parameters.
	 *
	 * <p>For operations like SUNION, SINTER, etc., this method will return
	 * multiple keys that the operation should be performed on.
	 *
	 * @param method the method being invoked
	 * @param args the arguments to the method
	 * @return a list of generated source keys
	 */
	private List<String> getKeys(Method method, Object[] args) {
		OrangeCrossKeysOperationArgHandlerMapping mapping = (OrangeCrossKeysOperationArgHandlerMapping)this.getOperationArgHandlerMapping();
		List<Class<?>> keyClasses = mapping.getKeyClasses(method);
		List<String> keys = new ArrayList<>();
		for(Class<?> keyClass : keyClasses) {
			OrangeRedisKey redisKey = keyClass.getAnnotation(OrangeRedisKey.class);
			String key = OrangeStringTemlateUtils.getString(getOriginKey(redisKey.key()), method, args);
			keys.add(key);
		}
		return keys;
	}
}