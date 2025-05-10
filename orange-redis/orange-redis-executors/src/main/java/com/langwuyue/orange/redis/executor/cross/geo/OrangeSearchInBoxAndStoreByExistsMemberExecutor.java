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
package com.langwuyue.orange.redis.executor.cross.geo;

import java.lang.annotation.Annotation;
import java.util.List;

import com.langwuyue.orange.redis.annotation.GetMembers;
import com.langwuyue.orange.redis.annotation.RedisValue;
import com.langwuyue.orange.redis.annotation.cross.CrossOperationKeys;
import com.langwuyue.orange.redis.annotation.cross.StoreTo;
import com.langwuyue.orange.redis.annotation.geo.Height;
import com.langwuyue.orange.redis.annotation.geo.SearchArgs;
import com.langwuyue.orange.redis.annotation.geo.Width;
import com.langwuyue.orange.redis.context.OrangeRedisContext;
import com.langwuyue.orange.redis.executor.OrangeRedisAbstractExecutor;
import com.langwuyue.orange.redis.executor.cross.geo.context.OrangeBoxContext;
import com.langwuyue.orange.redis.mapping.OrangeRedisExecutorIdGenerator;
import com.langwuyue.orange.redis.operations.OrangeRedisGeoOperations;
import com.langwuyue.orange.redis.utils.OrangeCollectionUtils;

/**
 * @author Liang.Zhong
 * @since 1.0.0
 */
public class OrangeSearchInBoxAndStoreByExistsMemberExecutor extends OrangeRedisAbstractExecutor {
	
	private OrangeRedisGeoOperations operations;

	public OrangeSearchInBoxAndStoreByExistsMemberExecutor(OrangeRedisGeoOperations operations,OrangeRedisExecutorIdGenerator idGenerator) {
		super(idGenerator);
		this.operations = operations;
	}

	@Override
	public Object execute(OrangeRedisContext context) throws Exception {
		OrangeBoxContext ctx = (OrangeBoxContext) context;
		return operations.searchBoxAndStore(
			ctx.getReferenceKey(),
			ctx.getStoreTo(),
			ctx.getValue(),
			ctx.getWidth(),
			ctx.getWidthUnit(),
			ctx.getHeight(),
			ctx.getHeightUnit(),
			ctx.getValueType(),
			ctx.getSearchArguments()
		);
	}

	@Override
	protected List<Class<? extends Annotation>> getSupportedAnnotationClasses() {
		return OrangeCollectionUtils.asList(
			GetMembers.class,
			SearchArgs.class,
			StoreTo.class,
			CrossOperationKeys.class,
			RedisValue.class,
			Width.class,
			Height.class
		);
	}

	@Override
	public Class<? extends OrangeRedisContext> getContextClass() {
		return OrangeBoxContext.class;
	}

	
}
