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
package com.langwuyue.orange.redis.template.hash;

import com.langwuyue.orange.redis.RedisValueTypeEnum;
import com.langwuyue.orange.redis.annotation.Decrement;
import com.langwuyue.orange.redis.annotation.Increment;
import com.langwuyue.orange.redis.annotation.RedisValue;
import com.langwuyue.orange.redis.annotation.hash.HashKey;
import com.langwuyue.orange.redis.annotation.hash.OrangeRedisHashClient;

/**
 * Interface template for Redis Hash operations. 
 * Developers should extend this interface and annotate the child interface with {@code OrangeRedisKey}.
 * 
 * <p>A example is:
 * <blockquote><pre>
 *  {@code @OrangeRedisKey(expirationTime = @Timeout(value = 1, unit = TimeUnit.HOURS), key = "orange:hash:example1")} 
 *  public interface OrangeRedisHashExample1Api extends DoubleOperationsTemplate {
 *  	
 *  }
 * </pre></blockquote>
 * 
 * 
 * <p>Please review examples for more information.
 * 
 * @author Liang.Zhong
 * @since 1.0.0
 */
@OrangeRedisHashClient(
	hashKeyType = RedisValueTypeEnum.STRING,
	hashValueType = RedisValueTypeEnum.DOUBLE
)
public interface DoubleOperationsTemplate extends JSONOperationsTemplate<Double> {
	
	/**
     * Increments the value associated with the given key by the specified delta.
     * @param key The hash key to increment
     * @param delta The amount to increment by
     * @return The new value after increment
     */
	@Increment
	Double increment(@HashKey String key, @RedisValue Double deltal);
	
	/**
     * Decrements the value associated with the given key by the specified delta.
     * @param key The hash key to decrement
     * @param delta The amount to decrement by
     * @return The new value after decrement
     */
	@Decrement
	Double decrement(@HashKey String key, @RedisValue Double deltal);
}
