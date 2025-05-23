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
 * @author Liang.Zhong
 * @since 1.0.0
 */
public class OrangeGetExpirationExecutor extends OrangeRedisGetAbstractExecutor {
	
	private OrangeRedisHashOperations operations;

	public OrangeGetExpirationExecutor(OrangeRedisHashOperations operations,OrangeRedisExecutorIdGenerator idGenerator) {
		super(idGenerator);
		this.operations = operations;
	}

	@Override
	protected List<Class<? extends Annotation>> getSupportedAnnotationClasses() {
		return OrangeCollectionUtils.asList(GetExpiration.class, Multiple.class,ContinueOnFailure.class);
	}
	
	@Override
	public Class<? extends OrangeRedisContext> getContextClass() {
		return OrangeMultipleLocksGetExpirationsContext.class;
	}

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
	
	protected Long convert(OrangeRedisContext ctx, Long source,TimeUnit sourceTimeUnit) {
		return ctx.getRedisKey().getExpirationTimeUnit().convert(source, sourceTimeUnit);
	}
	
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
