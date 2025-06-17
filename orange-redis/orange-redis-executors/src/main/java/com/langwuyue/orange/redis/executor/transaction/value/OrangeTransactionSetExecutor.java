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
package com.langwuyue.orange.redis.executor.transaction.value;

import java.lang.annotation.Annotation;
import java.util.List;

import com.langwuyue.orange.redis.RedisValueTypeEnum;
import com.langwuyue.orange.redis.annotation.RedisValue;
import com.langwuyue.orange.redis.annotation.value.SetValue;
import com.langwuyue.orange.redis.context.OrangeRedisContext;
import com.langwuyue.orange.redis.context.OrangeRedisValueContext;
import com.langwuyue.orange.redis.executor.OrangeRedisAbstractExecutor;
import com.langwuyue.orange.redis.executor.transaction.OrangeRedisDefaultTransactionManager;
import com.langwuyue.orange.redis.executor.transaction.OrangeRedisDefaultTransactionManager.OrangeRedisTransactionKey;
import com.langwuyue.orange.redis.executor.transaction.OrangeRedisDefaultTransactionManager.OrangeTransactionCommitProcessor;
import com.langwuyue.orange.redis.executor.transaction.OrangeRedisTransactionKeyConstants;
import com.langwuyue.orange.redis.mapping.OrangeRedisExecutorIdGenerator;
import com.langwuyue.orange.redis.operations.OrangeRedisHashOperations;
import com.langwuyue.orange.redis.utils.OrangeCollectionUtils;

/**
 * Executor implementation for handling transactional value setting operations in Redis.
 * 
 * <p>This executor is responsible for setting values in Redis within a transaction context,
 * providing proper isolation and consistency guarantees. It manages the versioning of values
 * and integrates with the transaction management system to ensure atomic operations.
 * 
 * <p>Key features:
 * <ul>
 *   <li>Automatic version increment for each value update</li>
 *   <li>Transaction-aware value storage with version tracking</li>
 *   <li>Integration with transaction commit processing</li>
 *   <li>Support for {@link SetValue} and {@link RedisValue} annotations</li>
 * </ul>
 * 
 * <p>The executor uses hash operations to store both the value and its version information,
 * allowing for efficient retrieval and update operations while maintaining transaction
 * isolation.
 *
 * @author Liang.Zhong
 * @since 1.0.0
 * @see <a href="https://orange.langwuyue.com/redis/advanced/transaction">Orange Redis Transaction Documentation</a>
 */
public class OrangeTransactionSetExecutor extends OrangeRedisAbstractExecutor {
	
	/**
	 * Redis hash operations for manipulating hash data structures.
	 * Used for storing versioned values and incrementing version counters.
	 */
	private OrangeRedisHashOperations hashOperations;
	
	/**
	 * Transaction manager that maintains the state of active transactions.
	 * Responsible for tracking transaction keys and their associated versions.
	 */
	private OrangeRedisDefaultTransactionManager transactionManager;
	
	/**
	 * Processor responsible for handling transaction commit operations.
	 * Executes the necessary steps to finalize transactions when they are committed.
	 */
	private OrangeTransactionCommitProcessor processor;

	/**
	 * Constructs a new transaction set executor with required dependencies.
	 *
	 * @param hashOperations Redis hash operations for manipulating hash data structures
	 * @param idGenerator Generator for creating unique executor identifiers
	 * @param transactionManager Manager for handling transaction state and lifecycle
	 * @param processor Processor for handling transaction commit operations
	 */
	public OrangeTransactionSetExecutor(
		OrangeRedisHashOperations hashOperations,
		OrangeRedisExecutorIdGenerator idGenerator,
		OrangeRedisDefaultTransactionManager transactionManager,
		OrangeTransactionCommitProcessor processor
	) {
		super(idGenerator);
		this.hashOperations = hashOperations;
		this.transactionManager = transactionManager;
		this.processor = processor;
	}

	/**
	 * Executes the transactional set operation for a Redis value.
	 * 
	 * 
	 * <p>This implementation ensures that values are properly versioned and that
	 * transaction isolation is maintained. Each value update creates a new version,
	 * allowing for concurrent operations without conflicts.
	 *
	 * @param context The Redis operation context containing key and value information
	 * @return The version number assigned to this transaction
	 * @throws Exception If an error occurs during the Redis operation
	 */
	@Override
	public Object execute(OrangeRedisContext context) throws Exception {
		OrangeRedisValueContext ctx = (OrangeRedisValueContext) context;
		String key = context.getRedisKey().getValue();
		Long version = this.hashOperations.increment(key, OrangeRedisTransactionKeyConstants.NEXT_VERSION, 1L, RedisValueTypeEnum.STRING);
		this.hashOperations.putMember(key, OrangeRedisTransactionKeyConstants.VERSION_PREFIX + version, ctx.getValue(), RedisValueTypeEnum.STRING, context.getValueType());
		OrangeRedisTransactionKey transactionKey = new OrangeRedisTransactionKey();
		transactionKey.setKey(key);
		transactionKey.setOriginKey(ctx.getRedisKey().getOriginalKey());
		transactionKey.setVersion(version);
		this.transactionManager.saveTransactionInfo(this.processor,transactionKey);
		return version;
	}

	/**
	 * Returns the list of annotation classes supported by this executor.
	 * 
	 * <p>This method defines which annotations this executor can handle. It supports
	 * two types of annotations:
	 * <ul>
	 *   <li>{@link RedisValue} - The general Redis value annotation</li>
	 *   <li>{@link SetValue} - The specific annotation for setting values</li>
	 * </ul>
	 * 
	 * <p>When a method is annotated with either of these annotations, this executor
	 * will be selected to handle the Redis operation, applying the transactional
	 * semantics defined in this class.
	 *
	 * @return A list containing the supported annotation classes
	 */
	@Override
	protected List<Class<? extends Annotation>> getSupportedAnnotationClasses() {
		return OrangeCollectionUtils.asList(RedisValue.class,SetValue.class);
	}

	/**
	 * Returns the context class used by this executor.
	 * 
	 * <p>The context class is used during the execution phase to properly cast
	 * the generic context object to the specific type needed by this executor.
	 *
	 * @return The class object representing the context type used by this executor
	 */
	@Override
	public Class<? extends OrangeRedisContext> getContextClass() {
		return OrangeRedisValueContext.class;
	}
}