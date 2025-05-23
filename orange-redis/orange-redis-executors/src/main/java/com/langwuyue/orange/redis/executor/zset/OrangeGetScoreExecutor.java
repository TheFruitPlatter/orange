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
package com.langwuyue.orange.redis.executor.zset;

import java.lang.annotation.Annotation;
import java.math.BigDecimal;
import java.util.List;

import com.langwuyue.orange.redis.annotation.RedisValue;
import com.langwuyue.orange.redis.annotation.zset.GetScores;
import com.langwuyue.orange.redis.context.OrangeRedisContext;
import com.langwuyue.orange.redis.context.OrangeRedisValueContext;
import com.langwuyue.orange.redis.executor.OrangeRedisAbstractExecutor;
import com.langwuyue.orange.redis.mapping.OrangeRedisExecutorIdGenerator;
import com.langwuyue.orange.redis.operations.OrangeRedisZSetOperations;
import com.langwuyue.orange.redis.utils.OrangeCollectionUtils;
/**
 * @author Liang.Zhong
 * @since 1.0.0
 */
public class OrangeGetScoreExecutor extends OrangeRedisAbstractExecutor {

	private OrangeRedisZSetOperations operations;

	public OrangeGetScoreExecutor(OrangeRedisZSetOperations operations,OrangeRedisExecutorIdGenerator idGenerator) {
		super(idGenerator);
		this.operations = operations;
	}

	@Override
	public Object execute(OrangeRedisContext context) throws Exception {
		OrangeRedisValueContext ctx = (OrangeRedisValueContext) context;
		List<Double> results = this.operations.score(context.getRedisKey().getValue(), ctx.getValueType(), ctx.getValue());
		if(results == null || results.isEmpty()) {
			return null;
		}
		Double result = results.get(0);
		Class returnClass = context.getOperationMethod().getReturnType();
		if(returnClass == Long.class || returnClass == long.class) {
			return result.longValue();
		}
		if(returnClass == Integer.class || returnClass == int.class) {
			return result.intValue();
		}
		if(returnClass == Float.class || returnClass == float.class) {
			return result.floatValue();
		}
		if(returnClass == BigDecimal.class) {
			return new BigDecimal(result.toString());
		}
		return result;
	}

	@Override
	protected List<Class<? extends Annotation>> getSupportedAnnotationClasses() {
		return OrangeCollectionUtils.asList(GetScores.class,RedisValue.class);
	}
	
	@Override
	public Class<? extends OrangeRedisContext> getContextClass() {
		return OrangeRedisValueContext.class;
	}

}
