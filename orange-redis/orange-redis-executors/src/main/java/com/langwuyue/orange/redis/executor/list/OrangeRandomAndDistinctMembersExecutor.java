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
package com.langwuyue.orange.redis.executor.list;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import com.langwuyue.orange.redis.annotation.Count;
import com.langwuyue.orange.redis.annotation.Distinct;
import com.langwuyue.orange.redis.annotation.GetMembers;
import com.langwuyue.orange.redis.annotation.Random;
import com.langwuyue.orange.redis.context.OrangeRedisContext;
import com.langwuyue.orange.redis.context.OrangeRedisCountContext;
import com.langwuyue.orange.redis.executor.OrangeRedisGetAbstractExecutor;
import com.langwuyue.orange.redis.mapping.OrangeRedisExecutorIdGenerator;
import com.langwuyue.orange.redis.operations.OrangeRedisListOperations;
import com.langwuyue.orange.redis.utils.OrangeCollectionUtils;

/**
 * @author Liang.Zhong
 * @since 1.0.0
 */
public class OrangeRandomAndDistinctMembersExecutor extends OrangeRedisGetAbstractExecutor {
	
	private OrangeRedisListOperations operations;

	public OrangeRandomAndDistinctMembersExecutor(OrangeRedisListOperations operations,OrangeRedisExecutorIdGenerator idGenerator) {
		super(idGenerator);
		this.operations = operations;
	}

	@Override
	protected List<Class<? extends Annotation>> getSupportedAnnotationClasses() {
		return OrangeCollectionUtils.asList(GetMembers.class,Random.class,Count.class,Distinct.class);
	}
	
	@Override
	public Class<? extends OrangeRedisContext> getContextClass() {
		return OrangeRedisCountContext.class;
	}

	@Override
	protected Collection doGet(OrangeRedisContext context, Field valueField, Type returnArgumentType) throws Exception {
		OrangeRedisCountContext ctx = (OrangeRedisCountContext) context;
		Integer count = ctx.getCount();
		if(count == null || count <= 0) {
			return new ArrayList<>();
		}
		String key = context.getRedisKey().getValue();
		Long size = this.operations.size(key);
		if(size == null || size <= 0) {
			return new ArrayList<>();
		}
		
		Set<Object> results = new LinkedHashSet<>(count);
		for(int i = 0; i < count; i++) {
			Long index = Math.round(Math.random() * (size-1));
			Object result = this.operations.index(
				key, 
				index, 
				context.getValueType(),
				valueField == null ? returnArgumentType : valueField.getGenericType()
			);
			results.add(result);
		}
		return results;
	}

}
