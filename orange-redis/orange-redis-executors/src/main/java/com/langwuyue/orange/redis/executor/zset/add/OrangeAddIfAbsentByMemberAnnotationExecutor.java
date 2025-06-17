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
import java.util.Collection;
import java.util.List;

import com.langwuyue.orange.redis.annotation.AddMembers;
import com.langwuyue.orange.redis.annotation.IfAbsent;
import com.langwuyue.orange.redis.annotation.Member;
import com.langwuyue.orange.redis.context.OrangeRedisContext;
import com.langwuyue.orange.redis.executor.zset.context.OrangeAddMemberIfAbsentContext;
import com.langwuyue.orange.redis.listener.OrangeRedisSetIfAbsentListener;
import com.langwuyue.orange.redis.mapping.OrangeRedisExecutorIdGenerator;
import com.langwuyue.orange.redis.operations.OrangeRedisZSetOperations;
import com.langwuyue.orange.redis.utils.OrangeCollectionUtils;

/**
 * Annotation-based conditional ZSet member addition executor implementation.
 * 
 * <p>Features:
 * <ul>
 *   <li>Driven by {@link AddMembers}, {@link Member} and {@link IfAbsent} annotations</li>
 *   <li>Only performs addition when member doesn't exist</li>
 *   <li>Supports notification listeners for addition results</li>
 *   <li>Extends {@link OrangeAddMemberIfAbsentExecutor} base implementation</li>
 * </ul>
 *
 * @author Liang.Zhong
 * @since 1.0.0
 * @see <a href="https://orange.langwuyue.com/redis/advanced/zset">Orange Redis ZSet Documentation</a>
 */
public class OrangeAddIfAbsentByMemberAnnotationExecutor extends OrangeAddMemberIfAbsentExecutor {

	/**
	 * Constructor.
	 *
	 * @param operations Redis ZSet operations interface instance
	 * @param idGenerator Executor ID generator
	 * @param listeners Conditional addition listeners collection
	 */
	public OrangeAddIfAbsentByMemberAnnotationExecutor(
			OrangeRedisZSetOperations operations,
			OrangeRedisExecutorIdGenerator idGenerator,
			Collection<OrangeRedisSetIfAbsentListener> listeners
	) {
		super(operations,idGenerator,listeners);
	}

	/**
	 * Gets the list of annotation types supported by this executor.
	 *
	 * <p>Supported annotation types:
	 * <ul>
	 *   <li>{@link AddMembers} - Marks batch addition operation</li>
	 *   <li>{@link Member} - Specifies member information</li>
	 *   <li>{@link IfAbsent} - Marks conditional addition operation</li>
	 * </ul>
	 *
	 * @return Immutable list of annotation classes
	 */
	@Override
	protected List<Class<? extends Annotation>> getSupportedAnnotationClasses() {
		return OrangeCollectionUtils.asList(AddMembers.class,Member.class,IfAbsent.class);
	}

	/**
	 * Gets the context class required by this executor.
	 *
	 * <p>Requires {@link OrangeAddMemberIfAbsentContext} to provide:
	 * <ul>
	 *   <li>Redis key information</li>
	 *   <li>Member information</li>
	 *   <li>Conditional addition parameters</li>
	 * </ul>
	 *
	 * @return Required context class
	 */
	@Override
	public Class<? extends OrangeRedisContext> getContextClass() {
		return OrangeAddMemberIfAbsentContext.class;
	}
}