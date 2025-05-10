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
import java.util.List;

import com.langwuyue.orange.redis.annotation.hash.HasKeys;
import com.langwuyue.orange.redis.annotation.hash.HashKey;
import com.langwuyue.orange.redis.context.OrangeRedisContext;
import com.langwuyue.orange.redis.executor.OrangeRedisAbstractExecutor;
import com.langwuyue.orange.redis.executor.hash.context.OrangeHashKeyContext;
import com.langwuyue.orange.redis.mapping.OrangeRedisExecutorIdGenerator;
import com.langwuyue.orange.redis.operations.OrangeRedisHashOperations;
import com.langwuyue.orange.redis.utils.OrangeCollectionUtils;

/**
 * @author Liang.Zhong
 * @since 1.0.0
 */
public class OrangeHasKeyExecutor extends OrangeRedisAbstractExecutor {
	
	private OrangeRedisHashOperations operations;

	public OrangeHasKeyExecutor(OrangeRedisHashOperations operations,OrangeRedisExecutorIdGenerator idGenerator) {
		super(idGenerator);
		this.operations = operations;
	}

	@Override
	protected List<Class<? extends Annotation>> getSupportedAnnotationClasses() {
		return OrangeCollectionUtils.asList(HasKeys.class,HashKey.class);
	}

	@Override
	public Class<? extends OrangeRedisContext> getContextClass() {
		return OrangeHashKeyContext.class;
	}

	@Override
	public Object execute(OrangeRedisContext context) throws Exception {
		OrangeHashKeyContext ctx = (OrangeHashKeyContext) context;
		return this.operations.hasKey(
				ctx.getRedisKey().getValue(),
				ctx.getHashKey(),
				ctx.getKeyType()
		);
	}
}
