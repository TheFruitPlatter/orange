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
import com.langwuyue.orange.redis.annotation.geo.Distance;
import com.langwuyue.orange.redis.annotation.geo.SearchArgs;
import com.langwuyue.orange.redis.context.OrangeRedisContext;
import com.langwuyue.orange.redis.executor.OrangeRedisAbstractExecutor;
import com.langwuyue.orange.redis.executor.cross.geo.context.OrangeRadiusContext;
import com.langwuyue.orange.redis.mapping.OrangeRedisExecutorIdGenerator;
import com.langwuyue.orange.redis.operations.OrangeRedisGeoOperations;
import com.langwuyue.orange.redis.utils.OrangeCollectionUtils;

/**
 * Executor for searching geo points within radius and storing results,
 * with additional member existence check.
 * 
 * @author Liang.Zhong
 * @since 1.0.0
 */
public class OrangeSearchInRadiusAndStoreByExistsMemberExecutor extends OrangeRedisAbstractExecutor {
	
	private OrangeRedisGeoOperations operations;

	/**
	 * Constructs a new executor for geo radius search and store operations.
	 *
	 * @param operations Geo operations implementation
	 * @param idGenerator Executor ID generator
	 */
	public OrangeSearchInRadiusAndStoreByExistsMemberExecutor(OrangeRedisGeoOperations operations,OrangeRedisExecutorIdGenerator idGenerator) {
		super(idGenerator);
		this.operations = operations;
	}

	/**
	 * Execute Redis geo radius search and store operation with member existence check.
	 * <p>
	 * Searches for geo points within specified radius using context parameters,
	 * stores results to target key, and checks if specified member exists within radius.
	 *
	 * @param context Context containing search parameters
	 * @return Result of store operation
	 * @throws Exception if execution fails
	 */
	@Override
	public Object execute(OrangeRedisContext context) throws Exception {
		OrangeRadiusContext ctx = (OrangeRadiusContext) context;
		return operations.searchRadiusAndStore(
			ctx.getReferenceKey(),
			ctx.getStoreTo(),
			ctx.getValue(),
			ctx.getDistance(),
			ctx.getUnit(),
			ctx.getValueType(),
			ctx.getRadiusArguments()
		);
	}

	/**
	 * Get the list of annotation classes supported by this executor.
	 *
	 * @return List of supported annotation classes
	 */
	@Override
	protected List<Class<? extends Annotation>> getSupportedAnnotationClasses() {
		return OrangeCollectionUtils.asList(
			GetMembers.class,
			SearchArgs.class,
			StoreTo.class,
			CrossOperationKeys.class,
			RedisValue.class,
			Distance.class
		);
	}

	/**
	 * Get the required context class type for this executor.
	 * <p>
	 * Returns the specific context class for geo radius search operations with member existence check.
	 *
	 * @return Context class type
	 */
	@Override
	public Class<? extends OrangeRedisContext> getContextClass() {
		return OrangeRadiusContext.class;
	}

	
}