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
import java.util.List;

import com.langwuyue.orange.redis.annotation.global.SetExpiration;
import com.langwuyue.orange.redis.context.OrangeRedisContext;
import com.langwuyue.orange.redis.context.OrangeRedisContext.Key;
import com.langwuyue.orange.redis.listener.OrangeRedisSetIfAbsentListener;
import com.langwuyue.orange.redis.mapping.OrangeRedisExecutorIdGenerator;
import com.langwuyue.orange.redis.operations.OrangeRedisHashOperations;

/**
 * @author Liang.Zhong
 * @since 1.0.0
 */
public class OrangeAddMemberIfAbsentWithExpirationExecutor extends OrangeAddMemberIfAbsentExecutor {
	
	public OrangeAddMemberIfAbsentWithExpirationExecutor(
		OrangeRedisHashOperations operations,
		OrangeRedisExecutorIdGenerator idGenerator,
		List<OrangeRedisSetIfAbsentListener> listeners
	) {
		super(operations,idGenerator,listeners);
	}
	
	@Override
	public Object execute(OrangeRedisContext context) throws Exception {
		Key key = context.getRedisKey();
		getOperations().expire(key.getValue(), key.getExpirationTime(), key.getExpirationTimeUnit());
		return super.execute(context);
	}

	@Override
	protected List<Class<? extends Annotation>> getSupportedAnnotationClasses() {
		List<Class<? extends Annotation>> classes = super.getSupportedAnnotationClasses();
		classes.add(SetExpiration.class);
		return classes;
	}

}
