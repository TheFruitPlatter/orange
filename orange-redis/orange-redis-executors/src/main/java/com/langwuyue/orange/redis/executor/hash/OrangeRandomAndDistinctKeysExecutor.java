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
import java.util.LinkedHashSet;
import java.util.List;

import com.langwuyue.orange.redis.annotation.Distinct;
import com.langwuyue.orange.redis.context.OrangeRedisContext;
import com.langwuyue.orange.redis.executor.hash.context.OrangeCountHashContext;
import com.langwuyue.orange.redis.mapping.OrangeRedisExecutorIdGenerator;
import com.langwuyue.orange.redis.operations.OrangeRedisHashOperations;

/**
 * @author Liang.Zhong
 * @since 1.0.0
 */
public class OrangeRandomAndDistinctKeysExecutor extends OrangeRandomKeysExecutor {
	
	private OrangeRedisHashOperations operations;

	public OrangeRandomAndDistinctKeysExecutor(OrangeRedisHashOperations operations,OrangeRedisExecutorIdGenerator idGenerator) {
		super(operations,idGenerator);
		this.operations = operations;
	}

	@Override
	protected List<Class<? extends Annotation>> getSupportedAnnotationClasses() {
		List classes = super.getSupportedAnnotationClasses();
		classes.add(Distinct.class);
		return classes;
	}

	@Override
	public Class<? extends OrangeRedisContext> getContextClass() {
		return OrangeCountHashContext.class;
	}
	
	@Override
	protected Collection doGet(OrangeRedisContext context, Field valueField, Type returnArgumentType) throws Exception {
		OrangeCountHashContext ctx = (OrangeCountHashContext) context;
		// Note: When the requested key count exceeds the total size,
		// 'randomKeys' method automatically returns all available keys instead. 
		List<Object> keys = this.operations.randomKeys(ctx.getRedisKey().getValue(), ctx.getCount(), ctx.getKeyType(), valueField == null ? returnArgumentType : valueField.getGenericType());
		// Proactively handle cases where the keys's size may exceed total size,
		// as the randomKeys method behavior might change in the future
		LinkedHashSet result = new LinkedHashSet<>();
		if(keys == null) {
			return result;
		}
		for(Object key : keys) {
			result.add(key);
		}
		return result;
	}
}
