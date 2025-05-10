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

import com.langwuyue.orange.redis.annotation.Random;
import com.langwuyue.orange.redis.annotation.hash.GetHashKeys;
import com.langwuyue.orange.redis.annotation.hash.HashKey;
import com.langwuyue.orange.redis.context.OrangeRedisContext;
import com.langwuyue.orange.redis.executor.OrangeRedisGetOneAbstractExecutor;
import com.langwuyue.orange.redis.executor.hash.context.OrangeHashContext;
import com.langwuyue.orange.redis.mapping.OrangeRedisExecutorIdGenerator;
import com.langwuyue.orange.redis.operations.OrangeRedisHashOperations;
import com.langwuyue.orange.redis.utils.OrangeCollectionUtils;

/**
 * @author Liang.Zhong
 * @since 1.0.0
 */
public class OrangeRandomKeyExecutor extends OrangeRedisGetOneAbstractExecutor {
	
	private OrangeRedisHashOperations operations;

	public OrangeRandomKeyExecutor(OrangeRedisHashOperations operations,OrangeRedisExecutorIdGenerator idGenerator) {
		super(idGenerator);
		this.operations = operations;
	}

	@Override
	protected List<Class<? extends Annotation>> getSupportedAnnotationClasses() {
		return OrangeCollectionUtils.asList(GetHashKeys.class,Random.class);
	}

	@Override
	public Class<? extends OrangeRedisContext> getContextClass() {
		return OrangeHashContext.class;
	}
	
	@Override
	protected Field getValueField(Type type) {
		Class<?> returnType = getRawType(type);
		Field[] fields = returnType.getDeclaredFields();
		for(Field field : fields) {
			if(field.isAnnotationPresent(HashKey.class)) {
				return field;
			}
		}
		return null;
	}

	@Override
	protected Collection doGet(OrangeRedisContext context, Field valueField, Type returnArgumentType) throws Exception {
		OrangeHashContext ctx = (OrangeHashContext) context;
		return this.operations.randomKeys(ctx.getRedisKey().getValue(), 1, ctx.getKeyType(), valueField == null ? returnArgumentType : valueField.getGenericType());
	}
}
