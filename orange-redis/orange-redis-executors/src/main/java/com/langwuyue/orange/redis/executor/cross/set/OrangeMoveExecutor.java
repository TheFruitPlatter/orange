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
package com.langwuyue.orange.redis.executor.cross.set;

import java.lang.annotation.Annotation;
import java.util.List;

import com.langwuyue.orange.redis.annotation.RedisValue;
import com.langwuyue.orange.redis.annotation.cross.CrossOperationKeys;
import com.langwuyue.orange.redis.annotation.cross.Move;
import com.langwuyue.orange.redis.annotation.cross.StoreTo;
import com.langwuyue.orange.redis.context.OrangeRedisContext;
import com.langwuyue.orange.redis.executor.OrangeRedisAbstractExecutor;
import com.langwuyue.orange.redis.executor.cross.set.context.OrangeMoveContext;
import com.langwuyue.orange.redis.mapping.OrangeRedisExecutorIdGenerator;
import com.langwuyue.orange.redis.operations.OrangeRedisSetOperations;
import com.langwuyue.orange.redis.utils.OrangeCollectionUtils;

/**
 * @author Liang.Zhong
 * @since 1.0.0
 */
public class OrangeMoveExecutor extends OrangeRedisAbstractExecutor {
	
	private OrangeRedisSetOperations operations;

	public OrangeMoveExecutor(OrangeRedisSetOperations operations,OrangeRedisExecutorIdGenerator idGenerator) {
		super(idGenerator);
		this.operations = operations;
	}

	@Override
	protected List<Class<? extends Annotation>> getSupportedAnnotationClasses() {
		return OrangeCollectionUtils.asList(Move.class,CrossOperationKeys.class,StoreTo.class,RedisValue.class);
	}

	@Override
	public Object execute(OrangeRedisContext context) throws Exception {
		OrangeMoveContext ctx = (OrangeMoveContext) context;
		return this.operations.move(ctx.getReferenceKey(),ctx.getValue(),ctx.getValueType(),ctx.getStoreTo());
	}
	
	@Override
	public Class<? extends OrangeRedisContext> getContextClass() {
		return OrangeMoveContext.class;
	}
}
