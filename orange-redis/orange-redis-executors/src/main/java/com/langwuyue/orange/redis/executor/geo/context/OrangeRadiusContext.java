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
package com.langwuyue.orange.redis.executor.geo.context;

import java.lang.reflect.Method;

import com.langwuyue.orange.redis.GeoDistanceUnitEnum;
import com.langwuyue.orange.redis.OrangeRedisException;
import com.langwuyue.orange.redis.RedisValueTypeEnum;
import com.langwuyue.orange.redis.annotation.Count;
import com.langwuyue.orange.redis.annotation.OrangeRedisOperationArg;
import com.langwuyue.orange.redis.annotation.geo.Distance;
import com.langwuyue.orange.redis.annotation.geo.SearchArgs;
import com.langwuyue.orange.redis.context.OrangeRedisValueContext;
import com.langwuyue.orange.redis.context.builder.OrangeMethodAnnotationHandler;
import com.langwuyue.orange.redis.context.builder.OrangeOperationArgAnnotationHandler;
import com.langwuyue.orange.redis.operations.OrangeRedisGeoOperations.SearchArguments;

/**
 * @author Liang.Zhong
 * @since 1.0.0
 */
public class OrangeRadiusContext extends OrangeRedisValueContext {

	@OrangeRedisOperationArg(binding = Distance.class)
	private Object value;
	
	@OrangeRedisOperationArg(binding = Distance.class, valueHandler = OrangeOperationArgAnnotationHandler.class)
	private Distance distance;
	
	@OrangeRedisOperationArg(binding = SearchArgs.class, valueHandler = OrangeMethodAnnotationHandler.class)
	private SearchArgs radiusArg;
	
	@OrangeRedisOperationArg(binding = Count.class)
	private Object count;
	
	public OrangeRadiusContext(
		Class<?> operationOwner, 
		Method operationMethod, 
		Object[] args,
		Key redisKey,
		RedisValueTypeEnum valueType
	) {
		super(operationOwner, operationMethod, args, redisKey,valueType);
	}

	public Double getDistance() {
		if(value == null) {
			throw new OrangeRedisException(String.format("The field annotated with @%s cannot be null", Distance.class));
		}
		if(!(value instanceof Number) && !(value instanceof String)) {
			throw new OrangeRedisException(String.format("The field annotated with @%s must be a number or a string", Distance.class));
		}
		return Double.valueOf(value.toString());
	}
	
	public GeoDistanceUnitEnum getUnit() {
		return distance.unit();
	}
	
	public SearchArguments getRadiusArguments() {
		SearchArguments arguments = new SearchArguments();
		arguments.setIncludeCoordinates(radiusArg.includeCoordinates());
		arguments.setIncludeDistance(radiusArg.includeDistance());
		arguments.setSortAscending(radiusArg.sortAscending());
		arguments.setCount(getCount());
		arguments.setAny(radiusArg.any());
		return arguments;
	}
	
	public int getCount() {
		if(count != null) {
			return Integer.valueOf(count.toString());
		}
		return radiusArg.count();
	}
}
