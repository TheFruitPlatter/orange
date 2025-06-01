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

import com.langwuyue.orange.redis.RedisValueTypeEnum;
import com.langwuyue.orange.redis.context.OrangeRedisContext;
import com.langwuyue.orange.redis.executor.hash.context.OrangeHashContext;

/**
 * A specialized context builder for Redis Hash operations.
 * 
 * <p>This builder extends {@link OrangeRedisContextBuilder} to support Redis Hash data structure
 * operations such as HSET, HGET, HMGET, HGETALL, etc. It provides additional configuration
 * for hash field types through the {@link #keyType(RedisValueTypeEnum)} method.
 * 
 * <p>Redis Hash operations work with key-value pairs stored within a hash, where:
 * <ul>
 *   <li>The Redis key identifies the hash structure itself</li>
 *   <li>Each hash contains multiple field-value pairs</li>
 *   <li>The field type (keyType) can be configured separately from the value type</li>
 * </ul>
 * 
 * <p>Example usage:
 * <pre>{@code
 * OrangeRedisHashContextBuilder builder = new OrangeRedisHashContextBuilder()
 *     .redisKey("user:profile:1001")
 *     .keyType(RedisValueTypeEnum.STRING)
 *     .valueType(RedisValueTypeEnum.JSON);
 * }</pre>
 *
 * @author Liang.Zhong
 * @since 1.0.0
 * @see OrangeRedisContextBuilder
 * @see OrangeHashContext
 */
public class OrangeRedisHashContextBuilder extends OrangeRedisContextBuilder {
	
	/**
	 * The data type for hash field names (keys within the hash).
	 * <p>This type determines how hash field names are serialized and deserialized
	 * when performing hash operations. For example, if set to {@link RedisValueTypeEnum#STRING},
	 * field names will be treated as plain strings. If set to {@link RedisValueTypeEnum#JSON},
	 * field names will be processed as JSON strings.
	 */
	private RedisValueTypeEnum keyType;
	
	/**
	 * Creates a new instance of OrangeRedisHashContextBuilder.
	 * 
	 * <p>Initializes a new builder with default settings. The following properties
	 * must be configured before building the context:
	 * <ul>
	 *   <li>Redis key (via {@link #redisKey})</li>
	 *   <li>Key type (via {@link #keyType})</li>
	 *   <li>Value type (via {@link #valueType})</li>
	 * </ul>
	 * 
	 * <p>Example initialization:
	 * <pre>{@code
	 * OrangeRedisHashContextBuilder builder = new OrangeRedisHashContextBuilder()
	 *     .redisKey("user:1001")
	 *     .keyType(RedisValueTypeEnum.STRING)
	 *     .valueType(RedisValueTypeEnum.JSON);
	 * }</pre>
	 */
	public OrangeRedisHashContextBuilder() {}
	
	/**
	 * Sets the data type for hash field names.
	 * 
	 * <p>This configuration affects how hash field names are processed when performing
	 * hash operations. The field type is independent of the value type (set via {@link #valueType}),
	 * allowing for different serialization strategies for fields and values.
	 * 
	 * <p>Common configurations include:
	 * <ul>
	 *   <li>{@link RedisValueTypeEnum#STRING} - for plain string field names</li>
	 *   <li>{@link RedisValueTypeEnum#JSON} - for JSON-formatted field names</li>
	 * </ul>
	 *
	 * @param keyType the type to use for hash field names. Must not be null.
	 * @return this builder instance for method chaining
	 */
	public OrangeRedisHashContextBuilder keyType(RedisValueTypeEnum keyType) {
		this.keyType = keyType;
		return this;
	}
	
	/**
	 * Creates a new context for Redis hash operations.
	 * 
	 * <p>This method instantiates a new {@link OrangeHashContext} with all the configured
	 * properties from this builder. The context creation process involves:
	 * <ul>
	 *   <li>Validating all required properties are set</li>
	 *   <li>Creating a new context instance of the specified class</li>
	 *   <li>Configuring the context with operation metadata</li>
	 *   <li>Setting up hash-specific properties (key type, value type)</li>
	 * </ul>
	 * 
	 * <p>The created context will contain:
	 * <ul>
	 *   <li>The Redis key identifying the hash structure</li>
	 *   <li>The configured key type for hash field names</li>
	 *   <li>The configured value type for hash values</li>
	 *   <li>Operation metadata (owner, method, arguments)</li>
	 * </ul>
	 *
	 * @return a new {@link OrangeHashContext} configured with the current builder settings
	 * @throws Exception if context creation or initialization fails
	 * @see OrangeHashContext
	 * @see #keyType(RedisValueTypeEnum)
	 * @see #valueType(RedisValueTypeEnum)
	 */
	@Override
	protected OrangeRedisContext newContext() throws Exception {
		return OrangeHashContext.newInstance(
				this.getContextClass(),
				this.getOperationOwner(),
				this.getOperationMethod(),
				this.getArgs(),
				this.getRedisKey(),
				this.getValueType(),
				this.keyType
		);
	}
}