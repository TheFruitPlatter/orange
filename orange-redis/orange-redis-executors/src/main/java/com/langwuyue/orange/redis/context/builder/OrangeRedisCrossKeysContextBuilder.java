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
package com.langwuyue.orange.redis.context.builder;

import java.util.List;

import com.langwuyue.orange.redis.context.OrangeRedisContext;
import com.langwuyue.orange.redis.executor.cross.context.OrangeCrossOperationContext;

/**
 * A specialized context builder for Redis operations that work with multiple keys.
 * 
 * <p>This builder extends {@link OrangeRedisContextBuilder} to support Redis operations
 * that operate across multiple keys, such as:
 * <ul>
 *   <li>Set operations (SUNION, SINTER, SDIFF)</li>
 *   <li>ZSet operations (ZUNION, ZINTER)</li>
 *   <li>List operations across multiple keys</li>
 *   <li>Geo operations involving multiple locations</li>
 * </ul>
 * 
 * <p>The builder provides additional configuration for:
 * <ul>
 *   <li>Multiple source keys through {@link #keys(List)}</li>
 *   <li>Optional destination key for storing operation results through {@link #storeTo(String)}</li>
 * </ul>
 * 
 * <p>Example usage:
 * <pre>{@code
 * OrangeRedisCrossKeysContextBuilder builder = new OrangeRedisCrossKeysContextBuilder()
 *     .keys(Arrays.asList("set1", "set2"))
 *     .storeTo("result_set");
 * }</pre>
 *
 * @author Liang.Zhong
 * @since 1.0.0
 * @see OrangeRedisContextBuilder
 * @see OrangeCrossOperationContext
 */
public class OrangeRedisCrossKeysContextBuilder extends OrangeRedisContextBuilder {
	
	/**
	 * The list of source keys for the cross-key operation.
	 * <p>These keys represent the source data sets that will be operated on.
	 * For example, in a set union operation (SUNION), these would be the sets
	 * whose members will be combined.
	 */
	private List<String> keys;
	
	/**
	 * The destination key where the operation result will be stored.
	 * <p>This is used in storing operations (e.g., SUNIONSTORE, ZINTERSTORE)
	 * to specify where the result should be saved. If null, the operation
	 * will only return the result without storing it.
	 */
	private String storeTo;
	
	/**
	 * Creates a new instance of OrangeRedisCrossKeysContextBuilder.
	 */
	public OrangeRedisCrossKeysContextBuilder() {}
	
	/**
	 * Creates a new context for cross-keys operations.
	 * 
	 * @return a new {@link OrangeCrossOperationContext} configured with the current builder settings
	 * @throws Exception if context creation fails
	 */
	@Override
	protected OrangeRedisContext newContext() throws Exception {
		return OrangeCrossOperationContext.newInstance(
			this.getContextClass(),
			this.getOperationOwner(),
			this.getOperationMethod(),
			this.getArgs(),
			this.keys,
			this.storeTo,
			((OrangeCrossKeysOperationArgHandlerMapping)this.getOperationArgHandlerMapping()).getValueType(this.getOperationMethod())
		);
	}

	/**
	 * Sets the source keys for the cross-key operation.
	 * 
	 * <p>These keys represent the Redis keys that will be used as input for the operation.
	 * For example:
	 * <ul>
	 *   <li>For set operations (SUNION, SINTER), these are the set keys to combine</li>
	 *   <li>For sorted set operations (ZUNION), these are the sorted set keys to merge</li>
	 *   <li>For geo operations, these are the geo set keys to process</li>
	 * </ul>
	 *
	 * @param keys a list of Redis keys to operate on. Must not be null or empty.
	 * @return this builder instance for method chaining
	 */
	public OrangeRedisCrossKeysContextBuilder keys(List<String> keys) {
		this.keys = keys;
		return this;
	}
	
	/**
	 * Sets the destination key where the operation result will be stored.
	 * 
	 * <p>This is used for store operations that save their results back to Redis,
	 * such as SUNIONSTORE, SINTERSTORE, ZUNIONSTORE, etc. If not set, the operation
	 * will only return the result without storing it in Redis.
	 * 
	 * <p>For example:
	 * <pre>{@code
	 * builder.keys(Arrays.asList("set1", "set2"))
	 *        .storeTo("result_set");
	 * // This will store the union of set1 and set2 into result_set
	 * }</pre>
	 *
	 * @param storeTo the key where the operation result should be stored. Can be null
	 *               for non-store operations.
	 * @return this builder instance for method chaining
	 */
	public OrangeRedisCrossKeysContextBuilder storeTo(String storeTo) {
		this.storeTo = storeTo;
		return this;
	}
}