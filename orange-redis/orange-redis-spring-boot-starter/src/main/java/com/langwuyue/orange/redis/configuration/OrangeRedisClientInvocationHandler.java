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

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.concurrent.atomic.AtomicInteger;

import com.langwuyue.orange.redis.OrangeRedisCircuitBreaker;
import com.langwuyue.orange.redis.OrangeRedisException;
import com.langwuyue.orange.redis.OrangeRedisState;
import com.langwuyue.orange.redis.RedisValueTypeEnum;
import com.langwuyue.orange.redis.annotation.OrangeRedisKey;
import com.langwuyue.orange.redis.annotation.Timeout;
import com.langwuyue.orange.redis.context.OrangeRedisContext;
import com.langwuyue.orange.redis.context.OrangeRedisContext.Key;
import com.langwuyue.orange.redis.context.builder.OrangeOperationArgHandlerMapping;
import com.langwuyue.orange.redis.context.builder.OrangeRedisContextBuilder;
import com.langwuyue.orange.redis.executor.OrangeRedisExecutor;
import com.langwuyue.orange.redis.mapping.OrangeRedisExecutorsMapping;
import com.langwuyue.orange.redis.registry.OrangeSlowOperationRegistry;
import com.langwuyue.orange.redis.util.OrangeStringTemlateUtils;

/**
 * @author Liang.Zhong
 * @since 1.0.0
 */
public class OrangeRedisClientInvocationHandler implements InvocationHandler {
	
	private static AtomicInteger ACTIVE_REQUEST_COUNTER = new AtomicInteger(0);
	
	private OrangeRedisKey redisKey;
	
	private Class<?> operationOwner;
	
	private OrangeRedisExecutorsMapping mapping;
	
	private OrangeOperationArgHandlerMapping operationArgHandlerMapping;
	
	private RedisValueTypeEnum valueType;
	
	private OrangeRedisCircuitBreaker circuitBreaker;
	
	private OrangeRedisProperties properties;
	
	public OrangeRedisClientInvocationHandler(
		Class<?> operationOwner,
		OrangeRedisExecutorsMapping mapping,
		OrangeRedisKey redisKey,
		OrangeOperationArgHandlerMapping operationArgHandlerMapping,
		RedisValueTypeEnum valueType,
		OrangeRedisCircuitBreaker circuitBreaker,
		OrangeRedisProperties properties
	) {
		this.operationOwner = operationOwner;
		this.mapping = mapping;
		this.redisKey = redisKey;
		this.operationArgHandlerMapping = operationArgHandlerMapping;
		this.valueType = valueType;
		this.circuitBreaker = circuitBreaker;
		this.properties = properties;
	}
	
	@Override
	public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
		if(Object.class.equals(method.getDeclaringClass())) {
			return method.invoke(this, args);
		}
		OrangeRedisContext context = null;
		try {
			
			long start = System.currentTimeMillis();
			if(properties.getMetrics().getEnabled().booleanValue()) {
				ACTIVE_REQUEST_COUNTER.incrementAndGet();
			}
			
			// Get executor
			OrangeRedisExecutor executor = this.mapping.getExecutor(method);
			this.mapping.getLogger().debug("Got a executor {} for method {}", executor.getClass(), method);
			
			// Build context
			context = createContextBuilder(executor,method,args).build();
			
			// Circuit Breaker​​ 
			if(this.circuitBreaker != null && OrangeRedisState.isOutOfService()) {
				this.circuitBreaker.outOfService(
					context != null ? context.getRedisKey().getValue() : null, 
					this.operationOwner, 
					method, 
					args
				);
				return null;
			}
			
			// Executing
			Object result = executor.execute(context);
			if(!this.properties.getSlowOperation().isEnabled()) {
				return result;
			}
			long cost = System.currentTimeMillis() - start;
			long threshold = this.properties.getSlowOperation().getSlowOperationThreshold().toMillis();
			if(cost >= threshold) {
				this.mapping.getLogger().warn(
					"Slow operation found: execution time {}ms >= threshold {}ms.\n Key: {}.\n Operation: {}.", 
					cost, 
					threshold,
					context.getRedisKey().getValue(),
					method
				);
				// Collect slow operations for management.
				OrangeSlowOperationRegistry.register(this.operationOwner,method, cost);
			}
			return result;
		}catch (Exception e) {
			if(this.circuitBreaker == null) {
				throw new OrangeRedisException(String.format("Execution Error! %n Operation Owner: %s %n Operation : %s", this.operationOwner,method),e);
			}
			String key = null;
			if(context != null && context.getRedisKey() != null) {
				key = context.getRedisKey().getValue();
			}
			this.circuitBreaker.onException(
				key, 
				this.operationOwner, 
				method, 
				args, 
				e
			);
			return null;
		}finally {
			if(properties.getMetrics().getEnabled().booleanValue()) {
				ACTIVE_REQUEST_COUNTER.decrementAndGet();
			}
		}
	}
	
	protected OrangeRedisContextBuilder newBuilder() {
		return new OrangeRedisContextBuilder();
	}
	
	protected OrangeRedisContextBuilder createContextBuilder(OrangeRedisExecutor executor,Method method, Object[] args) throws Exception {
		Method actualMethod = this.mapping.getActualMethod(method);
		Class<? extends OrangeRedisContext> contextClass = executor.getContextClass();
		OrangeRedisContextBuilder builder = newBuilder();
		builder.contextClass(contextClass);
		builder.operationArgHandlerMapping(this.operationArgHandlerMapping);
		builder.args(args);
		builder.actualMethod(actualMethod);
		builder.operationMethod(method);
		builder.operationOwner(this.operationOwner);
		builder.redisKey(getKey(method,args));
		builder.valueType(this.valueType);
		return builder;
	}
	
	protected Key getKey(Method method,Object[] args) {
		String originKey = getOriginKey(this.redisKey.key());
		String key = OrangeStringTemlateUtils.getString(originKey, method, args);
		Timeout timeout = this.redisKey.expirationTime();
		return new OrangeRedisContext.Key(
			originKey,
			key, 
			timeout.value(), 
			timeout.unit()
		);
	}
	
	protected String getOriginKey(String key) {
		return this.properties.getKeyPrefix() + key;
	}

	protected OrangeRedisKey getRedisKey() {
		return redisKey;
	}

	protected Class<?> getOperationOwner() {
		return operationOwner;
	}

	protected OrangeRedisExecutorsMapping getMapping() {
		return mapping;
	}

	protected OrangeOperationArgHandlerMapping getOperationArgHandlerMapping() {
		return operationArgHandlerMapping;
	}

	protected RedisValueTypeEnum getValueType() {
		return valueType;
	}
	
	public int getActiveRequestCount() {
		return ACTIVE_REQUEST_COUNTER.get();
	}
}
