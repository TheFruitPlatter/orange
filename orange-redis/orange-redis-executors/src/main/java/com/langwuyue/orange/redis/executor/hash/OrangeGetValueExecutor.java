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

import com.langwuyue.orange.redis.annotation.hash.GetHashValues;
import com.langwuyue.orange.redis.annotation.hash.HashKey;
import com.langwuyue.orange.redis.context.OrangeRedisContext;
import com.langwuyue.orange.redis.executor.OrangeRedisGetOneAbstractExecutor;
import com.langwuyue.orange.redis.executor.hash.context.OrangeHashKeyContext;
import com.langwuyue.orange.redis.mapping.OrangeRedisExecutorIdGenerator;
import com.langwuyue.orange.redis.operations.OrangeRedisHashOperations;
import com.langwuyue.orange.redis.utils.OrangeCollectionUtils;

/**
 * Executor for retrieving a single value from a Redis Hash.
 *
 * <p>This executor provides functionality to get a single value from a Redis Hash
 * by its key. It supports type conversion of the returned value.
 *
 * <p>The executor supports the following annotations:
 * <ul>
 *   <li>{@link GetHashValues} - Marks this as a hash value retrieval operation</li>
 *   <li>{@link HashKey} - Specifies the hash key</li>
 * </ul>
 *
 * <p>Key features:
 * <ul>
 *   <li>Returns a single value from the hash</li>
 *   <li>Supports type conversion of returned values</li>
 *   <li>Uses {@link OrangeRedisHashOperations} for Redis operations</li>
 * </ul>
 *
 * <p>Type conversion:
 * The executor will automatically convert the raw Redis value to the declared return type,
 * including support for complex object types through JSON serialization.
 *
 * @author Liang.Zhong
 * @since 1.0.0
 */
public class OrangeGetValueExecutor extends OrangeRedisGetOneAbstractExecutor {
	
	private OrangeRedisHashOperations operations;

	public OrangeGetValueExecutor(OrangeRedisHashOperations operations,OrangeRedisExecutorIdGenerator idGenerator) {
		super(idGenerator);
		this.operations = operations;
	}

	@Override
	protected List<Class<? extends Annotation>> getSupportedAnnotationClasses() {
		return OrangeCollectionUtils.asList(GetHashValues.class,HashKey.class);
	}

	@Override
	public Class<? extends OrangeRedisContext> getContextClass() {
		return OrangeHashKeyContext.class;
	}

	@Override
	protected Collection doGet(OrangeRedisContext context, Field valueField, Type returnArgumentType) throws Exception {
		OrangeHashKeyContext ctx = (OrangeHashKeyContext) context;
		Object value = this.operations.get(
				ctx.getRedisKey().getValue(),
				ctx.getHashKey(),
				ctx.getKeyType(), 
				ctx.getValueType(),
				valueField == null ? returnArgumentType : valueField.getGenericType()
		);
		return OrangeCollectionUtils.asList(value);
	}
}