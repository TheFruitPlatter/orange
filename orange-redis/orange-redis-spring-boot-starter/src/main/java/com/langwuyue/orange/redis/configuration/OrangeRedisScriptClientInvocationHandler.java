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
import com.langwuyue.orange.redis.context.builder.OrangeOperationArgHandlerMapping;
import com.langwuyue.orange.redis.context.builder.OrangeRedisContextBuilder;
import com.langwuyue.orange.redis.context.builder.OrangeRedisScriptContextBuilder;
import com.langwuyue.orange.redis.context.builder.OrangeScriptArgHandlerMapping;
import com.langwuyue.orange.redis.executor.OrangeRedisExecutor;
import com.langwuyue.orange.redis.mapping.OrangeRedisExecutorsMapping;
import com.langwuyue.orange.redis.util.OrangeStringTemlateUtils;

/**
 * @author Liang.Zhong
 * @since 1.0.0
 */
public class OrangeRedisScriptClientInvocationHandler extends OrangeRedisClientInvocationHandler {
	
	public OrangeRedisScriptClientInvocationHandler(
		Class<?> operationOwner,
		OrangeRedisExecutorsMapping mapping,
		OrangeOperationArgHandlerMapping operationArgHandlerMapping,
		OrangeRedisCircuitBreaker circuitBreaker,
		OrangeRedisProperties properties
	) {
		super(operationOwner, mapping, null, operationArgHandlerMapping,null,circuitBreaker,properties);
	}

	@Override
	protected OrangeRedisContextBuilder newBuilder() {
		return new OrangeRedisScriptContextBuilder();
	}
	
	@Override
	protected Key getKey(Method method,Object[] args) {
		return null;
	}
	
	@Override
	protected OrangeRedisContextBuilder createContextBuilder(OrangeRedisExecutor executor,Method method, Object[] args) throws Exception {
		OrangeRedisScriptContextBuilder builder = (OrangeRedisScriptContextBuilder)super.createContextBuilder(executor, method, args);
		builder.keys(getKeys(method,args));
		return builder;
	}
	
	private List<String> getKeys(Method method, Object[] args) {
		OrangeScriptArgHandlerMapping mapping = (OrangeScriptArgHandlerMapping)this.getOperationArgHandlerMapping();
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
