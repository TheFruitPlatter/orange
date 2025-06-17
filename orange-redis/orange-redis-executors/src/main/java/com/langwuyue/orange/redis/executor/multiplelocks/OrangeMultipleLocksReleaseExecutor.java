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
package com.langwuyue.orange.redis.executor.multiplelocks;

import java.lang.annotation.Annotation;
import java.util.List;

import com.langwuyue.orange.redis.annotation.ContinueOnFailure;
import com.langwuyue.orange.redis.annotation.Multiple;
import com.langwuyue.orange.redis.annotation.transaction.Release;
import com.langwuyue.orange.redis.context.OrangeRedisContext;
import com.langwuyue.orange.redis.context.OrangeRedisMultipleValueContext;
import com.langwuyue.orange.redis.executor.OrangeRemoveMembersAbstractExecutor;
import com.langwuyue.orange.redis.logger.OrangeRedisLogger;
import com.langwuyue.orange.redis.mapping.OrangeRedisExecutorIdGenerator;
import com.langwuyue.orange.redis.operations.OrangeRedisHashOperations;
import com.langwuyue.orange.redis.utils.OrangeCollectionUtils;

/**
 * An executor implementation that handles the release of multiple Redis locks.
 * 
 * <p>This executor is responsible for removing multiple lock entries from Redis hash structures.
 * It supports batch release operations through annotations and provides failure handling mechanisms.
 * The executor extends {@link OrangeRemoveMembersAbstractExecutor} to leverage common member removal
 * functionality while adding specific lock release behavior.
 * 
 * <p>Supported annotations:
 * <ul>
 *   <li>{@link Release} - Marks methods that release locks</li>
 *   <li>{@link Multiple} - Indicates multiple entries to be released</li>
 *   <li>{@link ContinueOnFailure} - Controls behavior when release operation fails</li>
 * </ul>
 * 
 * <p>This executor uses {@link OrangeRedisHashOperations} to perform the actual Redis operations
 * and works with {@link OrangeRedisMultipleValueContext} to manage the operation context.
 *
 * @author Liang.Zhong
 * @since 1.0.0
 * @see OrangeRemoveMembersAbstractExecutor
 * @see OrangeRedisHashOperations
 * @see OrangeRedisMultipleValueContext
 * @see <a href="https://orange.langwuyue.com/redis/advanced/multiple-locks">Orange Redis Multiple Locks Documentation</a>
 */
public class OrangeMultipleLocksReleaseExecutor extends OrangeRemoveMembersAbstractExecutor {
	
	private OrangeRedisHashOperations operations;

	public OrangeMultipleLocksReleaseExecutor(OrangeRedisHashOperations operations,OrangeRedisExecutorIdGenerator idGenerator,OrangeRedisLogger logger) {
		super(idGenerator,logger);
		this.operations = operations;
	}

	/**
	 * Returns the list of annotation classes supported by this executor.
	 * 
	 * <p>This executor supports the following annotations:
	 * <ul>
	 *   <li>{@link Release} - Identifies methods that release locks</li>
	 *   <li>{@link Multiple} - Indicates that multiple lock entries should be processed</li>
	 *   <li>{@link ContinueOnFailure} - Specifies that the operation should continue even if some releases fail</li>
	 * </ul>
	 *
	 * @return a list of supported annotation classes
	 */
	@Override
	protected List<Class<? extends Annotation>> getSupportedAnnotationClasses() {
		return OrangeCollectionUtils.asList(Release.class, Multiple.class, ContinueOnFailure.class);
	}
	
	/**
	 * Returns the context class used by this executor.
	 * 
	 * <p>This executor uses {@link OrangeRedisMultipleValueContext} to manage the state
	 * and data during lock release operations.
	 *
	 * @return the class of {@link OrangeRedisMultipleValueContext} used by this executor
	 */
	@Override
	public Class<? extends OrangeRedisContext> getContextClass() {
		return OrangeRedisMultipleValueContext.class;
	}

	/**
	 * Removes multiple lock entries from Redis.
	 * 
	 * <p>This method handles the batch removal of multiple lock entries.
	 *
	 * @param ctx the Redis operation context containing the keys and values to be removed
	 * @return the number of entries successfully removed
	 * @throws Exception if an error occurs during the removal operation
	 */
	@Override
	protected Long doRemove(OrangeRedisContext ctx) throws Exception {
		OrangeRedisMultipleValueContext context = (OrangeRedisMultipleValueContext)ctx;
		return this.operations.removeMembers(ctx.getRedisKey().getValue(), ctx.getValueType(), context.toArray());
	}

	/**
	 * Removes a specific lock entry from Redis.
	 * 
	 * <p>This method handles the removal of a specific lock entry. 
	 *
	 * <p>Note that despite handling a single value, this method uses the batch removal operation
	 * {@code removeMembers} internally, which allows for consistent behavior with the batch removal method.
	 *
	 * @param ctx the Redis operation context containing the key and value type information
	 * @param value the specific lock entry value to be removed
	 * @return the number of entries successfully removed (typically 1 if successful, 0 if not found)
	 * @throws Exception if an error occurs during the removal operation
	 */
	@Override
	protected Long doRemove(OrangeRedisContext ctx, Object value) throws Exception {
		return this.operations.removeMembers(ctx.getRedisKey().getValue(), ctx.getValueType(), value);
	}
}