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
package com.langwuyue.orange.redis.executor.cross.geo.context;

import java.lang.reflect.Method;
import java.util.List;

import com.langwuyue.orange.redis.GeoDistanceUnitEnum;
import com.langwuyue.orange.redis.OrangeRedisException;
import com.langwuyue.orange.redis.RedisValueTypeEnum;
import com.langwuyue.orange.redis.annotation.Count;
import com.langwuyue.orange.redis.annotation.OrangeRedisOperationArg;
import com.langwuyue.orange.redis.annotation.RedisValue;
import com.langwuyue.orange.redis.annotation.geo.Height;
import com.langwuyue.orange.redis.annotation.geo.SearchArgs;
import com.langwuyue.orange.redis.annotation.geo.Width;
import com.langwuyue.orange.redis.context.builder.OrangeMethodAnnotationHandler;
import com.langwuyue.orange.redis.context.builder.OrangeOperationArgAnnotationHandler;
import com.langwuyue.orange.redis.executor.cross.context.OrangeCrossOperationContext;
import com.langwuyue.orange.redis.operations.OrangeRedisGeoOperations.SearchArguments;

/**
 * @author Liang.Zhong
 * @since 1.0.0
 */
public class OrangeBoxContext extends OrangeCrossOperationContext {

	@OrangeRedisOperationArg(binding = Width.class)
	private Object width;
	
	@OrangeRedisOperationArg(binding = Width.class, valueHandler = OrangeOperationArgAnnotationHandler.class)
	private Width widthAnnotation;
	
	@OrangeRedisOperationArg(binding = Height.class)
	private Object height;
	
	@OrangeRedisOperationArg(binding = Height.class, valueHandler = OrangeOperationArgAnnotationHandler.class)
	private Height heightAnnotation;
	
	@OrangeRedisOperationArg(binding = SearchArgs.class, valueHandler = OrangeMethodAnnotationHandler.class)
	private SearchArgs searchArgs;
	
	@OrangeRedisOperationArg(binding = Count.class)
	private Object count;
	
	@OrangeRedisOperationArg(binding = RedisValue.class)
	private Object value;
	
	public OrangeBoxContext(
		Class<?> operationOwner, 
		Method operationMethod, 
		Object[] args, 
		List<String> keys,
		String storeTo, 
		RedisValueTypeEnum valueType
	) {
		super(operationOwner, operationMethod, args, keys, storeTo, valueType);
	}

	public Double getHeight() {
		if(height == null) {
			throw new OrangeRedisException(String.format("The field annotated with @%s cannot be null", Height.class));
		}
		if(!(height instanceof Number) && !(width instanceof String)) {
			throw new OrangeRedisException(String.format("The field annotated with @%s must be a number or a string", Height.class));
		}
		return Double.valueOf(height.toString());
	}
	
	public Double getWidth() {
		if(width == null) {
			throw new OrangeRedisException(String.format("The field annotated with @%s cannot be null", Width.class));
		}
		if(!(width instanceof Number) && !(width instanceof String)) {
			throw new OrangeRedisException(String.format("The field annotated with @%s must be a number or a string", Width.class));
		}
		return Double.valueOf(width.toString());
	}
	
	public GeoDistanceUnitEnum getWidthUnit() {
		return widthAnnotation.unit();
	}
	
	public GeoDistanceUnitEnum getHeightUnit() {
		return heightAnnotation.unit();
	}
	
	public SearchArguments getSearchArguments() {
		SearchArguments arguments = new SearchArguments();
		arguments.setIncludeCoordinates(searchArgs.includeCoordinates());
		arguments.setIncludeDistance(searchArgs.includeDistance());
		arguments.setSortAscending(searchArgs.sortAscending());
		arguments.setCount(getCount());
		arguments.setAny(searchArgs.any());
		return arguments;
	}
	
	public int getCount() {
		if(count != null) {
			return Integer.valueOf(count.toString());
		}
		return searchArgs.count();
	}
	
	public Object getValue() {
		if(value == null) {
			throw new OrangeRedisException(String.format("The argument annotated with @%s cannot be null", RedisValue.class));
		}
		return value;
	}
}
