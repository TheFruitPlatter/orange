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
import java.util.List;

import com.langwuyue.orange.redis.annotation.AddMembers;
import com.langwuyue.orange.redis.annotation.RedisValue;
import com.langwuyue.orange.redis.annotation.geo.Latitude;
import com.langwuyue.orange.redis.annotation.geo.Longitude;
import com.langwuyue.orange.redis.context.OrangeRedisContext;
import com.langwuyue.orange.redis.executor.OrangeRedisAbstractExecutor;
import com.langwuyue.orange.redis.executor.geo.context.OrangeValuePointContext;
import com.langwuyue.orange.redis.mapping.OrangeRedisExecutorIdGenerator;
import com.langwuyue.orange.redis.operations.OrangeRedisGeoOperations;
import com.langwuyue.orange.redis.utils.OrangeCollectionUtils;

/**
 * Executor for adding a geo member with coordinates to a Redis geo set.
 * This executor handles the addition of a single geo point with longitude and latitude
 * to a Redis geo set using the AddMembers, RedisValue, Longitude, and Latitude annotations.
 *
 * @author Liang.Zhong
 * @since 1.0.0
 * @see <a href="https://orange.langwuyue.com/redis/advanced/geo">Orange Redis Geo Documentation</a>
 */
public class OrangeAddMemberExecutor extends OrangeRedisAbstractExecutor {
	
	private OrangeRedisGeoOperations operations;

	/**
	 * Constructs a new OrangeAddMemberExecutor.
	 *
	 * @param operations the Redis geo operations instance to perform geo-related operations
	 * @param idGenerator the executor ID generator for generating unique executor identifiers
	 */
	public OrangeAddMemberExecutor(OrangeRedisGeoOperations operations,OrangeRedisExecutorIdGenerator idGenerator) {
		super(idGenerator);
		this.operations = operations;
	}

	/**
	 * Executes the geo add operation for a member with its coordinates.
	 * Adds a geo point to the specified Redis geo set using the provided key, member value,
	 * longitude, and latitude.
	 *
	 * @param context the Redis operation context containing the key, member value, and coordinates
	 * @return Long value indicating the number of elements added to the geo set (0 or 1)
	 * @throws Exception if an error occurs during the operation
	 */
	@Override
	public Object execute(OrangeRedisContext context) throws Exception {
		OrangeValuePointContext ctx = (OrangeValuePointContext) context;
		Long result = this.operations.add(context.getRedisKey().getValue(), OrangeCollectionUtils.asList(ctx.getMember()), ctx.getValueType());
		Class<?> returnClass = context.getOperationMethod().getReturnType();
		if(returnClass == Boolean.class || returnClass == boolean.class) {
			return result != null && result > 0;	
		}
		return result;
	}

	/**
	 * Returns the list of annotation classes that this executor supports.
	 *
	 * @return a list containing the AddMembers, RedisValue, Longitude, and Latitude annotation classes
	 */
	@Override
	protected List<Class<? extends Annotation>> getSupportedAnnotationClasses() {
		return OrangeCollectionUtils.asList(AddMembers.class,RedisValue.class,Longitude.class,Latitude.class);
	}

	/**
	 * Returns the context class that this executor uses.
	 *
	 * @return the OrangeAddMemberContext class for handling geo member addition operations
	 */
	@Override
	public Class<? extends OrangeRedisContext> getContextClass() {
		return OrangeValuePointContext.class;
	}
}