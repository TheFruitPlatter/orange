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
import java.util.List;

import com.langwuyue.orange.redis.annotation.ContinueOnFailure;
import com.langwuyue.orange.redis.annotation.Multiple;
import com.langwuyue.orange.redis.annotation.transaction.Release;
import com.langwuyue.orange.redis.context.OrangeRedisContext;
import com.langwuyue.orange.redis.context.OrangeRedisMultipleValueContext;
import com.langwuyue.orange.redis.executor.OrangeRemoveMembersAbstractExecutor;
import com.langwuyue.orange.redis.logger.OrangeRedisLogger;
import com.langwuyue.orange.redis.mapping.OrangeRedisExecutorIdGenerator;
import com.langwuyue.orange.redis.operations.OrangeRedisHashOperations;
import com.langwuyue.orange.redis.utils.OrangeCollectionUtils;

/**
 * @author Liang.Zhong
 * @since 1.0.0
 */
public class OrangeMultipleLocksReleaseExecutor extends OrangeRemoveMembersAbstractExecutor {
	
	private OrangeRedisHashOperations operations;

	public OrangeMultipleLocksReleaseExecutor(OrangeRedisHashOperations operations,OrangeRedisExecutorIdGenerator idGenerator,OrangeRedisLogger logger) {
		super(idGenerator,logger);
		this.operations = operations;
	}

	@Override
	protected List<Class<? extends Annotation>> getSupportedAnnotationClasses() {
		return OrangeCollectionUtils.asList(Release.class, Multiple.class, ContinueOnFailure.class);
	}
	
	@Override
	public Class<? extends OrangeRedisContext> getContextClass() {
		return OrangeRedisMultipleValueContext.class;
	}

	@Override
	protected Long doRemove(OrangeRedisContext ctx) throws Exception {
		OrangeRedisMultipleValueContext context = (OrangeRedisMultipleValueContext)ctx;
		return this.operations.removeMembers(ctx.getRedisKey().getValue(), ctx.getValueType(), context.toArray());
	}

	@Override
	protected Long doRemove(OrangeRedisContext ctx, Object value) throws Exception {
		return this.operations.removeMembers(ctx.getRedisKey().getValue(), ctx.getValueType(), value);
	}
}
