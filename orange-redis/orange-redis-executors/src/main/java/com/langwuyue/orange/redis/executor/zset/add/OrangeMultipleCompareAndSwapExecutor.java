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
package com.langwuyue.orange.redis.executor.zset.add;

import java.lang.annotation.Annotation;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.langwuyue.orange.redis.annotation.AddMembers;
import com.langwuyue.orange.redis.annotation.CAS;
import com.langwuyue.orange.redis.annotation.ContinueOnFailure;
import com.langwuyue.orange.redis.annotation.Multiple;
import com.langwuyue.orange.redis.context.OrangeRedisContext;
import com.langwuyue.orange.redis.executor.zset.context.OrangeMultipleCompareAndSwapContext;
import com.langwuyue.orange.redis.logger.OrangeRedisLogger;
import com.langwuyue.orange.redis.mapping.OrangeRedisExecutorIdGenerator;
import com.langwuyue.orange.redis.operations.OrangeRedisScriptOperations;
import com.langwuyue.orange.redis.operations.OrangeRedisZSetOperations.CASZSetEntry;
import com.langwuyue.orange.redis.operations.OrangeRedisZSetOperations.ZSetEntry;
import com.langwuyue.orange.redis.utils.OrangeCollectionUtils;

/**
 * @author Liang.Zhong
 * @since 1.0.0
 */
public class OrangeMultipleCompareAndSwapExecutor extends OrangeCompareAndSwapExecutor {
	
	public OrangeMultipleCompareAndSwapExecutor(OrangeRedisScriptOperations operations,OrangeRedisExecutorIdGenerator idGenerator,OrangeRedisLogger logger) {
		super(operations,idGenerator,logger);
	}

	@Override
	public Object execute(OrangeRedisContext context) throws Exception {
		OrangeMultipleCompareAndSwapContext ctx = (OrangeMultipleCompareAndSwapContext)context;
		Set<ZSetEntry> entries = ctx.getMembers();
		boolean continueOnFailure = ctx.continueOnFailure();
		Map<Object,Boolean> result = new LinkedHashMap<>();
		for(ZSetEntry entry : entries) {
			CASZSetEntry casEntry = (CASZSetEntry)entry;
			try {
				boolean success = casExecute(
					ctx.getRedisKey().getValue(), 
					casEntry.getValue(), 
					ctx.getValueType(), 
					casEntry.getOldScore(), 
					casEntry.getScore()
				);
				result.put(entry.getValue(), success);	
				if(!success && !continueOnFailure) {
					break;
				}
			}catch (Exception e) {
				// The operation may have been interrupted by a client timeout or network error, 
				// but it was actually completed successfully.
				if(!continueOnFailure) {
					break;
				}
			}
			
		}
		return result;
	}

	@Override
	protected List<Class<? extends Annotation>> getSupportedAnnotationClasses() {
		return OrangeCollectionUtils.asList(Multiple.class,CAS.class,ContinueOnFailure.class);
	}

	@Override
	public Class<? extends OrangeRedisContext> getContextClass() {
		return OrangeMultipleCompareAndSwapContext.class;
	}
}
