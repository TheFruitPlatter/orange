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
package com.langwuyue.orange.redis.executor.hash;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import com.langwuyue.orange.redis.annotation.Multiple;
import com.langwuyue.orange.redis.annotation.hash.GetHashValues;
import com.langwuyue.orange.redis.context.OrangeRedisContext;
import com.langwuyue.orange.redis.executor.OrangeRedisGetAbstractExecutor;
import com.langwuyue.orange.redis.executor.hash.context.OrangeHashKeysContext;
import com.langwuyue.orange.redis.mapping.OrangeRedisExecutorIdGenerator;
import com.langwuyue.orange.redis.operations.OrangeRedisHashOperations;
import com.langwuyue.orange.redis.utils.OrangeCollectionUtils;
import com.langwuyue.orange.redis.utils.OrangeReflectionUtils;

/**
 * @author Liang.Zhong
 * @since 1.0.0
 */
public class OrangeGetValuesExecutor extends OrangeRedisGetAbstractExecutor {
	
	private OrangeRedisHashOperations operations;

	public OrangeGetValuesExecutor(OrangeRedisHashOperations operations,OrangeRedisExecutorIdGenerator idGenerator) {
		super(idGenerator);
		this.operations = operations;
	}

	@Override
	protected List<Class<? extends Annotation>> getSupportedAnnotationClasses() {
		return OrangeCollectionUtils.asList(GetHashValues.class,Multiple.class);
	}

	@Override
	public Class<? extends OrangeRedisContext> getContextClass() {
		return OrangeHashKeysContext.class;
	}

	@Override
	protected Collection doGet(OrangeRedisContext context, Field valueField, Type returnArgumentType) throws Exception {
		OrangeHashKeysContext ctx = (OrangeHashKeysContext) context;
		return this.operations.multiGet(
				ctx.getRedisKey().getValue(),
				ctx.getHashKeys(),
				ctx.getKeyType(),
				ctx.getValueType(),
				valueField == null ? returnArgumentType : valueField.getGenericType()
		);
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
			OrangeHashKeysContext ctx = (OrangeHashKeysContext) context;
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

	OrangeRedisHashOperations getOperations() {
		return operations;
	}
}
