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
package com.langwuyue.orange.redis.executor.set.context;

import java.lang.reflect.Method;

import com.langwuyue.orange.redis.OrangeRedisException;
import com.langwuyue.orange.redis.RedisValueTypeEnum;
import com.langwuyue.orange.redis.annotation.OrangeRedisOperationArg;
import com.langwuyue.orange.redis.annotation.ScanPattern;
import com.langwuyue.orange.redis.annotation.zset.PageNo;
import com.langwuyue.orange.redis.context.OrangeRedisCountContext;
import com.langwuyue.orange.redis.utils.OrangeReflectionUtils;

/**
 * @author Liang.Zhong
 * @since 1.0.0
 */
public class OrangeScanContext extends OrangeRedisCountContext {

	@OrangeRedisOperationArg(binding = ScanPattern.class)
	private Object pattern;
	
	@OrangeRedisOperationArg(binding = PageNo.class)
	private Object pageNo;
	
	public OrangeScanContext(
		Class<?> operationOwner, 
		Method operationMethod, 
		Object[] args,
		Key redisKey,
		RedisValueTypeEnum valueType
	) {
		super(operationOwner, operationMethod, args, redisKey,valueType);
	}
	

	public String getPattern() {
		if(pattern == null) {
			throw new OrangeRedisException(String.format("The argument annotated with @%s cannot be null", ScanPattern.class));
		}
		return pattern.toString();
	}
	
	public Long getPageNo() {
		if(this.pageNo == null) {
			throw new OrangeRedisException(String.format("The argument annotated with @%s cannot be null", PageNo.class));
		}
		if(!(OrangeReflectionUtils.isInteger(this.pageNo.getClass())) && !(this.pageNo instanceof String)) {
			throw new OrangeRedisException(String.format("The argument annotated with @%s must be a integer or a string", PageNo.class));
		}
		Long value = Long.valueOf(this.pageNo.toString());
		if(value < 0) {
			throw new OrangeRedisException(String.format("The argument annotated with @%s must be greater than zero", PageNo.class));
		}
		return value;
	}

}
