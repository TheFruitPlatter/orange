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
package com.langwuyue.orange.redis.executor.cross.list.context;

import java.lang.reflect.Method;
import java.util.List;

import com.langwuyue.orange.redis.RedisValueTypeEnum;
import com.langwuyue.orange.redis.annotation.OrangeRedisOperationArg;
import com.langwuyue.orange.redis.annotation.Timeout;
import com.langwuyue.orange.redis.context.builder.OrangeMethodAnnotationHandler;

/**
 * @author Liang.Zhong
 * @since 1.0.0
 */
public class OrangeMoveTimeoutContext extends OrangeMoveContext {
	
	@OrangeRedisOperationArg(valueHandler = OrangeMethodAnnotationHandler.class,binding = Timeout.class)
	private Timeout timeout;

	public OrangeMoveTimeoutContext(
		Class<?> operationOwner, 
		Method operationMethod, 
		Object[] args, 
		List<String> keys,
		String storeTo, 
		RedisValueTypeEnum valueType
	) {
		super(operationOwner, operationMethod, args, keys, storeTo, valueType);
	}
	
	public Timeout getTimeout() {
		return timeout;
	}
	
}
