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
 * @author Liang.Zhong
 * @since 1.0.0
 */
public class OrangeCASWithExpirationExecutor extends OrangeCompareAndSwapExecutor {
	
	private OrangeRedisValueOperations operations;
	
	public OrangeCASWithExpirationExecutor(
			OrangeRedisScriptOperations scriptOperations,
			OrangeRedisValueOperations operations,
			OrangeRedisExecutorIdGenerator idGenerator,
			OrangeRedisLogger logger
	) {
		super(scriptOperations,idGenerator,logger);
		this.operations = operations;
	}

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

	@Override
	public List<Class<? extends Annotation>> getSupportedAnnotationClasses() {
		List<Class<? extends Annotation>> classes = super.getSupportedAnnotationClasses();
		classes.add(SetExpiration.class);
		return classes;
	}

	@Override
	public Class<? extends OrangeRedisContext> getContextClass() {
		return OrangeCompareAndSwapContext.class;
	}
}
