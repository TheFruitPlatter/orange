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

import com.langwuyue.orange.redis.annotation.Count;
import com.langwuyue.orange.redis.annotation.GetMembers;
import com.langwuyue.orange.redis.annotation.zset.PageNo;
import com.langwuyue.orange.redis.annotation.zset.ScoreRange;
import com.langwuyue.orange.redis.annotation.zset.WithScores;
import com.langwuyue.orange.redis.context.OrangeRedisContext;
import com.langwuyue.orange.redis.executor.zset.OrangeGetWithScoresAbstractExecutor;
import com.langwuyue.orange.redis.executor.zset.context.OrangeScoreRangePagNoCountContext;
import com.langwuyue.orange.redis.mapping.OrangeRedisExecutorIdGenerator;
import com.langwuyue.orange.redis.operations.OrangeRedisZSetOperations;
import com.langwuyue.orange.redis.operations.OrangeRedisZSetOperations.Pager;
import com.langwuyue.orange.redis.utils.OrangeCollectionUtils;

/**
 * @author Liang.Zhong
 * @since 1.0.0
 */
public class OrangeGetByScoreRangePageNoCountWithScoresExecutor extends OrangeGetWithScoresAbstractExecutor {
	
	private OrangeRedisZSetOperations operations;

	public OrangeGetByScoreRangePageNoCountWithScoresExecutor(OrangeRedisZSetOperations operations,OrangeRedisExecutorIdGenerator idGenerator) {
		super(idGenerator);
		this.operations = operations;
	}

	@Override
	public Collection doGet(OrangeRedisContext context, Field valueField, Type returnArgumentType) throws Exception {
		OrangeScoreRangePagNoCountContext ctx = (OrangeScoreRangePagNoCountContext)context;
		return this.operations.rangeByScoreWithScores(
				ctx.getRedisKey().getValue(), 
				ctx.getScoreRange(), 
				new Pager(ctx.getPageNo(),ctx.getCount()),
				ctx.getValueType(), 
				valueField == null ? returnArgumentType : valueField.getGenericType()
		);
	}

	@Override
	protected List<Class<? extends Annotation>> getSupportedAnnotationClasses() {
		return OrangeCollectionUtils.asList(GetMembers.class,ScoreRange.class,WithScores.class,PageNo.class,Count.class);
	}

	@Override
	public Class<? extends OrangeRedisContext> getContextClass() {
		return OrangeScoreRangePagNoCountContext.class;
	}
	
	
}
