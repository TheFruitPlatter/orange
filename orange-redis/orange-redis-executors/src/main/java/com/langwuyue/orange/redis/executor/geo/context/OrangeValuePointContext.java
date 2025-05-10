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

import com.langwuyue.orange.redis.OrangeRedisException;
import com.langwuyue.orange.redis.RedisValueTypeEnum;
import com.langwuyue.orange.redis.annotation.OrangeRedisOperationArg;
import com.langwuyue.orange.redis.annotation.geo.Latitude;
import com.langwuyue.orange.redis.annotation.geo.Longitude;
import com.langwuyue.orange.redis.context.OrangeRedisValueContext;
import com.langwuyue.orange.redis.operations.OrangeRedisGeoOperations.GeoEntry;

/**
 * @author Liang.Zhong
 * @since 1.0.0
 */
public class OrangeValuePointContext extends OrangeRedisValueContext {

	@OrangeRedisOperationArg(binding = Longitude.class)
	private Object longitude;
	
	@OrangeRedisOperationArg(binding = Latitude.class)
	private Object latitude;

	public OrangeValuePointContext(
		Class<?> operationOwner, 
		Method operationMethod, 
		Object[] args,
		Key redisKey,
		RedisValueTypeEnum valueType
	) {
		super(operationOwner, operationMethod, args, redisKey,valueType);
	}

	public GeoEntry getMember() {
		Object value = getValue();
		if(latitude == null) {
			throw new OrangeRedisException(String.format("The field annotated with @%s cannot be null", Latitude.class));
		}
		if(!(latitude instanceof Number) && !(latitude instanceof String)) {
			throw new OrangeRedisException(String.format("The field annotated with @%s must be a number or a string", Latitude.class));
		}
		if(longitude == null) {
			throw new OrangeRedisException(String.format("The field annotated with @%s cannot be null", Longitude.class));
		}
		if(!(longitude instanceof Number) && !(longitude instanceof String)) {
			throw new OrangeRedisException(String.format("The field annotated with @%s must be a number or a string", Longitude.class));
		}
		return new GeoEntry(value,Double.valueOf(longitude.toString()),Double.valueOf(latitude.toString()));
	}
}
