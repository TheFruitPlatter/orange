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
package com.langwuyue.orange.redis.executor.set;

import java.lang.annotation.Annotation;
import java.util.List;
import java.util.Map;

import com.langwuyue.orange.redis.annotation.RedisValue;
import com.langwuyue.orange.redis.annotation.set.IsMembers;
import com.langwuyue.orange.redis.context.OrangeRedisContext;
import com.langwuyue.orange.redis.context.OrangeRedisValueContext;
import com.langwuyue.orange.redis.executor.OrangeRedisAbstractExecutor;
import com.langwuyue.orange.redis.mapping.OrangeRedisExecutorIdGenerator;
import com.langwuyue.orange.redis.operations.OrangeRedisSetOperations;
import com.langwuyue.orange.redis.utils.OrangeCollectionUtils;

/**
 * @author Liang.Zhong
 * @since 1.0.0
 */
public class OrangeIsMemberExecutor extends OrangeRedisAbstractExecutor {
	
	private OrangeRedisSetOperations operations;

	public OrangeIsMemberExecutor(OrangeRedisSetOperations operations,OrangeRedisExecutorIdGenerator idGenerator) {
		super(idGenerator);
		this.operations = operations;
	}

	@Override
	public Object execute(OrangeRedisContext context) throws Exception {
		OrangeRedisValueContext ctx = (OrangeRedisValueContext)context;
		Map<Object, Boolean> resultMap = this.operations.isMember(ctx.getRedisKey().getValue(), context.getValueType(), ctx.getValue());
		return resultMap.get(ctx.getValue());
	}

	@Override
	protected List<Class<? extends Annotation>> getSupportedAnnotationClasses() {
		return OrangeCollectionUtils.asList(IsMembers.class,RedisValue.class);
	}
	
	@Override
	public Class<? extends OrangeRedisContext> getContextClass() {
		return OrangeRedisValueContext.class;
	}
}
