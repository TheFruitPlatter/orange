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
import com.langwuyue.orange.redis.annotation.geo.Height;
import com.langwuyue.orange.redis.annotation.geo.Latitude;
import com.langwuyue.orange.redis.annotation.geo.Longitude;
import com.langwuyue.orange.redis.annotation.geo.SearchArgs;
import com.langwuyue.orange.redis.annotation.geo.Width;
import com.langwuyue.orange.redis.context.OrangeRedisContext;
import com.langwuyue.orange.redis.executor.geo.context.OrangeBoxPointReferenceContext;
import com.langwuyue.orange.redis.mapping.OrangeRedisExecutorIdGenerator;
import com.langwuyue.orange.redis.operations.OrangeRedisGeoOperations;
import com.langwuyue.orange.redis.operations.OrangeRedisGeoOperations.GeoEntry;
import com.langwuyue.orange.redis.operations.OrangeRedisGeoOperations.GeoEntryInRadius;
import com.langwuyue.orange.redis.utils.OrangeCollectionUtils;

/**
 * Executor for retrieving Redis GEO members within a specified bounding box.
 * 
 * <p>This executor handles GEO queries that find members within a rectangular area
 * defined by width and height from a reference point. It supports the following
 * annotations:
 * <ul>
 *   <li>{@link GetMembers} - Marks the method as a GEO query</li>
 *   <li>{@link Width} - Specifies the box width</li>
 *   <li>{@link Height} - Specifies the box height</li>
 *   <li>{@link Longitude} - Specifies the reference point longitude</li>
 *   <li>{@link Latitude} - Specifies the reference point latitude</li>
 *   <li>{@link SearchArgs} - Optional search arguments</li>
 * </ul>
 *
 * @author Liang.Zhong
 * @since 1.0.0
 * @see <a href="https://orange.langwuyue.com/redis/advanced/geo">Orange Redis Geo Documentation</a>
 */
public class OrangeGetMembersInBoxExecutor extends OrangeGetMembersInRadiusExecutor {

	/**
	 * Constructs a new executor for GEO box queries.
	 * 
	 * @param operations the Redis GEO operations implementation
	 * @param idGenerator the executor ID generator
	 */
	public OrangeGetMembersInBoxExecutor(OrangeRedisGeoOperations operations,OrangeRedisExecutorIdGenerator idGenerator) {
		super(operations,idGenerator);
	}

	/**
	 * Gets the list of annotation classes supported by this executor.
	 * 
	 * @return list of supported annotation classes including:
	 *         - GetMembers: marks the method as a geo query
	 *         - Width: specifies the box width
	 *         - Height: specifies the box height  
	 *         - Longitude: specifies reference point longitude
	 *         - Latitude: specifies reference point latitude
	 *         - SearchArgs: optional search arguments
	 */
	@Override
	protected List<Class<? extends Annotation>> getSupportedAnnotationClasses() {
		return OrangeCollectionUtils.asList(GetMembers.class,Width.class,Height.class,Longitude.class,Latitude.class,SearchArgs.class);
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
		OrangeBoxPointReferenceContext context = (OrangeBoxPointReferenceContext) ctx;
		GeoEntry entry = context.getMember();
		return this.getOperations().box(
			context.getRedisKey().getValue(), 
			entry.getLongitude(),
			entry.getLatitude(),
			context.getWidthUnit(), 
			context.getWidth(),
			context.getHeightUnit(),
			context.getHeight(),
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
		return OrangeBoxPointReferenceContext.class;
	}
	
}