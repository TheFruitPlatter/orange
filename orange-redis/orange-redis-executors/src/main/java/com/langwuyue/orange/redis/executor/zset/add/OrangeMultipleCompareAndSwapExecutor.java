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
 * Redis executor for performing multiple Compare-And-Swap (CAS) operations on Redis Sorted Sets (ZSet).
 * 
 * <p>This executor handles the {@link Multiple} and {@link CAS} annotations to perform atomic
 * score updates on multiple members in a Redis ZSet. Each update is only performed if the current
 * score matches the expected old score (compare-and-swap semantics). This provides a way to
 * implement optimistic locking for multiple ZSet entries.
 * 
 * <p>The {@link ContinueOnFailure} annotation can be used to control whether the operation
 * should continue processing remaining entries if one of the CAS operations fails.
 * 
 * 
 * <p>The return value is a Map where keys are the member values and values are boolean flags
 * indicating whether the CAS operation succeeded for that member.
 *
 * @author Liang.Zhong
 * @since 1.0.0
 * @see <a href="https://orange.langwuyue.com/redis/advanced/zset">Orange Redis ZSet Documentation</a>
 */
public class OrangeMultipleCompareAndSwapExecutor extends OrangeCompareAndSwapExecutor {
	
	/**
	 * Constructs a new multiple compare-and-swap executor for Redis ZSet operations.
	 *
	 * @param operations The Redis script operations template used for executing atomic
	 *                  Lua scripts that perform the CAS operations
	 * @param idGenerator Generator for creating unique executor IDs, used for
	 *                    tracking and debugging executor instances
	 * @param logger Logger instance for recording operation results and any
	 *               potential failures during CAS operations
	 */
	public OrangeMultipleCompareAndSwapExecutor(OrangeRedisScriptOperations operations,OrangeRedisExecutorIdGenerator idGenerator,OrangeRedisLogger logger) {
		super(operations,idGenerator,logger);
	}

	/**
	 * Executes multiple Compare-And-Swap (CAS) operations on a Redis ZSet.
	 * 
	 * <p>This method iterates through a set of entries and performs CAS operations
	 * for each one. For each entry, it attempts to update the score only if the
	 * current score matches the expected old score. The operation can be configured
	 * to either continue or stop on failure using the continueOnFailure flag.
	 *
	 * @param context The Redis operation context containing:
	 *                - Redis key for the target ZSet
	 *                - Set of ZSetEntry objects with CAS operation details
	 *                - Flag indicating whether to continue on individual failures
	 * @return Map&lt;Object, Boolean&gt; A map where keys are member values and values
	 *         indicate whether the CAS operation succeeded for that member. The map
	 *         maintains insertion order using LinkedHashMap.
	 * @throws Exception if the operation fails due to Redis errors or invalid context
	 */
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

	/**
	 * Returns the list of annotations supported by this executor.
	 * 
	 * @return List containing:
	 *         <ul>
	 *           <li>{@link Multiple} - Marks methods that perform multiple operations</li>
	 *           <li>{@link CAS} - Marks methods that perform Compare-And-Swap operations</li>
	 *           <li>{@link ContinueOnFailure} - Controls whether to continue processing
	 *               on individual operation failures</li>
	 *         </ul>
	 */
	@Override
	protected List<Class<? extends Annotation>> getSupportedAnnotationClasses() {
		return OrangeCollectionUtils.asList(Multiple.class,CAS.class,ContinueOnFailure.class);
	}

	/**
	 * Returns the context class used by this executor.
	 * 
	 * @return {@link OrangeMultipleCompareAndSwapContext} class which contains:
	 *         - The Redis key for the target ZSet
	 *         - Set of entries with CAS operation details
	 *         - Configuration for failure handling behavior
	 */
	@Override
	public Class<? extends OrangeRedisContext> getContextClass() {
		return OrangeMultipleCompareAndSwapContext.class;
	}
}