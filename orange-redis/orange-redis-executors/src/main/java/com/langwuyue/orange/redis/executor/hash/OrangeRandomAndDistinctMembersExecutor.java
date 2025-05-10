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
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;

import com.langwuyue.orange.redis.annotation.Count;
import com.langwuyue.orange.redis.annotation.Distinct;
import com.langwuyue.orange.redis.annotation.Random;
import com.langwuyue.orange.redis.context.OrangeRedisContext;
import com.langwuyue.orange.redis.executor.hash.context.OrangeCountHashContext;
import com.langwuyue.orange.redis.mapping.OrangeRedisExecutorIdGenerator;
import com.langwuyue.orange.redis.operations.OrangeRedisHashOperations;

/**
 * @author Liang.Zhong
 * @since 1.0.0
 */
public class OrangeRandomAndDistinctMembersExecutor extends OrangeGetMembersExecutor {
	
	private OrangeRedisHashOperations operations;

	public OrangeRandomAndDistinctMembersExecutor(OrangeRedisHashOperations operations,OrangeRedisExecutorIdGenerator idGenerator) {
		super(operations,idGenerator);
		this.operations = operations;
	}

	@Override
	protected List<Class<? extends Annotation>> getSupportedAnnotationClasses() {
		List classes = super.getSupportedAnnotationClasses();
		classes.add(Random.class);
		classes.add(Count.class);
		classes.add(Distinct.class);
		return classes;
	}

	@Override
	public Class<? extends OrangeRedisContext> getContextClass() {
		return OrangeCountHashContext.class;
	}

	@Override
	protected Map doGet(OrangeRedisContext context, Type keyType, Type valueType) throws Exception {
		OrangeCountHashContext ctx = (OrangeCountHashContext) context;
		return this.operations.randomEntries(ctx.getRedisKey().getValue(), ctx.getCount(), ctx.getKeyType(), ctx.getValueType(), keyType, valueType);
	}
}
