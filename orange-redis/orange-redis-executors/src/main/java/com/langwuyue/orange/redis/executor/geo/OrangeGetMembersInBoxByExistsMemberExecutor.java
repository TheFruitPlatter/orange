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
package com.langwuyue.orange.redis.executor.geo;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.List;

import com.langwuyue.orange.redis.annotation.GetMembers;
import com.langwuyue.orange.redis.annotation.RedisValue;
import com.langwuyue.orange.redis.annotation.geo.Height;
import com.langwuyue.orange.redis.annotation.geo.SearchArgs;
import com.langwuyue.orange.redis.annotation.geo.Width;
import com.langwuyue.orange.redis.context.OrangeRedisContext;
import com.langwuyue.orange.redis.executor.geo.context.OrangeBoxContext;
import com.langwuyue.orange.redis.mapping.OrangeRedisExecutorIdGenerator;
import com.langwuyue.orange.redis.operations.OrangeRedisGeoOperations;
import com.langwuyue.orange.redis.operations.OrangeRedisGeoOperations.GeoEntryInRadius;
import com.langwuyue.orange.redis.utils.OrangeCollectionUtils;

/**
 * Executor for retrieving members within a bounding box by existing member.
 * <p>
 * This executor performs GEO queries using Redis' GEOSEARCH BOX command to find members
 * within a rectangular bounding box centered around an existing member.
 * </p>
 *
 * @author Liang.Zhong
 * @since 1.0.0
 * @see <a href="https://orange.langwuyue.com/redis/advanced/geo">Orange Redis Geo Documentation</a>
 */
public class OrangeGetMembersInBoxByExistsMemberExecutor extends OrangeGetMembersInRadiusExecutor {

	/**
	 * Constructs a new executor for retrieving members within a bounding box.
	 *
	 * @param operations the Redis GEO operations implementation
	 * @param idGenerator the executor ID generator
	 */
	public OrangeGetMembersInBoxByExistsMemberExecutor(OrangeRedisGeoOperations operations,OrangeRedisExecutorIdGenerator idGenerator) {
		super(operations,idGenerator);
	}

	/**
	 * Gets the list of annotation classes supported by this executor.
	 * 
	 * @return list of supported annotation classes including:
	 *         - GetMembers: marks the method as a geo query
	 *         - Width: specifies the box width
	 *         - Height: specifies the box height
	 *         - RedisValue: specifies the member value
	 *         - SearchArgs: specifies search arguments
	 */
	@Override
	protected List<Class<? extends Annotation>> getSupportedAnnotationClasses() {
		return OrangeCollectionUtils.asList(GetMembers.class,Width.class,Height.class,RedisValue.class,SearchArgs.class);
	}

	/**
	 * Executes the GEO query to retrieve members within the specified bounding box.
	 * 
	 * @param ctx the execution context containing box parameters
	 * @param valueField the field annotated with RedisValue
	 * @return list of GeoEntryInRadius objects containing the members found
	 * @throws Exception if the query fails or parameters are invalid
	 */
	@Override
	protected List<GeoEntryInRadius> doGet(OrangeRedisContext ctx, Field valueField) throws Exception {
		OrangeBoxContext context = (OrangeBoxContext) ctx;
		return this.getOperations().box(
			context.getRedisKey().getValue(), 
			context.getValue(), 
			context.getWidth(), 
			context.getWidthUnit(),
			context.getHeight(),
			context.getHeightUnit(),
			context.getValueType(),
			valueField.getGenericType(),
			context.getSearchArguments()
		);
	}

	/**
	 * Gets the context class used by this executor.
	 * 
	 * @return the OrangeBoxContext class that contains box parameters
	 */
	@Override
	public Class<? extends OrangeRedisContext> getContextClass() {
		return OrangeBoxContext.class;
	}
}