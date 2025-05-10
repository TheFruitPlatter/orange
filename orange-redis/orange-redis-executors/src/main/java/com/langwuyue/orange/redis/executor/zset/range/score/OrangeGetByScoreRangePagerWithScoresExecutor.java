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
package com.langwuyue.orange.redis.executor.zset.range.score;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.List;

import com.langwuyue.orange.redis.annotation.GetMembers;
import com.langwuyue.orange.redis.annotation.zset.Pager;
import com.langwuyue.orange.redis.annotation.zset.ScoreRange;
import com.langwuyue.orange.redis.annotation.zset.WithScores;
import com.langwuyue.orange.redis.context.OrangeRedisContext;
import com.langwuyue.orange.redis.executor.zset.OrangeGetWithScoresAbstractExecutor;
import com.langwuyue.orange.redis.executor.zset.context.OrangeScoreRangePagerContext;
import com.langwuyue.orange.redis.mapping.OrangeRedisExecutorIdGenerator;
import com.langwuyue.orange.redis.operations.OrangeRedisZSetOperations;
import com.langwuyue.orange.redis.utils.OrangeCollectionUtils;

/**
 * @author Liang.Zhong
 * @since 1.0.0
 */
public class OrangeGetByScoreRangePagerWithScoresExecutor extends OrangeGetWithScoresAbstractExecutor {
	
	private OrangeRedisZSetOperations operations;

	public OrangeGetByScoreRangePagerWithScoresExecutor(OrangeRedisZSetOperations operations,OrangeRedisExecutorIdGenerator idGenerator) {
		super(idGenerator);
		this.operations = operations;
	}

	@Override
	public Collection doGet(OrangeRedisContext context, Field valueField, Type returnArgumentType) throws Exception {
		OrangeScoreRangePagerContext ctx = (OrangeScoreRangePagerContext)context;
		return this.operations.rangeByScoreWithScores(
				ctx.getRedisKey().getValue(), 
				ctx.getScoreRange(), 
				ctx.getPager(),
				ctx.getValueType(), 
				valueField == null ? returnArgumentType : valueField.getGenericType()
		);
	}

	@Override
	protected List<Class<? extends Annotation>> getSupportedAnnotationClasses() {
		return OrangeCollectionUtils.asList(GetMembers.class,ScoreRange.class,WithScores.class,Pager.class);
	}

	@Override
	public Class<? extends OrangeRedisContext> getContextClass() {
		return OrangeScoreRangePagerContext.class;
	}
	
	
}
