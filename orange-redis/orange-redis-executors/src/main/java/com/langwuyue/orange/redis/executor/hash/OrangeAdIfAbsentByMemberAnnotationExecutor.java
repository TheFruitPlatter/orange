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
import java.util.Collection;
import java.util.List;

import com.langwuyue.orange.redis.annotation.AddMembers;
import com.langwuyue.orange.redis.annotation.IfAbsent;
import com.langwuyue.orange.redis.annotation.Member;
import com.langwuyue.orange.redis.context.OrangeRedisContext;
import com.langwuyue.orange.redis.executor.hash.context.OrangeAddMemberIfAbsentContext;
import com.langwuyue.orange.redis.listener.OrangeRedisSetIfAbsentListener;
import com.langwuyue.orange.redis.mapping.OrangeRedisExecutorIdGenerator;
import com.langwuyue.orange.redis.operations.OrangeRedisHashOperations;
import com.langwuyue.orange.redis.utils.OrangeCollectionUtils;

/**
 * @author Liang.Zhong
 * @since 1.0.0
 */
public class OrangeAdIfAbsentByMemberAnnotationExecutor extends OrangeAddMemberIfAbsentExecutor {
	
	public OrangeAdIfAbsentByMemberAnnotationExecutor(
		OrangeRedisHashOperations operations,
		OrangeRedisExecutorIdGenerator idGenerator,
		Collection<OrangeRedisSetIfAbsentListener> listeners
	) {
		super(operations,idGenerator,listeners);
	}

	@Override
	protected List<Class<? extends Annotation>> getSupportedAnnotationClasses() {
		return OrangeCollectionUtils.asList(AddMembers.class,Member.class,IfAbsent.class);
	}

	@Override
	public Class<? extends OrangeRedisContext> getContextClass() {
		return OrangeAddMemberIfAbsentContext.class;
	}
}
