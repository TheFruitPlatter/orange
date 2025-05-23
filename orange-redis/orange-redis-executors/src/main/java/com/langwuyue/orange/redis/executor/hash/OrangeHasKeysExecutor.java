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
package com.langwuyue.orange.redis.executor.hash;

import java.lang.annotation.Annotation;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;

import com.langwuyue.orange.redis.annotation.ContinueOnFailure;
import com.langwuyue.orange.redis.annotation.Multiple;
import com.langwuyue.orange.redis.annotation.hash.HasKeys;
import com.langwuyue.orange.redis.context.OrangeRedisContext;
import com.langwuyue.orange.redis.executor.OrangeRedisAbstractExecutor;
import com.langwuyue.orange.redis.executor.hash.context.OrangeHashKeysContext;
import com.langwuyue.orange.redis.logger.OrangeRedisLogger;
import com.langwuyue.orange.redis.mapping.OrangeRedisExecutorIdGenerator;
import com.langwuyue.orange.redis.operations.OrangeRedisHashOperations;
import com.langwuyue.orange.redis.utils.OrangeCollectionUtils;
import com.langwuyue.orange.redis.utils.OrangeReflectionUtils;

/**
 * @author Liang.Zhong
 * @since 1.0.0
 */
public class OrangeHasKeysExecutor extends OrangeRedisAbstractExecutor {
	
	private OrangeRedisHashOperations operations;
	
	private OrangeRedisLogger logger;

	public OrangeHasKeysExecutor(OrangeRedisHashOperations operations,OrangeRedisExecutorIdGenerator idGenerator,OrangeRedisLogger logger) {
		super(idGenerator);
		this.operations = operations;
		this.logger = logger;
	}

	@Override
	protected List<Class<? extends Annotation>> getSupportedAnnotationClasses() {
		return OrangeCollectionUtils.asList(HasKeys.class,Multiple.class,ContinueOnFailure.class);
	}

	@Override
	public Class<? extends OrangeRedisContext> getContextClass() {
		return OrangeHashKeysContext.class;
	}

	@Override
	public Object execute(OrangeRedisContext context) throws Exception {
		OrangeHashKeysContext ctx = (OrangeHashKeysContext) context;
		List keys = ctx.getHashKeys();
		Class returnClass = ctx.getOperationMethod().getReturnType();
		if(Map.class.isAssignableFrom(returnClass)) {
			Map map = OrangeReflectionUtils.newMap(returnClass);
			doExecute(ctx,map::put);
			return map;
		}
		Object instance = OrangeReflectionUtils.getArrayOrCollectionInstance(returnClass, keys.size());
		if(instance instanceof Collection) {
			Collection collection = (Collection) instance;
			doExecute(ctx,(k,r) -> collection.add(r));
		}else{
			List<Boolean> results = new ArrayList<>();
			doExecute(ctx,(k,r) -> results.add(r));
			for(int i = 0; i < results.size(); i++) {
				Array.set(instance, i, results.get(i));
			}
		}
		return instance;
	}
	
	private void doExecute(OrangeHashKeysContext ctx,BiConsumer<Object,Boolean> consumer) {
		List keys = ctx.getHashKeys();
		boolean alreayBreak = false;
		for(Object key : keys) {
			if(alreayBreak) {
				consumer.accept(key,null);
				continue;
			}
			try {
				Boolean has = this.operations.hasKey(
					ctx.getRedisKey().getValue(),
					key,
					ctx.getKeyType()
				);
				consumer.accept(key,has);
			}catch (Exception e) {
				consumer.accept(key,null);
				if(!ctx.continueOnFailure()) {
					this.logger.error(String.format("An exception occurred during hasKey operation executing.Method:%s", ctx.getOperationMethod()),e);
					alreayBreak = true;
				}else{
					this.logger.warn(String.format("An exception occurred during hasKey operation executing.Method:%s", ctx.getOperationMethod()),e);
				}
				
			}
		}
	}
}
