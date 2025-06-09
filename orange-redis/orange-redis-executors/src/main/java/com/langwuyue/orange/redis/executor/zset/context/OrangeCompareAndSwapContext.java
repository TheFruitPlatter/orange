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
package com.langwuyue.orange.redis.executor.zset.context;

import java.lang.reflect.Method;

import com.langwuyue.orange.redis.RedisValueTypeEnum;
import com.langwuyue.orange.redis.annotation.OrangeRedisOperationArg;
import com.langwuyue.orange.redis.annotation.zset.OldScore;

/**
 * Context class for atomic Compare-and-Swap (CAS) operations on Redis ZSet scores.
 * 
 * <p>This class extends {@link OrangeScoreContext} to provide atomic score updates
 * using the Compare-and-Swap pattern. It enables atomic updates by:
 * <ul>
 *   <li>Storing the expected old score value</li>
 *   <li>Comparing it with the current score before updating</li>
 *   <li>Only performing the update if the current score matches the expected value</li>
 * </ul>
 * 
 * <p>The CAS operation helps prevent race conditions in concurrent environments by
 * ensuring that updates only occur if the value hasn't changed since it was last read.
 * 
 *
 * @author Liang.Zhong
 * @since 1.0.0
 * @see OrangeScoreContext
 * @see OldScore
 */
public class OrangeCompareAndSwapContext extends OrangeScoreContext {
	
	/**
	 * The expected old score value for the CAS operation.
	 * This value is compared with the current score in the ZSet before performing the update.
	 * If the current score doesn't match this value, the update operation will fail.
	 * 
	 * <p>This field is bound to parameters annotated with {@link OldScore} in operation methods.
	 */
	@OrangeRedisOperationArg(binding = OldScore.class)
	private Object oldScore;
	
	/**
	 * Constructs a new OrangeCompareAndSwapContext for atomic ZSet score updates.
	 *
	 * <p>This constructor initializes a context for Compare-and-Swap operations on Redis ZSet scores.
	 * It processes the method parameters to extract the old score value that will be used
	 * for comparison during the CAS operation.
	 *
	 * @param operationOwner The class that owns the Redis operation method
	 * @param operationMethod The method annotated with Redis operation annotations
	 * @param args The array of arguments passed to the operation method
	 * @param redisKey The Redis key wrapper containing the target ZSet key
	 * @param valueType The type of Redis value being operated on (should be ZSET)
	 */
	public OrangeCompareAndSwapContext(
		Class<?> operationOwner, 
		Method operationMethod, 
		Object[] args,
		Key redisKey,
		RedisValueTypeEnum valueType
	) {
		super(operationOwner, operationMethod, args, redisKey,valueType);
	}

	/**
	 * Returns the expected old score value for the CAS operation.
	 * 
	 * <p>This method retrieves the expected score value that will be compared
	 * against the current score in the Redis ZSet. The CAS operation will only succeed
	 * if the current score matches this expected value.
	 * 
	 * <p>The score is extracted from the parameter annotated with {@link OldScore}
	 * and converted to a Double value. If the parameter value is null or invalid,
	 * this method may return null.
	 *
	 * @return The expected old score value as a Double, or null if no valid score is available
	 * @see OldScore
	 */
	public Double getOldScore() {
		return getNullableScore(oldScore,OldScore.class);
	}

	
	
}