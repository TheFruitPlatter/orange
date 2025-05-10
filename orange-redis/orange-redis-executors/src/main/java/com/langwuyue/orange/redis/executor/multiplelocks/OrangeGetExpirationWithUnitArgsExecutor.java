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
import java.util.concurrent.TimeUnit;

import com.langwuyue.orange.redis.annotation.ContinueOnFailure;
import com.langwuyue.orange.redis.annotation.Multiple;
import com.langwuyue.orange.redis.annotation.TimeoutUnit;
import com.langwuyue.orange.redis.annotation.global.GetExpiration;
import com.langwuyue.orange.redis.context.OrangeRedisContext;
import com.langwuyue.orange.redis.executor.multiplelocks.context.OrangeMultipleLocksGetExpirationsWithUnitArgsContext;
import com.langwuyue.orange.redis.mapping.OrangeRedisExecutorIdGenerator;
import com.langwuyue.orange.redis.operations.OrangeRedisHashOperations;
import com.langwuyue.orange.redis.utils.OrangeCollectionUtils;

/**
 * @author Liang.Zhong
 * @since 1.0.0
 */
public class OrangeGetExpirationWithUnitArgsExecutor extends OrangeGetExpirationExecutor {
	
	public OrangeGetExpirationWithUnitArgsExecutor(OrangeRedisHashOperations operations,OrangeRedisExecutorIdGenerator idGenerator) {
		super(operations,idGenerator);
	}

	@Override
	protected List<Class<? extends Annotation>> getSupportedAnnotationClasses() {
		return OrangeCollectionUtils.asList(GetExpiration.class,TimeoutUnit.class,Multiple.class,ContinueOnFailure.class);
	}

	@Override
	public Class<? extends OrangeRedisContext> getContextClass() {
		return OrangeMultipleLocksGetExpirationsWithUnitArgsContext.class;
	}

	@Override
	protected Long convert(OrangeRedisContext ctx, Long source, TimeUnit sourceTimeUnit) {
		OrangeMultipleLocksGetExpirationsWithUnitArgsContext context = (OrangeMultipleLocksGetExpirationsWithUnitArgsContext) ctx;
		return context.getUnit().convert(source, sourceTimeUnit);
	}
}
