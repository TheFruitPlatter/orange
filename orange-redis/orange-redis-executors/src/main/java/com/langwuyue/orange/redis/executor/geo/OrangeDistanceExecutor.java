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

import com.langwuyue.orange.redis.OrangeRedisException;
import com.langwuyue.orange.redis.annotation.Multiple;
import com.langwuyue.orange.redis.annotation.geo.Distance;
import com.langwuyue.orange.redis.context.OrangeRedisContext;
import com.langwuyue.orange.redis.executor.OrangeRedisAbstractExecutor;
import com.langwuyue.orange.redis.executor.geo.context.OrangeRedisComputeDistanceContext;
import com.langwuyue.orange.redis.mapping.OrangeRedisExecutorIdGenerator;
import com.langwuyue.orange.redis.operations.OrangeRedisGeoOperations;
import com.langwuyue.orange.redis.utils.OrangeCollectionUtils;

/**
 * Executor implementation for calculating the distance between two geo points in Redis.
 * This executor handles the {@link Distance} annotation and computes the distance
 * between two members in a geo set.
 *
 * @author Liang.Zhong
 * @since 1.0.0
 * @see <a href="https://orange.langwuyue.com/redis/advanced/geo">Orange Redis Geo Documentation</a>
 */
public class OrangeDistanceExecutor extends OrangeRedisAbstractExecutor {

	private OrangeRedisGeoOperations operations;

	/**
	 * Constructs a new OrangeDistanceExecutor with the specified operations and ID generator.
	 *
	 * @param operations the Redis geo operations to be used for distance calculations
	 * @param idGenerator the generator for creating executor IDs
	 */
	public OrangeDistanceExecutor(OrangeRedisGeoOperations operations,OrangeRedisExecutorIdGenerator idGenerator) {
		super(idGenerator);
		this.operations = operations;
	}

	/**
	 * Executes the distance calculation operation between two geo points.
	 * This method retrieves the distance between two members in a Redis geo set
	 * based on the provided context.
	 *
	 * @param context the context containing the key and members for distance calculation
	 * @return Double value representing the distance between the two members
	 * @throws Exception if an error occurs during the operation
	 */
	@Override
	public Object execute(OrangeRedisContext context) throws Exception {
		OrangeRedisComputeDistanceContext ctx = (OrangeRedisComputeDistanceContext) context;
		Object[] locations = ctx.toArray();
		if(locations == null || locations.length != 2) {
			throw new OrangeRedisException(String.format("Distance calculation requires two locations, but found %s", locations == null ? 0 : locations.length));
		}
		return this.operations.distance(ctx.getRedisKey().getValue(), locations[0], locations[1], ctx.getUnit(), ctx.getValueType());
	}

	/**
	 * Returns the list of annotation classes supported by this executor.
	 * This executor supports the {@link Distance} and {@link Multiple} annotations.
	 *
	 * @return a list containing the supported annotation classes
	 */
	@Override
	protected List<Class<? extends Annotation>> getSupportedAnnotationClasses() {
		return OrangeCollectionUtils.asList(Distance.class,Multiple.class);
	}

	/**
	 * Returns the context class required by this executor.
	 * This executor requires {@link OrangeRedisComputeDistanceContext} for its operations.
	 *
	 * @return the OrangeRedisComputeDistanceContext class
	 */
	@Override
	public Class<? extends OrangeRedisContext> getContextClass() {
		return OrangeRedisComputeDistanceContext.class;
	}

	
}