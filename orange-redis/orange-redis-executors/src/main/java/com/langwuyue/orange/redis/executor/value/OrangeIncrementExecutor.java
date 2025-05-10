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
package com.langwuyue.orange.redis.executor.value;

import java.lang.annotation.Annotation;
import java.util.List;

import com.langwuyue.orange.redis.OrangeRedisException;
import com.langwuyue.orange.redis.annotation.Increment;
import com.langwuyue.orange.redis.annotation.RedisValue;
import com.langwuyue.orange.redis.context.OrangeRedisContext;
import com.langwuyue.orange.redis.context.OrangeRedisValueContext;
import com.langwuyue.orange.redis.executor.OrangeRedisAbstractExecutor;
import com.langwuyue.orange.redis.mapping.OrangeRedisExecutorIdGenerator;
import com.langwuyue.orange.redis.operations.OrangeRedisValueOperations;
import com.langwuyue.orange.redis.utils.OrangeCollectionUtils;
import com.langwuyue.orange.redis.utils.OrangeReflectionUtils;

/**
 * @author Liang.Zhong
 * @since 1.0.0
 */
public class OrangeIncrementExecutor extends OrangeRedisAbstractExecutor {
	
	private OrangeRedisValueOperations operations;
	
	public OrangeIncrementExecutor(OrangeRedisValueOperations operations,OrangeRedisExecutorIdGenerator idGenerator) {
		super(idGenerator);
		this.operations = operations;
	}



	@Override
	public List<Class<? extends Annotation>> getSupportedAnnotationClasses() {
		return OrangeCollectionUtils.asList(Increment.class,RedisValue.class);
	}

	@Override
	public Object execute(OrangeRedisContext context) throws Exception {
		OrangeRedisValueContext ctx = (OrangeRedisValueContext)context;
		Object value = ctx.getValue();
		if(OrangeReflectionUtils.isFloat(value.getClass())) {
			return operations.increment(ctx.getRedisKey().getValue(),Double.valueOf(value.toString()));
		}else if(OrangeReflectionUtils.isInteger(value.getClass())) {
			return operations.increment(ctx.getRedisKey().getValue(),Long.valueOf(value.toString()));	
		}
		throw new OrangeRedisException("The value type of the increment operation must be either a double or a long");
	}



	@Override
	public Class<? extends OrangeRedisContext> getContextClass() {
		return OrangeRedisValueContext.class;
	}
}
